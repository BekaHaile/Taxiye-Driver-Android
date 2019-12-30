package product.clicklabs.jugnoo.driver.ui


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.crashlytics.android.beta.Beta
import com.google.firebase.iid.FirebaseInstanceId
import com.picker.CountryPickerDialog
import com.picker.OnCountryPickerListener
import kotlinx.android.synthetic.main.fragment_driver_info_update.*
import kotlinx.android.synthetic.main.fragment_vehicle_model.*
import org.json.JSONObject
import product.clicklabs.jugnoo.driver.*
import product.clicklabs.jugnoo.driver.Constants.KEY_ACCESS_TOKEN
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags
import product.clicklabs.jugnoo.driver.datastructure.DriverVehicleDetails
import product.clicklabs.jugnoo.driver.retrofit.RestClient
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse
import product.clicklabs.jugnoo.driver.ui.adapters.VehicleTypeSelectionAdapter
import product.clicklabs.jugnoo.driver.ui.api.APICommonCallbackKotlin
import product.clicklabs.jugnoo.driver.ui.api.ApiCommonKt
import product.clicklabs.jugnoo.driver.ui.api.ApiName
import product.clicklabs.jugnoo.driver.ui.models.CityResponse
import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponseKotlin
import product.clicklabs.jugnoo.driver.utils.*
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.mime.TypedByteArray


class DriverSetupFragment : Fragment() {
    private var parentActivity: Activity? = null

    private lateinit var accessToken: String
    private var cityId: String? = null
    private var citySelected: CityResponse.City? = null
    private var fleetSelected: CityResponse.Fleet? = null
    private var toolbarChangeListener: ToolbarChangeListener? = null
    private var citiesList:MutableList<CityResponse.City>? = null
    private var promoCodeFromServer:String? = null
    private val CITIES_DIALOG_FRAGMENT_TAG = "cities_fragment_dialog";
    private val FLEET_DIALOG_FRAGMENT_TAG = "fleet_fragment_dialog";
    private var fromVehicleDetailScreen: Boolean = false

    private val adapter by lazy { VehicleTypeSelectionAdapter(requireActivity(), rvVehicleTypes, null) }

    companion object {
        @JvmStatic
        fun newInstance(accessToken: String) =
                DriverSetupFragment().apply {
                    arguments = Bundle().apply {
                        putString(Constants.KEY_ACCESS_TOKEN, accessToken)
                    }
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            accessToken = it.getString(Constants.KEY_ACCESS_TOKEN)!!
            if (it.containsKey(Constants.FROM_VEHICLE_DETAILS_SCREEN)) {
                fromVehicleDetailScreen = it.getBoolean(Constants.FROM_VEHICLE_DETAILS_SCREEN)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if(!fromVehicleDetailScreen)
            toolbarChangeListener?.setToolbarText(getString(R.string.register_as_driver))
        else
            toolbarChangeListener?.setToolbarText(getString(R.string.title_vehicle_details))
        toolbarChangeListener?.setToolbarVisibility(true)
        return container?.inflate(R.layout.fragment_driver_info_update)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



            tvEnterName.typeface = Fonts.mavenMedium(parentActivity!!)
            editTextName.typeface = Fonts.mavenRegular(parentActivity!!)
            edtLastName.typeface = Fonts.mavenRegular(parentActivity!!)
            tvSelectVehicle.typeface = Fonts.mavenMedium(parentActivity!!)
            bContinue.typeface = Fonts.mavenRegular(parentActivity!!)
            bCancel.typeface = Fonts.mavenRegular(parentActivity!!)
            tvTermsOfUse.typeface = Fonts.mavenRegular(parentActivity!!)
            tvPromo.typeface = Fonts.mavenMedium(parentActivity!!)
            edtPromo.typeface = Fonts.mavenRegular(parentActivity!!)
            tvCities.typeface = Fonts.mavenRegular(parentActivity!!)



        bContinue.typeface = Fonts.mavenMedium(requireActivity())
        bContinue.setOnClickListener { if (validateData()) checkForPromoCode() }
        editTextName.setOnEditorActionListener { _, _, _ ->
            editTextEmail.requestFocus()
            editTextEmail.setSelection(editTextEmail.text.length)
            true }

        bCancel.typeface = Fonts.mavenMedium(requireActivity())
        bCancel.setOnClickListener { parentActivity?.onBackPressed() }
        tvCities.setOnClickListener{showCountriesDialog(requireActivity().supportFragmentManager)}
        tvFleetSelected.setOnClickListener{showFleetDialog(requireActivity().supportFragmentManager)}
        tvCities.paintFlags = tvCities.paintFlags with (Paint.UNDERLINE_TEXT_FLAG)
        with(rvVehicleTypes) {
            layoutManager = GridLayoutManager(requireActivity(), 3)
            addItemDecoration(ItemOffsetDecoration(parentActivity!!, R.dimen.spacing_grid_recycler_view));
            adapter = this@DriverSetupFragment.adapter
        }

        if(Prefs.with(requireActivity()).getInt(Constants.KEY_DRIVER_EMAIL_OPTIONAL, 1) == 0) {
            tvEnterEmail.text = getString(R.string.email)
        }
        getCitiesAPI()
        if (fromVehicleDetailScreen)
            showVehicleDetailsView()
        if(Data.getMultipleVehiclesEnabled()==1)
            multipleVehicleEnabledGroup.visibility=View.VISIBLE
        else
            multipleVehicleEnabledGroup.visibility=View.GONE

        selectVehicleVisibility()
    }

    private fun selectVehicleVisibility(){

        if(Prefs.with(requireActivity()).getInt(Constants.KEY_VEHICLE_MODEL_ENABLED,0)==1){
            rvVehicleTypes.visibility=View.GONE
            tvSelectVehicle.text=resources.getString(R.string.title_dialog_select_city)
            tvCities.text="city"
        }
        else{
            rvVehicleTypes.visibility=View.VISIBLE
            tvSelectVehicle.text=resources.getString(R.string.select_vehicle)
            tvCities.text=resources.getString(R.string.label_select_city)

        }
    }

    private fun showVehicleDetailsView() {
        groupDetails.visibility=View.GONE
    }

    private fun setupTermsAndConditionsTextView() {
        val showTerms = if (requireActivity().resources.getInteger(R.integer.show_t_and_c)
                == requireActivity().resources.getInteger(R.integer.view_visible)) 1 else 0
        if(Prefs.with(requireActivity()).getInt(Constants.KEY_SHOW_TERMS, showTerms) == 1){
            val termsText = getString(R.string.terms_and_conditions);
            val ss = SpannableString(getString(R.string.by_signing_you_agree) + " " + termsText)
            val clickableSpan = object : ClickableSpan() {
                override fun onClick(textView: View) {
                    startActivity(Intent(parentActivity, HelpActivity::class.java))
                }

               /* override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                }*/
            }
            val start = ss.length-termsText.length
            val end = ss.length
            ss.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            ss.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireActivity(), R.color.themeColor)), start, end, 0);
            tvTermsOfUse.text = ss
            tvTermsOfUse.movementMethod = LinkMovementMethod.getInstance()
            tvTermsOfUse.highlightColor = Color.TRANSPARENT
            tvTermsOfUse.typeface = Fonts.mavenRegular(requireActivity())

            tvTermsOfUse.visible()
        }else{
            tvTermsOfUse.gone()
        }

    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (!hidden) {
            toolbarChangeListener?.setToolbarText(getString(R.string.register_as_driver))
            toolbarChangeListener?.setToolbarVisibility(true)
        }
        super.onHiddenChanged(hidden)
    }

    private fun validateData(): Boolean {
        if (!fromVehicleDetailScreen) {
            if (editTextName.text.trim().toString().isBlank()) {

                DialogPopup.alertPopup(parentActivity, "", getString(R.string.first_name_required))
                return false
            }

        if (resources.getBoolean(R.bool.last_name_mandatory) && edtLastName.text.trim().toString().isBlank()) {
            DialogPopup.alertPopup(parentActivity, "", getString(R.string.last_name_required))
            return false
        }

            if (Prefs.with(requireActivity()).getInt(Constants.KEY_DRIVER_EMAIL_OPTIONAL, 1) == 0 && editTextEmail.text.trim().toString().isBlank()) {
                DialogPopup.alertPopup(parentActivity, "", getString(R.string.please_enter_email))
                return false
            }
            if (!editTextEmail.text.trim().toString().isBlank() && !Utils.isEmailValid(editTextEmail.text.trim().toString())) {
                DialogPopup.alertPopup(parentActivity, "", getString(R.string.please_enter_valid_email))
                return false
            }
            if (citySelected != null
                    && citySelected!!.mandatoryFleetRegistration == 1
                    && (fleetSelected == null || fleetSelected!!.id <= 0)) {
                DialogPopup.alertPopup(parentActivity, "", getString(R.string.please_select_fleet))
                return false
            }

            if (adapter.getCurrentSelectedVehicle() == null) {
                DialogPopup.alertPopup(parentActivity, "", getString(R.string.select_vehicle_type))
                return false
            }
        }
        val vehicleNumber = edtVehicleNo.text.toString().trim()
        if (Prefs.with(requireActivity()).getInt(Constants.KEY_VEHICLE_MODEL_ENABLED,0)==0
                && (vehicleNumber.isEmpty() && Data.getMultipleVehiclesEnabled()==1)) {
            DialogPopup.alertPopup(parentActivity, "", getString(R.string.invalid_vehicle_number))
            return false
        }


        if (!fromVehicleDetailScreen && (cityId == null || cityId == "0")) {
            DialogPopup.alertPopup(parentActivity, "", getString(R.string.city_unavailable))
            return false
        }



        return true
    }

    private fun checkForPromoCode(){

        var promoText:String? = null
        if(promoGroupView.visibility==View.VISIBLE && edtPromo.isEnabled && edtPromo.text.toString().trim().isNotEmpty()){
             promoText = edtPromo.text.toString().trim()
        }

        registerDriver(promoText)
    }

    private fun registerDriver(referralCode: String?) {
        Utils.hideSoftKeyboard(parentActivity, editTextName)
        var userName = editTextName.text.trim().toString()
        val lastName = edtLastName.text.trim().toString()
        if(lastName.isNotEmpty()){
            userName += " $lastName"
        }

        val userEmail = editTextEmail.text.trim().toString();
        val params = hashMapOf<String, String>(
                KEY_ACCESS_TOKEN to accessToken,
                "user_name" to userName ,
                "updated_user_email" to userEmail,
                "alt_phone_no" to "",
                "city" to cityId!!,
                "latitude" to "" + Data.latitude,
                "longitude" to "" + Data.longitude,
//                "vehicle_type" to vehicleType,
                "offering_type" to "" + 1,
                "vehicle_status" to ownershipSpinner.selectedItem.toString(),
                "device_type" to Data.DEVICE_TYPE,
                "device_name" to Data.deviceName,
                "app_version" to "" + Data.appVersion,
                "os_version" to Data.osVersion,
                "country" to Data.country,
                "client_id" to Data.CLIENT_ID,
                "login_type" to Data.LOGIN_TYPE,
                "referral_code" to "",
//                "device_token" to FirebaseInstanceId.getInstance().instanceId.result?.getToken()!!,
                "unique_device_id" to Data.uniqueDeviceId,
                "device_rooted" to if (Utils.isDeviceRooted()) "1" else "0"
        )

        if(Prefs.with(requireActivity()).getInt(Constants.KEY_VEHICLE_MODEL_ENABLED,0)==0){
            val vehicleType = (adapter.getCurrentSelectedVehicle()!!.vehicleType).toString();
            val regionId = (adapter.getCurrentSelectedVehicle()!!.regionId).toString();
            params["vehicle_type"] = vehicleType
            params[Constants.KEY_REGION_ID] = regionId
        }

        if(Data.getMultipleVehiclesEnabled()==1){
            params["vehicle_no"] = edtVehicleNo.text.toString()
            params["vehicle_ownership_status"] = ownershipSpinner.selectedItem.toString()
        }


        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener{
            if(!it.isSuccessful) {
                Log.w(TAG,"${SplashNewActivity.DEVICE_TOKEN_TAG} $TAG + driversetupfrag -> registerDriver device_token_unsuccessful",it.exception)
                return@addOnCompleteListener
            }
            if(it.result?.token != null) {
                Log.e("${SplashNewActivity.DEVICE_TOKEN_TAG} $TAG + driversetupfrag -> registerDriver", it.result?.token)
                params["device_token"] = it.result?.token!!
            }
            registerDriverFunc(referralCode, params, userName)
        }
    }

    private fun registerDriverFunc(referralCode: String?, params: HashMap<String, String>, userName: String) {
        if (referralCode != null) {
            params["referral_code"] = referralCode;
        }
        if (fleetSelected != null && fleetSelected!!.id > 0) {
            params[Constants.KEY_FLEET_ID] = fleetSelected!!.id.toString();
        }
        HomeUtil.putDefaultParams(params)
        if (Prefs.with(requireActivity()).getInt(Constants.KEY_VEHICLE_MODEL_ENABLED, 0) == 1) {
            if (activity is VehicleDetailsActivity)
                (activity as VehicleDetailsActivity).openVehicleDetails(accessToken, cityId!!,
                        "", userName, params)
            else
                (activity as DriverSplashActivity).openVehicleDetails(accessToken, cityId!!,
                        "", userName, params)
        } else if (fromVehicleDetailScreen) {
            hitAddVehicle(params)
        }
        else {
            ApiCommonKt<RegisterScreenResponse>(parentActivity!!).execute(params, ApiName.REGISTER_DRIVER, object : APICommonCallbackKotlin<RegisterScreenResponse>() {

                override fun onSuccess(t: RegisterScreenResponse?, message: String?, flag: Int) {
                    if (t != null) {
                        Log.d("", t.serverMessage())
                        when (t.flag) {
                            ApiResponseFlags.UPLOAD_DOCCUMENT.getOrdinal(), ApiResponseFlags.ACTION_COMPLETE.getOrdinal() -> {
                                if(t.driverVehicleMappinId!=-1){
                                    Data.setDriverMappingIdOnBoarding(t.driverVehicleMappinId)
                                }
//                                if(Prefs.with(requireActivity()).getInt(Constants.KEY_VEHICLE_MODEL_ENABLED, 0) == 1){
//                                    (activity as DriverSplashActivity).openVehicleDetails(accessToken,cityId!!,
//                                            vehicleType, userName)
//                                }else{
//                                    openDocumentUploadActivity()
//
//                                }
                                openDocumentUploadActivity()
                            }

                            ApiResponseFlags.AUTH_REGISTRATION_FAILURE.getOrdinal() -> {
                                DialogPopup.alertPopup(activity, "", message)
                            }

                            ApiResponseFlags.AUTH_ALREADY_REGISTERED.getOrdinal(), ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() -> {
                                DialogPopup.alertPopupWithListener(activity, "", message) {
                                    (parentActivity as DriverSplashActivity)?.openPhoneLoginScreen()
                                    (parentActivity as DriverSplashActivity)?.setToolbarVisibility(false)
                                }

                            }
                            else -> DialogPopup.alertPopup(activity, "", message)
                        }
                    }
                }

                override fun onError(t: RegisterScreenResponse?, message: String?, flag: Int): Boolean {
                    if (flag == ApiResponseFlags.SHOW_MESSAGE.getOrdinal()) {
                        DialogPopup.alertPopupWithListener(activity, "", message, {
                            setPromoLayout(true, referralCode)
                            openDocumentUploadActivity()
                        })
                        return true
                    } else {
                        return false

                    }

                }
            })
        }
    }
    private fun applyPromoCodeApi(){

        val promoCode =  edtPromo.text.toString().trim()
        ApiCommonKt<FeedCommonResponseKotlin>(requireActivity(),successFlag = ApiResponseFlags.SHOW_MESSAGE.getOrdinal())
                .execute( hashMapOf(Constants.CODE to promoCode,Constants.KEY_ACCESS_TOKEN to accessToken),ApiName.APPLY_PROMO,
                object : APICommonCallbackKotlin<FeedCommonResponseKotlin>(){

                    override fun onSuccess(t: FeedCommonResponseKotlin?, message: String?, flag: Int) {
                        setPromoLayout(true,promoCode)
                       // registerDriver(nu)
                    }

                    override fun onError(t: FeedCommonResponseKotlin?, message: String?, flag: Int): Boolean {
                        return false
                    }


                })

    }

    private fun getCitiesAPI() {
        val params = hashMapOf(
                Constants.KEY_ACCESS_TOKEN to accessToken,
                Constants.KEY_LATITUDE to Data.latitude.toString(),
                Constants.KEY_LONGITUDE to Data.longitude.toString())

        HomeUtil.putDefaultParams(params)

        ApiCommonKt<CityResponse>(requireActivity()).execute(params, ApiName.GET_CITIES, object : APICommonCallbackKotlin<CityResponse>() {
            override fun onSuccess(t: CityResponse?, message: String?, flag: Int) {
                if (ApiResponseFlags.ACK_RECEIVED.getOrdinal() == t?.flag) {
                    onError(t, t.serverMessage(), t.flag)
                    return
                }
                promoCodeFromServer = if(t != null) t.promoCode else ""
                citiesList = t!!.cities
                setCityData(t.currentCity)
                if(!fromVehicleDetailScreen){
                    groupView.visible()
                    setupTermsAndConditionsTextView()
                    if (Prefs.with(requireActivity()).getInt(Constants.KEY_DRIVER_EMAIL_OPTIONAL, 1) == 0
                        || Prefs.with(requireActivity()).getInt(Constants.KEY_EMAIL_INPUT_AT_SIGNUP, 0) == 1&&!fromVehicleDetailScreen) {
                    tvEnterEmail.visible()
                    editTextEmail.visible()
                    } else {
                    tvEnterEmail.gone()
                    editTextEmail.gone()
                    }
                }
            }

            override fun onError(t: CityResponse?, message: String?, flag: Int): Boolean {
                groupView.gone()
                DialogPopup.alertPopupWithListener(parentActivity, "", message, { parentActivity?.onBackPressed() })
                return true;
            }

            override fun onNotConnected(): Boolean {
                onError(null,parentActivity?.getString(R.string.check_internet_message),0)
                return true
            }

            override fun onFailure(error: RetrofitError?): Boolean {
                onError(null,parentActivity?.getString(R.string.some_error_occured),0)
                return true
            }

            override fun onException(e: Exception?): Boolean {
                onError(null,parentActivity?.getString(R.string.some_error_occured),0)
                return true
            }
        })
    }

    private fun setPromoLayout(show:Boolean,promoText:String? = null) {
        if (show) {
            promoGroupView.visible()
//            if (promoText != null && !promoText.isBlank()) {
//                edtPromo.setText(promoText)
//                edtPromo.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_ref_code,0,R.drawable.ic_tick_green_20,0)
//                edtPromo.isEnabled = false
//            } else {
            edtPromo.isEnabled = true
            edtPromo.setText(promoText)
            edtPromo.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_ref_code, 0, 0, 0)
//            }
        } else {
            promoGroupView.gone()
        }
    }

    private fun openDocumentUploadActivity() {
        val intent = Intent(activity, DriverDocumentActivity::class.java).apply {
            putExtra("access_token", accessToken)
            putExtra("in_side", false)
            putExtra("doc_required", 3)
        }
        startActivity(intent)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        parentActivity = context as Activity

        toolbarChangeListener = context as ToolbarChangeListener
    }

    override fun onDetach() {
        parentActivity = null
        toolbarChangeListener = null
        super.onDetach()
    }

    private fun setCityData(city: CityResponse.City?){
        if(Prefs.with(requireActivity()).getInt(Constants.KEY_VEHICLE_MODEL_ENABLED,0)==1)
            rvVehicleTypes.gone()
        if(city!=null){
            tvCities.text = city.cityName
            cityId = city.cityId.toString()
            citySelected = city
            adapter.setList(city.vehicleTypes,0)
            if(city.vehicleTypes==null || city.vehicleTypes.size==0){
                rvVehicleTypes.gone()
                Snackbar.make(view!!,getString(R.string.no_vehicles_available), Snackbar.LENGTH_SHORT).show()
            }else{
                if(Prefs.with(requireActivity()).getInt(Constants.KEY_VEHICLE_MODEL_ENABLED,0)==0)
                    rvVehicleTypes.visible()
                else
                    rvVehicleTypes.gone()
            }
            if (city.fleets != null && city.fleets.size > 0&&!fromVehicleDetailScreen) {
                fleetGroupView.visibility = View.VISIBLE
                if (fleetSelected != null && city.fleets.contains(fleetSelected)){
                    tvFleetSelected.text = fleetSelected!!.name
                } else {
                    fleetSelected = null
                    tvFleetSelected.text = null
                }
            } else {
                fleetGroupView.visibility = View.GONE
                fleetSelected = null
            }

            setPromoLayout(city.showPromo == 1, promoCodeFromServer)
        }else{
            rvVehicleTypes.gone()
            tvCities.text = getString(R.string.label_select_city)
            cityId = null
            citySelected = null
            fleetGroupView.visibility = View.GONE
            fleetSelected = null


        }


    }




    fun showCountriesDialog(supportFragmentManager: FragmentManager) {
        if (citiesList == null || citiesList!!.isEmpty()) {
            throw IllegalArgumentException(requireActivity().getString(R.string.error_no_cities_found))
        } else {
            val countryPickerDialog = CountryPickerDialog.newInstance(getString(R.string.title_dialog_select_city), false)
            countryPickerDialog.setCountryPickerListener(onCountryPickerListener)
            countryPickerDialog.setDialogInteractionListener(countryPickerDialogInteractionListener)
            countryPickerDialog.show(supportFragmentManager, CITIES_DIALOG_FRAGMENT_TAG)
        }
    }
    private val onCountryPickerListener = OnCountryPickerListener<CityResponse.City> { country ->
        if(country!=null){
            setCityData(country)
        }
    };

    private val countryPickerDialogInteractionListener = object : CountryPickerDialog.CountryPickerDialogInteractionListener<CityResponse.City> {
        override fun getAllCountries(): MutableList<CityResponse.City> {
            return citiesList!!
        }

        override fun sortCountries(searchResults: MutableList<CityResponse.City>?) {

        }

        override fun canSearch(): Boolean {
           return citiesList!=null && citiesList!!.size>7
        }


    }
    fun hitAddVehicle(params: HashMap<String, String>? = null) {

        RestClient.getApiServices().addNewVehicle(params, object : Callback<Any> {
            override fun success(o: Any, response: retrofit.client.Response) {
                val responseStr = String((response.body as TypedByteArray).bytes)
                Log.i(Beta.TAG, "AddNewVehicle response = $responseStr")
                DialogPopup.dismissLoadingDialog()
                try {
                    val jObj = JSONObject(responseStr)
                    val flag = jObj.optInt(Constants.KEY_FLAG, ApiResponseFlags.ACTION_COMPLETE.getOrdinal())
                    val message = JSONParser.getServerMessage(jObj)
                    if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                        if (jObj.has(Constants.KEY_DATA)) {
                            val dataObj = jObj.getJSONObject(Constants.KEY_DATA)
                            if (dataObj!= null) {
                                var driverVehicleDetail: DriverVehicleDetails?=null

                                driverVehicleDetail= DriverVehicleDetails.parseDocumentVehicleDetails(dataObj)
                                Data.userData.driverVehicleDetailsList.add(driverVehicleDetail)
                                (activity as VehicleDetailsActivity).vehicleAdded(driverVehicleDetail)
                            }

                        }
                        else
                            activity!!.supportFragmentManager.popBackStackImmediate()
                    }
                    DialogPopup.alertPopup(activity, "", message)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                DialogPopup.dismissLoadingDialog()
            }

            override fun failure(error: RetrofitError) {
                try {
                    DialogPopup.dismissLoadingDialog()
                    DialogPopup.alertPopup(activity, "", activity!!.getString(R.string.error_occured_tap_to_retry))
                } catch (e: Exception) {
                    DialogPopup.dismissLoadingDialog()
                    e.printStackTrace()
                }

                DialogPopup.dismissLoadingDialog()
            }
        })
    }


    private fun showFleetDialog(supportFragmentManager: FragmentManager) {
        if (citiesList == null || citiesList!!.isEmpty() || citySelected == null) {
            Utils.showToast(requireActivity(), getString(R.string.error_no_cities_found))
        } else if (citySelected!!.fleets == null || citySelected!!.fleets.size == 0) {
            Utils.showToast(requireActivity(), getString(R.string.error_no_fleets_in_this_city_format, citySelected!!.cityName))
        } else {
            val countryPickerDialog = CountryPickerDialog.newInstance(getString(R.string.select_fleet), false)
            countryPickerDialog.setCountryPickerListener(object:OnCountryPickerListener<CityResponse.Fleet>{
                override fun onSelectCountry(country: CityResponse.Fleet?) {
                    fleetSelected = country
                    tvFleetSelected.text = fleetSelected!!.name
                }

            });
            countryPickerDialog.setDialogInteractionListener(object:CountryPickerDialog.CountryPickerDialogInteractionListener<CityResponse.Fleet>{
                override fun getAllCountries(): MutableList<CityResponse.Fleet> {
                    val fleets = mutableListOf<CityResponse.Fleet>()
                    fleets.addAll(citySelected!!.fleets)
                    if(citySelected!!.mandatoryFleetRegistration != 1) {
                        val noneFleet = CityResponse.Fleet()
                        noneFleet.name = getString(R.string.none)
                        noneFleet.id = -1
                        fleets.add(noneFleet)
                    }
                    return fleets
                }

                override fun sortCountries(searchResults: MutableList<CityResponse.Fleet>?) {
                }

                override fun canSearch(): Boolean {
                    return citySelected!!.fleets.size>7
                }

            })
            countryPickerDialog.show(supportFragmentManager, FLEET_DIALOG_FRAGMENT_TAG)
        }
    }

    override fun onResume() {
        super.onResume()
        toolbarChangeListener?.setToolbarVisibility(true)
    }
}

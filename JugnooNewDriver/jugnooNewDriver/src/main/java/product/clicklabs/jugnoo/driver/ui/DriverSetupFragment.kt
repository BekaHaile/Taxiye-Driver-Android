package product.clicklabs.jugnoo.driver.ui


import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.DatePicker
import com.google.firebase.iid.FirebaseInstanceId
import com.picker.CountryPickerDialog
import com.picker.OnCountryPickerListener
import kotlinx.android.synthetic.main.fragment_driver_info_update.*
import product.clicklabs.jugnoo.driver.*
import product.clicklabs.jugnoo.driver.Constants.DOB_DATE_FORMAT
import product.clicklabs.jugnoo.driver.Constants.KEY_ACCESS_TOKEN
import product.clicklabs.jugnoo.driver.adapters.DropDownListAdapter
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags
import product.clicklabs.jugnoo.driver.datastructure.Gender
import product.clicklabs.jugnoo.driver.datastructure.GenderValues
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse
import product.clicklabs.jugnoo.driver.ui.adapters.VehicleTypeSelectionAdapter
import product.clicklabs.jugnoo.driver.ui.api.APICommonCallbackKotlin
import product.clicklabs.jugnoo.driver.ui.api.ApiCommonKt
import product.clicklabs.jugnoo.driver.ui.api.ApiName
import product.clicklabs.jugnoo.driver.ui.models.CityResponse
import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponseKotlin
import product.clicklabs.jugnoo.driver.utils.*
import retrofit.RetrofitError
import java.text.SimpleDateFormat
import java.util.*


class DriverSetupFragment : Fragment(), AdapterView.OnItemSelectedListener {
    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        if(IS_FIRST_ITEM_TITLE && pos != 0 || !IS_FIRST_ITEM_TITLE) {
            mGender = (parent?.getItemAtPosition(pos) as Gender).type
        }else{
            mGender =  null
        }
        setCityData(citySelected)
    }

    private var parentActivity: DriverSplashActivity? = null

    private lateinit var accessToken: String
    private var cityId: String? = null
    private var toolbarChangeListener: ToolbarChangeListener? = null
    private var citiesList:MutableList<CityResponse.City>? = null
    private val CITIES_DIALOG_FRAGMENT_TAG = "cities_fragment_dialog";
    private var mGender : Int? = null
    private var calendar: Calendar? = null


    private val adapter by lazy { VehicleTypeSelectionAdapter(requireActivity(), rvVehicleTypes, null) }

    companion object {
        @JvmStatic
        fun newInstance(accessToken: String) =
                DriverSetupFragment().apply {
                    arguments = Bundle().apply {
                        putString(Constants.KEY_ACCESS_TOKEN, accessToken)
                    }
                }

        private const val IS_FIRST_ITEM_TITLE = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            accessToken = it.getString(Constants.KEY_ACCESS_TOKEN)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        toolbarChangeListener?.setToolbarText(getString(R.string.register_as_driver))
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
            edtDob.typeface = Fonts.mavenRegular(parentActivity!!)
            tvCities.typeface = Fonts.mavenRegular(parentActivity!!)
            tvGender.typeface = Fonts.mavenRegular(parentActivity!!)
            tvDob.typeface = Fonts.mavenRegular(parentActivity!!)


        calendar = Calendar.getInstance()
        calendar?.timeInMillis = System.currentTimeMillis()

        setSpinnerListGender()
        bContinue.typeface = Fonts.mavenMedium(requireActivity())
        bContinue.setOnClickListener { if (validateData()) checkForPromoCode() }
        editTextName.setOnEditorActionListener { _, _, _ ->
            editTextEmail.requestFocus()
            editTextEmail.setSelection(editTextEmail.text.length)
            true }

        bCancel.typeface = Fonts.mavenMedium(requireActivity())
        bCancel.setOnClickListener { parentActivity?.onBackPressed() }
        tvCities.setOnClickListener{showCountriesDialog(requireActivity().supportFragmentManager)}
        tvCities.paintFlags = tvCities.paintFlags with (Paint.UNDERLINE_TEXT_FLAG)
        with(rvVehicleTypes) {
            layoutManager = GridLayoutManager(requireActivity(), 3)
            addItemDecoration(ItemOffsetDecoration(parentActivity!!, R.dimen.spacing_grid_recycler_view));
            adapter = this@DriverSetupFragment.adapter
        }

        edtDob.setOnClickListener {
            openDatePicker()
        }

        if(Prefs.with(requireActivity()).getInt(Constants.KEY_DRIVER_EMAIL_OPTIONAL, 1) == 0) {
            tvEnterEmail.text = getString(R.string.email)
        }
        getCitiesAPI()

    }

     private fun openDatePicker() {
        val date = DatePickerDialog.OnDateSetListener { view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->

            calendar?.set(Calendar.YEAR, year)
            calendar?.set(Calendar.MONTH, monthOfYear)
            calendar?.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateLabel()
        }

        val datePickerDialog = DatePickerDialog(context, R.style.DatePickerDialogTheme, date, calendar!!.get(Calendar.YEAR), calendar!!.get(Calendar.MONTH), calendar!!.get(Calendar.DAY_OF_MONTH))
         datePickerDialog.datePicker.maxDate = calendar!!.timeInMillis

         datePickerDialog.datePicker.minDate = System.currentTimeMillis() - (3.154e+12).toLong()
         datePickerDialog.show()

     }

    /**
     * formatting and updating
     */
    private fun updateLabel() {
        edtDob.setText(SimpleDateFormat(DOB_DATE_FORMAT, Locale.ENGLISH)
                .format(calendar?.getTime()))
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
        if (editTextName.text.trim().toString().isBlank()) {
            DialogPopup.alertPopup(parentActivity, "", getString(R.string.first_name_required))
            return false
        }

        if (resources.getBoolean(R.bool.last_name_mandatory) && edtLastName.text.trim().toString().isBlank()) {
            DialogPopup.alertPopup(parentActivity, "", getString(R.string.last_name_required))
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
        if (Prefs.with(requireActivity()).getInt(Constants.KEY_DRIVER_GENDER_FILTER, 0) == 1
                && mGender == null) {
            DialogPopup.alertPopup(parentActivity, "", getString(R.string.please_select_gender))
            return false
        }
        if (Prefs.with(requireActivity()).getInt(Constants.KEY_DRIVER_DOB_INPUT, 0) == 1
                && edtDob.text.toString().isEmpty()) {
            DialogPopup.alertPopup(parentActivity, "", getString(R.string.please_enter_date_of_birth))
            return false
        }
        if (!editTextEmail.text.trim().toString().isBlank() && !Utils.isEmailValid(editTextEmail.text.trim().toString())) {
            DialogPopup.alertPopup(parentActivity, "", getString(R.string.please_enter_valid_email))
            return false
        }

        if (adapter.getCurrentSelectedVehicle() == null) {
            DialogPopup.alertPopup(parentActivity, "", getString(R.string.select_vehicle_type))
            return false
        }


        if (cityId == null || cityId == "0") {
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
        val vehicleType = (adapter.getCurrentSelectedVehicle()!!.vehicleType).toString();
        val regionId = (adapter.getCurrentSelectedVehicle()!!.regionId).toString();
        var userName = editTextName.text.trim().toString()
        val lastName = edtLastName.text.trim().toString()
        val dob = edtDob.text.trim().toString()
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
                "vehicle_type" to vehicleType,
                Constants.KEY_REGION_ID to regionId,
                "offering_type" to "" + 1,
                "vehicle_status" to getString(R.string.owned),
                "device_type" to Data.DEVICE_TYPE,
                "device_name" to Data.deviceName,
                "app_version" to "" + Data.appVersion,
                "os_version" to Data.osVersion,
                "country" to Data.country,
                "client_id" to Data.CLIENT_ID,
                "login_type" to Data.LOGIN_TYPE,
                "referral_code" to "",
                "device_token" to FirebaseInstanceId.getInstance().getToken()!!,
                "unique_device_id" to Data.uniqueDeviceId,
                "device_rooted" to if (Utils.isDeviceRooted()) "1" else "0"
        )
        if(referralCode!=null){
            params["referral_code"] = referralCode
        }
        if(!dob.isEmpty()){
            params[Constants.KEY_DATE_OF_BIRTH] = dob
        }
        if(mGender != null){
            params[Constants.KEY_GENDER] = mGender!!.toString()
        }
    HomeUtil.putDefaultParams(params)
    ApiCommonKt<RegisterScreenResponse>(parentActivity!!).execute(params, ApiName.REGISTER_DRIVER, object : APICommonCallbackKotlin<RegisterScreenResponse>() {

            override fun onSuccess(t: RegisterScreenResponse?, message: String?, flag: Int) {
                if (t != null) {
                    Log.d("", t.serverMessage())
                    when (t.flag) {
                        ApiResponseFlags.UPLOAD_DOCCUMENT.getOrdinal(), ApiResponseFlags.ACTION_COMPLETE.getOrdinal() -> {

                            if(Prefs.with(requireActivity()).getInt(Constants.KEY_VEHICLE_MODEL_ENABLED, 0) == 1){
                                (activity as DriverSplashActivity).openVehicleDetails(accessToken,cityId!!,
                                        vehicleType, userName)
                            }else{
                                openDocumentUploadActivity()

                            }

                        }

                        ApiResponseFlags.AUTH_REGISTRATION_FAILURE.getOrdinal() -> {
                            DialogPopup.alertPopup(activity, "", message)
                        }

                        ApiResponseFlags.AUTH_ALREADY_REGISTERED.getOrdinal(), ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() -> {
                            DialogPopup.alertPopupWithListener(activity, "", message, {
                                parentActivity?.openPhoneLoginScreen()
                                parentActivity?.setToolbarVisibility(false)
                            })

                        }
                        else -> DialogPopup.alertPopup(activity, "", message)
                    }
                }
            }

            override fun onError(t: RegisterScreenResponse?, message: String?, flag: Int): Boolean {
                if(flag==ApiResponseFlags.SHOW_MESSAGE.getOrdinal()){
                    DialogPopup.alertPopupWithListener(activity, "", message, {
                        setPromoLayout(true,referralCode)
                        openDocumentUploadActivity()
                    })
                    return true
                }else{
                    return false

                }

            }
        })
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
     private fun setSpinnerListGender() {

         // Spinner Drop down elements
         val categories = java.util.ArrayList<Gender>()
         categories.add(Gender(0, getString(R.string.hint_gender)))
         categories.add(Gender(GenderValues.FEMALE.type, getString(R.string.gender_female)))
         categories.add(Gender(GenderValues.MALE.type, getString(R.string.gender_male)))
         categories.add(Gender(GenderValues.OTHER.type, getString(R.string.gender_others)))

//       // Creating adapter for spinner
         val dataAdapter = DropDownListAdapter(context!!,android.R.layout.simple_spinner_dropdown_item, categories, IS_FIRST_ITEM_TITLE)

//         val spinnerArrayAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, categories)
         spinnerGender?.adapter = dataAdapter
         spinnerGender?.onItemSelectedListener = this
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
                setCityData(t!!.currentCity)
                citiesList = t.cities;
                groupView.visible()
                setPromoLayout(t.getShowPromo(),t.promoCode)
                setupTermsAndConditionsTextView()
                if(Prefs.with(requireActivity()).getInt(Constants.KEY_DRIVER_EMAIL_OPTIONAL, 1) == 0
                        || Prefs.with(requireActivity()).getInt(Constants.KEY_EMAIL_INPUT_AT_SIGNUP, 0) == 1){
                    tvEnterEmail.visible()
                    editTextEmail.visible()
                } else {
                    tvEnterEmail.gone()
                    editTextEmail.gone()
                }
                if(Prefs.with(requireActivity()).getInt(Constants.KEY_DRIVER_GENDER_FILTER, 1) == 1){
                    tvGender.visible()
                    spinnerGender.visible()
                    tvArrow.visible()
                } else {
                    tvGender.gone()
                    spinnerGender.gone()
                    tvArrow.gone()
                }
                if(Prefs.with(requireActivity()).getInt(Constants.KEY_DRIVER_DOB_INPUT, 1) == 1){
                    tvDob.visible()
                    edtDob.visible()
                } else {
                    tvDob.gone()
                    edtDob.gone()
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
            if (promoText != null && !promoText.isBlank()) {
                edtPromo.setText(promoText)
                edtPromo.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_ref_code,0,R.drawable.ic_tick_green_20,0)
                edtPromo.isEnabled = false
            } else {
                edtPromo.isEnabled = true
                edtPromo.setText(null)
                edtPromo.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_ref_code,0,0,0)
            }
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
        parentActivity = context as DriverSplashActivity
        toolbarChangeListener = context as ToolbarChangeListener
    }

    override fun onDetach() {
        parentActivity = null
        toolbarChangeListener = null
        super.onDetach()
    }

    private fun setCityData(city: CityResponse.City?){
        if(city!=null){
            tvCities.text = city.cityName
            cityId = city.cityId.toString()

            var list = city.vehicleTypes
            if(Prefs.with(requireActivity()).getInt(Constants.KEY_DRIVER_GENDER_FILTER, 0) == 1) {
                list = mutableListOf<CityResponse.VehicleType>()
                for (vh in city.vehicleTypes) {
                    if (mGender == null
                            || mGender == 0
                            || vh.applicableGender == 0
                            || mGender == vh.applicableGender) {
                        list.add(vh)
                    }
                }
            }

            adapter.setList(list,0)
            if(list.size == 0){
                rvVehicleTypes.gone()
                Snackbar.make(view!!,getString(R.string.no_vehicles_available),Snackbar.LENGTH_SHORT).show()
            }else{
                rvVehicleTypes.visible()
            }
            citySelected = city
        }else{
            rvVehicleTypes.gone()
            tvCities.text = getString(R.string.label_select_city)
            cityId = null
            citySelected = null

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
    val onCountryPickerListener = object : OnCountryPickerListener<CityResponse.City> {
        override fun onSelectCountry(country: CityResponse.City?) {
                if(country!=null){
                    setCityData(country)
                }
        }

    };
    var citySelected: CityResponse.City? = null

    val countryPickerDialogInteractionListener = object : CountryPickerDialog.CountryPickerDialogInteractionListener<CityResponse.City> {
        override fun getAllCountries(): MutableList<CityResponse.City> {
            return citiesList!!
        }

        override fun sortCountries(searchResults: MutableList<CityResponse.City>?) {

        }

        override fun canSearch(): Boolean {
           return citiesList!=null && citiesList!!.size>7
        }


    };
}

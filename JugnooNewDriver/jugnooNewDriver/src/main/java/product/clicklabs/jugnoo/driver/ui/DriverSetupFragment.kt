package product.clicklabs.jugnoo.driver.ui


import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_driver_info_update.*
import product.clicklabs.jugnoo.driver.*
import product.clicklabs.jugnoo.driver.Constants.KEY_ACCESS_TOKEN
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags
import product.clicklabs.jugnoo.driver.datastructure.VehicleTypeValue
import product.clicklabs.jugnoo.driver.retrofit.model.CityResponse
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse
import product.clicklabs.jugnoo.driver.ui.adapters.VehicleTypeSelectionAdapter
import product.clicklabs.jugnoo.driver.ui.api.APICommonCallback
import product.clicklabs.jugnoo.driver.ui.api.ApiCommon
import product.clicklabs.jugnoo.driver.ui.api.ApiName
import product.clicklabs.jugnoo.driver.utils.*


class DriverSetupFragment : Fragment() {
    private lateinit var parentActivity: DriverSplashActivity

    private lateinit var accessToken: String
    private lateinit var cityId: String
    private lateinit var toolbarChangeListener: ToolbarChangeListener

    private var vehicleTypes = mutableListOf<CityResponse.VehicleType>()


    private val adapter by lazy { VehicleTypeSelectionAdapter(activity, rvVehicleTypes, vehicleTypes) }

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
            accessToken = it.getString(Constants.KEY_ACCESS_TOKEN)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_driver_info_update)
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbarChangeListener.setToolbarText(getString(R.string.register_as_driver))
        toolbarChangeListener.setToolbarVisibility(true)

        // TODO check fonts , replace latoLight with mavenLight(missing)
        tvEnterName.typeface = Fonts.latoLight(activity)
        editTextName.typeface = Fonts.mavenRegular(activity)
        tvSelectVehicle.typeface = Fonts.latoLight(activity)


        bContinue.typeface = Fonts.mavenRegular(activity)
        bContinue.setOnClickListener { if (validateData()) registerDriver() }

        bCancel.typeface = Fonts.mavenRegular(activity)
        bCancel.setOnClickListener { parentActivity.onBackPressed() }


        with(rvVehicleTypes) {
            layoutManager = GridLayoutManager(activity, 3)
            addItemDecoration(ItemOffsetDecoration(parentActivity, R.dimen.spacing_grid_recycler_view));
            adapter = this@DriverSetupFragment.adapter
        }

        getCitiesAPI()
        setupTermsAndConditionsTextView()

    }

    private fun setupTermsAndConditionsTextView() {
        val ss = SpannableString(getString(R.string.by_signing_you_agree))
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                startActivity(Intent(parentActivity, HelpActivity::class.java))
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.isUnderlineText = false
            }
        }
        val start = 31
        val end = 49
        ss.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(ForegroundColorSpan(ContextCompat.getColor(parentActivity, R.color.new_orange1)), start, end, 0);
        tvTermsOfUse.text = ss
        tvTermsOfUse.movementMethod = LinkMovementMethod.getInstance()
        tvTermsOfUse.highlightColor = Color.TRANSPARENT
    }

    override fun onHiddenChanged(hidden: Boolean) {
        if (!hidden) {
            toolbarChangeListener.setToolbarText(getString(R.string.register_as_driver))
            toolbarChangeListener.setToolbarVisibility(true)
        }
        super.onHiddenChanged(hidden)
    }

    private fun validateData(): Boolean {
        if (editTextName.text.trim().toString().isBlank()) {
            DialogPopup.alertPopup(parentActivity, "", getString(R.string.please_enter_your_name))
            return false
        }

        if (adapter.getCurrentSelectedVehicle() == null) {
            DialogPopup.alertPopup(parentActivity, "", getString(R.string.select_vehicle_type))
            return false
        }
        return true
    }

    private fun registerDriver() {
        val params = hashMapOf<String, String>(
                KEY_ACCESS_TOKEN to accessToken,
                "user_name" to editTextName.text.trim().toString(),
                "alt_phone_no" to "",
                "city" to cityId,
                "latitude" to "" + Data.latitude,
                "longitude" to "" + Data.longitude,
                "vehicle_type" to "" + adapter.getCurrentSelectedVehicle()!!.vehicleType,
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
                "device_token" to Data.deviceToken,
                "unique_device_id" to Data.uniqueDeviceId,
                "device_rooted" to if (Utils.isDeviceRooted()) "1" else "0"
        )
        HomeUtil.putDefaultParams(params)
        ApiCommon<RegisterScreenResponse>(parentActivity).execute(params, ApiName.REGISTER_DRIVER, object : APICommonCallback<RegisterScreenResponse>() {

            override fun onSuccess(t: RegisterScreenResponse?, message: String?, flag: Int) {
                if (t != null) {
                    Log.d("", t.message)
                    when (t.flag) {
                        ApiResponseFlags.UPLOAD_DOCCUMENT.getOrdinal(), ApiResponseFlags.ACTION_COMPLETE.getOrdinal() -> {
                            openDocumentUploadActivity()
                        }

                        ApiResponseFlags.AUTH_REGISTRATION_FAILURE.getOrdinal() -> {
                            DialogPopup.alertPopup(activity, "", message)
                        }

                        ApiResponseFlags.AUTH_ALREADY_REGISTERED.getOrdinal(), ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() -> {
                            DialogPopup.alertPopupWithListener(activity, "", message, {
                                parentActivity.openPhoneLoginScreen()
                                parentActivity.setToolbarVisibility(false)
                            })

                        }
                        else -> DialogPopup.alertPopup(activity, "", message)
                    }
                }
            }

            override fun onError(t: RegisterScreenResponse?, message: String?, flag: Int): Boolean {
                DialogPopup.alertPopupWithListener(parentActivity, "", message, { registerDriver() })
                return false
            }
        })
    }

    private fun getCitiesAPI() {
        val params = hashMapOf(
                Constants.KEY_LATITUDE to Data.latitude.toString(),
                Constants.KEY_LONGITUDE to Data.longitude.toString())

        HomeUtil.putDefaultParams(params)

        ApiCommon<CityResponse>(activity).execute(params, ApiName.GET_CITIES, object : APICommonCallback<CityResponse>() {
            override fun onSuccess(t: CityResponse?, message: String?, flag: Int) {
                vehicleTypes = t?.vehicleTypes as ArrayList<CityResponse.VehicleType>
                vehicleTypes.removeAt(0)
                cityId = t.currentCityId

                var defaultIndex : Int = -1

                for ((index, item) in vehicleTypes.withIndex()) {
                    if (item.vehicleType == VehicleTypeValue.AUTOS.value) {
                        item.isSelected = true
                        defaultIndex = index
                        break
                    }
                }

                adapter.setList(vehicleTypes)
                adapter.setCurrentSelectedPos(defaultIndex)
                groupView.visible()
            }

            override fun onError(t: CityResponse?, message: String?, flag: Int): Boolean {
                groupView.gone()
                DialogPopup.alertPopupWithListener(parentActivity, "", message, { getCitiesAPI() })
                return false;
            }
        })
    }

    private fun openDocumentUploadActivity() {
        val intent = Intent(activity, DriverDocumentActivity::class.java).apply {
            putExtra("access_token", accessToken)
            putExtra("in_side", false)
            putExtra("doc_required", 3)
        }
        Utils.enableReceiver(activity, IncomingSmsReceiver::class.java, false)
        startActivity(intent)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        parentActivity = context as DriverSplashActivity
        toolbarChangeListener = context as ToolbarChangeListener
    }
}

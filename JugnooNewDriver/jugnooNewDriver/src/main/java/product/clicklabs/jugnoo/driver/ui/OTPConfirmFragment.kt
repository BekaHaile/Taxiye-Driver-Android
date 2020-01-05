package product.clicklabs.jugnoo.driver.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Paint
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.google.firebase.iid.FirebaseInstanceId
import org.json.JSONObject
import product.clicklabs.jugnoo.driver.*
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags
import product.clicklabs.jugnoo.driver.datastructure.SPLabels
import product.clicklabs.jugnoo.driver.retrofit.RestClient
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse
import product.clicklabs.jugnoo.driver.ui.api.*
import product.clicklabs.jugnoo.driver.utils.*
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import retrofit.mime.TypedByteArray
import java.util.*


class OTPConfirmFragment : Fragment(){

    private lateinit var countryCode: String
    private var missedCallNumber: String? = null
    private lateinit var phoneNumber: String
    private var otpDialog: OtpDialog? = null
    private var countDownTimer: CountDownTimer? = null
    private lateinit var toolbarChangeListener: ToolbarChangeListener
    private lateinit var tvResendOTP: TextView
    private lateinit var btnSubmit: Button
    private lateinit var tvCall: TextView
    private lateinit var edtOTP: EditText
    private lateinit var labelNumber: TextView
    private lateinit var parentActivity: Activity
    private  var mListener: SplashFragment.InteractionListener?  = null;


    companion object {

        const val KEY_PHONE_NUMBER = "phone_number"
        const val KEY_COUNTRY_CODE = "country_code"
        const val MISSED_CALL_NUMBER = "miss_call_number"

        fun newInstance(phoneNumber: String, countryCode: String, missedCallNumber: String?): OTPConfirmFragment {
            val otpConfirmFragment = OTPConfirmFragment()
            val bundle = Bundle()
            bundle.putString(KEY_PHONE_NUMBER, phoneNumber)
            bundle.putString(KEY_COUNTRY_CODE, countryCode)
            if (missedCallNumber != null) bundle.putString(MISSED_CALL_NUMBER, missedCallNumber)
            otpConfirmFragment.arguments = bundle
            return otpConfirmFragment
        }

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        parentActivity = context as Activity
        toolbarChangeListener = context as ToolbarChangeListener
        if(context is SplashFragment.InteractionListener){
            mListener = context
        } else {
            throw IllegalArgumentException(TAG+" Interaction listener required")
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        phoneNumber = arguments!!.getString(KEY_PHONE_NUMBER)
        countryCode = arguments!!.getString(KEY_COUNTRY_CODE)
        if (arguments!!.containsKey(MISSED_CALL_NUMBER)) missedCallNumber = arguments!!.getString(MISSED_CALL_NUMBER)
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.frag_otp_confirm, container, false)

        toolbarChangeListener.setToolbarText(getString(R.string.verification))
        toolbarChangeListener.setToolbarVisibility(true)

        btnSubmit = rootView?.findViewById(R.id.btn_submit) as Button
        tvResendOTP = rootView.findViewById(R.id.tv_resend_otp) as TextView
        tvCall = rootView.findViewById(R.id.tv_call) as TextView
        labelNumber = rootView.findViewById(R.id.label_number) as TextView
        edtOTP = rootView.findViewById(R.id.edt_otp_number) as EditText
        (rootView.findViewById(R.id.label_otp) as TextView).typeface = Fonts.mavenRegular(requireActivity())
        labelNumber.typeface = Fonts.mavenMedium(requireActivity())
        edtOTP.typeface = Fonts.mavenMedium(requireActivity())
        tvResendOTP.typeface = Fonts.mavenRegular(requireActivity())
        btnSubmit.typeface = Fonts.mavenMedium(requireActivity())

        labelNumber.text = "$countryCode $phoneNumber"
        tvResendOTP.paintFlags = labelNumber.paintFlags with (Paint.UNDERLINE_TEXT_FLAG)

        // applyFonts

        parentActivity.let {
            (rootView.findViewById(R.id.label_otp) as TextView).typeface = Fonts.mavenLight(parentActivity)
            labelNumber.typeface = Fonts.mavenRegular(parentActivity)
            edtOTP.typeface = Fonts.mavenRegular(parentActivity)
            tvResendOTP.typeface = Fonts.mavenRegular(parentActivity)
            btnSubmit.typeface = Fonts.mavenRegular(parentActivity)
            tvCall.typeface = Fonts.mavenLight(parentActivity)
        }


        tvResendOTP.setOnClickListener({ generateOTP() })
        btnSubmit.setOnClickListener { verifyOTP(edtOTP.text.toString()) }
        tvCall.setOnClickListener {
            if (missedCallNumber != null && missedCallNumber!!.isNotEmpty()) {

                DialogPopup.alertPopupTwoButtonsWithListeners(this@OTPConfirmFragment.requireActivity(), "",
                        resources.getString(R.string.give_missed_call_dialog_text),
                        resources.getString(R.string.call_us),
                        resources.getString(R.string.cancel),
                        {
                            /* btnLogin.setVisibility(View.VISIBLE)
                             layoutResendOtp.setVisibility(View.GONE)
                             btnReGenerateOtp.setVisibility(View.GONE)*/
                            Utils.openCallIntent(this@OTPConfirmFragment.requireActivity(), missedCallNumber)
                        }, { }, false, false)
            }
        }
        labelNumber.setOnTouchListener(object : View.OnTouchListener {

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                val DRAWABLE_RIGHT = 2

                if (event?.action == MotionEvent.ACTION_DOWN) {
                    if (event.rawX >= (labelNumber.right - labelNumber.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {

                        requireActivity().onBackPressed()


                        return true
                    }
                }
                return false
            }

        })


        if (savedInstanceState==null) {
            if(mListener?.getPrefillOtpIfany()!=null){
                edtOTP.setText(mListener?.getPrefillOtpIfany())
                edtOTP.setSelection(edtOTP.text.length)
            }else{
                startSMSListener()
                showCountDownPopup()
            }
        }

        return rootView

    }

    private fun showCountDownPopup() {
        val builder = OtpDialog.Builder(requireActivity())
                .purpose(AppConstants.OperationType.CALL)
                .isNumberExist(missedCallNumber != null)
                .listener { purpose, _ ->
                    if (purpose == AppConstants.OperationType.CALL) {

                    } else if (purpose == AppConstants.OperationType.ENTER_OTP) {
                    }
                }

        otpDialog = builder.build()
        otpDialog?.show {
            edtOTP.requestFocus();
            Utils.showSoftKeyboard(requireActivity(), edtOTP)
        }


        countDownTimer = CustomCountDownTimer(if (BuildConfig.DEBUG_MODE) 3 * 1000L else 30 * 1000L, 5, object : CustomCountDownTimer.DownTimerOperation {
            override fun updateCounterView(text: String?, width: Double) {
                otpDialog?.updateCounterView(text, width)
            }

            override fun swictchLayout() {
                otpDialog?.swictchLayout()
            }
        })
        countDownTimer?.start()


    }



     fun onOtpReceived(otp: String) {
        if(view!=null){
            edtOTP.setText(otp)
            edtOTP.setSelection(edtOTP.text.length)
            otpDialog?.dismiss()
            countDownTimer?.cancel()
            btnSubmit.performClick()
        }

    }


    private fun verifyOTP(otp: String) {
        Utils.hideSoftKeyboard(parentActivity, edtOTP)
        if (AppStatus.getInstance(requireActivity().applicationContext).isOnline(requireActivity().applicationContext)) {
            val dialogLoading = DialogPopup.showLoadingDialog(requireActivity(), resources.getString(R.string.loading), true)
            val conf = resources.configuration

            if (Data.locationFetcher != null) {
                Data.latitude = Data.locationFetcher.latitude
                Data.longitude = Data.locationFetcher.longitude
            }

            val params = HashMap<String, String>()


            params["phone_no"] = countryCode + phoneNumber
            params["login_otp"] = otp

            FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener{
                if(!it.isSuccessful) {
                    Log.w("otp","device_token_unsuccessful - onReceive",it.exception)
                    return@addOnCompleteListener
                }
                if(it.result?.token != null) {
                    Log.e("${SplashNewActivity.DEVICE_TOKEN_TAG} $TAG + otp -> verifyOTP", it.result?.token)
                    params["device_token"] = it.result?.token!!
                }
                verifyOtpLoginFunc(params, conf, dialogLoading)
            }


        } else {
            DialogPopup.alertPopup(requireActivity(), "", Data.CHECK_INTERNET_MSG)
        }
    }

    private fun verifyOtpLoginFunc(params: HashMap<String, String>, conf: Configuration, dialogLoading: Dialog?) {
        params["device_type"] = Data.DEVICE_TYPE
        params["device_name"] = Data.deviceName
        params["app_version"] = "" + Data.appVersion
        params["os_version"] = Data.osVersion
        params["country"] = Data.country
        params["unique_device_id"] = Data.uniqueDeviceId
        params["latitude"] = "" + Data.latitude
        params["longitude"] = "" + Data.longitude
        params["client_id"] = Data.CLIENT_ID
        params["login_type"] = Data.LOGIN_TYPE
        params["locale"] = conf.locale.toString()
        HomeUtil.putDefaultParams(params)


        if (Utils.isAppInstalled(requireActivity(), Data.GADDAR_JUGNOO_APP)) {
            params["auto_n_cab_installed"] = "1"
        } else {
            params["auto_n_cab_installed"] = "0"
        }

        if (Utils.isAppInstalled(requireActivity(), Data.UBER_APP)) {
            params["uber_installed"] = "1"
        } else {
            params["uber_installed"] = "0"
        }

        if (Utils.telerickshawInstall(requireActivity())) {
            params["telerickshaw_installed"] = "1"
        } else {
            params["telerickshaw_installed"] = "0"
        }


        if (Utils.olaInstall(requireActivity())) {
            params["ola_installed"] = "1"
        } else {
            params["ola_installed"] = "0"
        }

        if (Utils.isDeviceRooted()) {
            params["device_rooted"] = "1"
        } else {
            params["device_rooted"] = "0"

        }

        RestClient.getApiServices().sendLoginValuesRetro(params, object : Callback<RegisterScreenResponse> {
            override fun success(registerScreenResponse: RegisterScreenResponse, response: Response) {
                try {

                    val jsonString = String((response.body as TypedByteArray).bytes)
                    val jObj: JSONObject
                    jObj = JSONObject(jsonString)
                    val flag = jObj.getInt("flag")
                    val message = JSONParser.getServerMessage(jObj)

                    if (!SplashNewActivity.checkIfTrivialAPIErrors(requireActivity(), jObj, flag, null)) {
                        if (ApiResponseFlags.INCORRECT_PASSWORD.getOrdinal() == flag) {
                            DialogPopup.alertPopup(requireActivity(), "", message)
                        } else if (ApiResponseFlags.CUSTOMER_LOGGING_IN.getOrdinal() == flag) {
                            SplashNewActivity.sendToCustomerAppPopup("Alert", message, requireActivity())
                        } else if (ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag) {
                            DialogPopup.alertPopup(requireActivity(), "", message)
                        } else if (ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag) {
                            DialogPopup.alertPopup(requireActivity(), "", message)
                            edtOTP.setText("")
                        } else if (ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag) {
                            DialogPopup.alertPopup(requireActivity(), "", resources.getString(R.string.no_not_verified))
                        } else if (ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag) {


                            if (!SplashNewActivity.checkIfUpdate(jObj.getJSONObject("login"), requireActivity())) {
                                JSONParser().parseAccessTokenLoginData(requireActivity(), jsonString)
                                requireActivity().startService(Intent(requireActivity().applicationContext, DriverLocationUpdateService::class.java))
                                mListener?.goToHomeScreen()
                            }
                        }
    //                            else if () {
    //                                (parentActivity as DriverSplashActivity)
    //                                        .openDriverSetupFragment(jObj.getJSONObject("login").getString("access_token"), countryCode, phoneNumber)
    //                            }
                        else if (ApiResponseFlags.UPLOAD_DOCCUMENT.getOrdinal() == flag) {
                            Prefs.with(requireActivity()).save(Constants.KEY_VEHICLE_MODEL_ENABLED, jObj.getJSONObject("login").optInt(Constants.KEY_VEHICLE_MODEL_ENABLED,
                                    if (resources.getBoolean(R.bool.vehicle_model_enabled)) 1 else 0))
                            val accessToken = jObj.getString("access_token")
                            val reqInactiveDrivers = jObj.optJSONObject(Constants.KEY_LOGIN)?.optInt(Constants.KEY_REQ_INACTIVE_DRIVER, 0)
                            JSONParser.saveAccessToken(requireActivity(), accessToken)
    //                                val intent = Intent(requireActivity(), DriverDocumentActivity::class.java)
    //                                intent.putExtra("access_token", jObj.getString("access_token"))
    //                                intent.putExtra("in_side", false)
    //                                intent.putExtra("doc_required", 3)
    //                                Utils.enableReceiver(requireActivity(), IncomingSmsReceiver::class.java, false)
    //                                startActivity(intent)

                            with(parentActivity as DriverSplashActivity) {
                                if (reqInactiveDrivers == 1) {
                                    Prefs.with(context).save(Constants.KEY_ACCESS_TOKEN,accessToken)
                                    loadTractionFragment(accessToken, true)
                                    setContainerSwitch()
                                } else {
                                    openDriverSetupFragment(accessToken)
                                }
                            }
                        } else {
                            DialogPopup.alertPopup(requireActivity(), "", message)
                        }
                        dialogLoading?.dismiss()
                    } else {
                        dialogLoading?.dismiss()
                    }
                } catch (exception: Exception) {
                    exception.printStackTrace()
                    try {
                        DialogPopup.alertPopup(requireActivity(), "", Data.SERVER_ERROR_MSG)
                    } catch (e: Exception) {
                    }
                }

                dialogLoading?.dismiss()
            }

            override fun failure(error: RetrofitError) {
                dialogLoading?.dismiss()
                DialogPopup.alertPopup(requireActivity(), "", Data.SERVER_NOT_RESOPNDING_MSG)
            }
        })
    }

    private fun generateOTP() {

        val params = HashMap<String, String>()
        params.put(Constants.KEY_PHONE_NO, countryCode + phoneNumber)
        params.put(Constants.KEY_COUNTRY_CODE, countryCode)
        params.put(Constants.LOGIN_TYPE, "1")
        Prefs.with(requireActivity()).save(SPLabels.DRIVER_LOGIN_PHONE_NUMBER, phoneNumber)
        Prefs.with(requireActivity()).save(SPLabels.DRIVER_LOGIN_TIME, System.currentTimeMillis())
        ApiCommon<RegisterScreenResponse>(requireActivity()).execute(params, ApiName.GENERATE_OTP, object : APICommonCallback<RegisterScreenResponse>() {
            override fun onNotConnected(): Boolean {
                return false
            }

            override fun onException(e: java.lang.Exception?): Boolean {
                return false
            }

            override fun onSuccess(t: RegisterScreenResponse?, message: String?, flag: Int) {
                if (flag == ApiResponseFlags.ACTION_COMPLETE.ordinal) {
                    missedCallNumber = t?.missedCallNumber
                    showCountDownPopup()
                } else {
                    DialogPopup.alertPopup(requireActivity(), "", message)
                }


            }

            override fun onError(t: RegisterScreenResponse?, message: String?, flag: Int): Boolean {

                return false
            }

            override fun onFailure(error: RetrofitError?): Boolean {
                return false
            }

            override fun onDialogClick() {

            }


        }
        )

        //hit for phone number
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            toolbarChangeListener.setToolbarText(getString(R.string.verification))
            toolbarChangeListener.setToolbarVisibility(true)
        }else{
        }

    }




      fun isWaitingForOTPDetection(): Boolean{

       return otpDialog!=null && otpDialog!!.isShown();
    }

    override fun onDestroyView() {
        otpDialog?.dismiss()
        if (::smsReceiver.isInitialized) {
            try {
                requireActivity().unregisterReceiver(smsReceiver)
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
        }
        super.onDestroyView()

    }

    private fun startSMSListener() {
        val client = SmsRetriever.getClient(requireActivity())
        val task = client.startSmsRetriever()
        task.addOnSuccessListener{
            smsReceiver = createSmsBroadcastReceiver()
            requireActivity().registerReceiver(smsReceiver, IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION))
        }
        task.addOnFailureListener{ }

    }

    private lateinit var smsReceiver: BroadcastReceiver

    private fun createSmsBroadcastReceiver() = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null && intent.extras != null
                    && SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.action)) {
                val extras = intent.extras
                val status = extras!!.get(SmsRetriever.EXTRA_STATUS) as Status
                if (status != null)
                    when (status.statusCode) {
                        CommonStatusCodes.SUCCESS -> {
                            val message = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                            if (message != null) {
                                onOtpReceived(Utils.retrieveOTPFromSMS(message))
                            }
                        }

                        CommonStatusCodes.TIMEOUT -> {
                        }
                    }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        toolbarChangeListener.setToolbarVisibility(true)
    }


}



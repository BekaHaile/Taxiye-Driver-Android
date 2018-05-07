package product.clicklabs.jugnoo.driver.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Paint
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import org.json.JSONObject
import product.clicklabs.jugnoo.driver.*
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags
import product.clicklabs.jugnoo.driver.datastructure.SPLabels
import product.clicklabs.jugnoo.driver.retrofit.RestClient
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse
import product.clicklabs.jugnoo.driver.ui.api.APICommonCallback
import product.clicklabs.jugnoo.driver.ui.api.ApiCommon
import product.clicklabs.jugnoo.driver.ui.api.ApiName
import product.clicklabs.jugnoo.driver.utils.*
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import retrofit.mime.TypedByteArray
import java.util.*


class OTPConfirmFragment : Fragment() {

    private lateinit var countryCode: String
    private var missedCallNumber: String? = null
    private lateinit var phoneNumber: String
    private var otpDialog: OtpDialog? = null
    private var countDownTimer: CountDownTimer? = null

    private lateinit var tvResendOTP: TextView
    private lateinit var btnSubmit: Button
    private lateinit var tvCall: TextView
    private lateinit var edtOTP: EditText
    private lateinit var labelNumber: TextView
    private lateinit var parentActivity: Activity


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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        phoneNumber = arguments.getString(KEY_PHONE_NUMBER)
        countryCode = arguments.getString(KEY_COUNTRY_CODE)
        if (arguments.containsKey(MISSED_CALL_NUMBER)) missedCallNumber = arguments.getString(MISSED_CALL_NUMBER)
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater?.inflate(R.layout.frag_otp_confirm, container, false)

        (parentActivity as DriverSplashActivity).setToolbarText(getString(R.string.verification))
        (parentActivity as DriverSplashActivity).setToolbarVisibility(true)

        btnSubmit = rootView?.findViewById(R.id.btn_submit) as Button
        tvResendOTP = rootView.findViewById(R.id.tv_resend_otp) as TextView
        tvCall = rootView.findViewById(R.id.tv_call) as TextView
        labelNumber = rootView.findViewById(R.id.label_number) as TextView
        edtOTP = rootView.findViewById(R.id.edt_otp_number) as EditText

        labelNumber.text = "$countryCode $phoneNumber"
        tvResendOTP.paintFlags = labelNumber.paintFlags with (Paint.UNDERLINE_TEXT_FLAG)

        tvResendOTP.setOnClickListener({ generateOTP() })
        btnSubmit.setOnClickListener({ verifyOTP(edtOTP.text.toString()) })
        tvCall.setOnClickListener({
            if (missedCallNumber != null && missedCallNumber!!.isNotEmpty()) {

                DialogPopup.alertPopupTwoButtonsWithListeners(this@OTPConfirmFragment.activity, "",
                        resources.getString(R.string.give_missed_call_dialog_text),
                        resources.getString(R.string.call_us),
                        resources.getString(R.string.cancel),
                        {
                            /* btnLogin.setVisibility(View.VISIBLE)
                             layoutResendOtp.setVisibility(View.GONE)
                             btnReGenerateOtp.setVisibility(View.GONE)*/
                            Utils.openCallIntent(this@OTPConfirmFragment.activity, missedCallNumber)
                        }, { }, false, false)
            }
        })
        labelNumber.setOnTouchListener(object : View.OnTouchListener {

            override fun onTouch(v: View?, event: MotionEvent?): Boolean {

                val DRAWABLE_RIGHT = 2

                if (event?.action == MotionEvent.ACTION_DOWN) {
                    if (event.rawX >= (labelNumber.right - labelNumber.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {

                        activity.onBackPressed()


                        return true
                    }
                }
                return false
            }

        })


        showCountDownPopup()
        return rootView

    }

    private fun showCountDownPopup() {
        val builder = OtpDialog.Builder(activity)
                .purpose(AppConstants.OperationType.CALL)
                .isNumberExist(missedCallNumber != null)
                .listener({ purpose, _ ->
                    if (purpose == AppConstants.OperationType.CALL) {

                    } else if (purpose == AppConstants.OperationType.ENTER_OTP) {

                    }
                })

        otpDialog = builder.build()
        otpDialog?.show()


        countDownTimer = CustomCountDownTimer(if (BuildConfig.DEBUG_MODE) 3 * 1000 else 30 * 1000, 5, object : CustomCountDownTimer.DownTimerOperation {
            override fun updateCounterView(text: String?, width: Double) {
                otpDialog?.updateCounterView(text, width)
            }

            override fun swictchLayout() {
                otpDialog?.swictchLayout()
            }
        })
        countDownTimer?.start()


    }


    //Using Rx
    private fun retrieveOTPFromSMS(intent: Intent) {
        try {
            var otp = ""
            if (intent.hasExtra("message")) {
                val message = intent.getStringExtra("message")

                if (message.toLowerCase().contains("paytm")) {
                    otp = message.split("\\ ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                } else {
                    val arr = message.split("and\\ it\\ is\\ valid\\ till\\ ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val arr2 = arr[0].split("Dear\\ Driver\\,\\ Your\\ One\\ Time\\ Password\\ is\\ ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    otp = arr2[1]
                    otp = otp.replace("\\ ".toRegex(), "")
                }
            }
            if (Utils.checkIfOnlyDigits(otp)) {
                if (!"".equals(otp, ignoreCase = true)) {
                    if (java.lang.Boolean.parseBoolean(Prefs.with(activity).getString(SPLabels.REQUEST_LOGIN_OTP_FLAG, "false"))) {
                        edtOTP.setText(otp)
                        edtOTP.setSelection(edtOTP.text.length)
                        otpDialog?.dismiss()
                        countDownTimer?.cancel()


                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun verifyOTP(otp: String) {

        if (AppStatus.getInstance(activity.applicationContext).isOnline(activity.applicationContext)) {
            val dialogLoading = DialogPopup.showLoadingDialog(activity, resources.getString(R.string.loading), true)
            val conf = resources.configuration

            if (Data.locationFetcher != null) {
                Data.latitude = Data.locationFetcher.latitude
                Data.longitude = Data.locationFetcher.longitude
            }

            val params = HashMap<String, String>()


            params["phone_no"] = countryCode + phoneNumber
            params["login_otp"] = otp
            params["device_token"] = Data.deviceToken
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


            if (Utils.isAppInstalled(activity, Data.GADDAR_JUGNOO_APP)) {
                params["auto_n_cab_installed"] = "1"
            } else {
                params["auto_n_cab_installed"] = "0"
            }

            if (Utils.isAppInstalled(activity, Data.UBER_APP)) {
                params["uber_installed"] = "1"
            } else {
                params["uber_installed"] = "0"
            }

            if (Utils.telerickshawInstall(activity)) {
                params["telerickshaw_installed"] = "1"
            } else {
                params["telerickshaw_installed"] = "0"
            }


            if (Utils.olaInstall(activity)) {
                params["ola_installed"] = "1"
            } else {
                params["ola_installed"] = "0"
            }

            if (Utils.isDeviceRooted()) {
                params["device_rooted"] = "1"
            } else {
                params["device_rooted"] = "0"

            }

            RestClient.getApiServices().sendLoginValuesRetro(params, object : Callback<product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse> {
                override fun success(registerScreenResponse: product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse, response: Response) {
                    try {

                        val jsonString = String((response.body as TypedByteArray).bytes)
                        val jObj: JSONObject
                        jObj = JSONObject(jsonString)
                        val flag = jObj.getInt("flag")
                        val message = JSONParser.getServerMessage(jObj)

                        if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag, null)) {
                            if (ApiResponseFlags.INCORRECT_PASSWORD.getOrdinal() == flag) {
                                DialogPopup.alertPopup(activity, "", message)
                            } else if (ApiResponseFlags.CUSTOMER_LOGGING_IN.getOrdinal() == flag) {
                                SplashNewActivity.sendToCustomerAppPopup("Alert", message, activity)
                            } else if (ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag) {
                                DialogPopup.alertPopup(activity, "", message)
                            } else if (ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag) {
                                DialogPopup.alertPopup(activity, "", message)
                                edtOTP.setText("")
                            } else if (ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag) {
                                DialogPopup.alertPopup(activity, "", resources.getString(R.string.no_not_verified))
                            } else if (ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag) {
                                if (!SplashNewActivity.checkIfUpdate(jObj.getJSONObject("login"), activity)) {
                                    JSONParser().parseAccessTokenLoginData(activity, jsonString)
                                    activity.startService(Intent(activity.applicationContext, DriverLocationUpdateService::class.java))
                                    Utils.enableReceiver(activity, IncomingSmsReceiver::class.java, false)
                                    startActivity(Intent(activity, HomeActivity::class.java))
                                    activity.finish()
                                    activity.overridePendingTransition(R.anim.right_in, R.anim.right_out)
                                }
                            }
//                            else if () {
//                                (parentActivity as DriverSplashActivity)
//                                        .openDriverSetupFragment(jObj.getJSONObject("login").getString("access_token"), countryCode, phoneNumber)
//                            }
                            else if (ApiResponseFlags.UPLOAD_DOCCUMENT.getOrdinal() == flag) {
                                val accessToken = jObj.getString("access_token")
                                JSONParser.saveAccessToken(activity, accessToken)
//                                val intent = Intent(activity, DriverDocumentActivity::class.java)
//                                intent.putExtra("access_token", jObj.getString("access_token"))
//                                intent.putExtra("in_side", false)
//                                intent.putExtra("doc_required", 3)
//                                Utils.enableReceiver(activity, IncomingSmsReceiver::class.java, false)
//                                startActivity(intent)

                                (parentActivity as DriverSplashActivity)
                                        .openDriverSetupFragment(accessToken)
                            } else {
                                DialogPopup.alertPopup(activity, "", message)
                            }
                            dialogLoading?.dismiss()
                        } else {
                            dialogLoading?.dismiss()
                        }
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                        DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG)
                    }

                    dialogLoading?.dismiss()
                }

                override fun failure(error: RetrofitError) {
                    dialogLoading?.dismiss()
                    DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG)
                }
            })

        } else {
            DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG)
        }
    }

    private fun generateOTP() {

        val params = HashMap<String, String>()
        params.put(Constants.KEY_PHONE_NO, countryCode + phoneNumber)
        params.put(Constants.KEY_COUNTRY_CODE, countryCode)
        params.put(Constants.LOGIN_TYPE, "1")
        Prefs.with(activity).save(SPLabels.DRIVER_LOGIN_PHONE_NUMBER, phoneNumber)
        Prefs.with(activity).save(SPLabels.DRIVER_LOGIN_TIME, System.currentTimeMillis())
        ApiCommon<RegisterScreenResponse>(activity).execute(params, ApiName.GENERATE_OTP, object : APICommonCallback<RegisterScreenResponse>() {
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
                    DialogPopup.alertPopup(activity, "", message)
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


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        parentActivity = context as Activity
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(activity).registerReceiver(broadcastReceiver,
                IntentFilter(Constants.INTENT_ACTION_NEW_MESSAGE))
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(broadcastReceiver)
    }

    private var broadcastReceiver:BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent != null && intent.action.equals(Constants.INTENT_ACTION_NEW_MESSAGE, ignoreCase = true)) {
                retrieveOTPFromSMS(intent)
            }
        }
    };

}



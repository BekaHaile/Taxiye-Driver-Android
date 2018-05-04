package product.clicklabs.jugnoo.driver.ui


import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.InputType
import android.text.TextUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.picker.Country
import com.picker.CountryPicker
import com.picker.OnCountryPickerListener
import product.clicklabs.jugnoo.driver.*
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags
import product.clicklabs.jugnoo.driver.datastructure.DriverDebugOpenMode
import product.clicklabs.jugnoo.driver.datastructure.SPLabels
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse
import product.clicklabs.jugnoo.driver.ui.api.*
import product.clicklabs.jugnoo.driver.ui.models.DriverLanguageResponse
import product.clicklabs.jugnoo.driver.utils.*
import product.clicklabs.jugnoo.driver.utils.DialogPopup.dialog
import retrofit.RetrofitError
import java.lang.Exception
import java.util.*

class LoginFragment : Fragment() {

    lateinit var parentActivity: Activity
    val TAG = LoginFragment::class.simpleName
    lateinit var rootView: View
    lateinit var spinner: Spinner
    lateinit var selectedLanguage: String


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater?.inflate(R.layout.frag_login_test, container, false)
        selectedLanguage = (activity as DriverSplashActivity).selectedLanguage
        spinner = rootView?.findViewById(R.id.language_spinner) as Spinner
        val tvCountryCode by bind<TextView>(R.id.tvCountryCode, rootView)
        val edtPhoneNo by bind<EditText>(R.id.edt_phone, rootView)
        val btnGenerateOtp by bind<Button>(R.id.btn_gen_otp, rootView)

        val countryPicker = CountryPicker.Builder().with(activity).listener(object : OnCountryPickerListener {
            override fun onSelectCountry(country: Country?) {
                tvCountryCode.text = country?.dialCode
            }
        }).build()
        tvCountryCode.setOnClickListener({ countryPicker.showDialog(activity.supportFragmentManager) })
        btnGenerateOtp.setOnClickListener(View.OnClickListener {
            val phoneNo: String = edtPhoneNo.text.trim().toString()
            val countryCode: String = tvCountryCode.text.trim().toString()
            if (phoneNo.length < 0) {
                return@OnClickListener
            }

            if (TextUtils.isEmpty(countryCode)) {
                Toast.makeText(this@LoginFragment.activity, getString(R.string.please_select_country_code), Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            if (!Utils.validPhoneNumber(phoneNo)) {
                Toast.makeText(this@LoginFragment.activity, getString(R.string.enter_valid_phone_number), Toast.LENGTH_SHORT).show()
                return@OnClickListener

            }


            val params = HashMap<String, String>()
            params.put(Constants.KEY_PHONE_NO, countryCode + phoneNo)
            params.put(Constants.KEY_COUNTRY_CODE, countryCode)
            params.put(Constants.LOGIN_TYPE, "1")
            params[Constants.KEY_COUNTRY_CODE] = countryCode
            params["device_token"] = Data.deviceToken
            params["unique_device_id"] = Data.uniqueDeviceId
            params["device_name"] = Data.deviceName
            params["device_type"] = Data.DEVICE_TYPE
            params["app_version"] = "" + Data.appVersion
            params["os_version"] = Data.osVersion
            params["latitude"] = "" + Data.latitude
            params["longitude"] = "" + Data.longitude
            params["login_type"] = Data.LOGIN_TYPE
            if (Utils.isDeviceRooted()) {
                params["device_rooted"] = "1"
            } else {
                params["device_rooted"] = "0"
            }



            Prefs.with(activity).save(SPLabels.DRIVER_LOGIN_PHONE_NUMBER, phoneNo)
            Prefs.with(activity).save(SPLabels.DRIVER_LOGIN_TIME, System.currentTimeMillis())
            ApiCommon<RegisterScreenResponse>(activity).execute(params, ApiName.GENERATE_OTP, object : APICommonCallback<RegisterScreenResponse>() {
                override fun onNotConnected(): Boolean {
                    return false
                }

                override fun onException(e: Exception?): Boolean {
                    return false
                }

                override fun onSuccess(t: RegisterScreenResponse?, message: String?, flag: Int) {
                    if (flag == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                        (activity as DriverSplashActivity).addLoginViaOTPScreen(phoneNo, countryCode, t?.missedCallNumber)
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


        })

        getLanguageList()

        return rootView
    }

    private fun getLanguageList() {
        val params = HashMap<String, String>()
        params["device_model_name"] = android.os.Build.MODEL
        params["android_version"] = android.os.Build.VERSION.RELEASE
        HomeUtil.putDefaultParams(params)

        ApiCommonKotlin<DriverLanguageResponse>(activity).execute(params, ApiName.GET_LANGUAGES, object : APICommonCallbackKotlin<DriverLanguageResponse>() {
            override fun onSuccess(t: DriverLanguageResponse?, message: String?, flag: Int) {


                if (t?.languageList != null && t.languageList.size > 0) {
                    val dataAdapter: ArrayAdapter<String> = ArrayAdapter(activity, android.R.layout.simple_spinner_item, t.languageList)
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.adapter = dataAdapter
                    spinner.visibility = View.VISIBLE

                    if (!t.languageList.contains(selectedLanguage)) {
                        t.languageList.add(selectedLanguage)
                    }
                    spinner.setSelection(t.languageList.indexOf(selectedLanguage))
                    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            val item = parent?.getItemAtPosition(position).toString()
                            if (!item.equals(selectedLanguage, ignoreCase = true)) {
                                (activity as DriverSplashActivity).updateLanguage(selectedLanguage)
                            }
                        }

                    }


                } else {
                    spinner.visibility = View.GONE
                }


            }

            override fun onError(t: DriverLanguageResponse?, message: String?, flag: Int): Boolean {
                return false
            }

        })
    }

    private fun confirmDebugPasswordPopup(mode: DriverDebugOpenMode) {

        val codeDialog = Dialog(parentActivity, android.R.style.Theme_Translucent_NoTitleBar)
        dialog.window!!.attributes.windowAnimations = R.style.Animations_LoadingDialogFade
        dialog.setContentView(R.layout.dialog_edittext)

        val frameLayout = dialog.findViewById(R.id.rv) as FrameLayout
        ASSL(activity, frameLayout, 1134, 720, true)

        val layoutParams = dialog.window!!.attributes
        layoutParams.dimAmount = 0.6f

        with(dialog) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            setCancelable(true)
            setCanceledOnTouchOutside(true)
        }

        val textHead = dialog.findViewById(R.id.textHead) as TextView
        textHead.typeface = Data.latoRegular(parentActivity)

        val textMessage = dialog.findViewById(R.id.textMessage) as TextView
        textMessage.typeface = Data.latoRegular(parentActivity)

        val etCode = dialog.findViewById(R.id.etCode) as EditText
        etCode.typeface = Data.latoRegular(parentActivity)

        textHead.text = if (DriverDebugOpenMode.DEBUG == mode) "Confirm Debug Password" else "Confirm Register Password"
        textMessage.gone()

        val btnConfirm = dialog.findViewById(R.id.btnConfirm) as Button
        btnConfirm.typeface = Data.latoRegular(parentActivity)
        btnConfirm.setOnClickListener {
            val code = etCode.text.toString().trim()

            if (code.isBlank()) {
                etCode.requestFocus()
                etCode.error = "Code can't be empty."
            } else {
                if (DriverDebugOpenMode.DEBUG == mode && Data.DEBUG_PASSWORD == code) {
                    dialog.dismiss()
                    changeServerLinkPopup()

                }
            }
        }

        codeDialog.show()
    }

    private fun setServerLink(link: String) {
        val preferences = parentActivity.getSharedPreferences(Data.SETTINGS_SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putString(Data.SP_SERVER_LINK, link)
        editor.commit()

        MyApplication.getInstance().initializeServerURLAndRestClient(parentActivity)
    }

    private fun changeServerLinkPopup() {

        val preferences = activity.getSharedPreferences(Data.SETTINGS_SHARED_PREF_NAME, Context.MODE_PRIVATE)
        val link = preferences.getString(Data.SP_SERVER_LINK, Data.DEFAULT_SERVER_URL)

        val textMessage = when {
            link.equals(Data.TRIAL_SERVER_URL, ignoreCase = true) -> "Current server is SALES.\nChange to:"
            link.equals(Data.LIVE_SERVER_URL, ignoreCase = true) -> "Current server is LIVE.\nChange to:"
            link.equals(Data.DEV_SERVER_URL, ignoreCase = true) -> "Current server is DEV.\nChange to:"
            else -> ""
        }

        DialogPopup.alertPopupThreeButtonsWithListeners(activity, "", textMessage, "LIVE", "DEV", "CUSTOM",
                { setServerLink(Data.LIVE_SERVER_URL) }, { Data.DEV_SERVER_URL },
                {
                    saveCustomURLDialog(parentActivity, InputType.TYPE_CLASS_TEXT, "Custom URL", "Please enter Custom URL", "URL can't be empty.", { code ->
                        Prefs.with(activity).save(SPLabels.CUSTOM_SERVER_URL, code)
                        val editor = preferences.edit()
                        editor.putString(Data.SP_SERVER_LINK, code)
                        editor.commit()

                        MyApplication.getInstance().initializeServerURLAndRestClient(activity)
                        dialog.dismiss()
                    })
                }, true, true)
    }

    private fun saveCustomURLDialog(activity: Activity, inputType: Int, title: String, message: String, errorMessage: String, onClick: (String) -> Unit) {

        val dialog = Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar)
        dialog.window!!.attributes.windowAnimations = R.style.Animations_LoadingDialogFade
        dialog.setContentView(R.layout.dialog_edittext)

        val layoutParams = dialog.window!!.attributes
        layoutParams.dimAmount = 0.6f
        dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)


        val textHead = dialog.findViewById(R.id.textHead) as TextView
        textHead.typeface = Data.latoRegular(activity)
        val textMessage = dialog.findViewById(R.id.textMessage) as TextView
        textMessage.typeface = Data.latoRegular(activity)
        val etCode = dialog.findViewById(R.id.etCode) as EditText
        etCode.typeface = Data.latoRegular(activity)

        etCode.inputType = inputType
        etCode.setTextSize(TypedValue.COMPLEX_UNIT_PX, 30f)

        etCode.setText(Prefs.with(activity).getString(SPLabels.CUSTOM_SERVER_URL, Data.SERVER_URL))

        val frameLayout = dialog.findViewById(R.id.rv) as FrameLayout
        ASSL(activity, frameLayout, 1134, 720, true)


        textHead.text = title
        if (message.isBlank()) textMessage.gone() else textMessage.text = message

        val btnConfirm = dialog.findViewById(R.id.btnConfirm) as Button
        btnConfirm.typeface = Data.latoRegular(activity)

        btnConfirm.setOnClickListener {
            val code = etCode.text.toString().trim()
            if ("".equals(code, ignoreCase = true)) {
                etCode.requestFocus()
                etCode.error = errorMessage
            } else {
                onClick.invoke(code)
            }
        }


        etCode.setOnEditorActionListener({ _, actionId, _ ->
            val result = actionId and EditorInfo.IME_MASK_ACTION
            when (result) {
                EditorInfo.IME_ACTION_DONE -> btnConfirm.performClick()

                EditorInfo.IME_ACTION_NEXT -> {
                }
            }
            true
        })

        dialog.findViewById(R.id.rv).setOnClickListener { dialog.dismiss() }

        dialog.show()
        etCode.setSelection(etCode.text.length)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        parentActivity = context as Activity
    }
}

package product.clicklabs.jugnoo.driver.ui


import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.annotation.StringRes
import android.support.transition.TransitionManager
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.transition.Transition
import android.transition.TransitionSet
import android.util.TypedValue
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.picker.Country
import com.picker.CountryPicker
import com.picker.OnCountryPickerListener
import kotlinx.android.synthetic.main.dialog_edittext.*
import kotlinx.android.synthetic.main.frag_login.*
import kotlinx.android.synthetic.main.frag_login.view.*
import product.clicklabs.jugnoo.driver.*
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags
import product.clicklabs.jugnoo.driver.datastructure.DriverDebugOpenMode
import product.clicklabs.jugnoo.driver.datastructure.SPLabels
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse
import product.clicklabs.jugnoo.driver.ui.api.APICommonCallbackKotlin
import product.clicklabs.jugnoo.driver.ui.api.ApiCommonKt
import product.clicklabs.jugnoo.driver.ui.api.ApiName
import product.clicklabs.jugnoo.driver.ui.models.DriverLanguageResponse
import product.clicklabs.jugnoo.driver.ui.models.LocaleModel
import product.clicklabs.jugnoo.driver.utils.*
import java.lang.Exception
import java.util.*
import com.google.firebase.iid.FirebaseInstanceId

class LoginFragment : Fragment() {
    private var mListener: SplashFragment.InteractionListener? = null


    companion object {
        private const val IS_SHARED_TRANSITION_ENABLED = "is_shared_transition_enabled"


    }

    private lateinit var parentActivity: Activity
    val TAG = LoginFragment::class.simpleName
    lateinit var rootView: View
    lateinit var selectedLanguage: String
    private lateinit var toolbarChangeListener: ToolbarChangeListener
    private var applyTransition = false;
    private var handler = Handler()


    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        if (activity is SplashFragment.InteractionListener) {
            mListener = activity;
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.frag_login, container, false)!!

        if(sharedElementEnterTransition!=null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                addTransitionEndListenerAndAnimateView()
            }
        }else{
            animateViews()
        }

        selectedLanguage = BaseFragmentActivity.selectedLanguage
        toolbarChangeListener.setToolbarVisibility(false)

        val countryPicker = CountryPicker.Builder().with(activity).listener(object : OnCountryPickerListener<Country>{
            override fun onSelectCountry(country: Country?) {
                rootView.tvCountryCode.text = country?.dialCode
            }
        }).build()

        with(rootView) {

            //apply fonts

            tvLanguage.typeface = Fonts.mavenLight(parentActivity)
            tvLabel.typeface = Fonts.mavenRegular(parentActivity)
            tvCountryCode.typeface = Fonts.mavenRegular(parentActivity)
            edtPhoneNo.typeface = Fonts.mavenRegular(parentActivity)
            btnGenerateOtp.typeface = Fonts.mavenRegular(parentActivity)


            imageView.setOnLongClickListener {
                confirmDebugPasswordPopup(DriverDebugOpenMode.DEBUG)
                FlurryEventLogger.debugPressed("no_token")
                return@setOnLongClickListener false
            }
            tvCountryCode.text = Utils.getCountryCode(parentActivity)
            tvCountryCode.setOnClickListener({ countryPicker.showDialog(activity.supportFragmentManager) })

            edtPhoneNo.addTextChangedListener(object: TextWatcher{
                override fun afterTextChanged(p0: Editable?) {
                    val s = p0?.toString() ?: ""
                    if (s.startsWith("0")) {
                        if (s.length > 1) {
                            edtPhoneNo.setText(s.toString().substring(1))
                        } else {
                            edtPhoneNo.setText("")
                        }
                        Toast.makeText(parentActivity, context.getString(R.string.number_should_not_start_with_zero), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }
            })

            btnGenerateOtp.setOnClickListener(View.OnClickListener {
                val phoneNo: String = rootView.edtPhoneNo.text.trim().toString()
                val countryCode: String = rootView.tvCountryCode.text.trim().toString()
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

                mListener?.registerForSmsReceiver(true);
                Utils.enableReceiver(activity, IncomingSmsReceiver::class.java, true)

                val params = HashMap<String, String>()
                params[Constants.KEY_PHONE_NO] = countryCode + phoneNo
                params[Constants.KEY_COUNTRY_CODE] = countryCode
                params[Constants.LOGIN_TYPE] = "1"
                params[Constants.KEY_COUNTRY_CODE] = countryCode
                params["device_token"] = FirebaseInstanceId.getInstance().getToken()!!
                params["unique_device_id"] = Data.uniqueDeviceId
                params["device_name"] = Data.deviceName
                params["device_type"] = Data.DEVICE_TYPE
                params["app_version"] = "" + Data.appVersion
                params["os_version"] = Data.osVersion
                params["latitude"] = "" + Data.latitude
                params["longitude"] = "" + Data.longitude
                params["login_type"] = Data.LOGIN_TYPE
                params["device_rooted"] = if (Utils.isDeviceRooted()) "1" else "0"


                Prefs.with(activity).save(SPLabels.DRIVER_LOGIN_PHONE_NUMBER, phoneNo)
                Prefs.with(activity).save(SPLabels.DRIVER_LOGIN_TIME, System.currentTimeMillis())
                Utils.hideSoftKeyboard(parentActivity, rootView.edtPhoneNo)
                ApiCommonKt<RegisterScreenResponse>(activity).execute(params, ApiName.GENERATE_OTP, object : APICommonCallbackKotlin<RegisterScreenResponse>() {
                    override fun onNotConnected(): Boolean {
                        return false
                    }

                    override fun onException(e: Exception?): Boolean {
                        return false
                    }

                    override fun onSuccess(t: RegisterScreenResponse?, message: String?, flag: Int) {
                        if (flag == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                            (activity as DriverSplashActivity).addLoginViaOTPScreen(phoneNo, countryCode, t?.missedCallNumber, t?.otpLength)
                        } else {
                            DialogPopup.alertPopup(activity, "", message)
                        }


                    }

                    override fun onError(t: RegisterScreenResponse?, message: String?, flag: Int): Boolean {

                        return false
                    }

                })
            })
            tvLanguage.setOnClickListener { getLanguageList(true) }

            if(edtPhoneNo.tag!=null && (edtPhoneNo.tag is String) &&
                    (edtPhoneNo.tag as String)==resources.getInteger(R.integer.tag_scroll_down_on_touch).toString()){

                edtPhoneNo.setOnTouchListener(object: View.OnTouchListener{
                    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                        this@LoginFragment.handler.postDelayed(showKeyboardRunnable,200);

                        return false
                    }

                })
            }

        }

        return rootView
    }
    val  showKeyboardRunnable = Runnable{
        if(scrollView!=null ){
            scrollView.fullScroll(View.FOCUS_DOWN)
        }
    }

    private fun getLanguageList(showError: Boolean) {
        val params = HashMap<String, String>()
        params["device_model_name"] = android.os.Build.MODEL
        params["android_version"] = android.os.Build.VERSION.RELEASE
        HomeUtil.putDefaultParams(params)


        setLanguageLoading(text = R.string.languages)

        ApiCommonKt<DriverLanguageResponse>(parentActivity, showLoader = false, checkForActionComplete = true)
                .execute(params, ApiName.GET_LANGUAGES, object : APICommonCallbackKotlin<DriverLanguageResponse>() {
                    override fun onSuccess(t: DriverLanguageResponse?, message: String?, flag: Int) {

                        if (t?.languageList == null || t.languageList.size == 0) {
                            onError(t, getString(R.string.select_language), flag)
                            return
                        }

                        setLanguageLoading(text = -1, showText = false, showProgress = false)

                        val dataAdapter: ArrayAdapter<LocaleModel> = LanguageAdapter(parentActivity, android.R.layout.simple_spinner_item, t.languageList)
                        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        rootView.language_spinner.adapter = dataAdapter
                        if (resources.getInteger(R.integer.show_language_control) == resources.getInteger(R.integer.view_visible)) rootView.language_spinner.visible() else rootView.language_spinner.gone()

                        if (t.defaultLang != null && Prefs.with(context).getString(Constants.KEY_DEFAULT_LANG, "eee").equals("eee")) {
                            Prefs.with(context).save(Constants.KEY_DEFAULT_LANG, t.defaultLang)
                            selectedLanguage = t.defaultLang
                            BaseFragmentActivity.updateLanguage(activity, t.defaultLang)
                        }

                        if (!t.languageList.contains(LocaleModel(selectedLanguage, ""))) {
                            selectedLanguage = t.languageList.get(0).locale!!
                            BaseFragmentActivity.updateLanguage(activity, selectedLanguage)
                        }
                        rootView.language_spinner.setSelection(t.languageList.indexOf(LocaleModel(selectedLanguage, "")))
                        rootView.language_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }

                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                val item = parent?.getItemAtPosition(position) as LocaleModel
                                if (!item.equals(LocaleModel(selectedLanguage, ""))) {
                                    selectedLanguage = item.locale!!
                                    BaseFragmentActivity.updateLanguage(activity, selectedLanguage)
                                }
                            }
                        }

                    }

                    override fun onError(t: DriverLanguageResponse?, message: String?, flag: Int): Boolean {
                        rootView.language_spinner.gone()
                        setLanguageLoading(text = R.string.select_language, showProgress = false, isClickable = true)
                        return !showError
                    }

                    override fun onNotConnected(): Boolean {
                        setLanguageLoading(text = R.string.select_language, showProgress = false, isClickable = true)
                        return !showError
                    }
                })
    }

    private fun confirmDebugPasswordPopup(mode: DriverDebugOpenMode) {
        val title = if (DriverDebugOpenMode.DEBUG == mode) "Confirm Debug Password" else "Confirm Register Password"

        openEditTextDialog(parentActivity,
                title = title, errorMessage = "Code can't be empty.",
                onTextValidated = { if (DriverDebugOpenMode.DEBUG == mode && Data.DEBUG_PASSWORD == it) changeServerLinkPopup() })
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
                { setServerLink(Data.LIVE_SERVER_URL) }, { setServerLink(Data.DEV_SERVER_URL) },
                {
                    openEditTextDialog(parentActivity,
                            inputType = InputType.TYPE_CLASS_TEXT,
                            defaultEditTextData = Prefs.with(activity).getString(SPLabels.CUSTOM_SERVER_URL, Data.SERVER_URL),
                            title = "Custom URL",
                            message = "Please enter Custom URL",
                            errorMessage = "URL can't be empty.",
                            onTextValidated = { link ->
                                Prefs.with(activity).save(SPLabels.CUSTOM_SERVER_URL, link)
                                val editor = preferences.edit()
                                editor.putString(Data.SP_SERVER_LINK, link)
                                editor.commit()

                                MyApplication.getInstance().initializeServerURLAndRestClient(activity)
                            })
                }, true, true)


    }

    private fun openEditTextDialog(activity: Activity,
                                   inputType: Int = InputType.TYPE_CLASS_NUMBER,
                                   defaultEditTextData: String = "", title: String = "", message: String = "",
                                   errorMessage: String = "", onTextValidated: ((String) -> Unit)? = null) {


        val dialog = Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar)

        with(dialog) {
            window!!.attributes.windowAnimations = R.style.Animations_LoadingDialogFade
            setContentView(R.layout.dialog_edittext)
            val layoutParams = dialog.window!!.attributes
            layoutParams.dimAmount = 0.6f

            window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            setCancelable(true)
            setCanceledOnTouchOutside(true)

            textHead.typeface = Fonts.mavenRegular(activity)
            textMessage.typeface = Fonts.mavenRegular(activity)
            etCode.typeface = Fonts.mavenRegular(activity)

            etCode.inputType = inputType
            etCode.setTextSize(TypedValue.COMPLEX_UNIT_PX, 30f)

            etCode.setText(defaultEditTextData)

            ASSL(activity, rv, 1134, 720, true)
            rv.setOnClickListener { dismiss() }

            textHead.text = title
            if (message.isBlank()) textMessage.gone() else textMessage.text = message

            btnConfirm.typeface = Fonts.mavenRegular(activity)
            btnConfirm.setOnClickListener {
                val code = etCode.text.toString().trim()
                if ("".equals(code, ignoreCase = true)) {
                    etCode.requestFocus()
                    etCode.error = errorMessage
                } else {
                    onTextValidated?.invoke(code)
                    dismiss()
                }
            }

            show()
            etCode.setSelection(etCode.text.length)

        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        parentActivity = context as Activity
        toolbarChangeListener = context as ToolbarChangeListener
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            mListener?.toggleDisplayFlags(false)
            toolbarChangeListener.setToolbarVisibility(false)
            JSONParser.saveAccessToken(parentActivity, "")
        }else{
            mListener?.toggleDisplayFlags(true)
        }
    }


    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)

        try {
            with(rootView) {
                tvLabel.text = getString(R.string.label_edt_phone)
                edtPhoneNo.hint = getString(R.string.hint_edt_phone)
                btnGenerateOtp.text = getString(R.string.btn_text_OTP)
                TransitionManager.beginDelayedTransition(constraint)
//            if(tvLanguage.tag != null) tvLanguage.text = getString(tvLanguage.tag as Int)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("", e.message)
        }

        // animate

    }

    private fun setLanguageLoading(@StringRes text: Int, showErrorImage: Boolean = false,
                                   showText: Boolean = true, showProgress: Boolean = true, isClickable: Boolean = false) {
        with(rootView) {


            if (showProgress
                    && resources.getInteger(R.integer.show_language_control) == resources.getInteger(R.integer.view_visible)) {
                progressLanguage.visible()
            } else {
                progressLanguage.gone()
            }
            if (showText
                    && resources.getInteger(R.integer.show_language_control) == resources.getInteger(R.integer.view_visible)) {
                tvLanguage.visible()
            } else {
                tvLanguage.gone()
            }

            tvLanguage.isClickable = isClickable
            if (text != -1) tvLanguage.text = getString(text)
//            tvLanguage.tag = text
            tvLanguage.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    if (showErrorImage) R.drawable.retry_icon_black else 0, 0)
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun addTransitionEndListenerAndAnimateView() {
        val sharedElementEnterTransition = sharedElementEnterTransition as TransitionSet
        sharedElementEnterTransition.addListener(object : Transition.TransitionListener {
            override fun onTransitionEnd(transition: Transition) {

                animateViews()
            }

            override fun onTransitionResume(transition: Transition) {}

            override fun onTransitionPause(transition: Transition) {}

            override fun onTransitionCancel(transition: Transition) {}

            override fun onTransitionStart(transition: Transition) {}
        })

    }

    private fun animateViews() {


        try {
            if (resources.getInteger(R.integer.show_language_control) == resources.getInteger(R.integer.view_visible)){
                getLanguageList(false)
            }

            with(rootView){
                if(!tvLabel.isGone())tvLabel.visible()
                if(!backgroundPhone.isGone())backgroundPhone.visible()
                if(!tvCountryCode.isGone())tvCountryCode.visible()
                if(!edtPhoneNo.isGone())edtPhoneNo.visible()
                if(!btnGenerateOtp.isGone())btnGenerateOtp.visible()
            }
        } catch (e: Exception) {
        }
    }

    private class LanguageAdapter(context: Context?, resource: Int, objects: MutableList<LocaleModel>?) : ArrayAdapter<LocaleModel>(context, resource, objects) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View = super.getView(position, convertView, parent)
            if (view is TextView) {
                (view).typeface = Fonts.mavenRegular(context)
            }

            return view
        }


        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view: View = super.getDropDownView(position, convertView, parent)
            if (view is TextView) {
                (view).typeface = Fonts.mavenRegular(context)
            }

            return view
        }

    }

    override fun onDestroyView() {
        mListener?.toggleDisplayFlags(true)
        super.onDestroyView()
    }
}

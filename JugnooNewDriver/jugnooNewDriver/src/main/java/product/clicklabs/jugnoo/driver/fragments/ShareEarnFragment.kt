package product.clicklabs.jugnoo.driver.fragments

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.picker.Country
import com.picker.CountryPicker
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_share_earn.*
import org.json.JSONObject
import product.clicklabs.jugnoo.driver.*
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags
import product.clicklabs.jugnoo.driver.retrofit.RestClient
import product.clicklabs.jugnoo.driver.retrofit.model.BranchUrlRequest
import product.clicklabs.jugnoo.driver.retrofit.model.BranchUrlResponse
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse
import product.clicklabs.jugnoo.driver.ui.api.APICommonCallbackKotlin
import product.clicklabs.jugnoo.driver.ui.api.ApiCommonKt
import product.clicklabs.jugnoo.driver.ui.api.ApiName
import product.clicklabs.jugnoo.driver.utils.*
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import retrofit.mime.TypedByteArray
import java.util.*


class ShareEarnFragment : BaseFragment() {

    internal var sstr: SpannableString? = null
    private var isCustomerSharing: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        isCustomerSharing = bundle!!.getBoolean(IS_CUSTOMER_SHARING, false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_share_earn, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            buttonShare.text = Data.userData.referralButtonText
            sstr = SpannableString(Data.userData.referralCode)
            val bss = StyleSpan(android.graphics.Typeface.BOLD)
            sstr!!.setSpan(bss, 0, sstr!!.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)


            textViewReferralCodeDisplay.text = ""
            textViewReferralCodeDisplay.append(resources.getString(R.string.your_referral_code))
            textViewReferralCodeValue.text = sstr

                textViewShareReferral.text = if (isCustomerSharing) {
                    Prefs.with(requireContext()).getString(Constants.KEY_D2C_DISPLAY_MESSAGE, "")
                } else {
                    Prefs.with(requireContext()).getString(Constants.KEY_D2D_DISPLAY_MESSAGE, "")
                }

            val imageUrl = if (isCustomerSharing) {
                Prefs.with(requireContext()).getString(Constants.KEY_D2C_REFERRAL_IMAGE, "")
            } else {
                Prefs.with(requireContext()).getString(Constants.KEY_D2D_REFERRAL_IMAGE, "")
            }
            val fallbackDrawableId = if (isCustomerSharing) R.drawable.graphic_refer else R.drawable.iv_driver_to_driver_referral

                if (TextUtils.isEmpty(imageUrl)) {
                    imageViewJugnooLogo.setImageResource(fallbackDrawableId)
                } else {
                    Picasso.with(activity).load(imageUrl)
                            .placeholder(fallbackDrawableId)
                            .error(fallbackDrawableId)
                            .into(imageViewJugnooLogo)
                }

        } catch (e: Exception) {
            e.printStackTrace()
            activity!!.finish()
            activity!!.overridePendingTransition(R.anim.left_in, R.anim.left_out)
        }

        buttonShare.setOnClickListener {
            MyApplication.getInstance().logEvent(FirebaseEvents.INVITE_AND_EARN + "_" + FirebaseEvents.SHARE, null)

            if(isWhatsappShareEnabled() && Utils.appInstalledOrNot(activity, "com.whatsapp")){
                generateBranchUrl()
            } else {
                confirmCustomerNumberPopup(activity)
            }
        }
        tvMoreSharingOptions.setOnClickListener{
            if(isWhatsappShareEnabled()){
                generateBranchUrl(true)
            }
        }

        val whatsappEnabled = isWhatsappShareEnabled()
        buttonShare.text = if(whatsappEnabled) getString(R.string.share_via_whatsapp) else getString(R.string.share)
        buttonShare.setCompoundDrawablesRelativeWithIntrinsicBounds(if(whatsappEnabled) R.drawable.ic_whatsapp else R.drawable.ic_share, 0, 0, 0)
        tvMoreSharingOptions.visibility = if(whatsappEnabled) View.VISIBLE else View.GONE
        if(whatsappEnabled){
            val ssb = SpannableStringBuilder(tvMoreSharingOptions.text)
            ssb.setSpan(UnderlineSpan(), 0, ssb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            tvMoreSharingOptions.text = ssb
        }
    }

    private fun isWhatsappShareEnabled(): Boolean {
        return if (isCustomerSharing) {
            Prefs.with(requireContext()).getInt(Constants.KEY_D2C_WHATSAPP_SHARE, 0) == 1
        } else {
            Prefs.with(requireContext()).getInt(Constants.KEY_D2D_WHATSAPP_SHARE, 0) == 1
        }
    }


    fun confirmCustomerNumberPopup(activity: FragmentActivity?) {

        try {
            val dialog = Dialog(activity!!, android.R.style.Theme_Translucent_NoTitleBar)
            dialog.window!!.attributes.windowAnimations = R.style.Animations_LoadingDialogFade
            dialog.setContentView(R.layout.dialog_share_enter_number)

            val layoutParams = dialog.window!!.attributes
            layoutParams.dimAmount = 0.6f
            dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            dialog.setCancelable(true)
            dialog.setCanceledOnTouchOutside(true)


            val textViewDialogTitle = dialog.findViewById<View>(R.id.textViewDialogTitle) as TextView
            val customerNumber = dialog.findViewById<View>(R.id.customerNumber) as EditText
            val tvCountryCode = dialog.findViewById<View>(R.id.tvCountryCode) as TextView
            tvCountryCode.text = Utils.getCountryCode(activity)
            val countryPicker = CountryPicker.Builder().with(activity)
                    .listener { country -> tvCountryCode.text = (country as Country).dialCode }
                    .build()
            tvCountryCode.setOnClickListener { countryPicker.showDialog(activity.supportFragmentManager) }
            customerNumber.hint = Data.userData.referralDialogHintText
            textViewDialogTitle.text = Data.userData.referralDialogText

            val btnOk = dialog.findViewById<View>(R.id.btnOk) as Button
            val btnCancel = dialog.findViewById<View>(R.id.btnCancel) as Button

            btnOk.setOnClickListener(View.OnClickListener {
                try {
                    var code = customerNumber.text.toString().trim { it <= ' ' }
                    if (TextUtils.isEmpty(tvCountryCode.text.toString())) {
                        Toast.makeText(activity, getString(R.string.please_select_country_code), Toast.LENGTH_SHORT).show()
                        return@OnClickListener
                    }
                    if ("".equals(code, ignoreCase = true)) {
                        customerNumber.requestFocus()
                        customerNumber.error = resources.getString(R.string.phone_no_cnt_be_empty)
                    } else {
                        code = Utils.retrievePhoneNumberTenChars(tvCountryCode.text.toString(), code)
                        if (!Utils.validPhoneNumber(code)) {
                            customerNumber.requestFocus()
                            customerNumber.error = resources.getString(R.string.valid_phone_number)
                        } else {
                            sendReferralMessage(activity, code, tvCountryCode.text.toString())
                            MyApplication.getInstance().logEvent(FirebaseEvents.INVITE_AND_EARN + "_"
                                    + FirebaseEvents.SHARE + "_" + FirebaseEvents.CONFIRM_YES, null)
                            dialog.dismiss()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()

                }
            })
            btnCancel.setOnClickListener {
                MyApplication.getInstance().logEvent(FirebaseEvents.INVITE_AND_EARN + "_"
                        + FirebaseEvents.SHARE + "_" + FirebaseEvents.CONFIRM_NO, null)
                dialog.dismiss()
            }
            customerNumber.setOnEditorActionListener { view, actionId, event ->
                val result = actionId and EditorInfo.IME_MASK_ACTION
                when (result) {
                    EditorInfo.IME_ACTION_DONE -> btnOk.performClick()

                    EditorInfo.IME_ACTION_NEXT -> {
                    }
                }
                true
            }

            dialog.show()
            Handler().postDelayed({ Utils.showSoftKeyboard(activity, customerNumber) }, 200)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun sendReferralMessage(activity: Activity?, phone_no: String, countryCode: String) {
        try {
            if (AppStatus.getInstance(activity).isOnline(activity)) {

                DialogPopup.showLoadingDialog(activity, activity!!.resources.getString(R.string.loading))

                val params = HashMap<String, String>()

                params["access_token"] = Data.userData.accessToken
                params["phone_no"] = phone_no
                params[Constants.KEY_COUNTRY_CODE] = countryCode
                params[Constants.KEY_IS_DRIVER] = if (isCustomerSharing) "0" else "1"
                HomeUtil.putDefaultParams(params)
                Log.i("params", "=$params")

                RestClient.getApiServices().sendReferralMessage(params, object : Callback<RegisterScreenResponse> {
                    override fun success(registerScreenResponse: RegisterScreenResponse, response: Response) {
                        val responseStr = String((response.body as TypedByteArray).bytes)
                        try {
                            val jObj = JSONObject(responseStr)
                            val flag = jObj.getInt("flag")
                            if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                                DialogPopup.alertPopup(activity, "", jObj.getString("message"))
                            } else {
                                DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG)
                            }
                        } catch (exception: Exception) {
                            exception.printStackTrace()
                            DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG)
                        }

                        DialogPopup.dismissLoadingDialog()
                    }

                    override fun failure(error: RetrofitError) {
                        Log.e("request fail", error.toString())
                        DialogPopup.dismissLoadingDialog()
                        DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG)
                    }
                })
            } else {
                DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun getTitle(): String {
        return if (isCustomerSharing) getString(R.string.refer_a_customer) else getString(R.string.refer_a_driver)
    }

    private fun generateBranchUrl(defaultIntentShare:Boolean = false) {
        if(Data.userData == null){
            return
        }

        val metaData = hashMapOf<String, String>(
                "referral_code" to Data.userData.referralCode
        )

        val branchKey = if(isCustomerSharing) {
            Prefs.with(requireContext()).getString(Constants.KEY_D2C_BRANCH_KEY, "")
        } else {
            Prefs.with(requireContext()).getString(Constants.KEY_D2D_BRANCH_KEY, "")
        }
        val branchSecret = if(isCustomerSharing) {
            Prefs.with(requireContext()).getString(Constants.KEY_D2C_BRANCH_SECRET, "")
        } else {
            Prefs.with(requireContext()).getString(Constants.KEY_D2D_BRANCH_SECRET, "")
        }

        if(branchKey.isEmpty() || branchSecret.isEmpty()){
            shareToWhatsapp(
                    if (isCustomerSharing) {
                        Prefs.with(requireContext()).getString(Constants.KEY_D2C_DEFAULT_SHARE_URL, "")
                    } else {
                        Prefs.with(requireContext()).getString(Constants.KEY_D2D_DEFAULT_SHARE_URL, "")
                    }
            , defaultIntentShare)
        } else {
            val branchUrlRequest = BranchUrlRequest(metaData, branchKey, branchSecret)

            ApiCommonKt<BranchUrlResponse>(requireActivity(), putDefaultParams = false).execute(branchUrlRequest, ApiName.BRANCH_GENERATE_URL,
                    object : APICommonCallbackKotlin<BranchUrlResponse>() {
                        override fun onSuccess(t: BranchUrlResponse?, message: String?, flag: Int) {
                            if (t != null) {
                                shareToWhatsapp(t.url!!, defaultIntentShare)
                            }
                        }

                        override fun onError(t: BranchUrlResponse?, message: String?, flag: Int): Boolean {
                            return false
                        }

                    })
        }
    }

    private fun shareToWhatsapp(url:String, defaultIntentShare:Boolean){

        val content = if(isCustomerSharing){
            Prefs.with(requireContext()).getString(Constants.KEY_D2C_SHARE_CONTENT, "")
        } else {
            Prefs.with(requireContext()).getString(Constants.KEY_D2D_SHARE_CONTENT, "")
        }
        val subject = if(isCustomerSharing){
            getString(R.string.download_jugnoo_app)
        } else {
            getString(R.string.download_jugnoo_driver_app)
        }
        val finalContent = content.plus(" ").plus(url).replace("{{{referral_code}}}", Data.userData.referralCode);

        if(defaultIntentShare){
            UtilsKt.defaultShareIntent(requireActivity(), finalContent, subject)
        } else {
            UtilsKt.whatsappIntent(requireActivity(), finalContent, subject)
        }
    }

    companion object {

        private val IS_CUSTOMER_SHARING = "is_customer_sharing"

        @JvmStatic
        fun newInstance(isCustomerSharing: Boolean): ShareEarnFragment {
            val fragment = ShareEarnFragment()
            val bundle = Bundle()
            bundle.putBoolean(IS_CUSTOMER_SHARING, isCustomerSharing)
            fragment.arguments = bundle
            return fragment
        }
    }

}

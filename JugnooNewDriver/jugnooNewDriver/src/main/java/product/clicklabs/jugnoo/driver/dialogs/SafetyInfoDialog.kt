package product.clicklabs.jugnoo.driver.dialogs

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.dialog_safety_info.*
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.adapters.SafetyInfoListAdapter
import product.clicklabs.jugnoo.driver.datastructure.UserData
import product.clicklabs.jugnoo.driver.utils.Fonts
import product.clicklabs.jugnoo.driver.utils.Utils

class SafetyInfoDialog : DialogFragment(){

    private var callback: Callback? = null
    private val TAG = SafetyInfoDialog::class.java.simpleName

    var safetyInfoListAdapter: SafetyInfoListAdapter? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Callback) {
            callback = context
        }
    }
    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
            dialog.setCancelable(true)
            dialog.window?.setBackgroundDrawableResource(R.color.transparent)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.Animations_LoadingDialogFade
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_safety_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews()
    }

    private fun setViews() {
        tvTitle.typeface = Fonts.mavenMedium(requireContext())
        btnOk.typeface = Fonts.mavenMedium(requireContext())
        btnCancel.typeface = Fonts.mavenMedium(requireContext())
        rvSafetyOptions.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        callback?.getUserData()?.safetyInfoData?.run{
            safetyPoints?.forEach {
                it.isSelected = false
            }

            safetyInfoListAdapter = SafetyInfoListAdapter(rvSafetyOptions, safetyPoints ?: arrayListOf())
            rvSafetyOptions.adapter = safetyInfoListAdapter
            tvTitle.text = title ?: ""

            if(!TextUtils.isEmpty(bannerImage)){
                Picasso.with(requireActivity()).load(bannerImage)
                        .placeholder(R.drawable.ic_notification_placeholder)
                        .error(R.drawable.ic_notification_placeholder)
                        .into(ivPicture)
            }
        }

        btnOk.setOnClickListener {

            callback?.getUserData()?.safetyInfoData?.run{
                val mandatoryCheckFailed = safetyPoints?.any {
                    it.isMandatory() && !it.isSelected
                }

                if(mandatoryCheckFailed == true){
                    Utils.showToast(requireContext(), getString(R.string.please_select_mandatory_points))
                } else {
                    callback?.onSafetyInfoDialogConfirmClick()
                    dismiss()
                }
            }
        }
        btnCancel.setOnClickListener {
            callback?.onSafetyInfoDialogDismiss()
            dismiss()
        }

    }

    companion object{

        fun newInstance():SafetyInfoDialog{
            val dialog = SafetyInfoDialog()
            val bundle = Bundle()

            dialog.arguments = bundle
            return dialog
        }
    }


    interface Callback {
        fun onSafetyInfoDialogConfirmClick()
        fun onSafetyInfoDialogDismiss()
        fun getUserData(): UserData?
    }

}
package product.clicklabs.jugnoo.driver.emergency

import android.app.Activity
import android.app.Dialog
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.JSONParser
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.utils.ASSL
import product.clicklabs.jugnoo.driver.utils.Fonts


/**
 * For the purpose of showing emergency options dialog
 * Options: Enable emergency mode
 * Send Ride Status
 * In App Customer Support
 *
 * Created by shankar on 2/22/16.
 */
class EmergencyDialog(private val activity: Activity, private val engagementId: String, private val callBack: CallBack) {


    fun show(modeEnabled: Int): Dialog? {
        try {
            val dialog = Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar)
            dialog.window!!.attributes.windowAnimations = R.style.Animations_LoadingDialogFade
            dialog.setContentView(R.layout.dialog_emergency)

            val relative = dialog.findViewById<View>(R.id.relative) as RelativeLayout
            ASSL(activity, relative, 1134, 720, true)

            val layoutParams = dialog.window!!.attributes
            layoutParams.dimAmount = 0.6f
            dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)

            val linearLayoutInner = dialog.findViewById<View>(R.id.linearLayoutInner) as LinearLayout

            val textViewEnableEmergencyMode = dialog.findViewById<View>(R.id.textViewEnableEmergencyMode) as TextView
            textViewEnableEmergencyMode.typeface = Fonts.mavenRegular(activity)

            val textViewSendRideStatus = dialog.findViewById<View>(R.id.textViewSendRideStatus) as TextView
            textViewSendRideStatus.typeface = Fonts.mavenRegular(activity)

            val textViewInAppCustomerSupport = dialog.findViewById<View>(R.id.textViewInAppCustomerSupport) as TextView
            textViewInAppCustomerSupport.typeface = Fonts.mavenRegular(activity)
            if (JSONParser.isTagEnabled(activity, Constants.CHAT_SUPPORT)) {
                textViewInAppCustomerSupport.visibility = View.VISIBLE
                textViewInAppCustomerSupport.setText(R.string.chat_with_us)
                dialog.findViewById<View>(R.id.vDivChat).visibility = View.VISIBLE
            } else {
                textViewInAppCustomerSupport.visibility = View.GONE
                dialog.findViewById<View>(R.id.vDivChat).visibility = View.GONE
            }

            val tvCallSupport = dialog.findViewById<TextView>(R.id.tvCallSupport)
            tvCallSupport.typeface = Fonts.mavenRegular(activity)

            val btnCancel = dialog.findViewById<View>(R.id.btnCancel) as Button
            btnCancel.typeface = Fonts.mavenRegular(activity)

            val onClickListener = View.OnClickListener { v ->
                when (v.id) {

                    R.id.textViewEnableEmergencyMode -> {
                        if (modeEnabled == 1) {
                            callBack.onEmergencyModeDisabled(engagementId)
                        } else {
                            callBack.onEnableEmergencyModeClick(v)
                        }
                        dialog.dismiss()
                    }

                    R.id.textViewSendRideStatus -> {
                        callBack.onSendRideStatusClick(v)
                        dialog.dismiss()
                    }

                    R.id.textViewInAppCustomerSupport -> {
                        callBack.onInAppCustomerSupportClick(v)
                        dialog.dismiss()
                    }

                    R.id.tvCallSupport -> {
                        callBack.onCallSupportClick(v)
                        dialog.dismiss()
                    }

                    R.id.btnCancel -> {
                        callBack.onDialogClosed(v)
                        dialog.dismiss()
                    }

                    R.id.linearLayoutInner -> {
                    }

                    R.id.relative -> {
                    }
                }
            }


            textViewEnableEmergencyMode.setOnClickListener(onClickListener)
            textViewSendRideStatus.setOnClickListener(onClickListener)
            textViewInAppCustomerSupport.setOnClickListener(onClickListener)
            tvCallSupport.setOnClickListener(onClickListener)
            btnCancel.setOnClickListener(onClickListener)
            linearLayoutInner.setOnClickListener(onClickListener)
            relative.setOnClickListener(onClickListener)

            dialog.setOnCancelListener { callBack.onDialogDismissed() }


            if (modeEnabled == 1) {
                textViewEnableEmergencyMode.setTextColor(activity.resources.getColorStateList(R.color.text_color_light_selector))
                textViewEnableEmergencyMode.text = activity.resources.getString(R.string.disable_emergency_mode)
            } else {
                textViewEnableEmergencyMode.setTextColor(activity.resources.getColorStateList(R.color.text_color_red_dark_aplha_selector))
                textViewEnableEmergencyMode.text = activity.resources.getString(R.string.enable_emergency_mode)
            }

            dialog.show()
            return dialog
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }


    interface CallBack {
        fun onEnableEmergencyModeClick(view: View)
        fun onEmergencyModeDisabled(engagementId: String)
        fun onSendRideStatusClick(view: View)
        fun onInAppCustomerSupportClick(view: View)
        fun onCallSupportClick(view: View)
        fun onDialogClosed(view: View)
        fun onDialogDismissed()
    }

}

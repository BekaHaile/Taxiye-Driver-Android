package product.clicklabs.jugnoo.driver.emergency

import android.app.Activity
import android.app.Dialog
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView

import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.utils.ASSL
import product.clicklabs.jugnoo.driver.utils.Fonts

/**
 * Created by shankar on 5/2/16.
 */
class EmergencyDisableDialog(private val activity: Activity) {

    private val TAG = EmergencyDisableDialog::class.java.simpleName
    private var dialog: Dialog? = null

    fun show(): EmergencyDisableDialog {
        try {
            dialog = Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar)
            dialog!!.window!!.attributes.windowAnimations = R.style.Animations_LoadingDialogFade
            dialog!!.setContentView(R.layout.dialog_emergency_disable)

            val relative = dialog!!.findViewById<View>(R.id.relative) as RelativeLayout
            ASSL(activity, relative, 1134, 720, false)

            val layoutParams = dialog!!.window!!.attributes
            layoutParams.dimAmount = 0.6f
            dialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            dialog!!.setCancelable(true)
            dialog!!.setCanceledOnTouchOutside(true)

            val linearLayoutInner = dialog!!.findViewById<View>(R.id.linearLayoutInner) as LinearLayout
            val btnOk = dialog!!.findViewById<View>(R.id.btnOk) as Button
            btnOk.typeface = Fonts.mavenRegular(activity)
            val textHead = dialog!!.findViewById<View>(R.id.textHead) as TextView
            textHead.typeface = Fonts.mavenMedium(activity)
            val textViewMessage = dialog!!.findViewById<View>(R.id.textMessage) as TextView
            textViewMessage.typeface = Fonts.mavenRegular(activity)
            textViewMessage.text = activity.getString(R.string.you_have_disabled_jugnoo_emergency, activity.getString(R.string.appname))

            btnOk.setOnClickListener { dialog!!.dismiss() }

            linearLayoutInner.setOnClickListener { }

            relative.setOnClickListener { dialog!!.dismiss() }

            dialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return this
    }


}
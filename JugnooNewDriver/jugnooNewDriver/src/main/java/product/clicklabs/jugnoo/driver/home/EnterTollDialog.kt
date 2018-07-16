package product.clicklabs.jugnoo.driver.home

import android.app.Activity
import android.app.Dialog
import android.view.WindowManager
import kotlinx.android.synthetic.main.dialog_edittext.*
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.utils.Utils

class EnterTollDialog(var activity: Activity) {

    fun show(tollValue: Double, callback: Callback){
        val dialog = Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar)
        with(dialog){
            window!!.attributes.windowAnimations = R.style.Animations_LoadingDialogFade
            setContentView(R.layout.dialog_edittext)
            val layoutParams = dialog.window!!.attributes
            layoutParams.dimAmount = 0.6f
            window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            setCancelable(true)
            setCanceledOnTouchOutside(true)

            textHead.text = activity.getString(R.string.enter_toll)
            textMessage.text = activity.getString(R.string.enter_toll_fare_during_ride)
            etCode.hint = activity.getString(R.string.enter_toll)
            if(tollValue > 0.0){
                etCode.setText(tollValue.toString())
                etCode.setSelection(etCode.text.length)
            }

            btnConfirm.setOnClickListener {
                val code = etCode.text.toString().trim()
                if (code.isEmpty() || !Utils.checkIfOnlyDigitsDecimal(code)) {
                    Utils.showToast(activity, activity.getString(R.string.please_enter_valid_fare))
                } else {
                    callback.tollEntered(code.toDouble())
                    dismiss()
                }
            }


            show()
        }

    }

    interface Callback{
        fun tollEntered(tollValue: Double);
    }

}
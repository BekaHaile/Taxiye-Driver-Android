package product.clicklabs.jugnoo.driver.utils

import android.app.Dialog
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.Data
import product.clicklabs.jugnoo.driver.HomeActivity
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.ui.api.APICommonCallbackKotlin
import product.clicklabs.jugnoo.driver.ui.api.ApiCommonKt
import product.clicklabs.jugnoo.driver.ui.api.ApiName
import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponseKotlin
import java.util.*

/**
 * Created by Parminder Saini on 25/08/18.
 */


class AddLuggageInteractor(val activity: HomeActivity){

    companion object {
        const val TAG_LUGGAGE_COUNT_SUBMITTED = 40;
    }

    private val addLuggagePopup:Dialog by lazy{
        val dialog = Dialog(activity,android.R.style.Theme_Translucent_NoTitleBar)
        dialog.apply {
            setContentView(R.layout.dialog_add_luggage)
            window?.run {
                getAttributes().dimAmount = 0.6f
                addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                setCancelable(true)
                setCanceledOnTouchOutside(true)
            }
        }
    }

    init {
        initViews()
    }

   private lateinit var tvIncrementLuggage:TextView
   private lateinit var tvDecrementLuggage:TextView
   private lateinit var tvLuggageValue:TextView
   private lateinit var tvLuggageFareInfo:TextView
   private lateinit var btnDone: Button
   private val baggageMaxValue = 100



    private fun initViews(){
        tvIncrementLuggage = addLuggagePopup.findViewById(R.id.tvAddLuggageCount) as TextView
        tvDecrementLuggage = addLuggagePopup.findViewById(R.id.tvSubLuggageCount) as TextView
        tvLuggageValue = addLuggagePopup.findViewById(R.id.tvLuggageValue) as TextView
        tvLuggageFareInfo = addLuggagePopup.findViewById(R.id.tvLuggageFareInfo) as TextView
        btnDone = addLuggagePopup.findViewById(R.id.btnDone) as Button
        tvIncrementLuggage.setOnClickListener{
            var count:Int = Integer.parseInt(tvLuggageValue.text.toString().trim())
            if(count < baggageMaxValue){
                tvLuggageValue.text = (++count).toString();
            }

        }
        tvDecrementLuggage.setOnClickListener{
            var count:Int = Integer.parseInt(tvLuggageValue.text.toString().trim())
            if(count>0){
                tvLuggageValue.text = (--count).toString();

            }

        }
        btnDone.setOnClickListener {
            if(it.tag!=null && it.tag== TAG_LUGGAGE_COUNT_SUBMITTED){
                setButtonState(null)
            }else{
                submitLuggageCount(Integer.parseInt(tvLuggageValue.text.toString().trim()))
            }
        }
    }

     fun showLuggagePopup(){


         addLuggagePopup.show()
        val currentLuggageCount = Data.getCurrentCustomerInfo().luggageCount;
        tvLuggageValue.text = currentLuggageCount.toString()

        val farePerLuggage =   Utils.formatCurrencyValue(Data.userData.currency,Data.fareStructure.baggageCharges);
        tvLuggageFareInfo.text = activity.getString(R.string.text_luggage_charges, farePerLuggage);

        setButtonState(if(currentLuggageCount>0) TAG_LUGGAGE_COUNT_SUBMITTED else null)

    }

    private fun setButtonState(tag:Int?) {
        btnDone.tag = tag
        if(tag== TAG_LUGGAGE_COUNT_SUBMITTED){
            btnDone.text = activity.getString(R.string.change)
            tvIncrementLuggage.isEnabled = false
            tvDecrementLuggage.isEnabled = false
        }else{
            btnDone.text = activity.getString(R.string.done)
            tvIncrementLuggage.isEnabled = true
            tvDecrementLuggage.isEnabled = true
        }
    }

    private fun submitLuggageCount(amount: Int) {
        val params = HashMap<String, String>()
        params[Constants.KEY_ENGAGEMENT_ID] = Data.getCurrentEngagementId().toString()
        params[Constants.KEY_LUGGAGE_COUNT] = amount.toString()

            ApiCommonKt<FeedCommonResponseKotlin>(activity, putAccessToken = true).execute(params, ApiName.UPDATE_LUGGAGE_COUNT,
                    object : APICommonCallbackKotlin<FeedCommonResponseKotlin>() {
                        override fun onSuccess(t: FeedCommonResponseKotlin?, message: String?, flag: Int) {
                            Data.getCurrentCustomerInfo().luggageCount = amount
                            activity.setLuggageUI()
                            addLuggagePopup.dismiss()

                        }

                        override fun onError(t: FeedCommonResponseKotlin?, message: String?, flag: Int): Boolean {
                            return false
                        }

                    })


    }


}
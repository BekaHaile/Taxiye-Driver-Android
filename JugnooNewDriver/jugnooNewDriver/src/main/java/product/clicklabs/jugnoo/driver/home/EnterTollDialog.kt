package product.clicklabs.jugnoo.driver.home

import android.app.Activity
import android.app.Dialog
import android.support.v7.widget.LinearLayoutManager
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.dialog_toll.*
import org.json.JSONArray
import org.json.JSONObject
import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.adapters.TollDataAdapter
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo
import product.clicklabs.jugnoo.driver.retrofit.model.TollData
import product.clicklabs.jugnoo.driver.ui.api.APICommonCallbackKotlin
import product.clicklabs.jugnoo.driver.ui.api.ApiCommonKt
import product.clicklabs.jugnoo.driver.ui.api.ApiName
import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponseKotlin
import product.clicklabs.jugnoo.driver.utils.Utils
import java.util.*

class EnterTollDialog(var activity: Activity, val customerInfo: CustomerInfo) {

    fun showAddToll(tollDatas: ArrayList<TollData>, callback:CallbackInt){
        val dialog = Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar)
        with(dialog){
            window!!.attributes.windowAnimations = R.style.Animations_LoadingDialogFade
            setContentView(R.layout.dialog_add_toll)
            val layoutParams = dialog.window!!.attributes
            layoutParams.dimAmount = 0.6f
            window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            setCancelable(true)
            setCanceledOnTouchOutside(true)

            val btnCancel = findViewById<Button>(R.id.btnCancel)
            val btnAdd = findViewById<Button>(R.id.btnAdd)
            val etTollName = findViewById<EditText>(R.id.etTollName)
            val etTollValue = findViewById<EditText>(R.id.etTollValue)


            btnAdd.setOnClickListener {
                val fare = etTollValue.text.toString().trim()
                val name = etTollName.text.toString().trim()
                if(name.isEmpty()){
                    Utils.showToast(activity, activity.getString(R.string.please_enter_something))
                    return@setOnClickListener
                }
                if (fare.isEmpty() || !Utils.checkIfOnlyDigitsDecimal(fare)) {
                    Utils.showToast(activity, activity.getString(R.string.invalid_fare))
                    return@setOnClickListener
                }

                tollDatas.add(TollData(-1, fare.toDouble(), name))
                callback.tollAdded()
                dismiss()
            }
            btnCancel.setOnClickListener {
                dismiss()
            }



            show()
        }

    }

    fun shows(callback: Callback){
        val dialog = Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar)
        with(dialog){
            window!!.attributes.windowAnimations = R.style.Animations_LoadingDialogFade
            setContentView(R.layout.dialog_toll)
            val layoutParams = dialog.window!!.attributes
            layoutParams.dimAmount = 0.6f
            window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            setCancelable(true)
            setCanceledOnTouchOutside(true)

            rvTolls.layoutManager = LinearLayoutManager(activity)
            rvTolls.setHasFixedSize(false)
            val adapter = TollDataAdapter(rvTolls, object:TollDataAdapter.Callback{
                override fun onDeleteClick(pos: Int, tollData: TollData) : ArrayList<TollData> {
                    tollData.deleted = true
                    return customerInfo.tollData
                }
            })
            rvTolls.adapter = adapter
            adapter.setList(customerInfo.tollData, customerInfo.currencyUnit)

            val params = rvTolls.layoutParams as LinearLayout.LayoutParams
            if(customerInfo.tollData.size > 4){
                params.height = Utils.dpToPx(activity, 200.0f)
            } else {
                params.height = LinearLayout.LayoutParams.WRAP_CONTENT
            }
            rvTolls.layoutParams = params

            btnConfirm.setOnClickListener {
                updateTollData(this)
            }
            btnAddToll.setOnClickListener {
                showAddToll(customerInfo.tollData, object:CallbackInt{
                    override fun tollAdded() {
                        adapter.setList(customerInfo.tollData, customerInfo.currencyUnit)
                    }
                })
            }


            show()
        }

    }

    private fun updateTollData(dialog: Dialog) {
        val params = HashMap<String, String>()
        params[Constants.KEY_ENGAGEMENT_ID] = customerInfo.getEngagementId().toString()
        val jsonArray = JSONArray()
        for(toll in customerInfo.tollData){
            val jsonObject = JSONObject()
            if(toll.tollVisitId == -1 && !toll.deleted){
                jsonObject.put(Constants.KEY_TOLL_NAME, toll.tollName)
                jsonObject.put(Constants.KEY_TOLL, toll.toll)
            } else if(toll.tollVisitId > 0 && toll.deleted){
                jsonObject.put(Constants.KEY_TOLL_VISIT_ID, toll.toll)
                jsonObject.put(Constants.KEY_TOLL, 0)
            }
            if(jsonObject.has(Constants.KEY_TOLL)){
                jsonArray.put(jsonObject)
            }
        }
        if(jsonArray.length() == 0){
            dialog.dismiss()
            return
        }
        params[Constants.KEY_UPDATED_TOLL] = jsonArray.toString()

        ApiCommonKt<FeedCommonResponseKotlin>(activity, true, true, true).execute(params, ApiName.UPDATE_TOLL_DATA,
                object : APICommonCallbackKotlin<FeedCommonResponseKotlin>() {
                    override fun onSuccess(feedCommonResponseKotlin: FeedCommonResponseKotlin, message: String, flag: Int) {
                        dialog.dismiss()
                    }

                    override fun onError(feedCommonResponseKotlin: FeedCommonResponseKotlin, message: String, flag: Int): Boolean {
                        return false
                    }
                })
    }


    interface Callback{
        fun tollEntered(tollValue: Double)
    }
    interface CallbackInt{
        fun tollAdded()
    }

}
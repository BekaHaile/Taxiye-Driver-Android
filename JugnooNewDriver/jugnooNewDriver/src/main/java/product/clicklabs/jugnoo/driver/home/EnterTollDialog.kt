package product.clicklabs.jugnoo.driver.home

import android.app.Activity
import android.app.Dialog
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import android.view.WindowManager
import android.widget.*
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

    fun showAddToll(tollToEdit:TollData?, manualTollValue:Double, tollDatas:ArrayList<TollData>, callback:CallbackInt){
        val dialog = Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar)
        with(dialog){
            window!!.attributes.windowAnimations = R.style.Animations_LoadingDialogFade
            setContentView(R.layout.dialog_add_toll)
            val layoutParams = dialog.window!!.attributes
            layoutParams.dimAmount = 0.6f
            window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            setCancelable(true)
            setCanceledOnTouchOutside(true)

            val rv = findViewById<RelativeLayout>(R.id.rv)
            rv.setOnClickListener{
                dismiss()
            }
            val btnCancel = findViewById<Button>(R.id.btnCancel)
            val btnAdd = findViewById<Button>(R.id.btnAdd)
            val textHead = findViewById<TextView>(R.id.textHead)
            val tvTollName = findViewById<TextView>(R.id.tvTollName)
            val etTollValue = findViewById<EditText>(R.id.etTollValue)
            if(tollToEdit == null){
                textHead.setText(R.string.add_toll)
                tvTollName.visibility = View.GONE
                if(manualTollValue > 0) {
                    etTollValue.setText(manualTollValue.toString())
                }
            } else {
                textHead.setText(R.string.edit_toll)
                tvTollName.visibility = View.VISIBLE
                tvTollName.text = activity.getString(R.string.toll_name) + ": " + tollToEdit.tollName
                etTollValue.setText(tollToEdit.toll.toString())
            }
            etTollValue.setSelection(etTollValue.text.length)


            btnAdd.setOnClickListener {
                val fare = etTollValue.text.toString().trim()
                if (fare.isEmpty() || !Utils.checkIfOnlyDigitsDecimal(fare)) {
                    Utils.showToast(activity, activity.getString(R.string.invalid_fare))
                    return@setOnClickListener
                }
                if(tollToEdit == null){
                    tollDatas.add(TollData(-1, fare.toDouble(), "extra"))
                } else {
                    if(tollToEdit.toll < fare.toDouble()){
                        Utils.showToast(activity, activity.getString(R.string.enter_less_amount))
                        return@setOnClickListener
                    }
                    tollToEdit.edited = true
                    tollToEdit.editedToll = fare.toDouble()
                }
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
        if(customerInfo.tollData.size == 0
                || (customerInfo.tollData.size == 1 && customerInfo.tollData[0].tollVisitId == -1)){
            val tollVal = if(customerInfo.tollData.size == 1) customerInfo.tollData[0].toll else 0.0
            showAddToll(null, tollVal, customerInfo.tollData, object:CallbackInt{
                override fun tollAdded() {
                    callback.tollEntered(customerInfo.tollFare)
                }
            })
            return
        }
        val dialog = Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar)
        with(dialog){
            window!!.attributes.windowAnimations = R.style.Animations_LoadingDialogFade
            setContentView(R.layout.dialog_toll)
            val layoutParams = dialog.window!!.attributes
            layoutParams.dimAmount = 0.6f
            window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            setCancelable(true)
            setCanceledOnTouchOutside(true)

            val rv = findViewById<RelativeLayout>(R.id.rv)
            rv.setOnClickListener{
                dismiss()
            }

            rvTolls.layoutManager = LinearLayoutManager(activity)
            rvTolls.setHasFixedSize(false)
            val adapter = TollDataAdapter(activity, rvTolls, object:TollDataAdapter.Callback{
                override fun onDeleteClick(pos: Int, tollData: TollData) : ArrayList<TollData> {
                    tollData.edited = true
                    tollData.editedToll = 0.0
                    return customerInfo.tollData
                }

                override fun onEditClick(pos: Int, tollData: TollData, f: (tollData: ArrayList<TollData>?, currency:String)->Unit){
                    showAddToll(tollData, 0.0, customerInfo.tollData, object:CallbackInt{
                        override fun tollAdded() {
                            f(customerInfo.tollData, customerInfo.currencyUnit)
                        }
                    })
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
                updateTollData(this, callback)
            }
            btnCancel.setOnClickListener {
                dismiss()
            }


            show()
        }

    }

    private fun updateTollData(dialog: Dialog, callback: Callback) {
        val params = HashMap<String, String>()
        params[Constants.KEY_ENGAGEMENT_ID] = customerInfo.getEngagementId().toString()
        val jsonArray = JSONArray()
        for(toll in customerInfo.tollData){
            val jsonObject = JSONObject()
            if(toll.tollVisitId == -1 && !toll.edited){
                jsonObject.put(Constants.KEY_TOLL_NAME, toll.tollName)
                jsonObject.put(Constants.KEY_MODIFIED_TOLL, toll.toll)
            } else if(toll.tollVisitId > 0 && toll.edited){
                jsonObject.put(Constants.KEY_TOLL_VISIT_ID, toll.tollVisitId)
                jsonObject.put(Constants.KEY_MODIFIED_TOLL, toll.toll)
            }
            if(jsonObject.has(Constants.KEY_MODIFIED_TOLL)){
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

                        for(tollData in customerInfo.tollData){
                            if(tollData.edited) {
                                tollData.toll = tollData.editedToll
                            }
                        }

                        dialog.dismiss()
                        callback.tollEntered(customerInfo.tollFare)
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
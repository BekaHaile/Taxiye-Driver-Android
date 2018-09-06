package product.clicklabs.jugnoo.driver.ui.popups

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.AppCompatCheckBox
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.synthetic.main.dialog_driver_service_type.*
import product.clicklabs.jugnoo.driver.Data
import product.clicklabs.jugnoo.driver.DriverProfileActivity
import product.clicklabs.jugnoo.driver.HomeUtil
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.ui.api.APICommonCallbackKotlin
import product.clicklabs.jugnoo.driver.ui.api.ApiCommonKt
import product.clicklabs.jugnoo.driver.ui.api.ApiName
import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponseKotlin

/**
 * Created by Parminder Saini on 03/09/18.
 */
public class DriverVehicleServiceTypePopup(var context: Activity, var serviceList  :List<VehicleServiceDetail>): Dialog(context) {




    private val TAG = DriverVehicleServiceTypePopup::class.qualifiedName

    class VehicleServiceDetail(@Expose @SerializedName("id") var id:Long,
                               @Expose @SerializedName("vehicle_name") var serviceName:String,
                               @Expose @SerializedName("is_selected") var checked: Int,
                               @Expose @SerializedName("is_enabled") var enabled:Int)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.getAttributes().dimAmount = 0.6f
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        setCancelable(true)
        setContentView(R.layout.dialog_driver_service_type)
        window.setBackgroundDrawableResource(android.R.color.transparent)
        window.setGravity(Gravity.CENTER)
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        setCanceledOnTouchOutside(true)


        rvVehicleTypes.layoutManager = LinearLayoutManager(context)
        rvVehicleTypes.adapter = ServiceTypeAdapter(context,serviceList,rvVehicleTypes)

        btnSave.setOnClickListener {
            updateServiceList(serviceList.toMutableList())
           /* val serviceListChecked = serviceList.toMutableList()
            val checked = serviceListChecked.filter { it.checked }.map { it.id }
            Log.i(TAG,checked.toString())*/

        }
        ivDismiss.setOnClickListener {
            dismiss()
        }

    }


    class ServiceTypeAdapter(var context: Context, var details: List<VehicleServiceDetail>, var recyclerView: RecyclerView) : RecyclerView.Adapter<ServiceTypeAdapter.ServiceTypeViewHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceTypeAdapter.ServiceTypeViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_service_type, parent, false)
            return ServiceTypeViewHolder(itemView)
        }

        override fun getItemCount(): Int {
            return details.size;
        }

        override fun onBindViewHolder(holder: ServiceTypeAdapter.ServiceTypeViewHolder, position: Int) {
            details[position].run {
                holder.checkBox.text = serviceName
                holder.checkBox.isChecked = checked==1
                holder.checkBox.isEnabled = enabled==1
            }

        }


        inner class ServiceTypeViewHolder(parentView: View): RecyclerView.ViewHolder(parentView) {
            val checkBox: AppCompatCheckBox = parentView.findViewById(R.id.cbService) as AppCompatCheckBox

           init {
               try {
                   checkBox.typeface = ResourcesCompat.getFont(context, R.font.maven_pro_regular)
               } catch (e: Exception) {
                   e.printStackTrace()
               }
               checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                   val pos = recyclerView.getChildLayoutPosition(parentView)
                   if(pos!=RecyclerView.NO_POSITION){
                       details[pos].checked = if(isChecked)1 else 0
                   }
               }
           }

        }
    }


    class ServiceDetailModel (@Expose @SerializedName("vehicle_sets")  var serviceList: List<VehicleServiceDetail>): HomeUtil.DefaultParams()


    class UpdateVehicleSetResponse(@Expose @SerializedName("vehicle_sets")  var serviceList: List<VehicleServiceDetail>):FeedCommonResponseKotlin()
    private fun updateServiceList(serviceList: List<VehicleServiceDetail>) {

        ApiCommonKt<UpdateVehicleSetResponse>(context, putAccessToken = true).execute(ServiceDetailModel(serviceList), ApiName.UPDATE_DRIVER_SERVICES,
                object : APICommonCallbackKotlin<UpdateVehicleSetResponse>() {
                    override fun onSuccess(t: UpdateVehicleSetResponse, message: String?, flag: Int) {
                          Data.userData.vehicleServicesModel  = t.serviceList
                           if(context is DriverProfileActivity){
                               (context as DriverProfileActivity).setVehicleSetDetails();
                           }
                        dismiss()


                    }

                    override fun onError(t: UpdateVehicleSetResponse?, message: String?, flag: Int): Boolean {
                        return false
                    }

                })


    }
}
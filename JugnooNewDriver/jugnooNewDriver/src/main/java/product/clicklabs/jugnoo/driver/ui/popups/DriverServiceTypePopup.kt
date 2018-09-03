package product.clicklabs.jugnoo.driver.ui.popups

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.widget.AppCompatCheckBox
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import kotlinx.android.synthetic.main.dialog_driver_service_type.*
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.adapters.VehicleDetail
import product.clicklabs.jugnoo.driver.utils.Log

/**
 * Created by Parminder Saini on 03/09/18.
 */
public class DriverServiceTypePopup(context: Context,var serviceList  :List<ServiceDetail>): Dialog(context) {



    private val TAG = DriverServiceTypePopup::class.qualifiedName

    class ServiceDetail(var id:Long,var serviceName:String, var checked: Boolean,var enabled:Boolean)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.getAttributes().dimAmount = 0.6f
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        setCancelable(true)
        setCanceledOnTouchOutside(true)

        setContentView(R.layout.dialog_driver_service_type)
        rvVehicleTypes.layoutManager = LinearLayoutManager(context)
        rvVehicleTypes.adapter = ServiceTypeAdapter(serviceList,rvVehicleTypes)

        btnSave.setOnClickListener {
            val serviceListChecked = serviceList.toMutableList()
            val checked = serviceListChecked.filter { it.checked }.map { it.id }
            Log.i(TAG,checked.toString())

        }

    }


    class ServiceTypeAdapter(var details: List<ServiceDetail>,var recyclerView: RecyclerView) : RecyclerView.Adapter<ServiceTypeAdapter.ServiceTypeViewHolder>() {


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
                holder.checkBox.isChecked = checked
                holder.checkBox.isEnabled = enabled
            }

        }


        inner class ServiceTypeViewHolder(parentView: View): RecyclerView.ViewHolder(parentView) {
            val checkBox: AppCompatCheckBox = parentView.findViewById(R.id.cbService) as AppCompatCheckBox

           init {
               checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
                   val pos = recyclerView.getChildLayoutPosition(parentView)
                   if(pos!=RecyclerView.NO_POSITION){
                       details[pos].checked = isChecked
                   }
               }
           }

        }
    }
}
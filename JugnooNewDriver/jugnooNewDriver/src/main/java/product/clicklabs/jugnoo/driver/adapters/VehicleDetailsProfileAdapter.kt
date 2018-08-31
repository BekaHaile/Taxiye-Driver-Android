package product.clicklabs.jugnoo.driver.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.datastructure.FareDetail
import product.clicklabs.jugnoo.driver.utils.Utils

class VehicleDetail(var name:String, var value: String)

public class VehicleDetailsLogin(@Expose @SerializedName("brand") var vehicleMake:String,
                                 @Expose @SerializedName("model_name") var vehicleModel:String,
                                 @Expose @SerializedName("color") var color:String,
                                 @Expose @SerializedName("no_of_doors") var doors:String,
                                 @Expose @SerializedName("no_of_seat_belts") var seatbelts:String)

class VehicleDetailsProfileAdapter(var details: ArrayList<VehicleDetail>) : RecyclerView.Adapter<VehicleDetailsProfileAdapter.VehicleDetailViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleDetailsProfileAdapter.VehicleDetailViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_profile_vehicle_details, parent, false)
        return VehicleDetailViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return details.size;
    }

    override fun onBindViewHolder(holder: VehicleDetailsProfileAdapter.VehicleDetailViewHolder, position: Int) {
        holder.tvName.text = details[position].name
        holder.tvValue.text = details[position].value
        holder.divider.apply {
            if(position==details.size-1){
                visibility = View.GONE
            }else{
                visibility = View.VISIBLE
            }
        }
    }


    inner class VehicleDetailViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvLabel) as TextView
        val tvValue: TextView = view.findViewById(R.id.tvValue) as TextView
        val divider: View = view.findViewById(R.id.sep) as View
    }
}
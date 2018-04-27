package product.clicklabs.jugnoo.driver.ui.adapters

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.adapters.ItemListener
import product.clicklabs.jugnoo.driver.datastructure.VehicleTypeValue
import product.clicklabs.jugnoo.driver.retrofit.model.CityResponse

class VehicleTypeSelectionAdapter(private val context: Context,
                                  private val recyclerView: RecyclerView,
                                  private var vehicleTypes: ArrayList<CityResponse.VehicleType>)
    : RecyclerView.Adapter<VehicleTypeSelectionAdapter.ViewHolderVehicle>(), ItemListener {
    override fun getItemCount(): Int {
        return vehicleTypes.size
    }

    fun setList(vehicleTypes: ArrayList<CityResponse.VehicleType>){
        this.vehicleTypes = vehicleTypes
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolderVehicle, i: Int) {
        val vehicle = vehicleTypes[i]
        val imageRes: Int = when(vehicle.vehicleType) {
            VehicleTypeValue.BIKES.value -> R.drawable.ic_ride_accept_bike
            VehicleTypeValue.TAXI.value -> R.drawable.ic_ride_accept_taxi
            else -> R.drawable.ic_auto_request
        };
        holder.ivVehicle.setImageResource(imageRes)
        holder.ivVehicleTick.setImageResource(if (vehicle.isSelected) R.drawable.ic_tick_green_20 else R.drawable.circle_grey_stroke_theme)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolderVehicle {
        val itemView = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_item_driver_vehicle, viewGroup, false)
        return ViewHolderVehicle(itemView, this)
    }

    inner class ViewHolderVehicle(convertView: View, itemListener: ItemListener) : RecyclerView.ViewHolder(convertView) {
        var clRoot: ConstraintLayout = convertView.findViewById(R.id.clRoot) as ConstraintLayout
        var ivVehicle: ImageView = convertView.findViewById(R.id.ivVehicle) as ImageView
        var ivVehicleTick: ImageView = convertView.findViewById(R.id.ivVehicleTick) as ImageView
        var id: Int = 0

        init{
            clRoot.setOnClickListener { v -> itemListener.onClickItem(convertView, v); }
        }
    }

    override fun onClickItem(parentView: View?, childView: View?) {
        val pos = recyclerView.getChildAdapterPosition(parentView);
        if(pos != RecyclerView.NO_POSITION){
            vehicleTypes[pos].isSelected = !vehicleTypes[pos].isSelected;
            notifyDataSetChanged();
        }
    }
}
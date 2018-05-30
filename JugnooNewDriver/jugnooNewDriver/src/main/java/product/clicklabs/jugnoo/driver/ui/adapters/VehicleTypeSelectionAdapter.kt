package product.clicklabs.jugnoo.driver.ui.adapters

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.adapters.ItemListener
import product.clicklabs.jugnoo.driver.datastructure.VehicleTypeValue
import product.clicklabs.jugnoo.driver.retrofit.model.CityResponse

class VehicleTypeSelectionAdapter(private val context: Context,
                                  private val recyclerView: RecyclerView,
                                  private var vehicleTypes: MutableList<CityResponse.VehicleType>)
    : RecyclerView.Adapter<VehicleTypeSelectionAdapter.ViewHolderVehicle>(), ItemListener {


    private var currentSelectedPos = -1

    fun getCurrentSelectedVehicle(): CityResponse.VehicleType? {
        return if (currentSelectedPos == -1 || currentSelectedPos > vehicleTypes.size) null
        else vehicleTypes[currentSelectedPos]
    }

    fun setCurrentSelectedPos(pos: Int) {
        currentSelectedPos = pos
    }

    override fun getItemCount(): Int {
        return vehicleTypes.size
    }

    fun setList(vehicleTypes: MutableList<CityResponse.VehicleType>) {
        this.vehicleTypes = vehicleTypes
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolderVehicle, i: Int) {
        val vehicle = vehicleTypes[i]
        val imageRes: Int = when (vehicle.vehicleType) {
            VehicleTypeValue.AUTOS.value -> R.drawable.ic_auto_request
            VehicleTypeValue.BIKES.value -> R.drawable.ic_ride_accept_bike
            VehicleTypeValue.TAXI.value -> R.drawable.ic_ride_accept_taxi
            VehicleTypeValue.MINI_TRUCK.value -> R.drawable.ic_ride_accept_mini_truck
            VehicleTypeValue.E_RICK.value -> R.drawable.ic_ride_accept_erick
            else -> R.drawable.ic_auto_request
        }
        if(!TextUtils.isEmpty(vehicle.image)){
            Picasso.with(context).load(vehicle.image).placeholder(imageRes).error(imageRes).into(holder.ivVehicle)
        } else {
            holder.ivVehicle.setImageResource(imageRes)
        }
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


        init {
            clRoot.setOnClickListener { v -> itemListener.onClickItem(convertView, v); }
        }
    }

    override fun onClickItem(parentView: View?, childView: View?) {
        val pos = recyclerView.getChildAdapterPosition(parentView);
        if (pos != RecyclerView.NO_POSITION) {

            if (currentSelectedPos >= 0 && currentSelectedPos < vehicleTypes.size && currentSelectedPos != pos) {
                vehicleTypes[currentSelectedPos].isSelected = false;
                notifyItemChanged(currentSelectedPos)

                vehicleTypes[pos].isSelected = true;
                currentSelectedPos = pos;
                notifyItemChanged(currentSelectedPos);
            }

        }
    }

}
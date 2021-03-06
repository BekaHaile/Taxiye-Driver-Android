package product.clicklabs.jugnoo.driver.ui.adapters

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item_driver_vehicle.view.*
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.adapters.ItemListener
import product.clicklabs.jugnoo.driver.datastructure.VehicleTypeValue
import product.clicklabs.jugnoo.driver.ui.models.CityResponse
import product.clicklabs.jugnoo.driver.utils.Fonts
import product.clicklabs.jugnoo.driver.utils.gone
import product.clicklabs.jugnoo.driver.utils.visible

class VehicleTypeSelectionAdapter(private val context: Context,
                                  private val recyclerView: RecyclerView,
                                  private var vehicleTypes: MutableList<CityResponse.VehicleType>?)
    : RecyclerView.Adapter<VehicleTypeSelectionAdapter.ViewHolderVehicle>(), ItemListener {


    private var currentSelectedPos = -1

    fun getCurrentSelectedVehicle(): CityResponse.VehicleType? {
        return if (currentSelectedPos == -1 || currentSelectedPos > vehicleTypes!!.size) null
        else vehicleTypes!![currentSelectedPos]
    }



    override fun getItemCount(): Int {
        return if(vehicleTypes==null) 0 else vehicleTypes!!.size
    }

    fun setList(vehicleTypes: MutableList<CityResponse.VehicleType>?,defaultIndex: Int) {
        resetCurrentSelectedPosition()
        currentSelectedPos= -1;
        this.vehicleTypes = vehicleTypes
        notifyDataSetChanged()
        setNewPosition(defaultIndex);

    }

    override fun onBindViewHolder(holder: ViewHolderVehicle, i: Int) {
        val vehicle = vehicleTypes!![i]
        val imageRes: Int = when (vehicle.vehicleType) {
            VehicleTypeValue.AUTOS.value -> R.drawable.ic_auto_request
            VehicleTypeValue.BIKES.value -> R.drawable.ic_ride_accept_bike
            VehicleTypeValue.TAXI.value -> R.drawable.ic_ride_accept_taxi
            VehicleTypeValue.MINI_TRUCK.value -> R.drawable.ic_ride_accept_mini_truck
            VehicleTypeValue.E_RICK.value -> R.drawable.ic_ride_accept_erick
            else -> R.drawable.ic_auto_request
        }
        if(!TextUtils.isEmpty(vehicle.driverIcon)){
            holder.pBar.visible()
            Picasso.with(context).load(vehicle.driverIcon).placeholder(null).into(holder.ivVehicle,object: Callback{
                override fun onSuccess() {
                    holder.pBar.gone()
                }

                override fun onError() {
                    holder.pBar.gone()
                    holder.ivVehicle.setImageResource(R.drawable.ic_error_grey_light)

                }
            })
        } else {
            holder.pBar.gone()
            holder.ivVehicle.setImageResource(imageRes)
        }
        holder.ivVehicleTick.setImageResource(if (vehicle.isSelected) R.drawable.ic_tick_green_20 else R.drawable.circle_grey_stroke_theme)
        holder.tvName.setText(vehicle.vehicleName)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolderVehicle {
        val itemView = LayoutInflater.from(viewGroup.context).inflate(R.layout.list_item_driver_vehicle, viewGroup, false)
        return ViewHolderVehicle(itemView, this)
    }

    inner class ViewHolderVehicle(convertView: View, itemListener: ItemListener) : RecyclerView.ViewHolder(convertView) {
        var clRoot: ConstraintLayout = convertView.findViewById(R.id.clRoot) as ConstraintLayout
        var ivVehicle: ImageView = convertView.findViewById(R.id.ivVehicle) as ImageView
        var tvName: TextView = convertView.findViewById(R.id.tvName) as TextView
        var ivVehicleTick: ImageView = convertView.findViewById(R.id.ivVehicleTick) as ImageView
        var pBar: View = convertView.findViewById(R.id.progress_bar) as View


        init {
            tvName.typeface = Fonts.mavenRegular(context)
            clRoot.setOnClickListener { v -> itemListener.onClickItem(convertView, v); }
        }
    }

    override fun onClickItem(parentView: View?, childView: View?) {
        val pos = recyclerView.getChildAdapterPosition(parentView!!);
        if (pos != RecyclerView.NO_POSITION) {

            setNewPosition(pos)

        }
    }

    private fun setNewPosition(pos: Int) {
        if (pos>=0 && vehicleTypes!=null && pos < vehicleTypes!!.size && currentSelectedPos != pos) {


            resetCurrentSelectedPosition()

            vehicleTypes!![pos].isSelected = true;
            currentSelectedPos = pos;
            notifyItemChanged(currentSelectedPos);
        }
    }

    private fun resetCurrentSelectedPosition() {
        if ( vehicleTypes!=null && currentSelectedPos >= 0 && currentSelectedPos < vehicleTypes!!.size) {
            vehicleTypes!![currentSelectedPos].isSelected = false;
            notifyItemChanged(currentSelectedPos)
        }
    }

}
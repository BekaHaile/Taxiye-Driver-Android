package product.clicklabs.jugnoo.driver.adapters

import android.os.Parcel
import android.os.Parcelable
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.datastructure.FareDetail
import product.clicklabs.jugnoo.driver.utils.Utils
import kotlin.properties.Delegates

class VehicleDetail(var name:String, var value: String?)



class VehicleDetailsProfileAdapter() : RecyclerView.Adapter<VehicleDetailsProfileAdapter.VehicleDetailViewHolder>() {



    lateinit var details: List<VehicleDetail>





    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleDetailsProfileAdapter.VehicleDetailViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_profile_vehicle_details, parent, false)
        return VehicleDetailViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return if(::details.isInitialized) details.size else 0
    }

    fun setList( details: List<VehicleDetail>){
        this.details = details.filter { !it.value.isNullOrEmpty() }
        notifyDataSetChanged()
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

class VehicleDetailsLogin @JvmOverloads constructor(@Expose @SerializedName("vehicle_number") var vehicleNumber:String?,
                                                    @Expose @SerializedName("vehicle_year") var year:String?,

                                                    @Expose @SerializedName("brand") var vehicleMake:String?=null,
                                                    @Expose @SerializedName("model_name") var vehicleModel:String?=null,
                                                    @Expose @SerializedName("model_id") var modelId:Int?=null,

                                                    @Expose @SerializedName("color") var color:String?=null,
                                                    @Expose @SerializedName("color_id") var colorID:Int?=null,

                                                    @Expose @SerializedName("no_of_doors") var doors:String?=null,
                                                    @Expose @SerializedName("door_id") var doorId:Int?=null,

                                                    @Expose @SerializedName("no_of_seat_belts") var seatbelts:String?=null,
                                                    @Expose @SerializedName("seat_belt_id") var seatBeltId:Int?=null):Parcelable{
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(vehicleNumber)
        parcel.writeString(year)
        parcel.writeString(vehicleMake)
        parcel.writeString(vehicleModel)
        parcel.writeValue(modelId)
        parcel.writeString(color)
        parcel.writeValue(colorID)
        parcel.writeString(doors)
        parcel.writeValue(doorId)
        parcel.writeString(seatbelts)
        parcel.writeValue(seatBeltId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VehicleDetailsLogin> {
        override fun createFromParcel(parcel: Parcel): VehicleDetailsLogin {
            return VehicleDetailsLogin(parcel)
        }

        override fun newArray(size: Int): Array<VehicleDetailsLogin?> {
            return arrayOfNulls(size)
        }
    }


}

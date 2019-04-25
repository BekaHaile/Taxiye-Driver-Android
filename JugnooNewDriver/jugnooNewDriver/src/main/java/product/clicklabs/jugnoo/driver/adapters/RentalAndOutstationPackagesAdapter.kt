package product.clicklabs.jugnoo.driver.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.datastructure.UserData
import product.clicklabs.jugnoo.driver.retrofit.model.Packages
import product.clicklabs.jugnoo.driver.utils.Fonts
import product.clicklabs.jugnoo.driver.utils.Utils

class RentalAndOutstationPackagesAdapter : RecyclerView.Adapter<RentalAndOutstationPackagesAdapter.FareDetailViewHolder>() {
    private val rental = 6
    private val outstation = 7
    private var details: ArrayList<Packages>? = null
    private var currency: String? = null

    fun setList(details: List<Packages>, currency: String?){
        this.details = details as ArrayList<Packages>
        this.currency = currency
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RentalAndOutstationPackagesAdapter.FareDetailViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_rental_outstation_packages, parent, false)
        return FareDetailViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return if(details == null) 0 else details!!.size
    }

    override fun onBindViewHolder(holder: RentalAndOutstationPackagesAdapter.FareDetailViewHolder, position: Int) {
        val context : Context = holder.textViewTripsText.context
        setTypeFace(holder, context)
        holder.textViewPKm.typeface = Fonts.mavenRegular(context)
        if(holder.adapterPosition == ((details?.size!!) - 1)){
            holder.ivSeparator.visibility = View.GONE
        }
        var strRentalInfo = ""
            if (details?.get(position)?.fareThresholdTime != null && details?.get(position)?.fareThresholdTime != 0.0) {
                val timeInMins = details?.get(position)?.fareThresholdTime!!
                val time: String
                if (timeInMins >= 60) {
                    val hours =  (timeInMins / 60).toInt()
                    val  minutes = timeInMins % 60
                    val strMins = if(minutes > 1) context.getString(R.string.rental_mins).plus(" ") else context.getString(R.string.rental_min).plus(" ")
                    val strHours = if(hours > 1) context.getString(R.string.rental_hours).plus(" ") else context.getString(R.string.rental_hour).plus(" ")
                    time = strRentalInfo + (Utils.getDecimalFormat().format(hours) + " " +strHours + Utils.getDecimalFormat().format(minutes)+" "+strMins + " | ")
                } else {
                    time = strRentalInfo + (timeInMins.toString() + " " + if (timeInMins <= 1) context.getString(R.string.rental_min).plus(" ") else context.getString(R.string.rental_mins).plus(" | "))
                }
                strRentalInfo += time
            }
            if (details?.get(position)?.fareThresholdDistance != null && details?.get(position)?.fareThresholdDistance != 0.0) {
                strRentalInfo = strRentalInfo + context.getString(R.string.rental_max) + " " + details?.get(position)?.fareThresholdDistance.toString().plus(" ") + UserData.getDistanceUnit(context) + " | "
            }
            if (details?.get(position)?.fareFixed != null && details?.get(position)?.fareFixed != 0.0) {
                strRentalInfo += Utils.formatCurrencyValue(currency, details?.get(position)?.fareFixed.toString())
            }
        if(details?.get(position)?.rideType == rental ){

            holder.textViewTripsText.text = strRentalInfo
            holder.tvBaseFare.text = context.getString(R.string.package_rate_format, " " + details!!.get(position).fareFixed)
            holder.tvBaseFare.visibility = View.GONE
            holder.tvFarePerMinute.text = context.getString(R.string.additional_per_min).plus(": ").plus(Utils.formatCurrencyValue(currency, details!![position].farePerMin, false))
            holder.tvFarePerMile.text = context.getString(R.string.additional_per_km_fare, Utils.getDistanceUnit(UserData.getDistanceUnit(context))).plus(": ").plus(Utils.formatCurrencyValue(currency, details!![position].farePerKmAfterThreshold, false))
            holder.tvPackageName.text = "" + (position + 1).toString().plus(". ").plus(details?.get(position)?.packageName)
            holder.llInRide.visibility = View.GONE
            holder.llBeforeRide.visibility = View.GONE
            holder.textViewTripsText.visibility = View.VISIBLE
            holder.tvPackageName.visibility = View.VISIBLE

        } else if(details?.get(position)?.rideType == outstation ){

            holder.llBeforeRide.visibility = View.GONE
            holder.textViewTripsText.text = strRentalInfo
            holder.tvBaseFare.visibility = View.GONE
            holder.llInRide.visibility = View.GONE
            holder.textViewTripsText.visibility = View.VISIBLE
            holder.tvPackageName.text = "" + (position + 1).toString().plus(". ").plus(details?.get(position)?.packageName)

            val baseFare = if(details?.get(position)?.displayBaseFare!! != 0.0) Utils.formatCurrencyValue(currency, details?.get(position)?.displayBaseFare!!) else Utils.formatCurrencyValue(currency, details?.get(position)?.farePerKm!!, false)
            holder.tvBaseFare.text = context.getString(R.string.base_fare_format, " ".plus(baseFare))
            holder.tvFarePerMinute.text = context.getString(R.string.nl_per_min).plus(": ").plus(Utils.formatCurrencyValue(currency, details?.get(position)?.farePerMin!!, false))
            holder.tvFarePerMile.text = context.getString(R.string.per_format, Utils.getDistanceUnit(UserData.getDistanceUnit(context)) + ": "
                    + Utils.formatCurrencyValue(currency, details?.get(position)?.farePerKm!!, false))
            holder.tvPackageName.visibility = View.VISIBLE

        }  else {
            holder.llBeforeRide.visibility = View.VISIBLE
            holder.textViewPKm.text = context.getString(R.string.per_format, Utils.getDistanceUnit(UserData.getDistanceUnit(context)))
            holder.textViewBaseFareValue.text = Utils.formatCurrencyValue(currency, details?.get(position)?.fareFixed!!, false)
            holder.textViewDistancePKmValue.text = Utils.formatCurrencyValue(currency, details?.get(position)?.farePerKm!!, false)
            holder.textViewTimePKmValue.text = Utils.formatCurrencyValue(currency, details?.get(position)?.farePerMin!!, false)
            holder.textViewPickupChargesperkm.text = context.getString(R.string.per_format, Utils.getDistanceUnit(UserData.getDistanceUnit(context)))
            if (details?.get(position)?.farePerKmThresholdDistance!! > 0) {

                holder.textViewDifferentialPricingEnable.visibility = View.VISIBLE
                holder.textViewDifferentialPricingEnable.text = context.getString(R.string.diffrential_pricing_rate,
                        details?.get(position)?.farePerKmThresholdDistance!!.toString()
                                + " " + Utils.getDistanceUnit(UserData.getDistanceUnit(context)),
                        (Utils.formatCurrencyValue(currency,
                                details?.get(position)?.farePerKmAfterThreshold!!, false)
                                + "/" + Utils.getDistanceUnit(UserData.getDistanceUnit(context))))
            } else {
                holder.textViewDifferentialPricingEnable.visibility = View.GONE
            }
            if (details?.get(position)?.acceptSubsidyThresholdDistance!! > 0.0) {
                holder.textViewPickupChargesCond.text = context.getString(R.string.applicable_after,
                        details?.get(position)?.acceptSubsidyThresholdDistance.toString()
                                + " " + Utils.getDistanceUnit(UserData.getDistanceUnit(context)))
                holder.textViewPickupChargesCondStar.text = "*"
            }
            else {
                holder.textViewPickupChargesCond.visibility = View.GONE
                holder.textViewPickupChargesCondStar.visibility = View.GONE
            }
            holder.textViewPickupChargesValues.text = Utils.formatCurrencyValue(currency, details?.get(position)?.acceptSubsidyAfterThreshold!!, false)
            holder.tvBaseFare.text = context.getString(R.string.package_rate_format, " " + details!![position].fareFixed)
            holder.tvBaseFare.visibility = View.GONE
            holder.tvFarePerMinute.text = context.getString(R.string.additional_per_min).plus(": ").plus(Utils.formatCurrencyValue(currency, details!![position].farePerMin, false))
            holder.tvFarePerMile.text = context.getString(R.string.additional_per_km_fare, Utils.getDistanceUnit(UserData.getDistanceUnit(context))).plus(": ").plus(Utils.formatCurrencyValue(currency, details!![position].farePerKmAfterThreshold, false))
            holder.llInRide.visibility = View.VISIBLE
            holder.textViewTripsText.visibility = View.GONE
            holder.tvPackageName.visibility = View.GONE
            holder.tvFarePerMinute.visibility = View.GONE
            holder.tvFarePerMile.visibility = View.GONE
            holder.ivSeparatorUpper.visibility = View.GONE
        }

    }

    private fun setTypeFace(holder: RentalAndOutstationPackagesAdapter.FareDetailViewHolder, context: Context) {
        holder.textViewPickupChargesValues.typeface = Fonts.mavenRegular(context)
        holder.textViewTripsText.typeface = Fonts.mavenMedium(context)
        holder.tvBaseFare.typeface = Fonts.mavenRegular(context)
        holder.tvFarePerMinute.typeface = Fonts.mavenRegular(context)
        holder.tvFarePerMile.typeface = Fonts.mavenRegular(context)
        holder.textViewPKm.typeface = Fonts.mavenRegular(context)
        holder.textViewBaseFareValue.typeface = Fonts.mavenRegular(context)
        holder.textViewDistancePKmValue.typeface = Fonts.mavenRegular(context)
        holder.textViewDifferentialPricingEnable.typeface = Fonts.mavenRegular(context)
        holder.textViewPickupChargesperkm.typeface = Fonts.mavenRegular(context)
        holder.textViewPickupChargesCond.typeface = Fonts.mavenRegular(context)
        holder.textViewPickupChargesCondStar.typeface = Fonts.mavenRegular(context)
        holder.textViewPickupChargesValues.typeface = Fonts.mavenRegular(context)
        holder.textViewPickupChargesValues.typeface = Fonts.mavenRegular(context)
        holder.tvPackageName.typeface = Fonts.mavenBold(context)
    }


    inner class FareDetailViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textViewTripsText: TextView = view.findViewById(R.id.textViewTripsText) as TextView
        val tvBaseFare: TextView = view.findViewById(R.id.tvBaseFare) as TextView
        val tvFarePerMinute: TextView = view.findViewById(R.id.tvFarePerMinute) as TextView
        val tvFarePerMile: TextView = view.findViewById(R.id.tvFarePerMile) as TextView
        val textViewPKm: TextView = view.findViewById(R.id.textViewPKm) as TextView
        val textViewBaseFareValue: TextView = view.findViewById(R.id.textViewBaseFareValue) as TextView
        val textViewDistancePKmValue: TextView = view.findViewById(R.id.textViewDistancePKmValue) as TextView
        val textViewTimePKmValue: TextView = view.findViewById(R.id.textViewTimePKmValue) as TextView
        val textViewDifferentialPricingEnable: TextView = view.findViewById(R.id.textViewDifferentialPricingEnable) as TextView
        val textViewPickupChargesperkm: TextView = view.findViewById(R.id.textViewPickupChargesperkm) as TextView
        val textViewPickupChargesCond: TextView = view.findViewById(R.id.textViewPickupChargesCond) as TextView
        val textViewPickupChargesCondStar: TextView = view.findViewById(R.id.textViewPickupChargesCondStar) as TextView
        val textViewPickupChargesValues: TextView = view.findViewById(R.id.textViewPickupChargesValues) as TextView
        val tvPackageName: TextView = view.findViewById(R.id.tvPackageName) as TextView
        val llInRide: LinearLayout = view.findViewById(R.id.llInRide) as LinearLayout
        val llBeforeRide: LinearLayout = view.findViewById(R.id.llBeforeRide) as LinearLayout
        val ivSeparator: ImageView = view.findViewById(R.id.ivSeparator) as ImageView
        val ivSeparatorUpper: ImageView = view.findViewById(R.id.ivSeparatorUpper) as ImageView
    }
}
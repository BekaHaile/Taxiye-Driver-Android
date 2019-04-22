package product.clicklabs.jugnoo.driver.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.retrofit.model.Packages

class RentalAndOutstationPackagesAdapter() : RecyclerView.Adapter<RentalAndOutstationPackagesAdapter.FareDetailViewHolder>() {
    private var details: ArrayList<Packages>? = null
    private var currency: String? = null

    public fun setList(details: List<Packages>, currency: String?){
        this.details = details as ArrayList<Packages>
        this.currency = currency
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RentalAndOutstationPackagesAdapter.FareDetailViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_rental_outstation_packages, parent, false)
        return FareDetailViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return if(details == null) 0 else details!!.size;
    }

    override fun onBindViewHolder(holder: RentalAndOutstationPackagesAdapter.FareDetailViewHolder, position: Int) {
        val context : Context = holder.textViewTripsText.context
        holder.textViewTripsText.text = details!![position].fareThresholdDistance.toString()
        holder.tvBaseFare.text = context.getString(R.string.package_rate_format, " " + details!!.get(position).fareFixed)
        holder.tvFarePerMinute.text = context.getString(R.string.additional_per_min).plus(": ").plus(details!!.get(position).fareFixed.toString())
        holder.tvFarePerMile.text = context.getString(R.string.additional_per_km_fare, ": " + details!!.get(position).fareFixed.toString())
    }


    inner class FareDetailViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textViewTripsText: TextView = view.findViewById(R.id.textViewTripsText) as TextView
        val tvBaseFare: TextView = view.findViewById(R.id.tvBaseFare) as TextView
        val tvFarePerMinute: TextView = view.findViewById(R.id.tvFarePerMinute) as TextView
        val tvFarePerMile: TextView = view.findViewById(R.id.tvFarePerMile) as TextView
    }
}
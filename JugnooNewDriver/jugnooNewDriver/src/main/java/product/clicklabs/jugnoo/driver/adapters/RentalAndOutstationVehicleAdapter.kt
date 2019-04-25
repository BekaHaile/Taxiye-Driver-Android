package product.clicklabs.jugnoo.driver.adapters

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.retrofit.model.RentalVehicle

class RentalAndOutstationVehicleAdapter() : RecyclerView.Adapter<RentalAndOutstationVehicleAdapter.FareDetailViewHolder>() {
    private var details: ArrayList<RentalVehicle>? = null
    private var currency: String? = null

    fun setList(details: ArrayList<RentalVehicle>?, currency: String?){
        this.details = details
        this.currency = currency
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RentalAndOutstationVehicleAdapter.FareDetailViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_rental_outstation_vehicle, parent, false)
        return FareDetailViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return if(details == null) 0 else details!!.size;
    }

    override fun onBindViewHolder(holder: RentalAndOutstationVehicleAdapter.FareDetailViewHolder, position: Int) {
        holder.tvPackages.text = details!![position].regionName
        holder.tvPackages.typeface = product.clicklabs.jugnoo.driver.utils.Fonts.mavenRegular(holder.tvPackages.context)

        holder.rvPackages.layoutManager = LinearLayoutManager(holder.rvPackages.context)
        holder.rvPackages.isNestedScrollingEnabled = false
        val rentalAndOutstationPackagesAdapter = RentalAndOutstationPackagesAdapter()
        rentalAndOutstationPackagesAdapter.setList(details!![position].fares, currency)
        holder.rvPackages.adapter = rentalAndOutstationPackagesAdapter
    }


    inner class FareDetailViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvPackages: TextView = view.findViewById(R.id.tvPackages) as TextView
        val rvPackages: RecyclerView = view.findViewById(R.id.rvPackages) as RecyclerView
    }
}
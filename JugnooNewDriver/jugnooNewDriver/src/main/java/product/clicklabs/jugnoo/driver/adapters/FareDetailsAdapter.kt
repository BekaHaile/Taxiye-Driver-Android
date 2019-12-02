package product.clicklabs.jugnoo.driver.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.datastructure.FareDetail
import product.clicklabs.jugnoo.driver.utils.Utils

class FareDetailsAdapter() : RecyclerView.Adapter<FareDetailsAdapter.FareDetailViewHolder>() {
    private var details: ArrayList<FareDetail>? = null
    private var currency: String? = null

    public fun setList(details: ArrayList<FareDetail>?, currency: String?){
        this.details = details
        this.currency = currency
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FareDetailsAdapter.FareDetailViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_fare_detail, parent, false)
        return FareDetailViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return if(details == null) 0 else details!!.size;
    }

    override fun onBindViewHolder(holder: FareDetailsAdapter.FareDetailViewHolder, position: Int) {
        holder.tvName.text = details!![position].name
        holder.tvValue.text = Utils.formatCurrencyValue(currency, details!![position].value)
    }


    inner class FareDetailViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName) as TextView
        val tvValue: TextView = view.findViewById(R.id.tvValue) as TextView
    }
}
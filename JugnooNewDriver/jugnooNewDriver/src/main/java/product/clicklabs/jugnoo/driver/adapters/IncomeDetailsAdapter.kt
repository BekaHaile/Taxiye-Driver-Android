package product.clicklabs.jugnoo.driver.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.utils.Fonts

class IncomeDetailsAdapter(var listIncome: List<String>,var callback: Callback) : RecyclerView.Adapter<IncomeDetailsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_support_option,parent,false)
        return ViewHolder(view,callback)
    }

    override fun getItemCount(): Int {
        return if(listIncome.isNullOrEmpty()) 0 else listIncome.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvName.text = listIncome[position]
    }


    class ViewHolder(itemView: View,callback: Callback) : RecyclerView.ViewHolder(itemView) {
        var tvName: TextView

        init {
            tvName = itemView.findViewById<View>(R.id.tvName) as TextView
            tvName.typeface = Fonts.mavenRegular(itemView.context)
            tvName.setOnClickListener { callback.onItemClick(adapterPosition)}
        }
    }

    interface Callback {
        fun onItemClick(position: Int)
    }
}
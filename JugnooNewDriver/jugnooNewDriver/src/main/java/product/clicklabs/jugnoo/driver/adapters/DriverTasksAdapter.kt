package product.clicklabs.jugnoo.driver.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.retrofit.model.drivertaks.Tasks
import product.clicklabs.jugnoo.driver.utils.DateOperations
import product.clicklabs.jugnoo.driver.utils.Utils

class DriverTasksAdapter() : RecyclerView.Adapter<DriverTasksAdapter.FareDetailViewHolder>() {
    private var details: ArrayList<Tasks>? = null
    private var currency: String? = null

    fun setList(details: ArrayList<Tasks>?, currency: String?){
        this.details = details
        this.currency = currency
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DriverTasksAdapter.FareDetailViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_tasks, parent, false)
        return FareDetailViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return if(details == null) 0 else details!!.size;
    }

    override fun onBindViewHolder(holder: DriverTasksAdapter.FareDetailViewHolder, position: Int) {
        val context = holder.tvBrandingOffer.context
        holder.tvBrandingOffer.text = context.getString(R.string.branding_msg, Utils.formatCurrencyValue(currency, details!![position].advertiseCredits.toString()))
        holder.tvBrandingOffer.typeface = product.clicklabs.jugnoo.driver.utils.Fonts.mavenRegular(holder.tvBrandingOffer.context)

        if(position == details!!.size -1 ){
            holder.ivSeparator.visibility = View.GONE
        } else {
            holder.ivSeparator.visibility = View.VISIBLE
        }
        if(details!![position].endTime != null) {
            holder.tvValidTill.visibility = View.VISIBLE
            holder.tvValidTill.text = context.getString(R.string.valid_till, DateOperations.utcToLocalForSelfBranding(details!![position].endTime))
            holder.tvValidTill.typeface = product.clicklabs.jugnoo.driver.utils.Fonts.mavenRegular(holder.tvBrandingOffer.context)
        } else {
            holder.tvValidTill.visibility = View.GONE
        }
        holder.viewMain.setOnClickListener {
            (context as DriverTasksListener).onTaskClicked(details!![position])
        }
    }


    inner class FareDetailViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val tvBrandingOffer: TextView = view.findViewById(R.id.tvBrandingOffer) as TextView
        val tvValidTill: TextView = view.findViewById(R.id.tvValidTill) as TextView
        val ivSeparator: ImageView = view.findViewById(R.id.ivSeparator) as ImageView
        val viewMain: View = view.findViewById(R.id.viewMain) as View
    }

    interface DriverTasksListener {
        fun onTaskClicked(tasks : Tasks)
    }
}
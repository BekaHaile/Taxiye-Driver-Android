package product.clicklabs.jugnoo.driver.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_refer_task.view.*
import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.retrofit.model.ReferInfo
import product.clicklabs.jugnoo.driver.utils.Prefs
import product.clicklabs.jugnoo.driver.utils.Utils

class ReferTaskAdapter(var list: List<ReferInfo>):RecyclerView.Adapter<ReferTaskAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_refer_task,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if(list.isNullOrEmpty()) 0 else list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var context = holder.tvStatus.context
        holder.tvDriverNameValue.text = list[position].name
        holder.tvCreditsValue.text = list[position].credits.toString()
        holder.tvDriverNoValue.text = list[position].phoneNo
        holder.tvMoneyValue.text = Utils.formatCurrencyValue(Prefs.with(context).getString(Constants.KEY_CURRENCY,"INR"),list[position].money.toString())
        if(list[position].status == TaskType.SUCCESS.i) {
            holder.tvStatus.text = context.getString(R.string.completed)
            holder.tvStatus.setTextColor(context.resources.getColor(R.color.green_online))
            holder.tvStatus.background = context.resources.getDrawable(R.drawable.green_rounded_with_dim_green)

        } else if(list[position].status == TaskType.PENDING.i) {
            holder.tvStatus.text = context.getString(R.string.pending)
            holder.tvStatus.setTextColor(context.resources.getColor(R.color.themeColor))
            holder.tvStatus.background = context.resources.getDrawable(R.drawable.theme_stroke_alpha_background)
        } else {
            holder.tvStatus.text = context.getString(R.string.failed)
            holder.tvStatus.setTextColor(context.resources.getColor(R.color.red_offline))
            holder.tvStatus.background = context.resources.getDrawable(R.drawable.red_rounded_with_alpha_background)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvMoneyValue = view.tvMoneyValue
        var tvCreditsValue = view.tvCreditsValue
        var tvDriverNoValue = view.tvDriverNoValue
        var tvDriverNameValue = view.tvDriverNameValue
        var tvStatus = view.tvStatus
    }
}

enum class TaskType(val i: Int) {
    PENDING(0),
    SUCCESS(1),
    FAILED(2)
}
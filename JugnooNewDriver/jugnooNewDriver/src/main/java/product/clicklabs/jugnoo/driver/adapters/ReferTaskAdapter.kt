package product.clicklabs.jugnoo.driver.adapters

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.item_refer_task.view.*
import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.MyApplication
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.retrofit.model.ReferInfo
import product.clicklabs.jugnoo.driver.utils.*

class ReferTaskAdapter(var list: List<ReferInfo>): RecyclerView.Adapter<ReferTaskAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_refer_task,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return if(list.isNullOrEmpty()) 0 else list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var context = holder.tvDriverNameValue.context
        if(list[position].name.isNullOrEmpty()) {
            holder.groupName.gone()
        } else {
            holder.groupName.visible()
            holder.tvDriverNameValue.text = list[position].name
        }
        holder.tvDriverNoValue.text = list[position].phoneNo

        if(list[position].totalMoney == 0){
            holder.groupTotalMoney.gone()
        } else {
            holder.groupTotalMoney.visible()
            holder.tvTotalMoneyValue.text = Utils.formatCurrencyValue(Prefs.with(context).getString(Constants.KEY_CURRENCY,
                    MyApplication.getInstance().getResources().getString(R.string.currency_fallback)),list[position].totalMoney.toString())
        }

        if(list[position].totalCredits == 0) {
            holder.groupTotalCredits.gone()
        } else {
            holder.groupTotalCredits.visible()
            holder.tvTotalCreditsValue.text = list[position].totalCredits.toString()
        }

        if(list[position].processedMoney == 0) {
            holder.groupMoneyProcessed.gone()
        } else {
            holder.groupMoneyProcessed.visible()
            holder.tvMoneyProcessedValue.text = Utils.formatCurrencyValue(Prefs.with(context).getString(Constants.KEY_CURRENCY,
                    MyApplication.getInstance().getResources().getString(R.string.currency_fallback)),list[position].processedMoney.toString())
        }

        if(list[position].processedCredits == 0) {
            holder.groupCreditsProcessed.gone()
        } else {
            holder.groupCreditsProcessed.visible()
            holder.tvCreditsProcessedValue.text = list[position].processedCredits.toString()
        }

        if(list[position].userNumRides == 0) {
            holder.groupTotalTargets.gone()
        } else {
            holder.groupTotalTargets.visible()
            holder.tvTotalTargetsValue.text = list[position].userNumRides.toString()
        }

        if(list[position].status == TaskType.SUCCESS.i) {
            if(list[position].nextTarget?.numOfRidesNextTarget == 0) {
                holder.tvStatus2.visibility = View.GONE
            } else {
                holder.tvStatus2.visibility = View.VISIBLE
            }
            holder.tvStatus2.setTextColor(context.resources.getColor(R.color.green_online))
            holder.tvStatus2.background = context.resources.getDrawable(R.drawable.green_rounded_with_dim_green)
            list[position].nextTarget?.let {
                holder.tvStatus2.setTextColor(context.resources.getColor(R.color.themeColor))
                holder.tvStatus2.background = context.resources.getDrawable(R.drawable.theme_stroke_alpha_background)
            }
            if(list[position].processedCredits != 0) {
                holder.groupCreditsProcessed.visible()
            }
            if(list[position].processedMoney != 0) {
                holder.groupMoneyProcessed.visible()
            }
            if(list[position].userNumRides != 0) {
                holder.groupTotalTargets.visible()
            }

        } else if(list[position].status == TaskType.PENDING.i) {
            holder.groupCreditsProcessed.gone()
            holder.groupMoneyProcessed.gone()
            holder.groupTotalTargets.gone()
            holder.tvStatus2.setTextColor(context.resources.getColor(R.color.themeColor))
            holder.tvStatus2.background = context.resources.getDrawable(R.drawable.theme_stroke_alpha_background)
        } else {
            holder.groupCreditsProcessed.gone()
            holder.groupMoneyProcessed.gone()
            holder.groupTotalTargets.gone()
            holder.tvStatus2.setTextColor(context.resources.getColor(R.color.red_offline))
            holder.tvStatus2.background = context.resources.getDrawable(R.drawable.red_rounded_with_alpha_background)
        }

        holder.tvStatus2.text = list[position].taskMessage
    }

    fun updateList(list: List<ReferInfo>) {
        this.list = list
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var groupTotalMoney = view.groupTotalMoney
        var groupTotalCredits = view.groupTotalCredits
        var groupMoneyProcessed = view.groupMoneyProcessed
        var groupCreditsProcessed = view.groupCreditsProcessed
        var groupTotalTargets = view.groupTotalTargets
        var groupName = view.groupName
        var groupNo = view.groupNo
        var tvCreditsProcessedValue = view.tvCreditsProcessedValue
        var tvMoneyProcessedValue = view.tvMoneyProcessedValue
        var tvTotalCreditsValue = view.tvTotalCreditsValue
        var tvTotalMoneyValue = view.tvTotalMoneyValue
        var tvTotalTargetsValue = view.tvTotalTargetsValue
        var tvDriverNoValue = view.tvDriverNoValue
        var tvDriverNameValue = view.tvDriverNameValue
//        var tvStatus = view.tvStatus
        var tvStatus2 = view.tvStatus2
    }
}

enum class TaskType(val i: Int) {
    PENDING(0),
    SUCCESS(1),
    FAILED(2)
}
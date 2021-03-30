package product.clicklabs.jugnoo.driver.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.textview_safety_info.view.*
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.datastructure.SafetyPoint
import product.clicklabs.jugnoo.driver.utils.Fonts
import java.util.*

/**
 * Created by shankar on 21/04/20.
 */
class SafetyInfoListAdapter(private val recyclerView: RecyclerView, private var safetyPoints: ArrayList<SafetyPoint>)
    : RecyclerView.Adapter<SafetyInfoListAdapter.ViewHolderSafetyInfo>(), ItemListener {


    fun setList(safetyPoints: ArrayList<SafetyPoint>) {
        this.safetyPoints = safetyPoints
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return safetyPoints.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderSafetyInfo {
        return ViewHolderSafetyInfo(LayoutInflater.from(parent.context).inflate(R.layout.textview_safety_info, parent, false), this)
    }

    override fun onBindViewHolder(holder: ViewHolderSafetyInfo, position: Int) {
        holder.bind(safetyPoints[position])
    }

    override fun onClickItem(parentView: View?, childView: View?) {
        if(parentView == null || childView == null){
            return
        }
        val pos = recyclerView.getChildLayoutPosition(parentView)
        if(pos == RecyclerView.NO_POSITION){
            return
        }
        when(childView.id){
            R.id.textView ->{
                val safetyPoint = safetyPoints[pos]
                safetyPoint.isSelected = !safetyPoint.isSelected
                notifyItemChanged(pos)
            }
        }

    }


    inner class ViewHolderSafetyInfo(view: View, listener: ItemListener) : RecyclerView.ViewHolder(view) {
        init {
            itemView.textView.typeface = Fonts.mavenRegular(itemView.context)
            itemView.textView.setOnClickListener{ v->
                listener.onClickItem(itemView, v)
            }
        }

        fun bind(safetyPoint:SafetyPoint){
            itemView.textView.text = (if(safetyPoint.isMandatory()) "*" else "").plus(safetyPoint.name)
            itemView.textView.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    if(safetyPoint.isSelected) R.drawable.ic_tick_green else R.drawable.ic_untick_box,
                    0, 0, 0)
        }
    }

}

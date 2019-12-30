package product.clicklabs.jugnoo.driver.adapters

import android.app.Activity
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import product.clicklabs.jugnoo.driver.R

class RingtoneSelectionAdapter(val activity: Activity, val rv: RecyclerView, val callback: Callback) :
        RecyclerView.Adapter<RingtoneSelectionAdapter.RingtoneViewHolder>(), ItemListener {

    private var ringtones = mutableListOf<RingtoneModel>()

    fun setList(ringtones: MutableList<RingtoneModel>) {
        this.ringtones = ringtones
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RingtoneViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_ringtone, parent, false)
        return RingtoneViewHolder(itemView, this)
    }

    override fun getItemCount(): Int {
        return ringtones.size
    }

    override fun onBindViewHolder(holder: RingtoneViewHolder, position: Int) {
        holder.rbRingtone.isChecked = ringtones[position].isChecked
        holder.rbRingtone.text = ringtones[position].ringName
    }


    inner class RingtoneViewHolder(view: View, listener: ItemListener) : RecyclerView.ViewHolder(view) {
        var rbRingtone: RadioButton = view as RadioButton

        init {
            rbRingtone.setOnClickListener {
                listener.onClickItem(view, rbRingtone)
            }
        }
    }

    override fun onClickItem(parentView: View?, childView: View?) {
        val pos: Int = rv.getChildLayoutPosition(parentView!!)
        if (pos != RecyclerView.NO_POSITION) {
            when (childView!!.id) {
                R.id.rbRingtone -> {
                    for (cat in ringtones) {
                        cat.isChecked = false
                    }
                    ringtones[pos].isChecked = true
                    notifyDataSetChanged()
                    callback.onRingtoneClick(pos, ringtones[pos])
                }
            }
        }
    }

    interface Callback {
        fun onRingtoneClick(pos: Int, category: RingtoneModel)
    }
}

class RingtoneModel(var ringType:Int, var ringName:String, var isChecked:Boolean)
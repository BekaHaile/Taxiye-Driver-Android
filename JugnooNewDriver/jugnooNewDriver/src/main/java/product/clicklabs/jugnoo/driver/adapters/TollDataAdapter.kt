package product.clicklabs.jugnoo.driver.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.retrofit.model.TollData
import product.clicklabs.jugnoo.driver.utils.Utils

class TollDataAdapter(val rv:RecyclerView, val callback:Callback) : RecyclerView.Adapter<TollDataAdapter.FareDetailViewHolder>(),ItemListener {

    private val tollData = mutableListOf<TollData>()
    private lateinit var currency: String

    public fun setList(tollData: ArrayList<TollData>?, currency:String){
        this.tollData.clear()
        for(toll in tollData!!.iterator()){
            if(!toll.deleted){
                this.tollData.add(toll)
            }
        }
        this.currency = currency
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TollDataAdapter.FareDetailViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_toll, parent, false)
        return FareDetailViewHolder(itemView, this)
    }

    override fun getItemCount(): Int {
        return tollData.size;
    }

    override fun onBindViewHolder(holder: TollDataAdapter.FareDetailViewHolder, position: Int) {
        holder.tvTollName.text = tollData[position].tollName
        holder.tvTollValue.text = Utils.formatCurrencyValue(currency, tollData[position].toll)
        holder.vDiv.visibility = if(position == itemCount-1) View.GONE else View.VISIBLE
    }


    inner class FareDetailViewHolder(view: View, listener:ItemListener): RecyclerView.ViewHolder(view) {
        val tvTollName: TextView = view.findViewById(R.id.tvTollName) as TextView
        val tvTollValue: TextView = view.findViewById(R.id.tvTollValue) as TextView
        val ivDelete: ImageView = view.findViewById(R.id.ivDelete) as ImageView
        val vDiv: View = view.findViewById(R.id.vDiv) as View
        init{
            ivDelete.setOnClickListener{v->
                listener.onClickItem(view, v)
            }
        }
    }

    override fun onClickItem(parentView: View?, childView: View?) {
        val pos:Int = rv.getChildLayoutPosition(parentView)
        if(pos != RecyclerView.NO_POSITION){
            when(childView!!.id){
                R.id.ivDelete->{
                    setList(callback.onDeleteClick(pos, tollData[pos]), currency)
                }
            }
        }
    }

    interface Callback {
        fun onDeleteClick(pos:Int, tollData:TollData) : ArrayList<TollData>
    }
}
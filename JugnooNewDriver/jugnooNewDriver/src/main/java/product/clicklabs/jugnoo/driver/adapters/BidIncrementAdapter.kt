package product.clicklabs.jugnoo.driver.adapters

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.utils.Utils



class BidIncrementAdapter(val activity:Activity, var rv:RecyclerView, val callback:Callback) : RecyclerView.Adapter<BidIncrementAdapter.BidValViewHolder>(),ItemListener {

    private var bidVals:MutableList<BidIncrementVal>? = mutableListOf()
    private var currency:String? = "INR"
    private var parentId:Int = 0

    fun setList(parentId:Int, currency:String?, initialBidVal:Double, increment:Double, selectedVal:Double,  rv:RecyclerView){
        this.rv = rv
        this.parentId = parentId
        this.currency = currency
        bidVals!!.clear()
        for(i in 1..5){
            val incValue = initialBidVal + (i*increment*initialBidVal/100.toDouble())
            bidVals!!.add(BidIncrementVal(incValue, selectedVal == incValue))
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BidValViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_bid_increment, parent, false)

        val dp10 = Utils.dpToPx(parent.context, 10f)
        val params = itemView.layoutParams as ViewGroup.MarginLayoutParams
        params.marginStart = dp10
        params.marginEnd = dp10
        params.bottomMargin = dp10
        itemView.layoutParams = params

        return BidValViewHolder(itemView, this)
    }

    override fun getItemCount(): Int {
        return if(bidVals == null) 0 else bidVals!!.size;
    }

    override fun onBindViewHolder(holder: BidValViewHolder, position: Int) {
        holder.tvBidVal.text = Utils.formatCurrencyValue(currency, bidVals!![position].value!!)
    }


    inner class BidValViewHolder(view: View, listener:ItemListener): RecyclerView.ViewHolder(view) {
        val tvBidVal: TextView = view as TextView
        init{
            tvBidVal.setOnClickListener{v->
                listener.onClickItem(view, v)
            }
        }
    }

    override fun onClickItem(parentView: View?, childView: View?) {
        val pos:Int = rv.getChildLayoutPosition(parentView)
        if(pos != RecyclerView.NO_POSITION){
            when(childView!!.id){
                R.id.tvBidVal->{
                    for(bid in bidVals!!){
                        bid.selected = false
                    }
                    bidVals!![pos].selected = true
                    notifyDataSetChanged()
                    callback.onClick(bidVals!![pos], parentId)
                }
            }
        }
    }

    interface Callback {
        fun onClick(incrementVal:BidIncrementVal, parentId:Int)
    }
}

class BidIncrementVal(var value:Double?, var selected:Boolean)
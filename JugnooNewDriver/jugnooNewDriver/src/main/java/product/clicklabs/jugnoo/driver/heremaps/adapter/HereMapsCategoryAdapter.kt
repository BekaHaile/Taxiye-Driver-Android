package product.clicklabs.jugnoo.driver.heremaps.adapter

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.adapters.ItemListener
import product.clicklabs.jugnoo.driver.heremaps.model.HMCategory

class HereMapsCategoryAdapter(val activity: Activity, val rv:RecyclerView, val callback: Callback) :
        RecyclerView.Adapter<HereMapsCategoryAdapter.HereMapsCategoryViewHolder>(), ItemListener {

    private var categories = mutableListOf<HMCategory>()

    public fun setList(categories: MutableList<HMCategory>){
        this.categories = categories
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HereMapsCategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_here_map_category, parent, false)
        return HereMapsCategoryViewHolder(itemView, this)
    }

    override fun getItemCount(): Int {
        return categories.size;
    }

    override fun onBindViewHolder(holder: HereMapsCategoryViewHolder, position: Int) {
        holder.rbCategoryName.isChecked = categories[position].isChecked
        holder.rbCategoryName.text = categories[position].description
    }


    inner class HereMapsCategoryViewHolder(view: View, listener:ItemListener): RecyclerView.ViewHolder(view) {
        var rbCategoryName:RadioButton = view as RadioButton
        init{
            rbCategoryName.setOnClickListener{
                listener.onClickItem(view, rbCategoryName)
            }
        }
    }

    override fun onClickItem(parentView: View?, childView: View?) {
        val pos:Int = rv.getChildLayoutPosition(parentView)
        if(pos != RecyclerView.NO_POSITION){
            when(childView!!.id){
                R.id.rbCategoryName->{
                    for(cat in categories){
                        cat.isChecked = false
                    }
                    categories[pos].isChecked = true
                    notifyDataSetChanged()
                    callback.onCategoryClick(pos, categories[pos])
                }
            }
        }
    }

    interface Callback {
        fun onCategoryClick(pos:Int, category: HMCategory)
    }
}
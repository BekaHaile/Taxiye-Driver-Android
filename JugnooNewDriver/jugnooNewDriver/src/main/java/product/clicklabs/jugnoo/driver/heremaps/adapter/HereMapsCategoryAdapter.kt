package product.clicklabs.jugnoo.driver.heremaps.adapter

import android.app.Activity
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.adapters.ItemListener
import product.clicklabs.jugnoo.driver.heremaps.model.HMCategory

class HereMapsCategoryAdapter(val activity: Activity, val rv: RecyclerView, val callback: Callback) :
        RecyclerView.Adapter<HereMapsCategoryAdapter.HereMapsCategoryViewHolder>(), ItemListener {

    private var categories = mutableListOf<HMCategory>()
    private var categoriesSearch = mutableListOf<HMCategory>()

    fun setList(categories: MutableList<HMCategory>) {
        this.categories = categories
        categoriesSearch.clear()
        categoriesSearch.addAll(categories)
        notifyDataSetChanged()
    }

    fun search(searchText: String, noResultView: View) {
        categoriesSearch.clear()
        if (searchText.isEmpty()) {
            categoriesSearch.addAll(categories)
        } else {
            val containsL = mutableListOf<HMCategory>()
            val startsL = mutableListOf<HMCategory>()
            for (category in categories) {
                if (category.description.equals(searchText, ignoreCase = true)) {
                    categoriesSearch.add(category)
                } else if (category.description.startsWith(searchText, ignoreCase = true)) {
                    startsL.add(category)
                } else if (category.description.contains(searchText, ignoreCase = true)) {
                    containsL.add(category)
                }
            }
            categoriesSearch.addAll(startsL)
            categoriesSearch.addAll(containsL)
        }
        noResultView.visibility = if(categoriesSearch.size == 0) View.VISIBLE else View.GONE
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HereMapsCategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_here_map_category, parent, false)
        return HereMapsCategoryViewHolder(itemView, this)
    }

    override fun getItemCount(): Int {
        return categoriesSearch.size;
    }

    override fun onBindViewHolder(holder: HereMapsCategoryViewHolder, position: Int) {
        holder.rbCategoryName.isChecked = categoriesSearch[position].isChecked
        holder.rbCategoryName.text = categoriesSearch[position].description
    }


    inner class HereMapsCategoryViewHolder(view: View, listener: ItemListener) : RecyclerView.ViewHolder(view) {
        var rbCategoryName: RadioButton = view as RadioButton

        init {
            rbCategoryName.setOnClickListener {
                listener.onClickItem(view, rbCategoryName)
            }
        }
    }

    override fun onClickItem(parentView: View?, childView: View?) {
        val pos: Int = rv.getChildLayoutPosition(parentView!!)
        if (pos != RecyclerView.NO_POSITION) {
            when (childView!!.id) {
                R.id.rbCategoryName -> {
                    for (cat in categoriesSearch) {
                        cat.isChecked = false
                    }
                    categoriesSearch[pos].isChecked = true
                    notifyDataSetChanged()
                    callback.onCategoryClick(pos, categoriesSearch[pos])
                }
            }
        }
    }

    interface Callback {
        fun onCategoryClick(pos: Int, category: HMCategory)
    }
}
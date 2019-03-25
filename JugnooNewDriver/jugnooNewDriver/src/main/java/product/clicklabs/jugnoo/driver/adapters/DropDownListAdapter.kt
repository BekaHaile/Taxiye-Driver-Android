package product.clicklabs.jugnoo.driver.adapters

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.datastructure.Gender
import product.clicklabs.jugnoo.driver.utils.Fonts


class DropDownListAdapter(mContext : Context, mResource : Int, dataList : ArrayList<Gender>, isFirstItemTitle : Boolean) : ArrayAdapter<Gender>(mContext, mResource, 0, dataList) {

    private var dataList: ArrayList<Gender> = ArrayList()
    private var isFirstItemTitle: Boolean = false
    private var resourceId: Int? = null

    init {
        this.dataList = dataList
        this.isFirstItemTitle = isFirstItemTitle
        this.resourceId = mResource
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = super.getView(position, convertView, parent)
        if (view is TextView) {
            (view).typeface = Fonts.mavenRegular(context)
            if(isFirstItemTitle && position == 0) {
                view.setTextColor(ContextCompat.getColor(parent!!.context, R.color.textColorLight))
            } else {
                view.setTextColor(ContextCompat.getColor(parent!!.context, R.color.textColor))
            }

        }

        return view
    }


    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = super.getDropDownView(position, convertView, parent)
        if (view is TextView) {
            (view).typeface = Fonts.mavenRegular(context)
        }

        return view
    }


}
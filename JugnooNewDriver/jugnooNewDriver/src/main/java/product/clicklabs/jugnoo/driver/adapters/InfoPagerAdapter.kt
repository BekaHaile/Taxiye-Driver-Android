package product.clicklabs.jugnoo.driver.adapters

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.datastructure.PagerInfo

/**
 * Created by Shankar on 13/08/19.
 */

class InfoPagerAdapter(private var mContext: Context, private var infoList: List<PagerInfo>?) : PagerAdapter() {
    private var mLayoutInflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return infoList!!.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as ConstraintLayout
    }

    @Synchronized
    fun setList(mResources: List<PagerInfo>) {
        this.infoList = mResources
        notifyDataSetChanged()
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView = mLayoutInflater.inflate(R.layout.info_pager_item, container, false)
        val params = itemView.layoutParams
        params.height = ViewPager.LayoutParams.MATCH_PARENT
        itemView.layoutParams = params

        val info = infoList!![position]

        val iv = itemView.findViewById(R.id.iv) as ImageView
        val tvTitle = itemView.findViewById(R.id.tvTitle) as TextView
        val tvMessage = itemView.findViewById(R.id.tvMessage) as TextView
        if(!TextUtils.isEmpty(info.image)){
            Picasso.with(mContext).load(info.image)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(iv)
        }

        tvTitle.text = info.title
        tvMessage.text = info.message
        tvTitle.visibility = if(TextUtils.isEmpty(info.title)) View.GONE else View.VISIBLE
        tvMessage.visibility = if(TextUtils.isEmpty(info.message)) View.GONE else View.VISIBLE


        container.addView(itemView)

        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)
    }
}

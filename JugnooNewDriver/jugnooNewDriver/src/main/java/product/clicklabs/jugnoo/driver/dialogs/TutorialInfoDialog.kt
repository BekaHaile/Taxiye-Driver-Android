package product.clicklabs.jugnoo.driver.dialogs

import android.app.Activity
import android.app.Dialog
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.adapters.InfoPagerAdapter
import product.clicklabs.jugnoo.driver.datastructure.PagerInfo
import java.util.*

object TutorialInfoDialog{

    fun showAddToll(activity: Activity, pagerInfos: ArrayList<PagerInfo>){
        val dialog = Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar)
        with(dialog){
            window!!.attributes.windowAnimations = R.style.Animations_LoadingDialogFade
            setContentView(R.layout.dialog_info_pager)
            val layoutParams = dialog.window!!.attributes
            layoutParams.dimAmount = 0.6f
            window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            setCancelable(false)
            setCanceledOnTouchOutside(false)

            val imageViewClose = findViewById<ImageView>(R.id.imageViewClose)
            val ivNext = findViewById<ImageView>(R.id.ivNext)
            val viewPagerInfo = findViewById<ViewPager>(R.id.viewPagerInfo)
            val tabDots = findViewById<TabLayout>(R.id.tabDots)
            val infoPagerAdapter = InfoPagerAdapter(activity, pagerInfos)
            viewPagerInfo.adapter = infoPagerAdapter

            tabDots.setupWithViewPager(viewPagerInfo, true)
            for (i in 0 until tabDots.tabCount) {
                val tab = (tabDots.getChildAt(0) as ViewGroup).getChildAt(i)
                val p = tab.layoutParams as ViewGroup.MarginLayoutParams
                p.setMargins(20, 0, 0, 0)
                p.marginStart = 20
                p.marginEnd = 0
                tab.requestLayout()
            }

            ivNext.setOnClickListener {
                if(viewPagerInfo.currentItem < viewPagerInfo.adapter!!.count-1) {
                    viewPagerInfo.currentItem = viewPagerInfo.currentItem + 1
                } else {
                    dismiss()
                }
            }

            imageViewClose.setOnClickListener {
                dismiss()
            }

            show()
        }

    }

}
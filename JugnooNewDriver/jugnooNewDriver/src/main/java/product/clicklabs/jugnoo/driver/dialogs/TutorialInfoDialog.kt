package product.clicklabs.jugnoo.driver.dialogs

import android.app.Activity
import android.app.Dialog
import android.support.design.widget.TabLayout
import android.support.v4.view.ViewPager
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import kotlinx.coroutines.*
import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.HomeUtil
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.adapters.InfoPagerAdapter
import product.clicklabs.jugnoo.driver.datastructure.PagerInfo
import product.clicklabs.jugnoo.driver.datastructure.TutorialDataResponse
import product.clicklabs.jugnoo.driver.retrofit.RestClient
import product.clicklabs.jugnoo.driver.utils.DialogPopup
import retrofit.mime.TypedByteArray

object TutorialInfoDialog{

    fun showAddToll(activity: Activity, latLng: LatLng){
        GlobalScope.launch(Dispatchers.Main){
            DialogPopup.showLoadingDialog(activity, activity.getString(R.string.loading))
            val list = getTutorialsApiAsync(latLng).await()
            DialogPopup.dismissLoadingDialog()
            if(list == null || list.size == 0){
                return@launch
            }

            val dialog = Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar)
            with(dialog){
                window!!.attributes.windowAnimations = R.style.Animations_LoadingDialogFade
                setContentView(R.layout.dialog_info_pager)
                val layoutParams = dialog.window!!.attributes
                layoutParams.dimAmount = 0.6f
                window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                setCancelable(true)
                setCanceledOnTouchOutside(true)

                val imageViewClose = findViewById<ImageView>(R.id.imageViewClose)
                val ivNext = findViewById<ImageView>(R.id.ivNext)
                val viewPagerInfo = findViewById<ViewPager>(R.id.viewPagerInfo)
                val tabDots = findViewById<TabLayout>(R.id.tabDots)
                val infoPagerAdapter = InfoPagerAdapter(activity, list)
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

    private fun getTutorialsApiAsync(latLng: LatLng):Deferred<MutableList<PagerInfo>?>{
        return GlobalScope.async(Dispatchers.IO){
            try {
                val params = HashMap<String, String>()
                params[Constants.KEY_LATITUDE] = latLng.latitude.toString()
                params[Constants.KEY_LONGITUDE] = latLng.longitude.toString()
                params[Constants.KEY_SECTION] = 10.toString()
                HomeUtil.putDefaultParams(params)

                val response = RestClient.getApiServices().fetchTutorialData(params)
                val responseStr = String((response.body as TypedByteArray).bytes)
                val gson = Gson()
                val tutDataResponse = gson.fromJson(responseStr, TutorialDataResponse::class.java)

                tutDataResponse.tutorialsData
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }


}
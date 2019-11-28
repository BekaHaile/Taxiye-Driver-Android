package product.clicklabs.jugnoo.driver.home

import product.clicklabs.jugnoo.driver.DriverCreditsActivity
import product.clicklabs.jugnoo.driver.HomeActivity
import product.clicklabs.jugnoo.driver.R

object HomeBannerAction {

    const val ACTION_D2C = 1
    const val ACTION_D2D = 2

    @JvmStatic
    fun performBannerClick(activity:HomeActivity, index:Int){

        when(index){
            ACTION_D2C, ACTION_D2D -> {
                activity.startActivity(DriverCreditsActivity.createIntent(activity, index))
                activity.overridePendingTransition(R.anim.right_in, R.anim.right_out)
            }
        }

    }

}
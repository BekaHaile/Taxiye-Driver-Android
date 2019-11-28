package product.clicklabs.jugnoo.driver.home

import product.clicklabs.jugnoo.driver.HomeActivity

object HomeBannerAction {

    const val ACTION_D2C = 1
    const val ACTION_D2D = 2
    const val ACTION_PLANS = 3
    const val ACTION_DRIVER_CREDITS = 4
    const val ACTION_MANUAL_REQUEST = 5
    const val ACTION_INCOME_DETAILS = 6
    const val ACTION_EARNINGS = 7
    const val ACTION_PAYMENT = 8
    const val ACTION_WALLET = 9
    const val ACTION_LEADERBOARD = 10
    const val ACTION_TASKS = 11
    const val ACTION_SUPPORT = 12
    const val ACTION_DRIVER_RESOURCES = 13
    const val ACTION_RATE_CARD = 14
    const val ACTION_RATE_CARD_HTML = 15
    const val ACTION_LANGUAGE = 16

    @JvmStatic
    fun performBannerClick(activity:HomeActivity, index:Int){

        when(index){
            ACTION_D2C, ACTION_D2D -> {
                activity.openDriverCreditsActivity(index)
            }
            ACTION_PLANS -> {
                activity.openPlansThroughWebview()
            }
            ACTION_DRIVER_CREDITS -> {
                activity.openDriverCreditsActivity(0)
            }
            ACTION_MANUAL_REQUEST -> {
                activity.openManualRequestActivity()
            }
            ACTION_INCOME_DETAILS -> {
                activity.openIncomeDetailsActivity()
            }
            ACTION_EARNINGS -> {
                activity.openEarningsActivity()
            }
            ACTION_PAYMENT -> {
                activity.openPaymentActivity()
            }
            ACTION_WALLET -> {
                activity.openWalletActivity()
            }
            ACTION_LEADERBOARD -> {
                activity.openLeaderboardActivity()
            }
            ACTION_TASKS -> {
                HomeActivity.openTasksActivity(activity)
            }
            ACTION_SUPPORT -> {
                activity.openSupportOptionsActivity()
            }
            ACTION_DRIVER_RESOURCES -> {
                activity.openDriverResourcesActivity()
            }
            ACTION_RATE_CARD -> {
                activity.openRateCardActivity(false)
            }
            ACTION_RATE_CARD_HTML -> {
                activity.openRateCardActivity(true)
            }
            ACTION_LANGUAGE -> {
                activity.openLanguagePreferenceActivity()
            }
        }

    }

}
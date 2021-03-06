package product.clicklabs.jugnoo.driver

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.appcompat.app.AppCompatActivity
import android.text.TextUtils
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_request.*
import org.json.JSONObject
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo
import product.clicklabs.jugnoo.driver.datastructure.EngagementStatus
import product.clicklabs.jugnoo.driver.fragments.BidInteractionListener
import product.clicklabs.jugnoo.driver.fragments.BidRequestFragment
import product.clicklabs.jugnoo.driver.retrofit.RestClient
import product.clicklabs.jugnoo.driver.retrofit.model.SettleUserDebt
import product.clicklabs.jugnoo.driver.ui.DriverSplashActivity
import product.clicklabs.jugnoo.driver.utils.*
import retrofit.RetrofitError
import retrofit.client.Response
import retrofit.mime.TypedByteArray
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set

class RequestActivity : AppCompatActivity(), ActivityStateCallback, BidInteractionListener {
    override fun acceptClick(customerInfo: CustomerInfo, bidValue: String) {
        acceptRideClick(customerInfo, bidValue)
    }

    override fun rejectCLick(customerInfo: CustomerInfo) {
        rejectRequestFuncCall(customerInfo)
    }

    override fun updateTabs() {
        updateTab()
    }

    override fun closeRequestView() {
        finish()
        overridePendingTransition(0, 0)
        GCMIntentService.stopRing(true, this)
    }

    companion object {
        const val INTENT_ACTION_REFRESH_BIDS = "INTENT_ACTION_REFRESH_BIDS"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request)
        tabDots.setupWithViewPager(vpRequests,true)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.getIntExtra(Constants.KEY_ENGAGEMENT_ID, -1) != -1) {
            setIntent(intent)
        }
    }

    fun updateViewPagerList() {
        if (vpRequests.adapter == null) {
            vpRequests.adapter = RequestPagerAdapter(supportFragmentManager,this).apply {
                notifyRequests()
            }
        } else {
            (vpRequests.adapter as RequestPagerAdapter).notifyRequests()
        }
    }


    fun updateFragments() {
        val adapter = (vpRequests.adapter as RequestPagerAdapter)
        for(i in 0..adapter.count-1){
            val myFragment = adapter.instantiateItem(vpRequests, i) as Fragment
            if(myFragment is BidRequestFragment){
                myFragment.setValuesToUI(adapter.requestList[i])
            }
        }
        updateTab()
    }

    override fun onResume() {
        super.onResume()
        try { LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, IntentFilter(INTENT_ACTION_REFRESH_BIDS)) } catch (ignored: Exception) { }
        if(!isFinishing && intent!=null) {
            if (intent?.getIntExtra(Constants.KEY_ENGAGEMENT_ID, -1) != -1) {
                updateViewPagerList()
            }
        }
        if(Data.getAssignedCustomerInfosListForStatus(
                        EngagementStatus.REQUESTED.getOrdinal()).isNullOrEmpty()) {
            this@RequestActivity.finish()
        }
    }

    fun acceptRideClick(customerInfo:CustomerInfo, bidValue:String) {
        try {
            MyApplication.getInstance().logEvent(FirebaseEvents.RIDE_RECEIVED + "_" + bidValue + "_" + FirebaseEvents.YES, null)
            if (customerInfo.isReverseBid()) {
                if (TextUtils.isEmpty(bidValue)) {
                    Toast.makeText(this, getString(R.string.please_enter_some_value), Toast.LENGTH_SHORT).show()
                    return
                }
                val maxBidMultiplier = Prefs.with(this).getString(Constants.KEY_DRIVER_MAX_BID_MULTIPLIER, "4").toDouble()
                if (maxBidMultiplier * customerInfo.getInitialBidValue() < bidValue.toDouble()) {
                    Utils.showToast(this, getString(R.string.please_enter_less_value_for_bid))
                    return
                }
                setBidForEngagementAPI(this, customerInfo, bidValue.toDouble())
            }
            GCMIntentService.stopRing(true, this)
            FlurryEventLogger.event(FlurryEventNames.RIDE_ACCEPTED)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun setBidForEngagementAPI(activity:Activity, customerInfo: CustomerInfo, bidValue: Double) {

        if (AppStatus.getInstance(activity).isOnline(activity)) {

            val params = HashMap<String, String>()
            params[Constants.KEY_ACCESS_TOKEN] = Data.userData.accessToken
            params[Constants.KEY_ENGAGEMENT_ID] = customerInfo.getEngagementId().toString()
            params[Constants.KEY_BID_VALUE] = bidValue.toString()
            HomeUtil.putDefaultParams(params)
            DialogPopup.showLoadingDialog(this, getString(R.string.loading))

            RestClient.getApiServices().setBidForEngagement(params, object : retrofit.Callback<SettleUserDebt> {
                override fun success(registerScreenResponse: SettleUserDebt, response: Response) {
                    try {
                        val jsonString = String((response.body as TypedByteArray).bytes)
                        val jObj = JSONObject(jsonString)
                        val flag = jObj.getInt("flag")
                        if (flag == ApiResponseFlags.INVALID_ACCESS_TOKEN.getOrdinal()) {
                            HomeActivity.logoutUser(activity, null)
                        } else if (flag == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                            //							DialogPopup.alertPopup(activity, "", jObj.getString(Constants.KEY_MESSAGE));
                            customerInfo.setBidPlaced(1)
                            customerInfo.bidValue = bidValue
                            GCMIntentService.clearNotifications(applicationContext)
                            GCMIntentService.stopRing(false, this@RequestActivity)
                            if(HomeActivity.appInterruptHandler == null) {
                                startActivity(Intent(this@RequestActivity, DriverSplashActivity::class.java))
                                this@RequestActivity.finish()
                            } else {
                                updateFragments()
                            }
                        } else {
                            DialogPopup.alertPopup(activity, "", JSONParser.getServerMessage(jObj))
                        }
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                        DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG)
                    }

                    DialogPopup.dismissLoadingDialog()
                }

                override fun failure(error: RetrofitError) {
                    DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG)
                    DialogPopup.dismissLoadingDialog()
                }
            })
        } else {
            DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG)
        }
    }


    fun rejectRequestFuncCall(customerInfo: CustomerInfo) {
        try {
            if(HomeActivity.appInterruptHandler != null){
                HomeActivity.appInterruptHandler.cancelRequest(customerInfo, object: RequestActivity.RejectRequestCallback{
                    override fun success() {
                        (vpRequests.adapter as RequestPagerAdapter).notifyRequests()
                    }
                })
            } else {
                finish()
                overridePendingTransition(0,0)
                GCMIntentService.stopRing(true, this@RequestActivity)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    override fun onPause() {
        super.onPause()
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver) } catch (ignored: Exception) {}

    }

    override fun onBackPressed() {
        if(HomeActivity.appInterruptHandler == null) {
            finish()
            overridePendingTransition(0,0)
            GCMIntentService.stopRing(true, this@RequestActivity)
        }
    }

    val broadcastReceiver = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            if(vpRequests.adapter != null) {
                (vpRequests.adapter as RequestPagerAdapter).notifyRequests()
                updateFragments()
            }
        }
    }

    private fun updateTab() {
        if (tabDots.tabCount > 1) {
            tabDots.visible()
        } else {
            tabDots.gone()
        }
        for (i in 0 until tabDots.tabCount) {
            val tab = (tabDots.getChildAt(0) as ViewGroup).getChildAt(i)
            val p = tab.layoutParams as ViewGroup.MarginLayoutParams
            p.setMargins(20, 0, 0, 0)
            p.marginStart = 20
            p.marginEnd = 0
            tab.requestLayout()
        }
    }

    interface RejectRequestCallback{
        fun success()
    }

}

class RequestPagerAdapter(var fragmentManager: FragmentManager, var activityStateCallback: ActivityStateCallback) : FragmentStatePagerAdapter(fragmentManager) {

    public val requestList by lazy { ArrayList<Int>() }

    override fun getItem(position: Int): Fragment {
        return BidRequestFragment.newInstance(requestList[position])
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    override fun getCount(): Int {
        return if (requestList.isNullOrEmpty()) 0 else requestList.size
    }

    fun notifyRequests() {
        val customerInfos = Data.getAssignedCustomerInfosListForStatus(
                EngagementStatus.REQUESTED.getOrdinal())
        requestList.clear()
        if(!customerInfos.isNullOrEmpty()) {
            for (i in customerInfos.indices) {
                if (HomeActivity.appInterruptHandler == null) {
                    if (!customerInfos[i].isBidPlaced) {
                        requestList.add(customerInfos[i].engagementId)
                    }
                } else {
                    requestList.add(customerInfos[i].engagementId)
                }
            }
        }
        if(requestList.size == 0) {
            activityStateCallback.closeRequestView()
        } else {
            notifyDataSetChanged()
        }
        activityStateCallback.updateTabs()
    }

}

interface ActivityStateCallback {
    fun closeRequestView()
    fun updateTabs()
}
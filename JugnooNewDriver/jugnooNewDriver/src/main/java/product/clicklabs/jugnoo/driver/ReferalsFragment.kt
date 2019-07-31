package product.clicklabs.jugnoo.driver

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_referals.*
import product.clicklabs.jugnoo.driver.adapters.ReferTaskAdapter
import product.clicklabs.jugnoo.driver.adapters.TaskType
import product.clicklabs.jugnoo.driver.retrofit.model.ReferInfo
import product.clicklabs.jugnoo.driver.retrofit.model.ReferralsInfoResponse
import product.clicklabs.jugnoo.driver.ui.api.APICommonCallbackKotlin
import product.clicklabs.jugnoo.driver.ui.api.ApiCommonKt
import product.clicklabs.jugnoo.driver.ui.api.ApiName
import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponseKotlin
import product.clicklabs.jugnoo.driver.utils.Prefs
import product.clicklabs.jugnoo.driver.utils.Utils
import android.util.TypedValue



class ReferalsFragment : Fragment() {

    var listRefer = ArrayList<ReferInfo>()
    var pendingList = ArrayList<ReferInfo>()
    var successList = ArrayList<ReferInfo>()
    var referTaskAdapter: ReferTaskAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_referals, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvReferralTasks.layoutManager = LinearLayoutManager(context)
        rvReferralTasks.itemAnimator = DefaultItemAnimator()
        callFetchDriverApi()
        tvTaskPending.setOnClickListener {
            if(rvReferralTasks.adapter == null) {
                referTaskAdapter = ReferTaskAdapter(pendingList)
                rvReferralTasks.adapter = referTaskAdapter
            } else {
                referTaskAdapter?.updateList(pendingList)
            }
            tvTaskPending.background = resources.getDrawable(R.color.grey_new)
            val outValue = TypedValue()
            context!!.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
            tvTaskCompleted.setBackgroundResource(outValue.resourceId)
            if(pendingList.isEmpty()) {
                tvNoData.visibility = View.VISIBLE
            } else {
                tvNoData.visibility = View.GONE
            }
        }
        tvTaskCompleted.setOnClickListener {
            if(rvReferralTasks.adapter == null) {
                referTaskAdapter = ReferTaskAdapter(successList)
                rvReferralTasks.adapter = referTaskAdapter
            } else {
                referTaskAdapter?.updateList(successList)
            }
            tvTaskCompleted.background = resources.getDrawable(R.color.grey_new)
            val outValue = TypedValue()
            context!!.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
            tvTaskPending.setBackgroundResource(outValue.resourceId)
            if(successList.isEmpty()) {
                tvNoData.visibility = View.VISIBLE
            } else {
                tvNoData.visibility = View.GONE
            }
        }
    }


    private fun getCountSpannable(count: Int, colorRes: Int): SpannableStringBuilder {
        val ssb = SpannableStringBuilder(count.toString())
        ssb.setSpan(ForegroundColorSpan(ContextCompat.getColor(context!!, colorRes)), 0, ssb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        ssb.setSpan(RelativeSizeSpan(1.4f), 0, ssb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        ssb.setSpan(StyleSpan(Typeface.BOLD), 0, ssb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return ssb
    }

    private fun callFetchDriverApi() {
        val params = hashMapOf(
                Constants.KEY_ACCESS_TOKEN to Data.userData.accessToken
        )
        ApiCommonKt<ReferralsInfoResponse>(activity as Activity).execute(params,ApiName.FETCH_DRIVER_REFERRAL_INFO,object : APICommonCallbackKotlin<ReferralsInfoResponse>() {
            override fun onSuccess(t: ReferralsInfoResponse?, message: String?, flag: Int) {
                t?.list?.let {
                    groupMain.visibility = View.VISIBLE
                    tvNoData.visibility = View.GONE
                    listRefer = it
                    countSuccededAndPending(listRefer) { succeeded, pending, list ->
                        tvTaskCompleted.text = getCountSpannable(succeeded,R.color.green_online)
                        tvTaskCompleted.append("\n")
                        tvTaskCompleted.append(getString(R.string.completed))
                        tvTaskPending.text = getCountSpannable(pending,R.color.themeColor)
                        tvTaskPending.append("\n")
                        tvTaskPending.append(getString(R.string.pending))
                        tvTaskPending.performClick()
                    }

                }
                if(t?.list.isNullOrEmpty()) {
                    groupMain.visibility = View.GONE
                    tvNoData.visibility = View.VISIBLE
                }
            }

            override fun onError(t: ReferralsInfoResponse?, message: String?, flag: Int): Boolean {
                return true
            }
        })
    }

    fun countSuccededAndPending(list: ArrayList<ReferInfo>,callback :(suceeded:Int,pending: Int,list: List<ReferInfo>) -> Unit) {

        var succeeded = 0
        var pending = 0

        for(item in list) {
            if (item.status == TaskType.PENDING.i) {
                item.taskMessage = getString(R.string.documents_pending)
                pending++
                pendingList.add(item)
            } else if (item.status == TaskType.FAILED.i) {
                item.taskMessage = getString(R.string.documents_rejected)
                pending++
                pendingList.add(item)
            } else if (item.status == TaskType.SUCCESS.i) {
                if (item.processedMoney == 0 && item.processedCredits == 0) {
                    handleSuccessItemMessage(item){
                        newItem ->  pendingList.add(newItem)
                    }
                    pending++
                } else {
                    handleSuccessItemMessage(item) {
                        newItem -> successList.add(item)
                    }
                    succeeded++
                }
            }
        }

        return callback(succeeded,pending,list)
    }

    fun handleSuccessItemMessage(item: ReferInfo,callback:(newItem :ReferInfo)-> Unit) {
        item.nextTarget?.let {
            if (item.userNumRides < item.nextTarget?.numOfRidesNextTarget!!) {
                if (item.nextTarget?.moneyNextTarget!! > 0 && item.nextTarget?.creditsNextTarget!! > 0) {
                    item.taskMessage = "Earn ${Utils.formatCurrencyValue(Prefs.with(context).getString(Constants.KEY_CURRENCY, "INR"),
                            item.nextTarget?.moneyNextTarget.toString())} and ${item.nextTarget?.creditsNextTarget} credits by completing ${item.nextTarget?.numOfRidesNextTarget!! - item.userNumRides} rides"
                } else if (item.nextTarget?.moneyNextTarget!! > 0) {
                    item.taskMessage = "Earn ${Utils.formatCurrencyValue(Prefs.with(context).getString(Constants.KEY_CURRENCY, "INR"),
                            item.nextTarget?.moneyNextTarget.toString())} by completing ${item.nextTarget?.numOfRidesNextTarget!! - item.userNumRides} rides"
                } else if (item.nextTarget?.creditsNextTarget!! > 0) {
                    item.taskMessage = "Earn ${item.nextTarget?.creditsNextTarget} credits by completing ${item.nextTarget?.numOfRidesNextTarget!! - item.userNumRides} rides"
                }
            }
        }
        if(item.nextTarget == null) {
            item.taskMessage = "Completed"
        }
        return callback(item)
    }
}

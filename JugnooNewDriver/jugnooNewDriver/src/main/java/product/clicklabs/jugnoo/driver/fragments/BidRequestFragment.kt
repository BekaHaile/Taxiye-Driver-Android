package product.clicklabs.jugnoo.driver.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_bid_request.*
import product.clicklabs.jugnoo.driver.*
import product.clicklabs.jugnoo.driver.adapters.BidIncrementAdapter
import product.clicklabs.jugnoo.driver.adapters.BidIncrementVal
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo
import product.clicklabs.jugnoo.driver.datastructure.UserData
import product.clicklabs.jugnoo.driver.utils.*
import java.lang.Double
import java.util.*
import kotlin.concurrent.timerTask


class BidRequestFragment : Fragment() {

    companion object{
        fun newInstance(engagementId:Int):BidRequestFragment{
            val fragment = BidRequestFragment()
            val bundle = Bundle()
            bundle.putInt(Constants.KEY_ENGAGEMENT_ID, engagementId)
            fragment.arguments = bundle
            return fragment
        }
    }

    lateinit var customerInfo :CustomerInfo
    var percent: Float = 10.0f;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(
                R.layout.fragment_bid_request, container, false) as ViewGroup
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        percent = Prefs.with(context).getFloat(Constants.BID_INCREMENT_PERCENT, 10f)
        val engagementId = arguments!!.getInt(Constants.KEY_ENGAGEMENT_ID)

        tvPickup.typeface = Fonts.mavenRegular(context!!)
        tvDrop.typeface = Fonts.mavenRegular(context!!)
        tvDistance.typeface = Fonts.mavenRegular(context!!)
        tvPrice.typeface = Fonts.mavenRegular(context!!)
        tvOffer.typeface = Fonts.mavenRegular(context!!)
        tvSkip.typeface = Fonts.mavenBold(context!!)
        btAccept.typeface = Fonts.mavenRegular(context!!)
        tvCommision.typeface = Fonts.mavenRegular(context!!)

        setValuesToUI(engagementId)
        btAccept.setOnClickListener {
            (activity as RequestActivity).acceptRideClick(customerInfo, customerInfo.initialBidValue.toString())
        }
        tvSkip.setOnClickListener(){
            (activity as RequestActivity).rejectRequestFuncCall(customerInfo)
        }
    }

    fun setValuesToUI(engagementId: Int) {
        val ci = Data.getCustomerInfo(engagementId.toString()) ?: return
        customerInfo = ci

        tvPickup.text = customerInfo.pickupAddress
        tvDrop.text = customerInfo.dropAddress
        tvDistance.text = Utils.getDecimalFormat().format(customerInfo.estimatedTripDistance
                * UserData.getDistanceUnitFactor(requireContext())) + " km"


        pbRequestTime.setProgress(customerInfo.progressValue)

        if (customerInfo.isReverseBid) {
            if(customerInfo.isBidPlaced) {
                rvBidValues.gone()
                tvOffer.gone()
                btAccept.gone()
                tvSkip.gone()
                tvPrice.text = getString(R.string.bid_placed) + ": " + Utils.formatCurrencyValue(customerInfo.currencyUnit, customerInfo.bidValue) + "\n" + getString(R.string.waiting_for_customer)
                tvCommision.gone()
            } else {
                rvBidValues.visible()
                tvOffer.visible()
                btAccept.visible()
                tvSkip.visible()
                tvCommision.visible()
                tvPrice.text = Utils.formatCurrencyValue(customerInfo.currencyUnit, customerInfo.initialBidValue)
                rvBidValues.itemAnimator = DefaultItemAnimator()
                rvBidValues.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                var bidIncrementAdapter = BidIncrementAdapter(activity as RequestActivity, rvBidValues, object : BidIncrementAdapter.Callback {
                    override fun onClick(incrementVal: BidIncrementVal, parentId: Int) {
                        (activity as RequestActivity).acceptRideClick(customerInfo, incrementVal.value.toString())
                    }
                })
                rvBidValues.adapter = bidIncrementAdapter
                bidIncrementAdapter.setList(0, customerInfo.getCurrencyUnit(), customerInfo.getInitialBidValue(), customerInfo.incrementPercent,
                        percent.toDouble(), customerInfo.initialBidValue.toInt(), rvBidValues);

                btAccept.setText(getString(R.string.accept_for) + " ");
                val ssb = SpannableStringBuilder(Utils.formatCurrencyValue(customerInfo.currencyUnit, customerInfo.initialBidValue))
                ssb.setSpan(RelativeSizeSpan(1.4f), 0, ssb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                ssb.setSpan(StyleSpan(Typeface.BOLD), 0, ssb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                btAccept.append(ssb)
                tvSkip.text = if(HomeActivity.appInterruptHandler != null) getString(R.string.cancel) else getString(R.string.skip)
            }
        } else {
            rvBidValues.gone()
            tvOffer.gone()
            btAccept.text = getString(R.string.accept)
        }
    }

}

package product.clicklabs.jugnoo.driver.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_bid_request.*
import product.clicklabs.jugnoo.driver.*
import product.clicklabs.jugnoo.driver.adapters.BidIncrementAdapter
import product.clicklabs.jugnoo.driver.adapters.BidIncrementVal
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo
import product.clicklabs.jugnoo.driver.datastructure.UserData
import product.clicklabs.jugnoo.driver.utils.Fonts
import product.clicklabs.jugnoo.driver.utils.Prefs
import product.clicklabs.jugnoo.driver.utils.Utils
import product.clicklabs.jugnoo.driver.utils.gone


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
        customerInfo = Data.getCustomerInfo(engagementId.toString())

        tvPickup.typeface = Fonts.mavenRegular(context!!)
        tvDrop.typeface = Fonts.mavenRegular(context!!)
        tvDistance.typeface = Fonts.mavenRegular(context!!)
        tvPrice.typeface = Fonts.mavenRegular(context!!)
        tvOffer.typeface = Fonts.mavenRegular(context!!)
        tvSkip.typeface = Fonts.mavenRegular(context!!)
        btAccept.typeface = Fonts.mavenRegular(context!!)

        tvPickup.text = customerInfo.pickupAddress
        tvDrop.text = customerInfo.dropAddress
        tvDistance.text = Utils.getDecimalFormat().format(customerInfo.estimatedTripDistance
                        * UserData.getDistanceUnitFactor(requireContext())) + " km"

        tvPrice.text = Utils.formatCurrencyValue(customerInfo.currencyUnit, customerInfo.bidValue)

        if(customerInfo.isReverseBid && !customerInfo.isBidPlaced) {
            rvBidValues.itemAnimator = DefaultItemAnimator()
            rvBidValues.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            var bidIncrementAdapter = BidIncrementAdapter(activity as RequestActivity, rvBidValues, object : BidIncrementAdapter.Callback {
                override fun onClick(incrementVal: BidIncrementVal, parentId: Int) {
                }
            })
            rvBidValues.adapter = bidIncrementAdapter
//            bidIncrementAdapter.setList(0, customerInfo.getCurrencyUnit(), customerInfo.getInitialBidValue(),
//                        percent.toDouble(), bidValues.get(position), rvBidValues);
        } else {
            rvBidValues.gone()
            tvOffer.gone()
        }
    }

}

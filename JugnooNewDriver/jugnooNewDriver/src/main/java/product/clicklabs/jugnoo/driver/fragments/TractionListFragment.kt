package product.clicklabs.jugnoo.driver.fragments

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_traction_list.*
import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.Data
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.adapters.OfflineRequestsAdapter
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo
import product.clicklabs.jugnoo.driver.retrofit.model.TractionResponse
import product.clicklabs.jugnoo.driver.ui.api.APICommonCallbackKotlin
import product.clicklabs.jugnoo.driver.ui.api.ApiCommonKt
import product.clicklabs.jugnoo.driver.ui.api.ApiName
import product.clicklabs.jugnoo.driver.utils.DialogPopup
import product.clicklabs.jugnoo.driver.utils.Log
import product.clicklabs.jugnoo.driver.utils.Prefs
import java.util.*


class TractionListFragment : Fragment() {

    companion object{
        @JvmStatic
        fun newInstance(accessToken: String):TractionListFragment{
            val fragment = TractionListFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var offlineRequests:MutableList<CustomerInfo>
    private lateinit var offlineRequestsAdapter:OfflineRequestsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(
                R.layout.fragment_traction_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        offlineRequests = mutableListOf()

        offlineRequestsAdapter = OfflineRequestsAdapter(offlineRequests as ArrayList<CustomerInfo>?, requireActivity(),
                R.layout.list_item_requests, 0, OfflineRequestsAdapter.Callback {
            Log.e(TractionListFragment::class.java.name,"no-i-will-not")

        })

        rvOfflineRequests.layoutManager = LinearLayoutManager(requireContext())
        rvOfflineRequests.adapter = offlineRequestsAdapter

    }

    private fun getTractionRides(refresh: Boolean) {
        val params = hashMapOf(
                Constants.KEY_ACCESS_TOKEN to Data.userData.accessToken
        )

        ApiCommonKt<TractionResponse>(requireActivity(), showLoader = true, putDefaultParams = true).execute(params, ApiName.FETCH_DRIVER_TRACTION_RIDES,
                object : APICommonCallbackKotlin<TractionResponse>() {
                    override fun onSuccess(response: TractionResponse, message: String, flag: Int) {
                        DialogPopup.dismissLoadingDialog()
                        offlineRequests.clear()
                        for (i in 0 until response.rides.size) {
                            val customerInfo = CustomerInfo(response.rides[i].userName, response.rides[i].requestAddress, response.rides[i].dropLocationAddress, response.rides[i].time, if (response.rides[i].fare == null) "0" else response.rides[i].fare, response.rides[i].distance, response.rides[i].userImage, response.rides[i].canAcceptRequest!!, response.rides[i].userId!!, if (response.rides[i].engagementId.isEmpty()) 0 else Integer.parseInt(response.rides[i].engagementId), response.rides[i].reverseBid!!)
                            offlineRequests.add(customerInfo)
                        }
                        offlineRequestsAdapter.notifyList(response.rides.size, offlineRequests as ArrayList<CustomerInfo>?, refresh)
                        if (response.rides.size > 0) {
                        } else {
                        }

                    }

                    override fun onError(response: TractionResponse, message: String, flag: Int): Boolean {
                        DialogPopup.dismissLoadingDialog()
                        return true
                    }
                })

    }


    private var handler: Handler? = Handler()

    private val runnableTraction = object : Runnable {
        override fun run() {

            if (showTractionList()) {

                getTractionRides(true)
                if (handler != null) {
                    var interval = Prefs.with(requireContext()).getInt(Constants.KEY_DRIVER_TRACTION_API_INTERVAL, 20000).toLong()
                    interval = 10000
                    handler!!.postDelayed(this, interval)
                }
            }
        }
    }

    private fun showTractionList() =
            Prefs.with(requireContext()).getInt(Constants.KEY_REQ_INACTIVE_DRIVER, 0) == 1

    override fun onStart() {
        super.onStart()
        if (showTractionList()) {
            handler = Handler()
            handler!!.removeCallbacks(runnableTraction)
            handler!!.post(runnableTraction)
        }
    }

    override fun onPause() {
        super.onPause()
        if (handler != null) {
            handler!!.removeCallbacks(runnableTraction)
            handler = null
        }
    }

}

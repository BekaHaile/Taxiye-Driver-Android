package product.clicklabs.jugnoo.driver

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import kotlinx.android.synthetic.main.activity_request.*
import product.clicklabs.jugnoo.driver.fragments.BidRequestFragment

class RequestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request)

        if(intent?.getIntExtra(Constants.KEY_ENGAGEMENT_ID,-1) != -1) {
            addRequests(intent?.getIntExtra(Constants.KEY_ENGAGEMENT_ID,0)!!)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        if(intent?.getIntExtra(Constants.KEY_ENGAGEMENT_ID,-1) != -1) {
            addRequests(intent?.getIntExtra(Constants.KEY_ENGAGEMENT_ID,0)!!)
        }
    }

    fun addRequests(engagementId: Int) {
        if(vpRequests.adapter == null) {
            vpRequests.adapter = RequestPagerAdapter(supportFragmentManager).apply {
                addFrag(engagementId)
            }
        } else {
            (vpRequests.adapter as RequestPagerAdapter).addFrag(engagementId = engagementId)
        }
    }

}

class RequestPagerAdapter(var fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    val requestList by lazy { ArrayList<Int>() }

    override fun getItem(position: Int): Fragment {
        return BidRequestFragment.newInstance(requestList[position])
    }

    override fun getCount(): Int {
        return if(requestList.isNullOrEmpty()) 0 else requestList.size
    }

    fun addFrag(engagementId: Int) {
        requestList.add(engagementId)
        notifyDataSetChanged()
    }

}

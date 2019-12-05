package product.clicklabs.jugnoo.driver

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.activity_support_options.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import kotlinx.android.synthetic.main.layout_top_bar.view.*
import product.clicklabs.jugnoo.driver.adapters.IncomeDetailsAdapter
import product.clicklabs.jugnoo.driver.fragments.DriverEarningsFragment
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames
import product.clicklabs.jugnoo.driver.utils.Prefs

class IncomeDetailsActivity : BaseFragmentActivity() {

    var listIncome = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support_options)
        topRl.title.text = getString(R.string.income_details)
        rvOptions.layoutManager = LinearLayoutManager(this)
        rvOptions.setHasFixedSize(false)
        rvOptions.itemAnimator = DefaultItemAnimator()
        if(!checkAvailibility().isNullOrEmpty()) {
            val adapter = IncomeDetailsAdapter(listIncome,object :IncomeDetailsAdapter.Callback {
                override fun onItemClick(position: Int) {
                    if(listIncome[position].equals(getString(R.string.Invoices))) {
                        startActivity(Intent(this@IncomeDetailsActivity, PaymentActivity::class.java))
                        overridePendingTransition(R.anim.right_in, R.anim.right_out)
                        FlurryEventLogger.event(FlurryEventNames.RIDES_OPENED)
                    } else if(listIncome[position].equals(getString(R.string.earnings))) {
                        if ((Data.getAssignedCustomerInfos() == null || Data.getAssignedCustomerInfos().size == 0)
                                && Prefs.with(this@IncomeDetailsActivity).getInt(Constants.KEY_EARNINGS_AS_HOME, 0) == 1) {
                            earningsVisibility(View.VISIBLE)
                        } else {
                            startActivity(Intent(this@IncomeDetailsActivity, EarningsActivity::class.java))
                            overridePendingTransition(R.anim.right_in, R.anim.right_out)
                        }
                    }
                }
            })
            rvOptions.adapter = adapter
        }
        backBtn.setOnClickListener { performBackPressed() }
    }

    fun checkAvailibility(): List<String> {
        if(Prefs.with(this).getInt(Constants.INVOICES_IN_MENU, 1) == 1) {
            listIncome.add(getString(R.string.Invoices))
        }
        if(Prefs.with(this).getInt(Constants.EARNINGS_IN_MENU, 1) == 1) {
            listIncome.add(getString(R.string.earnings))
        }
        return listIncome
    }

    private fun earningsVisibility(visibility: Int) {

        if (visibility == View.VISIBLE) {
            if (relativeLayoutContainerEarnings.visibility != View.VISIBLE) {
                relativeLayoutContainerEarnings.visibility = View.VISIBLE
                if (supportFragmentManager.findFragmentByTag(DriverEarningsFragment::class.java.name) == null) {
                    supportFragmentManager.beginTransaction()
                            .add(relativeLayoutContainerEarnings.getId(), DriverEarningsFragment(), DriverEarningsFragment::class.java.name)
                            .addToBackStack(DriverEarningsFragment::class.java.name)
                            .commitAllowingStateLoss()
                }
                rvOptions.visibility = View.GONE
                topRl.title.text = getString(R.string.earnings)
            }
        } else {
            relativeLayoutContainerEarnings.setVisibility(View.GONE)
            if (supportFragmentManager.findFragmentByTag(DriverEarningsFragment::class.java.name) != null) {
                supportFragmentManager.popBackStack()
            }
            rvOptions.setVisibility(View.VISIBLE)
            topRl.title.text = getString(R.string.income_details)
        }
    }

    override fun onBackPressed() {
        performBackPressed()
    }

    private fun performBackPressed() {
        if(supportFragmentManager.findFragmentByTag(DriverEarningsFragment::class.java.name) != null) {
            earningsVisibility(View.GONE)
        } else {
            super.onBackPressed()
        }
    }
}

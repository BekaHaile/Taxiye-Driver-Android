package product.clicklabs.jugnoo.driver.stripe.wallet

import android.os.Bundle
import android.support.v4.app.Fragment
import kotlinx.android.synthetic.main.activity_stripe_cards.*
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.stripe.wallet.StripeAddCardFragment
import product.clicklabs.jugnoo.driver.stripe.StripeCardsStateListener
import product.clicklabs.jugnoo.driver.stripe.wallet.StripeViewCardFragment
import product.clicklabs.jugnoo.driver.stripe.model.StripeCardData
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity
import product.clicklabs.jugnoo.driver.utils.inTransactionWithAnimation
import java.util.*

/**
 * Created by Parminder Saini on 05/07/18.
 */
class StripeCardsActivity : BaseFragmentActivity(), StripeCardsStateListener,StripeWalletFragment.StripeWalletInteractor {


    override fun openAddCard() {
        supportFragmentManager.inTransactionWithAnimation {

            add(container.id, StripeAddCardFragment(), StripeAddCardFragment::class.simpleName).
                    addToBackStack(StripeAddCardFragment::class.simpleName).
                    hide(supportFragmentManager.run { findFragmentByTag(getBackStackEntryAt(backStackEntryCount-1).name)})


        }
    }

    override fun openViewCard() {
        stripeData?.let {

            if(it.size>0){
                supportFragmentManager.inTransactionWithAnimation {
                    add(container.id, StripeViewCardFragment.newInstance( it[0]), StripeViewCardFragment::class.simpleName)
                    addToBackStack(StripeViewCardFragment::class.simpleName).
                            hide(supportFragmentManager.run { findFragmentByTag(getBackStackEntryAt(backStackEntryCount-1).name)})

                }
            }
        }

    }

    override fun onCardsUpdated(stripeCardData: ArrayList<StripeCardData>?) {

    }

    private var stripeData:List<StripeCardData>? = null /*listOf(StripeCardData("v_3","4444","Discover"))*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stripe_cards)


    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(supportFragmentManager.fragments==null && supportFragmentManager.fragments.size==0){
            super.onBackPressed()
        }
    }
}
package product.clicklabs.jugnoo.driver.stripe.wallet

import android.os.Bundle
import android.support.v4.app.Fragment
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_stripe_cards.*
import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.fragments.WalletTransactionFragment
import product.clicklabs.jugnoo.driver.stripe.wallet.StripeAddCardFragment
import product.clicklabs.jugnoo.driver.stripe.StripeCardsStateListener
import product.clicklabs.jugnoo.driver.stripe.wallet.StripeViewCardFragment
import product.clicklabs.jugnoo.driver.stripe.model.StripeCardData
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity
import product.clicklabs.jugnoo.driver.utils.Prefs
import product.clicklabs.jugnoo.driver.utils.inTransactionWithAnimation
import java.util.*

/**
 * Created by Parminder Saini on 05/07/18.
 */
class StripeCardsActivity : BaseFragmentActivity(), StripeCardsStateListener,StripeWalletFragment.StripeWalletInteractor {


    private  var isStripeCardsEnabled:Boolean= false

    override fun openWalletTransactions() {


        supportFragmentManager.inTransactionWithAnimation {

            add(container.id, WalletTransactionFragment(), WalletTransactionFragment::class.simpleName).
                    addToBackStack(WalletTransactionFragment::class.simpleName).
                    hide(supportFragmentManager.run { findFragmentByTag(frag_wallet.tag)})


        }
    }

    override fun isStripeEnabled(): Boolean {
       return isStripeCardsEnabled;
    }

    override fun openAddCard() {
        if(!isStripeCardsEnabled){
            Toast.makeText(this@StripeCardsActivity,getString(R.string.no_payment_methods_available),Toast.LENGTH_LONG).show()
            return;
        }

        supportFragmentManager.inTransactionWithAnimation {

            add(container.id, StripeAddCardFragment(), StripeAddCardFragment::class.simpleName).
                    addToBackStack(StripeAddCardFragment::class.simpleName).
                    hide(supportFragmentManager.run { findFragmentByTag(frag_wallet.tag)})


        }
    }


    override fun openViewCard(stripeCardData: StripeCardData) {

        if(!isStripeCardsEnabled){
            Toast.makeText(this@StripeCardsActivity,getString(R.string.no_payment_methods_available),Toast.LENGTH_LONG).show()
            return;
        }

        supportFragmentManager.inTransactionWithAnimation {
            add(container.id, StripeViewCardFragment.newInstance( stripeCardData), StripeViewCardFragment::class.simpleName)
            addToBackStack(StripeViewCardFragment::class.simpleName).
                    hide(supportFragmentManager.run { findFragmentByTag(frag_wallet.tag)})

        }

    }

    override fun onCardsUpdated(stripeCardData: ArrayList<StripeCardData>?) {
        (supportFragmentManager.findFragmentByTag(frag_wallet.tag) as StripeWalletFragment).setStripeData(
                stripeCardData?.run { if (this.size>0) this[0] else null}
        )




        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stripe_cards)
        isStripeCardsEnabled =  Prefs.with(this).getInt(Constants.KEY_STRIPE_CARDS_ENABLED,0)==1



    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(supportFragmentManager.fragments==null && supportFragmentManager.fragments.size==0){
            super.onBackPressed()
        }
    }
}
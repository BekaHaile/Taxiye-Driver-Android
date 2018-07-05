package product.clicklabs.jugnoo.driver.stripe.ui

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_stripe_cards.*
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.stripe.StripeAddCardFragment
import product.clicklabs.jugnoo.driver.stripe.StripeCardsStateListener
import product.clicklabs.jugnoo.driver.stripe.StripeViewCardFragment
import product.clicklabs.jugnoo.driver.stripe.model.StripeCardData
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity
import product.clicklabs.jugnoo.driver.utils.Log
import product.clicklabs.jugnoo.driver.utils.inTransactionWithAnimation
import java.util.*

/**
 * Created by Parminder Saini on 05/07/18.
 */
class StripeCardsActivity : BaseFragmentActivity(), StripeCardsStateListener {

    override fun onCardsUpdated(stripeCardData: ArrayList<StripeCardData>?) {

    }

    private var stripeData:ArrayList<StripeCardData>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stripe_cards)




        if (savedInstanceState == null) {

            val stripeData:ArrayList<StripeCardData>? = this.stripeData

            if(stripeData!=null && stripeData.size >0){

                supportFragmentManager.inTransactionWithAnimation {
                    add(container.id, StripeViewCardFragment.newInstance( stripeData[0]), StripeViewCardFragment::class.simpleName)
                }

            }else{
                supportFragmentManager.inTransactionWithAnimation {
                    add(container.id, StripeAddCardFragment(), StripeAddCardFragment::class.simpleName)
                }
            }


        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(supportFragmentManager.fragments==null && supportFragmentManager.fragments.size==0){
            super.onBackPressed()
        }
    }
}
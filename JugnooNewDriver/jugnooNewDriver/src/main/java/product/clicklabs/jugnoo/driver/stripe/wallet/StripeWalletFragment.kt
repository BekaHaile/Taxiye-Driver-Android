package product.clicklabs.jugnoo.driver.stripe.wallet

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.utils.inflate

/**
 * Created by Parminder Saini on 16/07/18.
 */
class StripeWalletFragment:Fragment(){

    interface StripeWalletInteractor{
        fun openAddCard();
        fun openViewCard();

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context is StripeWalletInteractor)
        {
            stripeWalletInteractor = context;
        }
    }

    private var stripeWalletInteractor:StripeWalletInteractor? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.frag_wallet);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
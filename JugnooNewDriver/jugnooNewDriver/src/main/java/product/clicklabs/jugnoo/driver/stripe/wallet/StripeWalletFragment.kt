package product.clicklabs.jugnoo.driver.stripe.wallet

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.frag_wallet.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.stripe.StripeUtils
import product.clicklabs.jugnoo.driver.stripe.model.StripeCardData
import product.clicklabs.jugnoo.driver.stripe.model.WalletModelResponse
import product.clicklabs.jugnoo.driver.ui.api.APICommonCallbackKotlin
import product.clicklabs.jugnoo.driver.ui.api.ApiCommonKt
import product.clicklabs.jugnoo.driver.ui.api.ApiName
import product.clicklabs.jugnoo.driver.utils.DialogPopup
import product.clicklabs.jugnoo.driver.utils.Utils
import product.clicklabs.jugnoo.driver.widgets.PrefixedEditText


/**
 * Created by Parminder Saini on 16/07/18.
 */
class StripeWalletFragment:Fragment(){

    interface StripeWalletInteractor{
        fun openAddCard();
        fun openViewCard(stripeCardData: StripeCardData);

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context is StripeWalletInteractor)
        {
            stripeWalletInteractor = context;
        }
    }

    private var stripeWalletInteractor:StripeWalletInteractor? = null
    private var stripeCardData:StripeCardData? = null;
    private var currencyUnit:String?=null;
    private var quickAddAmounts:List<Double>?=null;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_wallet,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        title.text = getString(R.string.wallet_details)
        backBtn.setOnClickListener{requireActivity().onBackPressed()}
        tvAddCard.setOnClickListener {

            stripeCardData?.let {

                stripeWalletInteractor?.openViewCard(it)

            }?:stripeWalletInteractor?.openAddCard();

        }
        btnAddCash.setOnClickListener {

           stripeCardData?.let {

               val input = edtAmount.text.toString().trim();

               if(input.length>0){
                   Snackbar.make(rootLayout,
                   getString(R.string.add_cash_stripe_confirmation,Utils.formatCurrencyValue(currencyUnit,input),it.last4),
                    Snackbar.LENGTH_LONG).setAction(getString(R.string.add)) {
                       addStripeCash(input);
                   }.show();

               }
           }

        }
        tvHintAmountOne.setFillListener(edtAmount)
        tvHintAmountTwo.setFillListener(edtAmount)
        tvHintAmountThree.setFillListener(edtAmount)

        fetchWalletData();


    }

    fun fetchWalletData(){
        ApiCommonKt<WalletModelResponse>(requireActivity(),putAccessToken = true).
        execute(params = null,apiName = ApiName.FETCH_WALLET,apiCommonCallback = object : APICommonCallbackKotlin<WalletModelResponse>() {
                    override fun onSuccess(t: WalletModelResponse, message: String?, flag: Int) {
                        tvCurrentBalance.text = Utils.formatCurrencyValue(t.currencyUnit,t.walletBalance);
                        currencyUnit = t.currencyUnit;
                        quickAddAmounts = t.quickAddAmounts;
                        edtAmount.setPrefix(Utils.getCurrencySymbol(currencyUnit))
                        setStripeData(t.stripeCards?.run { if(this.size>0) this[0] else null})



                    }

                    override fun onError(t: WalletModelResponse?, message: String?, flag: Int): Boolean {
                        return false;
                    }

                    override fun onDialogClick() {
                      requireActivity().onBackPressed();
                    }
        })
    }

     fun setStripeData(stripeCardData: StripeCardData?) {
        this.stripeCardData = stripeCardData
        stripeCardData?.let {
            tvAddCard.text = StripeUtils.getStripeCardDisplayString(activity, it.getLast4())
            tvInfoCard.visibility = View.GONE
            groupAddCash.visibility = View.VISIBLE
            groupQuickAmounts.visibility = quickAddAmounts?.run {if(this.size==3){
                for(i in 0 until this.size){
                    val textAmount = this[i].toString();
                    when(i){
                        0 -> tvHintAmountOne.run{
                            text = textAmount;
                            setPrefix(Utils.getCurrencySymbol(currencyUnit))
                        }
                        1 -> tvHintAmountTwo.run{
                            text = textAmount;
                            setPrefix(Utils.getCurrencySymbol(currencyUnit))
                        }
                        2 -> tvHintAmountThree.run{
                            text = textAmount;
                            setPrefix(Utils.getCurrencySymbol(currencyUnit))
                        }
                    }
                }
            View.VISIBLE
            }else View.GONE }?:View.GONE
        } ?: run {
            tvAddCard.text = getString(R.string.label_add_card)
            tvInfoCard.visibility = View.VISIBLE
            groupAddCash.visibility = View.GONE
            groupQuickAmounts.visibility = View.GONE
        }
    }

    private fun addStripeCash(amount:String){

        val params = hashMapOf("amount" to amount,
                                "currency" to (currencyUnit?:"INR"),
                                "card_id" to   (stripeCardData?.last4?:""))

        ApiCommonKt<WalletModelResponse>(requireActivity(),putAccessToken = true,checkForActionComplete = true).
        execute(params,apiName = ApiName.ADD_CASH_WALLET,apiCommonCallback = object : APICommonCallbackKotlin<WalletModelResponse>() {
                    override fun onSuccess(t: WalletModelResponse, message: String?, flag: Int) {
                        DialogPopup.alertPopup(requireActivity(),"",message);
                        tvCurrentBalance.text = Utils.formatCurrencyValue(t.currencyUnit,t.walletBalance);

                    }

                    override fun onError(t: WalletModelResponse?, message: String?, flag: Int): Boolean {
                        return false;
                    }


        })
    }

    fun TextView.setFillListener(targetView:EditText){
        setOnClickListener{

            targetView.setText(text)
            targetView.setSelection(text.length)

        }

    }


}
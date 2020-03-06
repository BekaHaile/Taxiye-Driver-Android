package product.clicklabs.jugnoo.driver.stripe.wallet

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.TouchDelegate
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.frag_wallet.*
import kotlinx.android.synthetic.main.layout_top_bar.*
import product.clicklabs.jugnoo.driver.Data
import product.clicklabs.jugnoo.driver.MyApplication
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.R.id.*
import product.clicklabs.jugnoo.driver.stripe.StripeUtils
import product.clicklabs.jugnoo.driver.stripe.model.StripeCardData
import product.clicklabs.jugnoo.driver.stripe.model.WalletModelResponse
import product.clicklabs.jugnoo.driver.ui.api.APICommonCallbackKotlin
import product.clicklabs.jugnoo.driver.ui.api.ApiCommonKt
import product.clicklabs.jugnoo.driver.ui.api.ApiName
import product.clicklabs.jugnoo.driver.utils.DialogPopup
import product.clicklabs.jugnoo.driver.utils.Utils
import product.clicklabs.jugnoo.driver.widgets.PrefixedEditText
import kotlin.math.roundToInt


/**
 * Created by Parminder Saini on 16/07/18.
 */
class StripeWalletFragment: Fragment(){

    interface StripeWalletInteractor{
        fun openAddCard();
        fun openViewCard(stripeCardData: StripeCardData);
        fun openWalletTransactions();
        fun isStripeEnabled():Boolean;

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is StripeWalletInteractor)
        {
            stripeWalletInteractor = context;
        }
    }

    private lateinit var stripeWalletInteractor:StripeWalletInteractor
    private var stripeCardData:StripeCardData? = null;
    private var currencyUnit:String?=null;
    private var quickAddAmounts:List<Double>?=null;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.frag_wallet,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        groupShowStripe(if(stripeWalletInteractor.isStripeEnabled()) View.VISIBLE else View.GONE)
        title.text = getString(R.string.wallet_details)
        backBtn.setOnClickListener{requireActivity().onBackPressed()}
        tvAddCard.setOnClickListener {

            stripeCardData?.let {

                stripeWalletInteractor.openViewCard(it)

            }?:stripeWalletInteractor.openAddCard();

        }
        btnAddCash.setOnClickListener {

            if(!stripeWalletInteractor.isStripeEnabled()){
                Toast.makeText(requireContext(),getString(R.string.no_payment_methods_available), Toast.LENGTH_LONG).show()
                return@setOnClickListener;
            }


           stripeCardData?.let {

               val input = edtAmount.text.toString().trim();

               if(input.length>0){
                   DialogPopup.alertPopupTwoButtonsWithListeners(requireActivity(),getString(R.string.add_cash_stripe_confirmation,Utils.formatCurrencyValue(currencyUnit,input),it.last4),
                    {addStripeCash(input);});

               }
           }

        }
        tvWalletTransactions.setOnClickListener {
            stripeWalletInteractor.openWalletTransactions()
        }
        tvQuickAmtOne.setFillListener(edtAmount)
        tvQuickAmtTwo.setFillListener(edtAmount)
        tvQuickAmtThree.setFillListener(edtAmount)
        fetchWalletData();


    }

    fun fetchWalletData(){
        ApiCommonKt<WalletModelResponse>(requireActivity(),putAccessToken = true).
        execute(params = null,apiName = ApiName.FETCH_WALLET,apiCommonCallback = object : APICommonCallbackKotlin<WalletModelResponse>() {
                    override fun onSuccess(t: WalletModelResponse, message: String?, flag: Int) {
                        tvCurrentBalance.text = Utils.formatCurrencyValue(t.currencyUnit,t.getBalance());
                        currencyUnit = t.currencyUnit;
                        quickAddAmounts = t.quickAddAmounts;
                        edtAmount.addTextChangedListener(UpdateCurrencyDrawableWatcher(edtAmount,currencyUnit));
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
            groupAddCash( View.VISIBLE)
            groupQuickAmounts(quickAddAmounts?.run {if(this.size==3){
                for(i in 0 until this.size){
                    val textAmount = this[i].roundToInt().toString();
                    when(i){
                        0 -> tvQuickAmtOne.run{
                            text = textAmount;
                            setPrefix(Utils.getCurrencySymbol(currencyUnit))
                        }
                        1 -> tvQuickAmtTwo.run{
                            text = textAmount;
                            setPrefix(Utils.getCurrencySymbol(currencyUnit))
                        }
                        2 -> tvQuickAmtThree.run{
                            text = textAmount;
                            setPrefix(Utils.getCurrencySymbol(currencyUnit))
                        }
                    }
                }
            View.VISIBLE
            }else View.GONE }?:View.GONE)

            applyTouchDelegateToLayoutAmount()
        } ?: run {
            tvAddCard.text = getString(R.string.label_add_card)
            tvInfoCard.visibility = if(stripeWalletInteractor.isStripeEnabled()) View.VISIBLE else View.GONE
            groupAddCash(View.GONE)
            groupQuickAmounts( View.GONE)
        }
    }

    private fun applyTouchDelegateToLayoutAmount() {
         layoutAmount.post {
            val delegateArea = Rect()
            edtAmount.getHitRect(delegateArea)
            delegateArea.right += 500;
            delegateArea.left -= 500;
            val touchDelegate = TouchDelegate(delegateArea, edtAmount)
            if (edtAmount.parent is View) {
                (edtAmount.parent as View).touchDelegate = touchDelegate;
            }

        }
    }

    private fun addStripeCash(amount:String){

        val params = hashMapOf("amount" to amount,
                                "currency" to (currencyUnit?: MyApplication.getInstance().getResources().getString(R.string.currency_fallback)),
                                "card_id" to   (stripeCardData?.cardId?:""))

        ApiCommonKt<WalletModelResponse>(requireActivity(),putAccessToken = true,checkForActionComplete = true).
        execute(params,apiName = ApiName.ADD_CASH_WALLET,apiCommonCallback = object : APICommonCallbackKotlin<WalletModelResponse>() {
                    override fun onSuccess(t: WalletModelResponse, message: String?, flag: Int) {
                        DialogPopup.alertPopup(requireActivity(),"",message);
                        tvCurrentBalance.text = Utils.formatCurrencyValue(currencyUnit,t.getBalance());
                        edtAmount.text = null
                        if(Data.userData!=null){
                            Data.userData.currency = currencyUnit;
                            Data.userData.creditsEarned = t.walletBalance;
                        }

                    }

                    override fun onError(t: WalletModelResponse?, message: String?, flag: Int): Boolean {
                        return false;
                    }


        })
    }

    fun TextView.setFillListener(targetView:EditText){
        (parent as ViewGroup).setOnClickListener{

            targetView.setText(text)
            targetView.setSelection(text.length)

        }

    }

    class UpdateCurrencyDrawableWatcher(val editText: PrefixedEditText, val currencyUnit:String?): TextWatcher{


        private var countBeforeChange = 0;
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            countBeforeChange=s.length;
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            if(s.length>0 && countBeforeChange==0){
                editText.setHint(null);
                if(editText.textDrawable==null){
                    editText.setPrefix(Utils.getCurrencySymbol(currencyUnit))

                }else{
                    editText.setCompoundDrawables(editText.textDrawable,null,null,null);
                }
            }else if(s.length==0){
                editText.setHint(R.string.enter_amount);
                editText.setCompoundDrawables(null,null,null,null);
            }
        }

    }



    fun groupAddCash(visibility: Int){

        if(visibility==View.VISIBLE){
            btnAddCash.visibility = View.VISIBLE
            layoutAmount.visibility = View.VISIBLE
            labelAddCash.visibility = View.VISIBLE
            currentBalanceDividerStart.visibility = View.VISIBLE
            currentBalanceDividerEnd.visibility = View.VISIBLE

        }else{
            btnAddCash.visibility = View.GONE
            layoutAmount.visibility = View.GONE
            labelAddCash.visibility = View.GONE
            currentBalanceDividerStart.visibility = View.GONE
            currentBalanceDividerEnd.visibility = View.GONE
        }
    }

    fun groupQuickAmounts(visibility: Int){
        if(visibility==View.VISIBLE){
            layoutQuickAmountThree.visibility = View.VISIBLE
            layoutQuickAmountTwo.visibility = View.VISIBLE
            layoutQuickAmountOne.visibility = View.VISIBLE
        }else{

            layoutQuickAmountThree.visibility = View.GONE
            layoutQuickAmountTwo.visibility = View.GONE
            layoutQuickAmountOne.visibility = View.GONE
        }
    }

    fun groupShowStripe(visibility: Int){

        labelCardDetails.visibility = visibility;
        tvAddCard.visibility = visibility;
        tvInfoCard.visibility = visibility;

    }

}
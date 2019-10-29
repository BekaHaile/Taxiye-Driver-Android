package product.clicklabs.jugnoo.driver.stripe.wallet;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.stripe.android.CardUtils;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardNumberEditText;
import com.stripe.android.view.ExpiryDateEditText;
import com.stripe.android.view.StripeEditText;

import java.util.HashMap;


import product.clicklabs.jugnoo.driver.BuildConfig;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.stripe.StripeCardsStateListener;
import product.clicklabs.jugnoo.driver.stripe.model.StripeCardResponse;
import product.clicklabs.jugnoo.driver.ui.api.APICommonCallbackKotlin;
import product.clicklabs.jugnoo.driver.ui.api.ApiCommonKt;
import product.clicklabs.jugnoo.driver.ui.api.ApiName;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Utils;

import static com.stripe.android.model.Card.BRAND_RESOURCE_MAP;

/**
 * Created by Parminder Saini on 10/05/18.
 */
public class StripeAddCardFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = StripeAddCardFragment.class.getName();


    private Stripe stripe;
    private CardNumberEditText edtCardNumber;
    private ExpiryDateEditText edtDate;
    private StripeEditText edtCvv;

    private StripeCardsStateListener stripeCardsStateListener;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof StripeCardsStateListener){
            stripeCardsStateListener = (StripeCardsStateListener) context;
        }


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_card, container, false);
        edtCardNumber = rootView.findViewById(R.id.edt_card_number);
        edtDate = rootView.findViewById(R.id.edt_date);
        edtCvv = rootView.findViewById(R.id.edt_cvv);
        rootView.findViewById(R.id.btn_add_card).setOnClickListener(this);
        rootView.findViewById(R.id.backBtn).setOnClickListener(this);
        ((TextView)rootView.findViewById(R.id.title)).setText(R.string.add_card_title);
        stripe = new Stripe(getActivity(), Data.SERVER_URL.equalsIgnoreCase(Data.LIVE_SERVER_URL)? BuildConfig.STRIPE_KEY_LIVE:BuildConfig.STRIPE_KEY_DEV);
        updateIcon(null);
        edtCardNumber.setErrorColor(ContextCompat.getColor(getActivity(), R.color.red_status));
        edtDate.setErrorColor(ContextCompat.getColor(getActivity(), R.color.red_status));
        edtCvv.setErrorColor(ContextCompat.getColor(getActivity(), R.color.red_status));
        edtCardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i < 4) {
                    String brand =  CardUtils.getPossibleCardType(charSequence.toString());
                    updateIcon(brand);
                    updateCvc(brand);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        return rootView;
    }




    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_add_card:
                String cardNumber = edtCardNumber.getCardNumber();
                int[] cardDate = edtDate.getValidDateFields();

                if(cardNumber==null){
                    Toast.makeText(getActivity(),getString(R.string.stripe_add_card_error,getString(R.string.card_number)),Toast.LENGTH_SHORT).show();
                    return;

                }


                if (cardDate == null || cardDate.length != 2) {
                    Toast.makeText(getActivity(),getString(R.string.stripe_add_card_error,getString(R.string.expiry_date)),Toast.LENGTH_SHORT).show();
                    return;
                }


                Card card = new Card(
                        edtCardNumber.getCardNumber(),
                        cardDate[0],
                        cardDate[1],
                        edtCvv.getText().toString()
                );

                if(!card.validateNumber()){
                    Toast.makeText(getActivity(),getString(R.string.stripe_add_card_error,getString(R.string.card_number)),Toast.LENGTH_SHORT).show();
                    return;

                }

                if(!card.validateExpiryDate()){
                    Toast.makeText(getActivity(),getString(R.string.stripe_add_card_error,getString(R.string.expiry_date)),Toast.LENGTH_SHORT).show();
                    return;

                }


                if (!card.validateCVC()) {
                    Toast.makeText(getActivity(),getString(R.string.stripe_add_card_error,getString(R.string.cvc)),Toast.LENGTH_SHORT).show();
                    return;
                }

                Utils.hideSoftKeyboard(getActivity(),edtCardNumber);
                DialogPopup.showLoadingDialog(getActivity(),getString(R.string.loading));
                stripe.createToken(
                        card,
                        new TokenCallback() {
                            public void onSuccess(Token token) {
                                // Send token to  server


                                DialogPopup.dismissLoadingDialog();
                                addCardApi( token);


                            }

                            public void onError(Exception error) {
                                // Show localized error message
                                Log.e(TAG, error.getMessage());
                                DialogPopup.dismissLoadingDialog();
                                Toast.makeText(getContext(),
                                        error.getLocalizedMessage(),
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
                break;
            case R.id.backBtn:
                getActivity().onBackPressed();
                break;
            default:
                break;



        }



    }

    private void addCardApi(Token token) {
        if(getActivity()==null)return;

        HashMap<String,String> params = new HashMap<>();
        params.put("stripe_token",token.getId());
        params.put("last_4",token.getCard().getLast4());
        params.put("brand",token.getCard().getBrand());
        params.put("exp_month",String.valueOf(token.getCard().getExpMonth()));
        params.put("exp_year",String.valueOf(token.getCard().getExpYear()));
        params.put("is_delete","0");




        new ApiCommonKt<StripeCardResponse>(getActivity(),true,true,true)
                .execute(params, ApiName.ADD_CARD_API, new APICommonCallbackKotlin<StripeCardResponse>() {
            @Override
            public void onSuccess(StripeCardResponse stripeCardResponse, String message, int flag) {

                if(stripeCardsStateListener!=null){
                    stripeCardsStateListener.onCardsUpdated(stripeCardResponse.getStripeCardData());
                }

                if(getView()==null || getActivity()==null){
                    return;
                }


                DialogPopup.alertPopupWithListener(getActivity(),"",message, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getActivity().onBackPressed();

                    }
                });




            }

            @Override
            public boolean onError(StripeCardResponse stripeCardResponse, String message, int flag) {
                return false;
            }
        });
    }


    private void updateCvc(@NonNull @Card.CardBrand String brand) {
        if (Card.AMERICAN_EXPRESS.equals(brand)) {
            edtCvv.setFilters(
                    new InputFilter[] {
                            new InputFilter.LengthFilter(Card.CVC_LENGTH_AMERICAN_EXPRESS)});
            edtCvv.setHint(getString(com.stripe.android.R.string.cvc_amex_hint));
        } else {
            edtCvv.setFilters(
                    new InputFilter[] {new InputFilter.LengthFilter(Card.CVC_LENGTH_COMMON)});
            edtCvv.setHint(getString(com.stripe.android.R.string.cvc_number_hint));
        }
    }

    private @Card.CardBrand String brand;
    private void updateIcon(@Card.CardBrand String brand) {


        if(this.brand!=null && this.brand.equals(brand)){
            return;

        }

        this.brand = brand;
        if (brand==null || Card.UNKNOWN.equals(brand)) {
            Drawable icon  = getResources().getDrawable(com.stripe.android.R.drawable.ic_unknown);
            edtCardNumber.setCompoundDrawablesWithIntrinsicBounds(icon,null,null,null);
        } else {
            edtCardNumber.setCompoundDrawablesWithIntrinsicBounds(BRAND_RESOURCE_MAP.get(brand),0,0,0);
        }
    }




}

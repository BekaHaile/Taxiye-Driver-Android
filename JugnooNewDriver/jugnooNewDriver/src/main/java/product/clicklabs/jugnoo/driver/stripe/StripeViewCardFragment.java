package product.clicklabs.jugnoo.driver.stripe;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;


import com.stripe.android.model.Card;

import java.util.HashMap;


import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.StripeUtils;
import product.clicklabs.jugnoo.driver.stripe.model.StripeCardData;
import product.clicklabs.jugnoo.driver.stripe.model.StripeCardResponse;
import product.clicklabs.jugnoo.driver.ui.api.APICommonCallbackKotlin;
import product.clicklabs.jugnoo.driver.ui.api.ApiCommonKt;
import product.clicklabs.jugnoo.driver.ui.api.ApiName;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;

import static com.stripe.android.model.Card.BRAND_RESOURCE_MAP;

/**
 * Created by Parminder Saini on 10/05/18.
 */
public class StripeViewCardFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = StripeViewCardFragment.class.getName();



    private TextView tvCard;
    private StripeCardData stripeCardData;
    private static final String ARGS_CARD_DATA = "edit_mode";
    private PopupMenu popupMenu;

    private StripeCardsStateListener stripeCardsStateListener;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof StripeCardsStateListener){
            stripeCardsStateListener = (StripeCardsStateListener) context;
        }


    }


    public static  <T extends StripeCardData>  StripeViewCardFragment newInstance(T stripeData) {
        StripeViewCardFragment stripeViewCardFragment = new StripeViewCardFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARGS_CARD_DATA, stripeData);
        stripeViewCardFragment.setArguments(bundle);
        return stripeViewCardFragment;


    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey(ARGS_CARD_DATA)) {
            stripeCardData = getArguments().getParcelable(ARGS_CARD_DATA);
        }


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view_card, container, false);
        ((TextView)rootView.findViewById(R.id.title)).setText(R.string.title_view_card);
        tvCard = rootView.findViewById(R.id.tv_card);
        ImageView ivMore = rootView.findViewById(R.id.ivMore);
        ivMore.setOnClickListener(this);
        rootView.findViewById(R.id.backBtn).setOnClickListener(this);

        if (stripeCardData != null) {

            tvCard.setText(StripeUtils.getStripeCardDisplayString(getActivity(),stripeCardData.getLast4()));
            tvCard.setVisibility(View.VISIBLE);
            ivMore.setVisibility(View.VISIBLE);
            updateIcon(stripeCardData.getBrand());
        }

        return rootView;
    }




    private @Card.CardBrand
    String brand;

    private void updateIcon(@Card.CardBrand String brand) {


        if (this.brand != null && this.brand.equals(brand)) {
            return;

        }

        this.brand = brand;
        if (brand == null || Card.UNKNOWN.equals(brand)) {
            Drawable icon = getResources().getDrawable(com.stripe.android.R.drawable.ic_unknown);
            tvCard.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
        } else {
            tvCard.setCompoundDrawablesWithIntrinsicBounds(BRAND_RESOURCE_MAP.get(brand), 0, 0, 0);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                getActivity().onBackPressed();
                break;
            case R.id.ivMore:
                if(popupMenu==null){


                    ContextThemeWrapper ctw = new ContextThemeWrapper(getActivity(), R.style.PopupMenu);
                    popupMenu = new PopupMenu(ctw, v);
                    //Inflating the Popup using xml file
                    popupMenu.getMenuInflater().inflate(R.menu.popup_menu_stripe_card, popupMenu.getMenu());

                    //registering popup with OnMenuItemClickListener
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            if(item.getItemId()==R.id.item_delete){
                                showDeletePopup(stripeCardData);
                                return true;

                            }
                            return false;
                        }
                    });
                }
                popupMenu.show();

                break;
        }
    }

    private void showDeletePopup(final StripeCardData stripeCardData) {
        DialogPopup.alertPopupTwoButtonsWithListeners(getActivity(), getString(R.string.stripe_delete_card,stripeCardData.getLast4()), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteCardApi(stripeCardData);
            }
        });
    }

    private void deleteCardApi(final StripeCardData stripeCardData) {
        if(getActivity()==null)return;

        HashMap<String,String> params = new HashMap<>();
        params.put("card_id",stripeCardData.getCardId());
        params.put("is_delete","1");


        new ApiCommonKt<StripeCardResponse>(getActivity(),true,true,true).execute(params, ApiName.ADD_CARD_API, new APICommonCallbackKotlin<StripeCardResponse>() {
            @Override
            public void onSuccess(StripeCardResponse stripeCardResponse, String message, int flag) {
                if(stripeCardsStateListener!=null){
                    stripeCardsStateListener.onCardsUpdated(stripeCardResponse.getStripeCardData());
                }

                if(getView()==null || getActivity()==null){
                    return;
                }


//                if(Data.autoData!=null && (Data.autoData.getPickupPaymentOption()== PaymentOption.STRIPE_CARDS.getOrdinal())){
//                    MyApplication.getInstance().getWalletCore().setDefaultPaymentOption(null);
//                }

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


}

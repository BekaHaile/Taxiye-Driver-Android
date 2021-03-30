package product.clicklabs.jugnoo.driver.subscription;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.DriverDocumentActivity;
import product.clicklabs.jugnoo.driver.JSONParser;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.SplashNewActivity;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.SubscriptionData;
import product.clicklabs.jugnoo.driver.stripe.StripeUtils;
import product.clicklabs.jugnoo.driver.stripe.model.StripeCardData;
import product.clicklabs.jugnoo.driver.stripe.model.WalletModelResponse;
import product.clicklabs.jugnoo.driver.stripe.wallet.StripeAddCardFragment;
import product.clicklabs.jugnoo.driver.ui.DriverSplashActivity;
import product.clicklabs.jugnoo.driver.ui.api.APICommonCallbackKotlin;
import product.clicklabs.jugnoo.driver.ui.api.ApiCommonKt;
import product.clicklabs.jugnoo.driver.ui.api.ApiName;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class SubscriptionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private AppCompatActivity activity;
    private List<SubscriptionData> subscriptionData;
    private String accessToken = "";
    private int stripe_enabled = 0;
    private int position = -1;
    private SubscriptionFragment fragment;
    private ArrayList<StripeCardData> cardsData = new ArrayList<>();

    public SubscriptionAdapter(AppCompatActivity activity, List<SubscriptionData> subscriptionData, String accessToken, int stripeEnabled, SubscriptionFragment frag) {
        this.activity = activity;
        this.subscriptionData = subscriptionData;
        this.accessToken = accessToken;
        this.stripe_enabled = stripeEnabled;
        this.fragment = frag;
    }

    SubscriptionViewHolder subscriptionViewHolder = null;

    @Override
    public SubscriptionViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_subscription, viewGroup, false);
        subscriptionViewHolder = new SubscriptionViewHolder(view);
        return subscriptionViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((SubscriptionViewHolder) holder).title.setText(subscriptionData.get(position).getTitle());
        ((SubscriptionViewHolder) holder).amount.setText(Utils.getCurrencySymbol(Data.userData.getCurrency()) + String.valueOf(subscriptionData.get(position).getAmount()));
        ((SubscriptionViewHolder) holder).validity.setText("Validity : " + subscriptionData.get(position).getNum_of_days() + " days");
        if (position == this.position && this.position != -1) {
            callSubscribePackage(subscriptionData.get(position).getSubscription_id());
           // ((SubscriptionFragment) fragment).finishSubscriptionProcess();
        }
    }

    @Override
    public int getItemCount() {
        return subscriptionData.size();
    }


    public class SubscriptionViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView amount, validity, tc;
        private RelativeLayout rl;


        public SubscriptionViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textViewPackageNameSubs);
            amount = itemView.findViewById(R.id.textViewAmountSubs);
            validity = itemView.findViewById(R.id.textViewValiditySubs);
            tc = itemView.findViewById(R.id.ivTcSubs);
            rl = itemView.findViewById(R.id.subscriptionItemLayout);


            tc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogPopup.alertPopup(activity, "", subscriptionData.get(getAdapterPosition()).getTerms_n_conditions());
                }
            });
            rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("position adapter without click", getAdapterPosition() + "");
//                    if (stripe_enabled == 0) {
//                        DialogPopup.alertPopup(activity, "", "Payment gateway is disabled, please contact your provider.");
//                    } else {
                        fetchWalletData(getAdapterPosition());
//                    }

                }
            });
        }
    }

    private void callSubscribePackage(int subscription_id) {
        DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.purchasing));
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.KEY_ACCESS_TOKEN, accessToken);
            params.put("subscription_id", subscription_id + "");
            params.put(Constants.KEY_LATITUDE, Data.latitude + "");
            params.put(Constants.KEY_LONGITUDE, Data.longitude + "");
            params.put(Constants.KEY_LOGIN_TYPE, Data.LOGIN_TYPE + "");
            params.put("payment_preference", "32");
            RestClient.getApiServices().purchaseSubscriptions(params, new Callback<RegisterScreenResponse>() {
                @Override
                public void success(RegisterScreenResponse registerScreenResponse, Response response) {
                    DialogPopup.dismissLoadingDialog();
                    try {
                        String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.e("driver subscription response subscribed", jsonString);

                        JSONObject jObj;
                        jObj = new JSONObject(jsonString);
                        int flag = jObj.getInt("flag");
                        String message = JSONParser.getServerMessage(jObj);
                        if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag, null)) {
                            if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
                                DialogPopup.dialogBanner(activity, message);
                            } else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                                ((SubscriptionFragment) fragment).finishSubscriptionProcess();
                            } else {
                                DialogPopup.alertPopup(activity, "", message);
                            }
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                        DialogPopup.dismissLoadingDialog();
                    }
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openAddard(int position) {
        StripeAddCardFragment subsFrag = new StripeAddCardFragment();
        Bundle args = new Bundle();
        args.putBoolean("keyFromSubscription", true);
        args.putInt("positionFromSub", position);
        subsFrag.setArguments(args);
        if(activity instanceof DriverDocumentActivity){
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                    .add(R.id.fragment, subsFrag, StripeAddCardFragment.class.getName())
                    .hide(activity.getSupportFragmentManager().findFragmentByTag(SubscriptionFragment.class.getName()))
                    .addToBackStack(StripeAddCardFragment.class.getName())
                    .commit();
        }else{
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                    .add(R.id.container, subsFrag, StripeAddCardFragment.class.getName())
                    .hide(activity.getSupportFragmentManager().findFragmentByTag(SubscriptionFragment.class.getName()))
                    .addToBackStack(StripeAddCardFragment.class.getName())
                    .commit();
        }

    }


    private void fetchWalletData(int pos) {
        DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.fetching));
        final HashMap<String, String> params = new HashMap<>();
        params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
        new ApiCommonKt<WalletModelResponse>(activity, true).execute((HashMap<String, String>) params, ApiName.FETCH_WALLET, new APICommonCallbackKotlin<WalletModelResponse>() {
            @Override
            public void onSuccess(WalletModelResponse presponse, String message, int flag) {
                DialogPopup.dismissLoadingDialog();
                Log.e("fetch wallet balance response", presponse.toString());
                if (flag == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                    if(subscriptionData.get(pos).getAmount()>presponse.getWalletBalance()){
                        fragment.tvBalance.setText("WALLET BALANCE: " +Utils.formatCurrencyValue(presponse.getCurrencyUnit(),presponse.getWalletBalance()));
                        fragment.tvBalance.setVisibility(View.VISIBLE);
                        Utils.showToast(activity,"Plan amount exceeds the wallet balance");
                    }
                    else {
                        fragment.tvBalance.setVisibility(View.GONE);
                        confirmationDialog("Buy Now", 1, "Are you sure you want to purchase this package?",pos);
                    }

                    Log.e("fetch wallet balance response", presponse.getStripeCards().size() + "");
//                    if (presponse.getStripeCards().size() > 0 && presponse.getStripeCards().size() == 1) {

//                        confirmationDialog("Buy Now", 1, "Are you sure you want to purchase this package? \n "+ StripeUtils.getStripeCardDisplayString(activity,presponse.getStripeCards().get(0).getLast4()),pos);
//                    } else if (presponse.getStripeCards().size() > 1) {
//                        cardsData.addAll(presponse.getStripeCards());
//                        confirmationDialog("Select Card", 0, "Please choose card to purchase this package",pos);
//                    } else {
//                        openAddard(pos);
//                    }

                }

            }

            @Override
            public boolean onError(WalletModelResponse t, String message, int flag) {
                return false;
            }

        });
    }


    void setDefaultDisable(int position) {
        this.position = position;
    }

    public void confirmationDialog(String okText, int action, String msg,int pos) {
        DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", msg, okText, "Cancel", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (action == 1) {
                    callSubscribePackage(subscriptionData.get(pos).getSubscription_id());
                } else if (action == 0) {
                    openChooseCardView(pos);
                }

            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogPopup.dismissAlertPopup();
            }
        }, false, false);
    }

    private void openChooseCardView(int pos) {
        Log.e("choose card", "clicked");
        SubscriptionChooseCardFragment subsFragChooseCard = new SubscriptionChooseCardFragment();
        Bundle args = new Bundle();
        args.putInt("subscription_id",subscriptionData.get(pos).getSubscription_id());
        args.putParcelableArrayList("card_data",cardsData);
        subsFragChooseCard.setArguments(args);
        if(activity instanceof DriverDocumentActivity) {
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                    .add(R.id.fragment, subsFragChooseCard, SubscriptionChooseCardFragment.class.getName())
                    .hide(activity.getSupportFragmentManager().findFragmentByTag(SubscriptionFragment.class.getName()))
                    .addToBackStack(SubscriptionChooseCardFragment.class.getName())
                    .commit();
        }else{
            activity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                    .add(R.id.container, subsFragChooseCard, SubscriptionChooseCardFragment.class.getName())
                    .hide(activity.getSupportFragmentManager().findFragmentByTag(SubscriptionFragment.class.getName()))
                    .addToBackStack(SubscriptionChooseCardFragment.class.getName())
                    .commit();
        }
    }

}
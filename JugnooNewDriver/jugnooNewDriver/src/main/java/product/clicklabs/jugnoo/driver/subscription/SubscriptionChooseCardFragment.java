package product.clicklabs.jugnoo.driver.subscription;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.JSONParser;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.SplashNewActivity;
import product.clicklabs.jugnoo.driver.ToolbarChangeListener;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.stripe.model.StripeCardData;
import product.clicklabs.jugnoo.driver.ui.DriverSplashActivity;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Log;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class SubscriptionChooseCardFragment extends Fragment {
    private View rootView;
    private RecyclerView cardView;
    private ToolbarChangeListener toolbarChangeListener;
    private ArrayList<StripeCardData> mStripcardData;
    private DriverSplashActivity activity;
    private int subscriptionId;
    private SubscriptionFragment fragment;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.subscription_view_choose_card, container, false);
        cardView = rootView.findViewById(R.id.recyclerViewChooseCardSubscription);
        activity = (DriverSplashActivity) getActivity();
        toolbarChangeListener.setToolbarText("Choose Card");
        toolbarChangeListener.setToolbarVisibility(true);
        fragment = (SubscriptionFragment) activity.getSupportFragmentManager().findFragmentByTag(SubscriptionFragment.class.getName());
        if(getArguments().containsKey("card_data"))
        {
            mStripcardData=getArguments().getParcelableArrayList("card_data");
        }
        subscriptionId= getArguments().getInt("subscription_id");
        attachAdapter();
        return rootView;
    }

    private SubscriptionChooseCardAdapter adapter;

    private void attachAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity.getApplicationContext());
        cardView.setLayoutManager(linearLayoutManager);
        adapter = new SubscriptionChooseCardAdapter(activity, mStripcardData, SubscriptionChooseCardFragment.this, new SubscriptionCallback() {
            @Override
            public void onCardSelectedCallback(String cardId) {
                callSubscribePackage(subscriptionId,cardId);
            }
        });
        cardView.setAdapter(adapter);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        toolbarChangeListener = (ToolbarChangeListener) context;
    }

    private void callSubscribePackage(int subscription_id,String cardId) {
        DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.purchasing));
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
            params.put("subscription_id", subscription_id + "");
            params.put(Constants.KEY_LATITUDE, Data.latitude + "");
            params.put(Constants.KEY_LONGITUDE, Data.longitude + "");
            params.put("card_id",cardId);
            params.put("payment_preference", "9");
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
                                 getActivity().onBackPressed();
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

    @Override
    public void onDetach() {
        super.onDetach();
        toolbarChangeListener.setToolbarText(getResources().getString(R.string.subscription_title));
    }

}

package product.clicklabs.jugnoo.driver.subscription;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.DriverDocumentActivity;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.JSONParser;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.SplashNewActivity;
import product.clicklabs.jugnoo.driver.ToolbarChangeListener;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.DriverSubscription;
import product.clicklabs.jugnoo.driver.datastructure.DriverSubscriptionEnabled;
import product.clicklabs.jugnoo.driver.dodo.fragments.DeliveryInfosListInRideFragment;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.DriverSubscriptionResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.SubscriptionData;
import product.clicklabs.jugnoo.driver.ui.DriverSplashActivity;
import product.clicklabs.jugnoo.driver.ui.OTPConfirmFragment;
import product.clicklabs.jugnoo.driver.ui.SplashFragment;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Log;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class SubscriptionFragment extends Fragment {
    private View rootView;
    private AppCompatActivity activity;
    private List<SubscriptionData> subscriptionData;
    private RecyclerView subscriptionView;
    private ToolbarChangeListener toolbarChangeListener;
    private int stripe_enabled = 0;
    private ImageView imageViewThumbsUpGif;
    public TextView tvSkip,tvBalance;
    private RelativeLayout relativeLayoutGreat;

    public static SubscriptionFragment newInstance() {
        Bundle args = new Bundle();
        SubscriptionFragment fragment = new SubscriptionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.subscription_view, container, false);
        if(getActivity() instanceof DriverSplashActivity) {
            activity = (DriverSplashActivity) getActivity();
        }
        else if(getActivity() instanceof DriverDocumentActivity){
            activity = (DriverDocumentActivity) getActivity();
        }
        else
        {
            activity = (HomeActivity) getActivity();
        }
        subscriptionView = rootView.findViewById(R.id.recyclerViewSubscriptionPackages);
        relativeLayoutGreat = rootView.findViewById(R.id.relativeLayoutGreat);
        imageViewThumbsUpGif = rootView.findViewById(R.id.imageViewThumbsUpGif);
        tvSkip = rootView.findViewById(R.id.tvSkip);
        tvBalance = rootView.findViewById(R.id.tvBalance);
        if(toolbarChangeListener != null) {
            toolbarChangeListener.setToolbarText(getResources().getString(R.string.subscription_title));
            toolbarChangeListener.setToolbarVisibility(true);
        }
        getSubscriptionData(getArguments().getString("AccessToken"));
        stripe_enabled = getArguments().getInt("stripe_key");
        if(Data.userData.getDriverSubscriptionEnabled() == DriverSubscriptionEnabled.ENABLED.getOrdinal()){
            tvSkip.setVisibility(View.VISIBLE);
        }
        else {
            tvSkip.setVisibility(View.GONE);
        }
        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(activity instanceof HomeActivity){
                    ((HomeActivity) getActivity()).relativeLayoutContainer.setVisibility(View.GONE);
                    ((HomeActivity) getActivity()).getSupportFragmentManager().beginTransaction()
                            .remove(((HomeActivity) getActivity()).getSupportFragmentManager().findFragmentByTag(SubscriptionFragment.class.getName()))
                            .commitAllowingStateLoss();
                }
                else if(activity instanceof DriverDocumentActivity){
                    ((DriverDocumentActivity) getActivity()).goToHomeScreen();

//                    ((DriverDocumentActivity) getActivity()).submitButton.setVisibility(View.VISIBLE);
//                    ((DriverDocumentActivity) getActivity()).getSupportFragmentManager().beginTransaction()
//                            .remove(((DriverDocumentActivity) getActivity()).getSupportFragmentManager().findFragmentByTag(SubscriptionFragment.class.getName()))
//                            .commitAllowingStateLoss();
                }
                else {
                    moveToHome();
                }
            }
        });
        if(activity instanceof DriverDocumentActivity){
            ((DriverDocumentActivity) getActivity()).submitButton.setVisibility(View.GONE);
        }
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //if(getActivity() instanceof DriverDocumentActivity)
        if(context instanceof ToolbarChangeListener) {
            toolbarChangeListener = (ToolbarChangeListener) context;
        }
    }

    SubscriptionAdapter subscriptionAdapter;

    private void getSubscriptionData(String accessToken) {
        DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.KEY_ACCESS_TOKEN, accessToken);
            RestClient.getApiServices().driverSubscriptionInfo(params,
                    new Callback<DriverSubscriptionResponse>() {
                        @Override
                        public void success(DriverSubscriptionResponse subscriptionResponse, Response response) {
                            try {
                                String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
                                Log.e("driver subscription response", jsonString);
                                JSONObject jObj;
                                jObj = new JSONObject(jsonString);
                                int flag = jObj.getInt("flag");
                                String message = JSONParser.getServerMessage(jObj);
                                if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag, null)) {
                                    if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
                                        DialogPopup.alertPopup(activity, "", message);
                                    } else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                                        DialogPopup.dismissLoadingDialog();
                                        try {
                                            if (subscriptionResponse.getData() != null) {
                                                subscriptionData = subscriptionResponse.getData();
                                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity.getApplicationContext());
                                                subscriptionView.setLayoutManager(linearLayoutManager);
                                                subscriptionAdapter = new SubscriptionAdapter(activity, subscriptionData, accessToken, stripe_enabled, SubscriptionFragment.this);
                                                subscriptionView.setAdapter(subscriptionAdapter);
                                            }else{
                                                DialogPopup.alertPopup(activity,"","There is no subscriptions plans for you. Please contact your provider");
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);

                                        }
                                    }
                                }
                            } catch (Exception exception) {
                                exception.printStackTrace();
                                DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                            }
                            DialogPopup.dismissLoadingDialog();

                        }


                        @Override
                        public void failure(RetrofitError error) {
                            DialogPopup.dismissLoadingDialog();
                            DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDisable(int position) {
        subscriptionAdapter.setDefaultDisable(position);
        subscriptionAdapter.notifyDataSetChanged();
    }

    public void finishSubscriptionProcess() {
        showSuccessGif();
    }

    private void showSuccessGif() {
        relativeLayoutGreat.setVisibility(View.VISIBLE);
        Data.userData.setDriverSubscription(DriverSubscription.SUBSCRIBED.getOrdinal());
        RequestOptions options = new RequestOptions().placeholder(R.drawable.ic_thumbs_up);
        Glide.with(activity)
                .load(R.drawable.android_thumbs_up)
                .apply(options)
                //.fitCenter()
                .into(imageViewThumbsUpGif);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                imageViewThumbsUpGif.setImageDrawable(null);
                if(activity instanceof DriverDocumentActivity){
                    Intent i = new Intent (activity,DriverSplashActivity.class);
                    startActivity(i);
                }else{
                    moveToHome();
                }

            }
        }, 3000);
    }

    private void moveToHome() {
        OTPConfirmFragment fragment = (OTPConfirmFragment) activity.getSupportFragmentManager().findFragmentByTag(OTPConfirmFragment.class.getSimpleName());
        if (fragment != null && fragment.isAdded() && !activity.isFinishing()) {
            fragment.openHomeFragment();
        } else {
            SplashFragment frag = (SplashFragment) activity.getSupportFragmentManager().findFragmentByTag(SplashFragment.class.getSimpleName());
            if (frag != null && frag.isAdded() && !activity.isFinishing()) {
                frag.openHomeFragment();
            }
        }
    }


}

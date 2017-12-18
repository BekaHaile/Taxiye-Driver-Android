package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.driver.datastructure.FetchDriverPlansResponse;
import product.clicklabs.jugnoo.driver.datastructure.InitiatePaymentResponse;
import product.clicklabs.jugnoo.driver.datastructure.PlanDetails;
import product.clicklabs.jugnoo.driver.fragments.JugnooPlanPaymentFragment;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class JugnooSubscriptionActivity extends BaseFragmentActivity implements JugnooPlanPaymentFragment.OnFragmentInteractionListener {


    public static final int DELAY_HITTING_API_AFTER_SUCCESS_PAYMENT = 10 * 1000;
    private TextView tvtopBarTitle, tvlabelCurrentSavings, tvinfoText, tvlabelOutstanding;
    private RecyclerView recyclerViewPlans;
    private ImageView ivBack;
    private Button buttonPay;
    private SubscriptionPlansAdapter subscriptionPlansAdapter;
    private TextView labelRecyclerView;
    private Handler handler = new Handler();
    private FrameLayout rlFragment;
    // TODO: 18/12/17  SHOw loader in fragment, sometimes shows error even when fragment closed,Dialog builder to made dismissable


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jugnoo_subscription);
        tvtopBarTitle = (TextView) findViewById(R.id.textViewTitle);
        rlFragment = (FrameLayout) findViewById(R.id.rlFragment);
        tvlabelCurrentSavings = (TextView) findViewById(R.id.label_savings);
        tvinfoText = (TextView) findViewById(R.id.info_text);
        labelRecyclerView = (TextView) findViewById(R.id.label_offers);
        tvlabelOutstanding = (TextView) findViewById(R.id.label_outstanding);
        recyclerViewPlans = (RecyclerView)findViewById(R.id.recyclerViewPlans);
        ((SimpleItemAnimator) recyclerViewPlans.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerViewPlans.setLayoutManager(new LinearLayoutManager(this));

        ivBack = (ImageView)findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        buttonPay = (Button) findViewById(R.id.buttonSubmitRequest);
        buttonPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlanDetails planDetails = subscriptionPlansAdapter.getCurrentSelectedPlan();
                if(planDetails!=null){
//                    Log.i("TAG", "onClick: "+ planDetails.getAmount());
//                    Toast.makeText(JugnooSubscriptionActivity.this, "Plan selected: " + planDetails.getValidityDays(), Toast.LENGTH_SHORT).show();
                        initiatePlanSubscription(planDetails.getPlanId(),JugnooSubscriptionActivity.this);

                }
            }
        });

        getSubscriptionData(this);

    }

    private  void getSubscriptionData(final Activity context) {
        try {
            DialogPopup.showLoadingDialog(context, context.getString(R.string.loading));
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
            params.put(Constants.KEY_PHONE_NO, Data.userData.phoneNo);
            params.put("latitude", String.valueOf(HomeActivity.myLocation.getLatitude()));
            params.put("longitude", String.valueOf(HomeActivity.myLocation.getLongitude()));

            RestClient.getApiServices().fetchDriverPlans(params, new Callback<FetchDriverPlansResponse>() {
                @Override
                public void success(FetchDriverPlansResponse dailyEarningResponse, Response response) {
                    try {

                        String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
                        JSONObject jObj;
                        DialogPopup.dismissLoadingDialog();
                        jObj = new JSONObject(jsonString);
                        if (!jObj.isNull("error")) {
                            String errorMessage = jObj.getString("error");
                            if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
                                HomeActivity.logoutUser(context);
                            } else {
                                DialogPopup.alertPopup(context, "", context.getString(R.string.error_occured_tap_to_retry));
                            }

                        } else {
                            setUpUI(dailyEarningResponse);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            DialogPopup.alertPopup(context, "", context.getString(R.string.error_occured_tap_to_retry));
                            DialogPopup.dismissLoadingDialog();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            DialogPopup.dismissLoadingDialog();
                        }
                    }

                }

                @Override
                public void failure(RetrofitError error) {
                    try {
                        DialogPopup.dismissLoadingDialog();
                        DialogPopup.alertPopup(context, "", context.getString(R.string.error_occured_tap_to_retry));
                    } catch (Exception e) {
                        DialogPopup.dismissLoadingDialog();
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initiatePlanSubscription(int planId, final Activity context){
        try {
            DialogPopup.showLoadingDialog(this, getString(R.string.loading));
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
            params.put(Constants.PLAN_ID, String.valueOf(planId));
            params.put("latitude", String.valueOf(HomeActivity.myLocation.getLatitude()));
            params.put("longitude", String.valueOf(HomeActivity.myLocation.getLongitude()));

            RestClient.getApiServices().initiatePlanSubscription(params, new Callback<InitiatePaymentResponse>() {
                @Override
                public void success(InitiatePaymentResponse initiatePaymentResponse, Response response) {
                    try {

                        String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
                        JSONObject jObj;
                        DialogPopup.dismissLoadingDialog();
                        jObj = new JSONObject(jsonString);
                        if (!jObj.isNull("error")) {
                            String errorMessage = jObj.getString("error");
                            if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
                                HomeActivity.logoutUser(context);
                            } else {
                                DialogPopup.alertPopup(context, "", context.getString(R.string.error_occured_tap_to_retry));
                            }

                        } else {
                            if(initiatePaymentResponse.getInitiatePaymentResponseData()!=null){
                                InitiatePaymentResponse.InitiatePaymentResponseData initiatePaymentResponseData = initiatePaymentResponse.getInitiatePaymentResponseData();
                                openPaymentFragment(initiatePaymentResponseData.getFileName(),initiatePaymentResponse.getSurl(),initiatePaymentResponse.getFurl());

                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            DialogPopup.alertPopup(context, "", context.getString(R.string.error_occured_tap_to_retry));
                            DialogPopup.dismissLoadingDialog();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            DialogPopup.dismissLoadingDialog();
                        }
                    }

                }

                @Override
                public void failure(RetrofitError error) {
                    try {
                        DialogPopup.dismissLoadingDialog();
                        DialogPopup.alertPopup(context, "", context.getString(R.string.error_occured_tap_to_retry));
                    } catch (Exception e) {
                        DialogPopup.dismissLoadingDialog();
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void openPaymentFragment(String html,String successUrl,String failureUrl){

        getSupportFragmentManager().beginTransaction().add(R.id.rlFragment,JugnooPlanPaymentFragment.newInstance(html,successUrl,failureUrl),JugnooPlanPaymentFragment.class.getName()).addToBackStack(JugnooPlanPaymentFragment.class.getName()).commit();

    }

    private  void setUpUI(FetchDriverPlansResponse dailyEarningResponse) {

        ArrayList<PlanDetails> planDetails = null;
        boolean isActivePlanArray = false ;
        if(dailyEarningResponse.getActivePlanDetails()==null || dailyEarningResponse.getActivePlanDetails().size()==0){

            /**
             * No plan
             */
            if(!TextUtils.isEmpty(dailyEarningResponse.getAmountSpentToday())){
                tvinfoText.setText(Utils.trimHTML(Html.fromHtml(dailyEarningResponse.getAmountSpentToday())));
                tvinfoText.setVisibility(View.VISIBLE);
            }else{
                tvinfoText.setVisibility(View.GONE);
            }


            labelRecyclerView.setText("Available Plans");
            planDetails = dailyEarningResponse.getAvailablePlanDetails();

            if(dailyEarningResponse.getOutstandingAmount()!=null){

                String amount =String.format("%s%s", getString(R.string.rupee), Utils.getDecimalFormatForMoney().format(dailyEarningResponse.getTotalSavings()));
                String label = "Current Outstanding: ";
                tvlabelOutstanding.setText(String.format("%s%s", label, amount));
                tvlabelOutstanding.setVisibility(View.VISIBLE);
            }else{
                tvlabelOutstanding.setVisibility(View.GONE);

            }

        }else{

            /**
             * Driver has an active plan
             */


            String expiryString = null;
            isActivePlanArray = true;
            planDetails = dailyEarningResponse.getActivePlanDetails();
            labelRecyclerView.setText("Active Plan");

            if(planDetails!=null && planDetails.size()>0){
                expiryString=planDetails.get(0).getExpiryString();
            }
            if(!TextUtils.isEmpty(expiryString)){
                tvinfoText.setText(Utils.trimHTML(Html.fromHtml(expiryString)));

                tvinfoText.setVisibility(View.VISIBLE);
            }else{
                tvinfoText.setVisibility(View.GONE);
            }





            if(dailyEarningResponse.getCurrentPlanSaving()!=null){
                String amount =String.format("%s%s", getString(R.string.rupee), Utils.getDecimalFormatForMoney().format(dailyEarningResponse.getCurrentPlanSaving()));
                String label = "Current Savings till now: \n";
                tvlabelCurrentSavings.setText(String.format("%s%s", label, amount));
                tvlabelCurrentSavings.setVisibility(View.VISIBLE);
            }else{
                tvlabelCurrentSavings.setVisibility(View.GONE);
            }


            if(dailyEarningResponse.getTotalSavings()!=null){
                String amount =String.format("%s%s", getString(R.string.rupee), Utils.getDecimalFormatForMoney().format(dailyEarningResponse.getTotalSavings()));
                String label = "Total Savings till now: \n";
                tvlabelOutstanding.setText(String.format("%s%s", label, amount));
                tvlabelOutstanding.setVisibility(View.VISIBLE);
            }else{
                tvlabelOutstanding.setVisibility(View.GONE);
            }


            buttonPay.setVisibility(View.GONE);


        }

        if(planDetails!=null && planDetails.size()>0){
            recyclerViewPlans.setVisibility(View.VISIBLE);
            subscriptionPlansAdapter = new SubscriptionPlansAdapter(this,planDetails,recyclerViewPlans,isActivePlanArray );
            recyclerViewPlans.setAdapter(subscriptionPlansAdapter);
            buttonPay.setVisibility(isActivePlanArray?View.GONE:View.VISIBLE);
            labelRecyclerView.setVisibility(View.VISIBLE);
        }else{
            recyclerViewPlans.setVisibility(View.GONE);
            buttonPay.setVisibility(View.GONE);
            labelRecyclerView.setVisibility(View.GONE);
        }


    }

    @Override
    public void onBackPressed() {


        if (getSupportFragmentManager().getFragments()!=null && getSupportFragmentManager().getBackStackEntryCount()>0) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setMessage("Do you really want to cancel the transaction ?");

            alertDialog.setCancelable(true);
            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    JugnooSubscriptionActivity.super.onBackPressed();
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.show();

        }else{
            JugnooSubscriptionActivity.super.onBackPressed();
        }
    }

    @Override
    public void onFragmentInteraction(boolean isSuccess) {
        if(isSuccess){
            handler.postDelayed(runnableGetPlansAPi, DELAY_HITTING_API_AFTER_SUCCESS_PAYMENT);


        }else{
//            DialogPopup.alertPopup(this,"Transaction failed","Transaction was unsuccessful!");
            JugnooSubscriptionActivity.super.onBackPressed();
        }
    }

    private Runnable runnableGetPlansAPi = new Runnable() {
        @Override
        public void run() {
            getSubscriptionData(JugnooSubscriptionActivity.this);
        }
    };
}

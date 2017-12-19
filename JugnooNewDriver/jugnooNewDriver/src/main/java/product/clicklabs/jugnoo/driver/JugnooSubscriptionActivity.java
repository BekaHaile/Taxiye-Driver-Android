package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
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


    public static final int DELAY_HITTING_API_AFTER_SUCCESS_PAYMENT = 6 * 1000;
    private TextView  tvlabelCurrentSavings, tvinfoText, tvlabelOutstanding;
    private RecyclerView recyclerViewPlans;
    private Button buttonPay;
    private SubscriptionPlansAdapter subscriptionPlansAdapter;
    private TextView labelRecyclerView;
    private Double currentOutstandingAmount;
    private Handler handler = new Handler();
    private CardView cardViewPriceBreakup;




    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jugnoo_subscription);
        cardViewPriceBreakup = (CardView) findViewById(R.id.cardViewBreakUp);
        tvlabelCurrentSavings = (TextView) findViewById(R.id.label_savings);
        tvinfoText = (TextView) findViewById(R.id.info_text);
        labelRecyclerView = (TextView) findViewById(R.id.label_offers);
        tvlabelOutstanding = (TextView) findViewById(R.id.label_outstanding);
        recyclerViewPlans = (RecyclerView)findViewById(R.id.recyclerViewPlans);
        recyclerViewPlans.setNestedScrollingEnabled(false);
        ((SimpleItemAnimator) recyclerViewPlans.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerViewPlans.setLayoutManager(new LinearLayoutManager(this));
        ((TextView)findViewById(R.id.layoutPlanBreakup).findViewById(R.id.tvLabel)).setText(R.string.plan_amount);
        ((TextView)findViewById(R.id.layoutOutstandingBreakUp).findViewById(R.id.tvLabel)).setText(R.string.label_outstanding_amount);
        ((TextView)findViewById(R.id.layoutTotalBreakUp).findViewById(R.id.tvLabel)).setText(R.string.label_total_amount);
        ImageView ivBack = (ImageView) findViewById(R.id.ivBack);
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
                final PlanDetails planDetails = subscriptionPlansAdapter.getCurrentSelectedPlan();
                if(planDetails!=null){

                    buttonPay.setEnabled(false);
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
            params.put(Constants.KEY_LATITUDE, String.valueOf(HomeActivity.myLocation.getLatitude()));
            params.put(Constants.KEY_LONGITUDE, String.valueOf(HomeActivity.myLocation.getLongitude()));

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
            DialogPopup.alertPopup(context, "", context.getString(R.string.error_occured_tap_to_retry));
            e.printStackTrace();
        }
    }

    private void initiatePlanSubscription(int planId, final Activity context){
        try {

            DialogPopup.showLoadingDialog(this, getString(R.string.loading));
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
            params.put(Constants.PLAN_ID, String.valueOf(planId));
            params.put(Constants.KEY_LATITUDE, String.valueOf(HomeActivity.myLocation.getLatitude()));
            params.put(Constants.KEY_LONGITUDE, String.valueOf(HomeActivity.myLocation.getLongitude()));

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
                    buttonPay.setEnabled(true);
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
                    buttonPay.setEnabled(true);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            buttonPay.setEnabled(true);

        }

    }

    private void openPaymentFragment(String html,String successUrl,String failureUrl){

        getSupportFragmentManager().beginTransaction().add(R.id.rlFragment,JugnooPlanPaymentFragment.newInstance(html,successUrl,failureUrl),JugnooPlanPaymentFragment.class.getName()).addToBackStack(JugnooPlanPaymentFragment.class.getName()).commit();

    }

    private  void setUpUI(FetchDriverPlansResponse dailyEarningResponse) {

        currentOutstandingAmount = dailyEarningResponse.getOutstandingAmount();
        ArrayList<PlanDetails> planDetails;
        boolean isActivePlanArray = false ;
        if(dailyEarningResponse.getActivePlanDetails()==null || dailyEarningResponse.getActivePlanDetails().size()==0){

            Double planAmount = null;
            /*
              No plan
             */
            if(!TextUtils.isEmpty(dailyEarningResponse.getAmountSpentToday())){
                tvinfoText.setText(Utils.trimHTML(Html.fromHtml(dailyEarningResponse.getAmountSpentToday())));
                tvinfoText.setTextColor(ContextCompat.getColor(this,R.color.black));
                tvinfoText.setVisibility(View.VISIBLE);
            }else{
                tvinfoText.setVisibility(View.GONE);
            }


            labelRecyclerView.setText(R.string.label_available_plans);
            planDetails = dailyEarningResponse.getAvailablePlanDetails();

            if(dailyEarningResponse.getOutstandingAmount()!=null){
                currentOutstandingAmount = dailyEarningResponse.getOutstandingAmount();
               /* String label = getString(R.string.label_current_outstanding) +" ";
                String amount =String.format("%s%s", getString(R.string.rupee), Utils.getDecimalFormatForMoney().format(currentOutstandingAmount));
                tvlabelOutstanding.setText(String.format("%s%s", label, amount));
                tvlabelOutstanding.setVisibility(View.VISIBLE);*/
                tvlabelOutstanding.setVisibility(View.GONE);
            }else{
                tvlabelOutstanding.setVisibility(View.GONE);

            }

            for(PlanDetails planDetail:dailyEarningResponse.getAvailablePlanDetails()){
                if(planDetail.getIsSelected()){
                    planAmount = planDetail.getAmount();
                }
            }

            setUpBreakUpData(planAmount);
            tvlabelCurrentSavings.setVisibility(View.GONE);

        }else{

            /*
              Driver has an active plan
             */


            String expiryString = null;
            isActivePlanArray = true;
            planDetails = dailyEarningResponse.getActivePlanDetails();
            labelRecyclerView.setText(R.string.label_active_plan);

            if(planDetails!=null && planDetails.size()>0){
                expiryString=planDetails.get(0).getExpiryString();
            }
            if(!TextUtils.isEmpty(expiryString)){
                tvinfoText.setText(Utils.trimHTML(Html.fromHtml(expiryString)));
                tvinfoText.setVisibility(View.VISIBLE);
                tvinfoText.setTextColor(ContextCompat.getColor(this,R.color.red_status));
            }else{
                tvinfoText.setVisibility(View.GONE);
            }





            if(dailyEarningResponse.getCurrentPlanSaving()!=null){
                String amount =String.format("%s%s", getString(R.string.rupee), Utils.getDecimalFormatForMoney().format(dailyEarningResponse.getCurrentPlanSaving()));
                String label = getString(R.string.label_current_savings) + " ";
                SpannableString spannableString = new SpannableString(label + amount);
                spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this,R.color.black)),0,label.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                tvlabelCurrentSavings.setText(spannableString);
                tvlabelCurrentSavings.setVisibility(View.VISIBLE);
            }else{
                tvlabelCurrentSavings.setVisibility(View.GONE);
            }


            if(dailyEarningResponse.getTotalSavings()!=null){
                String amount =String.format("%s%s", getString(R.string.rupee), Utils.getDecimalFormatForMoney().format(dailyEarningResponse.getTotalSavings()));
                String label = getString(R.string.label_total_savings) + " ";
                SpannableString spannableString = new SpannableString(label + amount);
                spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this,R.color.black)),0,label.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                tvlabelOutstanding.setText(spannableString);

                tvlabelOutstanding.setVisibility(View.VISIBLE);
            }else{
                tvlabelOutstanding.setVisibility(View.GONE);
            }


            buttonPay.setVisibility(View.GONE);
            cardViewPriceBreakup.setVisibility(View.GONE);

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
            alertDialog.setMessage(R.string.cancel_transaction_warning);

            alertDialog.setCancelable(true);
            alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Toast.makeText(JugnooSubscriptionActivity.this, R.string.transaction_failed, Toast.LENGTH_SHORT).show();
                    JugnooSubscriptionActivity.super.onBackPressed();
                }
            });
            alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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
            JugnooSubscriptionActivity.super.onBackPressed();
            Toast.makeText(this, R.string.transaction_successful, Toast.LENGTH_SHORT).show();
            DialogPopup.showLoadingDialog(this, getString(R.string.loading));
            handler.postDelayed(runnableGetPlansAPi, DELAY_HITTING_API_AFTER_SUCCESS_PAYMENT);


        }else{
            Toast.makeText(this, R.string.transaction_failed, Toast.LENGTH_SHORT).show();
            JugnooSubscriptionActivity.super.onBackPressed();
        }
    }

    private Runnable runnableGetPlansAPi = new Runnable() {
        @Override
        public void run() {
            getSubscriptionData(JugnooSubscriptionActivity.this);
        }
    };

    public void setUpBreakUpData(Double planAmount) {



        if(planAmount!=null){

            if(currentOutstandingAmount==null){
                findViewById(R.id.layoutOutstandingBreakUp).setVisibility(View.GONE);
                findViewById(R.id.dividerBelowOutstandingBreakup).setVisibility(View.GONE);
            }else{
                findViewById(R.id.layoutOutstandingBreakUp).setVisibility(View.VISIBLE);
                findViewById(R.id.dividerBelowOutstandingBreakup).setVisibility(View.VISIBLE);
                String value =String.format("%s%s", getString(R.string.rupee), Utils.getDecimalFormatForMoney().format(currentOutstandingAmount));
                ((TextView)findViewById(R.id.layoutOutstandingBreakUp).findViewById(R.id.tvValue)).setText(value);
            }


            String planValue =String.format("%s%s", getString(R.string.rupee), Utils.getDecimalFormatForMoney().format(planAmount));
            ((TextView)findViewById(R.id.layoutPlanBreakup).findViewById(R.id.tvValue)).setText(planValue);
            Double totalAmount = planAmount;
            if(currentOutstandingAmount!=null){
                totalAmount+=currentOutstandingAmount;
            }
            String totalValue =String.format("%s%s", getString(R.string.rupee), Utils.getDecimalFormatForMoney().format(totalAmount));
            ((TextView)findViewById(R.id.layoutTotalBreakUp).findViewById(R.id.tvValue)).setText(totalValue);


            cardViewPriceBreakup.setVisibility(View.VISIBLE);

        }else{
            cardViewPriceBreakup.setVisibility(View.GONE);

        }










    }


}

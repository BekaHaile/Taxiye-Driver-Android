package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.flurry.android.FlurryAgent;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.DriverScreenMode;
import product.clicklabs.jugnoo.driver.datastructure.RegisterOption;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.InvoiceDetailResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.RateCardResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.FirebaseEvents;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class DriverRateCard extends android.support.v4.app.Fragment {

	LinearLayout relative, linearLayoutDriverReferral, linearLayoutMain;
	RelativeLayout relativeLayoutDriverReferralHeading, relativeLayoutDriverReferralSingle, relativeLayoutNoData;
	TextView textViewPickupChargesValues, textViewBaseFareValue, textViewDistancePKmValue, textViewPickupChargesCond,
			textViewTimePKmValue, textViewDtoCValue, textViewDtoDValue, textViewDtoC, textViewDtoD, textViewDriverReferral,
			textViewDriverReferralValue, textViewDifferentialPricingEnable, textViewPickupChargesCondStar;
	ImageView imageViewHorizontal7;
	
	NewRateCardActivity activity;
	private View rootView;
	public DriverRateCard(){
		
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.activity_rate_card, container, false);
		activity = (NewRateCardActivity) getActivity();

		relative = (LinearLayout) rootView.findViewById(R.id.relative);
		new ASSL(activity, relative, 1134, 720, false);
		

		linearLayoutDriverReferral = (LinearLayout) rootView.findViewById(R.id.linearLayoutDriverReferral);
		relativeLayoutDriverReferralHeading = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutDriverReferralHeading);
		relativeLayoutNoData = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutNoData);
		relativeLayoutNoData.setVisibility(View.GONE);

		linearLayoutMain = (LinearLayout) rootView.findViewById(R.id.linearLayoutMain);
		linearLayoutMain.setVisibility(View.VISIBLE);

		textViewPickupChargesValues = (TextView) rootView.findViewById(R.id.textViewPickupChargesValues);
		textViewPickupChargesValues.setTypeface(Fonts.mavenRegular(activity));
		textViewBaseFareValue = (TextView) rootView.findViewById(R.id.textViewBaseFareValue);
		textViewBaseFareValue.setTypeface(Fonts.mavenRegular(activity));
		textViewDistancePKmValue = (TextView) rootView.findViewById(R.id.textViewDistancePKmValue);
		textViewDistancePKmValue.setTypeface(Fonts.mavenRegular(activity));
		textViewTimePKmValue = (TextView) rootView.findViewById(R.id.textViewTimePKmValue);
		textViewTimePKmValue.setTypeface(Fonts.mavenRegular(activity));
		textViewDtoCValue = (TextView) rootView.findViewById(R.id.textViewDtoCValue);
		textViewDtoCValue.setTypeface(Fonts.mavenRegular(activity));
		textViewDtoDValue = (TextView) rootView.findViewById(R.id.textViewDtoDValue);
		textViewDtoDValue.setTypeface(Fonts.mavenRegular(activity));
		textViewPickupChargesCondStar = (TextView) rootView.findViewById(R.id.textViewPickupChargesCondStar);
		textViewPickupChargesCondStar.setTypeface(Fonts.mavenRegular(activity));

		textViewDtoC = (TextView) rootView.findViewById(R.id.textViewDtoC);
		textViewDtoC.setTypeface(Fonts.mavenRegular(activity));
		textViewDtoD = (TextView) rootView.findViewById(R.id.textViewDtoD);
		textViewDtoD.setTypeface(Fonts.mavenRegular(activity));

		textViewPickupChargesCond = (TextView) rootView.findViewById(R.id.textViewPickupChargesCond);
		textViewPickupChargesCond.setTypeface(Fonts.mavenRegular(activity));

		textViewDriverReferral= (TextView) rootView.findViewById(R.id.textViewDriverReferral);
		textViewDriverReferral.setTypeface(Fonts.mavenRegular(activity));
		textViewDriverReferralValue= (TextView) rootView.findViewById(R.id.textViewDriverReferralValue);
		textViewDriverReferralValue.setTypeface(Fonts.mavenRegular(activity));
		textViewDifferentialPricingEnable = (TextView) rootView.findViewById(R.id.textViewDifferentialPricingEnable);
		textViewDifferentialPricingEnable.setTypeface(Fonts.mavenRegular(activity));

		((TextView) rootView.findViewById(R.id.textViewBeforeRide)).setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewPickupCharges)).setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewBaseFare)).setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewDistancePKm)).setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewTimePKm)).setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewPKm)).setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewPm)).setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewInRide)).setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewReferral)).setTypeface(Fonts.mavenRegular(activity));

		relativeLayoutDriverReferralSingle = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutDriverReferralSingle);
		imageViewHorizontal7 = (ImageView) rootView.findViewById(R.id.imageViewHorizontal7);

		getRateCardDetails(activity);
		return rootView;
	}

	public void performBackPressed() {
		activity.onBackPressed();
		activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}

	public void onBackPressed() {
		performBackPressed();
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		ASSL.closeActivity(relative);
		System.gc();
	}


//	Retrofit


	public void updateData(RateCardResponse rateCardResponse) {

		if (rateCardResponse != null) {

			textViewPickupChargesValues.setText(Utils.formatCurrencyValue(rateCardResponse.getRates().getCurrencyUnit() , rateCardResponse.getRates().getPickupCharges()));

			if(rateCardResponse.getRates().getPickupChargesThreshold() > 0){
				textViewPickupChargesCond.setText(getResources().getString(R.string.applicable_after,
						String.valueOf(rateCardResponse.getRates().getPickupChargesThreshold())));
				textViewPickupChargesCondStar.setText("*");
			} else {
				textViewPickupChargesCond.setVisibility(View.GONE);
				textViewPickupChargesCondStar.setVisibility(View.GONE);
			}

			textViewBaseFareValue.setText(Utils.formatCurrencyValue(rateCardResponse.getRates().getCurrencyUnit() ,  rateCardResponse.getRates().getBaseFare()));
			textViewDistancePKmValue.setText(Utils.formatCurrencyValue(rateCardResponse.getRates().getCurrencyUnit() ,  rateCardResponse.getRates().getFarePerKm()));
			textViewTimePKmValue.setText(Utils.formatCurrencyValue(rateCardResponse.getRates().getCurrencyUnit() ,  rateCardResponse.getRates().getFarePerMin()));

			if(rateCardResponse.getRates().getAfterThresholdDistance() > 0){
				textViewDifferentialPricingEnable.setVisibility(View.VISIBLE);
				textViewDifferentialPricingEnable.setText(getResources().getString(R.string.diffrential_pricing_rate,
						String.valueOf(rateCardResponse.getRates().getAfterThresholdDistance()),
						String.valueOf(rateCardResponse.getRates().getGetAfterThresholdValue())));
			}


			double dToCReferral = rateCardResponse.getRates().getDriverToCustomerReferral();
			double dToDReferral = rateCardResponse.getRates().getDriverToDriverReferral();

			if(dToCReferral == 0 && dToDReferral ==0){
				linearLayoutDriverReferral.setVisibility(View.GONE);
				relativeLayoutDriverReferralHeading.setVisibility(View.GONE);
			} else if(dToCReferral > 0 && dToDReferral ==0){
				linearLayoutDriverReferral.setVisibility(View.GONE);
				relativeLayoutDriverReferralSingle.setVisibility(View.VISIBLE);
				textViewDriverReferral.setText(getResources().getString(R.string.driver_to_customer));
				textViewDriverReferralValue.setText(Utils.formatCurrencyValue(rateCardResponse.getRates().getCurrencyUnit() ,  dToCReferral));
			} else if(dToCReferral == 0 && dToDReferral >0){
				linearLayoutDriverReferral.setVisibility(View.GONE);
				relativeLayoutDriverReferralSingle.setVisibility(View.VISIBLE);
				textViewDriverReferral.setText(getResources().getString(R.string.driver_to_driver));
				textViewDriverReferralValue.setText(Utils.formatCurrencyValue(rateCardResponse.getRates().getCurrencyUnit() ,  dToDReferral));
			} else if(dToCReferral > 0 && dToDReferral > 0){
				linearLayoutDriverReferral.setVisibility(View.VISIBLE);
				textViewDtoCValue.setText(Utils.formatCurrencyValue(rateCardResponse.getRates().getCurrencyUnit() ,  dToCReferral));
				textViewDtoDValue.setText(Utils.formatCurrencyValue(rateCardResponse.getRates().getCurrencyUnit() ,  dToDReferral));
			}

		} else {
			performBackPressed();
		}

	}

	private void getRateCardDetails(final NewRateCardActivity activity) {
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("access_token", Data.userData.accessToken);
			HomeUtil.putDefaultParams(params);
			RestClient.getApiServices().rateCardDetail(params, new Callback<RateCardResponse>() {
				@Override
				public void success(RateCardResponse rateCardResponse, Response response) {
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj = new JSONObject(jsonString);
						String message = JSONParser.getServerMessage(jObj);
						int flag = jObj.getInt("flag");

						if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
							updateData(rateCardResponse);
							linearLayoutMain.setVisibility(View.VISIBLE);
							relativeLayoutNoData.setVisibility(View.GONE);
						} else {
							relativeLayoutNoData.setVisibility(View.VISIBLE);
						}
					} catch (Exception exception) {
						exception.printStackTrace();
						relativeLayoutNoData.setVisibility(View.VISIBLE);
					}
				}
				@Override
				public void failure(RetrofitError error) {
					Log.i("error", String.valueOf(error));
					relativeLayoutNoData.setVisibility(View.VISIBLE);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			relativeLayoutNoData.setVisibility(View.VISIBLE);
		}
	}

}

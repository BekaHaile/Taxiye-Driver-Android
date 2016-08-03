package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
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

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.DriverScreenMode;
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
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class DriverRateCard extends BaseActivity {

	LinearLayout relative, linearLayoutDriverReferral;
	RelativeLayout relativeLayoutDriverReferralHeading, relativeLayoutDriverReferralSingle;
	Button backBtn;
	TextView title;
	TextView textViewPickupChargesValues, textViewBaseFareValue, textViewDistancePKmValue, textViewPickupChargesCond,
			textViewTimePKmValue, textViewDtoCValue, textViewDtoDValue, textViewDtoC, textViewDtoD, textViewDriverReferral,
			textViewDriverReferralValue;
	ImageView imageViewHorizontal7;

	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.init(this, Data.FLURRY_KEY);
		FlurryAgent.onStartSession(this, Data.FLURRY_KEY);
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rate_card);
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(DriverRateCard.this, relative, 1134, 720, false);

		linearLayoutDriverReferral = (LinearLayout) findViewById(R.id.linearLayoutDriverReferral);
		relativeLayoutDriverReferralHeading = (RelativeLayout) findViewById(R.id.relativeLayoutDriverReferralHeading);
		textViewPickupChargesValues = (TextView) findViewById(R.id.textViewPickupChargesValues);
		textViewPickupChargesValues.setTypeface(Fonts.mavenRegular(this));
		textViewBaseFareValue = (TextView) findViewById(R.id.textViewBaseFareValue);
		textViewBaseFareValue.setTypeface(Fonts.mavenRegular(this));
		textViewDistancePKmValue = (TextView) findViewById(R.id.textViewDistancePKmValue);
		textViewDistancePKmValue.setTypeface(Fonts.mavenRegular(this));
		textViewTimePKmValue = (TextView) findViewById(R.id.textViewTimePKmValue);
		textViewTimePKmValue.setTypeface(Fonts.mavenRegular(this));
		textViewDtoCValue = (TextView) findViewById(R.id.textViewDtoCValue);
		textViewDtoCValue.setTypeface(Fonts.mavenRegular(this));
		textViewDtoDValue = (TextView) findViewById(R.id.textViewDtoDValue);
		textViewDtoDValue.setTypeface(Fonts.mavenRegular(this));

		textViewDtoC = (TextView) findViewById(R.id.textViewDtoC);
		textViewDtoC.setTypeface(Fonts.mavenRegular(this));
		textViewDtoD = (TextView) findViewById(R.id.textViewDtoD);
		textViewDtoD.setTypeface(Fonts.mavenRegular(this));

		textViewPickupChargesCond = (TextView) findViewById(R.id.textViewPickupChargesCond);
		textViewPickupChargesCond.setTypeface(Fonts.mavenRegular(this));

		textViewDriverReferral= (TextView) findViewById(R.id.textViewDriverReferral);
		textViewDriverReferral.setTypeface(Fonts.mavenRegular(this));
		textViewDriverReferralValue= (TextView) findViewById(R.id.textViewDriverReferralValue);
		textViewDriverReferralValue.setTypeface(Fonts.mavenRegular(this));

		((TextView) findViewById(R.id.textViewBeforeRide)).setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.textViewPickupCharges)).setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.textViewBaseFare)).setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.textViewDistancePKm)).setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.textViewTimePKm)).setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.textViewPKm)).setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.textViewPm)).setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.textViewInRide)).setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.textViewReferral)).setTypeface(Fonts.mavenRegular(this));


		backBtn = (Button) findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Data.latoRegular(this), Typeface.BOLD);
		relativeLayoutDriverReferralSingle = (RelativeLayout) findViewById(R.id.relativeLayoutDriverReferralSingle);
		imageViewHorizontal7 = (ImageView) findViewById(R.id.imageViewHorizontal7);

		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});

		getRateCardDetails(this);
	}

	public void performBackPressed() {
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}

	@Override
	public void onBackPressed() {
		performBackPressed();
		super.onBackPressed();
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

			textViewPickupChargesValues.setText(getResources().getString(R.string.rupee)
					+ rateCardResponse.getRates().getPickupCharges());
			textViewPickupChargesCond.setText(getResources().getString(R.string.applicable_after,
					rateCardResponse.getRates().getPickupChargesThreshold()));

			textViewBaseFareValue.setText(getResources().getString(R.string.rupee)
					+ rateCardResponse.getRates().getBaseFare());
			textViewDistancePKmValue.setText(getResources().getString(R.string.rupee)
					+ rateCardResponse.getRates().getFarePerKm());
			textViewTimePKmValue.setText(getResources().getString(R.string.rupee)
					+ rateCardResponse.getRates().getFarePerMin());

			double dToCReferral = rateCardResponse.getRates().getDriverToCustomerReferral();
			double dToDReferral = rateCardResponse.getRates().getDriverToDriverReferral();

			if(dToCReferral == 0 && dToDReferral ==0){
				linearLayoutDriverReferral.setVisibility(View.GONE);
				relativeLayoutDriverReferralHeading.setVisibility(View.GONE);
			} else if(dToCReferral > 0 && dToDReferral ==0){
				linearLayoutDriverReferral.setVisibility(View.GONE);
				relativeLayoutDriverReferralSingle.setVisibility(View.VISIBLE);
				textViewDriverReferral.setText(getResources().getString(R.string.driver_to_customer));
				textViewDriverReferralValue.setText(getResources().getString(R.string.rupee)+ dToCReferral);
			} else if(dToCReferral == 0 && dToDReferral >0){
				linearLayoutDriverReferral.setVisibility(View.GONE);
				relativeLayoutDriverReferralSingle.setVisibility(View.VISIBLE);
				textViewDriverReferral.setText(getResources().getString(R.string.driver_to_driver));
				textViewDriverReferralValue.setText(getResources().getString(R.string.rupee)+ dToDReferral);
			} else if(dToCReferral > 0 && dToDReferral > 0){
				linearLayoutDriverReferral.setVisibility(View.VISIBLE);
				textViewDtoCValue.setText(getResources().getString(R.string.rupee) + dToCReferral);
				textViewDtoDValue.setText(getResources().getString(R.string.rupee)+ dToDReferral);
			}


		} else {
			performBackPressed();
		}

	}

	private void getRateCardDetails(final Activity activity) {
		try {
			RestClient.getApiServices().rateCardDetail(Data.userData.accessToken, new Callback<RateCardResponse>() {
				@Override
				public void success(RateCardResponse rateCardResponse, Response response) {
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj;
						jObj = new JSONObject(jsonString);
						if (!jObj.isNull("error")) {
							String errorMessage = jObj.getString("error");
							if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
								HomeActivity.logoutUser(activity);
							}
						} else {
							updateData(rateCardResponse);
						}
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
				@Override
				public void failure(RetrofitError error) {
					Log.i("error", String.valueOf(error));
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

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
import android.widget.LinearLayout;
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

	LinearLayout relative;
	Button backBtn;
	TextView title;
	TextView textViewPickupChargesValues, textViewBaseFareValue, textViewDistancePKmValue,
			textViewTimePKmValue, textViewDtoCValue, textViewDtoDValue;

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

		((TextView) findViewById(R.id.textViewBeforeRide)).setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.textViewPickupCharges)).setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.textViewBaseFare)).setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.textViewDistancePKm)).setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.textViewTimePKm)).setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.textViewInRide)).setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.textViewReferral)).setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.textViewDtoC)).setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.textViewDtoD)).setTypeface(Fonts.mavenRegular(this));

		backBtn = (Button) findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Data.latoRegular(this), Typeface.BOLD);

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


	public void updateData(InvoiceDetailResponse invoiceDetailResponse) {

		if (invoiceDetailResponse != null) {

			textViewPickupChargesValues.setText(getResources().getString(R.string.rupee)
					+ invoiceDetailResponse.getInvoiceDetails().getReferralAmount());
			textViewBaseFareValue.setText(getResources().getString(R.string.rupee)
					+ invoiceDetailResponse.getInvoiceDetails().getAmountToBePaid());
			textViewDistancePKmValue.setText(getResources().getString(R.string.rupee)
					+ invoiceDetailResponse.getInvoiceDetails().getPhoneDeductions());
			textViewTimePKmValue.setText(getResources().getString(R.string.rupee)
					+ invoiceDetailResponse.getInvoiceDetails().getCancelDistanceSubsidy());
			textViewDtoCValue.setText(getResources().getString(R.string.rupee)
					+ invoiceDetailResponse.getInvoiceDetails().getPaidByJugnoo());
			textViewDtoDValue.setText(getResources().getString(R.string.rupee)
					+ invoiceDetailResponse.getInvoiceDetails().getPaidUsingWallet());
		} else {
			performBackPressed();
		}

	}

	private void getRateCardDetails(final Activity activity) {
		try {
			RestClient.getApiServices().rateCardDetail(Data.userData.accessToken, new Callback<InvoiceDetailResponse>() {
				@Override
				public void success(InvoiceDetailResponse invoiceDetailResponse, Response response) {
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
							updateData(invoiceDetailResponse);
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

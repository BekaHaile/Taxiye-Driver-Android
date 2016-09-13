package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONObject;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RateCardResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Log;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class DriverEarningsNew extends BaseActivity {

	LinearLayout relative, linearLayoutDriverReferral;
	RelativeLayout relativeLayoutPayout;
	Button backBtn;
	TextView title;
	TextView textViewEstPayout, textViewThisWeek, textViewInvPeriod;
	ImageView imageViewHorizontal7, imageViewPrev, imageViewNext;

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
		setContentView(R.layout.activity_new_earnings);
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(DriverEarningsNew.this, relative, 1134, 720, false);

		BarChart barChart = (BarChart) findViewById(R.id.chart);

		linearLayoutDriverReferral = (LinearLayout) findViewById(R.id.linearLayoutDriverReferral);
		relativeLayoutPayout = (RelativeLayout) findViewById(R.id.relativeLayoutPayout);
		textViewEstPayout = (TextView) findViewById(R.id.textViewEstPayout);
		textViewEstPayout.setTypeface(Fonts.mavenRegular(this));
		textViewThisWeek = (TextView) findViewById(R.id.textViewThisWeek);
		textViewThisWeek.setTypeface(Fonts.mavenRegular(this));
		textViewInvPeriod = (TextView) findViewById(R.id.textViewInvPeriod);
		textViewInvPeriod.setTypeface(Fonts.mavenRegular(this));

//		((TextView) findViewById(R.id.textViewBeforeRide)).setTypeface(Fonts.mavenRegular(this));
//		((TextView) findViewById(R.id.textViewPickupCharges)).setTypeface(Fonts.mavenRegular(this));
//		((TextView) findViewById(R.id.textViewBaseFare)).setTypeface(Fonts.mavenRegular(this));
//		((TextView) findViewById(R.id.textViewDistancePKm)).setTypeface(Fonts.mavenRegular(this));
//		((TextView) findViewById(R.id.textViewTimePKm)).setTypeface(Fonts.mavenRegular(this));
//		((TextView) findViewById(R.id.textViewPKm)).setTypeface(Fonts.mavenRegular(this));
//		((TextView) findViewById(R.id.textViewPm)).setTypeface(Fonts.mavenRegular(this));
//		((TextView) findViewById(R.id.textViewInRide)).setTypeface(Fonts.mavenRegular(this));
//		((TextView) findViewById(R.id.textViewReferral)).setTypeface(Fonts.mavenRegular(this));


		backBtn = (Button) findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Data.latoRegular(this), Typeface.BOLD);
		imageViewHorizontal7 = (ImageView) findViewById(R.id.imageViewHorizontal7);

		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});

		// HorizontalBarChart barChart= (HorizontalBarChart) findViewById(R.id.chart);

		ArrayList<BarEntry> entries = new ArrayList<>();
		entries.add(new BarEntry(100f, 0));
		entries.add(new BarEntry(200f, 1));
		entries.add(new BarEntry(400f, 2));
		entries.add(new BarEntry(360f, 3));

		BarDataSet dataset = new BarDataSet(entries, "");

		ArrayList<String> labels = new ArrayList<String>();
		labels.add("MON");
		labels.add("TUE");
		labels.add("WED");
		labels.add("THU");


		BarData data = new BarData(labels, dataset);
		dataset.setColor(getResources().getColor(R.color.bg_grey));
//		dataset.setColors(ColorTemplate.COLORFUL_COLORS);

		barChart.setData(data);
		barChart.setNoDataTextDescription("");
		dataset.setBarSpacePercent(20);
		barChart.animateY(5000);

		XAxis xAxis = barChart.getXAxis();
		xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
		xAxis.setTextSize(15);
		YAxis yAxis = barChart.getAxisRight();
		yAxis.setDrawAxisLine(false);
		yAxis.setDrawLabels(false);

		barChart.getAxisLeft().setTextSize(12);


//		getRateCardDetails(this);
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

			double dToCReferral = rateCardResponse.getRates().getDriverToCustomerReferral();
			double dToDReferral = rateCardResponse.getRates().getDriverToDriverReferral();

			if(dToCReferral == 0 && dToDReferral ==0){
				linearLayoutDriverReferral.setVisibility(View.GONE);
			} else if(dToCReferral > 0 && dToDReferral ==0){
				linearLayoutDriverReferral.setVisibility(View.GONE);
			} else if(dToCReferral == 0 && dToDReferral >0){
				linearLayoutDriverReferral.setVisibility(View.GONE);
			} else if(dToCReferral > 0 && dToDReferral > 0){
				linearLayoutDriverReferral.setVisibility(View.VISIBLE);
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

package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONObject;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RateCardResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.CustomMarkerView;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Log;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class DriverEarningsNew extends BaseActivity  implements CustomMarkerView.Listener{

	LinearLayout  linearLayoutDriverReferral;
	RelativeLayout relativeLayoutPayout, relativeLayout1, relativeLayout2, relativeLayout3, relativeLayout4, relativeLayout5,
			relativeLayoutRideHistory, relativelayoutRandom, relativelayoutChart, relative;
	Button backBtn;
	TextView textViewEstPayout, textViewThisWeek, textViewInvPeriod, textViewDayDateVal1, textViewDayDateVal2, textViewDayDateVal3,
			textViewDayDateVal4, textViewDayDateVal5, textViewDailyValue1, textViewDailyValue2, textViewDailyValue3, textViewDailyValue4,
			textViewDailyValue5, title;
	ImageView imageViewHorizontal7, imageViewPrev, imageViewNext;
	BarChart barChart;

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
		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(DriverEarningsNew.this, relative, 1134, 720, false);

		barChart = (BarChart) findViewById(R.id.chart);

		linearLayoutDriverReferral = (LinearLayout) findViewById(R.id.linearLayoutDriverReferral);
		relativeLayoutPayout = (RelativeLayout) findViewById(R.id.relativeLayoutPayout);
		relativelayoutChart = (RelativeLayout) findViewById(R.id.relativelayoutChart);
		relativelayoutRandom = (RelativeLayout) findViewById(R.id.relativelayoutRandom);

		textViewEstPayout = (TextView) findViewById(R.id.textViewEstPayout);
		textViewEstPayout.setTypeface(Fonts.mavenRegular(this));
		textViewThisWeek = (TextView) findViewById(R.id.textViewThisWeek);
		textViewThisWeek.setTypeface(Fonts.mavenRegular(this));
		textViewInvPeriod = (TextView) findViewById(R.id.textViewInvPeriod);
		textViewInvPeriod.setTypeface(Fonts.mavenRegular(this));


		textViewDayDateVal1 = (TextView) findViewById(R.id.textViewDayDateVal1);
		textViewDayDateVal1.setTypeface(Fonts.mavenRegular(this));
		textViewDayDateVal2 = (TextView) findViewById(R.id.textViewDayDateVal2);
		textViewDayDateVal2.setTypeface(Fonts.mavenRegular(this));
		textViewDayDateVal3 = (TextView) findViewById(R.id.textViewDayDateVal3);
		textViewDayDateVal3.setTypeface(Fonts.mavenRegular(this));
		textViewDayDateVal4 = (TextView) findViewById(R.id.textViewDayDateVal4);
		textViewDayDateVal4.setTypeface(Fonts.mavenRegular(this));
		textViewDayDateVal5 = (TextView) findViewById(R.id.textViewDayDateVal5);
		textViewDayDateVal5.setTypeface(Fonts.mavenRegular(this));
		textViewDailyValue1 = (TextView) findViewById(R.id.textViewDailyValue1);
		textViewDailyValue1.setTypeface(Fonts.mavenRegular(this));
		textViewDailyValue2 = (TextView) findViewById(R.id.textViewDailyValue2);
		textViewDailyValue2.setTypeface(Fonts.mavenRegular(this));
		textViewDailyValue3 = (TextView) findViewById(R.id.textViewDailyValue3);
		textViewDailyValue3.setTypeface(Fonts.mavenRegular(this));
		textViewDailyValue4 = (TextView) findViewById(R.id.textViewDailyValue4);
		textViewDailyValue4.setTypeface(Fonts.mavenRegular(this));
		textViewDailyValue5 = (TextView) findViewById(R.id.textViewDailyValue5);
		textViewDailyValue5.setTypeface(Fonts.mavenRegular(this));



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

		Shader textShader=new LinearGradient(0, 0, 0, 20,
				new int[]{getResources().getColor(R.color.gradient_orange_v2), getResources().getColor(R.color.gradient_yellow_v2)},
				new float[]{0, 1}, Shader.TileMode.CLAMP);
		title.getPaint().setShader(textShader);

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
		dataset.setColor(getResources().getColor(R.color.white_grey_v2));
		dataset.setHighLightColor(R.drawable.orange_gradient);
//		dataset.setColors(ColorTemplate.COLORFUL_COLORS);

		barChart.setData(data);
		barChart.setNoDataTextDescription("");
		dataset.setBarSpacePercent(20);
		barChart.animateY(500);

		barChart.setBackgroundColor(getResources().getColor(R.color.transparent));

		XAxis xAxis = barChart.getXAxis();
		xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
		xAxis.setTextSize(15);
		xAxis.setTextColor(getResources().getColor(R.color.white_grey_v2));
		YAxis yAxis = barChart.getAxisRight();
		yAxis.setDrawAxisLine(false);
		yAxis.setDrawLabels(false);

		barChart.getAxisLeft().setTextSize(12);
		barChart.getAxisLeft().setTextColor(getResources().getColor(R.color.white_grey_v2));


		barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
			@Override
			public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
				Log.e("barchart", String.valueOf(e) + "ds:" + dataSetIndex + "h:" + h);
			}

			@Override
			public void onNothingSelected() {

			}
		});
		barChart.setDrawMarkerViews(true);
		CustomMarkerView mv = new CustomMarkerView(this, R.layout.graph_marker, this);
		barChart.setMarkerView(mv);
		barChart.setPinchZoom(false);
		barChart.setDescription("");
		barChart.setDoubleTapToZoomEnabled(false);
//		barChart.setDrawValueAboveBar(false);
		barChart.setDrawGridBackground(false);
		barChart.setExtraTopOffset(40);
		dataset.setDrawValues(false);


		relativelayoutRandom.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(DriverEarningsNew.this, "hello", Toast.LENGTH_LONG).show();
			}
		});


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

	@Override
	public void onMarkerViewLayout(int x, int y) {
//		if(getParent() == null || barChart == null) return;
		// remove the marker
		relativelayoutChart.removeView(relativelayoutRandom);
		relativelayoutChart.addView(relativelayoutRandom);

		// measure the layout
		// if this is not done, the first calculation of the layout parameter margins
		// will be incorrect as the layout at this point has no height or width
		int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(relativelayoutChart.getWidth(), View.MeasureSpec.UNSPECIFIED);
		int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(relativelayoutChart.getHeight(), View.MeasureSpec.UNSPECIFIED);
		relativelayoutRandom.measure(widthMeasureSpec, heightMeasureSpec);

		// set up layout parameters so our marker is in the same position as the mpchart marker would be (based no the x and y)
		RelativeLayout.LayoutParams lps = (RelativeLayout.LayoutParams) relativelayoutRandom.getLayoutParams();
		lps.height = 40;
		lps.width = 70;
		lps.leftMargin = x - (relativelayoutRandom.getMeasuredWidth() / 2) + 50;
		lps.topMargin = y - relativelayoutRandom.getMeasuredHeight()+ 135;
		relativelayoutRandom.setLayoutParams(lps);
	}

}

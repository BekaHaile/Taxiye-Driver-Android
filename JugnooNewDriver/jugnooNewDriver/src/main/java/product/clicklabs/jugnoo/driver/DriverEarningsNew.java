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
import product.clicklabs.jugnoo.driver.retrofit.model.DriverEarningsResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.RateCardResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.CustomMarkerView;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Utils;
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
			textViewDailyValue5, title, textViewPayOutValue;
	ImageView imageViewHorizontal7, imageViewPrev, imageViewNext;
	BarChart barChart;
	Shader textShader;
	DriverEarningsResponse res;

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
		relativeLayout1 = (RelativeLayout) findViewById(R.id.relativeLayout1);
		relativeLayout2 = (RelativeLayout) findViewById(R.id.relativeLayout2);
		relativeLayout3 = (RelativeLayout) findViewById(R.id.relativeLayout3);
		relativeLayout4 = (RelativeLayout) findViewById(R.id.relativeLayout4);
		relativeLayout5 = (RelativeLayout) findViewById(R.id.relativeLayout5);
		relativeLayoutRideHistory = (RelativeLayout) findViewById(R.id.relativeLayoutRideHistory);

		relativeLayout1.setVisibility(View.GONE);
		relativeLayout2.setVisibility(View.GONE);
		relativeLayout3.setVisibility(View.GONE);
		relativeLayout4.setVisibility(View.GONE);
		relativeLayout5.setVisibility(View.GONE);

		imageViewPrev = (ImageView) findViewById(R.id.imageViewPrev);
		imageViewNext = (ImageView) findViewById(R.id.imageViewNext);
		textViewEstPayout = (TextView) findViewById(R.id.textViewEstPayout);
		textViewEstPayout.setTypeface(Fonts.mavenRegular(this));
		textViewThisWeek = (TextView) findViewById(R.id.textViewThisWeek);
		textViewThisWeek.setTypeface(Fonts.mavenRegular(this));
		textViewInvPeriod = (TextView) findViewById(R.id.textViewInvPeriod);
		textViewInvPeriod.setTypeface(Fonts.mavenRegular(this));
		textViewPayOutValue = (TextView) findViewById(R.id.textViewPayOutValue);
		textViewPayOutValue.setTypeface(Fonts.mavenRegular(this));


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



		backBtn = (Button) findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Data.latoRegular(this), Typeface.BOLD);
		imageViewHorizontal7 = (ImageView) findViewById(R.id.imageViewHorizontal7);

		textShader=new LinearGradient(0, 0, 0, 20,
				new int[]{getResources().getColor(R.color.gradient_orange_v2), getResources().getColor(R.color.gradient_yellow_v2)},
				new float[]{0, 1}, Shader.TileMode.CLAMP);
		title.getPaint().setShader(textShader);

		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});

		imageViewPrev.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getEarningsDetails(DriverEarningsNew.this, 1);
			}
		});

		imageViewNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getEarningsDetails(DriverEarningsNew.this, 2);
			}
		});
		// HorizontalBarChart barChart= (HorizontalBarChart) findViewById(R.id.chart);

//		ArrayList<BarEntry> entries = new ArrayList<>();
//		entries.add(new BarEntry(100f, 0));
//		entries.add(new BarEntry(200f, 1));
//		entries.add(new BarEntry(400f, 2));
//		entries.add(new BarEntry(360f, 3));
//
//		BarDataSet dataset = new BarDataSet(entries, "");
//
//		ArrayList<String> labels = new ArrayList<String>();
//		labels.add("MON");
//		labels.add("TUE");
//		labels.add("WED");
//		labels.add("THU");
//
//
//		BarData data = new BarData(labels, dataset);
//		dataset.setColor(getResources().getColor(R.color.white_grey_v2));
//		dataset.setHighLightColor(R.drawable.orange_gradient);
//		dataset.setColors(ColorTemplate.COLORFUL_COLORS);
//
//		barChart.setData(data);
//		barChart.setNoDataTextDescription("");
//		dataset.setBarSpacePercent(20);
//		barChart.animateY(500);
//
//		barChart.setBackgroundColor(getResources().getColor(R.color.transparent));
//
//		XAxis xAxis = barChart.getXAxis();
//		xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//		xAxis.setTextSize(15);
//		xAxis.setTextColor(getResources().getColor(R.color.white_grey_v2));
//		YAxis yAxis = barChart.getAxisRight();
//		yAxis.setDrawAxisLine(false);
//		yAxis.setDrawLabels(false);
//
//		barChart.getAxisLeft().setTextSize(12);
//		barChart.getAxisLeft().setTextColor(getResources().getColor(R.color.white_grey_v2));


		barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
			@Override
			public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
				Log.e("barchart", String.valueOf(e) + "ds:" + dataSetIndex + "h:" + h);
			}

			@Override
			public void onNothingSelected() {

			}
		});
//		barChart.setDrawMarkerViews(true);
//		CustomMarkerView mv = new CustomMarkerView(this, R.layout.graph_marker, this);
//		barChart.setMarkerView(mv);
//		barChart.setPinchZoom(false);
//		barChart.setDescription("");
//		barChart.setDoubleTapToZoomEnabled(false);
//		barChart.setDrawValueAboveBar(false);
//		barChart.setDrawGridBackground(false);
//		barChart.setExtraTopOffset(40);
//		dataset.setDrawValues(false);


		relativelayoutRandom.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(DriverEarningsNew.this, "hello", Toast.LENGTH_LONG).show();
			}
		});


		getEarningsDetails(this, 0);
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


	public void updateData(DriverEarningsResponse driverEarningsResponse) {

		if (driverEarningsResponse != null) {

			if(driverEarningsResponse.getCurrentInvoiceId() == 0){
				relativeLayoutPayout.setVisibility(View.VISIBLE);
				textViewPayOutValue.setText(getResources().getString(R.string.rupee)+driverEarningsResponse.getEarnings().get(0).getEarnings());
			} else {
				relativeLayoutPayout.setVisibility(View.GONE);
			}

			if(driverEarningsResponse.getNextInvoiceId() != null){
				imageViewNext.setVisibility(View.VISIBLE);
			} else {
				imageViewNext.setVisibility(View.GONE);
			}

			if(driverEarningsResponse.getPreviousInvoiceId() != null){
				imageViewPrev.setVisibility(View.VISIBLE);
			} else {
				imageViewPrev.setVisibility(View.GONE);
			}

			if(driverEarningsResponse.getCurrentInvoiceId() == 0){
				textViewInvPeriod.setText(getResources().getString(R.string.this_week));
			} else {
				textViewInvPeriod.setText(driverEarningsResponse.getPeriod());
			}

			if(driverEarningsResponse.getEarnings().size() >=1){
				relativeLayout1.setVisibility(View.VISIBLE);
				textViewDayDateVal1.setText(driverEarningsResponse.getEarnings().get(0).getDay()
						+", "+DateOperations.convertMonthDayViaFormat(driverEarningsResponse.getEarnings().get(0).getDate()));
				textViewDailyValue1.setText(getResources().getString(R.string.rupee)+driverEarningsResponse.getEarnings().get(0).getEarnings());
			} else {
				relativeLayout1.setVisibility(View.GONE);
			}
			if(driverEarningsResponse.getEarnings().size() >=2){
				relativeLayout2.setVisibility(View.VISIBLE);
				textViewDayDateVal2.setText(driverEarningsResponse.getEarnings().get(1).getDay()
						+ ", " + DateOperations.convertMonthDayViaFormat(driverEarningsResponse.getEarnings().get(1).getDate()));
				textViewDailyValue2.setText(getResources().getString(R.string.rupee)+driverEarningsResponse.getEarnings().get(1).getEarnings());
			} else {
				relativeLayout2.setVisibility(View.GONE);
			}
			if(driverEarningsResponse.getEarnings().size() >=3){
				relativeLayout3.setVisibility(View.VISIBLE);
				textViewDayDateVal3.setText(driverEarningsResponse.getEarnings().get(2).getDay()+", "
						+ DateOperations.convertMonthDayViaFormat(driverEarningsResponse.getEarnings().get(2).getDate()));
				textViewDailyValue3.setText(getResources().getString(R.string.rupee)+driverEarningsResponse.getEarnings().get(2).getEarnings());
			} else {
				relativeLayout3.setVisibility(View.GONE);
			}
			if(driverEarningsResponse.getEarnings().size() >=4){
				relativeLayout4.setVisibility(View.VISIBLE);
				textViewDayDateVal4.setText(driverEarningsResponse.getEarnings().get(3).getDay()
						+", "+DateOperations.convertMonthDayViaFormat(driverEarningsResponse.getEarnings().get(3).getDate()));
				textViewDailyValue4.setText(getResources().getString(R.string.rupee)+driverEarningsResponse.getEarnings().get(3).getEarnings());
			} else {
				relativeLayout4.setVisibility(View.GONE);
			}
			if(driverEarningsResponse.getEarnings().size() >=5){
				relativeLayout5.setVisibility(View.VISIBLE);
				textViewDayDateVal5.setText(driverEarningsResponse.getEarnings().get(4).getDay()
						+", "+DateOperations.convertMonthDayViaFormat(driverEarningsResponse.getEarnings().get(4).getDate()));
				textViewDailyValue5.setText(getResources().getString(R.string.rupee)+driverEarningsResponse.getEarnings().get(4).getEarnings());
			} else {
				relativeLayout5.setVisibility(View.GONE);
			}



			ArrayList<BarEntry> entries = new ArrayList<>();
			ArrayList<String> labels = new ArrayList<String>();

			for(int i=0; i < driverEarningsResponse.getEarnings().size(); i++){
				entries.add(new BarEntry(driverEarningsResponse.getEarnings().get(i).getEarnings(), i));
				labels.add(driverEarningsResponse.getEarnings().get(i).getDay());
			}

			BarDataSet dataset = new BarDataSet(entries, "");
			BarData data = new BarData(labels, dataset);
			dataset.setColor(getResources().getColor(R.color.white_grey_v2));
			dataset.setHighLightColor(getResources().getColor(R.color.red_v2));
//			dataset.setColors(ColorTemplate.COLORFUL_COLORS);

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

			barChart.setDrawMarkerViews(true);
			CustomMarkerView mv = new CustomMarkerView(this, R.layout.graph_marker, this);
			barChart.setMarkerView(mv);
			barChart.setPinchZoom(false);
			barChart.setDescription("");
			barChart.setDoubleTapToZoomEnabled(false);
			barChart.setDrawGridBackground(false);
			barChart.setExtraTopOffset(40f);
			barChart.setExtraRightOffset(20f);
			dataset.setDrawValues(false);



		} else {
			performBackPressed();
		}

	}

	private void getEarningsDetails(final Activity activity, int invoice) {
		try {
			String invoiceId = "0";
			if(invoice == 0){
				invoiceId = "0";
			} else if(invoice == 1 && res != null){
				invoiceId = String.valueOf(res.getPreviousInvoiceId());
			}else if(invoice == 2 && res != null) {
				invoiceId = String.valueOf(res.getNextInvoiceId());
			}


			RestClient.getApiServices().earningNewDetails(Data.userData.accessToken, Data.LOGIN_TYPE, invoiceId, new Callback<DriverEarningsResponse>() {
				@Override
				public void success(DriverEarningsResponse driverEarningsResponse, Response response) {
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
							res = driverEarningsResponse;
							updateData(driverEarningsResponse);
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

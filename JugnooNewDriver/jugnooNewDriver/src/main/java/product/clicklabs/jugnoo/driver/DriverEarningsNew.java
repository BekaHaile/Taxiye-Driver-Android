package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Intent;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.DriverEarningsResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.RateCardResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.CustomMarkerView;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.FirebaseEvents;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class DriverEarningsNew extends BaseActivity  implements CustomMarkerView.Listener, FirebaseEvents{

	LinearLayout  linearLayoutDriverReferral;
	RelativeLayout relativeLayoutPayout, relativeLayout1, relativeLayout2, relativeLayout3, relativeLayout4, relativeLayout5,
			relativeLayoutRideHistory, relativelayoutRandom, relativelayoutChart, relative, relativeLayoutPrev,
			relativeLayoutNext, relativeLayoutChartData;
	Button backBtn;
	TextView textViewEstPayout, textViewInvPeriod, textViewDayDateVal1, textViewDayDateVal2, textViewDayDateVal3,
			textViewDayDateVal4, textViewDayDateVal5, textViewDailyValue1, textViewDailyValue2, textViewDailyValue3, textViewDailyValue4,
			textViewDailyValue5, title, textViewPayOutValue, textViewRideHistory, textViewNoChartData;
	ImageView imageViewHorizontal7, imageViewPrev, imageViewNext, arrow5, arrow4, arrow3, arrow2, arrow1;
	ASSL assl;
	BarChart barChart;
	int index = 0, maxIndex = 0;
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
		assl = new ASSL(DriverEarningsNew.this, relative, 1134, 720, false);

		barChart = (BarChart) findViewById(R.id.chart);

		linearLayoutDriverReferral = (LinearLayout) findViewById(R.id.linearLayoutDriverReferral);
		relativeLayoutPayout = (RelativeLayout) findViewById(R.id.relativeLayoutPayout);
		relativelayoutChart = (RelativeLayout) findViewById(R.id.relativelayoutChart);
		relativeLayoutChartData = (RelativeLayout) findViewById(R.id.relativeLayoutChartData);
		relativelayoutRandom = (RelativeLayout) findViewById(R.id.relativelayoutRandom);
		relativeLayout1 = (RelativeLayout) findViewById(R.id.relativeLayout1);
		relativeLayout2 = (RelativeLayout) findViewById(R.id.relativeLayout2);
		relativeLayout3 = (RelativeLayout) findViewById(R.id.relativeLayout3);
		relativeLayout4 = (RelativeLayout) findViewById(R.id.relativeLayout4);
		relativeLayout5 = (RelativeLayout) findViewById(R.id.relativeLayout5);
		relativeLayoutRideHistory = (RelativeLayout) findViewById(R.id.relativeLayoutRideHistory);
		relativeLayoutPrev = (RelativeLayout) findViewById(R.id.relativeLayoutPrev);
		relativeLayoutNext = (RelativeLayout) findViewById(R.id.relativeLayoutNext);

		relativeLayout1.setVisibility(View.GONE);
		relativeLayout2.setVisibility(View.GONE);
		relativeLayout3.setVisibility(View.GONE);
		relativeLayout4.setVisibility(View.GONE);
		relativeLayout5.setVisibility(View.GONE);

		arrow5 = (ImageView) findViewById(R.id.arrow5);
		arrow4 = (ImageView) findViewById(R.id.arrow4);
		arrow3 = (ImageView) findViewById(R.id.arrow3);
		arrow2 = (ImageView) findViewById(R.id.arrow2);
		arrow1 = (ImageView) findViewById(R.id.arrow1);


		imageViewPrev = (ImageView) findViewById(R.id.imageViewPrev);
		imageViewNext = (ImageView) findViewById(R.id.imageViewNext);
		textViewEstPayout = (TextView) findViewById(R.id.textViewEstPayout);
		textViewEstPayout.setTypeface(Fonts.mavenRegular(this));
		textViewInvPeriod = (TextView) findViewById(R.id.textViewInvPeriod);
		textViewInvPeriod.setTypeface(Fonts.mavenRegular(this));
		textViewPayOutValue = (TextView) findViewById(R.id.textViewPayOutValue);
		textViewPayOutValue.setTypeface(Fonts.mavenRegular(this));
		textViewRideHistory = (TextView) findViewById(R.id.textViewRideHistory);
		textViewRideHistory.setTypeface(Fonts.mavenBold(this));
		textViewNoChartData = (TextView) findViewById(R.id.textViewNoChartData);
		textViewNoChartData.setTypeface(Fonts.mavenRegular(this));
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
		title.setTypeface(Data.latoRegular(this), Typeface.NORMAL);
		imageViewHorizontal7 = (ImageView) findViewById(R.id.imageViewHorizontal7);

//		textShader=new LinearGradient(0, 0, 0, 20,
//				new int[]{getResources().getColor(R.color.gradient_orange_v2), getResources().getColor(R.color.gradient_yellow_v2)},
//				new float[]{0, 1}, Shader.TileMode.CLAMP);
//		title.getPaint().setShader(textShader);

		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FlurryEventLogger.event(FlurryEventNames.EARNINGS_BACK);
				MyApplication.getInstance().logEvent(EARNING+"_"+BACK, null);
				performBackPressed();
			}
		});

		relativeLayoutPrev.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FlurryEventLogger.event(FlurryEventNames.EARNINGS_CARD_THIS_WEEK);
				MyApplication.getInstance().logEvent(EARNING + "_" + CHART + "_" + PREVIOUS, null);
				getEarningsDetails(DriverEarningsNew.this, 1);
			}
		});

		relativeLayoutNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FlurryEventLogger.event(FlurryEventNames.EARNINGS_CARD_THIS_WEEK);
				MyApplication.getInstance().logEvent(EARNING + "_" + CHART + "_" + NEXT, null);
				getEarningsDetails(DriverEarningsNew.this, 2);
			}
		});


		barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
			@Override
			public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
				Log.e("barchart", String.valueOf(e) + "ds:" + dataSetIndex + "h:" + h);
				index = e.getXIndex();
			}

			@Override
			public void onNothingSelected() {

			}
		});

		relativeLayoutPayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FlurryEventLogger.event(FlurryEventNames.EARNINGS_CARD_TODAYS_EARNING);
				MyApplication.getInstance().logEvent(EARNING + "_" + TODAYS_EARNING, null);

				Calendar c = Calendar.getInstance();
				SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				String formattedDate = df.format(c.getTime());
				arrow1.setVisibility(View.VISIBLE);
				getDailyDetails(formattedDate);
			}
		});


		relativelayoutRandom.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					FlurryEventLogger.event(FlurryEventNames.EARNINGS_CARD__CHART);
					MyApplication.getInstance().logEvent(EARNING + "_" + CHART+"_"+index, null);

					if (index >= 0 && index < 5) {
						int newIndex = maxIndex-index-1;
						if(res.getEarnings().get(newIndex).getEarnings()>0) {
							getDailyDetails(res.getEarnings().get(newIndex).getDate());
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		relativeLayout1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(res.getEarnings().get(0).getEarnings() >0) {
					arrow1.setVisibility(View.VISIBLE);
					getDailyDetails(res.getEarnings().get(0).getDate());
				}

			}
		});

		relativeLayout2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(res.getEarnings().get(1).getEarnings() >0) {
					arrow2.setVisibility(View.VISIBLE);
					getDailyDetails(res.getEarnings().get(1).getDate());
				}

			}
		});

		relativeLayout3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(res.getEarnings().get(2).getEarnings() >0) {
					arrow3.setVisibility(View.VISIBLE);
					getDailyDetails(res.getEarnings().get(2).getDate());
				}

			}
		});


		relativeLayout4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(res.getEarnings().get(3).getEarnings() >0) {
					arrow4.setVisibility(View.VISIBLE);
					getDailyDetails(res.getEarnings().get(3).getDate());
				}

			}
		});

		relativeLayout5.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(res.getEarnings().get(4).getEarnings() >0) {
					arrow5.setVisibility(View.VISIBLE);
					getDailyDetails(res.getEarnings().get(4).getDate());
				}

			}
		});

		relativeLayoutRideHistory.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FlurryEventLogger.event(FlurryEventNames.EARNINGS_CARD_RIDE_HISTORY);
				MyApplication.getInstance().logEvent(EARNING + "_" + RIDE_HISTORY, null);
				Intent intent = new Intent(DriverEarningsNew.this, DriverRideHistoryNew.class);
				startActivity(intent);
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
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

	public void getDailyDetails(String date){
		Intent intent = new Intent(DriverEarningsNew.this, DailyRideDetailsActivity.class);
		intent.putExtra("date", date);
		startActivity(intent);
		overridePendingTransition(R.anim.right_in, R.anim.right_out);
	}


//	Retrofit


	public void updateData(DriverEarningsResponse driverEarningsResponse) {

		if (driverEarningsResponse != null) {

			if(driverEarningsResponse.getCurrentInvoiceId() == 0){
				relativeLayoutPayout.setVisibility(View.VISIBLE);
				textViewPayOutValue.setText(getResources().getString(R.string.rupee)+driverEarningsResponse.getEarnings().get(0).getEarnings());
			} else {
				relativeLayoutPayout.setVisibility(View.VISIBLE);
			}

			if(driverEarningsResponse.getNextInvoiceId() != null){
				imageViewNext.setVisibility(View.VISIBLE);
				relativeLayoutNext.setClickable(true);
			} else {
				imageViewNext.setVisibility(View.GONE);
				relativeLayoutNext.setClickable(false);
			}

			if(driverEarningsResponse.getPreviousInvoiceId() != null){
				imageViewPrev.setVisibility(View.VISIBLE);
				relativeLayoutPrev.setClickable(true);
			} else {
				imageViewPrev.setVisibility(View.GONE);
				relativeLayoutPrev.setClickable(false);
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
			int j = 0;
			maxIndex = driverEarningsResponse.getEarnings().size();
			boolean graphVisibility = false;
			for(int i=driverEarningsResponse.getEarnings().size() ; i > 0 ; i-- ){
				entries.add(new BarEntry(driverEarningsResponse.getEarnings().get(i-1).getEarnings(), j++));
				labels.add(driverEarningsResponse.getEarnings().get(i-1).getDay());
				if(driverEarningsResponse.getEarnings().get(i-1).getEarnings() > 0){
					graphVisibility =true;
				}
			}

			if(graphVisibility){
				relativeLayoutChartData.setVisibility(View.VISIBLE);
				textViewNoChartData.setVisibility(View.GONE);
			} else {
				relativeLayoutChartData.setVisibility(View.GONE);
				textViewNoChartData.setVisibility(View.VISIBLE);
			}

			BarDataSet dataset = new BarDataSet(entries, "");
			BarData data = new BarData(labels, dataset);
			dataset.setColor(getResources().getColor(R.color.white_grey_v2));
			dataset.setHighLightColor(getResources().getColor(R.color.red_v2));
			dataset.setHighLightAlpha(255);
//			dataset.setColors(ColorTemplate.COLORFUL_COLORS);

			barChart.setData(data);
			barChart.setNoDataTextDescription("");
			dataset.setBarSpacePercent(20);
			barChart.animateY(500);
			barChart.getLegend().setEnabled(false);
			barChart.setBackgroundColor(getResources().getColor(R.color.transparent));

			XAxis xAxis = barChart.getXAxis();
			xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
			xAxis.setTextSize(12);
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
			barChart.setExtraBottomOffset(10f);
			dataset.setDrawValues(false);

			if (entries.size() <3){ // barEntries is my Entry Array
				int factor = 7; // increase this to decrease the bar width. Decrease to increase he bar width
				int percent = (factor - entries.size())*10;
				dataset.setBarSpacePercent(percent);
			}



		} else {
			performBackPressed();
		}

	}

	private void getEarningsDetails(final Activity activity, int invoice) {
		try {
			if (AppStatus.getInstance(activity).isOnline(activity)) {
			String invoiceId = "0";
			if(invoice == 0){
				invoiceId = "0";
			} else if(invoice == 1 && res != null){
				invoiceId = String.valueOf(res.getPreviousInvoiceId());
			}else if(invoice == 2 && res != null) {
				invoiceId = String.valueOf(res.getNextInvoiceId());
			}

			DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));
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
							DialogPopup.dismissLoadingDialog();
							res = driverEarningsResponse;
							updateData(driverEarningsResponse);
						}
					} catch (Exception exception) {
						exception.printStackTrace();
						DialogPopup.dismissLoadingDialog();
					}
				}

				@Override
				public void failure(RetrofitError error) {
					Log.i("error", String.valueOf(error));
					DialogPopup.dismissLoadingDialog();
				}
			});
		} else {
			DialogPopup.alertPopup(DriverEarningsNew.this, "", Data.CHECK_INTERNET_MSG);
		}
		} catch (Exception e) {
			e.printStackTrace();
			DialogPopup.dismissLoadingDialog();
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
		lps.height = (int) (155f * ASSL.Yscale());
		lps.width = (int) (150f * ASSL.Xscale());
		lps.leftMargin = (x) - (int) (relativelayoutRandom.getMeasuredWidth() / 2) + (int) (75f * ASSL.Xscale());
		lps.topMargin = (y) - (int) (relativelayoutRandom.getMeasuredHeight()) + (int) (145f * ASSL.Yscale());
		relativelayoutRandom.setLayoutParams(lps);
	}

}

package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
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
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.driver.adapters.NewEarningsPerDayAdapter;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.DriverEarningsResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.CustomMarkerView;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.FirebaseEvents;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class DriverEarningsNew extends BaseActivity implements CustomMarkerView.Listener, FirebaseEvents {

	LinearLayout  linearLayoutDriverReferral;
	RelativeLayout relativeLayoutPayout ,
			relativeLayoutRideHistory, relativelayoutRandom, relativelayoutChart, relative, relativeLayoutPrev,
			relativeLayoutNext, relativeLayoutChartData, relativeLayoutWallet, relativeLayoutWalletCaptive,relativeLayoutNefy;
	Button backBtn;
	TextView textViewEstPayout, textViewInvPeriod,
			title, textViewPayOutValue, textViewRideHistory, textViewNoChartData,
			textViewWalletBalanceAmount,textViewWalletBalanceAmountCaptive, textViewWalletBalance,textViewWalletBalanceCaptive, textViewNefy, textViewNefyAmount;
	TextView tvDistanceCaptive, tvDaysLeftCaptive, tvAmountCollectedCaptive;
	ImageView imageViewHorizontal7, imageViewPrev, imageViewNext, arrow5, arrow4, arrow3, arrow2, arrow1;
	ASSL assl;
	BarChart barChart;
	int index = 0, maxIndex = 0;
	private LinearLayout llGraphWithEarnings,layoutCaptivePlanDetails;
	private NewEarningsPerDayAdapter newEarningsPerDayAdapter;
	private DriverEarningsResponse res;
	private Type listType = new TypeToken<List<DriverEarningsResponse.RechargeOption>>() {
	}.getType();
	private RecyclerView listEarningsPerDay;
	private TextView textViewNoData;
	private TextView textViewTripsLabel;
	private TextView tvDaysLeftCaptiveLabel;
	private TextView tvDistanceCaptiveLabel;
	private TextView tvAmountCollectedCaptiveLabel;
	private TextView dateTimeValue;
	private TextView tvTargetDistanceLabel;
	private TextView tvTargetDistance;
	private TextView tvAdjustedDistanceValue;
	private View.OnClickListener relativeLayoutWalletListener;
	private ImageView walletArrowCaptive;

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
		llGraphWithEarnings = (LinearLayout) findViewById(R.id.ll_graph_with_earnings);
		layoutCaptivePlanDetails = (LinearLayout) findViewById(R.id.layout_captive_plan_details);
		tvDistanceCaptive = (TextView) findViewById(R.id.tvDistanceCaptive);
		tvDistanceCaptiveLabel = (TextView) findViewById(R.id.tvDistanceCaptiveLabel);
		tvDistanceCaptive.setTypeface(Fonts.mavenRegular(this));
		tvDistanceCaptiveLabel.setTypeface(Fonts.mavenRegular(this));
		tvDaysLeftCaptive = (TextView) findViewById(R.id.tvDaysLeft);
		tvDaysLeftCaptive.setTypeface(Fonts.mavenRegular(this));
		tvDaysLeftCaptiveLabel = (TextView) findViewById(R.id.tvDaysLeftLabel);
		tvDaysLeftCaptiveLabel.setTypeface(Fonts.mavenRegular(this));
		tvAmountCollectedCaptive = (TextView) findViewById(R.id.tvAmountCollected);
		tvAmountCollectedCaptive.setTypeface(Fonts.mavenRegular(this));
		TextView tvAdjustedDistanceLabel = (TextView) findViewById(R.id.tvAdjustedDistanceLabel);
		tvAdjustedDistanceLabel.setTypeface(Fonts.mavenRegular(this));
		tvAdjustedDistanceValue = (TextView) findViewById(R.id.tvAdjustedDistanceValue);
		tvAdjustedDistanceValue.setTypeface(Fonts.mavenRegular(this));
		tvAmountCollectedCaptiveLabel = (TextView) findViewById(R.id.tvAmountCollectedLabel);
		tvAmountCollectedCaptiveLabel.setTypeface(Fonts.mavenRegular(this));
		tvTargetDistanceLabel = (TextView) findViewById(R.id.tvTargetDistanceLabel);
		tvTargetDistanceLabel.setTypeface(Fonts.mavenRegular(this));
		tvTargetDistance = (TextView) findViewById(R.id.tvTargetDistance);
		tvTargetDistance.setTypeface(Fonts.mavenRegular(this));
		assl = new ASSL(DriverEarningsNew.this, relative, 1134, 720, false);

		barChart = (BarChart) findViewById(R.id.chart);
		listEarningsPerDay = (RecyclerView) findViewById(R.id.list_earnings_per_day);
		listEarningsPerDay.setLayoutManager(new LinearLayoutManager(this));
		listEarningsPerDay.setNestedScrollingEnabled(false);
		linearLayoutDriverReferral = (LinearLayout) findViewById(R.id.linearLayoutDriverReferral);
		relativeLayoutPayout = (RelativeLayout) findViewById(R.id.relativeLayoutPayout);
		relativelayoutChart = (RelativeLayout) findViewById(R.id.relativelayoutChart);
		relativeLayoutChartData = (RelativeLayout) findViewById(R.id.relativeLayoutChartData);
		relativelayoutRandom = (RelativeLayout) findViewById(R.id.relativelayoutRandom);
		relativeLayoutRideHistory = (RelativeLayout) findViewById(R.id.relativeLayoutRideHistory);
		relativeLayoutWallet = (RelativeLayout) findViewById(R.id.relativeLayoutWallet);
		relativeLayoutWalletCaptive = (RelativeLayout) findViewById(R.id.relativeLayoutWalletCaptive);
		relativeLayoutNefy = (RelativeLayout) findViewById(R.id.relativeLayoutNefy);
		relativeLayoutPrev = (RelativeLayout) findViewById(R.id.relativeLayoutPrev);
		relativeLayoutNext = (RelativeLayout) findViewById(R.id.relativeLayoutNext);
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
		dateTimeValue = (TextView) findViewById(R.id.dateTimeValue);
		dateTimeValue.setTypeface(Fonts.mavenRegular(this));
		textViewNefy = (TextView) findViewById(R.id.textViewNefy);
		textViewNefy.setTypeface(Fonts.mavenBold(this));
		textViewNefyAmount = (TextView) findViewById(R.id.textViewNefyAmount);
		textViewNefyAmount.setTypeface(Fonts.mavenRegular(this));
		textViewWalletBalance = (TextView) findViewById(R.id.textViewWalletBalance);
		textViewWalletBalance.setTypeface(Fonts.mavenBold(this));
		textViewWalletBalanceCaptive = (TextView) findViewById(R.id.textViewWalletBalanceCaptive);
		walletArrowCaptive = (ImageView) findViewById(R.id.wallet_arrow_captive);
		textViewWalletBalanceCaptive.setTypeface(Fonts.mavenBold(this));
		textViewWalletBalanceAmount = (TextView) findViewById(R.id.textViewWalletBalanceAmount);
		textViewWalletBalanceAmount.setTypeface(Fonts.mavenRegular(this));
		textViewWalletBalanceAmountCaptive = (TextView) findViewById(R.id.textViewWalletBalanceAmountCaptive);
		textViewWalletBalanceAmountCaptive.setTypeface(Fonts.mavenRegular(this));
		ImageView imageViewWalletBalance = (ImageView) findViewById(R.id.imageViewWalletBalance);
		int color = Color.parseColor("#FFFFFF");
		imageViewWalletBalance.setColorFilter(color, PorterDuff.Mode.SRC_IN);

		backBtn = (Button) findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Fonts.mavenRegular(this), Typeface.NORMAL);
		imageViewHorizontal7 = (ImageView) findViewById(R.id.imageViewHorizontal7);

//		textShader=new LinearGradient(0, 0, 0, 20,
//				new int[]{getResources().getColor(R.color.gradient_orange_v2), getResources().getColor(R.color.gradient_yellow_v2)},
//				new float[]{0, 1}, Shader.TileMode.CLAMP);
//		title.getPaint().setShader(textShader);
		relativeLayoutWalletListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				FlurryEventLogger.event(FlurryEventNames.EARNINGS_CARD_RIDE_HISTORY);
//				MyApplication.getInstance().logEvent(EARNING + "_" + RIDE_HISTORY, null);
				if (res != null && res.getRechargeOptions() != null) {
					String data = new Gson().toJson(res.getRechargeOptions(), listType);
					Intent intent = new Intent(DriverEarningsNew.this, WalletActivity.class);
					intent.putExtra("data", data);
					intent.putExtra("amount", res.getJugnooBalance());
					startActivity(intent);
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
				} else {
					if (!Data.isCaptive()) {
						DialogPopup.alertPopupWithListener(DriverEarningsNew.this, "", getString(R.string.unable_to_fetch_wallet),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        getEarningsDetails(DriverEarningsNew.this, 0, true);
                                    }
                                });
					}
				}
			}
		};
		relativeLayoutWallet.setOnClickListener(relativeLayoutWalletListener);
		relativeLayoutWalletCaptive.setOnClickListener(relativeLayoutWalletListener);

		if(Prefs.with(this).getInt(Constants.WALLET_BALANCE_IN_EARNING, 1) == 1){
			relativeLayoutWallet.setVisibility(View.VISIBLE);
		} else {
			relativeLayoutWallet.setVisibility(View.GONE);
		}


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
				getDailyDetails(formattedDate,null);
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
							getDailyDetails(res.getEarnings().get(newIndex).getDate(),null);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
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


		textViewTripsLabel = (TextView)findViewById(R.id.textViewTripsText);
		textViewTripsLabel.setText(getString(R.string.day_wise_breakup));
		textViewTripsLabel.setTypeface(Fonts.mavenRegular(this));
		findViewById(R.id.layout_recycler).setVisibility(View.GONE);
		if(Data.isCaptive()){
			relativeLayoutRideHistory.setVisibility(View.GONE);
			getEarningsDetails(this, 0);
			llGraphWithEarnings.setVisibility(View.GONE);
			findViewById(R.id.divider_below_ride_history_bar).setVisibility(View.GONE);

		}else{
			getEarningsDetails(this, 0);


		}

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

	public void getDailyDetails(String date,DriverEarningsResponse.Earning earning){
		Intent intent = new Intent(DriverEarningsNew.this, DailyRideDetailsActivity.class);
		intent.putExtra("date", date);
		if(earning!=null){
			intent.putExtra(DailyRideDetailsActivity.EARNING_DATA, new Gson().toJson(earning, DriverEarningsResponse.Earning.class));

		}
		startActivity(intent);
		overridePendingTransition(R.anim.right_in, R.anim.right_out);
	}


//	Retrofit


	public void updateData(DriverEarningsResponse driverEarningsResponse, boolean walletClick) {

		if (driverEarningsResponse != null) {

			findViewById(R.id.layout_recycler).setVisibility(View.VISIBLE);
			if(driverEarningsResponse.getEarnings()!=null && driverEarningsResponse.getEarnings().size()>0){
				findViewById(R.id.textViewNoData).setVisibility(View.GONE);
			}else {
				findViewById(R.id.textViewNoData).setVisibility(View.VISIBLE);

			}
			setUpDailyEarningsAdapter(driverEarningsResponse.getEarnings());


			if(getResources().getInteger(R.integer.show_invoices)!=getResources().getInteger(R.integer.view_visible)){
				return;
			}

			if(Data.isCaptive()){
				layoutCaptivePlanDetails.setVisibility(View.VISIBLE);
				tvDistanceCaptive.setText(Utils.getKilometers(driverEarningsResponse.getCoveredDistance(),this));
				tvTargetDistance.setText(Utils.getKilometers(driverEarningsResponse.getTargetDistance(),this));
				tvDaysLeftCaptive.setText(" " + driverEarningsResponse.getDaysLeft());
				tvAmountCollectedCaptive.setText(" " + Utils.getAbsWithDecimalAmount(this,driverEarningsResponse.getAmountCollected(),driverEarningsResponse.getCurrencyUnit()));
				if(driverEarningsResponse.getPeriod()!=null){
					dateTimeValue.setText(driverEarningsResponse.getPeriod());
					dateTimeValue.setVisibility(View.VISIBLE);
				}else{
					dateTimeValue.setVisibility(View.GONE);
				}

				if(driverEarningsResponse.getAdjustedKilometer()!=null){
					findViewById(R.id.rl_adjusted_distance).setVisibility(View.VISIBLE);
					tvAdjustedDistanceValue.setText(Utils.getKilometers(driverEarningsResponse.getAdjustedKilometer(),this));
				}else{
					findViewById(R.id.rl_adjusted_distance).setVisibility(View.GONE);

				}

				if(driverEarningsResponse.getJugnooBalance()!=null){
					relativeLayoutWalletCaptive.setVisibility(View.VISIBLE);
					walletArrowCaptive.setVisibility(res != null && res.getRechargeOptions() != null?View.VISIBLE:View.GONE);
					relativeLayoutWalletCaptive.setClickable(res != null && res.getRechargeOptions() != null);
					setWalletData(walletClick, Utils.getDecimalFormatForMoney().format(driverEarningsResponse.getJugnooBalance()),textViewWalletBalanceCaptive,textViewWalletBalanceAmountCaptive,relativeLayoutWalletCaptive);

				}else{
					relativeLayoutWalletCaptive.setVisibility(View.GONE);
				}


			} else{
				//Graph set up Only required for nonCaptive Users
				layoutCaptivePlanDetails.setVisibility(View.GONE);
				if(driverEarningsResponse.getCurrentInvoiceId() == 0){
					relativeLayoutPayout.setVisibility(View.VISIBLE);
					textViewPayOutValue.setText(Utils.formatCurrencyValue(driverEarningsResponse.getEarnings().get(0).getCurrencyUnit(),driverEarningsResponse.getEarnings().get(0).getEarnings()));
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


				ArrayList<BarEntry> entries = new ArrayList<>();
				ArrayList<String> labels = new ArrayList<String>();
				int j = 0;
				maxIndex = driverEarningsResponse.getEarnings().size();
				boolean graphVisibility = false;
				for(int i=driverEarningsResponse.getEarnings().size() ; i > 0 ; i-- ){
					entries.add(new BarEntry(driverEarningsResponse.getEarnings().get(i-1).getEarnings(), j++,driverEarningsResponse.getEarnings().get(i-1).getCurrencyUnit()));
					labels.add(driverEarningsResponse.getEarnings().get(i-1).getDay());
					if(driverEarningsResponse.getEarnings().get(i-1).getEarnings() != 0){
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

				setWalletData(walletClick, Utils.getDecimalFormatForMoney().format(driverEarningsResponse.getJugnooBalance()),textViewWalletBalance,textViewWalletBalanceAmount,relativeLayoutWallet);

				if(driverEarningsResponse.getNeftPending() != null && driverEarningsResponse.getNeftPending()>0) {
					relativeLayoutNefy.setVisibility(View.VISIBLE);
					textViewNefyAmount.setText(getString(R.string.rupees_value_format, Utils.getDecimalFormatForMoney().format(driverEarningsResponse.getNeftPending())));

				} else {
					relativeLayoutNefy.setVisibility(View.GONE);
				}
			}


		} else {
			performBackPressed();
		}

	}

	private void setUpDailyEarningsAdapter(List<DriverEarningsResponse.Earning> earnings) {
		if(newEarningsPerDayAdapter==null){
			newEarningsPerDayAdapter = new NewEarningsPerDayAdapter(this, earnings, listEarningsPerDay, new NewEarningsPerDayAdapter.NewEarningsCallback() {
				@Override
				public void onDailyDetailsClick(DriverEarningsResponse.Earning earning) {
					getDailyDetails(earning.getDate(),earning);
				}
			});
			listEarningsPerDay.setAdapter(newEarningsPerDayAdapter);
		}

	}

	private void setWalletData(boolean walletClick, String amount,TextView textViewWalletBalance,TextView textViewWalletBalanceAmount,RelativeLayout relativeLayoutWallet) {

		String text = getString(R.string.wallet_balance);
		textViewWalletBalance.setText(text.toUpperCase());
		String amountStr = getString(R.string.rupees_value_format, amount);
		if(Double.parseDouble(amount)<0) {
			amountStr = getString(R.string.rupees_value_format_negtive, amount.replace("-", ""));
			textViewWalletBalanceAmount.setText(amountStr + getString(R.string.low_balance), TextView.BufferType.SPANNABLE);
		} else if(Double.parseDouble(amount) < Data.MINI_BALANCE) {
			textViewWalletBalanceAmount.setText(amountStr + getString(R.string.low_balance), TextView.BufferType.SPANNABLE);
		} else {
			textViewWalletBalanceAmount.setText(amountStr, TextView.BufferType.SPANNABLE);
		}
		Spannable spannable = (Spannable) textViewWalletBalanceAmount.getText();
		int index = amountStr.length();
		spannable.setSpan(new RelativeSizeSpan(2f), 0, index, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		if(Double.parseDouble(amount) < Data.MINI_BALANCE) {
			textViewWalletBalanceAmount.setTextColor(getResources().getColor(R.color.red_status));
		}
		if(walletClick) {
			relativeLayoutWallet.performClick();
		}
	}

	private void getEarningsDetails(final Activity activity, int invoice) {
		getEarningsDetails(activity, invoice, false);
	}
	private void getEarningsDetails(final Activity activity, int invoice, final boolean walletClick) {
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
				HashMap<String, String> params = new HashMap<String, String>();

				params.put("access_token", Data.userData.accessToken);
				params.put("login_type", Data.LOGIN_TYPE);
				params.put("invoice_id", invoiceId);
				HomeUtil.putDefaultParams(params);
			if(Data.isCaptive()){
				RestClient.getApiServices().earningNewDetailsCaptive(params, getCallbackEarningDetails(activity, walletClick));

			}else{
				RestClient.getApiServices().earningNewDetails(params, getCallbackEarningDetails(activity, walletClick));

			}
		} else {
			DialogPopup.alertPopup(DriverEarningsNew.this, "", Data.CHECK_INTERNET_MSG);
		}
		} catch (Exception e) {
			e.printStackTrace();
			DialogPopup.dismissLoadingDialog();
		}
	}

	@NonNull
	private Callback<DriverEarningsResponse> getCallbackEarningDetails(final Activity activity, final boolean walletClick) {
		return new Callback<DriverEarningsResponse>() {
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
                        }else{
							DialogPopup.alertPopup(activity,"",errorMessage);
							DialogPopup.dismissLoadingDialog();
						}


                    } else {
                        DialogPopup.dismissLoadingDialog();
                        res = driverEarningsResponse;
                        updateData(driverEarningsResponse, walletClick);
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
					DialogPopup.alertPopup(activity,"",activity.getString(R.string.some_error_occured));
                    DialogPopup.dismissLoadingDialog();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.i("error", String.valueOf(error));
				DialogPopup.alertPopup(activity,"",activity.getString(R.string.some_error_occured));
				DialogPopup.dismissLoadingDialog();
            }
        };
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

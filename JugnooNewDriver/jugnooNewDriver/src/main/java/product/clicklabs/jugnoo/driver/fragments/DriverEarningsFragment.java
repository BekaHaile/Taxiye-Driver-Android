package product.clicklabs.jugnoo.driver.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.DailyEarningActivity;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.DriverRideHistoryNew;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.HomeUtil;
import product.clicklabs.jugnoo.driver.MyApplication;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.WalletActivity;
import product.clicklabs.jugnoo.driver.adapters.NewEarningsPerDayAdapter;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.DriverEarningsResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.CustomMarkerView;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.FirebaseEvents;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class DriverEarningsFragment extends BaseFragment implements CustomMarkerView.Listener,FirebaseEvents {
	@Override
	public String getTitle() {
		return getString(R.string.earnings);
	}

	private Activity activity;



	LinearLayout linearLayoutDriverReferral;
	RelativeLayout relativeLayoutPayout ,relativeLayoutDeliveryEarnings,
			relativeLayoutRideHistory, relativelayoutRandom, relativelayoutChart, relativeLayoutPrev,
			relativeLayoutNext, relativeLayoutChartData, relativeLayoutWallet, relativeLayoutWalletCaptive,relativeLayoutNefy;
	TextView textViewEstPayout,textViewDeliveryEarnings, textViewInvPeriod,
			textViewPayOutValue,textViewDeliveryEarningsValue, textViewRideHistory, textViewNoChartData,
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
	private NestedScrollView nsvRoot;


	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		activity = getActivity();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_earnings, container, false);
		nsvRoot = rootView.findViewById(R.id.nsvRoot);
		llGraphWithEarnings = (LinearLayout) rootView.findViewById(R.id.ll_graph_with_earnings);
		layoutCaptivePlanDetails = (LinearLayout) rootView.findViewById(R.id.layout_captive_plan_details);
		tvDistanceCaptive = (TextView) rootView.findViewById(R.id.tvDistanceCaptive);
		tvDistanceCaptiveLabel = (TextView) rootView.findViewById(R.id.tvDistanceCaptiveLabel);
		tvDaysLeftCaptive = (TextView) rootView.findViewById(R.id.tvDaysLeft);
		tvDaysLeftCaptiveLabel = (TextView) rootView.findViewById(R.id.tvDaysLeftLabel);
		tvAmountCollectedCaptive = (TextView) rootView.findViewById(R.id.tvAmountCollected);
		TextView tvAdjustedDistanceLabel = (TextView) rootView.findViewById(R.id.tvAdjustedDistanceLabel);
		tvAdjustedDistanceValue = (TextView) rootView.findViewById(R.id.tvAdjustedDistanceValue);
		tvAmountCollectedCaptiveLabel = (TextView) rootView.findViewById(R.id.tvAmountCollectedLabel);
		tvTargetDistanceLabel = (TextView) rootView.findViewById(R.id.tvTargetDistanceLabel);
		tvTargetDistance = (TextView) rootView.findViewById(R.id.tvTargetDistance);
		assl = new ASSL(activity, nsvRoot, 1134, 720, false);

		barChart = (BarChart) rootView.findViewById(R.id.chart);
		listEarningsPerDay = (RecyclerView) rootView.findViewById(R.id.list_earnings_per_day);
		listEarningsPerDay.setLayoutManager(new LinearLayoutManager(activity));
		listEarningsPerDay.setNestedScrollingEnabled(false);
		linearLayoutDriverReferral = (LinearLayout) rootView.findViewById(R.id.linearLayoutDriverReferral);
		relativeLayoutPayout = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPayout);
		relativeLayoutDeliveryEarnings = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutDeliveryEarnings);
		relativelayoutChart = (RelativeLayout) rootView.findViewById(R.id.relativelayoutChart);
		relativeLayoutChartData = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutChartData);
		relativelayoutRandom = (RelativeLayout) rootView.findViewById(R.id.relativelayoutRandom);
		relativeLayoutRideHistory = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutRideHistory);
		relativeLayoutWallet = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutWallet);
		relativeLayoutWalletCaptive = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutWalletCaptive);
		relativeLayoutNefy = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutNefy);
		relativeLayoutPrev = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPrev);
		relativeLayoutNext = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutNext);
		imageViewPrev = (ImageView) rootView.findViewById(R.id.imageViewPrev);
		imageViewNext = (ImageView) rootView.findViewById(R.id.imageViewNext);
		textViewEstPayout = (TextView) rootView.findViewById(R.id.textViewEstPayout);
		textViewDeliveryEarnings = (TextView) rootView.findViewById(R.id.textViewDeliveryEarnings);
		textViewInvPeriod = (TextView) rootView.findViewById(R.id.textViewInvPeriod);
		textViewPayOutValue = (TextView) rootView.findViewById(R.id.textViewPayOutValue);
		textViewDeliveryEarningsValue = (TextView) rootView.findViewById(R.id.textViewDeliveryEarningsValue);
		textViewRideHistory = (TextView) rootView.findViewById(R.id.textViewRideHistory);
		textViewNoChartData = (TextView) rootView.findViewById(R.id.textViewNoChartData);
		dateTimeValue = (TextView) rootView.findViewById(R.id.dateTimeValue);
		textViewNefy = (TextView) rootView.findViewById(R.id.textViewNefy);
		textViewNefyAmount = (TextView) rootView.findViewById(R.id.textViewNefyAmount);
		textViewWalletBalance = (TextView) rootView.findViewById(R.id.textViewWalletBalance);
		textViewWalletBalanceCaptive = (TextView) rootView.findViewById(R.id.textViewWalletBalanceCaptive);
		walletArrowCaptive = (ImageView) rootView.findViewById(R.id.wallet_arrow_captive);
		textViewWalletBalanceAmount = (TextView) rootView.findViewById(R.id.textViewWalletBalanceAmount);
		textViewWalletBalanceAmountCaptive = (TextView) rootView.findViewById(R.id.textViewWalletBalanceAmountCaptive);
		ImageView imageViewWalletBalance = (ImageView) rootView.findViewById(R.id.imageViewWalletBalance);
		int color = Color.parseColor("#FFFFFF");
		imageViewWalletBalance.setColorFilter(color, PorterDuff.Mode.SRC_IN);


		imageViewHorizontal7 = (ImageView) rootView.findViewById(R.id.imageViewHorizontal7);

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
					Intent intent = new Intent(activity, WalletActivity.class);
					intent.putExtra("data", data);
					intent.putExtra("amount", res.getJugnooBalance());
					startActivity(intent);
					activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
				} else {
					if (!Data.isCaptive()) {
						DialogPopup.alertPopupWithListener(activity, "", getString(R.string.unable_to_fetch_wallet),
								new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										getEarningsDetails(activity, 0, true);
									}
								});
					}
				}
			}
		};
		relativeLayoutWallet.setOnClickListener(relativeLayoutWalletListener);
		relativeLayoutWalletCaptive.setOnClickListener(relativeLayoutWalletListener);

		if(Prefs.with(activity).getInt(Constants.WALLET_BALANCE_IN_EARNING, 1) == 1){
			relativeLayoutWallet.setVisibility(View.VISIBLE);
		} else {
			relativeLayoutWallet.setVisibility(View.GONE);
		}



		relativeLayoutPrev.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FlurryEventLogger.event(FlurryEventNames.EARNINGS_CARD_THIS_WEEK);
				MyApplication.getInstance().logEvent(EARNING + "_" + CHART + "_" + PREVIOUS, null);
				getEarningsDetails(activity, 1);
			}
		});

		relativeLayoutNext.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FlurryEventLogger.event(FlurryEventNames.EARNINGS_CARD_THIS_WEEK);
				MyApplication.getInstance().logEvent(EARNING + "_" + CHART + "_" + NEXT, null);
				getEarningsDetails(activity, 2);
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
				Intent intent = new Intent(activity, DriverRideHistoryNew.class);
				startActivity(intent);
				activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});


		textViewTripsLabel = (TextView)rootView.findViewById(R.id.textViewTripsText);
		textViewTripsLabel.setText(getString(R.string.day_wise_breakup));
		rootView.findViewById(R.id.layout_recycler).setVisibility(View.GONE);
		if(Data.isCaptive()){
			relativeLayoutRideHistory.setVisibility(View.GONE);
			getEarningsDetails(activity, 0);
			llGraphWithEarnings.setVisibility(View.GONE);
			rootView.findViewById(R.id.divider_below_ride_history_bar).setVisibility(View.GONE);

		}else{
			getEarningsDetails(activity, 0);
			llGraphWithEarnings.setVisibility(Prefs.with(activity).getInt(Constants.KEY_SHOW_GRAPH_IN_EARNINGS, 1) == 1 ? View.VISIBLE : View.GONE);

		}


		return rootView;
	}


	public void getDailyDetails(String date,DriverEarningsResponse.Earning earning){
		Intent intent = new Intent(activity, DailyEarningActivity.class);
		intent.putExtra("date", date);
		if(earning!=null){
			intent.putExtra(DailyEarningActivity.EARNING_DATA, new Gson().toJson(earning, DriverEarningsResponse.Earning.class));

		}
		startActivity(intent);
		activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
	}


//	Retrofit


	public void updateData(DriverEarningsResponse driverEarningsResponse, boolean walletClick) {
		if(getView() == null){
			return;
		}
		if (driverEarningsResponse != null) {

			nsvRoot.findViewById(R.id.layout_recycler).setVisibility(View.VISIBLE);
			if(driverEarningsResponse.getEarnings()!=null && driverEarningsResponse.getEarnings().size()>0){
				nsvRoot.findViewById(R.id.textViewNoData).setVisibility(View.GONE);
			}else {
				nsvRoot.findViewById(R.id.textViewNoData).setVisibility(View.VISIBLE);

			}
			setUpDailyEarningsAdapter(driverEarningsResponse.getEarnings());


			int defaultVisibility = getResources().getInteger(R.integer.show_invoices) == getResources().getInteger(R.integer.view_visible) ? 1 : 0;
			if(Prefs.with(activity).getInt(Constants.KEY_SHOW_GRAPH_IN_EARNINGS, defaultVisibility) != 1){
				return;
			}

			if(Data.isCaptive()){
				layoutCaptivePlanDetails.setVisibility(View.VISIBLE);
				tvDistanceCaptive.setText(Utils.getKilometers(driverEarningsResponse.getCoveredDistance(),activity, driverEarningsResponse.getDistanceUnit()));
				tvTargetDistance.setText(Utils.getKilometers(driverEarningsResponse.getTargetDistance(),activity, driverEarningsResponse.getDistanceUnit()));
				tvDaysLeftCaptive.setText(" " + driverEarningsResponse.getDaysLeft());
				tvAmountCollectedCaptive.setText(" " + Utils.formatCurrencyValue(driverEarningsResponse.getCurrencyUnit(), driverEarningsResponse.getAmountCollected()));
				if(driverEarningsResponse.getPeriod()!=null){
					dateTimeValue.setText(driverEarningsResponse.getPeriod());
					dateTimeValue.setVisibility(View.VISIBLE);
				}else{
					dateTimeValue.setVisibility(View.GONE);
				}

				if(driverEarningsResponse.getAdjustedKilometer()!=null){
					nsvRoot.findViewById(R.id.rl_adjusted_distance).setVisibility(View.VISIBLE);
					tvAdjustedDistanceValue.setText(Utils.getKilometers(driverEarningsResponse.getAdjustedKilometer(),activity, driverEarningsResponse.getDistanceUnit()));
				}else{
					nsvRoot.findViewById(R.id.rl_adjusted_distance).setVisibility(View.GONE);

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
				boolean earningIsNull = true;
				for(DriverEarningsResponse.Earning earning : driverEarningsResponse.getEarnings()) {
					if(earning.getEarnings()!=null) {
						earningIsNull = false;
					}
					if(earning.getDeliveryEarnings()==null) {
						earning.setDeliveryEarnings(0.0);
					}
//					if(earning.getEarnings()==null) {
//						earning.setEarnings(0.0);
//					}
				}




				//Graph set up Only required for nonCaptive Users
				layoutCaptivePlanDetails.setVisibility(View.GONE);
				if(driverEarningsResponse.getCurrentInvoiceId() == 0){
					relativeLayoutPayout.setVisibility(View.VISIBLE);
					if(Data.userData.getDeliveryEnabled()==1) {
						if(!earningIsNull) {
							textViewPayOutValue.setText(Utils.formatCurrencyValue(driverEarningsResponse.getEarnings().get(0).getCurrencyUnit(), driverEarningsResponse.getEarnings().get(0).getEarnings() - driverEarningsResponse.getEarnings().get(0).getDeliveryEarnings()));
							textViewDeliveryEarningsValue.setText(Utils.formatCurrencyValue(driverEarningsResponse.getEarnings().get(0).getCurrencyUnit(), driverEarningsResponse.getEarnings().get(0).getDeliveryEarnings()));
							relativeLayoutDeliveryEarnings.setVisibility(View.GONE);
						}
//
					}
					else {
						if(!earningIsNull) {
							textViewPayOutValue.setText(Utils.formatCurrencyValue(driverEarningsResponse.getEarnings().get(0).getCurrencyUnit(), driverEarningsResponse.getEarnings().get(0).getEarnings()));
							relativeLayoutDeliveryEarnings.setVisibility(View.GONE);
						}

					}
					if(!earningIsNull) {
						textViewPayOutValue.setText(Utils.formatCurrencyValue(driverEarningsResponse.getEarnings().get(0).getCurrencyUnit(), driverEarningsResponse.getEarnings().get(0).getEarnings()));

					}
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
					float value = Float.parseFloat(driverEarningsResponse.getEarnings().get(i-1).getEarnings()+"");
					entries.add(new BarEntry(value, j++,driverEarningsResponse.getEarnings().get(i-1).getCurrencyUnit()));
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
				dataset.setColor(getResources().getColor(R.color.bgColor));
				dataset.setHighLightColor(getResources().getColor(R.color.driverEarningHighLisghtColor));
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
				xAxis.setTextColor(getResources().getColor(R.color.bgColor));
				YAxis yAxis = barChart.getAxisRight();
				yAxis.setDrawAxisLine(false);
				yAxis.setDrawLabels(false);

				barChart.getAxisLeft().setTextSize(12);
				barChart.getAxisLeft().setTextColor(getResources().getColor(R.color.bgColor));

				barChart.setDrawMarkerViews(true);
				CustomMarkerView mv = new CustomMarkerView(activity, R.layout.graph_marker, this);
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
					relativeLayoutNefy.setVisibility(View.GONE);
					textViewNefyAmount.setText(getString(R.string.rupees_value_format, Utils.getDecimalFormatForMoney().format(driverEarningsResponse.getNeftPending())));

				} else {
					relativeLayoutNefy.setVisibility(View.GONE);
				}
			}


		} else {
			DialogPopup.alertPopup(activity, "", getString(R.string.some_error_occured));
		}

	}

	private void setUpDailyEarningsAdapter(List<DriverEarningsResponse.Earning> earnings) {
		if(newEarningsPerDayAdapter==null){
			newEarningsPerDayAdapter = new NewEarningsPerDayAdapter(activity, earnings, listEarningsPerDay, new NewEarningsPerDayAdapter.NewEarningsCallback() {
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
				} else if(invoice == 1 && res != null && res.getPreviousInvoiceId() != null){
					invoiceId = String.valueOf(res.getPreviousInvoiceId());
				}else if(invoice == 2 && res != null && res.getNextInvoiceId()!=null) {
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
				DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
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
							HomeActivity.logoutUser(activity, null);
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
		//relativelayoutRandom.setBackgroundColor(getResources().getColor(R.color.red));

		DisplayMetrics displayMetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int height = displayMetrics.heightPixels;
		int width = displayMetrics.widthPixels;
		// set up layout parameters so our marker is in the same position as the mpchart marker would be (based no the x and y)
		RelativeLayout.LayoutParams lps = (RelativeLayout.LayoutParams) relativelayoutRandom.getLayoutParams();
		lps.height = (int) (height>1000?80f:60f * ASSL.Yscale());
		lps.width = (int) (height>1000?240f:200f * ASSL.Xscale());
		lps.setMarginStart(x);// - (int) (relativelayoutRandom.getMeasuredWidth() / 2) + (int) (100f * ASSL.Xscale()));
		lps.topMargin = (y) - (int) (relativelayoutRandom.getMeasuredHeight()) + (int) (height>1000?220f:170f * ASSL.Yscale());
		relativelayoutRandom.setLayoutParams(lps);
	}

}

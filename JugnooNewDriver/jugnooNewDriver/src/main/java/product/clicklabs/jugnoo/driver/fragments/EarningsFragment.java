package product.clicklabs.jugnoo.driver.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeUtil;
import product.clicklabs.jugnoo.driver.JSONParser;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.SplashNewActivity;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.EarningsDetailResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class EarningsFragment extends Fragment {

	private RelativeLayout relativeLayoutRoot, rlLCMEarningsDeliveries, rlLCEarningsDeliveries, rlTYEarningsDeliveries;

	private TextView textViewTodayValue, textViewWeekValue, textViewMonthRidesNumber, textViewWeekReferralNumber,
			textViewMonthValue, textViewTodayRidesNumber, textViewMonthReferralNumber, textViewWeekRidesNumber, textViewTodayReferralNumber,
			dateTimeValueFromWeek, dateTimeValueToWeek, dateTimeValueFromMonth, dateTimeValueToMonth, textViewTodayDeliveriesNumber,
			textViewTodayRidesValue, textViewTodayReferralValue, textViewTodayDeliveriesValue, textViewWeekDeliveriesNumber,
			textViewWeekRidesValue, textViewWeekReferralValue, textViewWeekDeliveriesValue, textViewMonthDeliveriesNumber, textViewMonthRidesValue
			,textViewMonthReferralValue, textViewMonthDeliveriesValue, textViewTodayDate, textViewYesterdayDate, textViewYesterdayValue
			,textViewYesterdayRidesNumber, textViewYesterdayRidesValue, textViewYesterdayReferralNumber, textViewYesterdayReferralValue,
			textViewYesterdayDeliveriesNumber,textViewYesterdayDeliveriesValue, textViewCurrentWeekDate, textViewWeekDate, textViewCurrentWeekValue,
			textViewCurrentWeekRidesNumber, textViewCurrentWeekRidesValue, textViewCurrentWeekReferralNumber, textViewCurrentWeekReferralValue,
			textViewCurrentWeekDeliveriesNumber, textViewCurrentWeekDeliveriesValue, textViewCurrentMonthDate, textViewMonthDate, textViewCurrentMonthValue,
			textViewCurrentMonthRidesNumber, textViewCurrentMonthRidesValue, textViewCurrentMonthReferralNumber, textViewCurrentMonthReferralValue,
			textViewCurrentMonthDeliveriesNumber, textViewCurrentMonthDeliveriesValue, textViewCondtion;

	private View rootView;
	private FragmentActivity activity;
	EarningsDetailResponse earningsDetailResponse;

	@Override
	public void onStart() {
		super.onStart();
		FlurryAgent.init(activity, Data.FLURRY_KEY);
		FlurryAgent.onStartSession(activity, Data.FLURRY_KEY);
		FlurryAgent.onEvent(EarningsFragment.class.getSimpleName() + " started");
	}

	@Override
	public void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(activity);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_earnings_new, container, false);


		activity = getActivity();

		relativeLayoutRoot = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutRoot);
		try {
			if (relativeLayoutRoot != null) {
				new ASSL(activity, relativeLayoutRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		rlLCMEarningsDeliveries = (RelativeLayout) rootView.findViewById(R.id.rlLCMEarningsDeliveries);
		rlLCEarningsDeliveries = (RelativeLayout) rootView.findViewById(R.id.rlLCEarningsDeliveries);
		rlTYEarningsDeliveries = (RelativeLayout) rootView.findViewById(R.id.rlTYEarningsDeliveries);

		((TextView) rootView.findViewById(R.id.textViewToday)).setTypeface(Data.latoRegular(activity), Typeface.BOLD);
		((TextView) rootView.findViewById(R.id.textViewMonth)).setTypeface(Data.latoRegular(activity), Typeface.BOLD);
		((TextView) rootView.findViewById(R.id.textViewWeek)).setTypeface(Data.latoRegular(activity), Typeface.BOLD);

		((TextView) rootView.findViewById(R.id.textViewYesterday)).setTypeface(Data.latoRegular(activity), Typeface.BOLD);
		((TextView) rootView.findViewById(R.id.textViewCurrentWeek)).setTypeface(Data.latoRegular(activity), Typeface.BOLD);
		((TextView) rootView.findViewById(R.id.textViewCurrentMonth)).setTypeface(Data.latoRegular(activity), Typeface.BOLD);

//		((TextView) rootView.findViewById(R.id.dateTimeTextToWeek)).setTypeface(Data.latoRegular(activity), Typeface.NORMAL);
//		((TextView) rootView.findViewById(R.id.dateTimeTextFromWeek)).setTypeface(Data.latoRegular(activity), Typeface.NORMAL);
//		((TextView) rootView.findViewById(R.id.dateTimeTextFrom)).setTypeface(Data.latoRegular(activity), Typeface.NORMAL);
//		((TextView) rootView.findViewById(R.id.dateTimeTextTo)).setTypeface(Data.latoRegular(activity), Typeface.NORMAL);

		((TextView) rootView.findViewById(R.id.textViewTodayRidesText)).setTypeface(Data.latoRegular(activity), Typeface.BOLD);
		((TextView) rootView.findViewById(R.id.textViewTodayReferralText)).setTypeface(Data.latoRegular(activity), Typeface.BOLD);
		((TextView) rootView.findViewById(R.id.textViewTodayDeliveriesText)).setTypeface(Data.latoRegular(activity), Typeface.BOLD);

		((TextView) rootView.findViewById(R.id.textViewWeekRidesText)).setTypeface(Data.latoRegular(activity), Typeface.BOLD);
		((TextView) rootView.findViewById(R.id.textViewWeekReferralText)).setTypeface(Data.latoRegular(activity), Typeface.BOLD);
		((TextView) rootView.findViewById(R.id.textViewWeekDeliveriesText)).setTypeface(Data.latoRegular(activity), Typeface.BOLD);

		((TextView) rootView.findViewById(R.id.textViewMonthRidesText)).setTypeface(Data.latoRegular(activity), Typeface.BOLD);
		((TextView) rootView.findViewById(R.id.textViewMonthReferralText)).setTypeface(Data.latoRegular(activity), Typeface.BOLD);
		((TextView) rootView.findViewById(R.id.textViewMonthDeliveriesText)).setTypeface(Data.latoRegular(activity), Typeface.BOLD);


		textViewTodayValue = (TextView) rootView.findViewById(R.id.textViewTodayValue);
		textViewTodayValue.setTypeface(Data.latoRegular(activity));
		textViewMonthValue = (TextView) rootView.findViewById(R.id.textViewMonthValue);
		textViewMonthValue.setTypeface(Data.latoRegular(activity));
		textViewWeekValue = (TextView) rootView.findViewById(R.id.textViewWeekValue);
		textViewWeekValue.setTypeface(Data.latoRegular(activity));

		textViewTodayRidesNumber = (TextView) rootView.findViewById(R.id.textViewTodayRidesNumber);
		textViewTodayRidesNumber.setTypeface(Data.latoRegular(activity));
		textViewTodayRidesValue = (TextView) rootView.findViewById(R.id.textViewTodayRidesValue);
		textViewTodayRidesValue.setTypeface(Data.latoRegular(activity));

		textViewTodayReferralNumber = (TextView) rootView.findViewById(R.id.textViewTodayReferralNumber);
		textViewTodayReferralNumber.setTypeface(Data.latoRegular(activity));
		textViewTodayReferralValue = (TextView) rootView.findViewById(R.id.textViewTodayReferralValue);
		textViewTodayReferralValue.setTypeface(Data.latoRegular(activity));

		textViewTodayDeliveriesNumber = (TextView) rootView.findViewById(R.id.textViewTodayDeliveriesNumber);
		textViewTodayDeliveriesNumber.setTypeface(Data.latoRegular(activity));
		textViewTodayDeliveriesValue = (TextView) rootView.findViewById(R.id.textViewTodayDeliveriesValue);
		textViewTodayDeliveriesValue.setTypeface(Data.latoRegular(activity));

		textViewWeekRidesNumber = (TextView) rootView.findViewById(R.id.textViewWeekRidesNumber);
		textViewWeekRidesNumber.setTypeface(Data.latoRegular(activity));
		textViewWeekRidesValue = (TextView) rootView.findViewById(R.id.textViewWeekRidesValue);
		textViewWeekRidesValue.setTypeface(Data.latoRegular(activity));

		textViewWeekReferralNumber = (TextView) rootView.findViewById(R.id.textViewWeekReferralNumber);
		textViewWeekReferralNumber.setTypeface(Data.latoRegular(activity));
		textViewWeekReferralValue = (TextView) rootView.findViewById(R.id.textViewWeekReferralValue);
		textViewWeekReferralValue.setTypeface(Data.latoRegular(activity));

		textViewWeekDeliveriesNumber = (TextView) rootView.findViewById(R.id.textViewWeekDeliveriesNumber);
		textViewWeekDeliveriesNumber.setTypeface(Data.latoRegular(activity));
		textViewWeekDeliveriesValue = (TextView) rootView.findViewById(R.id.textViewWeekDeliveriesValue);
		textViewWeekDeliveriesValue.setTypeface(Data.latoRegular(activity));

		textViewMonthRidesNumber = (TextView) rootView.findViewById(R.id.textViewMonthRidesNumber);
		textViewMonthRidesNumber.setTypeface(Data.latoRegular(activity));
		textViewMonthRidesValue = (TextView) rootView.findViewById(R.id.textViewMonthRidesValue);
		textViewMonthRidesValue.setTypeface(Data.latoRegular(activity));

		textViewMonthDeliveriesNumber = (TextView) rootView.findViewById(R.id.textViewMonthDeliveriesNumber);
		textViewMonthDeliveriesNumber.setTypeface(Data.latoRegular(activity));
		textViewMonthDeliveriesValue = (TextView) rootView.findViewById(R.id.textViewMonthDeliveriesValue);
		textViewMonthDeliveriesValue.setTypeface(Data.latoRegular(activity));

		textViewMonthReferralNumber = (TextView) rootView.findViewById(R.id.textViewMonthReferralNumber);
		textViewMonthReferralNumber.setTypeface(Data.latoRegular(activity));
		textViewMonthReferralValue = (TextView) rootView.findViewById(R.id.textViewMonthReferralValue);
		textViewMonthReferralValue.setTypeface(Data.latoRegular(activity));

//		dateTimeValueFromWeek = (TextView) rootView.findViewById(R.id.dateTimeValueFromWeek);
//		dateTimeValueFromWeek.setTypeface(Data.latoRegular(activity));
//		dateTimeValueToWeek = (TextView) rootView.findViewById(R.id.dateTimeValueToWeek);
//		dateTimeValueToWeek.setTypeface(Data.latoRegular(activity));
//
//		dateTimeValueFromMonth = (TextView) rootView.findViewById(R.id.dateTimeValueFromMonth);
//		dateTimeValueFromMonth.setTypeface(Data.latoRegular(activity));
//		dateTimeValueToMonth = (TextView) rootView.findViewById(R.id.dateTimeValueToMonth);
//		dateTimeValueToMonth.setTypeface(Data.latoRegular(activity));

		textViewTodayDate = (TextView) rootView.findViewById(R.id.textViewTodayDate);
		textViewTodayDate.setTypeface(Data.latoRegular(activity));
		textViewYesterdayDate = (TextView) rootView.findViewById(R.id.textViewYesterdayDate);
		textViewYesterdayDate.setTypeface(Data.latoRegular(activity));
		textViewYesterdayValue = (TextView) rootView.findViewById(R.id.textViewYesterdayValue);
		textViewYesterdayValue.setTypeface(Data.latoRegular(activity));
		textViewYesterdayRidesNumber = (TextView) rootView.findViewById(R.id.textViewYesterdayRidesNumber);
		textViewYesterdayRidesNumber.setTypeface(Data.latoRegular(activity));
		textViewYesterdayRidesValue = (TextView) rootView.findViewById(R.id.textViewYesterdayRidesValue);
		textViewYesterdayRidesValue.setTypeface(Data.latoRegular(activity));
		textViewYesterdayReferralNumber = (TextView) rootView.findViewById(R.id.textViewYesterdayReferralNumber);
		textViewYesterdayReferralNumber.setTypeface(Data.latoRegular(activity));
		textViewYesterdayReferralValue = (TextView) rootView.findViewById(R.id.textViewYesterdayReferralValue);
		textViewYesterdayReferralValue.setTypeface(Data.latoRegular(activity));
		textViewYesterdayDeliveriesNumber = (TextView) rootView.findViewById(R.id.textViewYesterdayDeliveriesNumber);
		textViewYesterdayDeliveriesNumber.setTypeface(Data.latoRegular(activity));
		textViewYesterdayDeliveriesValue = (TextView) rootView.findViewById(R.id.textViewYesterdayDeliveriesValue);
		textViewYesterdayDeliveriesValue.setTypeface(Data.latoRegular(activity));


		textViewCurrentWeekDate = (TextView) rootView.findViewById(R.id.textViewCurrentWeekDate);
		textViewCurrentWeekDate.setTypeface(Data.latoRegular(activity));
		textViewWeekDate = (TextView) rootView.findViewById(R.id.textViewWeekDate);
		textViewWeekDate.setTypeface(Data.latoRegular(activity));
		textViewCurrentWeekValue = (TextView) rootView.findViewById(R.id.textViewCurrentWeekValue);
		textViewCurrentWeekValue.setTypeface(Data.latoRegular(activity));
		textViewCurrentWeekRidesNumber = (TextView) rootView.findViewById(R.id.textViewCurrentWeekRidesNumber);
		textViewCurrentWeekRidesNumber.setTypeface(Data.latoRegular(activity));
		textViewCurrentWeekRidesValue = (TextView) rootView.findViewById(R.id.textViewCurrentWeekRidesValue);
		textViewCurrentWeekRidesValue.setTypeface(Data.latoRegular(activity));
		textViewCurrentWeekReferralNumber = (TextView) rootView.findViewById(R.id.textViewCurrentWeekReferralNumber);
		textViewCurrentWeekReferralNumber.setTypeface(Data.latoRegular(activity));
		textViewCurrentWeekReferralValue = (TextView) rootView.findViewById(R.id.textViewCurrentWeekReferralValue);
		textViewCurrentWeekReferralValue.setTypeface(Data.latoRegular(activity));
		textViewCurrentWeekDeliveriesNumber = (TextView) rootView.findViewById(R.id.textViewCurrentWeekDeliveriesNumber);
		textViewCurrentWeekDeliveriesNumber.setTypeface(Data.latoRegular(activity));
		textViewCurrentWeekDeliveriesValue = (TextView) rootView.findViewById(R.id.textViewCurrentWeekDeliveriesValue);
		textViewCurrentWeekDeliveriesValue.setTypeface(Data.latoRegular(activity));


		textViewCurrentMonthDate = (TextView) rootView.findViewById(R.id.textViewCurrentMonthDate);
		textViewCurrentMonthDate.setTypeface(Data.latoRegular(activity));
		textViewMonthDate = (TextView) rootView.findViewById(R.id.textViewMonthDate);
		textViewMonthDate.setTypeface(Data.latoRegular(activity));
		textViewCurrentMonthValue = (TextView) rootView.findViewById(R.id.textViewCurrentMonthValue);
		textViewCurrentMonthValue.setTypeface(Data.latoRegular(activity));
		textViewCurrentMonthRidesNumber = (TextView) rootView.findViewById(R.id.textViewCurrentMonthRidesNumber);
		textViewCurrentMonthRidesNumber.setTypeface(Data.latoRegular(activity));
		textViewCurrentMonthRidesValue = (TextView) rootView.findViewById(R.id.textViewCurrentMonthRidesValue);
		textViewCurrentMonthRidesValue.setTypeface(Data.latoRegular(activity));
		textViewCurrentMonthReferralNumber = (TextView) rootView.findViewById(R.id.textViewCurrentMonthReferralNumber);
		textViewCurrentMonthReferralNumber.setTypeface(Data.latoRegular(activity));
		textViewCurrentMonthReferralValue = (TextView) rootView.findViewById(R.id.textViewCurrentMonthReferralValue);
		textViewCurrentMonthReferralValue.setTypeface(Data.latoRegular(activity));
		textViewCurrentMonthDeliveriesNumber = (TextView) rootView.findViewById(R.id.textViewCurrentMonthDeliveriesNumber);
		textViewCurrentMonthDeliveriesNumber.setTypeface(Data.latoRegular(activity));
		textViewCurrentMonthDeliveriesValue = (TextView) rootView.findViewById(R.id.textViewCurrentMonthDeliveriesValue);
		textViewCurrentMonthDeliveriesValue.setTypeface(Data.latoRegular(activity));
		textViewCondtion = (TextView) rootView.findViewById(R.id.textViewCondtion);
		textViewCondtion.setTypeface(Data.latoRegular(activity));

		getLeaderboardActivityCall();


		try {
		} catch (Exception e) {
			e.printStackTrace();
		}

		update();

		return rootView;
	}


	public void update() {
		try {
			if (earningsDetailResponse != null) {
				textViewTodayValue.setText(getResources().getString(R.string.rupee) + Utils.getDecimalFormatForMoney().
						format(earningsDetailResponse.getDaily().getEarnings()));
				textViewWeekValue.setText(getResources().getString(R.string.rupee) + Utils.getDecimalFormatForMoney().
						format(earningsDetailResponse.getWeekly().getEarnings()));
				textViewMonthValue.setText(getResources().getString(R.string.rupee) + Utils.getDecimalFormatForMoney().
						format(earningsDetailResponse.getMonthly().getEarnings()));


				textViewTodayRidesNumber.setText(""+earningsDetailResponse.getDaily().getRides());
				textViewTodayRidesValue.setText(getResources().getString(R.string.rupee)+Utils.getDecimalFormatForMoney().
						format(earningsDetailResponse.getDaily().getRidesAmount()));

				textViewTodayReferralNumber.setText(""+earningsDetailResponse.getDaily().getReferrals());
				textViewTodayReferralValue.setText(getResources().getString(R.string.rupee) + Utils.getDecimalFormatForMoney().
						format(earningsDetailResponse.getDaily().getReferralAmount()));

				if(earningsDetailResponse.getDaily().getDeliveryCharges()>0) {
					rlTYEarningsDeliveries.setVisibility(View.VISIBLE);
					textViewTodayDeliveriesNumber.setText("" + earningsDetailResponse.getDaily().getDeliveryCount());
					textViewTodayDeliveriesValue.setText(getResources().getString(R.string.rupee) + Utils.getDecimalFormatForMoney().
							format(earningsDetailResponse.getDaily().getDeliveryCharges()));
				}

				textViewWeekRidesNumber.setText(""+earningsDetailResponse.getWeekly().getRides());
				textViewWeekRidesValue.setText(getResources().getString(R.string.rupee)+Utils.getDecimalFormatForMoney().
						format(earningsDetailResponse.getWeekly().getRidesAmount()));

				textViewWeekReferralNumber.setText(""+earningsDetailResponse.getWeekly().getReferrals());
				textViewWeekReferralValue.setText(getResources().getString(R.string.rupee) + Utils.getDecimalFormatForMoney().
						format(earningsDetailResponse.getWeekly().getReferralAmount()));

				if(earningsDetailResponse.getWeekly().getDeliveryCharges()>0) {
					rlLCEarningsDeliveries.setVisibility(View.VISIBLE);
					textViewWeekDeliveriesNumber.setText("" + earningsDetailResponse.getWeekly().getDeliveryCount());
					textViewWeekDeliveriesValue.setText(getResources().getString(R.string.rupee) + Utils.getDecimalFormatForMoney().
							format(earningsDetailResponse.getWeekly().getDeliveryCharges()));
				}

				textViewMonthRidesNumber.setText(""+earningsDetailResponse.getMonthly().getRides());
				textViewMonthRidesValue.setText(getResources().getString(R.string.rupee) + Utils.getDecimalFormatForMoney().
						format(earningsDetailResponse.getMonthly().getRidesAmount()));

				if(earningsDetailResponse.getMonthly().getDeliveryCharges()>0) {
					rlLCMEarningsDeliveries.setVisibility(View.VISIBLE);
					textViewMonthDeliveriesNumber.setText("" + earningsDetailResponse.getMonthly().getDeliveryCount());
					textViewMonthDeliveriesValue.setText(getResources().getString(R.string.rupee) + Utils.getDecimalFormatForMoney().
							format(earningsDetailResponse.getMonthly().getDeliveryCharges()));
				}

				textViewMonthReferralNumber.setText(""+earningsDetailResponse.getMonthly().getReferrals());
				textViewMonthReferralValue.setText(getResources().getString(R.string.rupee)+Utils.getDecimalFormatForMoney().
						format(earningsDetailResponse.getMonthly().getReferralAmount()));


//				dateTimeValueFromWeek.setText(DateOperations.reverseDate(earningsDetailResponse.getWeekly().getStartDate()));
//				dateTimeValueToWeek.setText(DateOperations.reverseDate(earningsDetailResponse.getWeekly().getEndDate()));
//				dateTimeValueFromMonth.setText(DateOperations.reverseDate(earningsDetailResponse.getMonthly().getStartDate()));
//				dateTimeValueToMonth.setText(DateOperations.reverseDate(earningsDetailResponse.getMonthly().getEndDate()));


				textViewTodayDate.setText(""+earningsDetailResponse.getDaily().getShowDate());
				textViewYesterdayDate.setText(""+earningsDetailResponse.getYesterday().getShowDate());

				textViewYesterdayValue.setText(getResources().getString(R.string.rupee) + Utils.getDecimalFormatForMoney().
						format(earningsDetailResponse.getYesterday().getEarnings()));
				textViewYesterdayRidesNumber.setText(""+earningsDetailResponse.getYesterday().getRides());
				textViewYesterdayRidesValue.setText(getResources().getString(R.string.rupee) + Utils.getDecimalFormatForMoney().
						format(earningsDetailResponse.getYesterday().getRidesAmount()));

				textViewYesterdayReferralNumber.setText( ""+earningsDetailResponse.getYesterday().getReferrals());
				textViewYesterdayReferralValue.setText(getResources().getString(R.string.rupee) + Utils.getDecimalFormatForMoney().
						format(earningsDetailResponse.getYesterday().getReferralAmount()));

				if(earningsDetailResponse.getYesterday().getDeliveryCharges() >0) {
					rlTYEarningsDeliveries.setVisibility(View.VISIBLE);
					textViewYesterdayDeliveriesNumber.setText("" + earningsDetailResponse.getYesterday().getDeliveryCount());
					textViewYesterdayDeliveriesValue.setText(getResources().getString(R.string.rupee) + Utils.getDecimalFormatForMoney().
							format(earningsDetailResponse.getYesterday().getDeliveryCharges()));
				}

				textViewCurrentWeekDate.setText(""+earningsDetailResponse.getThisWeek().getShowDate());
				textViewWeekDate.setText(""+earningsDetailResponse.getWeekly().getShowDate());

				textViewCurrentWeekValue.setText(getResources().getString(R.string.rupee) + Utils.getDecimalFormatForMoney().
						format(earningsDetailResponse.getThisWeek().getEarnings()));
				textViewCurrentWeekRidesNumber.setText(""+earningsDetailResponse.getThisWeek().getRides());
				textViewCurrentWeekRidesValue.setText(getResources().getString(R.string.rupee) + Utils.getDecimalFormatForMoney().
						format(earningsDetailResponse.getThisWeek().getRidesAmount()));

				textViewCurrentWeekReferralNumber.setText(""+earningsDetailResponse.getThisWeek().getReferrals());
				textViewCurrentWeekReferralValue.setText(getResources().getString(R.string.rupee) + Utils.getDecimalFormatForMoney().
						format(earningsDetailResponse.getThisWeek().getReferralAmount()));

				if(earningsDetailResponse.getThisWeek().getDeliveryCharges() > 0) {
					rlLCEarningsDeliveries.setVisibility(View.VISIBLE);
					textViewCurrentWeekDeliveriesNumber.setText("" + earningsDetailResponse.getThisWeek().getDeliveryCount());
					textViewCurrentWeekDeliveriesValue.setText(getResources().getString(R.string.rupee) + Utils.getDecimalFormatForMoney().
							format(earningsDetailResponse.getThisWeek().getDeliveryCharges()));
				}

				textViewCurrentMonthDate.setText(""+earningsDetailResponse.getThisMonth().getShowDate());
				textViewMonthDate.setText(""+earningsDetailResponse.getMonthly().getShowDate());

				textViewCurrentMonthValue.setText(getResources().getString(R.string.rupee) + Utils.getDecimalFormatForMoney().
						format(earningsDetailResponse.getThisMonth().getEarnings()));
				textViewCurrentMonthRidesNumber.setText( ""+earningsDetailResponse.getThisMonth().getRides());
				textViewCurrentMonthRidesValue.setText(getResources().getString(R.string.rupee) + Utils.getDecimalFormatForMoney().
						format(earningsDetailResponse.getThisMonth().getRidesAmount()));

				textViewCurrentMonthReferralNumber.setText(""+earningsDetailResponse.getThisMonth().getReferrals());
				textViewCurrentMonthReferralValue.setText(getResources().getString(R.string.rupee) + Utils.getDecimalFormatForMoney().
						format(earningsDetailResponse.getThisMonth().getReferralAmount()));

				if(earningsDetailResponse.getThisMonth().getDeliveryCharges() > 0) {
					rlLCMEarningsDeliveries.setVisibility(View.VISIBLE);
					textViewCurrentMonthDeliveriesNumber.setText("" + earningsDetailResponse.getThisMonth().getDeliveryCount());
					textViewCurrentMonthDeliveriesValue.setText(getResources().getString(R.string.rupee) + Utils.getDecimalFormatForMoney().
							format(earningsDetailResponse.getThisMonth().getDeliveryCharges()));
				}
				textViewCondtion.setText(""+earningsDetailResponse.getNote());

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			ASSL.closeActivity(relativeLayoutRoot);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.gc();
	}

	public void getLeaderboardActivityCall() {
		try {
			if (!((BaseFragmentActivity)activity).checkIfUserDataNull() && AppStatus.getInstance(activity).isOnline(activity)) {
                DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));
				HashMap<String, String> params = new HashMap<String, String>();

				params.put("access_token", Data.userData.accessToken);
				params.put("login_type", Data.LOGIN_TYPE);
				HomeUtil.putDefaultParams(params);
                RestClient.getApiServices().earningDetails(params,
                        new Callback<EarningsDetailResponse>() {
                            @Override
                            public void success(EarningsDetailResponse earningsDetailResponse, Response response) {
                                DialogPopup.dismissLoadingDialog();
                                try {
                                    String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
                                    JSONObject jObj;
                                    jObj = new JSONObject(jsonString);
                                    int flag = jObj.optInt("flag", ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
                                    String message = JSONParser.getServerMessage(jObj);
                                    if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag)) {
                                        if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                                            EarningsFragment.this.earningsDetailResponse = earningsDetailResponse;
                                            update();
                                            Log.v("success at", "leaderboeard");
                                        } else {
                                            DialogPopup.alertPopup(activity, "", message);
                                        }
                                    }
                                } catch (Exception exception) {
                                    exception.printStackTrace();
                                }
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                DialogPopup.dismissLoadingDialog();
                            }
                        });
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}

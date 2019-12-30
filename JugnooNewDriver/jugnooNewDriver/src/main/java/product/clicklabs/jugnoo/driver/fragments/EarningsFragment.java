package product.clicklabs.jugnoo.driver.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Fonts;
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


	}

	@Override
	public void onStop() {
		super.onStop();
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

		((TextView) rootView.findViewById(R.id.textViewToday)).setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
		((TextView) rootView.findViewById(R.id.textViewMonth)).setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
		((TextView) rootView.findViewById(R.id.textViewWeek)).setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);

		((TextView) rootView.findViewById(R.id.textViewYesterday)).setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
		((TextView) rootView.findViewById(R.id.textViewCurrentWeek)).setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
		((TextView) rootView.findViewById(R.id.textViewCurrentMonth)).setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);

//		((TextView) rootView.findViewById(R.id.dateTimeTextToWeek)).setTypeface(Fonts.mavenRegular(activity), Typeface.NORMAL);
//		((TextView) rootView.findViewById(R.id.dateTimeTextFromWeek)).setTypeface(Fonts.mavenRegular(activity), Typeface.NORMAL);
//		((TextView) rootView.findViewById(R.id.dateTimeTextFrom)).setTypeface(Fonts.mavenRegular(activity), Typeface.NORMAL);
//		((TextView) rootView.findViewById(R.id.dateTimeTextTo)).setTypeface(Fonts.mavenRegular(activity), Typeface.NORMAL);

		((TextView) rootView.findViewById(R.id.textViewTodayRidesText)).setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
		((TextView) rootView.findViewById(R.id.textViewTodayReferralText)).setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
		((TextView) rootView.findViewById(R.id.textViewTodayDeliveriesText)).setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);

		((TextView) rootView.findViewById(R.id.textViewWeekRidesText)).setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
		((TextView) rootView.findViewById(R.id.textViewWeekReferralText)).setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
		((TextView) rootView.findViewById(R.id.textViewWeekDeliveriesText)).setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);

		((TextView) rootView.findViewById(R.id.textViewMonthRidesText)).setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
		((TextView) rootView.findViewById(R.id.textViewMonthReferralText)).setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
		((TextView) rootView.findViewById(R.id.textViewMonthDeliveriesText)).setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);


		textViewTodayValue = (TextView) rootView.findViewById(R.id.textViewTodayValue);
		textViewTodayValue.setTypeface(Fonts.mavenRegular(activity));
		textViewMonthValue = (TextView) rootView.findViewById(R.id.textViewMonthValue);
		textViewMonthValue.setTypeface(Fonts.mavenRegular(activity));
		textViewWeekValue = (TextView) rootView.findViewById(R.id.textViewWeekValue);
		textViewWeekValue.setTypeface(Fonts.mavenRegular(activity));

		textViewTodayRidesNumber = (TextView) rootView.findViewById(R.id.textViewTodayRidesNumber);
		textViewTodayRidesNumber.setTypeface(Fonts.mavenRegular(activity));
		textViewTodayRidesValue = (TextView) rootView.findViewById(R.id.textViewTodayRidesValue);
		textViewTodayRidesValue.setTypeface(Fonts.mavenRegular(activity));

		textViewTodayReferralNumber = (TextView) rootView.findViewById(R.id.textViewTodayReferralNumber);
		textViewTodayReferralNumber.setTypeface(Fonts.mavenRegular(activity));
		textViewTodayReferralValue = (TextView) rootView.findViewById(R.id.textViewTodayReferralValue);
		textViewTodayReferralValue.setTypeface(Fonts.mavenRegular(activity));

		textViewTodayDeliveriesNumber = (TextView) rootView.findViewById(R.id.textViewTodayDeliveriesNumber);
		textViewTodayDeliveriesNumber.setTypeface(Fonts.mavenRegular(activity));
		textViewTodayDeliveriesValue = (TextView) rootView.findViewById(R.id.textViewTodayDeliveriesValue);
		textViewTodayDeliveriesValue.setTypeface(Fonts.mavenRegular(activity));

		textViewWeekRidesNumber = (TextView) rootView.findViewById(R.id.textViewWeekRidesNumber);
		textViewWeekRidesNumber.setTypeface(Fonts.mavenRegular(activity));
		textViewWeekRidesValue = (TextView) rootView.findViewById(R.id.textViewWeekRidesValue);
		textViewWeekRidesValue.setTypeface(Fonts.mavenRegular(activity));

		textViewWeekReferralNumber = (TextView) rootView.findViewById(R.id.textViewWeekReferralNumber);
		textViewWeekReferralNumber.setTypeface(Fonts.mavenRegular(activity));
		textViewWeekReferralValue = (TextView) rootView.findViewById(R.id.textViewWeekReferralValue);
		textViewWeekReferralValue.setTypeface(Fonts.mavenRegular(activity));

		textViewWeekDeliveriesNumber = (TextView) rootView.findViewById(R.id.textViewWeekDeliveriesNumber);
		textViewWeekDeliveriesNumber.setTypeface(Fonts.mavenRegular(activity));
		textViewWeekDeliveriesValue = (TextView) rootView.findViewById(R.id.textViewWeekDeliveriesValue);
		textViewWeekDeliveriesValue.setTypeface(Fonts.mavenRegular(activity));

		textViewMonthRidesNumber = (TextView) rootView.findViewById(R.id.textViewMonthRidesNumber);
		textViewMonthRidesNumber.setTypeface(Fonts.mavenRegular(activity));
		textViewMonthRidesValue = (TextView) rootView.findViewById(R.id.textViewMonthRidesValue);
		textViewMonthRidesValue.setTypeface(Fonts.mavenRegular(activity));

		textViewMonthDeliveriesNumber = (TextView) rootView.findViewById(R.id.textViewMonthDeliveriesNumber);
		textViewMonthDeliveriesNumber.setTypeface(Fonts.mavenRegular(activity));
		textViewMonthDeliveriesValue = (TextView) rootView.findViewById(R.id.textViewMonthDeliveriesValue);
		textViewMonthDeliveriesValue.setTypeface(Fonts.mavenRegular(activity));

		textViewMonthReferralNumber = (TextView) rootView.findViewById(R.id.textViewMonthReferralNumber);
		textViewMonthReferralNumber.setTypeface(Fonts.mavenRegular(activity));
		textViewMonthReferralValue = (TextView) rootView.findViewById(R.id.textViewMonthReferralValue);
		textViewMonthReferralValue.setTypeface(Fonts.mavenRegular(activity));

//		dateTimeValueFromWeek = (TextView) rootView.findViewById(R.id.dateTimeValueFromWeek);
//		dateTimeValueFromWeek.setTypeface(Fonts.mavenRegular(activity));
//		dateTimeValueToWeek = (TextView) rootView.findViewById(R.id.dateTimeValueToWeek);
//		dateTimeValueToWeek.setTypeface(Fonts.mavenRegular(activity));
//
//		dateTimeValueFromMonth = (TextView) rootView.findViewById(R.id.dateTimeValueFromMonth);
//		dateTimeValueFromMonth.setTypeface(Fonts.mavenRegular(activity));
//		dateTimeValueToMonth = (TextView) rootView.findViewById(R.id.dateTimeValueToMonth);
//		dateTimeValueToMonth.setTypeface(Fonts.mavenRegular(activity));

		textViewTodayDate = (TextView) rootView.findViewById(R.id.textViewTodayDate);
		textViewTodayDate.setTypeface(Fonts.mavenRegular(activity));
		textViewYesterdayDate = (TextView) rootView.findViewById(R.id.textViewYesterdayDate);
		textViewYesterdayDate.setTypeface(Fonts.mavenRegular(activity));
		textViewYesterdayValue = (TextView) rootView.findViewById(R.id.textViewYesterdayValue);
		textViewYesterdayValue.setTypeface(Fonts.mavenRegular(activity));
		textViewYesterdayRidesNumber = (TextView) rootView.findViewById(R.id.textViewYesterdayRidesNumber);
		textViewYesterdayRidesNumber.setTypeface(Fonts.mavenRegular(activity));
		textViewYesterdayRidesValue = (TextView) rootView.findViewById(R.id.textViewYesterdayRidesValue);
		textViewYesterdayRidesValue.setTypeface(Fonts.mavenRegular(activity));
		textViewYesterdayReferralNumber = (TextView) rootView.findViewById(R.id.textViewYesterdayReferralNumber);
		textViewYesterdayReferralNumber.setTypeface(Fonts.mavenRegular(activity));
		textViewYesterdayReferralValue = (TextView) rootView.findViewById(R.id.textViewYesterdayReferralValue);
		textViewYesterdayReferralValue.setTypeface(Fonts.mavenRegular(activity));
		textViewYesterdayDeliveriesNumber = (TextView) rootView.findViewById(R.id.textViewYesterdayDeliveriesNumber);
		textViewYesterdayDeliveriesNumber.setTypeface(Fonts.mavenRegular(activity));
		textViewYesterdayDeliveriesValue = (TextView) rootView.findViewById(R.id.textViewYesterdayDeliveriesValue);
		textViewYesterdayDeliveriesValue.setTypeface(Fonts.mavenRegular(activity));


		textViewCurrentWeekDate = (TextView) rootView.findViewById(R.id.textViewCurrentWeekDate);
		textViewCurrentWeekDate.setTypeface(Fonts.mavenRegular(activity));
		textViewWeekDate = (TextView) rootView.findViewById(R.id.textViewWeekDate);
		textViewWeekDate.setTypeface(Fonts.mavenRegular(activity));
		textViewCurrentWeekValue = (TextView) rootView.findViewById(R.id.textViewCurrentWeekValue);
		textViewCurrentWeekValue.setTypeface(Fonts.mavenRegular(activity));
		textViewCurrentWeekRidesNumber = (TextView) rootView.findViewById(R.id.textViewCurrentWeekRidesNumber);
		textViewCurrentWeekRidesNumber.setTypeface(Fonts.mavenRegular(activity));
		textViewCurrentWeekRidesValue = (TextView) rootView.findViewById(R.id.textViewCurrentWeekRidesValue);
		textViewCurrentWeekRidesValue.setTypeface(Fonts.mavenRegular(activity));
		textViewCurrentWeekReferralNumber = (TextView) rootView.findViewById(R.id.textViewCurrentWeekReferralNumber);
		textViewCurrentWeekReferralNumber.setTypeface(Fonts.mavenRegular(activity));
		textViewCurrentWeekReferralValue = (TextView) rootView.findViewById(R.id.textViewCurrentWeekReferralValue);
		textViewCurrentWeekReferralValue.setTypeface(Fonts.mavenRegular(activity));
		textViewCurrentWeekDeliveriesNumber = (TextView) rootView.findViewById(R.id.textViewCurrentWeekDeliveriesNumber);
		textViewCurrentWeekDeliveriesNumber.setTypeface(Fonts.mavenRegular(activity));
		textViewCurrentWeekDeliveriesValue = (TextView) rootView.findViewById(R.id.textViewCurrentWeekDeliveriesValue);
		textViewCurrentWeekDeliveriesValue.setTypeface(Fonts.mavenRegular(activity));


		textViewCurrentMonthDate = (TextView) rootView.findViewById(R.id.textViewCurrentMonthDate);
		textViewCurrentMonthDate.setTypeface(Fonts.mavenRegular(activity));
		textViewMonthDate = (TextView) rootView.findViewById(R.id.textViewMonthDate);
		textViewMonthDate.setTypeface(Fonts.mavenRegular(activity));
		textViewCurrentMonthValue = (TextView) rootView.findViewById(R.id.textViewCurrentMonthValue);
		textViewCurrentMonthValue.setTypeface(Fonts.mavenRegular(activity));
		textViewCurrentMonthRidesNumber = (TextView) rootView.findViewById(R.id.textViewCurrentMonthRidesNumber);
		textViewCurrentMonthRidesNumber.setTypeface(Fonts.mavenRegular(activity));
		textViewCurrentMonthRidesValue = (TextView) rootView.findViewById(R.id.textViewCurrentMonthRidesValue);
		textViewCurrentMonthRidesValue.setTypeface(Fonts.mavenRegular(activity));
		textViewCurrentMonthReferralNumber = (TextView) rootView.findViewById(R.id.textViewCurrentMonthReferralNumber);
		textViewCurrentMonthReferralNumber.setTypeface(Fonts.mavenRegular(activity));
		textViewCurrentMonthReferralValue = (TextView) rootView.findViewById(R.id.textViewCurrentMonthReferralValue);
		textViewCurrentMonthReferralValue.setTypeface(Fonts.mavenRegular(activity));
		textViewCurrentMonthDeliveriesNumber = (TextView) rootView.findViewById(R.id.textViewCurrentMonthDeliveriesNumber);
		textViewCurrentMonthDeliveriesNumber.setTypeface(Fonts.mavenRegular(activity));
		textViewCurrentMonthDeliveriesValue = (TextView) rootView.findViewById(R.id.textViewCurrentMonthDeliveriesValue);
		textViewCurrentMonthDeliveriesValue.setTypeface(Fonts.mavenRegular(activity));
		textViewCondtion = (TextView) rootView.findViewById(R.id.textViewCondtion);
		textViewCondtion.setTypeface(Fonts.mavenRegular(activity));

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
                                    if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag, null)) {
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

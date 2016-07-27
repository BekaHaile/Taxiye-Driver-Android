package product.clicklabs.jugnoo.driver.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import org.json.JSONObject;

import product.clicklabs.jugnoo.driver.Data;
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
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class EarningsFragment extends Fragment {

	private LinearLayout linearLayoutRoot;

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
			textViewCurrentMonthDeliveriesNumber, textViewCurrentMonthDeliveriesValue;

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

		linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
		try {
			if (linearLayoutRoot != null) {
				new ASSL(activity, linearLayoutRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		((TextView) rootView.findViewById(R.id.textViewToday)).setTypeface(Data.latoRegular(activity), Typeface.BOLD);
		((TextView) rootView.findViewById(R.id.textViewMonth)).setTypeface(Data.latoRegular(activity), Typeface.BOLD);
		((TextView) rootView.findViewById(R.id.textViewWeek)).setTypeface(Data.latoRegular(activity), Typeface.BOLD);

		((TextView) rootView.findViewById(R.id.dateTimeTextToWeek)).setTypeface(Data.latoRegular(activity), Typeface.NORMAL);
		((TextView) rootView.findViewById(R.id.dateTimeTextFromWeek)).setTypeface(Data.latoRegular(activity), Typeface.NORMAL);
		((TextView) rootView.findViewById(R.id.dateTimeTextFrom)).setTypeface(Data.latoRegular(activity), Typeface.NORMAL);
		((TextView) rootView.findViewById(R.id.dateTimeTextTo)).setTypeface(Data.latoRegular(activity), Typeface.NORMAL);

		((TextView) rootView.findViewById(R.id.textViewTodayRidesText)).setTypeface(Data.latoRegular(activity), Typeface.NORMAL);
		((TextView) rootView.findViewById(R.id.textViewTodayReferralText)).setTypeface(Data.latoRegular(activity), Typeface.NORMAL);
		((TextView) rootView.findViewById(R.id.textViewTodayDeliveriesText)).setTypeface(Data.latoRegular(activity), Typeface.NORMAL);

		((TextView) rootView.findViewById(R.id.textViewWeekRidesText)).setTypeface(Data.latoRegular(activity), Typeface.NORMAL);
		((TextView) rootView.findViewById(R.id.textViewWeekReferralText)).setTypeface(Data.latoRegular(activity), Typeface.NORMAL);
		((TextView) rootView.findViewById(R.id.textViewWeekDeliveriesText)).setTypeface(Data.latoRegular(activity), Typeface.NORMAL);

		((TextView) rootView.findViewById(R.id.textViewMonthRidesText)).setTypeface(Data.latoRegular(activity), Typeface.NORMAL);
		((TextView) rootView.findViewById(R.id.textViewMonthReferralText)).setTypeface(Data.latoRegular(activity), Typeface.NORMAL);
		((TextView) rootView.findViewById(R.id.textViewMonthDeliveriesText)).setTypeface(Data.latoRegular(activity), Typeface.NORMAL);


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

		dateTimeValueFromWeek = (TextView) rootView.findViewById(R.id.dateTimeValueFromWeek);
		dateTimeValueFromWeek.setTypeface(Data.latoRegular(activity));
		dateTimeValueToWeek = (TextView) rootView.findViewById(R.id.dateTimeValueToWeek);
		dateTimeValueToWeek.setTypeface(Data.latoRegular(activity));

		dateTimeValueFromMonth = (TextView) rootView.findViewById(R.id.dateTimeValueFromMonth);
		dateTimeValueFromMonth.setTypeface(Data.latoRegular(activity));
		dateTimeValueToMonth = (TextView) rootView.findViewById(R.id.dateTimeValueToMonth);
		dateTimeValueToMonth.setTypeface(Data.latoRegular(activity));

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
				textViewTodayValue.setText(getResources().getString(R.string.rupee) + earningsDetailResponse.getDaily().getEarnings());
				textViewWeekValue.setText(getResources().getString(R.string.rupee) + earningsDetailResponse.getWeekly().getEarnings());
				textViewMonthValue.setText(getResources().getString(R.string.rupee) + earningsDetailResponse.getMonthly().getEarnings());


				textViewTodayRidesNumber.setText(""+earningsDetailResponse.getDaily().getRides());
				textViewTodayRidesValue.setText(getResources().getString(R.string.rupee)+earningsDetailResponse.getDaily().getRidesAmount());

				textViewTodayReferralNumber.setText(""+earningsDetailResponse.getDaily().getReferrals());
				textViewTodayReferralValue.setText(getResources().getString(R.string.rupee)+earningsDetailResponse.getDaily().getReferralAmount());

				textViewTodayDeliveriesNumber.setText(""+earningsDetailResponse.getDaily().getDeliveryCount());
				textViewTodayDeliveriesValue.setText(getResources().getString(R.string.rupee)+earningsDetailResponse.getDaily().getDeliveryCharges());


				textViewWeekRidesNumber.setText(""+earningsDetailResponse.getWeekly().getRides());
				textViewWeekRidesValue.setText(getResources().getString(R.string.rupee)+earningsDetailResponse.getWeekly().getRidesAmount());

				textViewWeekReferralNumber.setText(""+earningsDetailResponse.getWeekly().getReferrals());
				textViewWeekReferralValue.setText(getResources().getString(R.string.rupee)+earningsDetailResponse.getWeekly().getReferralAmount());

				textViewWeekDeliveriesNumber.setText(""+earningsDetailResponse.getWeekly().getDeliveryCount());
				textViewWeekDeliveriesValue.setText(getResources().getString(R.string.rupee)+earningsDetailResponse.getWeekly().getDeliveryCharges());

				textViewMonthRidesNumber.setText(""+earningsDetailResponse.getMonthly().getRides());
				textViewMonthRidesValue.setText(getResources().getString(R.string.rupee)+earningsDetailResponse.getMonthly().getRidesAmount());

				textViewMonthDeliveriesNumber.setText(""+earningsDetailResponse.getMonthly().getDeliveryCount());
				textViewMonthDeliveriesValue.setText(getResources().getString(R.string.rupee)+earningsDetailResponse.getMonthly().getDeliveryCharges());

				textViewMonthReferralNumber.setText(""+earningsDetailResponse.getMonthly().getReferrals());
				textViewMonthReferralValue.setText(getResources().getString(R.string.rupee)+earningsDetailResponse.getMonthly().getReferralAmount());


				dateTimeValueFromWeek.setText(DateOperations.reverseDate(earningsDetailResponse.getWeekly().getStartDate()));
				dateTimeValueToWeek.setText(DateOperations.reverseDate(earningsDetailResponse.getWeekly().getEndDate()));
				dateTimeValueFromMonth.setText(DateOperations.reverseDate(earningsDetailResponse.getMonthly().getStartDate()));
				dateTimeValueToMonth.setText(DateOperations.reverseDate(earningsDetailResponse.getMonthly().getEndDate()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			ASSL.closeActivity(linearLayoutRoot);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.gc();
	}

	public void getLeaderboardActivityCall() {
		try {
			if (!((BaseFragmentActivity)activity).checkIfUserDataNull() && AppStatus.getInstance(activity).isOnline(activity)) {
                DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));
                RestClient.getApiServices().earningDetails(Data.userData.accessToken, Data.LOGIN_TYPE,
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

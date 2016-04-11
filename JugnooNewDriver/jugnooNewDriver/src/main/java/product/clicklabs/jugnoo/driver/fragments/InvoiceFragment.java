package product.clicklabs.jugnoo.driver.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import org.json.JSONObject;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.JSONParser;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.SplashNewActivity;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.LeaderboardActivityResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class InvoiceFragment extends Fragment {

	private LinearLayout linearLayoutRoot;

	private TextView textViewLastInvValue, textViewLastInvDate, textViewJugnooCmsnValue, textViewReferralValue,
			textViewTotalAmntValue, textViewOutstandingAmntValue, textViewManualAdjValue, textViewPhoneDeductionValue,
			textViewCancelSubsidyValue, textViewPaidByJugnooValue, textViewPaidUsingCstmrValue, textViewPaidByCstmrValue,
			textViewCurrentInvoiceDate, textViewUpcomingInvoiceDate;

	private View rootView;
	private FragmentActivity activity;
	LeaderboardActivityResponse leaderboardActivityResponse;

	@Override
	public void onStart() {
		super.onStart();
		FlurryAgent.init(activity, Data.FLURRY_KEY);
		FlurryAgent.onStartSession(activity, Data.FLURRY_KEY);
		FlurryAgent.onEvent(InvoiceFragment.class.getSimpleName() + " started");
	}

	@Override
	public void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(activity);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_invoice, container, false);


		activity = getActivity();

		linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
		try {
			if (linearLayoutRoot != null) {
				new ASSL(activity, linearLayoutRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		((TextView) rootView.findViewById(R.id.textViewUpcomingInvoice)).setTypeface(Data.latoRegular(activity), Typeface.BOLD);
		((TextView) rootView.findViewById(R.id.textViewCurrentInvoice)).setTypeface(Data.latoRegular(activity), Typeface.NORMAL);
		((TextView) rootView.findViewById(R.id.textViewPaidByCstmr)).setTypeface(Data.latoRegular(activity), Typeface.NORMAL);
		((TextView) rootView.findViewById(R.id.textViewPaidUsingCstmr)).setTypeface(Data.latoRegular(activity), Typeface.NORMAL);
		((TextView) rootView.findViewById(R.id.textViewPaidByJugnoo)).setTypeface(Data.latoRegular(activity), Typeface.NORMAL);
		((TextView) rootView.findViewById(R.id.textViewCancelSubsidy)).setTypeface(Data.latoRegular(activity), Typeface.NORMAL);
		((TextView) rootView.findViewById(R.id.textViewPhoneDeduction)).setTypeface(Data.latoRegular(activity), Typeface.NORMAL);
		((TextView) rootView.findViewById(R.id.textViewReferral)).setTypeface(Data.latoRegular(activity), Typeface.NORMAL);
		((TextView) rootView.findViewById(R.id.textViewManualAdj)).setTypeface(Data.latoRegular(activity), Typeface.NORMAL);
		((TextView) rootView.findViewById(R.id.textViewJugnooCmsn)).setTypeface(Data.latoRegular(activity), Typeface.NORMAL);
		((TextView) rootView.findViewById(R.id.textViewOutstandingAmnt)).setTypeface(Data.latoRegular(activity), Typeface.NORMAL);
		((TextView) rootView.findViewById(R.id.textViewTotalAmnt)).setTypeface(Data.latoRegular(activity), Typeface.BOLD);
//		((TextView) rootView.findViewById(R.id.textViewLastInv)).setTypeface(Data.latoRegular(activity), Typeface.BOLD);


		textViewUpcomingInvoiceDate = (TextView) rootView.findViewById(R.id.textViewUpcomingInvoiceDate);
		textViewUpcomingInvoiceDate.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
		textViewPaidByCstmrValue = (TextView) rootView.findViewById(R.id.textViewPaidByCstmrValue);
		textViewPaidByCstmrValue.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
		textViewPaidUsingCstmrValue = (TextView) rootView.findViewById(R.id.textViewPaidUsingCstmrValue);
		textViewPaidUsingCstmrValue.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
		textViewPaidByJugnooValue = (TextView) rootView.findViewById(R.id.textViewPaidByJugnooValue);
		textViewPaidByJugnooValue.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
		textViewCancelSubsidyValue = (TextView) rootView.findViewById(R.id.textViewCancelSubsidyValue);
		textViewCancelSubsidyValue.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
		textViewPhoneDeductionValue = (TextView) rootView.findViewById(R.id.textViewPhoneDeductionValue);
		textViewPhoneDeductionValue.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
		textViewReferralValue = (TextView) rootView.findViewById(R.id.textViewReferralValue);
		textViewReferralValue.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
		textViewManualAdjValue = (TextView) rootView.findViewById(R.id.textViewManualAdjValue);
		textViewManualAdjValue.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
		textViewJugnooCmsnValue = (TextView) rootView.findViewById(R.id.textViewJugnooCmsnValue);
		textViewJugnooCmsnValue.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
		textViewOutstandingAmntValue = (TextView) rootView.findViewById(R.id.textViewOutstandingAmntValue);
		textViewOutstandingAmntValue.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
		textViewTotalAmntValue = (TextView) rootView.findViewById(R.id.textViewTotalAmntValue);
		textViewTotalAmntValue.setTypeface(Data.latoRegular(activity));
		textViewLastInvDate = (TextView) rootView.findViewById(R.id.textViewLastInvDate);
		textViewLastInvDate.setTypeface(Data.latoRegular(activity));
//		textViewLastInvValue = (TextView) rootView.findViewById(R.id.textViewLastInvValue);
//		textViewLastInvValue.setTypeface(Data.latoRegular(activity));


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
			if (leaderboardActivityResponse != null) {
//				textViewTodayValue.setText("" + leaderboardActivityResponse.getNDownloads());
//				textViewWeekValue.setText("" + leaderboardActivityResponse.getNFirstRides());
//				textViewMonthValue.setText("" + leaderboardActivityResponse.getNMoneyEarned());
//				textViewDataEffective.setText(activity.getResources()
//						.getString(R.string.data_effective_format) + " " + leaderboardActivityResponse.getDate());

//						textViewLastInvValue.setText(getResources().getString(R.string.rupee) + " 321");
//						textViewLastInvDate.setText(getResources().getString(R.string.rupee) + " 321");
						textViewJugnooCmsnValue.setText(getResources().getString(R.string.rupee) + " 78");
						textViewReferralValue.setText(getResources().getString(R.string.rupee) + " 100");
						textViewTotalAmntValue.setText(getResources().getString(R.string.rupee) + " 988");
						textViewOutstandingAmntValue.setText(getResources().getString(R.string.rupee) + " 41");
						textViewManualAdjValue.setText(getResources().getString(R.string.rupee) + " 24");
						textViewPhoneDeductionValue.setText(getResources().getString(R.string.rupee) + " 69");
						textViewCancelSubsidyValue.setText(getResources().getString(R.string.rupee) + " 34");
						textViewPaidByJugnooValue.setText(getResources().getString(R.string.rupee) + " 441");
						textViewPaidUsingCstmrValue.setText(getResources().getString(R.string.rupee) + " 321");
						textViewPaidByCstmrValue.setText(getResources().getString(R.string.rupee) + " 132");
//						textViewCurrentInvoiceDate.setText(getResources().getString(R.string.rupee) + " 321");
//						textViewUpcomingInvoiceDate.setText(getResources().getString(R.string.rupee) + " 321");


			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ASSL.closeActivity(linearLayoutRoot);
		System.gc();
	}

	public void getLeaderboardActivityCall() {
		if (!HomeActivity.checkIfUserDataNull(activity) && AppStatus.getInstance(activity).isOnline(activity)) {
			DialogPopup.showLoadingDialog(activity, "Loading...");
			RestClient.getApiServices().leaderboardActivityServerCall(Data.userData.accessToken, Data.LOGIN_TYPE,
					new Callback<LeaderboardActivityResponse>() {
						@Override
						public void success(LeaderboardActivityResponse leaderboardActivityResponse, Response response) {
							DialogPopup.dismissLoadingDialog();
							try {
								String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
								JSONObject jObj;
								jObj = new JSONObject(jsonString);
								int flag = jObj.optInt("flag", ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
								String message = JSONParser.getServerMessage(jObj);
								if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag)) {
									if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
										InvoiceFragment.this.leaderboardActivityResponse = leaderboardActivityResponse;
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
	}


}

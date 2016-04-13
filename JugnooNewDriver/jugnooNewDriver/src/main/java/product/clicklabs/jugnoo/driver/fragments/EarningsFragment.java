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


public class EarningsFragment extends Fragment {

	private LinearLayout linearLayoutRoot;

	private TextView textViewTodayValue, textViewWeekValue, textViewMonthRides, textViewWeekValueReferral,
			textViewMonthValue, textViewTodayRides,textViewMonthValueReferral, textViewWeekRides, textViewTodayValueReferral;

	private View rootView;
    private FragmentActivity activity;
	LeaderboardActivityResponse leaderboardActivityResponse;

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
        rootView = inflater.inflate(R.layout.fragment_earnings, container, false);


        activity = getActivity();

		linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
		try {
			if(linearLayoutRoot != null) {
				new ASSL(activity, linearLayoutRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		((TextView)rootView.findViewById(R.id.textViewToday)).setTypeface(Data.latoLight(activity), Typeface.BOLD);
		((TextView)rootView.findViewById(R.id.textViewMonth)).setTypeface(Data.latoLight(activity), Typeface.BOLD);
		((TextView)rootView.findViewById(R.id.textViewWeek)).setTypeface(Data.latoLight(activity), Typeface.BOLD);

		textViewTodayValue = (TextView)rootView.findViewById(R.id.textViewTodayValue);
		textViewTodayValue.setTypeface(Data.latoRegular(activity));
		textViewMonthValue = (TextView)rootView.findViewById(R.id.textViewMonthValue);
		textViewMonthValue.setTypeface(Data.latoRegular(activity));
		textViewWeekValue = (TextView)rootView.findViewById(R.id.textViewWeekValue);
		textViewWeekValue.setTypeface(Data.latoRegular(activity));
		textViewTodayRides = (TextView)rootView.findViewById(R.id.textViewTodayRides);
		textViewTodayRides.setTypeface(Data.latoRegular(activity));
		textViewTodayValueReferral = (TextView)rootView.findViewById(R.id.textViewTodayValueReferral);
		textViewTodayValueReferral.setTypeface(Data.latoRegular(activity));
		textViewWeekRides = (TextView)rootView.findViewById(R.id.textViewWeekRides);
		textViewWeekRides.setTypeface(Data.latoRegular(activity));
		textViewWeekValueReferral = (TextView)rootView.findViewById(R.id.textViewWeekValueReferral);
		textViewWeekValueReferral.setTypeface(Data.latoRegular(activity));
		textViewMonthRides = (TextView)rootView.findViewById(R.id.textViewMonthRides);
		textViewMonthRides.setTypeface(Data.latoRegular(activity));
		textViewMonthValueReferral = (TextView)rootView.findViewById(R.id.textViewMonthValueReferral);
		textViewMonthValueReferral.setTypeface(Data.latoRegular(activity));

		getLeaderboardActivityCall();


		try {
		} catch(Exception e){
			e.printStackTrace();
		}

		update();

		return rootView;
	}


	public void update(){
		try{
			if(leaderboardActivityResponse != null){
				textViewTodayValue.setText(getResources().getString(R.string.rupee) + " 78");
				textViewWeekValue.setText(getResources().getString(R.string.rupee) + " 211");
				textViewMonthValue.setText(getResources().getString(R.string.rupee) + " 1253");
//				textViewDataEffective.setText(activity.getResources()
//						.getString(R.string.data_effective_format)+" "+ leaderboardActivityResponse.getDate());
			}
		} catch(Exception e){
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
		if(!HomeActivity.checkIfUserDataNull(activity) && AppStatus.getInstance(activity).isOnline(activity)) {
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
										EarningsFragment.this.leaderboardActivityResponse = leaderboardActivityResponse;
										update();
										Log.v("success at", "leaderboeard");
									}
									else{
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

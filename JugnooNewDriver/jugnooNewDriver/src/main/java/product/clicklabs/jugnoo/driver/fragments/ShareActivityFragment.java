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

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeUtil;
import product.clicklabs.jugnoo.driver.JSONParser;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.SplashNewActivity;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.LeaderboardActivityResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Log;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class ShareActivityFragment extends Fragment {

	private LinearLayout linearLayoutRoot;

	private TextView textViewNumberOfDownloadsValue, textViewNumberOfFirstRidesValue,
			textViewMoneyEarnedValue, textViewDataEffective;

	private View rootView;
    private FragmentActivity activity;
	LeaderboardActivityResponse leaderboardActivityResponse;

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(activity, Data.FLURRY_KEY);
        FlurryAgent.onStartSession(activity, Data.FLURRY_KEY);
        FlurryAgent.onEvent(ShareActivityFragment.class.getSimpleName() + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
        FlurryAgent.onEndSession(activity);
    }
	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_share_activity, container, false);


        activity = getActivity();

		linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
		try {
			if(linearLayoutRoot != null) {
				new ASSL(activity, linearLayoutRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		((TextView)rootView.findViewById(R.id.textViewNumberOfDownloads)).setTypeface(Data.latoLight(activity), Typeface.BOLD);
		((TextView)rootView.findViewById(R.id.textViewNumberOfFirstRides)).setTypeface(Data.latoLight(activity), Typeface.BOLD);
		((TextView)rootView.findViewById(R.id.textViewMoneyEarned)).setTypeface(Data.latoLight(activity), Typeface.BOLD);

		textViewNumberOfDownloadsValue = (TextView)rootView.findViewById(R.id.textViewNumberOfDownloadsValue);
		textViewNumberOfDownloadsValue.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
		textViewNumberOfFirstRidesValue = (TextView)rootView.findViewById(R.id.textViewNumberOfFirstRidesValue);
		textViewNumberOfFirstRidesValue.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
		textViewMoneyEarnedValue = (TextView)rootView.findViewById(R.id.textViewMoneyEarnedValue);
		textViewMoneyEarnedValue.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
		textViewDataEffective = (TextView)rootView.findViewById(R.id.textViewDataEffective);
		textViewDataEffective.setTypeface(Data.latoRegular(activity));

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
				textViewNumberOfDownloadsValue.setText(""+leaderboardActivityResponse.getNDownloads());
				textViewNumberOfFirstRidesValue.setText(""+leaderboardActivityResponse.getNFirstRides());
				textViewMoneyEarnedValue.setText(""+leaderboardActivityResponse.getNMoneyEarned());
				textViewDataEffective.setText(activity.getResources()
						.getString(R.string.data_effective_format)+" "+ leaderboardActivityResponse.getDate());
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
		if(!((BaseFragmentActivity)activity).checkIfUserDataNull() && AppStatus.getInstance(activity).isOnline(activity)) {
			DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));
			HashMap<String, String> params = new HashMap<>();
			params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
			params.put("login_type", Data.LOGIN_TYPE);
			HomeUtil.putDefaultParams(params);
			RestClient.getApiServices().leaderboardActivityServerCall(params,
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
										ShareActivityFragment.this.leaderboardActivityResponse = leaderboardActivityResponse;
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

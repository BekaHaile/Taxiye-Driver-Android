package product.clicklabs.jugnoo.driver.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.HomeUtil;
import product.clicklabs.jugnoo.driver.MultipleAccountsActivity;
import product.clicklabs.jugnoo.driver.MyApplication;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.RideDetailsNewActivity;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.DocInfo;
import product.clicklabs.jugnoo.driver.oldRegistration.OldOTPConfirmScreen;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.BookingHistoryResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.DocRequirementResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.LeaderboardActivityResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.FirebaseEvents;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Log;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class RideIssueFragment extends Fragment {



	RelativeLayout relative;

	TextView textViewTitle;
	ImageView imageViewBack;

	TextView textViewRegistration;
	EditText editTextMessage;
	Button buttonSubmitRequest;
	String accessToken;
	String engagementId;
	ScrollView scrollView;
	TextView textViewScroll;

	private View rootView;
    private RideDetailsNewActivity activity;


	public RideIssueFragment(){

	}

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(activity, Data.FLURRY_KEY);
        FlurryAgent.onStartSession(activity, Data.FLURRY_KEY);
        FlurryAgent.onEvent(RideIssueFragment.class.getSimpleName() + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
        FlurryAgent.onEndSession(activity);
    }
	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_ride_issue, container, false);


        activity = (RideDetailsNewActivity)getActivity();

		relative = (RelativeLayout) rootView.findViewById(R.id.relative);
		try {
			if(relative != null) {
				new ASSL(activity, relative, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle);
		textViewTitle.setTypeface(Fonts.mavenRegular(activity));
		imageViewBack = (ImageView) rootView.findViewById(R.id.imageViewBack);

		textViewRegistration = (TextView) rootView.findViewById(R.id.textViewRegistration);
		textViewRegistration.setTypeface(Fonts.mavenRegular(activity));
		editTextMessage = (EditText) rootView.findViewById(R.id.editTextMessage);
		editTextMessage.setTypeface(Fonts.mavenRegular(activity));

		buttonSubmitRequest = (Button) rootView.findViewById(R.id.buttonSubmitRequest);
		buttonSubmitRequest.setTypeface(Fonts.mavenRegular(activity));

		scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
		textViewScroll = (TextView) rootView.findViewById(R.id.textViewScroll);


		imageViewBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				MyApplication.getInstance().logEvent(FirebaseEvents.FARE_ISSUE+"_"+FirebaseEvents.BACK, null);
				performBackPressed();
			}
		});


		buttonSubmitRequest.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				FlurryEventLogger.event(FlurryEventNames.FARE_ISSUE_SUBMIT);
				accessToken = getArguments().getString("access_token");
				engagementId = getArguments().getString("engagement_id");
				String messageStr = editTextMessage.getText().toString().trim();
				if(messageStr.equalsIgnoreCase("")){
					DialogPopup.alertPopup(activity,"",activity.getResources().getString(R.string.pls_enter_some_msg));
				}else {
					MyApplication.getInstance().logEvent(FirebaseEvents.FARE_ISSUE+"_"+FirebaseEvents.SUBMIT, null);
					sendIssue(messageStr);
				}
			}
		});

		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		return rootView;
	}

	public void performBackPressed(){
		hideKeyboard(activity);
		activity.performBackPressed();
	}


    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}

	public void sendIssue(String message) {
		try {
			DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));
			HashMap<String, String> params = new HashMap<String, String>();
				params.put("support_feedback_text", message);
				params.put("engagement_id", engagementId);
				params.put("ticket_type", "1");
				params.put("access_token", accessToken);
				HomeUtil.putDefaultParams(params);
			RestClient.getApiServices().sendIssue(params, new Callback<DocRequirementResponse>() {
				@Override
				public void success(DocRequirementResponse docRequirementResponse, Response response) {
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						final JSONObject jObj;
						jObj = new JSONObject(jsonString);
						if (!jObj.isNull("error")) {
							String errorMessage = jObj.getString("error");
							if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
								HomeActivity.logoutUser(activity);
							}
						} else {
							DialogPopup.dismissLoadingDialog();
							int flag = jObj.getInt("flag");
							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								String message = jObj.optString("message","");
								final String date = jObj.optString("date", "");
								DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										getActivity().getSupportFragmentManager().popBackStack(RideIssueFragment.class.getName(), getFragmentManager().POP_BACK_STACK_INCLUSIVE);
										activity.setTicketState(date);
									}
								});
							}

						}
					} catch (Exception exception) {
						exception.printStackTrace();
						DialogPopup.dismissLoadingDialog();
					}
				}

				@Override
				public void failure(RetrofitError error) {
					Log.i("DocError", error.toString());
					DialogPopup.dismissLoadingDialog();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			DialogPopup.dismissLoadingDialog();
		}
	}

	public static void hideKeyboard(Context ctx) {
		InputMethodManager inputManager = (InputMethodManager) ctx
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		// check if no view has focus:
		View v = ((Activity) ctx).getCurrentFocus();
		if (v == null)
			return;

		inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}
}

package product.clicklabs.jugnoo.driver.fragments;

import android.app.Activity;
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

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.MultipleAccountsActivity;
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
		textViewTitle.setTypeface(Data.latoRegular(activity));
		imageViewBack = (ImageView) rootView.findViewById(R.id.imageViewBack);

		textViewRegistration = (TextView) rootView.findViewById(R.id.textViewRegistration);
		textViewRegistration.setTypeface(Data.latoLight(activity));
		editTextMessage = (EditText) rootView.findViewById(R.id.editTextMessage);
		editTextMessage.setTypeface(Data.latoLight(activity), Typeface.BOLD);

		buttonSubmitRequest = (Button) rootView.findViewById(R.id.buttonSubmitRequest);
		buttonSubmitRequest.setTypeface(Data.latoRegular(activity));

		scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
		textViewScroll = (TextView) rootView.findViewById(R.id.textViewScroll);


		imageViewBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});


		buttonSubmitRequest.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				accessToken = getArguments().getString("access_token");
				String messageStr = editTextMessage.getText().toString().trim();
				sendIssue(activity, messageStr);
			}
		});

		activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		return rootView;
	}

	public void performBackPressed(){
		activity.performBackPressed();
	}


    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}

	public void sendIssue(final Activity activity, String message) {
		try {
			RestClient.getApiServices().sendIssue(accessToken, message, new Callback<DocRequirementResponse>() {
				@Override
				public void success(DocRequirementResponse docRequirementResponse, Response response) {
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
							int flag = jObj.getInt("flag");
							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								String message = jObj.optString("message","");
								DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
									@Override
									public void onClick(View v) {

									}
								});
							}

						}
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}

				@Override
				public void failure(RetrofitError error) {
					Log.i("DocError", error.toString());
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

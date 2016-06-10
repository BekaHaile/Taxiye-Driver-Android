package product.clicklabs.jugnoo.driver.fragments;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import org.json.JSONObject;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.BookingHistoryResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.LeaderboardActivityResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class NotificationTipsFragment extends Fragment {



	LinearLayout relative;

	ProgressBar progressBar;
	TextView textViewInfo;
	WebView webview;

	private View rootView;
    private FragmentActivity activity;
	LeaderboardActivityResponse leaderboardActivityResponse;


	public NotificationTipsFragment(){

	}

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(activity, Data.FLURRY_KEY);
        FlurryAgent.onStartSession(activity, Data.FLURRY_KEY);
        FlurryAgent.onEvent(NotificationTipsFragment.class.getSimpleName() + " started");
    }

    @Override
    public void onStop() {
		super.onStop();
        FlurryAgent.onEndSession(activity);
    }
	

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_notification_tips, container, false);


        activity = getActivity();

		relative = (LinearLayout) rootView.findViewById(R.id.relative);
		try {
			if(relative != null) {
				new ASSL(activity, relative, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
		textViewInfo = (TextView) rootView.findViewById(R.id.textViewInfo);
		textViewInfo.setTypeface(Data.latoRegular(activity));
		webview = (WebView) rootView.findViewById(R.id.webview);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setWebChromeClient(new WebChromeClient());
		webview.getSettings().setDomStorageEnabled(true);
		webview.getSettings().setDatabaseEnabled(true);


		//enable Javascript
		webview.getSettings().setJavaScriptEnabled(true);
		textViewInfo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				getFareDetailsAsync(activity);
			}
		});

		getFareDetailsAsync(activity);



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
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

    @Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}

	public void openHelpData(String data, boolean errorOccured) {
		if (errorOccured) {
			textViewInfo.setVisibility(View.VISIBLE);
			textViewInfo.setText(data);
			webview.setVisibility(View.GONE);
		} else {
			textViewInfo.setVisibility(View.GONE);
			webview.setVisibility(View.VISIBLE);
			loadHTMLContent(data);
		}
	}

	public void loadHTMLContent(String data) {
		final String mimeType = "text/html";
		final String encoding = "UTF-8";
		webview.loadDataWithBaseURL("", data, mimeType, encoding, "");
	}



//	Retrofit


	public void getFareDetailsAsync(final Activity activity) {
		if (AppStatus.getInstance(activity).isOnline(activity)) {
			Configuration conf = getResources().getConfiguration();
			if (activity != null) {
				progressBar.setVisibility(View.VISIBLE);
				textViewInfo.setVisibility(View.GONE);
				webview.setVisibility(View.GONE);
				loadHTMLContent("");


				RestClient.getApiServices().getHelpSectionNew(8,conf.locale.toString(),
						new Callback<BookingHistoryResponse>() {

					@Override
					public void success(BookingHistoryResponse bookingHistoryResponse, Response response) {
						try {
							String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
							JSONObject jObj;
							jObj = new JSONObject(jsonString);
							if (!jObj.isNull("error")) {
								String errorMessage = jObj.getString("error");
								if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
									HomeActivity.logoutUser(activity);
								} else {
									openHelpData(getResources().getString(R.string.error_occured_tap_to_retry), true);
								}
							} else {
								String data = jObj.getString("data");
								openHelpData(data, false);
							}

						} catch (Exception exception) {
							exception.printStackTrace();
							openHelpData(getResources().getString(R.string.error_occured_tap_to_retry), true);
						}
						progressBar.setVisibility(View.GONE);
					}

					@Override
					public void failure(RetrofitError error) {
						progressBar.setVisibility(View.GONE);
						openHelpData(getResources().getString(R.string.error_occured_tap_to_retry), true);

					}
				});
			}
		} else {
			openHelpData("No internet connection. Tap to retry.", true);
		}
	}



}

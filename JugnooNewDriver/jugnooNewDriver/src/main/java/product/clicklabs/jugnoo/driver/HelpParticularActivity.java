package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.datastructure.HelpSection;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.BookingHistoryResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class HelpParticularActivity extends BaseFragmentActivity {


	LinearLayout relative;

	View backBtn;
	TextView title;
	ProgressBar progressBar;
	TextView textViewInfo;
	WebView webview;


	public static HelpSection helpSection = HelpSection.FARE_DETAILS;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help_particular);

		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(HelpParticularActivity.this, relative, 1134, 720, false);


		backBtn = findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Fonts.mavenRegular(getApplicationContext()));

		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		textViewInfo = (TextView) findViewById(R.id.textViewInfo);
		textViewInfo.setTypeface(Fonts.mavenRegular(getApplicationContext()));
		webview = (WebView) findViewById(R.id.webview);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setWebChromeClient(new WebChromeClient());
		webview.getSettings().setDomStorageEnabled(true);
		webview.getSettings().setDatabaseEnabled(true);


		//enable Javascript
		webview.getSettings().setJavaScriptEnabled(true);



		if (helpSection != null) {
			title.setText(helpSection.getName(HelpParticularActivity.this));
		}


		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});


		textViewInfo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				getFareDetailsAsync(HelpParticularActivity.this);
			}
		});

		getFareDetailsAsync(HelpParticularActivity.this);

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
			if (helpSection != null) {
				progressBar.setVisibility(View.VISIBLE);
				textViewInfo.setVisibility(View.GONE);
				webview.setVisibility(View.GONE);
				loadHTMLContent("");
				HashMap<String, String> params = new HashMap<>();
				params.put("section", helpSection.getOrdinal()+"");
				HomeUtil.putDefaultParams(params);

				RestClient.getApiServices().getHelpSection(params, new Callback<BookingHistoryResponse>() {


					@Override
					public void success(BookingHistoryResponse bookingHistoryResponse, Response response) {
						try {
							String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
							JSONObject jObj;
							jObj = new JSONObject(jsonString);
							if (!jObj.isNull("error")) {
								String errorMessage = jObj.getString("error");
								if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
									HomeActivity.logoutUser(activity, null);
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
			openHelpData(getString(R.string.no_internet), true);
		}
	}


	public void performBackPressed() {
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}

	@Override
	public void onBackPressed() {
		performBackPressed();
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		ASSL.closeActivity(relative);
		System.gc();
	}

}


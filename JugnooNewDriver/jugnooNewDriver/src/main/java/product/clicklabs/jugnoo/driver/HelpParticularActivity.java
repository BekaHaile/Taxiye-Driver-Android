package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;

import product.clicklabs.jugnoo.driver.datastructure.HelpSection;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.BookingHistoryResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class HelpParticularActivity extends FragmentActivity {


	LinearLayout relative;

	Button backBtn;
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


		backBtn = (Button) findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Data.latoRegular(getApplicationContext()));

		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		textViewInfo = (TextView) findViewById(R.id.textViewInfo);
		textViewInfo.setTypeface(Data.latoRegular(getApplicationContext()));
		webview = (WebView) findViewById(R.id.webview);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setWebChromeClient(new WebChromeClient());
		webview.getSettings().setDomStorageEnabled(true);
		webview.getSettings().setDatabaseEnabled(true);


		//enable Javascript
		webview.getSettings().setJavaScriptEnabled(true);

		//override the web client to open all links in the same webview
		webview.setWebViewClient(new MyWebViewClient());
		webview.setWebChromeClient(new MyWebChromeClient());


		if (helpSection != null) {
			title.setText(helpSection.getName());
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


	private class MyWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			Log.i("shouldOverrideUrlLoading", "url=" + url);
			return false;
		}

		@Override
		public void onLoadResource(WebView view, String url) {
			Log.i("onLoadResource", "url=" + url);
			super.onLoadResource(view, url);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			Log.i("onPageFinished", "url=" + url);
			super.onPageFinished(view, url);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			Log.i("onPageStarted", "url=" + url);
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
									String description, String failingUrl) {
			Log.e("onReceivedError", "url=" + failingUrl);
			super.onReceivedError(view, errorCode, description, failingUrl);
		}


		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler,
									   SslError error) {
			Log.e("onReceivedSslError", "error=" + error);
			handler.proceed();
		}
	}

	private class MyWebChromeClient extends WebChromeClient {

		//display alert message in Web View
		@Override
		public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
			Log.d("message", message);
			return false;
		}

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


				RestClient.getApiServices().getHelpSection(helpSection.getOrdinal(), new Callback<BookingHistoryResponse>() {


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
									openHelpData("Some error occured. Tap to retry.", true);
								}
							} else {
								String data = jObj.getString("data");
								openHelpData(data, false);
							}

						} catch (Exception exception) {
							exception.printStackTrace();
							openHelpData("Some error occured. Tap to retry.", true);
						}
						progressBar.setVisibility(View.GONE);
					}

					@Override
					public void failure(RetrofitError error) {
						progressBar.setVisibility(View.GONE);
						openHelpData("Some error occured. Tap to retry.", true);

					}
				});
			}
		} else {
			openHelpData("No internet connection. Tap to retry.", true);
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


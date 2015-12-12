package product.clicklabs.jugnoo;

import org.json.JSONObject;

import product.clicklabs.jugnoo.datastructure.HelpSection;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

public class HelpParticularActivity extends Activity{
	
	
	LinearLayout relative;
	
	Button backBtn;
	TextView title;
	ProgressBar progressBar;
	TextView textViewInfo;
	WebView webview;
	
	AsyncHttpClient fetchHelpDataClient;
	
	public static HelpSection helpSection = HelpSection.FARE_DETAILS;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help_particular);
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(HelpParticularActivity.this, relative, 1134, 720, false);
		
		
		backBtn = (Button) findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title); title.setTypeface(Data.latoRegular(getApplicationContext()));
		
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		textViewInfo = (TextView) findViewById(R.id.textViewInfo); textViewInfo.setTypeface(Data.latoRegular(getApplicationContext()));
		webview = (WebView) findViewById(R.id.webview);
		
		if(helpSection != null){
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
	
	public void loadHTMLContent(String data){
		final String mimeType = "text/html";
        final String encoding = "UTF-8";
        webview.loadDataWithBaseURL("", data, mimeType, encoding, "");
	}
	
	
	/**
	 * ASync for get rides from server
	 */
	public void getFareDetailsAsync(final Activity activity) {
		if(fetchHelpDataClient == null){
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				if(helpSection != null){
					progressBar.setVisibility(View.VISIBLE);
					textViewInfo.setVisibility(View.GONE);
					webview.setVisibility(View.GONE);
					loadHTMLContent("");
					
					Log.e("helpSection", "="+helpSection.getOrdinal() + " " + helpSection.getName());
					
					RequestParams params = new RequestParams();
					params.put("section", ""+helpSection.getOrdinal());
					
					fetchHelpDataClient = Data.getClient();
					fetchHelpDataClient.post(Data.SERVER_URL + "/get_information", params,
							new CustomAsyncHttpResponseHandler() {
							private JSONObject jObj;
		
								@Override
								public void onFailure(Throwable arg3) {
									Log.e("request fail", arg3.toString());
									progressBar.setVisibility(View.GONE);
									openHelpData("Some error occured. Tap to retry.", true);
								}
		
								@Override
								public void onSuccess(String response) {
									Log.i("Server response faq ", "response = " + response);
									try {
										jObj = new JSONObject(response);
										if(!jObj.isNull("error")){
											String errorMessage = jObj.getString("error");
											if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
												HomeActivity.logoutUser(activity);
											}
											else{
												openHelpData("Some error occured. Tap to retry.", true);
											}
										}
										else{
											String data = jObj.getString("data");
											openHelpData(data, false);
										}
									}  catch (Exception exception) {
										exception.printStackTrace();
										openHelpData("Some error occured. Tap to retry.", true);
									}
									progressBar.setVisibility(View.GONE);
								}
								
								@Override
								public void onFinish() {
									super.onFinish();
									fetchHelpDataClient = null;
								}
							});
				}
			}
			else {
				openHelpData("No internet connection. Tap to retry.", true);
			}
		}
	}
	
	
	public void performBackPressed(){
		try {
			if(fetchHelpDataClient != null){
				fetchHelpDataClient.cancelAllRequests(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		try {
			if(fetchHelpDataClient != null){
				fetchHelpDataClient.cancelAllRequests(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        ASSL.closeActivity(relative);
        System.gc();
	}

}


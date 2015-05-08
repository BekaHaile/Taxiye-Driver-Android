package product.clicklabs.jugnoo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.HelpSection;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.Fonts;
import rmn.androidscreenlibrary.ASSL;

public class HelpParticularActivity extends Activity{
	
	
	LinearLayout relative;
	
	ImageView imageViewBack;
	TextView textViewTitle;
	
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
		
		
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.latoRegular(this), Typeface.BOLD);
		
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		textViewInfo = (TextView) findViewById(R.id.textViewInfo); textViewInfo.setTypeface(Fonts.latoRegular(this));
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
        
		
		
		
		if(helpSection != null){
			textViewTitle.setText(helpSection.getName().toUpperCase());
		}
		
		
		imageViewBack.setOnClickListener(new View.OnClickListener() {
		
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
	    	 Log.i("shouldOverrideUrlLoading", "url="+url);
	         return false;
	     }
	     
	     @Override
	    public void onLoadResource(WebView view, String url) {
	    	 Log.i("onLoadResource", "url="+url);
	    	super.onLoadResource(view, url);
	    }
	     
	     @Override
	    public void onPageFinished(WebView view, String url) {
	    	 Log.i("onPageFinished", "url="+url);
	    	super.onPageFinished(view, url);
	    }
	     @Override
	    public void onPageStarted(WebView view, String url, Bitmap favicon) {
	    	 Log.i("onPageStarted", "url="+url);
	    	super.onPageStarted(view, url, favicon);
	    }
	     
	     @Override
	    public void onReceivedError(WebView view, int errorCode,
	    		String description, String failingUrl) {
	    	 Log.e("onReceivedError", "url="+failingUrl);
	    	super.onReceivedError(view, errorCode, description, failingUrl);
	    }
	     
	     
	     @Override
	    public void onReceivedSslError(WebView view, SslErrorHandler handler,
	    		SslError error) {
	    	 Log.e("onReceivedSslError", "error="+error);
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
					params.put("access_token", Data.userData.accessToken);
					params.put("section", ""+helpSection.getOrdinal());
					
					fetchHelpDataClient = Data.getClient();
					fetchHelpDataClient.post(Config.getServerUrl() + "/get_information", params,
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
										if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
											String data = jObj.getString("data");
											openHelpData(data, false);
										}
										else{
											openHelpData("Some error occured. Tap to retry.", true);
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


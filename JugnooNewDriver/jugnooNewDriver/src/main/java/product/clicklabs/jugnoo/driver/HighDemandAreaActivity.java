package product.clicklabs.jugnoo.driver;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;



import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.CustomAppLauncher;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Fonts;


public class HighDemandAreaActivity extends BaseFragmentActivity implements FlurryEventNames {
	
	LinearLayout relative;

	View backBtn;
	TextView title;

	ProgressBar progressBar;
	TextView textViewInfo;
	WebView webview;
	String url;
	String driverId;
	Shader textShader;

	@Override
	protected void onStart() {
		super.onStart();


	}

	@Override
	protected void onStop() {
		super.onStop();

	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_high_demand_areas);

		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(HighDemandAreaActivity.this, relative, 1134, 720, false);


		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		textViewInfo = (TextView) findViewById(R.id.textViewInfo);
		textViewInfo.setTypeface(Fonts.mavenRegular(this));

		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Fonts.mavenRegular(this));
//		textShader=new LinearGradient(0, 0, 0, 20,
//				new int[]{getResources().getColor(R.color.gradient_orange_v2), getResources().getColor(R.color.gradient_yellow_v2)},
//				new float[]{0, 1}, Shader.TileMode.CLAMP);
//		title.getPaint().setShader(textShader);

		try {
			Intent intent = getIntent();
			url = intent.getStringExtra("extras");
			if(intent.getStringExtra("driverId") != null) {
				driverId = intent.getStringExtra("driverId");
			}
			title.setText(intent.getStringExtra("title"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		backBtn = findViewById(R.id.backBtn);
		webview = (WebView) findViewById(R.id.webview);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setDomStorageEnabled(true);
		webview.getSettings().setDatabaseEnabled(true);
		webview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		webview.setWebViewClient(new MyWebViewClient1());

		String accessToken = JSONParser.getAccessTokenPair(this).first;
		if(driverId != null) {
			String finalUrl = url + "/" + driverId;
			webview.loadUrl(finalUrl);

			PackageManager pm = getPackageManager();
			try {
				PackageInfo pi = pm.getPackageInfo("com.google.android.webview", 0);
				Log.d("version name: ", pi.versionName);
				Log.d("version code: " , String.valueOf(pi.versionCode));



				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M && pi.versionCode < 302908300){
					// Do something for lollipop and above versions
					redirectToApp();
				} else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP && pi.versionCode < 298713200) {
					// Do something for lollipop and above versions
					redirectToApp();
				}

			} catch (PackageManager.NameNotFoundException e) {
				Log.e("notFound","Android System WebView is not found");
			}
		} else if(accessToken != null) {
			Log.e("URL", url + "?access_token=" + accessToken);
			String finalUrl = url+"?access_token="+accessToken;
			webview.loadUrl(finalUrl);

			PackageManager pm = getPackageManager();
			try {
				PackageInfo pi = pm.getPackageInfo("com.google.android.webview", 0);
				Log.d("version name: ", pi.versionName);
				Log.d("version code: " , String.valueOf(pi.versionCode));



				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M && pi.versionCode < 302908300){
					// Do something for lollipop and above versions
					redirectToApp();
				} else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP && pi.versionCode < 298713200) {
					// Do something for lollipop and above versions
					redirectToApp();
				}

			} catch (PackageManager.NameNotFoundException e) {
				Log.e("notFound","Android System WebView is not found");
			}
		}


		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				performbackPressed();
			}
		});
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			super.onActivityResult(requestCode, resultCode, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void performbackPressed(){
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}
	

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		performbackPressed();
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			ASSL.closeActivity(relative);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.gc();
	}


	boolean loadingFinished = true;
	boolean redirect = false;

	private class MyWebViewClient1 extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String urlNewString) {
			if (!loadingFinished) {
				redirect = true;
			}
			loadingFinished = false;
			view.loadUrl(urlNewString);
			return true;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			loadingFinished = false;
			//SHOW LOADING IF IT ISNT ALREADY VISIBLE
			progressBar.setVisibility(View.VISIBLE);
			//jugnooAnimation.start();
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			if(!redirect){
				loadingFinished = true;
			}

			if(loadingFinished && !redirect){
				//HIDE LOADING IT HAS FINISHED
				progressBar.setVisibility(View.GONE);
				//jugnooAnimation.stop();
			} else{
				redirect = false;
			}

		}
	}

	public void redirectToApp(){
		DialogPopup.alertPopupTwoButtonsWithListeners(HighDemandAreaActivity.this, "",
				getResources().getString(R.string.update_webview),
				getResources().getString(R.string.ok),
				getResources().getString(R.string.cancel),
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						CustomAppLauncher.launchApp(HighDemandAreaActivity.this, "com.google.android.webview");
					}
				}, new View.OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				}, false, false);
	}
}

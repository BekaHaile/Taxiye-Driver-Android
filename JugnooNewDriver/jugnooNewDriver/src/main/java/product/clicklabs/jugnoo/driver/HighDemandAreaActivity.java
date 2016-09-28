package product.clicklabs.jugnoo.driver;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.flurry.android.FlurryAgent;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Prefs;


public class HighDemandAreaActivity extends BaseFragmentActivity implements FlurryEventNames {
	
	LinearLayout relative;

	Button backBtn;
	TextView textViewTitle, title;

	ProgressBar progressBar;
	TextView textViewInfo;
	WebView webview;
	String url;
	Shader textShader;

	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.init(this, Data.FLURRY_KEY);
		FlurryAgent.onStartSession(this, Data.FLURRY_KEY);
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
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
		textViewInfo.setTypeface(Data.latoRegular(this));

		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Data.latoRegular(this));
//		textShader=new LinearGradient(0, 0, 0, 20,
//				new int[]{getResources().getColor(R.color.gradient_orange_v2), getResources().getColor(R.color.gradient_yellow_v2)},
//				new float[]{0, 1}, Shader.TileMode.CLAMP);
//		title.getPaint().setShader(textShader);

		try {
			Intent intent = getIntent();
			url = intent.getStringExtra("extras");
		} catch (Exception e) {
			e.printStackTrace();
		}

		backBtn = (Button) findViewById(R.id.backBtn);
		webview = (WebView) findViewById(R.id.webview);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setDomStorageEnabled(true);
		webview.getSettings().setDatabaseEnabled(true);
		webview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		webview.setWebViewClient(new MyWebViewClient1());

		if(Database2.getInstance(this).getDLDAccessToken() != null) {
			Log.e("URL", url+"?access_token="+Database2.getInstance(this).getDLDAccessToken());
			webview.loadUrl(url+"?access_token="+Database2.getInstance(this).getDLDAccessToken());
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

}

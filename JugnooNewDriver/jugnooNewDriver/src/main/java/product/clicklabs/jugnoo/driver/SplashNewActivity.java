package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.DriverDebugOpenMode;
import product.clicklabs.jugnoo.driver.datastructure.PendingAPICall;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.CustomAppLauncher;
import product.clicklabs.jugnoo.driver.utils.DeviceTokenGenerator;
import product.clicklabs.jugnoo.driver.utils.DeviceUniqueID;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.HttpRequester;
import product.clicklabs.jugnoo.driver.utils.IDeviceTokenReceiver;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import rmn.androidscreenlibrary.ASSL;

public class SplashNewActivity extends Activity implements LocationUpdate, FlurryEventNames{
	
	LinearLayout relative;
	
	ImageView imageViewJugnooLogo;
	
	RelativeLayout jugnooTextImgRl;
	ImageView jugnooTextImg, jugnooTextImg2;
	
	ProgressBar progressBar1;
	
	Button buttonLogin, buttonRegister;
	
	boolean loginDataFetched = false, loginFailed = false;
	

	// *****************************Used for flurry work***************//
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.init(this, Data.FLURRY_KEY);
		FlurryAgent.onStartSession(this, Data.FLURRY_KEY);
		FlurryAgent.onEvent("Splash started");
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
	
	
	public static void initializeServerURL(Context context){
		SharedPreferences preferences = context.getSharedPreferences(Data.SETTINGS_SHARED_PREF_NAME, 0);
		String link = preferences.getString(Data.SP_SERVER_LINK, Data.DEFAULT_SERVER_URL);
		
		Data.SERVER_URL = Data.DEFAULT_SERVER_URL;
		
		if(link.equalsIgnoreCase(Data.TRIAL_SERVER_URL)){
			Data.SERVER_URL = Data.TRIAL_SERVER_URL.substring(0, Data.TRIAL_SERVER_URL.length()-4) + Database2.getInstance(context).getSalesPortNumber();
			Data.FLURRY_KEY = "STATIC_FLURRY_KEY";
		}
		else if(link.equalsIgnoreCase(Data.DEV_SERVER_URL)){
			Data.SERVER_URL = Data.DEV_SERVER_URL.substring(0, Data.DEV_SERVER_URL.length()-4) + Database2.getInstance(context).getDevPortNumber();
			Data.FLURRY_KEY = "STATIC_FLURRY_KEY";
		}
        else if(link.equalsIgnoreCase(Data.DEV_1_SERVER_URL)){
            Data.SERVER_URL = Data.DEV_1_SERVER_URL;
            Data.FLURRY_KEY = "STATIC_FLURRY_KEY";
        }
        else if(link.equalsIgnoreCase(Data.DEV_2_SERVER_URL)){
            Data.SERVER_URL = Data.DEV_2_SERVER_URL;
            Data.FLURRY_KEY = "STATIC_FLURRY_KEY";
        }
        else if(link.equalsIgnoreCase(Data.DEV_3_SERVER_URL)){
            Data.SERVER_URL = Data.DEV_3_SERVER_URL;
            Data.FLURRY_KEY ="STATIC_FLURRY_KEY";
        }
		else{
			Data.SERVER_URL = Data.LIVE_SERVER_URL.substring(0, Data.LIVE_SERVER_URL.length()-4) + Database2.getInstance(context).getLivePortNumber();
			Data.FLURRY_KEY = Data.STATIC_FLURRY_KEY;
		}
		Log.e("Data.SERVER_URL", "="+Data.SERVER_URL);
		RestClient.setupRestClient();
		DriverLocationUpdateService.updateServerData(context);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());
		
//		SharedPreferences preferences = getSharedPreferences(Data.SETTINGS_SHARED_PREF_NAME, 0);
//		String languageSelected = preferences.getString(Data.LANGUAGE_SELECTED, "default");
//		if(!"default".equalsIgnoreCase(languageSelected)){
//			Locale locale = new Locale(languageSelected); 
//		    Locale.setDefault(locale);
//		    Configuration config = new Configuration();
//		    config.locale = locale;
//		    getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
//		}
		
		
		initializeServerURL(this);
		
		FlurryAgent.init(this, Data.FLURRY_KEY);
		
		
//		Locale locale = new Locale("en");
//	    Locale.setDefault(locale);
//	    Configuration config = new Configuration();
//	    config.locale = locale;
//	    getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

//        String language = Locale.getDefault().getLanguage()       ;//---> en
//        String iso3Language = Locale.getDefault().getISO3Language()   ;//---> eng
//        String country = Locale.getDefault().getCountry()        ;//---> US
//        String iso3Country = Locale.getDefault().getISO3Country()    ;//---> USA
//        String displayCountry = Locale.getDefault().getDisplayCountry(); //---> United States
//        String displayName = Locale.getDefault().getDisplayName() ;   //---> English (United States)
//        String toString = Locale.getDefault().toString()    ;      //---> en_US
//        String displayLanguage = Locale.getDefault().getDisplayLanguage();//---> English
//
//        String localeInfo = language + " " + iso3Language + " " + country + " " + iso3Country + " " + displayCountry + " " + displayName + " " + toString + " " + displayLanguage;
//
//        Log.e("Locale info", "="+localeInfo);



		setContentView(R.layout.activity_splash_new);

		Data.locationFetcher = null;


		loginDataFetched = false;
		loginFailed = false;
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(SplashNewActivity.this, relative, 1134, 720, false);


		imageViewJugnooLogo = (ImageView) findViewById(R.id.imageViewJugnooLogo);
		
		jugnooTextImgRl = (RelativeLayout) findViewById(R.id.jugnooTextImgRl);
		jugnooTextImg = (ImageView) findViewById(R.id.jugnooTextImg);
		jugnooTextImg2 = (ImageView) findViewById(R.id.jugnooTextImg2);
		jugnooTextImgRl.setVisibility(View.GONE);
		
		
		progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
		progressBar1.setVisibility(View.GONE);
		
		buttonLogin = (Button) findViewById(R.id.buttonLogin); buttonLogin.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
		buttonRegister = (Button) findViewById(R.id.buttonRegister); buttonRegister.setTypeface(Data.latoRegular(getApplicationContext()), Typeface.BOLD);
		
		buttonLogin.setVisibility(View.GONE);
		buttonRegister.setVisibility(View.GONE);
		
		
		buttonLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(SplashNewActivity.this, SplashLogin.class));
				finish();
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
				FlurryEventLogger.event(LOGIN);
			}
		});
		
		buttonRegister.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(SplashNewActivity.this, RegisterScreen.class));
				finish();
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});
		
		jugnooTextImg.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				confirmDebugPasswordPopup(SplashNewActivity.this, DriverDebugOpenMode.DEBUG);
				FlurryEventLogger.debugPressed("no_token");
				return false;
			}
		});
		
		jugnooTextImg2.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				confirmDebugPasswordPopup(SplashNewActivity.this, DriverDebugOpenMode.REGISTER);
				FlurryEventLogger.debugPressed("driver_register");
				return false;
			}
		});
		
		
		Data.generateKeyHash(SplashNewActivity.this);
		
		
		try {																						// to get AppVersion, OS version, country code and device name
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			Data.appVersion = pInfo.versionCode;
			Log.i("appVersion", Data.appVersion + "..");
			Data.osVersion = android.os.Build.VERSION.RELEASE;
			Log.i("osVersion", Data.osVersion + "..");
			Data.country = getApplicationContext().getResources().getConfiguration().locale.getDisplayCountry(Locale.getDefault());
			Log.i("countryCode", Data.country + "..");
			Data.deviceName = (android.os.Build.MANUFACTURER + android.os.Build.MODEL).toString();
			Log.i("deviceName", Data.deviceName + "..");

			Data.uniqueDeviceId = DeviceUniqueID.getUniqueId(this);
			Log.i("uniqueDeviceId", Data.uniqueDeviceId);
		} catch (Exception e) {
			Log.e("error in fetching appversion and gcm key", ".." + e.toString());
		}
		

		noNetFirstTime = false;
		noNetSecondTime = false;
	    
	    
	    
	    
	    
		if(getIntent().hasExtra("no_anim")){
			imageViewJugnooLogo.clearAnimation();
			jugnooTextImgRl.setVisibility(View.VISIBLE);
			noNetFirstTime = true;
			getDeviceToken();
		}
		else{
			Animation animation = new AlphaAnimation(0, 1);
			animation.setFillAfter(false);
			animation.setDuration(1000);
			animation.setInterpolator(new AccelerateDecelerateInterpolator());
			animation.setAnimationListener(new ShowAnimListener());
			imageViewJugnooLogo.startAnimation(animation);
		}





		relative.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!loginDataFetched){
					noNetFirstTime = false;
					noNetSecondTime = false;
					getDeviceToken();
				}
			}
		});


//		boolean installed = Utils.isAppInstalled(this, Data.GADDAR_JUGNOO_APP);
//		if(installed){
//			DialogPopup.alertPopup(this, "", Data.GADDAR_JUGNOO_APP + " installed: " + installed);
//
//		}
//
//		boolean installede = Utils.isAppInstalled(this, Data.UBER_APP);
//		if(installede){
//			DialogPopup.alertPopup(this, "", Data.UBER_APP + " installed: " + installed);
//
//		}
//
//		boolean installedee = Utils.olaInstall(this);
//		if(installedee){
//			DialogPopup.alertPopup(this, "", Data.UBER_APP + " olaaaaaa: " + installed);
//
//		}

	}
	
	public void getDeviceToken(){
	    progressBar1.setVisibility(View.VISIBLE);
		new DeviceTokenGenerator(SplashNewActivity.this).generateDeviceToken(SplashNewActivity.this, new IDeviceTokenReceiver() {

			@Override
			public void deviceTokenReceived(final String regId) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Data.deviceToken = regId;
						Log.e("deviceToken in IDeviceTokenReceiver", Data.deviceToken + "..");
						progressBar1.setVisibility(View.GONE);
						pushAPIs(SplashNewActivity.this);
					}
				});

			}
		});

	}


	
	
	@Override
	protected void onResume() {
		try {
			if(Data.locationFetcher == null){
				Data.locationFetcher = new LocationFetcher(this, 1000, 1);
			}
			Data.locationFetcher.connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		if(resp != ConnectionResult.SUCCESS){
			Log.e("Google Play Service Error ","="+resp);
			DialogPopup.showGooglePlayErrorAlert(SplashNewActivity.this);
		}
		else{
			DialogPopup.showLocationSettingsAlert(SplashNewActivity.this);
		}
		
		super.onResume();
	}
	
	
	
	@Override
	protected void onPause() {
		Database2.getInstance(this).checkStartPendingApisService(this);
		try{
			Data.locationFetcher.destroy();
		} catch(Exception e){
			e.printStackTrace();
		}
		super.onPause();
		Database2.getInstance(this).close();
	}
	
	
	
	
	
	boolean noNetFirstTime = false, noNetSecondTime = false;
	
	Handler checkNetHandler = new Handler();
	Runnable checkNetRunnable = new Runnable() {
		
		@Override
		public void run() {
			
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					
					if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
						noNetSecondTime = false;
					    pushAPIs(SplashNewActivity.this);
					    FlurryEventLogger.appStarted(Data.deviceToken);
					}
					else{
						DialogPopup.alertPopup(SplashNewActivity.this, "", Data.CHECK_INTERNET_MSG);
						noNetSecondTime = true;
					}
					
				}
			});
			
		}
	};
	
	
	public void stopPendingAPIs(){
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				progressBar1.setVisibility(View.GONE);
				callFirstAttempt();
			}
		});
	}
	
	
    public Thread pushApiThread;
    public void pushAPIs(final Context context){
    	boolean mockLocationEnabled = Utils.mockLocationEnabled(this);
		if(mockLocationEnabled){
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					DialogPopup.alertPopupWithListener(SplashNewActivity.this, "", "Disable mock location first", new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							startActivity(new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
							finish();
						}
					});
				}
			});
		}
		else{
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					progressBar1.setVisibility(View.VISIBLE);
				}
			});
	    	stopService(new Intent(context, PushPendingCallsService.class));
	    	stopPushApiThread();
	    	try{
		    	pushApiThread = new Thread(new Runnable() {
					
					@Override
					public void run() {
						ArrayList<PendingAPICall> pendingAPICalls = Database2.getInstance(context).getAllPendingAPICalls();
						for(PendingAPICall pendingAPICall : pendingAPICalls){
							Log.e("pendingAPICall", "="+pendingAPICall);
							startAPI(context, pendingAPICall);
						}
						
						int pendingApisCount = Database2.getInstance(context).getAllPendingAPICallsCount();
						if(pendingApisCount > 0){
							pushAPIs(context);
						}
						else{
							stopPendingAPIs();
						}
					}
				});
		    	pushApiThread.start();
	    	} catch(Exception e){
	    		e.printStackTrace();
	    	}
		}
    }
    
    public void stopPushApiThread(){
    	try{
    		if(pushApiThread != null){
    			pushApiThread.interrupt();
    		}
    	} catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
	public void startAPI(Context context, PendingAPICall pendingAPICall) {
		if (AppStatus.getInstance(context).isOnline(context)) {
			HttpRequester simpleJSONParser = new HttpRequester();
			String result = simpleJSONParser.getJSONFromUrlParams(pendingAPICall.url, pendingAPICall.nameValuePairs);
			Log.e("result in pendingAPICall ", "=" + pendingAPICall + " and result = "+ result);
			if(result.contains(HttpRequester.SERVER_TIMEOUT)){
				
			}
			else{
				Database2.getInstance(context).deletePendingAPICall(pendingAPICall.id);
			}
		}
	}
	
	
	class ShowAnimListener implements AnimationListener{
		
		public ShowAnimListener(){
		}
		
		@Override
		public void onAnimationStart(Animation animation) {
			Log.i("onAnimationStart", "onAnimationStart");
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			Log.i("onAnimationStart", "onAnimationStart");
			imageViewJugnooLogo.clearAnimation();

			jugnooTextImgRl.setVisibility(View.VISIBLE);
			
			noNetFirstTime = true;
			getDeviceToken();
			
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}
		
	}
	
	
	
	public void callFirstAttempt(){
		runOnUiThread(new Runnable() {
		@Override
		public void run() {
			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
				noNetFirstTime = false;
			    accessTokenLogin(SplashNewActivity.this);

			}
			else{
				DialogPopup.alertPopup(SplashNewActivity.this, "", Data.CHECK_INTERNET_MSG);
			}
		}
		});
	}
	
	

//	Retrofit


	public void accessTokenLogin(final Activity activity) {

		Pair<String, String> accPair = JSONParser.getAccessTokenPair(activity);

		if(!"".equalsIgnoreCase(accPair.first)){
			buttonLogin.setVisibility(View.GONE);
			buttonRegister.setVisibility(View.GONE);
			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

				DialogPopup.showLoadingDialog(activity, "Loading...");

				if(Data.locationFetcher != null){
					Data.latitude = Data.locationFetcher.getLatitude();
					Data.longitude = Data.locationFetcher.getLongitude();
				}
				HashMap<String, String> params = new HashMap<String, String>();

//				RequestParams params = new RequestParams();
				params.put("access_token", accPair.first);
				params.put("access_token", accPair.first);
				params.put("device_token", Data.deviceToken);


				final String serviceRestartOnReboot = Database2.getInstance(activity).getDriverServiceRun();
				if(Database2.NO.equalsIgnoreCase(serviceRestartOnReboot)){
					params.put("latitude", "0");
					params.put("longitude", "0");
				}
				else{
					params.put("latitude", ""+Data.latitude);
					params.put("longitude", ""+Data.longitude);
				}


				params.put("app_version", ""+Data.appVersion);
				params.put("device_type", Data.DEVICE_TYPE);
				params.put("unique_device_id", Data.uniqueDeviceId);
				params.put("is_access_token_new", "1");
				params.put("client_id", Data.CLIENT_ID);
				params.put("login_type", Data.LOGIN_TYPE);

				if(Utils.isAppInstalled(activity, Data.GADDAR_JUGNOO_APP)){
					params.put("auto_n_cab_installed", "1");
				}
				else{
					params.put("auto_n_cab_installed", "0");
				}

				if(Utils.isAppInstalled(activity, Data.UBER_APP)){
					params.put("uber_installed", "1");
				}
				else{
					params.put("uber_installed", "0");
				}


				if(Utils.olaInstall(activity)){
					params.put("ola_installed", "1");
				}
				else{
					params.put("ola_installed", "0");
				}


				if(Utils.isDeviceRooted()){
					params.put("device_rooted", "1");
				}
				else{
					params.put("device_rooted", "0");
				}



				RestClient.getApiServices().accessTokenLoginRetro(params, new Callback<RegisterScreenResponse>() {
					@Override
					public void success(RegisterScreenResponse registerScreenResponse, Response response) {
						try {
							String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
							JSONObject jObj;
							jObj = new JSONObject(jsonString);
							int flag = jObj.getInt("flag");
							String message = JSONParser.getServerMessage(jObj);

							if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag)){
								if(ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag){
									DialogPopup.alertPopup(activity, "", message);
									DialogPopup.dismissLoadingDialog();
								}
								else if(ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag){
									DialogPopup.alertPopup(activity, "", message);
									DialogPopup.dismissLoadingDialog();
								}
								else if(ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag){
									if(!SplashNewActivity.checkIfUpdate(jObj.getJSONObject("login"), activity)){
										new AccessTokenDataParseAsync(activity, jsonString, message).execute();
									}
									else{
										DialogPopup.dismissLoadingDialog();
									}
								}
								else{
									DialogPopup.alertPopup(activity, "", message);
									DialogPopup.dismissLoadingDialog();
								}
							}
							else{
								DialogPopup.dismissLoadingDialog();
							}

						}  catch (Exception exception) {
							exception.printStackTrace();
							DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							DialogPopup.dismissLoadingDialog();
						}
					}

					@Override
					public void failure(RetrofitError error) {

						DialogPopup.dismissLoadingDialog();
						DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);

					}
				});



			}
			else {
				DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			}
		}
		else{
			buttonLogin.setVisibility(View.VISIBLE);
		}

	}
	
	class AccessTokenDataParseAsync extends AsyncTask<String, Integer, String>{
		
		Activity activity;
		String response, accessToken, message;
		
		public AccessTokenDataParseAsync(Activity activity, String response, String message){
			this.activity = activity;
			this.response = response;
            this.message = message;
		}
		
		@Override
		protected String doInBackground(String... params) {
			try {
				String resp = new JSONParser().parseAccessTokenLoginData(activity, response);
				return resp;
			} catch (Exception e) {
				e.printStackTrace();
				return HttpRequester.SERVER_TIMEOUT;
			}
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if(result.contains(HttpRequester.SERVER_TIMEOUT)){
				loginDataFetched = false;
				DialogPopup.alertPopup(activity, "", message);
			}
			else{

				noNetFirstTime = false;
				noNetSecondTime = false;
				
				loginDataFetched = true;
				
			}
			

			DialogPopup.dismissLoadingDialog();
		}
		
	}
	
	
	public static boolean checkIfUpdate(JSONObject jObj, Activity activity) throws Exception{
//		"popup": {
//	        "title": "Update Version",
//	        "text": "Update app with new version!",
//	        "cur_version": 116,			// could be used for local check
//	        "is_force": 1				// 1 for forced, 0 for not forced
//	}
		if(!jObj.isNull("popup")){
			try{
				JSONObject jupdatePopupInfo = jObj.getJSONObject("popup"); 
				String title = jupdatePopupInfo.getString("title");
				String text = jupdatePopupInfo.getString("text");
				int currentVersion = jupdatePopupInfo.getInt("cur_version");
				int isForce = jupdatePopupInfo.getInt("is_force");
				
				if(Data.appVersion >= currentVersion){
					return false;
				}
				else{
					SplashNewActivity.appUpdatePopup(title, text, isForce, activity);
					if(isForce == 1){
						return true;
					}
					else{
						return false;
					}
				}
			} catch(Exception e){
				return false;
			}
		}
		else{
			return false;
		}
	}
	
	
	
	/**
	 * Displays appUpdatePopup dialog
	 */
	public static void appUpdatePopup(String title, String message, final int isForced, final Activity activity) {
		try {

			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_custom_two_buttons);

			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, false);
			
			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			
			
			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.latoRegular(activity));
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.latoRegular(activity));
			textHead.setVisibility(View.VISIBLE);

			textMessage.setMovementMethod(new ScrollingMovementMethod());
			textMessage.setMaxHeight((int)(800.0f*ASSL.Yscale()));
			
			textMessage.setText(message);
			
			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.latoRegular(activity));
			btnOk.setText("Update");
			
			Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel); btnCancel.setTypeface(Data.latoRegular(activity));
			btnCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
					if(isForced == 1){
						activity.finish();
					}
				}
			});
			
			
			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse("market://details?id=product.clicklabs.jugnoo.driver"));
					activity.startActivity(intent);
					activity.finish();
				}
				
			});

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static void sendToCustomerAppPopup(String title, String message, final Activity activity) {
		try {

			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_customer_app);

			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, false);
			
			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			
			
			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.latoRegular(activity));
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.latoRegular(activity));

			textMessage.setMovementMethod(new ScrollingMovementMethod());
			textMessage.setMaxHeight((int)(800.0f*ASSL.Yscale()));
			
			textHead.setText(title);
			textMessage.setText(message);
			
			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.latoRegular(activity));
			
			frameLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
				}
			});
			
			dialog.findViewById(R.id.innerRl).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
				}
			});
			
			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
					CustomAppLauncher.launchApp(activity, "product.clicklabs.jugnoo");
					activity.finish();
				}
			});
			

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		if(hasFocus && noNetFirstTime){
			noNetFirstTime = false;
			checkNetHandler.postDelayed(checkNetRunnable, 4000);
		}
		else if(hasFocus && noNetSecondTime){
			noNetSecondTime = false;
			finish();
		}
		else if(hasFocus && loginDataFetched){
			loginDataFetched = false;
			startActivity(new Intent(SplashNewActivity.this, HomeActivity.class));
			finish();
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
		}
		else if(hasFocus && loginFailed){
			loginFailed = false;
			startActivity(new Intent(SplashNewActivity.this, SplashLogin.class));
			finish();
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
		}
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}
	
	
	
	
	
	
	
	
		public void confirmDebugPasswordPopup(final Activity activity, final DriverDebugOpenMode flag){

			try {
				final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
				dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
				dialog.setContentView(R.layout.dialog_edittext);

				FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
				new ASSL(activity, frameLayout, 1134, 720, true);
				
				WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
				layoutParams.dimAmount = 0.6f;
				dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				dialog.setCancelable(true);
				dialog.setCanceledOnTouchOutside(true);
				
				
				TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.latoRegular(activity));
				TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.latoRegular(activity));
				final EditText etCode = (EditText) dialog.findViewById(R.id.etCode); etCode.setTypeface(Data.latoRegular(activity));
				
				
				if(DriverDebugOpenMode.DEBUG == flag){
					textHead.setText("Confirm Debug Password");
				}
				else if(DriverDebugOpenMode.REGISTER == flag){
					textHead.setText("Confirm Register Password");
				}
				
				textMessage.setText("Please enter password to continue.");
				
				textMessage.setVisibility(View.GONE);
				
				
				final Button btnConfirm = (Button) dialog.findViewById(R.id.btnConfirm); btnConfirm.setTypeface(Data.latoRegular(activity));
				
				btnConfirm.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						String code = etCode.getText().toString().trim();
						if("".equalsIgnoreCase(code)){
							etCode.requestFocus();
							etCode.setError("Code can't be empty.");
						}
						else{
							if(DriverDebugOpenMode.DEBUG == flag){
								if(Data.DEBUG_PASSWORD.equalsIgnoreCase(code)){
									dialog.dismiss();
									changeServerLinkPopup(activity);
								}
                                else if(Data.DEBUG_PASSWORD_TEST.equalsIgnoreCase(code)){
                                    dialog.dismiss();

                                    DialogPopup.alertPopupThreeButtonsWithListeners(activity, "", "Current server is " + Data.SERVER_URL + "\n change to:",
                                        "DEV_1(8013)", "DEV_2(8014)", "DEV_3(8015)",
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                SharedPreferences preferences = activity.getSharedPreferences(Data.SETTINGS_SHARED_PREF_NAME, 0);
                                                SharedPreferences.Editor editor = preferences.edit();
                                                editor.putString(Data.SP_SERVER_LINK, Data.DEV_1_SERVER_URL);
                                                editor.commit();

                                                initializeServerURL(activity);
                                            }
                                        },
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                SharedPreferences preferences = activity.getSharedPreferences(Data.SETTINGS_SHARED_PREF_NAME, 0);
                                                SharedPreferences.Editor editor = preferences.edit();
                                                editor.putString(Data.SP_SERVER_LINK, Data.DEV_2_SERVER_URL);
                                                editor.commit();

                                                initializeServerURL(activity);
                                            }
                                        },
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                SharedPreferences preferences = activity.getSharedPreferences(Data.SETTINGS_SHARED_PREF_NAME, 0);
                                                SharedPreferences.Editor editor = preferences.edit();
                                                editor.putString(Data.SP_SERVER_LINK, Data.DEV_3_SERVER_URL);
                                                editor.commit();

                                                initializeServerURL(activity);
                                            }
                                        }, true, false);

                                }
								else{
									etCode.requestFocus();
									etCode.setError("Code not matched.");
								}
							}
							else if(DriverDebugOpenMode.REGISTER == flag){
								if(Data.REGISTER_PASSWORD.equalsIgnoreCase(code)){
									dialog.dismiss();
									buttonRegister.setVisibility(View.VISIBLE);
								}
								else{
									etCode.requestFocus();
									etCode.setError("Code not matched.");
								}
							}
						}
					}
					
				});
				
				
				etCode.setOnEditorActionListener(new OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
						int result = actionId & EditorInfo.IME_MASK_ACTION;
						switch (result) {
							case EditorInfo.IME_ACTION_DONE:
								btnConfirm.performClick();
							break;

							case EditorInfo.IME_ACTION_NEXT:
							break;

							default:
						}
						return true;
					}
				});
				

				dialog.findViewById(R.id.rl1).setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
					}
				});
				
				
				dialog.findViewById(R.id.rv).setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				
				dialog.show();

				dialog.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		}
	
	
		void changeServerLinkPopup(final Activity activity) {
				try {
					final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
					dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
					dialog.setContentView(R.layout.dialog_custom_three_buttons);

					FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
					new ASSL(activity, frameLayout, 1134, 720, true);
					
					WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
					layoutParams.dimAmount = 0.6f;
					dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
					dialog.setCancelable(true);
					dialog.setCanceledOnTouchOutside(true);
					
					
					
					TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
					TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Data.latoRegular(activity));
					
					
					SharedPreferences preferences = activity.getSharedPreferences(Data.SETTINGS_SHARED_PREF_NAME, 0);
					String link = preferences.getString(Data.SP_SERVER_LINK, Data.DEFAULT_SERVER_URL);
					
					if(link.equalsIgnoreCase(Data.TRIAL_SERVER_URL)){
						textMessage.setText("Current server is SALES.\nChange to:");
					}
					else if(link.equalsIgnoreCase(Data.LIVE_SERVER_URL)){
						textMessage.setText("Current server is LIVE.\nChange to:");
					}
					else if(link.equalsIgnoreCase(Data.DEV_SERVER_URL)){
						textMessage.setText("Current server is DEV.\nChange to:");
					}
					
					
					
					Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.latoRegular(activity));
					btnOk.setText("LIVE");
					
					Button btnNeutral = (Button) dialog.findViewById(R.id.btnNeutral); btnNeutral.setTypeface(Data.latoRegular(activity));
					btnNeutral.setText("DEV");
					
					Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel); btnCancel.setTypeface(Data.latoRegular(activity));
					btnCancel.setText("SALES");
					
					
					btnOk.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							SharedPreferences preferences = activity.getSharedPreferences(Data.SETTINGS_SHARED_PREF_NAME, 0);
							SharedPreferences.Editor editor = preferences.edit();
							editor.putString(Data.SP_SERVER_LINK, Data.LIVE_SERVER_URL);
							editor.commit();
							
							initializeServerURL(activity);
							
							dialog.dismiss();
						}
					});
					
					btnNeutral.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							SharedPreferences preferences = activity.getSharedPreferences(Data.SETTINGS_SHARED_PREF_NAME, 0);
							SharedPreferences.Editor editor = preferences.edit();
							editor.putString(Data.SP_SERVER_LINK, Data.DEV_SERVER_URL);
							editor.commit();
							
							initializeServerURL(activity);
							
							dialog.dismiss();
						}
					});
					
					btnCancel.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							
							SharedPreferences preferences = activity.getSharedPreferences(Data.SETTINGS_SHARED_PREF_NAME, 0);
							SharedPreferences.Editor editor = preferences.edit();
							editor.putString(Data.SP_SERVER_LINK, Data.TRIAL_SERVER_URL);
							editor.commit();
							
							initializeServerURL(activity);
							
							dialog.dismiss();
						}
					});

					dialog.findViewById(R.id.rl1).setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
						}
					});
					
					
					dialog.findViewById(R.id.rv).setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
					
					dialog.show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
	
	
	


	@Override
	public void onLocationChanged(Location location, int priority) {
		Data.latitude = location.getLatitude();
		Data.longitude = location.getLongitude();
	}
	
	
	public static boolean isLastLocationUpdateFine(Activity activity){
		try {
			String userMode = Database2.getInstance(activity).getUserMode();
			String driverScreenMode = Database2.getInstance(activity).getDriverScreenMode();
			long lastLocationUpdateTime = Database2.getInstance(activity).getDriverLastLocationTime();
			
			long currentTime = System.currentTimeMillis();
			
			if(lastLocationUpdateTime == 0){
				lastLocationUpdateTime = System.currentTimeMillis();
			}
			
			long systemUpTime = SystemClock.uptimeMillis();
			
			
			
			if(systemUpTime > HomeActivity.MAX_TIME_BEFORE_LOCATION_UPDATE_REBOOT){
				if(Database2.UM_DRIVER.equalsIgnoreCase(userMode) && 
						(currentTime >= (lastLocationUpdateTime + HomeActivity.MAX_TIME_BEFORE_LOCATION_UPDATE_REBOOT))){
					if(Database2.VULNERABLE.equalsIgnoreCase(driverScreenMode)){
						showRestartPhonePopup(activity);
						return false;
					}
					else{
						dismissRestartPhonePopup();
						return true;
					}
				}
				else{
					dismissRestartPhonePopup();
					return true;
				}
			}
			else{
				dismissRestartPhonePopup();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			dismissRestartPhonePopup();
			return true;
		}
	}
	
	
	public static Dialog restartPhoneDialog;
	public static void showRestartPhonePopup(final Activity activity){
		try {
			if(restartPhoneDialog == null || !restartPhoneDialog.isShowing()){
				restartPhoneDialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
				restartPhoneDialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
				restartPhoneDialog.setContentView(R.layout.dialog_custom_one_button);
	
				FrameLayout frameLayout = (FrameLayout) restartPhoneDialog.findViewById(R.id.rv);
				new ASSL(activity, frameLayout, 1134, 720, true);
	
				WindowManager.LayoutParams layoutParams = restartPhoneDialog.getWindow().getAttributes();
				layoutParams.dimAmount = 0.6f;
				restartPhoneDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				restartPhoneDialog.setCancelable(false);
				restartPhoneDialog.setCanceledOnTouchOutside(false);
	
				TextView textHead = (TextView) restartPhoneDialog.findViewById(R.id.textHead);
				textHead.setTypeface(Data.latoRegular(activity), Typeface.BOLD);
				textHead.setVisibility(View.GONE);
				TextView textMessage = (TextView) restartPhoneDialog.findViewById(R.id.textMessage);
				textMessage.setTypeface(Data.latoRegular(activity));
	
				textMessage.setMovementMethod(new ScrollingMovementMethod());
				textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));
				
				textMessage.setText("Network Problem. Please Switch OFF and Switch ON your phone and wait for 5 minutes to continue using Jugnoo.");
				
	
				Button btnOk = (Button) restartPhoneDialog.findViewById(R.id.btnOk);
				btnOk.setTypeface(Data.latoRegular(activity));
	
				btnOk.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						restartPhoneDialog.dismiss();
						activity.finish();
					}
				});
	
				restartPhoneDialog.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static void dismissRestartPhonePopup(){
		try{
			if(restartPhoneDialog != null && restartPhoneDialog.isShowing()){
				restartPhoneDialog.dismiss();
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static boolean checkIfTrivialAPIErrors(Activity activity, JSONObject jObj, int flag){
		try {
			if(ApiResponseFlags.INVALID_ACCESS_TOKEN.getOrdinal() == flag){
				DialogPopup.dismissLoadingDialog();
				HomeActivity.logoutUser(activity);
				return true;
			}
			else if(ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag){
				DialogPopup.dismissLoadingDialog();
				String errorMessage = jObj.getString("error");
				DialogPopup.alertPopup(activity, "", errorMessage);
				return true;
			}
			else if(ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag){
				DialogPopup.dismissLoadingDialog();
				String message = jObj.getString("message");
				DialogPopup.alertPopup(activity, "", message);
				return true;
			}
			else{
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
}

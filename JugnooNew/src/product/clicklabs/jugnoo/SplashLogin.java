package product.clicklabs.jugnoo;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DeviceTokenGenerator;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FacebookLoginCallback;
import product.clicklabs.jugnoo.utils.FacebookLoginHelper;
import product.clicklabs.jugnoo.utils.FacebookUserData;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.IDeviceTokenReceiver;
import product.clicklabs.jugnoo.utils.Log;
import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.Rect;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.facebook.Session;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

public class SplashLogin extends Activity implements LocationUpdate{
	
	TextView title;
	Button backBtn;
	AutoCompleteTextView emailEt;
	EditText passwordEt;
	Button signInBtn, forgotPasswordBtn, facebookSignInBtn;
	TextView extraTextForScroll;
	TextView orText;
	
	LinearLayout needHelpLinearLayout;
	TextView needHelpText, callUsText;
	
	
	
	LinearLayout relative;
	
	boolean loginDataFetched = false, facebookRegister = false, sendToOtpScreen = false;
	int otpFlag = 0; 
	String phoneNoOfUnverifiedAccount = "", otpErrorMsg = "", notRegisteredMsg = "";
	

	
	public static boolean isSystemPackage(PackageInfo pkgInfo) {
		return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
				: false;
	}
	
	
	
	
	String enteredEmail = "";
	
	String jugnooPhoneNumber = "+918556921929";
	
	public void resetFlags(){
		loginDataFetched = false;
		facebookRegister = false;
		sendToOtpScreen = false;
		otpFlag = 0;
		phoneNoOfUnverifiedAccount = "";
		otpErrorMsg = "";
		notRegisteredMsg = "";
	}
	
	// *****************************Used for flurry work***************//
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, Data.FLURRY_KEY);
		FlurryAgent.onEvent("Login started");
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_login);
		
		resetFlags();
		
		enteredEmail = "";
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(SplashLogin.this, relative, 1134, 720, false);
		
		title = (TextView) findViewById(R.id.title); title.setTypeface(Data.latoRegular(getApplicationContext()));
		backBtn = (Button) findViewById(R.id.backBtn); backBtn.setTypeface(Data.latoRegular(getApplicationContext()));
		
		emailEt = (AutoCompleteTextView) findViewById(R.id.emailEt); emailEt.setTypeface(Data.latoRegular(getApplicationContext()));
		passwordEt = (EditText) findViewById(R.id.passwordEt); passwordEt.setTypeface(Data.latoRegular(getApplicationContext()));
		
		signInBtn = (Button) findViewById(R.id.signInBtn); signInBtn.setTypeface(Data.latoRegular(getApplicationContext()));
		forgotPasswordBtn = (Button) findViewById(R.id.forgotPasswordBtn); forgotPasswordBtn.setTypeface(Data.latoRegular(getApplicationContext()));
		facebookSignInBtn = (Button) findViewById(R.id.facebookSignInBtn); facebookSignInBtn.setTypeface(Data.latoRegular(getApplicationContext()));
		
		extraTextForScroll = (TextView) findViewById(R.id.extraTextForScroll);
		
		orText = (TextView) findViewById(R.id.orText); orText.setTypeface(Data.latoRegular(getApplicationContext()));
		
		needHelpLinearLayout = (LinearLayout) findViewById(R.id.needHelpLinearLayout);
		needHelpText = (TextView) findViewById(R.id.needHelpText); needHelpText.setTypeface(Data.latoRegular(SplashLogin.this));
		callUsText = (TextView) findViewById(R.id.callUsText); callUsText.setTypeface(Data.latoRegular(SplashLogin.this));
		
		callUsText.setText("Call us now "+jugnooPhoneNumber);
		
		
		
		
		String[] emails = Database.getInstance(this).getEmails();
		Database.getInstance(this).close();
		
		Database2.getInstance(SplashLogin.this).updateDriverServiceFast("no");
		Database2.getInstance(SplashLogin.this).close();
		
		ArrayAdapter<String> adapter;
		
		if (emails == null) {																			// if emails from database are not null
			emails = new String[]{};
			adapter = new ArrayAdapter<String>(this, R.layout.dropdown_textview, emails);
		} else {																						// else reinitializing emails to be empty
			adapter = new ArrayAdapter<String>(this, R.layout.dropdown_textview, emails);
		}
		
		adapter.setDropDownViewResource(R.layout.dropdown_textview);					// setting email array to EditText DropDown list
		emailEt.setAdapter(adapter);
		
		
		
		signInBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String email = emailEt.getText().toString().trim();
				String password = passwordEt.getText().toString().trim();
				if("".equalsIgnoreCase(email)){
					emailEt.requestFocus();
					emailEt.setError("Please enter email");
				}
				else{
					if("".equalsIgnoreCase(password)){
						passwordEt.requestFocus();
						passwordEt.setError("Please enter password");
					}
					else{
						if(isEmailValid(email)){
							enteredEmail = email;
							sendLoginValues(SplashLogin.this, email, password);
							FlurryEventLogger.emailLoginClicked(email);
						}
						else{
							emailEt.requestFocus();
							emailEt.setError("Please enter valid email");
						}
					}
				}
			}
		});
		
		
		
		
		forgotPasswordBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ForgotPasswordScreen.emailAlready = emailEt.getText().toString();
				startActivity(new Intent(SplashLogin.this, ForgotPasswordScreen.class));
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
				finish();
			}
		});
		
		backBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});
		
		needHelpLinearLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent callIntent = new Intent(Intent.ACTION_VIEW);
		        callIntent.setData(Uri.parse("tel:"+jugnooPhoneNumber));
		        startActivity(callIntent);
			}
		});
		
		
		
		passwordEt.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				int result = actionId & EditorInfo.IME_MASK_ACTION;
				switch (result) {
					case EditorInfo.IME_ACTION_DONE:
						signInBtn.performClick();
					break;

					case EditorInfo.IME_ACTION_NEXT:
					break;

					default:
				}
				return true;
			}
		});
		
		
		facebookSignInBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				loginDataFetched = false;
				new FacebookLoginHelper().openFacebookSession(SplashLogin.this, facebookLoginCallback, true);
			}
		});
		
		
		
		
		final View activityRootView = findViewById(R.id.mainLinear);
		activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						Rect r = new Rect();
						// r will be populated with the coordinates of your view
						// that area still visible.
						activityRootView.getWindowVisibleDisplayFrame(r);

						int heightDiff = activityRootView.getRootView()
								.getHeight() - (r.bottom - r.top);
						if (heightDiff > 100) { // if more than 100 pixels, its
												// probably a keyboard...

							/************** Adapter for the parent List *************/

							ViewGroup.LayoutParams params_12 = extraTextForScroll
									.getLayoutParams();

							params_12.height = (int)(heightDiff);

							extraTextForScroll.setLayoutParams(params_12);
							extraTextForScroll.requestLayout();

						} else {

							ViewGroup.LayoutParams params = extraTextForScroll
									.getLayoutParams();
							params.height = 0;
							extraTextForScroll.setLayoutParams(params);
							extraTextForScroll.requestLayout();

						}
					}
				});
		
		
		
		try {																						// to get AppVersion, OS version, country code and device name
			PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			Data.appVersion = pInfo.versionCode;
			Log.i("appVersion", Data.appVersion + "..");
			Data.osVersion = android.os.Build.VERSION.RELEASE;
			Log.i("osVersion", Data.osVersion + "..");
			Data.country = getApplicationContext().getResources().getConfiguration().locale.getCountry();
			Log.i("countryCode", Data.country + "..");
			Data.deviceName = (android.os.Build.MANUFACTURER + android.os.Build.MODEL).toString();
			Log.i("deviceName", Data.deviceName + "..");
		} catch (Exception e) {
			Log.e("error in fetching appversion and gcm key", ".." + e.toString());
		}
		
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		
		try {
			if(getIntent().hasExtra("back_from_otp") && !RegisterScreen.facebookLogin){
				emailEt.setText(OTPConfirmScreen.emailRegisterData.emailId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		new DeviceTokenGenerator(this).generateDeviceToken(this, new IDeviceTokenReceiver() {
			
			@Override
			public void deviceTokenReceived(final String regId) {
				Data.deviceToken = regId;
				Log.e("deviceToken in IDeviceTokenReceiver", Data.deviceToken + "..");
			}
		});
		
		
	}
	
	FacebookLoginCallback facebookLoginCallback = new FacebookLoginCallback() {
		@Override
		public void facebookLoginDone() {
			if(FacebookLoginHelper.USER_DATA != null){
				Data.facebookUserData = new FacebookUserData(FacebookLoginHelper.USER_DATA.accessToken, FacebookLoginHelper.USER_DATA.fbId, 
						FacebookLoginHelper.USER_DATA.firstName, FacebookLoginHelper.USER_DATA.lastName, FacebookLoginHelper.USER_DATA.userName, 
						FacebookLoginHelper.USER_DATA.userEmail);
				sendFacebookLoginValues(SplashLogin.this);
				FlurryEventLogger.facebookLoginClicked(Data.facebookUserData.fbId);
			}
			else{
				Toast.makeText(getApplicationContext(), "Error occured during Facebook authentication", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(Data.locationFetcher == null){
			Data.locationFetcher = new LocationFetcher(SplashLogin.this, 1000, 1);
		}
		
		
		int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		if(resp != ConnectionResult.SUCCESS){
			Log.e("Google Play Service Error ","="+resp);
			new DialogPopup().showGooglePlayErrorAlert(SplashLogin.this);
		}
		else{
			new DialogPopup().showLocationSettingsAlert(SplashLogin.this);
		}
		
		HomeActivity.checkForAccessTokenChange(this);
	}
	
	@Override
	protected void onPause() {
		try{
			if(Data.locationFetcher != null){
				Data.locationFetcher.destroy();
				Data.locationFetcher = null;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		super.onPause();
		
	}
	
	
	@Override
	public void onBackPressed() {
		performBackPressed();
	}
	
	
	public void performBackPressed(){
		new FacebookLoginHelper().logoutFacebook();
		Intent intent = new Intent(SplashLogin.this, SplashNewActivity.class);
		intent.putExtra("no_anim", "yes");
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}

	
	
	boolean isEmailValid(CharSequence email) {
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}
	
	
	/**
	 * ASync for login from server
	 */
	public void sendLoginValues(final Activity activity, final String emailId, String password) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			resetFlags();
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
		
			if(Data.locationFetcher != null){
				Data.latitude = Data.locationFetcher.getLatitude();
				Data.longitude = Data.locationFetcher.getLongitude();
			}
			
			params.put("email", emailId);
			params.put("password", password);
			params.put("device_token", Data.deviceToken);
			params.put("device_type", Data.DEVICE_TYPE);
			params.put("device_name", Data.deviceName);
			params.put("app_version", ""+Data.appVersion);
			params.put("os_version", Data.osVersion);
			params.put("country", Data.country);
			params.put("unique_device_id", Data.uniqueDeviceId);
			params.put("latitude", ""+Data.latitude);
			params.put("longitude", ""+Data.longitude);
			params.put("client_id", Data.CLIENT_ID);
			

			Log.i("email", emailId);
			Log.i("password", password);
			Log.i("device_token", Data.deviceToken);
			Log.i("device_type", Data.DEVICE_TYPE);
			Log.i("device_name", Data.deviceName);
			Log.i("app_version", ""+Data.appVersion);
			Log.i("os_version", Data.osVersion);
			Log.i("country", Data.country);
			Log.i("unique_device_id", Data.uniqueDeviceId);
			Log.i("latitude", ""+Data.latitude);
			Log.i("longitude", ""+Data.longitude);
			Log.i("client_id", Data.CLIENT_ID);
			
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/login_using_email", params,
					new CustomAsyncHttpResponseHandler() {
					private JSONObject jObj;

						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
							DialogPopup.dismissLoadingDialog();
							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}
						

						@Override
						public void onSuccess(String response) {
							Log.i("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								int flag = jObj.getInt("flag");
								
								if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
									if(ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag){
										String error = jObj.getString("error");
										new DialogPopup().alertPopup(activity, "", error);
									}
									else if(ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag){
										String error = jObj.getString("error");
										new DialogPopup().alertPopup(activity, "", error);
									}
									else if(ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag){
										enteredEmail = emailId;
										phoneNoOfUnverifiedAccount = jObj.getString("phone_no");
										otpErrorMsg = jObj.getString("error");
										otpFlag = 0;
										sendToOtpScreen = true;
									}
									else if(ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag){
										if(!SplashNewActivity.checkIfUpdate(jObj.getJSONObject("login"), activity)){
											new JSONParser().parseAccessTokenLoginData(activity, response);
											Database.getInstance(SplashLogin.this).insertEmail(emailId);
											Database.getInstance(SplashLogin.this).close();
											loginDataFetched = true;
										}
									}
									else{
										new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
									}
									DialogPopup.dismissLoadingDialog();
								}
								else{
									DialogPopup.dismissLoadingDialog();
								}
								
							}  catch (Exception exception) {
								exception.printStackTrace();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
								DialogPopup.dismissLoadingDialog();
							}
						}
					});
		}
		else {
			new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}
	
	
	
	
	
	
	/**
	 * ASync for login from server
	 */
	public void sendFacebookLoginValues(final Activity activity) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			resetFlags();
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
		
			if(Data.locationFetcher != null){
				Data.latitude = Data.locationFetcher.getLatitude(); 
				Data.longitude = Data.locationFetcher.getLongitude();
			}

		
			params.put("user_fb_id", Data.facebookUserData.fbId);
			params.put("user_fb_name", Data.facebookUserData.firstName + " " + Data.facebookUserData.lastName);
			params.put("fb_access_token", Data.facebookUserData.accessToken);
			params.put("fb_mail", Data.facebookUserData.userEmail);
			params.put("username", Data.facebookUserData.userName);
			
			params.put("device_token", Data.deviceToken);
			params.put("device_type", Data.DEVICE_TYPE);
			params.put("device_name", Data.deviceName);
			params.put("app_version", ""+Data.appVersion);
			params.put("os_version", Data.osVersion);
			params.put("country", Data.country);
			params.put("unique_device_id", Data.uniqueDeviceId);
			params.put("latitude", ""+Data.latitude);
			params.put("longitude", ""+Data.longitude);
			params.put("client_id", Data.CLIENT_ID);
			

			Log.i("user_fb_id", Data.facebookUserData.fbId);
			Log.i("user_fb_name", Data.facebookUserData.firstName + " " + Data.facebookUserData.lastName);
			Log.i("fb_access_token", Data.facebookUserData.accessToken);
			Log.i("fb_mail", Data.facebookUserData.userEmail);
			Log.i("username", Data.facebookUserData.userName);
			
			Log.i("device_token", Data.deviceToken);
			Log.i("device_type", Data.DEVICE_TYPE);
			Log.i("device_name", Data.deviceName);
			Log.i("app_version", ""+Data.appVersion);
			Log.i("os_version", Data.osVersion);
			Log.i("country", Data.country);
			Log.i("unique_device_id", Data.uniqueDeviceId);
			Log.i("latitude", ""+Data.latitude);
			Log.i("longitude", ""+Data.longitude);
			Log.i("client_id", Data.CLIENT_ID);
			
			
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/login_using_facebook", params,
					new CustomAsyncHttpResponseHandler() {
					private JSONObject jObj;

						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
							DialogPopup.dismissLoadingDialog();
							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}

						@Override
						public void onSuccess(String response) {
							Log.i("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);

								int flag = jObj.getInt("flag");
								
								if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
									if(ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag){
										String error = jObj.getString("error");
										facebookRegister = true;
										notRegisteredMsg = error;
									}
									else if(ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag){
										String error = jObj.getString("error");
										new DialogPopup().alertPopup(activity, "", error);
									}
									else if(ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag){
										phoneNoOfUnverifiedAccount = jObj.getString("phone_no");
										otpErrorMsg = jObj.getString("error");
										otpFlag = 1;
										sendToOtpScreen = true;
									}
									else if(ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag){
										if(!SplashNewActivity.checkIfUpdate(jObj.getJSONObject("login"), activity)){
											new JSONParser().parseAccessTokenLoginData(activity, response);
											loginDataFetched = true;
											
											Database.getInstance(SplashLogin.this).insertEmail(Data.facebookUserData.userEmail);
											Database.getInstance(SplashLogin.this).close();
										}
									}
									else{
										new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
									}
									DialogPopup.dismissLoadingDialog();
								}
								else{
									DialogPopup.dismissLoadingDialog();
								}
								
							}  catch (Exception exception) {
								exception.printStackTrace();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
								DialogPopup.dismissLoadingDialog();
							}
	
						}
					});
		}
		else {
			new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}
	
	
	
	
	
	/**
	 * Send intent to otp screen by making required data objects
	 *  flag 0 for email, 1 for Facebook
	 */
	public void sendIntentToOtpScreen(){
		new DialogPopup().alertPopupWithListener(SplashLogin.this, "", otpErrorMsg, new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(0 == otpFlag){
					RegisterScreen.facebookLogin = false;
					OTPConfirmScreen.intentFromRegister = false;
					OTPConfirmScreen.emailRegisterData = new EmailRegisterData("", enteredEmail, phoneNoOfUnverifiedAccount, "", "");
					startActivity(new Intent(SplashLogin.this, OTPConfirmScreen.class));
					finish();
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
				}
				else if(1 == otpFlag){
					RegisterScreen.facebookLogin = true;
					OTPConfirmScreen.intentFromRegister = false;
					OTPConfirmScreen.facebookRegisterData = new FacebookRegisterData(phoneNoOfUnverifiedAccount, "", "");
					startActivity(new Intent(SplashLogin.this, OTPConfirmScreen.class));
					finish();
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
				}
			}
		});
	}
	
	
	public void sendIntentToRegisterScreen(){
		new DialogPopup().alertPopupWithListener(this, "", notRegisteredMsg, new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RegisterScreen.facebookLogin = true;
				startActivity(new Intent(SplashLogin.this, RegisterScreen.class));
				finish();
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			super.onActivityResult(requestCode, resultCode, data);
			Session.getActiveSession().onActivityResult(this, requestCode,
					resultCode, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		if(hasFocus && loginDataFetched){
			
			Database2.getInstance(SplashLogin.this).updateDriverLastLocationTime();
			Database2.getInstance(SplashLogin.this).close();
	        
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("username", Data.userData.userName);
			FlurryAgent.logEvent("App Login", articleParams);
			
			loginDataFetched = false;
			startActivity(new Intent(SplashLogin.this, HomeActivity.class));
			finish();
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
		}
		else if(hasFocus && facebookRegister){
			facebookRegister = false;
			sendIntentToRegisterScreen();
		}
		else if(hasFocus && sendToOtpScreen){
			sendIntentToOtpScreen();
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}
	

	@Override
	public void onLocationChanged(Location location, int priority) {
		Data.latitude = location.getLatitude();
		Data.longitude = location.getLongitude();
	}
	
}

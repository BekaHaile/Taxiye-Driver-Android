package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.facebook.Session;
import com.flurry.android.FlurryAgent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.driver.utils.DeviceTokenGenerator;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.IDeviceTokenReceiver;
import product.clicklabs.jugnoo.driver.utils.Log;
import rmn.androidscreenlibrary.ASSL;

public class SplashLogin extends Activity implements LocationUpdate{
	
	TextView title;
	Button backBtn;
	AutoCompleteTextView emailEt;
	EditText passwordEt;
	Button signInBtn, forgotPasswordBtn;


	LinearLayout relative;
	
	boolean loginDataFetched = false, sendToOtpScreen = false;
	String phoneNoOfLoginAccount = "";
	


	
	
	String enteredEmail = "";
	
	String jugnooPhoneNumber = "+919023121121";
	
	public void resetFlags(){
		loginDataFetched = false;
		sendToOtpScreen = false;
	}
	
	// *****************************Used for flurry work***************//
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.init(this, Data.FLURRY_KEY);
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
		setContentView(R.layout.activity_splash_login);

		Data.locationFetcher = null;
		
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


		
		
		String[] emails = Database.getInstance(this).getEmails();
		Database.getInstance(this).close();
		
		Database2.getInstance(SplashLogin.this).updateDriverServiceFast("no");
		
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
			if(getIntent().hasExtra("back_from_otp")){
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
	
	
	@Override
	protected void onResume() {
		super.onResume();
		
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
			DialogPopup.showGooglePlayErrorAlert(SplashLogin.this);
		}
		else{
			DialogPopup.showLocationSettingsAlert(SplashLogin.this);
		}
		
	}
	
	@Override
	protected void onPause() {
		try{
			if(Data.locationFetcher != null){
				Data.locationFetcher.destroy();
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		Database2.getInstance(this).close();
		super.onPause();
		
	}
	
	
	@Override
	public void onBackPressed() {
		performBackPressed();
		super.onBackPressed();
	}
	
	
	public void performBackPressed(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					if(Session.getActiveSession() != null){
						Session.getActiveSession().closeAndClearTokenInformation();
					}
				} catch(Exception e){
					e.printStackTrace();
				}
			}
		}).start();
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
	public void sendLoginValues(final Activity activity, final String emailId, final String password) {
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
			params.put("device_type", Data.DEVICE_TYPE);
			params.put("unique_device_id", Data.uniqueDeviceId);
			params.put("device_token", Data.deviceToken);
			params.put("latitude", ""+Data.latitude);
			params.put("longitude", ""+Data.longitude);
			params.put("country", Data.country);
			params.put("device_name", Data.deviceName);
			params.put("app_version", Data.appVersion);
			params.put("os_version", Data.osVersion);

			Log.i("Server uRL", "=" + Data.SERVER_URL);
			Log.i("email", "=" + emailId);
			Log.i("password", "=" + password);
			Log.e("device_token", "=" + Data.deviceToken);
			Log.i("latitude", "=" + Data.latitude);
			Log.i("longitude", "=" + Data.longitude);
			Log.i("country", "=" + Data.country);
			Log.i("device_name", "=" + Data.deviceName);
			Log.i("app_version", "=" + Data.appVersion);
			Log.i("os_version", "=" + Data.osVersion);
			Log.i("unique_device_id", "=" + Data.uniqueDeviceId);
			
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/login_driver_via_email", params,
					new CustomAsyncHttpResponseHandler() {
					private JSONObject jObj;

						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
							DialogPopup.dismissLoadingDialog();
							DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}
						

						@Override
						public void onSuccess(String response) {
							Log.i("Server response", "response = " + response);
							try {
								jObj = new JSONObject(response);
								boolean newUpdate = SplashNewActivity.checkIfUpdate(jObj, activity);
								if(!newUpdate){
									int flag = jObj.getInt("flag");
									if(ApiResponseFlags.INVALID_ACCESS_TOKEN.getOrdinal() == flag){
										HomeActivity.logoutUser(activity);
									}
									else if(ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag){
										String errorMessage = jObj.getString("error");
										DialogPopup.alertPopup(activity, "", errorMessage);
									}
									else if(ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag){
										String message = jObj.getString("message");
										DialogPopup.alertPopup(activity, "", message);
									}
									else if(ApiResponseFlags.INCORRECT_PASSWORD.getOrdinal() == flag){
										String errorMessage = jObj.getString("error");
										DialogPopup.alertPopup(activity, "", errorMessage);
									}
									else if(ApiResponseFlags.CUSTOMER_LOGGING_IN.getOrdinal() == flag){
										String errorMessage = jObj.getString("error");
										SplashNewActivity.sendToCustomerAppPopup("Alert", errorMessage, activity);
									}
									else if(ApiResponseFlags.LOGIN_SUCCESSFUL.getOrdinal() == flag){
										new JSONParser().parseLoginData(activity, response);
										Database.getInstance(SplashLogin.this).insertEmail(emailId);
										Database.getInstance(SplashLogin.this).close();
										loginDataFetched = true;
									}
									else{
										DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
									}
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
							DialogPopup.dismissLoadingDialog();
						}
					});
		}
		else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}
	
	

	public void sendIntentToOtpScreen() {
        OTPConfirmScreen.intentFromRegister = false;
        OTPConfirmScreen.emailRegisterData = new EmailRegisterData("", enteredEmail, phoneNoOfLoginAccount, "");
        startActivity(new Intent(SplashLogin.this, OTPConfirmScreen.class));
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }


    @Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		if(hasFocus && loginDataFetched){
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("username", Data.userData.userName);
			FlurryAgent.logEvent("App Login", articleParams);
			startActivity(new Intent(SplashLogin.this, HomeActivity.class));
			loginDataFetched = false;
			finish();
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
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

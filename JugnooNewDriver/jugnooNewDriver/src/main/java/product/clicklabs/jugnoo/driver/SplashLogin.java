package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
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


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.EmailRegisterData;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.ui.DriverSplashActivity;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.LocationInit;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class SplashLogin extends Activity implements LocationUpdate, FlurryEventNames{

	TextView title;
	Button backBtn;
	AutoCompleteTextView emailEt;
	EditText passwordEt;
	Button signInBtn, forgotPasswordBtn,signInUsingOtp;


	LinearLayout relative;

	boolean loginDataFetched = false, sendToOtpScreen = false, fromPreviousAccounts = false;
	String phoneNoOfLoginAccount = "", accessToken = "", otpErrorMsg = "";




	String enteredEmail = "";
	String enteredPhone = "";


	public void resetFlags(){
		loginDataFetched = false;
		sendToOtpScreen = false;
	}

	// *****************************Used for flurry work***************//
	@Override
	protected void onStart() {
		super.onStart();


	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try{
			if(getIntent().hasExtra("type")){
				if(getIntent().getStringExtra("type").equalsIgnoreCase("cancel")){
					Intent intent = new Intent(SplashLogin.this, DriverSplashActivity.class);
					intent.putExtras(getIntent().getExtras());
					startActivity(intent);
					finish();
				}
			}

		}catch (Exception e){
			e.printStackTrace();
		}

		setContentView(R.layout.activity_splash_login);

		Data.locationFetcher = null;

		resetFlags();

		enteredEmail = "";

		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(SplashLogin.this, relative, 1134, 720, false);

		title = (TextView) findViewById(R.id.title); title.setTypeface(Fonts.mavenRegular(getApplicationContext()));
		backBtn = (Button) findViewById(R.id.backBtn); backBtn.setTypeface(Fonts.mavenRegular(getApplicationContext()));

		emailEt = (AutoCompleteTextView) findViewById(R.id.emailEt); emailEt.setTypeface(Fonts.mavenRegular(getApplicationContext()));
		passwordEt = (EditText) findViewById(R.id.passwordEt); passwordEt.setTypeface(Fonts.mavenRegular(getApplicationContext()));

		signInBtn = (Button) findViewById(R.id.signInBtn); signInBtn.setTypeface(Fonts.mavenRegular(getApplicationContext()));
		signInUsingOtp = (Button) findViewById(R.id.signInUsingOtp); signInUsingOtp.setTypeface(Fonts.mavenRegular(getApplicationContext()));
		forgotPasswordBtn = (Button) findViewById(R.id.forgotPasswordBtn); forgotPasswordBtn.setTypeface(Fonts.mavenRegular(getApplicationContext()));




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
				String emailOrPhone = emailEt.getText().toString().trim();
				String password = passwordEt.getText().toString().trim();
				if ("".equalsIgnoreCase(emailOrPhone)) {
					emailEt.requestFocus();
					emailEt.setError(getResources().getString(R.string.Pls_enter_email_or_phn));
				} else {
					if ("".equalsIgnoreCase(password)) {
						passwordEt.requestFocus();
						passwordEt.setError(getResources().getString(R.string.enter_password));
					} else {
						if (isEmailValid(emailOrPhone)) {
							enteredEmail = emailOrPhone;
							sendLoginValues(SplashLogin.this, enteredEmail, "", password, "");
							FlurryEventLogger.event(LOGIN_EMAIL_ID);
							FlurryEventLogger.event(LOGIN_PASSWORD);
							FlurryEventLogger.event(LOGIN_IN_APP);
						} else if ((Utils.validPhoneNumber(emailOrPhone))) {
							enteredPhone = Utils.getCountryCode(SplashLogin.this) + emailOrPhone;
							sendLoginValues(SplashLogin.this, "", enteredPhone, password, "");
							FlurryEventLogger.event(LOGIN_EMAIL_ID);
							FlurryEventLogger.event(LOGIN_PASSWORD);
							FlurryEventLogger.event(LOGIN_IN_APP);
						} else {
							emailEt.requestFocus();
							emailEt.setError(getResources().getString(R.string.valid_email_phone_no));
						}
					}
				}
			}
		});


		forgotPasswordBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SplashLogin.this, ForgotPasswordScreen.class);
				intent.putExtra("forgotEmail", emailEt.getText().toString());
				intent.putExtra("fromPreviousAccounts", fromPreviousAccounts);
				startActivity(intent);
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
				finish();
				FlurryEventLogger.event(FORGOT_PASSWORD);
			}
		});

		signInUsingOtp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SplashLogin.this, LoginViaOTP.class);
				startActivity(intent);
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
//		Log.i(SplashNewActivity.DEVICE_TOKEN_TAG + "splash login", FirebaseInstanceId.getInstance().getInstanceId().getResult().getToken());
		FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
			@Override
			public void onComplete(@NonNull Task<InstanceIdResult> task) {
				if(!task.isSuccessful()) {
					Log.w("DRIVER_DOCUMENT_ACTIVITY","device_token_unsuccessful - onReceive",task.getException());
					return;
				}
				if(task.getResult() != null) {
					Log.e("DEVICE_TOKEN_TAG SPLASHLOGIN  -> onCreate", task.getResult().getToken());
					Data.deviceToken = 	task.getResult().getToken();
				}

			}
		});

		Log.e("deviceToken in IDeviceTokenReceiver", Data.deviceToken + "..");

		try {
			if (getIntent().hasExtra("previous_login_email")) {
				String previousLoginEmail = getIntent().getStringExtra("previous_login_email");
				emailEt.setText(previousLoginEmail);
				emailEt.setSelection(emailEt.getText().length());
				fromPreviousAccounts = true;
			}
			else{
				fromPreviousAccounts = false;
			}
		} catch(Exception e){
			e.printStackTrace();
			fromPreviousAccounts = false;
		}

		try {
			if (getIntent().hasExtra("forgot_login_email")) {
				String forgotLoginEmail = getIntent().getStringExtra("forgot_login_email");
				emailEt.setText(forgotLoginEmail);
				emailEt.setSelection(emailEt.getText().length());
			}
		} catch(Exception e){
			e.printStackTrace();
		}


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
			LocationInit.showLocationAlertDialog(SplashLogin.this);
//			DialogPopup.showLocationSettingsAlert(SplashLogin.this);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			super.onActivityResult(requestCode, resultCode, data);
			if (LocationInit.LOCATION_REQUEST_CODE == requestCode) {
				if (0 == resultCode) {
					finish();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void onBackPressed() {
		performBackPressed();
		super.onBackPressed();
	}


	public void performBackPressed(){
		if(fromPreviousAccounts){
			Intent intent = new Intent(SplashLogin.this, MultipleAccountsActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.left_in, R.anim.left_out);
		}
		else {

			Intent intent = new Intent(SplashLogin.this, DriverSplashActivity.class);
			intent.putExtra("no_anim", "yes");
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.left_in, R.anim.left_out);
		}
	}



	boolean isEmailValid(CharSequence email) {
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}


//	Retrofit


	public void sendLoginValues(final Activity activity, final String emailId,final String phoneNo, final String password, final String otp) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			resetFlags();
			DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));

//			RequestParams params = new RequestParams();

			if(Data.locationFetcher != null){
				Data.latitude = Data.locationFetcher.getLatitude();
				Data.longitude = Data.locationFetcher.getLongitude();
			}

			HashMap<String, String> params = new HashMap<String, String>();

			params.put("email", emailId);
			params.put("phone_no", phoneNo);
			params.put("password", password);
			params.put("login_otp",otp);
			params.put("device_token", Data.deviceToken);
			params.put("device_type", Data.DEVICE_TYPE);
			params.put("device_name", Data.deviceName);
			params.put("app_version", "" + Data.appVersion);
			params.put("os_version", Data.osVersion);
			params.put("country", Data.country);
			params.put("unique_device_id", Data.uniqueDeviceId);
			params.put("latitude", "" + Data.latitude);
			params.put("longitude", "" + Data.longitude);
			params.put("client_id", Data.CLIENT_ID);
			params.put("login_type", Data.LOGIN_TYPE);
			HomeUtil.putDefaultParams(params);

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

			if(Utils.telerickshawInstall(activity)){
				params.put("telerickshaw_installed", "1");
			}
			else{
				params.put("telerickshaw_installed", "0");
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

			RestClient.getApiServices().sendLoginValuesRetro(params, new Callback<RegisterScreenResponse>() {
				@Override
				public void success(RegisterScreenResponse registerScreenResponse, Response response) {
					try {

						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj;
						jObj = new JSONObject(jsonString);
						int flag = jObj.getInt("flag");
						String message = JSONParser.getServerMessage(jObj);

						if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag, null)){
							if(ApiResponseFlags.INCORRECT_PASSWORD.getOrdinal() == flag){
								DialogPopup.alertPopup(activity, "", message);
							}
							else if(ApiResponseFlags.CUSTOMER_LOGGING_IN.getOrdinal() == flag){
								SplashNewActivity.sendToCustomerAppPopup("Alert", message, activity);
							}
							else if(ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag){
								DialogPopup.alertPopup(activity, "", message);
							}
							else if(ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag){
								DialogPopup.alertPopup(activity, "", message);
							}
							else if(ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag){
								enteredEmail = emailId;
								phoneNoOfLoginAccount = jObj.getString("phone_no");
								accessToken = jObj.getString("access_token");
								otpErrorMsg = jObj.getString("error");
								sendToOtpScreen = true;
							}
							else if(ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag){
								if(!SplashNewActivity.checkIfUpdate(jObj.getJSONObject("login"), activity)){
									new JSONParser().parseAccessTokenLoginData(activity, jsonString);
									startService(new Intent(activity, DriverLocationUpdateService.class));
									Database.getInstance(SplashLogin.this).insertEmail(emailId);
									loginDataFetched = true;
								}
							}
							else{
								DialogPopup.alertPopup(activity, "", message);
							}
							DialogPopup.dismissLoadingDialog();
						}
						else{
							DialogPopup.dismissLoadingDialog();
						}
					}  catch (Exception exception) {
						exception.printStackTrace();
						DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
					}
					DialogPopup.dismissLoadingDialog();
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



	public void sendIntentToOtpScreen() {
		DialogPopup.alertPopupWithListener(SplashLogin.this, "", otpErrorMsg, new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				OTPConfirmScreen.intentFromRegister = false;
				OTPConfirmScreen.emailRegisterData = new EmailRegisterData("", enteredEmail, phoneNoOfLoginAccount, "", accessToken,"", "");
				startActivity(new Intent(SplashLogin.this, OTPConfirmScreen.class));
				finish();
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});
	}


	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if(hasFocus && loginDataFetched){
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("username", Data.userData.userName);
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

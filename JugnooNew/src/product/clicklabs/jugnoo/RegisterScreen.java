package product.clicklabs.jugnoo;

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
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.facebook.Session;
import com.flurry.android.FlurryAgent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

public class RegisterScreen extends Activity implements LocationUpdate{
	
	Button backBtn;
	TextView title;
	
	Button registerWithFacebookBtn;
	TextView orText;
	
	EditText nameEt, referralCodeEt, emailIdEt, phoneNoEt, passwordEt, confirmPasswordEt;
	Button signUpBtn;
	TextView extraTextForScroll;
	ScrollView scroll;
	
	LinearLayout relative;
	
	String name = "", referralCode = "", emailId = "", phoneNo = "", password = "";
	
	public static boolean facebookLogin = false;
	boolean loginDataFetched = false, sendToOtpScreen = false;
	int otpFlag = 0;
	
	public void resetFlags(){
		loginDataFetched = false;
		sendToOtpScreen = false;
		otpFlag = 0;
	}
	
	// *****************************Used for flurry work***************//
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, Data.FLURRY_KEY);
		FlurryAgent.onEvent("Register started");
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_screen);
		
		loginDataFetched = false;
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(RegisterScreen.this, relative, 1134, 720, false);
		
		backBtn = (Button) findViewById(R.id.backBtn); backBtn.setTypeface(Data.latoRegular(getApplicationContext()));
		title = (TextView) findViewById(R.id.title); title.setTypeface(Data.latoRegular(getApplicationContext()));
		
		registerWithFacebookBtn = (Button) findViewById(R.id.registerWithFacebookBtn); registerWithFacebookBtn.setTypeface(Data.latoRegular(getApplicationContext()));
		orText = (TextView) findViewById(R.id.orText); orText.setTypeface(Data.latoRegular(getApplicationContext()));
		
		scroll = (ScrollView) findViewById(R.id.scroll);
		
		nameEt = (EditText) findViewById(R.id.nameEt); nameEt.setTypeface(Data.latoRegular(getApplicationContext()));
		referralCodeEt = (EditText) findViewById(R.id.referralCodeEt); referralCodeEt.setTypeface(Data.latoRegular(getApplicationContext()));
		emailIdEt = (EditText) findViewById(R.id.emailIdEt); emailIdEt.setTypeface(Data.latoRegular(getApplicationContext()));
		phoneNoEt = (EditText) findViewById(R.id.phoneNoEt); phoneNoEt.setTypeface(Data.latoRegular(getApplicationContext()));
		passwordEt = (EditText) findViewById(R.id.passwordEt); passwordEt.setTypeface(Data.latoRegular(getApplicationContext()));
		confirmPasswordEt = (EditText) findViewById(R.id.confirmPasswordEt); confirmPasswordEt.setTypeface(Data.latoRegular(getApplicationContext()));
		
		signUpBtn = (Button) findViewById(R.id.signUpBtn); signUpBtn.setTypeface(Data.latoRegular(getApplicationContext()));
		
		extraTextForScroll = (TextView) findViewById(R.id.extraTextForScroll);

		
		

		registerWithFacebookBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new FacebookLoginHelper().openFacebookSession(RegisterScreen.this, facebookLoginCallback, true);
			}
		});
		
		backBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});
		
		nameEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				nameEt.setError(null);
				
			}
		});
		
		
		emailIdEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				emailIdEt.setError(null);
			}
		});
		
		phoneNoEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				phoneNoEt.setError(null);
			}
		});

		passwordEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				passwordEt.setError(null);
			}
		});

		confirmPasswordEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				confirmPasswordEt.setError(null);
			}
		});
		
		
		
		
		signUpBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String name = nameEt.getText().toString().trim();
				if(name.length() > 0){
					name = name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
				}
				String referralCode = referralCodeEt.getText().toString().trim();
				String emailId = emailIdEt.getText().toString().trim();
				boolean noFbEmail = false;
				
				if(facebookLogin && emailId.equalsIgnoreCase("")){
					emailId = "n@n.c";
					noFbEmail = true;
				}
				
				
				
				String phoneNo = phoneNoEt.getText().toString().trim();
				String password = passwordEt.getText().toString().trim();
				String confirmPassword = confirmPasswordEt.getText().toString().trim();

				
				
				if("".equalsIgnoreCase(name)){
					nameEt.requestFocus();
					nameEt.setError("Please enter name");
				}
				else{
					if("".equalsIgnoreCase(emailId)){
						emailIdEt.requestFocus();
						emailIdEt.setError("Please enter email id");
					}
					else{
						if("".equalsIgnoreCase(phoneNo)){
							phoneNoEt.requestFocus();
							phoneNoEt.setError("Please enter phone number");
						}
						else{
							//TODO remove extra characters phoneNo
							phoneNo = phoneNo.replace(" ", "");
							phoneNo = phoneNo.replace("(", "");
							phoneNo = phoneNo.replace("/", "");
							phoneNo = phoneNo.replace(")", "");
							phoneNo = phoneNo.replace("N", "");
							phoneNo = phoneNo.replace(",", "");
							phoneNo = phoneNo.replace("*", "");
							phoneNo = phoneNo.replace(";", "");
							phoneNo = phoneNo.replace("#", "");
							phoneNo = phoneNo.replace("-", "");
							phoneNo = phoneNo.replace(".", "");
							
							if(phoneNo.length() >= 10){
								phoneNo = phoneNo.substring(phoneNo.length()-10, phoneNo.length());
								if(phoneNo.charAt(0) == '0' || phoneNo.charAt(0) == '1' || phoneNo.contains("+")){
									phoneNoEt.requestFocus();
									phoneNoEt.setError("Please enter valid phone number");
								}
								else{
									phoneNo = "+91" + phoneNo;
									
									if("".equalsIgnoreCase(password)){
										passwordEt.requestFocus();
										passwordEt.setError("Please enter password");
									}
									else{
										if("".equalsIgnoreCase(confirmPassword)){
											confirmPasswordEt.requestFocus();
											confirmPasswordEt.setError("Please confirm password");
										}
										else{
											if(isEmailValid(emailId)){
												if(isPhoneValid(phoneNo)){
													if(password.equals(confirmPassword)){
														if(password.length() >= 6){
															
															if(facebookLogin){
																if(noFbEmail){
																	emailId = "";
																}
																sendFacebookSignupValues(RegisterScreen.this, referralCode, phoneNo, password);
																FlurryEventLogger.facebookSignupClicked(Data.facebookUserData.userEmail);
																if(!"".equalsIgnoreCase(referralCode)){
																	FlurryEventLogger.referralCodeAtFBSignup(Data.facebookUserData.userEmail, referralCode);
																}
															}
															else{
																sendSignupValues(RegisterScreen.this, name, referralCode, emailId, phoneNo, password);
																FlurryEventLogger.emailSignupClicked(emailId);
																if(!"".equalsIgnoreCase(referralCode)){
																	FlurryEventLogger.referralCodeAtEmailSignup(emailId, referralCode);
																}
															}
															
															
														}
														else{
															passwordEt.requestFocus();
															passwordEt.setError("Password must be of atleast six characters");
														}
													}
													else{
														passwordEt.requestFocus();
														passwordEt.setError("Passwords does not match");
													}
												}
												else{
													phoneNoEt.requestFocus();
													phoneNoEt.setError("Please enter valid phone number");
												}
											}
											else{
												emailIdEt.requestFocus();
												emailIdEt.setError("Please enter valid email id");
											}
										}
									}
								}
							}
							else{
								phoneNoEt.requestFocus();
								phoneNoEt.setError("Please enter valid phone number");
							}
						}
					}
				}
				
			}
		});
		
		
		referralCodeEt.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				int result = actionId & EditorInfo.IME_MASK_ACTION;
				switch (result) {
					case EditorInfo.IME_ACTION_DONE:
						signUpBtn.performClick();
					break;

					case EditorInfo.IME_ACTION_NEXT:
					break;

					default:
				}
				return true;
			}
		});
		
		
		
		if(facebookLogin){
			nameEt.setText(Data.facebookUserData.firstName + " " + Data.facebookUserData.lastName);
			nameEt.setEnabled(false);
			if("".equalsIgnoreCase(Data.facebookUserData.userEmail)){
				emailIdEt.setText("");
				emailIdEt.setEnabled(true);
			}
			else{
				emailIdEt.setText(Data.facebookUserData.userEmail);
				emailIdEt.setEnabled(false);
			}
		}
		
		try {
			if(getIntent().hasExtra("back_from_otp")){
				if(facebookLogin){
					referralCodeEt.setText(OTPConfirmScreen.facebookRegisterData.referralCode);
					phoneNoEt.setText(OTPConfirmScreen.facebookRegisterData.phoneNo);
					passwordEt.setText(OTPConfirmScreen.facebookRegisterData.password);
					confirmPasswordEt.setText(OTPConfirmScreen.facebookRegisterData.password);
				}
				else{
					nameEt.setText(OTPConfirmScreen.emailRegisterData.name);
					emailIdEt.setText(OTPConfirmScreen.emailRegisterData.emailId);
					referralCodeEt.setText(OTPConfirmScreen.emailRegisterData.referralCode);
					phoneNoEt.setText(OTPConfirmScreen.emailRegisterData.phoneNo);
					passwordEt.setText(OTPConfirmScreen.emailRegisterData.password);
					confirmPasswordEt.setText(OTPConfirmScreen.emailRegisterData.password);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
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
		
		
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		new DeviceTokenGenerator(this).generateDeviceToken(this, new IDeviceTokenReceiver() {
			
			@Override
			public void deviceTokenReceived(final String regId) {
				Data.deviceToken = regId;
				Log.e("deviceToken in IDeviceTokenReceiver", Data.deviceToken + "..");
			}
		});
		

		
//		nameEt.setText("Test");
//		lastNameEt.setText("Passenger84");
//		emailIdEt.setText("passenger84@click-labs.com");
//		phoneNoEt.setText("9999999999");
//		passwordEt.setText("passenger");
//		confirmPasswordEt.setText("passenger");
		
//		phoneNoEt.setText("+"+GetCountryZipCode());
		
//		Toast.makeText(getApplicationContext(), ""+GetCountryZipCode(), Toast.LENGTH_LONG).show();
		
		
	}
	
	FacebookLoginCallback facebookLoginCallback = new FacebookLoginCallback() {
		@Override
		public void facebookLoginDone() {
			if(FacebookLoginHelper.USER_DATA != null){
				Data.facebookUserData = new FacebookUserData(FacebookLoginHelper.USER_DATA.accessToken, FacebookLoginHelper.USER_DATA.fbId, 
						FacebookLoginHelper.USER_DATA.firstName, FacebookLoginHelper.USER_DATA.lastName, FacebookLoginHelper.USER_DATA.userName, 
						FacebookLoginHelper.USER_DATA.userEmail);
				facebookLogin = true;
				nameEt.setText(Data.facebookUserData.firstName + " " + Data.facebookUserData.lastName);
				emailIdEt.setText(Data.facebookUserData.userEmail);
				
				nameEt.setEnabled(false);
				emailIdEt.setEnabled(false);
				FlurryEventLogger.registerViaFBClicked(Data.facebookUserData.fbId);
			}
			else{
				Toast.makeText(getApplicationContext(), "Error occured during Facebook authentication", Toast.LENGTH_SHORT).show();
			}
		}
	};

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
	
	
	String GetCountryZipCode() {

		String CountryID = "";
		String CountryZipCode = "";

		TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
		// getNetworkCountryIso
		CountryID = manager.getSimCountryIso().toUpperCase();
		Log.e("CountryID", "="+CountryID);
		String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
		for (int i = 0; i < rl.length; i++) {
			String[] g = rl[i].split(",");
			if (g[1].trim().equals(CountryID.trim())) {
				CountryZipCode = g[0];
				return CountryZipCode;
			}
		}
		return "";
	}
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(Data.locationFetcher == null){
			Data.locationFetcher = new LocationFetcher(RegisterScreen.this, 1000, 1);
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
	
	
	
	
	/**
	 * ASync for register from server
	 */
	public void sendSignupValues(final Activity activity, final String name, final String referralCode, final String emailId, final String phoneNo, final String password) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			resetFlags();
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
		
			if(Data.locationFetcher != null){
				Data.latitude = Data.locationFetcher.getLatitude();
				Data.longitude = Data.locationFetcher.getLongitude();
			}

			params.put("user_name", name);
			params.put("phone_no", phoneNo);
			params.put("email", emailId);
			params.put("password", password);
			params.put("latitude", ""+Data.latitude);
			params.put("longitude", ""+Data.longitude);
			params.put("device_token", Data.deviceToken);
			params.put("device_type", Data.DEVICE_TYPE);
			params.put("device_name", Data.deviceName);
			params.put("app_version", ""+Data.appVersion);
			params.put("os_version", Data.osVersion);
			params.put("country", Data.country);
			params.put("unique_device_id", Data.uniqueDeviceId);
			params.put("client_id", Data.CLIENT_ID);
			params.put("referral_code", referralCode);
			

			Log.i("user_name", name);
			Log.i("phone_no", phoneNo);
			Log.i("email", emailId);
			Log.i("password", password);
			Log.i("latitude", ""+Data.latitude);
			Log.i("longitude", ""+Data.longitude);
			Log.i("device_token", Data.deviceToken);
			Log.i("device_type", Data.DEVICE_TYPE);
			Log.i("device_name", Data.deviceName);
			Log.i("app_version", ""+Data.appVersion);
			Log.i("os_version", Data.osVersion);
			Log.i("country", Data.country);
			Log.i("unique_device_id", Data.uniqueDeviceId);
			Log.i("client_id", Data.CLIENT_ID);
			Log.i("referral_code", referralCode);
			
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/register_using_email", params,
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
								
								if(!SplashNewActivity.checkIfUpdate(jObj, activity)){
									if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
										int flag = jObj.getInt("flag");
										if(ApiResponseFlags.AUTH_REGISTRATION_FAILURE.getOrdinal() == flag){
											String error = jObj.getString("error");
											new DialogPopup().alertPopup(activity, "", error);
										}
										else if(ApiResponseFlags.AUTH_ALREADY_REGISTERED.getOrdinal() == flag){
											String error = jObj.getString("error");
											new DialogPopup().alertPopup(activity, "", error);
										}
										else if(ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag){
											RegisterScreen.this.name = name;
											RegisterScreen.this.emailId = emailId;
											RegisterScreen.this.phoneNo = jObj.getString("phone_no");
											RegisterScreen.this.password = password;
											RegisterScreen.this.referralCode = referralCode;
											sendToOtpScreen = true;
											otpFlag = 0;
										}
										else{
											new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
										}
										DialogPopup.dismissLoadingDialog();
									}
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
	public void sendFacebookSignupValues(final Activity activity, final String referralCode, final String phoneNo, final String password) {
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
			
			params.put("phone_no", phoneNo);
			params.put("password", password);
			params.put("referral_code", referralCode);
			
			params.put("latitude", ""+Data.latitude);
			params.put("longitude", ""+Data.longitude);
			params.put("device_token", Data.deviceToken);
			params.put("device_type", Data.DEVICE_TYPE);
			params.put("device_name", Data.deviceName);
			params.put("app_version", ""+Data.appVersion);
			params.put("os_version", Data.osVersion);
			params.put("country", Data.country);
			params.put("unique_device_id", Data.uniqueDeviceId);
			params.put("client_id", Data.CLIENT_ID);
			
			

			Log.e("user_fb_id", Data.facebookUserData.fbId);
			Log.e("user_fb_name", Data.facebookUserData.firstName + " " + Data.facebookUserData.lastName);
			Log.e("fb_access_token", Data.facebookUserData.accessToken);
			Log.e("fb_mail", Data.facebookUserData.userEmail);
			Log.e("username", Data.facebookUserData.userName);
			
			Log.e("phone_no", phoneNo);
			Log.e("password", password);
			Log.e("latitude", ""+Data.latitude);
			Log.e("longitude", ""+Data.longitude);
			Log.e("device_token", Data.deviceToken);
			Log.e("device_type", Data.DEVICE_TYPE);
			Log.e("device_name", Data.deviceName);
			Log.e("app_version", ""+Data.appVersion);
			Log.e("os_version", Data.osVersion);
			Log.e("country", Data.country);
			Log.e("unique_device_id", Data.uniqueDeviceId);
			Log.e("client_id", Data.CLIENT_ID);
			Log.e("referral_code", referralCode);
			
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/register_using_facebook", params,
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
								
								if(!SplashNewActivity.checkIfUpdate(jObj, activity)){
									if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
										int flag = jObj.getInt("flag");
										if(ApiResponseFlags.AUTH_REGISTRATION_FAILURE.getOrdinal() == flag){
											String error = jObj.getString("error");
											new DialogPopup().alertPopup(activity, "", error);
										}
										else if(ApiResponseFlags.AUTH_ALREADY_REGISTERED.getOrdinal() == flag){
											String error = jObj.getString("error");
											new DialogPopup().alertPopup(activity, "", error);
										}
										else if(ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag){
											RegisterScreen.this.phoneNo = jObj.getString("phone_no");
											RegisterScreen.this.password = password;
											RegisterScreen.this.referralCode = referralCode;
											sendToOtpScreen = true;
											otpFlag = 1;
										}
										DialogPopup.dismissLoadingDialog();
									}
								}
								else{
									DialogPopup.dismissLoadingDialog();
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								DialogPopup.dismissLoadingDialog();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
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
		if(0 == otpFlag){
			RegisterScreen.facebookLogin = false;
			OTPConfirmScreen.intentFromRegister = true;
			OTPConfirmScreen.emailRegisterData = new EmailRegisterData(name, emailId, phoneNo, password, referralCode);
			startActivity(new Intent(RegisterScreen.this, OTPConfirmScreen.class));
			finish();
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
		}
		else if(1 == otpFlag){
			RegisterScreen.facebookLogin = true;
			OTPConfirmScreen.intentFromRegister = true;
			OTPConfirmScreen.facebookRegisterData = new FacebookRegisterData(phoneNo, password, referralCode);
			startActivity(new Intent(RegisterScreen.this, OTPConfirmScreen.class));
			finish();
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
		}
	}
	
	
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		if(hasFocus && loginDataFetched){
			loginDataFetched = false;
			Database2.getInstance(RegisterScreen.this).updateDriverLastLocationTime();
			Database2.getInstance(RegisterScreen.this).close();
			startActivity(new Intent(RegisterScreen.this, HomeActivity.class));
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
			finish();
		}
		else if(hasFocus && sendToOtpScreen){
			sendIntentToOtpScreen();
		}
		
	}
	
	boolean isEmailValid(CharSequence email) {
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}

	
	boolean isPhoneValid(CharSequence phone) {
		return android.util.Patterns.PHONE.matcher(phone).matches();
	}
	
	
	@Override
	public void onBackPressed() {
		performBackPressed();
		super.onBackPressed();
	}
	
	
	public void performBackPressed(){
		new FacebookLoginHelper().logoutFacebook();
		Intent intent = new Intent(RegisterScreen.this, SplashNewActivity.class);
		intent.putExtra("no_anim", "yes");
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
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

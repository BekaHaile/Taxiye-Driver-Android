package product.clicklabs.jugnoo;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DeviceTokenGenerator;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.IDeviceTokenReceiver;
import product.clicklabs.jugnoo.utils.Log;
import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.flurry.android.FlurryAgent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

public class OTPConfirmScreen extends Activity implements LocationUpdate{
	
	Button backBtn;
	TextView title;
	
	TextView otpHelpText, pleaseWaitText, weWillCallText;
	
	EditText otpEt;
	Button confirmBtn, callMeBtn;
	
	LinearLayout relative;
	
	boolean loginDataFetched = false;
	
	public static boolean intentFromRegister = true;
	public static EmailRegisterData emailRegisterData;
	public static FacebookRegisterData facebookRegisterData;
	
	String otpHelpStr = "Please enter the One Time Password you just received via SMS at ";
	String waitStr = "Please wait for ";
	
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, Data.FLURRY_KEY);
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.otp_confrim_screen);
		
		loginDataFetched = false;
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(OTPConfirmScreen.this, relative, 1134, 720, false);
		
		backBtn = (Button) findViewById(R.id.backBtn); backBtn.setTypeface(Data.latoRegular(getApplicationContext()));
		title = (TextView) findViewById(R.id.title); title.setTypeface(Data.latoRegular(getApplicationContext()));
		
		otpHelpText = (TextView) findViewById(R.id.otpHelpText); otpHelpText.setTypeface(Data.latoRegular(getApplicationContext()));
		pleaseWaitText = (TextView) findViewById(R.id.pleaseWaitText); pleaseWaitText.setTypeface(Data.latoRegular(getApplicationContext()));
		weWillCallText = (TextView) findViewById(R.id.weWillCallText); weWillCallText.setTypeface(Data.latoRegular(getApplicationContext()));
		
		otpEt = (EditText) findViewById(R.id.otpEt); otpEt.setTypeface(Data.latoRegular(getApplicationContext()));
		
		confirmBtn = (Button) findViewById(R.id.confirmBtn); confirmBtn.setTypeface(Data.latoRegular(getApplicationContext()));
		callMeBtn = (Button) findViewById(R.id.callMeBtn); callMeBtn.setTypeface(Data.latoRegular(getApplicationContext()));
		
		
		backBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});
		
		otpEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				otpEt.setError(null);
			}
		});
		
		
		confirmBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String otpCode = otpEt.getText().toString().trim();
				if(otpCode.length() > 0){
					stopWaitingTimer();
					callMeBtn.setBackgroundResource(R.drawable.blue_btn_selector);
					if(RegisterScreen.facebookLogin){
						verifyOtpViaFB(OTPConfirmScreen.this, otpCode);
						FlurryEventLogger.otpConfirmClick(otpCode);
					}
					else{
						verifyOtpViaEmail(OTPConfirmScreen.this, otpCode);
						FlurryEventLogger.otpConfirmClick(otpCode);
					}
				}
				else{
					otpEt.requestFocus();
					otpEt.setError("Code can't be empty");
				}
				
			}
		});
		
		otpEt.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				int result = actionId & EditorInfo.IME_MASK_ACTION;
				switch (result) {
					case EditorInfo.IME_ACTION_DONE:
						confirmBtn.performClick();
					break;

					case EditorInfo.IME_ACTION_NEXT:
					break;

					default:
				}
				return true;
			}
		});
		
		
		callMeBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				stopWaitingTimer();
				if (RegisterScreen.facebookLogin) {
					initiateOTPCallAsync(OTPConfirmScreen.this, facebookRegisterData.phoneNo);
					FlurryEventLogger.otpThroughCall(facebookRegisterData.phoneNo);
				} else {
					initiateOTPCallAsync(OTPConfirmScreen.this, emailRegisterData.phoneNo);
					FlurryEventLogger.otpThroughCall(emailRegisterData.phoneNo);
				}
			}
		});
		
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		try {
			if(RegisterScreen.facebookLogin){
				otpHelpText.setText(otpHelpStr + " " + facebookRegisterData.phoneNo);
			}
			else{
				otpHelpText.setText(otpHelpStr + " " + emailRegisterData.phoneNo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(intentFromRegister){
			startWaitingTimer();
			callMeBtn.setBackgroundResource(R.drawable.black_btn_selector);
		}
		else{
			stopWaitingTimer();
			callMeBtn.setBackgroundResource(R.drawable.blue_btn_selector);
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
		if(Data.locationFetcher == null){
			Data.locationFetcher = new LocationFetcher(OTPConfirmScreen.this, 1000, 1);
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
	
	
	
	Timer waitingTimer;
	TimerTask waitingTimerTask;
	int timerCount = 30;
	
	public void startWaitingTimer(){
		try{
			stopWaitingTimer();
			waitingTimer = new Timer();
			waitingTimerTask = new TimerTask() {
				
				@Override
				public void run() {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if(timerCount > 1){
								pleaseWaitText.setText(waitStr + timerCount + " seconds");
							}
							else{
								pleaseWaitText.setText(waitStr + timerCount + " second");
							}
							if(timerCount <= 0){
								stopWaitingTimer();
								callMeBtn.setBackgroundResource(R.drawable.blue_btn_selector);
							}
							timerCount--;
						}
					});
				}
			};
			pleaseWaitText.setVisibility(View.VISIBLE);
			timerCount = 30;
			waitingTimer.scheduleAtFixedRate(waitingTimerTask, 1000, 1000);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void stopWaitingTimer(){
		try{
			if(waitingTimerTask != null){
				waitingTimerTask.cancel();
			}
			if(waitingTimer != null){
				waitingTimer.cancel();
				waitingTimer.purge();
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		finally{
			waitingTimerTask = null;
			waitingTimer = null;
			timerCount = 30;
			pleaseWaitText.setVisibility(View.GONE);
		}
	}
	
	
	
	
	public void startPleaseWaitHandler(){
		pleaseWaitText.setVisibility(View.VISIBLE);
		pleaseWaitText.setText("Please wait for some time...");
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				pleaseWaitText.setVisibility(View.GONE);
			}
		}, 20 * 1000);
	}
	
	
	
	
	/**
	 * ASync for confirming otp from server
	 */
	public void verifyOtpViaEmail(final Activity activity, String otp) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
		
			if(Data.locationFetcher != null){
				Data.latitude = Data.locationFetcher.getLatitude();
				Data.longitude = Data.locationFetcher.getLongitude();
			}

		
			params.put("email", emailRegisterData.emailId);
			params.put("password", emailRegisterData.password);
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
			params.put("otp", otp);
			

			Log.i("email", emailRegisterData.emailId);
			Log.i("password", emailRegisterData.password);
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
			Log.i("otp", otp);
			
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/verify_otp", params,
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
									else if(ApiResponseFlags.AUTH_VERIFICATION_FAILURE.getOrdinal() == flag){
										String error = jObj.getString("error");
										new DialogPopup().alertPopup(activity, "", error);
									}
									else if(ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag){
										if(!SplashNewActivity.checkIfUpdate(jObj, activity)){
											new JSONParser().parseAccessTokenLoginData(activity, response);
											Database.getInstance(OTPConfirmScreen.this).insertEmail(emailRegisterData.emailId);
											Database.getInstance(OTPConfirmScreen.this).close();
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
	
	
	
	

	public void verifyOtpViaFB(final Activity activity, String otp) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
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
			params.put("otp", otp);
			

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
			Log.i("otp", otp);
			
			
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/verify_otp", params,
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
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								int flag = jObj.getInt("flag");
								
								if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
									if(ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag){
										String error = jObj.getString("error");
										new DialogPopup().alertPopup(activity, "", error);
									}
									else if(ApiResponseFlags.AUTH_VERIFICATION_FAILURE.getOrdinal() == flag){
										String error = jObj.getString("error");
										new DialogPopup().alertPopup(activity, "", error);
									}
									else if(ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag){
										if(!SplashNewActivity.checkIfUpdate(jObj, activity)){
											new JSONParser().parseAccessTokenLoginData(activity, response);
											loginDataFetched = true;
											Database.getInstance(OTPConfirmScreen.this).insertEmail(Data.facebookUserData.userEmail);
											Database.getInstance(OTPConfirmScreen.this).close();
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
	 * ASync for initiating OTP Call from server
	 */
	public void initiateOTPCallAsync(final Activity activity, String phoneNo) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
		
			params.put("phone_no", phoneNo);
			Log.i("phone_no", ">"+phoneNo);
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/send_otp_via_call", params,
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

								if(!jObj.isNull("error")){
									String errorMessage = jObj.getString("error");
									int flag = jObj.getInt("flag");
									if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
										HomeActivity.logoutUser(activity);
									}
									else if(ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag){
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									else{
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									DialogPopup.dismissLoadingDialog();
								}
								else{
									String message = jObj.getString("message");
									int flag = jObj.getInt("flag");
									if(ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag){
										new DialogPopup().alertPopup(activity, "", message);
									}
									startPleaseWaitHandler();
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
	
	
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		if(hasFocus && loginDataFetched){
			stopWaitingTimer();
			loginDataFetched = false;
			Database2.getInstance(OTPConfirmScreen.this).updateDriverLastLocationTime();
			Database2.getInstance(OTPConfirmScreen.this).close();
			startActivity(new Intent(OTPConfirmScreen.this, HomeActivity.class));
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
			finish();
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
		if(intentFromRegister){
			Intent intent = new Intent(OTPConfirmScreen.this, RegisterScreen.class);
			intent.putExtra("back_from_otp", true);
			startActivity(intent);
		}
		else{
			Intent intent = new Intent(OTPConfirmScreen.this, SplashLogin.class);
			intent.putExtra("back_from_otp", true);
			startActivity(intent);
		}
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}
	
	
	
	
	@Override
	protected void onDestroy() {
		try{
			if(Data.locationFetcher != null){
				Data.locationFetcher.destroy();
				Data.locationFetcher = null;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
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


class EmailRegisterData{
	String name, emailId, phoneNo, password, referralCode;
	public EmailRegisterData(String name, String emailId, String phoneNo, String password, String referralCode){
		this.name = name;
		this.emailId = emailId;
		this.phoneNo = phoneNo;
		this.password = password;
		this.referralCode = referralCode;
	}
}

class FacebookRegisterData{
	String phoneNo, password, referralCode;
	public FacebookRegisterData(String phoneNo, String password, String referralCode){
		this.phoneNo = phoneNo;
		this.password = password;
		this.referralCode = referralCode;
	}
}

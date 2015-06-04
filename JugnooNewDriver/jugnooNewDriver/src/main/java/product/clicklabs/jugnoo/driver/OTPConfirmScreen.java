package product.clicklabs.jugnoo.driver;

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

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.driver.utils.DeviceTokenGenerator;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.IDeviceTokenReceiver;
import product.clicklabs.jugnoo.driver.utils.Log;
import rmn.androidscreenlibrary.ASSL;

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

	String otpHelpStr = "Please enter the One Time Password you just received via SMS at ";
	String waitStr = "Please wait for ";
	
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_otp_confrim);
		
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
					sendSignupValues(OTPConfirmScreen.this, otpCode);
					FlurryEventLogger.otpConfirmClick(otpCode);
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
				initiateOTPCallAsync(OTPConfirmScreen.this, emailRegisterData.phoneNo);
				FlurryEventLogger.otpThroughCall(emailRegisterData.phoneNo);
			}
		});
		
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		try {
			otpHelpText.setText(otpHelpStr + " " + emailRegisterData.phoneNo);
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
		try {
			if(Data.locationFetcher == null){
				Data.locationFetcher = new LocationFetcher(this, 1000, 1);
			}
			Data.locationFetcher.connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	public void sendSignupValues(final Activity activity, String otp) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
		
			if(Data.locationFetcher != null){
				Data.latitude = Data.locationFetcher.getLatitude();
				Data.longitude = Data.locationFetcher.getLongitude();
			}

		
			params.put("user_name", emailRegisterData.name);
			params.put("ph_no", emailRegisterData.phoneNo);
			params.put("email", emailRegisterData.emailId);
			params.put("password", emailRegisterData.password);
			params.put("otp", otp);
			params.put("device_type", Data.DEVICE_TYPE);
			params.put("unique_device_id", Data.uniqueDeviceId);
			params.put("device_token", Data.deviceToken);
			params.put("latitude", ""+Data.latitude);
			params.put("longitude", ""+Data.longitude);
			params.put("country", Data.country);
			params.put("device_name", Data.deviceName);
			params.put("app_version", Data.appVersion);
			params.put("os_version", Data.osVersion);
			params.put("referral_code", "");


			Log.i("user_name", "=" + emailRegisterData.name);
			Log.i("ph_no", "=" + emailRegisterData.phoneNo);
			Log.i("email", "=" + emailRegisterData.emailId);
			Log.i("password", "=" + emailRegisterData.password);
			Log.i("otp", "=" + otp);
			Log.i("device_token", "=" + Data.deviceToken);
			Log.i("latitude", "=" + Data.latitude);
			Log.i("longitude", "=" + Data.longitude);
			Log.i("country", "=" + Data.country);
			Log.i("device_name", "=" + Data.deviceName);
			Log.i("app_version", "=" + Data.appVersion);
			Log.i("os_version", "=" + Data.osVersion);
			Log.i("unique_device_id", "=" + Data.uniqueDeviceId);
			
			
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/customer_registeration", params,
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
								
								if(!jObj.isNull("error")){
									DialogPopup.dismissLoadingDialog();
									String errorMessage = jObj.getString("error");
									if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
										HomeActivity.logoutUser(activity);
									}
									else{
										DialogPopup.alertPopup(activity, "", errorMessage);
									}
								}
								else{
									new JSONParser().parseLoginData(activity, response);
									
									Database.getInstance(OTPConfirmScreen.this).insertEmail(emailRegisterData.emailId);
									Database.getInstance(OTPConfirmScreen.this).close();
									
									loginDataFetched = true;
									
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
					});
		}
		else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
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
							DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
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
										DialogPopup.alertPopup(activity, "", errorMessage);
									}
									else{
										DialogPopup.alertPopup(activity, "", errorMessage);
									}
									DialogPopup.dismissLoadingDialog();
								}
								else{
									String message = jObj.getString("message");
									int flag = jObj.getInt("flag");
									if(ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag){
										DialogPopup.alertPopup(activity, "", message);
									}
									startPleaseWaitHandler();
									DialogPopup.dismissLoadingDialog();
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
								DialogPopup.dismissLoadingDialog();
							}
						}
					});
		}
		else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}
	
	
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		if(hasFocus && loginDataFetched){
			stopWaitingTimer();
			loginDataFetched = false;
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
	String name, emailId, phoneNo, password;
	public EmailRegisterData(String name, String emailId, String phoneNo, String password){
		this.name = name;
		this.emailId = emailId;
		this.phoneNo = phoneNo;
		this.password = password;
	}
}

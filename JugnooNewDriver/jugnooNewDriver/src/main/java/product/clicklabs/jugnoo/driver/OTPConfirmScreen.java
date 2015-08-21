package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.flurry.android.FlurryAgent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

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
	
	ImageView imageViewBack;
	TextView textViewTitle;
	
	TextView otpHelpText;
	EditText editTextOTP;
	Button buttonVerify;

    RelativeLayout relativeLayoutOTPThroughCall, relativeLayoutChangePhone;
	
	LinearLayout relative;
	
	boolean loginDataFetched = false;
	
	public static boolean intentFromRegister = true;
	public static EmailRegisterData emailRegisterData;

	String otpHelpStr = "Please enter the One Time Password you just received via SMS at ";

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

        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
        textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Data.latoRegular(this));
		
		otpHelpText = (TextView) findViewById(R.id.otpHelpText); otpHelpText.setTypeface(Data.latoRegular(this));
        editTextOTP = (EditText) findViewById(R.id.editTextOTP); editTextOTP.setTypeface(Data.latoRegular(this));

        buttonVerify = (Button) findViewById(R.id.buttonVerify); buttonVerify.setTypeface(Data.latoRegular(this));

        relativeLayoutOTPThroughCall = (RelativeLayout) findViewById(R.id.relativeLayoutOTPThroughCall);
        relativeLayoutChangePhone = (RelativeLayout) findViewById(R.id.relativeLayoutChangePhone);

        ((TextView)findViewById(R.id.textViewOTPNotReceived)).setTypeface(Data.latoRegular(this));
        ((TextView)findViewById(R.id.textViewCallMe)).setTypeface(Data.latoRegular(this), Typeface.BOLD);

        ((TextView)findViewById(R.id.textViewChangePhone)).setTypeface(Data.latoRegular(this));
        ((TextView)findViewById(R.id.textViewChange)).setTypeface(Data.latoRegular(this), Typeface.BOLD);


        imageViewBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                performBackPressed();
            }
        });

        editTextOTP.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    editTextOTP.setError(null);
                }
            }
        });


        buttonVerify.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String otpCode = editTextOTP.getText().toString().trim();
                if (otpCode.length() > 0) {
                    sendSignupValues(OTPConfirmScreen.this, otpCode);
                    FlurryEventLogger.otpConfirmClick(otpCode);
                } else {
                    editTextOTP.requestFocus();
                    editTextOTP.setError("Code can't be empty");
                }

            }
        });

        editTextOTP.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                int result = actionId & EditorInfo.IME_MASK_ACTION;
                switch (result) {
                    case EditorInfo.IME_ACTION_DONE:
                        buttonVerify.performClick();
                        break;

                    case EditorInfo.IME_ACTION_NEXT:
                        break;

                    default:
                }
                return true;
            }
        });


        relativeLayoutOTPThroughCall.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                initiateOTPCallAsync(OTPConfirmScreen.this, emailRegisterData.phoneNo);
                FlurryEventLogger.otpThroughCall(emailRegisterData.phoneNo);
            }
        });

        relativeLayoutChangePhone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(OTPConfirmScreen.this, ChangePhoneBeforeOTPActivity.class));
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });
		
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		try {
			otpHelpText.setText(otpHelpStr + " " + emailRegisterData.phoneNo);
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

        checkIfRegisterDataNull(this);
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

    public static void checkIfRegisterDataNull(Activity activity){
        try {
            if(emailRegisterData == null){
                activity.startActivity(new Intent(activity, SplashNewActivity.class));
                activity.finish();
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            params.put("login_type", Data.LOGIN_TYPE);
            params.put("otp", otp);

            Log.i("params", "" + params.toString());

			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/verify_otp", params,
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

                                int flag = jObj.getInt("flag");
                                String message = JSONParser.getServerMessage(jObj);
                                if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag)){
                                    if(ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag){
                                        DialogPopup.alertPopup(activity, "", message);
                                    }
                                    else if(ApiResponseFlags.AUTH_VERIFICATION_FAILURE.getOrdinal() == flag){
                                        DialogPopup.alertPopup(activity, "", message);
                                    }
									else if(ApiResponseFlags.CUSTOMER_LOGGING_IN.getOrdinal() == flag){
                                        DialogPopup.alertPopup(activity, "", message);
									}
                                    else if(ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag){
                                        DialogPopup.alertPopup(activity, "", message);
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
                                int flag = jObj.getInt("flag");
                                String message = JSONParser.getServerMessage(jObj);
                                if(ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag){
                                    DialogPopup.alertPopup(activity, "", message);
                                }
                                else if(ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag){
                                    DialogPopup.alertPopup(activity, "", message);
                                }
                                else{
                                    DialogPopup.alertPopup(activity, "", message);
                                }
                                DialogPopup.dismissLoadingDialog();
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
	String name, emailId, phoneNo, password, accessToken;
	public EmailRegisterData(String name, String emailId, String phoneNo, String password, String accessToken){
		this.name = name;
		this.emailId = emailId;
		this.phoneNo = phoneNo;
		this.password = password;
		this.accessToken = accessToken;
	}
}

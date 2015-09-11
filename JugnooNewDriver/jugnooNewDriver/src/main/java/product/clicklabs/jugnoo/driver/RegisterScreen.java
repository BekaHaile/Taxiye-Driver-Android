package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.PreviousAccountInfo;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.driver.utils.DeviceTokenGenerator;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.IDeviceTokenReceiver;
import product.clicklabs.jugnoo.driver.utils.Log;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import rmn.androidscreenlibrary.ASSL;

public class RegisterScreen extends Activity implements LocationUpdate{
	
	Button backBtn;
	TextView title;
	
	EditText nameEt, emailIdEt, phoneNoEt, passwordEt, confirmPasswordEt;
	Button signUpBtn;

	LinearLayout relative;
	
	String name = "", emailId = "", phoneNo = "", password = "", accessToken = "";
	
	boolean sendToOtpScreen = false;

    public static JSONObject multipleCaseJSON;


	public void resetFlags(){
		sendToOtpScreen = false;
	}
	
	// *****************************Used for flurry work***************//
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.init(this, Data.FLURRY_KEY);
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
		setContentView(R.layout.activity_register);


		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(RegisterScreen.this, relative, 1134, 720, false);
		
		backBtn = (Button) findViewById(R.id.backBtn); backBtn.setTypeface(Data.latoRegular(getApplicationContext()));
		title = (TextView) findViewById(R.id.title); title.setTypeface(Data.latoRegular(getApplicationContext()));

		nameEt = (EditText) findViewById(R.id.nameEt); nameEt.setTypeface(Data.latoRegular(getApplicationContext()));
		emailIdEt = (EditText) findViewById(R.id.emailIdEt); emailIdEt.setTypeface(Data.latoRegular(getApplicationContext()));
		phoneNoEt = (EditText) findViewById(R.id.phoneNoEt); phoneNoEt.setTypeface(Data.latoRegular(getApplicationContext()));
		passwordEt = (EditText) findViewById(R.id.passwordEt); passwordEt.setTypeface(Data.latoRegular(getApplicationContext()));
		confirmPasswordEt = (EditText) findViewById(R.id.confirmPasswordEt); confirmPasswordEt.setTypeface(Data.latoRegular(getApplicationContext()));
		
		signUpBtn = (Button) findViewById(R.id.signUpBtn); signUpBtn.setTypeface(Data.latoRegular(getApplicationContext()));

		

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
				String emailId = emailIdEt.getText().toString().trim();


				
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
										else {
											if (isEmailValid(emailId)) {
												if (isPhoneValid(phoneNo)) {
													if (password.equals(confirmPassword)) {
														if (password.length() >= 6) {
//															sendSignupValues(RegisterScreen.this, name, emailId, phoneNo, password);
															sendSignupValuesRetro(RegisterScreen.this, name, emailId, phoneNo, password);
															FlurryEventLogger.emailSignupClicked(emailId);
														} else {
															passwordEt.requestFocus();
															passwordEt.setError("Password must be of atleast six characters");
														}
													} else {
														passwordEt.requestFocus();
														passwordEt.setError("Passwords does not match");
													}
												} else {
													phoneNoEt.requestFocus();
													phoneNoEt.setError("Please enter valid phone number");
												}
											} else {
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


		confirmPasswordEt.setOnEditorActionListener(new OnEditorActionListener() {

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
		
		

		try {
			if(getIntent().hasExtra("back_from_otp")) {
				nameEt.setText(OTPConfirmScreen.emailRegisterData.name);
				emailIdEt.setText(OTPConfirmScreen.emailRegisterData.emailId);
				phoneNoEt.setText(OTPConfirmScreen.emailRegisterData.phoneNo);
				passwordEt.setText(OTPConfirmScreen.emailRegisterData.password);
				confirmPasswordEt.setText(OTPConfirmScreen.emailRegisterData.password);
			}
            nameEt.setSelection(nameEt.getText().length());
		} catch (Exception e) {
			e.printStackTrace();
		}
		

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
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		
		super.onPause();
		
		
	}
	
	
	
	
	/**
	 * ASync for register from server
	 */
	public void sendSignupValues(final Activity activity, final String name,
			final String emailId, final String phoneNo, final String password) {
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
            params.put("latitude", "" + Data.latitude);
            params.put("longitude", "" + Data.longitude);

            params.put("device_type", Data.DEVICE_TYPE);
            params.put("device_name", Data.deviceName);
            params.put("app_version", "" + Data.appVersion);
            params.put("os_version", Data.osVersion);
            params.put("country", Data.country);

            params.put("client_id", Data.CLIENT_ID);
			params.put("login_type", Data.LOGIN_TYPE);
            params.put("referral_code", "");

            params.put("device_token", Data.deviceToken);
            params.put("unique_device_id", Data.uniqueDeviceId);

            Log.i("register_using_email params", params.toString());



		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/register_using_email", params,
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

                                if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
                                    int flag = jObj.getInt("flag");
                                    String message = JSONParser.getServerMessage(jObj);

                                    if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag)) {

                                        if (ApiResponseFlags.AUTH_REGISTRATION_FAILURE.getOrdinal() == flag) {
                                            DialogPopup.alertPopup(activity, "", message);
                                        } else if (ApiResponseFlags.AUTH_ALREADY_REGISTERED.getOrdinal() == flag) {
                                            DialogPopup.alertPopup(activity, "", message);
                                        } else if (ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag) {
                                            RegisterScreen.this.name = name;
                                            RegisterScreen.this.emailId = emailId;
                                            RegisterScreen.this.phoneNo = jObj.getString("phone_no");
                                            RegisterScreen.this.password = password;
                                            RegisterScreen.this.accessToken = jObj.getString("access_token");
                                            sendToOtpScreen = true;
                                        } else if (ApiResponseFlags.AUTH_DUPLICATE_REGISTRATION.getOrdinal() == flag) {
                                            RegisterScreen.this.name = name;
                                            RegisterScreen.this.emailId = emailId;
                                            RegisterScreen.this.phoneNo = phoneNo;
                                            RegisterScreen.this.password = password;
                                            RegisterScreen.this.accessToken = "";
                                            parseDataSendToMultipleAccountsScreen(activity, jObj);
                                        } else {
                                            DialogPopup.alertPopup(activity, "", message);
                                        }
                                        DialogPopup.dismissLoadingDialog();
                                    }
                                } else {
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

//	Retrofit

	public void sendSignupValuesRetro(final Activity activity, final String name,
								 final String emailId, final String phoneNo, final String password) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			resetFlags();
			DialogPopup.showLoadingDialog(activity, "Loading...");

//			RequestParams params = new RequestParams();

			if(Data.locationFetcher != null){
				Data.latitude = Data.locationFetcher.getLatitude();
				Data.longitude = Data.locationFetcher.getLongitude();
			}

			HashMap<String, String> params = new HashMap<String, String>();
			params.put("user_name", name);
			params.put("phone_no", phoneNo);
			params.put("email", emailId);
			params.put("password", password);
			params.put("latitude", "" + Data.latitude);
			params.put("longitude", "" + Data.longitude);

			params.put("device_type", Data.DEVICE_TYPE);
			params.put("device_name", Data.deviceName);
			params.put("app_version", "" + Data.appVersion);
			params.put("os_version", Data.osVersion);
			params.put("country", Data.country);

			params.put("client_id", Data.CLIENT_ID);
			params.put("login_type", Data.LOGIN_TYPE);
			params.put("referral_code", "");

			params.put("device_token", Data.deviceToken);
			params.put("unique_device_id", Data.uniqueDeviceId);

			Log.i("register_using_email params", params.toString());

			RestClient.getApiServices().sendSignupValuesRetro(params, new Callback<RegisterScreenResponse>() {
				@Override
				public void success(RegisterScreenResponse registerScreenResponse, Response response) {
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj;
						jObj = new JSONObject(jsonString);

						if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
							int flag = jObj.getInt("flag");
							String message = JSONParser.getServerMessage(jObj);

							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag)) {

								if (ApiResponseFlags.AUTH_REGISTRATION_FAILURE.getOrdinal() == flag) {
									DialogPopup.alertPopup(activity, "", message);
								} else if (ApiResponseFlags.AUTH_ALREADY_REGISTERED.getOrdinal() == flag) {
									DialogPopup.alertPopup(activity, "", message);
								} else if (ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag) {
									RegisterScreenResponse data = registerScreenResponse;
									RegisterScreen.this.name = name;
									RegisterScreen.this.emailId = emailId;
									RegisterScreen.this.phoneNo = data.getPhoneNo();
									RegisterScreen.this.password = password;
									RegisterScreen.this.accessToken = data.getAccessToken();



									sendToOtpScreen = true;
								} else if (ApiResponseFlags.AUTH_DUPLICATE_REGISTRATION.getOrdinal() == flag) {
									RegisterScreen.this.name = name;
									RegisterScreen.this.emailId = emailId;
									RegisterScreen.this.phoneNo = phoneNo;
									RegisterScreen.this.password = password;
									RegisterScreen.this.accessToken = "";
									parseDataSendToMultipleAccountsScreen(activity, jObj);
								} else {
									DialogPopup.alertPopup(activity, "", message);
								}
								DialogPopup.dismissLoadingDialog();
							}
						} else {
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
	

	
	public void sendIntentToOtpScreen() {
		OTPConfirmScreen.intentFromRegister = true;
		OTPConfirmScreen.emailRegisterData = new EmailRegisterData(name, emailId, phoneNo, password, accessToken);
		startActivity(new Intent(RegisterScreen.this, OTPConfirmScreen.class));
		finish();
		overridePendingTransition(R.anim.right_in, R.anim.right_out);
	}


    public void parseDataSendToMultipleAccountsScreen(Activity activity, JSONObject jObj) {
        OTPConfirmScreen.emailRegisterData = new EmailRegisterData(name, emailId, phoneNo, password, accessToken);
        RegisterScreen.multipleCaseJSON = jObj;
        if (Data.previousAccountInfoList == null) {
            Data.previousAccountInfoList = new ArrayList<PreviousAccountInfo>();
        }
        Data.previousAccountInfoList.clear();
        Data.previousAccountInfoList.addAll(JSONParser.parsePreviousAccounts(jObj));
        startActivity(new Intent(activity, MultipleAccountsActivity.class));
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }
	
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		
		if(hasFocus && sendToOtpScreen){
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

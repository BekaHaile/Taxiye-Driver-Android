package product.clicklabs.jugnoo.driver.oldRegistration;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONObject;

import java.util.HashMap;

import androidx.annotation.NonNull;
import product.clicklabs.jugnoo.driver.ChangePhoneBeforeOTPActivity;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.HomeUtil;
import product.clicklabs.jugnoo.driver.JSONParser;
import product.clicklabs.jugnoo.driver.LocationFetcher;
import product.clicklabs.jugnoo.driver.LocationUpdate;
import product.clicklabs.jugnoo.driver.LoginViaOTP;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.SplashNewActivity;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.EmailRegisterData;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.BookingHistoryResponse;
import product.clicklabs.jugnoo.driver.ui.DriverSplashActivity;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Log;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class OldOTPConfirmScreen extends BaseActivity implements LocationUpdate {

	View backBtn;
	TextView title;

	TextView otpHelpText;
	EditText editTextOTP;
	Button buttonVerify;

	RelativeLayout relativeLayoutOTPThroughCall, relativeLayoutChangePhone;

	LinearLayout relative;

	boolean loginDataFetched = false;

	public static boolean intentFromRegister = true;
	public static EmailRegisterData emailRegisterData;


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
		setContentView(R.layout.activity_old_otp_confrim);

		loginDataFetched = false;

		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(OldOTPConfirmScreen.this, relative, 1134, 720, false);

		backBtn = (ImageView) findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Fonts.mavenRegular(this));
		title.setText(R.string.verification);

		otpHelpText = (TextView) findViewById(R.id.otpHelpText);
		otpHelpText.setTypeface(Fonts.mavenRegular(this));
		editTextOTP = (EditText) findViewById(R.id.editTextOTP);
		editTextOTP.setTypeface(Fonts.mavenRegular(this));

		buttonVerify = (Button) findViewById(R.id.buttonVerify);
		buttonVerify.setTypeface(Fonts.mavenRegular(this));

		relativeLayoutOTPThroughCall = (RelativeLayout) findViewById(R.id.relativeLayoutOTPThroughCall);
		relativeLayoutChangePhone = (RelativeLayout) findViewById(R.id.relativeLayoutChangePhone);

		((TextView) findViewById(R.id.textViewOTPNotReceived)).setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.textViewCallMe)).setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);

		((TextView) findViewById(R.id.textViewChangePhone)).setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.textViewChange)).setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);


		backBtn.setOnClickListener(new View.OnClickListener() {

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
                    sendSignupValues(OldOTPConfirmScreen.this, otpCode);
					FlurryEventLogger.otpConfirmClick(otpCode);
				} else {
					editTextOTP.requestFocus();
					editTextOTP.setError(getResources().getString(R.string.code_empty));
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
                initiateOTPCallAsync(OldOTPConfirmScreen.this, emailRegisterData.phoneNo);
//				initiateOTPCall(OldOTPConfirmScreen.this, emailRegisterData.phoneNo);
				FlurryEventLogger.otpThroughCall(emailRegisterData.phoneNo);
			}
		});

		relativeLayoutChangePhone.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(OldOTPConfirmScreen.this, ChangePhoneBeforeOTPActivity.class));
				finish();
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});


		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		try {
			otpHelpText.setText(getResources().getString(R.string.enter_otp_received) + " " + emailRegisterData.phoneNo);
		} catch (Exception e) {
			e.printStackTrace();
		}

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

	}


	@Override
	protected void onResume() {
		super.onResume();
		try {
			if (Data.locationFetcher == null) {
				Data.locationFetcher = new LocationFetcher(this, 1000, 1);
			}
			Data.locationFetcher.connect();
		} catch (Exception e) {
			e.printStackTrace();
		}

		checkIfRegisterDataNull(this);
	}

	@Override
	public void onPause() {
		try {
			if (Data.locationFetcher != null) {
				Data.locationFetcher.destroy();
				Data.locationFetcher = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onPause();
	}

	public static void checkIfRegisterDataNull(Activity activity) {
		try {
			if (emailRegisterData == null) {
				activity.startActivity(new Intent(activity, DriverSplashActivity.class));
				activity.finish();
				activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	//	Retrofit
	public void sendSignupValues(final Activity activity, String otp) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

			DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));

//		RequestParams params = new RequestParams();

			if (Data.locationFetcher != null) {
				Data.latitude = Data.locationFetcher.getLatitude();
				Data.longitude = Data.locationFetcher.getLongitude();
			}
			HashMap<String, String> params = new HashMap<>();
			params.put("email",emailRegisterData.emailId);
			params.put("password",emailRegisterData.password);
			params.put("device_token",Data.deviceToken);
			params.put("device_type",Data.DEVICE_TYPE);
			params.put("device_name",Data.deviceName);
			params.put("app_version",Data.appVersion+"");
			params.put("os_version",Data.osVersion);
			params.put("country",Data.country);
			params.put("unique_device_id",Data.uniqueDeviceId);
			params.put("latitude",Data.latitude+"");
			params.put("longitude",Data.longitude+"");
			params.put("client_id",Data.CLIENT_ID);
			params.put("login_type",Data.LOGIN_TYPE);
			params.put("otp",otp);
			HomeUtil.putDefaultParams(params);

			RestClient.getApiServices().verifyOtpOldUsingSignupFields(params, new Callback<BookingHistoryResponse>() {


						@Override
						public void success(BookingHistoryResponse bookingHistoryResponse, Response response) {
							try {
								String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
								JSONObject jObj;
								jObj = new JSONObject(jsonString);
								int flag = jObj.getInt("flag");
								String message = JSONParser.getServerMessage(jObj);
								if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag, null)) {
									if (ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag) {
										DialogPopup.alertPopup(activity, "", message);
									} else if (ApiResponseFlags.AUTH_VERIFICATION_FAILURE.getOrdinal() == flag) {
										DialogPopup.alertPopup(activity, "", message);
									} else if (ApiResponseFlags.CUSTOMER_LOGGING_IN.getOrdinal() == flag) {
										DialogPopup.alertPopup(activity, "", message);
									} else if (ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag) {
										DialogPopup.alertPopup(activity, "", message);
									} else {
										DialogPopup.alertPopup(activity, "", message);
									}
									DialogPopup.dismissLoadingDialog();
								} else {
									DialogPopup.dismissLoadingDialog();
								}

							} catch (Exception exception) {
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


		} else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}


//	Retrofit


	public void initiateOTPCallAsync(final Activity activity, String phoneNo) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

			DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));
			HashMap<String, String> params = new HashMap<>();
			params.put("phone_no",phoneNo);
			HomeUtil.putDefaultParams(params);
			RestClient.getApiServices().initiateOTPCall(params, new Callback<BookingHistoryResponse>() {


				@Override
				public void success(BookingHistoryResponse bookingHistoryResponse, Response response) {
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj;
						jObj = new JSONObject(jsonString);
						int flag = jObj.getInt("flag");
						String message = JSONParser.getServerMessage(jObj);
						if (ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag) {
							DialogPopup.alertPopup(activity, "", message);
						} else if (ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag) {
							DialogPopup.alertPopup(activity, "", message);
						} else {
							DialogPopup.alertPopup(activity, "", message);
						}
						DialogPopup.dismissLoadingDialog();

					} catch (Exception exception) {
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

		} else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}


	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if (hasFocus && loginDataFetched) {
			loginDataFetched = false;
			startActivity(new Intent(OldOTPConfirmScreen.this, HomeActivity.class));
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


	public void performBackPressed() {
		if (intentFromRegister) {
			Intent intent = new Intent(OldOTPConfirmScreen.this, OldRegisterScreen.class);
			intent.putExtra("back_from_otp", true);
			startActivity(intent);
		} else {
			Intent intent = new Intent(OldOTPConfirmScreen.this, LoginViaOTP.class);
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



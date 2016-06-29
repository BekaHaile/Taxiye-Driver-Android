package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by aneeshbansal on 29/03/16.
 */
public class LoginViaOTP extends BaseActivity {

	LinearLayout linearLayoutWaiting, otpETextLLayout, selectLanguageLl, layoutResendOtp, mainLoginLinear, mainLinear, btnReGenerateOtp, btnOtpViaCall;
	RelativeLayout relative;
	EditText phoneNoEt, otpEt;
	Button backBtn, btnGenerateOtp, loginViaOtp, btnLogin;
	ImageView imageViewYellowLoadingBar;
	TextView textViewCounter, textViewOr;
	String selectedLanguage = Prefs.with(LoginViaOTP.this).getString(SPLabels.SELECTED_LANGUAGE, "");
	int languagePrefStatus;
	Configuration conf;
	String knowlarityMissedCallNumber = "";
	Spinner spinner;
	public static String OTP_SCREEN_OPEN = null;
	List<String> categories = new ArrayList<>();

	String phoneNoOfLoginAccount = "", accessToken = "", otpErrorMsg = "";

	String enteredEmail = "";
	String enteredPhone = "";



	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onNewIntent(Intent intent) {

		if (intent.hasExtra("message")) {
			retrieveOTPFromSMS(intent);
		} else if (intent.hasExtra("otp")) {
//			retrieveOTPFromPush(intent);
		}

		super.onNewIntent(intent);
	}


	public void onCreate(Bundle savedInstanceState) {

		fetchLanguageList();
		Utils.enableReceiver(LoginViaOTP.this, IncomingSmsReceiver.class, true);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signin_otp);

		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(LoginViaOTP.this, relative, 1134, 720, false);

		phoneNoEt = (EditText) findViewById(R.id.phoneNoEt);
		phoneNoEt.setHint(getStringText(R.string.phone_number));
		phoneNoEt.setTypeface(Data.latoRegular(getApplicationContext()));
		phoneNoEt.setEnabled(true);
		otpEt = (EditText) findViewById(R.id.otpEt);
		otpEt.setTypeface(Data.latoRegular(getApplicationContext()));
		otpEt.setEnabled(false);
		linearLayoutWaiting = (LinearLayout) findViewById(R.id.linearLayoutWaiting);
		selectLanguageLl = (LinearLayout) findViewById(R.id.selectLanguageLl);
		layoutResendOtp = (LinearLayout) findViewById(R.id.layoutResendOtp);
		otpETextLLayout = (LinearLayout) findViewById(R.id.otpETextLLayout);

		mainLoginLinear = (LinearLayout) findViewById(R.id.mainLoginLinear);
		mainLinear = (LinearLayout) findViewById(R.id.mainLinear);

		backBtn = (Button) findViewById(R.id.backBtn);
		backBtn.setTypeface(Data.latoRegular(getApplicationContext()));
		btnGenerateOtp = (Button) findViewById(R.id.btnGenerateOtp);
		btnGenerateOtp.setTypeface(Data.latoRegular(getApplicationContext()));
		btnReGenerateOtp = (LinearLayout) findViewById(R.id.btnReGenerateOtp);

		btnOtpViaCall = (LinearLayout) findViewById(R.id.btnOtpViaCall);

		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setTypeface(Data.latoRegular(getApplicationContext()));

		loginViaOtp = (Button) findViewById(R.id.loginViaOtp);
		loginViaOtp.setTypeface(Data.latoRegular(getApplicationContext()));
		spinner = (Spinner) findViewById(R.id.language_spinner);
		imageViewYellowLoadingBar = (ImageView) findViewById(R.id.imageViewYellowLoadingBar);
		textViewCounter = (TextView) findViewById(R.id.textViewCounter);
		textViewCounter.setTypeface(Data.latoRegular(getApplicationContext()));

		textViewOr = (TextView) findViewById(R.id.textViewOr);
		textViewOr.setTypeface(Data.latoRegular(getApplicationContext()));


		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performbackPressed();
			}
		});

		relative.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				otpEt.setError(null);
			}
		});

		btnGenerateOtp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String phoneNo = phoneNoEt.getText().toString().trim();
				if ("".equalsIgnoreCase(phoneNo)) {
					phoneNoEt.requestFocus();
					phoneNoEt.setError(getResources().getString(R.string.enter_phone_number));

				} else if ((Utils.validPhoneNumber(phoneNo))) {
					phoneNoEt.setEnabled(false);
					generateOTP(phoneNo);
				} else {
					phoneNoEt.requestFocus();
					phoneNoEt.setError(getResources().getString(R.string.enter_phone_number));
				}
			}
		});


		btnReGenerateOtp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String phoneNo = phoneNoEt.getText().toString().trim();
				if ("".equalsIgnoreCase(phoneNo)) {
					phoneNoEt.requestFocus();
					phoneNoEt.setError(getResources().getString(R.string.enter_phone_number));
				} else if ((Utils.validPhoneNumber(phoneNo))) {
					generateOTP(phoneNo);
				} else {
					phoneNoEt.requestFocus();
					phoneNoEt.setError(getResources().getString(R.string.valid_phone_number));
				}
			}
		});


		btnOtpViaCall.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!"".equalsIgnoreCase(knowlarityMissedCallNumber)) {
					DialogPopup.alertPopupTwoButtonsWithListeners(LoginViaOTP.this, "",
							getResources().getString(R.string.give_missed_call_dialog_text),
							getResources().getString(R.string.call_us),
							getResources().getString(R.string.cancel),
							new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									btnLogin.setVisibility(View.VISIBLE);
									layoutResendOtp.setVisibility(View.GONE);
									Utils.openCallIntent(LoginViaOTP.this, knowlarityMissedCallNumber);
								}
							},
							new View.OnClickListener() {
								@Override
								public void onClick(View v) {

								}
							}, false, false
					);
				}
			}
		});


		loginViaOtp.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String otpCode = otpEt.getText().toString().trim();
				if (otpCode.length() > 0) {
					sendLoginValues(LoginViaOTP.this, "", "+91" + String.valueOf(phoneNoEt.getText()), "", otpCode);
				} else {
					otpEt.requestFocus();
					otpEt.setError(getResources().getString(R.string.code_empty));
				}
			}
		});


		btnLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
					sendLoginValues(LoginViaOTP.this, "", "+91" + String.valueOf(phoneNoEt.getText()), "", "99999");
			}
		});

		otpEt.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() > 0) {
					otpEt.setTextSize(20);
				} else {
					otpEt.setTextSize(15);
				}
			}
		});


		try {																						// to get AppVersion, OS version, country code and device name
			Data.filldetails(LoginViaOTP.this);
		} catch (Exception e) {
			Log.e("error in fetching appversion and gcm key", ".." + e.toString());
		}


		Prefs.with(LoginViaOTP.this).save(SPLabels.REQUEST_LOGIN_OTP_FLAG, "false");
		OTP_SCREEN_OPEN = "yes";
		Prefs.with(LoginViaOTP.this).save(SPLabels.LOGIN_VIA_OTP_STATE, true);

		if(System.currentTimeMillis() < (Prefs.with(LoginViaOTP.this).getLong(SPLabels.DRIVER_LOGIN_TIME,0) + 900000)
				&&(!"".equalsIgnoreCase(Prefs.with(LoginViaOTP.this).getString(SPLabels.DRIVER_LOGIN_PHONE_NUMBER, "")))){
			fetchMessages();
		}

	}


	public void fetchLanguageList() {
		try {
			if (AppStatus.getInstance(LoginViaOTP.this).isOnline(LoginViaOTP.this)) {
				DialogPopup.showLoadingDialog(LoginViaOTP.this, getResources().getString(R.string.loading));
				HashMap<String, String> params = new HashMap<>();
				params.put("device_model_name", android.os.Build.MODEL);
				params.put("android_version", android.os.Build.VERSION.RELEASE);

				RestClient.getApiServices().fetchLanguageList(params, new Callback<RegisterScreenResponse>() {
					@Override
					public void success(RegisterScreenResponse registerScreenResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						DialogPopup.dismissLoadingDialog();
						try {
							JSONObject jObj = new JSONObject(responseStr);
							String message = JSONParser.getServerMessage(jObj);
							int flag = jObj.getInt("flag");
							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								languagePrefStatus = jObj.getInt("locale_preference_enabled");
								JSONArray jArray = jObj.getJSONArray("locales");
								if (jArray != null) {
									categories.clear();
									for (int i = 0; i < jArray.length(); i++) {
										categories.add(jArray.get(i).toString());
									}
								}
								showLanguagePreference();

							} else {
								DialogPopup.alertPopup(LoginViaOTP.this, "", message);
							}
						} catch (Exception e) {
							e.printStackTrace();
							DialogPopup.alertPopup(LoginViaOTP.this, "", Data.SERVER_ERROR_MSG);
						}

					}

					@Override
					public void failure(RetrofitError error) {
						DialogPopup.dismissLoadingDialog();
						DialogPopup.alertPopup(LoginViaOTP.this, "", Data.SERVER_ERROR_MSG);
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void generateOTP(final String phoneNo) {
		try {
			if (AppStatus.getInstance(LoginViaOTP.this).isOnline(LoginViaOTP.this)) {
				DialogPopup.showLoadingDialog(LoginViaOTP.this, getResources().getString(R.string.loading));
				HashMap<String, String> params = new HashMap<>();
				params.put("phone_no", "+91" + phoneNo);
				params.put("login_type","1");
				Prefs.with(LoginViaOTP.this).save(SPLabels.DRIVER_LOGIN_PHONE_NUMBER, phoneNo);
				Prefs.with(LoginViaOTP.this).save(SPLabels.DRIVER_LOGIN_TIME, System.currentTimeMillis());

				RestClient.getApiServices().generateOtp(params, new Callback<RegisterScreenResponse>() {
					@Override
					public void success(RegisterScreenResponse registerScreenResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						DialogPopup.dismissLoadingDialog();
						try {
							JSONObject jObj = new JSONObject(responseStr);
							String message = JSONParser.getServerMessage(jObj);
							int flag = jObj.getInt("flag");
							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								DialogPopup.dialogBanner(LoginViaOTP.this, message);
								btnGenerateOtp.setVisibility(View.GONE);
								layoutResendOtp.setVisibility(View.GONE);
								btnLogin.setVisibility(View.GONE);
								loginViaOtp.setVisibility(View.VISIBLE);
								otpETextLLayout.setBackgroundResource(R.drawable.background_white_rounded_orange_bordered);
								otpEt.setEnabled(true);
								linearLayoutWaiting.setVisibility(View.VISIBLE);
								knowlarityMissedCallNumber = jObj.optString("knowlarity_missed_call_number", "");
								Prefs.with(LoginViaOTP.this).save(SPLabels.REQUEST_LOGIN_OTP_FLAG, "true");
								try {
									textViewCounter.setText("0:30");
									customCountDownTimer.start();
								} catch (Exception e) {
									e.printStackTrace();
									linearLayoutWaiting.setVisibility(View.GONE);
								}
							} else {
								DialogPopup.alertPopup(LoginViaOTP.this, "", message);
							}
						} catch (Exception e) {
							e.printStackTrace();
							DialogPopup.alertPopup(LoginViaOTP.this, "", Data.SERVER_ERROR_MSG);
						}

					}

					@Override
					public void failure(RetrofitError error) {
						DialogPopup.dismissLoadingDialog();
						DialogPopup.alertPopup(LoginViaOTP.this, "", Data.SERVER_ERROR_MSG);
					}
				});
			} else {
				DialogPopup.alertPopup(LoginViaOTP.this, "", Data.CHECK_INTERNET_MSG);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	CustomCountDownTimer customCountDownTimer = new CustomCountDownTimer(30000, 5);

	class CustomCountDownTimer extends CountDownTimer {

		private final long mMillisInFuture;

		public CustomCountDownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			mMillisInFuture = millisInFuture;
		}

		@Override
		public void onTick(long millisUntilFinished) {
			double percent = (((double) millisUntilFinished) * 100.0) / mMillisInFuture;

			double widthToSet = percent * ((double) (ASSL.Xscale() * 530)) / 100.0;

			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageViewYellowLoadingBar.getLayoutParams();
			params.width = (int) widthToSet;
			imageViewYellowLoadingBar.setLayoutParams(params);


			long seconds = (long) Math.ceil(((double) millisUntilFinished) / 1000.0d);
			String text = seconds < 10 ? "0:0" + seconds : "0:" + seconds;
			textViewCounter.setText(text);
		}

		@Override
		public void onFinish() {
			linearLayoutWaiting.setVisibility(View.GONE);
			if("".equalsIgnoreCase(knowlarityMissedCallNumber)){
				layoutResendOtp.setVisibility(View.GONE);
			}else {
				layoutResendOtp.setVisibility(View.VISIBLE);
			}
			btnReGenerateOtp.setVisibility(View.VISIBLE);
		}
	}

	private void retrieveOTPFromSMS(Intent intent) {
		try {
			String otp = "";
			if (intent.hasExtra("message")) {
				String message = intent.getStringExtra("message");

				if (message.toLowerCase().contains("paytm")) {
					otp = message.split("\\ ")[0];
				} else {
					String[] arr = message.split("and\\ it\\ is\\ valid\\ till\\ ");
					String[] arr2 = arr[0].split("Dear\\ Driver\\,\\ Your\\ Jugnoo\\ One\\ Time\\ Password\\ is\\ ");
					otp = arr2[1];
					otp = otp.replaceAll("\\ ", "");
				}
			}
			if (Utils.checkIfOnlyDigits(otp)) {
				if (!"".equalsIgnoreCase(otp)) {
					if (Boolean.parseBoolean(Prefs.with(LoginViaOTP.this).getString(SPLabels.REQUEST_LOGIN_OTP_FLAG, "false"))) {
						otpEt.setText(otp);
						otpEt.setSelection(otpEt.getText().length());
						loginViaOtp.performClick();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


//	private void retrieveOTPFromPush(Intent intent) {
//		try {
//			String otp = "";
//			if (intent.hasExtra("otp")) {
//				otp = intent.getStringExtra("otp");
//			}
//			if (Utils.checkIfOnlyDigits(otp)) {
//				if (!"".equalsIgnoreCase(otp)) {
//					if (Boolean.parseBoolean(Prefs.with(LoginViaOTP.this).getString(SPLabels.REQUEST_LOGIN_OTP_FLAG, "false"))) {
//						otpEt.setText(otp);
//						otpEt.setSelection(otpEt.getText().length());
//						loginViaOtp.performClick();
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}


	public void sendLoginValues(final Activity activity, final String emailId, final String phoneNo, final String password, final String otp) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			final Dialog dialogLoading = DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading), true);
			conf = getResources().getConfiguration();
//			RequestParams params = new RequestParams();

			if (Data.locationFetcher != null) {
				Data.latitude = Data.locationFetcher.getLatitude();
				Data.longitude = Data.locationFetcher.getLongitude();
			}

			HashMap<String, String> params = new HashMap<String, String>();

			params.put("email", emailId);
			params.put("phone_no", phoneNo);
			params.put("password", password);
			params.put("login_otp", otp);
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
			params.put("locale", conf.locale.toString());

			if (Utils.isAppInstalled(activity, Data.GADDAR_JUGNOO_APP)) {
				params.put("auto_n_cab_installed", "1");
			} else {
				params.put("auto_n_cab_installed", "0");
			}

			if (Utils.isAppInstalled(activity, Data.UBER_APP)) {
				params.put("uber_installed", "1");
			} else {
				params.put("uber_installed", "0");
			}

			if (Utils.telerickshawInstall(activity)) {
				params.put("telerickshaw_installed", "1");
			} else {
				params.put("telerickshaw_installed", "0");
			}


			if (Utils.olaInstall(activity)) {
				params.put("ola_installed", "1");
			} else {
				params.put("ola_installed", "0");
			}

			if (Utils.isDeviceRooted()) {
				params.put("device_rooted", "1");
			} else {
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

						if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag)) {
							if (ApiResponseFlags.INCORRECT_PASSWORD.getOrdinal() == flag) {
								DialogPopup.alertPopup(activity, "", message);
							} else if (ApiResponseFlags.CUSTOMER_LOGGING_IN.getOrdinal() == flag) {
								SplashNewActivity.sendToCustomerAppPopup("Alert", message, activity);
							} else if (ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag) {
								DialogPopup.alertPopup(activity, "", message);
							} else if (ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag) {
								DialogPopup.alertPopup(activity, "", message);
//								mainLinear.setVisibility(View.VISIBLE);
								layoutResendOtp.setVisibility(View.VISIBLE);
								btnLogin.setVisibility(View.GONE);
								otpEt.setText("");
							} else if (ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag) {
								DialogPopup.alertPopup(activity, "", getResources().getString(R.string.no_not_verified));
							} else if (ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag) {
								if (!SplashNewActivity.checkIfUpdate(jObj.getJSONObject("login"), activity)) {
									new JSONParser().parseAccessTokenLoginData(activity, jsonString);
									startService(new Intent(activity, DriverLocationUpdateService.class));
									Database.getInstance(LoginViaOTP.this).insertEmail(emailId);
									Utils.enableReceiver(LoginViaOTP.this, IncomingSmsReceiver.class, false);
									startActivity(new Intent(LoginViaOTP.this, HomeActivity.class));
									finish();
									overridePendingTransition(R.anim.right_in, R.anim.right_out);
								}
							} else if(ApiResponseFlags.UPLOAD_DOCCUMENT.getOrdinal() == flag){
								Intent intent = new Intent(LoginViaOTP.this, DriverDocumentActivity.class);
								intent.putExtra("access_token",jObj.getString("access_token"));
								startActivity(intent);
							} else {
								DialogPopup.alertPopup(activity, "", message);
							}
							if(dialogLoading != null) {
								dialogLoading.dismiss();
							}
						} else {
							if(dialogLoading != null) {
								dialogLoading.dismiss();
							}
						}
					} catch (Exception exception) {
						exception.printStackTrace();
						DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
					}
					if(dialogLoading != null) {
						dialogLoading.dismiss();
					}
				}

				@Override
				public void failure(RetrofitError error) {
					if(dialogLoading != null) {
						dialogLoading.dismiss();
					}
					DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
				}
			});

		} else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}

	public void performbackPressed() {
		Prefs.with(LoginViaOTP.this).save(SPLabels.REQUEST_LOGIN_OTP_FLAG, "false");
		Intent intent = new Intent(LoginViaOTP.this, SplashNewActivity.class);
		intent.putExtra("no_anim", "yes");
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}

	public void showLanguagePreference() {

		if (languagePrefStatus == 1) {
			selectLanguageLl.setVisibility(View.VISIBLE);
		} else {
			selectLanguageLl.setVisibility(View.GONE);
		}

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(dataAdapter);

		if (!selectedLanguage.equalsIgnoreCase("")) {
			int spinnerPosition = dataAdapter.getPosition(selectedLanguage);
			spinner.setSelection(spinnerPosition);
		}

		// Spinner click listener
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String item = parent.getItemAtPosition(position).toString();

				Prefs.with(LoginViaOTP.this).save(SPLabels.SELECTED_LANGUAGE, item);
				if (!selectedLanguage.equalsIgnoreCase(Prefs.with(LoginViaOTP.this).getString(SPLabels.SELECTED_LANGUAGE, ""))) {
					selectedLanguage = Prefs.with(LoginViaOTP.this).getString(SPLabels.SELECTED_LANGUAGE, "");
					customCountDownTimer.cancel();
					onCreate(new Bundle());
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}


//	public void sendIntentToOtpScreen() {
//		DialogPopup.alertPopupWithListener(LoginViaOTP.this, "", otpErrorMsg, new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				OTPConfirmScreen.intentFromRegister = false;
//				OTPConfirmScreen.emailRegisterData = new EmailRegisterData("", enteredEmail, phoneNoOfLoginAccount, "", accessToken);
//				startActivity(new Intent(LoginViaOTP.this, OTPConfirmScreen.class));
//				finish();
//				overridePendingTransition(R.anim.right_in, R.anim.right_out);
//			}
//		});
//	}

	@Override
	public void onBackPressed() {
		performbackPressed();
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		Prefs.with(LoginViaOTP.this).save(SPLabels.LOGIN_VIA_OTP_STATE, false);
		OTP_SCREEN_OPEN = null;
		Utils.enableReceiver(LoginViaOTP.this, IncomingSmsReceiver.class, false);

		super.onDestroy();
	}


	public void fetchMessages() {

		try {
			Uri uri = Uri.parse("content://sms/inbox");
			String[] selectionArgs;
			String selection;
			Cursor cursor;

			selectionArgs = new String[]{Long.toString(System.currentTimeMillis() - 900000)};
			selection = "date>?";
			cursor = LoginViaOTP.this.getContentResolver().query(uri, null, selection, selectionArgs, null);


			if (cursor != null) {
				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
					String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
					String sender = cursor.getString(cursor.getColumnIndexOrThrow("address"));
					try {
						String date = DateOperations.getTimeStampUTCFromMillis(Long
								.parseLong(cursor.getString(cursor.getColumnIndexOrThrow("date"))));
						if (body.toLowerCase().contains("jugnoo")) {
							String otp = "";
							String message = body;

							if (message.toLowerCase().contains("paytm")) {
								otp = message.split("\\ ")[0];
							} else {
								String[] arr = message.split("and\\ it\\ is\\ valid\\ till\\ ");
								String[] arr2 = arr[0].split("Dear\\ Driver\\,\\ Your\\ Jugnoo\\ One\\ Time\\ Password\\ is\\ ");
								otp = arr2[1];
								otp = otp.replaceAll("\\ ", "");
							}

							if (Utils.checkIfOnlyDigits(otp)) {
								if (!"".equalsIgnoreCase(otp)) {
									try {
										otpEt.setText(otp);
										otpEt.setSelection(otpEt.getText().length());
										phoneNoEt.setText(Prefs.with(LoginViaOTP.this).getString(SPLabels.DRIVER_LOGIN_PHONE_NUMBER, ""));
										loginViaOtp.performClick();
										break;
									} catch (Exception e) {
										e.printStackTrace();
									}

								}
							}
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			if (cursor != null) {
				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}

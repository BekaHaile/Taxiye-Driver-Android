package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
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
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
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

	LinearLayout linearLayoutWaiting, relative, otpETextLLayout,selectLanguageLl;
	EditText phoneNoEt, otpEt;
	Button backBtn, btnGenerateOtp, loginViaOtp, btnReGenerateOtp;
	ImageView imageViewYellowLoadingBar;
	TextView textViewCounter;
	String selectedLanguage = Prefs.with(LoginViaOTP.this).getString(SPLabels.SELECTED_LANGUAGE,"");
	int languagePrefStatus;
	Configuration conf;
	Spinner spinner;
	public static String OTP_SCREEN_OPEN = null;
	List<String> categories = new ArrayList<>();

	boolean loginDataFetched = false, sendToOtpScreen = false, fromPreviousAccounts = false;
	String phoneNoOfLoginAccount = "", accessToken = "", otpErrorMsg = "";

	String enteredEmail = "";
	String enteredPhone = "";


	public void resetFlags() {
		loginDataFetched = false;
		sendToOtpScreen = false;
	}


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
		retrieveOTPFromSMS(intent);
		super.onNewIntent(intent);
	}


	public void onCreate(Bundle savedInstanceState) {

		fetchLanguageList();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signin_otp);

		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(LoginViaOTP.this, relative, 1134, 720, false);

		phoneNoEt = (EditText) findViewById(R.id.phoneNoEt);
		phoneNoEt.setHint(getStringText(R.string.phone_number));
		phoneNoEt.setTypeface(Data.latoRegular(getApplicationContext()));
		otpEt = (EditText) findViewById(R.id.otpEt);
		otpEt.setTypeface(Data.latoRegular(getApplicationContext()));
		otpEt.setEnabled(false);
		linearLayoutWaiting = (LinearLayout) findViewById(R.id.linearLayoutWaiting);
		selectLanguageLl = (LinearLayout) findViewById(R.id.selectLanguageLl);
		otpETextLLayout = (LinearLayout) findViewById(R.id.otpETextLLayout);
		backBtn = (Button) findViewById(R.id.backBtn);
		backBtn.setTypeface(Data.latoRegular(getApplicationContext()));
		btnGenerateOtp = (Button) findViewById(R.id.btnGenerateOtp);
		btnGenerateOtp.setTypeface(Data.latoRegular(getApplicationContext()));
		btnReGenerateOtp = (Button) findViewById(R.id.btnReGenerateOtp);
		btnReGenerateOtp.setTypeface(Data.latoRegular(getApplicationContext()));
		loginViaOtp = (Button) findViewById(R.id.loginViaOtp);
		loginViaOtp.setTypeface(Data.latoRegular(getApplicationContext()));
		spinner = (Spinner) findViewById(R.id.language_spinner);
		imageViewYellowLoadingBar = (ImageView) findViewById(R.id.imageViewYellowLoadingBar);
		textViewCounter = (TextView) findViewById(R.id.textViewCounter);
		textViewCounter.setTypeface(Data.latoRegular(getApplicationContext()));


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
					generateOTP(phoneNo);
					try {
						textViewCounter.setText("0:30");
						customCountDownTimer.start();
					} catch (Exception e) {
						e.printStackTrace();
						linearLayoutWaiting.setVisibility(View.GONE);
					}
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
					try {
						textViewCounter.setText("0:30");
						customCountDownTimer.start();
					} catch (Exception e) {
						e.printStackTrace();
						linearLayoutWaiting.setVisibility(View.GONE);
					}
				} else {
					phoneNoEt.requestFocus();
					phoneNoEt.setError(getResources().getString(R.string.valid_phone_number));
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


		Prefs.with(LoginViaOTP.this).save(SPLabels.REQUEST_LOGIN_OTP_FLAG, "false");

		OTP_SCREEN_OPEN = "yes";




//		// Spinner Drop down elements
//
//		categories.add("Select Language");
//		categories.add("Hindi");
//		categories.add("Gujrati");
//		categories.add("Oriya");
//		categories.add("Malayalam");
//		categories.add("Telgu");
//		categories.add("Tamil");
//		categories.add("Kannada");
//		categories.add("English");

		// Creating adapter for spinner



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
								btnReGenerateOtp.setVisibility(View.GONE);
								loginViaOtp.setVisibility(View.VISIBLE);
								otpETextLLayout.setBackgroundResource(R.drawable.background_white_rounded_orange_bordered);
								otpEt.setEnabled(true);
								linearLayoutWaiting.setVisibility(View.VISIBLE);
								Prefs.with(LoginViaOTP.this).save(SPLabels.REQUEST_LOGIN_OTP_FLAG, "true");
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

	public void sendLoginValues(final Activity activity, final String emailId, final String phoneNo, final String password, final String otp) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			resetFlags();
			DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));
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
							} else if (ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag) {
								DialogPopup.alertPopup(activity, "", getResources().getString(R.string.no_not_verified));
							} else if (ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag) {
								if (!SplashNewActivity.checkIfUpdate(jObj.getJSONObject("login"), activity)) {
									new JSONParser().parseAccessTokenLoginData(activity, jsonString);
									startService(new Intent(activity, DriverLocationUpdateService.class));
									Database.getInstance(LoginViaOTP.this).insertEmail(emailId);
									loginDataFetched = true;
								}
							} else {
								DialogPopup.alertPopup(activity, "", message);
							}
							DialogPopup.dismissLoadingDialog();
						} else {
							DialogPopup.dismissLoadingDialog();
						}
					} catch (Exception exception) {
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

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus && loginDataFetched) {
			startActivity(new Intent(LoginViaOTP.this, HomeActivity.class));
			loginDataFetched = false;
			finish();
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
		} else if(hasFocus && sendToOtpScreen){
			sendIntentToOtpScreen();
		}
	}

	public void showLanguagePreference() {

		if (languagePrefStatus == 1) {
			selectLanguageLl.setVisibility(View.VISIBLE);
		} else {
			selectLanguageLl.setVisibility(View.GONE);
		}

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

		// Drop down layout style - list view with radio button
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// attaching data adapter to spinner
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


				// Showing selected spinner item

				Prefs.with(LoginViaOTP.this).save(SPLabels.SELECTED_LANGUAGE, item);

				if (!selectedLanguage.equalsIgnoreCase(Prefs.with(LoginViaOTP.this).getString(SPLabels.SELECTED_LANGUAGE, ""))) {
					finish();
					overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
					startActivity(getIntent());
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}


	public void sendIntentToOtpScreen() {
		DialogPopup.alertPopupWithListener(LoginViaOTP.this, "", otpErrorMsg, new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				OTPConfirmScreen.intentFromRegister = false;
				OTPConfirmScreen.emailRegisterData = new EmailRegisterData("", enteredEmail, phoneNoOfLoginAccount, "", accessToken);
				startActivity(new Intent(LoginViaOTP.this, OTPConfirmScreen.class));
				finish();
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});
	}

	@Override
	public void onBackPressed() {
		performbackPressed();
		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		OTP_SCREEN_OPEN = null;
		super.onDestroy();
	}


}

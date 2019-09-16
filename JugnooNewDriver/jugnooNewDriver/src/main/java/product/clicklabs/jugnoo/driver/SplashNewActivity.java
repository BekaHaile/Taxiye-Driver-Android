package product.clicklabs.jugnoo.driver;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Pair;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.picker.Country;
import com.picker.CountryPicker;
import com.picker.OnCountryPickerListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.CityInfo;
import product.clicklabs.jugnoo.driver.datastructure.DriverDebugOpenMode;
import product.clicklabs.jugnoo.driver.datastructure.EmailRegisterData;
import product.clicklabs.jugnoo.driver.datastructure.PendingAPICall;
import product.clicklabs.jugnoo.driver.datastructure.RegisterOption;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.datastructure.State;
import product.clicklabs.jugnoo.driver.oldRegistration.OldRegisterScreen;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.CityResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.ui.LogoutCallback;
import product.clicklabs.jugnoo.driver.ui.models.DriverLanguageResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.CustomAppLauncher;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.DeviceUniqueID;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.LocationInit;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.PendingApiHit;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

//import product.clicklabs.jugnoo.driver.pubnub.PubnubService;

public class SplashNewActivity extends BaseFragmentActivity implements LocationUpdate, FlurryEventNames, OnCountryPickerListener<Country> {

	private final String TAG = SplashNewActivity.class.getSimpleName();
	public final static String DEVICE_TOKEN_TAG = "DEVICE_TOKEN_TAG";
	LinearLayout relative, linearLayoutSignUpIn, linearLayoutLoginSignupButtons, linearLayoutLogin;
	RelativeLayout relativeLayoutSignup, relativeLayoutScrollStop;
	RelativeLayout relativeLayoutJugnooLogo, relativeLayoutLS;

	List<String> categories = new ArrayList<>();
	List<String> vehicleStatusCategories = new ArrayList<String>();

	ImageView imageViewJugnooLogo;
	
	RelativeLayout jugnooTextImgRl, selectLanguageLl;
	ImageView jugnooTextImg, jugnooTextImg2;
	ArrayList<CityInfo> cities = new ArrayList<>();
	ProgressBar progressBar1;
	boolean secondtime = false, refreshApp = false;
	Spinner spinner;
	String selectedLanguage;
	int languagePrefStatus, registerViaTooken = RegisterOption.ONLY_TOOKAN.getOrdinal();
	Configuration conf;

	private State state = State.SPLASH_LS;

	ImageView viewInitJugnoo, viewInitSplashJugnoo, viewInitLS;
	Button buttonLogin, buttonRegisterTookan, btnGenerateOtp, signUpBtn, backBtn, buttonRegister;
	private TextView tvCountryCode, tvCountryCodeL;

	static boolean loginDataFetched = false;

	EditText nameEt, phoneNoEt, referralCodeEt, phoneNoOPTEt, alternatePhoneNoEt, vehicleNumEt;
	Spinner selectCitySp, autoNumEt, VehicleType, offeringType;

	TextView textViewLoginRegister, textViewTandC, textViewRegLogin, textViewRegDriver, textViewCustomerApp;

	String name = "", emailId = "", phoneNo = "", password = "", accessToken = "", autoNum = "", vehicleStatus="";
	Integer cityposition, vehiclePosition, offeringPosition;
	CityResponse res = new CityResponse();
	boolean tandc = false, sendToOtpScreen = false, loginFailed = false;


	public static JSONObject multipleCaseJSON;

	ArrayList<CityResponse.VehicleType> vehicleTypes = new ArrayList<>();
	ArrayList<CityResponse.OfferingType> offeringTypes = new ArrayList<>();
	ArrayList<CityResponse.City>newCities = new ArrayList<>();
	private String countryCode;


	public void resetFlags() {
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
	


	Bundle bundleHomePush= new Bundle();

	private CountryPicker countryPicker;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		if(Prefs.with(this).getInt(Constants.SP_FIRST_TIME_OPEN, 0) == 0){
//			JSONParser.saveAccessToken(this, "");
//		}
//		Prefs.with(this).save(Constants.SP_FIRST_TIME_OPEN, 1);

		selectedLanguage = Prefs.with(SplashNewActivity.this).getString(SPLabels.SELECTED_LANGUAGE, "");
		bundleHomePush = getIntent().getExtras();





		setContentView(R.layout.activity_splash_new);

		Data.locationFetcher = null;

		Log.i("all locale", String.valueOf(getResources().getSystem().getAssets().getLocales()));
		Log.i("all locale2", String.valueOf(Locale.getAvailableLocales()));
		loginDataFetched = false;
		loginFailed = false;
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(SplashNewActivity.this, relative, 1134, 720, false);


		imageViewJugnooLogo = (ImageView) findViewById(R.id.imageViewJugnooLogo);
		
		jugnooTextImgRl = (RelativeLayout) findViewById(R.id.jugnooTextImgRl);
		jugnooTextImg = (ImageView) findViewById(R.id.jugnooTextImg);
		jugnooTextImg2 = (ImageView) findViewById(R.id.jugnooTextImg2);
		jugnooTextImgRl.setVisibility(View.GONE);
		relativeLayoutSignup = (RelativeLayout) findViewById(R.id.relativeLayoutSignup);
		linearLayoutSignUpIn = (LinearLayout) findViewById(R.id.linearLayoutSignUpIn);

		selectLanguageLl = (RelativeLayout) findViewById(R.id.selectLanguageLl);
		spinner = (Spinner) findViewById(R.id.language_spinner);
		relativeLayoutLS = (RelativeLayout) findViewById(R.id.relativeLayoutLS);

		linearLayoutLoginSignupButtons = (LinearLayout) findViewById(R.id.linearLayoutLoginSignupButtons);
		linearLayoutLogin = (LinearLayout) findViewById(R.id.linearLayoutLogin);
		progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
		progressBar1.setVisibility(View.GONE);
		viewInitLS = (ImageView) findViewById(R.id.viewInitLS);

		buttonLogin = (Button) findViewById(R.id.buttonLogin);
		buttonLogin.setTypeface(Fonts.mavenRegular(getApplicationContext()), Typeface.BOLD);

		btnGenerateOtp = (Button) findViewById(R.id.btnGenerateOtp);
		btnGenerateOtp.setTypeface(Fonts.mavenRegular(getApplicationContext()), Typeface.BOLD);

		relativeLayoutScrollStop = (RelativeLayout) findViewById(R.id.relativeLayoutScrollStop);
		relativeLayoutJugnooLogo = (RelativeLayout) findViewById(R.id.relativeLayoutJugnooLogo);

		buttonRegister = (Button) findViewById(R.id.buttonRegister);
		buttonRegister.setTypeface(Fonts.mavenRegular(getApplicationContext()), Typeface.BOLD);

		textViewCustomerApp= (TextView) findViewById(R.id.textViewCustomerApp);
		textViewCustomerApp.setTypeface(Fonts.mavenRegular(getApplicationContext()), Typeface.BOLD);

		textViewRegDriver = (TextView) findViewById(R.id.textViewRegDriver);
		textViewRegDriver.setTypeface(Fonts.mavenRegular(getApplicationContext()), Typeface.BOLD);

		backBtn = (Button) findViewById(R.id.backBtn);
		backBtn.setVisibility(View.GONE);
		buttonRegisterTookan = (Button) findViewById(R.id.buttonRegisterTookan);
		buttonRegisterTookan.setTypeface(Fonts.mavenRegular(getApplicationContext()), Typeface.BOLD);

		viewInitJugnoo = (ImageView) findViewById(R.id.viewInitJugnoo);
		viewInitSplashJugnoo = (ImageView) findViewById(R.id.viewInitSplashJugnoo);

		buttonLogin.setVisibility(View.VISIBLE);
		buttonRegister.setVisibility(getResources().getBoolean(R.bool.disable_register) ? View.GONE : View.VISIBLE);
		buttonRegisterTookan.setVisibility(View.GONE);

		viewInitJugnoo.setVisibility(View.VISIBLE);
		viewInitSplashJugnoo.setVisibility(View.VISIBLE);
		viewInitLS.setVisibility(View.VISIBLE);

		nameEt = (EditText) findViewById(R.id.nameEt);
		nameEt.setTypeface(Fonts.mavenRegular(getApplicationContext()));
		referralCodeEt = (EditText) findViewById(R.id.referralCodeEt);
		referralCodeEt.setTypeface(Fonts.mavenRegular(getApplicationContext()));
		phoneNoEt = (EditText) findViewById(R.id.phoneNoEt);
		phoneNoEt.setTypeface(Fonts.mavenRegular(getApplicationContext()));

		alternatePhoneNoEt  = (EditText) findViewById(R.id.alternatePhoneNoEt);
		alternatePhoneNoEt.setTypeface(Fonts.mavenRegular(getApplicationContext()));

		vehicleNumEt  = (EditText) findViewById(R.id.vehicleNumEt);
		vehicleNumEt.setTypeface(Fonts.mavenRegular(getApplicationContext()));

		phoneNoOPTEt = (EditText) findViewById(R.id.phoneNoOPTEt);
		phoneNoOPTEt.setTypeface(Fonts.mavenRegular(getApplicationContext()));

		autoNumEt = (Spinner) findViewById(R.id.autoNumEt);
		VehicleType = (Spinner) findViewById(R.id.VehicleType);
		selectCitySp = (Spinner) findViewById(R.id.selectCitySp);
		offeringType = (Spinner) findViewById(R.id.spinnerOfferingType);
		tvCountryCode = (TextView) findViewById(R.id.tvCountryCode);
		tvCountryCodeL = (TextView) findViewById(R.id.tvCountryCodeL);
		countryPicker =
				new CountryPicker.Builder().with(this)
						.listener(this)
						.build();

		signUpBtn = (Button) findViewById(R.id.buttonEmailSignup);
		signUpBtn.setTypeface(Fonts.mavenRegular(getApplicationContext()));
		textViewLoginRegister = (TextView) findViewById(R.id.textViewLoginRegister);
		textViewLoginRegister.setTypeface(Fonts.mavenRegular(getApplicationContext()));

		textViewRegLogin = (TextView) findViewById(R.id.textViewRegLogin);
		textViewRegLogin.setTypeface(Fonts.mavenRegular(getApplicationContext()));

		textViewTandC = (TextView) findViewById(R.id.textViewTandC);
		textViewTandC.setTypeface(Fonts.mavenRegular(getApplicationContext()));


		tvCountryCode.setText(Utils.getCountryCode(this));
		tvCountryCodeL.setText(Utils.getCountryCode(this));

		textViewLoginRegister.setVisibility(getResources().getBoolean(R.bool.disable_register) ? View.GONE : View.VISIBLE);

		try {
			Pair<String, String> accPair = JSONParser.getAccessTokenPair(this);
			if ("".equalsIgnoreCase(accPair.first)) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						getCityAsync();
					}
				}, 1000);

				viewInitJugnoo.setVisibility(View.GONE);
				viewInitSplashJugnoo.setVisibility(View.GONE);
				viewInitLS.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


		buttonLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
					try {
						if(System.currentTimeMillis() < (Prefs.with(SplashNewActivity.this).getLong(SPLabels.DRIVER_LOGIN_TIME,0) + 600000)
								&&(!"".equalsIgnoreCase(Prefs.with(SplashNewActivity.this).getString(SPLabels.DRIVER_LOGIN_PHONE_NUMBER, "")))){
							fetchMessages();
						} else{
							changeUIState(State.LOGIN);
						}
					} catch (Exception e) {
						e.printStackTrace();
						changeUIState(State.LOGIN);
					}
			}
		});

		Intent intent = new Intent();
		String packageName = SplashNewActivity.this.getPackageName();
		PowerManager pm = (PowerManager) SplashNewActivity.this.getSystemService(Context.POWER_SERVICE);
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		try {
			if(currentapiVersion >= Build.VERSION_CODES.M) {
				if (!pm.isIgnoringBatteryOptimizations(packageName)){
					intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
					intent.setData(Uri.parse("package:" + packageName));
					SplashNewActivity.this.startActivity(intent);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}



		batteryOptimizer(SplashNewActivity.this);

		textViewTandC.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(SplashNewActivity.this, HelpActivity.class));
				overridePendingTransition(R.anim.left_in, R.anim.left_out);
			}
		});

		textViewLoginRegister.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
					changeUIState(State.SIGNUP);
				}  else {
					DialogPopup.alertPopup(SplashNewActivity.this, "", Data.CHECK_INTERNET_MSG);
				}
			}
		});

		textViewRegLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				changeUIState(State.LOGIN);
			}
		});
		
		buttonRegister.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
//				linearLayoutSignUpIn.setVisibility(View.GONE);
				if(AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
					changeUIState(State.SIGNUP);
				}  else {
					DialogPopup.alertPopup(SplashNewActivity.this, "", Data.CHECK_INTERNET_MSG);
				}
			}
		});

		textViewCustomerApp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final String appPackageName = "product.clicklabs.jugnoo"; // getPackageName() from Context or Activity object
				try {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
				} catch (android.content.ActivityNotFoundException anfe) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
				}
			}
		});

		buttonRegisterTookan.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(SplashNewActivity.this, OldRegisterScreen.class));
				finish();
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});
		
		jugnooTextImg.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				confirmDebugPasswordPopup(SplashNewActivity.this, DriverDebugOpenMode.DEBUG);
				FlurryEventLogger.debugPressed("no_token");
				return false;
			}
		});
		
		jugnooTextImg2.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				confirmDebugPasswordPopup(SplashNewActivity.this, DriverDebugOpenMode.REGISTER);
				FlurryEventLogger.debugPressed("driver_register");
				return false;
			}
		});


		nameEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				nameEt.setError(null);

			}
		});

		spinner.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideKeyboard();
				return false;
			}
		});

		VehicleType.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideKeyboard();
				return false;
			}
		});

		autoNumEt.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideKeyboard();
				return false;
			}
		});

		selectCitySp.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideKeyboard();
				return false;
			}
		});

		offeringType.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideKeyboard();
				return false;
			}
		});

		phoneNoEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				phoneNoEt.setError(null);
			}
		});

		alternatePhoneNoEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				alternatePhoneNoEt.setError(null);
			}
		});

		vehicleNumEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				vehicleNumEt.setError(null);
			}
		});

		phoneNoOPTEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				phoneNoOPTEt.setError(null);
			}
		});

		phoneNoOPTEt.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				btnGenerateOtp.performClick();
				return true;
			}
		});


		referralCodeEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				referralCodeEt.setError(null);
			}
		});

		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});


		signUpBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {


				String name = nameEt.getText().toString().trim();
				if (name.length() > 0) {
					name = name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
				}
				String referralCode = referralCodeEt.getText().toString().trim();

				String autoNum = vehicleNumEt.getText().toString().trim();
				String phoneNo = phoneNoEt.getText().toString().trim();
				String altPhoneNo = alternatePhoneNoEt.getText().toString().trim();
				if(TextUtils.isEmpty(getCountryCodeSelected())){
					Toast.makeText(SplashNewActivity.this, getString(R.string.please_select_country_code), Toast.LENGTH_SHORT).show();
					return;
				}

				if ("".equalsIgnoreCase(name)) {
					nameEt.requestFocus();
					nameEt.setError("Please enter name");
				} else {
					if (!"".equalsIgnoreCase(autoNum)) {
						vehicleNumEt.requestFocus();
						vehicleNumEt.setError("Please enter vehicle number");
					} else {
						if ("".equalsIgnoreCase(phoneNo)) {
							phoneNoEt.requestFocus();
							phoneNoEt.setError("Please enter phone number");
						} else {
							phoneNo = Utils.retrievePhoneNumberTenChars(getCountryCodeSelected(), phoneNo);
							if (Utils.validPhoneNumber(phoneNo)) {
									phoneNo = getCountryCodeSelected() + phoneNo;
										if (cityposition != 0) {
											if (vehiclePosition != 0) {
												if (!vehicleStatus.equalsIgnoreCase(getResources().getString(R.string.vehicle_status))) {
													if (offeringPosition != 0) {
															if (!"".equalsIgnoreCase(altPhoneNo)) {
																altPhoneNo = Utils.retrievePhoneNumberTenChars(getCountryCodeSelected(), altPhoneNo);
																if (!Utils.validPhoneNumber(altPhoneNo)) {
																	alternatePhoneNoEt.requestFocus();
																	alternatePhoneNoEt.setError("Please enter valid phone number");
																} else {
																	altPhoneNo = getCountryCodeSelected() + altPhoneNo;
																	sendSignupValues(SplashNewActivity.this, name, phoneNo, altPhoneNo, password, referralCode, getCountryCodeSelected());
																}
															} else {
																sendSignupValues(SplashNewActivity.this, name, phoneNo, "", password, referralCode, getCountryCodeSelected());
															}
															FlurryEventLogger.emailSignupClicked(emailId);
													} else {
														DialogPopup.alertPopup(SplashNewActivity.this, "", getResources().getString(R.string.select_valid_offering));
													}
												} else {
													DialogPopup.alertPopup(SplashNewActivity.this, "", getResources().getString(R.string.select_valid_vehicle_status));
												}
											} else {
												DialogPopup.alertPopup(SplashNewActivity.this, "", getResources().getString(R.string.select_valid_vehicle_type));
											}
										} else {
											DialogPopup.alertPopup(SplashNewActivity.this, "", getResources().getString(R.string.select_valid_city));
										}
							} else {
								phoneNoEt.requestFocus();
								phoneNoEt.setError("Please enter valid phone number");
							}
						}

					}
				}
			}

		});

		View.OnClickListener countryCodeClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				countryPicker.showDialog(getSupportFragmentManager());
			}
		};
		tvCountryCode.setOnClickListener(countryCodeClickListener);
		tvCountryCodeL.setOnClickListener(countryCodeClickListener);

		btnGenerateOtp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String phoneNo = phoneNoOPTEt.getText().toString().trim();
				if(TextUtils.isEmpty(getCountryCodeSelected())){
					Toast.makeText(SplashNewActivity.this, getString(R.string.please_select_country_code), Toast.LENGTH_SHORT).show();
					return;
				}
				if ("".equalsIgnoreCase(phoneNo)) {
					phoneNoOPTEt.requestFocus();
					phoneNoOPTEt.setError(getResources().getString(R.string.enter_phone_number));

				} else if ((Utils.validPhoneNumber(phoneNo))) {
					phoneNoOPTEt.setEnabled(false);
					Intent loginIntent = new Intent(SplashNewActivity.this, LoginViaOTP.class);
					loginIntent.putExtra("phone_no",phoneNo);
					loginIntent.putExtra(Constants.KEY_COUNTRY_CODE, getCountryCodeSelected());
					startActivity(loginIntent);
					finish();
					overridePendingTransition(R.anim.right_in, R.anim.right_out);
				} else {
					phoneNoOPTEt.requestFocus();
					phoneNoOPTEt.setError(getResources().getString(R.string.enter_valid_phone_number));
				}
			}
		});

		try {
			if (getIntent().hasExtra("back_from_otp")) {
				nameEt.setText(OTPConfirmScreen.emailRegisterData.name);
				phoneNoEt.setText(OTPConfirmScreen.emailRegisterData.phoneNo);
//				autoNumEt.setText(OTPConfirmScreen.emailRegisterData.autoNum);
				selectCitySp.setSelection(cityposition);
			}
			nameEt.setSelection(nameEt.getText().length());
		} catch (Exception e) {
			e.printStackTrace();
		}

		try{
//			res =  (CityResponse)getIntent().getSerializableExtra("cityResponse");
			newCities.add(res.new City(0, "Select City"));
			selectCitySp.setAdapter(new CityArrayAdapter(this, R.layout.spinner_layout, newCities));
		} catch(Exception e){
			e.printStackTrace();
		}


		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


		selectCitySp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				cityposition = newCities.get(position).getCityId();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});




		// Spinner Drop down elements

		vehicleStatusCategories.clear();
		vehicleStatusCategories.add(getResources().getString(R.string.vehicle_status));
		vehicleStatusCategories.add(getResources().getString(R.string.owned));
		vehicleStatusCategories.add(getResources().getString(R.string.rented));


		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, vehicleStatusCategories);

		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


//		autoNumEt.setAdapter(dataAdapter);
		autoNumEt.setAdapter(new VehicleStatusArrayAdapter(this, R.layout.spinner_layout, vehicleStatusCategories));
		autoNumEt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				vehicleStatus = parent.getItemAtPosition(position).toString();
				Log.i("YES", "YES");

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				Log.i("NO", "NO");
			}
		});



		vehicleTypes.add(res.new VehicleType("Select Vehicle",0));
		VehicleType.setAdapter(new VehicyleArrayAdapter(this, R.layout.spinner_layout, vehicleTypes));
		VehicleType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String item = parent.getItemAtPosition(position).toString();
				vehiclePosition = vehicleTypes.get(position).getVehicleType();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		offeringTypes.add(res.new OfferingType("Enrolled For",0));
		offeringType.setAdapter(new OfferingArrayAdapter(this, R.layout.spinner_layout, offeringTypes));
		offeringType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String item = parent.getItemAtPosition(position).toString();
				offeringPosition = offeringTypes.get(position).getOfferingType();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});


//		Data.generateKeyHash(SplashNewActivity.this);
		
		
		try {																						// to get AppVersion, OS version, country code and device name
			Data.filldetails(SplashNewActivity.this);
		} catch (Exception e) {
			Log.e("error in fetching appversion and gcm key", ".." + e.toString());
		}
		

		noNetFirstTime = false;
		noNetSecondTime = false;
	    
	    
	    
	    
	    
		if(getIntent().hasExtra("no_anim")){
			imageViewJugnooLogo.clearAnimation();
			jugnooTextImgRl.setVisibility(View.VISIBLE);
			noNetFirstTime = true;
			getDeviceToken();
			changeUIState(State.LOGIN);
			if(getIntent().hasExtra("number")){
				phoneNoOPTEt.setText(getIntent().getStringExtra("number"));
			}
		}
		else{
			Animation animation = new AlphaAnimation(0, 1);
			animation.setFillAfter(false);
			animation.setDuration(1000);
			animation.setInterpolator(new AccelerateDecelerateInterpolator());
			animation.setAnimationListener(new ShowAnimListener());
			imageViewJugnooLogo.startAnimation(animation);
		}

//		try {
//			Pair<String, String> accPair = JSONParser.getAccessTokenPair(this);
//			if (!"".equalsIgnoreCase(accPair.first)){
//				refreshApp = true;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}


		relative.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/*if(!loginDataFetched){
					noNetFirstTime = false;
					noNetSecondTime = false;
					getDeviceToken();
				}*/
			}
		});

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

	}


	private Handler handler = new Handler();
	private Runnable deviceTokenNotFoundRunnable = new Runnable() {
		@Override
		public void run() {

			if(!SplashNewActivity.this.isFinishing()){
				progressBar1.setVisibility(View.GONE);
				DialogPopup.alertPopupWithListener(SplashNewActivity.this, "",
						getString(R.string.device_token_not_found_message),
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								SplashNewActivity.this.finish();
							}
						});

			}
		}
	};
	public void getDeviceToken(){
//	    progressBar1.setVisibility(View.VISIBLE);
		Data.deviceToken = "";
		if(handler!=null){
			handler.removeCallbacksAndMessages(null);
		}

//		Log.i(SplashNewActivity.DEVICE_TOKEN_TAG, "getDeviceToken() " +  FirebaseInstanceId.getInstance().getToken());

		FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
			@Override
			public void onComplete(@NonNull Task<InstanceIdResult> task) {
				if(!task.isSuccessful()) {
					Log.w("DRIVER_DOCUMENT_ACTIVITY","device_token_unsuccessful - onReceive",task.getException());
					return;
				}
				if(task.getResult() != null) {
					Log.e("DEVICE_TOKEN_TAG SPLASH_NEW_ACTIVITY  -> getDeviceToken", task.getResult().getToken());
					if(!TextUtils.isEmpty(task.getResult().getToken())){
						Data.deviceToken = task.getResult().getToken();
						Log.e("deviceToken in IDeviceTokenReceiver", Data.deviceToken + "..");
						checkForTokens();
					}else {
						//wait for broadcast
						try {
							LocalBroadcastManager.getInstance(SplashNewActivity.this).unregisterReceiver(deviceTokenReceiver);
						} catch (Exception e) {
							e.printStackTrace();
						}
						LocalBroadcastManager.getInstance(SplashNewActivity.this).registerReceiver(deviceTokenReceiver, deviceTokenReceiverFilter());
						//give message after waiting for 5 Seconds
						handler.postDelayed(deviceTokenNotFoundRunnable,5*1000);
					}
				}

			}
		});
//		Log.i(SplashNewActivity.DEVICE_TOKEN_TAG + "getDeviceToken()", FirebaseInstanceId.getInstance().getInstanceId().getResult().getToken());
//		if(!TextUtils.isEmpty(FirebaseInstanceId.getInstance().getInstanceId().getResult().getToken())){
//			Data.deviceToken = FirebaseInstanceId.getInstance().getInstanceId().getResult().getToken();
//			Log.e("deviceToken in IDeviceTokenReceiver", Data.deviceToken + "..");
//			checkForTokens();
//		}else {
//			//wait for broadcast
//			try {
//				LocalBroadcastManager.getInstance(this).unregisterReceiver(deviceTokenReceiver);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			LocalBroadcastManager.getInstance(this).registerReceiver(deviceTokenReceiver, deviceTokenReceiverFilter());
//			//give message after waiting for 5 Seconds
//			handler.postDelayed(deviceTokenNotFoundRunnable,5*1000);
//
//
//		}



	}

	private void checkForTokens(){

		if(!"".equalsIgnoreCase(Data.deviceToken)){
			progressBar1.setVisibility(View.GONE);
			pushAPIs(SplashNewActivity.this);
		}
	}


	
	
	@Override
	protected void onResume() {
		try {
			if(Data.locationFetcher == null){
				Data.locationFetcher = new LocationFetcher(this, 1000, 1);
			}
			Data.locationFetcher.connect();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(secondtime) {
			try {
				String packageName = SplashNewActivity.this.getPackageName();
				PowerManager pm = (PowerManager) SplashNewActivity.this.getSystemService(Context.POWER_SERVICE);
				int currentapiVersion = Build.VERSION.SDK_INT;
				if (currentapiVersion >= Build.VERSION_CODES.M) {
					if (!pm.isIgnoringBatteryOptimizations(packageName)) {
						Intent newIntent = getIntent();
						finish();
						startActivity(newIntent);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		secondtime = true;

		batteryOptimizer(SplashNewActivity.this);
		
		int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		if(resp != ConnectionResult.SUCCESS){
			Log.e("Google Play Service Error ","="+resp);
			DialogPopup.showGooglePlayErrorAlert(SplashNewActivity.this);
		}
		else{
			LocationInit.showLocationAlertDialog(this);
//			DialogPopup.showLocationSettingsAlert(SplashNewActivity.this);
		}


		super.onResume();
	}
	
	
	
	@Override
	public void onPause() {
		Database2.getInstance(this).checkStartPendingApisService(this);
		try{
			Data.locationFetcher.destroy();
		} catch(Exception e){
			e.printStackTrace();
		}
		super.onPause();
		Database2.getInstance(this).close();
	}


	public void batteryOptimizer(Context context){

	}


	public void sendSignupValues(final Activity activity, final String name, final String phoneNo, final String altPhoneNo, final String city, final String referralCode, final String countryCode) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			resetFlags();
			DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));

//			RequestParams params = new RequestParams();

			if (Data.locationFetcher != null) {
				Data.latitude = Data.locationFetcher.getLatitude();
				Data.longitude = Data.locationFetcher.getLongitude();
			}

			HashMap<String, String> params = new HashMap<String, String>();
			params.put("user_name", name);
			params.put("phone_no", phoneNo);
			params.put(Constants.KEY_COUNTRY_CODE, countryCode);
			params.put("alt_phone_no", altPhoneNo);
//			params.put("auto_num", autoNum);
			params.put("city", String.valueOf(cityposition));
			params.put("latitude", "" + Data.latitude);
			params.put("longitude", "" + Data.longitude);
			params.put("vehicle_type",""+vehiclePosition);
			params.put("offering_type",""+offeringPosition);
			params.put("vehicle_status",vehicleStatus);
			params.put("device_type", Data.DEVICE_TYPE);
			params.put("device_name", Data.deviceName);
			params.put("app_version", "" + Data.appVersion);
			params.put("os_version", Data.osVersion);
			params.put("country", Data.country);
			params.put("client_id", Data.CLIENT_ID);
			params.put("login_type", Data.LOGIN_TYPE);
			params.put("referral_code", ""+referralCode);
			params.put("device_token", Data.deviceToken);
			params.put("unique_device_id", Data.uniqueDeviceId);
			HomeUtil.putDefaultParams(params);
			if (Utils.isDeviceRooted()) {
				params.put("device_rooted", "1");
			} else {
				params.put("device_rooted", "0");
			}

			Log.i("register_using_email params", params.toString());

			RestClient.getApiServices().oneTimeRegisteration(params, new Callback<RegisterScreenResponse>() {
				@Override
				public void success(RegisterScreenResponse registerScreenResponse, Response response) {
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj;
						jObj = new JSONObject(jsonString);

						if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
							int flag = jObj.getInt("flag");
							String message = JSONParser.getServerMessage(jObj);

							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag, null)) {

								if (ApiResponseFlags.AUTH_REGISTRATION_FAILURE.getOrdinal() == flag) {
									DialogPopup.alertPopup(activity, "", message);
								} else if (ApiResponseFlags.AUTH_ALREADY_REGISTERED.getOrdinal() == flag) {
									DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											changeUIState(State.LOGIN);
										}
									});
								} else if (ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag) {
									RegisterScreenResponse data = registerScreenResponse;
									SplashNewActivity.this.name = name;
									SplashNewActivity.this.emailId = emailId;
									SplashNewActivity.this.phoneNo = data.getPhoneNo();
									SplashNewActivity.this.password = password;
									SplashNewActivity.this.countryCode = countryCode;
									SplashNewActivity.this.accessToken = data.getAccessToken();

									sendToOtpScreen = true;
								} else if (ApiResponseFlags.AUTH_DUPLICATE_REGISTRATION.getOrdinal() == flag) {
									DialogPopup.alertPopup(activity, "", message);
								} else if (ApiResponseFlags.INVALID_ACCESS_TOKEN.getOrdinal() == flag) {
									onBackPressed();

								} else {
									DialogPopup.alertPopup(activity, "", message);
								}
								DialogPopup.dismissLoadingDialog();
							}
						} else {
							DialogPopup.dismissLoadingDialog();
						}
					} catch (Exception exception) {
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


		} else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}


	public void sendIntentToOtpScreen() {
		OTPConfirmScreen.intentFromRegister = true;
		OTPConfirmScreen.emailRegisterData = new EmailRegisterData(name, emailId, phoneNo, password, accessToken, autoNum, countryCode);
		startActivity(new Intent(SplashNewActivity.this, OTPConfirmScreen.class)
				.putExtra(Constants.KEY_COUNTRY_CODE, countryCode));
		finish();
		overridePendingTransition(R.anim.right_in, R.anim.right_out);
	}
	
	
	boolean noNetFirstTime = false, noNetSecondTime = false;
	
	Handler checkNetHandler = new Handler();
	Runnable checkNetRunnable = new Runnable() {
		
		@Override
		public void run() {
			
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					
					if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
						noNetSecondTime = false;
					    pushAPIs(SplashNewActivity.this);
					    FlurryEventLogger.appStarted(Data.deviceToken);
					}
					else{
						DialogPopup.alertPopup(SplashNewActivity.this, "", Data.CHECK_INTERNET_MSG);
						noNetSecondTime = true;
					}
					
				}
			});
			
		}
	};
	
	
	public void stopPendingAPIs(){
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				progressBar1.setVisibility(View.GONE);
				callFirstAttempt();
			}
		});
	}
	
	
    public Thread pushApiThread;
    public void pushAPIs(final Context context){
    	boolean mockLocationEnabled = false;
		if(Data.locationFetcher != null){
			mockLocationEnabled = Utils.mockLocationEnabled(Data.locationFetcher.getLocationUnchecked());
		}
		if(mockLocationEnabled){
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					DialogPopup.alertPopupWithListener(SplashNewActivity.this, "", getResources().getString(R.string.disable_mock_location), new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							startActivity(new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
							finish();
						}
					});
				}
			});
		}
		else{
			/*runOnUiThread(new Runnable() {

				@Override
				public void run() {
					progressBar1.setVisibility(View.VISIBLE);
				}
			});*/
	    	stopService(new Intent(context, PushPendingCallsService.class));
	    	stopPushApiThread();
	    	try{
		    	pushApiThread = new Thread(new Runnable() {
					
					@Override
					public void run() {
						ArrayList<PendingAPICall> pendingAPICalls = Database2.getInstance(context).getAllPendingAPICalls();
						for(PendingAPICall pendingAPICall : pendingAPICalls){
							Log.e(TAG, "pendingApiCall="+pendingAPICall);
							new PendingApiHit().startAPI(context, pendingAPICall);
						}
						
						int pendingApisCount = Database2.getInstance(context).getAllPendingAPICallsCount();
						if(pendingApisCount > 0){
							recallCachedApis();
						}
						else{
							stopPendingAPIs();
						}
					}
				});
		    	pushApiThread.start();
	    	} catch(Exception e){
	    		e.printStackTrace();
	    	}
		}
    }

	private void recallCachedApis(){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						Log.e(TAG, "reached inside handler");
						pushAPIs(SplashNewActivity.this);
					}
				}, 60000);
			}
		});
	}
    
    public void stopPushApiThread(){
    	try{
    		if(pushApiThread != null){
    			pushApiThread.interrupt();
    		}
    	} catch(Exception e){
    		e.printStackTrace();
    	}
    }

	@Override
	public void onSelectCountry(Country country) {
		tvCountryCode.setText(country.getDialCode());
		tvCountryCodeL.setText(country.getDialCode());
	}
	private String getCountryCodeSelected(){
    	if(state == State.LOGIN){
    		return tvCountryCodeL.getText().toString();
		}
		return tvCountryCode.getText().toString();
	}


	class ShowAnimListener implements AnimationListener{
		
		public ShowAnimListener(){
		}
		
		@Override
		public void onAnimationStart(Animation animation) {
			Log.i("onAnimationStart", "onAnimationStart");
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			Log.i("onAnimationStart", "onAnimationStart");
			imageViewJugnooLogo.clearAnimation();

			jugnooTextImgRl.setVisibility(View.VISIBLE);
			
			noNetFirstTime = true;
			getDeviceToken();
			
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}
		
	}


	boolean isPhoneValid(CharSequence phone) {
		return android.util.Patterns.PHONE.matcher(phone).matches();
	}



	public class CityArrayAdapter extends ArrayAdapter<CityResponse.City> {
		private LayoutInflater inflater;
		private List<CityResponse.City> data;
		public CityArrayAdapter(Context context, int resource, List<CityResponse.City> objects) {
			super(context, resource, objects);
			data = objects;
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}


		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getSpinnerView(position);
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			return getSpinnerView(position);
		}

		View getSpinnerView(int position){
			View convertView = inflater.inflate(R.layout.spinner_layout, null);

			TextView textViewCity  = (TextView) convertView.findViewById(R.id.textViewCity);
			textViewCity.setText(data.get(position).getCityName());
			AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(360, 80);
			convertView.setLayoutParams(layoutParams);

			ASSL.DoMagic(convertView);
			return convertView;
		}

	}

	public class VehicleStatusArrayAdapter extends ArrayAdapter<String> {
		private LayoutInflater inflater;
		private List<String> data;
		public VehicleStatusArrayAdapter(Context context, int resource, List<String> objects) {
			super(context, resource, objects);
			data = objects;
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}


		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getSpinnerView(position);
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			return getSpinnerView(position);
		}

		View getSpinnerView(int position){
			View convertView = inflater.inflate(R.layout.spinner_layout, null);

			TextView textViewCity  = (TextView) convertView.findViewById(R.id.textViewCity);
			textViewCity.setText(vehicleStatusCategories.get(position));
			AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(360, 80);
			convertView.setLayoutParams(layoutParams);

			ASSL.DoMagic(convertView);
			return convertView;
		}

	}


	public class OfferingArrayAdapter extends ArrayAdapter<CityResponse.OfferingType>{
		private LayoutInflater inflate;
		private List<CityResponse.OfferingType> dataOffering;
		public OfferingArrayAdapter(Context context, int resource, List<CityResponse.OfferingType> objects) {
			super(context, resource, objects);
			dataOffering = objects;
			inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}


		@Override
		public int getCount() {
			return dataOffering.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getSpinnerView(position);
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			return getSpinnerView(position);
		}

		View getSpinnerView(int position){
			View convertView = inflate.inflate(R.layout.spinner_layout, null);

			TextView textViewCity  = (TextView) convertView.findViewById(R.id.textViewCity);
			textViewCity.setText(dataOffering.get(position).getOfferingName());

			AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(360, 80);
			convertView.setLayoutParams(layoutParams);

			ASSL.DoMagic(convertView);

			return convertView;
		}
	}


	public class VehicyleArrayAdapter extends ArrayAdapter<CityResponse.VehicleType> {
		private LayoutInflater inflater;
		private List<CityResponse.VehicleType> dataVehicle;
		public VehicyleArrayAdapter(Context context, int resource, List<CityResponse.VehicleType> objects) {
			super(context, resource, objects);
			dataVehicle = objects;
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}


		@Override
		public int getCount() {
			return dataVehicle.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getSpinnerView(position);
		}

		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			return getSpinnerView(position);
		}

		View getSpinnerView(int position){
			View convertView = inflater.inflate(R.layout.spinner_layout, null);

			TextView textViewCity  = (TextView) convertView.findViewById(R.id.textViewCity);
			textViewCity.setText(dataVehicle.get(position).getVehicleName());

			AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(360, 80);
			convertView.setLayoutParams(layoutParams);

			ASSL.DoMagic(convertView);

			return convertView;
		}

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

			if(!isSystemAlertPermissionGranted(SplashNewActivity.this)){
				requestSystemAlertPermission(SplashNewActivity.this, 23);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void requestSystemAlertPermission(Activity context, int requestCode) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
			return;
		final String packageName = context.getPackageName();
		final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + packageName));
		context.startActivityForResult(intent, requestCode);
	}

	@TargetApi(Build.VERSION_CODES.M)
	public static boolean isSystemAlertPermissionGranted(Context context) {
		final boolean result = Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context);
		return result;
	}
	
	public void callFirstAttempt(){
		runOnUiThread(new Runnable() {
		@Override
		public void run() {
			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
				noNetFirstTime = false;
			    accessTokenLogin(SplashNewActivity.this);

			}
			else{
				DialogPopup.alertPopup(SplashNewActivity.this, "", Data.CHECK_INTERNET_MSG);
			}
		}
		});
	}
	
	

//	Retrofit


	public void accessTokenLogin(final Activity activity) {

		Pair<String, String> accPair = JSONParser.getAccessTokenPair(activity);
		final long responseTime = System.currentTimeMillis();
		conf = getResources().getConfiguration();
		if (!"".equalsIgnoreCase(accPair.first)){
			linearLayoutLoginSignupButtons.setVisibility(View.GONE);
			viewInitLS.setVisibility(View.VISIBLE);
			viewInitSplashJugnoo.setVisibility(View.VISIBLE);
			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

				DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));

				if(Data.locationFetcher != null){
					Data.latitude = Data.locationFetcher.getLatitude();
					Data.longitude = Data.locationFetcher.getLongitude();
				}
				HashMap<String, String> params = new HashMap<String, String>();

//				RequestParams params = new RequestParams();
				params.put("access_token", accPair.first);
				params.put("device_token", Data.deviceToken);

				params.put("latitude", ""+Data.latitude);
				params.put("longitude", ""+Data.longitude);

				params.put("locale", conf.locale.toString());
				params.put("app_version", ""+Data.appVersion);
				params.put("device_type", Data.DEVICE_TYPE);
				params.put("unique_device_id", Data.uniqueDeviceId);
				params.put("is_access_token_new", "1");
				params.put("client_id", Data.CLIENT_ID);
				params.put("login_type", Data.LOGIN_TYPE);
				HomeUtil.putDefaultParams(params);

				params.put("device_name", Utils.getDeviceName());
				params.put("imei", DeviceUniqueID.getCachedUniqueId(this));

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



				RestClient.getApiServices().accessTokenLoginRetro(params, new Callback<RegisterScreenResponse>() {
					@Override
					public void success(RegisterScreenResponse registerScreenResponse, Response response) {
						try {
							String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
							JSONObject jObj;
							jObj = new JSONObject(jsonString);
							int flag = jObj.getInt("flag");
							String message = JSONParser.getServerMessage(jObj);

							if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag, null)){
								if(ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag){
									DialogPopup.alertPopup(activity, "", message);
									DialogPopup.dismissLoadingDialog();
								}
								else if(ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag){
									DialogPopup.alertPopup(activity, "", message);
									DialogPopup.dismissLoadingDialog();
								}
								else if(ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag){
									if(!SplashNewActivity.checkIfUpdate(jObj.getJSONObject("login"), activity)){
//										new AccessTokenDataParseAsync(activity, jsonString, message).execute();
										String resp;
										try {
											resp = new JSONParser().parseAccessTokenLoginData(activity, jsonString);
										} catch (Exception e) {
											e.printStackTrace();
											resp = Constants.SERVER_TIMEOUT;
										}

										if(resp.contains(Constants.SERVER_TIMEOUT)){
											loginDataFetched = false;
											DialogPopup.alertPopup(activity, "", message);
										}
										else{
											noNetFirstTime = false;
											noNetSecondTime = false;
											loginDataFetched = true;
											DialogPopup.showLoadingDialog(SplashNewActivity.this, getResources().getString(R.string.loading));
										}

										Utils.deleteMFile(activity);
//										Utils.clearApplicationData(SplashNewActivity.this);
										FlurryEventLogger.logResponseTime(activity, System.currentTimeMillis() - responseTime, FlurryEventNames.LOGIN_ACCESSTOKEN_RESPONSE);

										DialogPopup.dismissLoadingDialog();
									}
									else{
										DialogPopup.dismissLoadingDialog();
									}
								} else if(ApiResponseFlags.UPLOAD_DOCCUMENT.getOrdinal() == flag){
									JSONParser.saveAccessToken(activity, jObj.getString("access_token"));
									Intent intent = new Intent(SplashNewActivity.this, DriverDocumentActivity.class);
									intent.putExtra("access_token",jObj.getString("access_token"));
									intent.putExtra("in_side", false);
									intent.putExtra("doc_required", 3);
									startActivity(intent);
								}  else{
									DialogPopup.alertPopup(activity, "", message);
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
		} else{

			fetchLanguageList();

			buttonLogin.setVisibility(View.VISIBLE);
			buttonRegister.setVisibility(getResources().getBoolean(R.bool.disable_register) ? View.GONE : View.VISIBLE);
//			toggleRegistrationButton();
		}

	}

	public void toggleRegistrationButton(){
		if((registerViaTooken == RegisterOption.BOTH_TOOKAN_SELF_REGISTER.getOrdinal()
				|| registerViaTooken == RegisterOption.ONLY_SELF_REGISTER.getOrdinal())) {
			buttonRegister.setVisibility(getResources().getBoolean(R.bool.disable_register) ? View.GONE : View.VISIBLE);
		}
	}


	@Override
	public void onBackPressed() {
		try {
			hideKeyboard();
			resetFields();
			if (State.LOGIN == state) {
				changeUIState(State.SPLASH_LS);

			} else if (State.SIGNUP == state) {
				changeUIState(State.SPLASH_LS);

			} else {
				super.onBackPressed();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void hideKeyboard(){
		View view = this.getCurrentFocus();
		if (view != null) {
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}

	public void resetFields(){
		phoneNoOPTEt.setText("");
		phoneNoOPTEt.setError(null);
		nameEt.setText("");
		nameEt.setError(null);
		phoneNoEt.setText("");
		phoneNoEt.setError(null);
		vehicleNumEt.setText("");
		vehicleNumEt.setError(null);
		alternatePhoneNoEt.setText("");
		alternatePhoneNoEt.setError(null);
		referralCodeEt.setText("");
		referralCodeEt.setError(null);
		selectCitySp.setSelection(0);
		VehicleType.setSelection(0);
		autoNumEt.setSelection(0);
		offeringType.setSelection(0);
	}


	class AccessTokenDataParseAsync extends AsyncTask<String, Integer, String>{
		
		Activity activity;
		String response, accessToken, message;
		
		public AccessTokenDataParseAsync(Activity activity, String response, String message){
			this.activity = activity;
			this.response = response;
            this.message = message;
		}
		
		@Override
		protected String doInBackground(String... params) {
			try {
				String resp = new JSONParser().parseAccessTokenLoginData(activity, response);
				return resp;
			} catch (Exception e) {
				e.printStackTrace();
				return Constants.SERVER_TIMEOUT;
			}
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if(result.contains(Constants.SERVER_TIMEOUT)){
				loginDataFetched = false;
				DialogPopup.alertPopup(activity, "", message);
			}
			else{

				noNetFirstTime = false;
				noNetSecondTime = false;
				
				loginDataFetched = true;
				DialogPopup.showLoadingDialog(SplashNewActivity.this, getResources().getString(R.string.loading));
				
			}
			

			DialogPopup.dismissLoadingDialog();
		}
		
	}
	
	
	public static boolean checkIfUpdate(JSONObject jObj, Activity activity) throws Exception{
//		"popup": {
//	        "title": "Update Version",
//	        "text": "Update app with new version!",
//	        "cur_version": 116,			// could be used for local check
//	        "is_force": 1				// 1 for forced, 0 for not forced
//	}
		if(!jObj.isNull("popup")){
			try{
				JSONObject jupdatePopupInfo = jObj.getJSONObject("popup"); 
				String title = jupdatePopupInfo.getString("title");
				String text = jupdatePopupInfo.getString("text");
				int currentVersion = jupdatePopupInfo.getInt("cur_version");
				int isForce = jupdatePopupInfo.getInt("is_force");
				String link = jupdatePopupInfo.optString(Constants.KEY_LINK, "");
				
				if(Data.appVersion == currentVersion){
					return false;
				}
				else{
					SplashNewActivity.appUpdatePopup(title, text, isForce, activity, link);
					if(isForce == 1){
						return true;
					}
					else{
						return false;
					}
				}
			} catch(Exception e){
				return false;
			}
		}
		else{
			return false;
		}
	}
	
	
	
	/**
	 * Displays appUpdatePopup dialog
	 */
	public static void appUpdatePopup(String title, String message, final int isForced, final Activity activity,
									  final String link) {
		try {

			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_custom_two_buttons);

			RelativeLayout frameLayout = (RelativeLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, false);
			
			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			
			
			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Fonts.mavenRegular(activity));
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Fonts.mavenRegular(activity));
			textHead.setVisibility(View.VISIBLE);

			textMessage.setMovementMethod(new ScrollingMovementMethod());
			textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));
			
			textMessage.setText(message);
			
			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Fonts.mavenRegular(activity));
			btnOk.setText(activity.getResources().getString(R.string.update));
			
			Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
			btnCancel.setTypeface(Fonts.mavenRegular(activity));
			btnCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
					if (isForced == 1) {
						activity.finish();
					}
				}
			});


			
			
			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					try {
						loginDataFetched = false;
						dialog.dismiss();
						Intent intent = new Intent(Intent.ACTION_VIEW);
						if("".equalsIgnoreCase(link)) {
							intent.setData(Uri.parse("market://details?id="+BuildConfig.APPLICATION_ID));
						} else {
							intent.setData(Uri.parse(link));
						}
						activity.startActivity(intent);
						activity.finish();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
			});

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static void sendToCustomerAppPopup(String title, String message, final Activity activity) {
		try {

			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_customer_app);

			RelativeLayout frameLayout = (RelativeLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, false);
			
			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			
			
			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Fonts.mavenRegular(activity));
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Fonts.mavenRegular(activity));

			textMessage.setMovementMethod(new ScrollingMovementMethod());
			textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));
			
			textHead.setText(title);
			textMessage.setText(message);
			
			Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Fonts.mavenRegular(activity));
			
			frameLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
				}
			});
			
			dialog.findViewById(R.id.innerRl).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
				}
			});
			
			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dialog.dismiss();
					CustomAppLauncher.launchApp(activity, "product.clicklabs.jugnoo");
					activity.finish();
				}
			});
			

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

		if (hasFocus && sendToOtpScreen) {
			sendIntentToOtpScreen();
		}

		if(hasFocus && noNetFirstTime){
			noNetFirstTime = false;
			checkNetHandler.postDelayed(checkNetRunnable, 4000);
		}
		else if(hasFocus && noNetSecondTime){
			noNetSecondTime = false;
//			finish();
		}
		else if(hasFocus && loginDataFetched){
			loginDataFetched = false;
			Intent intent = new Intent(SplashNewActivity.this, HomeActivity.class);
			if(bundleHomePush != null)
			intent.putExtras(bundleHomePush);
			intent.putExtra(Constants.FUGU_CHAT_BUNDLE,getIntent().getExtras());
			if(HomeActivity.activity!=null){
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}else{
				startActivity(intent);
				ActivityCompat.finishAffinity(this);
			}

			overridePendingTransition(R.anim.right_in, R.anim.right_out);
		}
		else if(hasFocus && loginFailed){
			loginFailed = false;
			startActivity(new Intent(SplashNewActivity.this, LoginViaOTP.class));
			finish();
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
		}
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			LocalBroadcastManager.getInstance(this).unregisterReceiver(deviceTokenReceiver);
		} catch (Exception e) {
		}
        ASSL.closeActivity(relative);
        System.gc();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		bundleHomePush = intent.getExtras();

	}

	public void confirmDebugPasswordPopup(final Activity activity, final DriverDebugOpenMode flag){

			try {
				final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
				dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
				dialog.setContentView(R.layout.dialog_edittext);

				RelativeLayout frameLayout = (RelativeLayout) dialog.findViewById(R.id.rv);

				WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
				layoutParams.dimAmount = 0.6f;
				dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				dialog.setCancelable(true);
				dialog.setCanceledOnTouchOutside(true);
				
				
				TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Fonts.mavenRegular(activity));
				TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Fonts.mavenRegular(activity));
				final EditText etCode = (EditText) dialog.findViewById(R.id.etCode); etCode.setTypeface(Fonts.mavenRegular(activity));
				
				
				if(DriverDebugOpenMode.DEBUG == flag){
					textHead.setText("Confirm Debug Password");
				}
				else if(DriverDebugOpenMode.REGISTER == flag){
					textHead.setText("Confirm Register Password");
				}
				
				textMessage.setText(getResources().getString(R.string.password_to_continue));
				
				textMessage.setVisibility(View.GONE);
				
				
				final Button btnConfirm = (Button) dialog.findViewById(R.id.btnConfirm);
				btnConfirm.setTypeface(Fonts.mavenRegular(activity));
				
				btnConfirm.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						String code = etCode.getText().toString().trim();
						if("".equalsIgnoreCase(code)){
							etCode.requestFocus();
							etCode.setError("Code can't be empty.");
						}
						else{
							if(DriverDebugOpenMode.DEBUG == flag){
								if(Data.DEBUG_PASSWORD.equalsIgnoreCase(code)){
									dialog.dismiss();
									changeServerLinkPopup(activity);
								}
                                else if(Data.DEBUG_PASSWORD_TEST.equalsIgnoreCase(code)){
                                    dialog.dismiss();

                                    DialogPopup.alertPopupThreeButtonsWithListeners(activity, "", "Current server is " + Data.SERVER_URL + "\n change to:",
                                        "DEV_1(8013)", "DEV_2(8014)", "DEV_3(8015)",
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                SharedPreferences preferences = activity.getSharedPreferences(Data.SETTINGS_SHARED_PREF_NAME, Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = preferences.edit();
                                                editor.putString(Data.SP_SERVER_LINK, Data.DEV_1_SERVER_URL);
                                                editor.commit();

                                                MyApplication.getInstance().initializeServerURLAndRestClient(activity);
                                            }
                                        },
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                SharedPreferences preferences = activity.getSharedPreferences(Data.SETTINGS_SHARED_PREF_NAME, Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = preferences.edit();
                                                editor.putString(Data.SP_SERVER_LINK, Data.DEV_2_SERVER_URL);
                                                editor.commit();

												MyApplication.getInstance().initializeServerURLAndRestClient(activity);
                                            }
                                        },
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                SharedPreferences preferences = activity.getSharedPreferences(Data.SETTINGS_SHARED_PREF_NAME, Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = preferences.edit();
                                                editor.putString(Data.SP_SERVER_LINK, Data.DEV_3_SERVER_URL);
                                                editor.commit();

												MyApplication.getInstance().initializeServerURLAndRestClient(activity);
                                            }
                                        }, true, false);

                                }
								else{
									etCode.requestFocus();
									etCode.setError("Code not matched.");
								}
							}
							else if(DriverDebugOpenMode.REGISTER == flag && (registerViaTooken == RegisterOption.BOTH_TOOKAN_SELF_REGISTER.getOrdinal()
									|| registerViaTooken == RegisterOption.ONLY_TOOKAN.getOrdinal())){
								if(Data.REGISTER_PASSWORD.equalsIgnoreCase(code)){
									dialog.dismiss();
									buttonRegisterTookan.setVisibility(View.VISIBLE);
								}
								else{
									etCode.requestFocus();
									etCode.setError("Code not matched or disabled.");
								}
							}
						}
					}
					
				});
				
				
				etCode.setOnEditorActionListener(new OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
						int result = actionId & EditorInfo.IME_MASK_ACTION;
						switch (result) {
							case EditorInfo.IME_ACTION_DONE:
								btnConfirm.performClick();
								break;

							case EditorInfo.IME_ACTION_NEXT:
								break;

							default:
						}
						return true;
					}
				});
				

				dialog.findViewById(R.id.rl1).setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
					}
				});
				
				
				dialog.findViewById(R.id.rv).setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

				dialog.show();

				dialog.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		}
	
	
		void changeServerLinkPopup(final Activity activity) {
				try {
					final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
					dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
					dialog.setContentView(R.layout.dialog_custom_three_buttons);

					RelativeLayout frameLayout = (RelativeLayout) dialog.findViewById(R.id.rv);
					new ASSL(activity, frameLayout, 1134, 720, true);
					
					WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
					layoutParams.dimAmount = 0.6f;
					dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
					dialog.setCancelable(true);
					dialog.setCanceledOnTouchOutside(true);
					
					
					
					TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
					TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Fonts.mavenRegular(activity));
					
					
					SharedPreferences preferences = activity.getSharedPreferences(Data.SETTINGS_SHARED_PREF_NAME, Context.MODE_PRIVATE);
					String link = preferences.getString(Data.SP_SERVER_LINK, Data.DEFAULT_SERVER_URL);
					
					if(link.equalsIgnoreCase(Data.TRIAL_SERVER_URL)){
						textMessage.setText("Current server is SALES.\nChange to:");
					}
					else if(link.equalsIgnoreCase(Data.LIVE_SERVER_URL)){
						textMessage.setText("Current server is LIVE.\nChange to:");
					}
					else if(link.equalsIgnoreCase(Data.DEV_SERVER_URL)){
						textMessage.setText("Current server is DEV.\nChange to:");
					}
					
					
					
					Button btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Fonts.mavenRegular(activity));
					btnOk.setText("LIVE");
					
					Button btnNeutral = (Button) dialog.findViewById(R.id.btnNeutral);
					btnNeutral.setTypeface(Fonts.mavenRegular(activity));
					btnNeutral.setText("DEV");
					
					Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel); btnCancel.setTypeface(Fonts.mavenRegular(activity));
					btnCancel.setText("CUSTOM");
					
					
					btnOk.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							SharedPreferences preferences = activity.getSharedPreferences(Data.SETTINGS_SHARED_PREF_NAME, Context.MODE_PRIVATE);
							SharedPreferences.Editor editor = preferences.edit();
							editor.putString(Data.SP_SERVER_LINK, Data.LIVE_SERVER_URL);
							editor.commit();

							MyApplication.getInstance().initializeServerURLAndRestClient(activity);
							
							dialog.dismiss();
						}
					});
					
					btnNeutral.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							SharedPreferences preferences = activity.getSharedPreferences(Data.SETTINGS_SHARED_PREF_NAME, Context.MODE_PRIVATE);
							SharedPreferences.Editor editor = preferences.edit();
							editor.putString(Data.SP_SERVER_LINK, Data.DEV_SERVER_URL);
							editor.commit();

							MyApplication.getInstance().initializeServerURLAndRestClient(activity);

							dialog.dismiss();
						}
					});
					
					btnCancel.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							dialog.dismiss();
							saveCustomURLDialog(activity);
						}
					});

					dialog.findViewById(R.id.rl1).setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
						}
					});
					
					
					dialog.findViewById(R.id.rv).setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});

					dialog.show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}


	public void saveCustomURLDialog(final Activity activity){
		try {
			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_edittext);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);


			TextView textHead = (TextView) dialog.findViewById(R.id.textHead); textHead.setTypeface(Fonts.mavenRegular(activity));
			TextView textMessage = (TextView) dialog.findViewById(R.id.textMessage); textMessage.setTypeface(Fonts.mavenRegular(activity));
			final EditText etCode = (EditText) dialog.findViewById(R.id.etCode); etCode.setTypeface(Fonts.mavenRegular(activity));

			etCode.setInputType(InputType.TYPE_CLASS_TEXT);
			etCode.setTextSize(TypedValue.COMPLEX_UNIT_PX, 30);

			etCode.setText(Prefs.with(activity).getString(SPLabels.CUSTOM_SERVER_URL, Data.SERVER_URL));

			RelativeLayout frameLayout = (RelativeLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, true);


			textHead.setText("Custom URL");

			textMessage.setText("Please enter Custom URL");

			textMessage.setVisibility(View.GONE);


			final Button btnConfirm = (Button) dialog.findViewById(R.id.btnConfirm); btnConfirm.setTypeface(Fonts.mavenRegular(activity));

			btnConfirm.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					String code = etCode.getText().toString().trim();
					if ("".equalsIgnoreCase(code)) {
						etCode.requestFocus();
						etCode.setError("URL can't be empty.");
					} else {
						Prefs.with(activity).save(SPLabels.CUSTOM_SERVER_URL, code);
						SharedPreferences preferences = activity.getSharedPreferences(Data.SETTINGS_SHARED_PREF_NAME, Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = preferences.edit();
						editor.putString(Data.SP_SERVER_LINK, code);
						editor.commit();

						MyApplication.getInstance().initializeServerURLAndRestClient(activity);
						dialog.dismiss();
					}
				}

			});


			etCode.setOnEditorActionListener(new OnEditorActionListener() {

				@Override
				public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
					int result = actionId & EditorInfo.IME_MASK_ACTION;
					switch (result) {
						case EditorInfo.IME_ACTION_DONE:
							btnConfirm.performClick();
							break;

						case EditorInfo.IME_ACTION_NEXT:
							break;

						default:
					}
					return true;
				}
			});


			dialog.findViewById(R.id.rl1).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
				}
			});


			dialog.findViewById(R.id.rv).setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});

			dialog.show();
			etCode.setSelection(etCode.getText().length());

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	


	@Override
	public void onLocationChanged(Location location, int priority) {
		Data.latitude = location.getLatitude();
		Data.longitude = location.getLongitude();
	}
	
	
	public static boolean isLastLocationUpdateFine(Activity activity){
		try {
			String driverServiceRun = Database2.getInstance(activity).getDriverServiceRun();
			String driverScreenMode = Database2.getInstance(activity).getDriverScreenMode();
			long lastLocationUpdateTime = Database2.getInstance(activity).getDriverLastLocationTime();
			
			long currentTime = System.currentTimeMillis();
			
			if(lastLocationUpdateTime == 0){
				lastLocationUpdateTime = System.currentTimeMillis();
			}
			
			long systemUpTime = SystemClock.uptimeMillis();
			
			
			
			if(systemUpTime > HomeActivity.MAX_TIME_BEFORE_LOCATION_UPDATE_REBOOT){
				if(Database2.YES.equalsIgnoreCase(driverServiceRun) &&
						(currentTime >= (lastLocationUpdateTime + HomeActivity.MAX_TIME_BEFORE_LOCATION_UPDATE_REBOOT))){
					if(Database2.VULNERABLE.equalsIgnoreCase(driverScreenMode)){
						showRestartPhonePopup(activity);
						return false;
					}
					else{
						dismissRestartPhonePopup();
						return true;
					}
				}
				else{
					dismissRestartPhonePopup();
					return true;
				}
			}
			else{
				dismissRestartPhonePopup();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			dismissRestartPhonePopup();
			return true;
		}
	}
	
	
	public static Dialog restartPhoneDialog;
	public static void showRestartPhonePopup(final Activity activity){
		try {
			if(restartPhoneDialog == null || !restartPhoneDialog.isShowing()){
				restartPhoneDialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
				restartPhoneDialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
				restartPhoneDialog.setContentView(R.layout.dialog_custom_one_button);
	
				RelativeLayout frameLayout = (RelativeLayout) restartPhoneDialog.findViewById(R.id.rv);
				new ASSL(activity, frameLayout, 1134, 720, true);
	
				WindowManager.LayoutParams layoutParams = restartPhoneDialog.getWindow().getAttributes();
				layoutParams.dimAmount = 0.6f;
				restartPhoneDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				restartPhoneDialog.setCancelable(false);
				restartPhoneDialog.setCanceledOnTouchOutside(false);
	
				TextView textHead = (TextView) restartPhoneDialog.findViewById(R.id.textHead);
				textHead.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
				textHead.setVisibility(View.GONE);
				TextView textMessage = (TextView) restartPhoneDialog.findViewById(R.id.textMessage);
				textMessage.setTypeface(Fonts.mavenRegular(activity));
	
				textMessage.setMovementMethod(new ScrollingMovementMethod());
				textMessage.setMaxHeight((int) (800.0f * ASSL.Yscale()));
				
				textMessage.setText(activity.getString(R.string.network_problem, activity.getString(R.string.appname)));
				
	
				Button btnOk = (Button) restartPhoneDialog.findViewById(R.id.btnOk);
				btnOk.setTypeface(Fonts.mavenRegular(activity));
	
				btnOk.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						restartPhoneDialog.dismiss();
						activity.finish();
					}
				});
	
				restartPhoneDialog.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static void dismissRestartPhonePopup(){
		try{
			if(restartPhoneDialog != null && restartPhoneDialog.isShowing()){
				restartPhoneDialog.dismiss();
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static boolean checkIfTrivialAPIErrors(Activity activity, JSONObject jObj, int flag, LogoutCallback callback){
		try {
			if(ApiResponseFlags.INVALID_ACCESS_TOKEN.getOrdinal() == flag){
				DialogPopup.dismissLoadingDialog();
				HomeActivity.logoutUser(activity, callback);
				return true;
			}
			else if(ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag){
				DialogPopup.dismissLoadingDialog();
				String errorMessage = jObj.getString("error");
				DialogPopup.alertPopup(activity, "", errorMessage);
				return true;
			}
			else if(ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag){
				DialogPopup.dismissLoadingDialog();
				String message = jObj.getString("message");
				DialogPopup.alertPopup(activity, "", message);
				return true;
			}
			else{
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}


	private void getCityAsync(){
		DialogPopup.showLoadingDialog(SplashNewActivity.this, getResources().getString(R.string.loading));

		if (Data.locationFetcher != null) {
			Data.latitude = Data.locationFetcher.getLatitude();
			Data.longitude = Data.locationFetcher.getLongitude();
		}

		HashMap<String, String> params = new HashMap<String, String>();
		params.put("latitude", "" + Data.latitude);
		params.put("longitude", "" + Data.longitude);
		HomeUtil.putDefaultParams(params);
		RestClient.getApiServices().getCityRetro(params, "", new Callback<CityResponse>() {
			@Override
			public void success(CityResponse cityResponse, Response response) {
				try {
					String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
					JSONObject jObj;
					jObj = new JSONObject(jsonString);
					String message = JSONParser.getServerMessage(jObj);
					if (ApiResponseFlags.ACK_RECEIVED.getOrdinal() == cityResponse.getFlag()) {
						String errorMessage = jObj.getString("error");
					} else {
						res = cityResponse;
						vehicleTypes.clear();
						newCities.clear();
						offeringTypes.clear();
						vehicleTypes.addAll(res.getVehicleTypes());
						offeringTypes.addAll(res.getOfferingTypes());
						newCities.addAll(res.getCities());
						for(int i=0; i< res.getCities().size(); i++){
							if(res.getCities().get(i).getCityName().equalsIgnoreCase(res.getCurrentCity())){
								selectCitySp.setSelection(i);
								break;
							}
						}
						//setLanguageData(jObj);

//						Intent intent = new Intent(SplashNewActivity.this, RegisterScreen.class);
//						intent.putExtra("cityResponse", cityResponse);
//						startActivity(intent);
//						finish();
						overridePendingTransition(R.anim.right_in, R.anim.right_out);
					}
					DialogPopup.dismissLoadingDialog();
				} catch (Exception exception) {
					exception.printStackTrace();
					DialogPopup.dismissLoadingDialog();
				}
			}

			@Override
			public void failure(RetrofitError error) {
				DialogPopup.dismissLoadingDialog();
			}
		});
	}

	public void showLanguagePreference() {

		if (languagePrefStatus == 1 && State.SPLASH_LS == state) {
			if(getResources().getInteger(R.integer.show_language_control)==getResources().getInteger(R.integer.view_visible)){
				selectLanguageLl.setVisibility(View.VISIBLE);

			}
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
				try {
					String item = parent.getItemAtPosition(position).toString();
					((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.white));

					Prefs.with(SplashNewActivity.this).save(SPLabels.SELECTED_LANGUAGE, item);
					if (!selectedLanguage.equalsIgnoreCase(Prefs.with(SplashNewActivity.this).getString(SPLabels.SELECTED_LANGUAGE, ""))) {
						selectedLanguage = Prefs.with(SplashNewActivity.this).getString(SPLabels.SELECTED_LANGUAGE, "");
						onCreate(new Bundle());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}

	public void fetchLanguageList() {
		try {
			if (AppStatus.getInstance(SplashNewActivity.this).isOnline(SplashNewActivity.this)) {
				if(categories.size() == 0) {
				DialogPopup.showLoadingDialog(SplashNewActivity.this, getResources().getString(R.string.loading));
				HashMap<String, String> params = new HashMap<>();
				params.put("device_model_name", android.os.Build.MODEL);
				params.put("android_version", android.os.Build.VERSION.RELEASE);
					HomeUtil.putDefaultParams(params);

					RestClient.getApiServices().fetchLanguageList(params, new Callback<DriverLanguageResponse>() {
						@Override
						public void success(DriverLanguageResponse registerScreenResponse, Response response) {
							String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
							DialogPopup.dismissLoadingDialog();
							try {
								JSONObject jObj = new JSONObject(responseStr);
								String message = JSONParser.getServerMessage(jObj);
								int flag = jObj.getInt("flag");
								if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
									setLanguageData(jObj);

								} else {
									DialogPopup.alertPopup(SplashNewActivity.this, "", message);
								}
							} catch (Exception e) {
								e.printStackTrace();
								DialogPopup.alertPopup(SplashNewActivity.this, "", Data.SERVER_ERROR_MSG);
							}

						}

						@Override
						public void failure(RetrofitError error) {
							DialogPopup.dismissLoadingDialog();
							DialogPopup.alertPopup(SplashNewActivity.this, "", Data.SERVER_ERROR_MSG);
						}
					});
				} else {
					if(categories != null) {
						showLanguagePreference();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setLanguageData(JSONObject jObj) throws JSONException {
		languagePrefStatus = jObj.getInt("locale_preference_enabled");
		registerViaTooken = jObj.optInt("registration_enabled", RegisterOption.ONLY_TOOKAN.getOrdinal());
		JSONArray jArray = jObj.getJSONArray("locales");
//									toggleRegistrationButton();
		if (jArray != null) {
            categories.clear();
            for (int i = 0; i < jArray.length(); i++) {
                categories.add(jArray.get(i).toString());
            }
        }
		showLanguagePreference();
	}

	//	boolean loginState = false;
	private void changeUIState(State state) {
		imageViewJugnooLogo.requestFocus();
		relativeLayoutScrollStop.setVisibility(View.VISIBLE);
		switch (state) {
			case SPLASH_INIT:
				viewInitJugnoo.setVisibility(View.VISIBLE);
				viewInitSplashJugnoo.setVisibility(View.VISIBLE);
				viewInitLS.setVisibility(View.VISIBLE);
//				refreshApp = true;
				relativeLayoutJugnooLogo.setVisibility(View.VISIBLE);
				textViewRegDriver.setVisibility(View.GONE);
				relativeLayoutLS.setVisibility(View.VISIBLE);
				linearLayoutLoginSignupButtons.setVisibility(View.VISIBLE);
//				linearLayoutNoNet.setVisibility(View.GONE);

				linearLayoutLogin.setVisibility(View.VISIBLE);
				relativeLayoutSignup.setVisibility(View.VISIBLE);
				backBtn.setVisibility(View.GONE);
				break;

			case SPLASH_LS:
				viewInitJugnoo.setVisibility(View.GONE);
				viewInitSplashJugnoo.setVisibility(View.GONE);
				viewInitLS.setVisibility(View.GONE);
				if(getResources().getInteger(R.integer.show_language_control)==getResources().getInteger(R.integer.view_visible)){
					selectLanguageLl.setVisibility(View.VISIBLE);

				}
				relativeLayoutJugnooLogo.setVisibility(View.VISIBLE);
//				refreshApp = false;
				relativeLayoutLS.setVisibility(View.VISIBLE);
				linearLayoutLoginSignupButtons.setVisibility(View.VISIBLE);
//				linearLayoutNoNet.setVisibility(View.GONE);
				textViewRegDriver.setVisibility(View.GONE);
				linearLayoutLogin.setVisibility(View.VISIBLE);
				relativeLayoutSignup.setVisibility(View.VISIBLE);
				backBtn.setVisibility(View.GONE);
				break;

			case SPLASH_NO_NET:
				viewInitJugnoo.setVisibility(View.GONE);
				viewInitSplashJugnoo.setVisibility(View.VISIBLE);
				viewInitLS.setVisibility(View.GONE);
//				refreshApp = true;
				relativeLayoutJugnooLogo.setVisibility(View.VISIBLE);

				relativeLayoutLS.setVisibility(View.VISIBLE);
				linearLayoutLoginSignupButtons.setVisibility(View.GONE);
//				linearLayoutNoNet.setVisibility(View.VISIBLE);
				textViewRegDriver.setVisibility(View.GONE);
				linearLayoutLogin.setVisibility(View.VISIBLE);
				relativeLayoutSignup.setVisibility(View.VISIBLE);
				backBtn.setVisibility(View.GONE);
				break;

			case LOGIN:
				viewInitJugnoo.setVisibility(View.GONE);
				viewInitSplashJugnoo.setVisibility(View.GONE);
//				refreshApp = false;
				viewInitLS.setVisibility(View.GONE);
				selectLanguageLl.setVisibility(View.GONE);
				relativeLayoutJugnooLogo.setVisibility(View.VISIBLE);
//				loginState =true;
				relativeLayoutLS.setVisibility(View.GONE);
				linearLayoutLoginSignupButtons.setVisibility(View.VISIBLE);
//				linearLayoutNoNet.setVisibility(View.GONE);
				textViewRegDriver.setVisibility(View.GONE);
				linearLayoutLogin.setVisibility(View.VISIBLE);
				relativeLayoutSignup.setVisibility(View.GONE);
				backBtn.setVisibility(View.VISIBLE);
				resetFields();
				break;

			case SIGNUP:
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						getCityAsync();
					}
				}, 1000);
//				refreshApp = false;
				selectLanguageLl.setVisibility(View.GONE);
				viewInitJugnoo.setVisibility(View.GONE);
				viewInitSplashJugnoo.setVisibility(View.GONE);
				viewInitLS.setVisibility(View.GONE);
				relativeLayoutJugnooLogo.setVisibility(View.GONE);

				relativeLayoutLS.setVisibility(View.GONE);
				linearLayoutLoginSignupButtons.setVisibility(View.VISIBLE);
//				linearLayoutNoNet.setVisibility(View.GONE);
				textViewRegDriver.setVisibility(View.VISIBLE);
				linearLayoutLogin.setVisibility(View.GONE);
				relativeLayoutScrollStop.setVisibility(View.GONE);
				relativeLayoutSignup.setVisibility(View.VISIBLE);
				backBtn.setVisibility(View.VISIBLE);
				resetFields();
//				getAllowedAuthChannels(SplashNewActivity.this);
				break;

		}
		this.state = state;

		if(State.SPLASH_INIT == state) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					getDeviceToken();
				}
			}, 500);
		}
		else if(State.LOGIN == state){
			// set login screen values according to intent
//			setLoginScreenValuesOnCreate();
		}
		else if(State.SIGNUP == state) {
			// set signupscreen values according to intent
//			setSignupScreenValuesOnCreate();
		}
	}


	public void fetchMessages() {

		try {
			Uri uri = Uri.parse("content://sms/inbox");
			String[] selectionArgs;
			String selection;
			Cursor cursor;

			selectionArgs = new String[]{Long.toString(System.currentTimeMillis() - 900000)};
			selection = "date>?";
			cursor = SplashNewActivity.this.getContentResolver().query(uri, null, selection, selectionArgs, null);

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
								String[] arr2 = arr[0].split("Dear\\ Driver\\,\\ Your\\ One\\ Time\\ Password\\ is\\ ");
								otp = arr2[1];
								otp = otp.replaceAll("\\ ", "");
							}

							if (Utils.checkIfOnlyDigits(otp)) {
								if (!"".equalsIgnoreCase(otp)) {
									try {
										phoneNoOPTEt.setText(Prefs.with(SplashNewActivity.this).getString(SPLabels.DRIVER_LOGIN_PHONE_NUMBER, ""));
										phoneNoOPTEt.setEnabled(false);
										Intent loginIntent = new Intent(SplashNewActivity.this, LoginViaOTP.class);
										loginIntent.putExtra("phone_no",phoneNo);
										loginIntent.putExtra("otp",otp);
										startActivity(loginIntent);
										finish();
										overridePendingTransition(R.anim.right_in, R.anim.right_out);
										btnGenerateOtp.performClick();
										changeUIState(State.LOGIN);
										break;
									} catch (Exception e) {
										e.printStackTrace();
										changeUIState(State.LOGIN);
									}

								} else {
									changeUIState(State.LOGIN);
								}
							} else {
								changeUIState(State.LOGIN);
							}
						} else {
							changeUIState(State.LOGIN);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			if (cursor != null) {
				cursor.close();
				changeUIState(State.LOGIN);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

public BroadcastReceiver deviceTokenReceiver = new BroadcastReceiver() {
	@Override
	public void onReceive(Context context, Intent intent) {
			Log.i(SplashNewActivity.DEVICE_TOKEN_TAG, "onReceive: ");

		if(intent.getAction()!=null && intent.getAction().equals(Constants.ACTION_DEVICE_TOKEN_UPDATED)){
			Log.i(SplashNewActivity.DEVICE_TOKEN_TAG, "onReceive: going to call getDeviceToken()");

			//FirebaseInstanceId.getInstance().getToken() has   a token
			getDeviceToken();
		}


	}
};
	private IntentFilter intentFilter;

	private IntentFilter deviceTokenReceiverFilter(){
		if(intentFilter==null) {
			intentFilter = new IntentFilter();
			intentFilter.addAction(Constants.ACTION_DEVICE_TOKEN_UPDATED);
		}
		return intentFilter;
	}


}

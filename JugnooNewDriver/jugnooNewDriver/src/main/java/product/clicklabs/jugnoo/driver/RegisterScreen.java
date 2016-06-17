package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.location.Location;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.flurry.android.FlurryAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.Inflater;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.PreviousAccountInfo;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.CityResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.DeviceTokenGenerator;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.IDeviceTokenReceiver;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class RegisterScreen extends BaseActivity implements LocationUpdate{
	
	Button backBtn;
	TextView title;

	EditText nameEt, phoneNoEt;
	Button signUpBtn;
	Spinner selectCitySp, autoNumEt;
//	ImageView isRentedCheck, isOwnedCheck;
	LinearLayout relative;
//	RelativeLayout isOwnedRelative, isRentedRelative;

	String name = "", emailId = "", phoneNo = "", password = "", accessToken = "", autoNum = "";
	Integer cityposition;
	boolean sendToOtpScreen = false;
//	boolean isRented = false, isOwned = false;

	public static JSONObject multipleCaseJSON;


	public void resetFlags() {
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

//		isOwnedRelative = (RelativeLayout) findViewById(R.id.isOwnedRelative);
//		isRentedRelative = (RelativeLayout) findViewById(R.id.isRentedRelative);

		backBtn = (Button) findViewById(R.id.backBtn);
		backBtn.setTypeface(Data.latoRegular(getApplicationContext()));
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Data.latoRegular(getApplicationContext()));

		nameEt = (EditText) findViewById(R.id.nameEt);
		nameEt.setTypeface(Data.latoRegular(getApplicationContext()));
		phoneNoEt = (EditText) findViewById(R.id.phoneNoEt);
		phoneNoEt.setTypeface(Data.latoRegular(getApplicationContext()));
		autoNumEt = (Spinner) findViewById(R.id.autoNumEt);
//		autoNumEt.setTypeface(Data.latoRegular(getApplicationContext()));
//		isRentedCheck = (ImageView) findViewById(R.id.isRentedCheck);
//		isOwnedCheck = (ImageView) findViewById(R.id.isOwnedCheck);
		selectCitySp = (Spinner) findViewById(R.id.selectCitySp);

		signUpBtn = (Button) findViewById(R.id.signUpBtn);
		signUpBtn.setTypeface(Data.latoRegular(getApplicationContext()));


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


//		autoNumEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//
//			@Override
//			public void onFocusChange(View v, boolean hasFocus) {
//				autoNumEt.setError(null);
//			}
//		});
//
//		autoNumEt.setLongClickable(false);

		phoneNoEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				phoneNoEt.setError(null);
			}
		});

//		isOwnedRelative.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				isOwned= true;
//				isRented =false;
//				isOwnedCheck.setImageResource(R.drawable.radio_select);
//				isRentedCheck.setImageResource(R.drawable.radio_unslelcet);
//
//			}
//		});
//
//		isRentedRelative.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				isOwned= false;
//				isRented =true;
//				isOwnedCheck.setImageResource(R.drawable.radio_unslelcet);
//				isRentedCheck.setImageResource(R.drawable.radio_select);
//			}
//		});


		signUpBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				String name = nameEt.getText().toString().trim();
				if (name.length() > 0) {
					name = name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
				}
//				String autoNum = autoNumEt.getText().toString().trim();
				String phoneNo = phoneNoEt.getText().toString().trim();

				if ("".equalsIgnoreCase(name)) {
					nameEt.requestFocus();
					nameEt.setError("Please enter name");
				} else {
					if ("".equalsIgnoreCase(" ")) {
//						autoNumEt.requestFocus();
//						autoNumEt.setError("Please enter auto number");
					} else {
						if ("".equalsIgnoreCase(phoneNo)) {
							phoneNoEt.requestFocus();
							phoneNoEt.setError("Please enter phone number");
						} else {
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

							if (phoneNo.length() >= 10) {
								phoneNo = phoneNo.substring(phoneNo.length() - 10, phoneNo.length());
								if (phoneNo.charAt(0) == '0' || phoneNo.charAt(0) == '1' || phoneNo.contains("+")) {
									phoneNoEt.requestFocus();
									phoneNoEt.setError("Please enter valid phone number");
								} else {
									phoneNo = "+91" + phoneNo;
									if (isPhoneValid(phoneNo)) {
										sendSignupValues(RegisterScreen.this, name, phoneNo, password);
										FlurryEventLogger.emailSignupClicked(emailId);
									} else {
										phoneNoEt.requestFocus();
										phoneNoEt.setError("Please enter valid phone number");
									}
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
			CityResponse res =  (CityResponse)getIntent().getSerializableExtra("cityResponse");
			selectCitySp.setAdapter(new CityArrayAdapter(this, R.layout.spinner_layout, res.getCities()));
		} catch(Exception e){
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

		selectCitySp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				cityposition = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});






//		autoNumEt.setOnItemSelectedListener(this);

		// Spinner Drop down elements
		List<String> categories = new ArrayList<String>();
		categories.add("Owned");
		categories.add("Rented");

		// Creating adapter for spinner
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

		// Drop down layout style - list view with radio button
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// attaching data adapter to spinner
		autoNumEt.setAdapter(dataAdapter);

		autoNumEt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String item = parent.getItemAtPosition(position).toString();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

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

	}

	@Override
	protected void onPause() {
		try {
			if (Data.locationFetcher != null) {
				Data.locationFetcher.destroy();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.onPause();


	}


//	Retrofit

	public void sendSignupValues(final Activity activity, final String name, final String phoneNo, final String city) {
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
//			params.put("auto_num", autoNum);
			params.put("city", city);
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

			if (Utils.isDeviceRooted()) {
				params.put("device_rooted", "1");
			} else {
				params.put("device_rooted", "0");
			}

			Log.i("register_using_email params", params.toString());

			RestClient.getApiServices().registerUsingEmail(params, new Callback<RegisterScreenResponse>() {
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
		OTPConfirmScreen.emailRegisterData = new EmailRegisterData(name, emailId, phoneNo, password, accessToken, autoNum);
		startActivity(new Intent(RegisterScreen.this, OTPConfirmScreen.class));
		finish();
		overridePendingTransition(R.anim.right_in, R.anim.right_out);
	}


	public void parseDataSendToMultipleAccountsScreen(Activity activity, JSONObject jObj) {
		OTPConfirmScreen.emailRegisterData = new EmailRegisterData(name, emailId, phoneNo, password, accessToken, autoNum);
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

		if (hasFocus && sendToOtpScreen) {
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


	public void performBackPressed() {
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
			return convertView;
		}

	}

}

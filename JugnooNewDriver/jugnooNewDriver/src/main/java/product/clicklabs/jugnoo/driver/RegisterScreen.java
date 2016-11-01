package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
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
import android.widget.Toast;

import com.flurry.android.FlurryAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.Inflater;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.EmailRegisterData;
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

	EditText nameEt, phoneNoEt, referralCodeEt;
	Button signUpBtn;
	Spinner selectCitySp, autoNumEt, VehicleType;
//	ImageView isRentedCheck, isOwnedCheck;
	LinearLayout relative;
	TextView textViewTandC;
	ImageView imageViewTandC;
//	RelativeLayout isOwnedRelative, isRentedRelative;

	String name = "", emailId = "", phoneNo = "", password = "", accessToken = "", autoNum = "", vehicleStatus="";
	Integer cityposition, vehiclePosition;
	CityResponse res;
	boolean tandc = false;
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

		backBtn = (Button) findViewById(R.id.backBtn);
		backBtn.setTypeface(Data.latoRegular(getApplicationContext()));
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Data.latoRegular(getApplicationContext()));

		nameEt = (EditText) findViewById(R.id.nameEt);
		nameEt.setTypeface(Data.latoRegular(getApplicationContext()));
		referralCodeEt = (EditText) findViewById(R.id.referralCodeEt);
		referralCodeEt.setTypeface(Data.latoRegular(getApplicationContext()));
		phoneNoEt = (EditText) findViewById(R.id.phoneNoEt);
		phoneNoEt.setTypeface(Data.latoRegular(getApplicationContext()));
		autoNumEt = (Spinner) findViewById(R.id.autoNumEt);
		VehicleType = (Spinner) findViewById(R.id.VehicleType);
		selectCitySp = (Spinner) findViewById(R.id.selectCitySp);

		textViewTandC = (TextView) findViewById(R.id.textViewTandC);
		textViewTandC.setTypeface(Data.latoRegular(getApplicationContext()));
		imageViewTandC = (ImageView) findViewById(R.id.imageViewTandC);

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

		phoneNoEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				phoneNoEt.setError(null);
			}
		});

		referralCodeEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				referralCodeEt.setError(null);
			}
		});

		textViewTandC.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(RegisterScreen.this, HelpActivity.class));
			}
		});

		imageViewTandC.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(tandc){
					imageViewTandC.setImageResource(R.drawable.audit_checkbox);
					tandc = false;
				} else {
					imageViewTandC.setImageResource(R.drawable.boxwith_tick);
					tandc = true;
				}
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
										if (cityposition != 0) {
											if (!vehicleStatus.equalsIgnoreCase(getResources().getString(R.string.vehicle_status))) {
												if (vehiclePosition != 0) {
													if(tandc) {
														sendSignupValues(RegisterScreen.this, name, phoneNo, password, referralCode);
														FlurryEventLogger.emailSignupClicked(emailId);
													} else {
														DialogPopup.alertPopup(RegisterScreen.this, "", getResources().getString(R.string.select_tandc));
													}
												} else {
													DialogPopup.alertPopup(RegisterScreen.this, "", getResources().getString(R.string.select_valid_vehicle_type));
												}
											} else {
												DialogPopup.alertPopup(RegisterScreen.this, "", getResources().getString(R.string.select_valid_vehicle_status));
											}
										} else {
											DialogPopup.alertPopup(RegisterScreen.this, "", getResources().getString(R.string.select_valid_city));
										}

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
			res =  (CityResponse)getIntent().getSerializableExtra("cityResponse");
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
				cityposition = res.getCities().get(position).getCityId();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});




		// Spinner Drop down elements
		List<String> categories = new ArrayList<String>();
		categories.add(getResources().getString(R.string.vehicle_status));
		categories.add(getResources().getString(R.string.owned));
		categories.add(getResources().getString(R.string.rented));


		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		autoNumEt.setAdapter(dataAdapter);

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



		VehicleType.setAdapter(new VehicyleArrayAdapter(this, R.layout.spinner_layout, res.getVehicleTypes()));

		VehicleType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String item = parent.getItemAtPosition(position).toString();
				vehiclePosition = res.getVehicleTypes().get(position).getVehicleType();
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
	public void onPause() {
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

	public void sendSignupValues(final Activity activity, final String name, final String phoneNo, final String city, final String referralCode) {
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
			params.put("city", String.valueOf(cityposition));
			params.put("latitude", "" + Data.latitude);
			params.put("longitude", "" + Data.longitude);
			params.put("vehicle_type",""+vehiclePosition);
			params.put("vehicle_status",vehicleStatus);
			params.put("device_type", Data.DEVICE_TYPE);
			params.put("device_name", Data.deviceName);
			params.put("app_version", "" + Data.appVersion);
			params.put("os_version", Data.osVersion);
			params.put("country", Data.country);
			params.put("client_id", Data.CLIENT_ID);
			params.put("login_type", Data.LOGIN_TYPE);
			params.put("pushy_token", Data.pushyToken);
			params.put("referral_code", ""+referralCode);
			params.put("device_token", Data.deviceToken);
			params.put("unique_device_id", Data.uniqueDeviceId);

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

									DialogPopup.alertPopup(activity, "", message);

//									RegisterScreen.this.name = name;
//									RegisterScreen.this.emailId = emailId;
//									RegisterScreen.this.phoneNo = phoneNo;
//									RegisterScreen.this.password = password;
//									RegisterScreen.this.accessToken = "";
//									parseDataSendToMultipleAccountsScreen(activity, jObj);
								} else if (ApiResponseFlags.INVALID_ACCESS_TOKEN.getOrdinal() == flag) {
									performBackPressed();

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

}

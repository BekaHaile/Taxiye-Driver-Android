package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.picker.Country;
import com.picker.CountryPicker;
import com.picker.OnCountryPickerListener;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.ProfileUpdateMode;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.StripeLoginResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.FirebaseEvents;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by aneeshbansal on 14/01/16.
 */
public class EditDriverProfile extends BaseFragmentActivity {
	LinearLayout relative, activity_profile_screen;
	RelativeLayout driverDetailsRLL;
	Button backBtn;
	ImageView imageViewEditPhone;
	TextView title, TextViewAccNo, textViewIFSC, textViewBankName, textViewBankLoc;
	ScrollView scrollView;

	EditText editTextUserName, editTextPhone;
	TextView tvCountryCode;
	ImageView profileImg, imageViewTitleBarDEI;
	CountryPicker countryPicker;
	private LinearLayout accountDetailsLayout,layoutBankDetails;
	private Button buttonStripe;
	public static final int REQUEST_CODE_STRIPE_CONNECT = 0x23;
    private int stripeStatus;
    //	public static ProfileInfo openProfileInfo;


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
	protected void onResume() {
		super.onResume();
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activty_edit_driver_profile);
        stripeStatus = Prefs.with(EditDriverProfile.this).getInt(Constants.STRIPE_ACCOUNT_STATUS, 0);

		layoutBankDetails= (LinearLayout) findViewById(R.id.layout_bank_details);
		buttonStripe= (Button) findViewById(R.id.button_stripe);
		relative = (LinearLayout) findViewById(R.id.activity_profile_screen);
		activity_profile_screen = (LinearLayout) findViewById(R.id.activity_profile_screen);
		driverDetailsRLL = (RelativeLayout) findViewById(R.id.driverDetailsRLL);

		new ASSL(this, relative, 1134, 720, false);

		backBtn = (Button) findViewById(R.id.backBtn);
//		imageViewEditName = (ImageView) findViewById(R.id.imageViewEditName);
		imageViewEditPhone = (ImageView) findViewById(R.id.imageViewEditPhone);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Data.latoRegular(this));

		editTextUserName = (EditText) findViewById(R.id.editTextUserName);
		editTextUserName.setTypeface(Data.latoRegular(this));
		editTextPhone = (EditText) findViewById(R.id.editTextPhone);
		editTextPhone.setTypeface(Data.latoRegular(this));
		tvCountryCode = (TextView) findViewById(R.id.tvCountryCode);
		tvCountryCode.setTypeface(Data.latoRegular(this));
		TextViewAccNo = (TextView) findViewById(R.id.TextViewAccNo);
		TextViewAccNo.setTypeface(Data.latoRegular(this));
		textViewIFSC = (TextView) findViewById(R.id.textViewIFSC);
		textViewIFSC.setTypeface(Data.latoRegular(this));
		textViewBankName = (TextView) findViewById(R.id.textViewBankName);
		textViewBankName.setTypeface(Data.latoRegular(this));
		textViewBankLoc = (TextView) findViewById(R.id.textViewBankLoc);
		textViewBankLoc.setTypeface(Data.latoRegular(this));

		profileImg = (ImageView) findViewById(R.id.profileImg);
		imageViewTitleBarDEI = (ImageView) findViewById(R.id.imageViewTitleBarDEI);

		accountDetailsLayout = (LinearLayout) findViewById(R.id.bankDetailsLLayout);
		if(Prefs.with(this).getInt(Constants.BANK_DETAILS_IN_EDIT_PROFILE, 1) == 1){
			accountDetailsLayout.setVisibility(View.VISIBLE);
		} else {
			accountDetailsLayout.setVisibility(View.GONE);
		}

		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				MyApplication.getInstance().logEvent(FirebaseEvents.PROFILE_PAGE+"_2_"+FirebaseEvents.BACK, null);
				performBackPressed();
			}
		});
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		countryPicker = new CountryPicker.Builder().with(this)
				.listener(new OnCountryPickerListener() {
					@Override
					public void onSelectCountry(Country country) {
						tvCountryCode.setText(country.getDialCode());
					}
				})
				.build();
		tvCountryCode.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				countryPicker.showDialog(getSupportFragmentManager());
			}
		});

		if(DriverProfileActivity.openedProfileInfo != null){

			if(stripeStatus==StripeUtils.STRIPE_ACCOUNT_AVAILABLE || stripeStatus==StripeUtils.STRIPE_ACCOUNT_CONNECTED){
				buttonStripe.setVisibility(View.VISIBLE);
				layoutBankDetails.setVisibility(View.GONE);
				if(stripeStatus==StripeUtils.STRIPE_ACCOUNT_CONNECTED){
					buttonStripe.setText(getString(R.string.login_with_stripe));
				}


			}else{
				TextViewAccNo.setText(DriverProfileActivity.openedProfileInfo.accNo);
				textViewIFSC.setText(DriverProfileActivity.openedProfileInfo.ifscCode);
				textViewBankName.setText(DriverProfileActivity.openedProfileInfo.bankName);
				textViewBankLoc.setText(DriverProfileActivity.openedProfileInfo.bankLoc);
			}

		}

		imageViewEditPhone.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editTextPhone.setError(null);
				MyApplication.getInstance().logEvent(FirebaseEvents.PROFILE_PAGE + "_2_" + FirebaseEvents.CONTACT_EDIT, null);
				if (editTextPhone.isEnabled()) {
					String phoneChanged = editTextPhone.getText().toString().trim();
					if(TextUtils.isEmpty(tvCountryCode.getText().toString())){
						Toast.makeText(EditDriverProfile.this, getString(R.string.please_select_country_code), Toast.LENGTH_SHORT).show();
						return;
					}
					if ("".equalsIgnoreCase(phoneChanged)) {
						editTextPhone.requestFocus();
						editTextPhone.setError(getResources().getString(R.string.phone_no_cnt_be_empty));
					} else {
						phoneChanged = Utils.retrievePhoneNumberTenChars(tvCountryCode.getText().toString(), phoneChanged);
						if (Utils.validPhoneNumber(phoneChanged)) {
							phoneChanged = tvCountryCode.getText().toString() + phoneChanged;
							if (Data.userData.phoneNo.equalsIgnoreCase(phoneChanged)) {
								editTextPhone.requestFocus();
								editTextPhone.setError(getResources().getString(R.string.changed_no_same_as_previous));
							} else {
								updateUserProfileAPIRetroo(EditDriverProfile.this, phoneChanged, ProfileUpdateMode.PHONE, tvCountryCode.getText().toString());
							}
						} else {
							editTextPhone.requestFocus();
							editTextPhone.setError(getResources().getString(R.string.enter_valid_phone_number));
						}
					}
				} else {
					editTextPhone.requestFocus();
					editTextPhone.setEnabled(true);
					editTextPhone.setSelection(editTextPhone.getText().length());
					tvCountryCode.setEnabled(true);
					tvCountryCode.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_vector, 0);
					Utils.showSoftKeyboard(EditDriverProfile.this, editTextPhone);
				}
			}
		});

		editTextPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				imageViewEditPhone.performClick();
				return true;
			}
		});

		editTextUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					editTextUserName.setError(null);
				}
			}
		});

		editTextPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					editTextPhone.setError(null);
				}
			}
		});

		activity_profile_screen.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editTextUserName.setError(null);
				editTextPhone.setError(null);

			}
		});
		buttonStripe.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(stripeStatus == StripeUtils.STRIPE_ACCOUNT_CONNECTED){
					loginToStripe();


				}else if(stripeStatus ==StripeUtils.STRIPE_ACCOUNT_AVAILABLE){

					startActivityForResult(new Intent(EditDriverProfile.this, StripeConnectActivity.class).
					putExtra(StripeConnectActivity.ARGS_URL_TO_OPEN,StripeUtils.stripeConnectBuilder(EditDriverProfile.this).toString()), REQUEST_CODE_STRIPE_CONNECT);
					overridePendingTransition(R.anim.right_in, R.anim.right_out);

				}


			}
		});

		setUserData();
	}

	private void loginToStripe() {
		//login to stripe account
		DialogPopup.showLoadingDialog(EditDriverProfile.this,getString(R.string.loading));
		HashMap<String,String> params = new HashMap<>();
		params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
		HomeUtil.putDefaultParams(params);
		RestClient.getApiServices().fetchStripeLink(params, new Callback<StripeLoginResponse>() {
            @Override
            public void success(StripeLoginResponse stripeLoginResponse, Response response) {
                DialogPopup.dismissLoadingDialog();
                if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == stripeLoginResponse.getFlag()){

                    startActivity(new Intent(EditDriverProfile.this, StripeConnectActivity.class).
                    putExtra(StripeConnectActivity.ARGS_URL_TO_OPEN,stripeLoginResponse.getStripeLoginUrl()));
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                }else{
                    DialogPopup.alertPopup(EditDriverProfile.this, "", stripeLoginResponse.getErrorMesssage());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                DialogPopup.dismissLoadingDialog();
                DialogPopup.alertPopup(EditDriverProfile.this, "", error.getMessage());
            }
        });
	}


	public void performBackPressed() {
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}

	@Override
	public void onBackPressed() {
		performBackPressed();
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			ASSL.closeActivity(relative);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.gc();
	}


	public void setUserData() {
		try {
			editTextUserName.setEnabled(false);
			editTextPhone.setEnabled(false);
			tvCountryCode.setEnabled(false); tvCountryCode.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
			try {
				Picasso.with(this).load(Data.userData.userImage)
						.transform(new CircleTransform())
						.into(profileImg);
			} catch (Exception e) {
			}

			if (Data.userData != null) {
				editTextUserName.setText(Data.userData.userName);
				editTextPhone.setText(Utils.retrievePhoneNumberTenChars(Data.userData.getCountryCode(), Data.userData.phoneNo));
				tvCountryCode.setText(Data.userData.getCountryCode());
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void updateUserProfileAPIRetroo(final Activity activity, final String updatedField, final ProfileUpdateMode profileUpdateMode, final String countryCode) {
		if (AppStatus.getInstance(activity).isOnline(activity)) {

			DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.updating));

			HashMap<String, String> params = new HashMap<String, String>();

			params.put("access_token", Data.userData.accessToken);
			params.put("is_access_token_new", "1");
			params.put("login_type", Data.LOGIN_TYPE);
			params.put("client_id", Data.CLIENT_ID);

			if (ProfileUpdateMode.PHONE.getOrdinal() == profileUpdateMode.getOrdinal()) {
				params.put("updated_phone_no", updatedField);
				params.put(Constants.KEY_UPDATED_COUNTRY_CODE, countryCode);
			} else {
				params.put("updated_user_name", updatedField);
			}

			RestClient.getApiServices().updateUserProfileAPIRetroo(params, new Callback<RegisterScreenResponse>() {
				@Override
				public void success(RegisterScreenResponse registerScreenResponse, Response response) {
					DialogPopup.dismissLoadingDialog();
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj;
						jObj = new JSONObject(jsonString);
						int flag = jObj.getInt("flag");
						String message = JSONParser.getServerMessage(jObj);
						if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag)) {
							if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
								DialogPopup.dialogBanner(activity, message);
							} else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								if (ProfileUpdateMode.PHONE.getOrdinal() == profileUpdateMode.getOrdinal()) {
									Intent intent = new Intent(activity, OTPConfirmScreen.class);
									intent.putExtra(Constants.PHONE_NO_VERIFY, updatedField);
									intent.putExtra(Constants.KEY_COUNTRY_CODE, countryCode);
									intent.putExtra(Constants.KNOWLARITY_NO, jObj.optString("knowlarity_missed_call_number",""));
									activity.startActivity(intent);
									activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
									finish();
								} else {
									DialogPopup.dialogBanner(activity, message);
									Data.userData.userName = updatedField;
									editTextUserName.setEnabled(false);
									editTextUserName.setText(Data.userData.userName);
								}
							} else {
								DialogPopup.alertPopup(activity, "", message);
							}
						}
					} catch (Exception exception) {
						exception.printStackTrace();
						DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
						DialogPopup.dismissLoadingDialog();
					}
				}

				@Override
				public void failure(RetrofitError error) {

				}
			});
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==REQUEST_CODE_STRIPE_CONNECT){

			if(resultCode==RESULT_OK){
				stripeStatus = StripeUtils.STRIPE_ACCOUNT_CONNECTED;
				Prefs.with(EditDriverProfile.this).getInt(Constants.STRIPE_ACCOUNT_STATUS, stripeStatus);
				buttonStripe.setText(getString(R.string.login_with_stripe));


			}

		}

	}
}

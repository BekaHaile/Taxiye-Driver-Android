package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
import product.clicklabs.jugnoo.driver.stripe.StripeUtils;
import product.clicklabs.jugnoo.driver.stripe.connect.StripeConnectActivity;
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
	RelativeLayout relative;
	ImageView backBtn;
	ImageView imageViewEditPhone, imageViewEdit;
	TextView title, tvAccNo, textViewIFSC, textViewBankName, textViewBankLoc;
	ScrollView scrollView;

	EditText editTextUserName, editTextLastName, editTextPhone, editTextUserEmail;
	TextView tvCountryCode;
	ImageView profileImg, imageViewTitleBarDEI;
	CountryPicker countryPicker;
	private CardView cvBankLayout;
	private Button buttonStripe;
	public static final int REQUEST_CODE_STRIPE_CONNECT_EXPRESS = 0x23;
	public static final int REQUEST_CODE_STRIPE_CONNECT_STANDARD = 0x24;
    private int stripeStatus;

    private Button bEditRateCard, bUploadDocuments;
    private boolean canEditName ,canEditEmail;
    private Drawable saveDrawable;
    private Drawable editDrawable;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activty_edit_driver_profile);
        stripeStatus = Prefs.with(EditDriverProfile.this).getInt(Constants.STRIPE_ACCOUNT_STATUS, 0);
		canEditName = Prefs.with(EditDriverProfile.this).getInt(Constants.KEY_USERNAME_EDITABLE_IN_PROFILE, 0)==1;
		canEditEmail = Prefs.with(this).getInt(Constants.KEY_EMAIL_EDITABLE_IN_PROFILE, 0) == 1;
		buttonStripe= (Button) findViewById(R.id.button_stripe);
		relative = (RelativeLayout) findViewById(R.id.activity_profile_screen);

		new ASSL(this, relative, 1134, 720, false);

		backBtn = findViewById(R.id.backBtn);
//		imageViewEditName = (ImageView) findViewById(R.id.imageViewEditName);
		imageViewEditPhone = (ImageView) findViewById(R.id.imageViewEditPhone);
		saveDrawable = ContextCompat.getDrawable(this,R.drawable.ic_save);
		editDrawable = ContextCompat.getDrawable(this,R.drawable.edit_pencil_icon);
		saveDrawable.mutate().setColorFilter(ContextCompat.getColor(this, R.color.themeColor), PorterDuff.Mode.SRC_ATOP);
		editDrawable.mutate().setColorFilter(ContextCompat.getColor(this, R.color.themeColor), PorterDuff.Mode.SRC_ATOP);
		imageViewEdit = (ImageView) findViewById(R.id.imageViewEdit);
		imageViewEditPhone.setImageDrawable(editDrawable);
		imageViewEdit.setImageDrawable(editDrawable);
		title = (TextView) findViewById(R.id.title);
		title.setText(R.string.profile);

		editTextUserName = (EditText) findViewById(R.id.editTextUserName);
		editTextLastName = (EditText) findViewById(R.id.editTextLastName);
		editTextUserEmail = (EditText) findViewById(R.id.editTextUserEmail);
		editTextPhone = (EditText) findViewById(R.id.editTextPhone);
		tvCountryCode = (TextView) findViewById(R.id.tvCountryCode);
		tvAccNo = (TextView) findViewById(R.id.tvAccNo);
		textViewIFSC = (TextView) findViewById(R.id.textViewIFSC);
		textViewBankName = (TextView) findViewById(R.id.textViewBankName);
		textViewBankLoc = (TextView) findViewById(R.id.textViewBankLoc);
		bEditRateCard = (Button) findViewById(R.id.bEditRateCard);
		bUploadDocuments = (Button) findViewById(R.id.bUploadDocuments);

		profileImg = (ImageView) findViewById(R.id.profileImg);
		imageViewTitleBarDEI = (ImageView) findViewById(R.id.imageViewTitleBarDEI);

		cvBankLayout = (CardView) findViewById(R.id.cvBankLayout);

		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				MyApplication.getInstance().logEvent(FirebaseEvents.PROFILE_PAGE+"_2_"+FirebaseEvents.BACK, null);
				performBackPressed();
			}
		});
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		countryPicker = new CountryPicker.Builder().with(this)
				.listener(new OnCountryPickerListener<Country>() {
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


		if(Prefs.with(this).getInt(Constants.BANK_DETAILS_IN_EDIT_PROFILE, 1) == 1){
			cvBankLayout.setVisibility(View.VISIBLE);
		} else {
			cvBankLayout.setVisibility(View.GONE);
		}

		if(DriverProfileActivity.openedProfileInfo != null){
			tvAccNo.setText(DriverProfileActivity.openedProfileInfo.accNo);
			textViewIFSC.setText(DriverProfileActivity.openedProfileInfo.ifscCode);
			textViewBankName.setText(DriverProfileActivity.openedProfileInfo.bankName);
			textViewBankLoc.setText(DriverProfileActivity.openedProfileInfo.bankLoc);

		}
		buttonStripe.setVisibility(View.GONE);
		if(stripeStatus== StripeUtils.STRIPE_EXPRESS_ACCOUNT_AVAILABLE || stripeStatus==StripeUtils.STRIPE_EXPRESS_ACCOUNT_CONNECTED
				|| stripeStatus == StripeUtils.STRIPE_STANDARD_ACCOUNT_AVAILABLE || stripeStatus == StripeUtils.STRIPE_STANDARD_ACCOUNT_CONNECTED){
			buttonStripe.setVisibility(View.VISIBLE);
			if(stripeStatus==StripeUtils.STRIPE_EXPRESS_ACCOUNT_CONNECTED || stripeStatus ==StripeUtils.STRIPE_STANDARD_ACCOUNT_CONNECTED){
				buttonStripe.setText(getString(R.string.login_with_stripe));
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
								updateUserProfileAPIRetroo(EditDriverProfile.this, phoneChanged, "", ProfileUpdateMode.PHONE,
										tvCountryCode.getText().toString(),null);
							}
						} else {
							editTextPhone.requestFocus();
							editTextPhone.setError(getResources().getString(R.string.enter_valid_phone_number));
						}
					}
				} else {
					editTextPhone.requestFocus();
					editTextPhone.setEnabled(false);
					editTextPhone.setSelection(editTextPhone.getText().length());
					tvCountryCode.setEnabled(false);
					imageViewEditPhone.setImageDrawable(saveDrawable);
					tvCountryCode.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_vector, 0);
					Utils.showSoftKeyboard(EditDriverProfile.this, editTextPhone);
				}
			}
		});

		imageViewEdit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (editTextUserEmail.isEnabled()) {
					String phoneChanged = editTextPhone.getText().toString().trim();
					String emailChanged = editTextUserEmail.getText().toString().trim();
					if(!TextUtils.isEmpty(emailChanged) && !Utils.isEmailValid(emailChanged)){
						Toast.makeText(EditDriverProfile.this, getString(R.string.valid_email), Toast.LENGTH_LONG).show();
						return;
					}
					if(TextUtils.isEmpty(tvCountryCode.getText().toString())){
						Toast.makeText(EditDriverProfile.this, getString(R.string.please_select_country_code), Toast.LENGTH_LONG).show();
						return;
					}
					if(TextUtils.isEmpty(phoneChanged)){
						Toast.makeText(EditDriverProfile.this, getString(R.string.phone_no_cnt_be_empty), Toast.LENGTH_LONG).show();
						return;
					}
					phoneChanged = Utils.retrievePhoneNumberTenChars(tvCountryCode.getText().toString(), phoneChanged);
					if (!Utils.validPhoneNumber(phoneChanged)) {
						Toast.makeText(EditDriverProfile.this, getString(R.string.enter_valid_phone_number), Toast.LENGTH_LONG).show();
						return;
					}
					phoneChanged = tvCountryCode.getText().toString() + phoneChanged;

					String firstName = editTextUserName.getText().toString().trim();
					if(firstName.length()==0){
						Toast.makeText(EditDriverProfile.this, getString(R.string.first_name_required), Toast.LENGTH_LONG).show();
						return;
					}
					String userName = firstName;
					String lastName = editTextLastName.getText().toString().trim();
					if(lastName.length()>0){
						userName += " " + lastName;
					}

					if (Data.userData.phoneNo.equalsIgnoreCase(phoneChanged) && emailChanged.equalsIgnoreCase(Data.userData.userEmail) &&
						Data.userData.userName.equals(userName)) {
						Toast.makeText(EditDriverProfile.this, getString(R.string.nothing_changed), Toast.LENGTH_LONG).show();
						return;
					}
					updateUserProfileAPIRetroo(EditDriverProfile.this, phoneChanged, emailChanged, ProfileUpdateMode.PHONE, tvCountryCode.getText().toString(),userName);

				} else {

					editTextUserEmail.setEnabled(canEditEmail);
					editTextUserName.setEnabled(canEditName);
					editTextLastName.setEnabled(canEditName);
					if(canEditName){
						editTextUserName.requestFocus();
					}else if(canEditEmail){
						editTextUserEmail.requestFocus();
					}
					editTextPhone.setEnabled(false);
					editTextUserEmail.setSelection(editTextUserEmail.getText().length());
					tvCountryCode.setEnabled(false);
					tvCountryCode.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down_vector, 0);
					imageViewEdit.setImageDrawable(saveDrawable);
					Utils.showSoftKeyboard(EditDriverProfile.this, editTextUserEmail);
				}
			}
		});

		editTextUserEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				editTextPhone.requestFocus();
				return true;
			}
		});

		editTextPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				if(imageViewEditPhone.getVisibility() == View.VISIBLE){
					imageViewEditPhone.performClick();
				} else if(imageViewEdit.getVisibility() == View.VISIBLE){
					imageViewEdit.performClick();
				}
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

		relative.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editTextUserName.setError(null);
				editTextPhone.setError(null);
				editTextUserEmail.setError(null);

			}
		});
		buttonStripe.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(stripeStatus == StripeUtils.STRIPE_EXPRESS_ACCOUNT_CONNECTED){
					loginToStripe();


				}else if(stripeStatus ==StripeUtils.STRIPE_EXPRESS_ACCOUNT_AVAILABLE){

					startActivityForResult(new Intent(EditDriverProfile.this, StripeConnectActivity.class).
					putExtra(StripeConnectActivity.ARGS_URL_TO_OPEN,StripeUtils.stripeExpressConnectBuilder(EditDriverProfile.this).toString()), REQUEST_CODE_STRIPE_CONNECT_EXPRESS);
					overridePendingTransition(R.anim.right_in, R.anim.right_out);

				}else if(stripeStatus ==StripeUtils.STRIPE_STANDARD_ACCOUNT_AVAILABLE){

					startActivityForResult(new Intent(EditDriverProfile.this, StripeConnectActivity.class).
					putExtra(StripeConnectActivity.ARGS_URL_TO_OPEN,StripeUtils.stripeStandardConnectBuilder(EditDriverProfile.this).toString()), REQUEST_CODE_STRIPE_CONNECT_STANDARD);
					overridePendingTransition(R.anim.right_in, R.anim.right_out);

				}else if(stripeStatus == StripeUtils.STRIPE_STANDARD_ACCOUNT_CONNECTED){

					loginToStripe();
				/*	startActivity(new Intent(EditDriverProfile.this, StripeConnectActivity.class).
							putExtra(StripeConnectActivity.ARGS_URL_TO_OPEN,StripeUtils.stripeConnectLoginBuilder(EditDriverProfile.this).toString()));
					overridePendingTransition(R.anim.right_in, R.anim.right_out);*/

				}


			}
		});

		bEditRateCard.setVisibility(Prefs.with(this).getInt(Constants.KEY_SHOW_EDIT_RATE_CARD, 0) == 1 ? View.VISIBLE : View.GONE);
		bUploadDocuments.setVisibility(getIntent().getBooleanExtra(Constants.SHOW_UPLOAD_DOCUMENTS, false) ? View.VISIBLE : View.GONE);
		bEditRateCard.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(EditDriverProfile.this, EditRateCardActivity.class));
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});
		bUploadDocuments.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(EditDriverProfile.this, DriverDocumentActivity.class);
				intent.putExtra("access_token", Data.userData.accessToken);
				intent.putExtra("in_side", true);
				intent.putExtra("doc_required", 0);
				startActivity(intent);
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
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
			editTextLastName.setEnabled(false);
			editTextPhone.setEnabled(false);
			editTextUserEmail.setEnabled(false);
			tvCountryCode.setEnabled(false); tvCountryCode.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
			imageViewEditPhone.setImageDrawable(editDrawable);
			imageViewEdit.setImageDrawable(editDrawable);
			try {
				Picasso.with(this).load(Data.userData.userImage)
						.transform(new CircleTransform())
						.into(profileImg);
			} catch (Exception e) {
			}

			if (Data.userData != null) {
				setUserName();
				editTextPhone.setText(Utils.retrievePhoneNumberTenChars(Data.userData.getCountryCode(), Data.userData.phoneNo));
				String email = Data.userData.userEmail;
				if((email.startsWith("temp_") && email.endsWith("@email.com"))||
				   (email.startsWith("jugnoo_") && email.endsWith("@jugnoo.in"))){
					editTextUserEmail.setText("");
				} else {
					editTextUserEmail.setText(email);
				}
				tvCountryCode.setText(Data.userData.getCountryCode());
			}

			if(canEditEmail || canEditName){
                imageViewEdit.setVisibility(View.VISIBLE);
                imageViewEditPhone.setVisibility(View.GONE);
            }else{
                imageViewEdit.setVisibility(View.GONE);
                imageViewEditPhone.setVisibility(View.VISIBLE);
            }




		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setUserName() {
		if (Data.userData!=null) {
			String firstName = Data.userData.userName,lastName = null;
			String[] names = firstName.split("\\s+");
			if(names.length>1){
                lastName = names[names.length-1];
                firstName = firstName.substring(0,firstName.length()-(1+ lastName.length()));
                //additional 1 is added for space
            }
			editTextUserName.setText(firstName);
            if(!TextUtils.isEmpty(firstName)) {
                editTextUserName.setSelection(firstName.length());
            }
			editTextLastName.setText(lastName);
			if(!TextUtils.isEmpty(lastName)) {
                editTextLastName.setSelection(lastName.length());
            }
		}
	}


	private void updateUserProfileAPIRetroo(final Activity activity, final String changedPhoneNo, final String changedEmailId, final ProfileUpdateMode profileUpdateMode, final String countryCode,@Nullable final String userName) {
		if (AppStatus.getInstance(activity).isOnline(activity)) {

			DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.updating));

			HashMap<String, String> params = new HashMap<String, String>();

			params.put("access_token", Data.userData.accessToken);
			params.put("is_access_token_new", "1");
			params.put("login_type", Data.LOGIN_TYPE);
			params.put("client_id", Data.CLIENT_ID);

			if (ProfileUpdateMode.PHONE.getOrdinal() == profileUpdateMode.getOrdinal()) {
				if (!Data.userData.phoneNo.equalsIgnoreCase(changedPhoneNo)) {
					params.put("updated_phone_no", changedPhoneNo);
					params.put(Constants.KEY_UPDATED_COUNTRY_CODE, countryCode);
				}
			}
			if(!TextUtils.isEmpty(userName) && !userName.equals(Data.userData.userName)) {
				params.put("updated_user_name", userName);
			}
			if(!TextUtils.isEmpty(changedEmailId) && !changedEmailId.equalsIgnoreCase(Data.userData.userEmail)){
				params.put(Constants.KEY_UPDATED_USER_EMAIL, changedEmailId);
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
						if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag, null)) {
							if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
								DialogPopup.dialogBanner(activity, message);
							} else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								if(!TextUtils.isEmpty(changedEmailId)){
									Data.userData.userEmail = changedEmailId;

								}

								if(!TextUtils.isEmpty(userName)){
									Data.userData.userName = userName;
								}
								if (ProfileUpdateMode.PHONE.getOrdinal() == profileUpdateMode.getOrdinal()) {
									if (!Data.userData.phoneNo.equalsIgnoreCase(changedPhoneNo)) {
										Intent intent = new Intent(activity, OTPConfirmScreen.class);
										intent.putExtra(Constants.PHONE_NO_VERIFY, changedPhoneNo);
										intent.putExtra(Constants.KEY_COUNTRY_CODE, countryCode);
										intent.putExtra(Constants.KNOWLARITY_NO, jObj.optString("knowlarity_missed_call_number", ""));
										activity.startActivity(intent);
										activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
										finish();
									} else {
										setUserData();
									}
									Utils.hideSoftKeyboard(activity, editTextPhone);
								} else {
									DialogPopup.dialogBanner(activity, message);
									setUserData();
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
		if(requestCode==REQUEST_CODE_STRIPE_CONNECT_EXPRESS || requestCode == REQUEST_CODE_STRIPE_CONNECT_STANDARD) {

			if(resultCode==RESULT_OK){
				stripeStatus = StripeUtils.STRIPE_EXPRESS_ACCOUNT_CONNECTED;
				if(requestCode==REQUEST_CODE_STRIPE_CONNECT_STANDARD){
					stripeStatus = StripeUtils.STRIPE_STANDARD_ACCOUNT_CONNECTED;
				}
				Prefs.with(EditDriverProfile.this).save(Constants.STRIPE_ACCOUNT_STATUS, stripeStatus);
				buttonStripe.setText(getString(R.string.login_with_stripe));


			}

		}

	}
}

package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

import com.flurry.android.FlurryAgent;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.ProfileUpdateMode;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by aneeshbansal on 14/01/16.
 */
public class EditDriverProfile extends BaseActivity {
	LinearLayout relative, activity_profile_screen;
	RelativeLayout driverDetailsRLL;
	Button backBtn;
	ImageView imageViewEditPhone;
	TextView title, TextViewAccNo, textViewIFSC, textViewBankName, textViewBankLoc;
	ScrollView scrollView;

	EditText editTextUserName, editTextPhone;
	ImageView profileImg, imageViewTitleBarDEI;
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
		TextViewAccNo = (TextView) findViewById(R.id.TextViewAccNo);
		title.setTypeface(Data.latoRegular(this));
		textViewIFSC = (TextView) findViewById(R.id.textViewIFSC);
		title.setTypeface(Data.latoRegular(this));
		textViewBankName = (TextView) findViewById(R.id.textViewBankName);
		title.setTypeface(Data.latoRegular(this));
		textViewBankLoc = (TextView) findViewById(R.id.textViewBankLoc);
		title.setTypeface(Data.latoRegular(this));

		profileImg = (ImageView) findViewById(R.id.profileImg);
		imageViewTitleBarDEI = (ImageView) findViewById(R.id.imageViewTitleBarDEI);

		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


		if(DriverProfileActivity.openedProfileInfo != null){
			TextViewAccNo.setText(DriverProfileActivity.openedProfileInfo.accNo);
			textViewIFSC.setText(DriverProfileActivity.openedProfileInfo.ifscCode);
			textViewBankName.setText(DriverProfileActivity.openedProfileInfo.bankName);
			textViewBankLoc.setText(DriverProfileActivity.openedProfileInfo.bankLoc);
		}

		imageViewEditPhone.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editTextPhone.setError(null);
				if (editTextPhone.isEnabled()) {
					String phoneChanged = editTextPhone.getText().toString().trim();
					if ("".equalsIgnoreCase(phoneChanged)) {
						editTextPhone.requestFocus();
						editTextPhone.setError(getResources().getString(R.string.phone_no_cnt_be_empty));
					} else {
						phoneChanged = Utils.retrievePhoneNumberTenChars(phoneChanged);
						if (Utils.validPhoneNumber(phoneChanged)) {
							phoneChanged = "+91" + phoneChanged;
							if (Data.userData.phoneNo.equalsIgnoreCase(phoneChanged)) {
								editTextPhone.requestFocus();
								editTextPhone.setError(getResources().getString(R.string.changed_no_same_as_previous));
							} else {
								updateUserProfileAPIRetroo(EditDriverProfile.this, phoneChanged, ProfileUpdateMode.PHONE);
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
					Utils.showSoftKeyboard(EditDriverProfile.this, editTextPhone);
				}
			}
		});

//		editTextUserName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//
//			@Override
//			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
//				imageViewEditName.performClick();
//				return true;
//			}
//		});
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

		setUserData();
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
			try {
				Picasso.with(this).load(Data.userData.userImage)
						.transform(new CircleTransform())
						.into(profileImg);
			} catch (Exception e) {
			}

			if (Data.userData != null) {
				editTextUserName.setText(Data.userData.userName);
				editTextPhone.setText(Data.userData.phoneNo);
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void updateUserProfileAPIRetroo(final Activity activity, final String updatedField, final ProfileUpdateMode profileUpdateMode) {
		if (AppStatus.getInstance(activity).isOnline(activity)) {

			DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.updating));

			HashMap<String, String> params = new HashMap<String, String>();

			params.put("access_token", Data.userData.accessToken);
			params.put("is_access_token_new", "1");
			params.put("login_type", Data.LOGIN_TYPE);
			params.put("client_id", Data.CLIENT_ID);

			if (ProfileUpdateMode.PHONE.getOrdinal() == profileUpdateMode.getOrdinal()) {
				params.put("updated_phone_no", updatedField);
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
}

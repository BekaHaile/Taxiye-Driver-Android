package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.flurry.android.FlurryAgent;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.oldRegistration.OldOTPConfirmScreen;
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

public class ChangePhoneBeforeOTPActivity extends BaseActivity {

	ImageView imageViewBack;
	TextView textViewTitle;

	TextView textViewChangePhoneNoHelp;
	EditText editTextNewPhoneNumber;
	Button buttonChangePhoneNumber;

	LinearLayout relative;

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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_phone_before_verify);

		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(ChangePhoneBeforeOTPActivity.this, relative, 1134, 720, false);

		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
		textViewTitle = (TextView) findViewById(R.id.textViewTitle);
		textViewTitle.setTypeface(Data.latoRegular(this), Typeface.BOLD);
		textViewTitle.setText(getStringText(R.string.change_phone_no));
		textViewChangePhoneNoHelp = (TextView) findViewById(R.id.textViewChangePhoneNoHelp);
		textViewChangePhoneNoHelp.setTypeface(Data.latoRegular(this));
		textViewChangePhoneNoHelp.setText(getStringText(R.string.enter_new_phone_number));
		editTextNewPhoneNumber = (EditText) findViewById(R.id.editTextNewPhoneNumber);
		editTextNewPhoneNumber.setTypeface(Data.latoRegular(this));
		editTextNewPhoneNumber.setHint(getStringText(R.string.enter_new_phone_no));
		buttonChangePhoneNumber = (Button) findViewById(R.id.buttonChangePhoneNumber);
		buttonChangePhoneNumber.setTypeface(Data.latoRegular(this));


		imageViewBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});

		editTextNewPhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				editTextNewPhoneNumber.setError(null);
			}
		});


		buttonChangePhoneNumber.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String previousPhoneNumber = "", accessToken = "";
				previousPhoneNumber = OTPConfirmScreen.emailRegisterData.phoneNo;
				accessToken = OTPConfirmScreen.emailRegisterData.accessToken;

				String phoneNoChanged = editTextNewPhoneNumber.getText().toString().trim();
				if ("".equalsIgnoreCase(phoneNoChanged)) {
					editTextNewPhoneNumber.requestFocus();
					editTextNewPhoneNumber.setError(getStringText(R.string.Phone_number_not_empty));
				} else {
					phoneNoChanged = Utils.retrievePhoneNumberTenChars(phoneNoChanged);
					if (Utils.validPhoneNumber(phoneNoChanged)) {
						phoneNoChanged = "+91" + phoneNoChanged;
						if (previousPhoneNumber.equalsIgnoreCase(phoneNoChanged)) {
							editTextNewPhoneNumber.requestFocus();
							editTextNewPhoneNumber.setError(getStringText(R.string.change_phone_no_text));
						} else {
							updateUserProfileAPI(ChangePhoneBeforeOTPActivity.this, phoneNoChanged, accessToken);

						}
					} else {
						editTextNewPhoneNumber.requestFocus();
						editTextNewPhoneNumber.setError(getStringText(R.string.valid_phone_number));
					}
				}
			}
		});

		editTextNewPhoneNumber.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				int result = actionId & EditorInfo.IME_MASK_ACTION;
				switch (result) {
					case EditorInfo.IME_ACTION_DONE:
						buttonChangePhoneNumber.performClick();
						break;

					case EditorInfo.IME_ACTION_NEXT:
						break;

					default:
				}
				return true;
			}
		});


		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

	}


	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}


//    Retrofit

	public void updateUserProfileAPI(final Activity activity, final String updatedField, String accessToken) {
		if (AppStatus.getInstance(activity).isOnline(activity)) {

			DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.updating));

//            RequestParams params = new RequestParams();
			HashMap<String, String> params = new HashMap<String, String>();

			params.put("client_id", Data.CLIENT_ID);
			params.put("login_type", Data.LOGIN_TYPE);
			params.put("access_token", accessToken);
			params.put("is_access_token_new", "1");
			params.put("updated_phone_no", updatedField);

			RestClient.getApiServices().updateUserProfileAPIRetro(params, new Callback<RegisterScreenResponse>() {
				@Override
				public void success(RegisterScreenResponse registerScreenResponse, Response response) {
					DialogPopup.dismissLoadingDialog();
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj;
						jObj = new JSONObject(jsonString);
						int flag = ApiResponseFlags.ACTION_COMPLETE.getOrdinal();
						if (jObj.has("flag")) {
							flag = jObj.getInt("flag");
						}
						String message = JSONParser.getServerMessage(jObj);
						if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag)) {
							if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
								DialogPopup.alertPopup(activity, "", message);
							} else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								OTPConfirmScreen.emailRegisterData.phoneNo = updatedField;
								DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {

									@Override
									public void onClick(View v) {
										performBackPressed();
									}
								});
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
					DialogPopup.dismissLoadingDialog();
					DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
				}
			});


		} else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}


	@Override
	public void onBackPressed() {
		performBackPressed();
		super.onBackPressed();
	}


	public void performBackPressed() {
		Intent intent = new Intent(ChangePhoneBeforeOTPActivity.this, OldOTPConfirmScreen.class);
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

}
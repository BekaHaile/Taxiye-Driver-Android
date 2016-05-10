package product.clicklabs.jugnoo.driver;

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

import com.flurry.android.FlurryAgent;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.Log;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class PhoneEditOTPConfirmScreen extends BaseActivity implements LocationUpdate {

	ImageView imageViewBack;
	TextView textViewTitle;

	TextView otpHelpText, textViewOTPNotReceived, textViewChangePhone;
	EditText editTextOTP;
	Button buttonVerify;

	RelativeLayout relativeLayoutOTPThroughCall, relativeLayoutChangePhone;

	LinearLayout relative;

	String otpHelpStr = getResources().getString(R.string.enter_otp_received);
	String phoneNumberToVerify = "";

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
		setContentView(R.layout.activity_otp_confrim);

		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(PhoneEditOTPConfirmScreen.this, relative, 1134, 720, false);

		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
		textViewTitle = (TextView) findViewById(R.id.textViewTitle);
		textViewTitle.setTypeface(Data.latoRegular(this));

		otpHelpText = (TextView) findViewById(R.id.otpHelpText);
		otpHelpText.setTypeface(Data.latoRegular(this));
		editTextOTP = (EditText) findViewById(R.id.editTextOTP);
		editTextOTP.setTypeface(Data.latoRegular(this));
		editTextOTP.setHint(getStringText(R.string.enter_otp));

		buttonVerify = (Button) findViewById(R.id.buttonVerify);
		buttonVerify.setTypeface(Data.latoRegular(this));

		relativeLayoutOTPThroughCall = (RelativeLayout) findViewById(R.id.relativeLayoutOTPThroughCall);
		relativeLayoutChangePhone = (RelativeLayout) findViewById(R.id.relativeLayoutChangePhone);
		relativeLayoutOTPThroughCall.setVisibility(View.GONE);
		relativeLayoutChangePhone.setVisibility(View.GONE);

		textViewOTPNotReceived = (TextView) findViewById(R.id.textViewOTPNotReceived);
		textViewOTPNotReceived.setTypeface(Data.latoRegular(this));
		textViewOTPNotReceived.setText(getStringText(R.string.not_recived_otp));

		((TextView) findViewById(R.id.textViewCallMe)).setTypeface(Data.latoRegular(this), Typeface.BOLD);

		textViewChangePhone = (TextView) findViewById(R.id.textViewChangePhone);
		textViewChangePhone.setTypeface(Data.latoRegular(this));
		textViewChangePhone.setText(getStringText(R.string.change_phone_no));

		((TextView) findViewById(R.id.textViewChange)).setTypeface(Data.latoRegular(this), Typeface.BOLD);

		imageViewBack.setOnClickListener(new View.OnClickListener() {

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
					sendSignupValues(PhoneEditOTPConfirmScreen.this, phoneNumberToVerify, otpCode);
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


		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		try {
			phoneNumberToVerify = getIntent().getStringExtra(Constants.PHONE_NO_VERIFY);
			if(phoneNumberToVerify != null) {
				otpHelpText.setText(getStringText(R.string.enter_otp_received)+" "+phoneNumberToVerify);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}


	public void sendSignupValues(final Activity activity, final String phoneNo, String otp) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));
			HashMap<String, String> params = new HashMap<>();

			params.put("client_id", Data.CLIENT_ID);
			params.put("access_token", Data.userData.accessToken);
			params.put("is_access_token_new", "1");
			params.put("phone_no", phoneNo);
			params.put("verification_token", otp);

			Log.i("params", ">"+params);

			RestClient.getApiServices().verifyMyContactNumber(params, new Callback<RegisterScreenResponse>() {
				@Override
				public void success(RegisterScreenResponse registerScreenResponse, Response response) {
					Log.i("Server response", "response = " + response);
					try {
						String responseStr = new String(((TypedByteArray)response.getBody()).getBytes());
						JSONObject jObj = new JSONObject(responseStr);
						int flag = jObj.getInt("flag");
						if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag)) {
							if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
								String error = jObj.getString("error");
								DialogPopup.dialogBanner(activity, error);
							} else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								String message = jObj.getString("message");
								DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										performBackPressed();
										Data.userData.phoneNo = phoneNo;
									}
								});
							} else {
								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
						}
					} catch (Exception exception) {
						exception.printStackTrace();
						DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
					}
					DialogPopup.dismissLoadingDialog();
				}

				@Override
				public void failure(RetrofitError error) {
					Log.e("request fail", error.toString());
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
	}


	public void performBackPressed() {
		Intent intent = new Intent(PhoneEditOTPConfirmScreen.this, EditDriverProfile.class);
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

}



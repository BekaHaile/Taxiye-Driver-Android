package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.BlurTransform;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;

import org.json.JSONObject;

import java.text.DecimalFormat;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.EmailVerificationStatus;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.datastructure.ProfileUpdateMode;
import product.clicklabs.jugnoo.datastructure.UserMode;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FacebookLoginHelper;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import rmn.androidscreenlibrary.ASSL;

public class AccountActivity extends Activity {

	RelativeLayout relative;
	
	TextView textViewTitle;
	ImageView imageViewBack;
	
	ImageView imageViewUserImageBlur, imageViewProfileImage;
	ProgressBar progressBarProfileUpdate;
	
	ScrollView scrollView;
	LinearLayout linearLayoutMain;
	TextView textViewScroll;
	
	EditText editTextUserName, editTextEmail, editTextPhone;
	ImageView imageViewEditName, imageViewEditEmail, imageViewEditPhoneNo;
	ImageView imageViewEmailVerifyStatus;
	RelativeLayout relativeLayoutEmailVerify;
	TextView textViewEmailVerifyMessage, textViewEmailVerify;
	RelativeLayout relativeLayoutChangePassword;
	TextView textViewChangePassword;

	Button buttonLogout;
	
	DecimalFormat decimalFormat = new DecimalFormat("#.#");
	DecimalFormat decimalFormatNoDec = new DecimalFormat("#");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_user);

		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(this, relative, 1134, 720, false);
		
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.latoRegular(this), Typeface.BOLD);
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
		
		imageViewUserImageBlur = (ImageView) findViewById(R.id.imageViewUserImageBlur);
		imageViewProfileImage = (ImageView) findViewById(R.id.imageViewProfileImage);
		
		progressBarProfileUpdate = (ProgressBar) findViewById(R.id.progressBarProfileUpdate);
		
		scrollView = (ScrollView) findViewById(R.id.scrollView);
		linearLayoutMain = (LinearLayout) findViewById(R.id.linearLayoutMain);
		textViewScroll = (TextView) findViewById(R.id.textViewScroll);
		
		editTextUserName = (EditText) findViewById(R.id.editTextUserName); editTextUserName.setTypeface(Fonts.latoRegular(this));
		editTextEmail = (EditText) findViewById(R.id.editTextEmail); editTextEmail.setTypeface(Fonts.latoRegular(this));
		editTextPhone = (EditText) findViewById(R.id.editTextPhone); editTextPhone.setTypeface(Fonts.latoRegular(this));
		
		imageViewEditName = (ImageView) findViewById(R.id.imageViewEditName);
		imageViewEditEmail = (ImageView) findViewById(R.id.imageViewEditEmail);
		imageViewEditPhoneNo = (ImageView) findViewById(R.id.imageViewEditPhoneNo);
		
		
		imageViewEmailVerifyStatus = (ImageView) findViewById(R.id.imageViewEmailVerifyStatus);
		relativeLayoutEmailVerify = (RelativeLayout) findViewById(R.id.relativeLayoutEmailVerify);
		textViewEmailVerifyMessage = (TextView) findViewById(R.id.textViewEmailVerifyMessage); textViewEmailVerifyMessage.setTypeface(Fonts.latoRegular(this));
		textViewEmailVerify = (TextView) findViewById(R.id.textViewEmailVerify); textViewEmailVerify.setTypeface(Fonts.latoRegular(this));
		
		relativeLayoutChangePassword = (RelativeLayout) findViewById(R.id.relativeLayoutChangePassword);
		textViewChangePassword = (TextView) findViewById(R.id.textViewChangePassword); textViewChangePassword.setTypeface(Fonts.latoRegular(this));

		buttonLogout = (Button) findViewById(R.id.buttonLogout); buttonLogout.setTypeface(Fonts.latoRegular(this));
		
		
		
		setUserData(false);
		
		
		imageViewBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});
		
		
		editTextUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					scrollView.smoothScrollTo(0, editTextUserName.getTop());
				}
				editTextUserName.setError(null);
			}
		});

		editTextEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					scrollView.smoothScrollTo(0, editTextEmail.getTop());
				}
				editTextEmail.setError(null);
			}
		});

		editTextPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					scrollView.smoothScrollTo(0, editTextPhone.getTop());
				}
				editTextPhone.setError(null);
			}
		});
		
		
		imageViewEditName.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				editTextUserName.setError(null);
				if(editTextUserName.isEnabled()){
					String nameChanged = editTextUserName.getText().toString().trim();
					if("".equalsIgnoreCase(nameChanged)){
						editTextUserName.requestFocus();
						editTextUserName.setError("Username can't be empty");
					}
					else{
						if(Data.userData.userName.equalsIgnoreCase(nameChanged)){
							editTextUserName.requestFocus();
							editTextUserName.setError("Changed Username is same as the previous one.");
						}
						else{
							updateUserProfileAPI(AccountActivity.this, nameChanged, ProfileUpdateMode.NAME);
						}
					}
				}
				else{
					editTextUserName.requestFocus();
					editTextUserName.setEnabled(true);
					editTextUserName.setSelection(editTextUserName.getText().length());
					Utils.showSoftKeyboard(AccountActivity.this, editTextUserName);
				}
			}
		});
		
		editTextUserName.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				int result = actionId & EditorInfo.IME_MASK_ACTION;
				switch (result) {
					case EditorInfo.IME_ACTION_DONE:
						imageViewEditName.performClick();
					break;

					case EditorInfo.IME_ACTION_NEXT:
						imageViewEditName.performClick();
					break;

					default:
				}
				return true;
			}
		});
		
		
		
		imageViewEditEmail.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				editTextEmail.setError(null);
				imageViewEmailVerifyStatus.setVisibility(View.GONE);
				
				if(editTextEmail.isEnabled()){
					String emailChanged = editTextEmail.getText().toString().trim();
					if("".equalsIgnoreCase(emailChanged)){
						editTextEmail.requestFocus();
						editTextEmail.setError("Email can't be empty");
					}
					else{
						if(Data.userData.userEmail.equalsIgnoreCase(emailChanged)){
							editTextEmail.requestFocus();
							editTextEmail.setError("Changed email is same as the previous one.");
						}
						else{
							updateUserProfileAPI(AccountActivity.this, emailChanged, ProfileUpdateMode.EMAIL);
						}
					}
				}
				else{
					editTextEmail.requestFocus();
					editTextEmail.setEnabled(true);
					editTextEmail.setSelection(editTextEmail.getText().length());
					Utils.showSoftKeyboard(AccountActivity.this, editTextEmail);
				}
			}
		});
		
		editTextEmail.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				int result = actionId & EditorInfo.IME_MASK_ACTION;
				switch (result) {
					case EditorInfo.IME_ACTION_DONE:
						imageViewEditEmail.performClick();
					break;

					case EditorInfo.IME_ACTION_NEXT:
						imageViewEditEmail.performClick();
					break;

					default:
				}
				return true;
			}
		});
		

		imageViewEditPhoneNo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				editTextPhone.setError(null);
				if(editTextPhone.isEnabled()){
					String phoneNoChanged = editTextPhone.getText().toString().trim();
					if("".equalsIgnoreCase(phoneNoChanged)){
						editTextPhone.requestFocus();
						editTextPhone.setError("Phone number can't be empty");
					}
					else{
                        phoneNoChanged = Utils.retrievePhoneNumberTenChars(phoneNoChanged);
                        if(Utils.validPhoneNumber(phoneNoChanged)) {
                            phoneNoChanged = "+91" + phoneNoChanged;
                            if(Data.userData.phoneNo.equalsIgnoreCase(phoneNoChanged)){
                                editTextPhone.requestFocus();
                                editTextPhone.setError("Changed Phone number is same as the previous one.");
                            }
                            else{
                                updateUserProfileAPI(AccountActivity.this, phoneNoChanged, ProfileUpdateMode.PHONE);
                            }
                        }
                        else{
                            editTextPhone.requestFocus();
                            editTextPhone.setError("Please enter valid phone number");
                        }
					}
				}
				else{
					editTextPhone.requestFocus();
					editTextPhone.setEnabled(true);
					editTextPhone.setSelection(editTextPhone.getText().length());
					Utils.showSoftKeyboard(AccountActivity.this, editTextPhone);
				}
			}
		});
		
		editTextPhone.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				int result = actionId & EditorInfo.IME_MASK_ACTION;
				switch (result) {
					case EditorInfo.IME_ACTION_DONE:
						imageViewEditPhoneNo.performClick();
					break;

					case EditorInfo.IME_ACTION_NEXT:
						imageViewEditPhoneNo.performClick();
					break;

					default:
				}
				return true;
			}
		});
		
		imageViewEmailVerifyStatus.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(Data.userData.emailVerificationStatus != EmailVerificationStatus.EMAIL_VERIFIED.getOrdinal()){
					if(relativeLayoutEmailVerify.getVisibility() == View.GONE){
						relativeLayoutEmailVerify.setVisibility(View.VISIBLE);
					}
					else{
						relativeLayoutEmailVerify.setVisibility(View.GONE);
					}
				}
			}
		});
		
		linearLayoutMain.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(relativeLayoutEmailVerify.getVisibility() == View.VISIBLE){
					relativeLayoutEmailVerify.setVisibility(View.GONE);
				}
			}
		});

		
		
		relativeLayoutEmailVerify.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(EmailVerificationStatus.EMAIL_UNVERIFIED.getOrdinal() == Data.userData.emailVerificationStatus){
					sendEmailVerifyLink(AccountActivity.this);
				}
				else if(EmailVerificationStatus.WRONG_EMAIL.getOrdinal() == Data.userData.emailVerificationStatus){
					imageViewEditEmail.performClick();
				}
			}
		});
		
		relativeLayoutChangePassword.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(AccountActivity.this, ChangePasswordActivity.class));
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});
		
		
		buttonLogout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {

				DialogPopup.alertPopupTwoButtonsWithListeners(AccountActivity.this, "", "Are you sure you want to logout?", "Logout", "Cancel", 
						new View.OnClickListener() {
							
							@Override
							public void onClick(View v) {
								logoutAsync(AccountActivity.this);
							}
						}, 
						new View.OnClickListener() {
							
							@Override
							public void onClick(View v) {
							}
						}, 
						true, false);
			
			}
		});
		

		
		final View activityRootView = findViewById(R.id.linearLayoutMain);
		activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						Rect r = new Rect();
						// r will be populated with the coordinates of your view
						// that area still visible.
						activityRootView.getWindowVisibleDisplayFrame(r);

						int heightDiff = activityRootView.getRootView()
								.getHeight() - (r.bottom - r.top);
						if (heightDiff > 100) { // if more than 100 pixels, its
												// probably a keyboard...

							/************** Adapter for the parent List *************/

							ViewGroup.LayoutParams params_12 = textViewScroll
									.getLayoutParams();

							params_12.height = (int)(heightDiff);

							textViewScroll.setLayoutParams(params_12);
							textViewScroll.requestLayout();

						} else {

							ViewGroup.LayoutParams params = textViewScroll
									.getLayoutParams();
							params.height = 0;
							textViewScroll.setLayoutParams(params);
							textViewScroll.requestLayout();

						}
					}
				});
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
	}
	
	
	
	
	public void setUserData(boolean refreshed){
		try {
			editTextUserName.setEnabled(false);
			editTextEmail.setEnabled(false);
			editTextPhone.setEnabled(false);
			
			editTextUserName.setText(Data.userData.userName);
			editTextEmail.setText(Data.userData.userEmail);
			editTextPhone.setText(Data.userData.phoneNo);
			
			if(EmailVerificationStatus.EMAIL_UNVERIFIED.getOrdinal() == Data.userData.emailVerificationStatus){
				imageViewEmailVerifyStatus.setVisibility(View.VISIBLE);
				imageViewEmailVerifyStatus.setImageResource(R.drawable.warning_icon);
				
				relativeLayoutEmailVerify.setVisibility(View.GONE);
				textViewEmailVerifyMessage.setText("Please verify the Address.");
				textViewEmailVerify.setText("VERIFY ME");
			}
			else if(EmailVerificationStatus.WRONG_EMAIL.getOrdinal() == Data.userData.emailVerificationStatus){
				imageViewEmailVerifyStatus.setVisibility(View.VISIBLE);
				imageViewEmailVerifyStatus.setImageResource(R.drawable.alert_icon);
				
				relativeLayoutEmailVerify.setVisibility(View.GONE);
				textViewEmailVerifyMessage.setText("Please enter a valid Address.");
				textViewEmailVerify.setText("CHANGE");
			}
			else{
				if(refreshed){
					imageViewEmailVerifyStatus.setVisibility(View.VISIBLE);
					imageViewEmailVerifyStatus.setImageResource(R.drawable.ok_icon);
					new Handler().postDelayed(new Runnable() {
						
						@Override
						public void run() {
							imageViewEmailVerifyStatus.setVisibility(View.GONE);
						}
					}, 4000);
				}
				else{
					imageViewEmailVerifyStatus.setVisibility(View.GONE);
				}
				relativeLayoutEmailVerify.setVisibility(View.GONE);
			}
			
			
			try{
				if(!"".equalsIgnoreCase(Data.userData.userImage)){
					Picasso.with(this).load(Data.userData.userImage).transform(new CircleTransform()).into(imageViewProfileImage);
					Picasso.with(this).load(Data.userData.userImage).transform(new BlurTransform()).into(imageViewUserImageBlur);
				}
			} catch(Exception e){
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	public void performBackPressed(){
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}
	
	@Override
	public void onBackPressed() {
		performBackPressed();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		HomeActivity.checkForAccessTokenChange(this);
		
		reloadProfileAPI(this);

		scrollView.scrollTo(0, 0);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ASSL.closeActivity(relative);
		System.gc();
	}
	
	
	
	public void updateUserProfileAPI(final Activity activity, final String updatedField, final ProfileUpdateMode profileUpdateMode) {
		if(AppStatus.getInstance(activity).isOnline(activity)) {
			
			DialogPopup.showLoadingDialog(activity, "Updating...");
			
			RequestParams params = new RequestParams();
		
			params.put("client_id", Config.getClientId());
			params.put("access_token", Data.userData.accessToken);
			params.put("is_access_token_new", "1");
			
			if(ProfileUpdateMode.EMAIL.getOrdinal() == profileUpdateMode.getOrdinal()){
				params.put("updated_user_email", updatedField);
			}
			else if(ProfileUpdateMode.PHONE.getOrdinal() == profileUpdateMode.getOrdinal()){
				params.put("updated_phone_no", updatedField);
			}
			else{
				params.put("updated_user_name", updatedField);
			}
			
			
			AsyncHttpClient client = Data.getClient();
			client.post(Config.getServerUrl() + "/update_user_profile", params,
					new CustomAsyncHttpResponseHandler() {
					private JSONObject jObj;

						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
							DialogPopup.dismissLoadingDialog();
							DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}

						@Override
						public void onSuccess(String response) {
							Log.i("Server response", "response = " + response);
							DialogPopup.dismissLoadingDialog();
							try {
								jObj = new JSONObject(response);
								if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
									int flag = jObj.getInt("flag");
									if(ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag){
										String error = jObj.getString("error");
										DialogPopup.dialogBanner(activity, error);
									}
									else if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){
										String message = jObj.getString("message");
										if(ProfileUpdateMode.EMAIL.getOrdinal() == profileUpdateMode.getOrdinal()){
											DialogPopup.dialogBanner(activity, message);
											editTextEmail.setEnabled(false);
											reloadProfileAPI(activity);
										}
										else if(ProfileUpdateMode.PHONE.getOrdinal() == profileUpdateMode.getOrdinal()){
											Intent intent = new Intent(activity, PhoneNoOTPConfirmScreen.class);
											intent.putExtra("phone_no_verify", updatedField);
											activity.startActivity(intent);
											activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
										}
										else{
											DialogPopup.dialogBanner(activity, message);
											Data.userData.userName = updatedField;
											editTextUserName.setEnabled(false);
											editTextUserName.setText(Data.userData.userName);
										}
									}
									else{
										DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
									}
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
								DialogPopup.dismissLoadingDialog();
							}
						}
					});
		}
		else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}
	
	
	
	public void reloadProfileAPI(final Activity activity) {
		if(AppStatus.getInstance(activity).isOnline(activity)) {
			
			progressBarProfileUpdate.setVisibility(View.VISIBLE);
			
			RequestParams params = new RequestParams();
		
			params.put("client_id", Config.getClientId());
			params.put("access_token", Data.userData.accessToken);
			params.put("is_access_token_new", "1");
			
			AsyncHttpClient client = Data.getClient();
			client.post(Config.getServerUrl() + "/reload_my_profile", params,
					new CustomAsyncHttpResponseHandler() {
					private JSONObject jObj;

						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
							progressBarProfileUpdate.setVisibility(View.GONE);
						}

						@Override
						public void onSuccess(String response) {
							Log.i("Server response", "response = " + response);
							try {
								jObj = new JSONObject(response);
								if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
									int flag = jObj.getInt("flag");
									if(ApiResponseFlags.PROFILE_INFORMATION.getOrdinal() == flag){
										
										String userName = jObj.getString("user_name");
										String email = jObj.getString("user_email");
										String phoneNo = jObj.getString("phone_no");
										int emailVerificationStatus = jObj.getInt("email_verification_status");
										
										Data.userData.userName = userName;
										Data.userData.phoneNo = phoneNo;
										Data.userData.userEmail = email;
										
										boolean refresh = false;
										
										if(EmailVerificationStatus.EMAIL_VERIFIED.getOrdinal() != Data.userData.emailVerificationStatus
												&& EmailVerificationStatus.EMAIL_VERIFIED.getOrdinal() == emailVerificationStatus){
											refresh = true;
										}
										
										Data.userData.emailVerificationStatus = emailVerificationStatus;
										
										
										setUserData(refresh);
									}
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
							}
							progressBarProfileUpdate.setVisibility(View.GONE);
						}
					});
		}
	}
	
	
	
	
	public void sendEmailVerifyLink(final Activity activity) {
		if(AppStatus.getInstance(activity).isOnline(activity)) {
			
			DialogPopup.showLoadingDialog(activity, "Updating...");
			
			RequestParams params = new RequestParams();
		
			params.put("client_id", Config.getClientId());
			params.put("access_token", Data.userData.accessToken);
			params.put("is_access_token_new", "1");
			
			AsyncHttpClient client = Data.getClient();
			client.post(Config.getServerUrl() + "/send_verify_email_link", params,
					new CustomAsyncHttpResponseHandler() {
					private JSONObject jObj;

						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
							DialogPopup.dismissLoadingDialog();
							DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}

						@Override
						public void onSuccess(String response) {
							Log.i("Server response", "response = " + response);
							DialogPopup.dismissLoadingDialog();
							try {
								jObj = new JSONObject(response);
								if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
									int flag = jObj.getInt("flag");
									if(ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag){
										String error = jObj.getString("error");
										DialogPopup.dialogBanner(activity, error);
									}
									else if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){
										String message = jObj.getString("message");
										DialogPopup.dialogBanner(activity, message);
									}
									else{
										DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
									}
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
								DialogPopup.dismissLoadingDialog();
							}
						}
					});
		}
		else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}
	}
	
	
	void logoutAsync(final Activity activity) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Please Wait ...");
			
			RequestParams params = new RequestParams();
			
			params.put("access_token", Data.userData.accessToken);
			params.put("is_access_token_new", "1");
			params.put("client_id", Config.getClientId());

			Log.i("access_token", "="+Data.userData.accessToken);
		
			AsyncHttpClient client = Data.getClient();
			client.post(Config.getServerUrl()+"/logout_user", params,
					new CustomAsyncHttpResponseHandler() {
					private JSONObject jObj;

						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
							DialogPopup.dismissLoadingDialog();
							DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}

						@Override
						public void onSuccess(String response) {
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
									int flag = jObj.getInt("flag");
									if(ApiResponseFlags.AUTH_LOGOUT_FAILURE.getOrdinal() == flag){
										String error = jObj.getString("error");
										DialogPopup.alertPopup(activity, "", error);
									}
									else if(ApiResponseFlags.AUTH_LOGOUT_SUCCESSFUL.getOrdinal() == flag){

										PicassoTools.clearCache(Picasso.with(activity));
										
										new FacebookLoginHelper().logoutFacebook();
										
										GCMIntentService.clearNotifications(activity);
										
										Data.clearDataOnLogout(activity);
										
										HomeActivity.userMode = UserMode.PASSENGER;
										HomeActivity.passengerScreenMode = PassengerScreenMode.P_INITIAL;
										
										ActivityCompat.finishAffinity(activity);
										Intent intent = new Intent(activity, SplashNewActivity.class);
										startActivity(intent);
										overridePendingTransition(R.anim.left_in, R.anim.left_out);
									}
									else{
										DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
									}
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
							DialogPopup.dismissLoadingDialog();
						}
						
					});
		}
		else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}
	}
	



}

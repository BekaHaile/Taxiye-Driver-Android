package product.clicklabs.jugnoo;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.EmailVerificationStatus;
import product.clicklabs.jugnoo.datastructure.FutureSchedule;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.datastructure.ProfileUpdateMode;
import product.clicklabs.jugnoo.datastructure.RideInfoNew;
import product.clicklabs.jugnoo.datastructure.ScheduleCancelListener;
import product.clicklabs.jugnoo.datastructure.UserMode;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FacebookLoginHelper;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.BlurTransform;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoTools;

public class AccountActivity extends Activity {

	RelativeLayout relative;
	
	TextView textViewTitle;
	ImageView imageViewBack, imageViewLogout;
	
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
	
	
	RelativeLayout relativeLayoutRideTransactions;
	TextView textViewRecentTransactions;
	ListView listViewRideTransactions;
	TextView textViewInfo;
	ProgressBar progressBarList;
	
	RideTransactionAdapter rideTransactionAdapter;
	
	RelativeLayout relativeLayoutSeeMore;
	TextView textViewSeeMore;
	
	RelativeLayout relativeLayoutNotTriedJugnoo;
	TextView textViewNotTriedJugnoo, textViewGetARide;
	
	Button buttonLogout;
	
	public static FutureSchedule futureSchedule = null;
	public static ArrayList<RideInfoNew> rideInfosList = new ArrayList<RideInfoNew>();
	public static int totalRides = 0;
	
	DecimalFormat decimalFormat = new DecimalFormat("#.#");
	DecimalFormat decimalFormatNoDec = new DecimalFormat("#");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_user);
		
		
		futureSchedule = null;
		rideInfosList = new ArrayList<RideInfoNew>();
		totalRides = 0;
		
		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(this, relative, 1134, 720, false);
		
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Data.latoRegular(this), Typeface.BOLD);
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
		
		imageViewUserImageBlur = (ImageView) findViewById(R.id.imageViewUserImageBlur);
		imageViewProfileImage = (ImageView) findViewById(R.id.imageViewProfileImage);
		
		progressBarProfileUpdate = (ProgressBar) findViewById(R.id.progressBarProfileUpdate);
		
		scrollView = (ScrollView) findViewById(R.id.scrollView);
		linearLayoutMain = (LinearLayout) findViewById(R.id.linearLayoutMain);
		textViewScroll = (TextView) findViewById(R.id.textViewScroll);
		
		editTextUserName = (EditText) findViewById(R.id.editTextUserName); editTextUserName.setTypeface(Data.latoRegular(this));
		editTextEmail = (EditText) findViewById(R.id.editTextEmail); editTextEmail.setTypeface(Data.latoRegular(this));
		editTextPhone = (EditText) findViewById(R.id.editTextPhone); editTextPhone.setTypeface(Data.latoRegular(this));
		
		imageViewEditName = (ImageView) findViewById(R.id.imageViewEditName);
		imageViewEditEmail = (ImageView) findViewById(R.id.imageViewEditEmail);
		imageViewEditPhoneNo = (ImageView) findViewById(R.id.imageViewEditPhoneNo);
		
		
		imageViewEmailVerifyStatus = (ImageView) findViewById(R.id.imageViewEmailVerifyStatus);
		relativeLayoutEmailVerify = (RelativeLayout) findViewById(R.id.relativeLayoutEmailVerify);
		textViewEmailVerifyMessage = (TextView) findViewById(R.id.textViewEmailVerifyMessage); textViewEmailVerifyMessage.setTypeface(Data.latoRegular(this));
		textViewEmailVerify = (TextView) findViewById(R.id.textViewEmailVerify); textViewEmailVerify.setTypeface(Data.latoRegular(this));
		
		relativeLayoutChangePassword = (RelativeLayout) findViewById(R.id.relativeLayoutChangePassword);
		textViewChangePassword = (TextView) findViewById(R.id.textViewChangePassword); textViewChangePassword.setTypeface(Data.latoRegular(this));
		
		relativeLayoutRideTransactions = (RelativeLayout) findViewById(R.id.relativeLayoutRideTransactions);
		textViewRecentTransactions = (TextView) findViewById(R.id.textViewRecentTransactions); textViewRecentTransactions.setTypeface(Data.latoRegular(this));
		listViewRideTransactions = (ListView) findViewById(R.id.listViewRideTransactions);
		textViewInfo = (TextView) findViewById(R.id.textViewInfo); textViewInfo.setTypeface(Data.latoRegular(this));
		progressBarList = (ProgressBar) findViewById(R.id.progressBarList);
		
		LinearLayout viewF = (LinearLayout) getLayoutInflater().inflate(R.layout.list_item_see_more, null);
		listViewRideTransactions.addFooterView(viewF);
		viewF.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
		ASSL.DoMagic(viewF);
		
		relativeLayoutSeeMore = (RelativeLayout) viewF.findViewById(R.id.relativeLayoutSeeMore);
		textViewSeeMore = (TextView) viewF.findViewById(R.id.textViewSeeMore); textViewSeeMore.setTypeface(Data.latoLight(this), Typeface.BOLD);
		relativeLayoutSeeMore.setVisibility(View.GONE);
		
		rideTransactionAdapter = new RideTransactionAdapter(this);
		listViewRideTransactions.setAdapter(rideTransactionAdapter);
		
		relativeLayoutNotTriedJugnoo = (RelativeLayout) findViewById(R.id.relativeLayoutNotTriedJugnoo);
		textViewNotTriedJugnoo = (TextView) findViewById(R.id.textViewNotTriedJugnoo); textViewNotTriedJugnoo.setTypeface(Data.latoRegular(this));
		textViewGetARide = (TextView) findViewById(R.id.textViewGetARide); textViewGetARide.setTypeface(Data.latoRegular(this));
		

		relativeLayoutRideTransactions.setVisibility(View.VISIBLE);
		relativeLayoutNotTriedJugnoo.setVisibility(View.GONE);
		textViewRecentTransactions.setVisibility(View.GONE);
		
		
		buttonLogout = (Button) findViewById(R.id.buttonLogout); buttonLogout.setTypeface(Data.latoRegular(this));
		
		
		
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
		
		
		relativeLayoutNotTriedJugnoo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				performBackPressed();
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
		
		
		textViewInfo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getRecentRidesAPI(AccountActivity.this);
			}
		});
		
		
		relativeLayoutSeeMore.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AccountActivity.this, RideTransactionsActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
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
		
		getRecentRidesAPI(this);
		
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
		
			params.put("client_id", Data.CLIENT_ID);
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
			client.post(Data.SERVER_URL + "/update_user_profile", params,
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
		
			params.put("client_id", Data.CLIENT_ID);
			params.put("access_token", Data.userData.accessToken);
			params.put("is_access_token_new", "1");
			
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/reload_my_profile", params,
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
		
			params.put("client_id", Data.CLIENT_ID);
			params.put("access_token", Data.userData.accessToken);
			params.put("is_access_token_new", "1");
			
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/send_verify_email_link", params,
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
			params.put("client_id", Data.CLIENT_ID);

			Log.i("access_token", "="+Data.userData.accessToken);
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL+"/logout_user", params,
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
	
	
	public void getRecentRidesAPI(final Activity activity) {
		progressBarList.setVisibility(View.GONE);
		if(AppStatus.getInstance(activity).isOnline(activity)) {
			
			rideInfosList.clear();
			futureSchedule = null;
			
			progressBarList.setVisibility(View.VISIBLE);
			textViewInfo.setVisibility(View.GONE);
			
			RequestParams params = new RequestParams();
		
			params.put("access_token", Data.userData.accessToken);
			params.put("start_from", "0");
			
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/get_recent_rides", params,
					new CustomAsyncHttpResponseHandler() {
					private JSONObject jObj;

						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
							updateListData("Some error occurred, tap to retry", true);
							progressBarList.setVisibility(View.GONE);
						}

						@Override
						public void onSuccess(String response) {
							Log.i("Server response", "response = " + response);
							try {
								
//								{
//								    "flag": 173,
//								    "rides": [
//								        {
//								            "pickup_address": "1097, Madhya Marg, 28B, Sector 28, Chandigarh 160101, India",
//								            "drop_address": "1097, Madhya Marg, 28B, Sector 28, Chandigarh 160101, India",
//								            "distance": 0,
//								            "ride_time": 1,
//								            "date": "March 5th 2015",
//								            "amount": 26
//								        }
//								    ],
//								    "schedule": {
//								        "pickup_id": 117,
//								        "latitude": 30.7191,
//								        "longitude": 76.8104,
//								        "address": "1097, Madhya Marg, 28B, Sector 28, Chandigarh 160101, India",
//								        "pickup_date": "March 7th, 2015",
//								        "pickup_time": "04:43 PM",
//								        "difference": 64,
//								        "status": 0,
//								        "modifiable": 1,
//								    }
//								}
								
								jObj = new JSONObject(response);
								if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
									int flag = jObj.getInt("flag");
									if(ApiResponseFlags.RECENT_RIDES.getOrdinal() == flag){
										
										totalRides = jObj.getInt("num_rides");
										
										if(jObj.has("schedule")){
											JSONObject jSchedule = jObj.getJSONObject("schedule");
											futureSchedule = new FutureSchedule(jSchedule.getString("pickup_id"), 
													jSchedule.getString("address"), 
													jSchedule.getString("pickup_date"), 
													jSchedule.getString("pickup_time"), 
													new LatLng(jSchedule.getDouble("latitude"), jSchedule.getDouble("longitude")), 
													jSchedule.getInt("modifiable"),
													jSchedule.getInt("status"));
											totalRides = totalRides + 1;
										}
										else{
											futureSchedule = null;
										}
										
										
										rideInfosList.clear();
										
										if(jObj.has("rides")){
											JSONArray jRidesArr = jObj.getJSONArray("rides");
											for(int i=0; i<jRidesArr.length(); i++){
												JSONObject jRide = jRidesArr.getJSONObject(i);
												rideInfosList.add(new RideInfoNew(jRide.getString("pickup_address"), 
														jRide.getString("drop_address"), 
														jRide.getDouble("amount"), 
														jRide.getDouble("distance"), 
														jRide.getDouble("ride_time"), 
														jRide.getString("date")));
											}
										}
										
										Log.e("totalRides", "="+totalRides);
										Log.e("rideInfosList", "="+rideInfosList.size());
										Log.e("futureSchedule", "="+futureSchedule);
										
										
										
										updateListData("No rides currently", false);

									}
									else{
										updateListData("Some error occurred, tap to retry", true);
									}
								}
								else{
									updateListData("Some error occurred, tap to retry", true);
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								updateListData("Some error occurred, tap to retry", true);
							}
							progressBarList.setVisibility(View.GONE);
						}
					});
		}
		else {
			updateListData("No internet connection, tap to retry", true);
		}
	}
	
	public void updateListData(String message, boolean errorOccurred){
		
		if(errorOccurred){
			relativeLayoutNotTriedJugnoo.setVisibility(View.GONE);
			relativeLayoutRideTransactions.setVisibility(View.VISIBLE);
			textViewRecentTransactions.setVisibility(View.GONE);
			
			textViewInfo.setText(message);
			textViewInfo.setVisibility(View.VISIBLE);
			
			rideInfosList.clear();
			rideTransactionAdapter.notifyDataSetChanged();
			relativeLayoutSeeMore.setVisibility(View.GONE);
		}
		else{
			textViewInfo.setVisibility(View.GONE);
			if(rideInfosList.size() == 0 && futureSchedule == null){
				relativeLayoutNotTriedJugnoo.setVisibility(View.VISIBLE);
				relativeLayoutRideTransactions.setVisibility(View.GONE);
				relativeLayoutSeeMore.setVisibility(View.GONE);
			}
			else{
				relativeLayoutNotTriedJugnoo.setVisibility(View.GONE);
				relativeLayoutRideTransactions.setVisibility(View.VISIBLE);
				textViewRecentTransactions.setVisibility(View.VISIBLE);
				relativeLayoutSeeMore.setVisibility(View.VISIBLE);
			}
			rideTransactionAdapter.notifyDataSetChanged();
		}
		Utils.expandListForVariableHeight(listViewRideTransactions);
	}
	
	class ViewHolderRideTransaction {
		TextView textViewPickupAt, textViewFrom, textViewFromValue, textViewTo, 
		textViewToValue, textViewDetails, textViewDetailsValue, textViewAmount, textViewCancel;
		RelativeLayout relativeLayoutCancel, relativeLayoutTo;
		RelativeLayout relative;
		int id;
	}

	class RideTransactionAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderRideTransaction holder;
		Context context;
		public RideTransactionAdapter(Context context) {
			this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.context = context;
		}

		@Override
		public int getCount() {
			if(futureSchedule == null){
				if(rideInfosList.size() > 2){
					return 2;
				}
				else{
					return rideInfosList.size();
				}
			}
			else{
				if(rideInfosList.size() > 1){
					return 2;
				}
				else{
					return 1;
				}
			}
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				holder = new ViewHolderRideTransaction();
				convertView = mInflater.inflate(R.layout.list_item_ride_transaction, null);
				
				holder.textViewPickupAt = (TextView) convertView.findViewById(R.id.textViewPickupAt); holder.textViewPickupAt.setTypeface(Data.latoRegular(context));
				holder.textViewFrom = (TextView) convertView.findViewById(R.id.textViewFrom); holder.textViewFrom.setTypeface(Data.latoRegular(context));
				holder.textViewFromValue = (TextView) convertView.findViewById(R.id.textViewFromValue); holder.textViewFromValue.setTypeface(Data.latoRegular(context));
				holder.textViewTo = (TextView) convertView.findViewById(R.id.textViewTo); holder.textViewTo.setTypeface(Data.latoRegular(context));
				holder.textViewToValue = (TextView) convertView.findViewById(R.id.textViewToValue); holder.textViewToValue.setTypeface(Data.latoRegular(context));
				holder.textViewDetails = (TextView) convertView.findViewById(R.id.textViewDetails); holder.textViewDetails.setTypeface(Data.latoRegular(context));
				holder.textViewDetailsValue = (TextView) convertView.findViewById(R.id.textViewDetailsValue); holder.textViewDetailsValue.setTypeface(Data.latoRegular(context));
				holder.textViewAmount = (TextView) convertView.findViewById(R.id.textViewAmount); holder.textViewAmount.setTypeface(Data.latoRegular(context));
				holder.textViewCancel = (TextView) convertView.findViewById(R.id.textViewCancel); holder.textViewCancel.setTypeface(Data.latoRegular(context));
				
				holder.relative = (RelativeLayout) convertView.findViewById(R.id.relative);
				holder.relativeLayoutCancel = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutCancel);
				holder.relativeLayoutTo = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutTo);
				
				holder.relative.setTag(holder);
				holder.relativeLayoutCancel.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderRideTransaction) convertView.getTag();
			}
			
			
			holder.id = position;
			
			if(futureSchedule != null){
				if(position == 0){
					holder.textViewPickupAt.setVisibility(View.VISIBLE);
					holder.relativeLayoutTo.setVisibility(View.GONE);
					holder.textViewAmount.setVisibility(View.GONE);
						
					holder.textViewFromValue.setText(futureSchedule.pickupAddress);
					holder.textViewDetails.setText("Date: ");
					holder.textViewDetailsValue.setText(futureSchedule.pickupDate + ", " + futureSchedule.pickupTime);
						
					if(futureSchedule.modifiable == 1){
						holder.relativeLayoutCancel.setVisibility(View.VISIBLE);
					}
					else{
						holder.relativeLayoutCancel.setVisibility(View.GONE);
					}
				}
				else{
					RideInfoNew rideInfoNew = rideInfosList.get(position-1);
					
					holder.textViewPickupAt.setVisibility(View.GONE);
					holder.relativeLayoutTo.setVisibility(View.VISIBLE);
					holder.textViewAmount.setVisibility(View.VISIBLE);
					holder.relativeLayoutCancel.setVisibility(View.GONE);
					
					holder.textViewFromValue.setText(rideInfoNew.pickupAddress);
					holder.textViewToValue.setText(rideInfoNew.dropAddress);
					holder.textViewDetails.setText("Details: ");
					if(rideInfoNew.rideTime == 1){
						holder.textViewDetailsValue.setText(decimalFormat.format(rideInfoNew.distance) + " km, " 
								+ decimalFormatNoDec.format(rideInfoNew.rideTime) + " minute, "+rideInfoNew.date);
					}
					else{
						holder.textViewDetailsValue.setText(decimalFormat.format(rideInfoNew.distance) + " km, " 
								+ decimalFormatNoDec.format(rideInfoNew.rideTime) + " minutes, "+rideInfoNew.date);
					}
					holder.textViewAmount.setText(getResources().getString(R.string.rupee)+" "+decimalFormatNoDec.format(rideInfoNew.amount));
				}
			}
			else{
				RideInfoNew rideInfoNew = rideInfosList.get(position);
				
				holder.textViewPickupAt.setVisibility(View.GONE);
				holder.relativeLayoutTo.setVisibility(View.VISIBLE);
				holder.textViewAmount.setVisibility(View.VISIBLE);
				holder.relativeLayoutCancel.setVisibility(View.GONE);
				
				holder.textViewFromValue.setText(rideInfoNew.pickupAddress);
				holder.textViewToValue.setText(rideInfoNew.dropAddress);
				holder.textViewDetails.setText("Details: ");
				if(rideInfoNew.rideTime == 1){
					holder.textViewDetailsValue.setText(decimalFormat.format(rideInfoNew.distance) + " km, " 
							+ decimalFormatNoDec.format(rideInfoNew.rideTime) + " minute, "+rideInfoNew.date);
				}
				else{
					holder.textViewDetailsValue.setText(decimalFormat.format(rideInfoNew.distance) + " km, " 
							+ decimalFormatNoDec.format(rideInfoNew.rideTime) + " minutes, "+rideInfoNew.date);
				}
				holder.textViewAmount.setText(getResources().getString(R.string.rupee)+" "+decimalFormatNoDec.format(rideInfoNew.amount));
			}
			
			holder.relativeLayoutCancel.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(futureSchedule != null){
						DialogPopup.alertPopupTwoButtonsWithListeners(AccountActivity.this, "Cancel Scheduled Ride", "Are you sure you want to cancel the scheduled ride?", "OK", "Cancel",
								new View.OnClickListener() {
									
									@Override
									public void onClick(View v) {
										if(futureSchedule != null){
											removeScheduledRideAPI(AccountActivity.this, futureSchedule.pickupId, new ScheduleCancelListener() {
												
												@Override
												public void onCancelSuccess() {
													getRecentRidesAPI(AccountActivity.this);
												}
											});
										}
									}
								}, 
								new View.OnClickListener() {
									
									@Override
									public void onClick(View v) {
									}
								}, true, true);
					}
					
				}
			});
			
			
			return convertView;
		}

		
		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
			if(totalRides > getCount()){
				relativeLayoutSeeMore.setVisibility(View.VISIBLE);
			}
			else{
				relativeLayoutSeeMore.setVisibility(View.GONE);
			}
		}
		
	}
	
	
	
	/**
	 * ASync for removing scheduled ride from server
	 */
	public static void removeScheduledRideAPI(final Activity activity, String pickupId, final ScheduleCancelListener scheduleCancelListener) {
		if (AppStatus.getInstance(activity).isOnline(activity)) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
		
			params.put("access_token", Data.userData.accessToken);
			params.put("pickup_id", pickupId);
			
			Log.i("remove_pickup_schedule api params", ">"+params);
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/remove_pickup_schedule", params,
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
	
							try {
								jObj = new JSONObject(response);

								if(!jObj.isNull("error")){
									String errorMessage = jObj.getString("error");
									int flag = jObj.getInt("flag");
									if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
										HomeActivity.logoutUser(activity);
									}
									else if(ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag){
										DialogPopup.alertPopup(activity, "", errorMessage);
									}
									else{
										DialogPopup.alertPopup(activity, "", errorMessage);
									}
									DialogPopup.dismissLoadingDialog();
								}
								else{
									int flag = jObj.getInt("flag");
									if(ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag){
										String message = jObj.getString("message");
										DialogPopup.alertPopup(activity, "", message);
										scheduleCancelListener.onCancelSuccess();
									}
									DialogPopup.dismissLoadingDialog();
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
	

}

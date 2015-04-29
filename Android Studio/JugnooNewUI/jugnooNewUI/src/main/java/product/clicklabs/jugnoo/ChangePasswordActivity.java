package product.clicklabs.jugnoo;

import org.json.JSONObject;

import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Log;
import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.flurry.android.FlurryAgent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

public class ChangePasswordActivity extends Activity {

	LinearLayout relative;
	
	TextView textViewTitle;
	ImageView imageViewBack;
	
	EditText editTextOldPassword, editTextNewPassword, editTextRetypeNewPassword;
	
	Button buttonChangePassword;
	
	ScrollView scrollView;
	
	TextView textViewScroll;
	
	// *****************************Used for flurry work***************//
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.init(this, Data.FLURRY_KEY);
		FlurryAgent.onStartSession(this, Data.FLURRY_KEY);
		FlurryAgent.onEvent("Login started");
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(ChangePasswordActivity.this, relative, 1134, 720, false);
		
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Data.latoRegular(this), Typeface.BOLD);
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
		
		editTextOldPassword = (EditText) findViewById(R.id.editTextOldPassword); editTextOldPassword.setTypeface(Data.latoRegular(this));
		editTextNewPassword = (EditText) findViewById(R.id.editTextNewPassword); editTextNewPassword.setTypeface(Data.latoRegular(this));
		editTextRetypeNewPassword = (EditText) findViewById(R.id.editTextRetypeNewPassword); editTextRetypeNewPassword.setTypeface(Data.latoRegular(this));

		buttonChangePassword = (Button) findViewById(R.id.buttonChangePassword); buttonChangePassword.setTypeface(Data.latoRegular(this));

		scrollView = (ScrollView) findViewById(R.id.scrollView);
		textViewScroll = (TextView) findViewById(R.id.textViewScroll);
		
		
		
		
		buttonChangePassword.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String oldPassword = editTextOldPassword.getText().toString().trim();
				String newPassword = editTextNewPassword.getText().toString().trim();
				String retypeNewPassword = editTextRetypeNewPassword.getText().toString().trim();
				
				if("".equalsIgnoreCase(oldPassword)){
					editTextOldPassword.requestFocus();
					editTextOldPassword.setError("Please enter old password");
				}
				else{
					if("".equalsIgnoreCase(newPassword)){
						editTextNewPassword.requestFocus();
						editTextNewPassword.setError("Please enter new password");
					}
					else{
						if("".equalsIgnoreCase(retypeNewPassword)){
							editTextRetypeNewPassword.requestFocus();
							editTextRetypeNewPassword.setError("Please retype new password");
						}
						else{
							if(!newPassword.equalsIgnoreCase(retypeNewPassword)){
								editTextNewPassword.requestFocus();
								editTextNewPassword.setError("Passwords not matched");
							}
							else{
								if(newPassword.equalsIgnoreCase(oldPassword)){
									editTextNewPassword.requestFocus();
									editTextNewPassword.setError("New Password is same as the old one");
								}
								else{
									if(newPassword.length() >= 6){
										updateUserProfileChangePasswordAPI(ChangePasswordActivity.this, oldPassword, newPassword);
									}
									else{
										editTextNewPassword.requestFocus();
										editTextNewPassword.setError("New Password must be of atleast 6 characters");
									}
								}
							}
						}
					}
				}
			}
		});
		
		
		
		imageViewBack.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});
		
		
		editTextOldPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					scrollView.smoothScrollTo(0, editTextOldPassword.getBottom());
				}
				editTextOldPassword.setError(null);
			}
		});
		
		editTextNewPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					scrollView.smoothScrollTo(0, editTextNewPassword.getBottom());
				}
				editTextNewPassword.setError(null);
			}
		});
		
		editTextRetypeNewPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus){
					scrollView.smoothScrollTo(0, editTextRetypeNewPassword.getBottom());
				}
				editTextRetypeNewPassword.setError(null);
			}
		});
		
		editTextRetypeNewPassword.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				int result = actionId & EditorInfo.IME_MASK_ACTION;
				switch (result) {
					case EditorInfo.IME_ACTION_DONE:
						buttonChangePassword.performClick();
					break;

					case EditorInfo.IME_ACTION_NEXT:
					break;

					default:
				}
				return true;
			}
		});
		
		
		
		
		final View activityRootView = findViewById(R.id.mainLinear);
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
		
		
	}
	
	
	
	@Override
	protected void onResume() {
		super.onResume();
		HomeActivity.checkForAccessTokenChange(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	
	@Override
	public void onBackPressed() {
		performBackPressed();
	}
	
	
	public void performBackPressed(){
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}

	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}
	
	
	
	public void updateUserProfileChangePasswordAPI(final Activity activity, final String oldPassword, final String newPassword) {
		if(AppStatus.getInstance(activity).isOnline(activity)) {
			
			DialogPopup.showLoadingDialog(activity, "Updating...");
			
			RequestParams params = new RequestParams();
		
			params.put("client_id", Data.CLIENT_ID);
			params.put("access_token", Data.userData.accessToken);
			params.put("is_access_token_new", "1");
			params.put("old_password", oldPassword);
			params.put("new_password", newPassword);
			
			
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
										DialogPopup.alertPopup(activity, "", error);
									}
									else if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){
										String message = jObj.getString("message");
										DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
											
											@Override
											public void onClick(View v) {
												performBackPressed();
											}
										});
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
	
}

package product.clicklabs.jugnoo;

import org.json.JSONObject;

import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.Log;
import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.flurry.android.FlurryAgent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

public class ForgotPasswordScreen extends Activity{
	
	TextView title;
	Button backBtn;
	
	EditText emailEt;
	Button sendEmailBtn;
	TextView extraTextForScroll, forgotPasswordHelpText;
	
	LinearLayout relative;
	
	static String emailAlready = "";
	
	// *****************************Used for flurry work***************//
	@Override
	protected void onStart() {
		super.onStart();
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
		HomeActivity.checkForAccessTokenChange(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgot_password_screen);
		
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(ForgotPasswordScreen.this, relative, 1134, 720, false);
		
		
		title = (TextView) findViewById(R.id.title); title.setTypeface(Data.latoRegular(getApplicationContext()));
		backBtn = (Button) findViewById(R.id.backBtn); backBtn.setTypeface(Data.latoRegular(getApplicationContext()));
		
		forgotPasswordHelpText = (TextView) findViewById(R.id.forgotPasswordHelpText); forgotPasswordHelpText.setTypeface(Data.latoRegular(getApplicationContext()));
		
		emailEt = (EditText) findViewById(R.id.emailEt); emailEt.setTypeface(Data.latoRegular(getApplicationContext()));
		
		sendEmailBtn = (Button) findViewById(R.id.sendEmailBtn); sendEmailBtn.setTypeface(Data.latoRegular(getApplicationContext()));
		
		extraTextForScroll = (TextView) findViewById(R.id.extraTextForScroll);

		
		backBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(ForgotPasswordScreen.this, SplashLogin.class));
				overridePendingTransition(R.anim.left_in, R.anim.left_out);
				finish();
			}
		});
		

		emailEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				emailEt.setError(null);
			}
		});
		
		
		
		sendEmailBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				String email = emailEt.getText().toString().trim();
				
				if("".equalsIgnoreCase(email)){
					emailEt.requestFocus();
					emailEt.setError("Please enter email");
				}
				else{
					if(isEmailValid(email)){
						forgotPasswordAsync(ForgotPasswordScreen.this, email);
						FlurryEventLogger.forgotPasswordClicked(email);
					}
					else{
						emailEt.requestFocus();
						emailEt.setError("Please enter valid email");
					}
				}
				
			}
		});
		
		
		emailEt.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				int result = actionId & EditorInfo.IME_MASK_ACTION;
				switch (result) {
					case EditorInfo.IME_ACTION_DONE:
						sendEmailBtn.performClick();
					break;

					case EditorInfo.IME_ACTION_NEXT:
					break;

					default:
				}
				return true;
			}
		});
		
		emailEt.setText(emailAlready);
		emailEt.setSelection(emailEt.getText().toString().length());
		
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

							ViewGroup.LayoutParams params_12 = extraTextForScroll
									.getLayoutParams();

							params_12.height = (int)(heightDiff);

							extraTextForScroll.setLayoutParams(params_12);
							extraTextForScroll.requestLayout();

						} else {

							ViewGroup.LayoutParams params = extraTextForScroll
									.getLayoutParams();
							params.height = 0;
							extraTextForScroll.setLayoutParams(params);
							extraTextForScroll.requestLayout();

						}
					}
				});
		
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		
	}

	
	
	/**
	 * ASync for register from server
	 */
	public void forgotPasswordAsync(final Activity activity, final String email) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
		
			params.put("email", email);

			Log.i("email", "=" + email);
		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/forgot_password", params,
					new CustomAsyncHttpResponseHandler() {
					private JSONObject jObj;

					@Override
					public void onSuccess(String response) {
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								
								if(!jObj.isNull("error")){
									
									String errorMessage = jObj.getString("error");
									if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
										HomeActivity.logoutUser(activity);
									}
									else{
										new DialogPopup().alertPopup(activity, "", errorMessage);
									}
									
								}
								else{
									new DialogPopup().alertPopup(activity, "", jObj.getString("log"));
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								new DialogPopup().alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
	
							DialogPopup.dismissLoadingDialog();
						}
						

						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
							DialogPopup.dismissLoadingDialog();
							new DialogPopup().alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}
						
					});
		}
		else {
			new DialogPopup().alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}
	
	
	
	boolean isEmailValid(CharSequence email) {
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}
	
	
	@Override
	public void onBackPressed() {
		startActivity(new Intent(ForgotPasswordScreen.this, SplashLogin.class));
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
		finish();
		super.onBackPressed();
	}
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
        
        ASSL.closeActivity(relative);
        
        System.gc();
	}
	
}

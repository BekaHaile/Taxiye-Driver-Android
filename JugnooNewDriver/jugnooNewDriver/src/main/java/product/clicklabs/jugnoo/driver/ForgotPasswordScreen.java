package product.clicklabs.jugnoo.driver;

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

import org.json.JSONObject;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.Log;
import rmn.androidscreenlibrary.ASSL;

public class ForgotPasswordScreen extends Activity{
	
	TextView title;
	Button backBtn;
	
	EditText emailEt;
	Button sendEmailBtn;
	TextView extraTextForScroll, forgotPasswordHelpText;
	
	LinearLayout relative;

	String emailAlready = "";
    boolean fromPreviousAccounts = false;

	// *****************************Used for flurry work***************//
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
		setContentView(R.layout.activity_forgot_password);
		
		
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
                performBackPressed();
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


        try {
            if(getIntent().hasExtra("forgotEmail")){
                emailAlready = getIntent().getStringExtra("forgotEmail");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if(getIntent().hasExtra("fromPreviousAccounts")){
                fromPreviousAccounts = getIntent().getBooleanExtra("fromPreviousAccounts", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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
			client.post(Data.SERVER_URL + "/forgot_password_driver", params,
					new CustomAsyncHttpResponseHandler() {
					private JSONObject jObj;

					@Override
					public void onSuccess(String response) {
							Log.v("Server response", "response = " + response);
	
							try {
								jObj = new JSONObject(response);
								int flag = jObj.getInt("flag");
                                String message = JSONParser.getServerMessage(jObj);
								if(ApiResponseFlags.INVALID_ACCESS_TOKEN.getOrdinal() == flag){
									HomeActivity.logoutUser(activity);
								}
								else if(ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag){
									DialogPopup.alertPopup(activity, "", message);
								}
								else if(ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag){
									DialogPopup.alertPopup(activity, "", message);
								}
								else if(ApiResponseFlags.NO_SUCH_USER.getOrdinal() == flag){
									DialogPopup.alertPopup(activity, "", message);
								}
								else if(ApiResponseFlags.CUSTOMER_LOGGING_IN.getOrdinal() == flag){
									SplashNewActivity.sendToCustomerAppPopup("Alert", message, activity);
								}
								else if(ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag){
									DialogPopup.alertPopup(activity, "", message);
								}
								else if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){
									DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {

										@Override
										public void onClick(View v) {
                                            performBackPressed();
										}
									});
								}
								else{
									DialogPopup.alertPopup(activity, "", message);
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
							DialogPopup.dismissLoadingDialog();
						}

						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
							DialogPopup.dismissLoadingDialog();
							DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}
						
					});
		}
		else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}
	
	
	
	boolean isEmailValid(CharSequence email) {
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}
	
	
	@Override
	public void onBackPressed() {
        performBackPressed();
	}


    public void performBackPressed(){
        Intent intent = new Intent(ForgotPasswordScreen.this, SplashLogin.class);
        if(fromPreviousAccounts){
            intent.putExtra("previous_login_email", emailEt.getText().toString().trim());
        }
        else{
            intent.putExtra("forgot_login_email", emailEt.getText().toString().trim());
        }
        startActivity(intent);
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
        finish();
    }
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
        
        ASSL.closeActivity(relative);
        
        System.gc();
	}
	
}

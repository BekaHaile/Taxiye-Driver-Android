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

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.BookingHistoryResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class ForgotPasswordScreen extends Activity implements FlurryEventNames{
	
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
		
		
		title = (TextView) findViewById(R.id.title); title.setTypeface(Fonts.mavenRegular(getApplicationContext()));
		backBtn = (Button) findViewById(R.id.backBtn); backBtn.setTypeface(Fonts.mavenRegular(getApplicationContext()));
		
		forgotPasswordHelpText = (TextView) findViewById(R.id.forgotPasswordHelpText); forgotPasswordHelpText.setTypeface(Fonts.mavenRegular(getApplicationContext()));
		
		emailEt = (EditText) findViewById(R.id.emailEt); emailEt.setTypeface(Fonts.mavenRegular(getApplicationContext()));
		
		sendEmailBtn = (Button) findViewById(R.id.sendEmailBtn); sendEmailBtn.setTypeface(Fonts.mavenRegular(getApplicationContext()));
		
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
				
				String phoneNumber = emailEt.getText().toString().trim();
				
				if("".equalsIgnoreCase(phoneNumber)){
					emailEt.requestFocus();
					emailEt.setError(getResources().getString(R.string.enter_valid_phone_number));

				}
				else{
					String phone = Utils.retrievePhoneNumberTenChars(Utils.getCountryCode(ForgotPasswordScreen.this), phoneNumber);
					if(Utils.validPhoneNumber(phone)){
						forgotPasswordAsync(ForgotPasswordScreen.this, Utils.getCountryCode(ForgotPasswordScreen.this)+phone);
						FlurryEventLogger.event(CHANGE_PASSWORD_ENTER_EMAIL);
						FlurryEventLogger.event(CHANGE_PASSWORD);
					}
					else{
						emailEt.requestFocus();
						emailEt.setError(getResources().getString(R.string.enter_valid_phone_number));

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

//
//        try {
//            if(getIntent().hasExtra("forgotEmail")){
//                emailAlready = getIntent().getStringExtra("forgotEmail");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            if(getIntent().hasExtra("fromPreviousAccounts")){
//                fromPreviousAccounts = getIntent().getBooleanExtra("fromPreviousAccounts", false);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//		emailEt.setText(emailAlready);
//		emailEt.setSelection(emailEt.getText().toString().length());
		
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

//	Retrofit



	public void forgotPasswordAsync(final Activity activity, final String email){
		DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));
		HashMap<String, String> params = new HashMap<>();
		params.put("phone_number", email);
		HomeUtil.putDefaultParams(params);
		RestClient.getApiServices().forgotpassword(params, new Callback<BookingHistoryResponse>() {
			@Override
			public void success(BookingHistoryResponse bookingHistoryResponse, Response response) {
				if(response != null) {

					String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
					JSONObject jObj;
					try {
						jObj = new JSONObject(jsonString);
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
				}
				DialogPopup.dismissLoadingDialog();
			}

			@Override
			public void failure(RetrofitError error) {
				DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
				DialogPopup.dismissLoadingDialog();
			}
		});
	}
	
	
	
//	boolean isEmailValid(CharSequence phone_number) {
//		return Patterns.PHONE.matcher(phone_number).matches();
//	}
	
	
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

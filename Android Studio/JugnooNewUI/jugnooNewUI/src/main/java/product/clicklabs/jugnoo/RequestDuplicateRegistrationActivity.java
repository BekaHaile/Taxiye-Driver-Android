package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Log;
import rmn.androidscreenlibrary.ASSL;

public class RequestDuplicateRegistrationActivity extends Activity {

	RelativeLayout relative;

	TextView textViewTitle;
	ImageView imageViewBack;

    TextView textViewRegisterNameValue, textViewRegisterEmailValue, textViewRegisterPhoneValue, textViewRegisterHelp;
    EditText editTextMessage;
    Button buttonSubmitRequest;

	ScrollView scrollView;
	TextView textViewScroll;


	@Override
	protected void onResume() {
		super.onResume();
		HomeActivity.checkForAccessTokenChange(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_request_duplicate_registration);

		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(this, relative, 1134, 720, false);

		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Data.latoRegular(this), Typeface.BOLD);
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);

        textViewRegisterNameValue = (TextView) findViewById(R.id.textViewRegisterNameValue); textViewRegisterNameValue.setTypeface(Data.latoLight(this), Typeface.BOLD);
        textViewRegisterEmailValue = (TextView) findViewById(R.id.textViewRegisterEmailValue); textViewRegisterEmailValue.setTypeface(Data.latoLight(this), Typeface.BOLD);
        textViewRegisterPhoneValue = (TextView) findViewById(R.id.textViewRegisterPhoneValue); textViewRegisterPhoneValue.setTypeface(Data.latoLight(this), Typeface.BOLD);
        textViewRegisterHelp = (TextView) findViewById(R.id.textViewRegisterHelp); textViewRegisterHelp.setTypeface(Data.latoLight(this), Typeface.BOLD);

        ((TextView) findViewById(R.id.textViewRegistration)).setTypeface(Data.latoRegular(this), Typeface.BOLD);
        ((TextView) findViewById(R.id.textViewRegisterName)).setTypeface(Data.latoRegular(this));
        ((TextView) findViewById(R.id.textViewRegisterEmail)).setTypeface(Data.latoRegular(this));
        ((TextView) findViewById(R.id.textViewRegisterPhone)).setTypeface(Data.latoRegular(this));

        editTextMessage = (EditText) findViewById(R.id.editTextMessage); editTextMessage.setTypeface(Data.latoLight(this), Typeface.BOLD);

        buttonSubmitRequest = (Button) findViewById(R.id.buttonSubmitRequest); buttonSubmitRequest.setTypeface(Data.latoRegular(this));

		scrollView = (ScrollView) findViewById(R.id.scrollView);
		textViewScroll = (TextView) findViewById(R.id.textViewScroll);


		imageViewBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});


        buttonSubmitRequest.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String messageStr = editTextMessage.getText().toString().trim();
                String name = "";
                String email = "";
                String phone = "";

                if(RegisterScreen.facebookLogin) {
                    name = OTPConfirmScreen.facebookRegisterData.fbName;
                    email = OTPConfirmScreen.facebookRegisterData.fbUserEmail;
                    phone = OTPConfirmScreen.facebookRegisterData.phoneNo;
                }
                else{
                    name = OTPConfirmScreen.emailRegisterData.name;
                    email = OTPConfirmScreen.emailRegisterData.emailId;
                    phone = OTPConfirmScreen.emailRegisterData.phoneNo;
                }

                submitDuplicateRegistrationRequestAPI(RequestDuplicateRegistrationActivity.this, messageStr, name, email, phone);

			}
		});


        try{
            if(RegisterScreen.facebookLogin) {
                textViewRegisterNameValue.setText(OTPConfirmScreen.facebookRegisterData.fbName);
                textViewRegisterEmailValue.setText(OTPConfirmScreen.facebookRegisterData.fbUserEmail);
                textViewRegisterPhoneValue.setText(OTPConfirmScreen.facebookRegisterData.phoneNo);
            }
            else{
                textViewRegisterNameValue.setText(OTPConfirmScreen.emailRegisterData.name);
                textViewRegisterEmailValue.setText(OTPConfirmScreen.emailRegisterData.emailId);
                textViewRegisterPhoneValue.setText(OTPConfirmScreen.emailRegisterData.phoneNo);
            }
            editTextMessage.setHint("You have already created "+Data.previousAccountInfoList.size()+" accounts from this device. Please explain the reason for creating a new account.");
        } catch(Exception e){
            e.printStackTrace();
            performBackPressed();
        }

		
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

	
	public void performBackPressed(){
        startActivity(new Intent(RequestDuplicateRegistrationActivity.this, MultipleAccountsActivity.class));
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}
	
	@Override
	public void onBackPressed() {
		performBackPressed();
	}
	
	
	@Override
	protected void onDestroy() {
        ASSL.closeActivity(relative);
        System.gc();
		super.onDestroy();
	}
	
	
	
	
	public void submitDuplicateRegistrationRequestAPI(final Activity activity, String messageStr, String name, String email, String phone) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();

            params.put("user_name", name);
            params.put("user_email", email);
            params.put("phone_no", phone);
			params.put("user_message", ""+messageStr);

            try {
                if (RegisterScreen.multipleCaseJSON != null) {
                    params.put("users", RegisterScreen.multipleCaseJSON.getJSONArray("users"));
                }
            } catch(Exception e){
                e.printStackTrace();
            }

			Log.i("params request_dup_registration", "=" + params);

		
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/request_dup_registration", params,
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
							Log.i("Server response request_dup_registration", "response = " + response);
							try {
								jObj = new JSONObject(response);
								int flag = jObj.getInt("flag");
								if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
                                    if(ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag){
                                        String error = jObj.getString("error");
                                        DialogPopup.alertPopup(activity, "", error);
                                    }
									else if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){
                                        String message = jObj.getString("message");
                                        DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener(){
                                            @Override
                                            public void onClick(View v) {
                                                activity.startActivity(new Intent(activity, SplashNewActivity.class));
                                                activity.finish();
                                                activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
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

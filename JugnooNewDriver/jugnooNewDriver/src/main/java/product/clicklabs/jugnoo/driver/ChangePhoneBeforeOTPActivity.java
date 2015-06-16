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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Utils;
import rmn.androidscreenlibrary.ASSL;

public class ChangePhoneBeforeOTPActivity extends Activity{
	
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
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Data.latoRegular(this), Typeface.BOLD);

        textViewChangePhoneNoHelp = (TextView) findViewById(R.id.textViewChangePhoneNoHelp); textViewChangePhoneNoHelp.setTypeface(Data.latoRegular(this));
        editTextNewPhoneNumber = (EditText) findViewById(R.id.editTextNewPhoneNumber); editTextNewPhoneNumber.setTypeface(Data.latoRegular(this));
        buttonChangePhoneNumber = (Button) findViewById(R.id.buttonChangePhoneNumber); buttonChangePhoneNumber.setTypeface(Data.latoRegular(this));
		

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
                if("".equalsIgnoreCase(phoneNoChanged)){
                    editTextNewPhoneNumber.requestFocus();
                    editTextNewPhoneNumber.setError("Phone number can't be empty");
                }
                else{
                    phoneNoChanged = Utils.retrievePhoneNumberTenChars(phoneNoChanged);
                    if(Utils.validPhoneNumber(phoneNoChanged)) {
                        phoneNoChanged = "+91" + phoneNoChanged;
                        if(previousPhoneNumber.equalsIgnoreCase(phoneNoChanged)){
                            editTextNewPhoneNumber.requestFocus();
                            editTextNewPhoneNumber.setError("Changed Phone number is same as the previous one.");
                        }
                        else{
                            updateUserProfileAPI(ChangePhoneBeforeOTPActivity.this, phoneNoChanged, accessToken);
                        }
                    }
                    else{
                        editTextNewPhoneNumber.requestFocus();
                        editTextNewPhoneNumber.setError("Please enter valid phone number");
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



    public void updateUserProfileAPI(final Activity activity, final String updatedField, String accessToken) {
        if(AppStatus.getInstance(activity).isOnline(activity)) {

            DialogPopup.showLoadingDialog(activity, "Updating...");

            RequestParams params = new RequestParams();

            params.put("client_id", Data.CLIENT_ID);
            params.put("login_type", Data.LOGIN_TYPE);
            params.put("access_token", accessToken);
            params.put("is_access_token_new", "1");
            params.put("updated_phone_no", updatedField);


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
                            int flag = ApiResponseFlags.ACTION_COMPLETE.getOrdinal();
                            if(jObj.has("flag")){
                                flag = jObj.getInt("flag");
                            }
                            String message = JSONParser.getServerMessage(jObj);
                            if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag)){
                                if(ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag){
                                    DialogPopup.alertPopup(activity, "", message);
                                }
                                else if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){
                                    OTPConfirmScreen.emailRegisterData.phoneNo = updatedField;
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


	
	@Override
	public void onBackPressed() {
		performBackPressed();
		super.onBackPressed();
	}
	
	
	public void performBackPressed(){
        Intent intent = new Intent(ChangePhoneBeforeOTPActivity.this, OTPConfirmScreen.class);
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
package product.clicklabs.jugnoo.driver;

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

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.oldRegistration.OldOTPConfirmScreen;
import product.clicklabs.jugnoo.driver.oldRegistration.OldRegisterScreen;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class RequestDuplicateRegistrationActivity extends BaseActivity {

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

				name = OldOTPConfirmScreen.emailRegisterData.name;
				email = OldOTPConfirmScreen.emailRegisterData.emailId;
				phone = OldOTPConfirmScreen.emailRegisterData.phoneNo;

				submitDuplicateRegistrationRequestAPI(RequestDuplicateRegistrationActivity.this, messageStr, name, email, phone);

			}
		});


        try {
			textViewRegisterNameValue.setText(OldOTPConfirmScreen.emailRegisterData.name);
			textViewRegisterEmailValue.setText(OldOTPConfirmScreen.emailRegisterData.emailId);
			textViewRegisterPhoneValue.setText(OldOTPConfirmScreen.emailRegisterData.phoneNo);
			editTextMessage.setHint(getResources().getString(R.string.You_have_already_created, Data.previousAccountInfoList.size()));
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

						params_12.height = (int) (heightDiff);

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
	
	
	
	
//Retrofit


	public void submitDuplicateRegistrationRequestAPI(final Activity activity, String messageStr, String name, String email, String phone) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

			DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));

//			RequestParams params = new RequestParams();
			HashMap<String, String> params = new HashMap<String, String>();

			params.put("user_name", name);
			params.put("user_email", email);
			params.put("phone_no", phone);
			params.put("user_message", ""+messageStr);
			params.put("client_id", Data.CLIENT_ID);
			params.put("login_type", Data.LOGIN_TYPE);
			HomeUtil.putDefaultParams(params);

			try {
				if (OldRegisterScreen.multipleCaseJSON != null) {
					params.put("users", ""+ OldRegisterScreen.multipleCaseJSON.getJSONArray("users"));
				}
			} catch(Exception e){
				e.printStackTrace();
			}
			RestClient.getApiServices().submitDuplicateRegistrationRequestAPIRetro(params, new Callback<RegisterScreenResponse>() {
				@Override
				public void success(RegisterScreenResponse registerScreenResponse, Response response) {
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj;
						jObj = new JSONObject(jsonString);
						int flag = jObj.getInt("flag");
						String message = JSONParser.getServerMessage(jObj);
						if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag, null)){
							if(ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag){
								DialogPopup.alertPopup(activity, "", message);
							}
							else if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){
								DialogPopup.alertPopupWithListener(activity, "", message, new OnClickListener(){
									@Override
									public void onClick(View v) {
										activity.startActivity(new Intent(activity, SplashNewActivity.class));
										activity.finish();
										activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
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
					}
					DialogPopup.dismissLoadingDialog();
				}

				@Override
				public void failure(RetrofitError error) {
					DialogPopup.dismissLoadingDialog();
					DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
				}
			});




		}
		else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}
	}

	
}

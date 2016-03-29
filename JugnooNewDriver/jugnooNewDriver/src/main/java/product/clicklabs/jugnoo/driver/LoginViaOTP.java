package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by aneeshbansal on 29/03/16.
 */
public class LoginViaOTP extends Activity {

	LinearLayout linearLayoutWaiting, relative;
	EditText phoneNoEt, otpEt;
	Button backBtn, btnGenerateOtp, loginViaOtp;
	ImageView imageViewYellowLoadingBar;
	TextView textViewCounter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signin_otp);

		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(LoginViaOTP.this, relative, 1134, 720, false);

		phoneNoEt = (EditText) findViewById(R.id.phoneNoEt);
		phoneNoEt.setTypeface(Data.latoRegular(getApplicationContext()));
		otpEt = (EditText) findViewById(R.id.otpEt);
		otpEt.setTypeface(Data.latoRegular(getApplicationContext()));

		linearLayoutWaiting = (LinearLayout) findViewById(R.id.linearLayoutWaiting);
		backBtn = (Button) findViewById(R.id.backBtn);
		backBtn.setTypeface(Data.latoRegular(getApplicationContext()));
		btnGenerateOtp = (Button) findViewById(R.id.btnGenerateOtp);
		btnGenerateOtp.setTypeface(Data.latoRegular(getApplicationContext()));
		loginViaOtp = (Button) findViewById(R.id.loginViaOtp);
		loginViaOtp.setTypeface(Data.latoRegular(getApplicationContext()));

		imageViewYellowLoadingBar = (ImageView) findViewById(R.id.imageViewYellowLoadingBar);
		textViewCounter = (TextView) findViewById(R.id.textViewCounter);
		textViewCounter.setTypeface(Data.latoRegular(getApplicationContext()));

		btnGenerateOtp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String phoneNo = phoneNoEt.getText().toString().trim();
				if ("".equalsIgnoreCase(phoneNo)) {
					phoneNoEt.requestFocus();
					phoneNoEt.setError("Please enter Phone No.");
				} else if ((Utils.validPhoneNumber(phoneNo))) {
					generateOTP(phoneNo);
				} else {
					phoneNoEt.requestFocus();
					phoneNoEt.setError("Please enter valid email or phone no.");
				}
			}
		});


	}



	public void generateOTP(final String phoneNo) {
		try {
			if(AppStatus.getInstance(LoginViaOTP.this).isOnline(LoginViaOTP.this)) {
				DialogPopup.showLoadingDialog(LoginViaOTP.this, "Loading...");
				HashMap<String, String> params = new HashMap<>();
				params.put("phone_no", phoneNo);

				RestClient.getApiServices().generateOtp(params, new Callback<RegisterScreenResponse>() {
					@Override
					public void success(RegisterScreenResponse registerScreenResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						DialogPopup.dismissLoadingDialog();
						try {
							JSONObject jObj = new JSONObject(responseStr);
							String message = JSONParser.getServerMessage(jObj);
							int flag = jObj.getInt("flag");
							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								DialogPopup.dialogBanner(LoginViaOTP.this, message);
							} else {
								DialogPopup.alertPopup(LoginViaOTP.this, "", message);
							}
						} catch (Exception e) {
							e.printStackTrace();
							DialogPopup.alertPopup(LoginViaOTP.this, "", Data.SERVER_ERROR_MSG);
						}

						btnGenerateOtp.setVisibility(View.GONE);
						loginViaOtp.setVisibility(View.VISIBLE);


					}

					@Override
					public void failure(RetrofitError error) {
						DialogPopup.dismissLoadingDialog();
						DialogPopup.alertPopup(LoginViaOTP.this, "", Data.SERVER_ERROR_MSG);
					}
				});
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}

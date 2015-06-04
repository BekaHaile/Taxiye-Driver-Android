package product.clicklabs.jugnoo.driver;

import org.json.JSONObject;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Log;
import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.flurry.android.FlurryAgent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

public class AddCustomerCashActivity extends Activity{
	
	LinearLayout relative;
	
	Button backBtn;
	TextView title;
	
	TextView textViewCashByCustomer;
	EditText editTextCashAmount;
	Button buttonAddCustomerCash, buttonNoCash;
	
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
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_customer_cash);
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(AddCustomerCashActivity.this, relative, 1134, 720, false);
		
		backBtn = (Button) findViewById(R.id.backBtn); 
		title = (TextView) findViewById(R.id.title); title.setTypeface(Data.latoRegular(this), Typeface.BOLD);
		
		textViewCashByCustomer = (TextView) findViewById(R.id.textViewCashByCustomer); textViewCashByCustomer.setTypeface(Data.latoRegular(this), Typeface.BOLD);
		
		editTextCashAmount = (EditText) findViewById(R.id.editTextCashAmount); editTextCashAmount.setTypeface(Data.latoRegular(this));
		
		buttonAddCustomerCash = (Button) findViewById(R.id.buttonAddCustomerCash); buttonAddCustomerCash.setTypeface(Data.latoRegular(this));
		buttonNoCash = (Button) findViewById(R.id.buttonNoCash); buttonNoCash.setTypeface(Data.latoRegular(this));
		buttonNoCash.setVisibility(View.GONE);
		
		backBtn.setOnClickListener(new View.OnClickListener() {
		
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});
		
		buttonNoCash.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				addCustomerCashAPI(AddCustomerCashActivity.this, "0");
			}
		});
		
		buttonAddCustomerCash.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					String amountStr = editTextCashAmount.getText().toString().trim();
					if("".equalsIgnoreCase(amountStr)){
						editTextCashAmount.requestFocus();
						editTextCashAmount.setError("Please enter some amount");
					}
					else{
						double amount = Double.parseDouble(editTextCashAmount.getText().toString().trim());
						if(AppStatus.getInstance(AddCustomerCashActivity.this).isOnline(AddCustomerCashActivity.this)){
							if(amount > 1000){
								editTextCashAmount.requestFocus();
								editTextCashAmount.setError("Please enter less amount");
							}
							else if(Data.endRideData != null && amount < Data.endRideData.toPay){
								DialogPopup.alertPopup(AddCustomerCashActivity.this, "", "You cannot add amount less than \nRs. "+Data.endRideData.toPay);
							}
							else{
								addCustomerCashAPI(AddCustomerCashActivity.this, ""+amount);
							}
						}
						else{
							DialogPopup.alertPopup(AddCustomerCashActivity.this, "", Data.CHECK_INTERNET_MSG);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					
					editTextCashAmount.requestFocus();
					editTextCashAmount.setError("Please enter valid amount");
				}
			}
		});
		
		editTextCashAmount.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				int result = actionId & EditorInfo.IME_MASK_ACTION;
				switch (result) {
					case EditorInfo.IME_ACTION_DONE:
						buttonAddCustomerCash.performClick();
					break;

					case EditorInfo.IME_ACTION_NEXT:
					break;

					default:
				}
				return true;
			}
		});
		
		
		
		
		
	}
	
	
	public void performBackPressed(){
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}
	
	@Override
	public void onBackPressed() {
		performBackPressed();
		super.onBackPressed();
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
	}
	
	
	public void addCustomerCashAPI(final Activity activity, final String amount) {
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				DialogPopup.showLoadingDialog(activity, "Loading...");
				
				RequestParams params = new RequestParams();
				
				params.put("access_token", Data.userData.accessToken);
				params.put("engagement_id", Data.dEngagementId);
				params.put("money_added", amount);
			
				AsyncHttpClient asyncHttpClient = Data.getClient();
				asyncHttpClient.post(Data.SERVER_URL + "/add_money_to_wallet", params,
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
									
									int flag = jObj.getInt("flag");
									if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag)){
										if(ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag){
											String errorMessage = jObj.getString("error");
											DialogPopup.alertPopup(activity, "", errorMessage);	
										}
										else if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){
											if("0".equalsIgnoreCase(amount)){
												if(HomeActivity.appInterruptHandler != null){
													HomeActivity.appInterruptHandler.onCustomerCashDone();
													performBackPressed();
												}
											}
											else{
												String message = jObj.getString("message");
												DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
													
													@Override
													public void onClick(View v) {
														if(HomeActivity.appInterruptHandler != null){
															HomeActivity.appInterruptHandler.onCustomerCashDone();
															performBackPressed();
														}
													}
												});
											}
										}
										else{
											DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
										}
									}
									
								}  catch (Exception exception) {
									exception.printStackTrace();
									DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
									
								}
							}
							
							@Override
							public void onFinish() {
								super.onFinish();
							}
							
						});
			}
			else {
				DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			}
	}
	
}

package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.adapters.CancelOptionsListAdapter;
import product.clicklabs.jugnoo.driver.apis.ApiSendCallLogs;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.NonScrollListView;
import product.clicklabs.jugnoo.driver.utils.NudgeClient;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class RideCancellationActivity extends BaseActivity implements ActivityCloser {


	LinearLayout relative;

	ImageView imageViewBack;
	TextView textViewTitle;

	NonScrollListView listViewCancelOptions;
	CancelOptionsListAdapter cancelOptionsListAdapter;

	Button buttonCancelRide;

	ScrollView scrollView;
	LinearLayout linearLayoutMain;

	public static ActivityCloser activityCloser = null;

	String engagementId = "";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cancel_ride);

		if(getIntent().hasExtra(Constants.KEY_ENGAGEMENT_ID)) {
			engagementId = getIntent().getStringExtra(Constants.KEY_ENGAGEMENT_ID);
		} else{
			finish();
			return;
		}

		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(RideCancellationActivity.this, relative, 1134, 720, false);


		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
		textViewTitle = (TextView) findViewById(R.id.textViewTitle);
		textViewTitle.setTypeface(Data.latoRegular(this), Typeface.BOLD);


		listViewCancelOptions = (NonScrollListView) findViewById(R.id.listViewCancelOptions);
		cancelOptionsListAdapter = new CancelOptionsListAdapter(RideCancellationActivity.this);
		listViewCancelOptions.setAdapter(cancelOptionsListAdapter);


		buttonCancelRide = (Button) findViewById(R.id.buttonCancelRide);
		buttonCancelRide.setTypeface(Data.latoRegular(this));

		scrollView = (ScrollView) findViewById(R.id.scrollView);
		linearLayoutMain = (LinearLayout) findViewById(R.id.linearLayoutMain);


		imageViewBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed(false);
			}
		});


		buttonCancelRide.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Data.cancelOptionsList != null) {
					String cancelReasonsStr = "";
					for (int i = 0; i < Data.cancelOptionsList.size(); i++) {
						if (Data.cancelOptionsList.get(i).checked) {
							cancelReasonsStr = Data.cancelOptionsList.get(i).name;
							break;
						}
					}

					if ("".equalsIgnoreCase(cancelReasonsStr)) {
						DialogPopup.alertPopup(RideCancellationActivity.this, "", getResources().getString(R.string.select_reason));
					} else {
						driverCancelRideAsync(RideCancellationActivity.this, cancelReasonsStr);
					}
				}

			}
		});


		RideCancellationActivity.activityCloser = this;

		setCancellationOptions();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if(intent.hasExtra(Constants.KEY_KILL_APP)){
			finish();
		}
	}

	private void setCancellationOptions() {
		try {
			if (Data.cancelOptionsList != null) {
				for (int i = 0; i < Data.cancelOptionsList.size(); i++) {
					Data.cancelOptionsList.get(i).checked = false;
				}
				cancelOptionsListAdapter.notifyDataSetChanged();
			} else {
				performBackPressed(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			performBackPressed(false);
		}
	}


	public void performBackPressed(boolean result) {

		Intent intent=new Intent();
		intent.putExtra("result",result);
		setResult(12,intent);
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}

	@Override
	public void onBackPressed() {
		performBackPressed(false);
		super.onBackPressed();
	}


	@Override
	public void onDestroy() {
		RideCancellationActivity.activityCloser = null;
		super.onDestroy();
		try {
			ASSL.closeActivity(relative);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.gc();
	}


	@Override
	public void close() {
		performBackPressed(false);
	}


	public void driverCancelRideAsync(final Activity activity, final String reason) {
		try {
			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
				DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));

				HashMap<String, String> params = new HashMap<String, String>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_CUSTOMER_ID, String.valueOf(Data.getCustomerInfo(engagementId).getUserId()));
				params.put(Constants.KEY_ENGAGEMENT_ID, engagementId);
				params.put(Constants.KEY_CANCELLATION_REASON, reason);

				if (Data.getCustomerInfo(engagementId) != null) {
					params.put(Constants.KEY_REFERENCE_ID, String.valueOf(Data.getCustomerInfo(engagementId).getReferenceId()));
				}
				RestClient.getApiServices().driverCancelRideRetro(params, new Callback<RegisterScreenResponse>() {
					@Override
					public void success(RegisterScreenResponse registerScreenResponse, Response response) {
						DialogPopup.dismissLoadingDialog();
						try {
							String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
							JSONObject jObj;
							jObj = new JSONObject(jsonString);
							int flag = jObj.optInt(Constants.KEY_FLAG, ApiResponseFlags.RIDE_CANCELLED_BY_DRIVER.getOrdinal());
							String message = JSONParser.getServerMessage(jObj);
							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag)) {
								if (ApiResponseFlags.RIDE_CANCELLED_BY_DRIVER.getOrdinal() == flag) {
									performBackPressed(true);
									try {
										new ApiSendCallLogs().sendCallLogs(RideCancellationActivity.this, Data.userData.accessToken,
												engagementId, Data.getCustomerInfo(engagementId).getPhoneNumber());
									} catch (Exception e) {
										e.printStackTrace();
									}
									new DriverTimeoutCheck().timeoutBuffer(activity, 2);
									nudgeCancelRide(reason);

									if (HomeActivity.appInterruptHandler != null) {
										HomeActivity.appInterruptHandler.handleCancelRideSuccess(engagementId, message);
									}
							} else{
									performBackPressed(false);
									if (HomeActivity.appInterruptHandler != null) {
										HomeActivity.appInterruptHandler.handleCancelRideFailure(message);
									}
								}
							} else{
								performBackPressed(false);
								if (HomeActivity.appInterruptHandler != null) {
									HomeActivity.appInterruptHandler.handleCancelRideFailure(message);
								}
							}
						} catch (Exception exception) {
							exception.printStackTrace();
							performBackPressed(false);
							if (HomeActivity.appInterruptHandler != null) {
								HomeActivity.appInterruptHandler.handleCancelRideFailure(activity.getResources().getString(R.string.server_error));
							}
						}
					}

					@Override
					public void failure(RetrofitError error) {
						DialogPopup.dismissLoadingDialog();
						performBackPressed(false);
						if (HomeActivity.appInterruptHandler != null) {
							HomeActivity.appInterruptHandler.handleCancelRideFailure(activity.getResources().getString(R.string.server_not_responding));
						}
					}
				});
			} else {
				DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void nudgeCancelRide(String reasons){
		try{
			JSONObject map = new JSONObject();
			map.put(Constants.KEY_CANCELLATION_REASON, reasons);
			map.put(Constants.KEY_ENGAGEMENT_ID, engagementId);
			map.put(Constants.KEY_CUSTOMER_ID, String.valueOf(Data.getCustomerInfo(engagementId).getUserId()));
			NudgeClient.trackEvent(this, FlurryEventNames.NUDGE_CANCEL_RIDE, map);
		} catch(Exception e){
			e.printStackTrace();
		}
	}

}

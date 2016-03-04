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
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.NonScrollListView;
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


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cancel_ride);


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
				performBackPressed();
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
						DialogPopup.alertPopup(RideCancellationActivity.this, "", "Please select one reason");
					} else {
						driverCancelRideAsync(RideCancellationActivity.this, cancelReasonsStr);
					}
				}

			}
		});


		RideCancellationActivity.activityCloser = this;

		setCancellationOptions();
	}


	private void setCancellationOptions() {
		try {
			if (Data.cancelOptionsList != null) {
				for (int i = 0; i < Data.cancelOptionsList.size(); i++) {
					Data.cancelOptionsList.get(i).checked = false;
				}
				cancelOptionsListAdapter.notifyDataSetChanged();
			} else {
				performBackPressed();
			}
		} catch (Exception e) {
			e.printStackTrace();
			performBackPressed();
		}
	}


	public void performBackPressed() {
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
		RideCancellationActivity.activityCloser = null;
		super.onDestroy();
		ASSL.closeActivity(relative);
		System.gc();
	}


	@Override
	public void close() {
		performBackPressed();
	}


	public void driverCancelRideAsync(final Activity activity, String reason) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			DialogPopup.showLoadingDialog(activity, "Loading...");

			HashMap<String, String> params = new HashMap<String, String>();
			params.put("access_token", Data.userData.accessToken);
			params.put("customer_id", Data.dCustomerId);
			params.put("engagement_id", Data.dEngagementId);
			params.put("cancellation_reason", reason);

			if (Data.assignedCustomerInfo != null) {
				params.put("reference_id", "" + Data.assignedCustomerInfo.referenceId);
			}
			RestClient.getApiServices().driverCancelRideRetro(params, new Callback<RegisterScreenResponse>() {
				@Override
				public void success(RegisterScreenResponse registerScreenResponse, Response response) {
					DialogPopup.dismissLoadingDialog();
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj;
						jObj = new JSONObject(jsonString);

						if (!jObj.isNull("error")) {
							String errorMessage = jObj.getString("error");
							if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
								HomeActivity.logoutUser(activity);
							} else {
								DialogPopup.alertPopup(activity, "", errorMessage);
							}
						} else {
							try {
								int flag = jObj.getInt("flag");
								if (ApiResponseFlags.REQUEST_TIMEOUT.getOrdinal() == flag) {
									String log = jObj.getString("log");
									DialogPopup.alertPopup(activity, "", "" + log);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

							performBackPressed();
							if (HomeActivity.appInterruptHandler != null) {
								HomeActivity.appInterruptHandler.handleCancelRideSuccess();
							}
						}
						Prefs.with(activity).save(SPLabels.INGNORE_RIDEREQUEST_COUNT, Prefs.with(activity).getInt(SPLabels.INGNORE_RIDEREQUEST_COUNT, 0) + 1);
						if (Prefs.with(activity).getInt(SPLabels.MAX_INGNORE_RIDEREQUEST_COUNT, 0) <= Prefs.with(activity).getInt(SPLabels.INGNORE_RIDEREQUEST_COUNT, 0)) {
							Intent timeoutIntent = new Intent(activity, DriverTimeoutIntentService.class);
							activity.startService(timeoutIntent);
						}
					} catch (Exception exception) {
						exception.printStackTrace();
						DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
					}
				}

				@Override
				public void failure(RetrofitError error) {
					DialogPopup.dismissLoadingDialog();
					performBackPressed();
					if (HomeActivity.appInterruptHandler != null) {
						HomeActivity.appInterruptHandler.handleCancelRideFailure();
					}
				}
			});
		} else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}
	}


}

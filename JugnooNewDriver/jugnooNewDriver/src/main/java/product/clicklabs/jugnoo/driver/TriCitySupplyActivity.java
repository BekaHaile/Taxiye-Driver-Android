package product.clicklabs.jugnoo.driver;

import android.app.Activity;
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

import product.clicklabs.jugnoo.driver.adapters.DestinationOptionsListAdapter;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.NonScrollListView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class TriCitySupplyActivity extends BaseActivity implements ActivityCloser {


	LinearLayout relative;

	ImageView imageViewBack;
	TextView textViewTitle;

	NonScrollListView listViewDestinationOptions;
	DestinationOptionsListAdapter destinationOptionsListAdapter;

	Button buttonOk;

	ScrollView scrollView;
	LinearLayout linearLayoutMain;

	public static ActivityCloser activityCloser = null;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tricity_supply);


		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(TriCitySupplyActivity.this, relative, 1134, 720, false);


		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
		textViewTitle = (TextView) findViewById(R.id.textViewTitle);
		textViewTitle.setTypeface(Data.latoRegular(this), Typeface.BOLD);


		listViewDestinationOptions = (NonScrollListView) findViewById(R.id.listViewDestinationOptions);
		destinationOptionsListAdapter = new DestinationOptionsListAdapter(TriCitySupplyActivity.this);
		listViewDestinationOptions.setAdapter(destinationOptionsListAdapter);


		buttonOk = (Button) findViewById(R.id.buttonOk);
		buttonOk.setTypeface(Data.latoRegular(this));

		scrollView = (ScrollView) findViewById(R.id.scrollView);
		linearLayoutMain = (LinearLayout) findViewById(R.id.linearLayoutMain);


		imageViewBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});


		buttonOk.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Data.destiantionOptionsList != null) {
					String destiantionData = "";
					for (int i = 0; i < Data.destiantionOptionsList.size(); i++) {
						if (Data.destiantionOptionsList.get(i).checked) {
							destiantionData = Data.destiantionOptionsList.get(i).name;
							break;
						}
					}

					if ("".equalsIgnoreCase(destiantionData)) {
						DialogPopup.alertPopup(TriCitySupplyActivity.this, "", "Please select one Destination");
					} else {
						driverDestinationAsync(TriCitySupplyActivity.this, destiantionData);
					}
				}

			}
		});


		TriCitySupplyActivity.activityCloser = this;

		setDestinationOptions();
	}


	private void setDestinationOptions() {
		try {
			if (Data.destiantionOptionsList != null) {
				for (int i = 0; i < Data.destiantionOptionsList.size(); i++) {
					Data.destiantionOptionsList.get(i).checked = false;
				}
				destinationOptionsListAdapter.notifyDataSetChanged();
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
		TriCitySupplyActivity.activityCloser = null;
		super.onDestroy();
		ASSL.closeActivity(relative);
		System.gc();
	}


	@Override
	public void close() {
		performBackPressed();
	}


	public void driverDestinationAsync(final Activity activity, String destination) {
		if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			DialogPopup.showLoadingDialog(activity, "Loading...");

			HashMap<String, String> params = new HashMap<String, String>();
			params.put("access_token", Data.userData.accessToken);
			params.put("customer_id", Data.dCustomerId);
			params.put("engagement_id", Data.dEngagementId);
			params.put("destiantion", destination);

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

						new DriverTimeoutCheck().timeoutBuffer(activity, true);
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

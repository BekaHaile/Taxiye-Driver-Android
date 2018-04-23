package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.driver.adapters.SharingRidesAdapter;
import product.clicklabs.jugnoo.driver.adapters.SharingRidesAdapterHandler;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.datastructure.SharingRideData;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.SharedRideResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by aneeshbansal on 05/10/15.
 */
public class SharingRidesActivity extends BaseActivity {

	LinearLayout relative;
	Button backBtn;
	RecyclerView recyclerViewDrivers;
	TextView title;
	SharingRidesAdapter sharingRidesAdapter;
	SwipeRefreshLayout swipeRefreshLayoutShareRides;
	ArrayList<SharingRideData> sharedRides = new ArrayList<>();


	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

		try {
			String sharingEngagementData = intent.getStringExtra("sharing_engagement_data");
			JSONObject jObj = new JSONObject(sharingEngagementData);
			SharingRideData sharingRideData = new SharingRideData(jObj.getString("engagement_id"),
					jObj.getString("transaction_time"),
					jObj.getString("customer_phone_no"),
					jObj.getDouble("actual_fare"),
					jObj.getDouble("paid_in_cash"),
					jObj.getDouble("account_balance"),jObj.optString("currency"));
			if(!sharedRides.contains(sharingRideData)){
				sharedRides.add(0, sharingRideData);
				sharingRidesAdapter.notifyDataSetChanged();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sharedRides.clear();
		setContentView(R.layout.activity_jugnoo_share_payments);

		if(getIntent().hasExtra("wakeUp")){
			Intent homeScreen = new Intent(this, SplashNewActivity.class);
			startActivity(homeScreen);
			finish();
		}

		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(this, relative, 1134, 720, false);

		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Data.latoRegular(getApplicationContext()));
		backBtn = (Button) findViewById(R.id.backBtn);
		swipeRefreshLayoutShareRides = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayoutShareRides);

		swipeRefreshLayoutShareRides.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
//				getSharedRidesAsync(SharingRidesActivity.this);
			}
		});

		recyclerViewDrivers = (RecyclerView) findViewById(R.id.recyclerViewSharingRides);
		recyclerViewDrivers.setHasFixedSize(true);
		LinearLayoutManager llm = new LinearLayoutManager(this);
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerViewDrivers.setLayoutManager(llm);

		sharedRides = new ArrayList<>();
		sharingRidesAdapter = new SharingRidesAdapter(this, sharedRides, adapterHandler);
		recyclerViewDrivers.setAdapter(sharingRidesAdapter);

		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});

		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				DialogPopup.showLoadingDialog(SharingRidesActivity.this, getResources().getString(R.string.loading));
//				getSharedRidesAsync(SharingRidesActivity.this);
			}
		});

	}

	SharingRidesAdapterHandler adapterHandler = new SharingRidesAdapterHandler() {
		@Override
		public void okClicked(SharingRideData sharingRideData) {
			String completedEngagementIds = Prefs.with(SharingRidesActivity.this).getString(SPLabels.SHARING_ENGAGEMENTS_COMPLETED, "");
			if("".equalsIgnoreCase(completedEngagementIds)){
				completedEngagementIds = completedEngagementIds + sharingRideData.sharingEngagementId;
			}
			else{
				completedEngagementIds = completedEngagementIds + "," + sharingRideData.sharingEngagementId;
			}
			Prefs.with(SharingRidesActivity.this).save(SPLabels.SHARING_ENGAGEMENTS_COMPLETED, completedEngagementIds);
		}
	};

	public void updateListData(String message, boolean errorOccurred) {
		if (errorOccurred) {
			DialogPopup.alertPopup(SharingRidesActivity.this, "", message);
			sharedRides.clear();
			sharingRidesAdapter.notifyDataSetChanged();
		} else {
			sharingRidesAdapter.notifyDataSetChanged();
		}
	}

	private void getSharedRidesAsync(final Activity activity) {
		try {
			swipeRefreshLayoutShareRides.setRefreshing(true);
			HashMap<String, String> params = new HashMap<>();
			params.put("access_token", Data.userData.accessToken);
			HomeUtil.putDefaultParams(params);
			RestClient.getApiServices().getSharedRidesAsync(params, new Callback<SharedRideResponse>() {
				@Override
				public void success(SharedRideResponse sharedRideResponse, Response response) {
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.e("Shared rides jsonString", "="+jsonString);
						JSONObject jObj;
						jObj = new JSONObject(jsonString);
						if (!jObj.isNull("error")) {
							String errorMessage = jObj.getString("error");
							if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
								HomeActivity.logoutUser(activity);
							} else {
								updateListData(getResources().getString(R.string.error_occured_tap_to_retry), true);
							}
						} else {
							sharedRides.clear();
							String completedEngagementIds = Prefs.with(activity).getString(SPLabels.SHARING_ENGAGEMENTS_COMPLETED, "");
							List<String> arr = Arrays.asList(completedEngagementIds.split(","));
							for (int i = 0; i < sharedRideResponse.getBookingData().size(); i++) {
								SharedRideResponse.BookingData data = sharedRideResponse.getBookingData().get(i);
								SharingRideData rideInfo = new SharingRideData(data.getSharingEngagementId(), data.getTransactionTime(),
										data.getPhoneNo(), data.getActualFare(), data.getPaidInCash(), data.getAccountBalance(),data.getCurrencyUnit());
								if(arr.contains(rideInfo.sharingEngagementId)){
									rideInfo.completed = 1;
								}
								sharedRides.add(rideInfo);
							}
							updateListData(getResources().getString(R.string.no_rides_currently), false);
						}
					} catch (Exception exception) {
						exception.printStackTrace();
						updateListData(getResources().getString(R.string.error_occured_tap_to_retry), true);
					}
					swipeRefreshLayoutShareRides.setRefreshing(false);
					DialogPopup.dismissLoadingDialog();
				}

				@Override
				public void failure(RetrofitError error) {
					swipeRefreshLayoutShareRides.setRefreshing(false);
					DialogPopup.dismissLoadingDialog();
					updateListData(getResources().getString(R.string.error_occured_tap_to_retry), true);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}


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


}

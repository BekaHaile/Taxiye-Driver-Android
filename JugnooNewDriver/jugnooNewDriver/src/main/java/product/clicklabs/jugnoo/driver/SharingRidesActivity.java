package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import org.json.JSONObject;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.adapters.SharingRidesAdapter;
import product.clicklabs.jugnoo.driver.adapters.SharingRidesAdapterHandler;
import product.clicklabs.jugnoo.driver.datastructure.SharingRideData;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.SharedRideResponse;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import rmn.androidscreenlibrary.ASSL;

/**
 * Created by aneeshbansal on 05/10/15.
 */
public class SharingRidesActivity extends Activity {

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
		HomeActivity.checkIfUserDataNull(this);
		getSharedRidesAsync(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sharedRides.clear();
		setContentView(R.layout.activity_jugnoo_share_payments);

		relative = (LinearLayout) findViewById(R.id.relative);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Data.latoRegular(getApplicationContext()));
		backBtn = (Button) findViewById(R.id.backBtn);
		swipeRefreshLayoutShareRides = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayoutShareRides);

		swipeRefreshLayoutShareRides.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				getSharedRidesAsync(SharingRidesActivity.this);
			}
		});

		recyclerViewDrivers = (RecyclerView) findViewById(R.id.recyclerViewSharingRides);
		recyclerViewDrivers.setHasFixedSize(true);
		LinearLayoutManager llm = new LinearLayoutManager(this);
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerViewDrivers.setLayoutManager(llm);
		recyclerViewDrivers.setVisibility(View.GONE);

		sharedRides = new ArrayList<>();
		sharingRidesAdapter = new SharingRidesAdapter(this, sharedRides, adapterHandler);
		recyclerViewDrivers.setAdapter(sharingRidesAdapter);

		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {performBackPressed();
			}
		});
	}

	SharingRidesAdapterHandler adapterHandler = new SharingRidesAdapterHandler() {
		@Override
		public void okClicked(SharingRideData sharingRideData) {

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
		swipeRefreshLayoutShareRides.setRefreshing(true);
		RestClient.getApiServices().getSharedRidesAsync(Data.userData.accessToken, new Callback<SharedRideResponse>() {
			@Override
			public void success(SharedRideResponse sharedRideResponse, Response response) {
				try {
					String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
					JSONObject jObj;
					jObj = new JSONObject(jsonString);
					if (!jObj.isNull("error")) {
						String errorMessage = jObj.getString("error");
						if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
							HomeActivity.logoutUser(activity);
						} else {
							updateListData("Some error occurred. Tap to retry", true);
						}

					} else {

						for (int i = 0; i < sharedRideResponse.getBookingData().size(); i++) {
							SharedRideResponse.BookingData data = sharedRideResponse.getBookingData().get(i);
							SharingRideData rideInfo = new SharingRideData(data.getSharingEngagementId(), data.getTransactionTime(),
									data.getPhoneNo(), data.getAccountBalance(),
									data.getActualFare(), data.getPaidInCash());
							sharedRides.add(rideInfo);
						}
						updateListData("No rides currently", false);
					}
				} catch (Exception exception) {
					exception.printStackTrace();
					updateListData("Some error occurred. Tap to retry", true);
				}
				swipeRefreshLayoutShareRides.setRefreshing(false);
			}

			@Override
			public void failure(RetrofitError error) {
				swipeRefreshLayoutShareRides.setRefreshing(false);
				updateListData("Some error occurred. Tap to retry", true);
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


}

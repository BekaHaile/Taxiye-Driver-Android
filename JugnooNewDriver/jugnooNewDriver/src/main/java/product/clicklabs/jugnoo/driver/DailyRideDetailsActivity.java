package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.adapters.DailyRideDetailsAdapter;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.datastructure.DailyEarningItem;
import product.clicklabs.jugnoo.driver.datastructure.RideInfo;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.InfoTileResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.NewBookingHistoryRespose;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class DailyRideDetailsActivity extends BaseFragmentActivity {

	LinearLayout relative;

	Button backBtn;
	TextView title;


	ArrayList<DailyEarningItem> dailyEarningItems = new ArrayList<>();
	RecyclerView recyclerViewDailyInfo;
	DailyRideDetailsAdapter dailyRideDetailsAdapter;

	public static RideInfo openedRideInfo;
	public ASSL assl;
	CustomerInfo customerInfo;


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
		setContentView(R.layout.activity_daily_details_new);


		try {
			Intent intent = getIntent();
			String extra = intent.getStringExtra("extra");
			InfoTileResponse.Tile.Extras extras = new Gson().fromJson(extra, InfoTileResponse.Tile.Extras.class);

		} catch (Exception e) {
			e.printStackTrace();
		}


		relative = (LinearLayout) findViewById(R.id.relative);
		assl = new ASSL(DailyRideDetailsActivity.this, relative, 1134, 720, false);

		backBtn = (Button) findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Data.latoRegular(this));

		recyclerViewDailyInfo = (RecyclerView) findViewById(R.id.recyclerViewDailyInfo);
		recyclerViewDailyInfo.setHasFixedSize(true);
		LinearLayoutManager llm = new LinearLayoutManager(this);
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerViewDailyInfo.setLayoutManager(llm);
		recyclerViewDailyInfo.setItemAnimator(new DefaultItemAnimator());

		dailyEarningItems = new ArrayList<>();
		dailyRideDetailsAdapter = new DailyRideDetailsAdapter(this, dailyEarningItems, this);
		recyclerViewDailyInfo.setAdapter(dailyRideDetailsAdapter);

		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});


		dailyEarningItems.add(new DailyEarningItem(null, null, null, null, null, DailyRideDetailsAdapter.ViewType.TOTAL_AMNT));

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
		super.onDestroy();
		ASSL.closeActivity(relative);
		System.gc();
	}

	private void getRidesAsync(final Activity activity) {
		try {

			progressBar.setVisibility(View.VISIBLE);
			RestClient.getApiServices().bookingHistory(Data.userData.accessToken, "1",
					new Callback<NewBookingHistoryRespose>() {
						@Override
						public void success(NewBookingHistoryRespose newBookingHistoryRespose, Response response) {
							try {
								if(activity != null) {
									String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
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

									}
								}
							} catch (Exception e) {
								e.printStackTrace();
								try {
									updateListData(getResources().getString(R.string.error_occured_tap_to_retry), true);
								} catch (Exception e1) {
									e1.printStackTrace();
								}
							}

						}


						@Override
						public void failure(RetrofitError error) {
							try {
								progressBar.setVisibility(View.GONE);
								updateListData(getResources().getString(R.string.error_occured_tap_to_retry), true);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

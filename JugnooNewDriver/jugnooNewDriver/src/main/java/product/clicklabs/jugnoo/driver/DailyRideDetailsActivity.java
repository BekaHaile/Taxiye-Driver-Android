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
import android.widget.ListView;
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
import product.clicklabs.jugnoo.driver.retrofit.model.DailyEarningResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.InfoTileResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.NewBookingHistoryRespose;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class DailyRideDetailsActivity extends BaseFragmentActivity {

	LinearLayout linear;

	Button backBtn;
	TextView title;
	String date;
	TextView textViewInfoDisplay;
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
			date = intent.getStringExtra("date");
		} catch (Exception e) {
			e.printStackTrace();
		}


		linear = (LinearLayout) findViewById(R.id.linear);
		assl = new ASSL(DailyRideDetailsActivity.this, linear, 1134, 720, false);

		backBtn = (Button) findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Data.latoRegular(this));

		textViewInfoDisplay = (TextView) findViewById(R.id.textViewInfoDisplay);
		textViewInfoDisplay.setTypeface(Data.latoRegular(this));
		textViewInfoDisplay.setVisibility(View.GONE);

		textViewInfoDisplay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getRidesAsync(date, DailyRideDetailsActivity.this);
			}
		});

		recyclerViewDailyInfo = (RecyclerView) findViewById(R.id.recyclerViewDailyInfo);
		recyclerViewDailyInfo.setHasFixedSize(true);
		LinearLayoutManager llm = new LinearLayoutManager(this);
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerViewDailyInfo.setLayoutManager(llm);
		recyclerViewDailyInfo.setItemAnimator(new DefaultItemAnimator());

		dailyEarningItems = new ArrayList<>();
		dailyRideDetailsAdapter = new DailyRideDetailsAdapter(this, dailyEarningItems, new DailyRideDetailsAdapter.Callback() {
			@Override
			public void onRideClick(int position, Object extras) {

			}
		});
		recyclerViewDailyInfo.setAdapter(dailyRideDetailsAdapter);
		getRidesAsync(date, this);

		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});

	}

	public void updateListData(String message, boolean errorOccurred, DailyEarningResponse dailyEarningResponse) {
		if (errorOccurred) {
			textViewInfoDisplay.setText(message);
			textViewInfoDisplay.setVisibility(View.VISIBLE);
			dailyRideDetailsAdapter.notifyDataSetChanged();
		} else {
			dailyRideDetailsAdapter.setList(dailyEarningItems, dailyEarningResponse);
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
		super.onDestroy();
		ASSL.closeActivity(linear);
		System.gc();
	}

	private void getRidesAsync(String date, final Activity activity) {
		try {

			RestClient.getApiServices().getDailyRidesAsync(date, Data.userData.accessToken,
					new Callback<DailyEarningResponse>() {
						@Override
						public void success(DailyEarningResponse dailyEarningResponse, Response response) {
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
											updateListData(getResources().getString(R.string.error_occured_tap_to_retry), true, null);
										}

									} else {
										dailyEarningItems.clear();
										dailyEarningItems.add(new DailyEarningItem(null,0,null,0,null,DailyRideDetailsAdapter.ViewType.TOTAL_AMNT));

										for (int i=0; i<dailyEarningResponse.getDailyParam().size(); i++) {
											dailyEarningItems.add(new DailyEarningItem(dailyEarningResponse.getDailyParam().get(i).getText()
													, dailyEarningResponse.getDailyParam().get(i).getValue(),
													null, 0, null, DailyRideDetailsAdapter.ViewType.EARNING_PARAM));
										}
										dailyEarningItems.add(new DailyEarningItem(null,0,null,0,null,DailyRideDetailsAdapter.ViewType.TOTAL_VALUES));

										for (int i=0; i<dailyEarningResponse.getTrips().size(); i++) {
											dailyEarningItems.add(new DailyEarningItem(null, 0,dailyEarningResponse.getTrips().get(i).getTime(),
													 dailyEarningResponse.getTrips().get(i).getEarning(),
													 dailyEarningResponse.getTrips().get(i).getExtras(), DailyRideDetailsAdapter.ViewType.RIDE_INFO));
										}

										updateListData(getResources().getString(R.string.no_rides), false, dailyEarningResponse);
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
								try {
									updateListData(getResources().getString(R.string.error_occured_tap_to_retry), true, null);
								} catch (Exception e1) {
									e1.printStackTrace();
								}
							}

						}

						@Override
						public void failure(RetrofitError error) {
							try {
								updateListData(getResources().getString(R.string.error_occured_tap_to_retry), true, null);
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

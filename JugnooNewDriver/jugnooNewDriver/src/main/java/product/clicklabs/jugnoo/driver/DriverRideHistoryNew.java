package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Intent;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.driver.adapters.DailyRideDetailsAdapter;
import product.clicklabs.jugnoo.driver.adapters.DriverRideHistoryAdapter;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.datastructure.DailyEarningItem;
import product.clicklabs.jugnoo.driver.datastructure.RideHistoryItem;
import product.clicklabs.jugnoo.driver.datastructure.RideInfo;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.DailyEarningResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.InfoTileResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.InvoiceDetailResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Log;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class DriverRideHistoryNew extends BaseFragmentActivity {

	RelativeLayout linear;

	Button backBtn;
	TextView title;
	String date = "";
	TextView textViewInfoDisplay;
	ArrayList<RideHistoryItem> rideHistoryItems = new ArrayList<>();
	RecyclerView recyclerViewDailyInfo;

	DriverRideHistoryAdapter driverRideHistoryAdapter;
	Shader textShader;
	int totalRides = 0;
	public ASSL assl;
	int invoice_id = 0;
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

			if("".equalsIgnoreCase(intent.getStringExtra("date"))) {
				date = intent.getStringExtra("date");
			}
			if(intent.getIntExtra("invoice_id", 0) != 0) {
				invoice_id = intent.getIntExtra("invoice_id", 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


		linear = (RelativeLayout) findViewById(R.id.linear);
		assl = new ASSL(DriverRideHistoryNew.this, linear, 1134, 720, false);

		backBtn = (Button) findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Data.latoRegular(this));
//		textShader=new LinearGradient(0, 0, 0, 20,
//				new int[]{getResources().getColor(R.color.gradient_orange_v2), getResources().getColor(R.color.gradient_yellow_v2)},
//				new float[]{0, 1}, Shader.TileMode.CLAMP);
//		title.getPaint().setShader(textShader);

		title.setText(getResources().getString(R.string.ride_history_cap));
		textViewInfoDisplay = (TextView) findViewById(R.id.textViewInfoDisplay);
		textViewInfoDisplay.setTypeface(Data.latoRegular(this));
		textViewInfoDisplay.setVisibility(View.GONE);
		totalRides =0;
		textViewInfoDisplay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getRidesAsync(DriverRideHistoryNew.this, true);
			}
		});

		recyclerViewDailyInfo = (RecyclerView) findViewById(R.id.recyclerViewDailyInfo);
		recyclerViewDailyInfo.setHasFixedSize(true);
		LinearLayoutManager llm = new LinearLayoutManager(this);
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerViewDailyInfo.setLayoutManager(llm);
		recyclerViewDailyInfo.setItemAnimator(new DefaultItemAnimator());

		rideHistoryItems = new ArrayList<>();
		driverRideHistoryAdapter = new DriverRideHistoryAdapter(DriverRideHistoryNew.this, rideHistoryItems, totalRides, new DriverRideHistoryAdapter.Callback() {
			@Override
			public void onRideClick(int position, InfoTileResponse.Tile.Extras extras) {
				Intent intent = new Intent(DriverRideHistoryNew.this, RideDetailsNewActivity.class);
				Gson gson = new Gson();
				intent.putExtra("extras", gson.toJson(extras, InfoTileResponse.Tile.Extras.class));
				DriverRideHistoryNew.this.startActivity(intent);
				DriverRideHistoryNew.this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
			@Override
			public void onShowMoreClick() {
				getRidesAsync(DriverRideHistoryNew.this, false);
			}
		});
		recyclerViewDailyInfo.setAdapter(driverRideHistoryAdapter);


		getRidesAsync(this, true);
		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});

	}

	public void updateListData(String message, boolean errorOccurred) {
		if (errorOccurred) {
			textViewInfoDisplay.setText(message);
			textViewInfoDisplay.setVisibility(View.VISIBLE);
			driverRideHistoryAdapter.notifyDataSetChanged();
		} else {
			if(rideHistoryItems.size() > 0) {
				driverRideHistoryAdapter.setList(rideHistoryItems, totalRides);
			} else {
				textViewInfoDisplay.setText(getResources().getString(R.string.no_rides_currently));
				textViewInfoDisplay.setVisibility(View.VISIBLE);
				driverRideHistoryAdapter.notifyDataSetChanged();
			}
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

	private void getRidesAsync(final Activity activity, boolean refresh) {
		try {
			DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));
			HashMap<String, String> params = new HashMap<>();
			params.put("access_token", Data.userData.accessToken);
			params.put("start_from", "" + rideHistoryItems.size());
			params.put("engagement_date", "" + date);

			if(refresh){
				rideHistoryItems.clear();
			}

			RestClient.getApiServices().getDailyRidesAsync(params, new Callback<DailyEarningResponse>() {
						@Override
						public void success(DailyEarningResponse dailyEarningResponse, Response response) {
							try {
								if (activity != null) {
									String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
									JSONObject jObj;
									DialogPopup.dismissLoadingDialog();
									jObj = new JSONObject(jsonString);
									if (!jObj.isNull("error")) {
										String errorMessage = jObj.getString("error");
										if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
											HomeActivity.logoutUser(activity);
										} else {
											updateListData(getResources().getString(R.string.error_occured_tap_to_retry), true);
										}

									} else {
										totalRides = jObj.optInt("history_size", 10);
										for (int i = 0; i < dailyEarningResponse.getTrips().size(); i++) {

											rideHistoryItems.add(new RideHistoryItem(dailyEarningResponse.getTrips().get(i).getDate(),
													dailyEarningResponse.getTrips().get(i).getTime(), dailyEarningResponse.getTrips().get(i).getEarning(),
													dailyEarningResponse.getTrips().get(i).getType(), dailyEarningResponse.getTrips().get(i).getStatus(),
													dailyEarningResponse.getTrips().get(i).getExtras()));

										}

										updateListData(getResources().getString(R.string.no_rides), false);
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
								try {
									updateListData(getResources().getString(R.string.error_occured_tap_to_retry), true);
									DialogPopup.dismissLoadingDialog();
								} catch (Exception e1) {
									e1.printStackTrace();
									DialogPopup.dismissLoadingDialog();
								}
							}

						}

						@Override
						public void failure(RetrofitError error) {
							try {
								DialogPopup.dismissLoadingDialog();
								updateListData(getResources().getString(R.string.error_occured_tap_to_retry), true);
							} catch (Exception e) {
								DialogPopup.dismissLoadingDialog();
								e.printStackTrace();
							}
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

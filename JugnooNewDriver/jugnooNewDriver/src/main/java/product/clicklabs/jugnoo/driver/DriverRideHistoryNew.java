package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import product.clicklabs.jugnoo.driver.adapters.DriverRideHistoryAdapter;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.datastructure.RideHistoryItem;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.DailyEarningResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.Tile;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class DriverRideHistoryNew extends BaseFragmentActivity {

	RelativeLayout linear;

	ImageView backBtn;
	TextView title;
	String date = "";
	TextView textViewInfoDisplay;
	LinearLayout linearLayoutNoItems;
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_daily_earning);


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

		backBtn = (ImageView) findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Fonts.mavenRegular(this));
//		textShader=new LinearGradient(0, 0, 0, 20,
//				new int[]{getResources().getColor(R.color.gradient_orange_v2), getResources().getColor(R.color.gradient_yellow_v2)},
//				new float[]{0, 1}, Shader.TileMode.CLAMP);
//		title.getPaint().setShader(textShader);

		title.setText(getResources().getString(R.string.ride_history_cap));
		linearLayoutNoItems = findViewById(R.id.linearLayoutNoItems);
		textViewInfoDisplay = (TextView) findViewById(R.id.textViewInfoDisplay);
		textViewInfoDisplay.setTypeface(Fonts.mavenRegular(this));
		linearLayoutNoItems.setVisibility(View.GONE);
		totalRides =0;
		textViewInfoDisplay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				linearLayoutNoItems.setVisibility(View.GONE);
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
			public void onRideClick(int position, Tile.Extras extras) {
				Intent intent = new Intent(DriverRideHistoryNew.this, RideDetailsNewActivity.class);
				Gson gson = new Gson();
				intent.putExtra("extras", gson.toJson(extras, Tile.Extras.class));
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
			if(rideHistoryItems.size() > 0) {
				DialogPopup.alertPopup(DriverRideHistoryNew.this,"",message);
			} else {
				textViewInfoDisplay.setText(message);
				linearLayoutNoItems.setVisibility(View.VISIBLE);
				driverRideHistoryAdapter.notifyDataSetChanged();
			}
		} else {
			if(rideHistoryItems.size() > 0) {
				driverRideHistoryAdapter.setList(rideHistoryItems, totalRides);
			} else {
				textViewInfoDisplay.setText(getResources().getString(R.string.no_rides_currently));
				linearLayoutNoItems.setVisibility(View.VISIBLE);
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
			HomeUtil.putDefaultParams(params);

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
											HomeActivity.logoutUser(activity, null);
										} else {
											updateListData(getResources().getString(R.string.error_occured_tap_to_retry), true);
										}

									} else {
										totalRides = jObj.optInt("history_size", 10);
										for (int i = 0; i < dailyEarningResponse.getTrips().size(); i++) {

											rideHistoryItems.add(new RideHistoryItem(dailyEarningResponse.getTrips().get(i).getDate(),
													dailyEarningResponse.getTrips().get(i).getTime(), dailyEarningResponse.getTrips().get(i).getEarning(),
													dailyEarningResponse.getTrips().get(i).getType(), dailyEarningResponse.getTrips().get(i).getStatus(),
													dailyEarningResponse.getTrips().get(i).getExtras(),dailyEarningResponse.getTrips().get(i).getCurrencyUnit(),
													dailyEarningResponse.getTrips().get(i).getCollectCash()));

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

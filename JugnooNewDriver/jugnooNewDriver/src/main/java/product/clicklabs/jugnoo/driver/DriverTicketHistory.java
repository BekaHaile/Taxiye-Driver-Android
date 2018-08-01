package product.clicklabs.jugnoo.driver;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.driver.adapters.DriverTicketHistoryAdapter;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.TicketResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.PermissionCommon;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class DriverTicketHistory extends BaseFragmentActivity {

	RelativeLayout linear;

	ImageView backBtn;
	TextView title;
	TextView textViewInfoDisplay, textViewCall;
	ArrayList<TicketResponse.TicketDatum> ticketHistoryItems = new ArrayList<>();
	RecyclerView recyclerViewTicketInfo;
	RelativeLayout relativeLayoutCall1;
	DriverTicketHistoryAdapter driverTicketHistoryAdapter;
	int totalRides = 0;
	public ASSL assl;

	private PermissionCommon mPermissionCommon;


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
		setContentView(R.layout.activity_daily_details_new);


		linear = (RelativeLayout) findViewById(R.id.linear);
		assl = new ASSL(DriverTicketHistory.this, linear, 1134, 720, false);

		backBtn = (ImageView) findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Fonts.mavenRegular(this));
		title.setText(getResources().getString(R.string.support));

		textViewInfoDisplay = (TextView) findViewById(R.id.textViewInfoDisplay);
		textViewInfoDisplay.setTypeface(Fonts.mavenRegular(this));
		textViewCall = (TextView) findViewById(R.id.textViewCall);
		textViewCall.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);

		textViewInfoDisplay.setVisibility(View.GONE);
		totalRides =0;
		textViewInfoDisplay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				textViewInfoDisplay.setVisibility(View.GONE);
				getTicketsAsync(DriverTicketHistory.this, true);
			}
		});

		recyclerViewTicketInfo = (RecyclerView) findViewById(R.id.recyclerViewDailyInfo);
		recyclerViewTicketInfo.setHasFixedSize(true);
		LinearLayoutManager llm = new LinearLayoutManager(this);
		llm.setOrientation(LinearLayoutManager.VERTICAL);
		recyclerViewTicketInfo.setLayoutManager(llm);
		recyclerViewTicketInfo.setItemAnimator(new DefaultItemAnimator());
		relativeLayoutCall1 = (RelativeLayout) findViewById(R.id.relativeLayoutCall1);
		relativeLayoutCall1.setVisibility(View.VISIBLE);

		ticketHistoryItems = new ArrayList<>();
		driverTicketHistoryAdapter = new DriverTicketHistoryAdapter(DriverTicketHistory.this, ticketHistoryItems, totalRides, new DriverTicketHistoryAdapter.Callback() {
			@Override
			public void onTicketClick(int position, TicketResponse.TicketDatum extras) {
				Intent intent = new Intent(DriverTicketHistory.this, DriverTicketDetails.class);
				Gson gson = new Gson();
				intent.putExtra("extras", gson.toJson(extras, TicketResponse.TicketDatum.class));
				DriverTicketHistory.this.startActivity(intent);
				DriverTicketHistory.this.overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
			@Override
			public void onShowMoreClick() {
				getTicketsAsync(DriverTicketHistory.this, false);
			}
		});
		recyclerViewTicketInfo.setAdapter(driverTicketHistoryAdapter);
		relativeLayoutCall1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mPermissionCommon == null ) {
					mPermissionCommon = new PermissionCommon(DriverTicketHistory.this);
				}
					mPermissionCommon.setCallback(new PermissionCommon.PermissionListener() {
						@SuppressLint("MissingPermission")
						@Override
						public void permissionGranted(final int requestCode) {
							Utils.makeCallIntent(DriverTicketHistory.this, Data.userData.driverSupportNumber);
						}

						@Override
						public boolean permissionDenied(final int requestCode, boolean neverAsk) {
							return true;
						}

						@Override
						public void onRationalRequestIntercepted() {

						}
					}).getPermission(PermissionCommon.REQUEST_CODE_CALL_PHONE, Manifest.permission.CALL_PHONE);

				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});

		getTicketsAsync(this, true);
		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});

	}

	public void updateListData(String message, boolean errorOccurred) {
		if (errorOccurred) {
			if(ticketHistoryItems.size() > 0) {
				DialogPopup.alertPopup(DriverTicketHistory.this,"",message);
			} else {
				textViewInfoDisplay.setText(message);
				textViewInfoDisplay.setVisibility(View.VISIBLE);
				driverTicketHistoryAdapter.notifyDataSetChanged();
			}
		} else {
			if(ticketHistoryItems.size() > 0) {
				driverTicketHistoryAdapter.setList(ticketHistoryItems, totalRides);
			} else {
				textViewInfoDisplay.setText(getResources().getString(R.string.no_tickets));
				textViewInfoDisplay.setVisibility(View.VISIBLE);
				driverTicketHistoryAdapter.notifyDataSetChanged();
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

	private void getTicketsAsync(final Activity activity, boolean refresh) {
		try {
			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
			DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));
			HashMap<String, String> params = new HashMap<>();
			params.put("access_token", Data.userData.accessToken);
			params.put("start_from", "" + ticketHistoryItems.size());
				HomeUtil.putDefaultParams(params);

			if(refresh){
				ticketHistoryItems.clear();
			}

			RestClient.getApiServices().getDriverTicketsAsync(params, new Callback<TicketResponse>() {
						@Override
						public void success(TicketResponse ticketResponse, Response response) {
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
										totalRides = jObj.optInt("history_size", 0);
										ticketHistoryItems.addAll(ticketResponse.getTicketData());

										updateListData(getResources().getString(R.string.no_tickets), false);
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
			} else {
				DialogPopup.alertPopup(DriverTicketHistory.this, "", Data.CHECK_INTERNET_MSG);
				updateListData(getResources().getString(R.string.error_occured_tap_to_retry), true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (mPermissionCommon != null) {
			mPermissionCommon.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

}







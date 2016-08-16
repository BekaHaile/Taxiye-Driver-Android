package product.clicklabs.jugnoo.driver.home;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.apis.ApiGoogleGeocodeAddress;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.datastructure.DriverScreenMode;
import product.clicklabs.jugnoo.driver.home.adapters.CustomerInfoAdapter;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.MapUtils;
import product.clicklabs.jugnoo.driver.utils.NudgeClient;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by aneeshbansal on 28/05/16.
 */
public class CustomerSwitcher {

	private HomeActivity activity;

	private RecyclerView recyclerViewCustomersLinked;

	private TextView textViewCustomerName, textViewCustomerPickupAddress, textViewDeliveryCount,
			textViewDeliveryFare, textViewShowDistance;
	private RelativeLayout relativeLayoutCall;
	private LinearLayout linearLayoutDeliveryFare;

	private CustomerInfoAdapter customerInfoAdapter;
	double distanceRefreshTime = 0;

	public CustomerSwitcher(HomeActivity activity, View rootView) {
		this.activity = activity;
		init(rootView);
	}

	public void init(View rootView) {

		textViewCustomerName = (TextView) rootView.findViewById(R.id.textViewCustomerName);
		textViewCustomerName.setTypeface(Fonts.mavenRegular(activity));
		textViewCustomerPickupAddress = (TextView) rootView.findViewById(R.id.textViewCustomerPickupAddress);
		textViewCustomerPickupAddress.setTypeface(Fonts.mavenRegular(activity));
		textViewDeliveryCount = (TextView) rootView.findViewById(R.id.textViewDeliveryCount);
		textViewDeliveryCount.setTypeface(Fonts.mavenRegular(activity));
		textViewDeliveryFare = (TextView) rootView.findViewById(R.id.textViewDeliveryFare);
		textViewDeliveryFare.setTypeface(Fonts.mavenRegular(activity));
		textViewShowDistance = (TextView) rootView.findViewById(R.id.textViewShowDistance);
		textViewShowDistance.setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewDeliveryApprox)).setTypeface(Fonts.mavenRegular(activity));

		relativeLayoutCall = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutCall);
		linearLayoutDeliveryFare = (LinearLayout) rootView.findViewById(R.id.linearLayoutDeliveryFare);

		recyclerViewCustomersLinked = (RecyclerView) rootView.findViewById(R.id.recyclerViewCustomersLinked);
		recyclerViewCustomersLinked.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
		recyclerViewCustomersLinked.setItemAnimator(new DefaultItemAnimator());
		recyclerViewCustomersLinked.setHasFixedSize(false);


		customerInfoAdapter = new CustomerInfoAdapter(activity, new CustomerInfoAdapter.Callback() {
			@Override
			public void onClick(int position, CustomerInfo customerInfo) {
				Data.setCurrentEngagementId(String.valueOf(customerInfo.getEngagementId()));
				activity.switchDriverScreen(HomeActivity.driverScreenMode);
			}

		});


		recyclerViewCustomersLinked.setAdapter(customerInfoAdapter);

		relativeLayoutCall.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String callPhoneNumber = "";
				CustomerInfo customerInfo = Data.getCurrentCustomerInfo();
				if (customerInfo != null) {
					callPhoneNumber = customerInfo.phoneNumber;
				}
				if (!"".equalsIgnoreCase(callPhoneNumber)) {
					Utils.openCallIntent(activity, callPhoneNumber);
					try {
						JSONObject map = new JSONObject();
						map.put(Constants.KEY_CUSTOMER_PHONE_NO, callPhoneNumber);
						map.put(Constants.KEY_ENGAGEMENT_ID, customerInfo.getEngagementId());
						map.put(Constants.KEY_CUSTOMER_ID, customerInfo.getUserId());
						NudgeClient.trackEvent(activity, FlurryEventNames.NUDGE_CALL_USER, map);
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (DriverScreenMode.D_ARRIVED == HomeActivity.driverScreenMode) {
						FlurryEventLogger.event(FlurryEventNames.CALLED_CUSTOMER);
					} else if (DriverScreenMode.D_START_RIDE == HomeActivity.driverScreenMode) {
						FlurryEventLogger.event(FlurryEventNames.CALL_CUSTOMER_AFTER_ARRIVING);
					} else if (DriverScreenMode.D_IN_RIDE == HomeActivity.driverScreenMode) {
						FlurryEventLogger.event(FlurryEventNames.CUSTOMER_CALLED_WHEN_RIDE_IN_PROGRESS);
					}
				} else {
					Toast.makeText(activity, activity.getResources().getString(R.string.some_error_occured), Toast.LENGTH_SHORT).show();
				}
			}
		});

	}


	public void setCustomerData(int engagementId) {
		try {
			CustomerInfo customerInfo = Data.getCurrentCustomerInfo();
			if (engagementId == customerInfo.getEngagementId()) {
				Utils.setDrawableColor(relativeLayoutCall, customerInfo.getColor(),
						activity.getResources().getColor(R.color.new_orange));

				textViewCustomerName.setText(customerInfo.getName());
				if (DriverScreenMode.D_IN_RIDE == HomeActivity.driverScreenMode) {
					if (customerInfo.getIsDelivery() != 1
							&& customerInfo.getDropLatLng() != null) {
						textViewCustomerPickupAddress.setVisibility(View.VISIBLE);
						new ApiGoogleGeocodeAddress(activity, customerInfo.getDropLatLng(), true,
								new CustomGoogleGeocodeCallback(customerInfo.getEngagementId(),
										textViewCustomerPickupAddress)).execute();
						activity.buttonDriverNavigationSetVisibility(View.VISIBLE);
					} else {
						textViewCustomerPickupAddress.setVisibility(View.GONE);
						activity.buttonDriverNavigationSetVisibility(View.GONE);
					}
					updateDistanceOnLocationChanged();
					textViewDeliveryCount.setVisibility(View.GONE);
					linearLayoutDeliveryFare.setVisibility(View.GONE);

				} else {
					textViewCustomerPickupAddress.setVisibility(View.VISIBLE);
					activity.buttonDriverNavigationSetVisibility(View.VISIBLE);
					if (customerInfo.getAddress().equalsIgnoreCase("")) {
						new ApiGoogleGeocodeAddress(activity, customerInfo.getRequestlLatLng(), true,
								new CustomGoogleGeocodeCallback(customerInfo.getEngagementId(),
										textViewCustomerPickupAddress)).execute();
					} else {
						textViewCustomerPickupAddress.setText(customerInfo.getAddress());
					}
					updateDistanceOnLocationChanged();
					if (customerInfo.getIsDelivery() == 1) {
						textViewDeliveryCount.setVisibility(View.VISIBLE);
						textViewDeliveryCount.setText(activity.getResources().getString(R.string.delivery_numbers)
								+ " " + customerInfo.getTotalDeliveries());
						linearLayoutDeliveryFare.setVisibility(View.VISIBLE);
						textViewDeliveryFare.setText(activity.getResources().getString(R.string.fare)
								+ ": " + activity.getResources().getString(R.string.rupee)
								+ " " + customerInfo.getEstimatedFare());
					} else {
						textViewDeliveryCount.setVisibility(View.GONE);
						linearLayoutDeliveryFare.setVisibility(View.GONE);
					}
				}
			}
			if (Data.getAssignedCustomerInfosListForEngagedStatus().size() == 1) {
				recyclerViewCustomersLinked.setVisibility(View.GONE);
				textViewCustomerName.setVisibility(View.VISIBLE);
			} else {
				recyclerViewCustomersLinked.setVisibility(View.VISIBLE);
				textViewCustomerName.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateDistanceOnLocationChanged() {
		try {
			textViewShowDistance.setVisibility(View.GONE);
			if (DriverScreenMode.D_ARRIVED == HomeActivity.driverScreenMode) {
				textViewShowDistance.setVisibility(View.VISIBLE);
				if (System.currentTimeMillis() - distanceRefreshTime > 60000
						&& HomeActivity.myLocation != null) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								Response responseR = RestClient.getDistanceApiServices().getDistance(HomeActivity.myLocation.getLongitude()
										+ "," + HomeActivity.myLocation.getLatitude() + ";" + Data.getCurrentCustomerInfo().getRequestlLatLng().longitude
										+ "," + Data.getCurrentCustomerInfo().getRequestlLatLng().latitude);

								String response = new String(((TypedByteArray) responseR.getBody()).getBytes());

								try {
									JSONObject jsonObject = new JSONObject(response);
									String status = jsonObject.getString("code");
									if ("OK".equalsIgnoreCase(status)) {
										JSONObject element0 = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0);
										double distance = element0.optDouble("distance", 0);
										final double finalDistance = distance;

										activity.runOnUiThread(new Runnable() {
											@Override
											public void run() {
												if (finalDistance > 0) {
													distanceRefreshTime = System.currentTimeMillis();
													textViewShowDistance.setText(Utils.getDecimalFormatForMoney()
															.format(finalDistance / 1000d)
															+ " " + activity.getResources().getString(R.string.km_away));
												} else {
													textViewShowDistance.setText(Utils.getDecimalFormatForMoney()
															.format(MapUtils.distance(Data.getCurrentCustomerInfo().getRequestlLatLng(),
																	new LatLng(HomeActivity.myLocation.getLatitude(), HomeActivity.myLocation.getLongitude())) / 1000d)
															+ " " + activity.getResources().getString(R.string.km_away));
												}
											}
										});
									}
								} catch (Exception e) {
									e.printStackTrace();
									textViewShowDistance.setText(Utils.getDecimalFormatForMoney()
											.format(MapUtils.distance(Data.getCurrentCustomerInfo().getRequestlLatLng(),
													new LatLng(HomeActivity.myLocation.getLatitude(), HomeActivity.myLocation.getLongitude())) / 1000d)
											+ " " + activity.getResources().getString(R.string.km_away));
								}
							} catch (Exception e) {
								e.printStackTrace();
								textViewShowDistance.setText(Utils.getDecimalFormatForMoney()
										.format(MapUtils.distance(Data.getCurrentCustomerInfo().getRequestlLatLng(),
												new LatLng(HomeActivity.myLocation.getLatitude(), HomeActivity.myLocation.getLongitude())) / 1000d)
										+ " " + activity.getResources().getString(R.string.km_away));
							}

						}
					}).start();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}


	}


	class CustomGoogleGeocodeCallback implements ApiGoogleGeocodeAddress.Callback {

		private int engagementId;
		private TextView textView;

		public CustomGoogleGeocodeCallback(int engagementId, TextView textView) {
			this.engagementId = engagementId;
			this.textView = textView;
		}

		@Override
		public void onPre() {
			textView.setText("");
		}

		@Override
		public void onPost(String address) {
			try {
				Data.getCustomerInfo(String.valueOf(engagementId)).setAddress(address);
				textView.setText(address);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public void updateList() {
		customerInfoAdapter.notifyList();
	}
}

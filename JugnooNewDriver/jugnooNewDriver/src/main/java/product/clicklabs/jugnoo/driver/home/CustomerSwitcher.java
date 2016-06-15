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

import org.json.JSONObject;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.apis.ApiGoogleGeocodeAddress;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.datastructure.DriverScreenMode;
import product.clicklabs.jugnoo.driver.home.adapters.CustomerInfoAdapter;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.MapUtils;
import product.clicklabs.jugnoo.driver.utils.NudgeClient;
import product.clicklabs.jugnoo.driver.utils.Utils;

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

	public CustomerSwitcher(HomeActivity activity, View rootView){
		this.activity = activity;
		init(rootView);
	}

	public void init(View rootView){

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
		((TextView)rootView.findViewById(R.id.textViewDeliveryApprox)).setTypeface(Fonts.mavenRegular(activity));

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
				if (Data.getCurrentCustomerInfo() != null) {
					callPhoneNumber = Data.getCurrentCustomerInfo().phoneNumber;
				}
				if (!"".equalsIgnoreCase(callPhoneNumber)) {
					Utils.openCallIntent(activity, callPhoneNumber);
					try {
						JSONObject map = new JSONObject();
						map.put(Constants.KEY_CUSTOMER_PHONE_NO, callPhoneNumber);
						map.put(Constants.KEY_ENGAGEMENT_ID, Data.getCurrentEngagementId());
						map.put(Constants.KEY_CUSTOMER_ID, Data.getCurrentCustomerInfo().userId);
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
			if(engagementId == customerInfo.getEngagementId()) {
				Utils.setDrawableColor(relativeLayoutCall, Data.getCurrentCustomerInfo().getColor(),
						activity.getResources().getColor(R.color.new_orange));

				textViewCustomerName.setText(customerInfo.getName());
				if (DriverScreenMode.D_IN_RIDE == HomeActivity.driverScreenMode) {
					if (customerInfo.getDropLatLng() != null
							&& customerInfo.getIsDelivery() != 1) {
						textViewCustomerPickupAddress.setVisibility(View.VISIBLE);
						new ApiGoogleGeocodeAddress(activity, customerInfo.getDropLatLng(), true,
								callbackGetAddress).execute();
					} else {
						textViewCustomerPickupAddress.setVisibility(View.GONE);
						activity.buttonDriverNavigationSetVisibility(View.GONE);
					}
					updateDistanceOnLocationChanged();
					textViewDeliveryCount.setVisibility(View.GONE);
					linearLayoutDeliveryFare.setVisibility(View.GONE);

				} else {
					textViewCustomerPickupAddress.setVisibility(View.VISIBLE);
					activity.buttonDriverNavigationSetVisibility(View.GONE);
					if (customerInfo.getAddress().equalsIgnoreCase("")) {
						new ApiGoogleGeocodeAddress(activity, customerInfo.getRequestlLatLng(), true,
								callbackGetAddress).execute();
					} else {
						textViewCustomerPickupAddress.setText(customerInfo.getAddress());
						activity.buttonDriverNavigationSetVisibility(View.VISIBLE);
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

	public void updateDistanceOnLocationChanged(){
		try{
			textViewShowDistance.setVisibility(View.GONE);
			if(DriverScreenMode.D_ARRIVED == HomeActivity.driverScreenMode) {
				textViewShowDistance.setVisibility(View.VISIBLE);
				if (HomeActivity.myLocation != null) {
					textViewShowDistance.setText(Utils.getDecimalFormatForMoney()
							.format(MapUtils.distance(Data.getCurrentCustomerInfo().getRequestlLatLng(),
									new LatLng(HomeActivity.myLocation.getLatitude(), HomeActivity.myLocation.getLongitude())) / 1000d)
							+ " " + activity.getResources().getString(R.string.km_away));
				}
			}
		} catch(Exception e){
		}
	}


	private ApiGoogleGeocodeAddress.Callback callbackGetAddress = new ApiGoogleGeocodeAddress.Callback() {
		@Override
		public void onPre() {
			textViewCustomerPickupAddress.setText("");
		}

		@Override
		public void onPost(String address) {
			Data.getCurrentCustomerInfo().setAddress(address);
			textViewCustomerPickupAddress.setText(address);
			activity.buttonDriverNavigationSetVisibility(View.VISIBLE);
		}
	};


	public void updateList(){
		customerInfoAdapter.notifyList();
	}
}

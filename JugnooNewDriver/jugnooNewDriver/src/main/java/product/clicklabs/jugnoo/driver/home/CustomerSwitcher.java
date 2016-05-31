package product.clicklabs.jugnoo.driver.home;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
import product.clicklabs.jugnoo.driver.datastructure.EngagementStatus;
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

	private TextView textViewCustomerPickupAddress, textViewShowDistance, textViewRideStatus,
			driverPassengerCallText, textViewRideInstructions;
	private RelativeLayout driverPassengerCallRl, relativeLayoutShowDistance;

	private CustomerInfoAdapter customerInfoAdapter;

	public CustomerSwitcher(HomeActivity activity, View rootView){
		this.activity = activity;
		init(rootView);
	}

	public void init(View rootView){

		textViewCustomerPickupAddress = (TextView) rootView.findViewById(R.id.textViewCustomerPickupAddress);
		textViewCustomerPickupAddress.setTypeface(Fonts.mavenRegular(activity));
		textViewShowDistance = (TextView) rootView.findViewById(R.id.textViewShowDistance);
		textViewShowDistance.setTypeface(Fonts.mavenRegular(activity));
		textViewRideStatus = (TextView) rootView.findViewById(R.id.textViewRideStatus);
		textViewRideStatus.setTypeface(Fonts.mavenRegular(activity));
		driverPassengerCallText = (TextView) rootView.findViewById(R.id.driverPassengerCallText);
		driverPassengerCallText.setTypeface(Fonts.mavenRegular(activity));
		textViewRideInstructions = (TextView) rootView.findViewById(R.id.textViewRideInstructions);
		textViewRideInstructions.setTypeface(Fonts.mavenRegular(activity));

		driverPassengerCallRl = (RelativeLayout) rootView.findViewById(R.id.driverPassengerCallRl);
		relativeLayoutShowDistance = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutShowDistance);

		recyclerViewCustomersLinked = (RecyclerView) rootView.findViewById(R.id.recyclerViewCustomersLinked);
		recyclerViewCustomersLinked.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
		recyclerViewCustomersLinked.setItemAnimator(new DefaultItemAnimator());
		recyclerViewCustomersLinked.setHasFixedSize(false);


		customerInfoAdapter = new CustomerInfoAdapter(activity, new CustomerInfoAdapter.Callback() {
			@Override
			public void onClick(int position, CustomerInfo customerInfo) {
				Data.setCurrentEngagementId(String.valueOf(customerInfo.getEngagementId()));
				activity.switchDriverScreen(HomeActivity.driverScreenMode);
				setCustomerData();
			}

		});


		recyclerViewCustomersLinked.setAdapter(customerInfoAdapter);

		driverPassengerCallRl.setOnClickListener(new View.OnClickListener() {

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


	public void setCustomerData() {
		try {
			CustomerInfo customerInfo = Data.getCurrentCustomerInfo();
			if (DriverScreenMode.D_IN_RIDE == HomeActivity.driverScreenMode) {
				textViewCustomerPickupAddress.setVisibility(View.VISIBLE);
				textViewCustomerPickupAddress.setText("");

				if(customerInfo.getDropLatLng() != null
						&& customerInfo.getIsDelivery() != 1){
					new ApiGoogleGeocodeAddress(activity, customerInfo.getRequestlLatLng(), true,
							callbackGetAddress).execute();
				} else{
					textViewCustomerPickupAddress.setText("");
				}
				textViewShowDistance.setText("");
				relativeLayoutShowDistance.setVisibility(View.GONE);

			} else {
				new ApiGoogleGeocodeAddress(activity, customerInfo.getRequestlLatLng(), true,
						callbackGetAddress).execute();
				textViewShowDistance.setText(Utils.getDecimalFormatForMoney()
						.format(MapUtils.distance(customerInfo.getRequestlLatLng(),
								new LatLng(HomeActivity.myLocation.getLatitude(), HomeActivity.myLocation.getLongitude())))
						+ " " + activity.getResources().getString(R.string.km));
				relativeLayoutShowDistance.setVisibility(View.VISIBLE);
			}
			if(customerInfo.getStatus() == EngagementStatus.ACCEPTED.getOrdinal()){
				textViewRideStatus.setText(activity.getResources().getString(R.string.accepted));
				textViewRideInstructions.setText(activity.getResources().getString(R.string.arrive_at_pickup_location));
			}
			else if(customerInfo.getStatus() == EngagementStatus.ARRIVED.getOrdinal()){
				textViewRideStatus.setText(activity.getResources().getString(R.string.arrived));
				textViewRideInstructions.setText(activity.getResources().getString(R.string.arrived));
			}
			else if(customerInfo.getStatus() == EngagementStatus.STARTED.getOrdinal()){
				textViewRideStatus.setText(activity.getResources().getString(R.string.in_ride));
				textViewRideInstructions.setText(activity.getResources().getString(R.string.in_ride));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ApiGoogleGeocodeAddress.Callback callbackGetAddress = new ApiGoogleGeocodeAddress.Callback() {
		@Override
		public void onPre() {
			textViewCustomerPickupAddress.setText("");
		}

		@Override
		public void onPost(String address) {
			textViewCustomerPickupAddress.setText(address);
		}
	};


	public void updateList(){
		customerInfoAdapter.notifyList();
	}
}

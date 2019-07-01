package product.clicklabs.jugnoo.driver.home;

import android.graphics.Typeface;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import product.clicklabs.jugnoo.driver.MyApplication;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.apis.ApiGoogleGeocodeAddress;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.datastructure.DriverScreenMode;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.datastructure.UserData;
import product.clicklabs.jugnoo.driver.home.adapters.CustomerInfoAdapter;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.utils.FirebaseEvents;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.MapUtils;
import product.clicklabs.jugnoo.driver.utils.NotesDialog;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by aneeshbansal on 28/05/16.
 */
public class CustomerSwitcher {

	private HomeActivity activity;

	private RecyclerView recyclerViewCustomersLinked;

	private TextView textViewCustomerName1, textViewCustomerName, textViewCustomerPickupAddress, textViewDeliveryCount,
			textViewShowDistance, textViewCustomerCashRequired, textViewPickupFrm, tvCustomerNotes, tvRentalRideInfo;
	private RelativeLayout relativeLayoutCall, relativeLayoutCustomerInfo, relativeLayoutCall1;

	private LinearLayout llRentalRequest;
	private CustomerInfoAdapter customerInfoAdapter;
	double distanceRefreshTime = 0;
	String dropAddress;

	public CustomerSwitcher(HomeActivity activity, View rootView) {
		this.activity = activity;
		init(rootView);
	}

	public void init(View rootView) {

		textViewCustomerName1 = (TextView) rootView.findViewById(R.id.textViewCustomerName1);
		textViewCustomerName1.setTypeface(Fonts.mavenRegular(activity));
		tvCustomerNotes = (TextView) rootView.findViewById(R.id.tvCustomerNotes);
		textViewPickupFrm = (TextView) rootView.findViewById(R.id.textViewPickupFrm);
		textViewPickupFrm.setTypeface(Fonts.mavenRegular(activity));
		textViewCustomerName = (TextView) rootView.findViewById(R.id.textViewCustomerName);
		textViewCustomerName.setTypeface(Fonts.mavenRegular(activity));
		textViewCustomerPickupAddress = (TextView) rootView.findViewById(R.id.textViewCustomerPickupAddress);
		textViewCustomerPickupAddress.setTypeface(Fonts.mavenRegular(activity));
		tvRentalRideInfo = (TextView) rootView.findViewById(R.id.tvRentalRideInfo);
		tvRentalRideInfo.setTypeface(Fonts.mavenRegular(activity));
		llRentalRequest = rootView.findViewById(R.id.llRentalRequest);
		textViewCustomerCashRequired = (TextView) rootView.findViewById(R.id.textViewCustomerCashRequired);
		textViewCustomerCashRequired.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
		textViewDeliveryCount = (TextView) rootView.findViewById(R.id.textViewDeliveryCount);
		textViewDeliveryCount.setTypeface(Fonts.mavenRegular(activity));
		textViewShowDistance = (TextView) rootView.findViewById(R.id.textViewShowDistance);
		textViewShowDistance.setTypeface(Fonts.mavenRegular(activity));

		relativeLayoutCall = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutCall);
		relativeLayoutCall1 = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutCall1);
		relativeLayoutCustomerInfo = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutCustomerInfo);

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

			@Override
			public void onCancelClick(int position, CustomerInfo customerInfo) {

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
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (DriverScreenMode.D_ARRIVED == HomeActivity.driverScreenMode) {
						FlurryEventLogger.event(FlurryEventNames.CALLED_CUSTOMER);
						MyApplication.getInstance().logEvent(FirebaseEvents.RIDE_ACCEPTED + "_" + FirebaseEvents.CALL_CUSTOMER, null);
					} else if (DriverScreenMode.D_START_RIDE == HomeActivity.driverScreenMode) {
						FlurryEventLogger.event(FlurryEventNames.CALL_CUSTOMER_AFTER_ARRIVING);
						MyApplication.getInstance().logEvent(FirebaseEvents.RIDE_ARRIVED + "_" + FirebaseEvents.CALL_CUSTOMER, null);
					} else if (DriverScreenMode.D_IN_RIDE == HomeActivity.driverScreenMode) {
						FlurryEventLogger.event(FlurryEventNames.CUSTOMER_CALLED_WHEN_RIDE_IN_PROGRESS);
						MyApplication.getInstance().logEvent(FirebaseEvents.RIDE_START + "_" + FirebaseEvents.CALL_CUSTOMER, null);
					}
				} else {
					Toast.makeText(activity, activity.getResources().getString(R.string.some_error_occured), Toast.LENGTH_SHORT).show();
				}
			}
		});

		relativeLayoutCall1.setOnClickListener(new View.OnClickListener() {

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
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (DriverScreenMode.D_ARRIVED == HomeActivity.driverScreenMode) {
						FlurryEventLogger.event(FlurryEventNames.CALLED_CUSTOMER);
						MyApplication.getInstance().logEvent(FirebaseEvents.RIDE_ACCEPTED + "_" + FirebaseEvents.CALL_CUSTOMER, null);
					} else if (DriverScreenMode.D_START_RIDE == HomeActivity.driverScreenMode) {
						FlurryEventLogger.event(FlurryEventNames.CALL_CUSTOMER_AFTER_ARRIVING);
						MyApplication.getInstance().logEvent(FirebaseEvents.RIDE_ARRIVED + "_" + FirebaseEvents.CALL_CUSTOMER, null);
					} else if (DriverScreenMode.D_IN_RIDE == HomeActivity.driverScreenMode) {
						FlurryEventLogger.event(FlurryEventNames.CUSTOMER_CALLED_WHEN_RIDE_IN_PROGRESS);
						MyApplication.getInstance().logEvent(FirebaseEvents.RIDE_START + "_" + FirebaseEvents.CALL_CUSTOMER, null);
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
						activity.getResources().getColor(R.color.themeColor));

				textViewCustomerName1.setText(customerInfo.getName().toUpperCase());
				textViewCustomerName.setText(customerInfo.getName());
				if (DriverScreenMode.D_IN_RIDE == HomeActivity.driverScreenMode) {
					if (customerInfo.getIsDelivery() != 1
							&& customerInfo.getDropLatLng() != null) {
						if(customerInfo.getRentalInfo() != null && !customerInfo.getRentalInfo().isEmpty()){
							tvRentalRideInfo.setVisibility(View.VISIBLE);
							if(llRentalRequest != null) {
								llRentalRequest.setVisibility(View.VISIBLE);
							}
							tvRentalRideInfo.setText(customerInfo.getRentalInfo());
						}else {
							if(llRentalRequest != null) {
								llRentalRequest.setVisibility(View.GONE);
							}
							tvRentalRideInfo.setVisibility(View.GONE);
						}
						activity.buttonDriverNavigationSetVisibility(View.VISIBLE);
						textViewCustomerPickupAddress.setVisibility(View.VISIBLE);
						if(customerInfo.getDropAddress().equalsIgnoreCase("")){
							new ApiGoogleGeocodeAddress(activity, customerInfo.getDropLatLng(), true,
									new CustomGoogleGeocodeCallback(customerInfo.getEngagementId(),
											activity.getTextViewEnterDestination(), null, true)).execute();
							activity.getTextViewEnterDestination().setText(customerInfo.getDropAddressEng());
						}else {
							textViewCustomerPickupAddress.setText(customerInfo.getDropAddress());
							activity.setDropAddressToTextView(customerInfo.getDropAddress());
							dropAddress = customerInfo.getDropAddress();
						}
						if(customerInfo.getIsPooled() != 1){
							textViewCustomerPickupAddress.setVisibility(View.GONE);
							activity.setDropAddressToTextView(dropAddress);
						}
					} else if(customerInfo.getIsDelivery() == 1){
						textViewCustomerPickupAddress.setVisibility(View.GONE);
					} else {
						textViewCustomerPickupAddress.setVisibility(View.GONE);
						activity.buttonDriverNavigationSetVisibility(View.GONE);
					}
					updateDistanceOnLocationChanged();
					textViewDeliveryCount.setVisibility(View.GONE);

				} else {
					textViewCustomerPickupAddress.setVisibility(View.VISIBLE);
					if(!TextUtils.isEmpty(customerInfo.getCustomerNotes())){
						tvCustomerNotes.setVisibility(View.VISIBLE);
						if(customerInfo.getCustomerNotes().length() > 20) {
							tvCustomerNotes.setText(R.string.click_to_view_notes);
							tvCustomerNotes.setEnabled(true);
						} else {
							tvCustomerNotes.setText(activity.getString(R.string.note)+": "+customerInfo.getCustomerNotes());
							tvCustomerNotes.setEnabled(false);
						}
					} else {
						tvCustomerNotes.setVisibility(View.GONE);
						tvCustomerNotes.setText("");

					}
					if(DriverScreenMode.D_START_RIDE != HomeActivity.driverScreenMode) {
						activity.buttonDriverNavigationSetVisibility(View.VISIBLE);
					}
					if (customerInfo.getAddress().equalsIgnoreCase("")) {
						new ApiGoogleGeocodeAddress(activity, customerInfo.getRequestlLatLng(), true,
								new CustomGoogleGeocodeCallback(customerInfo.getEngagementId(),
										textViewCustomerPickupAddress, null, false)).execute();
						textViewCustomerPickupAddress.setText(customerInfo.getPickupAddressEng());
					} else {
						textViewCustomerPickupAddress.setText(customerInfo.getAddress());
					}

					updateDistanceOnLocationChanged();
					if (customerInfo.getIsDelivery() == 1 && customerInfo.getIsDeliveryPool() != 1) {
						textViewDeliveryCount.setVisibility(View.VISIBLE);
						textViewDeliveryCount.setText(activity.getResources().getString(R.string.deliveries)
								+ " " + customerInfo.getTotalDeliveries());
						textViewCustomerCashRequired.setVisibility(View.VISIBLE);
						textViewCustomerCashRequired.setText(activity.getResources().getString(R.string.cash_to_collected)
								+ ": " + activity.getResources().getString(R.string.rupee)
								+ "" + customerInfo.getCashOnDelivery());
					} else {
						textViewDeliveryCount.setVisibility(View.GONE);
						textViewCustomerCashRequired.setVisibility(View.GONE);
						if(customerInfo.getRentalInfo() != null && !customerInfo.getRentalInfo().isEmpty()){
							tvRentalRideInfo.setVisibility(View.VISIBLE);
							if(llRentalRequest != null) {
								llRentalRequest.setVisibility(View.VISIBLE);
							}
							tvRentalRideInfo.setText(customerInfo.getRentalInfo());
						}else {
							if(llRentalRequest != null) {
								llRentalRequest.setVisibility(View.GONE);
							}
							tvRentalRideInfo.setVisibility(View.GONE);
						}
					}
					if(customerInfo.getDropLatLng() != null) {
						activity.bDropAddressToggle.setVisibility(Prefs.with(activity).getInt(Constants.KEY_SHOW_DROP_ADDRESS_BEFORE_INRIDE, 1) == 0
								? View.GONE : View.VISIBLE);
						if(activity.bDropAddressToggle.getVisibility() == View.VISIBLE) {
							activity.tvDropAddressToggleView.setText(R.string.loading);
							if (customerInfo.getDropAddress().equalsIgnoreCase("")) {
								new ApiGoogleGeocodeAddress(activity, customerInfo.getDropLatLng(), true,
										new CustomGoogleGeocodeCallback(customerInfo.getEngagementId(),
												activity.tvDropAddressToggleView, null, true)).execute();
								activity.tvDropAddressToggleView.setText(customerInfo.getDropAddressEng());
							} else {
								activity.tvDropAddressToggleView.setText(customerInfo.getDropAddress());
							}
						}
					} else {
						activity.bDropAddressToggle.setVisibility(View.GONE);
						activity.tvDropAddressToggleView.setVisibility(View.GONE);
					}
				}
			}
			if (Data.getAssignedCustomerInfosListForEngagedStatus().size() == 1) {
				recyclerViewCustomersLinked.setVisibility(View.GONE);
				textViewCustomerName1.setVisibility(View.VISIBLE);
				textViewCustomerName.setVisibility(View.VISIBLE);
			} else {
				recyclerViewCustomersLinked.setVisibility(View.GONE);
				textViewCustomerName1.setVisibility(View.VISIBLE);
				textViewCustomerName.setVisibility(View.VISIBLE);
			}
			if(DriverScreenMode.D_ARRIVED != HomeActivity.driverScreenMode){
				textViewShowDistance.setText("");
			}

			tvCustomerNotes.setOnClickListener(view -> openNotesDialog(customerInfo.getCustomerNotes()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void openNotesDialog(final String customerNotes) {
		try {
			NotesDialog notesDialog = new NotesDialog(activity, customerNotes, notes -> {
			});
			notesDialog.show(false);
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
					if (Prefs.with(activity).getInt(SPLabels.OSRM_ENABLED, 0) == 1) {
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
													try {
														if (finalDistance > 0) {
															distanceRefreshTime = System.currentTimeMillis();
															textViewShowDistance.setText(Utils.getDecimalFormatForMoney()
																	.format(finalDistance * UserData.getDistanceUnitFactor(activity))+" "
																	+Utils.getDistanceUnit(UserData.getDistanceUnit(activity))+ "\n" + activity.getResources().getString(R.string.away_cap));
														} else {
															textViewShowDistance.setText(Utils.getDecimalFormatForMoney()
																	.format(MapUtils.distance(Data.getCurrentCustomerInfo().getRequestlLatLng(),
																			new LatLng(HomeActivity.myLocation.getLatitude(), HomeActivity.myLocation.getLongitude())) * UserData.getDistanceUnitFactor(activity))
																	+" "+Utils.getDistanceUnit(UserData.getDistanceUnit(activity))+ "\n" + activity.getResources().getString(R.string.away_cap));
														}
													} catch (Exception e) {
														e.printStackTrace();
													}
												}
											});
										}
									} catch (Exception e) {
										e.printStackTrace();
										setCustomerDistance();
									}
								} catch (Exception e) {
									e.printStackTrace();
									setCustomerDistance();
								}
							}
						}).start();
					} else {
						setCustomerDistance();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void setCustomerDistance() {

		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					textViewShowDistance.setText(Utils.getDecimalFormatForMoney()
							.format(MapUtils.distance(Data.getCurrentCustomerInfo().getRequestlLatLng(),
									new LatLng(HomeActivity.myLocation.getLatitude(), HomeActivity.myLocation.getLongitude())) * UserData.getDistanceUnitFactor(activity))
							+" "+Utils.getDistanceUnit(UserData.getDistanceUnit(activity))+ "\n" + activity.getResources().getString(R.string.away_cap));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}



	class CustomGoogleGeocodeCallback implements ApiGoogleGeocodeAddress.Callback {

		private int engagementId;
		private TextView textView, textView1;
		private boolean isDrop;

		public CustomGoogleGeocodeCallback(int engagementId, TextView textView, TextView textView1, boolean isDrop) {
			this.engagementId = engagementId;
			this.textView = textView;
			this.isDrop = isDrop;
			if(textView1 !=null) {
				this.textView1 = textView1;
			}
		}

		@Override
		public void onPre() {
			textView.setText("");
			if(textView1 !=null) {
				textView1.setText("");
			}
		}

		@Override
		public void onPost(String address) {
			try {
				if(isDrop){
					Data.getCustomerInfo(String.valueOf(engagementId)).setDropAddress(activity, address, true);
				} else {
					Data.getCustomerInfo(String.valueOf(engagementId)).setAddress(address);
				}
				textView.setText(address);
				if(textView1 !=null) {
					textView1.setText(address);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public void setCallButton(){
		if(Data.getCurrentCustomerInfo() != null){
			if(Data.getCurrentCustomerInfo().getIsDelivery() ==1 && DriverScreenMode.D_IN_RIDE == HomeActivity.driverScreenMode){
				relativeLayoutCustomerInfo.setVisibility(View.VISIBLE);
			}else {
				relativeLayoutCustomerInfo.setVisibility(View.GONE);
			}
		}
	}

	public void updateList() {
		customerInfoAdapter.notifyList();
	}
}

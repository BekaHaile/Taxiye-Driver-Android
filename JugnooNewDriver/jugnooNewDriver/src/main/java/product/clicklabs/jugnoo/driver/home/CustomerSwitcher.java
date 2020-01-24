package product.clicklabs.jugnoo.driver.home;

import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.DialogReviewImagesFragment;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.MyApplication;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.adapters.ImageWithTextAdapter;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.datastructure.DriverScreenMode;
import product.clicklabs.jugnoo.driver.datastructure.UserData;
import product.clicklabs.jugnoo.driver.google.GAPIAddress;
import product.clicklabs.jugnoo.driver.google.GoogleAPICoroutine;
import product.clicklabs.jugnoo.driver.home.adapters.CustomerInfoAdapter;
import product.clicklabs.jugnoo.driver.utils.FirebaseEvents;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.MapUtils;
import product.clicklabs.jugnoo.driver.utils.NotesDialog;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;

import static product.clicklabs.jugnoo.driver.Constants.KEY_SHOW_DROP_LOCATION_BELOW_PICKUP;
import static product.clicklabs.jugnoo.driver.Constants.KEY_SHOW_FARE_BEFORE_RIDE_START;


/**
 * Created by aneeshbansal on 28/05/16.
 */
public class CustomerSwitcher {

	private HomeActivity activity;

	private TextView textViewCustomerName1, textViewCustomerName, textViewCustomerPickupAddress, textViewCustomerDropAddress, textViewDeliveryCount,
			textViewShowDistance, textViewCustomerCashRequired, textViewPickupFrm, tvCustomerNotes, tvRentalRideInfo;
	private RelativeLayout relativeLayoutCall, relativeLayoutCustomerInfo, relativeLayoutCall1;

	private LinearLayout llRentalRequest;
	private RecyclerView rvPickupFeedImages;
	private CustomerInfoAdapter customerInfoAdapter;
	private TextView tvFeedInstructions;
	private RecyclerView recyclerViewCustomersLinked;


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
		textViewCustomerDropAddress = (TextView) rootView.findViewById(R.id.textViewCustomerDropAddress);
		textViewCustomerDropAddress.setTypeface(Fonts.mavenRegular(activity));
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
		rvPickupFeedImages = rootView.findViewById(R.id.rvPickupFeedImages);
		rvPickupFeedImages.setItemAnimator(new DefaultItemAnimator());
		rvPickupFeedImages.setLayoutManager(new LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false));
        tvFeedInstructions = rootView.findViewById(R.id.tvFeedInstructions);
        tvFeedInstructions.setTypeface(Fonts.mavenRegular(activity));



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
				if (customerInfo.getCustomerOrderImagesList() != null && customerInfo.getCustomerOrderImagesList().size() > 0) {
					rvPickupFeedImages.setAdapter(new ImageWithTextAdapter(customerInfo.getCustomerOrderImagesList(), new ImageWithTextAdapter.OnItemClickListener() {
						@Override
						public void onItemClick(String image, int pos) {
							ArrayList<String> imagesCustomer = new ArrayList<>();
							imagesCustomer.addAll(customerInfo.getCustomerOrderImagesList());
							DialogReviewImagesFragment dialog = DialogReviewImagesFragment.newInstance(pos,imagesCustomer);
							dialog.show(activity.getFragmentManager(),DialogReviewImagesFragment.class.getSimpleName());
						}
					}));
					rvPickupFeedImages.setVisibility(View.VISIBLE);
				} else {
					rvPickupFeedImages.setVisibility(View.GONE);
				}


                if (customerInfo.getVendorMessage() != null && !TextUtils.isEmpty(customerInfo.getVendorMessage())) {
					tvFeedInstructions.setVisibility(View.VISIBLE);
					if(customerInfo.getVendorMessage().length() > 20) {
						tvFeedInstructions.setText(R.string.click_to_view_message);
						tvFeedInstructions.setEnabled(true);
					} else {
						tvFeedInstructions.setText(activity.getString(R.string.instructions_colon) + customerInfo.getVendorMessage());
						tvFeedInstructions.setEnabled(false);
					}
                }
                else {
                    tvFeedInstructions.setVisibility(View.GONE);
                }
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
				}
				tvCustomerNotes.setOnClickListener(view -> openNotesDialog(!TextUtils.isEmpty(customerInfo.getCustomerNotes())?customerInfo.getCustomerNotes():""));
				tvFeedInstructions.setOnClickListener(view -> openNotesDialog(!TextUtils.isEmpty(customerInfo.getVendorMessage())?customerInfo.getVendorMessage():""));


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
							getAddress(customerInfo.getDropLatLng(), "inride_d",
									customerInfo.getEngagementId(),
											activity.getTextViewEnterDestination(), null, true);
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
					updateDistanceOnLocationChanged(customerInfo);
					textViewDeliveryCount.setVisibility(View.GONE);

				} else {
					textViewCustomerPickupAddress.setVisibility(View.VISIBLE);
					if(DriverScreenMode.D_START_RIDE != HomeActivity.driverScreenMode) {
						activity.buttonDriverNavigationSetVisibility(View.VISIBLE);
					}
					if (customerInfo.getAddress().equalsIgnoreCase("")) {
						getAddress(customerInfo.getRequestlLatLng(), "sride_p",
								customerInfo.getEngagementId(),
								textViewCustomerPickupAddress, null, false);
						textViewCustomerPickupAddress.setText(customerInfo.getPickupAddressEng());
					} else {
						textViewCustomerPickupAddress.setText(customerInfo.getAddress());
					}

					updateDistanceOnLocationChanged(customerInfo);
					if (customerInfo.getIsDelivery() == 1 && customerInfo.getIsDeliveryPool() != 1) {
						if (customerInfo.getDeliveryInfos().size() > 1) {
							textViewDeliveryCount.setVisibility(View.VISIBLE);
							textViewDeliveryCount.setText(activity.getResources().getString(R.string.deliveries)
									+ " " + customerInfo.getTotalDeliveries());
							textViewCustomerCashRequired.setVisibility(View.GONE);
							textViewCustomerCashRequired.setText(activity.getResources().getString(R.string.cash_to_collected)
									+ ": " + activity.getResources().getString(R.string.rupee)
									+ "" + customerInfo.getCashOnDelivery());
						} else {
							textViewDeliveryCount.setVisibility(View.GONE);
							textViewCustomerCashRequired.setVisibility(View.GONE);
						}
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

						if(Prefs.with(activity).getInt(KEY_SHOW_DROP_LOCATION_BELOW_PICKUP, 0) == 1) {
							textViewCustomerDropAddress.setVisibility(Prefs.with(activity).getInt(Constants.KEY_SHOW_DROP_ADDRESS_BEFORE_INRIDE, 1) == 0
									? View.GONE : View.VISIBLE);
							activity.bDropAddressToggle.setVisibility(View.GONE);
						} else {
							activity.bDropAddressToggle.setVisibility(Prefs.with(activity).getInt(Constants.KEY_SHOW_DROP_ADDRESS_BEFORE_INRIDE, 1) == 0
									? View.GONE : View.VISIBLE);
							textViewCustomerDropAddress.setVisibility(View.GONE);
						}
						if(activity.bDropAddressToggle.getVisibility() == View.VISIBLE || textViewCustomerDropAddress.getVisibility() == View.VISIBLE) {
							activity.tvDropAddressToggleView.setText(R.string.loading);
							if (customerInfo.getDropAddress().equalsIgnoreCase("")) {
								getAddress(customerInfo.getDropLatLng(), "sride_d",
										customerInfo.getEngagementId(),
										activity.tvDropAddressToggleView, null, true);
								activity.tvDropAddressToggleView.setText(customerInfo.getDropAddressEng());
								textViewCustomerDropAddress.setText(customerInfo.getDropAddressEng());
							} else {
								activity.tvDropAddressToggleView.setText(customerInfo.getDropAddress());
								textViewCustomerDropAddress.setText(customerInfo.getDropAddress());
							}
						}
					} else {
						activity.bDropAddressToggle.setVisibility(View.GONE);
						textViewCustomerDropAddress.setVisibility(View.GONE);
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

	public void updateDistanceOnLocationChanged(CustomerInfo customerInfo) {
		try {
			if(Data.fareStructure == null || customerInfo == null){
				return;
			}
			String fareAndDistance = "";
			if(Data.fareStructure.mandatoryFare > 0 && Prefs.with(activity).getInt(KEY_SHOW_FARE_BEFORE_RIDE_START, 0) == 1) {
				fareAndDistance = fareAndDistance.concat(activity.getString(R.string.fare)).concat(": ")
						.concat(Utils.formatCurrencyValue(customerInfo.getCurrencyUnit(), Data.fareStructure.mandatoryFare));
			}

			if (DriverScreenMode.D_ARRIVED == HomeActivity.driverScreenMode) {
				if (HomeActivity.myLocation != null) {
					fareAndDistance = fareAndDistance.concat("\n").concat(Utils.getDecimalFormatForMoney()
							.format(MapUtils.distance(customerInfo.getRequestlLatLng(),
									new LatLng(HomeActivity.myLocation.getLatitude(), HomeActivity.myLocation.getLongitude())) * 1.4F * UserData.getDistanceUnitFactor(activity, false))
							+" "+Utils.getDistanceUnit(UserData.getDistanceUnit(activity))+ "\n" + activity.getResources().getString(R.string.away_cap));
				}
			}
			if(!TextUtils.isEmpty(fareAndDistance)){
				textViewShowDistance.setText(fareAndDistance);
				textViewShowDistance.setVisibility(View.VISIBLE);
			} else {
				textViewShowDistance.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	void getAddress(LatLng currentLatLng, String source, int engagementId, TextView textView, TextView textView1, boolean isDrop){
		GoogleAPICoroutine.INSTANCE.hitGeocode(currentLatLng, source, (googleGeocodeResponse, singleAddress)  -> {
			try {
				String address = null;
				if(googleGeocodeResponse != null){
					GAPIAddress gapiAddress = MapUtils.parseGAPIIAddress(googleGeocodeResponse);
					address = gapiAddress.getSearchableAddress();
				} else if(singleAddress != null){
					address = singleAddress;
				}
				try {
					if(isDrop){
						Data.getCustomerInfo(String.valueOf(engagementId)).setDropAddress(activity, address, true);
					} else {
						Data.getCustomerInfo(String.valueOf(engagementId)).setAddress(address);
					}
					textView.setText(address);
					if(isDrop) {
						textViewCustomerDropAddress.setText(address);
					}
					if(textView1 !=null) {
						textView1.setText(address);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}


	public void setCallButton() {
		if (Data.getCurrentCustomerInfo() != null) {
			if ((Data.getCurrentCustomerInfo().getIsDelivery() == 1)
					&& DriverScreenMode.D_IN_RIDE == HomeActivity.driverScreenMode
					&& activity.getResources().getBoolean(R.bool.show_customer_info_in_inride)) {
				if (Data.getCurrentCustomerInfo().getName().equals(Data.getCurrentCustomerInfo().getDeliveryInfos().get(0).getCustomerName())
						&& Data.getCurrentCustomerInfo().getPhoneNumber().equals(Data.getCurrentCustomerInfo().getDeliveryInfos().get(0).getCustomerNo())) {
					relativeLayoutCustomerInfo.setVisibility(View.GONE);
				} else {
					relativeLayoutCustomerInfo.setVisibility(View.VISIBLE);
				}

			} else {
				relativeLayoutCustomerInfo.setVisibility(View.GONE);
			}
		}
	}

	public void updateList() {
		customerInfoAdapter.notifyList();
	}
}

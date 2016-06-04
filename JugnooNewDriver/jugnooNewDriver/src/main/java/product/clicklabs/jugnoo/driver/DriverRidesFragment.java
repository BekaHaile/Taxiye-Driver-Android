package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.datastructure.RideInfo;
import product.clicklabs.jugnoo.driver.datastructure.UpdateDriverEarnings;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.NewBookingHistoryRespose;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class DriverRidesFragment extends Fragment implements FlurryEventNames {

	ProgressBar progressBar;
	TextView textViewInfoDisplay;
	ListView listView;

	DriverRidesListAdapter driverRidesListAdapter;

	RelativeLayout main;

	ArrayList<RideInfo> rides = new ArrayList<RideInfo>();

	UpdateDriverEarnings updateDriverEarnings;

	public DriverRidesFragment() {
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		rides.clear();
		View rootView = inflater.inflate(R.layout.fragment_list, container, false);

		main = (RelativeLayout) rootView.findViewById(R.id.main);
		main.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		ASSL.DoMagic(main);

		progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
		textViewInfoDisplay = (TextView) rootView.findViewById(R.id.textViewInfoDisplay);
		textViewInfoDisplay.setTypeface(Data.latoRegular(getActivity()));
		listView = (ListView) rootView.findViewById(R.id.listView);

		driverRidesListAdapter = new DriverRidesListAdapter();
		listView.setAdapter(driverRidesListAdapter);

		progressBar.setVisibility(View.GONE);
		textViewInfoDisplay.setVisibility(View.GONE);

		textViewInfoDisplay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getRidesAsync(getActivity());
			}
		});

		getRidesAsync(getActivity());
		FlurryEventLogger.event(COMPLETE_RIDES_CHECKED);


		return rootView;
	}


	public void updateListData(String message, boolean errorOccurred) {
		if (errorOccurred) {
			textViewInfoDisplay.setText(message);
			textViewInfoDisplay.setVisibility(View.VISIBLE);

			rides.clear();
			driverRidesListAdapter.notifyDataSetChanged();
		} else {
			if (rides.size() == 0) {
				textViewInfoDisplay.setText(message);
				textViewInfoDisplay.setVisibility(View.VISIBLE);
			} else {
				textViewInfoDisplay.setVisibility(View.GONE);
			}
			driverRidesListAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onPause() {
		super.onPause();
	}


	class ViewHolderDriverRides {
		TextView dateTimeValue, textViewRideId, textViewStatusString, textViewActualFareFare,
				textViewCustomerPaid, textViewAccountBalance, textViewBalanceText, distanceValue, rideTimeValue,
				waitTimeValue, textViewTransTypeText, textViewCustomerPaidtext;
		ImageView imageViewRequestType;
		RelativeLayout relative;
		int id;
	}

	class DriverRidesListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderDriverRides holder;

		public DriverRidesListAdapter() {
			mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return rides.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				holder = new ViewHolderDriverRides();
				convertView = mInflater.inflate(R.layout.list_item_ride_history, null);

				holder.dateTimeValue = (TextView) convertView.findViewById(R.id.dateTimeValue);
				holder.dateTimeValue.setTypeface(Data.latoRegular(getActivity()));
				holder.textViewCustomerPaidtext = (TextView) convertView.findViewById(R.id.textViewCustomerPaidtext);
				holder.textViewCustomerPaidtext.setTypeface(Data.latoRegular(getActivity()));
				holder.textViewRideId = (TextView) convertView.findViewById(R.id.textViewRideId);
				holder.textViewRideId.setTypeface(Data.latoRegular(getActivity()));
				holder.textViewStatusString = (TextView) convertView.findViewById(R.id.textViewStatusString);
				holder.textViewStatusString.setTypeface(Data.latoRegular(getActivity()));
				holder.textViewActualFareFare = (TextView) convertView.findViewById(R.id.textViewActualFareFare);
				holder.textViewActualFareFare.setTypeface(Data.latoRegular(getActivity()));
				holder.textViewCustomerPaid = (TextView) convertView.findViewById(R.id.textViewCustomerPaid);
				holder.textViewCustomerPaid.setTypeface(Data.latoRegular(getActivity()));
				holder.textViewAccountBalance = (TextView) convertView.findViewById(R.id.textViewAccountBalance);
				holder.textViewAccountBalance.setTypeface(Data.latoRegular(getActivity()));
				holder.distanceValue = (TextView) convertView.findViewById(R.id.distanceValue);
				holder.distanceValue.setTypeface(Data.latoRegular(getActivity()));
				holder.rideTimeValue = (TextView) convertView.findViewById(R.id.rideTimeValue);
				holder.rideTimeValue.setTypeface(Data.latoRegular(getActivity()));
				holder.waitTimeValue = (TextView) convertView.findViewById(R.id.waitTimeValue);
				holder.waitTimeValue.setTypeface(Data.latoRegular(getActivity()));
				holder.textViewBalanceText = (TextView) convertView.findViewById(R.id.textViewBalanceText);
				holder.textViewBalanceText.setTypeface(Data.latoRegular(getActivity()));
				holder.textViewTransTypeText = (TextView) convertView.findViewById(R.id.textViewTransTypeText);
				holder.textViewTransTypeText.setTypeface(Data.latoRegular(getActivity()));
				holder.imageViewRequestType = (ImageView) convertView.findViewById(R.id.imageViewRequestType);
				holder.relative = (RelativeLayout) convertView.findViewById(R.id.relative);

				holder.relative.setTag(holder);

				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolderDriverRides) convertView.getTag();
			}

			holder.dateTimeValue.setVisibility(View.GONE);
			holder.textViewRideId.setVisibility(View.GONE);
			holder.textViewStatusString.setVisibility(View.GONE);
			holder.textViewActualFareFare.setVisibility(View.GONE);
			holder.textViewCustomerPaid.setVisibility(View.GONE);
			holder.textViewAccountBalance.setVisibility(View.GONE);
			holder.distanceValue.setVisibility(View.GONE);
			holder.rideTimeValue.setVisibility(View.GONE);
			holder.waitTimeValue.setVisibility(View.GONE);
			holder.textViewBalanceText.setVisibility(View.GONE);
			holder.textViewTransTypeText.setVisibility(View.GONE);


			final RideInfo rideInfo = rides.get(position);

			holder.id = position;

			PaymentActivity activity;
			activity = (PaymentActivity) getActivity();


			if (rideInfo.type.equalsIgnoreCase("ride")) {

				if (0 == rideInfo.driverPaymentStatus) {
					holder.relative.setBackgroundResource(R.drawable.list_white_selector);
				} else {
					holder.relative.setBackgroundResource(R.drawable.list_white_inv_selector);
				}
				holder.textViewCustomerPaidtext.setText(activity.getStringText(R.string.paid_cash));
				holder.textViewTransTypeText.setText(activity.getStringText(R.string.Ride));
				holder.textViewTransTypeText.setVisibility(View.VISIBLE);
				holder.dateTimeValue.setText(DateOperations.convertDate(DateOperations.utcToLocal(rideInfo.dateTime)));
				holder.dateTimeValue.setVisibility(View.VISIBLE);
				holder.textViewRideId.setText(activity.getStringText(R.string.Ride_id) + ": " + rideInfo.id);
				holder.textViewRideId.setVisibility(View.VISIBLE);

				if ("".equalsIgnoreCase(rideInfo.statusString)) {
					holder.textViewStatusString.setVisibility(View.GONE);
				} else {
					holder.textViewStatusString.setVisibility(View.VISIBLE);
					if ("Ride Cancelled".equalsIgnoreCase(rideInfo.statusString)) {
						holder.textViewStatusString.setTextColor(getActivity().getResources().getColor(R.color.red_status));
						holder.textViewStatusString.setText(activity.getStringText(R.string.rides_cancelled));
					} else {
						holder.textViewStatusString.setTextColor(getActivity().getResources().getColor(R.color.bg_grey_opaque));
						holder.textViewStatusString.setText(activity.getStringText(R.string.ride_complete));
					}
				}

				holder.textViewCustomerPaid.setText(getResources().getString(R.string.rupee) + " " + Utils.getDecimalFormatForMoney().format(Double.parseDouble(rideInfo.customerPaid)));
				holder.textViewCustomerPaid.setVisibility(View.VISIBLE);
				double balance = Double.parseDouble(rideInfo.accountBalance);
				holder.textViewBalanceText.setVisibility(View.VISIBLE);
				if (balance < 0) {
					holder.textViewAccountBalance.setTextColor(getActivity().getResources().getColor(R.color.bg_grey_opaque));
					holder.textViewBalanceText.setText("Money to\nJugnoo");
					holder.textViewBalanceText.setTextColor(getActivity().getResources().getColor(R.color.bg_grey_opaque));

				} else {
					holder.textViewAccountBalance.setTextColor(getActivity().getResources().getColor(R.color.bg_grey_opaque));
					holder.textViewBalanceText.setText(activity.getStringText(R.string.account));
					holder.textViewBalanceText.setTextColor(getActivity().getResources().getColor(R.color.bg_grey_opaque));

				}
				holder.textViewAccountBalance.setVisibility(View.VISIBLE);
				holder.textViewAccountBalance.setText(getResources().getString(R.string.rupee) + " " + Utils.getDecimalFormatForMoney().format(Math.abs(Double.parseDouble(rideInfo.accountBalance))));

				holder.textViewActualFareFare.setVisibility(View.VISIBLE);
				holder.textViewActualFareFare.setText(getResources().getString(R.string.rupee) + " " + Utils.getDecimalFormatForMoney().format(Double.parseDouble(rideInfo.actualFare)));

				holder.distanceValue.setVisibility(View.VISIBLE);
				holder.rideTimeValue.setVisibility(View.VISIBLE);
				holder.distanceValue.setText(rideInfo.distance + " " + activity.getStringText(R.string.km) + ", ");
				holder.rideTimeValue.setText(rideInfo.rideTime + " " + activity.getStringText(R.string.min));


			} else if (rideInfo.type.equalsIgnoreCase("delivery")) {

				if (0 == rideInfo.driverPaymentStatus) {
					holder.relative.setBackgroundResource(R.drawable.list_white_selector);
				} else {
					holder.relative.setBackgroundResource(R.drawable.list_white_inv_selector);
				}
				holder.textViewCustomerPaidtext.setText(activity.getStringText(R.string.paid_cash));
				holder.textViewTransTypeText.setText(activity.getStringText(R.string.delivery));
				holder.textViewTransTypeText.setVisibility(View.VISIBLE);
				holder.dateTimeValue.setText(DateOperations.convertDate(DateOperations.utcToLocal(rideInfo.dateTime)));
				holder.dateTimeValue.setVisibility(View.VISIBLE);
				holder.textViewRideId.setText(activity.getStringText(R.string.Ride_id) + ": " + rideInfo.id);
				holder.textViewRideId.setVisibility(View.VISIBLE);

				if ("".equalsIgnoreCase(rideInfo.statusString)) {
					holder.textViewStatusString.setVisibility(View.GONE);
				} else {
					holder.textViewStatusString.setVisibility(View.VISIBLE);
					if ("Delivery Cancelled".equalsIgnoreCase(rideInfo.statusString)) {
						holder.textViewStatusString.setTextColor(getActivity().getResources().getColor(R.color.red_status));
						holder.textViewStatusString.setText(activity.getStringText(R.string.delivery_completed));
					} else {
						holder.textViewStatusString.setTextColor(getActivity().getResources().getColor(R.color.bg_grey_opaque));
						holder.textViewStatusString.setText(activity.getStringText(R.string.delivery_cancelled));
					}
				}

				holder.textViewCustomerPaid.setText(getResources().getString(R.string.rupee) + " " + Utils.getDecimalFormatForMoney().format(Double.parseDouble(rideInfo.customerPaid)));
				holder.textViewCustomerPaid.setVisibility(View.VISIBLE);
				double balance = Double.parseDouble(rideInfo.accountBalance);
				holder.textViewBalanceText.setVisibility(View.VISIBLE);
				if (balance < 0) {
					holder.textViewAccountBalance.setTextColor(getActivity().getResources().getColor(R.color.bg_grey_opaque));
					holder.textViewBalanceText.setText("Money to\nJugnoo");
					holder.textViewBalanceText.setTextColor(getActivity().getResources().getColor(R.color.bg_grey_opaque));

				} else {
					holder.textViewAccountBalance.setTextColor(getActivity().getResources().getColor(R.color.bg_grey_opaque));
					holder.textViewBalanceText.setText(activity.getStringText(R.string.account));
					holder.textViewBalanceText.setTextColor(getActivity().getResources().getColor(R.color.bg_grey_opaque));

				}
				holder.textViewAccountBalance.setVisibility(View.VISIBLE);
				holder.textViewAccountBalance.setText(getResources().getString(R.string.rupee) + " " + Utils.getDecimalFormatForMoney().format(Math.abs(Double.parseDouble(rideInfo.accountBalance))));

				holder.textViewActualFareFare.setVisibility(View.VISIBLE);
				holder.textViewActualFareFare.setText(getResources().getString(R.string.rupee) + " " + Utils.getDecimalFormatForMoney().format(Double.parseDouble(rideInfo.actualFare)));

				holder.distanceValue.setVisibility(View.VISIBLE);
				holder.rideTimeValue.setVisibility(View.VISIBLE);
				holder.distanceValue.setText(rideInfo.distance + " " + activity.getStringText(R.string.km) + ", ");
				holder.rideTimeValue.setText(rideInfo.rideTime + " " + activity.getStringText(R.string.min));


			} else if (rideInfo.type.equalsIgnoreCase("referral")) {
				holder.textViewTransTypeText.setVisibility(View.VISIBLE);
				holder.dateTimeValue.setVisibility(View.VISIBLE);
				holder.textViewRideId.setVisibility(View.VISIBLE);
				holder.textViewActualFareFare.setVisibility(View.VISIBLE);
				holder.textViewCustomerPaid.setVisibility(View.VISIBLE);
				holder.textViewAccountBalance.setVisibility(View.VISIBLE);
				holder.textViewBalanceText.setVisibility(View.VISIBLE);
				holder.textViewTransTypeText.setText(activity.getStringText(R.string.referral));
				holder.dateTimeValue.setText(DateOperations.convertDate(DateOperations.utcToLocal(rideInfo.dateTime)));
				holder.textViewRideId.setText(getResources().getString(R.string.customer_id) + ": " + rideInfo.customerId);
				holder.textViewActualFareFare.setText(getResources().getString(R.string.rupee) + " " + Utils.getDecimalFormatForMoney().format(Double.parseDouble(rideInfo.referralAmount)));
				holder.textViewCustomerPaid.setText(getResources().getString(R.string.rupee) + " " + Utils.getDecimalFormatForMoney().format(Double.parseDouble("0")));
				holder.textViewAccountBalance.setText(getResources().getString(R.string.rupee) + " " + Utils.getDecimalFormatForMoney().format(Math.abs(Double.parseDouble(rideInfo.referralAmount))));
				holder.textViewAccountBalance.setTextColor(getActivity().getResources().getColor(R.color.bg_grey_opaque));
				holder.textViewBalanceText.setText(activity.getStringText(R.string.account));
				holder.textViewBalanceText.setTextColor(getActivity().getResources().getColor(R.color.bg_grey_opaque));

			} else if (rideInfo.type.equalsIgnoreCase("phone_deductions")) {
				holder.textViewTransTypeText.setVisibility(View.VISIBLE);
				holder.dateTimeValue.setVisibility(View.VISIBLE);
				holder.textViewActualFareFare.setVisibility(View.VISIBLE);
				holder.textViewCustomerPaid.setVisibility(View.VISIBLE);
				holder.textViewAccountBalance.setVisibility(View.VISIBLE);
				holder.textViewBalanceText.setVisibility(View.VISIBLE);
				holder.textViewTransTypeText.setText(activity.getStringText(R.string.phone_deduction));
				holder.dateTimeValue.setText(DateOperations.convertDate(DateOperations.utcToLocal(rideInfo.dateTime)));
				holder.textViewActualFareFare.setText(getResources().getString(R.string.rupee) + " " + Utils.getDecimalFormatForMoney().format(Double.parseDouble(rideInfo.amount)));
				holder.textViewCustomerPaid.setText(getResources().getString(R.string.rupee) + " " + Utils.getDecimalFormatForMoney().format(Double.parseDouble("0")));
				holder.textViewAccountBalance.setText(getResources().getString(R.string.rupee) + " " + Utils.getDecimalFormatForMoney().format(Math.abs(Double.parseDouble(rideInfo.amount))));
				holder.textViewAccountBalance.setTextColor(getActivity().getResources().getColor(R.color.bg_grey_opaque));
				holder.textViewBalanceText.setText("Money to\nJugnoo");
				holder.textViewBalanceText.setTextColor(getActivity().getResources().getColor(R.color.bg_grey_opaque));
			} else if (rideInfo.type.equalsIgnoreCase("paytm_transaction")) {
				holder.textViewTransTypeText.setVisibility(View.VISIBLE);
				holder.dateTimeValue.setVisibility(View.VISIBLE);
				holder.textViewRideId.setVisibility(View.VISIBLE);
				holder.textViewActualFareFare.setVisibility(View.VISIBLE);
				holder.textViewCustomerPaid.setVisibility(View.VISIBLE);
				holder.textViewAccountBalance.setVisibility(View.VISIBLE);
				holder.textViewBalanceText.setVisibility(View.VISIBLE);
				holder.textViewStatusString.setVisibility(View.VISIBLE);
				holder.textViewTransTypeText.setText(activity.getStringText(R.string.paytm_transaction));
				holder.textViewRideId.setText(activity.getStringText(R.string.phone_no) + rideInfo.phone);
				holder.dateTimeValue.setText(DateOperations.convertDate(DateOperations.utcToLocal(rideInfo.dateTime)));
				holder.textViewActualFareFare.setText(getResources().getString(R.string.rupee) + " " + Utils.getDecimalFormatForMoney().format(Double.parseDouble(rideInfo.amount)));
				holder.textViewCustomerPaid.setText(getResources().getString(R.string.rupee) + " " + Utils.getDecimalFormatForMoney().format(Double.parseDouble(rideInfo.amount)));
				holder.textViewAccountBalance.setText(getResources().getString(R.string.rupee) + " " + Utils.getDecimalFormatForMoney().format(Math.abs(Double.parseDouble(rideInfo.amount))));
				holder.textViewAccountBalance.setTextColor(getActivity().getResources().getColor(R.color.bg_grey_opaque));
				holder.textViewBalanceText.setText("Money to\nJugnoo");
				holder.textViewBalanceText.setTextColor(getActivity().getResources().getColor(R.color.bg_grey_opaque));
				if ("Failed".equalsIgnoreCase(rideInfo.status)) {
					holder.textViewStatusString.setTextColor(getActivity().getResources().getColor(R.color.red_status));
				} else {
					holder.textViewStatusString.setTextColor(getActivity().getResources().getColor(R.color.bg_grey_opaque));
				}
				holder.textViewStatusString.setText(rideInfo.status);
			}

			holder.relative.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					holder = (ViewHolderDriverRides) v.getTag();
					if (rides.get(holder.id).type.equalsIgnoreCase("ride")) {
						RideDetailsActivity.openedRideInfo = rides.get(holder.id);
						getActivity().startActivity(new Intent(getActivity(), RideDetailsActivity.class));
						getActivity().overridePendingTransition(R.anim.right_in, R.anim.right_out);
					}
				}
			});
			return convertView;
		}

	}


//	Retrofit

	private void getRidesAsync(final Activity activity) {
		try {

			progressBar.setVisibility(View.VISIBLE);
			RestClient.getApiServices().bookingHistory(Data.userData.accessToken, "1",
					new Callback<NewBookingHistoryRespose>() {
						@Override
						public void success(NewBookingHistoryRespose newBookingHistoryRespose, Response response) {
							try {
								if (activity != null) {
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

										for (int i = 0; i < newBookingHistoryRespose.getBookingData().size(); i++) {
											NewBookingHistoryRespose.BookingDatum data = newBookingHistoryRespose.getBookingData().get(i);
											RideInfo rideInfo = null;
											if (data.getType().equalsIgnoreCase("ride")) {
												rideInfo = new RideInfo(data.getId(), data.getFrom(), data.getTo(), data.getFare(),
														data.getCustomerPaid(), data.getBalance(), data.getSubsidy(), data.getDistance(),
														data.getRideTime(), data.getWaitTime(), data.getTime(), data.getCouponUsed(), data.getPaymentMode(),
														data.getBusinessId(), data.getDriverPaymentStatus(), data.getStatusString(), data.getConvenienceCharges(),
														data.getLuggageCharges(), data.getFareFactorApplied(), data.getFareFactorValue(), data.getAcceptSubsidy(),
														data.getCancelSubsidy(), data.getAccountBalance(), data.getActualFare(), data.getType(), data.getDriverRideFare());
											} else if (data.getType().equalsIgnoreCase("delivery")) {
												rideInfo = new RideInfo(data.getId(), data.getFrom(), data.getTo(), data.getFare(),
														data.getCustomerPaid(), data.getBalance(), data.getSubsidy(), data.getDistance(),
														data.getRideTime(), data.getWaitTime(), data.getTime(), data.getCouponUsed(), data.getPaymentMode(),
														data.getBusinessId(), data.getDriverPaymentStatus(), data.getStatusString(), data.getConvenienceCharges(),
														data.getLuggageCharges(), data.getFareFactorApplied(), data.getFareFactorValue(), data.getAcceptSubsidy(),
														data.getCancelSubsidy(), data.getAccountBalance(), data.getActualFare(), data.getType(), data.getDriverRideFare());
											} else if (data.getType().equalsIgnoreCase("referral")) {
												rideInfo = new RideInfo(data.getCustomerId(), data.getReferralAmount(), data.getReferredOn(), data.getType(), data.getTime());
											} else if (data.getType().equalsIgnoreCase("phone_deductions")) {
												rideInfo = new RideInfo(data.getAmount(), data.getType(), data.getTime());
											} else if (data.getType().equalsIgnoreCase("paytm_transaction")) {
												rideInfo = new RideInfo(data.getAmount(), data.getType(), data.getTime(), data.getStatus(), data.getPhone());
											}

											if (rideInfo != null) {
												rides.add(rideInfo);
											}
										}


										updateListData(getResources().getString(R.string.no_rides), false);
									}
								}
							} catch (Exception exception) {
								exception.printStackTrace();
								updateListData(getResources().getString(R.string.error_occured_tap_to_retry), true);
							}
							progressBar.setVisibility(View.GONE);
						}


						@Override
						public void failure(RetrofitError error) {
							progressBar.setVisibility(View.GONE);
							updateListData(getResources().getString(R.string.error_occured_tap_to_retry), true);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

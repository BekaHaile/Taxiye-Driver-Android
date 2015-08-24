package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.datastructure.BusinessType;
import product.clicklabs.jugnoo.driver.datastructure.PaymentMode;
import product.clicklabs.jugnoo.driver.datastructure.RideInfo;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import rmn.androidscreenlibrary.ASSL;

public class DriverRidesFragment extends Fragment {

	ProgressBar progressBar;
	TextView textViewInfoDisplay;
	ListView listView;
	
	DriverRidesListAdapter driverRidesListAdapter;
	
	RelativeLayout main;
	
	AsyncHttpClient fetchRidesClient;

	ArrayList<RideInfo> rides = new ArrayList<RideInfo>();
	
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
		textViewInfoDisplay = (TextView) rootView.findViewById(R.id.textViewInfoDisplay); textViewInfoDisplay.setTypeface(Data.latoRegular(getActivity()));
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
		
		return rootView;
	}

	
	public void updateListData(String message, boolean errorOccurred){
		if(errorOccurred){
			textViewInfoDisplay.setText(message);
			textViewInfoDisplay.setVisibility(View.VISIBLE);
			
			rides.clear();
			driverRidesListAdapter.notifyDataSetChanged();
		}
		else{
			if(rides.size() == 0){
				textViewInfoDisplay.setText(message);
				textViewInfoDisplay.setVisibility(View.VISIBLE);
			}
			else{
				textViewInfoDisplay.setVisibility(View.GONE);
			}
			driverRidesListAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public void onDestroy() {
		if(fetchRidesClient != null){
			fetchRidesClient.cancelAllRequests(true);
		}
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
		TextView dateTimeValue, textViewRideId, textViewStatusString, textViewBalance, textViewJugnooSubsidy,
			textViewCustomerPaid, textViewPaidToMerchant, textViewPaidByCustomer, textViewFare, distanceValue, rideTimeValue;
		ImageView couponImg, jugnooCashImg, imageViewRequestType;
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

				holder.dateTimeValue = (TextView) convertView.findViewById(R.id.dateTimeValue); holder.dateTimeValue.setTypeface(Data.latoRegular(getActivity()));
				holder.textViewRideId = (TextView) convertView.findViewById(R.id.textViewRideId); holder.textViewRideId.setTypeface(Data.latoRegular(getActivity()));
                holder.textViewStatusString = (TextView) convertView.findViewById(R.id.textViewStatusString); holder.textViewStatusString.setTypeface(Data.latoRegular(getActivity()));
				holder.textViewBalance = (TextView) convertView.findViewById(R.id.textViewBalance); holder.textViewBalance.setTypeface(Data.latoRegular(getActivity()), Typeface.BOLD);
				holder.textViewJugnooSubsidy = (TextView) convertView.findViewById(R.id.textViewJugnooSubsidy); holder.textViewJugnooSubsidy.setTypeface(Data.latoRegular(getActivity()));
				holder.textViewCustomerPaid = (TextView) convertView.findViewById(R.id.textViewCustomerPaid); holder.textViewCustomerPaid.setTypeface(Data.latoRegular(getActivity()));
				holder.textViewPaidToMerchant = (TextView) convertView.findViewById(R.id.textViewPaidToMerchant); holder.textViewPaidToMerchant.setTypeface(Data.latoRegular(getActivity()));
                holder.textViewPaidByCustomer = (TextView) convertView.findViewById(R.id.textViewPaidByCustomer); holder.textViewPaidByCustomer.setTypeface(Data.latoRegular(getActivity()));
				holder.textViewFare = (TextView) convertView.findViewById(R.id.textViewFare); holder.textViewFare.setTypeface(Data.latoRegular(getActivity()));
				holder.distanceValue = (TextView) convertView.findViewById(R.id.distanceValue); holder.distanceValue.setTypeface(Data.latoRegular(getActivity()));
				holder.rideTimeValue = (TextView) convertView.findViewById(R.id.rideTimeValue); holder.rideTimeValue.setTypeface(Data.latoRegular(getActivity()));
				
				holder.couponImg = (ImageView) convertView.findViewById(R.id.couponImg);
				holder.jugnooCashImg = (ImageView) convertView.findViewById(R.id.jugnooCashImg);
                holder.imageViewRequestType = (ImageView) convertView.findViewById(R.id.imageViewRequestType);
				
				holder.relative = (RelativeLayout) convertView.findViewById(R.id.relative);
				
				holder.relative.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderDriverRides) convertView.getTag();
			}
			
			
			RideInfo rideInfo = rides.get(position);
			
			holder.id = position;

            if(0 == rideInfo.driverPaymentStatus){
                holder.relative.setBackgroundResource(R.drawable.list_white_selector);
            }
            else{
                holder.relative.setBackgroundResource(R.drawable.list_white_inv_selector);
            }


			holder.dateTimeValue.setText(DateOperations.convertDate(DateOperations.utcToLocal(rideInfo.dateTime)));
			holder.textViewRideId.setText("Ride ID: "+rideInfo.id);

            if("".equalsIgnoreCase(rideInfo.statusString)){
                holder.textViewStatusString.setVisibility(View.GONE);
            }
            else{
                holder.textViewStatusString.setVisibility(View.VISIBLE);
                if("Ride Cancelled".equalsIgnoreCase(rideInfo.statusString)){
                    holder.textViewStatusString.setTextColor(getActivity().getResources().getColor(R.color.red_status));
                }
                else{
                    holder.textViewStatusString.setTextColor(getActivity().getResources().getColor(R.color.bg_grey_opaque));
                }
                holder.textViewStatusString.setText(rideInfo.statusString);
            }
			
			double balance = Double.parseDouble(rideInfo.balance);
			if(balance < 0){
				holder.textViewBalance.setTextColor(getActivity().getResources().getColor(R.color.red_status));
			}
			else{
				holder.textViewBalance.setTextColor(getActivity().getResources().getColor(R.color.bg_grey_opaque));
			}
			holder.textViewBalance.setText("Bal. from Jugnoo: Rs. " + rideInfo.balance);
			
			if("0".equalsIgnoreCase(rideInfo.subsidy)){
				holder.textViewJugnooSubsidy.setVisibility(View.GONE);
			}
			else{
				holder.textViewJugnooSubsidy.setVisibility(View.VISIBLE);
				holder.textViewJugnooSubsidy.setText("Jugnoo Subsidy: Rs. " + rideInfo.subsidy);
			}

			holder.textViewFare.setText("Fare: Rs. "+rideInfo.fare);
			
			holder.distanceValue.setText(rideInfo.distance + " km");
			holder.rideTimeValue.setText(rideInfo.rideTime + " min");


			if(1 == rideInfo.couponUsed){
				holder.couponImg.setVisibility(View.VISIBLE);
			}
			else{
				holder.couponImg.setVisibility(View.GONE);
			}
			
			if(PaymentMode.WALLET.getOrdinal() == rideInfo.paymentMode){
				holder.jugnooCashImg.setVisibility(View.VISIBLE);
			}
			else{
				holder.jugnooCashImg.setVisibility(View.GONE);
			}

            if(BusinessType.AUTOS.getOrdinal() == rideInfo.businessId){
                holder.textViewCustomerPaid.setVisibility(View.VISIBLE);
                holder.textViewPaidToMerchant.setVisibility(View.GONE);
                holder.textViewPaidByCustomer.setVisibility(View.GONE);

                holder.textViewCustomerPaid.setText("Paid by Customer: Rs. " + rideInfo.customerPaid);
                holder.imageViewRequestType.setImageResource(R.drawable.request_autos);
            }
            else if(BusinessType.FATAFAT.getOrdinal() == rideInfo.businessId){
                holder.textViewCustomerPaid.setVisibility(View.GONE);
                holder.textViewPaidToMerchant.setVisibility(View.VISIBLE);
                holder.textViewPaidByCustomer.setVisibility(View.VISIBLE);

                holder.textViewPaidToMerchant.setText("Paid to Merchant: Rs. "+rideInfo.paidToMerchant);
                holder.textViewPaidByCustomer.setText("Paid by Customer: Rs. "+rideInfo.paidByCustomer);
                holder.imageViewRequestType.setImageResource(R.drawable.request_fatafat);
            }
            else if(BusinessType.MEALS.getOrdinal() == rideInfo.businessId){
                holder.textViewCustomerPaid.setVisibility(View.VISIBLE);
                holder.textViewPaidToMerchant.setVisibility(View.GONE);
                holder.textViewPaidByCustomer.setVisibility(View.GONE);

                holder.textViewCustomerPaid.setText("Paid by Customer: Rs. " + rideInfo.customerPaid);
                holder.imageViewRequestType.setImageResource(R.drawable.request_meals);
            }

			
			holder.relative.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					holder = (ViewHolderDriverRides) v.getTag();
					RideDetailsActivity.openedRideInfo = rides.get(holder.id);
					getActivity().startActivity(new Intent(getActivity(), RideDetailsActivity.class));
					getActivity().overridePendingTransition(R.anim.right_in, R.anim.right_out);
				}
			});
			
			return convertView;
		}

	}
	


	/**
	 * ASync for get rides from server
	 */
	public void getRidesAsync(final Activity activity) {
		if(!HomeActivity.checkIfUserDataNull(activity)) {
			if (fetchRidesClient == null) {
				if (AppStatus.getInstance(activity).isOnline(activity)) {
					progressBar.setVisibility(View.VISIBLE);
					textViewInfoDisplay.setVisibility(View.GONE);
					RequestParams params = new RequestParams();
					params.put("access_token", Data.userData.accessToken);
					params.put("current_mode", "1");
					fetchRidesClient = Data.getClient();
					fetchRidesClient.post(Data.SERVER_URL + "/booking_history", params,
							new CustomAsyncHttpResponseHandler() {
								private JSONObject jObj;

								@Override
								public void onFailure(Throwable arg3) {
									Log.e("request fail", arg3.toString());
									progressBar.setVisibility(View.GONE);
									updateListData("Some error occurred. Tap to retry", true);
								}

								@Override
								public void onSuccess(String response) {
									Log.i("Server response", "response = " + response);

									try {
										jObj = new JSONObject(response);
										if (!jObj.isNull("error")) {
											String errorMessage = jObj.getString("error");
											if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
												HomeActivity.logoutUser(activity);
											} else {
												updateListData("Some error occurred. Tap to retry", true);
											}
										} else {
//										{
//										    "booking_data": [
//                                        {
//                                            "id": 13279,
//                                            "engagement_id": 13279,
//                                            "business_id": 1,
//                                            "from": "2387, Sector 19C, Chandigarh 160019, India",
//                                            "to": "SCO 27, Madhya Marg, Sector 26, Chandigarh 160019, India",
//                                            "fare": 47,
//                                            "distance": 1.6,
//                                            "ride_time": 3,
//                                            "wait_time": 0,
//                                            "customer_paid": 44,
//                                            "time": "2015-06-15 15:08:23",
//                                            "subsidy": 0,
//                                            "balance": 3,
//                                            "coupon_used": 0,
//                                            "payment_mode": 2,
//                                            "driver_payment_status": 0,
//                                            "status_string": "Ride Completed"
//                                        }
//										    ]
//										}

											JSONArray bookingData = jObj.getJSONArray("booking_data");
											rides.clear();
											DecimalFormat decimalFormat = new DecimalFormat("#.#");
											if (bookingData.length() > 0) {
												for (int i = 0; i < bookingData.length(); i++) {
													JSONObject booData = bookingData.getJSONObject(i);

													int paymentMode = PaymentMode.CASH.getOrdinal();
													if (booData.has("payment_mode")) {
														paymentMode = booData.getInt("payment_mode");
													}

													int businessId = BusinessType.AUTOS.getOrdinal();
													if (booData.has("business_id")) {
														businessId = booData.getInt("business_id");
													}

													String customerPaid = "0";
													if (booData.has("customer_paid")) {
														customerPaid = booData.getString("customer_paid");
													}
													String paidToMerchant = "0";
													if (booData.has("paid_to_merchant")) {
														paidToMerchant = booData.getString("paid_to_merchant");
													}
													String paidByCustomer = "0";
													if (booData.has("paid_by_customer")) {
														paidByCustomer = booData.getString("paid_by_customer");
													}

													int paymentStatus = 0;
													if (booData.has("driver_payment_status")) {
														paymentStatus = booData.getInt("driver_payment_status");
													}

													String statusString = "";
													if (booData.has("status_string")) {
														statusString = booData.getString("status_string");
													}


													RideInfo rideInfo = new RideInfo(booData.getString("id"), booData.getString("from"), booData.getString("to"),
															booData.getString("fare"), customerPaid, booData.getString("balance"), booData.getString("subsidy"),
															decimalFormat.format(booData.getDouble("distance")), booData.getString("ride_time"), booData.getString("wait_time"),
															booData.getString("time"),
															booData.getInt("coupon_used"), paymentMode, businessId, paidToMerchant, paidByCustomer,
															paymentStatus, statusString);
													rides.add(rideInfo);
												}
											}
											updateListData("No rides currently", false);
										}
									} catch (Exception exception) {
										exception.printStackTrace();
										updateListData("Some error occurred. Tap to retry", true);
									}
									progressBar.setVisibility(View.GONE);
								}

								@Override
								public void onFinish() {
									fetchRidesClient = null;
									super.onFinish();
								}


							});
				} else {
					updateListData("No Internet connection. Tap to retry", true);
				}
			}
		}

	}
	
	

}

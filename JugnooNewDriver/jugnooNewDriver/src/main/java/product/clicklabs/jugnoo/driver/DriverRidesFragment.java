package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
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

import com.loopj.android.http.AsyncHttpClient;

import org.json.JSONObject;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.datastructure.BusinessType;
import product.clicklabs.jugnoo.driver.datastructure.PaymentMode;
import product.clicklabs.jugnoo.driver.datastructure.RideInfo;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.BookingHistoryResponse;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import rmn.androidscreenlibrary.ASSL;

public class DriverRidesFragment extends Fragment implements FlurryEventNames {

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
		FlurryEventLogger.event(COMPLETE_RIDES_CHECKED);
		

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
		TextView dateTimeValue, textViewRideId, textViewStatusString, textViewActualFareFare,
				textViewCustomerPaid, textViewAccountBalance, distanceValue, rideTimeValue, waitTimeValue;
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

				holder.dateTimeValue = (TextView) convertView.findViewById(R.id.dateTimeValue); holder.dateTimeValue.setTypeface(Data.latoRegular(getActivity()));
				holder.textViewRideId = (TextView) convertView.findViewById(R.id.textViewRideId); holder.textViewRideId.setTypeface(Data.latoRegular(getActivity()));
                holder.textViewStatusString = (TextView) convertView.findViewById(R.id.textViewStatusString); holder.textViewStatusString.setTypeface(Data.latoRegular(getActivity()));
				holder.textViewActualFareFare = (TextView) convertView.findViewById(R.id.textViewActualFareFare); holder.textViewActualFareFare.setTypeface(Data.latoRegular(getActivity()));
				holder.textViewCustomerPaid = (TextView) convertView.findViewById(R.id.textViewCustomerPaid); holder.textViewCustomerPaid.setTypeface(Data.latoRegular(getActivity()));
				holder.textViewAccountBalance = (TextView) convertView.findViewById(R.id.textViewAccountBalance); holder.textViewAccountBalance.setTypeface(Data.latoRegular(getActivity()));
				holder.distanceValue = (TextView) convertView.findViewById(R.id.distanceValue); holder.distanceValue.setTypeface(Data.latoRegular(getActivity()));
				holder.rideTimeValue = (TextView) convertView.findViewById(R.id.rideTimeValue); holder.rideTimeValue.setTypeface(Data.latoRegular(getActivity()));
				holder.waitTimeValue = (TextView) convertView.findViewById(R.id.waitTimeValue); holder.waitTimeValue.setTypeface(Data.latoRegular(getActivity()));
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
				holder.textViewAccountBalance.setTextColor(getActivity().getResources().getColor(R.color.red_status));
			}
			else{
				holder.textViewAccountBalance.setTextColor(getActivity().getResources().getColor(R.color.bg_grey_opaque));
			}
			holder.textViewAccountBalance.setText(getResources().getString(R.string.rupee)+" "+ rideInfo.accountBalance);
			

			holder.textViewActualFareFare.setText(getResources().getString(R.string.rupee)+" "+rideInfo.actualFare);
			
			holder.distanceValue.setText(rideInfo.distance + " km");
			holder.rideTimeValue.setText(rideInfo.rideTime + " min");

			if("0".equalsIgnoreCase(rideInfo.waitTime)){
//				holder.waitTimeValue.setText("");
			}
			else{
//				holder.waitTimeValue.setText("Wait: "+rideInfo.waitTime + " min");
			}


//
//            if(BusinessType.AUTOS.getOrdinal() == rideInfo.businessId){
//                holder.textViewCustomerPaid.setText(getResources().getString(R.string.rupee)+" "+rideInfo.customerPaid);
//                holder.imageViewRequestType.setImageResource(R.drawable.request_autos);
//            }
//            else if(BusinessType.FATAFAT.getOrdinal() == rideInfo.businessId){
//                holder.textViewCustomerPaid.setText(getResources().getString(R.string.rupee)+" "+rideInfo.paidByCustomer);
//                holder.imageViewRequestType.setImageResource(R.drawable.request_fatafat);
//            }
//            else if(BusinessType.MEALS.getOrdinal() == rideInfo.businessId){
//                holder.textViewCustomerPaid.setText(getResources().getString(R.string.rupee)+" "+ rideInfo.customerPaid);
//                holder.imageViewRequestType.setImageResource(R.drawable.request_meals);
//            }

			
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
	

//	Retrofit

	private void getRidesAsync(final Activity activity){
		progressBar.setVisibility(View.VISIBLE);
		RestClient.getApiServices().bookingHistory(Data.userData.accessToken, "1",
				new Callback<BookingHistoryResponse>() {
					@Override
					public void success(BookingHistoryResponse bookingHistoryResponse, Response response) {
						try {
							String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
							JSONObject jObj;
							jObj = new JSONObject(jsonString);
							if (!jObj.isNull("error")) {
								String errorMessage = jObj.getString("error");
								if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
									HomeActivity.logoutUser(activity);
								} else {
									updateListData("Some error occurred. Tap to retry", true);
								}

							} else {

								for (int i = 0; i < bookingHistoryResponse.getBookingData().size(); i++) {
									BookingHistoryResponse.BookingData data = bookingHistoryResponse.getBookingData().get(i);
									RideInfo rideInfo = new RideInfo(data.getId(), data.getFrom(), data.getTo(), data.getFare(),
											data.getCustomerPaid(), data.getBalance(), data.getSubsidy(), data.getDistance(),
											data.getRideTime(), data.getWaitTime(), data.getTime(), data.getCouponUsed(), data.getPaymentMode(),
											data.getBusinessId(), data.getPaidToMerchant(), data.getPaidByCustomer(), data.getDriverPaymentStatus(),
											data.getStatusString(), data.getConvenienceCharges(), data.getLuggageCharges(), data.getFareFactorApplied(),
											data.getFareFactorValue(), data.getAcceptSubsidy(), data.getCancelSubsidy(), data.getAccountBalance(),
											data.getActualFare());
									rides.add(rideInfo);
								}
								updateListData("No rides currently", false);
							}
						}catch (Exception exception){
							exception.printStackTrace();
							updateListData("Some error occurred. Tap to retry", true);
						}
						progressBar.setVisibility(View.GONE);
					}


					@Override
					public void failure(RetrofitError error) {
						progressBar.setVisibility(View.GONE);
						updateListData("Some error occurred. Tap to retry", true);
					}
				});
	}

}

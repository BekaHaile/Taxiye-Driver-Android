package product.clicklabs.jugnoo.driver;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import product.clicklabs.jugnoo.driver.datastructure.PaymentMode;
import product.clicklabs.jugnoo.driver.datastructure.RideInfo;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

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
		textViewInfoDisplay = (TextView) rootView.findViewById(R.id.textViewInfoDisplay); textViewInfoDisplay.setTypeface(Data.regularFont(getActivity()));
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
		TextView textViewCustomerPaid, textViewFare, textViewBalance, textViewJugnooSubsidy;
		TextView fromText, fromValue, toText, toValue, distanceValue, rideTimeValue, dateTimeValue;
		ImageView couponImg, jugnooCashImg;
		LinearLayout relative;
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
				
				holder.textViewCustomerPaid = (TextView) convertView.findViewById(R.id.textViewCustomerPaid); holder.textViewCustomerPaid.setTypeface(Data.regularFont(getActivity()));
				holder.textViewFare = (TextView) convertView.findViewById(R.id.textViewFare); holder.textViewFare.setTypeface(Data.regularFont(getActivity()));
				holder.textViewBalance = (TextView) convertView.findViewById(R.id.textViewBalance); holder.textViewBalance.setTypeface(Data.regularFont(getActivity()));
				holder.textViewJugnooSubsidy = (TextView) convertView.findViewById(R.id.textViewJugnooSubsidy); holder.textViewJugnooSubsidy.setTypeface(Data.regularFont(getActivity()));
				
				holder.fromText = (TextView) convertView.findViewById(R.id.fromText); holder.fromText.setTypeface(Data.regularFont(getActivity()), Typeface.BOLD);
				holder.fromValue = (TextView) convertView.findViewById(R.id.fromValue); holder.fromValue.setTypeface(Data.regularFont(getActivity()));
				holder.toText = (TextView) convertView.findViewById(R.id.toText); holder.toText.setTypeface(Data.regularFont(getActivity()), Typeface.BOLD);
				holder.toValue = (TextView) convertView.findViewById(R.id.toValue); holder.toValue.setTypeface(Data.regularFont(getActivity()));
				holder.distanceValue = (TextView) convertView.findViewById(R.id.distanceValue); holder.distanceValue.setTypeface(Data.regularFont(getActivity()));
				holder.rideTimeValue = (TextView) convertView.findViewById(R.id.rideTimeValue); holder.rideTimeValue.setTypeface(Data.regularFont(getActivity()));
				holder.dateTimeValue = (TextView) convertView.findViewById(R.id.dateTimeValue); holder.dateTimeValue.setTypeface(Data.regularFont(getActivity()));
				
				holder.couponImg = (ImageView) convertView.findViewById(R.id.couponImg);
				holder.jugnooCashImg = (ImageView) convertView.findViewById(R.id.jugnooCashImg);
				
				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative); 
				
				holder.relative.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderDriverRides) convertView.getTag();
			}
			
			
			RideInfo rideInfo = rides.get(position);
			
			holder.id = position;
			
			holder.textViewCustomerPaid.setText("Customer Paid: Rs. " + rideInfo.customerPaid);
			holder.textViewFare.setText("Fare: Rs. "+rideInfo.fare);
			holder.textViewBalance.setText("Bal. from Jugnoo: Rs. " + rideInfo.balance);
			
			holder.fromValue.setText(rideInfo.fromLocation);
			holder.toValue.setText(rideInfo.toLocation);
			
			holder.distanceValue.setText(rideInfo.distance + " km");
			holder.rideTimeValue.setText(rideInfo.rideTime + " min");
			holder.dateTimeValue.setText(DateOperations.convertDate(DateOperations.utcToLocal(rideInfo.dateTime)));
			
			if("0".equalsIgnoreCase(rideInfo.subsidy)){
				holder.textViewJugnooSubsidy.setVisibility(View.GONE);
			}
			else{
				holder.textViewJugnooSubsidy.setVisibility(View.VISIBLE);
				holder.textViewJugnooSubsidy.setText(" + Jugnoo Subsidy: Rs. " + rideInfo.subsidy);
			}
			
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
			
			return convertView;
		}

	}
	


	/**
	 * ASync for get rides from server
	 */
	public void getRidesAsync(final Activity activity) {
		if(fetchRidesClient == null){
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
									if(!jObj.isNull("error")){
										String errorMessage = jObj.getString("error");
										if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
											HomeActivity.logoutUser(activity);
										}
										else{
											updateListData("Some error occurred. Tap to retry", true);
										}
									}
									else{
//										{
//										    "booking_data": [
//										        {
//										            "id": 0,
//										            "from": "1097, Madhya Marg, 28B, Sector 28, Chandigarh 160101, India",
//										            "to": "1097, Madhya Marg, 28B, Sector 28, Chandigarh 160101, India",
//										            "fare": 26,
//										            "distance": 0,
//										            "ride_time": 1,
//										            "wait_time": 0,
//										            "customer_paid": 26,
//										            "time": "2015-02-05 15:16:59",
//										            "subsidy": 0,
//										            "balance": 4,
//										            "coupon_used": 0
//										        }
//										    ]
//										}
										
										JSONArray bookingData = jObj.getJSONArray("booking_data");
										rides.clear();
										DecimalFormat decimalFormat = new DecimalFormat("#.#");
										if(bookingData.length() > 0){
											for(int i=0; i<bookingData.length(); i++){
												JSONObject booData = bookingData.getJSONObject(i);
												
												int paymentMode = PaymentMode.CASH.getOrdinal();
												if(booData.has("payment_mode")){
													paymentMode = booData.getInt("payment_mode");
												}
												
												RideInfo rideInfo = new RideInfo(booData.getString("id"), booData.getString("from"), booData.getString("to"), 
														booData.getString("fare"), booData.getString("customer_paid"),  booData.getString("balance"), booData.getString("subsidy"), 
														decimalFormat.format(booData.getDouble("distance")), booData.getString("ride_time"), booData.getString("wait_time"), booData.getString("time"), 
														booData.getInt("coupon_used"), paymentMode);
												rides.add(rideInfo);
											}
										}
										updateListData("No rides currently", false);
									}
								}  catch (Exception exception) {
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
			}
			else {
				updateListData("No Internet connection. Tap to retry", true);
			}
		}

	}
	
	

}

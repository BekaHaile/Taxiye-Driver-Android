package product.clicklabs.jugnoo;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import product.clicklabs.jugnoo.datastructure.PaymentMode;
import product.clicklabs.jugnoo.datastructure.RideInfo;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DateOperations;
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

public class RideHistoryFragment extends Fragment {

	ProgressBar progressBar;
	TextView textViewInfoDisplay;
	ListView listView;
	
	RideHistoryListAdapter rideHistoryListAdapter;
	
	RelativeLayout main;
	
	AsyncHttpClient fetchRidesClient;

	ArrayList<RideInfo> rides = new ArrayList<RideInfo>();
	
	public RideHistoryFragment() {
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
		
		rideHistoryListAdapter = new RideHistoryListAdapter(getActivity());
		listView.setAdapter(rideHistoryListAdapter);
		
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
			rideHistoryListAdapter.notifyDataSetChanged();
		}
		else{
			if(rides.size() == 0){
				textViewInfoDisplay.setText(message);
				textViewInfoDisplay.setVisibility(View.VISIBLE);
			}
			else{
				textViewInfoDisplay.setVisibility(View.GONE);
			}
			rideHistoryListAdapter.notifyDataSetChanged();
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

	
	class ViewHolderRideHistory {
		TextView fromText, fromValue, toText, toValue, distanceValue, timeValue, fareValue, balanceValue;
		ImageView couponImg, jugnooCashImg;
		LinearLayout relative;
		int id;
	}

	class RideHistoryListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderRideHistory holder;
		Context context;
		public RideHistoryListAdapter(Context context) {
			this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.context = context;
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
				holder = new ViewHolderRideHistory();
				convertView = mInflater.inflate(R.layout.list_item_ride_history, null);
				
				holder.fromText = (TextView) convertView.findViewById(R.id.fromText); holder.fromText.setTypeface(Data.latoRegular(context), Typeface.BOLD);
				holder.fromValue = (TextView) convertView.findViewById(R.id.fromValue); holder.fromValue.setTypeface(Data.latoRegular(context));
				holder.toText = (TextView) convertView.findViewById(R.id.toText); holder.toText.setTypeface(Data.latoRegular(context), Typeface.BOLD);
				holder.toValue = (TextView) convertView.findViewById(R.id.toValue); holder.toValue.setTypeface(Data.latoRegular(context));
				holder.distanceValue = (TextView) convertView.findViewById(R.id.distanceValue); holder.distanceValue.setTypeface(Data.latoRegular(context));
				holder.timeValue = (TextView) convertView.findViewById(R.id.timeValue); holder.timeValue.setTypeface(Data.latoRegular(context));
				holder.fareValue = (TextView) convertView.findViewById(R.id.fareValue); holder.fareValue.setTypeface(Data.latoRegular(context), Typeface.BOLD);
				holder.balanceValue = (TextView) convertView.findViewById(R.id.balanceValue); holder.balanceValue.setTypeface(Data.latoRegular(context), Typeface.BOLD);
				
				
				holder.couponImg = (ImageView) convertView.findViewById(R.id.couponImg);
				holder.jugnooCashImg = (ImageView) convertView.findViewById(R.id.jugnooCashImg);
				
				holder.relative = (LinearLayout) convertView.findViewById(R.id.relative); 
				
				holder.relative.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderRideHistory) convertView.getTag();
			}
			
			
			RideInfo booking = rides.get(position);
			
			holder.id = position;
			
			
			holder.fromValue.setText(booking.fromLocation);
			holder.toValue.setText(booking.toLocation);
			holder.distanceValue.setText(booking.distance + " km");
			holder.timeValue.setText(DateOperations.convertDate(DateOperations.utcToLocal(booking.time)));
			holder.fareValue.setText("Rs. "+booking.fare);
			holder.balanceValue.setVisibility(View.GONE);
			
			if(1 == booking.couponUsed){
				holder.couponImg.setVisibility(View.VISIBLE);
			}
			else{
				holder.couponImg.setVisibility(View.GONE);
			}
			
			if(PaymentMode.WALLET.getOrdinal() == booking.paymentMode){
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
				params.put("current_mode", "0");
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
								Log.d("Server response", "response = " + response);
								
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
										JSONArray bookingData = jObj.getJSONArray("booking_data");
										rides.clear();
										if(bookingData.length() > 0){
											for(int i=0; i<bookingData.length(); i++){
												JSONObject booData = bookingData.getJSONObject(i);
												
												int paymentMode = PaymentMode.CASH.getOrdinal();
												if(booData.has("payment_mode")){
													paymentMode = booData.getInt("payment_mode");
												}
												
												rides.add(new RideInfo(booData.getString("id"), booData.getString("from"),
														booData.getString("to"), booData.getString("fare"), booData.getString("distance"),
														booData.getString("time"), "", booData.getInt("coupon_used"), paymentMode));
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

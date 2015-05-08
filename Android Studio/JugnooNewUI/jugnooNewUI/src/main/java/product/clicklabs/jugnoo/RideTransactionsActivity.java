package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.FutureSchedule;
import product.clicklabs.jugnoo.datastructure.RideInfoNew;
import product.clicklabs.jugnoo.datastructure.ScheduleCancelListener;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Fonts;
import rmn.androidscreenlibrary.ASSL;

public class RideTransactionsActivity extends Activity {

	RelativeLayout relative;
	
	TextView textViewTitle;
	ImageView imageViewBack;
	
	ListView listViewRideTransactions;
	TextView textViewInfo;
	ProgressBar progressBarList;
	
	RideTransactionAdapter rideTransactionAdapter;
	
	RelativeLayout relativeLayoutShowMore;
	TextView textViewShowMore;
	
	DecimalFormat decimalFormat = new DecimalFormat("#.#");
	DecimalFormat decimalFormatNoDec = new DecimalFormat("#");
	
	@Override
	protected void onResume() {
		super.onResume();
		HomeActivity.checkForAccessTokenChange(this);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rides_transactions);
		
		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(this, (ViewGroup) relative, 1134, 720, false);
		
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.latoRegular(this), Typeface.BOLD);
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
		
		listViewRideTransactions = (ListView) findViewById(R.id.listViewRideTransactions);
		textViewInfo = (TextView) findViewById(R.id.textViewInfo); textViewInfo.setTypeface(Fonts.latoRegular(this));
		progressBarList = (ProgressBar) findViewById(R.id.progressBarList);
		textViewInfo.setVisibility(View.GONE);
		progressBarList.setVisibility(View.GONE);
		
		
		LinearLayout viewF = (LinearLayout) getLayoutInflater().inflate(R.layout.list_item_show_more, null);
		listViewRideTransactions.addFooterView(viewF);
		viewF.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
		ASSL.DoMagic(viewF);
		
		relativeLayoutShowMore = (RelativeLayout) viewF.findViewById(R.id.relativeLayoutShowMore);
		textViewShowMore = (TextView) viewF.findViewById(R.id.textViewShowMore); textViewShowMore.setTypeface(Fonts.latoLight(this), Typeface.BOLD);
		textViewShowMore.setText("Show More");
		relativeLayoutShowMore.setVisibility(View.GONE);
		
		rideTransactionAdapter = new RideTransactionAdapter(this);
		listViewRideTransactions.setAdapter(rideTransactionAdapter);
		rideTransactionAdapter.notifyDataSetChanged();
		
		
		imageViewBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});
		
		textViewInfo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getRecentRidesAPI(RideTransactionsActivity.this, true);
			}
		});
		
		
		relativeLayoutShowMore.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getRecentRidesAPI(RideTransactionsActivity.this, false);
			}
		});
		
		
	}

	
	public void performBackPressed(){
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}
	
	@Override
	public void onBackPressed() {
		performBackPressed();
	}
	
	
	@Override
	protected void onDestroy() {
        ASSL.closeActivity(relative);
        System.gc();
		super.onDestroy();
	}
	
	
	public void getRecentRidesAPI(final Activity activity, final boolean refresh) {
		progressBarList.setVisibility(View.GONE);
		if(AppStatus.getInstance(activity).isOnline(activity)) {
			
			if(refresh){
				AccountActivity.rideInfosList.clear();
				AccountActivity.futureSchedule = null;
			}
			
			progressBarList.setVisibility(View.VISIBLE);
			textViewInfo.setVisibility(View.GONE);
			
			RequestParams params = new RequestParams();
		
			params.put("access_token", Data.userData.accessToken);
			params.put("start_from", ""+AccountActivity.rideInfosList.size());
			
			AsyncHttpClient client = Data.getClient();
			client.post(Config.getServerUrl() + "/get_recent_rides", params,
					new CustomAsyncHttpResponseHandler() {
					private JSONObject jObj;

						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
							updateListData("Some error occurred, tap to retry", true);
							progressBarList.setVisibility(View.GONE);
						}

						@Override
						public void onSuccess(String response) {
							Log.i("Server response", "response = " + response);
							try {
								
								jObj = new JSONObject(response);
								if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
									int flag = jObj.getInt("flag");
									if(ApiResponseFlags.RECENT_RIDES.getOrdinal() == flag){
										
										AccountActivity.totalRides = jObj.getInt("num_rides");
										
										if(jObj.has("schedule")){
											JSONObject jSchedule = jObj.getJSONObject("schedule");
											AccountActivity.futureSchedule = new FutureSchedule(jSchedule.getString("pickup_id"), 
													jSchedule.getString("address"), 
													jSchedule.getString("pickup_date"), 
													jSchedule.getString("pickup_time"), 
													new LatLng(jSchedule.getDouble("latitude"), jSchedule.getDouble("longitude")), 
													jSchedule.getInt("modifiable"),
													jSchedule.getInt("status"));
											AccountActivity.totalRides = AccountActivity.totalRides + 1;
										}
										else{
											if(AccountActivity.rideInfosList.size() == 0){
												AccountActivity.futureSchedule = null;
											}
										}
										
										
										if(jObj.has("rides")){
											JSONArray jRidesArr = jObj.getJSONArray("rides");
											for(int i=0; i<jRidesArr.length(); i++){
												JSONObject jRide = jRidesArr.getJSONObject(i);
												AccountActivity.rideInfosList.add(new RideInfoNew(jRide.getString("pickup_address"), 
														jRide.getString("drop_address"), 
														jRide.getDouble("amount"), 
														jRide.getDouble("distance"), 
														jRide.getDouble("ride_time"), 
														jRide.getString("date")));
											}
										}
										
										updateListData("No rides currently", false);
										
									}
									else{
										updateListData("Some error occurred, tap to retry", true);
									}
								}
								else{
									updateListData("Some error occurred, tap to retry", true);
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								updateListData("Some error occurred, tap to retry", true);
							}
							progressBarList.setVisibility(View.GONE);
						}
					});
		}
		else {
			updateListData("No internet connection, tap to retry", true);
		}
	}
	
	public void updateListData(String message, boolean errorOccurred){
		
		if(errorOccurred){
			textViewInfo.setText(message);
			textViewInfo.setVisibility(View.VISIBLE);
			
			AccountActivity.rideInfosList.clear();
			rideTransactionAdapter.notifyDataSetChanged();
		}
		else{
			if(AccountActivity.rideInfosList.size() == 0 && AccountActivity.futureSchedule == null){
				textViewInfo.setVisibility(View.VISIBLE);
				textViewInfo.setText(message);
			}
			else{
				textViewInfo.setVisibility(View.GONE);
			}
			rideTransactionAdapter.notifyDataSetChanged();
		}
	}
	
	
	class ViewHolderRideTransaction {
		TextView textViewPickupAt, textViewFrom, textViewFromValue, textViewTo, 
		textViewToValue, textViewDetails, textViewDetailsValue, textViewAmount, textViewCancel;
		RelativeLayout relativeLayoutCancel, relativeLayoutTo;
		RelativeLayout relative;
		int id;
	}

	class RideTransactionAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderRideTransaction holder;
		Context context;
		public RideTransactionAdapter(Context context) {
			this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.context = context;
		}

		@Override
		public int getCount() {
			if(AccountActivity.futureSchedule == null){
				return AccountActivity.rideInfosList.size();
			}
			else{
				return AccountActivity.rideInfosList.size()+1;
			}
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
				holder = new ViewHolderRideTransaction();
				convertView = mInflater.inflate(R.layout.list_item_ride_transaction, null);
				
				holder.textViewPickupAt = (TextView) convertView.findViewById(R.id.textViewPickupAt); holder.textViewPickupAt.setTypeface(Fonts.latoRegular(context));
				holder.textViewFrom = (TextView) convertView.findViewById(R.id.textViewFrom); holder.textViewFrom.setTypeface(Fonts.latoRegular(context));
				holder.textViewFromValue = (TextView) convertView.findViewById(R.id.textViewFromValue); holder.textViewFromValue.setTypeface(Fonts.latoRegular(context));
				holder.textViewTo = (TextView) convertView.findViewById(R.id.textViewTo); holder.textViewTo.setTypeface(Fonts.latoRegular(context));
				holder.textViewToValue = (TextView) convertView.findViewById(R.id.textViewToValue); holder.textViewToValue.setTypeface(Fonts.latoRegular(context));
				holder.textViewDetails = (TextView) convertView.findViewById(R.id.textViewDetails); holder.textViewDetails.setTypeface(Fonts.latoRegular(context));
				holder.textViewDetailsValue = (TextView) convertView.findViewById(R.id.textViewDetailsValue); holder.textViewDetailsValue.setTypeface(Fonts.latoRegular(context));
				holder.textViewAmount = (TextView) convertView.findViewById(R.id.textViewAmount); holder.textViewAmount.setTypeface(Fonts.latoRegular(context), Typeface.BOLD);
				holder.textViewCancel = (TextView) convertView.findViewById(R.id.textViewCancel); holder.textViewCancel.setTypeface(Fonts.latoRegular(context));
				
				holder.relative = (RelativeLayout) convertView.findViewById(R.id.relative);
				holder.relativeLayoutCancel = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutCancel);
				holder.relativeLayoutTo = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutTo);
				
				holder.relative.setTag(holder);
				holder.relativeLayoutCancel.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderRideTransaction) convertView.getTag();
			}
			
			
			holder.id = position;
			
			if(AccountActivity.futureSchedule != null){
				if(position == 0){
					holder.textViewPickupAt.setVisibility(View.VISIBLE);
					holder.relativeLayoutTo.setVisibility(View.GONE);
					holder.textViewAmount.setVisibility(View.GONE);
						
					holder.textViewFromValue.setText(AccountActivity.futureSchedule.pickupAddress);
					holder.textViewDetails.setText("Date: ");
					holder.textViewDetailsValue.setText(AccountActivity.futureSchedule.pickupDate + ", " + AccountActivity.futureSchedule.pickupTime);
						
					if(AccountActivity.futureSchedule.modifiable == 1){
						holder.relativeLayoutCancel.setVisibility(View.VISIBLE);
					}
					else{
						holder.relativeLayoutCancel.setVisibility(View.GONE);
					}
				}
				else{
					RideInfoNew rideInfoNew = AccountActivity.rideInfosList.get(position-1);
					
					holder.textViewPickupAt.setVisibility(View.GONE);
					holder.relativeLayoutTo.setVisibility(View.VISIBLE);
					holder.textViewAmount.setVisibility(View.VISIBLE);
					holder.relativeLayoutCancel.setVisibility(View.GONE);
					
					holder.textViewFromValue.setText(rideInfoNew.pickupAddress);
					holder.textViewToValue.setText(rideInfoNew.dropAddress);
					holder.textViewDetails.setText("Details: ");
					if(rideInfoNew.rideTime == 1){
						holder.textViewDetailsValue.setText(decimalFormat.format(rideInfoNew.distance) + " km, " 
								+ decimalFormatNoDec.format(rideInfoNew.rideTime) + " minute, "+rideInfoNew.date);
					}
					else{
						holder.textViewDetailsValue.setText(decimalFormat.format(rideInfoNew.distance) + " km, " 
								+ decimalFormatNoDec.format(rideInfoNew.rideTime) + " minutes, "+rideInfoNew.date);
					}
					holder.textViewAmount.setText(getResources().getString(R.string.rupee)+" "+decimalFormatNoDec.format(rideInfoNew.amount));
				}
			}
			else{
				RideInfoNew rideInfoNew = AccountActivity.rideInfosList.get(position);
				
				holder.textViewPickupAt.setVisibility(View.GONE);
				holder.relativeLayoutTo.setVisibility(View.VISIBLE);
				holder.textViewAmount.setVisibility(View.VISIBLE);
				holder.relativeLayoutCancel.setVisibility(View.GONE);
				
				holder.textViewFromValue.setText(rideInfoNew.pickupAddress);
				holder.textViewToValue.setText(rideInfoNew.dropAddress);
				holder.textViewDetails.setText("Details: ");
				if(rideInfoNew.rideTime == 1){
					holder.textViewDetailsValue.setText(decimalFormat.format(rideInfoNew.distance) + " km, " 
							+ decimalFormatNoDec.format(rideInfoNew.rideTime) + " minute, "+rideInfoNew.date);
				}
				else{
					holder.textViewDetailsValue.setText(decimalFormat.format(rideInfoNew.distance) + " km, " 
							+ decimalFormatNoDec.format(rideInfoNew.rideTime) + " minutes, "+rideInfoNew.date);
				}
				holder.textViewAmount.setText(getResources().getString(R.string.rupee)+" "+decimalFormatNoDec.format(rideInfoNew.amount));
			}
			
			holder.relativeLayoutCancel.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(AccountActivity.futureSchedule != null){
						DialogPopup.alertPopupTwoButtonsWithListeners(RideTransactionsActivity.this, "Cancel Scheduled Ride", "Are you sure you want to cancel the scheduled ride?", "OK", "Cancel",
								new View.OnClickListener() {
									
									@Override
									public void onClick(View v) {
										if(AccountActivity.futureSchedule != null){
											AccountActivity.removeScheduledRideAPI(RideTransactionsActivity.this, AccountActivity.futureSchedule.pickupId, new ScheduleCancelListener() {
												
												@Override
												public void onCancelSuccess() {
													getRecentRidesAPI(RideTransactionsActivity.this, true);
												}
											});
										}
									}
								}, 
								new View.OnClickListener() {
									
									@Override
									public void onClick(View v) {
									}
								}, true, true);
					}
					
				}
			});
			
			
			return convertView;
		}

		
		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
			if(AccountActivity.totalRides > getCount()){
				relativeLayoutShowMore.setVisibility(View.VISIBLE);
			}
			else{
				relativeLayoutShowMore.setVisibility(View.GONE);
			}
		}
		
	}
	
}

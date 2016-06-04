package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import java.util.ArrayList;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.FeedbackMode;
import product.clicklabs.jugnoo.datastructure.FutureSchedule;
import product.clicklabs.jugnoo.datastructure.RideInfo;
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
	Button buttonGetRide;
	
	RideTransactionAdapter rideTransactionAdapter;
	
	RelativeLayout relativeLayoutShowMore;
	TextView textViewShowMore;

    FutureSchedule futureSchedule = null;
    ArrayList<RideInfo> rideInfosList = new ArrayList<RideInfo>();
    int totalRides = 0;

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

        futureSchedule = null;
        rideInfosList = new ArrayList<RideInfo>();
        totalRides = 0;

		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(this, (ViewGroup) relative, 1134, 720, false);
		
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.latoRegular(this), Typeface.BOLD);
		imageViewBack = (ImageView) findViewById(R.id.imageViewBack);
		
		listViewRideTransactions = (ListView) findViewById(R.id.listViewRideTransactions);
		textViewInfo = (TextView) findViewById(R.id.textViewInfo); textViewInfo.setTypeface(Fonts.latoRegular(this));
		progressBarList = (ProgressBar) findViewById(R.id.progressBarList);
		buttonGetRide = (Button) findViewById(R.id.buttonGetRide); buttonGetRide.setTypeface(Fonts.latoRegular(this));
		textViewInfo.setVisibility(View.GONE);
		progressBarList.setVisibility(View.GONE);
		buttonGetRide.setVisibility(View.GONE);
		
		
		LinearLayout viewF = (LinearLayout) getLayoutInflater().inflate(R.layout.list_item_show_more, null);
		listViewRideTransactions.addFooterView(viewF);
		viewF.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
		ASSL.DoMagic(viewF);
		
		relativeLayoutShowMore = (RelativeLayout) viewF.findViewById(R.id.relativeLayoutShowMore);
		textViewShowMore = (TextView) viewF.findViewById(R.id.textViewShowMore); textViewShowMore.setTypeface(Fonts.latoLight(this), Typeface.BOLD);
		textViewShowMore.setText("Show More");

		rideTransactionAdapter = new RideTransactionAdapter(this);
		listViewRideTransactions.setAdapter(rideTransactionAdapter);
		rideTransactionAdapter.notifyDataSetChanged();

        relativeLayoutShowMore.setVisibility(View.GONE);
		
		
		imageViewBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});

		buttonGetRide.setOnClickListener(new OnClickListener() {
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


        getRecentRidesAPI(RideTransactionsActivity.this, true);

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
				rideInfosList.clear();
				futureSchedule = null;
			}
			
			progressBarList.setVisibility(View.VISIBLE);
			textViewInfo.setVisibility(View.GONE);
			
			RequestParams params = new RequestParams();
		
			params.put("access_token", Data.userData.accessToken);
			params.put("start_from", ""+rideInfosList.size());
			
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
										
										totalRides = jObj.getInt("num_rides");
										
										if(jObj.has("schedule")){
											JSONObject jSchedule = jObj.getJSONObject("schedule");
											futureSchedule = new FutureSchedule(jSchedule.getString("pickup_id"),
													jSchedule.getString("address"), 
													jSchedule.getString("pickup_date"), 
													jSchedule.getString("pickup_time"), 
													new LatLng(jSchedule.getDouble("latitude"), jSchedule.getDouble("longitude")), 
													jSchedule.getInt("modifiable"),
													jSchedule.getInt("status"));
											totalRides = totalRides + 1;
										}
										else{
											if(rideInfosList.size() == 0){
												futureSchedule = null;
											}
										}
										
										
										if(jObj.has("rides")){
											JSONArray jRidesArr = jObj.getJSONArray("rides");
											for(int i=0; i<jRidesArr.length(); i++){
												JSONObject jRide = jRidesArr.getJSONObject(i);
												rideInfosList.add(new RideInfo(jRide.getString("pickup_address"),
														jRide.getString("drop_address"), 
														jRide.getDouble("amount"), 
														jRide.getDouble("distance"), 
														jRide.getDouble("ride_time"), 
														jRide.getString("date")));
											}
										}

										updateListData("You haven't tried Jugnoo yet.", false);
										
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
			buttonGetRide.setVisibility(View.GONE);
			
			rideInfosList.clear();
			rideTransactionAdapter.notifyDataSetChanged();
		}
		else{
			if(rideInfosList.size() == 0 && futureSchedule == null){
				textViewInfo.setVisibility(View.VISIBLE);
				textViewInfo.setText(message);
				buttonGetRide.setVisibility(View.VISIBLE);
			}
			else{
				textViewInfo.setVisibility(View.GONE);
				buttonGetRide.setVisibility(View.GONE);
			}
			rideTransactionAdapter.notifyDataSetChanged();
		}
	}
	
	
	class ViewHolderRideTransaction {
		TextView textViewPickupAt, textViewFrom, textViewFromValue, textViewTo, 
		textViewToValue, textViewDetails, textViewDetailsValue, textViewAmount, textViewCancel, textViewRateExperience;
		RelativeLayout relativeLayoutCancel, relativeLayoutTo, relativeLayoutRateExperience;
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
			if(futureSchedule == null){
				return rideInfosList.size();
			}
			else{
				return rideInfosList.size()+1;
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
		public View getView(int position, View convertView, ViewGroup parent) {
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
				holder.textViewRateExperience = (TextView) convertView.findViewById(R.id.textViewRateExperience); holder.textViewRateExperience.setTypeface(Fonts.latoRegular(context));
				
				holder.relative = (RelativeLayout) convertView.findViewById(R.id.relative);
				holder.relativeLayoutCancel = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutCancel);
				holder.relativeLayoutTo = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutTo);
                holder.relativeLayoutRateExperience = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutRateExperience);
				
				holder.relative.setTag(holder);
				holder.relativeLayoutCancel.setTag(holder);
                holder.relativeLayoutRateExperience.setTag(holder);
				
				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolderRideTransaction) convertView.getTag();
			}
			
			
			holder.id = position;
			
			if(futureSchedule != null){
				if(position == 0){
					holder.textViewPickupAt.setVisibility(View.VISIBLE);
					holder.relativeLayoutTo.setVisibility(View.GONE);
                    holder.relativeLayoutRateExperience.setVisibility(View.GONE);

					holder.textViewAmount.setVisibility(View.GONE);
						
					holder.textViewFromValue.setText(futureSchedule.pickupAddress);
					holder.textViewDetails.setText("Date: ");
					holder.textViewDetailsValue.setText(futureSchedule.pickupDate + ", " + futureSchedule.pickupTime);
						
					if(futureSchedule.modifiable == 1){
						holder.relativeLayoutCancel.setVisibility(View.VISIBLE);
					}
					else{
						holder.relativeLayoutCancel.setVisibility(View.GONE);
					}
				}
				else{
					RideInfo rideInfo = rideInfosList.get(position-1);
					
					holder.textViewPickupAt.setVisibility(View.GONE);
					holder.relativeLayoutTo.setVisibility(View.VISIBLE);
					holder.textViewAmount.setVisibility(View.VISIBLE);
					holder.relativeLayoutCancel.setVisibility(View.GONE);
					
					holder.textViewFromValue.setText(rideInfo.pickupAddress);
					holder.textViewToValue.setText(rideInfo.dropAddress);
					holder.textViewDetails.setText("Details: ");
					if(rideInfo.rideTime == 1){
						holder.textViewDetailsValue.setText(decimalFormat.format(rideInfo.distance) + " km, "
								+ decimalFormatNoDec.format(rideInfo.rideTime) + " minute, "+ rideInfo.date);
					}
					else{
						holder.textViewDetailsValue.setText(decimalFormat.format(rideInfo.distance) + " km, "
								+ decimalFormatNoDec.format(rideInfo.rideTime) + " minutes, "+ rideInfo.date);
					}
					holder.textViewAmount.setText(getResources().getString(R.string.rupee)+" "+decimalFormatNoDec.format(rideInfo.amount));

                    if(1 == rideInfo.rideRated){
                        holder.relativeLayoutRateExperience.setVisibility(View.VISIBLE);
                    }
                    else{
                        holder.relativeLayoutRateExperience.setVisibility(View.GONE);
                    }
				}
			}
			else{
				RideInfo rideInfo = rideInfosList.get(position);
				
				holder.textViewPickupAt.setVisibility(View.GONE);
				holder.relativeLayoutTo.setVisibility(View.VISIBLE);
				holder.textViewAmount.setVisibility(View.VISIBLE);
				holder.relativeLayoutCancel.setVisibility(View.GONE);
                holder.relativeLayoutRateExperience.setVisibility(View.VISIBLE);
				
				holder.textViewFromValue.setText(rideInfo.pickupAddress);
				holder.textViewToValue.setText(rideInfo.dropAddress);
				holder.textViewDetails.setText("Details: ");
				if(rideInfo.rideTime == 1){
					holder.textViewDetailsValue.setText(decimalFormat.format(rideInfo.distance) + " km, "
                        + decimalFormatNoDec.format(rideInfo.rideTime) + " minute, " + rideInfo.date);
				}
				else{
					holder.textViewDetailsValue.setText(decimalFormat.format(rideInfo.distance) + " km, "
                        + decimalFormatNoDec.format(rideInfo.rideTime) + " minutes, " + rideInfo.date);
				}
				holder.textViewAmount.setText(getResources().getString(R.string.rupee) + " " + decimalFormatNoDec.format(rideInfo.amount));

                if(1 == rideInfo.rideRated){
                    holder.relativeLayoutRateExperience.setVisibility(View.VISIBLE);
                }
                else{
                    holder.relativeLayoutRateExperience.setVisibility(View.GONE);
                }
			}
			
			holder.relativeLayoutCancel.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(futureSchedule != null){
						DialogPopup.alertPopupTwoButtonsWithListeners(RideTransactionsActivity.this, "Cancel Scheduled Ride", "Are you sure you want to cancel the scheduled ride?", "OK", "Cancel",
								new View.OnClickListener() {
									
									@Override
									public void onClick(View v) {
										if(futureSchedule != null){
											removeScheduledRideAPI(RideTransactionsActivity.this, futureSchedule.pickupId, new ScheduleCancelListener() {
												
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


            holder.relativeLayoutRateExperience.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        holder = (ViewHolderRideTransaction) v.getTag();

                        Intent intent = new Intent(RideTransactionsActivity.this, FeedbackActivity.class);
                        intent.putExtra(FeedbackMode.class.getName(), FeedbackMode.PAST_RIDE.getOrdinal());

                        if(futureSchedule != null) {
                            intent.putExtra("engagement_id", rideInfosList.get(holder.id-1).engagementId);
                        }
                        else{
                            intent.putExtra("engagement_id", rideInfosList.get(holder.id).engagementId);
                        }

                        startActivity(intent);
                        overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

			
			return convertView;
		}

		
		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
			if(totalRides > getCount()){
				relativeLayoutShowMore.setVisibility(View.VISIBLE);
            }
			else{
				relativeLayoutShowMore.setVisibility(View.GONE);
            }
		}
		
	}



    /**
     * ASync for removing scheduled ride from server
     */
    public static void removeScheduledRideAPI(final Activity activity, String pickupId, final ScheduleCancelListener scheduleCancelListener) {
        if (AppStatus.getInstance(activity).isOnline(activity)) {

            DialogPopup.showLoadingDialog(activity, "Loading...");

            RequestParams params = new RequestParams();

            params.put("access_token", Data.userData.accessToken);
            params.put("pickup_id", pickupId);

            product.clicklabs.jugnoo.utils.Log.i("remove_pickup_schedule api params", ">" + params);

            AsyncHttpClient client = Data.getClient();
            client.post(Config.getServerUrl() + "/remove_pickup_schedule", params,
                new CustomAsyncHttpResponseHandler() {
                    private JSONObject jObj;

                    @Override
                    public void onFailure(Throwable arg3) {
                        product.clicklabs.jugnoo.utils.Log.e("request fail", arg3.toString());
                        DialogPopup.dismissLoadingDialog();
                        DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
                    }

                    @Override
                    public void onSuccess(String response) {
                        product.clicklabs.jugnoo.utils.Log.i("Server response", "response = " + response);

                        try {
                            jObj = new JSONObject(response);

                            if(!jObj.isNull("error")){
                                String errorMessage = jObj.getString("error");
                                int flag = jObj.getInt("flag");
                                if(Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())){
                                    HomeActivity.logoutUser(activity);
                                }
                                else if(ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag){
                                    DialogPopup.alertPopup(activity, "", errorMessage);
                                }
                                else{
                                    DialogPopup.alertPopup(activity, "", errorMessage);
                                }
                                DialogPopup.dismissLoadingDialog();
                            }
                            else{
                                int flag = jObj.getInt("flag");
                                if(ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag){
                                    String message = jObj.getString("message");
                                    DialogPopup.alertPopup(activity, "", message);
                                    scheduleCancelListener.onCancelSuccess();
                                }
                                DialogPopup.dismissLoadingDialog();
                            }
                        }  catch (Exception exception) {
                            exception.printStackTrace();
                            DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                            DialogPopup.dismissLoadingDialog();
                        }
                    }
                });
        }
        else {
            DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
        }

    }
	
}

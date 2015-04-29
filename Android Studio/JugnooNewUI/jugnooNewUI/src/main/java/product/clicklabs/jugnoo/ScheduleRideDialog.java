package product.clicklabs.jugnoo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.datastructure.PromotionApplyMode;
import product.clicklabs.jugnoo.datastructure.PromotionDialogEventHandler;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.CustomDateTimePicker;
import product.clicklabs.jugnoo.utils.DateOperations;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.MapUtils;
import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

public class ScheduleRideDialog {

	
	private Dialog dialog;
	
	private FrameLayout frameLayout;
	private RelativeLayout relativeLayoutPickupLoaction, relativeLayoutPickupTime;
	private TextView textViewTitle, textViewPickupLocation, textViewPickupTime;
	private Button btnOk, btnCancel;
	
	private Activity activity;
	private CustomDateTimePicker customDateTimePicker;
	private Calendar selectedScheduleCalendar = Calendar.getInstance();
	private LatLng selectedScheduleLatLng;
	private PromotionDialogEventHandler promotionDialogEventHandler;
	
	public ScheduleRideDialog(Activity activity, Calendar calendar, LatLng latLng, PromotionDialogEventHandler promotionDialogEventHandler){
		this.activity = activity;
		this.selectedScheduleCalendar = calendar;
		this.selectedScheduleLatLng = latLng;
		this.promotionDialogEventHandler = promotionDialogEventHandler;
		
		this.customDateTimePicker = new CustomDateTimePicker(activity,
				new CustomDateTimePicker.ICustomDateTimeListener() {

			@Override
			public void onSet(Dialog dialog, Calendar calendarSelected,
					Date dateSelected, int year, String monthFullName,
					String monthShortName, int monthNumber, int date,
					String weekDayFullName, String weekDayShortName,
					int hour24, int hour12, int min, int sec,
					String AM_PM) {
				
				Calendar currentCalendar = Calendar.getInstance();
				
				if(Data.userData != null){
					currentCalendar.add(Calendar.MINUTE, Data.userData.schedulingLimitMinutes);
				}
				
				Date currentOffsetDate = currentCalendar.getTime();
			    Date selectedDate = calendarSelected.getTime();
			    long diff = selectedDate.getTime() - currentOffsetDate.getTime();
				
			    if(diff > 0){
					selectedScheduleCalendar = calendarSelected;
					setScheduleDateTimeValue(selectedScheduleCalendar);
			    }
			    else{
			    	Toast.makeText(ScheduleRideDialog.this.activity, 
			    			"Schedule time must be after "+Data.userData.schedulingLimitMinutes+" minutes than current time", 
			    			Toast.LENGTH_SHORT).show();
			    }
			}

			@Override
			public void onCancel() {
			}
		});
		
	}
	
	
	public void showDialog(final Activity activity) {
		try {
			dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_schedule_ride);

			frameLayout = (FrameLayout) dialog.findViewById(R.id.rv);
			new ASSL(activity, frameLayout, 1134, 720, true);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);

			relativeLayoutPickupLoaction = (RelativeLayout) dialog.findViewById(R.id.relativeLayoutPickupLoaction);
			relativeLayoutPickupTime = (RelativeLayout) dialog.findViewById(R.id.relativeLayoutPickupTime);
			
			textViewTitle = (TextView) dialog.findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Data.latoRegular(activity));
			textViewPickupLocation = (TextView) dialog.findViewById(R.id.textViewPickupLocation); textViewPickupLocation.setTypeface(Data.latoRegular(activity));
			textViewPickupTime = (TextView) dialog.findViewById(R.id.textViewPickupTime); textViewPickupTime.setTypeface(Data.latoRegular(activity));
			
			btnOk = (Button) dialog.findViewById(R.id.btnOk); btnOk.setTypeface(Data.latoRegular(activity), Typeface.BOLD); btnOk.setText("Schedule Ride");
			btnCancel = (Button) dialog.findViewById(R.id.btnCancel); btnCancel.setTypeface(Data.latoRegular(activity));

			btnOk.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					insertScheduleRideAsync(activity, selectedScheduleCalendar, selectedScheduleLatLng);
				}
			});
			
			btnCancel.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dismissAlert();
				}
			});
			
			relativeLayoutPickupLoaction.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dismissAlert();
					DialogPopup.dialogBanner(activity, "Please place the pin at the desired pickup location");
				}
			});
			
			relativeLayoutPickupTime.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					customDateTimePicker.set24HourFormat(false);
					customDateTimePicker.setTimePickerIntervalInMinutes(5);
					customDateTimePicker.setDate(selectedScheduleCalendar);
					customDateTimePicker.showDialog();
				}
			});
			

			dialog.show();
			
			getSchedulePickupLocationAddress(selectedScheduleLatLng);
			setScheduleDateTimeValue(selectedScheduleCalendar);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void dismissAlert(){
		try{
			if(dialog != null){
				dialog.dismiss();
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public void setScheduleDateTimeValue(final Calendar calendar){
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				String dayShortName = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
				String monthShortName = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
				String amOrPm = calendar.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.getDefault());
				String hourLongName = (calendar.get(Calendar.HOUR) < 10)?"0"+calendar.get(Calendar.HOUR):""+calendar.get(Calendar.HOUR);
				if(calendar.get(Calendar.HOUR) == 0){
					hourLongName = "12";
				}
				String minuteLongName = (calendar.get(Calendar.MINUTE) < 10)?"0"+calendar.get(Calendar.MINUTE):""+calendar.get(Calendar.MINUTE);
				textViewPickupTime.setText(dayShortName + ", " + calendar.get(Calendar.DATE) + " " + monthShortName + " " + calendar.get(Calendar.YEAR) 
            			+ ", " + hourLongName + ":" + minuteLongName  + " " + amOrPm);
			}
		});
	}
	
	
	
	public void setSchedulePickupLocationAddress(final String address){
		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				textViewPickupLocation.setText(address);
			}
		});
	}
	
	Thread schedulePickupLocationAddressFetcherThread;
	public void getSchedulePickupLocationAddress(final LatLng schedulePickupLatLng){
		stopSchedulePickupLocationAddressFetcherThread();
		setSchedulePickupLocationAddress("Loading address...");
		try{
			schedulePickupLocationAddressFetcherThread = new Thread(new Runnable() {
				@Override
				public void run() {
					setSchedulePickupLocationAddress(MapUtils.getGAPIAddress(schedulePickupLatLng));
					selectedScheduleLatLng = schedulePickupLatLng;
				}
			});
			schedulePickupLocationAddressFetcherThread.start();
		} catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void stopSchedulePickupLocationAddressFetcherThread(){
		try{
			if(schedulePickupLocationAddressFetcherThread != null){
				schedulePickupLocationAddressFetcherThread.interrupt();
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		schedulePickupLocationAddressFetcherThread = null;
	}
	
	
	/**
	 * ASync for inserting schedule ride event to server
	 */
	public void insertScheduleRideAsync(final Activity activity, Calendar selectedCalendar, final LatLng selectedLatLng) {
		if (AppStatus.getInstance(activity).isOnline(activity)) {
			
			DialogPopup.showLoadingDialog(activity, "Loading...");
			
			RequestParams params = new RequestParams();
		
			
			params.put("access_token", Data.userData.accessToken);
			params.put("latitude", ""+selectedLatLng.latitude);
			params.put("longitude", ""+selectedLatLng.longitude);
			params.put("pickup_time", ""+DateOperations.getTimeStampfromCalendar(selectedCalendar));

			Log.e("Server hit=", "=" + Data.SERVER_URL + "/insert_pickup_schedule");
			Log.i("params", "=" + params);
			
			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/insert_pickup_schedule", params,
					new CustomAsyncHttpResponseHandler() {
					private JSONObject jObj;

						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
							DialogPopup.dismissLoadingDialog();
							DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}
						

						@Override
						public void onSuccess(String response) {
							Log.i("Server response", "response = " + response);
							DialogPopup.dismissLoadingDialog();
							try {
								jObj = new JSONObject(response);
								
								dismissAlert();
								
								if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)){
									int flag = jObj.getInt("flag");
									if(ApiResponseFlags.SCHEDULE_ADDED.getOrdinal() == flag){
										
										ArrayList<PromoCoupon> promoCouponList = new ArrayList<PromoCoupon>();
										promoCouponList.addAll(JSONParser.parsePromoCoupons(jObj));
										
										String pickupId = jObj.getString("pickup_id");
										
										final PromotionDialog promotionDialog = new PromotionDialog(selectedLatLng, PromotionApplyMode.AFTER_SCHEDULE);
										promotionDialog.updateList(promoCouponList, pickupId);
										promotionDialog.showPromoAlert(activity, promotionDialogEventHandler);
										
									}
									else{
										DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
									}
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
	
						}
					});
		}
		else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}
	}
	
}

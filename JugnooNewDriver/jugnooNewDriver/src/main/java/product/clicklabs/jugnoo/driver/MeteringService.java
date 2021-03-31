package product.clicklabs.jugnoo.driver;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.IBinder;
import android.os.SystemClock;

import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

import androidx.core.app.NotificationCompat;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.datastructure.EngagementStatus;
import product.clicklabs.jugnoo.driver.datastructure.UserData;
import product.clicklabs.jugnoo.driver.ui.DriverSplashActivity;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;

public class MeteringService extends Service {
	
	public MeteringService() {
		Log.e("MeteringService"," instance created");
	}

	
	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implemented");
	}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	super.onStartCommand(intent, flags, startId);
		cancelAlarm();
		gpsInstance(this).start();

		startForeground(METER_NOTIF_ID,generateNotification(MeteringService.this,getString(R.string.metering_service_notif_label),METER_NOTIF_ID));

		return Service.START_STICKY;
    }
    
    
    @Override
    public void onTaskRemoved(Intent rootIntent) {
    	Log.e("MeteringService onTaskRemoved","="+rootIntent);
    }

	@Override
	public void onDestroy() {
		Log.e("MeteringService onDestroy","=");
		restartServiceViaAlarm();
	}




    
    
    public void restartServiceViaAlarm(){
    	try{
    		gpsInstance(this).saveState();
    	} catch(Exception e){
    		e.printStackTrace();
    	}
    	try {
    		String meteringState = Database2.getInstance(this).getMetringState();
    		if(!Database2.ON.equalsIgnoreCase(meteringState)){
				gpsInstance(this).stop();
				Database2.getInstance(this).deleteAllCurrentPathItems();
				stopForeground(true);
				stopSelf();
    		}
    		else{
				Intent restartService = new Intent(getApplicationContext(), this.getClass());
				restartService.setPackage(getPackageName());
				PendingIntent restartServicePI = PendingIntent.getService(getApplicationContext(), 1, restartService, PendingIntent.FLAG_ONE_SHOT);
				AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
				if (alarmService != null) {
					alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePI);
				}
				Log.e("MeteringService restartServiceViaAlarm","="+restartService);
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public void cancelAlarm(){
    	Intent restartService = new Intent(getApplicationContext(), this.getClass());
		restartService.setPackage(getPackageName());
		PendingIntent restartServicePI = PendingIntent.getService(getApplicationContext(), 1, restartService, PendingIntent.FLAG_ONE_SHOT);
		AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		if (alarmService != null) {
			alarmService.cancel(restartServicePI);
		}
	}
    
 

    
    
    private static DecimalFormat decimalFormat = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.ENGLISH));
    private static DecimalFormat getDecimalFormat(){
    	if(decimalFormat == null){
    		decimalFormat = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.ENGLISH));
    	}
    	return decimalFormat;
    }
    
    private static GpsDistanceTimeUpdater gpsDistanceTimeUpdater;
    private static GpsDistanceTimeUpdater getGpsDistanceTimeUpdater(final Context context){
		if(gpsDistanceTimeUpdater == null){
			gpsDistanceTimeUpdater = new GpsDistanceTimeUpdater() {
				
				@Override
				public void updateDistanceTime(double distance, long elapsedTime, long waitTime, Location lastGPSLocation,
											   Location lastFusedLocation, double totalHaversineDistance, boolean fromGPS) {
					ArrayList<CustomerInfo> customerInfos = Data.getAssignedCustomerInfosListForStatus(EngagementStatus.STARTED.getKOrdinal());
						if (fromGPS && customerInfos.size() > 0) {
							String message = context.getString(R.string.metering_service_notif_label);
							if(Prefs.with(context).getInt(Constants.KEY_DRIVER_FARE_MANDATORY, 0) == 0){
								message = context.getResources().getString(R.string.total_distance)
										+ " = " + getDecimalFormat().format(Math.abs(distance) * UserData.getDistanceUnitFactor(context, false)) +" "
										+ Utils.getDistanceUnit(UserData.getDistanceUnit(context)) + " "
										+ "\n" + context.getResources().getString(R.string.ride_time)
										+ " = " + Utils.getChronoTimeFromMillis(elapsedTime);
							}
							generateNotification(context, message,METER_NOTIF_ID);
						}
						if (HomeActivity.appInterruptHandler != null) {
							HomeActivity.appInterruptHandler.updateMeteringUI(lastGPSLocation, lastFusedLocation);
						}
				}
				
				@Override
				public void drawOldPath() {
					if(HomeActivity.appInterruptHandler != null){
						HomeActivity.appInterruptHandler.drawOldPath();
					}
				}
				
				@Override
				public void addPathToMap(PolylineOptions polylineOptions) {
					if(HomeActivity.appInterruptHandler != null){
						HomeActivity.appInterruptHandler.addPathToMap(polylineOptions);
					}
				}

				@Override
				public void googleApiHitStart() {
					if(HomeActivity.appInterruptHandler != null){
						HomeActivity.appInterruptHandler.googleApiHitStart();
					}
				}

				@Override
				public void googleApiHitStop() {
					if(HomeActivity.appInterruptHandler != null){
						HomeActivity.appInterruptHandler.googleApiHitStop();
					}
				}
			};
		}
		return gpsDistanceTimeUpdater;
	}
	
    public static GpsDistanceCalculator gpsInstance(Context context) {
		return GpsDistanceCalculator.getInstance(context, getGpsDistanceTimeUpdater(context),
				GpsDistanceCalculator.getTotalDistanceFromSP(context),
				GpsDistanceCalculator.getLastLocationTimeFromSP(context),
				GpsDistanceCalculator.getTotalHaversineDistanceFromSP(context));
	}
    



    public static int METER_NOTIF_ID = 1212;
    
	public static Notification generateNotification(Context context, String message,int notificationId) {
		try {
			long when = System.currentTimeMillis();
			NotificationManager notificationManager = GCMIntentService.getNotificationManagerSilent(context, Constants.NOTIF_CHANNEL_METERING);
			
			Intent notificationIntent = new Intent(context, DriverSplashActivity.class);
			
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);


			NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.NOTIF_CHANNEL_METERING);
			builder.setAutoCancel(false);
			builder.setContentTitle(context.getResources().getString(R.string.app_name));
			builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
			builder.setContentText(message);
			builder.setTicker(message);
			builder.setChannelId(Constants.NOTIF_CHANNEL_METERING);

			builder.setWhen(when);
			builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), GCMIntentService.NOTIFICATION_BIG_ICON));
			builder.setSmallIcon(GCMIntentService.NOTIFICATON_SMALL_ICON);
			builder.setContentIntent(intent);
			
			
			Notification notification = builder.build();
			notificationManager.notify(notificationId, notification);
			return notification;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void clearNotifications(Context context){
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		if (notificationManager != null) {
			notificationManager.cancel(METER_NOTIF_ID);
		}
	}

}

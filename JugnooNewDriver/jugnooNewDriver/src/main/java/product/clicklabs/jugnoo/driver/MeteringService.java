package product.clicklabs.jugnoo.driver;

import android.app.Activity;
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
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import product.clicklabs.jugnoo.driver.datastructure.DriverScreenMode;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.ui.DriverSplashActivity;
import product.clicklabs.jugnoo.driver.datastructure.UserData;
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
    public void onCreate() {
    }

	
    @Override
    public void onStart(Intent intent, int startId) {
    	cancelAlarm();
        gpsInstance(this).start();
        startUploadPathAlarm();
		startUploadInRideDataAlarm();
    }
    
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	super.onStartCommand(intent, flags, startId);
		try {
			startForeground(METER_NOTIF_ID,generateNotification(MeteringService.this,getString(R.string.metering_service_notif_label),METER_NOTIF_ID));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Service.START_STICKY;
    }
    
    
    @Override
    public void onTaskRemoved(Intent rootIntent) {
    	Log.e("MeteringService onTaskRemoved","="+rootIntent);
    	restartServiceViaAlarm();
    }

	@Override
	public void onDestroy() {
		Log.e("MeteringService onDestroy","=");
		restartServiceViaAlarm();
	}



    public static final int UPLOAD_PATH_PI_REQUEST_CODE = 112;
    public static final String UPOLOAD_PATH = "product.clicklabs.jugnoo.driver.UPOLOAD_PATH";
    public static final long ALARM_REPEAT_INTERVAL = 15000;
    public void startUploadPathAlarm() {
        // check task is scheduled or not
        boolean alarmUp = (PendingIntent.getBroadcast(this, UPLOAD_PATH_PI_REQUEST_CODE,
            new Intent(this, PathUploadReceiver.class).setAction(UPOLOAD_PATH),
            PendingIntent.FLAG_NO_CREATE) != null);

        if (alarmUp) {
            cancelUploadPathAlarm();
        }

        Intent intent = new Intent(this, PathUploadReceiver.class);
        intent.setAction(UPOLOAD_PATH);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, UPLOAD_PATH_PI_REQUEST_CODE,
            intent, PendingIntent.FLAG_UPDATE_CURRENT);

		        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),
				ALARM_REPEAT_INTERVAL, pendingIntent);

    }

    public void cancelUploadPathAlarm() {
        Intent intent = new Intent(this, PathUploadReceiver.class);
        intent.setAction(UPOLOAD_PATH);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, UPLOAD_PATH_PI_REQUEST_CODE,
            intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Activity.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }








	public static final int UPLOAD_IN_RIDE_DATA_PI_REQUEST_CODE = 114;
	public static final String UPLOAD_IN_RIDE_DATA = "product.clicklabs.jugnoo.driver.UPLOAD_IN_RIDE_DATA";
	public static final long UPLOAD_IN_RIDE_DATA_ALARM_REPEAT_INTERVAL = 30000;
	public void startUploadInRideDataAlarm() {
		boolean alarmUp = (PendingIntent.getBroadcast(this, UPLOAD_IN_RIDE_DATA_PI_REQUEST_CODE,
				new Intent(this, UploadInRideDataReceiver.class).setAction(UPLOAD_IN_RIDE_DATA),
				PendingIntent.FLAG_NO_CREATE) != null);
		if (alarmUp) {
			cancelUploadInRideDataAlarm();
		}

		Intent intent = new Intent(this, UploadInRideDataReceiver.class);
		intent.setAction(UPLOAD_IN_RIDE_DATA);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, UPLOAD_IN_RIDE_DATA_PI_REQUEST_CODE,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(),
				UPLOAD_IN_RIDE_DATA_ALARM_REPEAT_INTERVAL, pendingIntent);
	}

	public void cancelUploadInRideDataAlarm() {
		Intent intent = new Intent(this, UploadInRideDataReceiver.class);
		intent.setAction(UPLOAD_IN_RIDE_DATA);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, UPLOAD_IN_RIDE_DATA_PI_REQUEST_CODE,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) this.getSystemService(Activity.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
		pendingIntent.cancel();
	}










    
    
    public void restartServiceViaAlarm(){
    	try{
    		gpsInstance(this).saveState();
    	} catch(Exception e){
    		e.printStackTrace();
    	}
    	try {
    		String meteringState = Database2.getInstance(this).getMetringState();
			String meteringStateSp= Prefs.with(this).getString(SPLabels.METERING_STATE, Database2.OFF);
    		if(!Database2.ON.equalsIgnoreCase(meteringState) && !Database2.ON.equalsIgnoreCase(meteringStateSp)){
				gpsInstance(this).stop();
				Database2.getInstance(this).deleteAllCurrentPathItems();
				cancelUploadPathAlarm();
				cancelUploadInRideDataAlarm();
    		}
    		else{
				Intent restartService = new Intent(getApplicationContext(), this.getClass());
				restartService.setPackage(getPackageName());
				PendingIntent restartServicePI = PendingIntent.getService(getApplicationContext(), 1, restartService, PendingIntent.FLAG_ONE_SHOT);
				AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
				alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePI);
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
		alarmService.cancel(restartServicePI);
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
					int driverScreenMode = Prefs.with(context).getInt(SPLabels.DRIVER_SCREEN_MODE,
							DriverScreenMode.D_INITIAL.getOrdinal());
					if(!(DriverScreenMode.D_INITIAL.getOrdinal() == driverScreenMode)) {
						if (fromGPS && DriverScreenMode.D_IN_RIDE.getOrdinal() == driverScreenMode) {
							String message = context.getResources().getString(R.string.total_distance)
									+ " = " + getDecimalFormat().format(Math.abs(distance) * UserData.getDistanceUnitFactor(context)) +" "
									+ Utils.getDistanceUnit(UserData.getDistanceUnit(context)) + " "
									+ "\n" + context.getResources().getString(R.string.ride_time)
									+ " = " + Utils.getChronoTimeFromMillis(elapsedTime);
							generateNotification(context, message,METER_NOTIF_ID);
						}
						if (HomeActivity.appInterruptHandler != null) {
							HomeActivity.appInterruptHandler.updateMeteringUI(Math.abs(distance), elapsedTime, waitTime,
									lastGPSLocation,
									lastFusedLocation, totalHaversineDistance);
						}
					} else{
						Database2.getInstance(context).updateMetringState(Database2.OFF);
						Prefs.with(context).save(SPLabels.METERING_STATE, Database2.OFF);
						context.stopService(new Intent(context, MeteringService.class));
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
			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			
			Intent notificationIntent = new Intent(context, DriverSplashActivity.class);
			
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
			
			NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
			builder.setAutoCancel(false);
			builder.setContentTitle(context.getResources().getString(R.string.app_name));
			builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
			builder.setContentText(message);
			builder.setTicker(message);
			
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
		notificationManager.cancel(METER_NOTIF_ID);
    }
    

}

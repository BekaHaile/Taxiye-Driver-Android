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

import product.clicklabs.jugnoo.driver.utils.Log;
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
    }
    
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	super.onStartCommand(intent, flags, startId);
    	return Service.START_STICKY;
    }
    
    
    @Override
    public void onTaskRemoved(Intent rootIntent) {
    	Log.e("MeteringService onTaskRemoved","="+rootIntent);
    	restartServiceViaAlarm();
    }



    public static final int UPLOAD_PATH_PI_REQUEST_CODE = 112;
    public static final String UPOLOAD_PATH = "product.clicklabs.jugnoo.driver.UPOLOAD_PATH";
    public static final long ALARM_REPEAT_INTERVAL = 120000;


    public void startUploadPathAlarm() {
        // check task is scheduled or not
        boolean alarmUp = (PendingIntent.getBroadcast(this, UPLOAD_PATH_PI_REQUEST_CODE,
            new Intent(this, PathUploadReceiver.class).setAction(UPOLOAD_PATH).putExtra("engagement_id", gpsInstance(this).getEngagementIdFromSP(this)),
            PendingIntent.FLAG_NO_CREATE) != null);

        if (alarmUp) {
            cancelUploadPathAlarm();
        }

        Intent intent = new Intent(this, PathUploadReceiver.class);
        intent.setAction(UPOLOAD_PATH);
        intent.putExtra("engagement_id", gpsInstance(this).getEngagementIdFromSP(this));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, UPLOAD_PATH_PI_REQUEST_CODE,
            intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), ALARM_REPEAT_INTERVAL, pendingIntent);

    }

    public void cancelUploadPathAlarm() {
        Intent intent = new Intent(this, PathUploadReceiver.class);
        intent.setAction(UPOLOAD_PATH);
        intent.putExtra("engagement_id", gpsInstance(this).getEngagementIdFromSP(this));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, UPLOAD_PATH_PI_REQUEST_CODE,
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
    		if(Database2.ON.equalsIgnoreCase(meteringState)){
    			Intent restartService = new Intent(getApplicationContext(), this.getClass());
    			restartService.setPackage(getPackageName());
    			PendingIntent restartServicePI = PendingIntent.getService(getApplicationContext(), 1, restartService, PendingIntent.FLAG_ONE_SHOT);
    			AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
    			alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePI);
    		}
    		else{
    			gpsInstance(this).stop();
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
    
 
    @Override
    public void onDestroy() {
    	Log.e("MeteringService onDestroy","=");
    	restartServiceViaAlarm();
    }
    
    
    private static DecimalFormat decimalFormat = new DecimalFormat("#.##");
    private static DecimalFormat getDecimalFormat(){
    	if(decimalFormat == null){
    		decimalFormat = new DecimalFormat("#.##");
    	}
    	return decimalFormat;
    }
    
    private static GpsDistanceTimeUpdater gpsDistanceTimeUpdater;
    private static GpsDistanceTimeUpdater getGpsDistanceTimeUpdater(final Context context){
		if(gpsDistanceTimeUpdater == null){
			gpsDistanceTimeUpdater = new GpsDistanceTimeUpdater() {
				
				@Override
				public void updateDistanceTime(double distance, long elapsedTime, Location lastGPSLocation, Location lastFusedLocation, boolean fromGPS) {
					if(fromGPS){
						generateNotification(context, "Total distance = "+getDecimalFormat().format(Math.abs(distance) / 1000) 
								+ " km"+" time = "+Utils.getElapsedTimeFromMillis(elapsedTime));
					}
					if(HomeActivity.appInterruptHandler != null){
						HomeActivity.appInterruptHandler.updateMeteringUI(Math.abs(distance), elapsedTime, lastGPSLocation, lastFusedLocation);
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
//					if(HomeActivity.appInterruptHandler != null){
//						HomeActivity.appInterruptHandler.addPathToMap(polylineOptions);
//					}
				}
				
			};
		}
		return gpsDistanceTimeUpdater;
	}
	
    public static GpsDistanceCalculator gpsInstance(Context context){
		return GpsDistanceCalculator.getInstance(context, getGpsDistanceTimeUpdater(context), 
				GpsDistanceCalculator.getInstance(context, getGpsDistanceTimeUpdater(context), -1, System.currentTimeMillis()).getTotalDistanceFromSP(context),
				GpsDistanceCalculator.getInstance(context, getGpsDistanceTimeUpdater(context), -1, System.currentTimeMillis()).getLastLocationTimeFromSP(context));
	}
    
    
    
    
    public static int METER_NOTIF_ID = 1212;
    
	public static void generateNotification(Context context, String message) {
		try {
			long when = System.currentTimeMillis();
			
			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			
			Intent notificationIntent = new Intent(context, SplashNewActivity.class);
			
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
			
			NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
			builder.setAutoCancel(false);
			builder.setContentTitle("Jugnoo Auto Ride");
			builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
			builder.setContentText(message);
			builder.setTicker(message);
			
			builder.setWhen(when);
			builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.jugnoo_icon));
			builder.setSmallIcon(R.drawable.notif_icon);
			builder.setContentIntent(intent);
			
			
			Notification notification = builder.build();
			notificationManager.notify(METER_NOTIF_ID, notification);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void clearNotifications(Context context){
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(METER_NOTIF_ID);
    }
    
	
}

package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.location.LocationServices;

import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.MapUtils;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;

public class LocationReceiverDriver extends BroadcastReceiver {
    public static final double FREE_MAX_ACCURACY = 200;
	public static final double MAX_TIME_WINDOW = 3600000;


	@Override
    public void onReceive(final Context context, Intent intent) {
		final Location location = (Location) intent.getExtras().get(LocationServices.FusedLocationApi.KEY_LOCATION_CHANGED);
        if(!Utils.mockLocationEnabled(location)) {
			Log.writePathLogToFile("service_log",
					"LocationReceiverDriver onReceive location="+location);
            if (location != null) {
                Location oldlocation = Database2.getInstance(context).getDriverCurrentLocation();
                double displacement_1 = MapUtils.distance(oldlocation, location);
                double timediff_1= (System.currentTimeMillis()- oldlocation.getTime())/1000;
                double speed_1 = displacement_1/timediff_1;


                if(((Utils.compareDouble(location.getLatitude(), oldlocation.getLatitude()) == 0)
                        && (Utils.compareDouble(location.getLongitude(), oldlocation.getLongitude()) == 0))){
                    Log.i("equal_loc", "");
					context.stopService(new Intent(context, DriverLocationUpdateService.class));
                    setAlarm(context);
                }
                else if(speed_1 > 17){
                    Log.i("equal_speed", "");
					context.stopService(new Intent(context, DriverLocationUpdateService.class));
                    setAlarm(context);
                }
                else{

					if(location.getAccuracy() > FREE_MAX_ACCURACY) {
                        Log.i("equal_Low_acc", "");
						Prefs.with(context).save(SPLabels.BAD_ACCURACY_COUNT, Prefs.with(context).getInt(SPLabels.BAD_ACCURACY_COUNT, 0) + 1);
                    }

					long timeLapse = System.currentTimeMillis() - Prefs.with(context).getLong(SPLabels.ACCURACY_SAVED_TIME, 0);
					if(timeLapse <= MAX_TIME_WINDOW && (0 == (Prefs.with(context).getInt(SPLabels.TIME_WINDOW_FLAG, 0)))){
						if(5 <= Prefs.with(context).getInt(SPLabels.BAD_ACCURACY_COUNT, 0)) {
//							String restartMessageHindi = "अपने फोन को बंद करें और इसे फिर से चालू करें";
//
//							SoundMediaPlayer.startSound(context, R.raw.cancellation_ring, 1, true, false);
//							Intent dialogIntent = new Intent(context, BlankActivityForDialog.class);
//							dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//							dialogIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//							dialogIntent.putExtra("message2", restartMessageHindi);
//							context.startActivity(dialogIntent);
//							generateNotification(context, restartMessageHindi);

							location.setAccuracy(3000.001f);

							Prefs.with(context).save(SPLabels.BAD_ACCURACY_COUNT, 0);
							Prefs.with(context).save(SPLabels.TIME_WINDOW_FLAG, 1);
						}
					}
					else if(timeLapse > MAX_TIME_WINDOW) {
						Prefs.with(context).save(SPLabels.ACCURACY_SAVED_TIME, System.currentTimeMillis());
						Prefs.with(context).save(SPLabels.BAD_ACCURACY_COUNT, 0);
						Prefs.with(context).save(SPLabels.TIME_WINDOW_FLAG, 0);
					}



						new Thread(new Runnable() {
							@Override
							public void run() {
								Database2.getInstance(context).updateDriverCurrentLocation(context, location);
								Log.writeLogToFile("LocationReciever", "Receiver " + DateOperations.getCurrentTime() + " = " + location + " hasNet = " + AppStatus.getInstance(context).isOnline(context));
								new DriverLocationDispatcher().sendLocationToServer(context, "LocationReciever");
								Log.i("equal_data", location.toString());
							}
						}).start();

					if(location.getAccuracy() > 200) {
						Log.i("equal_Low_acc", "");
						context.stopService(new Intent(context, DriverLocationUpdateService.class));
						setAlarm(context);
					}
                }
            }
        }
    }


    private static int PI_REQUEST_CODE = 1234;
    private static final String RESTART_SERVICE = "product.clicklabs.jugnoo.driver.RESTART_SERVICE";

    public void setAlarm(Context context) {
        boolean alarmUp = (PendingIntent.getBroadcast(context, PI_REQUEST_CODE,
                new Intent(context, DriverServiceRestartReceiver.class).setAction(RESTART_SERVICE),
                PendingIntent.FLAG_NO_CREATE) != null);
        if (alarmUp) {
            cancelAlarm(context);
        }

        Intent intent = new Intent(context, DriverServiceRestartReceiver.class);
        intent.setAction(RESTART_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, PI_REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+20000, pendingIntent);

    }

    public void cancelAlarm(Context context) {
        Intent intent = new Intent(context, DriverServiceRestartReceiver.class);
        intent.setAction(RESTART_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, PI_REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

	public static void generateNotification(Context context, String message) {

		try {
			long when = System.currentTimeMillis();

			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

			Log.v("message", "," + message);

			Intent notificationIntent = new Intent(context, SplashNewActivity.class);

			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

			NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
			builder.setAutoCancel(true);
			builder.setContentTitle("Jugnoo");
			builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
			builder.setContentText(message);
			builder.setTicker(message);
			builder.setDefaults(Notification.DEFAULT_ALL);
			builder.setWhen(when);
			builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.jugnoo_icon));
			builder.setSmallIcon(R.drawable.notif_icon);
			builder.setContentIntent(intent);


			Notification notification = builder.build();
			notificationManager.notify(123, notification);

			PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
			wl.acquire(15000);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

    
}
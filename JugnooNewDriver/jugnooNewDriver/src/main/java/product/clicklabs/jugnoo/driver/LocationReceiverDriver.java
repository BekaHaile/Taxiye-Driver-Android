package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.SystemClock;

import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.model.LatLng;

import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.MapUtils;
import product.clicklabs.jugnoo.driver.utils.Utils;

public class LocationReceiverDriver extends BroadcastReceiver {
    public static final double FREE_MAX_ACCURACY = 200;
    @Override
    public void onReceive(final Context context, Intent intent) {
        if(!Utils.mockLocationEnabled(context)) {
            final Location location = (Location) intent.getExtras().get(LocationClient.KEY_LOCATION_CHANGED);

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
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            Log.i("equal_newloc", "");
                            Database2.getInstance(context).updateDriverCurrentLocation(location);
//			    	    Log.e("DriverLocationUpdateService location in pi reciever ", "=="+location);
                            Log.writeLogToFile("LocationReciever", "Receiver " + DateOperations.getCurrentTime() + " = " + location + " hasNet = " + AppStatus.getInstance(context).isOnline(context));
                            new DriverLocationDispatcher().sendLocationToServer(context, "LocationReciever");
                            Log.i("equal_data",location.toString());
                        }
                    }).start();
                    if(location.getAccuracy() > 200) {
                        Log.i("equal_Low_acc", "");
                        context.stopService(new Intent(context, DriverLocationUpdateService.class));
                        setAlarm(context);
                    }
                }




//                if(location.getAccuracy() < FREE_MAX_ACCURACY) {
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Database2.getInstance(context).updateDriverCurrentLocation(location);
//			    	    Log.e("DriverLocationUpdateService location in pi reciever ", "=="+location);
//                            Log.writeLogToFile("LocationReciever", "Receiver " + DateOperations.getCurrentTime() + " = " + location + " hasNet = " + AppStatus.getInstance(context).isOnline(context));
//                            new DriverLocationDispatcher().sendLocationToServer(context, "LocationReciever");
//                        }
//                    }).start();
//                }
//                else{
//                    context.stopService(new Intent(context, DriverLocationUpdateService.class));
//                    context.startService(new Intent(context, DriverLocationUpdateService.class));
//
//                }
            }
        }
    }


    private static int PI_REQUEST_CODE = 1234;
    private static final String RESTART_SERVICE = "product.clicklabs.jugnoo.driver.RESTART_SERVICE";

    public void setAlarm(Context context) {
        boolean alarmUp = (PendingIntent.getBroadcast(context, PI_REQUEST_CODE,
                new Intent(context, DriverLocationUpdateAlarmReceiver.class).setAction(RESTART_SERVICE),
                PendingIntent.FLAG_NO_CREATE) != null);
        if (alarmUp) {
            cancelAlarm(context);
        }

        Intent intent = new Intent(context, DriverServiceRestartReceiver.class);
        intent.setAction(RESTART_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, PI_REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+10000, pendingIntent);

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
    
}
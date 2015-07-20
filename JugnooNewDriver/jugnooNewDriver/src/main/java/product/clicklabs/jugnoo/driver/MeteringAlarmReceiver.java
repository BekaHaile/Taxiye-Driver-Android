package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;

import product.clicklabs.jugnoo.driver.datastructure.GpsState;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.SoundMediaPlayer;

public class MeteringAlarmReceiver extends BroadcastReceiver {

    private static final String CHECK_LOCATION = "product.clicklabs.jugnoo.driver.CHECK_LOCATION";

    private static final long MINUTE = 60 * 1000;
    private static final long HALF_MINUTE = 30 * 1000;
    private static final long MAX_TIME_BEFORE_LOCATION_UPDATE = 2 * MINUTE;


    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        if (CHECK_LOCATION.equals(action)) {
            long timeDiff = System.currentTimeMillis() - GpsDistanceCalculator.lastLocationTime;


//			if(timeDiff > MAX_TIME_BEFORE_LOCATION_UPDATE && timeDiff <= 7*HALF_MINUTE){
//				GpsDistanceCalculator.gpsLocationUpdate.refreshLocationFetchers(context);
//				Toast.makeText(context, "Old Location", Toast.LENGTH_LONG).show();
//			}
//			else if(timeDiff > 7*HALF_MINUTE && timeDiff< 9*HALF_MINUTE){
//				Intent i = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
//				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				context.startActivity(i);
//			}
//			else if(timeDiff >= 9*HALF_MINUTE && timeDiff <= 11*HALF_MINUTE){
//				GpsDistanceCalculator.gpsLocationUpdate.refreshLocationFetchers(context);
//				Toast.makeText(context, "Old Location", Toast.LENGTH_LONG).show();
//			}
//			else if(timeDiff > 11*HALF_MINUTE){
//				SoundMediaPlayer.startSound(context, R.raw.cancellation_ring, 4, true, true);
//				Toast.makeText(context, "Please Restart Your Phone", Toast.LENGTH_LONG).show();
//			}



            if (timeDiff >= 6 * MINUTE) {
                Database2.getInstance(context).updateGpsState(GpsState.GREATER_SIX.getOrdinal());
                SoundMediaPlayer.startSound(context, R.raw.cancellation_ring, 4, true, true);

                FlurryEventLogger.gpsStatus(context, "Device Restart");

                Intent dialogIntent = new Intent(context, BlankActivityForDialog.class);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                dialogIntent.putExtra("message", "Please Restart Your Phone");
                context.startActivity(dialogIntent);


            } else if (timeDiff > 7 * HALF_MINUTE) {
                if (Database2.getInstance(context).getGpsState()
                        == GpsState.TWO_LESS_FOUR.getOrdinal()) {
                    if(HomeActivity.activity != null) {
                        ActivityCompat.finishAffinity(HomeActivity.activity);
                        Intent i = new Intent(HomeActivity.activity, SplashNewActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        HomeActivity.activity.startActivity(i);
                    }

                    FlurryEventLogger.gpsStatus(context, "App restart");

                } else {
                    GpsDistanceCalculator.gpsLocationUpdate.refreshLocationFetchers(context);

                    FlurryEventLogger.gpsStatus(context, "Old Location After");

                    Toast.makeText(context, "Old Location after", Toast.LENGTH_LONG).show();
                }
                Database2.getInstance(context).updateGpsState(GpsState.GREATER_FOUR.getOrdinal());
            } else if (timeDiff > MAX_TIME_BEFORE_LOCATION_UPDATE) {
                GpsDistanceCalculator.gpsLocationUpdate.refreshLocationFetchers(context);
                Toast.makeText(context, "Old Location", Toast.LENGTH_LONG).show();
                Database2.getInstance(context).updateGpsState(GpsState.TWO_LESS_FOUR.getOrdinal());
                FlurryEventLogger.gpsStatus(context, "Old Location");

            } else {
                Database2.getInstance(context).updateGpsState(GpsState.ZERO_TWO.getOrdinal());
            }

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "" + (Database2.getInstance(context).getGpsState()), Toast.LENGTH_LONG).show();
                }
            });


        }
    }

}
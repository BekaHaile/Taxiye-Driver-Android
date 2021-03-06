package product.clicklabs.jugnoo.driver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.core.app.ActivityCompat;

import product.clicklabs.jugnoo.driver.datastructure.GpsState;
import product.clicklabs.jugnoo.driver.ui.DriverSplashActivity;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.SoundMediaPlayer;
import product.clicklabs.jugnoo.driver.utils.Utils;

public class MeteringAlarmReceiver extends BroadcastReceiver {

    private static final String CHECK_LOCATION = "product.clicklabs.jugnoo.driver.CHECK_LOCATION";

    private static final long MINUTE = 60 * 1000;
    private static final long HALF_MINUTE = 30 * 1000;
    private static final long MAX_TIME_BEFORE_LOCATION_UPDATE = 2 * MINUTE;


    @Override
    public void onReceive(final Context context, Intent intent) {
		try {
			String action = intent.getAction();
			if (CHECK_LOCATION.equals(action)) {
				long currTime = System.currentTimeMillis();
				long timeDiff = currTime - GpsDistanceCalculator.lastLocationTime;
				long timeDiffLocal = currTime - Prefs.with(context).getLong(Constants.SP_RECEIVER_LAST_LOCATION_TIME, currTime);
				if(timeDiffLocal < 10000){
					Prefs.with(context).save(Constants.SP_RECEIVER_LAST_LOCATION_TIME, currTime);
				}

				if (timeDiffLocal >= 6 * MINUTE && timeDiff >= 6 * MINUTE) {
					Database2.getInstance(context).updateGpsState(GpsState.GREATER_SIX.getOrdinal());

					if(Prefs.with(context).getString(Data.SP_SERVER_LINK, Data.DEFAULT_SERVER_URL).equalsIgnoreCase(Data.LIVE_SERVER_URL)){
						SoundMediaPlayer.startSound(context, R.raw.cancel_ring3, 1, true);
					} else {
						SoundMediaPlayer.startSound(context, R.raw.cancel_ring3, 1, false);
					}

					FlurryEventLogger.gpsStatus(context, "Device Restart");

					Intent dialogIntent = new Intent(context, BlankActivityForDialog.class);
					dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					dialogIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					dialogIntent.putExtra("restart_phone", context.getResources().getString(R.string.restart_phone));
					context.startActivity(dialogIntent);

					Prefs.with(context).save(Constants.SP_RECEIVER_LAST_LOCATION_TIME, currTime);

				} else if (timeDiffLocal > 7 * HALF_MINUTE && timeDiff > 7 * HALF_MINUTE) {
					if (Database2.getInstance(context).getGpsState()
							== GpsState.TWO_LESS_FOUR.getOrdinal()) {
						if(HomeActivity.activity != null) {
							ActivityCompat.finishAffinity(HomeActivity.activity);
							Intent i = new Intent(context, DriverSplashActivity.class);
							i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
							context.startActivity(i);
						}
						FlurryEventLogger.gpsStatus(context, "App restart");
					} else {
						if(GpsDistanceCalculator.gpsLocationUpdate != null) {
							GpsDistanceCalculator.gpsLocationUpdate.refreshLocationFetchers(context);
						} else {
							if(Utils.isServiceRunning(context, MeteringService.class)) {
								context.stopService(new Intent(context, MeteringService.class));
							} else {
								context.stopService(new Intent(context, MeteringService.class));
								context.startService(new Intent(context, MeteringService.class));
							}
						}
						FlurryEventLogger.gpsStatus(context, "Old Location After");
					}
					Database2.getInstance(context).updateGpsState(GpsState.GREATER_FOUR.getOrdinal());
				} else if (timeDiffLocal > MAX_TIME_BEFORE_LOCATION_UPDATE && timeDiff > MAX_TIME_BEFORE_LOCATION_UPDATE) {
					if(GpsDistanceCalculator.gpsLocationUpdate != null) {
						GpsDistanceCalculator.gpsLocationUpdate.refreshLocationFetchers(context);
					} else {
						if(Utils.isServiceRunning(context, MeteringService.class)) {
							context.stopService(new Intent(context, MeteringService.class));
						} else {
							context.stopService(new Intent(context, MeteringService.class));
							context.startService(new Intent(context, MeteringService.class));
						}
						Log.w(MeteringAlarmReceiver.class.getSimpleName(), "MeteringService restarted ---------------------------------------");
					}
					Database2.getInstance(context).updateGpsState(GpsState.TWO_LESS_FOUR.getOrdinal());
					FlurryEventLogger.gpsStatus(context, "Old Location");
				} else {
					Database2.getInstance(context).updateGpsState(GpsState.ZERO_TWO.getOrdinal());
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
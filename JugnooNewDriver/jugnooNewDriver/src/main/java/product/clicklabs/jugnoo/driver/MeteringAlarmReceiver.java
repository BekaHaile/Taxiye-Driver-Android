package product.clicklabs.jugnoo.driver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;

import product.clicklabs.jugnoo.driver.datastructure.GpsState;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.SoundMediaPlayer;

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
				long timeDiff = System.currentTimeMillis() - GpsDistanceCalculator.lastLocationTime;

				if (timeDiff >= 6 * MINUTE) {
					Database2.getInstance(context).updateGpsState(GpsState.GREATER_SIX.getOrdinal());
					SoundMediaPlayer.startSound(context, R.raw.cancellation_ring, 4, true, false);

					FlurryEventLogger.gpsStatus(context, "Device Restart");

					Intent dialogIntent = new Intent(context, BlankActivityForDialog.class);
					dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					dialogIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					dialogIntent.putExtra("restart_phone", context.getResources().getString(R.string.restart_phone));
					context.startActivity(dialogIntent);

					GpsDistanceCalculator.lastLocationTime = System.currentTimeMillis();
					GpsDistanceCalculator.saveLastLocationTimeToSP(context, GpsDistanceCalculator.lastLocationTime);

				} else if (timeDiff > 7 * HALF_MINUTE) {
					if (Database2.getInstance(context).getGpsState()
							== GpsState.TWO_LESS_FOUR.getOrdinal()) {
						if(HomeActivity.activity != null) {
							ActivityCompat.finishAffinity(HomeActivity.activity);
							Intent i = new Intent(context, SplashNewActivity.class);
							i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
							context.startActivity(i);
						}
						FlurryEventLogger.gpsStatus(context, "App restart");
					} else {
						GpsDistanceCalculator.gpsLocationUpdate.refreshLocationFetchers(context);
						FlurryEventLogger.gpsStatus(context, "Old Location After");
					}
					Database2.getInstance(context).updateGpsState(GpsState.GREATER_FOUR.getOrdinal());
				} else if (timeDiff > MAX_TIME_BEFORE_LOCATION_UPDATE) {
					GpsDistanceCalculator.gpsLocationUpdate.refreshLocationFetchers(context);
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
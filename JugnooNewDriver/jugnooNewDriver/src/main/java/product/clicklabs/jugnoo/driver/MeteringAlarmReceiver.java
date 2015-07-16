package product.clicklabs.jugnoo.driver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.SoundMediaPlayer;

public class MeteringAlarmReceiver extends BroadcastReceiver {

	private static final String CHECK_LOCATION = "product.clicklabs.jugnoo.driver.CHECK_LOCATION";
	
	private static final long MAX_TIME_BEFORE_LOCATION_UPDATE = 3 * 60000;
	
	@Override
	public void onReceive(final Context context, Intent intent) {
		String action = intent.getAction();
		if (CHECK_LOCATION.equals(action)) {
			long timeDiff = System.currentTimeMillis() - GpsDistanceCalculator.lastLocationTime;
			if(timeDiff > MAX_TIME_BEFORE_LOCATION_UPDATE){
				SoundMediaPlayer.startSound(context, R.raw.cancellation_ring, 4, true, true);

				GpsDistanceCalculator.gpsLocationUpdate.refreshLocationFetchers(context);
			}
		}
	}
	
}
package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.SystemClock;

import com.google.android.gms.location.LocationServices;

import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.utils.MapUtils;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;

public class LocationReceiverDriver extends BroadcastReceiver {
	public static final double FREE_MAX_ACCURACY = 200;
	public static final double MAX_TIME_WINDOW = 3600000;


	@Override
	public void onReceive(final Context context, Intent intent) {
		final Location location = (Location) intent.getExtras().get(LocationServices.FusedLocationApi.KEY_LOCATION_CHANGED);
		if (!Utils.mockLocationEnabled(location)) {
			if (location != null) {
				Location oldlocation = Database2.getInstance(context).getDriverCurrentLocation();
				double displacement_1 = MapUtils.distance(oldlocation, location);
				double timediff_1 = (System.currentTimeMillis() - oldlocation.getTime()) / 1000;
				double speed_1 = displacement_1 / timediff_1;

				if (speed_1 > 20) {
					context.stopService(new Intent(context, DriverLocationUpdateService.class));
					setAlarm(context);
				} else {

					if (location.getAccuracy() > FREE_MAX_ACCURACY) {
						Prefs.with(context).save(SPLabels.BAD_ACCURACY_COUNT, Prefs.with(context).getInt(SPLabels.BAD_ACCURACY_COUNT, 0) + 1);
					}

					long timeLapse = System.currentTimeMillis() - Prefs.with(context).getLong(SPLabels.ACCURACY_SAVED_TIME, 0);
					if (timeLapse <= MAX_TIME_WINDOW && (0 == (Prefs.with(context).getInt(SPLabels.TIME_WINDOW_FLAG, 0)))) {
						if (5 <= Prefs.with(context).getInt(SPLabels.BAD_ACCURACY_COUNT, 0)) {

							location.setAccuracy(3000.001f);

							Prefs.with(context).save(SPLabels.BAD_ACCURACY_COUNT, 0);
							Prefs.with(context).save(SPLabels.TIME_WINDOW_FLAG, 1);
						}
					} else if (timeLapse > MAX_TIME_WINDOW) {
						Prefs.with(context).save(SPLabels.ACCURACY_SAVED_TIME, System.currentTimeMillis());
						Prefs.with(context).save(SPLabels.BAD_ACCURACY_COUNT, 0);
						Prefs.with(context).save(SPLabels.TIME_WINDOW_FLAG, 0);
					}
					new Thread(new Runnable() {
						@Override
						public void run() {
							Database2.getInstance(context).updateDriverCurrentLocation(context, location);
							new DriverLocationDispatcher().sendLocationToServer(context);
						}
					}).start();

					if (location.getAccuracy() > 200) {
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
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 20000, pendingIntent);

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
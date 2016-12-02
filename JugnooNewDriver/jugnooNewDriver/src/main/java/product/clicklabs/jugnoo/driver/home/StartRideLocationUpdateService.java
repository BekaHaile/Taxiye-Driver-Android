package product.clicklabs.jugnoo.driver.home;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.MyApplication;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.SplashNewActivity;
import product.clicklabs.jugnoo.driver.datastructure.DriverScreenMode;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.home.utils.LocationFetcherBG;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.MapUtils;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.SoundMediaPlayer;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class StartRideLocationUpdateService extends Service {

	private final String TAG = StartRideLocationUpdateService.class.getSimpleName();
	private LocationFetcherBG locationFetcherBG;
	private CustomLocationReceiver locationReceiver;

	public StartRideLocationUpdateService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implemented");
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "service onStartCommand");
		try {
			MyApplication.getInstance().initializeServerURLAndRestClient(this);
			locationReceiver = new CustomLocationReceiver();
			registerReceiver(locationReceiver, new IntentFilter(Constants.ACTION_LOCATION_UPDATE));

			long locationUpdateInterval = Prefs.with(this).getLong(Constants.START_RIDE_ALARM_LOCATION_FETCHER_INTERVAL,
					5000);

			if (locationFetcherBG != null) {
				locationFetcherBG.destroy();
				locationFetcherBG = null;
			}
			locationFetcherBG = new LocationFetcherBG(this, locationUpdateInterval);

			return Service.START_STICKY;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Service.START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		unregisterRec();
	}

	private void unregisterRec(){
		if(locationReceiver != null){
			unregisterReceiver(locationReceiver);
		}
		if (locationFetcherBG != null) {
			locationFetcherBG.destroy();
			locationFetcherBG = null;
		}
	}

	@Override
	public void onTaskRemoved(Intent rootIntent) {
		try {
			int screenMode = Prefs.with(this).getInt(SPLabels.DRIVER_SCREEN_MODE,
					DriverScreenMode.D_INITIAL.getOrdinal());
			if(screenMode == DriverScreenMode.D_START_RIDE.getOrdinal()) {
				stopSelf();
				Intent restartService = new Intent(getApplicationContext(), this.getClass());
				restartService.setPackage(getPackageName());
				PendingIntent restartServicePI = PendingIntent.getService(getApplicationContext(), 1, restartService, PendingIntent.FLAG_ONE_SHOT);
				AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
				alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 5000, restartServicePI);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class CustomLocationReceiver extends BroadcastReceiver{

		private final String TAG = CustomLocationReceiver.class.getSimpleName();

		public CustomLocationReceiver(){
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			try{
				double latitude = intent.getDoubleExtra(Constants.KEY_LATITUDE, 0);
				double longitude = intent.getDoubleExtra(Constants.KEY_LONGITUDE, 0);
				Log.i(TAG, "customonReceive lat=" + latitude + ", lng=" + longitude);

				double pickupLat = Double.parseDouble(Prefs.with(context).getString(Constants.KEY_PICKUP_LATITUDE_ALARM, ""));
				double pickupLong = Double.parseDouble(Prefs.with(context).getString(Constants.KEY_PICKUP_LONGITUDE_ALARM, ""));

				double currentLat = Double.parseDouble(Prefs.with(context).getString(Constants.KEY_CURRENT_LATITUDE_ALARM,""));
				double currentLong = Double.parseDouble(Prefs.with(context).getString(Constants.KEY_CURRENT_LONGITUDE_ALARM,""));

				LatLng driverONPickupLatLng = new LatLng(latitude, longitude);
				LatLng pickupLatLng = new LatLng(pickupLat, pickupLong);
				LatLng currentLatLng = new LatLng(currentLat, currentLong);

				product.clicklabs.jugnoo.driver.utils.Log.e("startRideAlarmSErvice distance",
						String.valueOf(MapUtils.distance(driverONPickupLatLng, pickupLatLng)));
				product.clicklabs.jugnoo.driver.utils.Log.e("startRideAlarmSErvice distance",
						String.valueOf(MapUtils.distance(driverONPickupLatLng, currentLatLng)));



				if (Prefs.with(context).getBoolean(Constants.FLAG_REACHED_PICKUP, false) && Prefs.with(context).getBoolean(Constants.PLAY_START_RIDE_ALARM_FINALLY, false)
						&& ((int) MapUtils.distance(driverONPickupLatLng, pickupLatLng)
						>  Prefs.with(context).getInt(SPLabels.START_RIDE_ALERT_RADIUS_FINAL, 400))
						&& ((int) MapUtils.distance(driverONPickupLatLng, currentLatLng)
						>  Prefs.with(context).getInt(SPLabels.START_RIDE_ALERT_RADIUS_FINAL, 400))) {
					SoundMediaPlayer.startSound(context, R.raw.start_ride_accept_beep, 15, true);
					if(HomeActivity.appInterruptHandler != null){
						HomeActivity.appInterruptHandler.showStartRidePopup();
					}
					Prefs.with(context).save(Constants.PLAY_START_RIDE_ALARM_FINALLY, false);
				}
				if (Prefs.with(context).getBoolean(Constants.FLAG_REACHED_PICKUP, false) && Prefs.with(context).getBoolean(Constants.PLAY_START_RIDE_ALARM, false)
						&& ((int)MapUtils.distance(driverONPickupLatLng, pickupLatLng)
						>  Prefs.with(context).getInt(SPLabels.START_RIDE_ALERT_RADIUS, 200))
						&& ((int)MapUtils.distance(driverONPickupLatLng, currentLatLng)
						>  Prefs.with(context).getInt(SPLabels.START_RIDE_ALERT_RADIUS, 200))) {
					SoundMediaPlayer.startSound(context, R.raw.start_ride_accept_beep, 5, true);
					if(HomeActivity.appInterruptHandler != null){
						HomeActivity.appInterruptHandler.showStartRidePopup();
					}
					Prefs.with(context).save(Constants.PLAY_START_RIDE_ALARM, false);
					Prefs.with(context).save(Constants.PLAY_START_RIDE_ALARM_FINALLY, true);
				}
				if (MapUtils.distance(driverONPickupLatLng, pickupLatLng)
						< Prefs.with(context).getInt(SPLabels.START_RIDE_ALERT_RADIUS, 200)
						|| MapUtils.distance(driverONPickupLatLng, currentLatLng)
						< Prefs.with(context).getInt(SPLabels.START_RIDE_ALERT_RADIUS, 200)) {
					Prefs.with(context).save(Constants.FLAG_REACHED_PICKUP, true);
					Prefs.with(context).save(Constants.PLAY_START_RIDE_ALARM, true);
				}

				boolean a = Prefs.with(context).getBoolean(Constants.FLAG_REACHED_PICKUP, false);
				boolean b = Prefs.with(context).getBoolean(Constants.PLAY_START_RIDE_ALARM, false);
				product.clicklabs.jugnoo.driver.utils.Log.e("startRideAlarmSErvice distance",
						String.valueOf(a) + " "+String.valueOf(b));

			} catch (Exception e){
				e.printStackTrace();
			}
		}

	}

}

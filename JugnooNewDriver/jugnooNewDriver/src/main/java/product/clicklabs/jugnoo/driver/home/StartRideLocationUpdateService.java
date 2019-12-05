package product.clicklabs.jugnoo.driver.home;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.FusedLocationFetcherBackground;
import product.clicklabs.jugnoo.driver.GPSLocationUpdate;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.MeteringService;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.MapUtils;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.SoundMediaPlayer;

public class StartRideLocationUpdateService extends Service implements GPSLocationUpdate {

	private final String TAG = StartRideLocationUpdateService.class.getSimpleName();
	private static FusedLocationFetcherBackground locationFetcherBG;
	private final int NOTIF_ID = 1212;

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
			long locationUpdateInterval = Prefs.with(this).getLong(Constants.START_RIDE_ALARM_LOCATION_FETCHER_INTERVAL,
					5000);

			if (locationFetcherBG != null) {
				locationFetcherBG.disconnect();
				locationFetcherBG = null;
			}
			locationFetcherBG = new FusedLocationFetcherBackground(this, locationUpdateInterval, LocationRequest.PRIORITY_HIGH_ACCURACY, this);
			locationFetcherBG.connect();

			startForeground(NOTIF_ID, MeteringService.generateNotification(this, getString(R.string.arrived), NOTIF_ID));

			return START_STICKY;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		Log.e(TAG, "onDestroy");
		unregisterRec();
		stopForeground(true);
		stopSelf();
	}

	private void unregisterRec(){
		if (locationFetcherBG != null) {
			locationFetcherBG.disconnect();
			locationFetcherBG = null;
		}
	}

	@Override
	public void onTaskRemoved(Intent rootIntent) {
		super.onTaskRemoved(rootIntent);
		Log.e(TAG, "onTaskRemoved, intent="+rootIntent);
	}

	@Override
	public void onGPSLocationChanged(Location location) {
		try{
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			Log.i(TAG, "customonReceive lat=" + latitude + ", lng=" + longitude);

			Context context = this;

			double pickupLat = Double.parseDouble(Prefs.with(context).getString(Constants.KEY_PICKUP_LATITUDE_ALARM, ""));
			double pickupLong = Double.parseDouble(Prefs.with(context).getString(Constants.KEY_PICKUP_LONGITUDE_ALARM, ""));

			double currentLat = Double.parseDouble(Prefs.with(context).getString(Constants.KEY_CURRENT_LATITUDE_ALARM,""));
			double currentLong = Double.parseDouble(Prefs.with(context).getString(Constants.KEY_CURRENT_LONGITUDE_ALARM,""));

			LatLng driverONPickupLatLng = new LatLng(latitude, longitude);
			LatLng pickupLatLng = new LatLng(pickupLat, pickupLong);
			LatLng currentLatLng = new LatLng(currentLat, currentLong);

			Log.e(TAG+" distance",
					String.valueOf(MapUtils.distance(driverONPickupLatLng, pickupLatLng)));
			Log.e(TAG+" distance",
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
			Log.e(TAG+" distance",
					String.valueOf(a) + " "+String.valueOf(b));

		} catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void refreshLocationFetchers(Context context) {

	}

}

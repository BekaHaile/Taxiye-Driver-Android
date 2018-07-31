package product.clicklabs.jugnoo.driver;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Utils;

public class GPSForegroundLocationFetcher implements LocationListener {
	private static final String TAG = GPSForegroundLocationFetcher.class.getSimpleName();

	private LocationManager locationManager;
	private Context context;
	private GPSLocationUpdate gpsLocationUpdate;
	private long requestInterval;

	private Location location;

	private Handler checkLocationUpdateStartedHandler;
	private Runnable checkLocationUpdateStartedRunnable;

	private static final long CHECK_LOCATION_INTERVAL = 20000, LAST_LOCATON_TIME_THRESHOLD = 2 * 60000 * 10000;

	public GPSForegroundLocationFetcher(GPSLocationUpdate gpsLocationUpdate, long requestInterval){
		this.context = (Context) gpsLocationUpdate;
		this.gpsLocationUpdate = gpsLocationUpdate;
		this.requestInterval = requestInterval;
	}




	/**
	 * Checks if location fetching is enabled in device or not
	 * @param context application context
	 * @return true if any location provider is enabled else false
	 */
	private synchronized boolean isLocationEnabled(Context context) {
		try{
			ContentResolver contentResolver = context.getContentResolver();
			boolean gpsStatus = Settings.Secure.isLocationProviderEnabled(contentResolver, LocationManager.GPS_PROVIDER);
			boolean netStatus = Settings.Secure.isLocationProviderEnabled(contentResolver, LocationManager.NETWORK_PROVIDER);
			return gpsStatus || netStatus;
		} catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}


	public synchronized void connect(){
		destroy();
		if(isLocationEnabled(context)){
			if(locationManager != null){
				locationManager.removeUpdates(this);
			}
			this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
					&& ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				Log.e(TAG, "ACCESS_FINE_LOCATION NOT GRANTED");
				return;
			}
			this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, this.requestInterval, 0, this);
			Location loc = getLocation();
			if(loc != null){
				gpsLocationUpdate.onGPSLocationChanged(loc);
			}
		}
		startCheckingLocationUpdates();
	}

	public synchronized void destroyWaitAndConnect(){
		destroy();
		new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                connect();
            }
        }, 2000);
	}

	public synchronized void destroy(){
		try{
			this.location = null;
			if(locationManager != null){
				locationManager.removeUpdates(this);
			}
		} catch(Exception e){

		}
		stopCheckingLocationUpdates();
	}

	public Location getLocation(){
		try{
			if(location != null){
				return location;
			} else{
				if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
					if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
							&& ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
						Log.e(TAG, "ACCESS_FINE_LOCATION NOT GRANTED");
						return null;
					}
					location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					return location;
				}
			}
		} catch(Exception e){e.printStackTrace();}
		return null;
	}
	
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
	
	@Override
	public void onProviderEnabled(String provider) {
	}
	
	@Override
	public void onProviderDisabled(String provider) {
		this.location = null;
	}
	
	@Override
	public void onLocationChanged(Location location) {
		try{
            if(!Utils.mockLocationEnabled(location)) {
                if (location != null) {
                    this.location = location;
                    gpsLocationUpdate.onGPSLocationChanged(location);
                }
            }
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private synchronized void startCheckingLocationUpdates(){
		checkLocationUpdateStartedHandler = new Handler();
		checkLocationUpdateStartedRunnable = new Runnable() {
			@Override
			public void run() {
				if(GPSForegroundLocationFetcher.this.location == null){
					destroyWaitAndConnect();
				}
				else{
					long timeSinceLastLocationFix = System.currentTimeMillis() - GPSForegroundLocationFetcher.this.location.getTime();
					if(timeSinceLastLocationFix > LAST_LOCATON_TIME_THRESHOLD){
						destroyWaitAndConnect();
					}
					else{
						checkLocationUpdateStartedHandler.postDelayed(checkLocationUpdateStartedRunnable, CHECK_LOCATION_INTERVAL);
					}
				}
			}
		};
		checkLocationUpdateStartedHandler.postDelayed(checkLocationUpdateStartedRunnable, CHECK_LOCATION_INTERVAL);
	}
	
	public synchronized void stopCheckingLocationUpdates(){
		try{
			if(checkLocationUpdateStartedHandler != null && checkLocationUpdateStartedRunnable != null){
				checkLocationUpdateStartedHandler.removeCallbacks(checkLocationUpdateStartedRunnable);
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			checkLocationUpdateStartedHandler = null;
			checkLocationUpdateStartedRunnable = null;
		}
	}
	
}

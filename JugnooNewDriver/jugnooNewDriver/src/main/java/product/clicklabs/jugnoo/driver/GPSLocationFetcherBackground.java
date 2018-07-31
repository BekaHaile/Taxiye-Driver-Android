package product.clicklabs.jugnoo.driver;


import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import product.clicklabs.jugnoo.driver.utils.Log;

public class GPSLocationFetcherBackground {
	private static final String TAG = GPSLocationFetcherBackground.class.getSimpleName();

	private LocationManager locationManager;
	private PendingIntent locationIntent;

	private long requestInterval;
	private Context context;

	private static final int LOCATION_PI_ID = 6979;



	/**
	 * Constructor for initializing LocationFetcher class' object
	 * @param context application context
	 */
	public GPSLocationFetcherBackground(Context context, long requestInterval){
		this.context = context;
		this.requestInterval = requestInterval;
	}




	/**
	 * Checks if location fetching is enabled in device or not
	 * @param context application context
	 * @return true if any location provider is enabled else false
	 */
	public boolean isLocationEnabled(Context context) {
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



	public void connect(){
		destroy();
		if(isLocationEnabled(context)){															// location fetching enabled
			this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			Intent intent = new Intent(context, GPSLocationReceiverBackground.class);
			this.locationIntent = PendingIntent.getBroadcast(context, LOCATION_PI_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
					!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION)
					!= PackageManager.PERMISSION_GRANTED) {
				Log.e(TAG, "ACCESS_FINE_LOCATION NOT GRANTED");
				return;
			}
			this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, this.requestInterval, 0, locationIntent);
		}
	}
	
	
	
	public void destroy(){
		try{
			if(locationManager != null && locationIntent != null){
				locationManager.removeUpdates(locationIntent);
				locationIntent.cancel();
			}
		}catch(Exception e){
		}
	}

}


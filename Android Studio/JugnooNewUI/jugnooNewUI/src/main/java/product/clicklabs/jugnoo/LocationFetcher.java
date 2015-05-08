package product.clicklabs.jugnoo;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.Log;

public class LocationFetcher implements GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener,LocationListener {
	
	private final String TAG = this.getClass().getSimpleName();
	private LocationClient locationclient;
	private LocationRequest locationrequest;
	private Location location;
	
	private long requestInterval;
	private LocationUpdate locationUpdate;
	private Context context;
	
	
	private String LOCATION_SP = "location_sp",
			LOCATION_LAT = "location_lat",
			LOCATION_LNG = "location_lng";
	
	public int priority;
	
	private Handler checkLocationUpdateStartedHandler;
	private Runnable checkLocationUpdateStartedRunnable;
	
	private static final long CHECK_LOCATION_INTERVAL = 20000, LAST_LOCATON_TIME_THRESHOLD = 2 * 60000;
	
	public LocationFetcher(LocationUpdate locationUpdate, long requestInterval, int priority){
			this.locationUpdate = locationUpdate;
			this.context = (Context) locationUpdate;
			this.requestInterval = requestInterval;
			this.priority = priority;
			connect();
	}
	
	public synchronized void connect(){
		destroy();
		int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
		if(resp == ConnectionResult.SUCCESS){														// google play services working
			if(isLocationEnabled(context)){															// location fetching enabled
				locationclient = new LocationClient(context, this, this);
				locationclient.connect();
			}
			else{																					// location disabled
			}
		}
		else{																						// google play services not working
			Log.e("Google Play Service Error ","="+resp);
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
	
	private synchronized void saveLatLngToSP(double latitude, double longitude){
		SharedPreferences preferences = context.getSharedPreferences(LOCATION_SP, 0);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(LOCATION_LAT, ""+latitude);
		editor.putString(LOCATION_LNG, ""+longitude);
		editor.commit();
	}
	
	
	private synchronized double getSavedLatFromSP(){
		SharedPreferences preferences = context.getSharedPreferences(LOCATION_SP, 0);
		String latitude = preferences.getString(LOCATION_LAT, ""+ 0);
		Log.d("saved last lat", "=="+latitude);
		return Double.parseDouble(latitude);
	}
	
	private synchronized double getSavedLngFromSP(){
		SharedPreferences preferences = context.getSharedPreferences(LOCATION_SP, 0);
		String longitude = preferences.getString(LOCATION_LNG, ""+0);
		return Double.parseDouble(longitude);
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
	
	
	
	
	
	
	/**
	 * Function to get latitude
	 * */
	public double getLatitude(){
		try{
			Location loc = getLocation();
			if(loc != null){
				return loc.getLatitude();
			}
		} catch(Exception e){Log.e("e","="+e.toString());}
		return getSavedLatFromSP();
	}
	
	/**
	 * Function to get longitude
	 * */
	public double getLongitude(){
		try{
			Location loc = getLocation();
			if(loc != null){
				return loc.getLongitude();
			}
		} catch(Exception e){Log.e("e","="+e.toString());}
		return getSavedLngFromSP();
	}
	
	public Location getLocation(){
		try{
			if(location != null){
				return location;
			}
			else{
				if(locationclient != null && locationclient.isConnected()){
					location = locationclient.getLastLocation();
					Log.e("Fetching last fused location", "="+location);
					return location;
				}
			}
		} catch(Exception e){e.printStackTrace();}
		return null;
	}
	
	

	
	
	public synchronized void destroy(){
		try{
			this.location = null;
			Log.e("location","destroy");
			if(locationclient!=null){
				if(locationclient.isConnected()){
					locationclient.removeLocationUpdates(this);
					locationclient.disconnect();
				}
				else if(locationclient.isConnecting()){
					locationclient.disconnect();
				}
			}
		}catch(Exception e){
			Log.e("e", "="+e.toString());
		}
		stopCheckingLocationUpdates();
	}
	

	private synchronized void startRequest(){
		try {
			locationrequest = LocationRequest.create();
			
			if(priority == 0){
				locationrequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
			}
			else if(priority == 1){
				locationrequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
			}
			else if(priority == 2){
				locationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			}
			
			locationrequest.setFastestInterval(requestInterval);
			locationrequest.setInterval(requestInterval);
			
			locationclient.requestLocationUpdates(locationrequest, LocationFetcher.this);
			Log.i("connected with priority", "="+priority);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onConnected(Bundle connectionHint) {
		Log.e(TAG, "onConnected");
		Location loc = getLocation();
		if(loc != null){
			locationUpdate.onLocationChanged(loc, priority);
		}
		startRequest();
	}

	@Override
	public void onDisconnected() {
		Log.e(TAG, "onDisconnected");
		this.location = null;
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.e(TAG, "onConnectionFailed");
		this.location = null;
	}

	@Override
	public void onLocationChanged(Location location) {
		try{
			if(location!=null){
				Log.i("loc chanfged ________---------******","="+location);
				this.location = location;
				locationUpdate.onLocationChanged(location, priority);
				saveLatLngToSP(location.getLatitude(), location.getLongitude());
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
				FlurryEventLogger.locationLog(LocationFetcher.this.location);
				if(LocationFetcher.this.location == null){
					destroyWaitAndConnect();
					FlurryEventLogger.locationRestart("null location");
				}
				else{
					long timeSinceLastLocationFix = System.currentTimeMillis() - LocationFetcher.this.location.getTime();
					if(timeSinceLastLocationFix > LAST_LOCATON_TIME_THRESHOLD){
						destroyWaitAndConnect();
						FlurryEventLogger.locationRestart("old location");
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
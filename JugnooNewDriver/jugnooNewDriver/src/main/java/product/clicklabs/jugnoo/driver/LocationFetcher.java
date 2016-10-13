package product.clicklabs.jugnoo.driver;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Utils;

public class LocationFetcher implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,LocationListener {
	
	private final String TAG = this.getClass().getSimpleName();

	private GoogleApiClient googleApiClient;
	private LocationRequest locationrequest;
	private Location location, locationUnchecked;
	
	private long requestInterval;
	private LocationUpdate locationUpdate;
	private Context context;
	
	
	public int priority;

	public LocationFetcher(LocationUpdate locationUpdate, long requestInterval, int priority){
			this.locationUpdate = locationUpdate;
			this.context = (Context) locationUpdate;
			this.requestInterval = requestInterval;
			this.priority = priority;
	}
	
	public synchronized void connect(){
		destroy();
		int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
		if(resp == ConnectionResult.SUCCESS){														// google play services working
			if(isLocationEnabled(context)){															// location fetching enabled
				buildGoogleApiClient(context);
			}
			else{																					// location disabled
			}
		}
		else{																						// google play services not working
			Log.e("Google Play Service Error ","="+resp);
		}
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

	private synchronized void saveLatLngToSP(Location location){
		Database2.getInstance(context).updateDriverCurrentLocation(context, location);
	}


	public static double getSavedLatFromSP(Context context){
		return Database2.getInstance(context).getDriverCurrentLocation(context).getLatitude();
	}

	public static double getSavedLngFromSP(Context context){
		return Database2.getInstance(context).getDriverCurrentLocation(context).getLongitude();
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




	protected void createLocationRequest(long interval, int priority) {
		locationrequest = new LocationRequest();
		locationrequest.setInterval(interval);
		locationrequest.setFastestInterval(interval / 2);
		if(priority == 1){
			locationrequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
		}
		else if(priority == 2){
			locationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		}
		else{
			locationrequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
		}
	}


	protected synchronized void buildGoogleApiClient(Context context) {
		try {
			googleApiClient = new GoogleApiClient.Builder(context)
					.addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this)
					.addApi(LocationServices.API).build();
			googleApiClient.connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void startLocationUpdates(long interval, int priority) {
		createLocationRequest(interval, priority);
		LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationrequest, this);
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
		} catch(Exception e){Log.e("e", "=" + e.toString());}
		return getSavedLatFromSP(context);
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
		} catch(Exception e){Log.e("e", "=" + e.toString());}
		return getSavedLngFromSP(context);
	}

	public Location getLocation(){
		try{
			if(location != null){
				if(Utils.compareDouble(location.getLatitude(), 0) != 0 && Utils.compareDouble(location.getLongitude(), 0) != 0){
					return location;
				}
			}
			else{
				if(googleApiClient != null && googleApiClient.isConnected()){
					location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
					Log.e("last fused location", "=" + location);
					if(Utils.compareDouble(location.getLatitude(), 0) != 0 && Utils.compareDouble(location.getLongitude(), 0) != 0){
						return location;
					}
				}
			}
		} catch(Exception e){e.printStackTrace();}
		return null;
	}

	public Location getLocationUnchecked(){
		try{
			return locationUnchecked;
		} catch(Exception e){e.printStackTrace();}
		return null;
	}
	




	public synchronized void destroy(){
		try{
			this.location = null;
			Log.e("location", "destroy");
			if(googleApiClient!=null){
				if(googleApiClient.isConnected()){
					LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
					googleApiClient.disconnect();
				}
				else if(googleApiClient.isConnecting()){
					googleApiClient.disconnect();
				}
			}
		}catch(Exception e){
			Log.e("e", "=" + e.toString());
		}
	}


	private synchronized void startRequest(){
		try {
			startLocationUpdates(requestInterval, priority);
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}



	@Override
	public void onConnected(Bundle connectionHint) {
		Log.e(TAG, "onConnected");
		Location loc = getLocation();
		if(loc != null){
			Bundle bundle = new Bundle();
			bundle.putBoolean("cached", true);
			loc.setExtras(bundle);
			locationUpdate.onLocationChanged(loc, priority);
		}
		startRequest();
	}

	@Override
	public void onConnectionSuspended(int i) {
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
            if(!Utils.mockLocationEnabled(location)) {
				Log.i("loc chanfged ----******", "=" + location);
				if(Utils.compareDouble(location.getLatitude(), 0) != 0 && Utils.compareDouble(location.getLongitude(), 0) != 0){
					this.location = location;
					locationUpdate.onLocationChanged(location, priority);
					saveLatLngToSP(location);
				}
            }
			locationUnchecked = location;
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
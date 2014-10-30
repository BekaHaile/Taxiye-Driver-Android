package product.clicklabs.jugnoo;


import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

public class LocationFetcherDriver implements GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener {

	
	private final String TAG = this.getClass().getSimpleName();
	private LocationClient locationclient;
	private LocationRequest locationrequest;
	public Location location; // location
	private PendingIntent locationIntent;
	
	private long requestInterval;
	private Context context;
	
	private static final int LOCATION_PI_ID = 6978;
	
	
	
	/**
	 * Constructor for initializing LocationFetcher class' object
	 * @param context application context
	 */
	public LocationFetcherDriver(Context context, long requestInterval){
		this.context = context;
		this.requestInterval = requestInterval;
		connect();
	}
	
	
	
	public boolean isConnected(){
		if(locationclient != null){
			return locationclient.isConnected();
		}
		return false;
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
	}
	
	
	
	public void destroy(){
		try{
			Log.e("location","destroy");
			if(locationclient!=null){
				if(locationclient.isConnected()){
					locationclient.removeLocationUpdates(locationIntent);
					locationIntent.cancel();
					locationclient.disconnect();
				}
				else if(locationclient.isConnecting()){
					locationclient.disconnect();
				}
			}
		}catch(Exception e){
			Log.e("e", "="+e.toString());
		}
	}

	
	@Override
	public void onConnected(Bundle connectionHint) {
		Log.e(TAG, "onConnected ********************************************************");
		
		//PRIORITY_BALANCED_POWER_ACCURACY
		//PRIORITY_HIGH_ACCURACY
		
		locationrequest = LocationRequest.create();
		locationrequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
		locationrequest.setInterval(requestInterval);
		locationrequest.setFastestInterval(requestInterval);
		
		
		Intent intent = new Intent(context, LocationReceiverDriver.class);
		locationIntent = PendingIntent.getBroadcast(context, LOCATION_PI_ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		locationclient.requestLocationUpdates(locationrequest, locationIntent);
		
		Log.e("locationrequest priority", "="+locationrequest.getPriority());
	}

	@Override
	public void onDisconnected() {
		Log.e(TAG, "onDisconnected ********************************************************");
		destroy();
		connect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.e(TAG, "onConnectionFailed ********************************************************");
		destroy();
		connect();
	}

}


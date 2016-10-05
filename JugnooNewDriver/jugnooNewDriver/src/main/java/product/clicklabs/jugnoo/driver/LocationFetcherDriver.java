package product.clicklabs.jugnoo.driver;


import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import product.clicklabs.jugnoo.driver.utils.Log;

public class LocationFetcherDriver implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

	
	private final String TAG = this.getClass().getSimpleName();

	private GoogleApiClient googleApiClient;
	private LocationRequest locationrequest;
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
		if(requestInterval >= 10000) {
			this.requestInterval = requestInterval;
		} else {
			this.requestInterval = requestInterval;
		}
		connect();
	}
	
	
	
	public boolean isConnected(){
		if(googleApiClient != null){
			return googleApiClient.isConnected();
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
				buildGoogleApiClient(context);
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
			if(googleApiClient!=null){
				if(googleApiClient.isConnected()){
					LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, locationIntent);
					locationIntent.cancel();
					googleApiClient.disconnect();
				}
				else if(googleApiClient.isConnecting()){
					googleApiClient.disconnect();
				}
			}
		}catch(Exception e){
			Log.e("e", "="+e.toString());
		}
	}



	protected void createLocationRequest(long interval) {
		locationrequest = new LocationRequest();
		locationrequest.setInterval(interval);
		locationrequest.setFastestInterval(interval);
		locationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//		Toast.makeText(context,String.valueOf(interval),Toast.LENGTH_LONG).show();
	}


	protected synchronized void buildGoogleApiClient(Context context) {
		googleApiClient = new GoogleApiClient.Builder(context)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API).build();
		googleApiClient.connect();
	}

	protected void startLocationUpdates(long interval) {
		createLocationRequest(interval);
		Intent intent = new Intent(context, LocationReceiverDriver.class);
		locationIntent = PendingIntent.getBroadcast(context, LOCATION_PI_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationrequest, locationIntent);
	}





	@Override
	public void onConnected(Bundle connectionHint) {
		Log.e(TAG, "onConnected ********************************************************");
		startLocationUpdates(requestInterval);
	}

	@Override
	public void onConnectionSuspended(int i) {
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


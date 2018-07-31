package product.clicklabs.jugnoo.driver;


import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import product.clicklabs.jugnoo.driver.utils.Log;

public class FusedLocationFetcherBackgroundBalanced implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

	private static final String TAG = FusedLocationFetcherBackgroundBalanced.class.getSimpleName();
	private GoogleApiClient googleApiClient;
	private LocationRequest locationrequest;
	private PendingIntent locationIntent;

	private long requestInterval;
	private Context context;

	private static final int LOCATION_PI_ID = 6980;



	/**
	 * Constructor for initializing LocationFetcher class' object
	 * @param context application context
	 */
	public FusedLocationFetcherBackgroundBalanced(Context context, long requestInterval){
		this.context = context;
		this.requestInterval = requestInterval;
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
		locationrequest.setFastestInterval(interval / 2);
		locationrequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
	}


	protected synchronized void buildGoogleApiClient(Context context) {
		googleApiClient = new GoogleApiClient.Builder(context)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API).build();
		googleApiClient.connect();
	}

	protected void startLocationUpdates(long interval) {
		try {
			createLocationRequest(interval);
			Intent intent = new Intent(context, FusedLocationReceiverBackgroundBalanced.class);
			locationIntent = PendingIntent.getBroadcast(context, LOCATION_PI_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
					&& ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				Log.e(TAG, "ACCESS_FINE_LOCATION NOT GRANTED");
				return;
			}
			LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationrequest, locationIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	@Override
	public void onConnected(Bundle connectionHint) {
		startLocationUpdates(requestInterval);
	}

	@Override
	public void onConnectionSuspended(int i) {
		destroy();
		connect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		destroy();
		connect();
	}

}


package product.clicklabs.jugnoo.driver.home.utils;


import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;



public class LocationFetcherBG implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {


	private final String TAG = this.getClass().getSimpleName();

	private GoogleApiClient googleApiClient;
	private LocationRequest locationrequest;
	private PendingIntent locationIntent;

	private long requestInterval;
	private Context context;

	private static final int LOCATION_PI_ID = 611;


	/**
	 * Constructor for initializing LocationFetcher class' object
	 * @param context application context
	 */
	public LocationFetcherBG(Context context, long requestInterval){
		this.context = context;
		this.requestInterval = requestInterval;
		connect();
	}



	public boolean isConnected(){
		return googleApiClient != null && googleApiClient.isConnected();
	}



	public void connect(){
		int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
		if(resp == ConnectionResult.SUCCESS){														// google play services working
			buildGoogleApiClient(context);
		}
		else{																						// google play services not working
			Log.e("Google Play Service Error ", "=" + resp);
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
		Intent intent = new Intent(context, LocationReceiverBG.class);
		locationIntent = PendingIntent.getBroadcast(context, LOCATION_PI_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
				&& ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			product.clicklabs.jugnoo.driver.utils.Log.e(TAG, "ACCESS_FINE_LOCATION NOT GRANTED");
			return;
		}
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


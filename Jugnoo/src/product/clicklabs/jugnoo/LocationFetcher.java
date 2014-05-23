package product.clicklabs.jugnoo;


import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.android.gms.maps.model.LatLng;

public class LocationFetcher implements GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener,LocationListener {

	private final String TAG = this.getClass().getSimpleName();
	private LocationClient locationclient;
	private LocationRequest locationrequest;
	private static int count = 0;
	private static boolean dialogShown = false;
	private Location location; // location
	double latitude = 30.7500; // latitude
	double longitude = 76.7800; // longitude
//	
	/**
	 * Constructor for initializing LocationFetcher class' object
	 * @param context application context
	 */
	public LocationFetcher(Context context){
		int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
		if(resp == ConnectionResult.SUCCESS){														// google play services working
			if(isLocationEnabled(context)){															// location fetching enabled
				locationclient = new LocationClient(context, this, this);
				locationclient.connect();
				start(context);
			}
			else{																					// location disabled
//				showSettingsAlert(context);
			}
		}
		else{																						// google play services not working
			Log.e("Google Play Service Error ","="+resp);
		}
	}

	
	double distance(LatLng start, LatLng end) {
		try {
			Location location1 = new Location("locationA");
			location1.setLatitude(start.latitude);
			location1.setLongitude(start.longitude);
			Location location2 = new Location("locationA");
			location2.setLatitude(end.latitude);
			location2.setLongitude(end.longitude);

			double distance = location1.distanceTo(location2);
			return distance;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;

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
	
	
	/**
	 * Function to show settings alert dialog
	 * On pressing Settings button will lauch Settings Options
	 * */
	public void showSettingsAlert(final Context mContext){
		try{
		if(!dialogShown){
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
   	 
        // Setting Dialog Title
        alertDialog.setTitle("Loaction Settings");
        alertDialog.setCancelable(false);
 
        // Setting Dialog Message
        alertDialog.setMessage("Location is not enabled. Do you want to go to settings menu?");
 
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
            	Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            	mContext.startActivity(intent);
            	dialog.dismiss();
            	dialogShown = false;
            }
        });
 
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            dialogShown = false;
            }
        });
 
        // Showing Alert Message
        alertDialog.show();
        dialogShown = true;
		}} catch(Exception e){e.printStackTrace();}
	}
	
	
	
	
	/**
	 * Function to get latitude
	 * */
	public double getLatitude(){
		try{
		if(location == null){
			location =locationclient.getLastLocation();
			if(location != null){
				latitude = location.getLatitude();
				Log.e("last location","="+latitude);
			}
		}else{
			latitude = location.getLatitude();
		}
		
		} catch(Exception e){Log.e("e","="+e.toString());}
		
		return latitude;
	}
	
	/**
	 * Function to get longitude
	 * */
	public double getLongitude(){
		try{
		if(location == null){
			location =locationclient.getLastLocation();
			if(location != null){
				longitude = location.getLatitude();
				Log.e("last location","="+longitude);
			}
			
		}
		else{
			longitude = location.getLongitude();
		}
		} catch(Exception e){Log.e("e","="+e.toString());}
		
		return longitude;
	}
	
	
	/**
	 * Get last known location of device
	 * @return
	 */
	public Location getLocation(){
		try{
		if(locationclient!=null && locationclient.isConnected()){
			Location loc =locationclient.getLastLocation();
			if(loc != null){
				location = loc;
				Log.e(TAG, "Last Known Location :" + loc.getLatitude() + "," + loc.getLongitude());
			}
			return loc;
		}} catch(Exception e){e.printStackTrace();}
		return null;
	}
	
	/**
	 * Start receiving location updates
	 */
	public void start(final Context context){
		try{
		if(isLocationEnabled(context)){	
			try{
				this.stop();
			} catch(Exception e){e.printStackTrace();}
			
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				if(locationclient!=null && locationclient.isConnected()){
					Log.e("locationclient", "not null");
					
					locationrequest = LocationRequest.create();
					locationrequest.setInterval(20000);
					locationclient.requestLocationUpdates(locationrequest, LocationFetcher.this);
				}
				else{
					Log.e("locationclient", "null");
					locationclient = new LocationClient(context, LocationFetcher.this, LocationFetcher.this);
					locationclient.connect();
					start(context);
				}
			}
		}, 1000);
		}
		} catch(Exception e){e.printStackTrace();}
		
	}


	/**
	 * Stop receiving location updates
	 */
	public void stop() {
		try{
		Log.e("location","stop");
		if(locationclient!=null){
			locationclient.removeLocationUpdates(this);
		}
		} catch(Exception e){Log.e("e", "="+e.toString());}
	}
	
	public void destroy(){
		try{
		Log.e("location","destroy");
		if(locationclient!=null){
			locationclient.removeLocationUpdates(this);
			locationclient.disconnect();
		}}catch(Exception e){e.printStackTrace();}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Log.e(TAG, "onConnected");

	}

	@Override
	public void onDisconnected() {
		Log.e(TAG, "onDisconnected");

	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.e(TAG, "onConnectionFailed");

	}

	@Override
	public void onLocationChanged(Location location) {
		try{
		if(location!=null){
			this.location = location;
			Log.e(TAG+count, "Location Request :" + location.getLatitude() + "," + location.getLongitude());
			count ++;
		}}catch(Exception e){e.printStackTrace();}

	}


}
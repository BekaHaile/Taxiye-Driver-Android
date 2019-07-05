package product.clicklabs.jugnoo.driver;


import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Utils;

public class FusedLocationFetcherBackground extends LocationCallback {

	private FusedLocationProviderClient fusedLocationClient;
	private LocationRequest locationrequest;
	private GPSLocationUpdate gpsLocationUpdate;

	/**
	 * Constructor for initializing LocationFetcher class' object
	 */
	public FusedLocationFetcherBackground(Context context, long requestInterval, int priority, GPSLocationUpdate gpsLocationUpdate) {
		this.gpsLocationUpdate = gpsLocationUpdate;
		fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

		locationrequest = new LocationRequest();
		locationrequest.setInterval(requestInterval);
		locationrequest.setFastestInterval(requestInterval);
		locationrequest.setMaxWaitTime(requestInterval);
		locationrequest.setPriority(priority ); //LocationRequest.PRIORITY_HIGH_ACCURACY
	}


	public void connect() {
		fusedLocationClient.removeLocationUpdates(this);
		if (ActivityCompat.checkSelfPermission(fusedLocationClient.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
				&& ActivityCompat.checkSelfPermission(fusedLocationClient.getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			return;
		}
		fusedLocationClient.requestLocationUpdates(locationrequest, this, null);
	}



	public void disconnect(){
		try{
			Log.e("FusedLocationFetcherBackground", "disconnect");
			fusedLocationClient.removeLocationUpdates(this);
		}catch(Exception e){
			Log.e("e", "="+e.toString());
		}
	}

	@Override
	public void onLocationResult(LocationResult locationResult) {
		super.onLocationResult(locationResult);
		if (locationResult != null) {
			Location location = locationResult.getLocations().get(locationResult.getLocations().size() - 1);
			if(location != null && gpsLocationUpdate != null && !Utils.mockLocationEnabled(location)) {
				Log.w("FusedLocationFetcherBackground", "onReceive location="+location);
				gpsLocationUpdate.onGPSLocationChanged(location);
			}
		}

	}
}


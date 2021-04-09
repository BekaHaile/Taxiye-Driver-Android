package product.clicklabs.jugnoo.driver;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Utils;

public class LocationFetcher extends LocationCallback {

	private static final float SMALLEST_DISPLACEMENT = 100;

	private final String TAG = this.getClass().getSimpleName();

	private FusedLocationProviderClient fusedLocationClient;
	private LocationRequest locationrequest;
	private Location location, locationUnchecked;

	private LocationUpdate locationUpdate;
	private Context context;


	public int priority;

	public LocationFetcher(LocationUpdate locationUpdate, long requestInterval, int priority){
		this((Context) locationUpdate, locationUpdate, requestInterval, priority);
	}

	public LocationFetcher(Context context, LocationUpdate locationUpdate, long requestInterval, int priority){
		fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
		this.locationUpdate = locationUpdate;
		this.context = context;
		this.priority = priority;

		createLocationRequest(requestInterval, priority);
	}

	public void connect(){
		GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
		int resp = googleApiAvailability.isGooglePlayServicesAvailable(fusedLocationClient.getApplicationContext());
		if (resp != ConnectionResult.SUCCESS) {
			Log.e("Google Play error", "=" + resp);
			return;
		}

		fusedLocationClient.removeLocationUpdates(this);
		if (ActivityCompat.checkSelfPermission(fusedLocationClient.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
				&& ActivityCompat.checkSelfPermission(fusedLocationClient.getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			return;
		}
		fusedLocationClient.requestLocationUpdates(locationrequest, this, Looper.myLooper());
	}


	public static void saveLatLngToSP(Context context, Location location){
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
	private  boolean isLocationEnabled(Context context) {
		try{
			LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
			return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
					|| locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
			 		|| locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);
		} catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}




	protected void createLocationRequest(long interval, int priority) {
		locationrequest = new LocationRequest();
		locationrequest.setInterval(interval);
		locationrequest.setFastestInterval(interval / 2);
		locationrequest.setSmallestDisplacement(SMALLEST_DISPLACEMENT);
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
	@Nullable
	public Location getLocation(){
		try{
			if(location != null){
				if(Utils.compareDouble(location.getLatitude(), 0) != 0 && Utils.compareDouble(location.getLongitude(), 0) != 0){
					return location;
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
	




	public void destroy(){
		try{
			Log.e("location", "destroy");
			fusedLocationClient.removeLocationUpdates(this);
		} catch(Exception e){
			Log.e("e", "=" + e.toString());
		}
	}




	@Override
	public void onLocationResult(LocationResult locationResult) {
		super.onLocationResult(locationResult);
		if (locationResult != null) {
			Location location = locationResult.getLastLocation();
			if(location != null && !Utils.mockLocationEnabled(location)) {
				this.location = location;
				locationUpdate.onLocationChanged(location, priority);
				saveLatLngToSP(context, location);
				Log.w("Location_fetcher_onLocationResult", "onReceive location="+location);
			}
			locationUnchecked = location;
		}
	}
}
package product.clicklabs.jugnoo.driver;

import android.location.Location;

import com.google.android.gms.maps.model.PolylineOptions;

public interface GpsDistanceTimeUpdater {
	public void updateDistanceTime(double distance, long elapsedTime, long waitTime, Location lastGPSLocation, Location lastFusedLocation,
								   double totalHaversineDistance, boolean fromGPS);
	public void drawOldPath();
	public void addPathToMap(PolylineOptions polylineOptions);
	public void googleApiHitStart();
	public void googleApiHitStop();
}

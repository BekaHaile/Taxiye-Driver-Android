package product.clicklabs.jugnoo.driver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.google.android.gms.location.LocationClient;

public class FusedLocationReceiverBackgroundBalanced extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
    	final Location location = (Location) intent.getExtras().get(LocationClient.KEY_LOCATION_CHANGED);
    	if(location != null && GpsDistanceCalculator.fusedLocationUpdate != null){
    		GpsDistanceCalculator.fusedLocationUpdate.onFusedLocationChanged(location);
    	}
    }
    
}
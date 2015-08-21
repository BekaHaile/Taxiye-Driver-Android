package product.clicklabs.jugnoo.driver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;

import product.clicklabs.jugnoo.driver.utils.Utils;

public class GPSLocationReceiverBackground extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        if(!Utils.mockLocationEnabled(context)) {
            final Location location = (Location) intent.getExtras().get(LocationManager.KEY_LOCATION_CHANGED);
            if (location != null && GpsDistanceCalculator.gpsLocationUpdate != null) {
                GpsDistanceCalculator.gpsLocationUpdate.onGPSLocationChanged(location);
            }
        }
    }
    
}
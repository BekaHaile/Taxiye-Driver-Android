package product.clicklabs.jugnoo.driver;

import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.Log;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.model.LatLng;

public class LocationReceiverDriver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
    	final Location location = (Location) intent.getExtras().get(LocationClient.KEY_LOCATION_CHANGED);
    	if(location != null){
	    	new Thread(new Runnable() {
				@Override
				public void run() {
					Database2.getInstance(context).updateDriverCurrentLocation(new LatLng(location.getLatitude(), location.getLongitude()));
//			    	Log.e("DriverLocationUpdateService location in pi reciever ", "=="+location);
			    	Log.writeLogToFile("LocationReciever", "Receiver "+DateOperations.getCurrentTime()+" = "+location + " hasNet = "+AppStatus.getInstance(context).isOnline(context));
					new DriverLocationDispatcher().sendLocationToServer(context, "LocationReciever");
				}
			}).start();
    	}
    }
    
}
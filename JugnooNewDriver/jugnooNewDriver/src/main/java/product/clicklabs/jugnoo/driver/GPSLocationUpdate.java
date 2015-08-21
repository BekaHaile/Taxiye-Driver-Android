package product.clicklabs.jugnoo.driver;

import android.content.Context;
import android.location.Location;

public interface GPSLocationUpdate {
	void onGPSLocationChanged(Location location);
	void refreshLocationFetchers(Context context);
}

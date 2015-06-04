package product.clicklabs.jugnoo.driver;

import android.location.Location;

public interface LocationUpdate {
	public void onLocationChanged(Location location, int priority);
}

package product.clicklabs.jugnoo;

import android.location.Location;

public interface LocationUpdate {
	public void onLocationChanged(Location location, int priority);
}

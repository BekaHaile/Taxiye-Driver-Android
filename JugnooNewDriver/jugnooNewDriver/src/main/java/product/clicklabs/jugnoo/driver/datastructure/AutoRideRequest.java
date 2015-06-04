package product.clicklabs.jugnoo.driver.datastructure;

import com.google.android.gms.maps.model.LatLng;

public class AutoRideRequest extends DriverRideRequest{

	public AutoRideRequest(String engagementId, String customerId, LatLng latLng, String startTime, String address, int businessId,
			int referenceId){
		super(engagementId, customerId, latLng, startTime, address, businessId, referenceId);
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
	
	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}
	
}

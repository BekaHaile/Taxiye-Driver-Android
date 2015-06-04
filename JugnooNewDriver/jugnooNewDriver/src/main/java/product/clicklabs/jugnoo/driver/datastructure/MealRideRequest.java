package product.clicklabs.jugnoo.driver.datastructure;

import com.google.android.gms.maps.model.LatLng;

public class MealRideRequest extends DriverRideRequest{

	public String rideTime;
	
	public MealRideRequest(String engagementId, String customerId, LatLng latLng, String startTime, String address, int businessId,
			int referenceId, String rideTime){
		super(engagementId, customerId, latLng, startTime, address, businessId, referenceId);
		this.rideTime = rideTime;
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

package product.clicklabs.jugnoo.driver.datastructure;

import com.google.android.gms.maps.model.LatLng;

public class MealRideRequest extends DriverRideRequest{

	public String rideTime;
	
	public MealRideRequest(String engagementId, String customerId, LatLng latLng, String startTime, String address, int businessId,
			int referenceId, String rideTime, double fareFactor){
		super(engagementId, customerId, latLng, startTime, address, businessId, referenceId, fareFactor);
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

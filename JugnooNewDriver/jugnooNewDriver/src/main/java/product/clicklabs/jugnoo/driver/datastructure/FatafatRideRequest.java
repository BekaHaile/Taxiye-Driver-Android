package product.clicklabs.jugnoo.driver.datastructure;

import com.google.android.gms.maps.model.LatLng;

public class FatafatRideRequest extends DriverRideRequest{

	public int orderAmount;
	
	public FatafatRideRequest(String engagementId, String customerId, LatLng latLng, String startTime, String address, int businessId,
			int referenceId, int orderAmount){
		super(engagementId, customerId, latLng, startTime, address, businessId, referenceId);
		this.orderAmount = orderAmount;
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

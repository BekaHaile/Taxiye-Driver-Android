package product.clicklabs.jugnoo.datastructure;

import com.google.android.gms.maps.model.LatLng;

public class DriverRideRequest {
	
	public String engagementId, customerId, address, startTime;
	public LatLng latLng;
	
	public DriverRideRequest(String engagementId, String customerId, LatLng latLng, String startTime, String address){
		this.engagementId = engagementId;
		this.customerId = customerId;
		this.latLng = latLng;
		this.startTime = startTime;
		this.address = address;
	}
	
	@Override
	public String toString() {
		return engagementId + " " + customerId + " " + latLng + " " + startTime;
	}
	
	@Override
	public boolean equals(Object o) {
		try{
			if(((DriverRideRequest)o).engagementId.equalsIgnoreCase(engagementId)){
				return true;
			}
			else{
				return false;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return super.equals(o);
	}
	
}

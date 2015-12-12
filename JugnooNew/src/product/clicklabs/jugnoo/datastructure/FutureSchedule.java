package product.clicklabs.jugnoo.datastructure;

import com.google.android.gms.maps.model.LatLng;

public class FutureSchedule {
	
	public String pickupId, pickupAddress, pickupTime;
	public LatLng pickupLatLng;
	public int modifiable, status;
	
	
	public FutureSchedule(String pickupId, String pickupAddress, String pickupTime, LatLng pickupLatLng, int modifiable, int status){
		this.pickupId = pickupId;
		this.pickupAddress = pickupAddress;
		this.pickupTime = pickupTime;
		this.pickupLatLng = pickupLatLng;
		this.modifiable = modifiable;
		this.status = status;
	}
	
	@Override
	public String toString() {
		return pickupId + " " + pickupLatLng + " " + pickupTime + " " + status;
	}
	
}

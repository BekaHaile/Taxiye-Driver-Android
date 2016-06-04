package product.clicklabs.jugnoo.datastructure;

import com.google.android.gms.maps.model.LatLng;

public class FutureSchedule {
	
	public String pickupId, pickupAddress, pickupDate, pickupTime;
	public LatLng pickupLatLng;
	public int modifiable, status;
	
	
	public FutureSchedule(String pickupId, String pickupAddress, String pickupDate, String pickupTime, LatLng pickupLatLng, int modifiable, int status){
		this.pickupId = pickupId;
		this.pickupAddress = pickupAddress;
		this.pickupDate = pickupDate;
		this.pickupTime = pickupTime;
		this.pickupLatLng = pickupLatLng;
		this.modifiable = modifiable;
		this.status = status;
	}
	
	@Override
	public String toString() {
		return pickupId + " " + pickupLatLng + " " + pickupDate + " " + pickupTime + " " + status;
	}
	
}

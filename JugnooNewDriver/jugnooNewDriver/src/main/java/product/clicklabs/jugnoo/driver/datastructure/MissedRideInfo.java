package product.clicklabs.jugnoo.driver.datastructure;

public class MissedRideInfo {
	public String engagementId;
	public String pickupLocationAddress;
	public String timestamp;
	public String customerName;
	public String customerDistance;
	public MissedRideInfo(String engagementId, String pickupLocationAddress, String timestamp, String customerName, String customerDistance){
		this.engagementId = engagementId;
		this.pickupLocationAddress = pickupLocationAddress;
		this.timestamp = timestamp;
		this.customerName = customerName;
		this.customerDistance = customerDistance;
	}
}

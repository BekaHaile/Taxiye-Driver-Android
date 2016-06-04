package product.clicklabs.jugnoo.datastructure;

public class RideInfo {
	
	public String pickupAddress;
	public String dropAddress;
	public double amount;
	public double distance;
	public double rideTime;
	public String date;
    public int engagementId;
	public int rideRated;
	
	public RideInfo(String pickupAddress, String dropAddress, double amount, double distance, double rideTime, String date){
		this.pickupAddress = pickupAddress;
		this.dropAddress = dropAddress;
		this.amount = amount;
		this.distance = distance;
		this.rideTime = rideTime;
		this.date = date;
        this.engagementId = 1;
        this.rideRated = 1;
	}
	
	@Override
	public String toString() {
		return pickupAddress + " " + dropAddress + " " + amount + " " + distance + " " + date;
	}
	
}

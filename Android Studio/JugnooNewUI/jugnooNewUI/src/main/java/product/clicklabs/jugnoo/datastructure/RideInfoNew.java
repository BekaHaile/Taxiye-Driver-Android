package product.clicklabs.jugnoo.datastructure;

public class RideInfoNew {
	
	public String pickupAddress;
	public String dropAddress;
	public double amount;
	public double distance;
	public double rideTime;
	public String date;
	
	public RideInfoNew(String pickupAddress, String dropAddress, double amount, double distance, double rideTime, String date){
		this.pickupAddress = pickupAddress;
		this.dropAddress = dropAddress;
		this.amount = amount;
		this.distance = distance;
		this.rideTime = rideTime;
		this.date = date;
	}
	
	@Override
	public String toString() {
		return pickupAddress + " " + dropAddress + " " + amount + " " + distance + " " + date;
	}
	
}

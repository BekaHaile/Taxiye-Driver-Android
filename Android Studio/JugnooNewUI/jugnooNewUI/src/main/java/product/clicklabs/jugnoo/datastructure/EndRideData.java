package product.clicklabs.jugnoo.datastructure;

public class EndRideData {
	
	public String engagementId, 
	pickupAddress, dropAddress, 
	pickupTime, dropTime, banner;
	public double fare, discount, paidUsingWallet, toPay, 
	distance, rideTime, baseFare;
	
	public EndRideData(String engagementId, String pickupAddress, String dropAddress, String pickupTime, String dropTime, String banner, 
			double fare, double discount, double paidUsingWallet, double toPay, double distance, double rideTime, double baseFare){
		this.engagementId = engagementId;
		this.pickupAddress = pickupAddress;
		this.dropAddress = dropAddress;
		this.pickupTime = pickupTime;
		this.dropTime = dropTime;
		this.banner = banner;
		
		this.fare = fare;
		this.discount = discount;
		this.paidUsingWallet = paidUsingWallet;
		this.toPay = toPay;
		
		this.distance = distance;
		this.rideTime = rideTime;
		this.baseFare = baseFare;
	}
	
}

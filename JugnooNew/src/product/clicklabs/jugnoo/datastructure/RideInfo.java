package product.clicklabs.jugnoo.datastructure;

public class RideInfo {
	
	public String id;
	public String fromLocation;
	public String toLocation;
	public String fare;
	public String distance;
	public String time;
	public String balance;
	public int couponUsed;
	public int paymentMode;
	
	public RideInfo(String id, String fromLocation, String toLocation, String fare, String distance, String time, 
			String balance, int couponUsed, int paymentMode){
		this.id = id;
		this.fromLocation = fromLocation;
		this.toLocation = toLocation;
		this.fare = fare;
		this.distance = distance;
		this.time = time;
		this.couponUsed = couponUsed;
		this.balance = balance;
		this.paymentMode = paymentMode;
	}
	
	@Override
	public String toString() {
		return fromLocation + " " + toLocation + " " + fare + " " + distance + " " + time;
	}
	
}

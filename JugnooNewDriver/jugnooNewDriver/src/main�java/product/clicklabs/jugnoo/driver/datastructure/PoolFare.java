package product.clicklabs.jugnoo.driver.datastructure;

/**
 * Created by shankar on 6/14/16.
 */
public class PoolFare {
	private double distance;
	private long rideTime;
	private double convenienceCharge, fare;

	public PoolFare(double distance, long rideTime, double convenienceCharge, double fare) {
		this.distance = distance;
		this.rideTime = rideTime;
		this.convenienceCharge = convenienceCharge;
		this.fare = fare;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public long getRideTime() {
		return rideTime;
	}

	public void setRideTime(long rideTime) {
		this.rideTime = rideTime;
	}

	public double getConvenienceCharge() {
		return convenienceCharge;
	}

	public void setConvenienceCharge(double convenienceCharge) {
		this.convenienceCharge = convenienceCharge;
	}

	public double getFare() {
		return fare;
	}

	public void setFare(double fare) {
		this.fare = fare;
	}
}

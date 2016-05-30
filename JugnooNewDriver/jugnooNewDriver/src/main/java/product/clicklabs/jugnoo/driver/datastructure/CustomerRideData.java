package product.clicklabs.jugnoo.driver.datastructure;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shankar on 5/30/16.
 */
public class CustomerRideData {
	@SerializedName("distance")
	@Expose
	private double distance = 0;

	@SerializedName("haversineDistance")
	@Expose
	private double haversineDistance = 0;

	@SerializedName("rideTime")
	@Expose
	private long rideTime = 0;

	@SerializedName("waitTime")
	@Expose
	private long waitTime = 0;

	public CustomerRideData(){}

	public CustomerRideData(double distance, double haversineDistance, long rideTime, long waitTime){
		this.distance = distance;
		this.haversineDistance = haversineDistance;
		this.rideTime = rideTime;
		this.waitTime = waitTime;
	}


	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public double getTotalDistance(double totalDistance) {
		return totalDistance - distance;
	}





	public double getHaversineDistance() {
		return haversineDistance;
	}

	public void setHaversineDistance(double haversineDistance) {
		this.haversineDistance = haversineDistance;
	}

	public double getTotalHaversineDistance(double totalHaversineDistance) {
		return totalHaversineDistance - haversineDistance;
	}





	public long getRideTime() {
		return rideTime;
	}

	public void setRideTime(long rideTime) {
		this.rideTime = rideTime;
	}





	public long getWaitTime() {
		return waitTime;
	}

	public void setWaitTime(long waitTime) {
		this.waitTime = waitTime;
	}

}

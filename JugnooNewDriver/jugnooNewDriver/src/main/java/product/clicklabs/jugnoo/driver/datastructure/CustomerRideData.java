package product.clicklabs.jugnoo.driver.datastructure;

import android.content.Context;

import product.clicklabs.jugnoo.driver.GpsDistanceCalculator;

/**
 * Created by shankar on 5/30/16.
 */
public class CustomerRideData {
	private double distance = 0;
	private double haversineDistance = 0;
	private long startRideTime = System.currentTimeMillis();
	private long waitTime = 0;
	public CustomerRideData(){}

	public double getDistance(Context context) {
		double spDistance = GpsDistanceCalculator.getTotalDistanceFromSP(context);
		if(spDistance > distance){
			distance = spDistance;
		}
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}




	public double getHaversineDistance() {
		return haversineDistance;
	}

	public void setHaversineDistance(double haversineDistance) {
		this.haversineDistance = haversineDistance;
	}






	public long getStartRideTime() {
		return startRideTime;
	}

	public void setStartRideTime(long startRideTime) {
		this.startRideTime = startRideTime;
	}






	public long getWaitTime() {
		return waitTime;
	}

	public void setWaitTime(long waitTime) {
		this.waitTime = waitTime;
	}

}

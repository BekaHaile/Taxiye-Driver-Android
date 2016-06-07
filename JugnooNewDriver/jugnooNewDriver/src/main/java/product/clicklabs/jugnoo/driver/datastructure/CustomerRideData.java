package product.clicklabs.jugnoo.driver.datastructure;

/**
 * Created by shankar on 5/30/16.
 */
public class CustomerRideData {
	private double distance = 0;
	private double haversineDistance = 0;
	private long startRideTime = System.currentTimeMillis();
	private long waitTime = 0;
	public CustomerRideData(){}

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





	public long getStartRideTime() {
		return startRideTime;
	}

	public long getElapsedRideTime() {
		return System.currentTimeMillis() - startRideTime;
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

	public long getTotalWaitTime(long totalWaitTime) {
		return totalWaitTime - waitTime;
	}



	public void setValues(CustomerRideData customerRideData){
		setDistance(customerRideData.getDistance());
		setHaversineDistance(customerRideData.getHaversineDistance());
		setStartRideTime(System.currentTimeMillis());
		setWaitTime(customerRideData.getWaitTime());
	}

}

package product.clicklabs.jugnoo.datastructure;

public class FareStructure {
	public double fixedFare;
	public double thresholdDistance;
	public double farePerKm;
	public double farePerMin;
	public double freeMinutes;
	
	public FareStructure(double fixedFare, double thresholdDistance, double farePerKm, double farePerMin, double freeMinutes){
		this.fixedFare = fixedFare;
		this.thresholdDistance = thresholdDistance;
		this.farePerKm = farePerKm;
		this.farePerMin = farePerMin;
		this.freeMinutes = freeMinutes;
	}
	
	public double calculateFare(double totalDistanceInKm, double totalTimeInMin){
		totalTimeInMin = totalTimeInMin - freeMinutes;
		if(totalTimeInMin < 0){
			totalTimeInMin = 0;
		}
		double fareOfRideTime = totalTimeInMin * farePerMin;
		double fare = fareOfRideTime + fixedFare + ((totalDistanceInKm <= thresholdDistance) ? (0) : ((totalDistanceInKm - thresholdDistance)* farePerKm));
		fare = Math.ceil(fare);
		return fare;
	}
}

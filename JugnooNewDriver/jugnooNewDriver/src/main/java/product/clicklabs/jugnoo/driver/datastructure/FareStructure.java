package product.clicklabs.jugnoo.driver.datastructure;

public class FareStructure {
	public double fixedFare;
	public double thresholdDistance;
	public double farePerKm;
	public double farePerMin;
	public double freeMinutes;
	public double farePerWaitingMin;
	public double freeWaitingMinutes;
	
	public double fareFactor;

	public double luggageFare;
	public double convenienceCharge, convenienceChargeWaiver;
	
	public FareStructure(double fixedFare, double thresholdDistance, double farePerKm, double farePerMin, double freeMinutes, double farePerWaitingMin, double freeWaitingMinutes){
		this.fixedFare = fixedFare;
		this.thresholdDistance = thresholdDistance;
		this.farePerKm = farePerKm;
		this.farePerMin = farePerMin;
		this.freeMinutes = freeMinutes;
		this.farePerWaitingMin = farePerWaitingMin;
		this.freeWaitingMinutes = freeWaitingMinutes;
		this.fareFactor = 1;
		this.luggageFare = 0;
		this.convenienceCharge = 0;
		this.convenienceChargeWaiver = 0;
	}
	
	public double calculateFare(double totalDistanceInKm, double totalTimeInMin, double totalWaitTimeInMin){
		totalTimeInMin = totalTimeInMin - freeMinutes;
		if(totalTimeInMin < 0){
			totalTimeInMin = 0;
		}
		double fareOfRideTime = totalTimeInMin * farePerMin;
		
		totalWaitTimeInMin = totalWaitTimeInMin - freeWaitingMinutes;
		if(totalWaitTimeInMin < 0){
			totalWaitTimeInMin = 0;
		}
		double fareOfWaitTime = totalWaitTimeInMin * farePerWaitingMin;
		
		double fare = fareOfRideTime + fareOfWaitTime + fixedFare + ((totalDistanceInKm <= thresholdDistance) ? (0) : ((totalDistanceInKm - thresholdDistance) * farePerKm));
		fare = fare * fareFactor;

		fare = fare + getEffectiveConvenienceCharge();

		fare = Math.round(fare);
		return fare;
	}

	public double getEffectiveConvenienceCharge(){
		return (convenienceCharge - convenienceChargeWaiver);
	}
	
	@Override
	public String toString() {
		return "fixedFare=" + fixedFare + ", thresholdDistance=" + thresholdDistance + ", farePerKm=" + farePerKm + ", farePerMin=" + farePerMin + ", freeMinutes=" + freeMinutes
				+ ", farePerWaitingMin=" + farePerWaitingMin + ", freeWaitingMinutes=" + freeWaitingMinutes + " fareFactor = " + fareFactor+", luggageFare="+luggageFare
				+", convenienceCharge="+convenienceCharge+", convenienceChargeWaiver="+convenienceChargeWaiver;
	}
}

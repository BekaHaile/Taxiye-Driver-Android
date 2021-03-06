package product.clicklabs.jugnoo.driver.datastructure;

import product.clicklabs.jugnoo.driver.utils.Utils;

public class FareStructure {
	public double fixedFare;
	public double thresholdDistance, farePerKmThresholdDistance;
	public double farePerKm, farePerKmBeforeThreshold, farePerKmAfterThreshold;
	public double farePerMin;
	public double freeMinutes;
	public double farePerWaitingMin;
	public double freeWaitingMinutes, fareMinimum;
	
	public double fareFactor;

	public double luggageFare;
	public double convenienceCharge, convenienceChargeWaiver;
	public double mandatoryFare, mandatoryFareCapping;
	public int mandatoryFareApplicable = 0;
	private double baggageCharges ;
	private double taxPercent;
	private double mandatoryDistance;
	private double mandatoryTime;

	public FareStructure(double fixedFare, double thresholdDistance, double farePerKm, double farePerMin, double freeMinutes,
						 double farePerWaitingMin, double freeWaitingMinutes, double farePerKmThresholdDistance, double farePerKmAfterThreshold,
						 double farePerKmBeforeThreshold, double fareMinimum, double mandatoryFare, double mandatoryFareCapping,
						 double baggageCharges, double taxPercent, double mandatoryDistance, double mandatoryTime){
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
		this.farePerKmThresholdDistance = farePerKmThresholdDistance;
		this.farePerKmAfterThreshold = farePerKmAfterThreshold;
		this.farePerKmBeforeThreshold =farePerKmBeforeThreshold;
		this.mandatoryFare = mandatoryFare;
		this.mandatoryFareCapping = mandatoryFareCapping;
		this.fareMinimum = fareMinimum;
		this.baggageCharges = baggageCharges;
		this.taxPercent = taxPercent;
		this.mandatoryDistance = mandatoryDistance;
		this.mandatoryTime = mandatoryTime;
	}
	
	public double calculateFare(double totalDistanceInKm, double totalTimeInMin, double totalWaitTimeInMin, int luggageCount){
		totalDistanceInKm = Utils.round(totalDistanceInKm, 2);
		totalTimeInMin = totalTimeInMin - freeMinutes;
		if(totalTimeInMin < 0){
			totalTimeInMin = 0;
		}
		double fareOfRideTime = totalTimeInMin * farePerMin;
		
		totalWaitTimeInMin = totalWaitTimeInMin - freeWaitingMinutes;
		if(totalWaitTimeInMin < 0){
			totalWaitTimeInMin = 0;
		}

		double fareOfDistance =0;
		double fare = 0;


		if (farePerKmThresholdDistance > 0) {
			if (totalDistanceInKm < thresholdDistance) {
				fareOfDistance = 0;
			} else if (totalDistanceInKm < farePerKmThresholdDistance) {
				fareOfDistance = (totalDistanceInKm - thresholdDistance) * farePerKmBeforeThreshold;
			} else {
				fareOfDistance = ((farePerKmThresholdDistance - thresholdDistance) * farePerKmBeforeThreshold)
						+ ((totalDistanceInKm - farePerKmThresholdDistance) * farePerKmAfterThreshold);
			}

			fare = fareOfRideTime + fixedFare + fareOfDistance;

		} else {
			fare = fareOfRideTime + fixedFare + ((totalDistanceInKm <= thresholdDistance) ? (0) : ((totalDistanceInKm - thresholdDistance) * farePerKmAfterThreshold));
		}

        //congestion fare
        double fareOfWaitTime = totalWaitTimeInMin * farePerWaitingMin;
        fare = fare + fareOfWaitTime;

		fare = fare * fareFactor;

		fare = fare + getEffectiveConvenienceCharge();

		if(fareMinimum > 0){
			if(fare < fareMinimum){
				fare = fareMinimum;
			}
		}

		if(mandatoryFare > 0) {
			double cappedFareUp = mandatoryFare + (mandatoryFareCapping * mandatoryFare / 100D);
			double cappedFareDown = mandatoryFare - (mandatoryFareCapping * mandatoryFare / 100D);
			if(fare <= cappedFareUp && fare >= cappedFareDown){
				fare = mandatoryFare;
				mandatoryFareApplicable = 1;
			} else {
				mandatoryFareApplicable = 0;
			}
		}

		fare = fare + computeLuggageChargesCharges(luggageCount);

		return fare;
	}

	private double getEffectiveConvenienceCharge(){
		return (convenienceCharge - convenienceChargeWaiver);
	}

	public int getMandatoryFareApplicable(){
		return  mandatoryFareApplicable;
	}

	public double computeLuggageChargesCharges(int luggageCount) {
		return Utils.currencyPrecision(((double)luggageCount) * baggageCharges);
	}

	public double getBaggageCharges() {
		return baggageCharges;
	}

	@Override
	public String toString() {
		return "fixedFare=" + fixedFare + ", thresholdDistance=" + thresholdDistance + ", farePerKm=" + farePerKm + ", farePerMin=" + farePerMin + ", freeMinutes=" + freeMinutes
				+ ", farePerWaitingMin=" + farePerWaitingMin + ", freeWaitingMinutes=" + freeWaitingMinutes + " fareFactor = " + fareFactor+", luggageFare="+luggageFare
				+", convenienceCharge="+convenienceCharge+", convenienceChargeWaiver="+convenienceChargeWaiver + "baggageCharges= " + baggageCharges;
	}

	public double getTaxPercent() {
		return taxPercent;
	}

	public double getMandatoryDistance() {
		return mandatoryDistance;
	}

	public double getMandatoryTime() {
		return mandatoryTime;
	}
}

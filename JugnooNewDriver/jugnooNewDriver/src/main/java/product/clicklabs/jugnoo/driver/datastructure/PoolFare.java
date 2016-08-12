package product.clicklabs.jugnoo.driver.datastructure;

import android.content.Context;

import product.clicklabs.jugnoo.driver.Database2;

/**
 * Created by shankar on 6/14/16.
 */
public class PoolFare {
	private double distance;
	private long rideTime;
	private double convenienceCharge, fare, discountedfare, discountPercentage, poolDropRadius;
	private int discountedFareEnabled;

	public PoolFare(double distance, long rideTime, double convenienceCharge, double fare, double discountedfare,
					int discountedFareEnabled, double discountPercentage, double poolDropRadius) {
		this.distance = distance;
		this.rideTime = rideTime;
		this.convenienceCharge = convenienceCharge;
		this.fare = fare;
		this.discountedfare = discountedfare;
		this.discountedFareEnabled = discountedFareEnabled;
		this.discountPercentage = discountPercentage;
		this.poolDropRadius =poolDropRadius;

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

	public double getFare(Context context, int engagementId) {
		try {
			if(1 == Database2.getInstance(context).getPoolDiscountFlag(engagementId) && discountedFareEnabled ==1){
				return discountedfare;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fare;
	}

	public int getDiscountedFareEnabled() {
		return discountedFareEnabled;
	}

	public void setDiscountedFareEnabled(int discountedFareEnabled) {
		this.discountedFareEnabled = discountedFareEnabled;
	}

	public void setFare(double fare) {
		this.fare = fare;
	}

	public double getDiscountedfare() {
		return discountedfare;
	}

	public void setDiscountedfare(double discountedfare) {
		this.discountedfare = discountedfare;
	}

	public double getDiscountPercentage() {
		return discountPercentage;
	}

	public void setDiscountPercentage(double discountPercentage) {
		this.discountPercentage = discountPercentage;
	}

	public double getPoolDropRadius() {
		return poolDropRadius;
	}

	public void setPoolDropRadius(double poolDropRadius) {
		this.poolDropRadius = poolDropRadius;
	}
}

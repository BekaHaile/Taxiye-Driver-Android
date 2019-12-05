package product.clicklabs.jugnoo.driver.datastructure;

import android.content.Context;

import product.clicklabs.jugnoo.driver.Database2;

/**
 * Created by shankar on 6/14/16.
 */
public class PoolFare {
	private double fare, discountedfare, discountPercentage, poolDropRadius;
	private int discountedFareEnabled;

	public PoolFare(double fare, double discountedfare,
					int discountedFareEnabled, double discountPercentage, double poolDropRadius) {
		this.fare = fare;
		this.discountedfare = discountedfare;
		this.discountedFareEnabled = discountedFareEnabled;
		this.discountPercentage = discountPercentage;
		this.poolDropRadius =poolDropRadius;

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

	public double getDiscountPercentage() {
		return discountPercentage;
	}

	public double getPoolDropRadius() {
		return poolDropRadius;
	}

}

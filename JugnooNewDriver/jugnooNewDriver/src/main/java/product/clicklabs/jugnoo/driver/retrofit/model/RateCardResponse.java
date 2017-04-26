package product.clicklabs.jugnoo.driver.retrofit.model;

/**
 * Created by aneeshbansal on 16/07/16.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class RateCardResponse {

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("message")
	@Expose
	private String message;
	@SerializedName("rates")
	@Expose
	private Rates rates;

	/**
	 *
	 * @return
	 * The flag
	 */
	public Integer getFlag() {
		return flag;
	}

	/**
	 *
	 * @param flag
	 * The flag
	 */
	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	/**
	 *
	 * @return
	 * The message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 *
	 * @param message
	 * The message
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 *
	 * @return
	 * The rates
	 */
	public Rates getRates() {
		return rates;
	}

	/**
	 *
	 * @param rates
	 * The rates
	 */
	public void setRates(Rates rates) {
		this.rates = rates;
	}

	public class Rates {

		@SerializedName("pickup_charges")
		@Expose
		private Double pickupCharges;
		@SerializedName("pickup_charges_threshold")
		@Expose
		private Double pickupChargesThreshold;
		@SerializedName("base_fare")
		@Expose
		private Double baseFare;
		@SerializedName("fare_per_km")
		@Expose
		private Double farePerKm;
		@SerializedName("fare_per_min")
		@Expose
		private Double farePerMin;
		@SerializedName("driver_to_customer_referral")
		@Expose
		private Double driverToCustomerReferral;
		@SerializedName("driver_to_driver_referral")
		@Expose
		private Double driverToDriverReferral;
		@SerializedName("fare_per_km_threshold_distance")
		@Expose
		private Double afterThresholdDistance;
		@SerializedName("fare_per_km_after_threshold")
		@Expose
		private Double getAfterThresholdValue;

		/**
		 *
		 * @return
		 * The pickupCharges
		 */
		public Double getPickupCharges() {
			return pickupCharges;
		}

		/**
		 *
		 * @param pickupCharges
		 * The pickup_charges
		 */
		public void setPickupCharges(Double pickupCharges) {
			this.pickupCharges = pickupCharges;
		}

		/**
		 *
		 * @return
		 * The pickupChargesThreshold
		 */
		public Double getPickupChargesThreshold() {
			return pickupChargesThreshold;
		}

		/**
		 *
		 * @param pickupChargesThreshold
		 * The pickup_charges_threshold
		 */
		public void setPickupChargesThreshold(Double pickupChargesThreshold) {
			this.pickupChargesThreshold = pickupChargesThreshold;
		}

		/**
		 *
		 * @return
		 * The baseFare
		 */
		public Double getBaseFare() {
			return baseFare;
		}

		/**
		 *
		 * @param baseFare
		 * The base_fare
		 */
		public void setBaseFare(Double baseFare) {
			this.baseFare = baseFare;
		}

		/**
		 *
		 * @return
		 * The farePerKm
		 */
		public Double getFarePerKm() {
			return farePerKm;
		}

		/**
		 *
		 * @param farePerKm
		 * The fare_per_km
		 */
		public void setFarePerKm(Double farePerKm) {
			this.farePerKm = farePerKm;
		}

		/**
		 *
		 * @return
		 * The farePerMin
		 */
		public Double getFarePerMin() {
			return farePerMin;
		}

		/**
		 *
		 * @param farePerMin
		 * The fare_per_min
		 */
		public void setFarePerMin(Double farePerMin) {
			this.farePerMin = farePerMin;
		}

		/**
		 *
		 * @return
		 * The driverToCustomerReferral
		 */
		public Double getDriverToCustomerReferral() {
			return driverToCustomerReferral;
		}

		/**
		 *
		 * @param driverToCustomerReferral
		 * The driver_to_customer_referral
		 */
		public void setDriverToCustomerReferral(Double driverToCustomerReferral) {
			this.driverToCustomerReferral = driverToCustomerReferral;
		}

		/**
		 *
		 * @return
		 * The driverToDriverReferral
		 */
		public Double getDriverToDriverReferral() {
			return driverToDriverReferral;
		}

		/**
		 *
		 * @param driverToDriverReferral
		 * The driver_to_driver_referral
		 */
		public void setDriverToDriverReferral(Double driverToDriverReferral) {
			this.driverToDriverReferral = driverToDriverReferral;
		}

		public Double getAfterThresholdDistance() {
			return afterThresholdDistance;
		}

		public void setAfterThresholdDistance(Double afterThresholdDistance) {
			this.afterThresholdDistance = afterThresholdDistance;
		}

		public Double getGetAfterThresholdValue() {
			return getAfterThresholdValue;
		}

		public void setGetAfterThresholdValue(Double getAfterThresholdValue) {
			this.getAfterThresholdValue = getAfterThresholdValue;
		}
	}

}
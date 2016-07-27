package product.clicklabs.jugnoo.driver.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by aneeshbansal on 16/04/16.
 */

public class EarningsDetailResponse {

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("daily")
	@Expose
	private Daily daily;
	@SerializedName("weekly")
	@Expose
	private Weekly weekly;
	@SerializedName("monthly")
	@Expose
	private Monthly monthly;

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
	 * The daily
	 */
	public Daily getDaily() {
		return daily;
	}

	/**
	 *
	 * @param daily
	 * The daily
	 */
	public void setDaily(Daily daily) {
		this.daily = daily;
	}

	/**
	 *
	 * @return
	 * The weekly
	 */
	public Weekly getWeekly() {
		return weekly;
	}

	/**
	 *
	 * @param weekly
	 * The weekly
	 */
	public void setWeekly(Weekly weekly) {
		this.weekly = weekly;
	}

	/**
	 *
	 * @return
	 * The monthly
	 */
	public Monthly getMonthly() {
		return monthly;
	}

	/**
	 *
	 * @param monthly
	 * The monthly
	 */
	public void setMonthly(Monthly monthly) {
		this.monthly = monthly;
	}

	public class Daily {

		@SerializedName("earnings")
		@Expose
		private Integer earnings;
		@SerializedName("rides")
		@Expose
		private Integer rides;
		@SerializedName("rides_amount")
		@Expose
		private String ridesAmount;
		@SerializedName("referrals")
		@Expose
		private Integer referrals;
		@SerializedName("referral_amount")
		@Expose
		private Integer referralAmount;
		@SerializedName("delivery_count")
		@Expose
		private Integer deliveryCount;
		@SerializedName("delivery_charges")
		@Expose
		private Integer deliveryCharges;
		@SerializedName("date")
		@Expose
		private String date;

		/**
		 *
		 * @return
		 * The earnings
		 */
		public Integer getEarnings() {
			return earnings;
		}

		/**
		 *
		 * @param earnings
		 * The earnings
		 */
		public void setEarnings(Integer earnings) {
			this.earnings = earnings;
		}

		/**
		 *
		 * @return
		 * The rides
		 */
		public Integer getRides() {
			return rides;
		}

		/**
		 *
		 * @param rides
		 * The rides
		 */
		public void setRides(Integer rides) {
			this.rides = rides;
		}

		/**
		 *
		 * @return
		 * The ridesAmount
		 */
		public String getRidesAmount() {
			return ridesAmount;
		}

		/**
		 *
		 * @param ridesAmount
		 * The rides_amount
		 */
		public void setRidesAmount(String ridesAmount) {
			this.ridesAmount = ridesAmount;
		}

		/**
		 *
		 * @return
		 * The referrals
		 */
		public Integer getReferrals() {
			return referrals;
		}

		/**
		 *
		 * @param referrals
		 * The referrals
		 */
		public void setReferrals(Integer referrals) {
			this.referrals = referrals;
		}

		/**
		 *
		 * @return
		 * The referralAmount
		 */
		public Integer getReferralAmount() {
			return referralAmount;
		}

		/**
		 *
		 * @param referralAmount
		 * The referral_amount
		 */
		public void setReferralAmount(Integer referralAmount) {
			this.referralAmount = referralAmount;
		}

		/**
		 *
		 * @return
		 * The deliveryCount
		 */
		public Integer getDeliveryCount() {
			return deliveryCount;
		}

		/**
		 *
		 * @param deliveryCount
		 * The delivery_count
		 */
		public void setDeliveryCount(Integer deliveryCount) {
			this.deliveryCount = deliveryCount;
		}

		/**
		 *
		 * @return
		 * The deliveryCharges
		 */
		public Integer getDeliveryCharges() {
			return deliveryCharges;
		}

		/**
		 *
		 * @param deliveryCharges
		 * The delivery_charges
		 */
		public void setDeliveryCharges(Integer deliveryCharges) {
			this.deliveryCharges = deliveryCharges;
		}

		/**
		 *
		 * @return
		 * The date
		 */
		public String getDate() {
			return date;
		}

		/**
		 *
		 * @param date
		 * The date
		 */
		public void setDate(String date) {
			this.date = date;
		}

	}

	public class Monthly {

		@SerializedName("earnings")
		@Expose
		private Integer earnings;
		@SerializedName("rides")
		@Expose
		private Integer rides;
		@SerializedName("rides_amount")
		@Expose
		private String ridesAmount;
		@SerializedName("referrals")
		@Expose
		private Integer referrals;
		@SerializedName("referral_amount")
		@Expose
		private Integer referralAmount;
		@SerializedName("delivery_count")
		@Expose
		private Integer deliveryCount;
		@SerializedName("delivery_charges")
		@Expose
		private Integer deliveryCharges;
		@SerializedName("start_date")
		@Expose
		private String startDate;
		@SerializedName("end_date")
		@Expose
		private String endDate;

		/**
		 *
		 * @return
		 * The earnings
		 */
		public Integer getEarnings() {
			return earnings;
		}

		/**
		 *
		 * @param earnings
		 * The earnings
		 */
		public void setEarnings(Integer earnings) {
			this.earnings = earnings;
		}

		/**
		 *
		 * @return
		 * The rides
		 */
		public Integer getRides() {
			return rides;
		}

		/**
		 *
		 * @param rides
		 * The rides
		 */
		public void setRides(Integer rides) {
			this.rides = rides;
		}

		/**
		 *
		 * @return
		 * The ridesAmount
		 */
		public String getRidesAmount() {
			return ridesAmount;
		}

		/**
		 *
		 * @param ridesAmount
		 * The rides_amount
		 */
		public void setRidesAmount(String ridesAmount) {
			this.ridesAmount = ridesAmount;
		}

		/**
		 *
		 * @return
		 * The referrals
		 */
		public Integer getReferrals() {
			return referrals;
		}

		/**
		 *
		 * @param referrals
		 * The referrals
		 */
		public void setReferrals(Integer referrals) {
			this.referrals = referrals;
		}

		/**
		 *
		 * @return
		 * The referralAmount
		 */
		public Integer getReferralAmount() {
			return referralAmount;
		}

		/**
		 *
		 * @param referralAmount
		 * The referral_amount
		 */
		public void setReferralAmount(Integer referralAmount) {
			this.referralAmount = referralAmount;
		}

		/**
		 *
		 * @return
		 * The deliveryCount
		 */
		public Integer getDeliveryCount() {
			return deliveryCount;
		}

		/**
		 *
		 * @param deliveryCount
		 * The delivery_count
		 */
		public void setDeliveryCount(Integer deliveryCount) {
			this.deliveryCount = deliveryCount;
		}

		/**
		 *
		 * @return
		 * The deliveryCharges
		 */
		public Integer getDeliveryCharges() {
			return deliveryCharges;
		}

		/**
		 *
		 * @param deliveryCharges
		 * The delivery_charges
		 */
		public void setDeliveryCharges(Integer deliveryCharges) {
			this.deliveryCharges = deliveryCharges;
		}

		/**
		 *
		 * @return
		 * The startDate
		 */
		public String getStartDate() {
			return startDate;
		}

		/**
		 *
		 * @param startDate
		 * The start_date
		 */
		public void setStartDate(String startDate) {
			this.startDate = startDate;
		}

		/**
		 *
		 * @return
		 * The endDate
		 */
		public String getEndDate() {
			return endDate;
		}

		/**
		 *
		 * @param endDate
		 * The end_date
		 */
		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}

	}

	public class Weekly {

		@SerializedName("earnings")
		@Expose
		private Integer earnings;
		@SerializedName("rides")
		@Expose
		private Integer rides;
		@SerializedName("rides_amount")
		@Expose
		private Integer ridesAmount;
		@SerializedName("referrals")
		@Expose
		private Integer referrals;
		@SerializedName("referral_amount")
		@Expose
		private Integer referralAmount;
		@SerializedName("delivery_count")
		@Expose
		private Integer deliveryCount;
		@SerializedName("delivery_charges")
		@Expose
		private Integer deliveryCharges;
		@SerializedName("start_date")
		@Expose
		private String startDate;
		@SerializedName("end_date")
		@Expose
		private String endDate;

		/**
		 *
		 * @return
		 * The earnings
		 */
		public Integer getEarnings() {
			return earnings;
		}

		/**
		 *
		 * @param earnings
		 * The earnings
		 */
		public void setEarnings(Integer earnings) {
			this.earnings = earnings;
		}

		/**
		 *
		 * @return
		 * The rides
		 */
		public Integer getRides() {
			return rides;
		}

		/**
		 *
		 * @param rides
		 * The rides
		 */
		public void setRides(Integer rides) {
			this.rides = rides;
		}

		/**
		 *
		 * @return
		 * The ridesAmount
		 */
		public Integer getRidesAmount() {
			return ridesAmount;
		}

		/**
		 *
		 * @param ridesAmount
		 * The rides_amount
		 */
		public void setRidesAmount(Integer ridesAmount) {
			this.ridesAmount = ridesAmount;
		}

		/**
		 *
		 * @return
		 * The referrals
		 */
		public Integer getReferrals() {
			return referrals;
		}

		/**
		 *
		 * @param referrals
		 * The referrals
		 */
		public void setReferrals(Integer referrals) {
			this.referrals = referrals;
		}

		/**
		 *
		 * @return
		 * The referralAmount
		 */
		public Integer getReferralAmount() {
			return referralAmount;
		}

		/**
		 *
		 * @param referralAmount
		 * The referral_amount
		 */
		public void setReferralAmount(Integer referralAmount) {
			this.referralAmount = referralAmount;
		}

		/**
		 *
		 * @return
		 * The deliveryCount
		 */
		public Integer getDeliveryCount() {
			return deliveryCount;
		}

		/**
		 *
		 * @param deliveryCount
		 * The delivery_count
		 */
		public void setDeliveryCount(Integer deliveryCount) {
			this.deliveryCount = deliveryCount;
		}

		/**
		 *
		 * @return
		 * The deliveryCharges
		 */
		public Integer getDeliveryCharges() {
			return deliveryCharges;
		}

		/**
		 *
		 * @param deliveryCharges
		 * The delivery_charges
		 */
		public void setDeliveryCharges(Integer deliveryCharges) {
			this.deliveryCharges = deliveryCharges;
		}

		/**
		 *
		 * @return
		 * The startDate
		 */
		public String getStartDate() {
			return startDate;
		}

		/**
		 *
		 * @param startDate
		 * The start_date
		 */
		public void setStartDate(String startDate) {
			this.startDate = startDate;
		}

		/**
		 *
		 * @return
		 * The endDate
		 */
		public String getEndDate() {
			return endDate;
		}

		/**
		 *
		 * @param endDate
		 * The end_date
		 */
		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}

	}
}




package product.clicklabs.jugnoo.driver.retrofit.model;

/**
 * Created by aneeshbansal on 08/09/15.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class BookingHistoryResponse {

	@SerializedName("booking_data")
	@Expose
	private List<BookingData> bookingData = new ArrayList<BookingData>();

	/**
	 * @return The bookingData
	 */
	public List<BookingData> getBookingData() {
		return bookingData;
	}

	/**
	 * @param bookingData The booking_data
	 */
	public void setBookingData(List<BookingData> bookingData) {
		this.bookingData = bookingData;
	}

	public class BookingData {

		@Expose
		private String id;
		@SerializedName("engagement_id")
		@Expose
		private Integer engagementId;
		@SerializedName("business_id")
		@Expose
		private Integer businessId;
		@Expose
		private String from;
		@Expose
		private String to;
		@Expose
		private String fare;
		@Expose
		private String distance;
		@SerializedName("ride_time")
		@Expose
		private String rideTime;
		@SerializedName("wait_time")
		@Expose
		private String waitTime;
		@SerializedName("customer_paid")
		@Expose
		private String customerPaid;
		@Expose
		private String time;
		@Expose
		private String subsidy;
		@Expose
		private String balance;
		@SerializedName("coupon_used")
		@Expose
		private Integer couponUsed;
		@SerializedName("payment_mode")
		@Expose
		private Integer paymentMode;
		@SerializedName("driver_payment_status")
		@Expose
		private Integer driverPaymentStatus;
		@SerializedName("status_string")
		@Expose
		private String statusString;
		@SerializedName("paid_to_merchant")
		@Expose
		private String paidToMerchant;
		@SerializedName("paid_by_customer")
		@Expose
		private String paidByCustomer;

		/**
		 * @return The id
		 */
		public String getId() {
			return id;
		}

		/**
		 * @param id The id
		 */
		public void setId(String id) {
			this.id = id;
		}

		/**
		 * @return The engagementId
		 */
		public Integer getEngagementId() {
			return engagementId;
		}

		/**
		 * @param engagementId The engagement_id
		 */
		public void setEngagementId(Integer engagementId) {
			this.engagementId = engagementId;
		}

		/**
		 * @return The businessId
		 */
		public Integer getBusinessId() {
			return businessId;
		}

		/**
		 * @param businessId The business_id
		 */
		public void setBusinessId(Integer businessId) {
			this.businessId = businessId;
		}

		/**
		 * @return The from
		 */
		public String getFrom() {
			return from;
		}

		/**
		 * @param from The from
		 */
		public void setFrom(String from) {
			this.from = from;
		}

		/**
		 * @return The to
		 */
		public String getTo() {
			return to;
		}

		/**
		 * @param to The to
		 */
		public void setTo(String to) {
			this.to = to;
		}

		/**
		 * @return The fare
		 */
		public String getFare() {
			return fare;
		}

		/**
		 * @param fare The fare
		 */
		public void setFare(String fare) {
			this.fare = fare;
		}

		/**
		 * @return The distance
		 */
		public String getDistance() {
			return distance;
		}

		/**
		 * @param distance The distance
		 */
		public void setDistance(String distance) {
			this.distance = distance;
		}

		/**
		 * @return The rideTime
		 */
		public String getRideTime() {
			return rideTime;
		}

		/**
		 * @param rideTime The ride_time
		 */
		public void setRideTime(String rideTime) {
			this.rideTime = rideTime;
		}

		/**
		 * @return The waitTime
		 */
		public String getWaitTime() {
			return waitTime;
		}

		/**
		 * @param waitTime The wait_time
		 */
		public void setWaitTime(String waitTime) {
			this.waitTime = waitTime;
		}

		/**
		 * @return The customerPaid
		 */
		public String getCustomerPaid() {
			return customerPaid;
		}

		/**
		 * @param customerPaid The customer_paid
		 */
		public void setCustomerPaid(String customerPaid) {
			this.customerPaid = customerPaid;
		}

		/**
		 * @return The time
		 */
		public String getTime() {
			return time;
		}

		/**
		 * @param time The time
		 */
		public void setTime(String time) {
			this.time = time;
		}

		/**
		 * @return The subsidy
		 */
		public String getSubsidy() {
			return subsidy;
		}

		/**
		 * @param subsidy The subsidy
		 */
		public void setSubsidy(String subsidy) {
			this.subsidy = subsidy;
		}

		/**
		 * @return The balance
		 */
		public String getBalance() {
			return balance;
		}

		/**
		 * @param balance The balance
		 */
		public void setBalance(String balance) {
			this.balance = balance;
		}

		/**
		 * @return The couponUsed
		 */
		public Integer getCouponUsed() {
			return couponUsed;
		}

		/**
		 * @param couponUsed The coupon_used
		 */
		public void setCouponUsed(Integer couponUsed) {
			this.couponUsed = couponUsed;
		}

		/**
		 * @return The paymentMode
		 */
		public Integer getPaymentMode() {
			return paymentMode;
		}

		/**
		 * @param paymentMode The payment_mode
		 */
		public void setPaymentMode(Integer paymentMode) {
			this.paymentMode = paymentMode;
		}

		/**
		 * @return The driverPaymentStatus
		 */
		public Integer getDriverPaymentStatus() {
			return driverPaymentStatus;
		}

		/**
		 * @param driverPaymentStatus The driver_payment_status
		 */
		public void setDriverPaymentStatus(Integer driverPaymentStatus) {
			this.driverPaymentStatus = driverPaymentStatus;
		}

		/**
		 * @return The statusString
		 */
		public String getStatusString() {
			return statusString;
		}

		/**
		 * @param statusString The status_string
		 */
		public void setStatusString(String statusString) {
			this.statusString = statusString;
		}

		public String getPaidToMerchant() {
			return paidToMerchant;
		}

		public void setPaidToMerchant(String paidToMerchant) {
			this.paidToMerchant = paidToMerchant;
		}

		public String getPaidByCustomer() {
			return paidByCustomer;
		}

		public void setPaidByCustomer(String paidByCustomer) {
			this.paidByCustomer = paidByCustomer;
		}
	}
}


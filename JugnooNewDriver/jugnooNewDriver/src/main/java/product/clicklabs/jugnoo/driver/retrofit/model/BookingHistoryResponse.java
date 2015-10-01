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
	 *
	 * @return
	 * The bookingData
	 */
	public List<BookingData> getBookingData() {
		return bookingData;
	}

	/**
	 *
	 * @param bookingData
	 * The booking_data
	 */
	public void setBookingData(List<BookingData> bookingData) {
		this.bookingData = bookingData;
	}

	public class BookingData {

		@SerializedName("id")
		@Expose
		private String id;
		@SerializedName("engagement_id")
		@Expose
		private Integer engagementId;
		@SerializedName("business_id")
		@Expose
		private Integer businessId;
		@SerializedName("from")
		@Expose
		private String from;
		@SerializedName("to")
		@Expose
		private String to;
		@SerializedName("fare")
		@Expose
		private String fare;
		@SerializedName("distance")
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
		@SerializedName("time")
		@Expose
		private String time;
		@SerializedName("subsidy")
		@Expose
		private String subsidy;
		@SerializedName("balance")
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
		@SerializedName("luggage_charges")
		@Expose
		private String luggageCharges;
		@SerializedName("convenience_charges")
		@Expose
		private String convenienceCharges;
		@SerializedName("fare_factor_applied")
		@Expose
		private String fareFactorApplied;
		@SerializedName("fare_factor_value")
		@Expose
		private String fareFactorValue;
		@SerializedName("accept_subsidy")
		@Expose
		private String acceptSubsidy;
		@SerializedName("cancel_subsidy")
		@Expose
		private String cancelSubsidy;
		@SerializedName("account_balance")
		@Expose
		private String accountBalance;
		@SerializedName("actual_fare")
		@Expose
		private String actualFare;
		@SerializedName("paid_to_merchant")
		@Expose
		private String paidToMerchant;
		@SerializedName("paid_by_customer")
		@Expose
		private String paidByCustomer;

		/**
		 *
		 * @return
		 * The id
		 */
		public String getId() {
			return id;
		}

		/**
		 *
		 * @param id
		 * The id
		 */
		public void setId(String id) {
			this.id = id;
		}

		/**
		 *
		 * @return
		 * The engagementId
		 */
		public Integer getEngagementId() {
			return engagementId;
		}

		/**
		 *
		 * @param engagementId
		 * The engagement_id
		 */
		public void setEngagementId(Integer engagementId) {
			this.engagementId = engagementId;
		}

		/**
		 *
		 * @return
		 * The businessId
		 */
		public Integer getBusinessId() {
			return businessId;
		}

		/**
		 *
		 * @param businessId
		 * The business_id
		 */
		public void setBusinessId(Integer businessId) {
			this.businessId = businessId;
		}

		/**
		 *
		 * @return
		 * The from
		 */
		public String getFrom() {
			return from;
		}

		/**
		 *
		 * @param from
		 * The from
		 */
		public void setFrom(String from) {
			this.from = from;
		}

		/**
		 *
		 * @return
		 * The to
		 */
		public String getTo() {
			return to;
		}

		/**
		 *
		 * @param to
		 * The to
		 */
		public void setTo(String to) {
			this.to = to;
		}

		/**
		 *
		 * @return
		 * The fare
		 */
		public String getFare() {
			return fare;
		}

		/**
		 *
		 * @param fare
		 * The fare
		 */
		public void setFare(String fare) {
			this.fare = fare;
		}

		/**
		 *
		 * @return
		 * The distance
		 */
		public String getDistance() {
			return distance;
		}

		/**
		 *
		 * @param distance
		 * The distance
		 */
		public void setDistance(String distance) {
			this.distance = distance;
		}

		/**
		 *
		 * @return
		 * The rideTime
		 */
		public String getRideTime() {
			return rideTime;
		}

		/**
		 *
		 * @param rideTime
		 * The ride_time
		 */
		public void setRideTime(String rideTime) {
			this.rideTime = rideTime;
		}

		/**
		 *
		 * @return
		 * The waitTime
		 */
		public String getWaitTime() {
			return waitTime;
		}

		/**
		 *
		 * @param waitTime
		 * The wait_time
		 */
		public void setWaitTime(String waitTime) {
			this.waitTime = waitTime;
		}

		/**
		 *
		 * @return
		 * The customerPaid
		 */
		public String getCustomerPaid() {
			return customerPaid;
		}

		/**
		 *
		 * @param customerPaid
		 * The customer_paid
		 */
		public void setCustomerPaid(String customerPaid) {
			this.customerPaid = customerPaid;
		}

		/**
		 *
		 * @return
		 * The time
		 */
		public String getTime() {
			return time;
		}

		/**
		 *
		 * @param time
		 * The time
		 */
		public void setTime(String time) {
			this.time = time;
		}

		/**
		 *
		 * @return
		 * The subsidy
		 */
		public String getSubsidy() {
			return subsidy;
		}

		/**
		 *
		 * @param subsidy
		 * The subsidy
		 */
		public void setSubsidy(String subsidy) {
			this.subsidy = subsidy;
		}

		/**
		 *
		 * @return
		 * The balance
		 */
		public String getBalance() {
			return balance;
		}

		/**
		 *
		 * @param balance
		 * The balance
		 */
		public void setBalance(String balance) {
			this.balance = balance;
		}

		/**
		 *
		 * @return
		 * The couponUsed
		 */
		public Integer getCouponUsed() {
			return couponUsed;
		}

		/**
		 *
		 * @param couponUsed
		 * The coupon_used
		 */
		public void setCouponUsed(Integer couponUsed) {
			this.couponUsed = couponUsed;
		}

		/**
		 *
		 * @return
		 * The paymentMode
		 */
		public Integer getPaymentMode() {
			return paymentMode;
		}

		/**
		 *
		 * @param paymentMode
		 * The payment_mode
		 */
		public void setPaymentMode(Integer paymentMode) {
			this.paymentMode = paymentMode;
		}

		/**
		 *
		 * @return
		 * The driverPaymentStatus
		 */
		public Integer getDriverPaymentStatus() {
			return driverPaymentStatus;
		}

		/**
		 *
		 * @param driverPaymentStatus
		 * The driver_payment_status
		 */
		public void setDriverPaymentStatus(Integer driverPaymentStatus) {
			this.driverPaymentStatus = driverPaymentStatus;
		}

		/**
		 *
		 * @return
		 * The statusString
		 */
		public String getStatusString() {
			return statusString;
		}

		/**
		 *
		 * @param statusString
		 * The status_string
		 */
		public void setStatusString(String statusString) {
			this.statusString = statusString;
		}

		/**
		 *
		 * @return
		 * The luggageCharges
		 */
		public String getLuggageCharges() {
			return luggageCharges;
		}

		/**
		 *
		 * @param luggageCharges
		 * The luggage_charges
		 */
		public void setLuggageCharges(String luggageCharges) {
			this.luggageCharges = luggageCharges;
		}

		/**
		 *
		 * @return
		 * The convenienceCharges
		 */
		public String getConvenienceCharges() {
			return convenienceCharges;
		}

		/**
		 *
		 * @param convenienceCharges
		 * The convenience_charges
		 */
		public void setConvenienceCharges(String convenienceCharges) {
			this.convenienceCharges = convenienceCharges;
		}

		/**
		 *
		 * @return
		 * The fareFactorApplied
		 */
		public String getFareFactorApplied() {
			return fareFactorApplied;
		}

		/**
		 *
		 * @param fareFactorApplied
		 * The fare_factor_applied
		 */
		public void setFareFactorApplied(String fareFactorApplied) {
			this.fareFactorApplied = fareFactorApplied;
		}

		/**
		 *
		 * @return
		 * The fareFactorValue
		 */
		public String getFareFactorValue() {
			return fareFactorValue;
		}

		/**
		 *
		 * @param fareFactorValue
		 * The fare_factor_value
		 */
		public void setFareFactorValue(String fareFactorValue) {
			this.fareFactorValue = fareFactorValue;
		}

		/**
		 *
		 * @return
		 * The acceptSubsidy
		 */
		public String getAcceptSubsidy() {
			return acceptSubsidy;
		}

		/**
		 *
		 * @param acceptSubsidy
		 * The accept_subsidy
		 */
		public void setAcceptSubsidy(String acceptSubsidy) {
			this.acceptSubsidy = acceptSubsidy;
		}

		/**
		 *
		 * @return
		 * The cancelSubsidy
		 */
		public String getCancelSubsidy() {
			return cancelSubsidy;
		}

		/**
		 *
		 * @param cancelSubsidy
		 * The cancel_subsidy
		 */
		public void setCancelSubsidy(String cancelSubsidy) {
			this.cancelSubsidy = cancelSubsidy;
		}

		/**
		 *
		 * @return
		 * The accountBalance
		 */
		public String getAccountBalance() {
			return accountBalance;
		}

		/**
		 *
		 * @param accountBalance
		 * The account_balance
		 */
		public void setAccountBalance(String accountBalance) {
			this.accountBalance = accountBalance;
		}

		/**
		 *
		 * @return
		 * The actualFare
		 */
		public String getActualFare() {
			return actualFare;
		}

		/**
		 *
		 * @param actualFare
		 * The actual_fare
		 */
		public void setActualFare(String actualFare) {
			this.actualFare = actualFare;
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
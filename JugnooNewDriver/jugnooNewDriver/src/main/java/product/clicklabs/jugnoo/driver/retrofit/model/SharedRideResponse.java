package product.clicklabs.jugnoo.driver.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aneeshbansal on 06/10/15.
 */



public class SharedRideResponse {

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

		@SerializedName("sharing_engagement_id")
		@Expose
		private String sharingEngagementId;
		@SerializedName("user_id")
		@Expose
		private Integer userId;
		@SerializedName("transaction_time")
		@Expose
		private String transactionTime;
		@SerializedName("actual_fare")
		@Expose
		private Double actualFare;
		@SerializedName("paid_in_cash")
		@Expose
		private Double paidInCash;
		@SerializedName("account_balance")
		@Expose
		private Double accountBalance;
		@SerializedName("phone_no")
		@Expose
		private String phoneNo;

		/**
		 *
		 * @return
		 * The sharingEngagementId
		 */
		public String getSharingEngagementId() {
			return sharingEngagementId;
		}

		/**
		 *
		 * @param sharingEngagementId
		 * The sharing_engagement_id
		 */
		public void setSharingEngagementId(String sharingEngagementId) {
			this.sharingEngagementId = sharingEngagementId;
		}

		/**
		 *
		 * @return
		 * The userId
		 */
		public Integer getUserId() {
			return userId;
		}

		/**
		 *
		 * @param userId
		 * The user_id
		 */
		public void setUserId(Integer userId) {
			this.userId = userId;
		}

		/**
		 *
		 * @return
		 * The transactionTime
		 */
		public String getTransactionTime() {
			return transactionTime;
		}

		/**
		 *
		 * @param transactionTime
		 * The transaction_time
		 */
		public void setTransactionTime(String transactionTime) {
			this.transactionTime = transactionTime;
		}

		/**
		 *
		 * @return
		 * The actualFare
		 */
		public Double getActualFare() {
			return actualFare;
		}

		/**
		 *
		 * @param actualFare
		 * The actual_fare
		 */
		public void setActualFare(Double actualFare) {
			this.actualFare = actualFare;
		}

		/**
		 *
		 * @return
		 * The paidInCash
		 */
		public Double getPaidInCash() {
			return paidInCash;
		}

		/**
		 *
		 * @param paidInCash
		 * The paid_in_cash
		 */
		public void setPaidInCash(Double paidInCash) {
			this.paidInCash = paidInCash;
		}

		/**
		 *
		 * @return
		 * The accountBalance
		 */
		public Double getAccountBalance() {
			return accountBalance;
		}

		/**
		 *
		 * @param accountBalance
		 * The account_balance
		 */
		public void setAccountBalance(Double accountBalance) {
			this.accountBalance = accountBalance;
		}

		/**
		 *
		 * @return
		 * The phoneNo
		 */
		public String getPhoneNo() {
			return phoneNo;
		}

		/**
		 *
		 * @param phoneNo
		 * The phone_no
		 */
		public void setPhoneNo(String phoneNo) {
			this.phoneNo = phoneNo;
		}

	}

}
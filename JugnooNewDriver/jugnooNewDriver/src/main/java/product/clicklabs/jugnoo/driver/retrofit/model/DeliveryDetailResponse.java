package product.clicklabs.jugnoo.driver.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import product.clicklabs.jugnoo.driver.retrofit.CurrencyModel;

/**
 * Created by aneeshbansal on 14/06/16.
 */



public class DeliveryDetailResponse {

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("details")
	@Expose
	private Details details;

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
	 * The details
	 */
	public Details getDetails() {
		return details;
	}

	/**
	 *
	 * @param details
	 * The details
	 */
	public void setDetails(Details details) {
		this.details = details;
	}

	public class Details extends CurrencyModel {

		@SerializedName("time")
		@Expose
		private String time;
		@SerializedName("ride_id")
		@Expose
		private Integer rideId;
		@SerializedName("total_time")
		@Expose
		private Integer totalTime;
		@SerializedName("ride_distance")
		@Expose
		private String rideDistance;
		@SerializedName("return_distance")
		@Expose
		private String returnDistance;
		@SerializedName("no_of_deliveries")
		@Expose
		private Integer noOfDeliveries;
		@SerializedName("ride_fare")
		@Expose
		private String rideFare;
		@SerializedName("delivery_fare")
		@Expose
		private String deliveryFare;
		@SerializedName("return_subsidy")
		@Expose
		private String returnSubsidy;
		@SerializedName("jugnoo_cut")
		@Expose
		private String jugnooCut;
		@SerializedName("paid_in_cash")
		@Expose
		private String paidInCash;
		@SerializedName("total_fare")
		@Expose
		private String totalFare;
		@SerializedName("account_balance")
		@Expose
		private String accountBalance;
		@SerializedName("from")
		@Expose
		private String from;
		@SerializedName("to")
		@Expose
		private List<To> to = new ArrayList<To>();


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
		 * The rideId
		 */
		public Integer getRideId() {
			return rideId;
		}

		/**
		 *
		 * @param rideId
		 * The ride_id
		 */
		public void setRideId(Integer rideId) {
			this.rideId = rideId;
		}

		/**
		 *
		 * @return
		 * The totalTime
		 */
		public Integer getTotalTime() {
			return totalTime;
		}

		/**
		 *
		 * @param totalTime
		 * The total_time
		 */
		public void setTotalTime(Integer totalTime) {
			this.totalTime = totalTime;
		}

		/**
		 *
		 * @return
		 * The rideDistance
		 */
		public String getRideDistance() {
			return rideDistance;
		}

		/**
		 *
		 * @param rideDistance
		 * The ride_distance
		 */
		public void setRideDistance(String rideDistance) {
			this.rideDistance = rideDistance;
		}

		/**
		 *
		 * @return
		 * The returnDistance
		 */
		public String getReturnDistance() {
			return returnDistance;
		}

		/**
		 *
		 * @param returnDistance
		 * The return_distance
		 */
		public void setReturnDistance(String returnDistance) {
			this.returnDistance = returnDistance;
		}


		public Integer getNoOfDeliveries() {
			return noOfDeliveries;
		}

		public void setNoOfDeliveries(Integer noOfDeliveries) {
			this.noOfDeliveries = noOfDeliveries;
		}

		/**
		 *
		 * @return
		 * The rideFare
		 */
		public String getRideFare() {
			return rideFare;
		}

		/**
		 *
		 * @param rideFare
		 * The ride_fare
		 */
		public void setRideFare(String rideFare) {
			this.rideFare = rideFare;
		}

		/**
		 *
		 * @return
		 * The deliveryFare
		 */
		public String getDeliveryFare() {
			return deliveryFare;
		}

		/**
		 *
		 * @param deliveryFare
		 * The delivery_fare
		 */
		public void setDeliveryFare(String deliveryFare) {
			this.deliveryFare = deliveryFare;
		}

		/**
		 *
		 * @return
		 * The returnSubsidy
		 */
		public String getReturnSubsidy() {
			return returnSubsidy;
		}

		/**
		 *
		 * @param returnSubsidy
		 * The return_subsidy
		 */
		public void setReturnSubsidy(String returnSubsidy) {
			this.returnSubsidy = returnSubsidy;
		}

		/**
		 *
		 * @return
		 * The jugnooCut
		 */
		public String getJugnooCut() {
			return jugnooCut;
		}

		/**
		 *
		 * @param jugnooCut
		 * The jugnoo_cut
		 */
		public void setJugnooCut(String jugnooCut) {
			this.jugnooCut = jugnooCut;
		}

		/**
		 *
		 * @return
		 * The paidInCash
		 */
		public String getPaidInCash() {
			return paidInCash;
		}

		/**
		 *
		 * @param paidInCash
		 * The paid_in_cash
		 */
		public void setPaidInCash(String paidInCash) {
			this.paidInCash = paidInCash;
		}

		/**
		 *
		 * @return
		 * The totalFare
		 */
		public String getTotalFare() {
			return totalFare;
		}

		/**
		 *
		 * @param totalFare
		 * The total_fare
		 */
		public void setTotalFare(String totalFare) {
			this.totalFare = totalFare;
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
		public List<To> getTo() {
			return to;
		}

		/**
		 *
		 * @param to
		 * The to
		 */
		public void setTo(List<To> to) {
			this.to = to;
		}

		public class To {

			@SerializedName("address")
			@Expose
			private String address;

			/**
			 *
			 * @return
			 * The address
			 */
			public String getAddress() {
				return address;
			}

			/**
			 *
			 * @param address
			 * The address
			 */
			public void setAddress(String address) {
				this.address = address;
			}

		}

	}



}




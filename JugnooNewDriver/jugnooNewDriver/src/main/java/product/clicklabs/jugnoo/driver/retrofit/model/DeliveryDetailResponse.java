package product.clicklabs.jugnoo.driver.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

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

	public class Details {

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
		private Integer rideDistance;
		@SerializedName("return_distance")
		@Expose
		private Integer returnDistance;
		@SerializedName("ride_fare")
		@Expose
		private Integer rideFare;
		@SerializedName("delivery_fare")
		@Expose
		private Integer deliveryFare;
		@SerializedName("return_subsidy")
		@Expose
		private Integer returnSubsidy;
		@SerializedName("jugnoo_cut")
		@Expose
		private Integer jugnooCut;
		@SerializedName("paid_in_cash")
		@Expose
		private Integer paidInCash;
		@SerializedName("total_fare")
		@Expose
		private Integer totalFare;
		@SerializedName("account_balance")
		@Expose
		private Integer accountBalance;
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
		public Integer getRideDistance() {
			return rideDistance;
		}

		/**
		 *
		 * @param rideDistance
		 * The ride_distance
		 */
		public void setRideDistance(Integer rideDistance) {
			this.rideDistance = rideDistance;
		}

		/**
		 *
		 * @return
		 * The returnDistance
		 */
		public Integer getReturnDistance() {
			return returnDistance;
		}

		/**
		 *
		 * @param returnDistance
		 * The return_distance
		 */
		public void setReturnDistance(Integer returnDistance) {
			this.returnDistance = returnDistance;
		}

		/**
		 *
		 * @return
		 * The rideFare
		 */
		public Integer getRideFare() {
			return rideFare;
		}

		/**
		 *
		 * @param rideFare
		 * The ride_fare
		 */
		public void setRideFare(Integer rideFare) {
			this.rideFare = rideFare;
		}

		/**
		 *
		 * @return
		 * The deliveryFare
		 */
		public Integer getDeliveryFare() {
			return deliveryFare;
		}

		/**
		 *
		 * @param deliveryFare
		 * The delivery_fare
		 */
		public void setDeliveryFare(Integer deliveryFare) {
			this.deliveryFare = deliveryFare;
		}

		/**
		 *
		 * @return
		 * The returnSubsidy
		 */
		public Integer getReturnSubsidy() {
			return returnSubsidy;
		}

		/**
		 *
		 * @param returnSubsidy
		 * The return_subsidy
		 */
		public void setReturnSubsidy(Integer returnSubsidy) {
			this.returnSubsidy = returnSubsidy;
		}

		/**
		 *
		 * @return
		 * The jugnooCut
		 */
		public Integer getJugnooCut() {
			return jugnooCut;
		}

		/**
		 *
		 * @param jugnooCut
		 * The jugnoo_cut
		 */
		public void setJugnooCut(Integer jugnooCut) {
			this.jugnooCut = jugnooCut;
		}

		/**
		 *
		 * @return
		 * The paidInCash
		 */
		public Integer getPaidInCash() {
			return paidInCash;
		}

		/**
		 *
		 * @param paidInCash
		 * The paid_in_cash
		 */
		public void setPaidInCash(Integer paidInCash) {
			this.paidInCash = paidInCash;
		}

		/**
		 *
		 * @return
		 * The totalFare
		 */
		public Integer getTotalFare() {
			return totalFare;
		}

		/**
		 *
		 * @param totalFare
		 * The total_fare
		 */
		public void setTotalFare(Integer totalFare) {
			this.totalFare = totalFare;
		}

		/**
		 *
		 * @return
		 * The accountBalance
		 */
		public Integer getAccountBalance() {
			return accountBalance;
		}

		/**
		 *
		 * @param accountBalance
		 * The account_balance
		 */
		public void setAccountBalance(Integer accountBalance) {
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




package product.clicklabs.jugnoo.driver.retrofit.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class DeliveryRateCardResponse {

	@SerializedName("flag")
	@Expose
	private Integer flag;
	@SerializedName("message")
	@Expose
	private String message;
	@SerializedName("data")
	@Expose
	private Data data;

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


	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public class Data {

		@SerializedName("base_fare")
		@Expose
		private List<Fare> baseFare = null;
		@SerializedName("distance_fare")
		@Expose
		private List<Fare> distanceFare = null;
		@SerializedName("delivery_fare")
		@Expose
		private List<Fare> deliveryFare = null;
		@SerializedName("return_fare")
		@Expose
		private List<Fare> returnFare = null;
		@SerializedName("pickup_wait_time_fare")
		@Expose
		private List<Fare> pickupWaitTimeFare = null;
		@SerializedName("pickup_distance_fare")
		@Expose
		private List<Fare> pickupDistanceFare = null;
		@SerializedName("loading_fare")
		@Expose
		private List<Fare> loadingFare = null;
		@SerializedName("unloading_fare")
		@Expose
		private List<Fare> unloadingFare = null;

		public List<Fare> getBaseFare() {
			return baseFare;
		}

		public void setBaseFare(List<Fare> baseFare) {
			this.baseFare = baseFare;
		}

		public List<Fare> getDistanceFare() {
			return distanceFare;
		}

		public void setDistanceFare(List<Fare> distanceFare) {
			this.distanceFare = distanceFare;
		}

		public List<Fare> getDeliveryFare() {
			return deliveryFare;
		}

		public void setDeliveryFare(List<Fare> deliveryFare) {
			this.deliveryFare = deliveryFare;
		}

		public List<Fare> getReturnFare() {
			return returnFare;
		}

		public void setReturnFare(List<Fare> returnFare) {
			this.returnFare = returnFare;
		}

		public List<Fare> getPickupWaitTimeFare() {
			return pickupWaitTimeFare;
		}

		public void setPickupWaitTimeFare(List<Fare> pickupWaitTimeFare) {
			this.pickupWaitTimeFare = pickupWaitTimeFare;
		}

		public List<Fare> getPickupDistanceFare() {
			return pickupDistanceFare;
		}

		public void setPickupDistanceFare(List<Fare> pickupDistanceFare) {
			this.pickupDistanceFare = pickupDistanceFare;
		}

		public List<Fare> getLoadingFare() {
			return loadingFare;
		}

		public void setLoadingFare(List<Fare> loadingFare) {
			this.loadingFare = loadingFare;
		}

		public List<Fare> getUnloadingFare() {
			return unloadingFare;
		}

		public void setUnloadingFare(List<Fare> unloadingFare) {
			this.unloadingFare = unloadingFare;
		}

		public class Fare {

			@SerializedName("time_interval")
			@Expose
			private String timeInterval;
			@SerializedName("slots")
			@Expose
			private List<Slot> slots = null;

			public String getTimeInterval() {
				return timeInterval;
			}

			public void setTimeInterval(String timeInterval) {
				this.timeInterval = timeInterval;
			}

			public List<Slot> getSlots() {
				return slots;
			}

			public void setSlots(List<Slot> slots) {
				this.slots = slots;
			}

		}

		public class Slot {

			@SerializedName("range")
			@Expose
			private String range;
			@SerializedName("value")
			@Expose
			private Double value;

			public String getRange() {
				return range;
			}

			public void setRange(String range) {
				this.range = range;
			}

			public Double getValue() {
				return value;
			}

			public void setValue(Double value) {
				this.value = value;
			}

		}

	}

}

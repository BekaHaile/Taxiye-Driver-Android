package product.clicklabs.jugnoo.driver.dodo.datastructure;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by aneeshbansal on 03/03/17.
 */

public class DeliveryInfoInRideDetails {

	@SerializedName("pickup_data")
	@Expose
	private PickupData pickupData = new PickupData();
	@SerializedName("delivery_data")
	@Expose
	private List<DeliveryDatum> deliveryData = null;

	public PickupData getPickupData() {
		return pickupData;
	}

	public void setPickupData(PickupData pickupData) {
		this.pickupData = pickupData;
	}

	public List<DeliveryDatum> getDeliveryData() {
		return deliveryData;
	}

	public void setDeliveryData(List<DeliveryDatum> deliveryData) {
		this.deliveryData = deliveryData;
	}

	public class PickupData {

		@SerializedName("name")
		@Expose
		private String name;
		@SerializedName("cash_to_collect")
		@Expose
		private Double cashToCollect;
		@SerializedName("phone")
		@Expose
		private String phone;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Double getCashToCollect() {
			return cashToCollect;
		}

		public void setCashToCollect(Double cashToCollect) {
			this.cashToCollect = cashToCollect;
		}

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}
	}

	public static class DeliveryDatum {

		@SerializedName("name")
		@Expose
		private String name;
		@SerializedName("address")
		@Expose
		private String address;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

	}

}

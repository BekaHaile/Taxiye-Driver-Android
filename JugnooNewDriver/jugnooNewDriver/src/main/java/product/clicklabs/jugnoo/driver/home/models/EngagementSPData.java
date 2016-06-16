package product.clicklabs.jugnoo.driver.home.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shankar on 6/15/16.
 */
public class EngagementSPData {

	@SerializedName("pathStartId")
	@Expose
	private int pathStartId;
	@SerializedName("status")
	@Expose
	private int status;
	@SerializedName("pickupLatitude")
	@Expose
	private double pickupLatitude;
	@SerializedName("pickupLongitude")
	@Expose
	private double pickupLongitude;
	@SerializedName("customerId")
	@Expose
	private int customerId;
	@SerializedName("referenceId")
	@Expose
	private int referenceId;



	public EngagementSPData(int pathStartId, int status, double pickupLatitude, double pickupLongitude,
							int customerId, int referenceId) {
		this.pathStartId = pathStartId;
		this.status = status;
		this.pickupLatitude = pickupLatitude;
		this.pickupLongitude = pickupLongitude;
		this.customerId = customerId;
		this.referenceId = referenceId;
	}

	public int getPathStartId() {
		return pathStartId;
	}

	public void setPathStartId(int pathStartId) {
		this.pathStartId = pathStartId;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public double getPickupLatitude() {
		return pickupLatitude;
	}

	public void setPickupLatitude(double pickupLatitude) {
		this.pickupLatitude = pickupLatitude;
	}

	public double getPickupLongitude() {
		return pickupLongitude;
	}

	public void setPickupLongitude(double pickupLongitude) {
		this.pickupLongitude = pickupLongitude;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public int getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(int referenceId) {
		this.referenceId = referenceId;
	}
}

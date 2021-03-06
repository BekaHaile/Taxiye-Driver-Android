package product.clicklabs.jugnoo.driver.home.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shankar on 6/15/16.
 */
public class EngagementSPData {

	@SerializedName("engagementId")
	@Expose
	private int engagementId;
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


	public EngagementSPData(int engagementId, int status, double pickupLatitude, double pickupLongitude,
							int customerId, int referenceId) {
		this.engagementId = engagementId;
		this.status = status;
		this.pickupLatitude = pickupLatitude;
		this.pickupLongitude = pickupLongitude;
		this.customerId = customerId;
		this.referenceId = referenceId;
	}

	public EngagementSPData(int engagementId){
		this.engagementId = engagementId;
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

	public int getEngagementId() {
		return engagementId;
	}

	public void setEngagementId(int engagementId) {
		this.engagementId = engagementId;
	}

	@Override
	public boolean equals(Object o) {
		try{
			return ((EngagementSPData)o).getEngagementId() == getEngagementId();
		} catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
}

package product.clicklabs.jugnoo.driver.datastructure;

import com.google.android.gms.maps.model.LatLng;

/**
 * Class for saving Request Data
 * @author shankar
 *
 */
public class DriverRideRequest {
	
	public String engagementId, customerId, address, startTime;
	public int referenceId;
	public LatLng latLng;
	public BusinessType businessType;
    public double fareFactor;
	
	/**
	 * generic constructor
	 * @param engagementId
	 * @param customerId
	 * @param latLng
	 * @param startTime
	 * @param address
	 * @param businessId
	 * @param referenceId
	 */
	public DriverRideRequest(String engagementId, String customerId, LatLng latLng, String startTime, String address, int businessId,
			int referenceId, double fareFactor){
		this.engagementId = engagementId;
		this.customerId = customerId;
		this.latLng = latLng;
		this.startTime = startTime;
		this.address = address;
		
		if(BusinessType.AUTOS.getOrdinal() == businessId){
			this.businessType = BusinessType.AUTOS;
		}
		else if(BusinessType.MEALS.getOrdinal() == businessId){
			this.businessType = BusinessType.MEALS;
		}
		else if(BusinessType.FATAFAT.getOrdinal() == businessId){
			this.businessType = BusinessType.FATAFAT;
		}
		
		this.referenceId = referenceId;

        this.fareFactor = fareFactor;
	}
	
	
	/**
	 * Template constructor for object matching
	 * @param engagementId
	 */
	public DriverRideRequest(String engagementId){
		this.engagementId = engagementId;
	}
	
	
	@Override
	public String toString() {
		return engagementId + " " + customerId + " " + latLng + " " + startTime + " " + businessType;
	}
	
	@Override
	public boolean equals(Object o) {
		try{
			if(((DriverRideRequest)o).engagementId.equalsIgnoreCase(engagementId)){
				return true;
			}
			else{
				return false;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
}

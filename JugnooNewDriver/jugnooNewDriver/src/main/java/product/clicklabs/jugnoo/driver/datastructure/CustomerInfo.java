package product.clicklabs.jugnoo.driver.datastructure;

import com.google.android.gms.maps.model.LatLng;

/**
 * Super class for customer info
 * @author shankar
 *
 */
public class CustomerInfo {
	
	public int engagementId;
	public int userId, referenceId;
	public BusinessType businessType;
	public String name, phoneNumber;
	public LatLng requestlLatLng;
	private int cachedApiEnabled;
	
	/**
	 * Customer Info super constructor
	 * @param engagementId
	 * @param userId
	 * @param referenceId
	 * @param name
	 * @param phoneNumber
	 * @param requestlLatLng
	 */
	public CustomerInfo(int engagementId, int userId, int referenceId, String name, String phoneNumber, LatLng requestlLatLng, int cachedApiEnabled){
		this.engagementId = engagementId;
		this.userId = userId;
		this.referenceId = referenceId;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.requestlLatLng = requestlLatLng;
		this.cachedApiEnabled = cachedApiEnabled;
	}
	
	
	@Override
	public String toString() {
		return "engagementId = "+engagementId+" userId = "+userId+" referenceId = "+referenceId+" businessType = "+businessType+" name = "+name+" requestlLatLng = "+requestlLatLng+", cachedApiEnabled="+cachedApiEnabled;
	}

	public int getCachedApiEnabled() {
		return cachedApiEnabled;
	}

	public void setCachedApiEnabled(int cachedApiEnabled) {
		this.cachedApiEnabled = cachedApiEnabled;
	}
}

package product.clicklabs.jugnoo.driver.datastructure;

import com.google.android.gms.maps.model.LatLng;

/**
 * Class for saving meals related customer
 * @author shankar
 *
 */
public class MealCustomerInfo extends CustomerInfo{
	
	public String address;
	
	public MealCustomerInfo(int engagementId, int userId, int referenceId, String name, String phoneNumber, LatLng requestlLatLng,
			String address){
		super(engagementId, userId, referenceId, name, phoneNumber, requestlLatLng);
		this.businessType = BusinessType.MEALS;
		this.address = address;
	}
	
	@Override
	public String toString() {
		return super.toString()+" address = "+address;
	}
	
}

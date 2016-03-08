package product.clicklabs.jugnoo.driver.datastructure;

import com.google.android.gms.maps.model.LatLng;

/**
 * Class for saving fatafat related customer
 * @author shankar
 *
 */
public class FatafatOrderInfo extends CustomerInfo{

	public String address;
	public int orderAmount;
	public FatafatCustomerInfo customerInfo;
	public FatafatDeliveryInfo deliveryInfo;
	
	public FatafatOrderInfo(int engagementId, int userId, int referenceId, String name, String phoneNumber, LatLng requestlLatLng,
			String address, int orderAmount){
		super(engagementId, userId, referenceId, name, phoneNumber, requestlLatLng, 0);
		this.businessType = BusinessType.FATAFAT;
		this.address = address;
		this.orderAmount = orderAmount;
	}
	
	public void setCustomerDeliveryInfo(FatafatCustomerInfo customerInfo, FatafatDeliveryInfo deliveryInfo){
		this.customerInfo = customerInfo;
		this.deliveryInfo = deliveryInfo;
	}
	
	@Override
	public String toString() {
		return super.toString()+" address = "+address+" orderAmount = "+orderAmount+" customerInfo = "+customerInfo+" deliveryInfo = "+deliveryInfo;
	}
	
}

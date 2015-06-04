package product.clicklabs.jugnoo.driver.datastructure;

import com.google.android.gms.maps.model.LatLng;

/**
 * Class for saving auto related customer
 * @author shankar
 *
 */
public class AutoCustomerInfo extends CustomerInfo{

	public String image, rating, schedulePickupTime;
	public int freeRide;
	public CouponInfo couponInfo;
	public PromoInfo promoInfo;
	public double jugnooBalance;
	
	public AutoCustomerInfo(int engagementId, int userId, int referenceId, String name, String phoneNumber, LatLng requestlLatLng,
			String image, String rating, String schedulePickupTime, int freeRide,
			CouponInfo couponInfo, PromoInfo promoInfo, double jugnooBalance){
		super(engagementId, userId, referenceId, name, phoneNumber, requestlLatLng);
		this.businessType = BusinessType.AUTOS;
		this.image = image;
		this.rating = rating;
		this.schedulePickupTime = schedulePickupTime;
		this.freeRide = freeRide;
		this.couponInfo = couponInfo;
		this.promoInfo = promoInfo;
		this.jugnooBalance = jugnooBalance;
		
		this.image = this.image.replace("http://graph.facebook", "https://graph.facebook");
	}
	
	public AutoCustomerInfo(int engagementId, int userId, int referenceId, String name, String phoneNumber, LatLng requestlLatLng,
			String image, CouponInfo couponInfo, PromoInfo promoInfo){
		super(engagementId, userId, referenceId, name, phoneNumber, requestlLatLng);
		this.businessType = BusinessType.AUTOS;
		this.businessType = BusinessType.AUTOS;
		this.image = image;
		this.rating = "4";
		this.schedulePickupTime = "";
		this.freeRide = 0;
		this.couponInfo = couponInfo;
		this.promoInfo = promoInfo;
		this.jugnooBalance = 0;
	}
	
	@Override
	public String toString() {
		return super.toString()+" couponInfo = "+couponInfo+" promoInfo = "+promoInfo+" jugnooBalance = "+jugnooBalance;
	}
}
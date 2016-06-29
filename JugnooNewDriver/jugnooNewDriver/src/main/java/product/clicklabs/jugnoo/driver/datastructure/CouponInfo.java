package product.clicklabs.jugnoo.driver.datastructure;

import com.google.android.gms.maps.model.LatLng;

import product.clicklabs.jugnoo.driver.utils.DateOperations;

public class CouponInfo {
	
	public int status;
	public String title;
	public String subtitle;
	public String description;
	public String image;
	public String redeemedOn;
	public String expiryDate;
	public double discountPrecent, maximumDiscountValue, cappedFare, cappedFareMaximum;
	public int count;
	public boolean enabled;
    public int couponType, benefitType;
    public double dropRadius;
    public LatLng droplLatLng;
    public boolean couponApplied;

	public CouponInfo(String title, String subtitle, String description,
			double discountPrecent, double maximumDiscountValue, double cappedFare, double cappedFareMaximum,
                      int couponType, int benefitType, double dropLatitude, double dropLongitude, double dropRadius){
		this.status = CouponStatus.ACTIVE.getOrdinal();
		this.title = title;
		this.subtitle = subtitle;
		this.description = description;
		this.image = "";
		this.redeemedOn = DateOperations.getCurrentTime();
		this.expiryDate = DateOperations.getCurrentTime();
		this.discountPrecent = discountPrecent;
		this.maximumDiscountValue = maximumDiscountValue;
		this.cappedFare = cappedFare;
		this.cappedFareMaximum = cappedFareMaximum;
		this.count = 1;
		this.enabled = true;

        this.couponType = couponType;
        this.benefitType = benefitType;
        this.droplLatLng = new LatLng(dropLatitude, dropLongitude);
        this.dropRadius = dropRadius;
        this.couponApplied = false;
	}
	
	@Override
	public boolean equals(Object o) {
		try{
			if((((CouponInfo)o).couponType == this.couponType) && (((CouponInfo)o).expiryDate.equalsIgnoreCase(this.expiryDate))){
				return true;
			}
			else{
				return false;
			}
		} catch(Exception e){
			return false;
		}
	}
	
	@Override
	public String toString() {
		return status+","+title+", "+discountPrecent+", "+maximumDiscountValue
				+", "+cappedFare+", "+cappedFareMaximum+", "+couponType+", "+benefitType+", "+droplLatLng+", "+dropRadius;
	}

}

package product.clicklabs.jugnoo.driver.datastructure;


import com.google.android.gms.maps.model.LatLng;

public class PromoInfo {

	public String title;
    public int promoType, benefitType;
	public double discountPercentage, discountMaximum, cappedFare, cappedFareMaximum, cashbackPercentage, dropRadius;
    public LatLng droplLatLng;
    public boolean promoApplied;
	
	public PromoInfo(String title, int promoType, int benefitType, double discountPercentage, double discountMaximum, double cappedFare, double cappedFareMaximum,
                     double cashbackPercentage, double dropLatitude, double dropLongitude, double dropRadius){
		this.title = title;
        this.promoType = promoType;
        this.benefitType = benefitType;
		this.discountPercentage = discountPercentage;
		this.discountMaximum = discountMaximum;
		this.cappedFare = cappedFare;
		this.cappedFareMaximum = cappedFareMaximum;
        this.cashbackPercentage = cashbackPercentage;
        this.droplLatLng = new LatLng(dropLatitude, dropLongitude);
        this.dropRadius = dropRadius;
        this.promoApplied = false;
	}
	
	
	
	@Override
	public String toString() {
		return title+" "+discountPercentage+" "+discountMaximum+" "+cappedFare+" "+cappedFareMaximum+" "+promoType+" "+benefitType+" "+cashbackPercentage+""+droplLatLng+" "+dropRadius;
	}
	
}

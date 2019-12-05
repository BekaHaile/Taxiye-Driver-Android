package product.clicklabs.jugnoo.driver.datastructure;


import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class PromoInfo {

	public String title;
    public int promoType, benefitType;
	public double discountPercentage, discountMaximum, cappedFare, cappedFareMaximum, cashbackPercentage, dropRadius;
    public LatLng droplLatLng;
    public boolean promoApplied;
    private ArrayList<LatLng> locationsCoordinates;
	
	public PromoInfo(String title, int promoType, int benefitType, double discountPercentage, double discountMaximum, double cappedFare, double cappedFareMaximum,
                     double cashbackPercentage, double dropLatitude, double dropLongitude, double dropRadius, ArrayList<LatLng> locationsCoordinates){
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
        this.locationsCoordinates = locationsCoordinates;
	}
	
	
	
	@Override
	public String toString() {
		return title+" "+discountPercentage+" "+discountMaximum+" "+cappedFare+" "+cappedFareMaximum+" "+promoType+" "+benefitType+" "+cashbackPercentage+""+droplLatLng+" "+dropRadius;
	}

	public ArrayList<LatLng> getLocationsCoordinates() {
		return locationsCoordinates;
	}

	public void setLocationsCoordinates(ArrayList<LatLng> locationsCoordinates) {
		this.locationsCoordinates = locationsCoordinates;
	}
}

package product.clicklabs.jugnoo.driver.datastructure;


public class PromoInfo {
	
	public String title;
	public double discountPercentage, discountMaximum, cappedFare, cappedFareMaximum;
	
	public PromoInfo(String title, double discountPercentage, double discountMaximum, double cappedFare, double cappedFareMaximum){
		this.title = title;
		this.discountPercentage = discountPercentage;
		this.discountMaximum = discountMaximum;
		this.cappedFare = cappedFare;
		this.cappedFareMaximum = cappedFareMaximum;
	}
	
	
	
	@Override
	public String toString() {
		return title + " " + discountPercentage + " " + discountMaximum + " " + cappedFare + " " + cappedFareMaximum;
	}
	
}

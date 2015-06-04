package product.clicklabs.jugnoo.driver.datastructure;

import com.google.android.gms.maps.model.LatLng;

public class FatafatDeliveryInfo {
	
	public int orderId;
	public String deliveryAddress;
	public LatLng deliveryLatLng;
	public double finalPrice, discount, paidFromWallet, customerToPay;
	
	
	public FatafatDeliveryInfo(int orderId, String deliveryAddress, LatLng deliveryLatLng, 
			double finalPrice, double discount, double paidFromWallet, double customerToPay){
		this.orderId = orderId;
		this.deliveryAddress = deliveryAddress;
		this.deliveryLatLng = deliveryLatLng;
		
		this.finalPrice = finalPrice;
		this.discount = discount;
		this.paidFromWallet = paidFromWallet;
		this.customerToPay = customerToPay;
	}
	
	public void updatePrices(double finalPrice, double discount, double paidFromWallet, double customerToPay){
		this.finalPrice = finalPrice;
		this.discount = discount;
		this.paidFromWallet = paidFromWallet;
		this.customerToPay = customerToPay;
	}
	
	@Override
	public String toString() {
		return "orderId = "+orderId+" finalPrice = "+finalPrice+" discount = "+discount+" paidFromWallet = "+paidFromWallet+" customerToPay = "+customerToPay;
	}
	
}

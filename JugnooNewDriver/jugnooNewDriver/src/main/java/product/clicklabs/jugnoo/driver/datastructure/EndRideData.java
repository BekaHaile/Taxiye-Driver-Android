package product.clicklabs.jugnoo.driver.datastructure;

import java.util.ArrayList;

public class EndRideData {
	
	public String engagementId;
	public double fare, discount, paidUsingWallet, toPay;
	public int paymentMode;
	private String currency;
	private ArrayList<FareDetail> fareDetails;
	
	public EndRideData(String engagementId, double fare, double discount, double paidUsingWallet, double toPay, int paymentMode,String currency,
					   ArrayList<FareDetail> fareDetails){
		this.engagementId = engagementId;
		
		this.fare = fare;
		this.discount = discount;
		this.paidUsingWallet = paidUsingWallet;
		this.toPay = toPay;
		this.paymentMode = paymentMode;
		this.currency = currency;
		this.fareDetails = fareDetails;
	}
	
	@Override
	public String toString() {
		return "engagementId="+engagementId+", fare="+fare+", discount="+discount+", paidUsingWallet="+paidUsingWallet+", toPay="+toPay+"," +
				" paymentMode="+paymentMode  + "currency=" +currency;
	}

	public String getCurrency() {
		return currency;
	}

	public ArrayList<FareDetail> getFareDetails() {
		return fareDetails;
	}

	public void setFareDetails(ArrayList<FareDetail> fareDetails) {
		this.fareDetails = fareDetails;
	}
}

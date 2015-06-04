package product.clicklabs.jugnoo.driver.datastructure;

public class EndRideData {
	
	public String engagementId;
	public double fare, discount, paidUsingWallet, toPay;
	public int paymentMode;
	
	public EndRideData(String engagementId, double fare, double discount, double paidUsingWallet, double toPay, int paymentMode){
		this.engagementId = engagementId;
		
		this.fare = fare;
		this.discount = discount;
		this.paidUsingWallet = paidUsingWallet;
		this.toPay = toPay;
		this.paymentMode = paymentMode;
	}
	
	@Override
	public String toString() {
		return "engagementId="+engagementId+", fare="+fare+", discount="+discount+", paidUsingWallet="+paidUsingWallet+", toPay="+toPay+", paymentMode="+paymentMode;
	}
	
}

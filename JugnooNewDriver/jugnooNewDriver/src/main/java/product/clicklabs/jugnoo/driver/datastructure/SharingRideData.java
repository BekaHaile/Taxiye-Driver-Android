package product.clicklabs.jugnoo.driver.datastructure;

/**
 * Created by aneeshbansal on 05/10/15.
 */
public class SharingRideData {

	public String sharingEngagementId, transactionDateTime, customerPhoneNumber;
	public double actualFare, customerPaid, accountBalance;
	public int completed;
	private String currency;

	public SharingRideData(String sharingEngagementId, String transactionDateTime, String customerPhoneNumber,
						   double actualFare, double customerPaid, double accountBalance,String currency) {

		this.sharingEngagementId = sharingEngagementId;
		this.transactionDateTime = transactionDateTime;
		this.customerPhoneNumber = customerPhoneNumber;
		this.actualFare = actualFare;
		this.customerPaid =  customerPaid;
		this.accountBalance = accountBalance;
		this.completed = 0;
		this.currency = currency;
	}


	@Override
	public boolean equals(Object o) {
		try{
			if(((SharingRideData)o).sharingEngagementId.equalsIgnoreCase(this.sharingEngagementId)){
				return true;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	public String getCurrency() {
		return currency;
	}
}

package product.clicklabs.jugnoo.driver.datastructure;

/**
 * Created by aneeshbansal on 05/10/15.
 */
public class SharingRideData {

	public String sharingEngagementId, transactionDateTime, customerPhoneNumber;
	public double actualFare, customerPaid, accountBalance;
	public int completed;

	public SharingRideData(String sharingEngagementId, String transactionDateTime, String customerPhoneNumber,
						   double actualFare, double customerPaid, double accountBalance) {

		this.sharingEngagementId = sharingEngagementId;
		this.transactionDateTime = transactionDateTime;
		this.customerPhoneNumber = customerPhoneNumber;
		this.actualFare = actualFare;
		this.customerPaid =  customerPaid;
		this.accountBalance = accountBalance;
	}

}

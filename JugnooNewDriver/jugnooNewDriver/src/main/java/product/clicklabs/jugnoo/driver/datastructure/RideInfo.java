package product.clicklabs.jugnoo.driver.datastructure;

public class RideInfo {

	public String id;
	public String fromLocation;
	public String toLocation;
	public String fare;
	public String customerPaid;
	public String balance;
	public String subsidy;
	public String distance;
	public String rideTime;
	public String waitTime;
	public String dateTime;
	public int couponUsed;
	public int paymentMode;
	public int businessId;
	public String paidToMerchant;
	public String paidByCustomer;
	public int driverPaymentStatus;
	public String statusString;
	public String convenienceCharges, luggageCharges, fareFactorApplied,
			fareFactorValue, acceptSubsidy, cancelSubsidy, accountBalance, actualFare, driverRideFair;


	public RideInfo(String id, String fromLocation, String toLocation,
					String fare, String customerPaid, String balance, String subsidy,
					String distance, String rideTime, String waitTime, String dateTime,
					int couponUsed, int paymentMode, int businessId, String paidToMerchant, String paidByCustomer,
					int paymentStatus, String statusString, String convenienceCharges, String luggageCharges, String fareFactorApplied,
					String fareFactorValue, String acceptSubsidy, String cancelSubsidy, String accountBalance, String actualFare, String driverRideFair) {
		this.id = id;
		this.fromLocation = fromLocation;
		this.toLocation = toLocation;

		this.fare = fare;
		this.customerPaid = customerPaid;
		this.balance = balance;
		this.subsidy = subsidy;


		this.distance = distance;
		this.rideTime = rideTime;
		this.waitTime = waitTime;
		this.dateTime = dateTime;

		this.couponUsed = couponUsed;
		this.balance = balance;

		this.paymentMode = paymentMode;

		this.businessId = businessId;
		this.paidToMerchant = paidToMerchant;
		this.paidByCustomer = paidByCustomer;

		this.driverPaymentStatus = paymentStatus;
		this.statusString = statusString;

		this.convenienceCharges = convenienceCharges;
		this.luggageCharges = luggageCharges;
		this.fareFactorApplied = fareFactorApplied;
		this.fareFactorValue = fareFactorValue;
		this.acceptSubsidy = acceptSubsidy;
		this.cancelSubsidy = cancelSubsidy;
		this.accountBalance = accountBalance;
		this.actualFare = actualFare;
		this.driverRideFair = driverRideFair;
	}

	@Override
	public String toString() {
		return id + " " + dateTime;
	}

}

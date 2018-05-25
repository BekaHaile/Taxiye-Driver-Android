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
	public int driverPaymentStatus, customerId;
	public String statusString, referredOn, phone, status, type;
	public String convenienceCharges, luggageCharges, fareFactorApplied, referralAmount, amount, jugnooCut,
			fareFactorValue, acceptSubsidy, cancelSubsidy, accountBalance, actualFare, driverRideFair,currency;
	private String distanceUnit;


	public RideInfo(String id, String fromLocation, String toLocation,
					String fare, String customerPaid, String balance, String subsidy,
					String distance, String rideTime, String waitTime, String dateTime,
					int couponUsed, int paymentMode, int businessId, int paymentStatus,
					String statusString, String convenienceCharges, String luggageCharges, String fareFactorApplied,
					String fareFactorValue, String acceptSubsidy, String cancelSubsidy, String accountBalance, String actualFare,
					String type, String driverRideFair, String jugnooCut,String currency, String distanceUnit) {
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
		this.type = type;
		this.driverRideFair = driverRideFair;
		this.jugnooCut = jugnooCut;
		this.currency = currency;
		this.distanceUnit = distanceUnit;
	}

	public RideInfo(int customerId, String referralAmount, String referredOn, String type, String dateTime,String currency){
		this.customerId = customerId;
		this.referralAmount = referralAmount;
		this.referredOn = referredOn;
		this.type = type;
		this.dateTime = dateTime;
		this.currency = currency;
	}

	public RideInfo( String amount, String type, String dateTime,String currency){
		this.amount = amount;
		this.type = type;
		this.dateTime = dateTime;
		this.currency = currency;
	}

	public RideInfo( String amount, String type, String dateTime, String status, String phone,String currency){
		this.amount = amount;
		this.type = type;
		this.dateTime = dateTime;
		this.status = status;
		this.phone = phone;
		this.currency = currency;
	}


	public RideInfo(String id, String fromLocation, String toLocation,
					String fare, String customerPaid, String balance, String subsidy,
					String distance, String rideTime, String waitTime, String dateTime,
					int paymentMode, int paymentStatus,
					String statusString, String convenienceCharges, String luggageCharges, String fareFactorApplied,
					String fareFactorValue, String acceptSubsidy, String cancelSubsidy, String accountBalance, String actualFare,
					String type, String driverRideFair, String jugnooCut,String currency) {
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
		this.type = type;
		this.driverRideFair = driverRideFair;
		this.jugnooCut = jugnooCut;
		this.currency = currency;
	}

	@Override
	public String toString() {
		return id + " " + dateTime;
	}

	public String getDistanceUnit() {
		return distanceUnit;
	}

	public void setDistanceUnit(String distanceUnit) {
		this.distanceUnit = distanceUnit;
	}
}


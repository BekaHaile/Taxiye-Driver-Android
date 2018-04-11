package product.clicklabs.jugnoo.driver.datastructure;

public class UserData {
	public String accessToken, userName, userImage, referralCode, phoneNo, referralMessage, referralButtonText;
	public int freeRideIconDisable, autosEnabled, mealsEnabled, fatafatEnabled,
			autosAvailable, mealsAvailable, fatafatAvailable;
	public int sharingEnabled, sharingAvailable, paytmRechargeEnabled, destinationOptionEnable;
	public double showDriverRating;
	public String deiValue, customerReferralBonus, driverSupportNumber, referralSMSToCustomer, referralDialogText;
	public String driverOnlineHours, referralDialogHintText, timeoutMessage;
	public double driverArrivalDistance;
	public long remainigPenaltyPeriod, walletUpdateTimeout;
	public String userId, userEmail, blockedAppPackageMessage;
	private int deliveryEnabled, deliveryAvailable;
	public Integer fareCachingLimit,isCaptiveDriver;
	private String countryCode;

	public UserData(String accessToken, String userName, String userImage, String referralCode, String phoneNo,
					int freeRideIconDisable, int autosEnabled, int mealsEnabled, int fatafatEnabled,
					int autosAvailable, int mealsAvailable, int fatafatAvailable, String deiValue, String customerReferralBonus,
					int sharingEnabled, int sharingAvailable, String driverSupportNumber, String referralSMSToCustomer,
					double showDriverRating, double driverArrivalDistance, String referralMessage, String referralButtonText,
					String referralDialogText, String referralDialogHintText, long remainigPenaltyPeriod,
					String timeoutMessage, int paytmRechargeEnabled, int destinationOptionEnable, long walletUpdateTimeout,
					String userId, String userEmail, String blockedAppPackageMessage,
					int deliveryEnabled, int deliveryAvailable,Integer fareCachingLimit, int isCaptiveDriver, String countryCode) {

		this.accessToken = accessToken;
		this.userName = userName;
		this.userImage = userImage;
		this.referralCode = referralCode;
		this.phoneNo = phoneNo;
		this.freeRideIconDisable = freeRideIconDisable;

		this.autosEnabled = autosEnabled;
		this.mealsEnabled = mealsEnabled;
		this.fatafatEnabled = fatafatEnabled;
		this.autosAvailable = autosAvailable;

		this.mealsAvailable = mealsAvailable;
		this.fatafatAvailable = fatafatAvailable;
		this.deiValue = deiValue;
		this.customerReferralBonus = customerReferralBonus;

		this.sharingEnabled = sharingEnabled;
		this.sharingAvailable = sharingAvailable;
		this.driverSupportNumber = driverSupportNumber;
		this.referralSMSToCustomer = referralSMSToCustomer;
		this.driverOnlineHours = "00:00";

		this.showDriverRating = showDriverRating;
		this.driverArrivalDistance = driverArrivalDistance;
		this.referralMessage = referralMessage;
		this.referralButtonText = referralButtonText;
		this.referralDialogText = referralDialogText;
		this.referralDialogHintText = referralDialogHintText;
		this.remainigPenaltyPeriod = remainigPenaltyPeriod;
		this.timeoutMessage = timeoutMessage;
		this.paytmRechargeEnabled = paytmRechargeEnabled;
		this.destinationOptionEnable = destinationOptionEnable;
		this.walletUpdateTimeout = walletUpdateTimeout;
		this.userId = userId;
		this.userEmail = userEmail;
		this.blockedAppPackageMessage = blockedAppPackageMessage;
		this.deliveryEnabled = deliveryEnabled;
		this.deliveryAvailable = deliveryAvailable;
		this.fareCachingLimit = fareCachingLimit;
		this.isCaptiveDriver = isCaptiveDriver;
		this.countryCode = countryCode;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public int getDeliveryEnabled() {
		return deliveryEnabled;
	}

	public void setDeliveryEnabled(int deliveryEnabled) {
		this.deliveryEnabled = deliveryEnabled;
	}

	public int getDeliveryAvailable() {
		return deliveryAvailable;
	}

	public void setDeliveryAvailable(int deliveryAvailable) {
		this.deliveryAvailable = deliveryAvailable;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
}

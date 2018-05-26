package product.clicklabs.jugnoo.driver.datastructure;

import android.content.Context;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.utils.Prefs;

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
	private String userIdentifier;
	private String driverSupportEmail, driverSupportEmailSubject;
	private String hippoTicketFAQ;
	private String currency;
	public Double creditsEarned,commissionSaved;

	public UserData(String accessToken, String userName, String userImage, String referralCode, String phoneNo,
					int freeRideIconDisable, int autosEnabled, int mealsEnabled, int fatafatEnabled,
					int autosAvailable, int mealsAvailable, int fatafatAvailable, String deiValue, String customerReferralBonus,
					int sharingEnabled, int sharingAvailable, String driverSupportNumber, String referralSMSToCustomer,
					double showDriverRating, double driverArrivalDistance, String referralMessage, String referralButtonText,
					String referralDialogText, String referralDialogHintText, long remainigPenaltyPeriod,
					String timeoutMessage, int paytmRechargeEnabled, int destinationOptionEnable, long walletUpdateTimeout,
					String userId, String userEmail, String blockedAppPackageMessage,
					int deliveryEnabled, int deliveryAvailable,Integer fareCachingLimit, int isCaptiveDriver, String countryCode,String userIdentifier,
					String driverSupportEmail, String driverSupportEmailSubject, String hippoTicketFAQ, String currency,
					Double creditsEarned,Double commissionSaved) {

		this.userIdentifier = userIdentifier;
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
		this.driverSupportEmail = driverSupportEmail;
		this.driverSupportEmailSubject = driverSupportEmailSubject;
		this.hippoTicketFAQ = hippoTicketFAQ;
		this.currency = currency;
		this.creditsEarned = creditsEarned;
		this.commissionSaved = commissionSaved;
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

	public String getUserIdentifier() {
		return userIdentifier;
	}

	public void setUserIdentifier(String userIdentifier) {
		this.userIdentifier = userIdentifier;
	}

	public String getDriverSupportEmail() {
		return driverSupportEmail;
	}

	public void setDriverSupportEmail(String driverSupportEmail) {
		this.driverSupportEmail = driverSupportEmail;
	}

	public String getDriverSupportEmailSubject() {
		return driverSupportEmailSubject;
	}

	public void setDriverSupportEmailSubject(String driverSupportEmailSubject) {
		this.driverSupportEmailSubject = driverSupportEmailSubject;
	}

	public String getHippoTicketFAQ() {
		return hippoTicketFAQ;
	}

	public void setHippoTicketFAQ(String hippoTicketFAQ) {
		this.hippoTicketFAQ = hippoTicketFAQ;
	}

	public static String getDistanceUnit(Context context){
		return Prefs.with(context).getString(Constants.KEY_DISTANCE_UNIT, context.getString(R.string.km));
	}
	public static double getDistanceUnitFactor(Context context){
		double factor = (double) Prefs.with(context).getFloat(Constants.KEY_DISTANCE_UNIT_FACTOR, 1F);
		return factor/1000.0D;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
}

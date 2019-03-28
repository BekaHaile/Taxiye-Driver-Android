package product.clicklabs.jugnoo.driver.datastructure;

import android.content.Context;

import java.util.List;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.adapters.VehicleDetailsLogin;
import product.clicklabs.jugnoo.driver.ui.popups.DriverVehicleServiceTypePopup;
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
	private String hippoTicketFAQ;
	private String currency;
	public Double creditsEarned,commissionSaved;
	private String referralMessageDriver;
	private String referralImageD2D, referralImageD2C;
	private String getCreditsInfo, getCreditsImage;
	private int sendCreditsEnabled;
	private VehicleDetailsLogin vehicleDetailsLogin;
	private  List<DriverVehicleServiceTypePopup.VehicleServiceDetail> vehicleServicesModel;
	private int gender;
	private String dateOfBirth;

	public UserData(String accessToken, String userName, String userImage, String referralCode, String phoneNo,
					int freeRideIconDisable, int autosEnabled, int mealsEnabled, int fatafatEnabled,
					int autosAvailable, int mealsAvailable, int fatafatAvailable, String deiValue, String customerReferralBonus,
					int sharingEnabled, int sharingAvailable, String driverSupportNumber, String referralSMSToCustomer,
					double showDriverRating, double driverArrivalDistance, String referralMessage, String referralButtonText,
					String referralDialogText, String referralDialogHintText, long remainigPenaltyPeriod,
					String timeoutMessage, int paytmRechargeEnabled, int destinationOptionEnable, long walletUpdateTimeout,
					String userId, String userEmail, String blockedAppPackageMessage,
					int deliveryEnabled, int deliveryAvailable, Integer fareCachingLimit, int isCaptiveDriver, String countryCode, String userIdentifier,
					String hippoTicketFAQ, String currency,
					Double creditsEarned, Double commissionSaved, String referralMessageDriver,
					String referralImageD2D, String referralImageD2C, String getCreditsInfo, String getCreditsImage,
					int sendCreditsEnabled, VehicleDetailsLogin vehicleDetailsLogin, List<DriverVehicleServiceTypePopup.VehicleServiceDetail> vehicleServicesModel, int gender, String dateOfBirth) {

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
		this.getCreditsInfo = getCreditsInfo;
		this.getCreditsImage = getCreditsImage;
		this.gender = gender;
		this.dateOfBirth = dateOfBirth;
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
		this.hippoTicketFAQ = hippoTicketFAQ;
		this.currency = currency;
		this.creditsEarned = creditsEarned;
		this.commissionSaved = commissionSaved;
		this.referralMessageDriver = referralMessageDriver;
		this.referralImageD2D = referralImageD2D;
		this.referralImageD2C = referralImageD2C;
		this.sendCreditsEnabled = sendCreditsEnabled;
		this.vehicleDetailsLogin = vehicleDetailsLogin;
		setVehicleServicesModel(vehicleServicesModel);
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

	public String getReferralMessageDriver() {
		return referralMessageDriver;
	}

	public void setReferralMessageDriver(String referralMessageDriver) {
		this.referralMessageDriver = referralMessageDriver;
	}

	public String getReferralImageD2C() {
		return referralImageD2C;
	}

	public void setReferralImageD2C(String referralImageD2C) {
		this.referralImageD2C = referralImageD2C;
	}

	public String getReferralImageD2D() {
		return referralImageD2D;
	}

	public void setReferralImageD2D(String referralImageD2D) {
		this.referralImageD2D = referralImageD2D;
	}

	public String getGetCreditsInfo() {
		return getCreditsInfo;
	}

	public void setGetCreditsInfo(String getCreditsInfo) {
		this.getCreditsInfo = getCreditsInfo;
	}

	public String getGetCreditsImage() {
		return getCreditsImage;
	}

	public void setGetCreditsImage(String getCreditsImage) {
		this.getCreditsImage = getCreditsImage;
	}

	public int getSendCreditsEnabled() {
		return sendCreditsEnabled;
	}

	public void setSendCreditsEnabled(int sendCreditsEnabled) {
		this.sendCreditsEnabled = sendCreditsEnabled;
	}

	public VehicleDetailsLogin getVehicleDetailsLogin() {
		return vehicleDetailsLogin;
	}

	public  List<DriverVehicleServiceTypePopup.VehicleServiceDetail> getVehicleServicesModel() {
		if(vehicleServicesModel!=null){
			for(DriverVehicleServiceTypePopup.VehicleServiceDetail model:vehicleServicesModel){
				model.setChecked(model.getServerSelected());
			}

		}
		return vehicleServicesModel;

	}

	public void setVehicleServicesModel(List<DriverVehicleServiceTypePopup.VehicleServiceDetail> vehicleServicesModel) {
		if(vehicleServicesModel!=null){
			for(DriverVehicleServiceTypePopup.VehicleServiceDetail model:vehicleServicesModel){
				model.setServerSelected(model.getChecked());
			}

		}
		this.vehicleServicesModel = vehicleServicesModel;

	}

	public void setVehicleDetailsLogin(VehicleDetailsLogin vehicleDetailsLogin) {
		this.vehicleDetailsLogin = vehicleDetailsLogin;
	}

	public int getGender() {
		return gender;
	}
	public String getGenderName(Context context) {
		if(gender == GenderValues.MALE.getType()){
			return context.getString(R.string.gender_male);
		} else if(gender == GenderValues.FEMALE.getType()){
			return context.getString(R.string.gender_female);
		} else if(gender == GenderValues.OTHER.getType()){
			return context.getString(R.string.gender_others);
		} else {
			return "";
		}
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public String getDateOfBirth() {
		if(dateOfBirth != null && dateOfBirth.equalsIgnoreCase("null")){
			dateOfBirth = "";
		}
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
}

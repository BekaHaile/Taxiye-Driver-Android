package product.clicklabs.jugnoo.datastructure;

public class UserData {
	public String accessToken, authKey, userName, userEmail, userImage, referralCode, phoneNo, nukkadIcon, jugnooMealsPackageName, jugnooFbBanner;
	public int canSchedule, canChangeLocation, schedulingLimitMinutes, isAvailable, exceptionalDriver, gcmIntent, christmasIconEnable, nukkadEnable, enableJugnooMeals,
		freeRideIconDisable;
	public int emailVerificationStatus;
	public double jugnooBalance;
	public int numCouponsAvaliable;
	public double fareFactor;
	public UserData(String accessToken, String authKey, String userName, String userEmail, int emailVerificationStatus, 
			String userImage, String referralCode, String phoneNo,
			int canSchedule, int canChangeLocation, int schedulingLimitMinutes, int isAvailable, int exceptionalDriver, int gcmIntent, int christmasIconEnable, 
			int nukkadEnable, String nukkadIcon, int enableJugnooMeals, String jugnooMealsPackageName, int freeRideIconDisable, double jugnooBalance, double fareFactor,
			String jugnooFbBanner, int numCouponsAvaliable){
		this.accessToken = accessToken;
		this.authKey = authKey;
		this.userName = userName;
		this.userEmail = userEmail;
		this.emailVerificationStatus = emailVerificationStatus;
		
		this.userImage = userImage;
		this.referralCode = referralCode;
		this.phoneNo = phoneNo;
		this.canSchedule = canSchedule;
		this.canChangeLocation = canChangeLocation;
		this.schedulingLimitMinutes = schedulingLimitMinutes;
		this.isAvailable = isAvailable;
		this.exceptionalDriver = exceptionalDriver;
		this.gcmIntent = gcmIntent;
		this.christmasIconEnable = christmasIconEnable;
		this.nukkadEnable = nukkadEnable;
		this.nukkadIcon = nukkadIcon;
		this.enableJugnooMeals = enableJugnooMeals;
		this.jugnooMealsPackageName = jugnooMealsPackageName;
		this.freeRideIconDisable = freeRideIconDisable;
		this.jugnooBalance = jugnooBalance;
		this.fareFactor = fareFactor;
		
		this.jugnooFbBanner = jugnooFbBanner;
		this.numCouponsAvaliable = numCouponsAvaliable;
	}
}

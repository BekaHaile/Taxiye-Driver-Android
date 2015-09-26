package product.clicklabs.jugnoo.driver.datastructure;

public class UserData {
	public String accessToken, userName, userImage, referralCode, phoneNo;
	public int freeRideIconDisable, autosEnabled, mealsEnabled, fatafatEnabled,
		autosAvailable, mealsAvailable, fatafatAvailable;
    public String deiValue, customerReferralBonus;
	public UserData(String accessToken, String userName, String userImage, String referralCode, String phoneNo,
			int freeRideIconDisable, int autosEnabled, int mealsEnabled, int fatafatEnabled,
			int autosAvailable, int mealsAvailable, int fatafatAvailable, String deiValue, String customerReferralBonus){
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
		this. customerReferralBonus = customerReferralBonus;
	}
}

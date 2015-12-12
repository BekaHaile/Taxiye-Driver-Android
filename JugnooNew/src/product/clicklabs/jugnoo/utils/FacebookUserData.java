package product.clicklabs.jugnoo.utils;

public class FacebookUserData {

	public String accessToken, fbId, firstName, lastName, userName, userEmail;
	
	public FacebookUserData(String accessToken, String fbId, String firstName, String lastName, String userName, String userEmail){
		this.accessToken = accessToken;
		this.fbId = fbId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.userName = userName;
		this.userEmail = userEmail;
	}
	
	@Override
	public String toString() {
		return fbId + " " + firstName + " " + lastName + " " + userName + " " + userEmail;
	}
	
}

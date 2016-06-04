package product.clicklabs.jugnoo.datastructure;

/**
 * Created by shankar on 4/25/15.
 */
public class FacebookRegisterData{

    public String fbId, fbName, fbAccessToken, fbUserEmail, fbUserName;
    public String phoneNo, password, referralCode;
    public String accessToken;

    public FacebookRegisterData(String fbId, String fbName, String fbAccessToken, String fbUserEmail, String fbUserName,
        String phoneNo, String password, String referralCode, String accessToken){
        this.fbId = fbId;
        this.fbName = fbName;
        this.fbAccessToken = fbAccessToken;
        this.fbUserEmail = fbUserEmail;
        this.fbUserName = fbUserName;
        this.phoneNo = phoneNo;
        this.password = password;
        this.referralCode = referralCode;
        this.accessToken = accessToken;
    }
}

package product.clicklabs.jugnoo.datastructure;

/**
 * Created by shankar on 4/25/15.
 */
public class EmailRegisterData{

    public String name, emailId, phoneNo, password, referralCode;
    public String accessToken;

    public EmailRegisterData(String name, String emailId, String phoneNo, String password, String referralCode, String accessToken){
        this.name = name;
        this.emailId = emailId;
        this.phoneNo = phoneNo;
        this.password = password;
        this.referralCode = referralCode;
        this.accessToken = accessToken;
    }
}
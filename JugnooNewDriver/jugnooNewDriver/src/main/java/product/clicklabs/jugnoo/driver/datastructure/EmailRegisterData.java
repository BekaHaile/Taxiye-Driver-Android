package product.clicklabs.jugnoo.driver.datastructure;

/**
 * Created by aneeshbansal on 01/08/16.
 */
public class EmailRegisterData {

	public String name, emailId, phoneNo, password, accessToken, autoNum, countryCode;

	public EmailRegisterData(String name, String emailId, String phoneNo, String password, String accessToken, String autoNum, String countryCode) {
		this.name = name;
		this.emailId = emailId;
		this.phoneNo = phoneNo;
		this.password = password;
		this.accessToken = accessToken;
		this.autoNum = autoNum;
		this.countryCode = countryCode;
	}

}

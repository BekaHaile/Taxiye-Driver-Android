package product.clicklabs.jugnoo.driver.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by aneeshbansal on 09/09/15.
 */

public class RegisterScreenResponse {

	@Expose
	private Integer flag;
	@SerializedName("phone_no")
	@Expose
	private String phoneNo;
	@SerializedName("access_token")
	@Expose
	private String accessToken;
	@SerializedName("user_email")
	@Expose
	private String userEmail;
	@Expose
	private String error;

	/**
	 * @return The flag
	 */
	public Integer getFlag() {
		return flag;
	}

	/**
	 * @param flag The flag
	 */
	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	/**
	 * @return The phoneNo
	 */
	public String getPhoneNo() {
		return phoneNo;
	}

	/**
	 * @param phoneNo The phone_no
	 */
	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}

	/**
	 * @return The accessToken
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * @param accessToken The access_token
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * @return The userEmail
	 */
	public String getUserEmail() {
		return userEmail;
	}

	/**
	 * @param userEmail The user_email
	 */
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	/**
	 * @return The error
	 */
	public String getError() {
		return error;
	}

	/**
	 * @param error The error
	 */
	public void setError(String error) {
		this.error = error;
	}

}
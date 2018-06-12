package product.clicklabs.jugnoo.driver.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponse;

/**
 * Created by aneeshbansal on 09/09/15.
 */

public class RegisterScreenResponse extends FeedCommonResponse {


	@SerializedName("phone_no")
	@Expose
	private String phoneNo;
	@SerializedName("access_token")
	@Expose
	private String accessToken;
	@SerializedName("user_email")
	@Expose
	private String userEmail;

	@SerializedName("knowlarity_missed_call_number")
	@Expose
	private String missedCallNumber;

	@SerializedName("otp_length")
	@Expose
	private Integer otpLength;

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

	public String getMissedCallNumber() {
		return missedCallNumber;
	}

	public Integer getOtpLength() {
		return otpLength;
	}
}
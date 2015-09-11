package product.clicklabs.jugnoo.driver.retrofit;

import java.util.Map;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.retrofit.model.BookingHistoryResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.DriverLeaderBoard;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import retrofit.http.PUT;

/**
 * Created by aneeshbansal on 08/09/15.
 */
public interface APIServices {

	@FormUrlEncoded
	@POST("/booking_history")
	void bookingHistory(@Field("access_token") String accessToken,
						@Field("current_mode") String currentMode,
						Callback<BookingHistoryResponse> callback);

	@FormUrlEncoded
	@POST("/forgot_password_driver")
	void forgotpassword(@Field("email") String email,
						Callback<BookingHistoryResponse> callback);

	@FormUrlEncoded
	@POST("/driver/show/leader_board")
	void driverLeaderBoard(@Field("access_token") String accessToken,
						   Callback<DriverLeaderBoard> callback);

	@FormUrlEncoded
	@POST("/get_information")
	void gethelp(@Field("section") int section,
				 Callback<BookingHistoryResponse> callback);

	@FormUrlEncoded
	@POST("/get_information")
	void getHelpSection(@Field("section") int section,
						Callback<BookingHistoryResponse> callback);

	@FormUrlEncoded
	@POST("/send_otp_via_call")
	void initiateOTPCall(@Field("phone_no") String phoneNo,
						 Callback<BookingHistoryResponse> callback);

	@FormUrlEncoded
	@POST("/verify_otp")
	void sendSignupValuesRetro(@Field ("email") String email,
							   @Field ("password") String password,
							   @Field ("device_token") String deviceToken,
							   @Field ("device_type") String deviceType,
							   @Field ("device_name") String deviceName,
							   @Field ("app_version") int appVersion,
							   @Field ("os_version") String osVersion,
							   @Field ("country") String Country,
							   @Field ("unique_device_id") String uniqueDeviceId,
							   @Field ("latitude") double latitude,
							   @Field ("longitude") double longitude,
							   @Field ("client_id") String clientId,
							   @Field ("login_type") String loginType,
							   @Field ("otp") String Otp,
							   Callback<BookingHistoryResponse> callback);

	@FormUrlEncoded
	@POST("/register_using_email")
	void sendSignupValuesRetro(@FieldMap Map<String, String> params,
								Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/logout_driver")
	void logoutRetro(@FieldMap Map<String, String> params,
					 Callback<RegisterScreenResponse> callback);


	@FormUrlEncoded
	@POST("/get_nearest_station")
	void fetchStationDataAPIRetro(@FieldMap Map<String, String> params,
					 Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/mark_delivered")
	void fatafatEndRideAPIRetro(@FieldMap Map<String, String> params,
								  Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/upload_ride_data")
	void driverUploadPathDataFileRetro(@FieldMap Map<String, String> params,
								Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/start_ride")
	void driverStartRideRetro(@FieldMap Map<String, String> params,
									   Callback<RegisterScreenResponse> callback);


}

package product.clicklabs.jugnoo.driver.retrofit;

import java.util.Map;

import product.clicklabs.jugnoo.driver.retrofit.model.BookingHistoryResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.DriverLeaderBoard;
import product.clicklabs.jugnoo.driver.retrofit.model.HeatMapResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.SharedRideResponse;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

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
	@POST("/share_ride_history")
	void getSharedRidesAsync(@Field("access_token") String accessToken,
						Callback<SharedRideResponse> callback);

	@FormUrlEncoded
	@POST("/forgot_password_driver")
	void forgotpassword(@Field("phone_number") String phoneNumber,
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
	void verifyOtpUsingSignupFields(@Field("email") String email,
									@Field("password") String password,
									@Field("device_token") String deviceToken,
									@Field("pushy_token") String pushyToken,
									@Field("device_type") String deviceType,
									@Field("device_name") String deviceName,
									@Field("app_version") int appVersion,
									@Field("os_version") String osVersion,
									@Field("country") String Country,
									@Field("unique_device_id") String uniqueDeviceId,
									@Field("latitude") double latitude,
									@Field("longitude") double longitude,
									@Field("client_id") String clientId,
									@Field("login_type") String loginType,
									@Field("otp") String Otp,
									Callback<BookingHistoryResponse> callback);

	@FormUrlEncoded
	@POST("/register_using_email")
	void registerUsingEmail(@FieldMap Map<String, String> params,
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

	@FormUrlEncoded
	@POST("/reject_a_request")
	void driverRejectRequestRetro(@FieldMap Map<String, String> params,
							  Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/mark_arrived")
	void driverMarkArriveRideRetro(@FieldMap Map<String, String> params,
								  Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/cancel_the_ride")
	void driverCancelRideRetro(@FieldMap Map<String, String> params,
								   Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/accept_a_request")
	void driverAcceptRideRetro(@FieldMap Map<String, String> params,
								   Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/end_ride")
	void autoEndRideAPIRetro(@FieldMap Map<String, String> params,
							   Callback<RegisterScreenResponse> callback);
	@FormUrlEncoded
	@POST("/get_current_user_status")
	Response getUserStatusRetro(@Field ("access_token") String accessToken);

	@FormUrlEncoded
	@POST("/update_user_profile")
	void updateUserProfileAPIRetro(@FieldMap Map<String, String> params,
							 Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/request_dup_registration")
	void submitDuplicateRegistrationRequestAPIRetro(@FieldMap Map<String, String> params,
								   Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/add_money_to_wallet")
	void addCustomerCashRetro(@FieldMap Map<String, String> params,
							  Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/start_end_wait")
	void startEndWaitRetro(@FieldMap Map<String, String> params,
							  Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/acknowledge_stationing")
	void acknowledgeStationDataReadRetro(@FieldMap Map<String, String> params,
						   Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/change_availability")
	Response switchJugnooOnThroughServerRetro(@FieldMap Map<String, String> params);

	@FormUrlEncoded
	@POST("/toggle_sharing_mode")
	Response toggleSharingMode(@FieldMap Map<String, String> params);

	@FormUrlEncoded
	@POST("/acknowledge_port_change")
	Response sendChangePortAckToServerRetro(@Field ("access_token") String accessToken);

	@FormUrlEncoded
	@POST("/acknowledge_heartbeat")
	void sendHeartbeatAckToServerRetro(@FieldMap Map<String, String> params,
										   Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/acknowledge_request")
	Response sendRequestAckToServerRetro(@FieldMap Map<String, String> params);

	@FormUrlEncoded
	@POST("/login_using_email")
	void sendLoginValuesRetro(@FieldMap Map<String, String> params,
										 Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/login_using_access_token")
	void accessTokenLoginRetro(@FieldMap Map<String, String> params,
							  Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/update_in_ride_data")
	Response updateInRideDataRetro(@FieldMap Map<String, String> params);

	@FormUrlEncoded
	@POST("/driver_profile")
	void driverProfileInfo(@Field("access_token") String accessToken,
						Callback<BookingHistoryResponse> callback);

	@FormUrlEncoded
	@POST("/daily_online_hours")
	void dailyOnlineHours(@Field("access_token") String accessToken,
						   Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/heat_map_data")
	void getHeatMapAsync(@Field("access_token") String accessToken,
						 Callback<HeatMapResponse> callback);
}

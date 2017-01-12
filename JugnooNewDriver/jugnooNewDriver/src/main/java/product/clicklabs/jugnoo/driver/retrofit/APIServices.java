package product.clicklabs.jugnoo.driver.retrofit;

import java.util.Map;

import product.clicklabs.jugnoo.driver.retrofit.model.AuditStateResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.AuditTypeResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.BookingHistoryResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.DailyEarningResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.DeliveryDetailResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.DestinationDataResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.DocRequirementResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.CityResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.DriverEarningsResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.DriverLeaderBoard;
import product.clicklabs.jugnoo.driver.retrofit.model.EarningsDetailResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.FetchChatResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.HeatMapResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.InfoTileResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.InvoiceDetailResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.InvoiceDetailResponseNew;
import product.clicklabs.jugnoo.driver.retrofit.model.InvoiceHistoryResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.LeaderboardActivityResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.NewBookingHistoryRespose;
import product.clicklabs.jugnoo.driver.retrofit.model.NewLeaderBoard;
import product.clicklabs.jugnoo.driver.retrofit.model.NotificationAlarmResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.NotificationInboxResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.RateCardResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.SharedRideResponse;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.PartMap;
import retrofit.mime.TypedFile;

/**
 * Created by aneeshbansal on 08/09/15.
 */
public interface APIServices {

	@FormUrlEncoded
	@POST("/booking_history")
	void bookingHistory(@Field("access_token") String accessToken,
						@Field("current_mode") String currentMode,
						Callback<NewBookingHistoryRespose> callback);

	@FormUrlEncoded
	@POST("/share_ride_history")
	void getSharedRidesAsync(@Field("access_token") String accessToken,
							 Callback<SharedRideResponse> callback);

	@FormUrlEncoded
	@POST("/v2/booking_history")
	void getDailyRidesAsync(@FieldMap Map<String, String> params,
							 Callback<DailyEarningResponse> callback);

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
				 @Field("login_type") String loginType,
				 Callback<BookingHistoryResponse> callback);

	@FormUrlEncoded
	@POST("/get_information")
	void getHelpSection(@Field("section") int section,
						Callback<BookingHistoryResponse> callback);

	@FormUrlEncoded
	@POST("/get_information")
	void getHelpSectionNew(@Field("section") int section,
						   @Field("locale") String locale,
						Callback<BookingHistoryResponse> callback);

	@FormUrlEncoded
	@POST("/send_otp_via_call")
	void initiateOTPCall(@Field("phone_no") String phoneNo,
						 Callback<BookingHistoryResponse> callback);

	@FormUrlEncoded
	@POST("/verify_otp_for_driver")
	void verifyOtpUsingSignupFields(@Field("phone_no") String email,
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
	@POST("/verify_otp")
	void verifyOtpOldUsingSignupFields(@Field("email") String email,
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
	@POST("/mark_delivered")
	Response markDeliveredSync(@FieldMap Map<String, String> params);

	@FormUrlEncoded
	@POST("/upload_ride_data")
	void driverUploadPathDataFileRetro(@FieldMap Map<String, String> params,
									   Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/upload_ride_data")
	Response uploadRideDataSync(@FieldMap Map<String, String> params);

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
	@POST("/mark_arrived")
	Response driverMarkArriveSync(@FieldMap Map<String, String> params);

	@FormUrlEncoded
	@POST("/cancel_the_ride")
	void driverCancelRideRetro(@FieldMap Map<String, String> params,
							   Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/accept_a_request")
	void driverAcceptRideRetro(@FieldMap Map<String, String> params,
							   Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/accept_a_request")
	Response driverAcceptRideSync(@FieldMap Map<String, String> params);

	@FormUrlEncoded
	@POST("/end_ride")
	void autoEndRideAPIRetro(@FieldMap Map<String, String> params,
							 Callback<RegisterScreenResponse> callback);
	@FormUrlEncoded
	@POST("/end_ride")
	Response endRideSync(@FieldMap Map<String, String> params);

	@FormUrlEncoded
	@POST("/get_current_user_status")
	Response getUserStatusRetro(@Field("access_token") String accessToken);

	@FormUrlEncoded
	@POST("/update_user_profile")
	void updateUserProfileAPIRetro(@FieldMap Map<String, String> params,
								   Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/request_dup_registration")
	void submitDuplicateRegistrationRequestAPIRetro(@FieldMap Map<String, String> params,
													Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/initiate_user_paytm_recharge")
	void addCustomerCashRetro(@FieldMap Map<String, String> params,
							  Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/change_availability")
	Response switchJugnooOnThroughServerRetro(@FieldMap Map<String, String> params);

	@FormUrlEncoded
	@POST("/toggle_sharing_mode")
	Response toggleSharingMode(@FieldMap Map<String, String> params);


	@FormUrlEncoded
	@POST("/acknowledge_heartbeat")
	void sendHeartbeatAckToServerRetro(@FieldMap Map<String, String> params,
									   Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/acknowledge_request")
	Response sendRequestAckToServerRetro(@FieldMap Map<String, String> params);

	@FormUrlEncoded
	@POST("/driver/login")
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

	@FormUrlEncoded
	@POST("/update_user_profile")
	void updateUserProfileAPIRetroo(@FieldMap Map<String, String> params,
									Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/driver/show/leader_board")
	void leaderboardServerCall(@Field("access_token") String accessToken,
							   @Field("client_id") String clientId,
							   Callback<NewLeaderBoard> callback);

	@FormUrlEncoded
	@POST("/driver/referrals/get_activity")
	void leaderboardActivityServerCall(@Field("access_token") String accessToken,
									   @Field("login_type") String loginType,
									   Callback<LeaderboardActivityResponse> callback);

	@FormUrlEncoded
	@POST("/update_driver_location")
	Response updateDriverLocation(@FieldMap Map<String, String> params) throws RetrofitError;


	@FormUrlEncoded
	@POST("/verify_my_contact_number")
	void verifyMyContactNumber(@FieldMap Map<String, String> params,
							Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/get_missed_rides")
	void getMissedRides(@FieldMap Map<String, String> params,
							   Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/rate_the_customer")
	void rateTheCustomer(@FieldMap Map<String, String> params,
						Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/make_driver_eligible_for_pr")
	void perfectRideRegionRequest(@FieldMap Map<String, String> params,
						 Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/make_driver_eligible_for_pr")
	void updateCustomerLocation(@FieldMap Map<String, String> params,
								  Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/acknowledge_manual_engagement")
	void acknowledgeManualEngagement(@FieldMap Map<String, String> params,
						 Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/log_ongoing_ride_path")
	Response logOngoingRidePath(@FieldMap Map<String, String> params);

	@FormUrlEncoded
	@POST("/send_referral_sms")
	void sendReferralMessage(@FieldMap Map<String, String> params,
						 Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/generate_login_otp")
	void generateOtp(@FieldMap Map<String, String> params,
							 Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/fetch_media_data")
	NotificationAlarmResponse updateNotificationData(@Field("access_token") String accessToken,
													 @Field("file_category") String fileType);

	@FormUrlEncoded
	@POST("/fetch_driver_regions")
	void getDestinationData(@Field("access_token") String accessToken,
						 Callback<DestinationDataResponse> callback);

	@FormUrlEncoded
	@POST("/update_driver_regions")
	void updateDriverRegion(@FieldMap Map<String, String> params,
							   Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/driver/upload_contacts")
	Response sendAllContactsSync(@FieldMap Map<String, String> params);

	@FormUrlEncoded
	@POST("/driver/upload_sms")
	void uploadAnalyticsMessages(@FieldMap Map<String, String> params,
								 Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/driver/upload_sms")
	Response uploadAnalyticsMessagesSync(@FieldMap Map<String, String> params);

	@FormUrlEncoded
	@POST("/driver/upload_call_logs")
	void sendCallLogs(@FieldMap Map<String, String> params,
								  Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/get_all_invoices_for_driver")
	void invoiceHistory(@Field("access_token") String accessToken,
						@Field("current_mode") String currentMode,
						Callback<InvoiceHistoryResponse> callback);

	@FormUrlEncoded
	@POST("/get_invoice_details")
	void invoiceDetail(@Field("access_token") String accessToken,
						@Field("invoice_id") String invoiceId,
						Callback<InvoiceDetailResponse> callback);

	@FormUrlEncoded
	@POST("/v2/get_invoice_details\n")
	void invoiceDetailNew(@Field("access_token") String accessToken,
					   @Field("invoice_id") String invoiceId,
					   Callback<InvoiceDetailResponseNew> callback);

	@FormUrlEncoded
	@POST("/get_wallet_balance_end_ride")
	void updateWalletBalance(@FieldMap Map<String, String> params,
							Callback<RegisterScreenResponse> callback);
	@FormUrlEncoded
	@POST("/driver/upload_notification_logs")
	void sendDriverPushes(@FieldMap Map<String, String> params,
							 Callback<RegisterScreenResponse> callback);


	@FormUrlEncoded
	@POST("/get_driver_earnings")
	void earningDetails(@Field("access_token") String accessToken,
									   @Field("login_type") String loginType,
									   Callback<EarningsDetailResponse> callback);

	@FormUrlEncoded
	@POST("/v2/get_driver_earnings")
	void earningNewDetails(@Field("access_token") String accessToken,
						@Field("login_type") String loginType,
						   @Field("invoice_id") String invoiceId,
						Callback<DriverEarningsResponse> callback);

	@FormUrlEncoded
	@POST("/delivery_details")
	void deliveryDetails(@Field("access_token") String accessToken,
						@Field("login_type") String loginType,
						 @Field("ride_id") String rideId,
						Callback<DeliveryDetailResponse> callback);

	@FormUrlEncoded
	@POST("/fetch_pushes_for_user")
	void notificationInbox(@FieldMap Map<String, String> params,
						   Callback<NotificationInboxResponse> callback);

	@FormUrlEncoded
	@POST("/fetch_active_locales")
	void fetchLanguageList(@FieldMap Map<String, String> params,
					 Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/set_locale_preference")
	void setPreferredLang(@FieldMap Map<String, String> params,
						   Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/mark_delivered")
	void markDelivered(@FieldMap Map<String, String> params,
						Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/cancel_delivery")
	void cancelDelivery(@FieldMap Map<String, String> params,
					   Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/end_delivery")
	void endDelivery(@FieldMap Map<String, String> params,
					   Callback<RegisterScreenResponse> callback);
	@FormUrlEncoded
	@POST("/end_delivery")
	Response endDelivery(@FieldMap Map<String, String> params);

	@FormUrlEncoded
	@POST("/fetch_all_driver_apps")
	void fetchAlldriverApps(@FieldMap Map<String, String> params,
								  Callback<RegisterScreenResponse> callback);

	@Multipart
	@POST("/upload_m_file")
	Response sendmFileToServer(@Part("mFile") TypedFile image,
							   @PartMap Map<String, String> params);

	@FormUrlEncoded
	@POST("/show_rate_card")
	void rateCardDetail(@Field("access_token") String accessToken,
					   Callback<RateCardResponse> callback);

	@FormUrlEncoded
	@POST("/fetch_required_docs")
	void docRequest(@Field("access_token") String accessToken,
					Callback<DocRequirementResponse> callback);

	@Multipart
	@POST("/upload_document")
	void uploadImageToServer(@Part("image") TypedFile image,
							 @PartMap Map<String, String> params,
							 Callback<DocRequirementResponse> cb);

	@Multipart
	@POST("/upload_signature_document")
	void uploadSignatureToServer(@Part("image") TypedFile image,
							 @PartMap Map<String, String> params,
							 Callback<DocRequirementResponse> cb);

	@FormUrlEncoded
	@POST("/delete_document")
	void deleteImage(@FieldMap Map<String, String> params,
					Callback<DocRequirementResponse> callback);

	@FormUrlEncoded
	@POST("/get_all_cities")
	void getCityRetro(@Field("password") String password,
					  Callback<CityResponse> callback);

	@FormUrlEncoded
	@POST("/upload_ring_count_data")
	void sendRingCountData(@FieldMap Map<String, String> params,
						   Callback<RegisterScreenResponse> callback);


	@FormUrlEncoded
	@POST("/verify_document_status")
	void docSubmission(@Field("access_token") String accessToken,
					Callback<DocRequirementResponse> callback);

	@FormUrlEncoded
	@POST("/register_a_driver")
	void oneTimeRegisteration(@FieldMap Map<String, String> params,
							Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/show_tiles")
	void getInfoTilesAsync(@Field("access_token") String accessToken,
							 Callback<InfoTileResponse> callback);
//	@FormUrlEncoded
	@POST("/driver/push/ack")
	Response sendPushAckToServerRetro(@Body String data);


	@Multipart
	@POST("/update_audit_image")
	void uploadAuditImageToServer(@Part("image_file") TypedFile image,
							 @PartMap Map<String, String> params,
							 Callback<DocRequirementResponse> cb);

	@FormUrlEncoded
	@POST("/fetch_audit_details_for_app")
	void fetchAuditDetails(@FieldMap Map<String, String> params,
							  Callback<AuditTypeResponse> callback);

	@FormUrlEncoded
	@POST("/fetch_audit_type_status")
	void fetchAuditTypeStatus(@FieldMap Map<String, String> params,
						   Callback<AuditStateResponse> callback);

	@FormUrlEncoded
	@POST("/update_njb_driver_details")
	void sendAuditDetails(@FieldMap Map<String, String> params,
							 Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/cancel_audit_by_driver")
	void cancelAuditByDriver(@FieldMap Map<String, String> params,
							 Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/submit_audit_images")
	void submitAuditImages(@FieldMap Map<String, String> params,
							 Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/update_audit_image")
	void skipImageToServer(@FieldMap Map<String, String> params,
						   Callback<DocRequirementResponse> cb);

	@FormUrlEncoded
	@POST("/upload_data_usage_logs")
	Response usageData(@FieldMap Map<String, String> params);

	@FormUrlEncoded
	@POST("/log_usl_calls")
	Response uslData(@FieldMap Map<String, String> params);

	@FormUrlEncoded
	@POST("/generate_driver_support_ticket")
	void sendIssue(@Field("access_token") String accessToken,
				   @Field("support_feedback_text") String message,
				   @Field("engagement_id") String engagementId,
				   @Field("ticket_type") String ticketType,
					Callback<DocRequirementResponse> callback);

	@FormUrlEncoded
	@POST("/update_drop_latlng")
	void updateDropLatLng(@FieldMap Map<String, String> params,
						   Callback<InfoTileResponse> callback);

}

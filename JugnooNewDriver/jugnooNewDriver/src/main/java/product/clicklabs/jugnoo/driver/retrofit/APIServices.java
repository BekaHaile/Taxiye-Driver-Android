package product.clicklabs.jugnoo.driver.retrofit;

import java.util.Map;

import product.clicklabs.jugnoo.driver.datastructure.FetchDriverPlansResponse;
import product.clicklabs.jugnoo.driver.datastructure.InitiatePaymentResponse;
import product.clicklabs.jugnoo.driver.datastructure.WalletTransactionResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.AuditStateResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.AuditTypeResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.BookingHistoryResponse;
import product.clicklabs.jugnoo.driver.stripe.model.StripeCardResponse;
import product.clicklabs.jugnoo.driver.stripe.model.WalletModelResponse;
import product.clicklabs.jugnoo.driver.ui.models.CityResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.DailyEarningResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.DeliveryDetailResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.DeliveryRateCardResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.DestinationDataResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.DocRequirementResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.DriverCreditResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.DriverEarningsResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.DriverLeaderBoard;
import product.clicklabs.jugnoo.driver.retrofit.model.EarningsDetailResponse;
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
import product.clicklabs.jugnoo.driver.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.driver.retrofit.model.SharedRideResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.TicketResponse;
import product.clicklabs.jugnoo.driver.tutorial.TourResponseModel;
import product.clicklabs.jugnoo.driver.tutorial.UpdateTourStatusModel;
import product.clicklabs.jugnoo.driver.ui.models.DriverLanguageResponse;
import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponseKotlin;
import product.clicklabs.jugnoo.driver.ui.models.ManualRideResponse;
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
	void bookingHistory(@FieldMap Map<String, String> params,
						Callback<NewBookingHistoryRespose> callback);

	@FormUrlEncoded
	@POST("/share_ride_history")
	void getSharedRidesAsync(@FieldMap Map<String, String> params,
							 Callback<SharedRideResponse> callback);

	@FormUrlEncoded
	@POST("/v2/booking_history")
	void getDailyRidesAsync(@FieldMap Map<String, String> params,
							 Callback<DailyEarningResponse> callback);

	@FormUrlEncoded
	@POST("/forgot_password_driver")
	void forgotpassword(@FieldMap Map<String, String> params,
						Callback<BookingHistoryResponse> callback);

	@FormUrlEncoded
	@POST("/driver/show/leader_board")
	void driverLeaderBoard(@FieldMap Map<String, String> params,
						   Callback<DriverLeaderBoard> callback);

	@FormUrlEncoded
	@POST("/get_information")
	void gethelp(@FieldMap Map<String, String> params,
				 Callback<BookingHistoryResponse> callback);

	@FormUrlEncoded
	@POST("/get_information")
	void getHelpSection(@FieldMap Map<String, String> params,
						Callback<BookingHistoryResponse> callback);

	@FormUrlEncoded
	@POST("/get_information")
	void getHelpSectionNew(@FieldMap Map<String, String> params,
						Callback<BookingHistoryResponse> callback);

	@FormUrlEncoded
	@POST("/send_otp_via_call")
	void initiateOTPCall(@FieldMap Map<String, String> params,
						 Callback<BookingHistoryResponse> callback);

	@FormUrlEncoded
	@POST("/verify_otp_for_driver")
	void verifyOtpUsingSignupFields(@FieldMap Map<String, String> params,
									Callback<BookingHistoryResponse> callback);


	@FormUrlEncoded
	@POST("/verify_otp")
	void verifyOtpOldUsingSignupFields(@FieldMap Map<String, String> params,
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
	Response getUserStatusRetro(@FieldMap Map<String, String> params);

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
	void driverProfileInfo(@FieldMap Map<String, String> params,
						   Callback<BookingHistoryResponse> callback);

	@FormUrlEncoded
	@POST("/daily_online_hours")
	void dailyOnlineHours(@Field("access_token") String accessToken,
						  Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/heat_map_data")
	void getHeatMapAsync(@FieldMap Map<String, String> params,
						 Callback<HeatMapResponse> callback);

	@FormUrlEncoded
	@POST("/update_user_profile")
	void updateUserProfileAPIRetroo(@FieldMap Map<String, String> params,
									Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/driver/show/leader_board")
	void leaderboardServerCall(@FieldMap Map<String, String> params,
							   Callback<NewLeaderBoard> callback);

	@FormUrlEncoded
	@POST("/driver/referrals/get_activity")
	void leaderboardActivityServerCall(@FieldMap Map<String, String> params,
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
	@POST("/v2/generate_login_otp")
	void generateOtpK(@FieldMap Map<String, String> params,
							 Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/fetch_media_data")
	NotificationAlarmResponse updateNotificationData(@FieldMap Map<String, String> params);

	@FormUrlEncoded
	@POST("/fetch_driver_regions")
	void getDestinationData(@FieldMap Map<String, String> params,
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
	void invoiceHistory(@FieldMap Map<String, String> params,
						Callback<InvoiceHistoryResponse> callback);

	@FormUrlEncoded
	@POST("/get_invoice_details")
	void invoiceDetail(@FieldMap Map<String, String> params,
						Callback<InvoiceDetailResponse> callback);

	@FormUrlEncoded
	@POST("/v2/get_invoice_details\n")
	void invoiceDetailNew(@FieldMap Map<String, String> params,
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
	void earningDetails(@FieldMap Map<String, String> params,
									   Callback<EarningsDetailResponse> callback);

	@FormUrlEncoded
	@POST("/v2/get_driver_earnings")
	void earningNewDetails(@FieldMap Map<String, String> params,
						Callback<DriverEarningsResponse> callback);

	@FormUrlEncoded
	@POST("/get_captive_driver_earnings")
	void earningNewDetailsCaptive(@FieldMap Map<String, String> params,
						Callback<DriverEarningsResponse> callback);

	@FormUrlEncoded
	@POST("/delivery_details")
	void deliveryDetails(@FieldMap Map<String, String> params,
						Callback<DeliveryDetailResponse> callback);

	@FormUrlEncoded
	@POST("/fetch_pushes_for_user")
	void notificationInbox(@FieldMap Map<String, String> params,
						   Callback<NotificationInboxResponse> callback);

	@FormUrlEncoded
	@POST("/fetch_active_locales")
	void fetchLanguageList(@FieldMap Map<String, String> params,
					 Callback<DriverLanguageResponse> callback);

	@FormUrlEncoded
	@POST("/fetch_active_locales")
	void fetchLanguageListKotlin(@FieldMap Map<String, String> params,
					 Callback<DriverLanguageResponse> callback);
	@FormUrlEncoded
	@POST("/driver/self_assign")
	void requestManualRide(@FieldMap Map<String, String> params,
					 Callback<ManualRideResponse> callback);

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
	void rateCardDetail(@FieldMap Map<String, String> params,
					   Callback<RateCardResponse> callback);

	@FormUrlEncoded
	@POST("/fetch_required_docs")
	void docRequest(@FieldMap Map<String, String> params,
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
	void getCities(@FieldMap Map<String, String> params,
					  @Field("password") String password,
					  Callback<CityResponse> callback);
	@FormUrlEncoded
	@POST("/get_all_cities")
	void getCityRetro(@FieldMap Map<String, String> params,
					  @Field("password") String password,
					  Callback<product.clicklabs.jugnoo.driver.retrofit.model.CityResponse> callback);

	@FormUrlEncoded
	@POST("/get_driver_signup_details")
	void getDriverSignUpDetails(@FieldMap Map<String, String> params,
					  Callback<CityResponse> callback);

	@FormUrlEncoded
	@POST("/upload_ring_count_data")
	void sendRingCountData(@FieldMap Map<String, String> params,
						   Callback<RegisterScreenResponse> callback);


	@FormUrlEncoded
	@POST("/verify_document_status")
	void docSubmission(@FieldMap Map<String, String> params,
					Callback<DocRequirementResponse> callback);

	@FormUrlEncoded
	@POST("/register_a_driver")
	void oneTimeRegisteration(@FieldMap Map<String, String> params,
							Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/show_tiles")
	void getInfoTilesAsync(@FieldMap Map<String, String> params,
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
	void sendIssue(@FieldMap Map<String, String> params,
					Callback<DocRequirementResponse> callback);

	@FormUrlEncoded
	@POST("/update_drop_latlng")
	void updateDropLatLng(@FieldMap Map<String, String> params,
						   Callback<InfoTileResponse> callback);

	@FormUrlEncoded
	@POST("/get_simulated_data")
	void getTourData(@FieldMap Map<String, String> params,
						  Callback<TourResponseModel> callback);

	@FormUrlEncoded
	@POST("/update_simulation_status")
	Response updateDriverStatus(@FieldMap Map<String, String> params);

	@FormUrlEncoded
	@POST("/update_simulation_status")
	void updateDriverStatus(@FieldMap Map<String, String> params,
					 Callback<UpdateTourStatusModel> callback);

	@FormUrlEncoded
	@POST("/fetch_driver_tickets")
	void getDriverTicketsAsync(@FieldMap Map<String, String> params,
							Callback<TicketResponse> callback);

	@FormUrlEncoded
	@POST("/show_delivery_rate_card")
	void deliveryRateCardDetail(@FieldMap Map<String, String> params,
						Callback<DeliveryRateCardResponse> callback);

	@FormUrlEncoded
	@POST("/get_transaction_history")
	void getUserTransaction(@FieldMap Map<String, String> params,
						  Callback<WalletTransactionResponse> callback);

	@FormUrlEncoded
	@POST("/schedule_call_driver")
	void scheduleCallDriver(@FieldMap Map<String, String> params,
						  Callback<SettleUserDebt> callback);

	@FormUrlEncoded
	@POST("/fetch_driver_plans")
	void fetchDriverPlans(@FieldMap Map<String, String> params,
						  Callback<FetchDriverPlansResponse> callback);

		@FormUrlEncoded
	@POST("/initiate_plan_subscription")
	void initiatePlanSubscription(@FieldMap Map<String, String> params,
						  Callback<InitiatePaymentResponse> callback);

	@FormUrlEncoded
	@POST("/set_bid_for_engagement")
	void setBidForEngagement(@FieldMap Map<String, String> params,
							 Callback<SettleUserDebt> callback);

	@FormUrlEncoded
	@POST("/set_agreement_status")
	void agreeTerms(@FieldMap Map<String, String> params,
							 Callback<SettleUserDebt> callback);

    @FormUrlEncoded
    @POST("/update_driver_info")
    void updateDriverInfo(@FieldMap Map<String, String> params,
                              Callback<RegisterScreenResponse> callback);

	@FormUrlEncoded
	@POST("/driver/send_credits")
	void sendCredits(@FieldMap Map<String, String> params,
							 Callback<SettleUserDebt> callback);

	@FormUrlEncoded
	@POST("/driver/credit_history")
	void creditHistory(@FieldMap Map<String, String> params,
					   Callback<DriverCreditResponse> callback);

	@FormUrlEncoded
	@POST("/driver/fetch_stripe_login_link")
	void fetchStripeLink(@FieldMap Map<String,String> params,Callback<StripeLoginResponse> callback);


	@FormUrlEncoded
	@POST("/enter_code")
	void applyPromo(@FieldMap Map<String, String> params,
						  Callback<FeedCommonResponseKotlin> callback);


	@FormUrlEncoded
	@POST("/driver/add_stripe_card")
	void addCardToDriver(@FieldMap Map<String, String> params,
						 Callback<StripeCardResponse> callback);
	@FormUrlEncoded
	@POST("/driver/fetch_wallet_balance")
	void fetchWalletBalance(@FieldMap Map<String, String> params,
							Callback<WalletModelResponse> callback);

	@FormUrlEncoded
	@POST("/driver/add_money_via_stripe")
	void addMoneyViaStripe(@FieldMap Map<String, String> params,
						   Callback<WalletModelResponse> callback);



}

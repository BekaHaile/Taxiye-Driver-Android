package product.clicklabs.jugnoo.driver.retrofit;

import java.util.Map;

import product.clicklabs.jugnoo.driver.datastructure.EndStopResponse;
import product.clicklabs.jugnoo.driver.datastructure.FetchDriverPlansResponse;
import product.clicklabs.jugnoo.driver.datastructure.InitiatePaymentResponse;
import product.clicklabs.jugnoo.driver.datastructure.WalletTransactionResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.AuditStateResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.AuditTypeResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.BookingHistoryResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.DailyEarningResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.DeliveryDetailResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.DeliveryRateCardResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.DestinationDataResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.DocRequirementResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.DriverCreditResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.DriverEarningsResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.DriverLeaderBoard;
import product.clicklabs.jugnoo.driver.retrofit.model.DriverSubscriptionResponse;
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
import product.clicklabs.jugnoo.driver.retrofit.model.ReferralsInfoResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.driver.retrofit.model.SharedRideResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.TicketResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.TollDataResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.TractionResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.drivertaks.DriverTasks;
import product.clicklabs.jugnoo.driver.stripe.model.StripeCardResponse;
import product.clicklabs.jugnoo.driver.stripe.model.WalletModelResponse;
import product.clicklabs.jugnoo.driver.tutorial.TourResponseModel;
import product.clicklabs.jugnoo.driver.tutorial.UpdateTourStatusModel;
import product.clicklabs.jugnoo.driver.ui.models.CityResponse;
import product.clicklabs.jugnoo.driver.ui.models.DriverLanguageResponse;
import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponse;
import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponseKotlin;
import product.clicklabs.jugnoo.driver.ui.models.ManualRideResponse;
import product.clicklabs.jugnoo.driver.ui.models.ProgramModel;
import product.clicklabs.jugnoo.driver.ui.models.VehicleDetailsResponse;
import product.clicklabs.jugnoo.driver.ui.models.VehicleModelCustomisationsResponse;
import product.clicklabs.jugnoo.driver.ui.popups.DriverVehicleServiceTypePopup;
import product.clicklabs.jugnoo.driver.wallet.model.CbeBirrCashoutResponse;
import product.clicklabs.jugnoo.driver.wallet.model.HelloCashCashoutResponse;
import product.clicklabs.jugnoo.driver.wallet.model.ResponseModel;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.PartMap;
import retrofit.http.QueryMap;
import retrofit.mime.MultipartTypedOutput;
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


	@GET("/get_all_subscriptions")
	void driverSubscriptionInfo(@QueryMap Map<String, String> params,
						   Callback<DriverSubscriptionResponse> callback);

    @FormUrlEncoded
    @POST("/driver_purchase_subscriptions")
    void purchaseSubscriptions(@FieldMap Map<String, String> params,
                          Callback<RegisterScreenResponse> callback);

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
	@POST("/toggle_delivery_for_driver")
	void enableDelivery(@FieldMap Map<String, String> params,
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

	@FormUrlEncoded
	@POST("/upload_document")
	void uploadFields(@FieldMap Map<String, String> params,
					Callback<FeedCommonResponseKotlin> callback);


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
	@POST("/driver/credit_history")
	void creditHistoryWallet(@FieldMap Map<String, String> params,
					   Callback<WalletTransactionResponse> callback);

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

    @FormUrlEncoded
    @POST("/get_vehicle_make_details")
    void getVehicleMakeDetails(@FieldMap Map<String, String> params,
                               Callback<VehicleDetailsResponse> callback);
    @FormUrlEncoded
    @POST("/get_vehicle_make_custom_details")
    void getVehicleModelDetails(@FieldMap Map<String, String> params,
                                Callback<VehicleModelCustomisationsResponse> callback);

	@FormUrlEncoded
	@POST("/update_luggage_count")
	void updateLuggageCount(@FieldMap Map<String, String> params,
							Callback<FeedCommonResponseKotlin> callback);

	@FormUrlEncoded
	@POST("/update_driver_fares")
	void updateDriverFares(@FieldMap Map<String, String> params,
							Callback<FeedCommonResponseKotlin> callback);

	@POST("/update_driver_vehicle_sets")
	void updateDriverVehicleServices(@Body DriverVehicleServiceTypePopup.ServiceDetailModel list,
									 Callback<DriverVehicleServiceTypePopup.UpdateVehicleSetResponse> callback);


	@FormUrlEncoded
	@POST("/log_google_api_hits")
	Response logGoogleApiHits(@FieldMap Map<String, String> params);
	@FormUrlEncoded
	@POST("/log_google_api_hits")
	void logGoogleApiHitsC(@FieldMap Map<String, String> params, Callback<SettleUserDebt> callback);

	@GET("/get_toll_data")
	void getTollData(@QueryMap Map<String, String> params, Callback<TollDataResponse> callback);

	@GET("/get_driver_subscriptions")
	void getDriverSubscriptionData(@QueryMap Map<String, String> params, Callback<DriverSubscriptionResponse> callback);

	@FormUrlEncoded
	@POST("/update_toll_data")
	void updateTollData(@FieldMap Map<String, String> params, Callback<FeedCommonResponseKotlin> callback);


	@FormUrlEncoded
	@POST("/emergency/contacts/add_multiple")
	void emergencyContactsAddMultiple(@FieldMap Map<String, String> params,
									  Callback<SettleUserDebt> callback);


	@FormUrlEncoded
	@POST("/emergency/contacts/delete")
	void emergencyContactsDelete(@FieldMap Map<String, String> params,
								 Callback<SettleUserDebt> callback);

	@FormUrlEncoded
	@POST("/emergency/send_ride_status_message")
	void emergencySendRideStatusMessage(@FieldMap Map<String, String> params,
										Callback<SettleUserDebt> callback);


	@GET("/emergency/contacts/list")
	void emergencyContactsList(@QueryMap Map<String, String> params,
							   Callback<SettleUserDebt> callback);


	@FormUrlEncoded
	@POST("/emergency/alert")
	void emergencyAlert(@FieldMap Map<String, String> params,
						Callback<SettleUserDebt> callback);
	@FormUrlEncoded
	@POST("/fetch_incentive_data")
	void fetchPrograms(@FieldMap Map<String, String> params,
							   Callback<ProgramModel> callback);


	@FormUrlEncoded
	@POST("/emergency/disable")
	void emergencyDisable(@FieldMap Map<String, String> params,
						  Callback<SettleUserDebt> callback);

	@FormUrlEncoded
	@POST("/emergency/alert")
	Response emergencyAlertSync(@FieldMap Map<String, String> params);

	@FormUrlEncoded
	@POST("/fetch_driver_tasks")
	void fetchDriverTask(@FieldMap Map<String, String> params, Callback<DriverTasks> callback);


	@FormUrlEncoded
	@POST("/send_email_invoice")
	void sendEmailInvoice(@FieldMap Map<String, String> params, Callback<FeedCommonResponse> callback);

	@FormUrlEncoded
	@POST("/fetch_driver_referral_info")
	void fetchDriverReferral(@FieldMap Map<String, String> params, Callback<ReferralsInfoResponse> callback);

	@FormUrlEncoded
	@POST("/fetch_driver_traction_rides")
	void fetchDriverTractionRides(@FieldMap Map<String, String> params, Callback<TractionResponse> callback);

	@FormUrlEncoded
	@POST("/get_information")
	Response fetchTutorialData(@FieldMap Map<String, String> params);

	@FormUrlEncoded
	@POST("/v2/add_home_and_work_address")
	void addHomeAndWorkAddress(@FieldMap Map<String, String> params,
							   Callback<Object> callback);
	@FormUrlEncoded
	@POST("/toggle_driver_destination")
	void toggleDriverDest(@FieldMap Map<String, String> params,
							   Callback<Object> callback);

    @GET("/driver/fetch_driver_vehicles")
    void fetchDriverVehicles(@QueryMap Map<String, String> params, Callback<Object> callback);

    @FormUrlEncoded
    @POST("/driver/add_new_vehicle")
    void addNewVehicle(@FieldMap Map<String, String> params, Callback<Object> callback);

    @FormUrlEncoded
    @POST("/driver/remove_vehicle")
    void removeVehicle(@FieldMap Map<String, String> params, Callback<Object> callback);

    @GET("/driver/fetch_driver_vehicle_document")
    void fetchDriverVehicleDocuments(@QueryMap Map<String, String> params, Callback<DocRequirementResponse> callback);

	@FormUrlEncoded
	@POST("/update_driver_property")
	void updateDriverProperty(@FieldMap Map<String, String> params, Callback<FeedCommonResponseKotlin> callback);

	@FormUrlEncoded
	@POST("/paytm/request_otp")
	void paytmRequestOtp(@FieldMap Map<String, String> params,
						 Callback<FeedCommonResponseKotlin> callback);

	@FormUrlEncoded
	@POST("/paytm/login_with_otp")
	void paytmLoginWithOtp(@FieldMap Map<String, String> params,
						   Callback<FeedCommonResponseKotlin> callback);
//	@FormUrlEncoded
//	@POST("/driver_purchase_subscriptions")
//	void purchaseSubscriptions(@FieldMap Map<String, String> params,
//						   Callback<FeedCommonResponseKotlin> callback);

	@FormUrlEncoded
	@POST("/paytm/delete_paytm")
	void paytmDeletePaytm(@FieldMap Map<String, String> params,
						  Callback<FeedCommonResponse> callback);

	@FormUrlEncoded
	@POST("/update_driver_property")
	void updateDriverPropertyJava(@FieldMap Map<String, String> params, Callback<FeedCommonResponse> callback);

	@POST("/update_driver_vehicle_sets")
	void updateDriverVehicleServicesJava(@Body DriverVehicleServiceTypePopup.ServiceDetailModel list,
									 Callback<DriverVehicleServiceTypePopup.UpdateVehicleSetResponse> callback);
	@FormUrlEncoded
	@POST("/paytm/login_with_otp")
	void paytmLoginWithOtpJava(@FieldMap Map<String, String> params,
						   Callback<FeedCommonResponse> callback);

	@FormUrlEncoded
	@POST("/driver/set_gps_preference")
	void updateGpsPreference(@FieldMap Map<String, String> params, Callback<FeedCommonResponseKotlin> callback);
	@POST("/end_stop")
	void endStop(@FieldMap Map<String,String> params, Callback<EndStopResponse> callback);

	@FormUrlEncoded
	@POST("/send_otp_via_call")
	void sendOtpViaCall(@FieldMap Map<String, String> params,
						Callback<FeedCommonResponseKotlin> callback);

	@POST("/delivery/upload_images")
    void uploadImagesRide(@Body MultipartTypedOutput params,
						  Callback<FeedCommonResponseKotlin> callback);

	@FormUrlEncoded
	@POST("/deposit/request")
	void cbeCashOut(@FieldMap Map<String, String> params,
					Callback<ResponseModel<CbeBirrCashoutResponse>> callback);

	@FormUrlEncoded
	@POST("/deposit/request")
	void cbeTopUp(@FieldMap Map<String, String> params,
					Callback<ResponseModel<CbeBirrCashoutResponse>> callback);

	@FormUrlEncoded
	@POST("/deposit/request")
	void mpesaCashOut(@FieldMap Map<String, String> params,
					Callback<CbeBirrCashoutResponse> callback);

	@FormUrlEncoded
	@POST("/deposit/request")
	void mpesaTopUp(@FieldMap Map<String, String> params,
					Callback<CbeBirrCashoutResponse> callback);

	@FormUrlEncoded
	@POST("/deposit/request")
	void helloCashTopUp(@FieldMap Map<String, String> params,
				  Callback<ResponseModel<HelloCashCashoutResponse>> callback);

	@FormUrlEncoded
	@POST("/cashout/request")
	void helloCashCashout(@FieldMap Map<String, String> params,
					Callback<ResponseModel<HelloCashCashoutResponse>> callback);

}

package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Pair;

import com.fugu.CaptureUserData;
import com.fugu.HippoNotificationConfig;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.driver.apis.ApiAcceptRide;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.CancelOption;
import product.clicklabs.jugnoo.driver.datastructure.CouponInfo;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.datastructure.DriverScreenMode;
import product.clicklabs.jugnoo.driver.datastructure.EndRideData;
import product.clicklabs.jugnoo.driver.datastructure.EngagementStatus;
import product.clicklabs.jugnoo.driver.datastructure.FareStructure;
import product.clicklabs.jugnoo.driver.datastructure.PaymentMode;
import product.clicklabs.jugnoo.driver.datastructure.PoolFare;
import product.clicklabs.jugnoo.driver.datastructure.PreviousAccountInfo;
import product.clicklabs.jugnoo.driver.datastructure.PromoInfo;
import product.clicklabs.jugnoo.driver.datastructure.ReverseBidFare;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.datastructure.UserData;
import product.clicklabs.jugnoo.driver.datastructure.UserMode;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryInfo;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryReturnOption;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.support.SupportOption;
import product.clicklabs.jugnoo.driver.utils.AuthKeySaver;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class JSONParser implements Constants {


	public JSONParser() {

	}

	public static String getServerMessage(JSONObject jObj) {
		String message = Data.SERVER_ERROR_MSG;
		try {
			if (jObj.has("message")) {
				message = jObj.getString("message");
			} else if (jObj.has("log")) {
				message = jObj.getString("log");
			} else if (jObj.has("error")) {
				message = jObj.getString("error");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return message;
	}


	public static void saveAccessToken(Context context, String accessToken) {
		AuthKeySaver.writeAuthToFile(accessToken);
		SharedPreferences pref = context.getSharedPreferences(Data.SHARED_PREF_NAME, Context.MODE_PRIVATE);
		Editor editor = pref.edit();
		editor.putString(Data.SP_ACCESS_TOKEN_KEY, accessToken);
		editor.apply();
	}

	public static Pair<String, String> getAccessTokenPair(Context context) {
		SharedPreferences pref = context.getSharedPreferences(Data.SHARED_PREF_NAME, Context.MODE_PRIVATE);
		String accessToken = pref.getString(Data.SP_ACCESS_TOKEN_KEY, "");
		String isAccessTokenNew = "1";

		if(TextUtils.isEmpty(accessToken)){
			accessToken = AuthKeySaver.readAuthFromFile();
		}

		return new Pair<String, String>(accessToken, isAccessTokenNew);
	}


	public static FareStructure parseFareObject(JSONObject jObj) {
		try {
			JSONObject fareDetails = jObj.getJSONObject("fare_details");
			if(fareDetails.has("mandatory_fare_details")){
				JSONObject mandatoryFareDetails = fareDetails.getJSONObject("mandatory_fare_details");
				return new FareStructure(fareDetails.getDouble("fare_fixed"),
						fareDetails.getDouble("fare_threshold_distance"),
						fareDetails.getDouble("fare_per_km"),
						fareDetails.getDouble("fare_per_min"),
						fareDetails.getDouble("fare_threshold_time"),
						fareDetails.getDouble("fare_per_waiting_min"),
						fareDetails.getDouble("fare_threshold_waiting_time"),
						fareDetails.getDouble("fare_per_km_threshold_distance"),
						fareDetails.getDouble("fare_per_km_after_threshold"),
						fareDetails.getDouble("fare_per_km_before_threshold"),
						fareDetails.getDouble("fare_minimum"),
						mandatoryFareDetails.getDouble("mandatory_fare_value"),
						mandatoryFareDetails.getDouble("mandatory_fare_capping"));
			} else {
				return new FareStructure(fareDetails.getDouble("fare_fixed"),
						fareDetails.getDouble("fare_threshold_distance"),
						fareDetails.getDouble("fare_per_km"),
						fareDetails.getDouble("fare_per_min"),
						fareDetails.getDouble("fare_threshold_time"),
						fareDetails.getDouble("fare_per_waiting_min"),
						fareDetails.getDouble("fare_threshold_waiting_time"),
						fareDetails.getDouble("fare_per_km_threshold_distance"),
						fareDetails.getDouble("fare_per_km_after_threshold"),
						fareDetails.getDouble("fare_per_km_before_threshold"),
						fareDetails.getDouble("fare_minimum"),0,0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new FareStructure(10, 0, 5, 1, 0, 0, 0, 0, 5, 0, 40, 0, 0);
		}
	}


	public static CouponInfo parseCouponInfo(JSONObject jObj) {
		try {
			JSONObject couponObject = jObj.getJSONObject("coupon");
			CouponInfo couponInfo = new CouponInfo(couponObject.getString("title"),
					couponObject.getString("subtitle"),
					couponObject.getString("description"),
					couponObject.getDouble("discount_percentage"),
					couponObject.getDouble("discount_maximum"),
					couponObject.getDouble("capped_fare"),
					couponObject.getDouble("capped_fare_maximum"),
					couponObject.getInt("coupon_type"),
					couponObject.getInt("benefit_type"),
					couponObject.getDouble("drop_latitude"),
					couponObject.getDouble("drop_longitude"),
					couponObject.getDouble("drop_radius")
			);
			return couponInfo;
		} catch (Exception e) {
			return null;
		}
	}

	public static PromoInfo parsePromoInfo(JSONObject jObj) {
		try {
			JSONObject jPromoObject = jObj.getJSONObject("promotion");
			PromoInfo promoInfo = new PromoInfo(jPromoObject.getString("title"),
					jPromoObject.getInt("promo_type"),
					jPromoObject.getInt("benefit_type"),
					jPromoObject.getDouble("discount_percentage"),
					jPromoObject.getDouble("discount_maximum"),
					jPromoObject.getDouble("capped_fare"),
					jPromoObject.getDouble("capped_fare_maximum"),
					jPromoObject.getDouble("cashback_percentage"),
					jPromoObject.getDouble("drop_latitude"),
					jPromoObject.getDouble("drop_longitude"),
					jPromoObject.getDouble("drop_radius"));

			return promoInfo;
		} catch (Exception e) {
			Log.w("promoInfo", "e=" + e.toString());
			return null;
		}
	}


	public UserData parseUserData(Context context, JSONObject userData) throws Exception {


		int freeRideIconDisable = 1;

		int autosEnabled = 1, mealsEnabled = 0, fatafatEnabled = 0;
		int autosAvailable = 1, mealsAvailable = 0, fatafatAvailable = 0;
		Integer fareCachingLimit= 0,isCaptiveDriver = 0;

		if (userData.has("free_ride_icon_disable")) {
			freeRideIconDisable = userData.getInt("free_ride_icon_disable");
		}

		if (userData.has("autos_enabled")) {
			autosEnabled = userData.getInt("autos_enabled");
		}
		if(userData.has("fare_caching_limit")){
			fareCachingLimit = userData.getInt("fare_caching_limit");
		}

		if(userData.has("is_captive_driver")){
			isCaptiveDriver = userData.getInt("is_captive_driver");
		}
		if (userData.has("meals_enabled")) {
			mealsEnabled = userData.getInt("meals_enabled");
		}
		if (userData.has("fatafat_enabled")) {
			fatafatEnabled = userData.getInt("fatafat_enabled");
		}

		if (userData.has("autos_available")) {
			autosAvailable = userData.getInt("autos_available");
		}
		if (userData.has("meals_available")) {
			mealsAvailable = userData.getInt("meals_available");
		}
		if (userData.has("fatafat_available")) {
			fatafatAvailable = userData.getInt("fatafat_available");
		}

		if (1 != autosEnabled) {
			autosAvailable = 0;
		}
		if (1 != mealsEnabled) {
			mealsAvailable = 0;
		}
		if (1 != fatafatEnabled) {
			fatafatAvailable = 0;
		}

		int sharingEnabled = 0, sharingAvailable = 0;
		if (userData.has("sharing_enabled")) {
			sharingEnabled = userData.getInt("sharing_enabled");
		}
		if (userData.has("sharing_available")) {
			sharingAvailable = userData.getInt("sharing_available");
		}
		if (1 != sharingEnabled) {
			sharingAvailable = 0;
		}

		int deliveryEnabled = userData.optInt(KEY_DELIVERY_ENABLED, 0);
		int deliveryAvailable = deliveryEnabled == 0 ? 0 : userData.optInt(KEY_DELIVERY_AVAILABLE, 0);


		try {
			if (userData.has("gcm_intent")) {
				Database2.getInstance(context).updateDriverGcmIntent(userData.getInt("gcm_intent"));
			}
		} catch (Exception e) {
		}

		String customerReferralBonus = userData.optString("customer_referral_bonus", "30");

		String deiValue = userData.optString("driver_dei", "-1");

		String accessToken = userData.getString("access_token");
		double showDriverRating = userData.optDouble("showDriverRating");

		String driverSupportNumber = userData.optString("driver_support_number", context.getString(R.string.support_phone_number));
		String driverSupportEmail = userData.optString(Constants.DRIVER_SUPPORT_EMAIL, context.getString(R.string.support_email));
		String driverSupportEmailSubject = userData.optString(Constants.DRIVER_SUPPORT_EMAIL_SUBJECT, context.getString(R.string.support_email_subject));
		String hippoTicketFAQ = userData.optString(Constants.HIPPO_TICKET_FAQ_NAME, context.getString(R.string.hippo_support_faq_name_default));
		String referralCode = userData.getString("referral_code");


		String referralSMSToCustomer = userData.optString("referral_sms_to_customer",
		context.getResources().getString(R.string.referal_sms_message,referralCode,context.getResources().getString(R.string.customer_app_download_link)));
		String referralMessage = userData.optString(Constants.KEY_REFERRAL_MESSAGE);
		String referralMessageDriver = userData.optString(Constants.KEY_REFERRAL_MESSAGE_DRIVER);
		String referralButtonText = userData.optString("referral_button_text", "Share");
		String referralDialogText = userData.optString("referral_dialog_text", "Enter Phone No.");
		String referralDialogHintText = userData.optString("referral_dialog_hint_text", "Phone No.");
		String referralImageD2D = userData.optString(Constants.KEY_REFERRAL_IMAGE_D2D);
		String referralImageD2C = userData.optString(Constants.KEY_REFERRAL_IMAGE_D2C);
		String getCreditsInfo = userData.optString(Constants.KEY_GET_CREDITS_INFO);
		String getCreditsImage = userData.optString(Constants.KEY_GET_CREDITS_IMAGE);
		int sendCreditsEnabled = userData.optInt(Constants.KEY_SEND_CREDITS_ENABLED, 0);

		Prefs.with(context).save(SPLabels.RING_COUNT_FREQUENCY, userData.optLong("ring_count_frequency", 0));
		Prefs.with(context).save(SPLabels.MAX_INGNORE_RIDEREQUEST_COUNT, userData.optInt("max_allowed_timeouts", 0));
		Prefs.with(context).save(SPLabels.MAX_TIMEOUT_RELIEF, userData.optLong("timeout_relief", 30000));
		Prefs.with(context).save(SPLabels.BUFFER_TIMEOUT_PERIOD, userData.optLong("timeout_counter_buffer", 120000));
		Prefs.with(context).save(SPLabels.DRIVER_TIMEOUT_FLAG, userData.optInt("penalise_driver_timeout", 0));
		Prefs.with(context).save(SPLabels.DRIVER_TIMEOUT_FACTOR, userData.optInt("customer_cancel_timeout_factor", 1));
		Prefs.with(context).save(SPLabels.DRIVER_TIMEOUT_FACTOR_HIGH, userData.optInt("driver_cancel_timeout_factor", 2));
		Prefs.with(context).save(SPLabels.DRIVER_TIMEOUT_TTL, userData.optLong("timeout_ttl", 86400000));
		Prefs.with(context).save(SPLabels.NOTIFICATION_SAVE_COUNT, userData.optInt("push_upload_count", 0));
		Prefs.with(context).save(SPLabels.HEAT_MAP_REFRESH_FREQUENCY, userData.optLong("heatmap_refresh_frequency", 300000));
		Prefs.with(context).save(SPLabels.PUBNUB_PUBLISHER_KEY, userData.optString("pubnub_publish_key", ""));
		Prefs.with(context).save(SPLabels.PUBNUB_SUSCRIBER_KEY, userData.optString("pubnub_subscribe_key", ""));
		Prefs.with(context).save(SPLabels.PUBNUB_CHANNEL, userData.optString("pubnub_channel", ""));
		Prefs.with(context).save(SPLabels.OSRM_ENABLED, userData.optInt("driver_app_osrm_enabled", 0));
		Prefs.with(context).save(SPLabels.SHOW_SUPPORT_IN_RESOURCES, userData.optInt("show_support_in_resources", 0));
		Prefs.with(context).save(SPLabels.MENU_OPTION_VISIBILITY, userData.optInt("menu_option_visibility", 0));
		Prefs.with(context).save(SPLabels.VEHICLE_TYPE, userData.optInt("vehicle_type", 0));

		Prefs.with(context).save(SPLabels.SET_TRAINING_ID, userData.optInt("driver_training_id", 0));
		Prefs.with(context).save(SPLabels.SET_AUDIT_STATUS_POPUP, userData.optInt("self_audit_popup_status", 0));
		Prefs.with(context).save(SPLabels.DIGITAL_SIGNATURE_POPUP_STATUS, userData.optInt("digital_signature_popup_status", 0));
		Prefs.with(context).save(SPLabels.SET_AUDIT_POPUP_STRING, userData.optString("self_audit_popup_message", ""));
		Prefs.with(context).save(SPLabels.SET_DIGITAL_SIGNATURE_POPUP_STRING, userData.optString("set_digital_signature_popup_string", ""));
//		Prefs.with(context).save(SPLabels.CHAT_ENABLED, userData.optInt("chat_enabled", 1));
		Prefs.with(context).save(KEY_SP_METER_DISP_MIN_THRESHOLD, String.valueOf(userData.optInt("meter_disp_min_threshold", 14)));
		Prefs.with(context).save(KEY_SP_METER_DISP_MAX_THRESHOLD, String.valueOf(userData.optInt("meter_disp_max_threshold", 200)));
		Prefs.with(context).save(SPLabels.START_RIDE_ALERT_RADIUS, userData.optInt("start_ride_alert_radius", 200));
		Prefs.with(context).save(SPLabels.START_RIDE_ALERT_RADIUS_FINAL, userData.optInt("start_ride_alert_radius_final", 400));

		Prefs.with(context).save(Constants.FETCH_APP_API_ENABLED, userData.optInt("fetch_all_driver_app_status", 0));
		Prefs.with(context).save(Constants.FETCH_APP_API_FREQUENCY, userData.optLong("fetch_all_driver_app_frequency", 0));

		Prefs.with(context).save(Constants.START_NAVIGATION_ACCEPT, userData.optInt("start_navigation_accept", 3));
		Prefs.with(context).save(Constants.START_NAVIGATION_START, userData.optInt("start_navigation_start", 0));
		Prefs.with(context).save(Constants.HIGH_DEMAND_AREA_POPUP, userData.optString("high_demand_area_popup", ""));
		Prefs.with(context).save(Constants.HIGH_DEMAND_WEB_URL, userData.optString("high_demand_web_url", ""));

		Prefs.with(context).save(Constants.KEY_DRIVER_ARRIVED_DISTANCE, userData.optInt("driver_arrived_distance", 100));

		Prefs.with(context).save(Constants.FREE_STATE_UPDATE_TIME_PERIOD, userData.optLong("driver_free_state_update_time_period", 110000));
		Prefs.with(context).save(Constants.ACCEPTED_STATE_UPDATE_TIME_PERIOD, userData.optLong("driver_accepted_state_update_time_period", 12000));
		Prefs.with(context).save(Constants.SHOW_INVOICE_DETAILS, userData.optInt("show_invoice_details", 0));
		Prefs.with(context).save(Constants.FREE_STATE_UPDATE_TIME_PERIOD_CHARGING, userData.optLong("driver_free_state_update_time_period_charging", 10000));
		Prefs.with(context).save(Constants.FREE_STATE_UPDATE_TIME_PERIOD_CHARGING_V5, userData.optLong("driver_free_state_update_time_period_charging_v5", 10000));
		Prefs.with(context).save(Constants.FREE_STATE_UPDATE_TIME_PERIOD_NON_CHARGING, userData.optLong("driver_free_state_update_time_period", 110000));
		Prefs.with(context).save(Constants.DRIVER_OFFLINE_PERIOD, userData.optLong("driver_offline_period", 180000));

		Prefs.with(context).save(Constants.UPLOAD_DOCUMENT_MESSAGE, userData.optString("upload_document_message", ""));
		Prefs.with(context).save(Constants.UPLOAD_DOCUMENT_DAYS_LEFT, userData.optString("upload_document_days_left", ""));
		Prefs.with(context).save(Constants.AVERAGE_DRIVER_EARNING, userData.optInt("average_driver_earning", 0));
		Prefs.with(context).save(Constants.AVERAGE_EARNING_DAYS, userData.optInt("average_earning_days", 0));

		Prefs.with(context).save(Constants.KEY_CURRENCY, userData.optString(Constants.KEY_CURRENCY, "INR"));
		Prefs.with(context).save(Constants.DIFF_MAX_EARNING, userData.optInt("diff_max_earning", 0));
		Prefs.with(context).save(Constants.UPDATE_LOCATION_OFFLINE, userData.optInt("update_location_offline", 0));
		Prefs.with(context).save(Constants.OFFLINE_UPDATE_TIME_PERIOD, userData.optLong("offline_update_time_period", 180000));
//		Prefs.with(context).save(Constants.SHOW_AGREEMENT_SCREEN, userData.optInt("agreement_status", 1));

		Prefs.with(context).save(Constants.END_RIDE_CUSTOM_TEXT, userData.optString("end_ride_custom_text", ""));

		long remainigPenaltyPeriod = userData.optLong("remaining_penalty_period", 0);
		String timeoutMessage = userData.optString("timeout_message", "We have noticed that, you aren't taking "+ context.getString(R.string.white_label_name)+" rides. So we are blocking you for some time");
		Log.i("timeOut", timeoutMessage);
		int paytmRechargeEnabled = userData.optInt("paytm_recharge_enabled", 0);
		int destinationOptionEnable = userData.optInt("set_destination_option_enabled", 0);
		long walletUpdateTimeout = userData.optLong("end_ride_fetch_balance_timeout", 3000);
		saveAccessToken(context, accessToken);
		String blockedAppPackageMessage = userData.optString("blocked_app_package_message", "");


		double driverArrivalDistance = userData.optDouble("driver_arrival_distance", 100);

        int stripeAccountStatus = userData.optInt(Constants.STRIPE_ACCOUNT_STATUS, 0);
        Prefs.with(context).save(Constants.STRIPE_ACCOUNT_STATUS, stripeAccountStatus);
		Prefs.with(context).save(Constants.SHOW_NOTIFICATION_TIPS, userData.optInt("show_notification_tips", 0));
		Prefs.with(context).save(Constants.NOTIFICATION_TIPS_TEXT, userData.optString("notification_tips_text", "Tips To Earn"));
		Prefs.with(context).save(Constants.NOTIFICATION_MSG_TEXT, userData.optString("notification_message_text", "Messages"));
		Prefs.with(context).save(Constants.BID_INCREMENT_PERCENT, (float)userData.optDouble(Constants.BID_INCREMENT_PERCENT, 10d));
		Prefs.with(context).save(Constants.STRIPE_REDIRECT_URI, userData.optString("stripe_redirect_uri", ""));
		if (autosAvailable == 1
				|| mealsAvailable == 1
				|| fatafatAvailable == 1
				|| sharingAvailable == 1
				) {
			Database2.getInstance(context).updateDriverServiceRun(Database2.YES);
		} else {
			Database2.getInstance(context).updateDriverServiceRun(Database2.NO);
		}

		String userEmail = userData.optString("user_email", "");
		String phoneNo = userData.getString("phone_no");
		String userId = userData.optString(KEY_USER_ID, phoneNo);
		String currency = userData.optString(KEY_CURRENCY, "INR");
		String userIdentifier = userData.optString(KEY_USER_IDENTIFIER, "");
		String countryCode = "+"+userData.optString(Constants.KEY_COUNTRY_CODE, "91");
		Prefs.with(context).save(Constants.KEY_DISTANCE_UNIT, userData.optString(Constants.KEY_DISTANCE_UNIT, context.getString(R.string.km)));
		Prefs.with(context).save(Constants.KEY_DISTANCE_UNIT_FACTOR, (float) userData.optDouble(Constants.KEY_DISTANCE_UNIT_FACTOR, 1D));
		Prefs.with(context).save(SP_USER_ID, userId);
		Double creditsEarned = userData.has(Constants.KEY_CREDIT_BALANCE)?userData.optDouble(Constants.KEY_CREDIT_BALANCE):null;
		Double commissionSaved = userData.has(Constants.KEY_COMMISSION_SAVED)?userData.optDouble(Constants.KEY_COMMISSION_SAVED):null;
		parseSideMenu(context, userData);
		Prefs.with(context).save(Constants.KEY_NAVIGATION_TYPE, userData.optInt(Constants.KEY_NAVIGATION_TYPE, Constants.NAVIGATION_TYPE_GOOGLE_MAPS));

		return new UserData(accessToken, userData.getString("user_name"),
				userData.getString("user_image"), referralCode, phoneNo, freeRideIconDisable,
				autosEnabled, mealsEnabled, fatafatEnabled, autosAvailable, mealsAvailable, fatafatAvailable,
				deiValue, customerReferralBonus, sharingEnabled, sharingAvailable, driverSupportNumber,
				referralSMSToCustomer, showDriverRating, driverArrivalDistance, referralMessage,
				referralButtonText,referralDialogText, referralDialogHintText,remainigPenaltyPeriod,
				timeoutMessage, paytmRechargeEnabled, destinationOptionEnable, walletUpdateTimeout,
				userId, userEmail, blockedAppPackageMessage, deliveryEnabled, deliveryAvailable,fareCachingLimit,
				isCaptiveDriver, countryCode,userIdentifier, driverSupportEmail, driverSupportEmailSubject,
				hippoTicketFAQ, currency,creditsEarned,commissionSaved, referralMessageDriver,
				referralImageD2D, referralImageD2C, getCreditsInfo, getCreditsImage, sendCreditsEnabled);
	}

	public String parseAccessTokenLoginData(Context context, String response) throws Exception {


		Log.e("response ==", "=" + response);

		JSONObject jObj = new JSONObject(response);

		//Fetching login data
		JSONObject jLoginObject = jObj.getJSONObject("login");
		Prefs.with(context).save(Constants.KEY_DRIVER_SHOW_ARRIVE_UI_DISTANCE, jObj.optInt("driver_show_arrive_ui_distance", 600));
		Data.userData = parseUserData(context, jLoginObject);
		saveAccessToken(context, Data.userData.accessToken);
		Data.blockAppPackageNameList = jLoginObject.getJSONArray("block_app_package_name_list");


		//Fetching user current status
		JSONObject jUserStatusObject = jObj.getJSONObject("status");
		String resp = parseCurrentUserStatus(context, jUserStatusObject);

		parseCancellationReasons(jObj,context);
		Data.deliveryReturnOptionList = JSONParser.parseDeliveryReturnOptions(jObj);

		try {


			if(isChatSupportEnabled(context)){

				CaptureUserData captureUserData = Data.getFuguUserData(context);
				if(captureUserData!=null){
					HippoNotificationConfig.updateFcmRegistrationToken(FirebaseInstanceId.getInstance().getToken());
					Data.initFugu((Activity) context, captureUserData,
							jLoginObject.optString(Constants.KEY_FUGU_APP_KEY),
							jLoginObject.optInt(KEY_FUGU_APP_TYPE, 1));
				}

			}
			} catch (Exception e) {
			e.printStackTrace();
		}

		/*try {
			NudgeClient.initialize(context, Data.userData.getUserId(), Data.userData.userName,
					Data.userData.getUserEmail(), Data.userData.phoneNo);
		} catch (Exception e) {
			e.printStackTrace();
		}
*/
		return resp;
	}

	public String getUserStatus(Context context, String accessToken) {
		String returnResponse = "";
		try {
			HashMap<String, String> params = new HashMap<>();
			params.put(KEY_ACCESS_TOKEN, accessToken);
			HomeUtil.putDefaultParams(params);
			Response response = RestClient.getApiServices().getUserStatusRetro(params);
			String result = new String(((TypedByteArray) response.getBody()).getBytes());

			Log.e("result of = user_status", "=" + result);
			if (response == null || result == null) {
				returnResponse = Constants.SERVER_TIMEOUT;
				return returnResponse;
			} else {
				JSONObject jObject1 = new JSONObject(result);
				returnResponse = parseCurrentUserStatus(context, jObject1);
				return returnResponse;
			}
		} catch (Exception e) {
			e.printStackTrace();
			returnResponse = Constants.SERVER_TIMEOUT;
			return returnResponse;
		}
	}



	public String parseCurrentUserStatus(Context context, JSONObject jObject1) {
		HomeActivity.userMode = UserMode.DRIVER;
		try {
			if (jObject1.has(KEY_ERROR)) {
				return Constants.SERVER_TIMEOUT;
			} else {
				int flag = jObject1.getInt(KEY_FLAG);

				fillDriverRideRequests(jObject1, context);
				setPreferredLangString(jObject1, context);

				Data.clearAssignedCustomerInfosListForStatus(EngagementStatus.ACCEPTED.getOrdinal());
				Data.clearAssignedCustomerInfosListForStatus(EngagementStatus.ARRIVED.getOrdinal());
				Data.clearAssignedCustomerInfosListForStatus(EngagementStatus.STARTED.getOrdinal());
				Data.nextPickupLatLng = null;
				if (ApiResponseFlags.ENGAGEMENT_DATA.getOrdinal() == flag) {
					JSONArray lastEngInfoArr = jObject1.getJSONArray(KEY_LAST_ENGAGEMENT_INFO);
					for(int i=0; i<lastEngInfoArr.length(); i++) {
						JSONObject jObjCustomer = lastEngInfoArr.getJSONObject(i);

						parseFareStructureForCustomer(jObjCustomer);
						if(i == 0) {
							parsePerfectRideData(context, jObjCustomer);
						}

						int engagementStatus = jObjCustomer.getInt(KEY_STATUS);
						if ((EngagementStatus.ACCEPTED.getOrdinal() == engagementStatus) ||
								(EngagementStatus.STARTED.getOrdinal() == engagementStatus) ||
								(EngagementStatus.ARRIVED.getOrdinal() == engagementStatus)) {
							int referenceId = jObjCustomer.optInt(KEY_REFERENCE_ID, 0);
							String engagementId = jObjCustomer.getString(KEY_ENGAGEMENT_ID);
							int isDelivery = jObjCustomer.optInt(KEY_IS_DELIVERY, 0);
							int isDeliveryPool = 0;
							if(jObjCustomer.optInt(KEY_RIDE_TYPE,0)==4){
								isDeliveryPool =1;
							}

							String userId = "", userName = "", userImage = "", phoneNo = "", rating = "", address = "",
									vendorMessage = "", estimatedDriverFare = "";
							int forceEndDelivery =0, loadingStatus =0;
							double jugnooBalance = 0, pickupLatitude = 0, pickupLongitude = 0, estimatedFare = 0, cashOnDelivery=0,
									currrentLatitude =0, currrentLongitude =0;
							int totalDeliveries = 0, falseDeliveries = 0, orderId =0;
							if(isDelivery == 1){
								JSONObject userData = jObjCustomer.optJSONObject(KEY_USER_DATA);
								userId = userData.optString(KEY_USER_ID, "0");
								userName = userData.optString(KEY_NAME, "");
								userImage = userData.optString(KEY_USER_IMAGE, "");
								phoneNo = userData.optString(KEY_PHONE, "");
								rating = userData.optString(KEY_USER_RATING, "4");
								jugnooBalance = userData.optDouble(KEY_JUGNOO_BALANCE, 0);
								pickupLatitude = userData.optDouble(KEY_LATITUDE, 0);
								pickupLongitude = userData.optDouble(KEY_LONGITUDE, 0);
								address = userData.optString(KEY_ADDRESS, "");
								totalDeliveries = userData.optInt(Constants.KEY_TOTAL_DELIVERIES, 0);
								estimatedFare = userData.optDouble(Constants.KEY_ESTIMATED_FARE, 0d);
								vendorMessage = userData.optString(Constants.KEY_VENDOR_MESSAGE, "");
								forceEndDelivery = jObjCustomer.optInt(Constants.KEY_END_DELIVERY_FORCED, 0);
								cashOnDelivery = userData.optDouble(Constants.KEY_TOTAL_CASH_TO_COLLECT_DELIVERY, 0);
								estimatedDriverFare= userData.optString(KEY_ESTIMATED_DRIVER_FARE, "");
								falseDeliveries = userData.optInt("false_deliveries",0);
								orderId = userData.optInt("order_id",0);
								loadingStatus = userData.optInt(KEY_IS_LOADING, 0);

							} else {
								userId = jObjCustomer.optString(KEY_USER_ID, "0");
								userName = jObjCustomer.optString(KEY_USER_NAME, "");
								userImage = jObjCustomer.optString(KEY_USER_IMAGE, "");
								phoneNo = jObjCustomer.optString(KEY_PHONE_NO, "");
								rating = jObjCustomer.optString(KEY_USER_RATING, "4");
								jugnooBalance = jObjCustomer.optDouble(KEY_JUGNOO_BALANCE, 0);
								pickupLatitude = jObjCustomer.optDouble(KEY_PICKUP_LATITUDE, 0);
								pickupLongitude = jObjCustomer.optDouble(KEY_PICKUP_LONGITUDE, 0);
								address = jObjCustomer.optString(KEY_ADDRESS, "");
								currrentLatitude = jObjCustomer.getDouble(Constants.KEY_CURRENT_LATITUDE);
								currrentLongitude = jObjCustomer.getDouble(Constants.KEY_CURRENT_LONGITUDE);
								Prefs.with(context).save(Constants.KEY_CURRENT_LATITUDE_ALARM, String.valueOf(currrentLatitude));
								Prefs.with(context).save(Constants.KEY_CURRENT_LONGITUDE_ALARM, String.valueOf(currrentLongitude));
							}


							CouponInfo couponInfo = JSONParser.parseCouponInfo(jObjCustomer);
							PromoInfo promoInfo = JSONParser.parsePromoInfo(jObjCustomer);

							if (Prefs.with(context).getLong(SPLabels.CURRENT_ETA, 0) == 0) {
								Prefs.with(context).save(SPLabels.CURRENT_ETA, System.currentTimeMillis() + jObjCustomer.optLong("eta", 0));
							}

							Prefs.with(context).save(SPLabels.CHAT_ENABLED,jObjCustomer.optInt("chat_enabled",0));
							int meterFareApplicable = jObjCustomer.optInt("meter_fare_applicable", 0);
							int getJugnooFareEnabled = jObjCustomer.optInt("get_jugnoo_fare_enabled", 1);
							int luggageChargesApplicable = jObjCustomer.optInt("luggage_charges_applicable", 0);
							int waitingChargesApplicable = jObjCustomer.optInt("waiting_charges_applicable", 0);

							int cachedApiEnabled = jObjCustomer.optInt(KEY_CACHED_API_ENABLED, 0);
							int isPooled = jObjCustomer.optInt(KEY_IS_POOLED, 0);
							String currency = jObjCustomer.optString(Constants.KEY_CURRENCY);


							if(i == 0){
								Data.setCurrentEngagementId(engagementId);
							}

							CustomerInfo customerInfo = new CustomerInfo(Integer.parseInt(engagementId), Integer.parseInt(userId),
									referenceId, userName, phoneNo, new LatLng(pickupLatitude, pickupLongitude), cachedApiEnabled,
									userImage, rating, couponInfo, promoInfo, jugnooBalance, meterFareApplicable, getJugnooFareEnabled,
									luggageChargesApplicable, waitingChargesApplicable, engagementStatus, isPooled,
									isDelivery, isDeliveryPool, address, totalDeliveries, estimatedFare, vendorMessage, cashOnDelivery,
									new LatLng(currrentLatitude, currrentLongitude), forceEndDelivery, estimatedDriverFare, falseDeliveries,
									orderId, loadingStatus, currency);

							if(customerInfo.getIsDelivery() == 1){
								customerInfo.setDeliveryInfos(JSONParser.parseDeliveryInfos(jObjCustomer));
								parseReturnDeliveryInfos(jObjCustomer, customerInfo);
							}

							updateDropAddressLatlng(jObjCustomer, customerInfo);

							parsePoolOrReverseBidFare(jObjCustomer, customerInfo);

							Data.addCustomerInfo(customerInfo);


						}
					}

					try {
						MyApplication.getInstance().writePathLogToFile("accept", "JSONPARSER  = " + jObject1);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

				if (Data.nextPickupLatLng == null) {
					Prefs.with(context).save(SPLabels.PERFECT_ACCEPT_RIDE_DATA, " ");
					Prefs.with(context).save(SPLabels.PERFECT_CUSTOMER_CONT, "");
					new ApiAcceptRide().perfectRideVariables(context, "", "", "", 0, 0);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Constants.SERVER_TIMEOUT;
		}


		if (Data.getAssignedCustomerInfosListForEngagedStatus().size() == 0) {
			HomeActivity.driverScreenMode = DriverScreenMode.D_INITIAL;
			Prefs.with(context).save(SP_CUSTOMER_RIDE_DATAS_OBJECT, EMPTY_OBJECT);
		} else {
			HomeActivity.driverScreenMode = Data.getDriverScreenModeFromEngagementStatus(
					Data.getCurrentCustomerInfo().getStatus());
		}

		return "";
	}

	public static void updateDropAddressLatlng(JSONObject jObjCustomer, CustomerInfo customerInfo) {
		try {
			if (jObjCustomer.has(KEY_OP_DROP_LATITUDE) && jObjCustomer.has(KEY_OP_DROP_LONGITUDE)) {
				double dropLatitude = jObjCustomer.getDouble(KEY_OP_DROP_LATITUDE);
				double dropLongitude = jObjCustomer.getDouble(KEY_OP_DROP_LONGITUDE);
				if ((Utils.compareDouble(dropLatitude, 0) == 0) && (Utils.compareDouble(dropLongitude, 0) == 0)) {
					customerInfo.setDropLatLng(null);
				} else {
					customerInfo.setDropLatLng(new LatLng(dropLatitude, dropLongitude));
				}
			}
			if(jObjCustomer.has(KEY_DROP_ADDRESS)){
				customerInfo.setDropAddress(jObjCustomer.getString(KEY_DROP_ADDRESS));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void parsePerfectRideData(Context context, JSONObject jObjCustomer){
		try {
			if (jObjCustomer.has(KEY_PERFECT_PICKUP_LATITUDE) && jObjCustomer.has(KEY_PERFECT_PICKUP_LONGITUDE)) {
				double perfectPickupLatitude = jObjCustomer.getDouble(KEY_PERFECT_PICKUP_LATITUDE);
				double perfectPickupLongitude = jObjCustomer.getDouble(KEY_PERFECT_PICKUP_LONGITUDE);
				Data.nextPickupLatLng = new LatLng(perfectPickupLatitude, perfectPickupLongitude);
				Data.nextCustomerName = jObjCustomer.optString(KEY_PERFECT_USER_NAME, "");
				Prefs.with(context).save(SPLabels.PERFECT_CUSTOMER_CONT, jObjCustomer.optString(KEY_PERFECT_PHONE_NO, ""));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void parseFareStructureForCustomer(JSONObject jObjCustomer){
		try {
			Data.fareStructure = JSONParser.parseFareObject(jObjCustomer);
			Data.fareStructure.fareFactor = jObjCustomer.optDouble(KEY_FARE_FACTOR, 1);
			Data.fareStructure.luggageFare = jObjCustomer.optDouble(KEY_LUGGAGE_CHARGES, 0d);
			Data.fareStructure.convenienceCharge = jObjCustomer.optDouble(KEY_CONVENIENCE_CHARGE, 0);
			Data.fareStructure.convenienceChargeWaiver = jObjCustomer.optDouble(KEY_CONVENIENCE_CHARGE_WAIVER, 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void fillDriverRideRequests(JSONObject jObject1,Context context ) {

		try {
			Data.clearAssignedCustomerInfosListForStatus(EngagementStatus.REQUESTED.getOrdinal());

			JSONArray jActiveRequests = jObject1.getJSONArray("active_requests");

			for (int i = 0; i < jActiveRequests.length(); i++) {
				JSONObject jActiveRequest = jActiveRequests.getJSONObject(i);
				String requestEngagementId = jActiveRequest.getString("engagement_id");
				String currency = jActiveRequest.getString(KEY_CURRENCY);
				String requestUserId = jActiveRequest.getString("user_id");
				double requestLatitude = jActiveRequest.getDouble("pickup_latitude");
				double requestLongitude = jActiveRequest.getDouble("pickup_longitude");

				double currrentLatitude = jActiveRequest.getDouble(Constants.KEY_CURRENT_LATITUDE);
				double currrentLongitude = jActiveRequest.getDouble(Constants.KEY_CURRENT_LONGITUDE);
				Prefs.with(context).save(Constants.KEY_CURRENT_LATITUDE_ALARM, String.valueOf(currrentLatitude));
				Prefs.with(context).save(Constants.KEY_CURRENT_LONGITUDE_ALARM, String.valueOf(currrentLongitude));

				String requestAddress = jActiveRequest.getString("pickup_location_address");
				double dryDistance = jActiveRequest.optDouble(Constants.KEY_DRY_DISTANCE, 0);

				String startTime = jActiveRequest.getString("start_time");
				String endTime = "";
				if (jActiveRequest.has("end_time")) {
					endTime = jActiveRequest.getString("end_time");
				}

				String startTimeLocal = DateOperations.utcToLocal(startTime);

				long requestTimeOutMillis = GCMIntentService.REQUEST_TIMEOUT;
				if ("".equalsIgnoreCase(endTime)) {
					long serverStartTimeLocalMillis = DateOperations.getMilliseconds(startTimeLocal);
					long serverStartTimeLocalMillisPlus60 = serverStartTimeLocalMillis + 60000;
					requestTimeOutMillis = serverStartTimeLocalMillisPlus60 - System.currentTimeMillis();
				} else {
					long startEndDiffMillis = DateOperations.getTimeDifference(DateOperations.utcToLocal(endTime),
							startTimeLocal);
					Log.i("startEndDiffMillis = ", "=" + startEndDiffMillis);
					if (startEndDiffMillis < GCMIntentService.REQUEST_TIMEOUT) {
						requestTimeOutMillis = startEndDiffMillis;
					} else {
						requestTimeOutMillis = GCMIntentService.REQUEST_TIMEOUT;
					}
				}

				startTime = DateOperations.getDelayMillisAfterCurrentTime(requestTimeOutMillis);

				int referenceId = jActiveRequest.optInt(KEY_REFERENCE_ID, 0);

				double fareFactor = 1;
				if (jActiveRequest.has("fare_factor")) {
					fareFactor = jActiveRequest.getDouble("fare_factor");
				}
				int isPooled = jActiveRequest.optInt(KEY_IS_POOLED, 0);
				int isDelivery = jActiveRequest.optInt(KEY_IS_DELIVERY, 0);
				int totalDeliveries = jActiveRequest.optInt(Constants.KEY_TOTAL_DELIVERIES, 0);
				double estimatedFare = jActiveRequest.optDouble(Constants.KEY_ESTIMATED_FARE, 0d);
				String userName = jActiveRequest.optString(Constants.KEY_NAME, "");
				double cashOnDelivery = jActiveRequest.optDouble(Constants.KEY_TOTAL_CASH_TO_COLLECT_DELIVERY, 0);
				String estimatedDriverFare = jActiveRequest.optString(KEY_ESTIMATED_DRIVER_FARE, "");
				double estimatedDist = jActiveRequest.optDouble(Constants.KEY_ESTIMATED_DISTANCE, 0d);
				int reverseBid = jActiveRequest.optInt(Constants.KEY_REVERSE_BID, 0);
				int bidPlaced = jActiveRequest.optInt(Constants.KEY_BID_PLACED, 0);
				double bidValue = jActiveRequest.optInt(Constants.KEY_BID_VALUE, 0);
				double initialBidValue = jActiveRequest.optDouble(Constants.KEY_INITIAL_BID_VALUE, 10);
				double estimatedTripDistance = jActiveRequest.optDouble(Constants.KEY_ESTIMATED_TRIP_DISTANCE, 0);
				int isDeliveryPool = 0;
				ArrayList<String> dropPoints = new ArrayList<>();
				if(jActiveRequest.has(Constants.KEY_DROP_POINTS)) {
					dropPoints = parseDropPoints(jActiveRequest);
				}
				if(jActiveRequest.optInt(KEY_RIDE_TYPE,0)==4){
					isDeliveryPool =1;
				}

				CustomerInfo customerInfo = new CustomerInfo(Integer.parseInt(requestEngagementId),
						Integer.parseInt(requestUserId), new LatLng(requestLatitude, requestLongitude),
						startTime, requestAddress, referenceId, fareFactor,
						EngagementStatus.REQUESTED.getOrdinal(), isPooled, isDelivery, isDeliveryPool,
						totalDeliveries, estimatedFare, userName, dryDistance, cashOnDelivery,
						new LatLng(currrentLatitude, currrentLongitude), estimatedDriverFare, dropPoints,
						estimatedDist,currency, reverseBid, bidPlaced, bidValue, initialBidValue, estimatedTripDistance);

				Data.addCustomerInfo(customerInfo);

				Log.i("inserter in db", "insertDriverRequest = " + requestEngagementId);
			}


			if (jActiveRequests.length() == 0) {
				GCMIntentService.stopRing(true, context);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setPreferredLangString(JSONObject jsonObject1, Context context){

		try {
			JSONObject preferredLangStrings = jsonObject1.getJSONObject("locale_texts");
			parseCancellationReasons(preferredLangStrings, context);
			Data.userData.referralButtonText = preferredLangStrings.optString("referral_button_text", "Share");
			Data.userData.referralDialogText = preferredLangStrings.optString("referral_dialog_text", "Enter Phone No.");
			Data.userData.referralDialogHintText = preferredLangStrings.optString("referral_dialog_hint_text", "Phone No.");
			Data.userData.timeoutMessage = preferredLangStrings.optString("timeout_message", "We have noticed that, you aren't taking "+ context.getString(R.string.white_label_name) + " rides. So we are blocking you for some time");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	public static EndRideData parseEndRideData(JSONObject jObj, String engagementId, double totalFare) {
		try {
			return new EndRideData(engagementId,
					jObj.getDouble("fare"),
					jObj.getDouble("discount"),
					jObj.getDouble("paid_using_wallet"),
					jObj.getDouble("to_pay"),
					jObj.getInt("payment_mode"),jObj.optString(KEY_CURRENCY));

		} catch (JSONException e) {
			e.printStackTrace();
			return new EndRideData(engagementId,
					totalFare,
					0,
					0,
					totalFare,
					PaymentMode.CASH.getOrdinal(),"");
		}
	}


	public static ArrayList<PreviousAccountInfo> parsePreviousAccounts(JSONObject jsonObject) {
		ArrayList<PreviousAccountInfo> previousAccountInfoList = new ArrayList<PreviousAccountInfo>();
		try {
			JSONArray jPreviousAccountsArr = jsonObject.getJSONArray("users");
			for (int i = 0; i < jPreviousAccountsArr.length(); i++) {
				JSONObject jPreviousAccount = jPreviousAccountsArr.getJSONObject(i);
				previousAccountInfoList.add(new PreviousAccountInfo(jPreviousAccount.getInt("user_id"),
						jPreviousAccount.getString("user_name"),
						jPreviousAccount.getString("user_email"),
						jPreviousAccount.getString("phone_no"),
						jPreviousAccount.getString("date_registered")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return previousAccountInfoList;
	}


	public static void parseCancellationReasons(JSONObject jObj, Context context) {
		try {
			Data.cancelOptionsList = new ArrayList<>();
			Data.cancelOptionsList.add(new CancelOption(context.getResources().getString(R.string.Auto_not_working)));
			Data.cancelOptionsList.add(new CancelOption(context.getResources().getString(R.string.Traffic)));
			Data.cancelOptionsList.add(new CancelOption(context.getResources().getString(R.string.accepted_by_mistake)));
			Data.cancelOptionsList.add(new CancelOption(context.getResources().getString(R.string.Customer_asked_to_cancel)));
			Data.cancelOptionsList.add(new CancelOption(context.getResources().getString(R.string.Not_able_to_contact_customer)));
			Data.cancelOptionsList.add(new CancelOption(context.getResources().getString(R.string.Customer_behavior)));

			JSONArray jCancellationReasons = jObj.getJSONArray("cancellation_reasons");

			Data.cancelOptionsList.clear();

			for (int i = 0; i < jCancellationReasons.length(); i++) {
				Data.cancelOptionsList.add(new CancelOption(jCancellationReasons.getString(i)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	public static ArrayList<DeliveryInfo> parseDeliveryInfos(JSONObject jsonObject) {
		ArrayList<DeliveryInfo> deliveryInfos = new ArrayList<>();
		try {
			JSONArray jsonArray = jsonObject.getJSONArray(KEY_DELIVERY_DATA);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jDelivery = jsonArray.getJSONObject(i);
				DeliveryInfo deliveryInfo = new DeliveryInfo(jDelivery.getInt(KEY_DELIVERY_ID),
						new LatLng(jDelivery.getDouble(KEY_LATITUDE), jDelivery.getDouble(KEY_LONGITUDE)),
						jDelivery.getString(KEY_NAME),
						jDelivery.getString(KEY_ADDRESS),
						jDelivery.getString(KEY_PHONE),
						jDelivery.getDouble(KEY_COLLECT_CASH),
						jDelivery.getInt(KEY_STATUS),
						jDelivery.optDouble(KEY_DISTANCE, 0),
						jDelivery.optLong(KEY_RIDE_TIME, System.currentTimeMillis()),
						jDelivery.optLong(KEY_WAIT_TIME, 0),
						jDelivery.optString(KEY_CANCEL_REASON, ""), i, false,
						jDelivery.optInt(KEY_FALSE_DELIVERY, 0),
						jDelivery.optInt(KEY_IS_UNLOADING, 0),jDelivery.optString(KEY_CURRENCY));

				deliveryInfos.add(deliveryInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return deliveryInfos;
	}

	public static void parseReturnDeliveryInfos(JSONObject jsonObject, CustomerInfo customerInfo) {
		try {
			if (jsonObject.getJSONObject("returnObj") != null) {
				JSONObject jDelivery = jsonObject.getJSONObject("returnObj");
				DeliveryInfo deliveryInfo = new DeliveryInfo(jDelivery.getInt("order_id"),
						new LatLng(jDelivery.getDouble(KEY_LATITUDE), jDelivery.getDouble(KEY_LONGITUDE)),
						jDelivery.getString(KEY_NAME),
						jDelivery.getString(KEY_ADDRESS),
						jDelivery.getString(KEY_PHONE),
						jDelivery.getDouble(KEY_COLLECT_CASH),
						jDelivery.getInt(KEY_STATUS),
						jDelivery.optDouble(KEY_DISTANCE, 0),
						jDelivery.optLong(KEY_RIDE_TIME, System.currentTimeMillis()),
						jDelivery.optLong(KEY_WAIT_TIME, 0),
						jDelivery.optString(KEY_CANCEL_REASON, ""),
						customerInfo.getDeliveryInfos().size(), false,
						jDelivery.optInt(KEY_FALSE_DELIVERY, 0),
						jDelivery.optInt(KEY_IS_UNLOADING, 0),jDelivery.optString(KEY_CURRENCY));
				deliveryInfo.setReturnData(jDelivery.getInt("total_delivery"), jDelivery.getInt("delivery_success"), jDelivery.getInt("delivery_fail"));
				customerInfo.getDeliveryInfos().add(deliveryInfo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	public static ArrayList<DeliveryReturnOption> parseDeliveryReturnOptions(JSONObject jsonObject){
		ArrayList<DeliveryReturnOption> deliveryReturnOptions = new ArrayList<>();
		try{
			JSONArray jsonArray = jsonObject.getJSONArray(KEY_DELIVERY_CANCEL_REASONS);
			for (int i = 0; i < jsonArray.length(); i++) {
				deliveryReturnOptions.add(new DeliveryReturnOption(jsonArray.getString(i)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return deliveryReturnOptions;
	}

	public static ArrayList<String> parseDropPoints (JSONObject jObj){
		ArrayList<String> dropPoints = new ArrayList<>();
		try{
			JSONArray jsonArray = jObj.getJSONArray(KEY_DROP_POINTS);
			for (int i = 0; i < jsonArray.length(); i++) {
				dropPoints.add(jsonArray.getString(i));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dropPoints;
	}


	public static void parsePoolOrReverseBidFare(JSONObject jObjCustomer, CustomerInfo customerInfo){
		try {
			if(jObjCustomer.has(KEY_POOL_FARE)){
				JSONObject jPoolFare = jObjCustomer.optJSONObject(KEY_POOL_FARE);
				double distance = jPoolFare.optDouble(KEY_DISTANCE) * 1000d;
				long rideTime = jPoolFare.optLong(KEY_RIDE_TIME) * 60000l;
				double convenienceCharge = jPoolFare.optDouble(KEY_CONVENIENCE_CHARGE);
				double fare = jPoolFare.optDouble(KEY_FARE);
				double discountedfare = jPoolFare.optDouble(KEY_DISCOUNTED_FARE);
				int discountedFareEnabled = jPoolFare.optInt(KEY_DISCOUNT_ENABLED, 0);
				double discountPercentage = jPoolFare.optDouble(KEY_DISCOUNT_PERCENTAGE, 0);
				double poolDropRadius = jPoolFare.optDouble(KEY_POOL_DROP_RADIUS, 0);
				customerInfo.setPoolFare(new PoolFare(distance, rideTime, convenienceCharge, fare, discountedfare, discountedFareEnabled, discountPercentage, poolDropRadius));
			}
			if(jObjCustomer.has(KEY_REVERSE_BID_FARE)){
				JSONObject jFare = jObjCustomer.optJSONObject(KEY_REVERSE_BID_FARE);
				double fare = jFare.optDouble(KEY_FARE);
				customerInfo.setReverseBidFare(new ReverseBidFare(fare));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private static void parseSideMenu(Context context, JSONObject userData){
		JSONArray menu = userData.optJSONArray(Constants.KEY_MENU);
		if(menu == null){
			return;
		}

		String keys[] = new String[]{
				Constants.LANGUAGE_PREFERENCE_IN_MENU,
				Constants.INVITE_FRIENDS_IN_MENU,
				Constants.DRIVER_RESOURCES_IN_MENU,
				Constants.SUPER_DRIVERS_IN_MENU,
				Constants.INVOICES_IN_MENU,
				Constants.EARNINGS_IN_MENU,
				Constants.BANK_DETAILS_IN_EDIT_PROFILE,
				Constants.SHOW_PLANS_IN_MENU,
				Constants.SHOW_SUPPORT_IN_MENU,
				Constants.SELF_AUDIT_BUTTON_STATUS,
				Constants.SHOW_CALL_US_MENU,
				Constants.SHOW_IN_APP_CALL_US,
				Constants.SHOW_RATE_CARD_IN_MENU,
				Constants.SET_DRIVER_TUTORIAL_STATUS,
				Constants.SHOW_NOTIFICATION_TIPS,
				Constants.CHAT_SUPPORT,
				Constants.WALLET_BALANCE_IN_EARNING,
				Constants.SUPPORT_MAIN,
				Constants.TICKET_SUPPORT,
				Constants.MAIL_SUPPORT,
				Constants.BRANDING_IMAGE,
				Constants.DRIVER_CREDITS,
				Constants.KEY_LOGOUT
		};
		List<String> keysArr = Arrays.asList(keys);
		for(String key : keysArr){
			Prefs.with(context).save(key, 0);
		}

		ArrayList<SupportOption> supportOptions = new ArrayList<>(), creditOptions = new ArrayList<>();
		for(int i=0; i<menu.length(); i++){
			JSONObject menuItem = menu.optJSONObject(i);
			if(menuItem.optInt(Constants.KEY_SHOW_IN_ACCOUNT, 0) == 1) {
				supportOptions.add(new SupportOption(menuItem.optString(Constants.KEY_NAME),
						menuItem.optString(Constants.KEY_TAG)));
			} else if(menuItem.optInt(Constants.KEY_SHOW_IN_EARN_CREDITS, 0) == 1) {
				creditOptions.add(new SupportOption(menuItem.optString(Constants.KEY_NAME),
						menuItem.optString(Constants.KEY_TAG),menuItem.optDouble(Constants.KEY_AMOUNT)));
			} else {
				Prefs.with(context).save(menuItem.optString(Constants.KEY_TAG), 1);
			}
		}
		Data.setSupportOptions(supportOptions);
		Data.setCreditOptions(creditOptions);
	}

	public static boolean isChatSupportEnabled(Context context){
		if(Prefs.with(context).getInt(Constants.CHAT_SUPPORT, 0) == 1
				|| Prefs.with(context).getInt(Constants.TICKET_SUPPORT, 0) == 1){
			return true;
		}

		boolean fuguInSupport = false;
		if(Data.getSupportOptions() != null){
			for(SupportOption supportOption : Data.getSupportOptions()){
				if(supportOption.getTag().equalsIgnoreCase(Constants.CHAT_SUPPORT)
						|| supportOption.getTag().equalsIgnoreCase(Constants.TICKET_SUPPORT)){
					fuguInSupport = true;
					break;
				}
			}
		}
		return fuguInSupport;
	}

}

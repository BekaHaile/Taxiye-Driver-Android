package product.clicklabs.jugnoo.driver;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
import product.clicklabs.jugnoo.driver.datastructure.PreviousAccountInfo;
import product.clicklabs.jugnoo.driver.datastructure.PromoInfo;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.datastructure.UserData;
import product.clicklabs.jugnoo.driver.datastructure.UserMode;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.NudgeClient;
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
		SharedPreferences pref = context.getSharedPreferences(Data.SHARED_PREF_NAME, 0);
		Editor editor = pref.edit();
		editor.putString(Data.SP_ACCESS_TOKEN_KEY, accessToken);
		editor.commit();
	}

	public static Pair<String, String> getAccessTokenPair(Context context) {
		SharedPreferences pref = context.getSharedPreferences(Data.SHARED_PREF_NAME, 0);
		String accessToken = pref.getString(Data.SP_ACCESS_TOKEN_KEY, "");
		String isAccessTokenNew = "1";
		return new Pair<String, String>(accessToken, isAccessTokenNew);
	}


	public static FareStructure parseFareObject(JSONObject jObj) {
		try {
			JSONObject fareDetails = jObj.getJSONObject("fare_details");
			return new FareStructure(fareDetails.getDouble("fare_fixed"),
					fareDetails.getDouble("fare_threshold_distance"),
					fareDetails.getDouble("fare_per_km"),
					fareDetails.getDouble("fare_per_min"),
					fareDetails.getDouble("fare_threshold_time"),
					fareDetails.getDouble("fare_per_waiting_min"),
					fareDetails.getDouble("fare_threshold_waiting_time"));
		} catch (Exception e) {
			e.printStackTrace();
			return new FareStructure(25, 2, 6, 1, 6, 0, 0);
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

		if (userData.has("free_ride_icon_disable")) {
			freeRideIconDisable = userData.getInt("free_ride_icon_disable");
		}

		if (userData.has("autos_enabled")) {
			autosEnabled = userData.getInt("autos_enabled");
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

		String driverSupportNumber = userData.optString("driver_support_number", "+919023121121");
		String referralCode = userData.getString("referral_code");


		String referralSMSToCustomer = userData.optString("referral_sms_to_customer",
				"Use my code " + referralCode + " to download Jugnoo customer App and earn jugnoo cash.\n" +
						"Download it from here\nhttp://smarturl.it/jugnoo");
		String referralMessage = userData.optString("referral_message");
		String referralButtonText = userData.optString("referral_button_text", "Share");
		String referralDialogText = userData.optString("referral_dialog_text", "Please enter Customer Phone No.");
		String referralDialogHintText = userData.optString("referral_dialog_hint_text", "Phone No.");

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

		long remainigPenaltyPeriod = userData.optLong("remaining_penalty_period", 0);
		String timeoutMessage = userData.optString("timeout_message", "We have noticed that, you aren't taking Jugnoo rides. So we are blocking you for some time");
		Log.i("timeOut", timeoutMessage);
		int paytmRechargeEnabled = userData.optInt("paytm_recharge_enabled", 0);
		int destinationOptionEnable = userData.optInt("set_destination_option_enabled", 0);
		long walletUpdateTimeout = userData.optLong("end_ride_fetch_balance_timeout", 3000);
		saveAccessToken(context, accessToken);
		String blockedAppPackageMessage = userData.optString("blocked_app_package_message", "");

		double driverArrivalDistance = userData.optDouble("driver_arrival_distance", 100);


		if (autosAvailable == 1
				|| mealsAvailable == 1
				|| fatafatAvailable == 1
				|| sharingAvailable == 1) {
			Database2.getInstance(context).updateDriverServiceRun(Database2.YES);
		} else {
			Database2.getInstance(context).updateDriverServiceRun(Database2.NO);
		}

		String userEmail = userData.optString("user_email", "");
		String phoneNo = userData.getString("phone_no");
		String userId = userData.optString(KEY_USER_ID, phoneNo);
		Prefs.with(context).save(SP_USER_ID, userId);

		return new UserData(accessToken, userData.getString("user_name"),
				userData.getString("user_image"), referralCode, phoneNo, freeRideIconDisable,
				autosEnabled, mealsEnabled, fatafatEnabled, autosAvailable, mealsAvailable, fatafatAvailable,
				deiValue, customerReferralBonus, sharingEnabled, sharingAvailable, driverSupportNumber,
				referralSMSToCustomer, showDriverRating, driverArrivalDistance, referralMessage,
				referralButtonText,referralDialogText, referralDialogHintText,remainigPenaltyPeriod,
				timeoutMessage, paytmRechargeEnabled, destinationOptionEnable, walletUpdateTimeout,
				userId, userEmail, blockedAppPackageMessage);
	}

	public String parseAccessTokenLoginData(Context context, String response) throws Exception {


		Log.e("response ==", "=" + response);

		JSONObject jObj = new JSONObject(response);

		//Fetching login data
		JSONObject jLoginObject = jObj.getJSONObject("login");

		Data.userData = parseUserData(context, jLoginObject);
		saveAccessToken(context, Data.userData.accessToken);
		Data.blockAppPackageNameList = jLoginObject.getJSONArray("block_app_package_name_list");


		//Fetching user current status
		JSONObject jUserStatusObject = jObj.getJSONObject("status");
		String resp = parseCurrentUserStatus(context, jUserStatusObject);

		parseCancellationReasons(jObj,context);


		try {
			NudgeClient.initialize(context, Data.userData.getUserId(), Data.userData.userName,
					Data.userData.getUserEmail(), Data.userData.phoneNo);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return resp;
	}

	public String getUserStatus(Context context, String accessToken) {
		String returnResponse = "";
		try {
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("access_token", accessToken));

			Response response = RestClient.getApiServices().getUserStatusRetro(accessToken);
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

				fillDriverRideRequests(jObject1);
				setPreferredLangString(jObject1, context);

				Data.clearAssignedCustomerInfosListForStatus(EngagementStatus.ACCEPTED.getOrdinal());
				Data.clearAssignedCustomerInfosListForStatus(EngagementStatus.ARRIVED.getOrdinal());
				Data.clearAssignedCustomerInfosListForStatus(EngagementStatus.STARTED.getOrdinal());
				if (ApiResponseFlags.ENGAGEMENT_DATA.getOrdinal() == flag) {
					JSONArray lastEngInfoArr = jObject1.getJSONArray(KEY_LAST_ENGAGEMENT_INFO);
					for(int i=0; i<lastEngInfoArr.length(); i++) {
						JSONObject jObjCustomer = lastEngInfoArr.getJSONObject(i);

						//TODO ask ronak for fare details
						parseFareStructureForCustomer(jObjCustomer);
						if(i == 0) {
							parsePerfectRideData(context, jObjCustomer);
						}

						int engagementStatus = jObjCustomer.getInt(KEY_STATUS);
						if ((EngagementStatus.ACCEPTED.getOrdinal() == engagementStatus) ||
								(EngagementStatus.STARTED.getOrdinal() == engagementStatus) ||
								(EngagementStatus.ARRIVED.getOrdinal() == engagementStatus)) {
							int dReferenceId = jObjCustomer.optInt(KEY_REFERENCE_ID, 0);
							String engagementId = jObjCustomer.getString(KEY_ENGAGEMENT_ID);
							String userId = jObjCustomer.getString(KEY_USER_ID);
							double pickupLatitude = jObjCustomer.getDouble(KEY_PICKUP_LATITUDE);
							double pickupLongitude = jObjCustomer.getDouble(KEY_PICKUP_LONGITUDE);
							String customerName = jObjCustomer.getString(KEY_USER_NAME);
							String customerImage = jObjCustomer.getString(KEY_USER_IMAGE);
							String customerPhone = jObjCustomer.getString(KEY_PHONE_NO);
							String customerRating = jObjCustomer.optString(KEY_RATING, "4");
							double jugnooBalance = jObjCustomer.optDouble(KEY_JUGNOO_BALANCE, 0);
							CouponInfo couponInfo = JSONParser.parseCouponInfo(jObjCustomer);
							PromoInfo promoInfo = JSONParser.parsePromoInfo(jObjCustomer);

							if (Prefs.with(context).getLong(SPLabels.CURRENT_ETA, 0) == 0) {
								Prefs.with(context).save(SPLabels.CURRENT_ETA, System.currentTimeMillis() + jObjCustomer.optLong("eta", 0));
							}

							int meterFareApplicable = jObjCustomer.optInt("meter_fare_applicable", 0);
							int getJugnooFareEnabled = jObjCustomer.optInt("get_jugnoo_fare_enabled", 1);
							int luggageChargesApplicable = jObjCustomer.optInt("luggage_charges_applicable", 0);
							int waitingChargesApplicable = jObjCustomer.optInt("waiting_charges_applicable", 0);
							int cachedApiEnabled = jObjCustomer.optInt(KEY_CACHED_API_ENABLED, 0);
							int isPooled = jObjCustomer.optInt(KEY_IS_POOLED, 0);

							if(i == 0){
								Data.setCurrentEngagementId(engagementId);
							}

							Data.addCustomerInfo(new CustomerInfo(Integer.parseInt(engagementId), Integer.parseInt(userId),
									dReferenceId, customerName, customerPhone, new LatLng(pickupLatitude, pickupLongitude), cachedApiEnabled,
									customerImage, customerRating, couponInfo, promoInfo, jugnooBalance, meterFareApplicable, getJugnooFareEnabled,
									luggageChargesApplicable, waitingChargesApplicable, engagementStatus, isPooled));
							try {
								if (jObjCustomer.has(KEY_OP_DROP_LATITUDE) && jObjCustomer.has(KEY_OP_DROP_LONGITUDE)) {
									double dropLatitude = jObjCustomer.getDouble(KEY_OP_DROP_LATITUDE);
									double dropLongitude = jObjCustomer.getDouble(KEY_OP_DROP_LONGITUDE);
									if ((Utils.compareDouble(dropLatitude, 0) == 0) && (Utils.compareDouble(dropLongitude, 0) == 0)) {
										(Data.getCustomerInfo(engagementId)).dropLatLng = null;
									} else {
										(Data.getCustomerInfo(engagementId)).dropLatLng = new LatLng(dropLatitude, dropLongitude);
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					}

					try {
						//TODO ask ronak to give single master id
						Log.writePathLogToFile(Data.getCurrentEngagementId() + "accept", "JSONPARSER  = " + jObject1);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}

				if (Data.getAssignedCustomerInfosListForStatus(EngagementStatus.STARTED.getOrdinal()).size() == 0) {
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
		} else {
			HomeActivity.driverScreenMode = Data.getDriverScreenModeFromEngagementStatus(
					Data.getCurrentCustomerInfo().getStatus());
		}

		return "";
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
		} catch (JSONException e) {
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


	public void fillDriverRideRequests(JSONObject jObject1) {

		try {
			Data.clearAssignedCustomerInfosListForStatus(EngagementStatus.REQUESTED.getOrdinal());

			JSONArray jActiveRequests = jObject1.getJSONArray("active_requests");

			for (int i = 0; i < jActiveRequests.length(); i++) {
				JSONObject jActiveRequest = jActiveRequests.getJSONObject(i);
				String requestEngagementId = jActiveRequest.getString("engagement_id");
				String requestUserId = jActiveRequest.getString("user_id");
				double requestLatitude = jActiveRequest.getDouble("pickup_latitude");
				double requestLongitude = jActiveRequest.getDouble("pickup_longitude");
				String requestAddress = jActiveRequest.getString("pickup_location_address");

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

				int referenceId = jActiveRequest.getInt("reference_id");

				double fareFactor = 1;
				if (jActiveRequest.has("fare_factor")) {
					fareFactor = jActiveRequest.getDouble("fare_factor");
				}
				int isPooled = jActiveRequest.optInt(KEY_IS_POOLED, 0);

				CustomerInfo customerInfo = new CustomerInfo(Integer.parseInt(requestEngagementId),
						Integer.parseInt(requestUserId), new LatLng(requestLatitude, requestLongitude),
						startTime, requestAddress, referenceId, fareFactor,
						EngagementStatus.REQUESTED.getOrdinal(), isPooled);
				Data.addCustomerInfo(customerInfo);

				Log.i("inserter in db", "insertDriverRequest = " + requestEngagementId);
			}


			if (jActiveRequests.length() == 0) {
				GCMIntentService.stopRing(true);
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
			Data.userData.referralDialogText = preferredLangStrings.optString("referral_dialog_text", "Please enter Customer Phone No.");
			Data.userData.referralDialogHintText = preferredLangStrings.optString("referral_dialog_hint_text", "Phone No.");
			Data.userData.timeoutMessage = preferredLangStrings.optString("timeout_message", "We have noticed that, you aren't taking Jugnoo rides. So we are blocking you for some time");

		} catch (JSONException e) {
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
					jObj.getInt("payment_mode"));

		} catch (JSONException e) {
			e.printStackTrace();
			return new EndRideData(engagementId,
					totalFare,
					0,
					0,
					totalFare,
					PaymentMode.CASH.getOrdinal());
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

}

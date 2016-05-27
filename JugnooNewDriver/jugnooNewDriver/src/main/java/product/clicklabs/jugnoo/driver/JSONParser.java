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
		editor.putString(Data.SP_IS_ACCESS_TOKEN_NEW, "1");
		editor.commit();
	}

	public static Pair<String, String> getAccessTokenPair(Context context) {
		SharedPreferences pref = context.getSharedPreferences(Data.SHARED_PREF_NAME, 0);
		String accessToken = pref.getString(Data.SP_ACCESS_TOKEN_KEY, "");
		String isAccessTokenNew = pref.getString(Data.SP_IS_ACCESS_TOKEN_NEW, "not_found");
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

//        {
//            "flag": 150,
//            "login": {
//            "user_data": {
//                "access_token": "35adf238dd7d1af82291ac6c2fe2f0ca6a978ed543d087507f7cf2e2b814dcfd",
//                    "user_name": "Driver 2",
//                    "user_image": "http://tablabar.s3.amazonaws.com/brand_images/user.png",
//                    "phone_no": "+919780298413",
//                    "referral_code": "DRIVER19",
//                    "is_available": 0,
//                    "autos_enabled": 1,
//                    "meals_enabled": 0,
//                    "fatafat_enabled": 1,
//                    "autos_available": 0,
//                    "meals_available": 0,
//                    "fatafat_available": 0,
//                    "gcm_intent": 1,
//                    "current_user_status": 1,
//                    "fare_details": [
//                {
//                    "fare_fixed": 20,
//                    "fare_per_km": 5,
//                    "fare_threshold_distance": 0,
//                    "fare_per_min": 1,
//                    "fare_threshold_time": 0
//                }
//                ],
//                "exceptional_driver": 0
//            },
//            "popup": 0
//        },
//            "status": {
//            "flag": 133,
//                "active_requests": []
//        }
//        }


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
		Data.termsAgreed = 1;
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

		//current_user_status = 1 driver or 2 user
		int currentUserStatus = jLoginObject.getInt("current_user_status");


		//Fetching user current status
		JSONObject jUserStatusObject = jObj.getJSONObject("status");
		String resp = parseCurrentUserStatus(context, currentUserStatus, jUserStatusObject);

		parseCancellationReasons(jObj,context);


		try {
			NudgeClient.initialize(context, Data.userData.getUserId(), Data.userData.userName,
					Data.userData.getUserEmail(), Data.userData.phoneNo);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return resp;
	}


	public void parsePortNumber(Context context, JSONObject jLoginObject) {
		try {
			if (jLoginObject.has("port_number")) {
				String port = jLoginObject.getString("port_number");
				updatePortNumber(context, port);
				SplashNewActivity.initializeServerURL(context);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updatePortNumber(Context context, String port) {
		SharedPreferences preferences = context.getSharedPreferences(Data.SETTINGS_SHARED_PREF_NAME, 0);
		String link = preferences.getString(Data.SP_SERVER_LINK, Data.DEFAULT_SERVER_URL);

		if (link.equalsIgnoreCase(Data.TRIAL_SERVER_URL)) {
			Database2.getInstance(context).updateSalesPortNumber(port);
		} else if (link.equalsIgnoreCase(Data.DEV_SERVER_URL)) {
			Database2.getInstance(context).updateDevPortNumber(port);
		} else {
			Database2.getInstance(context).updateLivePortNumber(port);
		}
	}


//	Retrofit

	public String getUserStatus(Context context, String accessToken, int currentUserStatus) {
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
				returnResponse = parseCurrentUserStatus(context, currentUserStatus, jObject1);
				return returnResponse;
			}
		} catch (Exception e) {
			e.printStackTrace();
			returnResponse = Constants.SERVER_TIMEOUT;
			return returnResponse;
		}
	}


	public void parseLastRideData(JSONObject jObj){

		try {
			JSONArray lastEngInfoArr = jObj.getJSONArray("last_engagement_info");
			JSONObject jLastRideData = lastEngInfoArr.getJSONObject(0);

			Log.e("jLastRideData", "=" + jLastRideData);

			Data.setCurrentEngagementId(jLastRideData.getString(KEY_ENGAGEMENT_ID));

			HomeActivity.totalDistance = jLastRideData.getDouble("distance_travelled");
			HomeActivity.waitTime = jLastRideData.optString("wait_time", "0");
			HomeActivity.rideTime = jLastRideData.getString("ride_time");
			HomeActivity.totalFare = jLastRideData.getDouble("fare");
			HomeActivity.waitStart = 2;

			CouponInfo couponInfo = JSONParser.parseCouponInfo(jLastRideData);
			PromoInfo promoInfo = JSONParser.parsePromoInfo(jLastRideData);

			int referenceId = 0;
			int cachedApiEnabled = jObj.optInt(KEY_CACHED_API_ENABLED, 0);

			Data.addCustomerInfo(new CustomerInfo(Integer.parseInt(Data.getCurrentEngagementId()), Integer.parseInt(jLastRideData.getString("user_id")),
					referenceId, jLastRideData.getString("user_name"), jLastRideData.getString("phone_no"), 
					new LatLng(0, 0), cachedApiEnabled,
					jLastRideData.getString("user_image"), couponInfo, promoInfo, EngagementStatus.ENDED.getOrdinal()));
			
			
			Data.fareStructure = JSONParser.parseFareObject(jLastRideData);

			if (jLastRideData.has("fare_factor")) {
				try {
					Data.fareStructure.fareFactor = jLastRideData.getDouble("fare_factor");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (jLastRideData.has("luggage_charges")) {
				try {
					Data.fareStructure.luggageFare = jLastRideData.getDouble("luggage_charges");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (jLastRideData.has("convenience_charge")) {
				try {
					Data.fareStructure.convenienceCharge = jLastRideData.getDouble("convenience_charge");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (jLastRideData.has("convenience_charge_waiver")) {
				try {
					Data.fareStructure.convenienceChargeWaiver = jLastRideData.getDouble("convenience_charge_waiver");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
//									"convenience_charge": 10,
//									"convenience_charge_waiver": 0,

			parseEndRideData(jLastRideData, Data.getCurrentEngagementId(), HomeActivity.totalFare);


			HomeActivity.driverScreenMode = DriverScreenMode.D_RIDE_END;

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}


	public String parseCurrentUserStatus(Context context, int currentUserStatus, JSONObject jObject1) {

		String returnResponse = "";

		if (currentUserStatus == 1) {

			int engagementStatus = -1;
			String engagementId = "", userId = "", customerName = "", customerImage = "", customerPhone = "", customerRating = "4", schedulePickupTime = "";
			double pickupLatitude = 0, pickupLongitude = 0;
			int freeRide = 0;
			int meterFareApplicable = 0, getJugnooFareEnabled = 1, luggageChargesApplicable = 0, waitingChargesApplicable = 0;
			CouponInfo couponInfo = null;
			PromoInfo promoInfo = null;
			double jugnooBalance = 0, dropLatitude = 0, dropLongitude = 0;
			int dBusinessId = 1;
			int dReferenceId = 0;
			String storeAddress = "";
			int storeOrderAmount = 0, cachedApiEnabled = 0;

			HomeActivity.userMode = UserMode.DRIVER;

			try {

				if (jObject1.has("error")) {
					returnResponse = Constants.SERVER_TIMEOUT;
					return returnResponse;
				} else {
					int flag = jObject1.getInt(KEY_FLAG);

					fillDriverRideRequests(jObject1);
					setPreferredLangString(jObject1, context);

					if (ApiResponseFlags.ENGAGEMENT_DATA.getOrdinal() == flag) {
						JSONArray lastEngInfoArr = jObject1.getJSONArray(KEY_LAST_ENGAGEMENT_INFO);
						JSONObject jObject = lastEngInfoArr.getJSONObject(0);

						dReferenceId = jObject.getInt(KEY_REFERENCE_ID);
						dBusinessId = jObject.getInt(KEY_BUSINESS_ID);

						Data.fareStructure = JSONParser.parseFareObject(jObject);
						Data.fareStructure.fareFactor = jObject.optDouble(KEY_FARE_FACTOR, 1);
						Data.fareStructure.luggageFare = jObject.optDouble(KEY_LUGGAGE_CHARGES, 0d);
						Data.fareStructure.convenienceCharge = jObject.optDouble(KEY_CONVENIENCE_CHARGE, 0);
						Data.fareStructure.convenienceChargeWaiver = jObject.optDouble(KEY_CONVENIENCE_CHARGE_WAIVER, 0);

						if (jObject.has(KEY_PERFECT_PICKUP_LATITUDE) && jObject.has(KEY_PERFECT_PICKUP_LONGITUDE)) {
							try {
								double perfectPickupLatitude = jObject.getDouble(KEY_PERFECT_PICKUP_LATITUDE);
								double perfectPickupLongitude = jObject.getDouble(KEY_PERFECT_PICKUP_LONGITUDE);
								Data.nextPickupLatLng = new LatLng(perfectPickupLatitude, perfectPickupLongitude);
								Data.nextCustomerName = jObject.optString(KEY_PERFECT_USER_NAME, "");
								Prefs.with(context).save(SPLabels.PERFECT_CUSTOMER_CONT, jObject.optString(KEY_PERFECT_PHONE_NO, ""));
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						engagementStatus = jObject.getInt(KEY_STATUS);

						if ((EngagementStatus.ACCEPTED.getOrdinal() == engagementStatus) ||
								(EngagementStatus.STARTED.getOrdinal() == engagementStatus) ||
								(EngagementStatus.ARRIVED.getOrdinal() == engagementStatus)) {
							engagementId = jObject.getString(KEY_ENGAGEMENT_ID);
							userId = jObject.getString(KEY_USER_ID);
							pickupLatitude = jObject.getDouble(KEY_PICKUP_LATITUDE);
							pickupLongitude = jObject.getDouble(KEY_PICKUP_LONGITUDE);
							customerName = jObject.getString(KEY_USER_NAME);
							customerImage = jObject.getString(KEY_USER_IMAGE);
							customerPhone = jObject.getString(KEY_PHONE_NO);
							customerRating = jObject.optString(KEY_RATING, customerRating);
							int isScheduled = jObject.optInt(KEY_IS_SCHEDULED, 0);
							schedulePickupTime = jObject.optString(KEY_PICKUP_TIME, "");
							jugnooBalance = jObject.optDouble(KEY_JUGNOO_BALANCE, 0);
							freeRide = jObject.optInt(KEY_FREE_RIDE, 0);
							couponInfo = JSONParser.parseCouponInfo(jObject);
							promoInfo = JSONParser.parsePromoInfo(jObject);

							if (Prefs.with(context).getLong(SPLabels.CURRENT_ETA, 0) == 0) {
								Prefs.with(context).save(SPLabels.CURRENT_ETA, System.currentTimeMillis() + jObject.optLong("eta", 0));
							}

							try {
								if (jObject.has(KEY_OP_DROP_LATITUDE) && jObject.has(KEY_OP_DROP_LONGITUDE)) {
									dropLatitude = jObject.getDouble(KEY_OP_DROP_LATITUDE);
									dropLongitude = jObject.getDouble(KEY_OP_DROP_LONGITUDE);
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

							meterFareApplicable = jObject.optInt("meter_fare_applicable", 0);
							getJugnooFareEnabled = jObject.optInt("get_jugnoo_fare_enabled", 1);
							luggageChargesApplicable = jObject.optInt("luggage_charges_applicable", 0);
							waitingChargesApplicable = jObject.optInt("waiting_charges_applicable", 0);
							cachedApiEnabled = jObject.optInt(KEY_CACHED_API_ENABLED, 0);
						}
						try {
							Log.writePathLogToFile(engagementId + "accept", "JSONPARSER  = " + jObject1);
						} catch (Exception e) {
							e.printStackTrace();
						}

					} else if (ApiResponseFlags.LAST_ENGAGEMENT_DATA.getOrdinal() == flag) {
						parseLastRideData(jObject1);
						return returnResponse;
					}

					if (EngagementStatus.STARTED.getOrdinal() != engagementStatus) {
						Prefs.with(context).save(SPLabels.PERFECT_ACCEPT_RIDE_DATA, " ");
						Prefs.with(context).save(SPLabels.PERFECT_CUSTOMER_CONT, "");
						new ApiAcceptRide().perfectRideVariables(context, "", "", "", 0, 0);

					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				engagementStatus = -1;
				returnResponse = Constants.SERVER_TIMEOUT;
				return returnResponse;
			}



			if (EngagementStatus.ACCEPTED.getOrdinal() != engagementStatus
					&& EngagementStatus.ARRIVED.getOrdinal() != engagementStatus
					&& EngagementStatus.STARTED.getOrdinal() != engagementStatus) {
				HomeActivity.driverScreenMode = DriverScreenMode.D_INITIAL;
				clearSPData(context);
			} else {

				Data.setCurrentEngagementId(engagementId);
				Data.clearAssignedCustomerInfosListAll();

				Data.addCustomerInfo(new CustomerInfo(Integer.parseInt(engagementId), Integer.parseInt(userId),
						dReferenceId, customerName, customerPhone, new LatLng(pickupLatitude, pickupLongitude), cachedApiEnabled,
						customerImage, customerRating, schedulePickupTime, freeRide,
						couponInfo, promoInfo, jugnooBalance, meterFareApplicable, getJugnooFareEnabled,
						luggageChargesApplicable, waitingChargesApplicable, engagementStatus));
				if ((Utils.compareDouble(dropLatitude, 0) == 0) && (Utils.compareDouble(dropLongitude, 0) == 0)) {
					(Data.getCurrentCustomerInfo()).dropLatLng = null;
				} else {
					(Data.getCurrentCustomerInfo()).dropLatLng = new LatLng(dropLatitude, dropLongitude);
				}


				if (EngagementStatus.ACCEPTED.getOrdinal() == engagementStatus) {
					HomeActivity.driverScreenMode = DriverScreenMode.D_ARRIVED;
				} else if (EngagementStatus.ARRIVED.getOrdinal() == engagementStatus) {
					HomeActivity.driverScreenMode = DriverScreenMode.D_START_RIDE;
				} else if (EngagementStatus.STARTED.getOrdinal() == engagementStatus) {
					HomeActivity.driverScreenMode = DriverScreenMode.D_IN_RIDE;

					SharedPreferences pref = context.getSharedPreferences(Data.SHARED_PREF_NAME, 0);

					HomeActivity.totalDistance = Double.parseDouble(pref.getString(Data.SP_TOTAL_DISTANCE, "-1"));
					HomeActivity.previousWaitTime = GpsDistanceCalculator.getWaitTimeFromSP(context);

					long rideStartTime = Long.parseLong(pref.getString(Data.SP_RIDE_START_TIME, "" + System.currentTimeMillis()));
					long timeDiffToAdd = System.currentTimeMillis() - rideStartTime;
					if (timeDiffToAdd > 0) {
						HomeActivity.previousRideTime = timeDiffToAdd;
					} else {
						HomeActivity.previousRideTime = 0;
					}


					HomeActivity.waitStart = 2;

				} else {
					HomeActivity.driverScreenMode = DriverScreenMode.D_INITIAL;
				}
			}

		}

		return returnResponse;
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


	public void clearSPData(final Context context) {
		SharedPreferences pref = context.getSharedPreferences(Data.SHARED_PREF_NAME, 0);
		Editor editor = pref.edit();

		editor.putString(Data.SP_TOTAL_DISTANCE, "-1");
		editor.putString(Data.SP_RIDE_START_TIME, "" + System.currentTimeMillis());
		editor.putString(Data.SP_LAST_LATITUDE, "0");
		editor.putString(Data.SP_LAST_LONGITUDE, "0");
		editor.putString(Data.SP_LAST_LOCATION_TIME, "" + System.currentTimeMillis());

		editor.commit();

		Database.getInstance(context).deleteSavedPath();

	}


	public static void parseEndRideData(JSONObject jObj, String engagementId, double totalFare) {
		try {
			Data.endRideData = new EndRideData(engagementId,
					jObj.getDouble("fare"),
					jObj.getDouble("discount"),
					jObj.getDouble("paid_using_wallet"),
					jObj.getDouble("to_pay"),
					jObj.getInt("payment_mode"));

		} catch (JSONException e) {
			e.printStackTrace();
			Data.endRideData = new EndRideData(engagementId,
					totalFare,
					0,
					0,
					totalFare,
					PaymentMode.CASH.getOrdinal());
		}
	}


	public static ArrayList<PreviousAccountInfo> parsePreviousAccounts(JSONObject jsonObject) {
		ArrayList<PreviousAccountInfo> previousAccountInfoList = new ArrayList<PreviousAccountInfo>();

//        {
//            "flag": 400,
//            "users": [
//            {
//                "user_id": 145,
//                "user_name": "Shankar16",
//                "user_email": "shankar+16@jugnoo.in",
//                "phone_no": "+919780111116",
//                "date_registered": "2015-01-26T13:55:58.000Z"
//            }
//            ]
//        }

//        {
//            "flag": 400,
//            "users": [
//            {
//                "user_id": 145,
//                "user_name": "Shankar16",
//                "user_email": "shankar+16@jugnoo.in",
//                "phone_no": "+919780111116",
//                "date_registered": "2015-01-26T13:55:58.000Z",
//                "allow_dup_regis": 0
//            }
//            ]
//        }

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

//		"cancellation": {
//      "message": "Cancellation of a ride more than 5 minutes after the driver is allocated will lead to cancellation charges of Rs. 20",
//      "reasons": [
//          "Driver is late",
//          "Driver denied duty",
//          "Changed my mind",
//          "Booked another auto"
//      ],
//        "addn_reason":"foo"
//  }


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

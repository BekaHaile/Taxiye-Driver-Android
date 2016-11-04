package product.clicklabs.jugnoo.driver;

/**
 * Created by Shankar on 12/1/2015.
 */
public interface Constants {


    String PHONE_NO_VERIFY = "phone_no_verify";

    String FIRST_TIME_DB= "first_time_db";

	String KEY_SAVE_NOTIFICATION = "canStore";
	String KEY_NOTIFICATION_ID = "notification_id";
    String SERVER_TIMEOUT = "SERVER_TIMEOUT";

    String KEY_CACHED_API_ENABLED = "cached_api_enabled";
    String KEY_FLAG = "flag";
    String ACTION_LOADING_COMPLETE = "product.clicklabs.jugnoo.driver.ACTION_LOADING_COMPLETE";
    String SP_ANALYTICS_LAST_MESSAGE_READ_TIME = "sp_analytics_last_message_read_time";
	String KEY_ANALYTICS_SMS_LIST= "analytics_sms_list";
	String KEY_ACCESS_TOKEN = "access_token";

	String JUGNOO_AUDIO = "jugnoo_audio";

    long HEAT_MAP_FETCH_DELAY = 120000;
    String ACTION_LOCATION_UPDATE = "jugnoo.ACTION_LOCATION_UPDATE";
    String KEY_RIDE_TIME_SECONDS = "ride_time_seconds";
    String KEY_RIDE_TIME_SECONDS_DB = "ride_time_seconds_db";
    String KEY_WAIT_TIME_SECONDS = "wait_time_seconds";
    String KEY_PICTURE = "picture";
    String KEY_IMAGE = "image";

    String START_RIDE_ALARM_LOCATION_FETCHER_INTERVAL = "start_ride_alarm_location_fetcher_interval";
    String KEY_USER_NAME = "user_name";
    String KEY_EMAIL = "email";
    String KEY_PHONE_NO = "phone_no";
    String KEY_SIGNED_UP_AT = "signed_up_at";
    String KEY_IS_DRIVER = "is_driver";
    String KEY_CONTACT_TYPE = "contact_type";
    String KEY_CONTACT_VALUE = "contact_value";
    String KEY_SUBSCRIPTION_STATUS = "subscription_status";
    String KEY_ACTIVE = "active";
    String KEY_PHONE = "phone";
    String KEY_CONTACTS = "contacts";
    String KEY_USER_ID = "user_id";
    String SP_USER_ID = "sp_user_id";

    String KEY_LATITUDE = "latitude";
    String KEY_LONGITUDE = "longitude";

    String KEY_CURRENT_LATITUDE= "current_latitude";
    String KEY_CURRENT_LONGITUDE= "current_longitude";
    String KEY_ENGAGEMENT_ID = "engagement_id";
    String KEY_CUSTOMER_ID = "customer_id";
    String KEY_GIVEN_RATING = "given_rating";
    String KEY_INVOICE_ID = "invoice_id";
    String KEY_CUSTOMER_PHONE_NO = "customer_phone_no";
    String KEY_CANCELLATION_REASON = "cancellation_reason";
    String KEY_CALL_LOGS = "call_logs";

    String FETCH_APP_API_ENABLED = "fetch_app_api_enabled";
    String FETCH_APP_API_FREQUENCY = "fetch_app_api_frequency";
    String FETCH_APP_TIME = "fetch_app_time";
	String SHOW_NOTIFICATION_TIPS = "show_notification_tips";
	String NOTIFICATION_MSG_TEXT = "notification_msg_text";
	String NOTIFICATION_TIPS_TEXT = "notification_tips_text";

    String KEY_IS_POOLED = "is_pooled";
    String KEY_FARE_FACTOR = "fare_factor";
    String KEY_LUGGAGE_CHARGES = "luggage_charges";
    String KEY_CONVENIENCE_CHARGE = "convenience_charge";
    String KEY_CONVENIENCE_CHARGE_WAIVER = "convenience_charge_waiver";
    String KEY_REFERENCE_ID = "reference_id";
    String KEY_USER_DATA = "user_data";
    String KEY_USER_IMAGE = "user_image";
    String KEY_USER_RATING = "user_rating";
    String KEY_JUGNOO_BALANCE = "jugnoo_balance";
    String KEY_PICKUP_LATITUDE = "pickup_latitude";
    String KEY_PICKUP_LONGITUDE = "pickup_longitude";

    String KEY_PICKUP_LATITUDE_ALARM = "pickup_latitude_alarm";
    String KEY_PICKUP_LONGITUDE_ALARM = "pickup_longitude_alarm";

    String KEY_DEVICE_NAME = "device_name";
    String KEY_IMEI = "imei";
    String KEY_APP_VERSION = "app_version";
    String KEY_PERFECT_PICKUP_LATITUDE = "perfect_pickup_latitude";
    String KEY_PERFECT_PICKUP_LONGITUDE = "perfect_pickup_longitude";
    String KEY_PERFECT_USER_NAME = "perfect_user_name";
    String KEY_PERFECT_PHONE_NO = "perfect_phone_no";
    String KEY_LAST_ENGAGEMENT_INFO = "last_engagement_info";
    String KEY_STATUS = "status";
    String KEY_RATING = "rating";
    String KEY_OP_DROP_LATITUDE = "op_drop_latitude";
    String KEY_OP_DROP_LONGITUDE = "op_drop_longitude";
    String KEY_DROP_ADDRESS = "drop_address";
    String KEY_DRYRUN_DISTANCE = "dryrun_distance";
    String KEY_ERROR = "error";
    String KEY_WALLET_BALANCE = "wallet_balance";
    String KEY_DISTANCE = "distance";
    String KEY_RIDE_PATH_DATA = "ride_path_data";
    String KEY_POOL_FARE ="pool_fare";

	String KEY_SP_METER_DISP_MIN_THRESHOLD = "meter_disp_min_threshold";
	String KEY_SP_METER_DISP_MAX_THRESHOLD = "meter_disp_max_threshold";

    String SP_CUSTOMER_RIDE_DATAS_OBJECT = "sp_customer_ride_datas_object";
    String KEY_HAVERSINE_DISTANCE = "haversine_distance";
    String KEY_RIDE_TIME = "ride_time";
    String KEY_WAIT_TIME = "wait_time";
    String KEY_CANCEL_REASON = "cancel_reason";
    String EMPTY_OBJECT = "{}";
    String KEY_IS_DELIVERY = "is_delivery";
    String KEY_DISTANCE_TRAVELLED = "distance_travelled";
    String KEY_DISTANCE_TRAVELLED_LOG = "distance_travelled_log";
    String KEY_IS_CACHED = "is_cached";
    String KEY_PR_DISTANCE = "pr_distance";
    String KEY_DELIVERY_DATA = "delivery_data";
    String KEY_DELIVERY_ID = "delivery_id";
    String KEY_ADDRESS = "address";
    String KEY_COLLECT_CASH = "collect_cash";
    String KEY_DELIVERY_CANCEL_REASONS = "delivery_cancel_reasons";
    String KEY_ID = "id";
    String KEY_NAME = "name";
    String KEY_TITLE = "title";
    String KEY_PERFECT_RIDE = "perfect_ride";
    String KEY_END_TIME = "end_time";
    String KEY_DELIVERY_ENABLED = "delivery_enabled";
    String KEY_DELIVERY_AVAILABLE = "delivery_available";
    String KEY_DELIVERY_FLAG = "delivery_flag";
    String KEY_TOTAL_DELIVERIES = "total_deliveries";
    String KEY_ESTIMATED_FARE = "estimated_fare";
    String KEY_VENDOR_MESSAGE = "vendor_message";
    String KEY_TOTAL_CASH_TO_COLLECT_DELIVERY = "total_cash_to_collect_delivery";
    String KNOWLARITY_NO = "knowlarity_no";
    String ACCEPTED_STATE_UPDATE_TIME_PERIOD = "accepted_state_update_time_period";
    String FREE_STATE_UPDATE_TIME_PERIOD = "free_state_update_time_period";
    String KEY_RING_COUNT = "ring_count";

	String HIGH_DEMAND_WEB_URL = "high_demand_web_url";

    String KEY_FARE = "fare";
	String KEY_DISCOUNTED_FARE = "discounted_fare";
	String KEY_DISCOUNT_ENABLED = "discount_enabled";

    String KEY_BEARING = "bearing";
    String KEY_DEVICE_TOKEN = "device_token";
    String KEY_LOCATION_ACCURACY = "location_accuracy";

    String KEY_KILL_APP = "kill_app";
	String MIME_TYPE = "application/octet-stream";
    String KEY_LINK = "link";

	String START_NAVIGATION_ACCEPT = "start_navigation_accept";
	String START_NAVIGATION_START = "start_navigation_start";
	String POWER_OFF_INITIATED = "power_off_initiated";

    String HIGH_DEMAND_AREA_POPUP = "high_demand_area_popup";

    String SP_START_LATITUDE = "sp_start_latitude";
    String SP_START_LONGITUDE = "sp_start_longitude";
    String KEY_PICKUP_TIME = "pickup_time";
    String KEY_DRY_DISTANCE = "dry_distance";
    String SHOW_INVOICE_DETAILS = "show_invoice_details";
//    String SHOW_EDIT_IMAGE_FLAG = "show_edit_image_flag";
//	String SHOW_EDIT_ON_REJECT = "show_edit_on_reject";


	String ACTION_UPDATE_DOCUMENT_LIST = "ACTION_UPDATE_DOCUMENT_LIST";

	String KEY_DISCOUNT_PERCENTAGE = "discount_percentage";
	String KEY_POOL_DROP_RADIUS = "pool_drop_radius";
    String DRIVER_RIDE_EARNING = "driver_ride_earning";
    String DRIVER_RIDE_DATE = "driver_ride_date";
    String ACTION_UPDATE_RIDE_EARNING = "action_update_ride_earning";
    String ACTION_REFRESH_USL = "action_refresh_usl";
    String ALERT_BATTERY_LOW = "alert_battery_low";
	String ALERT_CHARGING = "alert_charging";
	String MOBILE_DATA_STATE = "mobile_data_state";

	String FREE_STATE_UPDATE_TIME_PERIOD_CHARGING = "free_state_update_time_period_charging";
	String FREE_STATE_UPDATE_TIME_PERIOD_CHARGING_V5 = "free_state_update_time_period_charging_v5";

	String FREE_STATE_UPDATE_TIME_PERIOD_NON_CHARGING = "free_state_update_time_period_non_charging";
	String DRIVER_OFFLINE_PERIOD = "driver_offline_period";
    long PUSHY_REFRESH_INTERVAL_DEFAULT = 60 * 3;


	String EVENT_LR_SPEED_20PLUS_RESTART = "LR_SPEED_20PLUS_RESTART";
	String EVENT_LR_5_LOC_BAD_ACCURACY = "LR_5_LOC_BAD_ACCURACY";
	String EVENT_LR_LOC_BAD_ACCURACY_RESTART = "LR_LOC_BAD_ACCURACY_RESTART";
	String EVENT_DLD_LOC_RECEIVED = "DLD_LOC_RECEIVED";
	String EVENT_DLD_LOC_REJECTED_TIME_DIFF = "DLD_LOC_REJECTED_TIME_DIFF";
	String EVENT_DLD_LOW_MEMORY = "DLD_LOW_MEMORY";
    String EVENT_DLD_TRIM_MEMORY = "DLD_TRIM_MEMORY";
    String EVENT_DLD_LOC_SENT = "DLD_LOC_SENT";

    String EVENT_DLD_LOC_FAILED_RETRO = "DLD_LOC_FAILED_RETRO";
	String EVENT_DLD_LOC_FAILED_GENERIC = "DLD_LOC_FAILED_GENERIC";
	String EVENT_LRD_STALE_GPS_RESTART_SERVICE = "LRD_STALE_GPS_RESTART_SERVICE";

	String EVENT_DLD_DEVICE_TOKEN_RESET = "DLD_DEVICE_TOKEN_RESET";
	String EVENT_DL_ALARM_LOC_NOT_SENT_TILL_3_MIN = "DL_ALARM_LOC_NOT_SENT_TILL_3_MIN";

    String PLAY_START_RIDE_ALARM = "play_start_ride_alarm";
    String PLAY_START_RIDE_ALARM_FINALLY ="play_start_ride_alarm_finally";
    String FLAG_REACHED_PICKUP ="flag_reached_pickup";
	String START_RIDE_ALARM_SERVICE_STATUS = "start_ride_alarm_service_status";

}


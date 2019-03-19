package product.clicklabs.jugnoo.driver;

/**
 * Created by Shankar on 12/1/2015.
 */
public interface Constants {

    int REQUEST_OVERLAY_PERMISSION = 3564;


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
    String KEY_USER_IDENTIFIER = "user_identifier";
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
    String KEY_CURRENT_LATITUDE_ALARM = "current_latitude_alarm";
    String KEY_CURRENT_LONGITUDE_ALARM = "current_longitude_alarm";

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
	String KEY_FALSE_DELIVERY = "false_delivery";
	String KEY_LOADING_STATUS = "loading_status";
	String KEY_IS_LOADING = "is_loading";
	String KEY_IS_UNLOADING = "is_unloading";
    String EMPTY_OBJECT = "{}";
    String KEY_IS_DELIVERY = "is_delivery";
	String KEY_IS_DELIVERY_POOL = "is_delivery_pool";
    String KEY_DISTANCE_TRAVELLED = "distance_travelled";
	String KEY_RING_TYPE = "ring_type";

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
	String KEY_ESTIMATED_DISTANCE = "estimated_distance";
    String KEY_VENDOR_MESSAGE = "vendor_message";
    String KEY_END_DELIVERY_FORCED = "end_delivery_forced";
    String KEY_TOTAL_CASH_TO_COLLECT_DELIVERY = "total_cash_to_collect_delivery";
	String KEY_ESTIMATED_DRIVER_FARE = "estimated_driver_fare";
    String KNOWLARITY_NO = "knowlarity_no";
    String ACCEPTED_STATE_UPDATE_TIME_PERIOD = "accepted_state_update_time_period";
    String FREE_STATE_UPDATE_TIME_PERIOD = "free_state_update_time_period";
    String KEY_RING_COUNT = "ring_count";
	String KEY_RIDE_TYPE = "ride_type";
	String KEY_DROP_POINTS = "drop_points";

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
    String DRIVER_RIDE_EARNING_CURRENCY = "driver_ride_earning_currency";
    String DRIVER_RIDE_DATE = "driver_ride_date";
    String ACTION_UPDATE_RIDE_EARNING = "action_update_ride_earning";
    String ACTION_REFRESH_USL = "action_refresh_usl";
    String ALERT_BATTERY_LOW = "alert_battery_low";
	String ALERT_CHARGING = "alert_charging";
	String DISMISS_END_DELIVERY_POPUP = "dismiss_end_delivery_popup";
	String MOBILE_DATA_STATE = "mobile_data_state";

	String FREE_STATE_UPDATE_TIME_PERIOD_CHARGING = "free_state_update_time_period_charging";
	String FREE_STATE_UPDATE_TIME_PERIOD_CHARGING_V5 = "free_state_update_time_period_charging_v5";

	String FREE_STATE_UPDATE_TIME_PERIOD_NON_CHARGING = "free_state_update_time_period_non_charging";
	String DRIVER_OFFLINE_PERIOD = "driver_offline_period";


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

    String KEY_CHAT_COUNT = "chat_count";
	String KEY_DRIVER_ARRIVED_DISTANCE = "driver_arrived_distance";
	String KEY_DRIVER_START_DISTANCE = "driver_start_distance";
	String KEY_DRIVER_SHOW_ARRIVE_UI_DISTANCE = "driver_show_arrive_ui_distance";
	String UPLOAD_DOCUMENT_MESSAGE = "upload_document_message";
	String UPLOAD_DOCUMENT_DAYS_LEFT = "upload_document_days_left";
	String DIFF_MAX_EARNING = "diff_max_earning";
	String AVERAGE_DRIVER_EARNING = "average_driver_earning";
	String AVERAGE_EARNING_DAYS = "average_earning_days";

	String SP_FIRST_TIME_OPEN = "sp_first_time_open";
	String UPDATE_LOCATION_OFFLINE = "update_location_offline";
	String OFFLINE_UPDATE_TIME_PERIOD = "offline_update_time_period";
	String IS_OFFLINE = "is_offline";
    String END_RIDE_CUSTOM_TEXT = "end_ride_custom_text";

    String PLAN_ID = "plan_id";
    String ACTION_DEVICE_TOKEN_UPDATED = "action.device_token_updated";

	String KEY_CURRENCY = "currency";

	String KEY_COUNTRY_CODE = "country_code";
	String KEY_UPDATED_COUNTRY_CODE = "updated_country_code";
	String KEY_OPERATOR_TOKEN = "operator_token";
	String LOGIN_TYPE = "login_type";
	String KEY_VEHICLE_TYPES = "vehicle_types";
	String UPDATE_MPESA_PRICE="update_mpesa_price";

	String INTENT_ACTION_NEW_MESSAGE = "product.clicklabs.jugnoo.driver.INTENT_ACTION_NEW_MESSAGE";
	String LANGUAGE_PREFERENCE_IN_MENU = "language_preference_in_menu";
	String INVITE_FRIENDS_IN_MENU = "invite_friends_in_menu";
	String DRIVER_RESOURCES_IN_MENU = "driver_resources_in_menu";
	String SUPER_DRIVERS_IN_MENU = "super_drivers_in_menu";
	String INVOICES_IN_MENU = "invoices_in_menu";
	String EARNINGS_IN_MENU = "earnings_in_menu";
	String BANK_DETAILS_IN_EDIT_PROFILE = "bank_details_in_edit_profile";
	String SHOW_PLANS_IN_MENU = "show_plans_in_menu";
	String SHOW_SUPPORT_IN_MENU = "show_support_in_menu";
	String SELF_AUDIT_BUTTON_STATUS = "self_audit_button_status";
	String SHOW_CALL_US_MENU = "show_call_us_menu";
	String SHOW_IN_APP_CALL_US = "show_in_app_call_us";
	String SHOW_RATE_CARD_IN_MENU = "show_rate_card_in_menu";
	String SET_DRIVER_TUTORIAL_STATUS = "set_driver_tutorial_status";
	String KEY_MENU = "menu";
	String KEY_TAG = "tag";
	String CHAT_SUPPORT = "chat_support";
	String TICKET_SUPPORT = "ticket_support";
	String MAIL_SUPPORT = "mail_support";
	String WALLET_BALANCE_IN_EARNING = "wallet_balance_in_earning";

	String FUGU_CHAT_BUNDLE = "fugu_chat_bundle";
	String KEY_REVERSE_BID = "reverse_bid";
	String KEY_BID_PLACED = "bid_placed";
	String KEY_REVERSE_BID_FARE = "reverse_bid_fare";
	String KEY_BID_VALUE = "bid_value";
	String KEY_MESSAGE = "message";
	String KEY_INITIAL_BID_VALUE = "initial_bid_value";
	String KEY_FUGU_APP_KEY = "fugu_app_key";
	String KEY_FUGU_APP_TYPE = "fugu_app_type";
	String ASK_USER_CONFIRMATION = "ask_user_confirmation";
	String KEY_ESTIMATED_TRIP_DISTANCE = "estimated_trip_distance";
	String BID_INCREMENT_PERCENT = "bid_increment_percent";
	String DRIVER_SUPPORT_EMAIL = "driver_support_email";
	String DRIVER_SUPPORT_EMAIL_SUBJECT = "driver_support_email_subject";
	String SUPPORT_MAIN = "support_main";
	String HIPPO_TICKET_FAQ_NAME = "hippo_ticket_faq_name";
	String KEY_SHOW_IN_ACCOUNT = "show_in_account";
	String BRANDING_IMAGES_ONLY = "branding_images_only";
	String BRANDING_IMAGE = "branding_image";
	String KEY_DISTANCE_UNIT = "distance_unit";
	String KEY_DISTANCE_UNIT_FACTOR = "distance_unit_factor";

	String ACTION_FINISH_ACTIVITY = BuildConfig.APPLICATION_ID+".finish_activity";

	String KEY_CREDITS = "credits";
	String DRIVER_CREDITS = "driver_credits";
	String SHOW_MANUAL_RIDE = "self_assign";
    String KEY_SHOW_IN_EARN_CREDITS = "show_in_earn_credits";
    String KEY_AMOUNT = "amount";
    String KEY_REFER_A_DRIVER = "refer_a_driver";
    String KEY_REFER_A_CUSTOMER = "refer_a_customer";
    String KEY_ADVERTISE_WITH_US = "advertise_with_us";
    String KEY_GET_CREDITS = "get_credits";
    String KEY_CREDIT_BALANCE = "credit_balance";
    String KEY_COMMISSION_SAVED = "commission_saved";
	String KEY_REFERRAL_MESSAGE = "referral_message";
	String KEY_REFERRAL_MESSAGE_DRIVER = "referral_message_driver";
	String KEY_REFERRAL_IMAGE_D2D = "referral_image_d2d";
	String KEY_REFERRAL_IMAGE_D2C = "referral_image_d2c";
	String KEY_GET_CREDITS_INFO = "get_credits_info";
	String KEY_GET_CREDITS_IMAGE = "get_credits_image";
	String KEY_SEND_CREDITS_ENABLED = "send_credits_enabled";
	String HIPPO_TAG_DRIVER_APP = "Driver App";

	int NAVIGATION_TYPE_GOOGLE_MAPS = 0;
	int NAVIGATION_TYPE_WAZE = 1;
	String KEY_NAVIGATION_TYPE = "navigation_type";
	String KEY_SHOW_WAZE_TOGGLE = "show_waze_toggle";
	String STRIPE_ACCOUNT_STATUS = "is_stripe_enabled";
	String STRIPE_REDIRECT_URI = "stripe_redirect_uri";
	String KEY_LOGOUT = "logout";
	String CODE = "code";
	String KEY_DEFAULT_LANG = "default_lang";
	String KEY_LOCALE = "locale";
	String KEY_TOLL_CHARGE = "toll_charge";
	String KEY_SHOW_TOLL_CHARGE = "show_toll_charge";
	String KEY_DEVICE_TYPE = "device_type";
	String WALLET = "wallet";
	String SP_OVERLAY_PERMISSION_ASKED = "sp_overlay_permission_asked";
	String KEY_STRIPE_CARDS_ENABLED = "stripe_cards_enabled";

	String NOTIF_CHANNEL_DEFAULT = "Notifications";
	String KEY_CURRENCY_PRECISION = "currency_precision";

	String KEY_DEFAULT_COUNTRY_CODE = "default_country_code";
	String KEY_DEFAULT_SUB_COUNTRY_CODE = "default_sub_country_code";
	String KEY_DEFAULT_COUNTRY_ISO = "default_country_iso";
	String KEY_TIP_AMOUNT = "tip_amount";
	String KEY_MAX_SOUND = "max_sound";
	String KEY_FARE_BREAKDOWN = "fare_breakdown";
	String KEY_VALUE = "value";
	String KEY_SHOW_DETAILS_IN_TAKE_CASH = "show_details_in_take_cash";
    String KEY_SHOW_LUGGAGE_CHARGE = "show_luggage_charges";
    String KEY_LUGGAGE_COUNT = "luggage_count";


	String KEY_FACEBOOK_PAGE_ID = "facebook_page_id";
	String KEY_FACEBOOK_PAGE_URL = "facebook_page_url";
	String KEY_SHOW_TERMS = "show_terms";
	String KEY_SHOW_ABOUT = "show_about";
	String KEY_FACEBOOK_LIKE_ENABLED = "facebook_like_enabled";
	String KEY_SHOW_ARRIVAL_TIMER = "show_arrival_timer";
	String KEY_VEHICLE_MODEL_ENABLED = "vehicle_model_enabled";
	String KEY_SHOW_FAQ = "show_faq";
	String KEY_SHOW_DRIVER_AGREEMENT = "show_driver_agreement";
	String KEY_SHOW_BANK_DEPOSIT = "show_bank_deposit";
	String KEY_SHOW_TOTAL_FARE_AT_RIDE_END = "show_total_fare_at_ride_end";
	String KEY_SHOW_TAKE_CASH_AT_RIDE_END = "show_take_cash_at_ride_end";
	String KEY_SHOW_GRAPH_IN_EARNINGS = "show_graph_in_earnings";

	String KEY_FARE_PER_KM = "fare_per_km";
	String KEY_FARE_PER_MIN = "fare_per_min";
	String KEY_FARE_FIXED = "fare_fixed";
	String KEY_SHOW_EDIT_RATE_CARD = "show_edit_rate_card";
	String KEY_EDIT_PROFILE_IN_HOME_SCREEN = "edit_profile_in_home_screen";
	String KEY_TAX_PERCENTAGE = "tax_percentage";
	String SHOW_UPLOAD_DOCUMENTS = "show_upload_documents";

	String KEY_DRIVER_GET_DIRECTIONS_INTERVAL = "driver_get_directions_interval";
	String KEY_UPDATED_USER_EMAIL = "updated_user_email";

	String KEY_EMAIL_INPUT_AT_SIGNUP = "email_input_at_signup";
	String KEY_GENDER_INPUT_AT_SIGNUP = "gender_input_at_signup";
	String KEY_DOB_INPUT_AT_SIGNUP = "dob_input_at_signup";
	String KEY_EMAIL_EDITABLE_IN_PROFILE = "email_editable_in_profile";
	String KEY_USERNAME_EDITABLE_IN_PROFILE = "username_editable_in_profile";
	String SP_LAST_PHONE_NUMBER_SAVED = "last_phone_number_saved";
	String SP_LAST_COUNTRY_CODE_SAVED = "last_country_code_saved";
	String KEY_ENABLE_VEHICLE_SETS = "enable_vehicle_sets";
	String KEY_ENABLE_VEHICLE_EDIT_SETTING = "enable_vehicle_edit_setting";
	String KEY_MAX_SPEED_THRESHOLD = "max_speed_threshold";
	String KEY_REGION_ID = "region_id";
	String KEY_BATTERY_CHECK_ACCEPT = "driver_battery_check_accept";
	String KEY_BATTERY_CHECK_START = "driver_battery_check_start";

	String KEY_USE_DIRECTIONS_API_FOR_METERING = "driver_use_directions_api_for_metering";
	String KEY_ENABLE_WAYPOINTS_DISTANCE_CALCULATION = "driver_enable_waypoints_distance_calculation";
	String KEY_WAYPOINTS_COLLECTION_INTERVAL = "driver_waypoints_collection_interval";
	String KEY_WAYPOINTS_RETRY_COUNT_AT_ENDRIDE = "driver_waypoints_retry_count_at_endride";
	String KEY_USE_WAYPOINT_DISTANCE_FOR_FARE = "driver_use_waypoint_distance_for_fare";

	String KEY_TOTAL_DISTANCE_MAX = "driver_total_distance_max";
	String KEY_DELTA_DISTANCE_MAX = "driver_delta_distance_max";
	String KEY_WAYPOINT_DISTANCE = "waypoint_distance";
	String KEY_METERING_DISTANCE = "metering_distance";

	String KEY_WAYPOINTS = "waypoints";
	String KEY_EARNINGS_AS_HOME = "driver_show_earnings_as_home";
	String KEY_SHOW_DROP_ADDRESS_BEFORE_INRIDE = "driver_show_drop_address_before_inride";
	String KEY_PICKUP_ADDRESS = "pickup_address";
	String KEY_API_NAME = "api_name";
	String KEY_DRIVER_GOOGLE_APIS_LOGGING = "driver_google_apis_logging";
	String SP_RECEIVER_LAST_LOCATION_TIME = "sp_receiver_last_location_time";

	String KEY_DRIVER_AUTO_ZOOM_ENABLED = "driver_auto_zoom_enabled";

	String KEY_TOLL_NAME = "toll_name";
	String KEY_TOLL = "toll";
	String KEY_MODIFIED_TOLL = "modified_toll";
	String KEY_TOLL_VISIT_ID = "toll_visit_id";
	String KEY_UPDATED_TOLL = "updated_toll";
	String SP_BATTERY_OPTIMIZATIONS_ASKED = "sp_battery_optimizations_asked";
	String KEY_DRIVER_CHECK_LOCALE_FOR_ADDRESS = "driver_check_locale_for_address";
	String KEY_IS_CORPORATE_RIDE = "is_corporate_ride";
	String KEY_CUSTOMER_NOTE = "customer_note";
	String KEY_DRIVER_EMAIL_OPTIONAL = "driver_email_optional";
	String KEY_GENDER_OPTIONAL = "gender_optional";
	String KEY_DOB_OPTIONAL = "dob_optional";
	String KEY_DRIVER_MAX_BID_MULTIPLIER = "driver_max_bid_multiplier";
	String KEY_DRIVER_GOOGLE_TRAFFIC_ENABLED = "driver_google_traffic_enabled";

	String KEY_DRIVER_SHOW_POOL_REQUEST_DEST = "driver_show_pool_request_dest";
	String DOB_DATE_FORMAT = "dd-MM-yyyy";
}


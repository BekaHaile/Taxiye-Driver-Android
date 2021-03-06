package product.clicklabs.jugnoo.driver.datastructure;

/**
 * Created by shankar on 5/22/15.
 */
public class SPLabels {

    public static final String SETTINGS_SP = "settingsPref",
        RECEIVE_REQUESTS1 = "receiveRequests";


    // variables for metering service
    public static final String
            LOCATION_LAT = "location_lat",
            LOCATION_LNG = "location_lng",
            TOTAL_DISTANCE = "total_distance",
            LOCATION_TIME = "location_time",
            START_TIME = "start_time",
    		WAIT_TIME = "wait_time",
            TOTAL_HAVERSINE_DISTANCE = "total_haversine_distance";

    public static final String
            BAD_ACCURACY_COUNT = "badAccuracyCount",
            ACCURACY_SAVED_TIME = "accuracySavedTiem",
            TIME_WINDOW_FLAG = "timeWindowFlag";

    public static final String
            NOTIFICATION_ID = "notificationId";
    public static final String
            DISTANCE_RESET_LOG_ID = "distanceResetLogId";

    public static final String
            GPS_REFRESH_TIME = "GpsRefreshTime";

    public static final String SHARING_ENGAGEMENTS_COMPLETED = "sharingEngagementsCompleted";


	public static final String CUSTOM_SERVER_URL = "customServer";

	public static final String DRIVER_SCREEN_MODE = "driverScreenMode";
    public static final String DRIVER_ARRIVED_DISTANCE = "driverArrivedDistance";
    public static final String NOTIFICATION_UNREAD_COUNT = "notification_unread_count";
    public static final String DOWNLOADED_FILE_ID = "downloadFileId";
    public static final String INGNORE_RIDEREQUEST_COUNT = "ignoreRideRequestCount";

    public static final String MAX_INGNORE_RIDEREQUEST_COUNT = "maxIgnoreRideRequestCount";
    public static final String MAX_TIMEOUT_RELIEF = "maxTimeoutRelief";
	public static final String BUFFER_TIMEOUT_PERIOD ="bufferTimeoutPeriod";
	public static final String BUFFER_TIMEOUT_VALUE ="bufferTimeoutValue";
	public static final String DRIVER_TIMEOUT_FLAG = "driverTimeoutFlag";
    public static final String DRIVER_TIMEOUT_FACTOR = "driverTimeoutFactor";
    public static final String DRIVER_TIMEOUT_FACTOR_HIGH = "driverTimeoutFactorHigh";
    public static final String DRIVER_TIMEOUT_TTL = "driverTimeoutTtl";
    public static final String CUSTOMER_PHONE_NUMBER = "customerPhoneNumer";
    public static final String CURRENT_ETA = "currentEta";
	public static final String ETA_EXPIRE = "etaExpire";
	public static final String ON_FINISH_CALLED = "onFinishCalled";

	public static final String PERFECT_ACCEPT_RIDE_DATA = "perfectAcceptRideData";
	public static final String PERFECT_ENGAGEMENT_ID = "perfectEngagementId";
    public static final String PERFECT_CUSTOMER_CONT = "perfectCustomerCont";
    public static final String PERFECT_CUSTOMER_ID = "perfectCustomerId";
    public static final String PERFECT_REFERENCE_ID = "perfectReferenceId";
    public static final String PERFECT_LATITUDE = "perfectLatitude";
    public static final String PERFECT_LONGITUDE = "perfectLongitude";
	public static final String PERFECT_DISTANCE = "perfectDistance";
	public static final String PERFECT_RIDE_REGION_REQUEST_STATUS = "perfectRideRegionRequestStatus";

    public static final String REQUEST_LOGIN_OTP_FLAG = "requestLoginOtpFlag";
    public static final String ACCEPT_RIDE_TIME = "acceptRideTime";
    public static final String NOTIFICATION_SAVE_COUNT = "notification_save_count";
    public static final String SELECTED_LANGUAGE = "selected_language";
    public static final String HEAT_MAP_REFRESH_FREQUENCY = "heat_map_refresh_frequency";

    public static final String PUBNUB_PUBLISHER_KEY = "pubnub_publisher_key";
    public static final String PUBNUB_SUSCRIBER_KEY = "pubnub_suscriber_key";
    public static final String PUBNUB_CHANNEL = "pubnub_channel";
    public static final String LOGIN_VIA_OTP_STATE = "login_via_otp_state";

    public static final String DRIVER_LOGIN_PHONE_NUMBER = "driver_login_phone_number";
    public static final String DRIVER_LOGIN_TIME = "driver_login_time";
	public static final String SEND_RING_COUNT_FREQUENCY = "send_ring_count_frequency";
	public static final String RING_COUNT_FREQUENCY = "ring_count_frequency";
    public static final String CLEAR_APP_CACHE_TIME = "clear_app_cache_time";

	public static final String UPDATE_DRIVER_LOCATION_TIME = "update_driver_location_time";
    public static final String OSRM_ENABLED = "osrm_enabled";


    public static final String GET_USL_STATUS = "get_usl_status";
	public static final String SET_AUDIT_STATUS_POPUP = "set_audit_status_popup";
	public static final String DIGITAL_SIGNATURE_POPUP_STATUS = "digital_signature_popup_status";
	public static final String SET_AUDIT_POPUP_STRING = "set_audit_popup_string";
	public static final String SET_DIGITAL_SIGNATURE_POPUP_STRING = "set_digital_signature_popup_string";

    public static final String ACCEPT_RIDE_VIA_PUSH = "accept_ride_via_push";
	public static final String LAST_DESTINATION ="last_destination";
	public static final String LAST_PICK_UP = "last_pick_up";
	public static final String ADD_HOME = "add_home";
	public static final String ADD_WORK = "add_work";
    public static final String START_RIDE_ALERT_RADIUS = "start_ride_alert_radius";
    public static final String START_RIDE_ALERT_RADIUS_FINAL = "start_ride_alert_radius_final";
    public static final String CHAT_ENABLED = "chat_enabled";
    public static final String DELIVERY_IN_PROGRESS = "delivery_in_progress";
	public static final String CHAT_SIZE = "chat_size";

    // for tutorial screen
    public static final String SET_TRAINING_ID = "training_id";
    public static final String SET_DRIVER_TOUR_STATUS = "tour_status";
    // for tour pref data
    public static final String PREF_TRAINING_ACCESS_TOKEN = "tour_access_token";
    public static final String PREF_TRAINING_ID = "pref_training_id";

	public static final String SHOW_SUPPORT_IN_RESOURCES = "show_support_in_resources";
	public static final String MENU_OPTION_VISIBILITY = "menu_option_visibility";
	public static final String VEHICLE_TYPE = "vehicle_type";
	public static final String CITY_ID = "city_id";

	public static final String FIRST_TIME = "first_time";
}

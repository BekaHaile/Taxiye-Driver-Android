package product.clicklabs.jugnoo.driver.datastructure;

/**
 * Created by shankar on 5/22/15.
 */
public class SPLabels {

    public static final String SETTINGS_SP = "settingsPref",
        RECEIVE_REQUESTS = "receiveRequests";


    // variables for metering service
    public static final String
            LOCATION_LAT = "location_lat",
            LOCATION_LNG = "location_lng",
            TOTAL_DISTANCE = "total_distance",
            LOCATION_TIME = "location_time",
            START_TIME = "start_time",
    		WAIT_TIME = "wait_time",
            TRACKING = "tracking",
            ENGAGEMENT_ID = "engagement_id",
            TOTAL_HAVERSINE_DISTANCE = "total_haversine_distance";

    public static final String
            BAD_ACCURACY_COUNT = "badAccuracyCount",
            ACCURACY_SAVED_TIME = "accuracySavedTiem",
            TIME_WINDOW_FLAG = "timeWindowFlag";

    public static final String
            METERING_STATE = "meteringstate";
    public static final String
            GPS_GSM_DISTANCE_COUNT = "GpsGsmDistanceCount";

    public static final String SHARING_ENGAGEMENTS_COMPLETED = "sharingEngagementsCompleted";
    public static final String HEAT_MAP_RESPONSE = "heatmapresponse";
}

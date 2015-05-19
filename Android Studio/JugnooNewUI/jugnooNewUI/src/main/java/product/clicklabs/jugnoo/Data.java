package product.clicklabs.jugnoo;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;

import java.security.KeyStore;
import java.util.ArrayList;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.CancelOptionsList;
import product.clicklabs.jugnoo.datastructure.DriverInfo;
import product.clicklabs.jugnoo.datastructure.EndRideData;
import product.clicklabs.jugnoo.datastructure.FareStructure;
import product.clicklabs.jugnoo.datastructure.PreviousAccountInfo;
import product.clicklabs.jugnoo.datastructure.ReferralMessages;
import product.clicklabs.jugnoo.datastructure.UserData;
import product.clicklabs.jugnoo.utils.FacebookUserData;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.MySSLSocketFactory;

/**
 * Stores common static data for access for all activities across the application
 * @author shankar
 *
 */
public class Data {
	



	public static final String INVALID_ACCESS_TOKEN = "invalid access token";

	public static final String SHARED_PREF_NAME = "myPref", SETTINGS_SHARED_PREF_NAME = "settingsPref";
	public static final String SP_ACCESS_TOKEN_KEY = "access_token",
			
			SP_TOTAL_DISTANCE = "total_distance", 
			SP_WAIT_TIME = "wait_time",
			SP_RIDE_TIME = "ride_time", 
			SP_RIDE_START_TIME = "ride_start_time", 
			SP_LAST_LATITUDE = "last_latitude",
			SP_LAST_LONGITUDE = "last_longitude"

			
			;
	
	
	public static final String SP_SERVER_LINK = "sp_server_link";
	
	
	
	
	
	
	public static final String LANGUAGE_SELECTED = "language_selected";
	
	
	public static String D_START_RIDE = "D_START_RIDE", D_IN_RIDE = "D_IN_RIDE";
	public static String P_RIDE_END = "P_RIDE_END", P_IN_RIDE = "P_IN_RIDE", P_REQUEST_FINAL = "P_REQUEST_FINAL", 
			P_ASSIGNING = "P_ASSIGNING";
	
	
	public static LatLng startRidePreviousLatLng;
	
	
	
	

	
	
	
	
	
	
	
	// dev review http://107.21.79.63:4001
	//Dev staged :  "http://54.81.229.172:7000";
	
	// Dev staged :   http://54.81.229.172:8000
	
	// Dev Trial :   http://54.81.229.172:8001
	
	// live 1st:    http://dev.jugnoo.in:3000
	// live 2nd:    http://dev.jugnoo.in:4000
	// live 3rd:    http://dev.jugnoo.in:4002
	// review 3:    http://dev.jugnoo.in:4003
	// live 4th:    http://dev.jugnoo.in:4004
	// live 6th:    https://dev.jugnoo.in:4006
	// live 8th:    https://dev.jugnoo.in:4008
	// live 10th:    https://dev.jugnoo.in:4010
	// live 12th:    https://dev.jugnoo.in:4012     app versions: 126, 127, 128, 129, 130
	// live 13th:    https://dev.jugnoo.in:4013
	
	//iOS 4012
	//
	// Dev new dispatcher :   https://54.81.229.172:8012
	
	//https://test.jugnoo.in:8012 to http://54.173.65.120:9000


	
	public static final String SERVER_ERROR_MSG = "Connection lost. Please try again later.";
	public static final String SERVER_NOT_RESOPNDING_MSG = "Connection lost. Please try again later.";
	public static final String CHECK_INTERNET_MSG = "Check your internet connection.";
	
	
	


	public static double latitude, longitude;
	
	
	public static ArrayList<DriverInfo> driverInfos = new ArrayList<DriverInfo>();
	
	
	
	
	public static UserData userData;
	
	public static LocationFetcher locationFetcher;
	

	public static final String DEVICE_TYPE = "0";
	public static String deviceToken = "", country = "", deviceName = "", osVersion = "", uniqueDeviceId = "";
	public static int appVersion;
	
	
	
	public static String cEngagementId = "", cDriverId = "", cSessionId = "";
	public static DriverInfo assignedDriverInfo;
	
	
	
	public static String dEngagementId = "", dCustomerId = "";
	public static LatLng dCustLatLng;
	
	
	public static boolean driversRefreshedFirstTime = false;
	
	
	public static EndRideData endRideData;
	
	public static int customerRateAppFlag = 0;
	
	
	public static LatLng pickupLatLng;

	public static FacebookUserData facebookUserData;
	
	
	
	public static FareStructure fareStructure;
	
	public static CancelOptionsList cancelOptionsList;
	
	public static ReferralMessages referralMessages;

    public static ArrayList<PreviousAccountInfo> previousAccountInfoList = new ArrayList<PreviousAccountInfo>();
	
	
	
	public static void clearDataOnLogout(Context context){
		try{
			driverInfos = new ArrayList<DriverInfo>();
			userData = null;
			locationFetcher = null;
			deviceToken = ""; country = ""; deviceName = ""; appVersion = 0; osVersion = "";
			cEngagementId = ""; cDriverId = "";
			assignedDriverInfo = null;
			pickupLatLng = null;
			facebookUserData = null;
			
			AccessTokenGenerator.saveLogoutToken(context);
			
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	


	
	public static AsyncHttpClient mainClient;
	
	public static final int SOCKET_TIMEOUT = 30000;
	public static final int CONNECTION_TIMEOUT = 30000;
	public static final int MAX_RETRIES = 0;
	public static final int RETRY_TIMEOUT = 1000;
	
	public static AsyncHttpClient getClient() {
		if (mainClient == null) {
			mainClient = Config.getAsyncHttpClient();
			try {
				KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
				trustStore.load(null, null);
				MySSLSocketFactory sf = new MySSLSocketFactory(trustStore);
				sf.setHostnameVerifier(MySSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
				mainClient.setSSLSocketFactory(sf);
			} catch (Exception e) {
				Log.e("exception in https hostname", "="+e.toString());
			}
			mainClient.setConnectTimeout(CONNECTION_TIMEOUT);
			mainClient.setResponseTimeout(SOCKET_TIMEOUT);
			mainClient.setMaxRetriesAndTimeout(MAX_RETRIES, RETRY_TIMEOUT);
		}
		return mainClient;
	}
	
	

	
}

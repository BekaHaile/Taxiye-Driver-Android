package product.clicklabs.jugnoo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.util.Base64;

import com.androidquery.callback.ImageOptions;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.maps.model.LatLng;

/**
 * Stores common static data for access for all activities across the application
 * @author shankar
 *
 */
public class Data {
	
	public static final String SHARED_PREF_NAME = "myPref";
	public static final String SP_ACCESS_TOKEN_KEY = "access_token", SP_ID_KEY = "session_id",
			
			SP_TOTAL_DISTANCE = "total_distance", 
			SP_WAIT_TIME = "wait_time",
			SP_LAST_LATITUDE = "last_latitude",
			SP_LAST_LONGITUDE = "last_longitude",
			
			SP_DRIVER_SCREEN_MODE = "driver_screen_mode", 
			
			SP_D_ENGAGEMENT_ID = "d_engagement_id", 
			SP_D_LATITUDE = "d_latitude",
			SP_D_LONGITUDE = "d_longitude",
			SP_D_CUSTOMER_ID = "d_customer_id",
			SP_D_CUSTOMER_NAME = "d_customer_name", 
			SP_D_CUSTOMER_IMAGE = "d_customer_image", 
			SP_D_CUSTOMER_PHONE = "d_customer_phone", 
			
			SP_D_NEW_RIDE_REQUEST = "d_new_ride_request",
			
			SP_D_NR_ENGAGEMENT_ID = "d_nr_engagement_id",
			SP_D_NR_USER_ID = "d_nr_user_id",
			SP_D_NR_LATITUDE = "d_nr_latitude",
			SP_D_NR_LONGITUDE = "d_nr_longitude",
			
			
			
			SP_CUSTOMER_SCREEN_MODE = "customer_screen_mode",
			
			SP_C_ENGAGEMENT_ID = "c_engagement_id",
			SP_C_DRIVER_ID = "c_driver_id",
			SP_C_LATITUDE = "c_latitude",
			SP_C_LONGITUDE = "c_longitude",
			SP_C_DRIVER_NAME = "c_driver_name",
			SP_C_DRIVER_IMAGE = "c_driver_image",
			SP_C_DRIVER_CAR_IMAGE = "c_driver_car_image",
			SP_C_DRIVER_PHONE = "c_driver_phone",
			SP_C_DRIVER_DISTANCE = "c_driver_distance",
			SP_C_DRIVER_DURATION = "c_driver_duration",
			
			SP_C_TOTAL_DISTANCE = "c_total_distance",
			SP_C_TOTAL_FARE = "c_total_fare",
			SP_C_WAIT_TIME = "c_wait_time"
			
			;
	
	public static String D_START_RIDE = "D_START_RIDE", D_IN_RIDE = "D_IN_RIDE";
	public static String P_RIDE_END = "P_RIDE_END", P_IN_RIDE = "P_IN_RIDE", P_REQUEST_FINAL = "P_REQUEST_FINAL";
	
	public static LatLng startRidePreviousLatLng;
	
	
	public static final int SERVER_TIMEOUT = 60000;

	
	//"http://54.81.229.172:7000";
	// staged http://54.81.229.172:8000
	public static final String SERVER_URL = "http://54.81.229.172:7000";
	
	public static final String SERVER_ERROR_MSG = "Server error. Please try again later.";
	public static final String SERVER_NOT_RESOPNDING_MSG = "Oops!! Server not responding. Please try again later.";
	public static final String CHECK_INTERNET_MSG = "Check your internet connection.";
	
	
	
	public static final String GOOGLE_PROJECT_ID = "506849624961";

	public static final String MAPS_BROWSER_KEY = "AIzaSyAHVDCyeC13xO_GxG5zE8_wbRJolqkBg90";
	
	public static final String FACEBOOK_APP_ID = "782131025144439";
	
	
	
	public static double latitude = 30.7500, longitude = 76.7800;
	
	public static LatLng chandigarhLatLng = new LatLng(30.7500, 76.7800);
	
	
	
	public static ArrayList<DriverInfo> driverInfos = new ArrayList<DriverInfo>();
	
	public static ArrayList<FavoriteLocation> favoriteLocations = new ArrayList<FavoriteLocation>();
	
	public static ArrayList<Booking> bookings = new ArrayList<Booking>();
	
	public static ArrayList<FriendInfo> friendInfos = new ArrayList<FriendInfo>();
	public static ArrayList<FriendInfo> friendInfosDuplicate = new ArrayList<FriendInfo>();
	
	
	
	
	public static UserData userData;
	
	public static LocationFetcher locationFetcher;
	

	public static String deviceToken = "", country = "", deviceName = "", appVersion = "", osVersion = "";
	
	
	
	public static String cEngagementId = "", cDriverId = "";
	
	public static DriverInfo assignedDriverInfo;
	
	
	
	public static String dEngagementId = "", dCustomerId = "";
	public static LatLng dCustLatLng;
	
	public static ArrayList<DriverRideRequest> driverRideRequests = new ArrayList<DriverRideRequest>();
	
	public static CustomerInfo assignedCustomerInfo;
	
	public static boolean driversRefreshedFirstTime = false;
	
	
	public static double totalDistance = 0, totalFare = 0;
	public static String waitTime = "";
	
	
	public static LatLng mapTarget;

	public static String fbAccessToken = "", fbId = "", fbFirstName = "", fbLastName = "", fbUserName = "", fbUserEmail = "";
	
	
	
	public static LatLng getChandigarhLatLng(){
		if(chandigarhLatLng == null){
			chandigarhLatLng = new LatLng(30.7500, 76.7800);
		}
		return chandigarhLatLng;
	}
	
	
	
	public static void clearDataOnLogout(Context context){
		try{
			latitude = 30.7500; longitude = 76.7800;
			chandigarhLatLng = new LatLng(30.7500, 76.7800);
			driverInfos = new ArrayList<DriverInfo>();
			favoriteLocations = new ArrayList<FavoriteLocation>();
			friendInfos = new ArrayList<FriendInfo>();
			friendInfosDuplicate = new ArrayList<FriendInfo>();
			userData = null;
			locationFetcher = null;
			deviceToken = ""; country = ""; deviceName = ""; appVersion = ""; osVersion = "";
			cEngagementId = ""; cDriverId = "";
			assignedDriverInfo = null;
			mapTarget = null;
			fbAccessToken = ""; fbId = ""; fbFirstName = ""; fbLastName = ""; fbUserName = ""; fbUserEmail = "";
			
			SharedPreferences pref = context.getSharedPreferences(Data.SHARED_PREF_NAME, 0);
			Editor editor = pref.edit();
			editor.putString(Data.SP_ACCESS_TOKEN_KEY, "");
			editor.clear();
			editor.commit();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	public static ImageOptions imageOptionsRound(){
		ImageOptions options = new ImageOptions();
		 options.round = 10;
		 options.memCache = true;
		 options.fileCache = true;
		 return options;
	}
	
	public static ImageOptions imageOptionsFullRound(){
		ImageOptions options = new ImageOptions();
		 options.round = 500;
		 options.memCache = true;
		 options.fileCache = true;
		 return options;
	}
	
	public static ImageOptions imageOptions(){
		ImageOptions options = new ImageOptions();
		 options.memCache = true;
		 options.fileCache = true;
		 return options;
	}
	
//	
//	/**
//	 * Function to register device with Google Cloud Messaging Services and receive Device Token
//	 * @param context application context
//	 */
//	public static void registerForGCM(Context context){
//		try { // registering GCM services
//			GCMRegistrar.checkManifest(context);
//			Data.deviceToken = GCMRegistrar.getRegistrationId(context);
//			if (Data.deviceToken.equals("")) {
//				GCMRegistrar.register(context, Data.GOOGLE_PROJECT_ID);
//				Data.deviceToken = GCMRegistrar.getRegistrationId(context);
//				Log.i("deviceToken in if", ">" + Data.deviceToken);
//			} else {
//				Log.i("GCM", "Already registered");
//				Log.i("deviceToken....in else", ">" + Data.deviceToken);
//				Log.i("deviceToken....length", ">"+Data.deviceToken.length());
//				
//			}
//		} catch (Exception e) {
//			Log.e("exception GCM", ""+e.toString());
//		}
//	}
	
	
	
	
	public static void generateKeyHash(Context context){
		try { // single sign-on for fb application
			PackageInfo info = context.getPackageManager().getPackageInfo(
					"product.clicklabs.jugnoo",
					PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.e("KeyHash:",
						","
								+ Base64.encodeToString(md.digest(),
										Base64.DEFAULT));
			}
		} catch (NameNotFoundException e) {
			Log.e("error:", "," + e.toString());
		} catch (NoSuchAlgorithmException e) {
			Log.e("error:", "," + e.toString());
		}
	}
	
	
	
	public static Typeface regular;																// fonts declaration
	

	public static Typeface regularFont(Context appContext) {											// accessing fonts functions
		if (regular == null) {
			regular = Typeface.createFromAsset(appContext.getAssets(), "fonts/lato_regular.ttf");
		}
		return regular;
	}
	
}
package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.text.TextUtils;
import android.util.Base64;

import com.fugu.CaptureUserData;
import com.fugu.FuguColorConfig;
import com.fugu.FuguConfig;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Locale;

import product.clicklabs.jugnoo.driver.datastructure.CancelOption;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.datastructure.DriverScreenMode;
import product.clicklabs.jugnoo.driver.datastructure.EngagementStatus;
import product.clicklabs.jugnoo.driver.datastructure.FareStructure;
import product.clicklabs.jugnoo.driver.datastructure.PreviousAccountInfo;
import product.clicklabs.jugnoo.driver.datastructure.UserData;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryReturnOption;
import product.clicklabs.jugnoo.driver.fugu.FuguColorConfigStrings;
import product.clicklabs.jugnoo.driver.home.CustomerInfoPaperUtil;
import product.clicklabs.jugnoo.driver.support.SupportOption;
import product.clicklabs.jugnoo.driver.utils.AuthKeySaver;
import product.clicklabs.jugnoo.driver.utils.DeviceUniqueID;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;

/**
 * Stores common static data for access for all activities across the application
 * @author shankar
 *
 */
public class Data {

	public static final String STATIC_FLURRY_KEY = "8CN4DKPSN69HNRSJKKF3";
    public static final int MINI_BALANCE = 50;

    public static String FLURRY_KEY = "8CN4DKPSN69HNRSJKKF3";

	public static final String INVALID_ACCESS_TOKEN = "invalid access token";

	public static final String DEBUG_PASSWORD = "3131", REGISTER_PASSWORD = "1485",
                                DEBUG_PASSWORD_TEST = "4343";

	public static final String SHARED_PREF_NAME = "myPref", SETTINGS_SHARED_PREF_NAME = "settingsPref";
	public static final String SP_ACCESS_TOKEN_KEY = "access_token";
	public static final String SP_SERVER_LINK = "sp_server_link";
	private static int multipleVehiclesEnabled=0;
	private static int driverMappingIdOnBoarding=-1;

	public static String getGpsDeviceImeiNo() {
		return GPS_DEVICE_IMEI_NO;
	}
	public static void setGpsDeviceImeiNo(String imei){
		Data.GPS_DEVICE_IMEI_NO = imei;
	}

	private static String GPS_DEVICE_IMEI_NO = "device_imei_no";

	public static int getExternalGpsEnabled() {
		return externalGpsEnabled;
	}

	public static void setExternalGpsEnabled(int externalGpsEnabled) {
		Data.externalGpsEnabled = externalGpsEnabled;
	}

	private static int externalGpsEnabled = 0;

	public static int getGpsPreference() {
		return gpsPreference;
	}

	public static void setGpsPreference(int gpsPreference) {
		Data.gpsPreference = gpsPreference;
	}

	private static int gpsPreference = 0;

	public static int getMultipleVehiclesEnabled() {
		return multipleVehiclesEnabled;
	}

	public static int getDriverMappingIdOnBoarding() {
		return driverMappingIdOnBoarding;
	}
	public static void setDriverMappingIdOnBoarding(int driverMappingId) {
		Data.driverMappingIdOnBoarding = driverMappingId;
	}

	public static void setMultipleVehiclesEnabled(int multipleVehiclesEnabled) {
		Data.multipleVehiclesEnabled = multipleVehiclesEnabled;
	}
	//TODO
	public static final String DEV_SERVER_URL = "https://prod.taxiye.com:8012";
	public static final String LIVE_SERVER_URL = BuildConfig.LIVE_URL;
	public static final String TRIAL_SERVER_URL = "https://prod.taxiye.com:8200";

    public static final String DEV_1_SERVER_URL = "https://prod.taxiye.com:8013";
    public static final String DEV_2_SERVER_URL = "https://prod.taxiye.com:8014";
    public static final String DEV_3_SERVER_URL = "https://prod.taxiye.com:8015";

    public static final String CHAT_URL_DEV = "https://prod.taxiye.com:8095";
    public static final String CHAT_URL_LIVE = "https://prod.taxiye.com:4010";

	public static final String DEFAULT_SERVER_URL = LIVE_SERVER_URL;

	public static final String JUNGLE_MAPS_SERVER_URL = "http://nominatim-api-live.jungleworks.com";
	public static final String BRANCH_SERVER_URL = "https://api.branch.io/v1";




//for live



	public static String SERVER_URL = DEFAULT_SERVER_URL;





	public static final String CLIENT_ID = "EEBUOvQq7RRJBxJm";
	public static final String LOGIN_TYPE = "1";




	public static final String SERVER_ERROR_MSG = "Connection lost. Please try again later.";
	public static final String SERVER_NOT_RESOPNDING_MSG = "Connection lost. Please try again later.";
	public static final String TOUR_FAILED_MSG = "Connection lost. Please try again.";
	public static final String CHECK_INTERNET_MSG = "Check your internet connection.";
	public static final String GADDAR_JUGNOO_APP = "com.autoncab.driver";
	public static final String UBER_APP = "com.ubercab.driver";

	public static final String GOOGLE_PROJECT_ID = "506849624961";



	public static double latitude, longitude;






	public static UserData userData;

	public static LocationFetcher locationFetcher;


	public static final String DEVICE_TYPE = "0";
	public static String deviceToken = "",
			country = "", deviceName = "", osVersion = "", uniqueDeviceId = "";
	public static int appVersion = BuildConfig.VERSION_CODE;


	public static ArrayList<CancelOption> cancelOptionsList;
	public static ArrayList<DeliveryReturnOption> deliveryReturnOptionList;

	private static String dEngagementId = "";

	private static ArrayList<CustomerInfo> assignedCustomerInfos;



	public static LatLng nextPickupLatLng;
	public static String nextCustomerName;



	public static FareStructure fareStructure;

    public static ArrayList<PreviousAccountInfo> previousAccountInfoList = new ArrayList<>();

	public static JSONArray blockAppPackageNameList;

	public static boolean appMinimized = false;



	public static void clearDataOnLogout(Context context){
		try{
			userData = null;


			Prefs.with(context).remove(Constants.KEY_NAVIGATION_TYPE);
			Prefs.with(context).remove(Constants.SP_OVERLAY_PERMISSION_ASKED);

			AuthKeySaver.writeAuthToFile(context, "");
			SharedPreferences pref = context.getSharedPreferences(Data.SHARED_PREF_NAME, Context.MODE_PRIVATE);
			Editor editor = pref.edit();
			editor.clear();
			editor.apply();
			Prefs.with(context).save(Constants.IS_OFFLINE, 1);
			FuguConfig.clearFuguData((Activity) context);
		} catch(Exception e){
			e.printStackTrace();
		}
	}






	//Ud5ggfKqOCYcpGV+0KijA3ZJW+c=
	public static void generateKeyHash(Context context){
		try { // single sign-on for fb application
			PackageInfo info = context.getPackageManager().getPackageInfo(
					"product.clicklabs.jugnoo.driver",
					PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.e("KeyHash:", ","
								+ Base64.encodeToString(md.digest(),
										Base64.DEFAULT));
			}
		} catch (Exception e) {
			Log.e("error:", "," + e.toString());
		}
	}




	public static void filldetails(Context context){
		try {																						// to get AppVersion, OS version, country code and device name
//			Data.appVersion = Utils.getAppVersion(context);
			Log.i("appVersion", Data.appVersion + "..");
			Data.osVersion = android.os.Build.VERSION.RELEASE;
			Log.i("osVersion", Data.osVersion + "..");
			Data.country = context.getResources().getConfiguration().locale.getDisplayCountry(Locale.getDefault());
			Log.i("countryCode", Data.country + "..");
			Data.deviceName = Utils.getDeviceName();
			Log.i("deviceName", Data.deviceName + "..");

			Data.uniqueDeviceId = DeviceUniqueID.getCachedUniqueId(context);
			Log.i("uniqueDeviceId", Data.uniqueDeviceId);
		} catch (Exception e) {
			Log.e("error in fetching appversion and gcm key", ".." + e.toString());
		}
	}





	public static ArrayList<CustomerInfo> getAssignedCustomerInfos(){
		if(assignedCustomerInfos == null){
			assignedCustomerInfos = CustomerInfoPaperUtil.INSTANCE.readCustomerInfos();
		}
		return assignedCustomerInfos;
	}

	public static void saveAssignedCustomers(){
		Log.e("Data", "saveAssignedCustomers");
		if(assignedCustomerInfos != null){
			CustomerInfoPaperUtil.INSTANCE.writeCustomerInfos(assignedCustomerInfos);
		}
	}
	public static void clearAssignedCustomers(){
		if(assignedCustomerInfos == null){
			assignedCustomerInfos = new ArrayList<>();
		}
		assignedCustomerInfos.clear();
		CustomerInfoPaperUtil.INSTANCE.writeCustomerInfos(assignedCustomerInfos);
	}

	public static ArrayList<CustomerInfo> getAssignedCustomerInfosListForStatus(int status){
		if(assignedCustomerInfos != null) {
			ArrayList<CustomerInfo> customerInfos = new ArrayList<>();
			for (CustomerInfo customerInfo : assignedCustomerInfos) {
				if (customerInfo.getStatus() == status) {
					customerInfos.add(customerInfo);
				}
			}
			return customerInfos;
		} else{
			return null;
		}
	}

	public static ArrayList<CustomerInfo> getAssignedCustomerInfosListForEngagedStatus(){
		if(assignedCustomerInfos != null) {
			ArrayList<CustomerInfo> customerInfos = new ArrayList<>();
			for (CustomerInfo customerInfo : assignedCustomerInfos) {
				if (customerInfo.getStatus() == EngagementStatus.ACCEPTED.getOrdinal()
						|| customerInfo.getStatus() == EngagementStatus.ARRIVED.getOrdinal()
						|| customerInfo.getStatus() == EngagementStatus.STARTED.getOrdinal()) {
					customerInfos.add(customerInfo);
				}
			}
			return customerInfos;
		} else{
			return null;
		}
	}

	public static void clearAssignedCustomerInfosListForStatus(int status){
		if(getAssignedCustomerInfos() != null) {
			for(int i=0; i<getAssignedCustomerInfos().size(); i++){
				if(getAssignedCustomerInfos().get(i).getStatus() == status){
					getAssignedCustomerInfos().remove(i);
					i--;
				}
			}
			Log.e("Data", "clearAssignedCustomerInfosListForStatus");
			saveAssignedCustomers();
			if(HomeActivity.appInterruptHandler != null){
				HomeActivity.appInterruptHandler.updateCustomers();
			}
		}
	}

	public static void clearAssignedCustomerInfosListForStatusWithDelivery(int status, int isDelivery){
		if(getAssignedCustomerInfos() != null) {
			for(int i=0; i<getAssignedCustomerInfos().size(); i++){
				if(getAssignedCustomerInfos().get(i).getStatus() == status
						&& getAssignedCustomerInfos().get(i).getIsDelivery() == isDelivery){
					getAssignedCustomerInfos().remove(i);
					i--;
				}
			}
			Log.e("Data", "clearAssignedCustomerInfosListForStatusWithDelivery");
			saveAssignedCustomers();
			if(HomeActivity.appInterruptHandler != null){
				HomeActivity.appInterruptHandler.updateCustomers();
			}
		}
	}

	public static CustomerInfo getCustomerInfo(String engagementId){
		try {
			int index = getAssignedCustomerInfos().indexOf(new CustomerInfo(Integer.parseInt(engagementId)));
			if(index > -1){
				return getAssignedCustomerInfos().get(index);
			}
		} catch (Exception ignored) {}
		return null;
	}

	public static void addCustomerInfo(CustomerInfo customerInfo){
		if(getAssignedCustomerInfos() != null) {
			if (getAssignedCustomerInfos().contains(customerInfo)) {
				int index = getAssignedCustomerInfos().indexOf(customerInfo);
				getAssignedCustomerInfos().remove(index);
				getAssignedCustomerInfos().add(index, customerInfo);
			} else {
				getAssignedCustomerInfos().add(customerInfo);
			}
			Log.e("Data", "addCustomerInfo");
			saveAssignedCustomers();
			if(HomeActivity.appInterruptHandler != null){
				HomeActivity.appInterruptHandler.updateCustomers();
			}
		}
	}

	public static boolean removeCustomerInfo(int engagementId){
		if(getAssignedCustomerInfos() != null) {
			int index = getAssignedCustomerInfos().indexOf(new CustomerInfo(engagementId));
			if (index > -1) {
				getAssignedCustomerInfos().remove(index);
				Log.e("Data", "removeCustomerInfo");
				saveAssignedCustomers();
				if (HomeActivity.appInterruptHandler != null) {
					HomeActivity.appInterruptHandler.updateCustomers();
				}
				return true;
			}
		}
		return false;
	}

	public static void setCustomerState(String engagementId, DriverScreenMode driverScreenMode){
		CustomerInfo customerInfo = getCustomerInfo(engagementId);
		if(customerInfo != null){
			customerInfo.setStatus(getEngagementStatusFromDriverScreenMode(driverScreenMode).getOrdinal());
		}
		Log.e("Data", "setCustomerState");
		saveAssignedCustomers();
	}


	public static DriverScreenMode getCurrentState(){
		CustomerInfo currentCustomerInfo = getCurrentCustomerInfo();
		if(currentCustomerInfo == null){
			if(getAssignedCustomerInfosListForEngagedStatus() != null
					&& getAssignedCustomerInfosListForEngagedStatus().size() > 0){
				currentCustomerInfo = getAssignedCustomerInfosListForEngagedStatus().get(0);
				setCurrentEngagementId(String.valueOf(currentCustomerInfo.getEngagementId()));
				return getDriverScreenModeFromEngagementStatus(currentCustomerInfo.getStatus());
			}
		} else{
			return getDriverScreenModeFromEngagementStatus(currentCustomerInfo.getStatus());
		}
		return DriverScreenMode.D_INITIAL;
	}

	public static CustomerInfo getCurrentCustomerInfo(){
		return getCustomerInfo(Data.getCurrentEngagementId());
	}

	public static String getCurrentEngagementId(){
		if(TextUtils.isEmpty(Data.dEngagementId)){
			ArrayList<CustomerInfo> customerInfos = getAssignedCustomerInfosListForEngagedStatus();
			if(customerInfos.size() > 0){
				Data.dEngagementId = String.valueOf(customerInfos.get(0).getEngagementId());
			}
		}
		return Data.dEngagementId;
	}

	public static void setCurrentEngagementId(String engagementId){
		Data.dEngagementId = engagementId;
	}



	public static EngagementStatus getEngagementStatusFromDriverScreenMode(DriverScreenMode driverScreenMode){
		switch(driverScreenMode){
			case D_ARRIVED:
				return EngagementStatus.ACCEPTED;

			case D_START_RIDE:
				return EngagementStatus.ARRIVED;

			case D_IN_RIDE:
				return EngagementStatus.STARTED;

			case D_RIDE_END:
				return EngagementStatus.ENDED;

			case D_BEFORE_END_OPTIONS:
				return EngagementStatus.STARTED;

			case D_REQUEST_ACCEPT:
				return EngagementStatus.REQUESTED;

			default:
				return EngagementStatus.REQUESTED;
		}
	}

	public static DriverScreenMode getDriverScreenModeFromEngagementStatus(int status){
		if(EngagementStatus.ACCEPTED.getOrdinal() == status){
			return DriverScreenMode.D_ARRIVED;
		}
		else if(EngagementStatus.ARRIVED.getOrdinal() == status){
			return DriverScreenMode.D_START_RIDE;
		}
		else if(EngagementStatus.STARTED.getOrdinal() == status){
			return DriverScreenMode.D_IN_RIDE;
		}
		else if(EngagementStatus.ENDED.getOrdinal() == status){
			return DriverScreenMode.D_RIDE_END;
		}
		else{
			return DriverScreenMode.D_INITIAL;
		}
	}

	public static ArrayList<SupportOption> getSupportOptions() {
		return supportOptions;
	}

	public static void setSupportOptions(ArrayList<SupportOption> supportOptions) {
		Data.supportOptions = supportOptions;
	}
	public static ArrayList<SupportOption> getCreditOptions() {
		return creditOptions;
	}

	public static void setCreditOptions(ArrayList<SupportOption> creditOptions) {
		Data.creditOptions = creditOptions;
	}

	public static interface TxnType {
		public static final int CREDITED = 1;
		public static final int DEBITED = 2;
		int DEBT = -1;
	}

	public static boolean isCaptive(){
		return Data.userData!=null && Data.userData.isCaptiveDriver!=null && Data.userData.isCaptiveDriver==1;
	}
	public static void setCaptive(boolean isCaptiveDriver){
		Data.userData.isCaptiveDriver = isCaptiveDriver?1:0;
	}

	public static CaptureUserData getFuguUserData(Context context) {
		if (Data.userData == null)
			return null;

		return new CaptureUserData.Builder()
				.userUniqueKey(Data.userData.getUserIdentifier())
				.fullName(Data.userData.userName)
				.phoneNumber(Data.userData.phoneNo)
				.latitude(LocationFetcher.getSavedLatFromSP(context))
				.longitude(LocationFetcher.getSavedLngFromSP(context))
				.build();
	}

	public static  void initFugu(Activity activity, CaptureUserData captureUserData,
								 String keyFromServer, int appTypeFromServer) {
		if (Data.userData!= null && captureUserData != null) {
			if(Data.SERVER_URL.equalsIgnoreCase(Data.LIVE_SERVER_URL)){
				if(TextUtils.isEmpty(keyFromServer) || "null".equalsIgnoreCase(keyFromServer)){
					keyFromServer = activity.getString(R.string.fugu_app_key);
				}
				FuguConfig.init(String.valueOf(appTypeFromServer), keyFromServer, activity, "live", captureUserData, activity.getString(R.string.file_provider));
			} else {
				if(TextUtils.isEmpty(keyFromServer) || "null".equalsIgnoreCase(keyFromServer)){
					keyFromServer = activity.getString(R.string.fugu_app_key_test);
				}
				FuguConfig.init(String.valueOf(appTypeFromServer), keyFromServer, activity, "test", captureUserData, activity.getString(R.string.file_provider));
			}

//			FuguConfig.getInstance().setHomeUpIndicatorDrawableId(R.drawable.ic_profile);

			FuguColorConfig fuguColorConfig = new FuguColorConfig.Builder()
					.fuguActionBarBg(FuguColorConfigStrings.FUGU_ACTION_BAR_BG)
					.fuguActionBarText(FuguColorConfigStrings.FUGU_ACTION_BAR_TEXT)
					.fuguBgMessageYou(FuguColorConfigStrings.FUGU_BG_MESSAGE_YOU)
					.fuguBgMessageFrom(FuguColorConfigStrings.FUGU_BG_MESSAGE_FROM)
					.fuguPrimaryTextMsgYou(FuguColorConfigStrings.FUGU_PRIMARY_TEXT_MSG_YOU)
					.fuguMessageRead(FuguColorConfigStrings.FUGU_MESSAG_EREAD)
					.fuguPrimaryTextMsgFrom(FuguColorConfigStrings.FUGU_PRIMARY_TEXT_MSG_FROM)
					.fuguSecondaryTextMsgYou(FuguColorConfigStrings.FUGU_SECONDARY_TEXT_MSG_YOU)
					.fuguSecondaryTextMsgFrom(FuguColorConfigStrings.FUGU_SECONDARY_TEXT_MSG_FROM)
					.fuguTextColorPrimary(FuguColorConfigStrings.FUGU_TEXT_COLOR_PRIMARY)
					.fuguChannelDateText(FuguColorConfigStrings.FUGU_CHANNEL_DATE_TEXT)
					.fuguChatBg(FuguColorConfigStrings.FUGU_CHAT_BG)
					.fuguBorderColor(FuguColorConfigStrings.FUGU_BORDER_COLOR)
					.fuguChatDateText(FuguColorConfigStrings.FUGU_CHAT_DATE_TEXT)
					.fuguThemeColorPrimary(FuguColorConfigStrings.FUGU_THEME_COLOR_PRIMARY)
					.fuguThemeColorSecondary(FuguColorConfigStrings.FUGU_THEME_COLOR_SECONDARY)
					.fuguTypeMessageBg(FuguColorConfigStrings.FUGU_TYPE_MESSAGE_BG)
					.fuguTypeMessageHint(FuguColorConfigStrings.FUGU_TYPE_MESSAGE_HINT)
					.fuguTypeMessageText(FuguColorConfigStrings.FUGU_TYPE_MESSAGE_TEXT)
					.fuguChannelBg(FuguColorConfigStrings.FUGU_CHANNEL_BG)
					.fuguChannelItemBg(FuguColorConfigStrings.FUGU_CHANNEL_BG)
					.build();

			FuguConfig.getInstance().setColorConfig(fuguColorConfig);
		}
	}

	private static ArrayList<SupportOption> supportOptions, creditOptions;

	public static String getCurrencyNullSafety(String currencyUnit){
		return currencyUnit != null && !currencyUnit.isEmpty() ? currencyUnit
				: userData != null && userData.getCurrency() != null && !userData.getCurrency().isEmpty()
				? userData.getCurrency()
				: null;
	}

}

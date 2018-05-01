package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
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
import product.clicklabs.jugnoo.driver.utils.AuthKeySaver;
import product.clicklabs.jugnoo.driver.utils.DeviceUniqueID;
import product.clicklabs.jugnoo.driver.utils.Log;
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
	
	//TODO
	public static final String DEV_SERVER_URL = "https://test.jugnoo.in:8012";
	public static final String LIVE_SERVER_URL = "https://prod-autos-api.jugnoo.in";
	public static final String TRIAL_SERVER_URL = "https://test.jugnoo.in:8200";

    public static final String DEV_1_SERVER_URL = "https://test.jugnoo.in:8013";
    public static final String DEV_2_SERVER_URL = "https://test.jugnoo.in:8014";
    public static final String DEV_3_SERVER_URL = "https://test.jugnoo.in:8015";

	public static final String DEFAULT_SERVER_URL = LIVE_SERVER_URL;




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
	public static int appVersion;

	public static Activity context = null;
	
	public static ArrayList<CancelOption> cancelOptionsList;
	public static ArrayList<DeliveryReturnOption> deliveryReturnOptionList;

	public static String dEngagementId = "";

	private static ArrayList<CustomerInfo> assignedCustomerInfos = new ArrayList<>();
	


	
	public static LatLng nextPickupLatLng;
	public static String nextCustomerName;


	
	public static FareStructure fareStructure;

    public static ArrayList<PreviousAccountInfo> previousAccountInfoList = new ArrayList<>();

	public static JSONArray blockAppPackageNameList;

	public static boolean appMinimized = false;
	
	
	
	public static void clearDataOnLogout(Context context){
		try{
			userData = null;
			deviceToken = ""; country = ""; deviceName = ""; appVersion = 0; osVersion = "";

			AuthKeySaver.writeAuthToFile("");
			SharedPreferences pref = context.getSharedPreferences(Data.SHARED_PREF_NAME, Context.MODE_PRIVATE);
			Editor editor = pref.edit();
			editor.clear();
			editor.apply();
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
	
	
	
	private static Typeface latoRegular, museoSlab, latoLight, latoHeavy, digitalRegular, latoSemiBold;																// fonts declaration
	

	public static Typeface latoRegular(Context appContext) {											// accessing fonts functions
		if (latoRegular == null) {
			latoRegular = Typeface.createFromAsset(appContext.getAssets(), "fonts/lato_regular.ttf");
		}
		return latoRegular;
	}
	
	
	public static Typeface museoSlab(Context appContext) {
		if (museoSlab == null) {
			museoSlab = Typeface.createFromAsset(appContext.getAssets(), "fonts/museo_slab.otf");
		}
		return museoSlab;
	}

    public static Typeface latoLight(Context appContext) {											// accessing fonts functions
        if (latoLight == null) {
            latoLight = Typeface.createFromAsset(appContext.getAssets(), "fonts/lato_light.ttf");
        }
        return latoLight;
    }

	public static Typeface latoHeavy(Context appContext) {											// accessing fonts functions
		if (latoHeavy == null) {
			latoHeavy = Typeface.createFromAsset(appContext.getAssets(), "fonts/lato-heavy.ttf");
		}
		return latoHeavy;
	}

	public static Typeface latoSemiBold(Context appContext) {											// accessing fonts functions
		if (latoSemiBold == null) {
			latoSemiBold = Typeface.createFromAsset(appContext.getAssets(), "fonts/lato_semibold.ttf");
		}
		return latoSemiBold;
	}

	public static Typeface digitalRegular(Context appContext) {											// accessing fonts functions
		if (digitalRegular == null) {
			digitalRegular = Typeface.createFromAsset(appContext.getAssets(), "fonts/digital_regular.ttf");
		}
		return digitalRegular;
	}

	public static void filldetails(Context context){
		try {																						// to get AppVersion, OS version, country code and device name
			Data.appVersion = Utils.getAppVersion(context);
			Log.i("appVersion", Data.appVersion + "..");
			Data.osVersion = android.os.Build.VERSION.RELEASE;
			Log.i("osVersion", Data.osVersion + "..");
			Data.country = context.getResources().getConfiguration().locale.getDisplayCountry(Locale.getDefault());
			Log.i("countryCode", Data.country + "..");
			Data.deviceName = Utils.getDeviceName();
			Log.i("deviceName", Data.deviceName + "..");

			Data.uniqueDeviceId = DeviceUniqueID.getUniqueId(context);
			Log.i("uniqueDeviceId", Data.uniqueDeviceId);
		} catch (Exception e) {
			Log.e("error in fetching appversion and gcm key", ".." + e.toString());
		}
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
		if(assignedCustomerInfos != null) {
			for(int i=0; i<assignedCustomerInfos.size(); i++){
				if(assignedCustomerInfos.get(i).getStatus() == status){
					MyApplication.getInstance().getEngagementSP().removeCustomer(assignedCustomerInfos.get(i).getEngagementId());
					assignedCustomerInfos.remove(i);
					i--;
				}
			}
			if(HomeActivity.appInterruptHandler != null){
				HomeActivity.appInterruptHandler.updateCustomers();
			}
		}
	}

	public static void clearAssignedCustomerInfosListForStatusWithDelivery(int status, int isDelivery){
		if(assignedCustomerInfos != null) {
			for(int i=0; i<assignedCustomerInfos.size(); i++){
				if(assignedCustomerInfos.get(i).getStatus() == status
						&& assignedCustomerInfos.get(i).getIsDelivery() == isDelivery){
					MyApplication.getInstance().getEngagementSP().removeCustomer(assignedCustomerInfos.get(i).getEngagementId());
					assignedCustomerInfos.remove(i);
					i--;
				}
			}
			if(HomeActivity.appInterruptHandler != null){
				HomeActivity.appInterruptHandler.updateCustomers();
			}
		}
	}

	public static CustomerInfo getCustomerInfo(String engagementId){
		try {
			int index = assignedCustomerInfos.indexOf(new CustomerInfo(Integer.parseInt(engagementId)));
			if(index > -1){
				return assignedCustomerInfos.get(index);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void addCustomerInfo(CustomerInfo customerInfo){
		if(assignedCustomerInfos != null) {
			if (assignedCustomerInfos.contains(customerInfo)) {
				int index = assignedCustomerInfos.indexOf(customerInfo);
				assignedCustomerInfos.remove(index);
				assignedCustomerInfos.add(index, customerInfo);
			} else {
				assignedCustomerInfos.add(customerInfo);
			}
			if(HomeActivity.appInterruptHandler != null){
				HomeActivity.appInterruptHandler.updateCustomers();
			}
			MyApplication.getInstance().getEngagementSP().addCustomer(customerInfo);
		}
	}

	public static boolean removeCustomerInfo(int engagementId, int status){
		if(assignedCustomerInfos != null) {
			int index = assignedCustomerInfos.indexOf(new CustomerInfo(engagementId));
			if (index > -1) {
				if(assignedCustomerInfos.get(index).getStatus() == status) {
					assignedCustomerInfos.remove(index);
					if (HomeActivity.appInterruptHandler != null) {
						HomeActivity.appInterruptHandler.updateCustomers();
					}
					MyApplication.getInstance().getEngagementSP().removeCustomer(engagementId);
					return true;
				}
			}
		}
		return false;
	}

	public static void setCustomerState(String engagementId, DriverScreenMode driverScreenMode){
		CustomerInfo customerInfo = getCustomerInfo(engagementId);
		if(customerInfo != null){
			customerInfo.setStatus(getEngagementStatusFromDriverScreenMode(driverScreenMode).getOrdinal());
			MyApplication.getInstance().getEngagementSP().addCustomer(customerInfo);
		}
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

	public static interface TxnType {
		public static final int CREDITED = 1;
		public static final int DEBITED = 2;
	}

	public static boolean isCaptive(){
		return Data.userData.isCaptiveDriver!=null && Data.userData.isCaptiveDriver==1;
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

	public static  void initFugu(Activity activity,CaptureUserData captureUserData) {
		if (Data.userData!= null && captureUserData != null) {
			FuguConfig.init(1, activity.getString(R.string.fugu_app_key), activity, "test", captureUserData, BuildConfig.APPLICATION_ID + ".provider");

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


}

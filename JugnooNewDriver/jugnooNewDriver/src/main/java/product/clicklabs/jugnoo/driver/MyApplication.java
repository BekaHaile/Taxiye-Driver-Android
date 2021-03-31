package product.clicklabs.jugnoo.driver;

/**
 * Created by aneeshbansal on 17/03/16.
 */

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

import androidx.multidex.MultiDexApplication;
import io.paperdb.Paper;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.room.database.CommonRoomDatabase;
import product.clicklabs.jugnoo.driver.sticky.GeanieView;
import product.clicklabs.jugnoo.driver.utils.AnalyticsTrackers;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.MapLatLngBoundsCreator;
import product.clicklabs.jugnoo.driver.utils.Prefs;


public class MyApplication extends MultiDexApplication implements Application.ActivityLifecycleCallbacks {
    public static final String TAG = MyApplication.class
            .getSimpleName();

    private static MyApplication mInstance;
    static Context mContext;

    private MapLatLngBoundsCreator mapLatLngBoundsCreator;

    private FirebaseAnalytics mFirebaseAnalytics;
    public Activity mActivity;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        FirebaseApp.initializeApp(this);
        Paper.init(this);
        registerActivityLifecycleCallbacks(this);

        AnalyticsTrackers.initialize(this);
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
        initializeServerURLAndRestClient(this);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Initialize Places.
        Places.initialize(this, getResources().getString(R.string.google_maps_api_key));
    }

    public FirebaseAnalytics getmFirebaseAnalytics() {
        if (mFirebaseAnalytics == null) {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        }
        return mFirebaseAnalytics;
    }

    public Activity getmActivity() {
        return mActivity;
    }

    public void setmActivity(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void logEvent(String eventText, Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        if (eventText.length() > 31) {
            eventText.substring(0, 31);
        }
        getmFirebaseAnalytics().logEvent(eventText, bundle);
    }

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    public synchronized Tracker getGoogleAnalyticsTracker() {
        AnalyticsTrackers analyticsTrackers = AnalyticsTrackers.getInstance();
        return analyticsTrackers.get(AnalyticsTrackers.Target.APP);
    }

    /***
     * Tracking screen view
     *
     * @param screenName screen name to be displayed on GA dashboard
     */
    public void trackScreenView(String screenName) {
        Tracker t = getGoogleAnalyticsTracker();

        // Set screen name.
        t.setScreenName(screenName);

        // Send a screen view.
        t.send(new HitBuilders.ScreenViewBuilder().build());

        GoogleAnalytics.getInstance(this).dispatchLocalHits();
    }

    /***
     * Tracking exception
     *
     * @param e exception to be tracked
     */
    public void trackException(Exception e) {
        if (e != null) {
            Tracker t = getGoogleAnalyticsTracker();

            t.send(new HitBuilders.ExceptionBuilder()
                    .setDescription(
                            new StandardExceptionParser(this, null)
                                    .getDescription(Thread.currentThread().getName(), e))
                    .setFatal(false)
                    .build()
            );
        }
    }

    /***
     * Tracking event
     *
     * @param category event category
     * @param action   action of the event
     * @param label    label
     */
    public void trackEvent(String category, String action, String label) {
        Tracker t = getGoogleAnalyticsTracker();

        // Build and send an Event.
        t.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
    }


    /***
     * Tracking event
     *
     * @param category event category
     * @param action   action of the event
     * @param label    label
     * @param map      hash map for key value pairs
     */
    public void trackEvent(String category, String action, String label, Map<String, String> map) {
        Tracker t = getGoogleAnalyticsTracker();
        HitBuilders.EventBuilder eventBuilder = new HitBuilders.EventBuilder();
        eventBuilder.setCategory(category).setAction(action).setLabel(label);
        for (String key : map.keySet()) {
            eventBuilder.set(key, map.get(key));
        }
        // Build and send an Event.
        t.send(eventBuilder.build());
    }


    public MapLatLngBoundsCreator getMapLatLngBoundsCreator() {
        if (mapLatLngBoundsCreator == null) {
            mapLatLngBoundsCreator = new MapLatLngBoundsCreator();
        }
        return mapLatLngBoundsCreator;
    }


    public synchronized void writePathLogToFile(final String suffix, final String text) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
					ArrayList<CustomerInfo> list = Data.getAssignedCustomerInfosListForEngagedStatus();
                    for (CustomerInfo customerInfo : list) {
                        Log.writePathLogToFile(MyApplication.this, customerInfo.getEngagementId() + suffix, text);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
	public void insertRideLogToEngagements(String log) {
		try {
			ArrayList<CustomerInfo> list = Data.getAssignedCustomerInfosListForEngagedStatus();
			for (CustomerInfo customerInfo : list) {
				Database2.getInstance(this).insertRideLog(DateOperations.getMillisInHHMMSS(System.currentTimeMillis())+": "+log, customerInfo.getEngagementId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    public void insertRideDataToEngagements(String lat, String lng, String t) {
        try {
			ArrayList<CustomerInfo> list = Data.getAssignedCustomerInfosListForEngagedStatus();
            for (CustomerInfo customerInfo : list) {
                Database2.getInstance(this).insertRideData(this, lat, lng, t, customerInfo.getEngagementId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void initializeServerURLAndRestClient(Context context) {
        String link = Prefs.with(context).getString(Data.SP_SERVER_LINK, Data.DEFAULT_SERVER_URL);

        String CUSTOM_URL = Prefs.with(context).getString(SPLabels.CUSTOM_SERVER_URL, Data.DEFAULT_SERVER_URL);

        Data.SERVER_URL = Data.DEFAULT_SERVER_URL;

        if (link.equalsIgnoreCase(Data.TRIAL_SERVER_URL)) {
            Data.SERVER_URL = Data.TRIAL_SERVER_URL;
            Data.FLURRY_KEY = "STATIC_FLURRY_KEY";
        } else if (link.equalsIgnoreCase(Data.DEV_SERVER_URL)) {
            Data.SERVER_URL = Data.DEV_SERVER_URL;
            Data.FLURRY_KEY = "STATIC_FLURRY_KEY";
        } else if (link.equalsIgnoreCase(Data.LIVE_SERVER_URL)) {
            Data.SERVER_URL = Data.LIVE_SERVER_URL;
            Data.FLURRY_KEY = Data.STATIC_FLURRY_KEY;
        } else if (link.equalsIgnoreCase(Data.DEV_1_SERVER_URL)) {
            Data.SERVER_URL = Data.DEV_1_SERVER_URL;
            Data.FLURRY_KEY = "STATIC_FLURRY_KEY";
        } else if (link.equalsIgnoreCase(Data.DEV_2_SERVER_URL)) {
            Data.SERVER_URL = Data.DEV_2_SERVER_URL;
            Data.FLURRY_KEY = "STATIC_FLURRY_KEY";
        } else if (link.equalsIgnoreCase(Data.DEV_3_SERVER_URL)) {
            Data.SERVER_URL = Data.DEV_3_SERVER_URL;
            Data.FLURRY_KEY = "STATIC_FLURRY_KEY";
        } else {
            Data.SERVER_URL = CUSTOM_URL;
            Data.FLURRY_KEY = "STATIC_FLURRY_KEY";
        }
        Log.e("Data.SERVER_URL", "=" + Data.SERVER_URL);
        RestClient.setupRestClient(Data.SERVER_URL);
    }

    public Locale getCurrentLocale(){
        return getResources().getConfiguration().locale;
    }

    private Toast toast;
    public Toast getToast(){
        return toast;
    }
    public void setToast(Toast toast){
        this.toast = toast;
    }



    private boolean appMinimized = false;
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {
        appMinimized= false;
        stopService(new Intent(this, GeanieView.class));

    }

    @Override
    public void onActivityPaused(Activity activity) {
        appMinimized = true;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if(appMinimized){
            startService(new Intent(this, GeanieView.class));
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    public boolean isOnline() {
        try {
            ConnectivityManager connectManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = connectManager.getActiveNetworkInfo();
            if (activeNetwork != null) { // connected to the internet
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    return true;
                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    return true;
                }
            } else {
                return false;
            }
            return false;
        } catch (Exception e) {
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
        }
        return false;
    }

    private CommonRoomDatabase commonRoomDatabase;
    public CommonRoomDatabase getCommonRoomDatabase(){
    	if(commonRoomDatabase == null){
    		commonRoomDatabase = CommonRoomDatabase.Companion.getInstance(this);
		}
    	return commonRoomDatabase;
	}

	@Override
	public void onLowMemory() {
    	Data.saveAssignedCustomers();
		super.onLowMemory();
	}

	@Override
	public void onTrimMemory(int level) {
		Data.saveAssignedCustomers();
		super.onTrimMemory(level);
	}
}

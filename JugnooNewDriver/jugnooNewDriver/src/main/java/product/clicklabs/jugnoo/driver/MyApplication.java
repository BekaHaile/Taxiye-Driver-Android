package product.clicklabs.jugnoo.driver;

/**
 * Created by aneeshbansal on 17/03/16.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Locale;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import io.paperdb.Paper;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.home.EngagementSP;
import product.clicklabs.jugnoo.driver.home.models.EngagementSPData;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.utils.AnalyticsTrackers;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.MapLatLngBoundsCreator;
import product.clicklabs.jugnoo.driver.utils.Prefs;


public class MyApplication extends MultiDexApplication {
    public static final String TAG = MyApplication.class
            .getSimpleName();

    private static MyApplication mInstance;

    private EngagementSP engagementSP;

    private MapLatLngBoundsCreator mapLatLngBoundsCreator;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        Fabric.with(this, new Crashlytics());
        FirebaseApp.initializeApp(this);
        Paper.init(this);

//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);

        AnalyticsTrackers.initialize(this);
        AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);
        initializeServerURLAndRestClient(this);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    public FirebaseAnalytics getmFirebaseAnalytics() {
        if (mFirebaseAnalytics == null) {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        }
        return mFirebaseAnalytics;
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

    public EngagementSP getEngagementSP() {
        if (engagementSP == null) {
            engagementSP = new EngagementSP(this);
        }
        return engagementSP;
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
                    for (EngagementSPData engagementSPData : getEngagementSP().getEngagementSPDatasArray()) {
                        Log.writePathLogToFile(engagementSPData.getEngagementId() + suffix, text);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void insertRideDataToEngagements(String lat, String lng, String t) {
        try {
            for (EngagementSPData engagementSPData : getEngagementSP().getEngagementSPDatasArray()) {
                Database2.getInstance(this).insertRideData(lat, lng, t, engagementSPData.getEngagementId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void initializeServerURLAndRestClient(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Data.SETTINGS_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String link = preferences.getString(Data.SP_SERVER_LINK, Data.DEFAULT_SERVER_URL);

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
        DriverLocationUpdateService.updateServerData(context);
    }

    public Locale getCurrentLocale(){
        return getResources().getConfiguration().locale;
    }
}

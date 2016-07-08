package product.clicklabs.jugnoo.driver;

/**
 * Created by aneeshbansal on 17/03/16.
 */

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.StandardExceptionParser;
import com.google.android.gms.analytics.Tracker;

import java.util.Map;

import product.clicklabs.jugnoo.driver.home.EngagementSP;
import product.clicklabs.jugnoo.driver.home.models.EngagementSPData;
import product.clicklabs.jugnoo.driver.utils.AnalyticsTrackers;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.MapLatLngBoundsCreator;


public class MyApplication extends Application {
	public static final String TAG = MyApplication.class
			.getSimpleName();

	private static MyApplication mInstance;

	private EngagementSP engagementSP;

	private MapLatLngBoundsCreator mapLatLngBoundsCreator;


	@Override
	public void onCreate() {
		super.onCreate();
		mInstance = this;

		AnalyticsTrackers.initialize(this);
		AnalyticsTrackers.getInstance().get(AnalyticsTrackers.Target.APP);

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
	 * @param map hash map for key value pairs
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
		if(engagementSP == null){
			engagementSP = new EngagementSP(this);
		}
		return engagementSP;
	}


	public MapLatLngBoundsCreator getMapLatLngBoundsCreator() {
		if(mapLatLngBoundsCreator == null){
			mapLatLngBoundsCreator = new MapLatLngBoundsCreator();
		}
		return mapLatLngBoundsCreator;
	}


	public void writePathLogToFile(final String suffix, final String text){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					for(EngagementSPData engagementSPData : getEngagementSP().getEngagementSPDatasArray()){
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

}

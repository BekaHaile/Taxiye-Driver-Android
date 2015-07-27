package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.driver.datastructure.LatLngPair;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.HttpRequester;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.MapUtils;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;

public class GpsDistanceCalculator {
	
	private static GpsDistanceCalculator instance;
	
	private static long LOCATION_UPDATE_INTERVAL = 2000; // in milliseconds
	private static final double MAX_DISPLACEMENT_THRESHOLD = 200; //in meters
	public static final double MAX_SPEED_THRESHOLD = 19; //in meters per second
	public static final double MAX_ACCURACY = 500;

	public double totalDistance;
	public Location lastGPSLocation, lastFusedLocation;
	public static long lastLocationTime;
	
	private FusedLocationFetcherBackgroundHigh gpsForegroundLocationFetcher;
	private FusedLocationFetcherBackgroundBalanced fusedLocationFetcherBackgroundBalanced;
	public static GPSLocationUpdate gpsLocationUpdate;
	public static FusedLocationUpdate fusedLocationUpdate;
	private GpsDistanceTimeUpdater gpsDistanceUpdater;
	private Context context;
	
	private ArrayList<LatLngPair> deltaLatLngPairs = new ArrayList<LatLngPair>();
	private ArrayList<DirectionsAsyncTask> directionsAsyncTasks = new ArrayList<DirectionsAsyncTask>();

	
//	private static final String LOCATION_SP = "metering_sp",
//			LOCATION_LAT = "location_lat",
//			LOCATION_LNG = "location_lng",
//			TOTAL_DISTANCE = "total_distance",
//			LOCATION_TIME = "location_time",
//			START_TIME = "start_time",
//			TRACKING = "tracking",
//			ENGAGEMENT_ID = "engagement_id";
	
	
	private GpsDistanceCalculator(Context context, GpsDistanceTimeUpdater gpsDistanceUpdater,
			double totalDistance, long lastLocationTime){
		this.context = context;
		this.totalDistance = totalDistance;
		this.lastGPSLocation = null;
		this.lastFusedLocation = null;
		this.lastLocationTime = lastLocationTime;
		this.gpsDistanceUpdater = gpsDistanceUpdater;
		this.deltaLatLngPairs = new ArrayList<LatLngPair>();
		this.directionsAsyncTasks = new ArrayList<DirectionsAsyncTask>();
		
		disconnectGPSListener();
		this.gpsForegroundLocationFetcher = null;
		this.fusedLocationFetcherBackgroundBalanced = null;
		initializeGPSForegroundLocationFetcher(context);

		Log.e("GpsDistanceCalculator constructor", "=totalDistance="+totalDistance);
	}
	
	public static GpsDistanceCalculator getInstance(Context context, GpsDistanceTimeUpdater gpsDistanceUpdater,
			double totalDistance, long lastLocationTime) {
		if (instance == null) {
			instance = new GpsDistanceCalculator(context, gpsDistanceUpdater, totalDistance, lastLocationTime);
		}

		instance.context = context;
		instance.gpsDistanceUpdater = gpsDistanceUpdater;
		instance.totalDistance = totalDistance;
		instance.lastLocationTime = lastLocationTime;

		return instance;
	}
	
	
	public void start(){
		if(1 != getTrackingFromSP(context)){
			saveStartTimeToSP(context, System.currentTimeMillis());
			saveTotalDistanceToSP(context, -1);
			saveLastLocationTimeToSP(context, System.currentTimeMillis());
			saveTrackingToSP(context, 1);
		}
		connectGPSListener(context);
		setupMeteringAlarm(context);

		GpsDistanceCalculator.this.gpsDistanceUpdater.updateDistanceTime(totalDistance, getElapsedMillis(), lastGPSLocation, lastFusedLocation, true);
		Log.e("GpsDistanceCalculator start", "=totalDistance="+totalDistance);
		Log.writePathLogToFile(getEngagementIdFromSP(context)+"m", "totalDistance at start ="+totalDistance);
	}
	
	public void resume(){
		if(1 == getTrackingFromSP(context)){
			connectGPSListener(context);
		}
	}
	
	public void pause(){
		if(1 == getTrackingFromSP(context)){
			saveData(context, lastGPSLocation, totalDistance, lastLocationTime);
			disconnectGPSListener();
		}
	}
	
	
	
	public void saveState(){
		saveData(context, lastGPSLocation, totalDistance, lastLocationTime);
	}
	
	public void stop(){
		disconnectGPSListener();
		cancelMeteringAlarm(context);

		saveStartTimeToSP(context, System.currentTimeMillis());
		saveLatLngToSP(context, 0, 0);
		saveTotalDistanceToSP(context, -1);
		saveLastLocationTimeToSP(context, System.currentTimeMillis());
		saveTrackingToSP(context, 0);
		instance.totalDistance = -1;
		instance.lastLocationTime = System.currentTimeMillis();
		instance.lastGPSLocation = null;
		instance.lastFusedLocation = null;


		Log.writePathLogToFile(getEngagementIdFromSP(context) + "m", "totalDistance at stop =" + totalDistance);
		Log.e("stop instance=", "=" + instance);
	}



	private static int METERING_PI_REQUEST_CODE = 112;
	private static final String CHECK_LOCATION = "product.clicklabs.jugnoo.driver.CHECK_LOCATION";
	private static final long ALARM_REPEAT_INTERVAL = 30000;


	public void setupMeteringAlarm(Context context) {
		// check task is scheduled or not
		boolean alarmUp = (PendingIntent.getBroadcast(context, METERING_PI_REQUEST_CODE,
				new Intent(context, DriverLocationUpdateAlarmReceiver.class).setAction(CHECK_LOCATION),
				PendingIntent.FLAG_NO_CREATE) != null);
		if (alarmUp) {
			cancelMeteringAlarm(context);
		}

		Intent intent = new Intent(context, MeteringAlarmReceiver.class);
		intent.setAction(CHECK_LOCATION);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, METERING_PI_REQUEST_CODE,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 20000,
				ALARM_REPEAT_INTERVAL, pendingIntent);
	}

	public void cancelMeteringAlarm(Context context) {
		Intent intent = new Intent(context, MeteringAlarmReceiver.class);
		intent.setAction(CHECK_LOCATION);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, METERING_PI_REQUEST_CODE,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
		pendingIntent.cancel();
	}


	
	private void initializeGPSForegroundLocationFetcher(Context context){
		if(gpsForegroundLocationFetcher == null){
			gpsForegroundLocationFetcher = new FusedLocationFetcherBackgroundHigh(context, LOCATION_UPDATE_INTERVAL);
		}
		initializeFusedLocationFetcherBackgroundBalanced(context);
		initializeGpsLocationUpdate();
	}
	
	private void initializeFusedLocationFetcherBackgroundBalanced(Context context){
		if(fusedLocationFetcherBackgroundBalanced == null){
			fusedLocationFetcherBackgroundBalanced = new FusedLocationFetcherBackgroundBalanced(context, LOCATION_UPDATE_INTERVAL);
		}
	}
	
	private void initializeGpsLocationUpdate(){
		if(gpsLocationUpdate == null){
			gpsLocationUpdate = new GPSLocationUpdate() {
				
				@Override
				public void onGPSLocationChanged(Location location) {
					long lastUpdateTime = System.currentTimeMillis();
					try {
						if(location.getAccuracy() < MAX_ACCURACY){
//							if(location.getAccuracy() <= BEST_ACCURACY && LOCATION_UPDATE_INTERVAL == 1000){
//								LOCATION_UPDATE_INTERVAL = 5000;
//								connectGPSListener(context);
//							}
//							else if(location.getAccuracy() > BEST_ACCURACY && LOCATION_UPDATE_INTERVAL == 5000){
//								LOCATION_UPDATE_INTERVAL = 1000;
//								connectGPSListener(context);
//							}

							if((Utils.compareDouble(location.getLatitude(), 0.0) != 0) && (Utils.compareDouble(location.getLongitude(), 0.0) != 0)){
								drawLocationChanged(location);
							}
						}
						GpsDistanceCalculator.this.gpsDistanceUpdater.updateDistanceTime(totalDistance, getElapsedMillis(), lastGPSLocation, lastFusedLocation, true);

						Log.v("diff", String.valueOf(System.currentTimeMillis() - lastUpdateTime));

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void refreshLocationFetchers(final Context context) {
					reconnectGPSHandler();

//					Handler mainHandler = new Handler(context.getMainLooper());
//					mainHandler.post(new Runnable() {
//						@Override
//						public void run() {
//							//try{Toast.makeText(context, "Old location detected", Toast.LENGTH_LONG).show();}catch(Exception e){}
//						}
//					});
				}
			};
		}
		initializeFusedLocationUpdate();
	}
	
	private void initializeFusedLocationUpdate(){
		if(fusedLocationUpdate == null){
			fusedLocationUpdate = new FusedLocationUpdate() {
				
				@Override
				public void onFusedLocationChanged(Location location) {
					lastFusedLocation = location;
					GpsDistanceCalculator.this.gpsDistanceUpdater.updateDistanceTime(totalDistance, getElapsedMillis(), lastGPSLocation, lastFusedLocation, false);
				}
			};
		}
	}
	
	
	private void connectGPSListener(Context context){
		disconnectGPSListener();
		try {
			initializeGPSForegroundLocationFetcher(context);
			gpsForegroundLocationFetcher.connect();
			fusedLocationFetcherBackgroundBalanced.connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void disconnectGPSListener(){
		try {
			if(gpsForegroundLocationFetcher != null){
				gpsForegroundLocationFetcher.destroy();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try{
			if(fusedLocationFetcherBackgroundBalanced != null){
				fusedLocationFetcherBackgroundBalanced.destroy();
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	private synchronized void drawLocationChanged(Location location){
		try {
				final LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
				long newLocationTime = System.currentTimeMillis();
	
				if (Utils.compareDouble(totalDistance, -1.0) == 0) {
					totalDistance = 0;
					lastGPSLocation = null;
					lastLocationTime = System.currentTimeMillis();
				}
	

				LatLng lastLatLng = null;
				
				if (lastGPSLocation != null) {
					lastLatLng = new LatLng(lastGPSLocation.getLatitude(), lastGPSLocation.getLongitude());
				} else {
					GpsDistanceCalculator.this.gpsDistanceUpdater.drawOldPath();
					lastLatLng = getSavedLatLngFromSP(context);
                    Database2.getInstance(context).insertRideData("" + lastLatLng.latitude, "" + lastLatLng.longitude, "" + System.currentTimeMillis());
					Log.writePathLogToFile(getEngagementIdFromSP(context) + "m", "first time lastLatLng =" + lastLatLng);
				}
				
				long millisDiff = newLocationTime - lastLocationTime;
				long secondsDiff = millisDiff / 1000;

				double displacement = MapUtils.distance(lastLatLng, currentLatLng);
				double speedMPS = 0;
				if(secondsDiff > 0){
					speedMPS = displacement / secondsDiff;
				}

				if(speedMPS < MAX_SPEED_THRESHOLD){
//					if(totalDistance < 1000) {
						if((Utils.compareDouble(lastLatLng.latitude, 0.0) != 0) && (Utils.compareDouble(lastLatLng.longitude, 0.0) != 0)){
							addLatLngPathToDistance(lastLatLng, currentLatLng, location);
						}
						else{
							lastLocationTime = System.currentTimeMillis();
							saveData(context, lastGPSLocation, totalDistance, lastLocationTime);
						}
						lastGPSLocation = location;
//					}
				}
				else{
					reconnectGPSHandler();
				}
				Log.writePathLogToFile(getEngagementIdFromSP(context)+"m", "speedMPS="+speedMPS+" currentLatLng ="+currentLatLng);
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
	public void reconnectGPSHandler(){
		disconnectGPSListener();
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				connectGPSListener(context);
			}
		}, 5000);
	}
	
	private synchronized void addLatLngPathToDistance(final LatLng lastLatLng, final LatLng currentLatLng, final Location currentLocation){
		try {
			final double displacement = MapUtils.distance(lastLatLng, currentLatLng);
			if(Utils.compareDouble(displacement, MAX_DISPLACEMENT_THRESHOLD) == -1){
				boolean validDistance = updateTotalDistance(lastLatLng, currentLatLng, displacement, currentLocation);
				if(validDistance){
                    Database2.getInstance(context).insertCurrentPathItem(-1, lastLatLng.latitude, lastLatLng.longitude,
                        currentLatLng.latitude, currentLatLng.longitude, 0, 0);
					GpsDistanceCalculator.this.gpsDistanceUpdater.updateDistanceTime(totalDistance, getElapsedMillis(), lastGPSLocation, lastFusedLocation, true);
					GpsDistanceCalculator.this.gpsDistanceUpdater.addPathToMap(new PolylineOptions().add(lastLatLng, currentLatLng));
				}
			}
			else{
                final long rowId = Database2.getInstance(context).insertCurrentPathItem(-1, lastLatLng.latitude, lastLatLng.longitude,
                    currentLatLng.latitude, currentLatLng.longitude, 1, 1);
                callGoogleDirectionsAPI(lastLatLng, currentLatLng, displacement, currentLocation, rowId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private synchronized boolean updateTotalDistance(LatLng lastLatLng, LatLng currentLatLng, double deltaDistance, Location currentLocation){
		boolean validDistance = false;
		if(deltaDistance > 0.0){
			LatLngPair latLngPair = new LatLngPair(lastLatLng, currentLatLng, deltaDistance);
			if(deltaLatLngPairs == null){
				deltaLatLngPairs = new ArrayList<LatLngPair>();
			}
			
			if(!deltaLatLngPairs.contains(latLngPair)){
				totalDistance = totalDistance + deltaDistance;
				deltaLatLngPairs.add(latLngPair);
				validDistance = true;

				lastLocationTime = System.currentTimeMillis();

                Database2.getInstance(context).insertRideData("" + currentLatLng.latitude, "" + currentLatLng.longitude, "" + System.currentTimeMillis());

				Log.writePathLogToFile(getEngagementIdFromSP(context)+"m", 
						DateOperations.getTimeStampFromMillis(currentLocation.getTime())+","
						+currentLatLng.latitude+","
						+currentLatLng.longitude+","
						+currentLocation.getAccuracy()+","
						+GpsDistanceCalculator.this.totalDistance+","
						+deltaDistance+","
						+lastLatLng.latitude+","
						+lastLatLng.longitude+","
						+DateOperations.getTimeStampFromMillis(GpsDistanceCalculator.this.lastLocationTime));
			}
			saveData(context, lastGPSLocation, totalDistance, lastLocationTime);
		}
		return validDistance;
	}
	
	
	
	private synchronized void callGoogleDirectionsAPI(LatLng lastLatLng, LatLng currentLatLng, double displacement, Location currentLocation, long rowId){
		if(directionsAsyncTasks == null){
			directionsAsyncTasks = new ArrayList<DirectionsAsyncTask>();
		}
		DirectionsAsyncTask directionsAsyncTask = new DirectionsAsyncTask(lastLatLng, currentLatLng, displacement, currentLocation, rowId);
		if(!directionsAsyncTasks.contains(directionsAsyncTask)){
			directionsAsyncTasks.add(directionsAsyncTask);
			directionsAsyncTask.execute();
		}
	}
	
	private class DirectionsAsyncTask extends AsyncTask<Void, Void, String>{
	    String url;
	    double displacementToCompare;
	    LatLng source, destination;
	    Location currentLocation;
        long rowId;
	    public DirectionsAsyncTask(LatLng source, LatLng destination, double displacementToCompare, Location currentLocation, long rowId){
	    	this.source = source;
	    	this.destination = destination;
	        this.url = MapUtils.makeDirectionsURL(source, destination);
	        this.displacementToCompare = displacementToCompare;
	        this.currentLocation = currentLocation;
            this.rowId = rowId;
	    }
	    
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	    }
	    @Override
	    protected String doInBackground(Void... params) {
	    	return new HttpRequester().getJSONFromUrl(url);
	    }
	    @Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        if(result!=null){
	            updateGAPIDistance(result, displacementToCompare, source, destination, currentLocation, rowId);
	        }
	        directionsAsyncTasks.remove(this);
	    }
	    
	    
	    @Override
	    public boolean equals(Object o) {
	    	try{
				DirectionsAsyncTask matchO = (DirectionsAsyncTask) o;
				if (((Utils.compareDouble(matchO.source.latitude, this.source.latitude) == 0)
						&& (Utils.compareDouble(matchO.source.longitude, this.source.longitude) == 0)) ||
						((Utils.compareDouble(matchO.destination.latitude, this.destination.latitude) == 0)
								&& (Utils.compareDouble(matchO.destination.longitude, this.destination.longitude) == 0))) {
					return true;
				} else {
					return false;
				}
	    	} catch(Exception e){
	    		e.printStackTrace();
				return false;
	    	}
	    }
	}
	
	
	private synchronized void updateGAPIDistance(String result, double displacementToCompare, LatLng source, LatLng destination, Location currentLocation, long rowId) {
	    try {
	    	double distanceOfPath = Double.MAX_VALUE;
	    	JSONObject jsonObject = new JSONObject(result);
	    	String status = jsonObject.getString("status");
	    	if("OK".equalsIgnoreCase(status)){
	    		JSONObject leg0 = jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0);
		    	distanceOfPath = leg0.getJSONObject("distance").getDouble("value");
	    	}
	    	if(Utils.compareDouble(distanceOfPath, (displacementToCompare*1.5)) <= 0){														// distance would be approximately correct
		        boolean validDistance = updateTotalDistance(source, destination, distanceOfPath, currentLocation);
		        if(validDistance){
		        	GpsDistanceCalculator.this.gpsDistanceUpdater.updateDistanceTime(totalDistance, getElapsedMillis(), lastGPSLocation, lastFusedLocation, true);
		        	
					JSONArray routeArray = jsonObject.getJSONArray("routes");
					JSONObject routes = routeArray.getJSONObject(0);
					JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
					String encodedString = overviewPolylines.getString("points");
					List<LatLng> list = MapUtils.decodeDirectionsPolyline(encodedString);
					
					PolylineOptions polylineOptions = new PolylineOptions();
					
					for(int z = 0; z < list.size()-1; z++){
		                LatLng src= list.get(z);
		                LatLng dest= list.get(z+1);
		                polylineOptions.add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude, dest.longitude));

                        Database2.getInstance(context).insertCurrentPathItem(rowId, src.latitude, src.longitude,
                            dest.latitude, dest.longitude, 0, 0);
		            }

                    Database2.getInstance(context).updateCurrentPathItemSectionIncomplete(rowId, 0);

					GpsDistanceCalculator.this.gpsDistanceUpdater.addPathToMap(polylineOptions);
		        }
		        Log.writePathLogToFile(getEngagementIdFromSP(context)+"m", "gapi case successful");
	    	}
	    	else{
	    		throw new Exception();
	    	}
	    } 
	    catch (Exception e) {
	    	e.printStackTrace(); 																		// displacement would be correct
	    	boolean validDistance = updateTotalDistance(source, destination, displacementToCompare, currentLocation);
	    	if(validDistance){
	    		GpsDistanceCalculator.this.gpsDistanceUpdater.updateDistanceTime(totalDistance, getElapsedMillis(), lastGPSLocation, lastFusedLocation, true);
	    		GpsDistanceCalculator.this.gpsDistanceUpdater.addPathToMap(new PolylineOptions().add(source, destination));

                Database2.getInstance(context).updateCurrentPathItemSectionIncompleteAndGooglePath(rowId, 0, 0);

	    	}
	    	Log.writePathLogToFile(getEngagementIdFromSP(context)+"m", "gapi case unsuccessful");
	    }
	} 
	
	
	
	
	
	
	
	
	
	public long getElapsedMillis(){
		long rideStartTime = getStartTimeFromSP(context);
		
		long timeDiff = System.currentTimeMillis() - rideStartTime;
		long elapsedTime = 0;
		if(timeDiff > 0){
			elapsedTime = timeDiff;
		}
		
		return elapsedTime;
	}
	
	

	
	
	
	
	
	
	public static void saveData(Context context, Location location, double totalDistance, long lastLocationTime){
		if(location != null){
			saveLatLngToSP(context, location.getLatitude(), location.getLongitude());
			saveTotalDistanceToSP(context, totalDistance);
			saveLastLocationTimeToSP(context, lastLocationTime);
		}
	}
	

	public static synchronized void saveLatLngToSP(Context context, double latitude, double longitude){
		Prefs.with(context).save(SPLabels.LOCATION_LAT, "" + latitude);
		Prefs.with(context).save(SPLabels.LOCATION_LNG, "" + longitude);
	}
	
	public static synchronized LatLng getSavedLatLngFromSP(Context context){
		return new LatLng(Double.parseDouble(Prefs.with(context).getString(SPLabels.LOCATION_LAT, "" + 0)),
				Double.parseDouble(Prefs.with(context).getString(SPLabels.LOCATION_LNG, ""+ 0)));
	}
	
	
	public static synchronized void saveTotalDistanceToSP(Context context, double totalDistance){
		Prefs.with(context).save(SPLabels.TOTAL_DISTANCE, ""+totalDistance);
	}

	public static synchronized double getTotalDistanceFromSP(Context context){
		return Double.parseDouble(Prefs.with(context).getString(SPLabels.TOTAL_DISTANCE, "" + -1));
	}

	public static synchronized void saveLastLocationTimeToSP(Context context, long lastLocationTime){
		Prefs.with(context).save(SPLabels.LOCATION_TIME, ""+lastLocationTime);
	}

	public static synchronized long getLastLocationTimeFromSP(Context context){
		return Long.parseLong(Prefs.with(context).getString(SPLabels.LOCATION_TIME, ""+ System.currentTimeMillis()));
	}
	
	public static synchronized void saveStartTimeToSP(Context context, long startTime){
		Prefs.with(context).save(SPLabels.START_TIME, ""+startTime);
	}

	public static synchronized long getStartTimeFromSP(Context context){
		return Long.parseLong(Prefs.with(context).getString(SPLabels.START_TIME, "0"));
	}
	
	public static synchronized void saveTrackingToSP(Context context, int tracking){
		Prefs.with(context).save(SPLabels.TRACKING, "" + tracking);
	}

	public static synchronized int getTrackingFromSP(Context context){
		return Integer.parseInt(Prefs.with(context).getString(SPLabels.TRACKING, "0"));
	}
	
	public static synchronized void saveEngagementIdToSP(Context context, String engagementId){
		Prefs.with(context).save(SPLabels.ENGAGEMENT_ID, engagementId);
	}

	public static synchronized String getEngagementIdFromSP(Context context){
		return Prefs.with(context).getString(SPLabels.ENGAGEMENT_ID, "0");
	}
	
}

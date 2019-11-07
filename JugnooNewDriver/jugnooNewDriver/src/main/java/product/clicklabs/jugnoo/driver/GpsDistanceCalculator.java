package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.driver.datastructure.DriverScreenMode;
import product.clicklabs.jugnoo.driver.datastructure.LatLngPair;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.directions.GAPIDirections;
import product.clicklabs.jugnoo.driver.directions.room.model.Path;
import product.clicklabs.jugnoo.driver.home.models.EngagementSPData;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.MapUtils;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;

public class GpsDistanceCalculator {

	private static final String TAG = GpsDistanceCalculator.class.getSimpleName();

	private static GpsDistanceCalculator instance;

	private static long LOCATION_UPDATE_INTERVAL = 2000; // in milliseconds
	private static final double MAX_DISPLACEMENT_THRESHOLD = 200; //in meters
	public static final double MAX_SPEED_THRESHOLD = 20; //in meters per second
	public static final double MAX_ACCURACY = 200;
	public static final long WAITING_WINDOW_TIME_MILLIS = 10000;
	public final double DISTANCE_RESET_TOLERANCE = 100; // in meters
	public final double WAIT_TIME_RESET_TOLERANCE = 10000; // in milliseconds
	public static final int TOTAL_DISTANCE_MAX = 200000;
	public static final int DELTA_DISTANCE_MAX = 20000;


	public double totalDistance;
	public double totalHaversineDistance;
	public Location lastGPSLocation, lastFusedLocation;
	public static long lastLocationTime;
	private double accumulativeSpeed;
	private int speedCounter;
	private long lastWaitWindowTime;



	private FusedLocationFetcherBackground gpsForegroundLocationFetcher, fusedLocationFetcherBackgroundBalanced;
	public static GPSLocationUpdate gpsLocationUpdate, fusedLocationUpdate;
	private GpsDistanceTimeUpdater gpsDistanceUpdater;
	private Context context;

	private ArrayList<LatLngPair> deltaLatLngPairs = new ArrayList<LatLngPair>();
	private ArrayList<DirectionsAsyncTask> directionsAsyncTasks = new ArrayList<DirectionsAsyncTask>();



	private GpsDistanceCalculator(Context context, GpsDistanceTimeUpdater gpsDistanceUpdater,
								  double totalDistance, long lastLocationTime, double totalHaversineDistance) {
		this.context = context;
		this.totalDistance = totalDistance;
		this.lastGPSLocation = null;
		this.totalHaversineDistance = totalHaversineDistance;
		this.lastFusedLocation = null;
		this.lastLocationTime = lastLocationTime;
		this.gpsDistanceUpdater = gpsDistanceUpdater;
		this.deltaLatLngPairs = new ArrayList<LatLngPair>();
		this.directionsAsyncTasks = new ArrayList<DirectionsAsyncTask>();

		this.accumulativeSpeed = 0;
		this.speedCounter = 0;
		this.lastWaitWindowTime = System.currentTimeMillis();

		disconnectGPSListener();
		this.gpsForegroundLocationFetcher = null;
		this.fusedLocationFetcherBackgroundBalanced = null;
		initializeGPSForegroundLocationFetcher(context);
		this.handler = new Handler();

	}

	public static GpsDistanceCalculator getInstance(Context context, GpsDistanceTimeUpdater gpsDistanceUpdater,
													double totalDistance, long lastLocationTime, double totalHaversineDistance) {
		if (instance == null) {
			instance = new GpsDistanceCalculator(context, gpsDistanceUpdater, totalDistance, lastLocationTime, totalHaversineDistance);
		}

		instance.context = context;
		instance.gpsDistanceUpdater = gpsDistanceUpdater;
		instance.totalDistance = totalDistance;
		instance.totalHaversineDistance = totalHaversineDistance;
		instance.lastLocationTime = lastLocationTime;

		return instance;
	}


	public void start() {
		if (!isMeteringStateActive(context)) {
			saveTrackingToSP(context, 1);
			saveStartTimeToSP(context, System.currentTimeMillis());
			saveWaitTimeToSP(context, 0);
			saveTotalDistanceToSP(context, -1);
			saveLastLocationTimeToSP(context, System.currentTimeMillis());
			Prefs.with(context).save(Constants.SP_RECEIVER_LAST_LOCATION_TIME, System.currentTimeMillis());
		}
		connectGPSListener(context);
		setupMeteringAlarm(context);
		startUploadRunnables();

		GpsDistanceCalculator.this.gpsDistanceUpdater.updateDistanceTime(totalDistance, getElapsedMillis(),
				getWaitTimeFromSP(context), lastGPSLocation,
				lastFusedLocation, totalHaversineDistance, true);
		MyApplication.getInstance().writePathLogToFile("m", "totalDistance at start =" + totalDistance);
		GpsDistanceCalculator.this.gpsDistanceUpdater.googleApiHitStop();
	}


	public void saveState() {
		saveData(context, lastGPSLocation, lastLocationTime);
	}

	public void stop() {
		disconnectGPSListener();
		cancelMeteringAlarm(context);
		stopUploadRunnables();

		saveStartTimeToSP(context, System.currentTimeMillis());
		saveWaitTimeToSP(context, 0);
		saveLatLngToSP(context, 0, 0);
		saveTotalDistanceToSP(context, -1);
		saveLastLocationTimeToSP(context, System.currentTimeMillis());
		saveTrackingToSP(context, 0);
		instance.totalDistance = -1;
		instance.totalHaversineDistance = -1;
		instance.lastLocationTime = System.currentTimeMillis();
		instance.lastGPSLocation = null;
		instance.lastFusedLocation = null;

		instance.accumulativeSpeed = 0;
		instance.speedCounter = 0;
		instance.lastWaitWindowTime = System.currentTimeMillis();

		MyApplication.getInstance().writePathLogToFile("m", "totalDistance at stop =" + totalDistance);
	}

//	public void distanceResetForced(){
//		saveTotalDistanceToSP(context, -1);
//		saveStartTimeToSP(context, System.currentTimeMillis());
//		saveWaitTimeToSP(context, 0);
//		instance.totalDistance = -1;
//	}


	private static int METERING_PI_REQUEST_CODE = 112;
	private static final String CHECK_LOCATION = "product.clicklabs.jugnoo.driver.CHECK_LOCATION";
	private static final long ALARM_REPEAT_INTERVAL = 60000;


	private void setupMeteringAlarm(Context context) {
		// check task is scheduled or not
		boolean alarmUp = (PendingIntent.getBroadcast(context, METERING_PI_REQUEST_CODE,
				new Intent(context, MeteringAlarmReceiver.class).setAction(CHECK_LOCATION),
				PendingIntent.FLAG_NO_CREATE) != null);
		if (alarmUp) {
			cancelMeteringAlarm(context);
		}

		Intent intent = new Intent(context, MeteringAlarmReceiver.class);
		intent.setAction(CHECK_LOCATION);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, METERING_PI_REQUEST_CODE,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		if (alarmManager != null) {
			alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 20000,
					ALARM_REPEAT_INTERVAL, pendingIntent);
		}
	}

	private void cancelMeteringAlarm(Context context) {
		Intent intent = new Intent(context, MeteringAlarmReceiver.class);
		intent.setAction(CHECK_LOCATION);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, METERING_PI_REQUEST_CODE,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
		if (alarmManager != null) {
			alarmManager.cancel(pendingIntent);
		}
		pendingIntent.cancel();
	}


	private void initializeGPSForegroundLocationFetcher(Context context) {
		initializeGpsLocationUpdate();

		if (gpsForegroundLocationFetcher == null) {
			gpsForegroundLocationFetcher = new FusedLocationFetcherBackground(context, LOCATION_UPDATE_INTERVAL, LocationRequest.PRIORITY_HIGH_ACCURACY, gpsLocationUpdate);
		}
		if (fusedLocationFetcherBackgroundBalanced == null) {
			fusedLocationFetcherBackgroundBalanced = new FusedLocationFetcherBackground(context, LOCATION_UPDATE_INTERVAL, LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, fusedLocationUpdate);
		}

	}


	private void initializeGpsLocationUpdate() {
		if (gpsLocationUpdate == null) {
			gpsLocationUpdate = new GPSLocationUpdate() {

				@Override
				public void onGPSLocationChanged(Location location) {
					try {


						boolean locationLogged = false;
						if (location.getAccuracy() < MAX_ACCURACY) {
								if ((Utils.compareDouble(location.getLatitude(), 0.0) != 0) && (Utils.compareDouble(location.getLongitude(), 0.0) != 0)) {
									locationLogged= true;
									drawLocationChanged(location);
								}

							try {
								Database2.getInstance(context).updateDriverCurrentLocation(context, location);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						try {
							if(!locationLogged){
								MyApplication.getInstance().writePathLogToFile("m", "location received from gps: "+location);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						GpsDistanceCalculator.this.gpsDistanceUpdater.updateDistanceTime(totalDistance, getElapsedMillis(),
								getWaitTimeFromSP(context), lastGPSLocation,
								lastFusedLocation, totalHaversineDistance, true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				@Override
				public void refreshLocationFetchers(final Context context) {
					reconnectGPSHandler();
				}
			};
		}
		if (fusedLocationUpdate == null) {
			fusedLocationUpdate = new GPSLocationUpdate() {

				@Override
				public void onGPSLocationChanged(Location location) {
					lastFusedLocation = location;
					GpsDistanceCalculator.this.gpsDistanceUpdater.updateDistanceTime(totalDistance, getElapsedMillis(),
							getWaitTimeFromSP(context), lastGPSLocation,
							lastFusedLocation, totalHaversineDistance, false);
				}

				@Override
				public void refreshLocationFetchers(Context context) {
				}
			};
		}
	}



	private void connectGPSListener(Context context) {
		disconnectGPSListener();
		try {
			initializeGPSForegroundLocationFetcher(context);
			Log.e("gpsForegroundLocationFetcher", "connect called connectGPSListener");
			gpsForegroundLocationFetcher.connect();
			fusedLocationFetcherBackgroundBalanced.connect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void disconnectGPSListener() {
		try {
			if (gpsForegroundLocationFetcher != null) {
				gpsForegroundLocationFetcher.disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			if (fusedLocationFetcherBackgroundBalanced != null) {
				fusedLocationFetcherBackgroundBalanced.disconnect();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private synchronized void drawLocationChanged(Location location) {
		try {
			final LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
			long newLocationTime = System.currentTimeMillis();

			if (Utils.compareDouble(totalDistance, -1.0) == 0) {
				totalDistance = 0;
				totalHaversineDistance = 0;
				lastGPSLocation = null;
				lastLocationTime = System.currentTimeMillis();
			}


			LatLng lastLatLng = null;

			if (lastGPSLocation != null) {
				lastLatLng = new LatLng(lastGPSLocation.getLatitude(), lastGPSLocation.getLongitude());
			} else {
				GpsDistanceCalculator.this.gpsDistanceUpdater.drawOldPath();
				lastLatLng = getSavedLatLngFromSP(context);
			}

			long millisDiff = newLocationTime - lastLocationTime;
			long secondsDiff = millisDiff / 1000;

			double displacement = MapUtils.distance(lastLatLng, currentLatLng);
			if ((Utils.compareDouble(lastLatLng.latitude, 0.0) != 0) && (Utils.compareDouble(lastLatLng.longitude, 0.0) != 0)) {
				totalHaversineDistance = totalHaversineDistance + displacement;
				saveTotalHaversineDistanceToSP(context, totalHaversineDistance);
			}

			double speedMPS = 0;
			if (secondsDiff > 0) {
				speedMPS = displacement / secondsDiff;
			}

			if (speedMPS <= (double)(Prefs.with(context).getFloat(Constants.KEY_MAX_SPEED_THRESHOLD, (float) MAX_SPEED_THRESHOLD))) {
				if ((Utils.compareDouble(lastLatLng.latitude, 0.0) != 0) && (Utils.compareDouble(lastLatLng.longitude, 0.0) != 0)) {
					calculateWaitTime(speedMPS);
					boolean locationAccepted = addLatLngPathToDistance(lastLatLng, currentLatLng, location);
					if (lastGPSLocation == null) {
						MyApplication.getInstance().insertRideDataToEngagements("" + lastLatLng.latitude, "" + lastLatLng.longitude, "" + System.currentTimeMillis(),
								isInRideState());
						MyApplication.getInstance().writePathLogToFile("m", "first time lastLatLng =" + lastLatLng);
					}
					if(locationAccepted){
						lastGPSLocation = location;
					}
				} else {
					lastLocationTime = System.currentTimeMillis();
					saveData(context, lastGPSLocation, lastLocationTime);
					lastGPSLocation = location;
				}
			} else {
				reconnectGPSHandler();
			}
			MyApplication.getInstance().writePathLogToFile("m", "speedMPS=" + speedMPS + " currentLatLng =" + currentLatLng);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void calculateWaitTime(double speedMPS){
		long millisTillWaitWindow = System.currentTimeMillis() - lastWaitWindowTime;
		if(millisTillWaitWindow < WAITING_WINDOW_TIME_MILLIS){
			accumulativeSpeed = accumulativeSpeed + speedMPS;
			speedCounter++;
		} else{
			if(speedCounter == 0 || (accumulativeSpeed / speedCounter) <= waitSpeed()){
				long waitTime = getWaitTimeFromSP(context) + millisTillWaitWindow;
				saveWaitTimeToSP(context, waitTime);
				Log.e(TAG, "calculateWaitTime waitTime="+(waitTime/60000.0));
			}
			this.accumulativeSpeed = 0;
			this.speedCounter = 0;
			this.lastWaitWindowTime = System.currentTimeMillis();
		}
	}

	private double waitSpeed(){
		try {
			return Double.parseDouble(Prefs.with(context).getString(Constants.KEY_DRIVER_WAIT_SPEED, "2"));
		} catch (Exception e) {
			return 2;
		}
	}


	public void reconnectGPSHandler() {
		disconnectGPSListener();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				connectGPSListener(context);
			}
		}, 5000);
	}

	private synchronized boolean addLatLngPathToDistance(final LatLng lastLatLng, final LatLng currentLatLng, final Location currentLocation) {
		try {
			final double displacement = MapUtils.distance(lastLatLng, currentLatLng);
			if (!useDirectionsApi() || (Utils.compareDouble(displacement, Double.parseDouble(Prefs.with(context).getString(Constants.KEY_SP_METER_DISP_MIN_THRESHOLD, String.valueOf(14d)))) == 1
					&& Utils.compareDouble(displacement, Double.parseDouble(Prefs.with(context).getString(Constants.KEY_SP_METER_DISP_MAX_THRESHOLD, String.valueOf(200d)))) == -1)) {
				Log.e("addLatLngPathToDistance", "direct straight line");
				boolean validDistance = updateTotalDistance(lastLatLng, currentLatLng, displacement, currentLocation);
				if (validDistance) {
					if(isInRideState()) {
						Database2.getInstance(context).insertCurrentPathItem(-1, lastLatLng.latitude, lastLatLng.longitude,
								currentLatLng.latitude, currentLatLng.longitude, 0, 0);
					}
					GpsDistanceCalculator.this.gpsDistanceUpdater.updateDistanceTime(totalDistance, getElapsedMillis(),
							getWaitTimeFromSP(context), lastGPSLocation,
							lastFusedLocation, totalHaversineDistance, true);
					GpsDistanceCalculator.this.gpsDistanceUpdater.addPathToMap(new PolylineOptions().add(lastLatLng, currentLatLng));
				}
				return true;
			} else if(Utils.compareDouble(displacement, Double.parseDouble(Prefs.with(context).getString(Constants.KEY_SP_METER_DISP_MAX_THRESHOLD, String.valueOf(200d)))) >= 0){
				Log.e("addLatLngPathToDistance", "Google api");
				long rowId = -1;
				if(isInRideState()) {
					rowId = Database2.getInstance(context).insertCurrentPathItem(-1, lastLatLng.latitude, lastLatLng.longitude,
							currentLatLng.latitude, currentLatLng.longitude, 1, 1);
				}
				Log.e(TAG, "callGoogleDirectionsAPI");
				callGoogleDirectionsAPI(lastLatLng, currentLatLng, displacement, currentLocation, rowId);
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
	}

	private boolean isInRideState() {
		return getDriverScreenModeSP(context) == DriverScreenMode.D_IN_RIDE.getOrdinal();
	}

	private synchronized boolean updateTotalDistance(LatLng lastLatLng, LatLng currentLatLng, double deltaDistance, Location currentLocation) {
		boolean validDistance = false;
		try {
			if (deltaDistance > 0.0 && deltaDistance < Prefs.with(context).getInt(Constants.KEY_DELTA_DISTANCE_MAX, DELTA_DISTANCE_MAX)) {
				LatLngPair latLngPair = new LatLngPair(lastLatLng, currentLatLng, deltaDistance);
				if (deltaLatLngPairs == null) {
					deltaLatLngPairs = new ArrayList<LatLngPair>();
				}

				if (!deltaLatLngPairs.contains(latLngPair) && totalDistance < Prefs.with(context).getInt(Constants.KEY_TOTAL_DISTANCE_MAX, TOTAL_DISTANCE_MAX)) {
					totalDistance = totalDistance + deltaDistance;
					deltaLatLngPairs.add(latLngPair);
					validDistance = true;

					lastLocationTime = System.currentTimeMillis();

					MyApplication.getInstance().insertRideDataToEngagements("" + currentLatLng.latitude, "" + currentLatLng.longitude, "" + System.currentTimeMillis(),
							isInRideState());

					MyApplication.getInstance().writePathLogToFile("m",
							DateOperations.getTimeStampFromMillis(currentLocation.getTime()) + ","
									+ currentLatLng.latitude + ","
									+ currentLatLng.longitude + ","
									+ currentLocation.getAccuracy() + ","
									+ GpsDistanceCalculator.this.totalDistance + ","
									+ deltaDistance + ","
									+ lastLatLng.latitude + ","
									+ lastLatLng.longitude + ","
									+ DateOperations.getTimeStampFromMillis(GpsDistanceCalculator.this.lastLocationTime) + ","
									+ totalHaversineDistance);
					saveData(context, lastGPSLocation, lastLocationTime);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return validDistance;
	}


	private synchronized void callGoogleDirectionsAPI(LatLng lastLatLng, LatLng currentLatLng, double displacement, Location currentLocation, long rowId) {
		if (directionsAsyncTasks == null) {
			directionsAsyncTasks = new ArrayList<DirectionsAsyncTask>();
		}
		DirectionsAsyncTask directionsAsyncTask = new DirectionsAsyncTask(lastLatLng, currentLatLng, displacement, currentLocation, rowId);
		if (!directionsAsyncTasks.contains(directionsAsyncTask)) {
			directionsAsyncTasks.add(directionsAsyncTask);
			directionsAsyncTask.execute();
		}
	}

	private class DirectionsAsyncTask extends AsyncTask<Void, Void, GAPIDirections.DirectionsResult> {
		double displacementToCompare;
		LatLng source, destination;
		Location currentLocation;
		long rowId;

		public DirectionsAsyncTask(LatLng source, LatLng destination, double displacementToCompare, Location currentLocation, long rowId) {
			this.source = source;
			this.destination = destination;
			this.displacementToCompare = displacementToCompare;
			this.currentLocation = currentLocation;
			this.rowId = rowId;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			GpsDistanceCalculator.this.gpsDistanceUpdater.googleApiHitStart();
		}

		@Override
		protected GAPIDirections.DirectionsResult doInBackground(Void... params) {
			try {
				List<EngagementSPData> list = MyApplication.getInstance().getEngagementSP().getEngagementSPDatasArray();
				long engagementId = list.size() > 0 ? list.get(0).getEngagementId() : System.currentTimeMillis();

				return GAPIDirections.INSTANCE.getDirectionsPathSync(engagementId, source, destination, "metering");
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(GAPIDirections.DirectionsResult result) {
			super.onPostExecute(result);
			if (result != null) {
				updateGAPIDistance(result.getLatLngs(), result.getPath(), displacementToCompare, source, destination, currentLocation, rowId);
			}
			GpsDistanceCalculator.this.gpsDistanceUpdater.googleApiHitStop();
			directionsAsyncTasks.remove(this);
		}


		@Override
		public boolean equals(Object o) {
			try {
				DirectionsAsyncTask matchO = (DirectionsAsyncTask) o;
				if (((Utils.compareDouble(matchO.source.latitude, this.source.latitude) == 0)
						&& (Utils.compareDouble(matchO.source.longitude, this.source.longitude) == 0)) ||
						((Utils.compareDouble(matchO.destination.latitude, this.destination.latitude) == 0)
								&& (Utils.compareDouble(matchO.destination.longitude, this.destination.longitude) == 0))) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}


	private synchronized void updateGAPIDistance(List<LatLng> latLngs, Path path, double displacementToCompare, LatLng source, LatLng destination, Location currentLocation, long rowId) {
		try {
			double distanceOfPath = path.getDistance();
			if (Utils.compareDouble(distanceOfPath, (displacementToCompare * 1.5)) <= 0) {                                                        // distance would be approximately correct
				boolean validDistance = updateTotalDistance(source, destination, distanceOfPath, currentLocation);
				if (validDistance) {
					GpsDistanceCalculator.this.gpsDistanceUpdater.updateDistanceTime(totalDistance, getElapsedMillis(),
							getWaitTimeFromSP(context), lastGPSLocation,
							lastFusedLocation, totalHaversineDistance, true);

					List<LatLng> list = latLngs;

					PolylineOptions polylineOptions = new PolylineOptions();

					for (int z = 0; z < list.size() - 1; z++) {
						LatLng src = list.get(z);
						LatLng dest = list.get(z + 1);
						polylineOptions.add(new LatLng(src.latitude, src.longitude), new LatLng(dest.latitude, dest.longitude));

						if(rowId != -1) {
							Database2.getInstance(context).insertCurrentPathItem(rowId, src.latitude, src.longitude,
									dest.latitude, dest.longitude, 0, 0);
						}
					}

					if(rowId != -1) {
						Database2.getInstance(context).updateCurrentPathItemSectionIncomplete(rowId, 0);
					}

					GpsDistanceCalculator.this.gpsDistanceUpdater.addPathToMap(polylineOptions);
				}
				MyApplication.getInstance().writePathLogToFile("m", "gapi case successful");
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			e.printStackTrace();                                                                        // displacement would be correct
			boolean validDistance = updateTotalDistance(source, destination, displacementToCompare, currentLocation);
			if (validDistance) {
				GpsDistanceCalculator.this.gpsDistanceUpdater.updateDistanceTime(totalDistance, getElapsedMillis(),
						getWaitTimeFromSP(context), lastGPSLocation,
						lastFusedLocation, totalHaversineDistance, true);
				GpsDistanceCalculator.this.gpsDistanceUpdater.addPathToMap(new PolylineOptions().add(source, destination));

				if(rowId != -1) {
					Database2.getInstance(context).updateCurrentPathItemSectionIncompleteAndGooglePath(rowId);
				}

			}
			MyApplication.getInstance().writePathLogToFile("m", "gapi case unsuccessful");
		}
	}


	public long getElapsedMillis() {
		long rideStartTime = getStartTimeFromSP(context);
		long timeDiff = System.currentTimeMillis() - rideStartTime;
		long elapsedTime = 0;
		if (timeDiff > 0) {
			elapsedTime = timeDiff;
		}
		return elapsedTime;
	}


	public void saveData(Context context, Location location, long lastLocationTime) {
		if (location != null) {
			saveLatLngToSP(context, location.getLatitude(), location.getLongitude());

			double savedTotalDist = getTotalDistanceFromSP(context);
			if (totalDistance < savedTotalDist) {
				totalDistance = savedTotalDist;
			} else {
				saveTotalDistanceToSP(context, totalDistance);
			}
			saveLastLocationTimeToSP(context, lastLocationTime);
		}
	}


	public static synchronized void saveLatLngToSP(Context context, double latitude, double longitude) {
		Prefs.with(context).save(SPLabels.LOCATION_LAT, "" + latitude);
		Prefs.with(context).save(SPLabels.LOCATION_LNG, "" + longitude);
	}

	public static synchronized LatLng getSavedLatLngFromSP(Context context) {
		return new LatLng(Double.parseDouble(Prefs.with(context).getString(SPLabels.LOCATION_LAT, "" + 0)),
				Double.parseDouble(Prefs.with(context).getString(SPLabels.LOCATION_LNG, "" + 0)));
	}


	public static synchronized void saveTotalDistanceToSP(Context context, double totalDistance) {
		Prefs.with(context).save(SPLabels.TOTAL_DISTANCE, "" + totalDistance);
		Database2.getInstance(context).updateTotalDistance(totalDistance);
	}

	public static synchronized double getTotalDistanceFromSP(Context context) {
		double spDistance = Double.parseDouble(Prefs.with(context).getString(SPLabels.TOTAL_DISTANCE, "" + -1));
		double dbDistance = Database2.getInstance(context).getTotalDistance();
		if(spDistance >= dbDistance) {
			return spDistance;
		}
		else {
			return dbDistance;
		}
	}

	public static synchronized void saveTotalHaversineDistanceToSP(Context context, double totalHaversineDistance) {
		Prefs.with(context).save(SPLabels.TOTAL_HAVERSINE_DISTANCE, "" + totalHaversineDistance);
	}

	public static synchronized double getTotalHaversineDistanceFromSP(Context context) {
		return Double.parseDouble(Prefs.with(context).getString(SPLabels.TOTAL_HAVERSINE_DISTANCE, "" + -1));
	}


	public static synchronized void saveLastLocationTimeToSP(Context context, long lastLocationTime) {
		Prefs.with(context).save(SPLabels.LOCATION_TIME, "" + lastLocationTime);
	}

	public static synchronized long getLastLocationTimeFromSP(Context context) {
		return Long.parseLong(Prefs.with(context).getString(SPLabels.LOCATION_TIME, "" + System.currentTimeMillis()));
	}

	public static synchronized void saveStartTimeToSP(Context context, long startTime) {
		Prefs.with(context).save(SPLabels.START_TIME, "" + startTime);
	}

	public static synchronized long getStartTimeFromSP(Context context) {
		return Long.parseLong(Prefs.with(context).getString(SPLabels.START_TIME, "0"));
	}

	public static synchronized void saveWaitTimeToSP(Context context, long waitTime) {
		Prefs.with(context).save(SPLabels.WAIT_TIME, "" + waitTime);
		Database2.getInstance(context).updateWaitTime(String.valueOf(waitTime));
	}

	public static synchronized long getWaitTimeFromSP(Context context) {

		try {
			long waitTimeFromSP = Long.parseLong(Prefs.with(context).getString(SPLabels.WAIT_TIME, "0"));
			long waitTimeFromDB = Long.parseLong(Database2.getInstance(context).getWaitTimeFromDB());

			if (waitTimeFromDB >= waitTimeFromSP) {
				return waitTimeFromDB;
			} else {
				return waitTimeFromSP;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static synchronized void saveTrackingToSP(Context context, int tracking) {
		Prefs.with(context).save(SPLabels.TRACKING, tracking);
	}

	public static synchronized int getTrackingFromSP(Context context) {
		try {
			return Prefs.with(context).getInt(SPLabels.TRACKING, 0);
		} catch (Exception e) {
			e.printStackTrace();
			saveTrackingToSP(context, 0);
			return 0;
		}
	}

	public static synchronized boolean isMeteringStateActive(Context context) {
		int tracking = getTrackingFromSP(context);
		if(1 == tracking){
			return true;
		}
		else{
			boolean hasKey = Prefs.with(context).contains(SPLabels.TRACKING);
			if(hasKey){
				return false;
			}
			else{
				String meteringState = Database2.getInstance(context).getMetringState();
				String meteringStateSp= Prefs.with(context).getString(SPLabels.METERING_STATE, Database2.OFF);
				if(!Database2.ON.equalsIgnoreCase(meteringState) && !Database2.ON.equalsIgnoreCase(meteringStateSp)){
					return false;
				}
				else{
					saveTrackingToSP(context, 1);
					return true;
				}
			}
		}
	}

	public void updateDistanceInCaseOfReset(final double distance, final long rideTime, final long waitTime){
		MyApplication.getInstance().writePathLogToFile("m",
				"updateDistanceInCaseOfReset func distance from server:" + distance
						+ " & totalDistance:" + totalDistance + " & waitTime:"+waitTime+" & rideTime:"+rideTime);
		if(distance > totalDistance + DISTANCE_RESET_TOLERANCE){
			totalDistance = totalDistance + distance;
			saveTotalDistanceToSP(context, totalDistance);
			MyApplication.getInstance().writePathLogToFile("m",
					"updateDistanceInCaseOfReset func totalDistance updated:"+ totalDistance);
		}
		long spElapsedTime = getElapsedMillis();
		if(rideTime > spElapsedTime + WAIT_TIME_RESET_TOLERANCE){
			saveStartTimeToSP(context, System.currentTimeMillis() - rideTime - spElapsedTime);
			MyApplication.getInstance().writePathLogToFile("m",
					"updateDistanceInCaseOfReset func rideTime updated:"+ (System.currentTimeMillis() - rideTime - spElapsedTime));
		}
		long spWaitTime = getWaitTimeFromSP(context);
		if(waitTime > spWaitTime + WAIT_TIME_RESET_TOLERANCE){
			saveWaitTimeToSP(context, waitTime+spWaitTime);
			MyApplication.getInstance().writePathLogToFile("m",
					"updateDistanceInCaseOfReset func waitTime updated:"+ (waitTime+spWaitTime));
		}
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, "Distance reset case => "+distance+", "+rideTime+", "+waitTime, Toast.LENGTH_LONG).show();
			}
		});
	}

	public static synchronized void saveDriverScreenModeMetering(Context context, DriverScreenMode driverScreenMode) {
		Prefs.with(context).save(SPLabels.DRIVER_SCREEN_MODE_METERING, driverScreenMode.getOrdinal());
	}

	public static synchronized int getDriverScreenModeSP(Context context) {
		int state = Prefs.with(context).getInt(SPLabels.DRIVER_SCREEN_MODE_METERING, DriverScreenMode.D_IN_RIDE.getOrdinal());
		return state;
	}




	private boolean useDirectionsApi(){
		return !HomeActivity.isAltMeteringEnabledForDriver(context)
				&& Prefs.with(context).getInt(Constants.KEY_USE_DIRECTIONS_API_FOR_METERING, 1) == 1;
	}

	private Handler handler;
	private boolean uploadRunnablesRunning;
	private static final long PATH_UPLOAD_INTERVAL = 15000;
	private static final long UPLOAD_IN_RIDE_DATA_INTERVAL = 30000;

	private Runnable pathUploadRunnable = new Runnable() {
		@Override
		public void run() {
			if(uploadRunnablesRunning) {
				GpsDistanceRideDataUpload.INSTANCE.uploadInRidePath(context, () -> handler.postDelayed(pathUploadRunnable, PATH_UPLOAD_INTERVAL));
			}
		}
	};
	private Runnable inRideDataUploadRunnable = new Runnable() {
		@Override
		public void run() {
			if(uploadRunnablesRunning){
				GpsDistanceRideDataUpload.INSTANCE.updateInRideData(context, lastGPSLocation, () -> handler.postDelayed(inRideDataUploadRunnable, UPLOAD_IN_RIDE_DATA_INTERVAL));
			}
		}
	};

	private void startUploadRunnables(){
		handler.removeCallbacks(pathUploadRunnable);
		handler.removeCallbacks(inRideDataUploadRunnable);
		uploadRunnablesRunning = true;
		handler.postDelayed(pathUploadRunnable, PATH_UPLOAD_INTERVAL);
		handler.postDelayed(inRideDataUploadRunnable, UPLOAD_IN_RIDE_DATA_INTERVAL);
	}

	private void stopUploadRunnables(){
		uploadRunnablesRunning = false;
		handler.removeCallbacks(pathUploadRunnable);
		handler.removeCallbacks(inRideDataUploadRunnable);
	}

}

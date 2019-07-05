package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;

import com.google.android.gms.location.LocationRequest;

import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.MapUtils;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;

public class DriverLocationUpdateService extends Service implements GPSLocationUpdate{

	private static final String TAG = DriverLocationUpdateService.class.getSimpleName();
	
	private static FusedLocationFetcherBackground locationFetcherDriver;

	public static int DRIVER_UPDATE_SERVICE_NOTFI_ID = 1818;

	public DriverLocationUpdateService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implemented");
	}
	



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	super.onStartCommand(intent, flags, startId);

		try {
			int isOffline = getSharedPreferences(SPLabels.SETTINGS_SP,Context.MODE_PRIVATE).getInt(Constants.IS_OFFLINE, 1);
			Log.i(TAG, "onStartCommand: is Offline" +	isOffline );

			if(locationFetcherDriver != null){
				locationFetcherDriver.disconnect();
				locationFetcherDriver = null;
			}

			if(isOffline == 0){
                startForeground(DRIVER_UPDATE_SERVICE_NOTFI_ID,MeteringService.generateNotification(this,getString(R.string.update_location_notif_label),DRIVER_UPDATE_SERVICE_NOTFI_ID));


				String driverServiceRun = Database2.getInstance(this).getDriverServiceRun();
				if(Database2.YES.equalsIgnoreCase(driverServiceRun)){
					String fast = Database2.getInstance(DriverLocationUpdateService.this).getDriverServiceFast();
					long interval = 120000;
					if(fast.equalsIgnoreCase(Database2.NO)){
						interval = Prefs.with(this).getLong(Constants.FREE_STATE_UPDATE_TIME_PERIOD, 120000);
					} else{
						interval = Prefs.with(this).getLong(Constants.ACCEPTED_STATE_UPDATE_TIME_PERIOD, 15000);
					}
					Log.i(TAG, "onStartCommand interval="+interval);
					locationFetcherDriver = new FusedLocationFetcherBackground(this, interval, LocationRequest.PRIORITY_HIGH_ACCURACY, this);
					locationFetcherDriver.connect();
					setupLocationUpdateAlarm();
				}
				else{
					stopForeground(true);
					stopSelf();
				}
            }else{
                stopForeground(true);
                stopSelf();
            }
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Service.START_STICKY;
    }



	@Override
    public void onTaskRemoved(Intent rootIntent) {
		super.onTaskRemoved(rootIntent);
		Log.i(TAG, "onTaskRemoved rootIntent="+rootIntent);
//    	try {
//    		String driverServiceRun = Database2.getInstance(this).getDriverServiceRun();
//
//    		if(Database2.YES.equalsIgnoreCase(driverServiceRun)) {
//				Log.i("driverLocation", driverServiceRun);
//				Log.i("driverLocation", driverServiceRun + " " + driverServiceRun);
//				Intent restartService = new Intent(getApplicationContext(), this.getClass());
//				restartService.setPackage(getPackageName());
//				PendingIntent restartServicePI = PendingIntent.getService(getApplicationContext(), 1, restartService, PendingIntent.FLAG_ONE_SHOT);
//				AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//				alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePI);
//			} else{
//				stopService(new Intent(getApplicationContext(), DriverLocationUpdateService.class));
//    		}
//    		stopForeground(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
    }

	@Override
	public void onGPSLocationChanged(Location location) {
		onReceiveLocation(this, location);
	}

	@Override
	public void refreshLocationFetchers(Context context) {

	}

	public void destroyLocationFetcher(){
		if(locationFetcherDriver != null){
			locationFetcherDriver.disconnect();
			locationFetcherDriver = null;

		}
	}

    @Override
    public void onDestroy() {
		Log.i(TAG, "onDestroy: ");
		destroyLocationFetcher();

		if (!Database2.YES.equalsIgnoreCase(Database2.getInstance(this).getDriverServiceRun())) {
			cancelLocationUpdateAlarm();
		}
		stopForeground(true);
		stopSelf();

	}

	@Override
	public void onTrimMemory(int level) {
		Database2.getInstance(this).insertUSLLog(Constants.EVENT_DLD_TRIM_MEMORY+"_"+level);
		super.onTrimMemory(level);
	}

	@Override
	public void onLowMemory() {
		Database2.getInstance(this).insertUSLLog(Constants.EVENT_DLD_LOW_MEMORY);
		super.onLowMemory();
	}

	private static int DRIVER_LOCATION_PI_REQUEST_CODE = 111;
	private static final String SEND_LOCATION = "product.clicklabs.jugnoo.driver.SEND_LOCATION";
	private static final long ALARM_REPEAT_INTERVAL = 3 * 60000;
	
	
	public void setupLocationUpdateAlarm() {
        // check task is scheduled or not
        boolean alarmUp = (PendingIntent.getBroadcast(this, DRIVER_LOCATION_PI_REQUEST_CODE,
            new Intent(this, DriverLocationUpdateAlarmReceiver.class).setAction(SEND_LOCATION),
            PendingIntent.FLAG_NO_CREATE) != null);

        if (alarmUp) {
            cancelLocationUpdateAlarm();
        }

        Intent intent = new Intent(this, DriverLocationUpdateAlarmReceiver.class);
        intent.setAction(SEND_LOCATION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, DRIVER_LOCATION_PI_REQUEST_CODE,
            intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), ALARM_REPEAT_INTERVAL, pendingIntent);

    }

    public void cancelLocationUpdateAlarm() {
        Intent intent = new Intent(this, DriverLocationUpdateAlarmReceiver.class);
        intent.setAction(SEND_LOCATION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, DRIVER_LOCATION_PI_REQUEST_CODE,
            intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Activity.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

	public static final double FREE_MAX_ACCURACY = 200;
	public static final double MAX_TIME_WINDOW = 3600000;
	public void onReceiveLocation(final Context context, Location location) {
		try {
			if (!Utils.mockLocationEnabled(location)) {
				if (location != null) {
					Location oldlocation = Database2.getInstance(context).getDriverCurrentLocation(context);
					double displacement_1 = MapUtils.distance(oldlocation, location);
					double timediff_1 = (System.currentTimeMillis() - oldlocation.getTime()) / 1000L;
					double speed_1 = displacement_1 / timediff_1;

					if (timediff_1 > 0 && speed_1 > (double)(Prefs.with(context).getFloat(Constants.KEY_MAX_SPEED_THRESHOLD,
							(float) GpsDistanceCalculator.MAX_SPEED_THRESHOLD))) {
						Log.i(TAG, "onReceive DriverLocationUpdateService restarted speed_1="+speed_1);
						setAlarm();
						Database2.getInstance(context).insertUSLLog(Constants.EVENT_LR_SPEED_20PLUS_RESTART);
					} else {

						if (location.getAccuracy() > FREE_MAX_ACCURACY) {
							Prefs.with(context).save(SPLabels.BAD_ACCURACY_COUNT, Prefs.with(context).getInt(SPLabels.BAD_ACCURACY_COUNT, 0) + 1);
						}

						long timeLapse = System.currentTimeMillis() - Prefs.with(context).getLong(SPLabels.ACCURACY_SAVED_TIME, 0);
						if (timeLapse <= MAX_TIME_WINDOW && (0 == (Prefs.with(context).getInt(SPLabels.TIME_WINDOW_FLAG, 0)))) {
							if (5 <= Prefs.with(context).getInt(SPLabels.BAD_ACCURACY_COUNT, 0)) {

								location.setAccuracy(3000.001f);
								Database2.getInstance(context).insertUSLLog(Constants.EVENT_LR_5_LOC_BAD_ACCURACY);

								Prefs.with(context).save(SPLabels.BAD_ACCURACY_COUNT, 0);
								Prefs.with(context).save(SPLabels.TIME_WINDOW_FLAG, 1);
							}
						} else if (timeLapse > MAX_TIME_WINDOW) {
							Prefs.with(context).save(SPLabels.ACCURACY_SAVED_TIME, System.currentTimeMillis());
							Prefs.with(context).save(SPLabels.BAD_ACCURACY_COUNT, 0);
							Prefs.with(context).save(SPLabels.TIME_WINDOW_FLAG, 0);
						}
						new Thread(new Runnable() {
							@Override
							public void run() {
								Database2.getInstance(context).updateDriverCurrentLocation(context, location);
								new DriverLocationDispatcher().sendLocationToServer(context);
							}
						}).start();


						int currentapiVersion = android.os.Build.VERSION.SDK_INT;


						if(Prefs.with(context).getInt(Constants.IS_OFFLINE, 0) == 1){
							Prefs.with(context).save(Constants.FREE_STATE_UPDATE_TIME_PERIOD,
									Prefs.with(context).getLong(Constants.OFFLINE_UPDATE_TIME_PERIOD, 180000l));
						} else {
							if (Utils.isBatteryCharging(context)) {
								if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP) {
									// Do something for lollipop and above versions
									if (Prefs.with(context).getLong(Constants.FREE_STATE_UPDATE_TIME_PERIOD, 120000l)
											!= Prefs.with(context).getLong(Constants.FREE_STATE_UPDATE_TIME_PERIOD_CHARGING_V5, 10000l)) {
										Prefs.with(context).save(Constants.FREE_STATE_UPDATE_TIME_PERIOD,
												Prefs.with(context).getLong(Constants.FREE_STATE_UPDATE_TIME_PERIOD_CHARGING_V5, 10000l));
										setAlarm();
									}
								} else {
									// do something for phones running an SDK before lollipop
									if (Prefs.with(context).getLong(Constants.FREE_STATE_UPDATE_TIME_PERIOD, 120000l)
											!= Prefs.with(context).getLong(Constants.FREE_STATE_UPDATE_TIME_PERIOD_CHARGING, 10000l)) {
										Prefs.with(context).save(Constants.FREE_STATE_UPDATE_TIME_PERIOD,
												Prefs.with(context).getLong(Constants.FREE_STATE_UPDATE_TIME_PERIOD_CHARGING, 10000l));
										setAlarm();
									}
								}
							} else {
								if (location.getAccuracy() > 200) {
									Log.i(TAG, "onReceive DriverLocationUpdateService restarted location.getAccuracy()=" + location.getAccuracy());
									setAlarm();
									Database2.getInstance(context).insertUSLLog(Constants.EVENT_LR_LOC_BAD_ACCURACY_RESTART);
								} else {
									if (Prefs.with(context).getLong(Constants.FREE_STATE_UPDATE_TIME_PERIOD, 120000l)
											!= Prefs.with(context).getLong(Constants.FREE_STATE_UPDATE_TIME_PERIOD_NON_CHARGING, 120000l)) {
										Prefs.with(context).save(Constants.FREE_STATE_UPDATE_TIME_PERIOD,
												Prefs.with(context).getLong(Constants.FREE_STATE_UPDATE_TIME_PERIOD_NON_CHARGING, 120000l));
										setAlarm();
									}
								}
							}
						}

						if((Utils.compareDouble(oldlocation.getLatitude(), location.getLatitude())==0)
								&& (Utils.compareDouble(oldlocation.getLongitude(), location.getLongitude())==0)){
							Database2.getInstance(context).insertUSLLog(Constants.EVENT_LRD_STALE_GPS_RESTART_SERVICE);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Handler handler;
	private Runnable runnableReconnectLocation;
	private void setAlarm(){
		locationFetcherDriver.disconnect();

		if(handler == null){
			handler = new Handler();
		}
		if(runnableReconnectLocation == null){
			runnableReconnectLocation = () -> locationFetcherDriver.connect();
		}
		handler.postDelayed(runnableReconnectLocation, 5000);
		Log.i(TAG, "setAlarm");
	}

}

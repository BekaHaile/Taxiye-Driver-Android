package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;

import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.utils.DeviceTokenGenerator;
import product.clicklabs.jugnoo.driver.utils.IDeviceTokenReceiver;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;

public class DriverLocationUpdateService extends Service {
	
	LocationFetcherDriver locationFetcherDriver;


	public DriverLocationUpdateService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	
	@Override
    public void onCreate() {
        
    }


	public static void updateServerData(final Context context){
    	String SHARED_PREF_NAME = "myPref";
    	String SP_ACCESS_TOKEN_KEY = "access_token";
    	String accessToken = "", deviceToken = "", SERVER_URL = "";
    	
    	//TODO Toggle live to trial
		String DEV_SERVER_URL = "https://test.jugnoo.in:8012";
		String LIVE_SERVER_URL = "https://prod-autos-api.jugnoo.in";
		String TRIAL_SERVER_URL = "https://test.jugnoo.in:8200";

        String DEV_1_SERVER_URL = "https://test.jugnoo.in:8013";
        String DEV_2_SERVER_URL = "https://test.jugnoo.in:8014";
        String DEV_3_SERVER_URL = "https://test.jugnoo.in:8015";

		String DEFAULT_SERVER_URL = LIVE_SERVER_URL;

		String CUSTOM_URL = Prefs.with(context).getString(SPLabels.CUSTOM_SERVER_URL, DEFAULT_SERVER_URL);

		
		
		
		
		String SETTINGS_SHARED_PREF_NAME = "settingsPref", SP_SERVER_LINK = "sp_server_link";
		
		SERVER_URL = DEFAULT_SERVER_URL;
		
		SharedPreferences preferences = context.getSharedPreferences(SETTINGS_SHARED_PREF_NAME, 0);
		String link = preferences.getString(SP_SERVER_LINK, DEFAULT_SERVER_URL);
		
		if(link.equalsIgnoreCase(TRIAL_SERVER_URL)){
			SERVER_URL = TRIAL_SERVER_URL;
		}
		else if(link.equalsIgnoreCase(DEV_SERVER_URL)){
			SERVER_URL = DEV_SERVER_URL;
		}
		else if(link.equalsIgnoreCase(LIVE_SERVER_URL)){
			SERVER_URL = LIVE_SERVER_URL;
		}
        else if(link.equalsIgnoreCase(DEV_1_SERVER_URL)){
            SERVER_URL = DEV_1_SERVER_URL;
        }
        else if(link.equalsIgnoreCase(DEV_2_SERVER_URL)){
            SERVER_URL = DEV_2_SERVER_URL;
        }
        else if(link.equalsIgnoreCase(DEV_3_SERVER_URL)){
            SERVER_URL = DEV_3_SERVER_URL;
        }
		else{
			SERVER_URL = CUSTOM_URL;
		}

		
		SharedPreferences pref = context.getSharedPreferences(SHARED_PREF_NAME, 0);
		accessToken = pref.getString(SP_ACCESS_TOKEN_KEY, "");

		final String finalAccessToken = accessToken;
		final String finalSERVER_URL = SERVER_URL;
		new DeviceTokenGenerator(context).generateDeviceToken(context, new IDeviceTokenReceiver() {
			@Override
			public void deviceTokenReceived(String deviceToken) {
				try {
					Database2.getInstance(context).insertDriverLocData(finalAccessToken, deviceToken, finalSERVER_URL);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		String pushyToken = context.getSharedPreferences(SplashLogin.class.getSimpleName(),
				Context.MODE_PRIVATE).getString("pushy_registration_id", "");
		Database2.getInstance(context).updatePushyToken(pushyToken);

		RestClient.setupRestClient(SERVER_URL);

    }
    
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	super.onStartCommand(intent, flags, startId);
		RestClient.setCurrentUrl("");

		if(Utils.isServiceRunning(this, DriverLocationUpdateService.class)){
			}
		else{
		}

    	return Service.START_STICKY;
    }

	@Override
	public void onStart(Intent intent, int startId) {
		try{
			String driverServiceRun = Database2.getInstance(this).getDriverServiceRun();
			if(Database2.YES.equalsIgnoreCase(driverServiceRun)){
				updateServerData(this);
				String fast = Database2.getInstance(DriverLocationUpdateService.this).getDriverServiceFast();
				if(fast.equalsIgnoreCase(Database2.NO)){
					if(locationFetcherDriver != null){
						locationFetcherDriver.destroy();
						locationFetcherDriver = null;
					}
					locationFetcherDriver = new LocationFetcherDriver(DriverLocationUpdateService.this, Prefs.with(this).getLong(Constants.FREE_STATE_UPDATE_TIME_PERIOD, 120000));
				}
				else{
					if(locationFetcherDriver != null){
						locationFetcherDriver.destroy();
						locationFetcherDriver = null;
					}
					locationFetcherDriver = new LocationFetcherDriver(DriverLocationUpdateService.this, Prefs.with(this).getLong(Constants.ACCEPTED_STATE_UPDATE_TIME_PERIOD, 15000));
				}
				setupLocationUpdateAlarm();
			}
			else{
				stopService(new Intent(this, DriverLocationUpdateService.class));
			}

		} catch(Exception e){
			e.printStackTrace();
		}
	}



	@Override
    public void onTaskRemoved(Intent rootIntent) {
    	try {
    		String driverServiceRun = Database2.getInstance(this).getDriverServiceRun();
			Log.i("driverLocation","");
    		if(Database2.YES.equalsIgnoreCase(driverServiceRun)) {
				Log.i("driverLocation", driverServiceRun);
				Log.i("driverLocation", driverServiceRun + " " + driverServiceRun);
				Intent restartService = new Intent(getApplicationContext(), this.getClass());
				restartService.setPackage(getPackageName());
				PendingIntent restartServicePI = PendingIntent.getService(getApplicationContext(), 1, restartService, PendingIntent.FLAG_ONE_SHOT);
				AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
				alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePI);
			} else{
				stopService(new Intent(this, DriverLocationUpdateService.class));
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    


	public void destroyLocationFetcher(){
		if(locationFetcherDriver != null){
			locationFetcherDriver.destroy();
			locationFetcherDriver = null;
		}
	}

    @Override
    public void onDestroy() {
		destroyLocationFetcher();
		if (!Database2.YES.equalsIgnoreCase(Database2.getInstance(this).getDriverServiceRun())) {
			cancelLocationUpdateAlarm();
		}
        
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


}

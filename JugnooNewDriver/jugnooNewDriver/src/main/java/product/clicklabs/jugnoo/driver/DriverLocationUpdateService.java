package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;

public class DriverLocationUpdateService extends Service {
	
	LocationFetcherDriver locationFetcherDriver;

	long serverUpdateTimePeriod = 90000;
	
	
	public DriverLocationUpdateService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	
	@Override
    public void onCreate() {
        
    }

	
    @Override
    public void onStart(Intent intent, int startId) {
        try{
        	String userMode = Database2.getInstance(this).getUserMode();
    		if(Database2.UM_DRIVER.equalsIgnoreCase(userMode)){
	        	updateServerData(this);
	    		String fast = Database2.getInstance(DriverLocationUpdateService.this).getDriverServiceFast();
	    		if(fast.equalsIgnoreCase(Database2.NO)){
	    			if(locationFetcherDriver != null){
	    				locationFetcherDriver.destroy();
	    				locationFetcherDriver = null;
	    			}
	    			serverUpdateTimePeriod = 90000;
	    			locationFetcherDriver = new LocationFetcherDriver(DriverLocationUpdateService.this, serverUpdateTimePeriod);
	    		}
	    		else{
	    			if(locationFetcherDriver != null){
	    				locationFetcherDriver.destroy();
	    				locationFetcherDriver = null;
	    			}
	    			serverUpdateTimePeriod = 15000;
	    			locationFetcherDriver = new LocationFetcherDriver(DriverLocationUpdateService.this, serverUpdateTimePeriod);
	    		}
	            setupLocationUpdateAlarm();
    		}
    		else{
    			new DriverServiceOperations().stopService(this);
    		}
        	
        } catch(Exception e){
        	e.printStackTrace();
        }
    }
    
    
    public static void updateServerData(Context context){
    	String SHARED_PREF_NAME = "myPref";
    	String SP_ACCESS_TOKEN_KEY = "access_token";
    	String accessToken = "", deviceToken = "", SERVER_URL = "";
    	
    	//TODO Toggle live to trial
		String DEV_SERVER_URL = "https://test.jugnoo.in:8012";
		String LIVE_SERVER_URL = "https://dev.jugnoo.in:4012";
		String TRIAL_SERVER_URL = "https://test.jugnoo.in:8200";

        String DEV_1_SERVER_URL = "https://test.jugnoo.in:8013";
        String DEV_2_SERVER_URL = "https://test.jugnoo.in:8014";
        String DEV_3_SERVER_URL = "https://test.jugnoo.in:8015";

		String DEFAULT_SERVER_URL = LIVE_SERVER_URL;
		
		
		
		
		
		String SETTINGS_SHARED_PREF_NAME = "settingsPref", SP_SERVER_LINK = "sp_server_link";
		
		SERVER_URL = DEFAULT_SERVER_URL;
		
		SharedPreferences preferences = context.getSharedPreferences(SETTINGS_SHARED_PREF_NAME, 0);
		String link = preferences.getString(SP_SERVER_LINK, DEFAULT_SERVER_URL);
		
		if(link.equalsIgnoreCase(TRIAL_SERVER_URL)){
			SERVER_URL = TRIAL_SERVER_URL.substring(0, TRIAL_SERVER_URL.length()-4) + Database2.getInstance(context).getSalesPortNumber();
		}
		else if(link.equalsIgnoreCase(DEV_SERVER_URL)){
			SERVER_URL = DEV_SERVER_URL.substring(0, DEV_SERVER_URL.length()-4) + Database2.getInstance(context).getDevPortNumber();
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
			SERVER_URL = LIVE_SERVER_URL.substring(0, LIVE_SERVER_URL.length()-4) + Database2.getInstance(context).getLivePortNumber();
		}

		
		SharedPreferences pref = context.getSharedPreferences(SHARED_PREF_NAME, 0);
		accessToken = pref.getString(SP_ACCESS_TOKEN_KEY, "");
		
		deviceToken = context.getSharedPreferences(SplashLogin.class.getSimpleName(), 
				Context.MODE_PRIVATE).getString("registration_id", "");
		String pushyToken = context.getSharedPreferences(SplashLogin.class.getSimpleName(),
				Context.MODE_PRIVATE).getString("pushy_registration_id", "");
    	

		Database2.getInstance(context).insertDriverLocData(accessToken, deviceToken, SERVER_URL);
		Database2.getInstance(context).updatePushyToken(pushyToken);
    }
    
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	
    	if(isMyServiceRunning()){
    	}
    	else{
    	}
    	
    	super.onStartCommand(intent, flags, startId);
    	return Service.START_STICKY;
    }
    
    
    @Override
    public void onTaskRemoved(Intent rootIntent) {
    	try {
    		String userMode = Database2.getInstance(this).getUserMode();
    		if(Database2.UM_DRIVER.equalsIgnoreCase(userMode)){
	    		String serviceRestartOnReboot = Database2.getInstance(DriverLocationUpdateService.this).getDriverServiceRun();
	    		if(Database2.YES.equalsIgnoreCase(serviceRestartOnReboot)){
	    			Intent restartService = new Intent(getApplicationContext(), this.getClass());
	    			restartService.setPackage(getPackageName());
	    			PendingIntent restartServicePI = PendingIntent.getService(getApplicationContext(), 1, restartService, PendingIntent.FLAG_ONE_SHOT);
	    			AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
	    			alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePI);
	    		}
    		}
    		else{
    			new DriverServiceOperations().stopService(this);
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    
    
    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (DriverLocationUpdateService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    
    
    
 
    @Override
    public void onDestroy() {
        if(locationFetcherDriver != null){
        	locationFetcherDriver.destroy();
        	locationFetcherDriver = null;
        }

        cancelLocationUpdateAlarm();
        
        Database2.getInstance(DriverLocationUpdateService.this).close();
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

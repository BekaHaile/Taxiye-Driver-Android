package product.clicklabs.jugnoo;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.SystemClock;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.maps.model.LatLng;

public class DriverLocationUpdateService extends Service {
	
	static int count = 0; 
	
	LocationFetcher locationFetcher;
	
	SendDriverLocationToServer sendDriverLocationToServer;
	
	boolean stop = false;
	
	LatLng lastLocation;
	boolean noUpdate = true;
	
	public DriverLocationUpdateService() {
		Log.e("DriverLocationUpdateService"," instance created");
		
		lastLocation = new LatLng(30.7900, 76.7800);
		stop = false;
		noUpdate = true;
		count = 0; 
		
	}

	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	
	@Override
    public void onCreate() {
       
//        Notification notification = new Notification();
//        startForeground(1, notification);
        
        
    }
 
	
	
    @Override
    public void onStart(Intent intent, int startId) {
    	//For time consuming an long tasks you can launch a new thread here...
       
        try{
        	
        	
        	if(sendDriverLocationToServer != null){
        		sendDriverLocationToServer.cancel(true);
        		sendDriverLocationToServer = null;
        	}
        	
        	sendDriverLocationToServer = new SendDriverLocationToServer();
        	sendDriverLocationToServer.execute();
        	
        	
        } catch(Exception e){
        	e.printStackTrace();
        }
        
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	
    	if(isMyServiceRunning()){
    		Log.e("service already running","-===");
    	}
    	else{
    		Log.e("service running not","-===");
    	}
    	
    	super.onStartCommand(intent, flags, startId);
    	return Service.START_STICKY;
    }
    
    
    @Override
    public void onTaskRemoved(Intent rootIntent) {
    	Log.e("onTaskRemoved","="+rootIntent);
        Intent restartService = new Intent(getApplicationContext(),
                this.getClass());
        restartService.setPackage(getPackageName());
        PendingIntent restartServicePI = PendingIntent.getService(
                getApplicationContext(), 1, restartService,
                PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() +1000, restartServicePI);

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
    	stop = true;
    	Log.e("DriverLocationUpdateService onDestroy","=");
        if(sendDriverLocationToServer != null){
    		sendDriverLocationToServer.cancel(true);
    		sendDriverLocationToServer = null;
    	}
        if(locationFetcher != null){
        	locationFetcher.destroy();
        	locationFetcher = null;
        }
    }
    
    
    double distance(LatLng start, LatLng end) {
		try {
			Location location1 = new Location("locationA");
			location1.setLatitude(start.latitude);
			location1.setLongitude(start.longitude);
			Location location2 = new Location("locationA");
			location2.setLatitude(end.latitude);
			location2.setLongitude(end.longitude);

			double distance = location1.distanceTo(location2);
			return distance;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;

	}
    
    class SendDriverLocationToServer extends AsyncTask<String, Integer, String>{
    	
    	
    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
    		
//    		if(locationFetcher != null){
//				locationFetcher.destroy();
//				locationFetcher = null;
//			}
			
			
			if(locationFetcher == null){
				locationFetcher = new LocationFetcher(DriverLocationUpdateService.this);
			}
			
    	}
    	
    	
    	
    	
    	
    	
    	
    	
    	@Override
    	protected String doInBackground(String... params) {
    		
    		
    		//Toggle live to trial
//    		String LIVE_SERVER_URL = "http://dev.jugnoo.in:4004";
//    		String TRIAL_SERVER_URL = "http://54.81.229.172:8001";
    		String SERVER_URL = "http://54.81.229.172:8001";
    		
//    		String SETTINGS_SHARED_PREF_NAME = "settingsPref", SP_SERVER_LINK = "sp_server_link";
//    		
//    		SharedPreferences preferences = getSharedPreferences(SETTINGS_SHARED_PREF_NAME, 0);
//    		String link = preferences.getString(SP_SERVER_LINK, TRIAL_SERVER_URL);
//    		
//    		if(link.equalsIgnoreCase(TRIAL_SERVER_URL)){
//    			SERVER_URL = TRIAL_SERVER_URL;
//    		}
//    		else if(link.equalsIgnoreCase(LIVE_SERVER_URL)){
//    			SERVER_URL = LIVE_SERVER_URL;
//    		}
    		
    		
    		
    		
    		
    		
    		
    		String GOOGLE_PROJECT_ID = "506849624961", 
    				PROPERTY_REG_ID = "registration_id", 
    				PROPERTY_APP_VERSION = "appVersion";
    		
    		String SHARED_PREF_NAME = "myPref";
    		String SP_ACCESS_TOKEN_KEY = "access_token";
    		
    		SharedPreferences pref = getSharedPreferences(SHARED_PREF_NAME, 0);
    		String accessToken = pref.getString(SP_ACCESS_TOKEN_KEY, "");
    		
    		String deviceToken = DriverLocationUpdateService.this.getSharedPreferences(SplashLogin.class.getSimpleName(), 
    				Context.MODE_PRIVATE).getString("registration_id", "");
    		
    		if("".equalsIgnoreCase(deviceToken)){
    			
    			try {
					GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(DriverLocationUpdateService.this);
					String regid = gcm.register(GOOGLE_PROJECT_ID);
					deviceToken = regid;
					final SharedPreferences prefs = getSharedPreferences(SplashLogin.class.getSimpleName(), Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = prefs.edit();
					editor.putString(PROPERTY_REG_ID, regid);
					editor.putInt(PROPERTY_APP_VERSION, getPackageManager().getPackageInfo(getPackageName(), 0).versionCode);
					editor.commit();
				} catch (Exception e) {
					e.printStackTrace();
				}
    			
    		}
    		
    		
    		
    		try{
    			
    			if(noUpdate){
					Log.i("noUpdate","inside");
					if(locationFetcher != null){
						lastLocation = new LatLng(locationFetcher.getLatitude(), locationFetcher.getLongitude());
					}
    				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	    			nameValuePairs.add(new BasicNameValuePair("access_token", accessToken));
	    			nameValuePairs.add(new BasicNameValuePair("latitude", ""+lastLocation.latitude));
	    			nameValuePairs.add(new BasicNameValuePair("longitude", ""+lastLocation.longitude)); //device_token
	    			nameValuePairs.add(new BasicNameValuePair("device_token", deviceToken));
	    			
	    			Log.e("nameValuePairs "+count,"="+nameValuePairs);
	    			
	    			
	    			SimpleJSONParser simpleJSONParser = new SimpleJSONParser();
	    			String result = simpleJSONParser.getJSONFromUrlParams(SERVER_URL+"/update_driver_location", nameValuePairs);
	    			
	    			Log.e("result","="+result);
	    			
	    			simpleJSONParser = null;
	    			nameValuePairs = null;
    				
    				noUpdate = false;
				}
    			
    			

    			
    			if(count > 0){
	    			try{
	    				Thread.sleep(30000);
	    			} catch(Exception e){
	    			}
    			}
    			
				
    			if(!stop){
    			
	    		if(locationFetcher != null){

					Log.i("locationFetcher not null","inside");
	    			LatLng currentLatLng = new LatLng(locationFetcher.getLatitude(), locationFetcher.getLongitude());
	    			Log.e("currentLatLng = ", "="+currentLatLng);
	    			Log.e("lastLocation = ", "="+lastLocation);
	    			
//	    			if(distance(DriverLocationUpdateService.this.lastLocation, currentLatLng) >= 100){
		    			
	    				DriverLocationUpdateService.this.lastLocation = currentLatLng;
	    				
		    			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		    			nameValuePairs.add(new BasicNameValuePair("access_token", accessToken));
		    			nameValuePairs.add(new BasicNameValuePair("latitude", ""+currentLatLng.latitude));
		    			nameValuePairs.add(new BasicNameValuePair("longitude", ""+currentLatLng.longitude));
		    			nameValuePairs.add(new BasicNameValuePair("device_token", deviceToken));
		    			
		    			Log.e("nameValuePairs "+count,"="+nameValuePairs);
		    			
		    			
		    			SimpleJSONParser simpleJSONParser = new SimpleJSONParser();
		    			String result = simpleJSONParser.getJSONFromUrlParams(SERVER_URL+"/update_driver_location", nameValuePairs);
		    			
		    			Log.e("result","="+result);
		    			
		    			simpleJSONParser = null;
		    			nameValuePairs = null;
		    			currentLatLng = null;
		    			
		    			if(result.equalsIgnoreCase(SimpleJSONParser.SERVER_TIMEOUT)){
		    				
		    			}
		    			else{
		    				return result;
		    			}
//	    			}
	    			
	    		}
    		}
    		} catch(Exception e){
    			e.printStackTrace();
    		}
    		return "";
    	}
    	
    	
    	@Override
    	protected void onPostExecute(String result) {
    		super.onPostExecute(result);
    		
    		if(!"".equalsIgnoreCase(result)){
    			try{
    				JSONObject jObj = new JSONObject(result);
    				
    				if(!jObj.isNull("error")){
    					
    					stopSelf();
    					
    				}
    				
    			} catch(Exception e){
    				e.printStackTrace();
    			}
    			
    		}
    		
    		count++;
    		noUpdate = false;
        	
//			locationFetcher.destroy();
//			locationFetcher = null;
    		
        	sendDriverLocationToServer = null;
        	
        	sendDriverLocationToServer = new SendDriverLocationToServer();
        	sendDriverLocationToServer.execute();
    		
    	}
    	
    	
    }
    
    
}

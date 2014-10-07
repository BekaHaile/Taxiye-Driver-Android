package product.clicklabs.jugnoo;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class CRequestRideService extends Service {
	
	static int count = 0; 
	static boolean stop = false;
	
	RequestRideAsync requestRideAsync;
	
	public CRequestRideService() {
		Log.e("CRequestRideService"," instance created");
		count = 0; 
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
    	stop = false;
        try{
        	if(requestRideAsync != null){
        		requestRideAsync.cancel(true);
        		requestRideAsync = null;
        	}
        	
        	requestRideAsync = new RequestRideAsync(0, Data.pickupLatLng);
			requestRideAsync.execute();
        	
        	
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
    	return Service.START_NOT_STICKY;
    }
    
    
    
    
    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (CRequestRideService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    
 
    @Override
    public void onDestroy() {
        if(requestRideAsync != null){
        	
        	stop = true;
        	
        	requestRideAsync.cancel(true);
    		requestRideAsync = null;
    		
    		if(HomeActivity.appInterruptHandler != null){
    			HomeActivity.appInterruptHandler.apiEnd();
			}
    	}
        System.gc();
    }
    
    
    class RequestRideAsync extends AsyncTask<String, Integer, String>{

		int driverPos;
		LatLng pickupLatLng;
		
		public RequestRideAsync(int driverPos, LatLng pickupLatLng){
			this.driverPos = driverPos;
			this.pickupLatLng = pickupLatLng;
		}
		
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Log.e("RequestRideAsync","working");
		}
		
		
		
		@Override
		protected String doInBackground(String... params) {
			try{
				Log.i("===========================driverPos","="+driverPos);
				if(driverPos > 0){
					try{
						Thread.sleep(60000);
					} catch(Exception e){
					}
				}
				
				if(!stop){
				
					if(HomeActivity.appInterruptHandler != null){
						HomeActivity.appInterruptHandler.apiStart(driverPos+1);
					}
					
					
		
				
					String currentDriverId = "";
					String previousDriverId = "";
					if(driverPos == 0){
						previousDriverId = "";
						currentDriverId = ""+Data.driverInfos.get(driverPos).userId;
					}
					else if(driverPos > 0 && driverPos < Data.driverInfos.size()){
						previousDriverId = ""+Data.driverInfos.get(driverPos-1).userId;
						currentDriverId = ""+Data.driverInfos.get(driverPos).userId;
					}
					else if(driverPos == Data.driverInfos.size()){
						previousDriverId = ""+Data.driverInfos.get(driverPos-1).userId;
						currentDriverId = "";
					}
					
					int flag = 0;
					if(driverPos == Data.driverInfos.size()){
						flag = 1;
					}
					
					if(driverPos >= 0 && driverPos < Data.driverInfos.size()){
						Data.assignedDriverInfo = Data.driverInfos.get(driverPos);
					}
					
					
					
					ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("access_token", Data.userData.accessToken));
					nameValuePairs.add(new BasicNameValuePair("session_id", Data.cSessionId));
					nameValuePairs.add(new BasicNameValuePair("user_id", currentDriverId));
					nameValuePairs.add(new BasicNameValuePair("pickup_latitude", ""+pickupLatLng.latitude));
					nameValuePairs.add(new BasicNameValuePair("pickup_longitude", "" + pickupLatLng.longitude));
					nameValuePairs.add(new BasicNameValuePair("pre_user_id", previousDriverId));
					nameValuePairs.add(new BasicNameValuePair("pre_engage_id", Data.cEngagementId));
					nameValuePairs.add(new BasicNameValuePair("flag", ""+flag));
					
					
					
					Log.i("access_token", "=" + Data.userData.accessToken);
					Log.i("user_id", "=" + currentDriverId);
					Log.i("pre_user_id", "=" + previousDriverId);
					Log.i("pre_engage_id", "=" + Data.cEngagementId);
					Log.i("flag", "=" + flag);
					Log.i("pickup_latitude", "=" + Data.latitude);
					Log.i("pickup_longitude", "=" + Data.longitude);
					Log.i("session_id", "="+Data.cSessionId);
					
					
					HttpRequester simpleJSONParser = new HttpRequester();
					String result = simpleJSONParser.getJSONFromUrlParams(Data.SERVER_URL + "/send_req_for_ride", nameValuePairs);
					
					Log.i("result","="+result);
					
					simpleJSONParser = null;
					nameValuePairs = null;
					
					return result;
				}
				else{
					return "";
				}
			} catch(Exception e){
				e.printStackTrace();
				return "";
			}
			
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			
			
			
			if(result.equalsIgnoreCase(HttpRequester.SERVER_TIMEOUT)){
				Log.e("timeout","=");
			}
			else{
				try{
					JSONObject jObj = new JSONObject(result);
					
					if(!jObj.isNull("error")){
						String errorMessage = jObj.getString("error");
						
//						Engagement already made
						if("Engagement already made".equalsIgnoreCase(errorMessage)){
							HomeActivity.appInterruptHandler.apiInterrupted();
							requestRideAsync = null;
							stopSelf();
						}
						else{
							if(HomeActivity.appInterruptHandler != null){
								HomeActivity.appInterruptHandler.apiEnd();
							}
							
							if(driverPos < Data.driverInfos.size()-1){
								requestRideAsync = null;
								requestRideAsync = new RequestRideAsync(driverPos+1, pickupLatLng);
								requestRideAsync.execute();
							}
							else if(driverPos == Data.driverInfos.size()-1){
								requestRideAsync = null;
								requestRideAsync = new RequestRideAsync(driverPos+1, pickupLatLng);
								requestRideAsync.execute();
							}
							else{
								Log.e("requestRideInterrupt(0) ============ ","requestRideInterrupt(0)");
								requestRideAsync = null;
								if(HomeActivity.appInterruptHandler != null){
//									HomeActivity.appInterruptHandler.requestRideInterrupt(0, jObj);
								}
								stopSelf();
							}
						}
					}
					else{
						//{"engagement_id":9,"driver_id":"9"}
						Data.cEngagementId = jObj.getString("engagement_id");
						Data.cDriverId = jObj.getString("driver_id");
						
						try{
							Data.cSessionId = jObj.getString("session_id");
						} catch(Exception e){
						}
						

						if(HomeActivity.appInterruptHandler != null){
							HomeActivity.appInterruptHandler.apiEnd();
						}
						
						if(driverPos < Data.driverInfos.size()-1){
							requestRideAsync = null;
							requestRideAsync = new RequestRideAsync(driverPos+1, pickupLatLng);
							requestRideAsync.execute();
						}
						else if(driverPos == Data.driverInfos.size()-1){
							requestRideAsync = null;
							requestRideAsync = new RequestRideAsync(driverPos+1, pickupLatLng);
							requestRideAsync.execute();
						}
						else{
							Log.e("requestRideInterrupt(0) ============ ","requestRideInterrupt(0)");
							requestRideAsync = null;
							if(HomeActivity.appInterruptHandler != null){
//								HomeActivity.appInterruptHandler.requestRideInterrupt(0, jObj);
							}
							stopSelf();
						}
					
					}
				} catch(Exception e){
					e.printStackTrace();
				}
			}
			
			Log.e("RequestRideAsync","stopped");
		}
		
	}
    
    
    
    Timer timerCheckIfEngagementActive;
	TimerTask timerTaskCheckIfEngagementActive;
	
	public void startCheckIfEngagementActiveTimer(){
		cancelCheckIfEngagementActiveTimer();
		try {
			timerCheckIfEngagementActive = new Timer();
			timerTaskCheckIfEngagementActive = new TimerTask() {
				@Override
				public void run() {
					
					try {
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					
				}
			};
			timerCheckIfEngagementActive.scheduleAtFixedRate(timerTaskCheckIfEngagementActive, 0, 10000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void cancelCheckIfEngagementActiveTimer(){
		try{
			if(timerTaskCheckIfEngagementActive != null){
				timerTaskCheckIfEngagementActive.cancel();
				timerTaskCheckIfEngagementActive = null;
			}
			if(timerCheckIfEngagementActive != null){
				timerCheckIfEngagementActive.cancel();
				timerCheckIfEngagementActive.purge();
				timerCheckIfEngagementActive = null;
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
    
    
}
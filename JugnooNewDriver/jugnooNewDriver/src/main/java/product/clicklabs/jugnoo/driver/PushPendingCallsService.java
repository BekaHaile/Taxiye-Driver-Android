package product.clicklabs.jugnoo.driver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import product.clicklabs.jugnoo.driver.datastructure.PendingAPICall;
import product.clicklabs.jugnoo.driver.datastructure.PendingCall;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.PendingApiHit;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class PushPendingCallsService extends Service {

	private final String TAG = PushPendingCallsService.class.getSimpleName();

	public PushPendingCallsService() {
		Log.e("PushPendinsCallsService"," instance created");
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
        	Log.i("PushPendinsCallsService started", "=======");

			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					pushAPIs(PushPendingCallsService.this);
				}
			}, 10);
        } catch(Exception e){
        	e.printStackTrace();
        }
    }
    
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	super.onStartCommand(intent, flags, startId);
    	return Service.START_STICKY;
    }
    
    
    @Override
    public void onTaskRemoved(Intent rootIntent) {
    	try {
	    	Log.e("onTaskRemoved","="+rootIntent);
	    	Intent restartService = new Intent(getApplicationContext(), this.getClass());
	    	restartService.setPackage(getPackageName());
	    	PendingIntent restartServicePI = PendingIntent.getService(getApplicationContext(), 1, restartService, PendingIntent.FLAG_ONE_SHOT);
	    	AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
	    	alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePI);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
 
    @Override
    public void onDestroy() {
    	Log.e("PushPendinsCallsService onDestroy","=");
    	stopPushApiThread();
    }
    
    
    public Thread pushApiThread;
    public void pushAPIs(final Context context){
    	stopPushApiThread();
    	try{
	    	pushApiThread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					ArrayList<PendingAPICall> pendingAPICalls = Database2.getInstance(context).getAllPendingAPICalls();
					for(PendingAPICall pendingAPICall : pendingAPICalls){
						Log.e(TAG, "pendingApiCall="+pendingAPICall);
						new PendingApiHit().startAPI(context, pendingAPICall);
					}
					
					int pendingApisCount = Database2.getInstance(context).getAllPendingAPICallsCount();
					if(pendingApisCount > 0){
						restartServiceThroughAlarm(context);
						PushPendingCallsService.this.stopSelf();
					}
					else{
						PushPendingCallsService.this.stopSelf();
					}
				}
			});
	    	pushApiThread.start();
    	} catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public void restartServiceThroughAlarm(Context context){
    	Intent restartService = new Intent(context, this.getClass());
		restartService.setPackage(getPackageName());
		PendingIntent restartServicePI = PendingIntent.getService(getApplicationContext(), 1, restartService, PendingIntent.FLAG_ONE_SHOT);
		AlarmManager alarmService = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
		alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + (60000), restartServicePI);
    }
    
    
    public void stopPushApiThread(){
    	try{
    		if(pushApiThread != null){
    			pushApiThread.interrupt();
    		}
    	} catch(Exception e){
    		e.printStackTrace();
    	}
    }

}

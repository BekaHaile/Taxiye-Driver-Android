package product.clicklabs.jugnoo.driver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Utils;

public class DriverLocationUpdateAlarmReceiver extends BroadcastReceiver {

	private static final String SEND_LOCATION = "product.clicklabs.jugnoo.driver.SEND_LOCATION";
	
	private static final long MAX_TIME_BEFORE_LOCATION_UPDATE = 3 * 60000;
	
	@Override
	public void onReceive(final Context context, Intent intent) {
		String userMode = Database2.getInstance(context).getUserMode();
		if(Database2.UM_DRIVER.equalsIgnoreCase(userMode)){
	    	GCMHeartbeatRefresher.refreshGCMHeartbeat(context);
			String action = intent.getAction();
			if (SEND_LOCATION.equals(action)) {
				try {
					long lastTime = Database2.getInstance(context).getDriverLastLocationTime();
					String accessToken = Database2.getInstance(context).getDLDAccessToken();
					if("".equalsIgnoreCase(accessToken)){
						DriverLocationUpdateService.updateServerData(context);
					}
					long currentTime = System.currentTimeMillis();

			    	Log.writeLogToFile("AlarmReceiver", "Receiver "+DateOperations.getCurrentTime()+" = "+(currentTime - lastTime) 
			    			+ " hasNet = "+AppStatus.getInstance(context).isOnline(context));
					
					if(currentTime >= (lastTime + MAX_TIME_BEFORE_LOCATION_UPDATE)){
						new Thread(new Runnable() {
							@Override
							public void run() {
								new DriverLocationDispatcher().sendLocationToServer(context);
							}
						}).start();
					}
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
				finally{
				}
			}
			if (Database2.UM_DRIVER.equalsIgnoreCase(Database2.getInstance(context).getUserMode())) {
				if(!Utils.isServiceRunning(context, DriverLocationUpdateService.class)) {
					new DriverServiceOperations().startDriverService(context);
				}
			}

		}
		else{
			new DriverServiceOperations().stopService(context);
		}
	}
	
}
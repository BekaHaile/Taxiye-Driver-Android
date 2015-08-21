package product.clicklabs.jugnoo.driver;

import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DriverLocationUpdateAlarmReceiver extends BroadcastReceiver {

	private static final String SEND_LOCATION = "product.clicklabs.jugnoo.driver.SEND_LOCATION";
	
	private static final long MAX_TIME_BEFORE_LOCATION_UPDATE = 3 * 60000;
	
	@Override
	public void onReceive(final Context context, Intent intent) {
//		Log.writePathLogToFile("batteryC", "" + Utils.getBatteryPercentage(context));
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
								new DriverLocationDispatcher().sendLocationToServer(context, "AlarmReceiver");
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
		}
		else{
			new DriverServiceOperations().stopService(context);
		}
	}
	
}
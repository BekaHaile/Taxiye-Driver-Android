package product.clicklabs.jugnoo.driver;

import product.clicklabs.jugnoo.driver.utils.Log;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReceiver extends BroadcastReceiver {

    final static String TAG = "BootCompletedReceiver";

    @Override
    public void onReceive(final Context context, Intent arg1) {
        Log.w(TAG, "starting service...");
        
        try{
	        final String serviceRestartOnReboot = Database2.getInstance(context).getDriverServiceRun();
	        Database2.getInstance(context).updateDriverLastLocationTime();
	        
	    	if(Database2.YES.equalsIgnoreCase(serviceRestartOnReboot)){
	    		new DriverServiceOperations().startDriverService(context);
	    	}
	    	else if(Database2.NO.equalsIgnoreCase(serviceRestartOnReboot)){
	    		new DriverServiceOperations().rescheduleDriverService(context);
	    	}
        } catch(Exception e){
        	e.printStackTrace();
        } finally{
        	Database2.getInstance(context).close();
        }
    	
    }
}
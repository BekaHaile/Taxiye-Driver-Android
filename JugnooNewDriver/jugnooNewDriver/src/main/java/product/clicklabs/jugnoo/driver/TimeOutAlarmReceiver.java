package product.clicklabs.jugnoo.driver;

/**
 * Created by aneeshbansal on 02/03/16.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.widget.Toast;

import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Prefs;


public class TimeOutAlarmReceiver extends BroadcastReceiver {

	static Location myLocation;

	@Override
	public void onReceive(Context context, Intent intent) {

		// For our recurring task, we'll just display a message
//		Toast.makeText(context, "i m running", Toast.LENGTH_SHORT).show();

		new DriverTimeoutCheck().timeoutBuffer(context,1);
	}
}

package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import java.util.List;

import product.clicklabs.jugnoo.driver.datastructure.GpsState;
import product.clicklabs.jugnoo.driver.sticky.GeanieView;
import product.clicklabs.jugnoo.driver.utils.Log;

public class BootCompletedReceiver extends BroadcastReceiver {


    final static String TAG = "BootCompletedReceiver";

    @Override
    public void onReceive(final Context context, Intent arg1) {
        Log.w(TAG, "starting service...");
        String driverServiceRun = Database2.getInstance(context).getDriverServiceRun();
        if (arg1.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			if (GpsState.GREATER_SIX.getOrdinal() == Database2.getInstance(context).getGpsState()) {

				if (Database2.YES.equalsIgnoreCase(driverServiceRun)) {
					GpsDistanceCalculator.lastLocationTime = System.currentTimeMillis() - 270000;
					GpsDistanceCalculator.saveLastLocationTimeToSP(context, GpsDistanceCalculator.lastLocationTime);

					Intent i = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					context.startActivity(i);
					Database2.getInstance(context).updateGpsState(GpsState.ZERO_TWO.getOrdinal());
				}
			} else {
				try {
					if (Database2.YES.equalsIgnoreCase(driverServiceRun)) {
						Database2.getInstance(context).updateDriverLastLocationTime();
						context.startService(new Intent(context.getApplicationContext(), DriverLocationUpdateService.class));
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					Database2.getInstance(context).close();
				}
			}

			if(!isForeground(context)) {
				context.startService(new Intent(context, GeanieView.class));
			}
		}

    }

	public static boolean isForeground(Context context) {
		try {
			ActivityManager manager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
			List<ActivityManager.RunningTaskInfo> runningTaskInfo = manager
					.getRunningTasks(1);
			ComponentName componentInfo = runningTaskInfo.get(0).topActivity;
			if (componentInfo.getPackageName().equals(context.getPackageName()))
				return true;
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
	}
}
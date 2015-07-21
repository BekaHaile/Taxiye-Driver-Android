package product.clicklabs.jugnoo.driver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import product.clicklabs.jugnoo.driver.datastructure.GpsState;
import product.clicklabs.jugnoo.driver.utils.Log;

public class BootCompletedReceiver extends BroadcastReceiver {

    final static String TAG = "BootCompletedReceiver";

    @Override
    public void onReceive(final Context context, Intent arg1) {
        Log.w(TAG, "starting service...");

        if (arg1.getAction().equals(Intent.ACTION_BOOT_COMPLETED) &&
                (GpsState.GREATER_SIX.getOrdinal() == Database2.getInstance(context).getGpsState())) {


            GpsDistanceCalculator.lastLocationTime = System.currentTimeMillis()-270000;
            GpsDistanceCalculator.saveLastLocationTimeToSP(context, GpsDistanceCalculator.lastLocationTime);

            Intent i = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);
            Database2.getInstance(context).updateGpsState(GpsState.ZERO_TWO.getOrdinal());
        }
        else if (arg1.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            try {
                final String serviceRestartOnReboot = Database2.getInstance(context).getDriverServiceRun();
                Database2.getInstance(context).updateDriverLastLocationTime();

                if (Database2.YES.equalsIgnoreCase(serviceRestartOnReboot)) {
                    new DriverServiceOperations().startDriverService(context);
                } else if (Database2.NO.equalsIgnoreCase(serviceRestartOnReboot)) {
                    new DriverServiceOperations().rescheduleDriverService(context);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Database2.getInstance(context).close();
            }
        }

    }
}
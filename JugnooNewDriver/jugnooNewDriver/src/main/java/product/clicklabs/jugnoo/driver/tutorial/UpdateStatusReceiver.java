package product.clicklabs.jugnoo.driver.tutorial;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import java.util.List;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.Database2;
import product.clicklabs.jugnoo.driver.DriverLocationUpdateService;
import product.clicklabs.jugnoo.driver.GpsDistanceCalculator;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.datastructure.DriverScreenMode;
import product.clicklabs.jugnoo.driver.datastructure.GpsState;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.sticky.GeanieView;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Prefs;

public class UpdateStatusReceiver extends BroadcastReceiver {


    final static String TAG = "UpdateStatusReceiver";

    @Override
    public void onReceive(final Context context, Intent arg1) {
        Log.w(TAG, "starting service...");
//		Intent intent = new Intent(context, UpdateDriverStatus.class);
//		intent.putExtra("access_token", arg1.getStringExtra(Constants.KEY_ACCESS_TOKEN));
//		String status = "2";
//		if(driverScreenMode == DriverScreenMode.D_RIDE_END) {
//			status = "3";
//		}
//		Prefs.with(activity).save(SPLabels.SET_DRIVER_TOUR_STATUS, status);
//		intent.putExtra("status", status);
//		intent.putExtra("training_id", String.valueOf(tourResponseModel.responses.requestResponse.getEngagementId()));
//		startService(intent);

    }

}
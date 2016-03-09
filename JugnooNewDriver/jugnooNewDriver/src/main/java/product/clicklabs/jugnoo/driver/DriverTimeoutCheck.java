package product.clicklabs.jugnoo.driver;

import android.content.Context;
import android.content.Intent;

import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Prefs;

/**
 * Created by aneeshbansal on 04/03/16.
 */
public class DriverTimeoutCheck {

	public void timeoutBuffer(Context context, boolean highPenalty) {
		int penaltyFactor = 1;
		if(highPenalty){
			penaltyFactor = Prefs.with(context).getInt(SPLabels.DRIVER_TIMEOUT_FACTOR, 1);
			Prefs.with(context).save(SPLabels.BUFFER_TIMEOUT_VALUE, System.currentTimeMillis() - 5000);
		}

		if ((Prefs.with(context).getInt(SPLabels.DRIVER_TIMEOUT_FLAG, 0) == 1)) {
			if (System.currentTimeMillis() >
					Prefs.with(context).getLong(SPLabels.BUFFER_TIMEOUT_VALUE, System.currentTimeMillis() - 5000)) {
				Prefs.with(context).save(SPLabels.INGNORE_RIDEREQUEST_COUNT, Prefs.with(context).getInt(SPLabels.INGNORE_RIDEREQUEST_COUNT, 0) + penaltyFactor);

				Prefs.with(context).save(SPLabels.BUFFER_TIMEOUT_VALUE,
						System.currentTimeMillis() + Prefs.with(context).getLong(SPLabels.BUFFER_TIMEOUT_PERIOD, 0));
			}
			Log.i("TimeOutAlarmReceiver", "1");

			if (Prefs.with(context).getInt(SPLabels.MAX_INGNORE_RIDEREQUEST_COUNT, 0) <= Prefs.with(context).getInt(SPLabels.INGNORE_RIDEREQUEST_COUNT, 0)) {
				Intent timeoutIntent = new Intent(context, DriverTimeoutIntentService.class);
				context.startService(timeoutIntent);
			}
			GCMIntentService.cancelUploadPathAlarm(context);
		}
	}


	public void clearCount(Context context) {
		Prefs.with(context).save(SPLabels.INGNORE_RIDEREQUEST_COUNT, 0);
	}


}

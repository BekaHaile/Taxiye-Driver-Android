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

	public void timeoutBuffer(Context context, int highPenalty) {
		int penaltyFactor = 1;
		try {
			if (highPenalty>0) {
				if(highPenalty ==1) {
					penaltyFactor = Prefs.with(context).getInt(SPLabels.DRIVER_TIMEOUT_FACTOR, 1); //customer_cancel_timeout_factor (ride cancel by customer)
				}else if(highPenalty ==2){
					penaltyFactor = Prefs.with(context).getInt(SPLabels.DRIVER_TIMEOUT_FACTOR_HIGH, 1); //driver_cancel_timeout_factor (ride cancel by driver)
				}
				Prefs.with(context).save(SPLabels.BUFFER_TIMEOUT_VALUE, System.currentTimeMillis() - 5000);
			}

			if ((Prefs.with(context).getInt(SPLabels.DRIVER_TIMEOUT_FLAG, 0) == 1)) {//penalise_driver_timeout (feature control flag)
				if (System.currentTimeMillis() >
						Prefs.with(context).getLong(SPLabels.BUFFER_TIMEOUT_VALUE, System.currentTimeMillis() - 5000)) {
					Prefs.with(context).save(SPLabels.INGNORE_RIDEREQUEST_COUNT, Prefs.with(context).getInt(SPLabels.INGNORE_RIDEREQUEST_COUNT, 0) + penaltyFactor);
					Database2.getInstance(context).insertPenalityData(String.valueOf(System.currentTimeMillis()), penaltyFactor);
					Prefs.with(context).save(SPLabels.BUFFER_TIMEOUT_VALUE,
							System.currentTimeMillis() + Prefs.with(context).getLong(SPLabels.BUFFER_TIMEOUT_PERIOD, 0)); //timeout_counter_buffer (time frame in which multiple request missed/canceled events will be considered one only)
				}
				Log.i("TimeOutAlarmReceiver", "1");

				long timeDiff = System.currentTimeMillis() - Prefs.with(context).getLong(SPLabels.DRIVER_TIMEOUT_TTL, 0); //timeout_ttl (till how much time any penalty will be valid)
				if (Prefs.with(context).getInt(SPLabels.MAX_INGNORE_RIDEREQUEST_COUNT, 0) //max_allowed_timeouts
						<= Database2.getInstance(context).getPenalityData(String.valueOf(timeDiff))) {
					Intent timeoutIntent = new Intent(context, DriverTimeoutIntentService.class);
					context.startService(timeoutIntent);
				}
			}
			GCMIntentService.cancelUploadPathAlarm(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void clearCount(Context context) {
		Prefs.with(context).save(SPLabels.INGNORE_RIDEREQUEST_COUNT, 0);
		Database2.getInstance(context).deletePenalityData();
	}


}


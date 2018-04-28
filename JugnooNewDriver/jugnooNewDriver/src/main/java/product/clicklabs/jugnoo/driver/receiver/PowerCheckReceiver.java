package product.clicklabs.jugnoo.driver.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.utils.Prefs;

/**
 * Created by aneeshbansal on 07/09/16.
 */
public class PowerCheckReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		Prefs.with(context).save(Constants.POWER_OFF_INITIATED, true);
	}
}



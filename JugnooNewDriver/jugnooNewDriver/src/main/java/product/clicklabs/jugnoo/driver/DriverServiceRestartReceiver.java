package product.clicklabs.jugnoo.driver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by aneeshbansal on 05/08/15.
 */
public class DriverServiceRestartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        context.startService(new Intent(context, DriverLocationUpdateService.class));
    }

}

package product.clicklabs.jugnoo.driver;

/**
 * Created by aneeshbansal on 12/10/15.
 */


	import android.app.Activity;
	import android.app.Notification;
	import android.app.NotificationManager;
	import android.app.PendingIntent;
	import android.content.BroadcastReceiver;
	import android.content.ComponentName;
	import android.content.Context;
	import android.content.Intent;
	import android.graphics.BitmapFactory;
	import android.graphics.Color;
	import android.os.PowerManager;
	import android.support.v4.app.NotificationCompat;
	import android.support.v4.content.WakefulBroadcastReceiver;

	import product.clicklabs.jugnoo.driver.utils.Log;

public class PushReceiver extends WakefulBroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {

//		if (intent.getStringExtra("message") != null ) {
//			generateNotification(context, intent.getStringExtra("message"));
//		}

		// Explicitly specify that GcmIntentService will handle the intent.
		ComponentName comp = new ComponentName(context.getPackageName(), GCMIntentService.class.getName());
		// Start the service, keeping the device awake while it is launching.
		startWakefulService(context, (intent.setComponent(comp)));
//		setResultCode(Activity.RESULT_OK);
	}


}
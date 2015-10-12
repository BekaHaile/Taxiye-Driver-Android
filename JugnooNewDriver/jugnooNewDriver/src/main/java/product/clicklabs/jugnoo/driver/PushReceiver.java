package product.clicklabs.jugnoo.driver;

/**
 * Created by aneeshbansal on 12/10/15.
 */


	import android.app.Activity;
	import android.app.Notification;
	import android.app.NotificationManager;
	import android.content.BroadcastReceiver;
	import android.content.ComponentName;
	import android.content.Context;
	import android.content.Intent;
	import android.support.v4.content.WakefulBroadcastReceiver;

public class PushReceiver extends WakefulBroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{

		String notificationTitle = "Pushy";
		String notificationDesc = "Test notification";

		if ( intent.getStringExtra("message") != null )
		{
			notificationDesc = intent.getStringExtra("message");
		}


		Notification notification = new Notification(android.R.drawable.ic_dialog_info, notificationDesc, System.currentTimeMillis());
		notification.defaults = Notification.DEFAULT_ALL;
		notification.flags = Notification.FLAG_AUTO_CANCEL;


		notification.setLatestEventInfo(context, notificationTitle, notificationDesc, null);
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		mNotificationManager.notify(0, notification);


		// Explicitly specify that GcmIntentService will handle the intent.
		ComponentName comp = new ComponentName(context.getPackageName(), GCMIntentService.class.getName());
		// Start the service, keeping the device awake while it is launching.

		startWakefulService(context, (intent.setComponent(comp)));
		setResultCode(Activity.RESULT_OK);
	}
}
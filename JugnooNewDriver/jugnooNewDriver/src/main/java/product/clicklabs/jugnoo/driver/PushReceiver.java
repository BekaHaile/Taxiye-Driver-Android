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

		if (intent.getStringExtra("message") != null ) {
			generateNotification(context, intent.getStringExtra("message"));
		}

		// Explicitly specify that GcmIntentService will handle the intent.
		ComponentName comp = new ComponentName(context.getPackageName(), GCMIntentService.class.getName());
		// Start the service, keeping the device awake while it is launching.
		startWakefulService(context, (intent.setComponent(comp)));
		setResultCode(Activity.RESULT_OK);
	}

	public static void generateNotification(Context context, String message) {

		try {
			long when = System.currentTimeMillis();

			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			Intent notificationIntent = new Intent(context, SplashNewActivity.class);

			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

			NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
			builder.setAutoCancel(true);
			builder.setContentTitle("Jugnoo Pushy");
			builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
			builder.setContentText(message);
			builder.setTicker(message);

			builder.setDefaults(Notification.DEFAULT_ALL);

			builder.setWhen(when);
			builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.jugnoo_icon));
			builder.setSmallIcon(R.drawable.notif_icon);
			builder.setContentIntent(intent);

			Notification notification = builder.build();
			notificationManager.notify(1111, notification);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
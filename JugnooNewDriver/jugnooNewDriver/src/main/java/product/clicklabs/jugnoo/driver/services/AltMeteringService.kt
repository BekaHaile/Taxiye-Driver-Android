package product.clicklabs.jugnoo.driver.services

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.GCMIntentService
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.ui.DriverSplashActivity
import product.clicklabs.jugnoo.driver.utils.Log

class AltMeteringService : Service(){

    private val TAG = AltMeteringService::class.java.simpleName
    private val METER_NOTIF_ID = 10102;

    override fun onBind(intent: Intent?): IBinder {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(METER_NOTIF_ID, generateNotification(this, TAG, METER_NOTIF_ID))
        return Service.START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.e(TAG, "onTaskRemoved, intent="+rootIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "onDestroy")
    }

    fun generateNotification(context: Context, message: String, notificationId: Int): Notification? {
        try {
            val `when` = System.currentTimeMillis()
            val notificationManager = GCMIntentService.getNotificationManager(context, Constants.NOTIF_CHANNEL_DEFAULT)

            val notificationIntent = Intent(context, DriverSplashActivity::class.java)

            notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            val intent = PendingIntent.getActivity(context, 0, notificationIntent, 0)


            val builder = NotificationCompat.Builder(context, Constants.NOTIF_CHANNEL_DEFAULT)
            builder.setAutoCancel(false)
            builder.setContentTitle(context.resources.getString(R.string.app_name))
            builder.setStyle(NotificationCompat.BigTextStyle().bigText(message))
            builder.setContentText(message)
            builder.setTicker(message)
            builder.setChannelId(Constants.NOTIF_CHANNEL_DEFAULT)

            builder.setWhen(`when`)
            builder.setLargeIcon(BitmapFactory.decodeResource(context.resources, GCMIntentService.NOTIFICATION_BIG_ICON))
            builder.setSmallIcon(GCMIntentService.NOTIFICATON_SMALL_ICON)
            builder.setContentIntent(intent)


            val notification = builder.build()
            notificationManager.notify(notificationId, notification)
            return notification

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }

}

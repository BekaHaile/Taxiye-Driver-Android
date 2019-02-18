package product.clicklabs.jugnoo.driver.altmetering

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.text.TextUtils
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import product.clicklabs.jugnoo.driver.*
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.altmetering.db.MeteringDatabase
import product.clicklabs.jugnoo.driver.altmetering.model.Segment
import product.clicklabs.jugnoo.driver.ui.DriverSplashActivity
import product.clicklabs.jugnoo.driver.utils.GoogleRestApis
import product.clicklabs.jugnoo.driver.utils.Log
import product.clicklabs.jugnoo.driver.utils.MapUtils
import retrofit.mime.TypedByteArray
import java.util.*

class AltMeteringService : Service() {

    private val TAG = AltMeteringService::class.java.simpleName
    private val METER_NOTIF_ID = 10102;

    private val LOCATION_UPDATE_INTERVAL: Long = 2000 // in milliseconds
    private val LOCATION_SMALLEST_DISPLACEMENT: Float = 30f // in meters

    var meteringDB: MeteringDatabase? = null
        get() {
            if (field == null) {
                field = MeteringDatabase.getInstance(MyApplication.getInstance())
            }
            return field
        }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onBind(intent: Intent?): IBinder {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(METER_NOTIF_ID, generateNotification(this, TAG, METER_NOTIF_ID))
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (!GpsDistanceCalculator.isMeteringStateActive(this)) {
            val sourceLat = intent!!.getDoubleExtra(Constants.KEY_PICKUP_LATITUDE, 0.0)
            val sourceLng = intent.getDoubleExtra(Constants.KEY_PICKUP_LONGITUDE, 0.0)
            val destinationLat = intent.getDoubleExtra(Constants.KEY_OP_DROP_LATITUDE, 0.0)
            val destinationLng = intent.getDoubleExtra(Constants.KEY_OP_DROP_LONGITUDE, 0.0)

            FetchPathAsync(meteringDB, LatLng(sourceLat, sourceLng), LatLng(destinationLat, destinationLng),
                    arrayListOf<LatLng>(), ::requestLocationUpdates).execute()
        }
        else{
            requestLocationUpdates()
        }

        return Service.START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.e(TAG, "onTaskRemoved, intent=" + rootIntent)
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

    @SuppressLint("MissingPermission")
    fun requestLocationUpdates(){

        val locationRequest = LocationRequest().apply {
            interval = LOCATION_UPDATE_INTERVAL
            fastestInterval = LOCATION_UPDATE_INTERVAL
            maxWaitTime = LOCATION_UPDATE_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, object: LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                if(locationResult != null) {
                    val location = locationResult.locations[locationResult.locations.size - 1]
                    Log.e("new onLocationResult", "location = " + location)
                }
            }
        }, null)
    }



    internal class FetchPathAsync(val meteringDB: MeteringDatabase?, val source: LatLng, val destination: LatLng,
                                  waypoints: ArrayList<LatLng>,
                                  val onPost: ()->Unit) : AsyncTask<Unit, Unit, String?>() {
        private var strWaypoints: String

        init {
            val sb = StringBuilder()
            for (i in waypoints.indices) {
                sb.append("via:")
                        .append(waypoints[i].latitude)
                        .append("%2C")
                        .append(waypoints[i].longitude)
                        .append("%7C")
            }
            strWaypoints = sb.toString()
        }

        override fun doInBackground(vararg params: Unit?): String? {
            try {
                val strOrigin = source.latitude.toString() + "," + source.longitude.toString()
                val strDestination = destination.latitude.toString() + "," + destination.longitude.toString()
                val response = if (!TextUtils.isEmpty(strWaypoints)) {
                    GoogleRestApis.getDirectionsWaypoints(strOrigin, strDestination, strWaypoints)
                } else {
                    GoogleRestApis.getDirections(strOrigin, strDestination, false, "driving", false)
                }
                val result = String((response.body as TypedByteArray).bytes)

                val list = MapUtils.getLatLngListFromPath(result)
                if (list.size > 0) {
                    val segments: MutableList<Segment> = arrayListOf()
                    for (i in list.indices) {
                        if (i < list.size - 1) {
                            val segment = Segment(list[i].latitude, list[i].longitude, list[i + 1].latitude, list[i + 1].longitude, i + 1)
                            segments.add(segment)
                        }
                    }
                    meteringDB!!.getMeteringDao().insertAllSegments(segments)
                    val segments2: MutableList<Segment> = meteringDB.getMeteringDao().getAllSegments(0) as MutableList<Segment>
                    Log.e("FetchPathAsync", "segments2 = " + segments2)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return ""
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            onPost()
        }

    }
}

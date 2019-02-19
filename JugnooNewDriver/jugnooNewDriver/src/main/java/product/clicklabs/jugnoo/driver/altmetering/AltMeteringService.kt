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
import product.clicklabs.jugnoo.driver.altmetering.model.LastLocationTimestamp
import product.clicklabs.jugnoo.driver.altmetering.model.ScanningPointer
import product.clicklabs.jugnoo.driver.altmetering.model.Segment
import product.clicklabs.jugnoo.driver.altmetering.model.Waypoint
import product.clicklabs.jugnoo.driver.altmetering.utils.PolyUtil
import product.clicklabs.jugnoo.driver.ui.DriverSplashActivity
import product.clicklabs.jugnoo.driver.utils.GoogleRestApis
import product.clicklabs.jugnoo.driver.utils.Log
import product.clicklabs.jugnoo.driver.utils.MapUtils
import retrofit.mime.TypedByteArray

class AltMeteringService : Service() {

    private val TAG = AltMeteringService::class.java.simpleName
    private val METER_NOTIF_ID = 10102;

    private val LOCATION_UPDATE_INTERVAL: Long = 10000 // in milliseconds
    private val LOCATION_SMALLEST_DISPLACEMENT: Float = 30f // in meters
    private val PATH_POINT_DISTANCE_TOLLERANCE: Double = 100.0 // in meters
    val LAST_LOCATION_TIME_DIFF: Long = 30000 // in meters

    var meteringDB: MeteringDatabase? = null
        get() {
            if (field == null) {
                field = MeteringDatabase.getInstance(MyApplication.getInstance())
            }
            return field
        }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var globalPath: MutableList<LatLng>
    private var globalPathDistance: Double = 0.0
    private lateinit var source: LatLng
    private lateinit var destination: LatLng

    override fun onBind(intent: Intent?): IBinder {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(METER_NOTIF_ID, generateNotification(this, TAG, METER_NOTIF_ID))
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val sourceLat = intent!!.getDoubleExtra(Constants.KEY_PICKUP_LATITUDE, 0.0)
        val sourceLng = intent.getDoubleExtra(Constants.KEY_PICKUP_LONGITUDE, 0.0)
        val destinationLat = intent.getDoubleExtra(Constants.KEY_OP_DROP_LATITUDE, 0.0)
        val destinationLng = intent.getDoubleExtra(Constants.KEY_OP_DROP_LONGITUDE, 0.0)

        source = LatLng(sourceLat, sourceLng)
        destination = LatLng(destinationLat, destinationLng)

        if (!GpsDistanceCalculator.isMeteringStateActive(this)) {
            GpsDistanceCalculator.saveTrackingToSP(this, 1)
            FirstTimeDataClearAsync(meteringDB, ::fetchPathAsyncCall).execute()

        } else {
            ReadPathAsync(meteringDB, ::requestLocationUpdates).execute()
        }

        return Service.START_STICKY
    }

    fun fetchPathAsyncCall(){
        FetchPathAsync(meteringDB, source, destination,
                mutableListOf(), ::requestLocationUpdates).execute()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.e(TAG, "onTaskRemoved, intent=" + rootIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
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
    fun requestLocationUpdates(list: MutableList<LatLng>) {
        globalPath = list
        globalPathDistance = 0.0
        for (i in list.indices) {
            if (i < list.size - 1) {
                globalPathDistance += MapUtils.distance(list[i], list[i + 1])
            }
        }
        if (HomeActivity.appInterruptHandler != null) {
            HomeActivity.appInterruptHandler.pathAlt(list)
        }
        val locationRequest = LocationRequest().apply {
            interval = LOCATION_UPDATE_INTERVAL
            fastestInterval = LOCATION_UPDATE_INTERVAL
            maxWaitTime = LOCATION_UPDATE_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            if (locationResult != null) {
                val location = locationResult.locations[locationResult.locations.size - 1]
                val latLng = LatLng(location.latitude, location.longitude)
                Log.e("new onLocationResult", "location = " + location)
                var time = System.currentTimeMillis()
                val position = PolyUtil.locationIndexOnPath(latLng, globalPath,
                        true, PATH_POINT_DISTANCE_TOLLERANCE)
                Log.e("new onLocationResult", "position on path = " + position)
                Log.e("new onLocationResult", "timeDiff = " + (System.currentTimeMillis() - time))
                time = System.currentTimeMillis()
                if(position > -1){
                    UpdateTimeStampPointerAsync(meteringDB, position, time).execute()
                } else {
                    GetLastLocationTimeAndWaypointsAsync(meteringDB, time, latLng).execute()
                }


                if (HomeActivity.appInterruptHandler != null) {
                    val start = if (position > -1) globalPath[position] else null
                    val end = if (position > -1) globalPath[position + 1] else null
                    HomeActivity.appInterruptHandler.polylineAlt(start, end)

                    HomeActivity.appInterruptHandler.updateMeteringUI(globalPathDistance, 0, 0,
                            location,
                            location, globalPathDistance)
                }
            }
        }
    }


    class FetchPathAsync(val meteringDB: MeteringDatabase?, val source: LatLng, val destination: LatLng,
                         waypoints: MutableList<LatLng>,
                         val onPost: (MutableList<LatLng>) -> Unit) : AsyncTask<Unit, Unit, MutableList<LatLng>>() {
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

        override fun doInBackground(vararg params: Unit?): MutableList<LatLng> {
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
                    meteringDB!!.getMeteringDao().deleteAllSegments()
                    meteringDB.getMeteringDao().insertAllSegments(segments)
                    val segments2: MutableList<Segment> = meteringDB.getMeteringDao().getAllSegments(0) as MutableList<Segment>
                    Log.e("FetchPathAsync", "segments2 = " + segments2)
                }
                return list
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return mutableListOf()
        }

        override fun onPostExecute(result: MutableList<LatLng>) {
            super.onPostExecute(result)
            onPost(result)
        }

    }

    class ReadPathAsync(val meteringDB: MeteringDatabase?, val onPost: (MutableList<LatLng>) -> Unit)
        : AsyncTask<Unit, Unit, MutableList<LatLng>>() {
        override fun doInBackground(vararg params: Unit?): MutableList<LatLng> {

            val segments2: MutableList<Segment> = meteringDB!!.getMeteringDao().getAllSegments(0) as MutableList<Segment>
            Log.e("ReadPathAsync", "segments2 = " + segments2)
            val list = mutableListOf<LatLng>()
            for (segment in segments2) {
                list.add(LatLng(segment.slat, segment.sLng))
            }
            return list
        }

        override fun onPostExecute(result: MutableList<LatLng>) {
            super.onPostExecute(result)
            onPost(result)
        }
    }


    class UpdateTimeStampPointerAsync(val meteringDB: MeteringDatabase?, val position:Int, val timeStamp:Long): AsyncTask<Unit, Unit, Unit>(){
        override fun doInBackground(vararg params: Unit?) {
            meteringDB!!.getMeteringDao().deleteScanningPointer()
            meteringDB.getMeteringDao().insertScanningPointer(ScanningPointer(position))

            meteringDB.getMeteringDao().deleteLastLocationTimestamp()
            meteringDB.getMeteringDao().insertLastLocationTimestamp(LastLocationTimestamp(timeStamp))
        }
    }


    inner class GetLastLocationTimeAndWaypointsAsync(val meteringDB: MeteringDatabase?, val time:Long, val waypoint:LatLng)
        : AsyncTask<Unit, Unit, MutableList<LatLng>>(){
        override fun doInBackground(vararg params: Unit?) : MutableList<LatLng> {
            val timestamps:MutableList<LastLocationTimestamp> = meteringDB!!.getMeteringDao().getLastLocationTimeStamp() as MutableList<LastLocationTimestamp>
            if(timestamps.size > 0){
                val diff = time - timestamps[0].timestamp
                if (diff > LAST_LOCATION_TIME_DIFF){
                    fusedLocationClient.removeLocationUpdates(locationCallback)
                    meteringDB.getMeteringDao().deleteScanningPointer()
                    meteringDB.getMeteringDao().insertWaypoint(Waypoint(waypoint.latitude, waypoint.longitude))
                    val waypoints = meteringDB.getMeteringDao().getAllWaypoints()
                    val latlngs:MutableList<LatLng> = mutableListOf()
                    for(wp in waypoints){
                        latlngs.add(LatLng(wp.lat, wp.lng))
                    }


//                    val disposable = CompositeDisposable().add(meteringDB.getMeteringDao().getAllWaypoints().subscribeOn(Schedulers.io())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe({
//
//                            }, {
//
//                            }))


                    return latlngs
                }
            }
            return mutableListOf()
        }

        override fun onPostExecute(result: MutableList<LatLng>) {
            super.onPostExecute(result)
            if(result.size > 0){
                FetchPathAsync(meteringDB, source, destination, result, ::requestLocationUpdates).execute()
            }
        }

    }

    class FirstTimeDataClearAsync(val meteringDB:MeteringDatabase?, val post:()->Unit) : AsyncTask<Unit, Unit, Unit>(){
        override fun doInBackground(vararg params: Unit?) {
            meteringDB!!.getMeteringDao().deleteAllSegments()
            meteringDB.getMeteringDao().deleteAllWaypoints()
            meteringDB.getMeteringDao().deleteScanningPointer()
            meteringDB.getMeteringDao().deleteLastLocationTimestamp()
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            post()
        }

    }
}

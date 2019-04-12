package product.clicklabs.jugnoo.driver.altmetering

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.location.Location
import android.os.AsyncTask
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.support.v4.content.LocalBroadcastManager
import android.text.TextUtils
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import product.clicklabs.jugnoo.driver.*
import product.clicklabs.jugnoo.driver.GpsDistanceCalculator.MAX_ACCURACY
import product.clicklabs.jugnoo.driver.GpsDistanceCalculator.MAX_SPEED_THRESHOLD
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.altmetering.db.MeteringDatabase
import product.clicklabs.jugnoo.driver.altmetering.model.LastLocationTimestamp
import product.clicklabs.jugnoo.driver.altmetering.model.ScanningPointer
import product.clicklabs.jugnoo.driver.altmetering.model.Segment
import product.clicklabs.jugnoo.driver.altmetering.model.Waypoint
import product.clicklabs.jugnoo.driver.altmetering.utils.PolyUtil
import product.clicklabs.jugnoo.driver.datastructure.UserData
import product.clicklabs.jugnoo.driver.ui.DriverSplashActivity
import product.clicklabs.jugnoo.driver.utils.*
import product.clicklabs.jugnoo.driver.utils.Utils.getDecimalFormat
import retrofit.mime.TypedByteArray

class AltMeteringService : Service() {

    companion object {
        const val INTENT_ACTION_END_RIDE_TRIGGER = "SERVICE_INTENT_ACTION_END_RIDE_TRIGGER"
        private const val METERING_STATE_ACTIVE = "alt_metering_state_active"
        private const val METERING_ = "alt_metering_"

        fun updateMeteringActive(context: Context, meteringState: Boolean){
            Prefs.with(context).save(METERING_STATE_ACTIVE, meteringState)
            if(!meteringState){
                Prefs.with(context).save(METERING_+ Constants.KEY_ENGAGEMENT_ID, 0)
                Prefs.with(context).save(METERING_+ Constants.KEY_PICKUP_LATITUDE, 0.toString())
                Prefs.with(context).save(METERING_+ Constants.KEY_PICKUP_LONGITUDE, 0.toString())
                Prefs.with(context).save(METERING_+ Constants.KEY_OP_DROP_LATITUDE, 0.toString())
                Prefs.with(context).save(METERING_+ Constants.KEY_OP_DROP_LONGITUDE, 0.toString())
            }
        }
    }


    private fun isMeteringActive(context:Context): Boolean{
        return Prefs.with(context).getBoolean(METERING_STATE_ACTIVE, false)
    }

    private val TAG = AltMeteringService::class.java.simpleName
    private val METER_NOTIF_ID = 10102;

    private val LOCATION_UPDATE_INTERVAL: Long = 10000 // in milliseconds
    private val LOCATION_SMALLEST_DISPLACEMENT: Float = 30f // in meters
    private val PATH_POINT_DISTANCE_TOLLERANCE: Double = 100.0 // in meters
    val LAST_LOCATION_TIME_DIFF: Long = 60000 // in meters

    var meteringDB: MeteringDatabase? = null
        get() {
            if (field == null) {
                field = MeteringDatabase.getInstance(MyApplication.getInstance())
            }
            return field
        }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationRequest = LocationRequest().apply {
        interval = LOCATION_UPDATE_INTERVAL
        fastestInterval = LOCATION_UPDATE_INTERVAL
        maxWaitTime = LOCATION_UPDATE_INTERVAL
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private lateinit var globalPath: MutableList<LatLng>
    private var globalPathDistance: Double = 0.0
    private var globalWaypointLatLngs:MutableList<LatLng>? = null
    private var engagementId:Int = 0
    private lateinit var source: LatLng
    private lateinit var destination: LatLng
    private lateinit var currentLocation: Location
    private var currentLocationTime: Long = 0

    override fun onBind(intent: Intent?): IBinder {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand, intent=$intent")

        if (intent != null && intent.hasExtra(Constants.KEY_ENGAGEMENT_ID)) {
            val sourceLat = intent.getDoubleExtra(Constants.KEY_PICKUP_LATITUDE, 0.0)
            val sourceLng = intent.getDoubleExtra(Constants.KEY_PICKUP_LONGITUDE, 0.0)
            val destinationLat = intent.getDoubleExtra(Constants.KEY_OP_DROP_LATITUDE, 0.0)
            val destinationLng = intent.getDoubleExtra(Constants.KEY_OP_DROP_LONGITUDE, 0.0)

            engagementId = intent.getIntExtra(Constants.KEY_ENGAGEMENT_ID, 0)
            source = LatLng(sourceLat, sourceLng)
            destination = LatLng(destinationLat, destinationLng)

            Prefs.with(this).save(METERING_+ Constants.KEY_ENGAGEMENT_ID, engagementId)
            Prefs.with(this).save(METERING_+ Constants.KEY_PICKUP_LATITUDE, sourceLat.toString())
            Prefs.with(this).save(METERING_+ Constants.KEY_PICKUP_LONGITUDE, sourceLng.toString())
            Prefs.with(this).save(METERING_+ Constants.KEY_OP_DROP_LATITUDE, destinationLat.toString())
            Prefs.with(this).save(METERING_+ Constants.KEY_OP_DROP_LONGITUDE, destinationLng.toString())

        } else if(isMeteringActive(this)) {

            val sourceLat = Prefs.with(this).getString(METERING_+ Constants.KEY_PICKUP_LATITUDE, 0.toString()).toDouble()
            val sourceLng = Prefs.with(this).getString(METERING_+ Constants.KEY_PICKUP_LONGITUDE, 0.toString()).toDouble()
            val destinationLat = Prefs.with(this).getString(METERING_+ Constants.KEY_OP_DROP_LATITUDE, 0.toString()).toDouble()
            val destinationLng = Prefs.with(this).getString(METERING_+ Constants.KEY_OP_DROP_LONGITUDE, 0.toString()).toDouble()

            engagementId = Prefs.with(this).getInt(METERING_+ Constants.KEY_ENGAGEMENT_ID, 0)
            source = LatLng(sourceLat, sourceLng)
            destination = LatLng(destinationLat, destinationLng)

        } else {
            stopSelf()
            return Service.START_NOT_STICKY
        }


        startForeground(METER_NOTIF_ID, generateNotification(this, getNotificationMessage(), METER_NOTIF_ID))
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        LocalBroadcastManager.getInstance(this).registerReceiver(activityBroadcastReceiver, IntentFilter(INTENT_ACTION_END_RIDE_TRIGGER))


        if (!isMeteringActive(this)) {
            updateMeteringActive(this, true)
            FirstTimeDataClearAsync(meteringDB, engagementId, ::fetchPathAsyncCall).execute()

        } else {
            ReadPathAsync(meteringDB, engagementId, ::requestLocationUpdates).execute()
        }

        return Service.START_STICKY
    }

    private fun fetchPathAsyncCall(){
        FetchPathAsync(meteringDB, engagementId, source, destination,
                mutableListOf(), ::requestLocationUpdates).execute()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.e(TAG, "onTaskRemoved, intent=$rootIntent")
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(activityBroadcastReceiver)
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
        updatePathAndDistance(list)
        fusedLocationClient.removeLocationUpdates(locationCallback)
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun updatePathAndDistance(list: MutableList<LatLng>) {
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
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            if (locationResult != null) {
                val location = locationResult.locations[locationResult.locations.size - 1]
                val latLng = LatLng(location.latitude, location.longitude)
                val time = System.currentTimeMillis()
                if (location.accuracy > MAX_ACCURACY) { //accuracy check
                    Log.e("new onLocationResult", "accuracy wrong")
                    return
                }
                if(::currentLocation.isInitialized){ //speed limit check
                    val displacement = MapUtils.distance(latLng, LatLng(currentLocation.latitude, currentLocation.longitude))
                    val millisDiff = time - currentLocationTime
                    val secondsDiff = millisDiff / 1000L
                    val speedMPS = if (secondsDiff > 0) displacement / secondsDiff else 0.0
                    Log.e("new onLocationResult", "speedMPS =$speedMPS")
                    if (speedMPS > Prefs.with(this@AltMeteringService).getFloat(Constants.KEY_MAX_SPEED_THRESHOLD, MAX_SPEED_THRESHOLD.toFloat()).toDouble()) {
                        Log.e("new onLocationResult", "speedMPS error")
                       return
                    }
                }
                currentLocation = location
                currentLocationTime = time
                Log.e("new onLocationResult", "location = $location")
                GetScanningPointerAndShortenPathToScan(latLng, time).execute()
            }
        }
    }

    fun getNotificationMessage():String{
        return getString(R.string.estimated_dis) + ": " +
                getDecimalFormat().format(Math.abs(globalPathDistance) * UserData.getDistanceUnitFactor(this)) +" "+
                Utils.getDistanceUnit(UserData.getDistanceUnit(this))
    }


    class FetchPathAsync(private val meteringDB: MeteringDatabase?, val engagementId:Int, val source: LatLng, val destination: LatLng,
                         waypoints: MutableList<LatLng>?,
                         val onPost: (MutableList<LatLng>) -> Unit) : AsyncTask<Unit, Unit, MutableList<LatLng>>() {
        private var strWaypoints: String

        init {
            val sb = StringBuilder()
            if(waypoints != null) {
                for (i in waypoints.indices) {
                    sb.append("via:")
                            .append(waypoints[i].latitude)
                            .append("%2C")
                            .append(waypoints[i].longitude)
                            .append("%7C")
                }
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
                            val segment = Segment(engagementId, list[i].latitude, list[i].longitude, list[i + 1].latitude, list[i + 1].longitude, i + 1)
                            segments.add(segment)
                        }
                    }
                    meteringDB!!.getMeteringDao().deleteAllSegments(engagementId)
                    meteringDB.getMeteringDao().insertAllSegments(segments)
                    val segments2: MutableList<Segment> = meteringDB.getMeteringDao().getAllSegments(engagementId, 0) as MutableList<Segment>
                    Log.e("FetchPathAsync", "segments2 = $segments2")
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

    class ReadPathAsync(private val meteringDB: MeteringDatabase?, val engagementId: Int, val onPost: (MutableList<LatLng>) -> Unit)
        : AsyncTask<Unit, Unit, MutableList<LatLng>>() {
        override fun doInBackground(vararg params: Unit?): MutableList<LatLng> {

            val segments2: MutableList<Segment> = meteringDB!!.getMeteringDao().getAllSegments(engagementId, 0) as MutableList<Segment>
            Log.e("ReadPathAsync", "segments2 = $segments2")
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


    class UpdateTimeStampPointerAsync(private val meteringDB: MeteringDatabase?, val engagementId: Int, val position:Int, val timeStamp:Long): AsyncTask<Unit, Unit, Unit>(){
        override fun doInBackground(vararg params: Unit?) {
            meteringDB!!.getMeteringDao().deleteScanningPointer(engagementId)
            meteringDB.getMeteringDao().insertScanningPointer(ScanningPointer(engagementId, position))

            meteringDB.getMeteringDao().deleteLastLocationTimestamp(engagementId)
            meteringDB.getMeteringDao().insertLastLocationTimestamp(LastLocationTimestamp(engagementId, timeStamp))
        }
    }


    inner class GetLastLocationTimeAndWaypointsAsync(private val meteringDB: MeteringDatabase?, val time:Long, private val waypoint:LatLng)
        : AsyncTask<Unit, Unit, MutableList<LatLng>?>(){
        override fun doInBackground(vararg params: Unit?) : MutableList<LatLng> {
            val timestamps:MutableList<LastLocationTimestamp> = meteringDB!!.getMeteringDao().getLastLocationTimeStamp(engagementId) as MutableList<LastLocationTimestamp>
            if(timestamps.size > 0){
                Log.e("GetLastLocationTimeAndWaypointsAsync", "timestamps[0].timestamp = " + timestamps[0].timestamp)
                val diff = time - timestamps[0].timestamp
                Log.e("GetLastLocationTimeAndWaypointsAsync", "diff = $diff")
                if (diff > LAST_LOCATION_TIME_DIFF){
                    fusedLocationClient.removeLocationUpdates(locationCallback)
                    meteringDB.getMeteringDao().deleteScanningPointer(engagementId)
                    meteringDB.getMeteringDao().insertWaypoint(Waypoint(engagementId, waypoint.latitude, waypoint.longitude))
                    val waypoints = meteringDB.getMeteringDao().getAllWaypoints(engagementId)
                    globalWaypointLatLngs = mutableListOf()
                    for(wp in waypoints){
                        globalWaypointLatLngs!!.add(LatLng(wp.lat, wp.lng))
                    }
                    return globalWaypointLatLngs!!
                }
            }
            return mutableListOf()
        }

        override fun onPostExecute(result: MutableList<LatLng>?) {
            super.onPostExecute(result)
            if(result != null && result.size > 0){
                FetchPathAsync(meteringDB, engagementId, source, destination, result, ::requestLocationUpdates).execute()
            }
        }

    }

    class FirstTimeDataClearAsync(private val meteringDB:MeteringDatabase?, val engagementId: Int, val post:()->Unit) : AsyncTask<Unit, Unit, Unit>(){
        override fun doInBackground(vararg params: Unit?) {
            meteringDB!!.getMeteringDao().deleteAllSegments(engagementId)
            meteringDB.getMeteringDao().deleteAllWaypoints(engagementId)
            meteringDB.getMeteringDao().deleteScanningPointer(engagementId)
            meteringDB.getMeteringDao().deleteLastLocationTimestamp(engagementId)
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            post()
        }

    }

    private var intentEngagementId:Int = 0

    private val activityBroadcastReceiver:BroadcastReceiver = object:BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            intentEngagementId = intent!!.getIntExtra(Constants.KEY_ENGAGEMENT_ID, engagementId)
            if(::currentLocation.isInitialized){
                fusedLocationClient.removeLocationUpdates(locationCallback)
                val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
                val distance = MapUtils.distance(destination, latLng)
                if(distance <= PATH_POINT_DISTANCE_TOLLERANCE){
                    //no need to do anything
                    updateDistanceAndTriggerEndRide()
                } else {
                    //update path by changing destination
                    destination = latLng
                    FetchPathAsync(meteringDB, engagementId, source, destination, globalWaypointLatLngs, ::updateDistanceAndCallbackEndRide).execute()
                }

            } else {
                Utils.showToast(this@AltMeteringService, getString(R.string.waiting_for_location))
                LocalBroadcastManager.getInstance(this@AltMeteringService).sendBroadcast(Intent(HomeActivity.INTENT_ACTION_ACTIVITY_END_RIDE_CALLBACK))
            }
        }
    }

    private fun updateDistanceAndTriggerEndRide() {
        InsertRideDataAndEndRide().execute()
    }

    private fun updateDistanceAndCallbackEndRide(list: MutableList<LatLng>) {
        updatePathAndDistance(list)
        updateDistanceAndTriggerEndRide()
    }

    inner class InsertRideDataAndEndRide() : AsyncTask<Unit, Unit, Unit>(){
        var csvPathStr:String = ""
        var csvWaypointsStr:String = ""
        var numWaypoints:Int = 0
        override fun doInBackground(vararg params: Unit?) {

            val templatePath = "lat,lng,accDistance"
            val newLine = "\n"
            val csvPath = StringBuilder()
            if(globalPath.size > 0){ csvPath.append(templatePath).append(newLine) }
            var accDistance = 0.0
            for (i in globalPath.indices) {
                if (i < globalPath.size - 1) {
                    accDistance += MapUtils.distance(globalPath[i], globalPath[i + 1])
                    csvPath.append(globalPath[i].latitude.toString()+","+globalPath[i].longitude.toString()+","+accDistance)
                    csvPath.append(newLine)
//                    Database2.getInstance(this@AltMeteringService).insertRideDataWOChecks(this@AltMeteringService,
//                            globalPath[i].latitude.toString(), globalPath[i].longitude.toString(), System.currentTimeMillis().toString(),
//                            engagementId, accDistance, 0)
                }
            }
            csvPathStr = csvPath.toString()

            val templateWP = "lat,lng"
            val csvWP = StringBuilder()
            if(globalWaypointLatLngs != null && globalWaypointLatLngs!!.size > 0) {
                csvWP.append(templateWP).append(newLine)
                for (i in globalWaypointLatLngs!!.indices){
                    csvWP.append(globalWaypointLatLngs!![i].latitude.toString()+","+globalWaypointLatLngs!![i].longitude.toString())
                    csvWP.append(newLine)
                }
                numWaypoints = globalWaypointLatLngs!!.size
            }
            csvWaypointsStr = csvWP.toString()

        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)

            if (HomeActivity.appInterruptHandler != null) {
//                HomeActivity.appInterruptHandler.updateMeteringUI(globalPathDistance, 0, 0,
//                        currentLocation,
//                        currentLocation, globalPathDistance)
            }
            val intent = Intent(HomeActivity.INTENT_ACTION_ACTIVITY_END_RIDE_CALLBACK)
            intent.apply {
                putExtra(Constants.KEY_SUCCESS, true)
                putExtra(Constants.KEY_ENGAGEMENT_ID, intentEngagementId)
                putExtra(Constants.KEY_CSV_PATH, csvPathStr)
                putExtra(Constants.KEY_CSV_WAYPOINTS, csvWaypointsStr)
                putExtra(Constants.KEY_NUM_WAYPOINTS, numWaypoints.toString())
                putExtra(Constants.KEY_WAYPOINT_DISTANCE, globalPathDistance)
            }
            LocalBroadcastManager.getInstance(this@AltMeteringService).sendBroadcast(intent)
            stopForeground(true)
            stopSelf()
        }

    }

    inner class GetScanningPointerAndShortenPathToScan(val currentLatLng: LatLng, val time:Long): AsyncTask<Unit, Unit, Int>(){
        override fun doInBackground(vararg params: Unit?): Int {
            val list :List<ScanningPointer> = meteringDB!!.getMeteringDao().getScanningPointer(engagementId)
            var lastScanningPoint = 0
            if(list.isNotEmpty()){
                lastScanningPoint = list[0].position
            }

            val pathLength = globalPath.size
            val position = PolyUtil.locationIndexOnPath(currentLatLng, globalPath.subList(lastScanningPoint, pathLength),
                    true, PATH_POINT_DISTANCE_TOLLERANCE)
            Log.i("GetScanningPointerAndShortenPathToScan", "position=$position")
            return position
        }

        override fun onPostExecute(result: Int?) {
            super.onPostExecute(result)
            val position = result
            if(position != null && position > -1){
                UpdateTimeStampPointerAsync(meteringDB, engagementId, position, time).execute()
            } else {
                GetLastLocationTimeAndWaypointsAsync(meteringDB, time, currentLatLng).execute()
            }


            if (HomeActivity.appInterruptHandler != null) {
                val start = if (position != null && position > -1) globalPath[position] else null
                val end = if (position != null && position > -1) globalPath[position + 1] else null
                HomeActivity.appInterruptHandler.polylineAlt(start, end)

//                    HomeActivity.appInterruptHandler.updateMeteringUI(globalPathDistance, 0, 0,
//                            location,
//                            location, globalPathDistance)
            }
            generateNotification(this@AltMeteringService, getNotificationMessage(), METER_NOTIF_ID)
        }
    }

}

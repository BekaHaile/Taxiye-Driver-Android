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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import product.clicklabs.jugnoo.driver.*
import product.clicklabs.jugnoo.driver.GpsDistanceCalculator.MAX_ACCURACY
import product.clicklabs.jugnoo.driver.GpsDistanceCalculator.MAX_SPEED_THRESHOLD
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.altmetering.db.MeteringDatabase
import product.clicklabs.jugnoo.driver.altmetering.model.*
import product.clicklabs.jugnoo.driver.altmetering.utils.PolyUtil
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo
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
        val TAG = AltMeteringService::class.java.simpleName

        fun updateMeteringActive(context: Context, meteringState: Boolean){
            Log.e(TAG, "updateMeteringActive meteringState=$meteringState")
            Prefs.with(context).save(METERING_STATE_ACTIVE, meteringState)
            if(!meteringState){
                Prefs.with(context).save(METERING_+ Constants.KEY_ENGAGEMENT_ID, 0)
                Prefs.with(context).save(METERING_+ Constants.KEY_PICKUP_LATITUDE, 0.toString())
                Prefs.with(context).save(METERING_+ Constants.KEY_PICKUP_LONGITUDE, 0.toString())
                Prefs.with(context).save(METERING_+ Constants.KEY_OP_DROP_LATITUDE, 0.toString())
                Prefs.with(context).save(METERING_+ Constants.KEY_OP_DROP_LONGITUDE, 0.toString())
            }
        }

        var fusedLocationClient: FusedLocationProviderClient? = null
        var activityBroadcastReceiver:BroadcastReceiver? = null
    }


    private fun isMeteringActive(context:Context): Boolean{
        val state = Prefs.with(context).getBoolean(METERING_STATE_ACTIVE, false)
        Log.e(TAG, "isMeteringActive state=$state")
        return state
    }

    private val METER_NOTIF_ID = 10102;

    private val LOCATION_UPDATE_INTERVAL: Long = 5000 // in milliseconds
    private val COROUTINE_EXCEPTION_DELAY: Long = 200 // in milliseconds
    private val PATH_POINT_DISTANCE_TOLLERANCE: Double = 100.0 // in meters
    private val DROP_DISTANCE_DEVIATION: Double = 200.0 // in meters
    val LAST_LOCATION_TIME_DIFF: Long = 60000 // in meters

    private fun getDeviationDistance(): Double{
        return Prefs.with(this).getString(Constants.KEY_DRIVER_ALT_DEVIATION_DISTANCE, PATH_POINT_DISTANCE_TOLLERANCE.toString()).toDouble()
    }
    private fun getDropDeviationDistance(): Double{
        return Prefs.with(this).getString(Constants.KEY_DRIVER_ALT_DROP_DEVIATION_DISTANCE, DROP_DISTANCE_DEVIATION.toString()).toDouble()
    }
    private fun getDeviationTime(): Long{
        return Prefs.with(this).getString(Constants.KEY_DRIVER_ALT_DEVIATION_TIME, LAST_LOCATION_TIME_DIFF.toString()).toLong()
    }

    var meteringDB: MeteringDatabase? = null
        get() {
            if (field == null) {
                field = MeteringDatabase.getInstance(MyApplication.getInstance())
            }
            return field
        }

    private var locationRequest = LocationRequest().apply {
        interval = LOCATION_UPDATE_INTERVAL
        fastestInterval = LOCATION_UPDATE_INTERVAL
        maxWaitTime = LOCATION_UPDATE_INTERVAL
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private lateinit var globalPath: MutableList<LatLng>
    private var globalPathDistance: Double = 0.0
    private var engagementId:Int = 0
    private lateinit var source: LatLng
    private lateinit var destination: LatLng
    private var currentLocation: Location? = null
    private var currentLocationTime: Long = 0

    override fun onBind(intent: Intent?): IBinder {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartCommand, intent=$intent")
        log("service", "startCommand="+isMeteringActive(this))

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
            return START_NOT_STICKY
        }


        startForeground(METER_NOTIF_ID, generateNotification(this, getNotificationMessage(), METER_NOTIF_ID))
        if(fusedLocationClient == null){
            Log.e(TAG, "fusedLocationClient initiated")
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        }
        registerActivityBroadcast()


        if (!isMeteringActive(this)) {
            updateMeteringActive(this, true)
            firstTimeDataClearAsync(meteringDB, engagementId, ::fetchPathAsyncCall)

        } else {
            readPathAsync(meteringDB, engagementId, ::requestLocationUpdates)
        }

        return START_STICKY
    }

    private fun fetchPathAsyncCall(){
        FetchPathAsync(meteringDB, engagementId, source, destination,
                mutableListOf(), ::requestLocationUpdates).execute()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.e(TAG, "onTaskRemoved, intent=$rootIntent")
        log("service", "taskRemoved")
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient?.removeLocationUpdates(locationCallback)
        fusedLocationClient = null
        unregisterActivityBroadcast()
        stopForeground(true)
        stopSelf()
        Log.e(TAG, "onDestroy")
        log("service", "destroy")
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
    fun requestLocationUpdates(list: MutableList<LatLng>, waypoints: MutableList<LatLng>?) {
        updatePathAndDistance(list, waypoints)
        fusedLocationClient?.removeLocationUpdates(locationCallback)
        fusedLocationClient?.requestLocationUpdates(locationRequest, locationCallback, null)
        log("service", "requestLocation")
    }

    private fun updatePathAndDistance(list: MutableList<LatLng>, waypoints: MutableList<LatLng>?) {
        globalPath = list
        globalPathDistance = 0.0
        for (i in list.indices) {
            if (i < list.size - 1) {
                globalPathDistance += MapUtils.distance(list[i], list[i + 1])
            }
        }
        Log.e(TAG, "updatePathAndDistance globalPathDistance=$globalPathDistance")
        log("distance", "total=$globalPathDistance")
        CustomerInfo.setMapValue(engagementId, Constants.KEY_WAYPOINT_DISTANCE, globalPathDistance.toString())
        if (HomeActivity.appInterruptHandler != null) {
            HomeActivity.appInterruptHandler.pathAlt(list, waypoints)
        }
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            super.onLocationResult(locationResult)
            if (locationResult != null) {
                val location = locationResult.lastLocation
                if(location != null && !Utils.mockLocationEnabled(location)) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    val time = System.currentTimeMillis()
                    if (location.accuracy > MAX_ACCURACY) { //accuracy check
                        Log.e("$TAG new onLocationResult", "accuracy wrong")
                        return
                    }
                    if (currentLocation != null) { //speed limit check
                        val displacement = MapUtils.distance(latLng, LatLng(currentLocation!!.latitude, currentLocation!!.longitude))
                        val millisDiff = time - currentLocationTime
                        val secondsDiff = millisDiff / 1000L
                        val speedMPS = if (secondsDiff > 0) displacement / secondsDiff else 0.0
                        Log.e("$TAG new onLocationResult", "speedMPS =$speedMPS")
                        if (speedMPS > Prefs.with(this@AltMeteringService).getFloat(Constants.KEY_MAX_SPEED_THRESHOLD, MAX_SPEED_THRESHOLD.toFloat()).toDouble()) {
                            Log.e("$TAG new onLocationResult", "speedMPS error")
                            log("location", "speedextra=$speedMPS")
                            return
                        }
                    }
                    currentLocation = location
                    currentLocationTime = time
                    Log.e("$TAG new onLocationResult", "location = $location")
                    getScanningPointerAndShortenPathToScan(latLng, time)
                }
            }
        }
    }

    fun getNotificationMessage():String{
        return getString(R.string.estimated_dis) + ": " +
                getDecimalFormat().format(Math.abs(globalPathDistance) * UserData.getDistanceUnitFactor(this)) +" "+
                Utils.getDistanceUnit(UserData.getDistanceUnit(this))
    }


    inner class FetchPathAsync(private val meteringDB: MeteringDatabase?, val engagementId:Int, val source: LatLng, val destination: LatLng,
                         var waypoints: MutableList<LatLng>?,
                         val onPost: (MutableList<LatLng>, MutableList<LatLng>?) -> Unit) : AsyncTask<Unit, Unit, MutableList<LatLng>>() {
        private lateinit var strWaypoints: String

        fun getWaypointsStr() {
            if(waypoints == null){
                val waypointsDB = meteringDB!!.getMeteringDao().getAllWaypoints(engagementId)
                waypoints = mutableListOf()
                for(wp in waypointsDB){
                    waypoints!!.add(LatLng(wp.lat, wp.lng))
                }
            }

            val sb = StringBuilder()
            if(waypoints != null) {
                for (i in waypoints!!.indices) {
                    sb.append("via:")
                            .append(waypoints!![i].latitude)
                            .append("%2C")
                            .append(waypoints!![i].longitude)
                            .append("%7C")
                }
            }
            strWaypoints = sb.toString()
        }

        override fun doInBackground(vararg params: Unit?): MutableList<LatLng> {
            try {
                getWaypointsStr()

                val strOrigin = source.latitude.toString() + "," + source.longitude.toString()
                val strDestination = destination.latitude.toString() + "," + destination.longitude.toString()
                log("gapi", "strOrigin=$strOrigin, strDest=$strDestination, strWp=$strWaypoints")

                val response = if (!TextUtils.isEmpty(strWaypoints)) {
                    GoogleRestApis.getDirectionsWaypoints(strOrigin, strDestination, strWaypoints)
                } else {
                    GoogleRestApis.getDirections(strOrigin, strDestination, false, "driving", false)
                }
                val result = String((response.body as TypedByteArray).bytes)
                val json = JSONObject(result)

                val list = MapUtils.getLatLngListFromPath(result)
                log("gapi", "status="+json.getString("status")+", list="+list.size)
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
                    Log.e("$TAG FetchPathAsync", "segments2 = $segments2")
                    Log.e("$TAG FetchPathAsync", "segments2size = "+segments2.size)
                }
                return list
            } catch (e: Exception) {
                e.printStackTrace()
                log("gapi", "error=$e")
            }
            return mutableListOf()
        }

        override fun onPostExecute(result: MutableList<LatLng>) {
            super.onPostExecute(result)
            onPost(result, waypoints)
        }

    }

    private fun readPathAsync(meteringDB: MeteringDatabase?, engagementId: Int, onPost: (MutableList<LatLng>, MutableList<LatLng>?) -> Unit){
        GlobalScope.launch(Dispatchers.IO) {
            try {
                delay(COROUTINE_EXCEPTION_DELAY)
                val segments2: MutableList<Segment> = try {
                    meteringDB!!.getMeteringDao().getAllSegments(engagementId, 0) as MutableList<Segment>
                } catch (e: Exception) {
                    delay(COROUTINE_EXCEPTION_DELAY)
                    meteringDB!!.getMeteringDao().getAllSegments(engagementId, 0) as MutableList<Segment>
                }
                Log.e("$TAG ReadPathAsync", "segments2 = $segments2")
                Log.e("$TAG ReadPathAsync", "segments2size = "+segments2.size)
                val list = mutableListOf<LatLng>()
                for (segment in segments2) {
                    list.add(LatLng(segment.slat, segment.sLng))
                }

                val waypoints = try {
                    meteringDB!!.getMeteringDao().getAllWaypoints(engagementId)
                } catch (e: Exception) {
                    delay(COROUTINE_EXCEPTION_DELAY)
                    meteringDB!!.getMeteringDao().getAllWaypoints(engagementId)
                }
                val waypointsL:MutableList<LatLng> = mutableListOf()
                for(wp in waypoints){
                    waypointsL.add(LatLng(wp.lat, wp.lng))
                }
                log("old", "segments="+list.size+", waypoints="+waypoints.size)

                launch(Dispatchers.Main){onPost(list, waypointsL)}
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateTimeStampPointerAsync(meteringDB: MeteringDatabase?, engagementId: Int, position:Int, timeStamp:Long){
        GlobalScope.launch(Dispatchers.IO) {
            try {
                try {
                    meteringDB!!.getMeteringDao().deleteScanningPointer(engagementId)
                } catch (e: Exception) {
                    delay(COROUTINE_EXCEPTION_DELAY)
                    meteringDB!!.getMeteringDao().deleteScanningPointer(engagementId)
                }
                meteringDB!!.getMeteringDao().insertScanningPointer(ScanningPointer(engagementId, position))

                meteringDB.getMeteringDao().deleteLastLocationTimestamp(engagementId)
                meteringDB.getMeteringDao().insertLastLocationTimestamp(LastLocationTimestamp(engagementId, timeStamp))
                Log.e(TAG, "updateTimeStampPointerAsync position=$position")
            } catch (e: Exception) {
            }
        }
    }


    private fun getLastLocationTimeAndWaypointsAsync(meteringDB: MeteringDatabase?, time:Long, waypoint:LatLng){
        GlobalScope.launch(Dispatchers.IO) {
            var globalWaypointLatLngs:MutableList<LatLng>? = null
            try {
                val timestamps: MutableList<LastLocationTimestamp> = try {
                    meteringDB!!.getMeteringDao().getLastLocationTimeStamp(engagementId) as MutableList<LastLocationTimestamp>
                } catch (e: Exception) {
                    delay(COROUTINE_EXCEPTION_DELAY)
                    meteringDB!!.getMeteringDao().getLastLocationTimeStamp(engagementId) as MutableList<LastLocationTimestamp>
                }
                Log.e("$TAG GetLastLocationTimeAndWaypointsAsync", "timestamps= $timestamps")

                if(timestamps.size > 0){
                    Log.e("$TAG GetLastLocationTimeAndWaypointsAsync", "timestamps[0].timestamp = " + timestamps[0].timestamp)
                    val diff = time - timestamps[0].timestamp
                    Log.e("$TAG GetLastLocationTimeAndWaypointsAsync", "diff = $diff")

                    if (diff > getDeviationTime()){
                        log("deviation", "diff="+(diff/1000)+", serverTime="+getDeviationTime())
                        fusedLocationClient?.removeLocationUpdates(locationCallback)

                        meteringDB!!.getMeteringDao().deleteScanningPointer(engagementId)
                        meteringDB.getMeteringDao().insertWaypoint(Waypoint(engagementId, waypoint.latitude, waypoint.longitude))

                        val waypoints = meteringDB.getMeteringDao().getAllWaypoints(engagementId)
                        globalWaypointLatLngs = mutableListOf()
                        for(wp in waypoints){
                            globalWaypointLatLngs.add(LatLng(wp.lat, wp.lng))
                        }
                        Log.e("$TAG GetLastLocationTimeAndWaypointsAsync", "waypoint added = $waypoint")
                    }
                } else {
                    fusedLocationClient?.removeLocationUpdates(locationCallback)
                    meteringDB!!.getMeteringDao().deleteScanningPointer(engagementId)

                    source = waypoint
                    Prefs.with(this@AltMeteringService).save(METERING_+ Constants.KEY_PICKUP_LATITUDE, source.toString())
                    Prefs.with(this@AltMeteringService).save(METERING_+ Constants.KEY_PICKUP_LONGITUDE, source.toString())

                    Log.e("$TAG GetLastLocationTimeAndWaypointsAsync", "source shifted to waypoint = $waypoint")
                    log("deviation", "first")
                    globalWaypointLatLngs = mutableListOf()
                }
            } catch (e: Exception) {
            }
            launch(Dispatchers.Main){
                if(globalWaypointLatLngs != null) {
                    FetchPathAsync(meteringDB, engagementId, source, destination, globalWaypointLatLngs, ::requestLocationUpdates).execute()
                }
            }
        }
    }

    private fun firstTimeDataClearAsync(meteringDB:MeteringDatabase?, engagementId: Int, post:()->Unit){
        GlobalScope.launch(Dispatchers.IO){
            try {
                try {
                    meteringDB!!.getMeteringDao().deleteAllSegments(engagementId)
                } catch (e: Exception) {
                    delay(COROUTINE_EXCEPTION_DELAY)
                    meteringDB!!.getMeteringDao().deleteAllSegments(engagementId)
                }
                meteringDB!!.getMeteringDao().deleteAllWaypoints(engagementId)
                meteringDB.getMeteringDao().deleteScanningPointer(engagementId)
                meteringDB.getMeteringDao().deleteLastLocationTimestamp(engagementId)
                meteringDB.getMeteringDao().deleteLogItem(engagementId)
                Log.e(TAG, "FirstTimeDataClearAsync")
                log("firstClear", "done")
            } catch (e: Exception) {
            }
            launch(Dispatchers.Main){ post() }
        }
    }

    private var intentEngagementId:Int = 0


    private fun registerActivityBroadcast(){
        if(activityBroadcastReceiver == null){
            activityBroadcastReceiver = object:BroadcastReceiver(){
                override fun onReceive(context: Context?, intent: Intent?) {
                    intentEngagementId = intent!!.getIntExtra(Constants.KEY_ENGAGEMENT_ID, engagementId)
                    val latLng = LatLng(intent.getDoubleExtra(Constants.KEY_LATITUDE, 0.0),
                            intent.getDoubleExtra(Constants.KEY_LONGITUDE, 0.0))
                    if (Math.abs(latLng.latitude) > 0 && Math.abs(latLng.longitude) > 0) {
                        fusedLocationClient?.removeLocationUpdates(locationCallback)
                        val distance = MapUtils.distance(destination, latLng)
                        Log.e(TAG, "activityBroadcastReceiver distance = $distance")
                        log("receiver", "end dist=$distance, latlng="+latLng.latitude+","+latLng.longitude+", destination="+destination.latitude+","+destination.longitude)
                        if(distance <= getDropDeviationDistance()){
                            //no need to do anything
                            insertRideDataAndEndRide()
                        } else {
                            //update path by changing destination
                            destination = latLng
                            FetchPathAsync(meteringDB, engagementId, source, destination, null, ::updateDistanceAndCallbackEndRide).execute()
                        }

                    } else {
                        Utils.showToast(this@AltMeteringService, getString(R.string.waiting_for_location))
                        LocalBroadcastManager.getInstance(this@AltMeteringService).sendBroadcast(Intent(HomeActivity.INTENT_ACTION_ACTIVITY_END_RIDE_CALLBACK))
                    }
                }
            }
            LocalBroadcastManager.getInstance(this@AltMeteringService).registerReceiver(activityBroadcastReceiver!!, IntentFilter(INTENT_ACTION_END_RIDE_TRIGGER))
            log("service", "registerReceiver")
        }
    }

    private fun unregisterActivityBroadcast(){
        if(activityBroadcastReceiver != null) {
            LocalBroadcastManager.getInstance(this@AltMeteringService).unregisterReceiver(activityBroadcastReceiver!!)
            activityBroadcastReceiver = null
            log("service", "unregisterReceiver")
        }
    }

    private fun updateDistanceAndCallbackEndRide(list: MutableList<LatLng>, waypoints: MutableList<LatLng>?) {
        updatePathAndDistance(list, waypoints)
        insertRideDataAndEndRide()
    }

    fun insertRideDataAndEndRide(){
        var accDistance = 0.0
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val segments2: MutableList<Segment> = try {
                    meteringDB!!.getMeteringDao().getAllSegments(engagementId, 0) as MutableList<Segment>
                } catch (e: Exception) {
                    delay(COROUTINE_EXCEPTION_DELAY)
                    meteringDB!!.getMeteringDao().getAllSegments(engagementId, 0) as MutableList<Segment>
                }
                Log.e("$TAG InsertRideDataAndEndRide", "segments2 = $segments2")
                for (segment in segments2) {
                    accDistance += MapUtils.distance(LatLng(segment.slat, segment.sLng), LatLng(segment.dLat, segment.dLng))
                }
                Log.e("$TAG InsertRideDataAndEndRide", "accDistance = $accDistance")
                //save ride values to db
                CustomerInfo.setMapValue(engagementId, Constants.KEY_WAYPOINT_DISTANCE, accDistance.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }

            launch(Dispatchers.Main){
                if (HomeActivity.appInterruptHandler != null) {
//                HomeActivity.appInterruptHandler.updateMeteringUI(globalPathDistance, 0, 0,
//                        currentLocation,
//                        currentLocation, globalPathDistance)
                }
                val intent = Intent(HomeActivity.INTENT_ACTION_ACTIVITY_END_RIDE_CALLBACK)
                intent.apply {
                    putExtra(Constants.KEY_SUCCESS, true)
                    putExtra(Constants.KEY_ENGAGEMENT_ID, intentEngagementId)
                }
                Log.e(TAG, "InsertRideDataAndEndRide intent = "+intent.extras)
                LocalBroadcastManager.getInstance(this@AltMeteringService).sendBroadcast(intent)
                stopForeground(true)
                stopSelf()
            }
        }
    }

    fun getScanningPointerAndShortenPathToScan(currentLatLng: LatLng, time:Long){
        var lastScanningPoint: Int
        GlobalScope.launch(Dispatchers.IO){
            try {
                val list :List<ScanningPointer> = meteringDB!!.getMeteringDao().getScanningPointer(engagementId)
                lastScanningPoint = 0
                if(list.isNotEmpty()){
                    lastScanningPoint = list[0].position
                }
                Log.e(TAG, "GetScanningPointerAndShortenPathToScan lastScanningPoint=$lastScanningPoint")

                val pathLength = globalPath.size
                val result = PolyUtil.locationIndexOnPath(currentLatLng, globalPath.subList(lastScanningPoint, pathLength),
                        true, getDeviationDistance())

                launch(Dispatchers.Main){

                    Log.i(TAG, "GetScanningPointerAndShortenPathToScan position=$result")

                    if(result > -1){
                        updateTimeStampPointerAsync(meteringDB, engagementId, lastScanningPoint + result, time)
                    } else {
                        getLastLocationTimeAndWaypointsAsync(meteringDB, time, currentLatLng)
                    }


                    try {
                        if (HomeActivity.appInterruptHandler != null) {
                            val start = if (result > -1 && (lastScanningPoint + result) < globalPath.size) globalPath[lastScanningPoint + result] else null
                            val end = if (result > -1 && (lastScanningPoint + result + 1) < globalPath.size) globalPath[lastScanningPoint + result + 1] else null
                            HomeActivity.appInterruptHandler.polylineAlt(start, end)

        //                    HomeActivity.appInterruptHandler.updateMeteringUI(globalPathDistance, 0, 0,
        //                            location,
        //                            location, globalPathDistance)
                        }
                    } catch (e: Exception) {
                    }
                    generateNotification(this@AltMeteringService, getNotificationMessage(), METER_NOTIF_ID)
                }
            } catch (e: Exception) {
            }
        }
    }

    fun log(tag:String, message:String){
        if(Prefs.with(this).getInt(Constants.KEY_DRIVER_ALT_LOGGING_ENABLED, 1) == 1) {
            Log.e(tag, message)
            GlobalScope.launch(Dispatchers.IO){
                try {
                    meteringDB!!.getMeteringDao().insertLogItem(LogItem(engagementId, "$tag: $message"))
                } catch (e: Exception) {
                }
            }
        }
    }
}

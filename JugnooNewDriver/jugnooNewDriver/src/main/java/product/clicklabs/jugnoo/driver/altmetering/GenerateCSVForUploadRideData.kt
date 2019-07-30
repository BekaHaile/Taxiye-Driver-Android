package product.clicklabs.jugnoo.driver.altmetering

import android.os.AsyncTask
import com.google.android.gms.maps.model.LatLng
import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.MyApplication
import product.clicklabs.jugnoo.driver.altmetering.db.MeteringDatabase
import product.clicklabs.jugnoo.driver.altmetering.model.LogItem
import product.clicklabs.jugnoo.driver.altmetering.model.Segment
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo
import product.clicklabs.jugnoo.driver.utils.Log
import product.clicklabs.jugnoo.driver.utils.MapUtils
import product.clicklabs.jugnoo.driver.utils.Prefs
import java.text.SimpleDateFormat
import java.util.*

abstract class GenerateCSVForUploadRideData(val customerInfo: CustomerInfo) : AsyncTask<Int, Int, Int>(){
    val TAG = GenerateCSVForUploadRideData::class.java.simpleName
    private var csvPathStr:String = ""
    private var csvWaypointsStr:String = ""
    private var numWaypoints:Int = 0
    override fun doInBackground(vararg params: Int?):Int {
        if (customerInfo.getDropLatLng() != null && Prefs.with(MyApplication.getInstance()).getInt(Constants.KEY_DRIVER_ALT_DISTANCE_LOGIC, 0) == 1) {
            val engagementId = customerInfo.getEngagementId()
            val meteringDB = MeteringDatabase.getInstance(MyApplication.getInstance())

            val segments2: MutableList<Segment> = meteringDB!!.getMeteringDao().getAllSegments(engagementId, 0) as MutableList<Segment>
            Log.e(TAG, "segments2 = $segments2")
            val list = mutableListOf<LatLng>()
            for (i in segments2.indices) {
                list.add(LatLng(segments2[i].slat, segments2[i].sLng))
                if(i == segments2.size-1){
                    list.add(LatLng(segments2[i].dLat, segments2[i].dLng))
                }
            }

            val templatePath = "lat,lng,accDistance"
            val newLine = "\n"
            val csvPath = StringBuilder()
            if (list.size > 0) {
                csvPath.append(templatePath).append(newLine)
            }
            var accDistance = 0.0
            for (i in list.indices) {
                if (i < list.size - 1) {
                    accDistance += MapUtils.distance(list[i], list[i + 1])
                }
                csvPath.append(list[i].latitude.toString() + "," + list[i].longitude.toString() + "," + accDistance)
                csvPath.append(newLine)
//                    Database2.getInstance(this).insertRideDataWOChecks(this,
//                            globalPath[i].latitude.toString(), globalPath[i].longitude.toString(), System.currentTimeMillis().toString(),
//                            engagementId, accDistance, 0)
            }
            csvPath.append(newLine).append(accDistance)

            csvPathStr = csvPath.toString()

            Log.e(TAG, "accDistance = $accDistance")

            val waypoints = meteringDB.getMeteringDao().getAllWaypoints(engagementId)
            val waypointsL = mutableListOf<LatLng>()
            for (wp in waypoints) {
                waypointsL.add(LatLng(wp.lat, wp.lng))
            }

            meteringDB.getMeteringDao().insertLogItem(LogItem(engagementId, "endAsync: " + "wppath=" + list.size + ", accDist=" + accDistance))

            val templateWP = "lat,lng"
            val csvWP = StringBuilder()
            csvWP.append(templateWP).append(newLine)
            for (i in waypointsL.indices) {
                csvWP.append(waypointsL[i].latitude.toString() + "," + waypointsL[i].longitude.toString())
                csvWP.append(newLine)
            }
            numWaypoints = waypointsL.size
            csvWP.append(newLine).append(numWaypoints)


            val logs = meteringDB.getMeteringDao().getLogItem(engagementId)
            csvWP.append(newLine)
            val sdfTo = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
            for (i in logs.indices) {
                csvWP.append(sdfTo.format(Date(logs[i].creationDate))).append(" ").append(logs[i].message).append(newLine)
            }

            csvWaypointsStr = csvWP.toString()
            Log.e(TAG, "waypointsL = " + waypointsL.size)

            meteringDB.getMeteringDao().deleteAllSegments(engagementId)
            meteringDB.getMeteringDao().deleteAllWaypoints(engagementId)
            meteringDB.getMeteringDao().deleteScanningPointer(engagementId)
            meteringDB.getMeteringDao().deleteLastLocationTimestamp(engagementId)
            meteringDB.getMeteringDao().deleteLogItem(engagementId)
            AltMeteringService.updateMeteringActive(MyApplication.getInstance(), false, engagementId)

            Log.e(TAG, "allSegments = "+meteringDB.getMeteringDao().getAllSegments())
            Log.e(TAG, "allWaypoints = "+meteringDB.getMeteringDao().getAllWaypoints())
            Log.e(TAG, "allScanningPointers = "+meteringDB.getMeteringDao().getAllScanningPointers())
            Log.e(TAG, "allLocationTimestamps = "+meteringDB.getMeteringDao().getAllLocationTimeStamps())
            Log.e(TAG, "allLogItems = "+meteringDB.getMeteringDao().getAllLogItems())

            //save ride values to db
//        CustomerInfo.setMapValue(engagementId, Constants.KEY_WAYPOINT_DISTANCE, accDistance.toString())
//        CustomerInfo.setMapValue(engagementId, Constants.KEY_CSV_PATH, csvPathStr)
//        CustomerInfo.setMapValue(engagementId, Constants.KEY_CSV_WAYPOINTS, csvWaypointsStr)
//        CustomerInfo.setMapValue(engagementId, Constants.KEY_NUM_WAYPOINTS, numWaypoints.toString())

        }
        return 0
    }

    override fun onPostExecute(result: Int?) {
        super.onPostExecute(result)

        onDataReceived(csvPathStr, csvWaypointsStr, numWaypoints)
    }

    abstract fun onDataReceived(csvPath:String, csvWaypoints:String, numWaypoints:Int)

}
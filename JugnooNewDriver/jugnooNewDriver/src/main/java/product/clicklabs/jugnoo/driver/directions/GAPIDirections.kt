package product.clicklabs.jugnoo.driver.directions

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.MyApplication
import product.clicklabs.jugnoo.driver.directions.room.database.DirectionsPathDatabase
import product.clicklabs.jugnoo.driver.directions.room.model.Path
import product.clicklabs.jugnoo.driver.directions.room.model.Point
import product.clicklabs.jugnoo.driver.google.GoogleRestApis
import product.clicklabs.jugnoo.driver.retrofit.RestClient
import product.clicklabs.jugnoo.driver.utils.MapUtils
import product.clicklabs.jugnoo.driver.utils.Prefs
import retrofit.mime.TypedByteArray
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.*

object GAPIDirections {

    private var db: DirectionsPathDatabase? = null
        get() {
            if (field == null) {
                field = DirectionsPathDatabase.getInstance(MyApplication.getInstance())
            }
            return field
        }

    private var numberFormat: NumberFormat? = null
        get() {
            if(field == null){
                field = NumberFormat.getInstance(Locale.ENGLISH)
                field!!.minimumFractionDigits = 3
                field!!.maximumFractionDigits = 3
                field!!.roundingMode = RoundingMode.HALF_UP
                field!!.isGroupingUsed = false
            }
            return field
        }

    fun getDirectionsPath(engagementId:Long, source:LatLng, destination:LatLng, apiSource:String, callback:Callback?){

        GlobalScope.launch(Dispatchers.IO){
            try {
                val directionsResult = getDirectionsPathSync(engagementId, source, destination, apiSource)
                if(directionsResult != null){
                    launch(Dispatchers.Main){callback?.onSuccess(directionsResult.latLngs, directionsResult.path)}
                } else {
                    launch(Dispatchers.Main){callback?.onFailure()}
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main){callback?.onFailure()}
            }
        }

    }

    fun getDirectionsPathSync(engagementId:Long, source:LatLng, destination:LatLng, apiSource:String) : DirectionsResult? {
        var directionsResult:DirectionsResult? = null
        val timeStamp = System.currentTimeMillis()

        val sourceLat = numberFormat!!.format(source.latitude).toDouble()
        val sourceLng = numberFormat!!.format(source.longitude).toDouble()
        val destinationLat = numberFormat!!.format(destination.latitude).toDouble()
        val destinationLng = numberFormat!!.format(destination.longitude).toDouble()

        val paths = db!!.getDao().getPath(engagementId,
                sourceLat,
                sourceLng,
                destinationLat,
                destinationLng,
                timeStamp - Constants.DAY_MILLIS*30)

        val cachingEnabled = Prefs.with(MyApplication.getInstance()).getInt(Constants.KEY_DRIVER_DIRECTIONS_CACHING, 1) == 1
        //path is not found
        if(!cachingEnabled || paths == null || paths.isEmpty()){

            try {
                val jungleObj = JSONObject(Prefs.with(MyApplication.getInstance()).getString(Constants.KEY_JUNGLE_MAPS_OBJ, Constants.EMPTY_JSON_OBJECT))
                if(jungleObj.has(Constants.KEY_JUNGLE_OPTIONS)){

                    val option = jungleObj.optInt(Constants.KEY_JUNGLE_OPTIONS, 0)

                    val pointsJ = JSONArray()
                    val startJ = JSONObject()
                    startJ.put(Constants.KEY_LAT, sourceLat.toString()).put(Constants.KEY_LNG, sourceLng.toString())
                    val destJ = JSONObject()
                    destJ.put(Constants.KEY_LAT, destinationLat.toString()).put(Constants.KEY_LNG, destinationLng.toString())
                    pointsJ.put(startJ).put(destJ)

                    val params = HashMap<String, String>()
                    params[Constants.KEY_JUNGLE_POINTS] = pointsJ.toString()
                    params[Constants.KEY_JUNGLE_OPTIONS] = option.toString()

                    when(option){
                        1 -> { //here map
                            params[Constants.KEY_JUNGLE_APP_ID] = jungleObj.optString(Constants.KEY_JUNGLE_APP_ID)
                            params[Constants.KEY_JUNGLE_APP_CODE] = jungleObj.optString(Constants.KEY_JUNGLE_APP_CODE)
                        }
                        2 -> { //google
                            params[Constants.KEY_JUNGLE_API_KEY] = jungleObj.optString(Constants.KEY_JUNGLE_API_KEY)
                        }
                        3 -> { //map box
                            params[Constants.KEY_JUNGLE_ACCESS_TOKEN] = jungleObj.optString(Constants.KEY_JUNGLE_ACCESS_TOKEN)
                        }
                    }
                    val response = RestClient.getJungleMapsApi().directions(params)


                    val result = String((response.body as TypedByteArray).bytes)
                    val jObj = JSONObject(result)

                    val list = mutableListOf<LatLng>()
                    list.addAll(MapUtils.getLatLngListFromPathJungle(result))
                    val distanceValue = jObj.getJSONObject("data").getJSONArray("paths").getJSONObject(0).getDouble("distance")
                    val timeValue = jObj.getJSONObject("data").getJSONArray("paths").getJSONObject(0).getDouble("time")/1000

                    val path = Path(engagementId,
                            sourceLat,
                            sourceLng,
                            destinationLat,
                            destinationLng,
                            distanceValue, timeValue,
                            timeStamp)

                    directionsResult = DirectionsResult(list, path)

                } else {
                    throw Exception()
                }

            } catch (e: Exception) {
                //google directions hit
                val response = GoogleRestApis.getDirections("$sourceLat,$sourceLng", "$destinationLat,$destinationLng",
                        false, "driving", false, apiSource)
                val result = String((response.body as TypedByteArray).bytes)
                val jObj = JSONObject(result)

                val list = mutableListOf<LatLng>()
                list.addAll(MapUtils.getLatLngListFromPath(result))
                val distanceValue = jObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getDouble("value")
                val timeValue = jObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getDouble("value")

                val path = Path(engagementId,
                        sourceLat,
                        sourceLng,
                        destinationLat,
                        destinationLng,
                        distanceValue, timeValue,
                        timeStamp)

                directionsResult = DirectionsResult(list, path)
            }


            if(cachingEnabled && directionsResult != null) {
                db!!.getDao().deleteAllPath(timeStamp)
                db!!.getDao().insertPath(directionsResult.path)

                //inserting path points
                val points = mutableListOf<Point>()
                for (latlng in directionsResult.latLngs) {
                    points.add(Point(timeStamp, latlng.latitude, latlng.longitude))
                }
                db!!.getDao().insertPathPoints(points)
            }

        } else {
            val segments = db!!.getDao().getPathPoints(paths[0].timeStamp)
            if (segments != null) {
                val list = mutableListOf<LatLng>()
                for(segment in segments){
                    list.add(LatLng(segment.lat, segment.lng))
                }
                directionsResult = DirectionsResult(list, paths[0])
            }
        }
        return directionsResult
    }

    fun deleteDirectionsPath(engagementId:Long){
        GlobalScope.launch(Dispatchers.IO){
            db!!.getDao().deleteAllPath(engagementId)
            db!!.getDao().deleteOldPaths(System.currentTimeMillis() - Constants.DAY_MILLIS*30)
        }
    }

    interface Callback{
        fun onSuccess(latLngs:MutableList<LatLng>, path:Path)
        fun onFailure()
    }

    data class DirectionsResult(val latLngs:MutableList<LatLng>, val path:Path)

}
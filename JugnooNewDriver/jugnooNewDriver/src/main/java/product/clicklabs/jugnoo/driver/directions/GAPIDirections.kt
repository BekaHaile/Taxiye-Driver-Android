package product.clicklabs.jugnoo.driver.directions

import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
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
import product.clicklabs.jugnoo.driver.google.GoogleGeocodeResponse
import product.clicklabs.jugnoo.driver.google.GoogleRestApis
import product.clicklabs.jugnoo.driver.retrofit.RestClient
import product.clicklabs.jugnoo.driver.utils.MapUtils
import product.clicklabs.jugnoo.driver.utils.Prefs
import retrofit.mime.TypedByteArray
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.*
import kotlin.collections.HashMap

object GAPIDirections {

    private val gson = Gson()

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
                field!!.minimumFractionDigits = 4
                field!!.maximumFractionDigits = 4
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

    fun putJungleOptionsParams(params:HashMap<String, String>, jungleObj:JSONObject){
        val option = jungleObj.optInt(Constants.KEY_JUNGLE_OPTIONS, 0)
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
                val jungleObj = JSONObject(Prefs.with(MyApplication.getInstance()).getString(Constants.KEY_JUNGLE_DIRECTIONS_OBJ, Constants.EMPTY_JSON_OBJECT))
                if(jungleObj.has(Constants.KEY_JUNGLE_OPTIONS)){

                    val pointsJ = JSONArray()
                    val startJ = JSONObject()
                    startJ.put(Constants.KEY_LAT, sourceLat.toString()).put(Constants.KEY_LNG, sourceLng.toString())
                    val destJ = JSONObject()
                    destJ.put(Constants.KEY_LAT, destinationLat.toString()).put(Constants.KEY_LNG, destinationLng.toString())
                    pointsJ.put(startJ).put(destJ)

                    val params = HashMap<String, String>()
                    params[Constants.KEY_JUNGLE_POINTS] = pointsJ.toString()

                    putJungleOptionsParams(params, jungleObj)

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
                try {//google directions hit
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
                } catch (e: Exception) {
                }
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

    fun getDistanceMatrix(sourceLatLng: LatLng, destLatLng: LatLng, apiSource:String) : DistanceMatrixResult? {
        var distanceMatrixResult:DistanceMatrixResult? = null
        try {
            val jungleObj = JSONObject(Prefs.with(MyApplication.getInstance()).getString(Constants.KEY_JUNGLE_DISTANCE_MATRIX_OBJ, Constants.EMPTY_JSON_OBJECT))
            if(jungleObj.has(Constants.KEY_JUNGLE_OPTIONS)){

                val params = HashMap<String, String>()
                params[Constants.KEY_JUNGLE_ORIGIN_LAT] = sourceLatLng.latitude.toString()
                params[Constants.KEY_JUNGLE_ORIGIN_LNG] = sourceLatLng.longitude.toString()
                params[Constants.KEY_JUNGLE_DEST_LAT] = destLatLng.latitude.toString()
                params[Constants.KEY_JUNGLE_DEST_LNG] = destLatLng.longitude.toString()

                putJungleOptionsParams(params, jungleObj)

                val response = RestClient.getJungleMapsApi().distancematrix(params)

                val result = String((response.body as TypedByteArray).bytes)
                val jObj = JSONObject(result)

                val distStr = jObj.getJSONObject("data").getString("distance")
                val timeStr = jObj.getJSONObject("data").getString("Time")

                val distance = if(jObj.getJSONObject("data").has("distance_in_meter")){
                    jObj.getJSONObject("data").getDouble("distance_in_meter")
                } else if(distStr.contains(' ')){
                    distStr.split(' ')[0].toDouble() * 1000.0
                } else {
                    distStr.toDouble()
                }

                val time = if(jObj.getJSONObject("data").has("time_in_second")){
                    jObj.getJSONObject("data").getDouble("time_in_second")
                } else if(timeStr.contains(' ')){
                    timeStr.split(' ')[0].toDouble()
                } else {
                    timeStr.toDouble()
                }

                distanceMatrixResult = DistanceMatrixResult(distance, time)
            } else {
                throw Exception()
            }

        } catch (e: Exception) {
            try {//google distance matrix hit
                val response = GoogleRestApis.getDistanceMatrix("${sourceLatLng.latitude},${sourceLatLng.longitude}", "${destLatLng.latitude},${destLatLng.longitude}",
                        "EN", false, false, apiSource)
                val result = String((response.body as TypedByteArray).bytes)
                val jObj = JSONObject(result)

                val distanceValue = jObj.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance").getDouble("value")
                val timeValue = jObj.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").getDouble("value")
                distanceMatrixResult = DistanceMatrixResult(distanceValue, timeValue)
            } catch (e: Exception) {
            }
        }
        return distanceMatrixResult
    }

    fun getGeocodeAddress(sourceLatLng: LatLng, language:String, apiSource:String) : GeocodeResult? {
        var geocodeResult:GeocodeResult? = null
        try {
            val jungleObj = JSONObject(Prefs.with(MyApplication.getInstance()).getString(Constants.KEY_JUNGLE_GEOCODE_OBJ, Constants.EMPTY_JSON_OBJECT))
            if(jungleObj.has(Constants.KEY_JUNGLE_OPTIONS)){

                val params = HashMap<String, String>()
                params[Constants.KEY_JUNGLE_LAT] = sourceLatLng.latitude.toString()
                params[Constants.KEY_JUNGLE_LNG] = sourceLatLng.longitude.toString()

                putJungleOptionsParams(params, jungleObj)

                val response = RestClient.getJungleMapsApi().searchReverse(params)

                val result = String((response.body as TypedByteArray).bytes)
                val jObj = JSONObject(result)

                val address = jObj.getJSONObject("data").getString("address")

                geocodeResult = GeocodeResult(null, address)
            } else {
                throw Exception()
            }

        } catch (e: Exception) {
            try {//google reverse geocode hit
                val response = GoogleRestApis.geocode("${sourceLatLng.latitude},${sourceLatLng.longitude}", language, apiSource)
                val result = String((response.body as TypedByteArray).bytes)
                val googleGeocodeResponse = gson.fromJson(result, GoogleGeocodeResponse::class.java)
                if (googleGeocodeResponse.results != null && googleGeocodeResponse.results!!.isNotEmpty()) {
                    geocodeResult = GeocodeResult(googleGeocodeResponse, null)
                }
            } catch (e: Exception) {
            }
        }
        return geocodeResult
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
    data class DistanceMatrixResult(val distanceValue:Double, val timeValue:Double)
    data class GeocodeResult(val googleGeocodeResponse: GoogleGeocodeResponse?, val singleAddress:String?)

}
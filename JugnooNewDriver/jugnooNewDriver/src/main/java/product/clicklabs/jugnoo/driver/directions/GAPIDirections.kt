package product.clicklabs.jugnoo.driver.directions

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import product.clicklabs.jugnoo.driver.MyApplication
import product.clicklabs.jugnoo.driver.google.GoogleRestApis
import product.clicklabs.jugnoo.driver.room.database.DirectionsPathDatabase
import product.clicklabs.jugnoo.driver.room.model.Path
import product.clicklabs.jugnoo.driver.room.model.Point
import product.clicklabs.jugnoo.driver.utils.MapUtils
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

                val timeStamp = System.currentTimeMillis()
                val paths = db!!.getDao().getPath(engagementId,
                        numberFormat!!.format(source.latitude).toDouble(),
                        numberFormat!!.format(source.longitude).toDouble(),
                        numberFormat!!.format(destination.latitude).toDouble(),
                        numberFormat!!.format(destination.longitude).toDouble(),
                        timeStamp)

                //path is not found
                if(paths == null || paths.isEmpty()){
                    val list = mutableListOf<LatLng>()

                    //google directions hit
                    val response = GoogleRestApis.getDirections(source.latitude.toString() + "," + source.longitude,
                            destination.latitude.toString() + "," + destination.longitude,
                            false, "driving", false, apiSource)
                    val result = String((response.body as TypedByteArray).bytes)
                    val jObj = JSONObject(result)

                    list.addAll(MapUtils.getLatLngListFromPath(result))
                    val distanceValue = jObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("distance").getDouble("value")
                    val timeValue = jObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("duration").getDouble("value")

                    //inserting path
                    val path = Path(engagementId,
                            numberFormat!!.format(source.latitude).toDouble(),
                            numberFormat!!.format(source.longitude).toDouble(),
                            numberFormat!!.format(destination.latitude).toDouble(),
                            numberFormat!!.format(destination.longitude).toDouble(),
                            distanceValue, timeValue, timeStamp)

                    db!!.getDao().deleteAllPath(engagementId)
                    db!!.getDao().insertPath(path)

                    //inserting path points
                    val points = mutableListOf<Point>()
                    for(latlng in list){
                        points.add(Point(timeStamp, latlng.latitude, latlng.longitude))
                    }
                    db!!.getDao().insertPathPoints(points)

                    callback?.onSuccess(list, distanceValue, timeValue)

                } else {
                    val segments = db!!.getDao().getPathPoints(paths[0].id)
                    if (segments != null) {
                        val list = mutableListOf<LatLng>()
                        for(segment in segments){
                            list.add(LatLng(segment.lat, segment.lng))
                        }
                        callback?.onSuccess(list, paths[0].distance, paths[0].time)
                    } else {
                        callback?.onFailure()
                    }
                }
            } catch (e: Exception) {
                callback?.onFailure()
            }
        }


    }

    fun deleteDirectionsPath(engagementId:Long){
        GlobalScope.launch(Dispatchers.IO){
            db!!.getDao().deleteAllPath(engagementId)
        }
    }

    interface Callback{
        fun onSuccess(latLngs:MutableList<LatLng>, distance:Double, time:Double)
        fun onFailure()
    }

}
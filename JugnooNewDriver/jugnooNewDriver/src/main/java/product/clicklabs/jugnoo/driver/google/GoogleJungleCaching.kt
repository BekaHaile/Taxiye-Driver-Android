package product.clicklabs.jugnoo.driver.google

import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import org.json.JSONObject
import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.Data
import product.clicklabs.jugnoo.driver.MyApplication
import product.clicklabs.jugnoo.driver.directions.JungleApisImpl
import product.clicklabs.jugnoo.driver.retrofit.RestClient
import product.clicklabs.jugnoo.driver.retrofit.model.PlaceDetailsResponse
import product.clicklabs.jugnoo.driver.retrofit.model.PlacesAutocompleteResponse
import product.clicklabs.jugnoo.driver.retrofit.model.Prediction
import product.clicklabs.jugnoo.driver.utils.LocaleHelper
import product.clicklabs.jugnoo.driver.utils.MapUtils
import product.clicklabs.jugnoo.driver.utils.Prefs
import retrofit.mime.TypedByteArray

object GoogleJungleCaching {

    private val gson = Gson()

    private fun isGoogleCachingEnabled():Boolean{
        return Prefs.with(MyApplication.getInstance()).getInt(Constants.KEY_DRIVER_GOOGLE_CACHING_ENABLED, 1) == 1
    }
    private fun getUserId():String{
        return if(Data.userData != null) Data.userData.userId else Prefs.with(MyApplication.getInstance()).getString(Constants.KEY_USER_ID, "0")
    }

    //Api for text input autocomplete Place search
    fun getAutoCompletePredictions(input:String, sessiontoken:String, components:String, location:String, radius:String, callback: PlacesCallback): Job{
        return GlobalScope.launch(Dispatchers.Main){
            var predictions: MutableList<Prediction>? = null
            val arr = location.split(",")
            try{
                try {
                    val jungleObj = JSONObject(Prefs.with(MyApplication.getInstance()).getString(Constants.KEY_JUNGLE_AUTOCOMPLETE_OBJ, Constants.EMPTY_JSON_OBJECT))
                    if(JungleApisImpl.checkIfJungleApiEnabled(jungleObj)){
                        throw Exception()
                    }
                    if (!isGoogleCachingEnabled()) {
                        throw Exception()
                    }
                    val response = withContext(Dispatchers.IO) {
                        try { RestClient.getMapsCachingService().getAutocompleteData(input, arr[0].toDouble(), arr[1].toDouble(),
                                JUNGOO_APP_PRODUCT_ID, getUserId()) } catch (e: Exception) { null }
                    }
                    val responseStr = String((response!!.body as TypedByteArray).bytes)
                    val jsonObject = JSONObject(responseStr)
                    val responseCached = jsonObject.getJSONArray("data").toString()
                    predictions = gson.fromJson(responseCached, object : TypeToken<MutableList<Prediction>>() {}.type)
                } catch (e: Exception) {
                    val response: JungleApisImpl.AutoCompleteResult? = withContext(Dispatchers.IO) {
                        try { JungleApisImpl.getAutoCompletePredictions(input, sessiontoken, components, location, radius) } catch (e: Exception) { null }
                    }
                    if(response != null){
                        predictions = response.placesAutocompleteResponse.predictions

                        try{if(isGoogleCachingEnabled() && !response.junglePassed
                                && predictions != null && predictions.size > 0) {
                            val param = InsertAutocomplete(JUNGOO_APP_PRODUCT_ID, TYPE_AUTO_COMPLETE, input, getUserId(),
                                    arr[0].toDouble(), arr[1].toDouble(), response.placesAutocompleteResponse)
                            insertPlaceAutocompleteCache(param)
                        }} catch(e1:Exception){}
                    }

                }
                callback.onAutocompletePredictionsReceived(predictions!!)
            } catch (e: Exception) {
                callback.onAutocompleteError()
            }

        }
    }


    //Ai for finding place details by place Id
    fun getPlaceById(placeId:String, placeAddress:String, latLng: LatLng, sessiontoken:String, callback: PlaceDetailCallback): Job{
        return GlobalScope.launch(Dispatchers.Main){
            var placesResponse: PlaceDetailsResponse? = null
            try {
                try{
                    val jungleObj = JSONObject(Prefs.with(MyApplication.getInstance()).getString(Constants.KEY_JUNGLE_AUTOCOMPLETE_OBJ, Constants.EMPTY_JSON_OBJECT))
                    if(JungleApisImpl.checkIfJungleApiEnabled(jungleObj)){
                        throw Exception()
                    }
                    if (!isGoogleCachingEnabled()) {
                        throw Exception()
                    }
                    val response = withContext(Dispatchers.IO) {
                        try { RestClient.getMapsCachingService().getGeocodingData(placeId, JUNGOO_APP_PRODUCT_ID, getUserId()) } catch (e: Exception) { null }
                    }
                    val responseStr = String((response!!.body as TypedByteArray).bytes)
                    val jsonObject = JSONObject(responseStr)
                    val responseCached = jsonObject.getJSONArray("data").getJSONObject(0).toString()
                    val results: PlaceDetailsResponse = gson.fromJson(responseCached, PlaceDetailsResponse::class.java)
                    placesResponse = results
                } catch(e:Exception){
                    val response:JungleApisImpl.PlaceDetailResult? = withContext(Dispatchers.IO){
                        try { JungleApisImpl.getPlaceById(placeId, latLng, sessiontoken) } catch (e: Exception) { null }
                    }
                    if(response != null) {
                        placesResponse = response.placeDetailsResponse

                        try {
                            if (isGoogleCachingEnabled() && !response.junglePassed
                                    && placesResponse.results != null && placesResponse.results!!.size > 0) {
                                val param = InsertPlaceDetail(JUNGOO_APP_PRODUCT_ID, TYPE_GEOCODING, placeAddress, getUserId(),
                                        placeId, placesResponse)
                                insertPlaceDetailCache(param)
                            }
                        } catch (e1: Exception) {
                        }
                    }
                }
                callback.onPlaceDetailReceived(placesResponse!!)
            } catch (e: Exception) {
                callback.onPlaceDetailError()
            }
        }
    }

    //api for address from LatLng
    fun hitGeocode(latLng: LatLng, source:String, callback: GeocodeCachingCallback): Job {
        return GlobalScope.launch(Dispatchers.Main) {
            var address: GoogleGeocodeResponse? = null
            var singleAddress: String? = null
            try {
                val jungleObj = JSONObject(Prefs.with(MyApplication.getInstance()).getString(Constants.KEY_JUNGLE_GEOCODE_OBJ, Constants.EMPTY_JSON_OBJECT))
                if(JungleApisImpl.checkIfJungleApiEnabled(jungleObj)){
                    throw Exception()
                }
                if(!isGoogleCachingEnabled()){
                    throw Exception()
                }
                val response = withContext(Dispatchers.IO) {
                    try {RestClient.getMapsCachingService().getReverseGeocode(latLng.latitude, latLng.longitude,
                            JUNGOO_APP_PRODUCT_ID, getUserId()) } catch (e: Exception) {null}
                }
                val responseStr = String((response!!.body as TypedByteArray).bytes)
                val jsonObject = JSONObject(responseStr)

                val responseCached = jsonObject.getJSONArray("data").getJSONObject(0).getString("json_data")
                val googleGeocodeResponse = gson.fromJson(responseCached, GoogleGeocodeResponse::class.java)
                address = googleGeocodeResponse
            } catch (e: Exception) {
                var language = ""
                language = LocaleHelper.getLanguage(MyApplication.getInstance())
                if (language.equals("hi", ignoreCase = true) || language.equals("hi_in", ignoreCase = true)) {
                    language = "hi"
                }
                val geocodeResult = withContext(Dispatchers.IO) {
                    try { JungleApisImpl.getGeocodeAddress(latLng, language, source)} catch (e: Exception) {null}
                }
                if (geocodeResult != null) {
                    if(geocodeResult.googleGeocodeResponse != null && geocodeResult.googleGeocodeResponse.results != null && geocodeResult.googleGeocodeResponse.results!!.isNotEmpty()){
                        address = geocodeResult.googleGeocodeResponse

                        if(isGoogleCachingEnabled() && !geocodeResult.junglePassed) {
                            val gapiAddress = MapUtils.parseGAPIIAddress(geocodeResult.googleGeocodeResponse)
                            val body = InsertGeocode(JUNGOO_APP_PRODUCT_ID, TYPE_REVERSE_GEOCODING, gapiAddress.searchableAddress, getUserId(),
                                    latLng.latitude, latLng.longitude, geocodeResult.googleGeocodeResponse)
                            insertGeocodeCache(body)
                        }
                    } else if(geocodeResult.singleAddress != null){
                        singleAddress = geocodeResult.singleAddress
                    }
                }
            }
            callback.geocodeAddressFetched(address, singleAddress)
        }
    }


    private const val JUNGOO_APP_PRODUCT_ID = 2
    private const val TYPE_REVERSE_GEOCODING = "reverse_geocoding"
    private const val TYPE_AUTO_COMPLETE = "auto_complete"
    private const val TYPE_GEOCODING = "geocoding"

    private fun insertGeocodeCache(params: InsertGeocode){
        GlobalScope.launch(Dispatchers.IO) {
            try {RestClient.getMapsCachingService().insertGeocode(params)} catch (ignored: Exception) {}
        }
    }
    private fun insertPlaceAutocompleteCache(params: InsertAutocomplete){
        GlobalScope.launch(Dispatchers.IO) {
            try {RestClient.getMapsCachingService().insertAutoComplete(params)} catch (ignored: Exception) {}
        }
    }
    private fun insertPlaceDetailCache(params: InsertPlaceDetail){
        GlobalScope.launch(Dispatchers.IO) {
            try {RestClient.getMapsCachingService().insertPlaceDetail(params)} catch (ignored: Exception) {}
        }
    }
}

interface PlacesCallback{
    fun onAutocompletePredictionsReceived(predictions:MutableList<Prediction>)
    fun onAutocompleteError()
}
interface PlaceDetailCallback{
    fun onPlaceDetailReceived(placeDetailsResponse: PlaceDetailsResponse)
    fun onPlaceDetailError()
}
interface GeocodeCachingCallback{
    fun geocodeAddressFetched(address: GoogleGeocodeResponse?, singleAddress:String?)
}

class InsertAutocomplete(
        @SerializedName(Constants.KEY_PRODUCT_ID)
        val productId:Int,
        @SerializedName(Constants.KEY_TYPE)
        val type:String,
        @SerializedName(Constants.KEY_ADDRESS)
        val address:String,
        @SerializedName(Constants.KEY_USER_ID)
        val userId:String,
        @SerializedName(Constants.KEY_LAT)
        val lat:Double,
        @SerializedName(Constants.KEY_LNG)
        val lng:Double,
        @SerializedName(Constants.KEY_JSONDATA)
        val jsonData: PlacesAutocompleteResponse
)
class InsertPlaceDetail(
        @SerializedName(Constants.KEY_PRODUCT_ID)
        val productId:Int,
        @SerializedName(Constants.KEY_TYPE)
        val type:String,
        @SerializedName(Constants.KEY_ADDRESS)
        val address:String,
        @SerializedName(Constants.KEY_USER_ID)
        val userId:String,
        @SerializedName(Constants.KEY_PLACEID)
        val placeId:String,
        @SerializedName(Constants.KEY_JSONDATA)
        val jsonData:PlaceDetailsResponse
)
class InsertGeocode(
        @SerializedName(Constants.KEY_PRODUCT_ID)
        val productId:Int,
        @SerializedName(Constants.KEY_TYPE)
        val type:String,
        @SerializedName(Constants.KEY_ADDRESS)
        val address:String,
        @SerializedName(Constants.KEY_USER_ID)
        val userId:String,
        @SerializedName(Constants.KEY_LAT)
        val lat:Double,
        @SerializedName(Constants.KEY_LNG)
        val lng:Double,
        @SerializedName(Constants.KEY_JSONDATA)
        val jsonData:GoogleGeocodeResponse
)
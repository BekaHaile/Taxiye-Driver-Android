package product.clicklabs.jugnoo.driver.utils

import android.util.Base64
import product.clicklabs.jugnoo.driver.*
import product.clicklabs.jugnoo.driver.retrofit.RestClient
import product.clicklabs.jugnoo.driver.retrofit.model.SettleUserDebt
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object GoogleRestApis {

    private fun CHANNEL(): String {
        return BuildConfig.FLAVOR + "-android-driver"
    }

    fun getDirections(originLatLng: String, destLatLng: String, sensor: Boolean?,
                      mode: String, alternatives: Boolean?): Response {
        val response:Response
        Log.e(GoogleRestApis::class.java.simpleName, "getDirections hit")
        if (BuildConfig.MAPS_APIS_SIGN) {
            val urlToSign = ("/maps/api/directions/json?" +
                    "origin=" + originLatLng
                    + "&destination=" + destLatLng
                    + "&sensor=" + sensor
                    + "&mode=" + mode
                    + "&alternatives=" + alternatives
                    + "&client=" + BuildConfig.MAPS_CLIENT
                    + "&channel=" + CHANNEL())
            var googleSignature: String? = null
            try {
                googleSignature = generateGoogleSignature(urlToSign)
            } catch (ignored: Exception) {
            }

            response = RestClient.getGoogleApiServices().getDirections(originLatLng, destLatLng,
                    sensor, mode, alternatives, BuildConfig.MAPS_CLIENT, BuildConfig.FLAVOR + "-android-driver", googleSignature)
        } else {
            response = RestClient.getGoogleApiServices().getDirections(originLatLng, destLatLng,
                    sensor, mode, alternatives, BuildConfig.MAPS_BROWSER_KEY)
        }
        if(originLatLng.contains(",")) {
            logGoogleRestAPI(originLatLng.split(",")[0], originLatLng.split(",")[1], API_NAME_DIRECTIONS)
        }
        return response
    }

    fun getDistanceMatrix(originLatLng: String, destLatLng: String, language: String,
                          sensor: Boolean?, alternatives: Boolean?): Response {
        Log.e(GoogleRestApis::class.java.simpleName, "getDistanceMatrix hit")
        val response:Response
        if (BuildConfig.MAPS_APIS_SIGN) {
            val urlToSign = ("/maps/api/distancematrix/json?" +
                    "origins=" + originLatLng
                    + "&destinations=" + destLatLng
                    + "&language=" + language
                    + "&sensor=" + sensor
                    + "&alternatives=" + alternatives
                    + "&client=" + BuildConfig.MAPS_CLIENT
                    + "&channel=" + CHANNEL())
            var googleSignature: String? = null
            try {
                googleSignature = generateGoogleSignature(urlToSign)
            } catch (ignored: Exception) {
            }

            response = RestClient.getGoogleApiServices().getDistanceMatrix(originLatLng, destLatLng, language,
                    sensor, alternatives, BuildConfig.MAPS_CLIENT, BuildConfig.FLAVOR + "-android-driver", googleSignature)
        } else {
            response = RestClient.getGoogleApiServices().getDistanceMatrix(originLatLng, destLatLng, language,
                    sensor, alternatives, BuildConfig.MAPS_BROWSER_KEY)
        }
        if(originLatLng.contains(",")) {
            logGoogleRestAPI(originLatLng.split(",")[0], originLatLng.split(",")[1], API_NAME_DISTANCE_MATRIX)
        }
        return response
    }

    fun geocode(latLng: String, language: String): Response {
        Log.e(GoogleRestApis::class.java.simpleName, "geocode hit")
        val response:Response
        if (BuildConfig.MAPS_APIS_SIGN) {
            val urlToSign = ("/maps/api/geocode/json?" +
                    "latlng=" + latLng
                    + "&language=" + language
                    + "&sensor=" + false
                    + "&client=" + BuildConfig.MAPS_CLIENT
                    + "&channel=" + CHANNEL())
            var googleSignature: String? = null
            try {
                googleSignature = generateGoogleSignature(urlToSign)
            } catch (ignored: Exception) {
            }

            response = RestClient.getGoogleApiServices().geocode(latLng, language, false, BuildConfig.MAPS_CLIENT, BuildConfig.FLAVOR + "-android-driver", googleSignature)
        } else {
            response = RestClient.getGoogleApiServices().geocode(latLng, language, false, BuildConfig.MAPS_BROWSER_KEY)
        }
        if(latLng.contains(",")) {
            logGoogleRestAPI(latLng.split(",")[0], latLng.split(",")[1], API_NAME_GEOCODE)
        }
        return response
    }


    fun getDirectionsWaypoints(strOrigin: String, strDestination: String, strWaypoints: String): Response {
        Log.e(GoogleRestApis::class.java.simpleName, "getDirectionsWaypoints hit")
        val response:Response
        if (BuildConfig.MAPS_APIS_SIGN) {
            val urlToSign = ("/maps/api/directions/json?" +
                    "origin=" + strOrigin
                    + "&destination=" + strDestination
                    + "&waypoints=" + strWaypoints
                    + "&client=" + BuildConfig.MAPS_CLIENT
                    + "&channel=" + CHANNEL())
            var googleSignature: String? = null
            try {
                googleSignature = generateGoogleSignature(urlToSign)
            } catch (ignored: Exception) {
            }


            response = RestClient.getGoogleApiServices().getDirectionsWaypoints(strOrigin, strDestination,
                    strWaypoints, BuildConfig.MAPS_CLIENT, BuildConfig.FLAVOR + "-android-driver", googleSignature)
        } else {
            response = RestClient.getGoogleApiServices().getDirectionsWaypoints(strOrigin, strDestination,
                    strWaypoints, BuildConfig.MAPS_BROWSER_KEY)
        }
        if(strOrigin.contains(",")) {
            logGoogleRestAPI(strOrigin.split(",")[0], strOrigin.split(",")[1], API_NAME_DIRECTIONS)
        }
        return response
    }

    fun snapToRoads(path: String): Response {
        Log.e(GoogleRestApis::class.java.simpleName, "snapToRoads hit")
        val response:Response
        if (BuildConfig.MAPS_APIS_SIGN) {
            val urlToSign = ("/v1/snapToRoads?" +
                    "path=" + path
                    + "&client=" + BuildConfig.MAPS_CLIENT
                    + "&channel=" + CHANNEL())
            var googleSignature: String? = null
            try {
                googleSignature = generateGoogleSignature(urlToSign)
            } catch (ignored: Exception) {
            }


            response = RestClient.getRoadsApiService().snapToRoads(path, BuildConfig.MAPS_CLIENT,
                    BuildConfig.FLAVOR + "-android-driver", googleSignature)
        } else {
            response = RestClient.getRoadsApiService().snapToRoads(path, BuildConfig.MAPS_BROWSER_KEY)
        }
        if(path.contains(",")) {
            val latLng:String = path.split("|")[0]
            logGoogleRestAPI(latLng.split(",")[0], latLng.split(",")[1], API_NAME_SNAP_TO_ROAD)
        }
        return response
    }

    @Throws(NoSuchAlgorithmException::class, InvalidKeyException::class)
    private fun generateGoogleSignature(urlToSign: String): String {

        // Convert the key from 'web safe' base 64 to binary
        var keyString = BuildConfig.MAPS_PRIVATE_KEY
        keyString = keyString.replace('-', '+')
        keyString = keyString.replace('_', '/')
        // Base64 is JDK 1.8 only - older versions may need to use Apache Commons or similar.
        val key = Base64.decode(keyString, Base64.DEFAULT)


        val sha1Key = SecretKeySpec(key, "HmacSHA1")

        // Get an HMAC-SHA1 Mac instance and initialize it with the HMAC-SHA1 key
        val mac = Mac.getInstance("HmacSHA1")
        mac.init(sha1Key)

        // compute the binary signature for the request
        val sigBytes = mac.doFinal(urlToSign.toByteArray())

        // base 64 encode the binary signature
        // Base64 is JDK 1.8 only - older versions may need to use Apache Commons or similar.
        var signature = Base64.encodeToString(sigBytes, Base64.DEFAULT)

        // convert the signature to 'web safe' base 64
        signature = signature.replace('+', '-')
        signature = signature.replace('/', '_')

        return signature
    }



    fun logGoogleRestAPI(latitude: String, longitude: String, apiName: String) {
        if(Prefs.with(MyApplication.getInstance()).getInt(Constants.KEY_DRIVER_GOOGLE_APIS_LOGGING, 0) == 1) {
            val map = hashMapOf<String, String>()
            map[Constants.KEY_ACCESS_TOKEN] = JSONParser.getAccessTokenPair(MyApplication.getInstance()).first
            map[Constants.KEY_LATITUDE] = latitude
            map[Constants.KEY_LONGITUDE] = longitude
            map[Constants.KEY_API_NAME] = apiName
            HomeUtil.putDefaultParams(map)
            RestClient.getApiServices().logGoogleApiHits(map)
        }
    }
    fun logGoogleRestAPIC(latitude: String, longitude: String, apiName: String) {
        if(Prefs.with(MyApplication.getInstance()).getInt(Constants.KEY_DRIVER_GOOGLE_APIS_LOGGING, 0) == 1) {
            val map = hashMapOf<String, String>()
            map[Constants.KEY_ACCESS_TOKEN] = JSONParser.getAccessTokenPair(MyApplication.getInstance()).first
            map[Constants.KEY_LATITUDE] = latitude
            map[Constants.KEY_LONGITUDE] = longitude
            map[Constants.KEY_API_NAME] = apiName
            HomeUtil.putDefaultParams(map)
            RestClient.getApiServices().logGoogleApiHitsC(map, object : Callback<SettleUserDebt> {
                override fun success(t: SettleUserDebt?, response: Response?) {
                }

                override fun failure(error: RetrofitError?) {
                }
            })
        }
    }

    fun getAutoCompletePredictions(input:String, sessiontoken:String, components:String, location:String, radius:String): Response {
        val response:Response = RestClient.getGoogleApiServices().autocompletePredictions(input, sessiontoken, components, location, radius, BuildConfig.MAPS_BROWSER_KEY)
        logGoogleRestAPI("0", "0", API_NAME_AUTOCOMPLETE)
        return response
    }

    fun getPlaceDetails(placeId:String): Response {
        val response:Response
        if (BuildConfig.MAPS_APIS_SIGN) {
            val urlToSign = ("/maps/api/geocode/json?" +
                    "place_id=" + placeId
                    + "&client=" + BuildConfig.MAPS_CLIENT
                    + "&channel=" + CHANNEL())
            var googleSignature: String? = null
            try {
                googleSignature = generateGoogleSignature(urlToSign)
            } catch (ignored: Exception) {
            }


            response = RestClient.getGoogleApiServices().placeDetails(placeId, BuildConfig.MAPS_CLIENT, CHANNEL(), googleSignature)
        } else {
            response = RestClient.getGoogleApiServices().placeDetails(placeId, BuildConfig.MAPS_BROWSER_KEY)
        }

        logGoogleRestAPI("0", "0", API_NAME_PLACES)
        return response
    }

    const val API_NAME_DIRECTIONS = "directions"
    const val API_NAME_DISTANCE_MATRIX = "distance_matrix"
    const val API_NAME_GEOCODE = "geocode"
    const val API_NAME_SNAP_TO_ROAD = "snap_to_road"
    const val API_NAME_PLACES = "places"
    const val API_NAME_AUTOCOMPLETE = "autocomplete"

}

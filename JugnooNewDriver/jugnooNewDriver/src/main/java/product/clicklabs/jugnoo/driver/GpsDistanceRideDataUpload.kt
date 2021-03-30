package product.clicklabs.jugnoo.driver

import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.SystemClock
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags
import product.clicklabs.jugnoo.driver.datastructure.CurrentPathItem
import product.clicklabs.jugnoo.driver.datastructure.EngagementStatus
import product.clicklabs.jugnoo.driver.datastructure.SPLabels
import product.clicklabs.jugnoo.driver.retrofit.RestClient
import product.clicklabs.jugnoo.driver.utils.*
import retrofit.mime.TypedByteArray
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

object GpsDistanceRideDataUpload {

    fun uploadInRidePath(context: Context, callbackUploadPath: CallbackUploadPath?){
        GlobalScope.launch(Dispatchers.IO){
            uploadInRidePathInternal(context)
            launch(Dispatchers.Main){
                callbackUploadPath?.pathUploaded()
            }
        }
    }


    private fun uploadInRidePathInternal(context: Context) {
        try {
            val pathFilterDistance = context.resources.getInteger(R.integer.path_filter_distance).toDouble()
            val validCurrentPathItems = ArrayList<CurrentPathItem>()
            validCurrentPathItems.addAll(Database2.getInstance(context).currentPathItemsToUpload)

            if (validCurrentPathItems.size > 0) {

                val locationDataArr = JSONArray()

                var pathSource: LatLng? = null

                for (i in validCurrentPathItems.indices) {
                    val currentPathItem = validCurrentPathItems[i]
                    var nextSourceLatLng: LatLng? = null
                    if (1 == currentPathItem.googlePath) {
                    } else {
                        try {
                            if (pathSource == null) {
                                pathSource = currentPathItem.sLatLng
                                nextSourceLatLng = currentPathItem.sLatLng
                            }
                            var addPath = false
                            if (i < validCurrentPathItems.size - 1) {
                                if (MapUtils.distance(currentPathItem.dLatLng, validCurrentPathItems[i + 1].sLatLng) < 2) {
                                    if (MapUtils.distance(pathSource, currentPathItem.dLatLng) < pathFilterDistance) {
                                        //dont add
                                        addPath = false
                                    } else {
                                        //add pathSource, currentPathItem.dLatLng
                                        nextSourceLatLng = currentPathItem.dLatLng
                                        addPath = true
                                    }
                                } else {
                                    //add pathSource, currentPathItem.dLatLng
                                    nextSourceLatLng = validCurrentPathItems[i + 1].sLatLng
                                    addPath = true
                                }
                            } else {
                                //add pathSource, currentPathItem.dLatLng
                                nextSourceLatLng = currentPathItem.dLatLng
                                addPath = true
                            }

                            if (addPath) {
                                val locationData = JSONObject()
                                locationData.put("location_id", currentPathItem.id)
                                locationData.put("source_latitude", pathSource!!.latitude)
                                locationData.put("source_longitude", pathSource.longitude)
                                locationData.put("destination_latitude", currentPathItem.dLatLng.latitude)
                                locationData.put("destination_longitude", currentPathItem.dLatLng.longitude)
                                locationDataArr.put(locationData)
                                pathSource = nextSourceLatLng
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }


                val accessToken = JSONParser.getAccessTokenPair(context).first
                val responseTime = System.currentTimeMillis()
                if (!"".equals(accessToken, ignoreCase = true)) {
                    val nameValuePairs = HashMap<String, String>()
                    nameValuePairs[Constants.KEY_ACCESS_TOKEN] = accessToken

                    val customerInfos = Data.getAssignedCustomerInfosListForEngagedStatus()
                    val engagementsJsonArray = JSONArray()
                    for (customerInfo in customerInfos) {
                        try {
                            if (customerInfo.status == EngagementStatus.STARTED.getOrdinal()) {
                                engagementsJsonArray.put(customerInfo.engagementId)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                    if (engagementsJsonArray.length() > 0) {
                        if (locationDataArr.length() > 0) {
                            nameValuePairs["engagement_ids"] = engagementsJsonArray.toString()
                            nameValuePairs["locations"] = locationDataArr.toString()
                            Log.i("GpsDistanceRideDataUpload", "logOngoingRidePath params=$nameValuePairs")
                            HomeUtil.putDefaultParams(nameValuePairs)

                            val response = RestClient.getApiServices().logOngoingRidePath(nameValuePairs)
                            val result = String((response.body as TypedByteArray).bytes)
                            try {
                                val jObj = JSONObject(result)
                                if (jObj.has(Constants.KEY_FLAG)) {
                                    val flag = jObj.getInt(Constants.KEY_FLAG)
                                    if (ApiResponseFlags.RIDE_PATH_RECEIVED.getOrdinal() == flag) {
                                        val rowIds = ArrayList<Long>()
                                        for (currentPathItem in validCurrentPathItems) {
                                            rowIds.add(currentPathItem.id)
                                        }
                                        FlurryEventLogger.logResponseTime(context, System.currentTimeMillis() - responseTime, FlurryEventNames.PATH_UPLOAD_RESPONSE)
                                        Database2.getInstance(context).updateCurrentPathItemAcknowledgedForArray(rowIds)
                                        if (HomeActivity.appInterruptHandler != null) {
                                            HomeActivity.appInterruptHandler.addPathNew(validCurrentPathItems)
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                        }

                    }
                }
            }

            val meteringState = Database2.getInstance(context).metringState

            if (Database2.ON.equals(meteringState, ignoreCase = true)) {
                if (!Utils.isServiceRunning(context, MeteringService::class.java)) {
                    context.startService(Intent(context, MeteringService::class.java))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun updateInRideData(context: Context, location: Location?, callbackUploadPath: CallbackUploadPath?){
        GlobalScope.launch(Dispatchers.IO){
            updateInRideDataInternal(context, location)
            launch(Dispatchers.Main){
                callbackUploadPath?.pathUploaded()
            }
        }
    }

    private fun updateInRideDataInternal(context: Context, location: Location?) {
        try {
            val responseTime = System.currentTimeMillis()
            val customerInfos = Data.getAssignedCustomerInfosListForEngagedStatus()
            val accessToken = JSONParser.getAccessTokenPair(context).first
            if (customerInfos.size > 0
                    && location != null
                    && !"".equals(accessToken, ignoreCase = true)) {
                val totalDistanceInKm = Math.abs(GpsDistanceCalculator.getTotalDistanceFromSP(context) / 1000.0)
                val rideTimeSeconds = (SystemClock.elapsedRealtime() - GpsDistanceCalculator.getStartTimeFromSP(context)) / 1000
                val rideTimeMinutes = Math.ceil((rideTimeSeconds / 60).toDouble())
                val waitTimeSeconds = GpsDistanceCalculator.getWaitTimeFromSP(context) / 1000
                val waitTimeMinutes = Math.ceil((waitTimeSeconds / 60).toDouble())
                var lastLogId = Integer.parseInt(Prefs.with(context).getString(SPLabels.DISTANCE_RESET_LOG_ID, "" + 0))

                val decimalFormat = DecimalFormat("#.##", DecimalFormatSymbols(Locale.ENGLISH))
                val decimalFormatNoDecimal = DecimalFormat("#", DecimalFormatSymbols(Locale.ENGLISH))


                val params = HashMap<String, String>()
                params[Constants.KEY_ACCESS_TOKEN] = accessToken
                params[Constants.KEY_ENGAGEMENT_ID] = customerInfos[0].engagementId.toString()
                params["current_latitude"] = location.latitude.toString()
                params["current_longitude"] = location.longitude.toString()
                params["distance_travelled"] = decimalFormat.format(totalDistanceInKm)
                params["ride_time"] = decimalFormatNoDecimal.format(rideTimeMinutes)
                params["wait_time"] = decimalFormatNoDecimal.format(waitTimeMinutes)
                params["last_log_id"] = "" + lastLogId
                HomeUtil.putDefaultParams(params)

                val response = RestClient.getApiServices().updateInRideDataRetro(params)
                if (response != null) {
                    val jsonString = String((response.body as TypedByteArray).bytes)
                    val jObj = JSONObject(jsonString)
                    val flag = jObj.optInt("flag", ApiResponseFlags.ACTION_COMPLETE.getOrdinal())
                    lastLogId = jObj.optInt("last_log_id", 0)
                    updateWalletBalance(jObj)
                    Prefs.with(context).save(SPLabels.DISTANCE_RESET_LOG_ID, "" + lastLogId)
                    if (ApiResponseFlags.DISTANCE_RESET.getOrdinal() == flag) {
                        try {
                            val distance = jObj.getDouble("total_distance") * 1000
                            val rideTime = jObj.optLong("ride_time", 0) * 60000
                            val waitTime = jObj.optLong("wait_time", 0) * 60000
                            MeteringService.gpsInstance(context).updateDistanceInCaseOfReset(distance, rideTime, waitTime)
                            FlurryEventLogger.logResponseTime(context, System.currentTimeMillis() - responseTime, FlurryEventNames.UPDATE_IN_RIDE_DATA_RESPONSE)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }
                }
            }
            //            String meteringState = Database2.getInstance(context).getMetringState();
            //            if(!Database2.ON.equalsIgnoreCase(meteringState)){
            //            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun updateWalletBalance(jsonObject: JSONObject) {
        try {
            val jsonArray = jsonObject.getJSONArray("balance_array")
            for (i in 0 until jsonArray.length()) {
                val engagementId = jsonArray.getJSONObject(i).getInt("engagement_id")
                val walletBalance = jsonArray.getJSONObject(i).getInt("wallet_balance").toDouble()
                val customerInfo = Data.getCustomerInfo(engagementId.toString())
                if (customerInfo != null) {
                    if (walletBalance > -1) {
                        customerInfo.setJugnooBalance(walletBalance)
                    }
                }
            }
        } catch (e: Exception) {}

    }




}

interface CallbackUploadPath{
    fun pathUploaded()
}
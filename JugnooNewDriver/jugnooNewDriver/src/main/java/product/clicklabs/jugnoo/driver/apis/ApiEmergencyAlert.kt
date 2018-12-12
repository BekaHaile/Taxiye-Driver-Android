package product.clicklabs.jugnoo.driver.apis

import android.app.Activity
import android.location.Location
import org.json.JSONObject
import product.clicklabs.jugnoo.driver.*
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags
import product.clicklabs.jugnoo.driver.datastructure.PendingCall
import product.clicklabs.jugnoo.driver.retrofit.RestClient
import product.clicklabs.jugnoo.driver.retrofit.model.SettleUserDebt
import product.clicklabs.jugnoo.driver.utils.Log
import retrofit.RetrofitError
import retrofit.client.Response
import retrofit.mime.TypedByteArray
import java.util.*

/**
 * API for raising an emergency alert to customer support
 * (Fire and forget kind,
 * but if failed it will be
 * registered in pending calls table and will be called when net is available)
 *
 * Created by shankar on 2/22/16.
 */
class ApiEmergencyAlert(private val activity: Activity, private val callback: Callback) {

    private val TAG = ApiEmergencyAlert::class.java.simpleName

    fun raiseEmergencyAlertAPI(myLocation: Location?, alertType: String, customerId: String, engagementId: String) {
        try {
            if (MyApplication.getInstance().isOnline) {
                val params = HashMap<String, String>()
                params[Constants.KEY_ACCESS_TOKEN] = Data.userData.accessToken
                params[Constants.KEY_CUSTOMER_ID] = customerId
                params[Constants.KEY_ENGAGEMENT_ID] = engagementId
                params[Constants.KEY_ALERT_TYPE] = alertType

                if (myLocation != null) {
                    params[Constants.KEY_LATITUDE] = myLocation.latitude.toString()
                    params[Constants.KEY_LONGITUDE] = myLocation.longitude.toString()
                } else {
                    params[Constants.KEY_LATITUDE] = callback.savedLatitude.toString()
                    params[Constants.KEY_LONGITUDE] = callback.savedLongitude.toString()
                }

                HomeUtil.putDefaultParams(params)
                RestClient.getApiServices().emergencyAlert(params, object : retrofit.Callback<SettleUserDebt> {
                    override fun success(settleUserDebt: SettleUserDebt, response: Response) {
                        val responseStr = String((response.body as TypedByteArray).bytes)
                        Log.i(TAG, "emergencyAlert response = $responseStr")
                        try {
                            val jObj = JSONObject(responseStr)
                            val flag = jObj.optInt(Constants.KEY_FLAG, ApiResponseFlags.ACTION_COMPLETE.getOrdinal())
                            if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                                callback.onSuccess()
                            }
                        } catch (exception: Exception) {
                            exception.printStackTrace()
                        }

                    }

                    override fun failure(error: RetrofitError) {
                        Log.e(TAG, "emergencyAlert error" + error.toString())
                        Database2.getInstance(activity).insertPendingAPICall(activity,
                                PendingCall.EMERGENCY_ALERT.path, params)
                        callback.onFailure()
                    }
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    interface Callback {
        val savedLatitude: Double
        val savedLongitude: Double
        fun onSuccess()
        fun onFailure()
    }

}

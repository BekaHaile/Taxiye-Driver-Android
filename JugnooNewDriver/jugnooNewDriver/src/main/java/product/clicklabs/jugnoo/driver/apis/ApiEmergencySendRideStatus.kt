package product.clicklabs.jugnoo.driver.apis

import android.app.Activity
import android.view.View
import org.json.JSONArray
import org.json.JSONObject
import product.clicklabs.jugnoo.driver.*
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags
import product.clicklabs.jugnoo.driver.emergency.EmergencyActivity
import product.clicklabs.jugnoo.driver.emergency.models.ContactBean
import product.clicklabs.jugnoo.driver.retrofit.RestClient
import product.clicklabs.jugnoo.driver.retrofit.model.SettleUserDebt
import product.clicklabs.jugnoo.driver.utils.DialogPopup
import product.clicklabs.jugnoo.driver.utils.Log
import retrofit.RetrofitError
import retrofit.client.Response
import retrofit.mime.TypedByteArray
import java.util.*

/**
 * For fetching emergency contacts list from server
 *
 * Created by shankar on 3/2/16.
 */
class ApiEmergencySendRideStatus(private val activity: Activity, private val callback: Callback) {

    private val TAG = ApiEmergencySendRideStatus::class.java.simpleName

    fun emergencySendRideStatusMessage(engagementId: String, selectedContacts: ArrayList<ContactBean>) {
        try {
            if (MyApplication.getInstance().isOnline) {

                DialogPopup.showLoadingDialog(activity, activity.resources.getString(R.string.loading))

                val params = HashMap<String, String>()
                params[Constants.KEY_ACCESS_TOKEN] = Data.userData.accessToken
                params[Constants.KEY_ENGAGEMENT_ID] = engagementId

                val jsonArray = JSONArray()
                for (contact in selectedContacts) {
                    if (jsonArray.length() < EmergencyActivity.MAX_EMERGENCY_CONTACTS_TO_SEND_RIDE_STATUS) {
                        val jsonObject = JSONObject()
                        jsonObject.put(Constants.KEY_PHONE_NO, contact.phoneNo)
                        jsonObject.put(Constants.KEY_COUNTRY_CODE, contact.countryCode)
                        jsonArray.put(jsonObject)
                    }
                }
                params[Constants.KEY_PHONE_NO] = jsonArray.toString()

                Log.i("params", "=" + params.toString())

                HomeUtil.putDefaultParams(params)
                RestClient.getApiServices().emergencySendRideStatusMessage(params, object : retrofit.Callback<SettleUserDebt> {
                    override fun success(settleUserDebt: SettleUserDebt, response: Response) {
                        val responseStr = String((response.body as TypedByteArray).bytes)
                        Log.i(TAG, "emergencySendRideStatusMessage response = $responseStr")
                        DialogPopup.dismissLoadingDialog()
                        try {
                            val jObj = JSONObject(responseStr)
                            val message = JSONParser.getServerMessage(jObj)
                            val flag = jObj.getInt("flag")
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag, null)) {
                                if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                                    callback.onSuccess(message)
                                } else {
                                    DialogPopup.alertPopup(activity, "", message)
                                }
                            }
                        } catch (exception: Exception) {
                            exception.printStackTrace()
                            retryDialog(activity.getString(R.string.some_error_occured))
                        }

                        DialogPopup.dismissLoadingDialog()
                    }

                    override fun failure(error: RetrofitError) {
                        Log.e(TAG, "emergencySendRideStatusMessage error" + error.toString())
                        DialogPopup.dismissLoadingDialog()
                        retryDialog(activity.getString(R.string.connection_lost))
                        callback.onFailure()
                    }
                })
            } else {
                retryDialog(activity.getString(R.string.check_internet_message))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun retryDialog(dialogErrorType: String) {
        DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", dialogErrorType,
                activity.getString(R.string.retry),
                activity.getString(R.string.cancel),
                { v -> callback.onRetry(v) }, { v -> callback.onNoRetry(v) }, false, false)
    }


    interface Callback {
        fun onSuccess(message: String)
        fun onFailure()
        fun onRetry(view: View)
        fun onNoRetry(view: View)
    }

}

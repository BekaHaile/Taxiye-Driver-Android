package product.clicklabs.jugnoo.driver.apis

import android.app.Activity
import android.view.View

import org.json.JSONObject

import java.util.HashMap

import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.Data
import product.clicklabs.jugnoo.driver.HomeUtil
import product.clicklabs.jugnoo.driver.JSONParser
import product.clicklabs.jugnoo.driver.MyApplication
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.SplashNewActivity
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags
import product.clicklabs.jugnoo.driver.retrofit.RestClient
import product.clicklabs.jugnoo.driver.retrofit.model.SettleUserDebt
import product.clicklabs.jugnoo.driver.utils.DialogPopup
import product.clicklabs.jugnoo.driver.utils.Log
import product.clicklabs.jugnoo.driver.utils.Prefs
import retrofit.RetrofitError
import retrofit.client.Response
import retrofit.mime.TypedByteArray

/**
 * For fetching emergency contacts list from server
 *
 * Created by shankar on 3/2/16.
 */
class ApiEmergencyDisable(private val activity: Activity, private val callback: Callback) {

    private val TAG = ApiEmergencyDisable::class.java.simpleName

    fun emergencyDisable(engagementId: String) {
        try {
            if (MyApplication.getInstance().isOnline) {

                DialogPopup.showLoadingDialog(activity, activity.resources.getString(R.string.loading))

                val params = HashMap<String, String>()
                params[Constants.KEY_ACCESS_TOKEN] = Data.userData.accessToken
                params[Constants.KEY_ENGAGEMENT_ID] = engagementId
                Log.i("params", "=" + params.toString())

                HomeUtil.putDefaultParams(params)
                RestClient.getApiServices().emergencyDisable(params, object : retrofit.Callback<SettleUserDebt> {
                    override fun success(settleUserDebt: SettleUserDebt, response: Response) {
                        val responseStr = String((response.body as TypedByteArray).bytes)
                        Log.i(TAG, "emergencyContactsList response = $responseStr")
                        DialogPopup.dismissLoadingDialog()
                        try {
                            val jObj = JSONObject(responseStr)
                            val message = JSONParser.getServerMessage(jObj)
                            val flag = jObj.getInt("flag")
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag, null)) {
                                if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                                    Prefs.with(activity).save(Constants.SP_EMERGENCY_MODE_ENABLED, 0)
                                    callback.onSuccess()
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
                        Log.e(TAG, "emergencyContactsList error" + error.toString())
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
        fun onSuccess()
        fun onFailure()
        fun onRetry(view: View)
        fun onNoRetry(view: View)
    }

}

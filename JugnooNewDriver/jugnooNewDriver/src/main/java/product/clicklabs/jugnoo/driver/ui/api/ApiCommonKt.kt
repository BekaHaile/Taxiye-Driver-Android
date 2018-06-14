package product.clicklabs.jugnoo.driver.ui.api

import android.app.Activity
import product.clicklabs.jugnoo.driver.*
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags
import product.clicklabs.jugnoo.driver.retrofit.RestClient
import product.clicklabs.jugnoo.driver.retrofit.model.CityResponse
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse
import product.clicklabs.jugnoo.driver.ui.models.DriverLanguageResponse
import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponseKotlin
import product.clicklabs.jugnoo.driver.utils.AppStatus
import product.clicklabs.jugnoo.driver.utils.DialogPopup
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import retrofit.mime.MultipartTypedOutput
import java.lang.ref.WeakReference

class ApiCommonKt<T : FeedCommonResponseKotlin>(

        activityM: Activity,
        private val showLoader: Boolean = true,
        private val putDefaultParams: Boolean = true,
        private var putAccessToken: Boolean = false,
        private var checkForActionComplete: Boolean = false,
        val isCancelled: Boolean = false) {

    private val activity:WeakReference<Activity> = WeakReference(activityM)


    private var apiCommonCallback: APICommonCallbackKotlin<T>? = null
    private lateinit var params: HashMap<String, String>
    private lateinit var multipartTypedOutput: MultipartTypedOutput
    private lateinit var apiName: ApiName
    private var isInProgress = false


    fun execute(params: HashMap<String, String>? = null, apiName: ApiName,
                apiCommonCallback: APICommonCallbackKotlin<T>? = null) {
        this.apiCommonCallback = apiCommonCallback
        this.apiName = apiName

        this.params = params ?: HashMap()
        hitApi(false)

    }

    fun execute(multipartTypedOutput: MultipartTypedOutput? = null, apiName: ApiName,
                apiCommonCallback: APICommonCallbackKotlin<T>? = null) {
        this.apiCommonCallback = apiCommonCallback
        this.apiName = apiName

        this.multipartTypedOutput = multipartTypedOutput ?: MultipartTypedOutput()
        hitApi(true)
    }

    private fun hitApi(isMultiPartRequest: Boolean) {

        if (!AppStatus.getInstance(activity.get()).isOnline(activity.get())) {
            apiCommonCallback?.onFinish()
            if (apiCommonCallback?.onNotConnected() != true) {
                DialogPopup.alertPopup(activity.get(), "", Data.CHECK_INTERNET_MSG)
            }
            return
        }

       val callback = object : Callback<T> {
            override fun success(feedCommonResponse: T, response: Response?) {
                isInProgress = false
                if (showLoader) {
                    DialogPopup.dismissLoadingDialog()
                }


                if (isCancelled || activity.get()==null || activity.get()!!.isFinishing) return

                try {
                    if (!isTrivialError(feedCommonResponse.flag) && (!checkForActionComplete
                                    || feedCommonResponse.flag == ApiResponseFlags.ACTION_COMPLETE.getOrdinal())) {

                        apiCommonCallback?.onFinish()
                        apiCommonCallback?.onSuccess(feedCommonResponse, feedCommonResponse.serverMessage(), feedCommonResponse.flag)


                    } else if (feedCommonResponse.flag == ApiResponseFlags.INVALID_ACCESS_TOKEN.getOrdinal()) {
                        apiCommonCallback?.onFinish()
                        HomeActivity.logoutUser(activity.get(), null)
                    } else {
                        apiCommonCallback?.onFinish()
                        if (apiCommonCallback?.onError(feedCommonResponse, feedCommonResponse.serverMessage(), feedCommonResponse.flag) != true) {
                            retryDialog(feedCommonResponse.serverMessage())
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    apiCommonCallback?.onFinish()

                    if (apiCommonCallback?.onException(e) != true) {
                        retryDialog(Data.CHECK_INTERNET_MSG)
                    }
                }
            }

            override fun failure(error: RetrofitError?) {
                isInProgress = false

                if (showLoader) {
                    DialogPopup.dismissLoadingDialog()
                }

                 if (isCancelled || activity.get()==null || activity.get()!!.isFinishing) return


                error?.printStackTrace()
                apiCommonCallback?.onFinish()
                if (apiCommonCallback?.onFailure(error) != true) {
                    retryDialog(Data.CHECK_INTERNET_MSG)
                }
            }
        }

        if (putDefaultParams) {
            if (isMultiPartRequest) {
                HomeUtil.putDefaultParams(multipartTypedOutput)
            } else {
                HomeUtil.putDefaultParams(params)
            }
        }

        if (putAccessToken) {
            params[Constants.KEY_ACCESS_TOKEN] = Data.userData.accessToken
        }

        if (showLoader) {
            DialogPopup.showLoadingDialog(activity.get(), activity.get()!!.resources.getString(R.string.loading))
        }

        isInProgress = true

        // make api hit
        when (apiName) {

            ApiName.GENERATE_OTP -> RestClient.getApiServices().generateOtpK(params, callback as Callback<RegisterScreenResponse>)
            ApiName.GET_CITIES -> RestClient.getApiServices().getCityRetro(params, BuildConfig.CITIES_PASSWORD, callback as Callback<CityResponse>)
            ApiName.GET_LANGUAGES -> RestClient.getApiServices().fetchLanguageListKotlin(params, callback as Callback<DriverLanguageResponse>)
            ApiName.REGISTER_DRIVER ->  RestClient.getApiServices().updateDriverInfo(params, callback as Callback<RegisterScreenResponse> )
            else -> throw IllegalArgumentException("API Type not declared")
        }
    }

    fun isTrivialError(flag: Int): Boolean {
        return (flag == ApiResponseFlags.INVALID_ACCESS_TOKEN.getOrdinal() || flag == ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal()
                || flag == ApiResponseFlags.SHOW_MESSAGE.getOrdinal())
    }

    private fun retryDialog(message: String) {
        DialogPopup.alertPopupWithListener(activity.get(), "", message) { apiCommonCallback?.onDialogClick() }
    }

}
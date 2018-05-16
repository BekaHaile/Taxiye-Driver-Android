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

class ApiCommonKt<T : FeedCommonResponseKotlin>(

        private val activity: Activity,
        private val showLoader: Boolean = true,
        private val putDefaultParams: Boolean = true,
        private var putAccessToken: Boolean = false,
        private var checkForActionComplete: Boolean = true,
        val isCancelled: Boolean = false) {

    private lateinit var callback: Callback<T>
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

        if (!AppStatus.getInstance(activity).isOnline(activity)) {
            apiCommonCallback?.onFinish()
            if (apiCommonCallback?.onNotConnected() != true) {
                DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG)
            }
            return
        }

        callback = object : Callback<T> {
            override fun success(feedCommonResponse: T, response: Response?) {
                isInProgress = false
                if (showLoader) {
                    DialogPopup.dismissLoadingDialog()
                }

                if (isCancelled) return

                try {
                    if (!isTrivialError(feedCommonResponse.flag) && (!checkForActionComplete
                                    || feedCommonResponse.flag == ApiResponseFlags.ACTION_COMPLETE.getOrdinal())) {

                        apiCommonCallback?.onFinish()
                        apiCommonCallback?.onSuccess(feedCommonResponse, feedCommonResponse.message, feedCommonResponse.flag)


                    } else if (feedCommonResponse.flag == ApiResponseFlags.INVALID_ACCESS_TOKEN.getOrdinal()) {
                        apiCommonCallback?.onFinish()
                        HomeActivity.logoutUser(activity, null)
                    } else {
                        apiCommonCallback?.onFinish()
                        if (apiCommonCallback?.onError(feedCommonResponse, feedCommonResponse.message, feedCommonResponse.flag) != true) {
                            retryDialog(feedCommonResponse.message)
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

                if (isCancelled) return

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
            DialogPopup.showLoadingDialog(activity, activity.resources.getString(R.string.loading))
        }

        isInProgress = true

        // make api hit
        when (apiName) {

            ApiName.GENERATE_OTP -> RestClient.getApiServices().generateOtpK(params, callback as Callback<RegisterScreenResponse>)
            ApiName.GET_CITIES -> RestClient.getApiServices().getCityRetro(params, BuildConfig.CITIES_PASSWORD, callback as Callback<CityResponse>)
            ApiName.GET_LANGUAGES -> RestClient.getApiServices().fetchLanguageListKotlin(params, callback as Callback<DriverLanguageResponse>)
            else -> throw IllegalArgumentException("API Type not declared")
        }
    }

    fun isTrivialError(flag: Int): Boolean {
        return (flag == ApiResponseFlags.INVALID_ACCESS_TOKEN.getOrdinal() || flag == ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal()
                || flag == ApiResponseFlags.SHOW_MESSAGE.getOrdinal())
    }

    private fun retryDialog(message: String) {
        DialogPopup.alertPopupWithListener(activity, "", message) { apiCommonCallback?.onDialogClick() }
    }

}
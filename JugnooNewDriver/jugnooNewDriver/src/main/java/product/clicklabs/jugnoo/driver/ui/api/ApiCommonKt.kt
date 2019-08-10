package product.clicklabs.jugnoo.driver.ui.api

import android.app.Activity
import android.text.TextUtils
import product.clicklabs.jugnoo.driver.*
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags
import product.clicklabs.jugnoo.driver.retrofit.RestClient
import product.clicklabs.jugnoo.driver.retrofit.model.ReferralsInfoResponse
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse
import product.clicklabs.jugnoo.driver.retrofit.model.TollDataResponse
import product.clicklabs.jugnoo.driver.retrofit.model.TractionResponse
import product.clicklabs.jugnoo.driver.stripe.model.StripeCardResponse
import product.clicklabs.jugnoo.driver.stripe.model.WalletModelResponse
import product.clicklabs.jugnoo.driver.ui.models.*
import product.clicklabs.jugnoo.driver.ui.popups.DriverVehicleServiceTypePopup
import product.clicklabs.jugnoo.driver.utils.AppStatus
import product.clicklabs.jugnoo.driver.utils.DialogPopup
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import retrofit.mime.MultipartTypedOutput
import retrofit.mime.TypedString
import java.lang.ref.WeakReference


class ApiCommonKt <T : FeedCommonResponseKotlin> @JvmOverloads constructor(

        activityM: Activity,
        private val showLoader: Boolean = true,
        private val putDefaultParams: Boolean = true,
        private var putAccessToken: Boolean = false,
        private var checkForActionComplete: Boolean = false,
        private var successFlag: Int? = null,
        val isCancelled: Boolean = false) {

    private val activity:WeakReference<Activity> = WeakReference(activityM)


    private var apiCommonCallback: APICommonCallbackKotlin<T>? = null
    private lateinit var params: HashMap<String, String>
    private lateinit var multipartTypedOutput: MultipartTypedOutput
    private lateinit var bodyParams: Any
    private lateinit var apiName: ApiName
    private var isInProgress = false


    fun execute(params: HashMap<String, String>? = null, apiName: ApiName,
                apiCommonCallback: APICommonCallbackKotlin<T>? = null) {
        this.apiCommonCallback = apiCommonCallback
        this.apiName = apiName

        this.params = params ?: HashMap()
        hitApi()

    }

    fun execute(multipartTypedOutput: MultipartTypedOutput? = null, apiName: ApiName,
                apiCommonCallback: APICommonCallbackKotlin<T>? = null) {
        this.apiCommonCallback = apiCommonCallback
        this.apiName = apiName
        this.multipartTypedOutput = multipartTypedOutput ?: MultipartTypedOutput()
        hitApi()
    }


    fun <S:Any> execute(bodyParams: S , apiName: ApiName,
                apiCommonCallback: APICommonCallbackKotlin<T>? = null) {
        this.apiCommonCallback = apiCommonCallback
        this.apiName = apiName
        this.bodyParams = bodyParams
        this.params = HashMap()//for putting default params
        hitApi()
    }

    private  fun  hitApi() {

        if (!AppStatus.getInstance(activity.get()).isOnline(activity.get())) {
            apiCommonCallback?.onFinish()
            if (apiCommonCallback?.onNotConnected() != true) {
                retryDialog(Data.CHECK_INTERNET_MSG)
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
                    if (!isTrivialError(feedCommonResponse.flag) &&
                        (!checkForActionComplete || feedCommonResponse.flag == ApiResponseFlags.ACTION_COMPLETE.getOrdinal())
                          && (successFlag==null || successFlag==feedCommonResponse.flag)) {

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
                        retryDialog(Data.SERVER_ERROR_MSG)
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
                    retryDialog(Data.SERVER_NOT_RESOPNDING_MSG)
                }
            }
        }

        if (putDefaultParams) {
            if (::multipartTypedOutput.isInitialized) {
                HomeUtil.putDefaultParams(multipartTypedOutput)
            }
            if(::params.isInitialized) {
                HomeUtil.putDefaultParams(params)
            }
        }

        if (putAccessToken && Data.userData!=null && !TextUtils.isEmpty(Data.userData.accessToken)) {

            if (::multipartTypedOutput.isInitialized) {
             multipartTypedOutput.addPart(Constants.KEY_ACCESS_TOKEN,TypedString(Data.userData.accessToken))
            }
            if(::params.isInitialized) {
                params[Constants.KEY_ACCESS_TOKEN] = Data.userData.accessToken
            }

        }

        if (showLoader) {
            DialogPopup.showLoadingDialog(activity.get(), activity.get()!!.resources.getString(R.string.loading))
        }

        isInProgress = true

        // make api hit
        when (apiName) {

            ApiName.GENERATE_OTP -> RestClient.getApiServices().generateOtpK(params, callback as Callback<RegisterScreenResponse>)
            ApiName.GET_CITIES -> RestClient.getApiServices().getDriverSignUpDetails(params, callback as Callback<CityResponse>)
            ApiName.GET_LANGUAGES -> RestClient.getApiServices().fetchLanguageListKotlin(params, callback as Callback<DriverLanguageResponse>)
            ApiName.MANUAL_RIDE -> RestClient.getApiServices().requestManualRide(params, callback as Callback<ManualRideResponse>)
            ApiName.REGISTER_DRIVER ->  RestClient.getApiServices().updateDriverInfo(params, callback as Callback<RegisterScreenResponse> )
            ApiName.APPLY_PROMO ->  RestClient.getApiServices().applyPromo(params, callback as Callback<FeedCommonResponseKotlin> )
            ApiName.ADD_CARD_API ->  RestClient.getApiServices().addCardToDriver(params, callback as Callback<StripeCardResponse> )
            ApiName.FETCH_WALLET ->  RestClient.getApiServices().fetchWalletBalance(params, callback as Callback<WalletModelResponse> )
            ApiName.ADD_CASH_WALLET ->  RestClient.getApiServices().addMoneyViaStripe(params, callback as Callback<WalletModelResponse> )
            ApiName.VEHICLE_MAKE_DATA ->  RestClient.getApiServices().getVehicleMakeDetails(params, callback as Callback<VehicleDetailsResponse> )
            ApiName.VEHICLE_MODEL_DATA ->  RestClient.getApiServices().getVehicleModelDetails(params, callback as
                    Callback<VehicleModelCustomisationsResponse> )
            ApiName.UPDATE_LUGGAGE_COUNT ->  RestClient.getApiServices().updateLuggageCount(params, callback as Callback<FeedCommonResponseKotlin> )
            ApiName.UPDATE_DRIVER_SERVICES ->  RestClient.getApiServices().updateDriverVehicleServices(bodyParams as DriverVehicleServiceTypePopup.ServiceDetailModel,
                    callback as Callback<DriverVehicleServiceTypePopup.UpdateVehicleSetResponse> )
            ApiName.UPDATE_DOC_FIELDS ->  RestClient.getApiServices().uploadFields(params, callback as Callback<FeedCommonResponseKotlin> )
            ApiName.UPDATE_FARES ->  RestClient.getApiServices().updateDriverFares(params, callback as Callback<FeedCommonResponseKotlin> )
            ApiName.GET_TOLL_DATA ->  RestClient.getApiServices().getTollData(params, callback as Callback<TollDataResponse> )
            ApiName.UPDATE_TOLL_DATA ->  RestClient.getApiServices().updateTollData(params, callback as Callback<FeedCommonResponseKotlin> )
            ApiName.FETCH_DRIVER_REFERRAL_INFO -> RestClient.getApiServices().fetchDriverReferral(params, callback as Callback<ReferralsInfoResponse>)
            ApiName.FETCH_DRIVER_TRACTION_RIDES-> RestClient.getApiServices().fetchDriverTractionRides(params, callback as Callback<TractionResponse>)
            else -> throw IllegalArgumentException("API Type not declared")
        }
    }

    fun isTrivialError(flag: Int): Boolean {
        return (flag == ApiResponseFlags.INVALID_ACCESS_TOKEN.getOrdinal() || flag == ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal()
                || flag == ApiResponseFlags.SHOW_MESSAGE.getOrdinal() || flag == ApiResponseFlags.PARAMETER_MISSING.getOrdinal()
                || flag == ApiResponseFlags.ACTION_FAILED.getOrdinal()   )
    }

    private fun retryDialog(message: String) {
        DialogPopup.alertPopupWithListener(activity.get(), "", message) { apiCommonCallback?.onDialogClick() }
    }

}
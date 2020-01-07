package product.clicklabs.jugnoo.driver.ui.api;

import android.app.Activity;
import androidx.annotation.NonNull;
import android.view.View;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.BuildConfig;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.HomeUtil;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.BranchUrlRequest;
import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponse;
import product.clicklabs.jugnoo.driver.ui.popups.DriverVehicleServiceTypePopup;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.MultipartTypedOutput;

/**
 * Created by Parminder Singh on 3/27/17.
 */

/**
 *
 * @param <T> Expected Response Type Class
 */
public class ApiCommon<T extends FeedCommonResponse> {
    private Callback callback;
    private Activity activity;
    private boolean showLoader = true;
    private boolean putDefaultParams = true;
    private APICommonCallback<T> apiCommonCallback;
    private HashMap<String, String> params;
    private MultipartTypedOutput multipartTypedOutput;
    private ApiName apiName;
    private boolean putAccessToken = true;
    private boolean isCancelled;
    private boolean isErrorCancellable = true;
    private boolean checkForTrivialErrors = true;
    private Object objParams;
    private boolean isObjectRequest = false;

    public boolean isInProgress() {
        return isInProgress;
    }

    public void setInProgress(boolean inProgress) {
        isInProgress = inProgress;
    }

    private boolean isInProgress;



    /**
     * Generates a new constructor with type parameter and context
     * @param activity  Context Of The Calling Activity
     */
    public ApiCommon (Activity activity) {
        this.activity = activity;
    }

    public ApiCommon<T> showLoader(boolean showLoader) {
        this.showLoader = showLoader;
        return this;
    }

    public ApiCommon<T> isErrorCancellable(boolean isErrorCancellable) {
        this.isErrorCancellable = isErrorCancellable;
        return this;
    }

    public ApiCommon<T> putDefaultParams(boolean putDefaultParams) {
        this.putDefaultParams = putDefaultParams;
        return this;
    }


    public ApiCommon<T> putAccessToken(boolean putAccessToken) {
        this.putAccessToken = putAccessToken;
        return this;
    }
    public ApiCommon<T> checkForTrivialErrors(boolean check){
        this.checkForTrivialErrors = check;
        return this;
    }

    public void  execute(HashMap<String, String> params, @NonNull ApiName apiName, APICommonCallback<T> apiCommonCallback) {
        isObjectRequest = false;
        this.apiCommonCallback = apiCommonCallback;
        this.params = params;
        this.apiName = apiName;
        if(this.params==null){
            this.params = new HashMap<>();
        }
        hitAPI(false);
    }
    public void  execute(Object params, @NonNull ApiName apiName, APICommonCallback<T> apiCommonCallback) {
        isObjectRequest = true;
        this.apiCommonCallback = apiCommonCallback;
        this.objParams = params;
        this.apiName = apiName;
        if(this.objParams==null){
            this.objParams = new HashMap<>();
        }
        hitAPI(false);
    }

    public void execute(MultipartTypedOutput params, @NonNull ApiName apiName, APICommonCallback<T> apiCommonCallback) {
        isObjectRequest = false;
        this.apiCommonCallback = apiCommonCallback;
        if(multipartTypedOutput==null){
            multipartTypedOutput = new MultipartTypedOutput();
        }
        this.multipartTypedOutput = params;
        this.apiName = apiName;
        hitAPI(true);
    }


    private void hitAPI(boolean isMultiPartRequest) {


        if (!AppStatus.getInstance(activity).isOnline(activity)) {
            apiCommonCallback.onFinish();
            if (!apiCommonCallback.onNotConnected()) {
                DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);

            }
            return;
        }



        if (callback == null) {
            callback = new Callback<T>() {
                @Override
                public void success(T feedCommonResponse, Response response) {
                  setInProgress(false);

                    if(showLoader) {
                        DialogPopup.dismissLoadingDialog();
                    }
                    if(isCancelled())
                        return;

                    try {

                        if (!isTrivialError(feedCommonResponse.getFlag())
//                                &&
//                                (!checkForActionComplete || feedCommonResponse.flag == ApiResponseFlags.ACTION_COMPLETE.getOrdinal())
//                                && (successFlag==null || successFlag==feedCommonResponse.flag)
                        ) {
                            apiCommonCallback.onFinish();
							apiCommonCallback.onSuccess(feedCommonResponse, feedCommonResponse.getMessage(), feedCommonResponse.getFlag());
                        } else if(feedCommonResponse.getFlag()==ApiResponseFlags.INVALID_ACCESS_TOKEN.getOrdinal()){
                            apiCommonCallback.onFinish();
                            HomeActivity.logoutUser(activity, null);
						}else{
                            apiCommonCallback.onFinish();
                            if (!apiCommonCallback.onError(feedCommonResponse, feedCommonResponse.getMessage(), feedCommonResponse.getFlag())) {
                                retryDialog(feedCommonResponse.getMessage());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        apiCommonCallback.onFinish();
                        if (!apiCommonCallback.onException(e)) {
                            retryDialog( Data.CHECK_INTERNET_MSG);
                        }
                    }


                }

                @Override
                public void failure(RetrofitError error) {
                   setInProgress(false);
                    if(showLoader) {
                        DialogPopup.dismissLoadingDialog();
                    }
                    if(isCancelled())
                        return;
                    error.printStackTrace();
                    apiCommonCallback.onFinish();
                    if (!apiCommonCallback.onFailure(error)) {
                        retryDialog( Data.CHECK_INTERNET_MSG);
                    }


                }
            };
        }

        if(isMultiPartRequest){
            HomeUtil.putDefaultParams(multipartTypedOutput);
        } else if(!isObjectRequest){
            HomeUtil.putDefaultParams(params);
        }


        /*f(putAccessToken){
            if(isMultiPartRequest){
                multipartTypedOutput.addPart(Constants.KEY_ACCESS_TOKEN, new TypedString(Data.userData.accessToken));
            } else {
               params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
            }
        }*/



        if(showLoader) {
            DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));
        }
        setInProgress(true);
        switch (apiName) {
            case GENERATE_OTP:
                RestClient.getApiServices().generateOtpK(params,callback);
                break;
            case GET_CITIES:
//                RestClient.getApiServices().getCityRetro(params, BuildConfig.CITIES_PASSWORD, callback);
                RestClient.getApiServices().getDriverSignUpDetails(params, callback);
                break;
            case REGISTER_DRIVER:
                RestClient.getApiServices().updateDriverInfo(params, callback);
                break;
            case SHOW_RATE_CARD:
                RestClient.getApiServices().rateCardDetail(params, callback);
                break;
            case SEND_EMAIL_INVOICE:
                RestClient.getApiServices().sendEmailInvoice(params, callback);
                break;
            case UPDATE_DRIVER_PROPERTY:
                RestClient.getApiServices().updateDriverPropertyJava (params, callback);
                break;

            case UPDATE_DRIVER_SERVICES:
                RestClient.getApiServices().updateDriverVehicleServicesJava ((DriverVehicleServiceTypePopup.ServiceDetailModel) objParams, callback);
                break;
            case BRANCH_GENERATE_URL:
                RestClient.getBranchApi().generateUrl((BranchUrlRequest) objParams, callback);
                break;
            case GET_LANGUAGES:
                RestClient.getApiServices().fetchLanguageList(params, callback);
                break;
            case PAYTM_LOGIN_WITH_OTP:
                RestClient.getApiServices().paytmLoginWithOtpJava(params, callback);
                break;
            case PURCHASE_SUBSCRIPTION:
                RestClient.getApiServices().purchaseSubscriptions(params, callback);
                break;
            case PAYTM_REMOVE_WALLET:
                RestClient.getApiServices().paytmDeletePaytm(params, callback);
                break;
            case FETCH_WALLET:
                RestClient.getApiServices().fetchWalletBalance(params, callback);
                break;
            case ADD_CASH_WALLET:
                RestClient.getApiServices().addMoneyViaStripe(params, callback);
                break;
            case UPDATE_DOC_FIELDS:
                RestClient.getApiServices().uploadFields(params, callback);
                break;
            case GET_TOLL_DATA:
                RestClient.getApiServices().getTollData(params, callback);
                break;
            case UPDATE_TOLL_DATA:
                RestClient.getApiServices().updateTollData(params, callback);
                break;
            case VEHICLE_MAKE_DATA:
                RestClient.getApiServices().getVehicleMakeDetails(params, callback);
                break;
            case VEHICLE_MODEL_DATA:
                RestClient.getApiServices().getVehicleModelDetails(params, callback);
                break;
            default:
                throw new IllegalArgumentException("API Type not declared");

        }


    }

    public static boolean isTrivialError(int flag){
        return  flag ==ApiResponseFlags.INVALID_ACCESS_TOKEN.getOrdinal() || flag==ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal()
                || flag==ApiResponseFlags.SHOW_MESSAGE.getOrdinal();
    }

    private void retryDialog(String message) {
        DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiCommonCallback.onDialogClick();
         }
        });

    }

    public boolean isCancelled() {
        return isCancelled;
    }


    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }


}

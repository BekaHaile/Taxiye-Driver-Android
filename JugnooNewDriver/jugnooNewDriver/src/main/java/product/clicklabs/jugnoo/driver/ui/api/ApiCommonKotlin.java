package product.clicklabs.jugnoo.driver.ui.api;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.BuildConfig;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.HomeUtil;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponseKotlin;
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
public class ApiCommonKotlin<T extends FeedCommonResponseKotlin> {
    private Callback callback;
    private Activity activity;
    private boolean showLoader = true;
    private boolean putDefaultParams = true;
    private APICommonCallbackKotlin<T> apiCommonCallback;
    private HashMap<String, String> params;
    private MultipartTypedOutput multipartTypedOutput;
    private ApiName apiName;
    private boolean putAccessToken = true;
    private boolean isCancelled;
    private boolean isErrorCancellable = true;
    private boolean checkForTrivialErrors = true;
    private boolean checkForActionComplete= true;

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
    public ApiCommonKotlin(Activity activity) {
        this.activity = activity;
    }

    public ApiCommonKotlin<T> showLoader(boolean showLoader) {
        this.showLoader = showLoader;
        return this;
    }

    public ApiCommonKotlin<T> isErrorCancellable(boolean isErrorCancellable) {
        this.isErrorCancellable = isErrorCancellable;
        return this;
    }

    public ApiCommonKotlin<T> putDefaultParams(boolean putDefaultParams) {
        this.putDefaultParams = putDefaultParams;
        return this;
    }


    public ApiCommonKotlin<T> putAccessToken(boolean putAccessToken) {
        this.putAccessToken = putAccessToken;
        return this;
    }
    public ApiCommonKotlin<T> checkForTrivialErrors(boolean check){
        this.checkForTrivialErrors = check;
        return this;
    }

    public ApiCommonKotlin<T> checkForActionComplete(boolean check){
        this.checkForActionComplete = check;
        return this;
    }

    public void  execute(HashMap<String, String> params, @NonNull ApiName apiName, APICommonCallbackKotlin<T> apiCommonCallback) {
        this.apiCommonCallback = apiCommonCallback;
        this.params = params;
        this.apiName = apiName;
        if(this.params==null){
            this.params = new HashMap<>();
        }
        hitAPI(false);
    }

    public void execute(MultipartTypedOutput params, @NonNull ApiName apiName, APICommonCallbackKotlin<T> apiCommonCallback) {
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
                return;
            }

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

                        if (!isTrivialError(feedCommonResponse.getFlag()) && (!checkForActionComplete || feedCommonResponse.getFlag()==ApiResponseFlags.ACTION_COMPLETE.getOrdinal())) {
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
        } else {
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
                RestClient.getApiServices().getCityRetro(params, BuildConfig.CITIES_PASSWORD, callback);
                break;
            case GET_LANGUAGES:
                RestClient.getApiServices().fetchLanguageListKotlin(params, callback);
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

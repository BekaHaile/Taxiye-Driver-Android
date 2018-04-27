package product.clicklabs.jugnoo.driver.ui.api;


import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponse;
import retrofit.RetrofitError;

/**
 * Created by Parminder Singh on 3/28/17.
 */

public abstract class APICommonCallback<T extends FeedCommonResponse> {

    public boolean onNotConnected(){
        return false;
    }

    public boolean onException(Exception e){
        return false;
    }


    public abstract void onSuccess(T t, String message, int flag);

    public abstract boolean onError(T t, String message, int flag);

    public boolean onFailure(RetrofitError error){
        return false;
    }


    public void onDialogClick(){}


    public void onFinish() {
    }
}

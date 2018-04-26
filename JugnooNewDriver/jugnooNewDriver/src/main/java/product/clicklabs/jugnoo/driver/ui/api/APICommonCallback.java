package product.clicklabs.jugnoo.driver.ui.api;


import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponse;
import retrofit.RetrofitError;

/**
 * Created by Parminder Singh on 3/28/17.
 */

public abstract class APICommonCallback<T extends FeedCommonResponse> {

    public abstract boolean onNotConnected();

    public abstract boolean onException(Exception e);


    public abstract void onSuccess(T t, String message, int flag);

    public abstract boolean onError(T t, String message, int flag);

    public abstract boolean onFailure(RetrofitError error);


    public abstract void onDialogClick();


    public void onFinish() {
    }
}

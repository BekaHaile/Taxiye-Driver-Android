package product.clicklabs.jugnoo.driver.retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import product.clicklabs.jugnoo.driver.retrofit.model.SettleUserDebt;

/**
 * Created by Parminder Saini on 06/06/18.
 */
public class StripeLoginResponse extends SettleUserDebt{


    @Expose
    @SerializedName("login_link")
    String url;

    public String getStripeLoginUrl() {
        return url;
    }
}

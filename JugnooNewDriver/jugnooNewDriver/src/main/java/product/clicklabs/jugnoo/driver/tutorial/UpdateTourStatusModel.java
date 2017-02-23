package product.clicklabs.jugnoo.driver.tutorial;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gurmail on 2/8/17.
 */

public class UpdateTourStatusModel {

    @SerializedName("flag")
    @Expose
    public Integer flag;
    @SerializedName("message")
    @Expose
    public String message;

}

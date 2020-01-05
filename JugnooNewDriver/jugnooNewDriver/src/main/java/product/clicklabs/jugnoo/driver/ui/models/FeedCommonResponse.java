package product.clicklabs.jugnoo.driver.ui.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Parminder Saini on 26/03/17.
 */

public class FeedCommonResponse {



    @SerializedName("flag")
    @Expose
    public int flag;
    @SerializedName("message")
    @Expose
    public String message;
    @SerializedName("error")
    @Expose
    public String error;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message == null ? error : message;
    }


    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String serverMessage() {
        if (message == null || message.isEmpty()) {
            if (error != null) return error;
            else return "";
        } else {
            return message;
        }
}
}

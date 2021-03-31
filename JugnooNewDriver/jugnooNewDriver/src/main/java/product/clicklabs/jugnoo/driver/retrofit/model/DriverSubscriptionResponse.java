package product.clicklabs.jugnoo.driver.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class DriverSubscriptionResponse {
    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<SubscriptionData> getData() {
        return data;
    }

    public void setData(List<SubscriptionData> data) {
        this.data = data;
    }

    @SerializedName("flag")
    @Expose
    private int flag;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<SubscriptionData> data = new ArrayList<SubscriptionData>();

}

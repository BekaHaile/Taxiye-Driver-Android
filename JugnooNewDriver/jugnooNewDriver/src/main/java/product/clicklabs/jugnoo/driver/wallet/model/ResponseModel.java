package product.clicklabs.jugnoo.driver.wallet.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseModel<T> {
    @Expose
    @SerializedName("flag")
    private int flag;

    @Expose
    @SerializedName("message")
    private String message;

    @Expose
    @SerializedName("data")
    private T data;

    public int getFlag() {
        return flag;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(T data) {
        this.data = data;
    }
}

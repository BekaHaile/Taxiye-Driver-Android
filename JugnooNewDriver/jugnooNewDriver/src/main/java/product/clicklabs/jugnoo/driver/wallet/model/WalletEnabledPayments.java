package product.clicklabs.jugnoo.driver.wallet.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponse;

public class WalletEnabledPayments<T> extends FeedCommonResponse {

    @Expose
    @SerializedName("amount")
    private int amount;

    @Expose
    @SerializedName("payment_mode_config_data")
    private T[] data;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public T[] getData() {
        return data;
    }

    public void setData(T[] data) {
        this.data = data;
    }
}


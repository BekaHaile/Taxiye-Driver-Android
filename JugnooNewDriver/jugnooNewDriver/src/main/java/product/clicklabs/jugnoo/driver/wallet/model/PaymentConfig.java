package product.clicklabs.jugnoo.driver.wallet.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentConfig {
    @Expose
    @SerializedName("name")
    private String name;

    @Expose
    @SerializedName("enabled")
    private int enabled;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEnabled() {
        return enabled;
    }

    public void setEnabled(int enabled) {
        this.enabled = enabled;
    }
}

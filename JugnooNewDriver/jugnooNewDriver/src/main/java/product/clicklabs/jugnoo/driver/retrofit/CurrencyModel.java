package product.clicklabs.jugnoo.driver.retrofit;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Parminder Saini on 11/04/18.
 */

public class CurrencyModel {

    @SerializedName("currency")
    private String currencyUnit;
    @SerializedName("distance_unit")
    private String distanceUnit;

    public String getCurrencyUnit() {
        return currencyUnit;
    }

    public void setCurrencyUnit(String currencyUnit) {
        this.currencyUnit = currencyUnit;
    }

    public String getDistanceUnit() {
        return distanceUnit;
    }

    public void setDistanceUnit(String distanceUnit) {
        this.distanceUnit = distanceUnit;
    }
}

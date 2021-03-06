
package product.clicklabs.jugnoo.driver.retrofit.model;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponse;
import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponseKotlin;


public class TractionResponse extends FeedCommonResponseKotlin {


    @SerializedName("rides")
    @Expose
    private ArrayList<Ride> rides = null;
    @SerializedName("currency")
    @Expose
    private String currency ;

    public ArrayList<Ride> getRides() {
        return rides;
    }

    public void setRides(ArrayList<Ride> rides) {
        this.rides = rides;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}

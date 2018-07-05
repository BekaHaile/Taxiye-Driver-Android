package product.clicklabs.jugnoo.driver.stripe.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponseKotlin;

/**
 * Created by Parminder Saini on 24/05/18.
 */
public class StripeCardResponse extends FeedCommonResponseKotlin {

    @SerializedName("card_data")
    private ArrayList<StripeCardData> stripeCardData;

    public ArrayList<StripeCardData> getStripeCardData() {
        return stripeCardData;
    }
}

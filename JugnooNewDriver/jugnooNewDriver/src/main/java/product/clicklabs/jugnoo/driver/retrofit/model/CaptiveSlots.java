package product.clicklabs.jugnoo.driver.retrofit.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Parminder Saini on 11/01/18.
 */

public class CaptiveSlots {


    @SerializedName("online_min")
    private int onlineMin;
    @SerializedName("slot_name")
    private String slotName;

    public int getOnlineMin() {
        return onlineMin;
    }

    public String getSlotName() {
        return slotName;
    }
}

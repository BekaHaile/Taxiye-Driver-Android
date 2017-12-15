package product.clicklabs.jugnoo.driver.datastructure;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Parminder Saini on 14/12/17.
 */

public class PlanDetails {


    @SerializedName("description")
    private String description;
    @SerializedName("amount")
    private Double amount;
    @SerializedName("is_selected")
    private int isSelected;
    @SerializedName("validity_days")
    private int validityDays;

    public boolean getIsSelected() {
        return isSelected==1;
    }

    public void setIsSelected(int isSelected) {
        this.isSelected = isSelected;
    }

    public String getDescription() {
        return description;
    }

    public Double getAmount() {
        return amount;
    }

    public int getValidityDays() {
        return validityDays;
    }
}

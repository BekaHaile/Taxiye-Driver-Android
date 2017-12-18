package product.clicklabs.jugnoo.driver.datastructure;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Parminder Saini on 14/12/17.
 */

public class FetchDriverPlansResponse {

    @SerializedName("flag")
    private  int flag;

    @SerializedName("message")
    private String message;


    @SerializedName("plan_details")
    private ArrayList<PlanDetails> availablePlanDetails;

    @SerializedName("active_plan_details")
    private ArrayList<PlanDetails> activePlanDetails;

    @SerializedName("outstanding_amount")
    private Double outstandingAmount;

    @SerializedName("current_plan_savings")
    private Double currentPlanSaving;

    @SerializedName("total_savings")
    private Double totalSavings;

    @SerializedName("amount_spent_today")
    private String amountSpentToday;


    public String getAmountSpentToday() {
        return amountSpentToday;
    }

    public int getFlag() {
        return flag;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<PlanDetails> getAvailablePlanDetails() {
        return availablePlanDetails;
    }

    public ArrayList<PlanDetails> getActivePlanDetails() {
        return activePlanDetails;
    }

    public Double getOutstandingAmount() {
        return outstandingAmount;
    }

    public Double getCurrentPlanSaving() {
        return currentPlanSaving;
    }

    public Double getTotalSavings() {
        return totalSavings;
    }


}

package product.clicklabs.jugnoo.driver.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.retrofit.CurrencyModel;

/**
 * Created by Parminder Saini on 26/05/18.
 */
public class DriverCreditResponse extends CurrencyModel  {

    @SerializedName("flag")
    @Expose
    private Integer flag;
    @SerializedName("message")
    @Expose
    private String message;

    @Expose
    @SerializedName("credits_earned")
    private double creditsEarned;

    @Expose
    @SerializedName("commission_saved")
    private Double comissionSaved;


    @Expose
    @SerializedName("credit_history")
    private ArrayList<CreditHistory> creditHistoryList ;

    @Expose
    @SerializedName("rate_card")
    private RateCard rateCard;


    public Integer getFlag() {
        return flag;
    }

    public String getMessage() {
        return message;
    }

    public double getCreditsEarned() {
        return creditsEarned;
    }

    public Double getComissionSaved() {
        return comissionSaved;
    }

    public ArrayList<CreditHistory> getCreditHistoryList() {
        return creditHistoryList;
    }

    public class CreditHistory extends CurrencyModel{

        @Expose
        @SerializedName("id")
        private String creditId;

        @Expose
        @SerializedName("date")
        private String date;

        @Expose
        @SerializedName("desc")
        private String desc;

        @Expose
        @SerializedName("amount")
        private double amount;


        public String getCreditId() {
            return creditId;
        }

        public String getDate() {
            return date;
        }

        public String getDesc() {
            return desc;
        }

        public double getAmount() {
            return amount;
        }
    }

    public class RateCard extends CurrencyModel{
        @Expose
        @SerializedName("driver_credit")
        private double driverCredit;
        @Expose
        @SerializedName("customer_credit")
        private double customerCredit;
        @Expose
        @SerializedName("advertise_credit")
        private double advertiseCredit;

        public double getDriverCredit() {
            return driverCredit;
        }

        public double getCustomerCredit() {
            return customerCredit;
        }

        public double getAdvertiseCredit() {
            return advertiseCredit;
        }
    }
}

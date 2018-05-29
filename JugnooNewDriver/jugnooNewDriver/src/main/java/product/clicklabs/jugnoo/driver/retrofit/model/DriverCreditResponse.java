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
    @SerializedName("transactions")
    private ArrayList<CreditHistory> creditHistoryList ;

    public Integer getFlag() {
        return flag;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<CreditHistory> getCreditHistoryList() {
        return creditHistoryList;
    }

    public class CreditHistory extends CurrencyModel{

        @Expose
        @SerializedName("txn_id")
        private int txnId;

        @Expose
        @SerializedName("txn_date")
        private String txnDate;

        @Expose
        @SerializedName("desc")
        private String desc;

        @Expose
        @SerializedName("amount")
        private double amount;

        public int getTxnId() {
            return txnId;
        }

        public void setTxnId(int txnId) {
            this.txnId = txnId;
        }

        public String getTxnDate() {
            return txnDate;
        }

        public void setTxnDate(String txnDate) {
            this.txnDate = txnDate;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }
    }
}

package product.clicklabs.jugnoo.driver.datastructure;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gurmail on 29/08/17.
 */

public class WalletTransactionResponse {
    @SerializedName("flag")
    private int flag;
    @SerializedName("banner")
    private String banner;
    @SerializedName("balance")
    private int balance;
    @SerializedName("num_txns")
    private int numTxns;
    @SerializedName("page_size")
    private int pageSize;
    @SerializedName("transactions")
    private List<Transactions> transactions = new ArrayList<>();

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getNumTxns() {
        return numTxns;
    }

    public void setNumTxns(int numTxns) {
        this.numTxns = numTxns;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public List<Transactions> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transactions> transactions) {
        this.transactions = transactions;
    }

    public static class Transactions {
        @SerializedName("txn_id")
        private int txnId;
        @SerializedName("txn_type")
        private int txnType;
        @SerializedName("amount")
        private Double amount;
        @SerializedName("txn_date")
        private String txnDate;
        @SerializedName("txn_time")
        private String txnTime;
        @SerializedName("logged_on")
        private String loggedOn;
        @SerializedName("wallet_txn")
        private int walletTxn;
        @SerializedName("paytm")
        private int paytm;
        @SerializedName("mobikwik")
        private int mobikwik;
        @SerializedName("freecharge")
        private int freecharge;
        @SerializedName("reference_id")
        private int referenceId;
        @SerializedName("event")
        private int event;
        @SerializedName("txn_text")
        private String txnText;

        public int getTxnId() {
            return txnId;
        }

        public void setTxnId(int txnId) {
            this.txnId = txnId;
        }

        public int getTxnType() {
            return txnType;
        }

        public void setTxnType(int txnType) {
            this.txnType = txnType;
        }

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }

        public String getTxnDate() {
            return txnDate;
        }

        public void setTxnDate(String txnDate) {
            this.txnDate = txnDate;
        }

        public String getTxnTime() {
            return txnTime;
        }

        public void setTxnTime(String txnTime) {
            this.txnTime = txnTime;
        }

        public String getLoggedOn() {
            return loggedOn;
        }

        public void setLoggedOn(String loggedOn) {
            this.loggedOn = loggedOn;
        }

        public int getWalletTxn() {
            return walletTxn;
        }

        public void setWalletTxn(int walletTxn) {
            this.walletTxn = walletTxn;
        }

        public int getPaytm() {
            return paytm;
        }

        public void setPaytm(int paytm) {
            this.paytm = paytm;
        }

        public int getMobikwik() {
            return mobikwik;
        }

        public void setMobikwik(int mobikwik) {
            this.mobikwik = mobikwik;
        }

        public int getFreecharge() {
            return freecharge;
        }

        public void setFreecharge(int freecharge) {
            this.freecharge = freecharge;
        }

        public int getReferenceId() {
            return referenceId;
        }

        public void setReferenceId(int referenceId) {
            this.referenceId = referenceId;
        }

        public int getEvent() {
            return event;
        }

        public void setEvent(int event) {
            this.event = event;
        }

        public String getTxnText() {
            return txnText;
        }

        public void setTxnText(String txnText) {
            this.txnText = txnText;
        }
    }
}

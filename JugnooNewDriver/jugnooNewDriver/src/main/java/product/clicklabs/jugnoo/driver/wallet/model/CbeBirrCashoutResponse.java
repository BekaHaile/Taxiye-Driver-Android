package product.clicklabs.jugnoo.driver.wallet.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CbeBirrCashoutResponse {

    @Expose
    @SerializedName("billRefNo")
    private String billRefNo;

    @Expose
    @SerializedName("amount")
    private String amount;

    @Expose
    @SerializedName("status")
    private int status;

    @Expose
    @SerializedName("generatedDate")
    private String generatedDate;

    @Expose
    @SerializedName("payerName")
    private String payerName ;

    @Expose
    @SerializedName("bankTransactionId")
    private String bankTransactionId ;

    @Expose
    @SerializedName("sourceIdentifier")
    private String sourceIdentifier ;

    @Expose
    @SerializedName("callbackUrl")
    private String callbackUrl ;

    @Expose
    @SerializedName("expiryDate")
    private String expiryDate ;

    @Expose
    @SerializedName("lastStatusDate")
    private String lastStatusDate ;

    @Expose
    @SerializedName("id")
    private int id ;

    @Expose
    @SerializedName("sourceNotified")
    private boolean sourceNotified ;

    @Expose
    @SerializedName("cbeShortCode")
    private String cbeShortCode ;

    public String getBillRefNo() {
        return billRefNo;
    }

    public String getAmount() {
        return amount;
    }

    public int getStatus() {
        return status;
    }


    public String getPayerName() {
        return payerName;
    }

    public String getBankTransactionId() {
        return bankTransactionId;
    }

    public String getSourceIdentifier() {
        return sourceIdentifier;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getLastStatusDate() {
        return lastStatusDate;
    }

    public String getGeneratedDate() {
        return generatedDate;
    }

    public void setGeneratedDate(String generatedDate) {
        this.generatedDate = generatedDate;
    }

    public String getCbeShortCode() {
        return cbeShortCode;
    }

    public void setCbeShortCode(String cbeShortCode) {
        this.cbeShortCode = cbeShortCode;
    }

    public int getId() {
        return id;
    }

    public boolean isSourceNotified() {
        return sourceNotified;
    }

    public void setBillRefNo(String billRefNo) {
        this.billRefNo = billRefNo;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public void setBankTransactionId(String bankTransactionId) {
        this.bankTransactionId = bankTransactionId;
    }

    public void setSourceIdentifier(String sourceIdentifier) {
        this.sourceIdentifier = sourceIdentifier;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setLastStatusDate(String lastStatusDate) {
        this.lastStatusDate = lastStatusDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSourceNotified(boolean sourceNotified) {
        this.sourceNotified = sourceNotified;
    }
}

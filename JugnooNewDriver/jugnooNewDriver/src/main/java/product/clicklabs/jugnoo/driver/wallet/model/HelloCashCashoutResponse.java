package product.clicklabs.jugnoo.driver.wallet.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HelloCashCashoutResponse {

    @Expose
    @SerializedName("amount")
    private int amount;

    @Expose
    @SerializedName("code")
    private int code;

    @Expose
    @SerializedName("currency")
    private String currency;

    @Expose
    @SerializedName("date")
    private String date;

    @Expose
    @SerializedName("description")
    private String description;

    @Expose
    @SerializedName("expires")
    private String expires ;

    @Expose
    @SerializedName("from")
    private String from ;

    @Expose
    @SerializedName("fromname")
    private String fromName ;

    @Expose
    @SerializedName("id")
    private String id ;

    @Expose
    @SerializedName("status")
    private String status ;

    @Expose
    @SerializedName("statusdetail")
    private String statusDetail ;

    @Expose
    @SerializedName("toname")
    private String toName ;

    @Expose
    @SerializedName("to")
    private String to ;

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDetail() {
        return statusDetail;
    }

    public void setStatusDetail(String statusDetail) {
        this.statusDetail = statusDetail;
    }

    public String isToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}

package product.clicklabs.jugnoo.driver.retrofit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubscriptionData {
    public int getSubscription_id() {
        return subscription_id;
    }

    public void setSubscription_id(int subscription_id) {
        this.subscription_id = subscription_id;
    }

    public int getIs_active() {
        return is_active;
    }

    public void setIs_active(int is_active) {
        this.is_active = is_active;
    }

    public int getOperator_id() {
        return operator_id;
    }

    public void setOperator_id(int operator_id) {
        this.operator_id = operator_id;
    }

    public int getCity_id() {
        return city_id;
    }

    public void setCity_id(int city_id) {
        this.city_id = city_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getNum_of_rides() {
        return num_of_rides;
    }

    public void setNum_of_rides(int num_of_rides) {
        this.num_of_rides = num_of_rides;
    }

    public int getNum_of_days() {
        return num_of_days;
    }

    public void setNum_of_days(int num_of_days) {
        this.num_of_days = num_of_days;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAllowed_vehicles() {
        return allowed_vehicles;
    }

    public void setAllowed_vehicles(int allowed_vehicles) {
        this.allowed_vehicles = allowed_vehicles;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTerms_n_conditions() {
        return terms_n_conditions;
    }

    public void setTerms_n_conditions(String terms_n_conditions) {
        this.terms_n_conditions = terms_n_conditions;
    }

    public String getStart_from() {
        return start_from;
    }

    public void setStart_from(String start_from) {
        this.start_from = start_from;
    }

    public String getEnd_on() {
        return end_on;
    }

    public void setEnd_on(String end_on) {
        this.end_on = end_on;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public String getUpdated_on() {
        return updated_on;
    }

    public void setUpdated_on(String updated_on) {
        this.updated_on = updated_on;
    }

    @SerializedName("subscription_id")
    @Expose
    private int subscription_id;
    @SerializedName("is_active")
    @Expose
    private int is_active;
    @SerializedName("operator_id")
    @Expose
    private int operator_id;
    @SerializedName("city_id")
    @Expose
    private int city_id;
    @SerializedName("type")
    @Expose
    private int type;
    @SerializedName("num_of_rides")
    @Expose
    private int num_of_rides;
    @SerializedName("num_of_days")
    @Expose
    private int num_of_days;
    @SerializedName("amount")
    @Expose
    private int amount;
    @SerializedName("allowed_vehicles")
    @Expose
    private int allowed_vehicles;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("terms_n_conditions")
    @Expose
    private String terms_n_conditions;
    @SerializedName("start_from")
    @Expose
    private String start_from;
    @SerializedName("end_on")
    @Expose
    private String end_on;
    @SerializedName("created_on")
    @Expose
    private String created_on;
    @SerializedName("updated_on")
    @Expose
    private String updated_on;

    public int getCurrent_ride_count() {
        return current_ride_count;
    }

    public void setCurrent_ride_count(int current_ride_count) {
        this.current_ride_count = current_ride_count;
    }

    public int getNum_of_rides_allowed() {
        return num_of_rides_allowed;
    }

    public void setNum_of_rides_allowed(int num_of_rides_allowed) {
        this.num_of_rides_allowed = num_of_rides_allowed;
    }

    @SerializedName("current_ride_count")
    @Expose
    private int current_ride_count;

    @SerializedName("num_of_rides_allowed")
    @Expose
    private int num_of_rides_allowed;





}

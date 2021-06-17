package product.clicklabs.jugnoo.driver.wallet.model;

import com.google.gson.annotations.SerializedName;

import product.clicklabs.jugnoo.driver.datastructure.SearchResultNew;

public class MpesaCashoutResponse {

    @SerializedName("name")
    private String name;

    @SerializedName("phone_number")
    private String phoneNumber;

    @SerializedName("secondary_phone_number")
    private String secondaryPhoneNumber;

    @SerializedName("destination")
    private SearchResultNew destination;

    @SerializedName("deliveryStatus")
    private int deliveryStatus ;

    @SerializedName("cancellationReason")
    private String cancellationReason ;


    //Getters
    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getSecondaryPhoneNumber() {
        return secondaryPhoneNumber;
    }

    public SearchResultNew getDestination() {
        return destination;
    }

    public int getDeliveryStatus() {
        return deliveryStatus;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setSecondaryPhoneNumber(String secondaryPhoneNumber) {
        this.secondaryPhoneNumber = secondaryPhoneNumber;
    }

    public void setDestination(SearchResultNew destination) {
        this.destination = destination;
    }

    public void setDeliveryStatus(int deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }
}

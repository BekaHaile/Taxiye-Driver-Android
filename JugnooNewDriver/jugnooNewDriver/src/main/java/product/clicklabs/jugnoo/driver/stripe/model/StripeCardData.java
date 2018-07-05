package product.clicklabs.jugnoo.driver.stripe.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Parminder Saini on 23/05/18.
 */
public class StripeCardData  implements Parcelable {




    @Expose
    @SerializedName("id")
    private String cardId;
    @Expose
    @SerializedName("last_4")
    private String last4;
    @Expose
    @SerializedName("exp_month")
    private String expiryMonth;
    @Expose
    @SerializedName("exp_year")
    private String expiryYear;
    @Expose
    @SerializedName("_cvv")
    private String _cvv;
    @Expose
    @SerializedName("brand")
    private String brand;

    public StripeCardData(String cardId, String last4, String brand) {
        this.cardId = cardId;
        this.last4 = last4;
        this.brand = brand;
    }

    protected StripeCardData(Parcel in) {
        cardId = in.readString();
        last4 = in.readString();
        expiryMonth = in.readString();
        expiryYear = in.readString();
        _cvv = in.readString();
        brand = in.readString();
    }

    public static final Creator<StripeCardData> CREATOR = new Creator<StripeCardData>() {
        @Override
        public StripeCardData createFromParcel(Parcel in) {
            return new StripeCardData(in);
        }

        @Override
        public StripeCardData[] newArray(int size) {
            return new StripeCardData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(cardId);
        parcel.writeString(last4);
        parcel.writeString(expiryMonth);
        parcel.writeString(expiryYear);
        parcel.writeString(_cvv);
        parcel.writeString(brand);
    }

    public String getCardId() {
        return cardId;
    }

    public String getLast4() {
        return last4;
    }

    public String getExpiryMonth() {
        return expiryMonth;
    }

    public String getExpiryYear() {
        return expiryYear;
    }

    public String get_cvv() {
        return _cvv;
    }

    public String getBrand() {
        return brand;
    }
}


package product.clicklabs.jugnoo.driver.retrofit.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Parminder Saini on 13/09/18.
 */
public class DocFieldsInfo implements Parcelable {

    @SerializedName("key")
    private String key;

    @SerializedName("label")
    private String label;


    @SerializedName("type")
    private String type;


    @SerializedName("value")
    private String value;

    protected DocFieldsInfo(Parcel in) {
        key = in.readString();
        label = in.readString();
        type = in.readString();
        value = in.readString();
    }

    public static final Creator<DocFieldsInfo> CREATOR = new Creator<DocFieldsInfo>() {
        @Override
        public DocFieldsInfo createFromParcel(Parcel in) {
            return new DocFieldsInfo(in);
        }

        @Override
        public DocFieldsInfo[] newArray(int size) {
            return new DocFieldsInfo[size];
        }
    };

    public String getKey() {
        return key;
    }

    public String getLabel() {
        return label;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeString(label);
        dest.writeString(type);
        dest.writeString(value);
    }
}

package product.clicklabs.jugnoo.driver.retrofit.model;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import product.clicklabs.jugnoo.driver.ui.models.SearchDataModel;

/**
 * Created by Parminder Saini on 13/09/18.
 */
public class DocFieldsInfo extends SearchDataModel {

    @SerializedName("key")
    private String key;

    @SerializedName("label")
    private String label;


    @SerializedName("type")
    private String type;


    @SerializedName("value")
    private String value;
    @SerializedName("set_value")
    private List<String> setValue;
    @SerializedName("set")
    private List<DocFieldsInfo> set;
    @SerializedName("ref_key")
    private String refKey;
    @SerializedName("confirm_key")
    private String confirmKey;
    @SerializedName("is_secure")
    private int isSecure;
    @SerializedName("mandatory")
    private boolean mandatory;

    @SerializedName("is_selected")
    private boolean isSelected;

    public String getKey() {
        return key;
    }

    public String getLabel() {
        if(type.equalsIgnoreCase("element") && TextUtils.isEmpty(label)){
            return value;
        }
        return label;
    }

    @Override
    public int getImage(Context context) {
        return 0;
    }

    @Override
    public boolean showImage() {
        return false;
    }

    @Override
    public boolean isSelected() {
        return isSelected;
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

    public List<String> getSetValue() {
        return setValue;
    }

    public void setSetValue(List<String> setValue) {
        this.setValue = setValue;
    }

    public List<DocFieldsInfo> getSet() {
        return set;
    }

    public void setSet(List<DocFieldsInfo> set) {
        this.set = set;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getRefKey() {
        return refKey;
    }

    public void setRefKey(String refKey) {
        this.refKey = refKey;
    }

    public String getConfirmKey() {
        return confirmKey;
    }

    public void setConfirmKey(String confirmKey) {
        this.confirmKey = confirmKey;
    }

    public int getIsSecured() {
        return isSecure;
    }

    public void setIsSecured(int isSecured) {
        this.isSecure = isSecured;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void isMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }
}

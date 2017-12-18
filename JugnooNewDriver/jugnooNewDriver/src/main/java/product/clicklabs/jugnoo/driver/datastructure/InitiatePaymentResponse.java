package product.clicklabs.jugnoo.driver.datastructure;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Parminder Saini on 18/12/17.
 */

public class InitiatePaymentResponse  {
    @SerializedName("flag")
    private  int flag;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private InitiatePaymentResponseData initiatePaymentResponseData;


    @SerializedName("surl")
    private String surl;
    @SerializedName("furl")
    private String furl;



    public String getSurl() {
        return surl;
    }

    public String getFurl() {
        return furl;
    }
    public class InitiatePaymentResponseData {
        @SerializedName("file_name")
        private String fileName;


        public String getFileName() {
            return fileName;
        }


    }

    public InitiatePaymentResponseData getInitiatePaymentResponseData() {
        return initiatePaymentResponseData;
    }
}

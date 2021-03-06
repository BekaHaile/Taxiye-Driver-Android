package product.clicklabs.jugnoo.driver.datastructure;


import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import product.clicklabs.jugnoo.driver.Constants;

public class DriverVehicleDetails {

    @SerializedName("brand")
    private String brandName = "";
    @SerializedName("vehicle_no")
    private String vehicleNo = "";
    @SerializedName("model_name")
    private String modelName = "";
    @SerializedName("no_of_seat_belts")
    private String noOfSeatBelts = "";
    @SerializedName("no_of_doors")
    private String noOfDoors = "";
    @SerializedName("image")
    private String image = "";
    @SerializedName(Constants.DRIVER_VEHICLE_MAPPING_ID)
    private int driverVehicleMappingId=-1;
    @SerializedName("reason")
    private String rejectReason="";
    @SerializedName("vehicle_type")
    private int vehicleType;


    private String vehicleName;
    private int driverVehicleMappingStatus;

    public DriverVehicleDetails(String vehicleName,
                                String vehicleNo,
                                int driverVehicleMappingId,
                                int driverVehicleMappingStatus, String image) {
        this.image = image;
        this.vehicleName = vehicleName;
        this.vehicleNo = vehicleNo;
        this.driverVehicleMappingId = driverVehicleMappingId;
        this.driverVehicleMappingStatus = driverVehicleMappingStatus;
    }

    public DriverVehicleDetails(String brandName,
                                String modelName,
                                String vehicleNo,
                                int driverVehicleMappingId,
                                int driverVehicleMappingStatus, String image,String rejectReason,int vehicleType) {
        this.image = image;
        this.vehicleNo = vehicleNo;
        this.driverVehicleMappingId = driverVehicleMappingId;
        this.driverVehicleMappingStatus = driverVehicleMappingStatus;
        this.modelName = modelName;
        this.brandName = brandName;
        this.rejectReason=rejectReason;
        this.vehicleType=vehicleType;
    }

    public String getModelName() {
        return modelName;
    }

    public String getBrandName() {
        return brandName;
    }


    public void setDriverVehicleMappingStatus(int driverVehicleMappingStatus) {
        this.driverVehicleMappingStatus = driverVehicleMappingStatus;
    }

    public int getDriverVehicleMappingId() {
        return driverVehicleMappingId;
    }

    public String getDriverVehicleMappingStatus() {
        switch (driverVehicleMappingStatus) {
            case 0:
                return "Pending";
            case 1:
                return "Approved";
            case -1:
                return "Rejected";
            case 2:
                return "Removed By Driver";
            default:
                return "";
        }
    }

    public String getImage() {
        return image;
    }

    public String getVehicleName() {

        return brandName +" "+modelName;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public String getNoOfDoors() {
        return noOfDoors;
    }

    public String getNoOfSeatBelts() {
        return noOfSeatBelts;
    }

    public String getReason() {
        return rejectReason;
    }


    public static DriverVehicleDetails parseDocumentVehicleDetails(JSONObject vehObj){
        if(vehObj!=null) {
            int driverMappingId = vehObj.optInt(Constants.DRIVER_VEHICLE_MAPPING_ID,-1);
            int driverMappingStatus = vehObj.optInt(Constants.DRIVER_VEHICLE_MAPPING_STATUS);
            String vehicleNo = vehObj.optString(Constants.VEHICLE_NO, "-----");
            String brandName = vehObj.optString(Constants.BRAND, " ");
            String modelName = vehObj.optString(Constants.MODEL_NAME, "");
            String rejectReason = vehObj.optString(Constants.REJECT_REASON, "");
            int vehicletype = vehObj.optInt("vehicle_type");
            rejectReason=rejectReason.toLowerCase().equals("null")?"":rejectReason;
            String vehicleImage = vehObj.optString(Constants.BRAND, " ") + ", " + vehObj.optString(Constants.KEY_IMAGE, "");

            return new DriverVehicleDetails(brandName,modelName, vehicleNo, driverMappingId, driverMappingStatus, vehicleImage,rejectReason,vehicletype);
        }
        return null;
    }

    public String getVehicleType() {
        switch (vehicleType){
            case 1:
                return "Autos";
            case 2:
                return "Bikes";
            case 3:
                return "Taxi";
            case 4:
                return "Mini Truck";
            case 5:
                return "E-Rickshaw";
             default:
                 return "";

        }
    }
}
package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.Inflater;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.UserData;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.ui.DriverSetupFragment;
import product.clicklabs.jugnoo.driver.ui.VehicleDetailsFragment;
import product.clicklabs.jugnoo.driver.ui.api.APICommonCallback;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Log;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

import static com.crashlytics.android.beta.Beta.TAG;

public class VehicleDetailsActivity extends AppCompatActivity implements ToolbarChangeListener{

    RecyclerView rvVehicles;
    VehicleDetailsAdapter vehicleDetailsAdapter;
    ImageView ivAddDestRide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_details);
        hitFetchDriverVehicles();
        initViews();
        setListeners();
    }

    private void initViews() {
        ((AppCompatTextView) findViewById(R.id.title)).setText(R.string.your_vehicles);
        rvVehicles = findViewById(R.id.rvVehicles);
        vehicleDetailsAdapter = new VehicleDetailsAdapter(this, Data.userData.getDriverVehicleDetailsList());
        rvVehicles.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvVehicles.setAdapter(vehicleDetailsAdapter);
        ivAddDestRide = findViewById(R.id.ivAddDestRide);

    }

    private void setListeners() {
        ivAddDestRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DriverSetupFragment frag = DriverSetupFragment.newInstance(Data.userData.accessToken);
                Bundle bundle = new Bundle();
                bundle.putAll(frag.getArguments());
                bundle.putBoolean(Constants.FROM_VEHICLE_DETAILS_SCREEN, true);
                frag.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().add(R.id.container, frag, DriverSetupFragment.class.getName())
                        .addToBackStack(DriverSetupFragment.class.getName()).commit();

            }
        });
    }

    private void updateVehicleList() {
        if (vehicleDetailsAdapter != null) {
            vehicleDetailsAdapter.notifyDataSetChanged();
        }
    }

    public void openVehicleDetails(String accessToken, String cityId, String vehicleType, String userName, HashMap<String, String> driverDetails) {
        VehicleDetailsFragment frag=VehicleDetailsFragment.newInstance(accessToken, cityId, vehicleType, userName, null, false, driverDetails);
        Bundle bundle = new Bundle();
        bundle.putAll(frag.getArguments());
        bundle.putBoolean(Constants.FROM_VEHICLE_DETAILS_SCREEN, true);
        frag.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container,frag , VehicleDetailsFragment.class.getName())
                .addToBackStack(VehicleDetailsFragment.class.getName())
                .commit();
    }

    public void vehicleAdded(UserData.DriverVehicleDetails driverVehicleDetails){
        HashMap<String,String> params =new HashMap<>() ;
        params.put(Constants.DRIVER_VEHICLE_MAPPING_ID, driverVehicleDetails.getDriverVehicleMappingId()+"");
        Intent intent =new Intent(this,DriverDocumentActivity.class);
        intent.putExtra(Constants.DRIVER_VEHICLE_MAPPING_ID,driverVehicleDetails.getDriverVehicleMappingId());
        startActivity(intent);
    }

    public void hitFetchDriverVehicles() {
        DialogPopup.showLoadingDialog(this, getString(R.string.loading));
        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
        RestClient.getApiServices().fetchDriverVehicles(params, new Callback<Object>() {
            @Override
            public void success(Object o, Response response) {
                String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                Log.i(TAG, "fetchDriverVehicle response = " + responseStr);
                DialogPopup.dismissLoadingDialog();
                try {
                    JSONObject jObj = new JSONObject(responseStr);
                    int flag = jObj.optInt(Constants.KEY_FLAG, ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
                    String message = JSONParser.getServerMessage(jObj);
                    if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                        if (jObj.has(Constants.KEY_DATA)) {
                            Data.userData.getDriverVehicleDetailsList().clear();
                            JSONArray dataArr = jObj.getJSONArray(Constants.KEY_DATA);
                            if (dataArr.length() > 0) {
                                for (int i = 0; i < dataArr.length(); i++) {

                                    JSONObject vehObj = dataArr.getJSONObject(i);
                                    int driverMappingId = vehObj.optInt(Constants.DRIVER_VEHICLE_MAPPING_ID);
                                    int driverMappingStatus = vehObj.optInt(Constants.DRIVER_VEHICLE_MAPPING_STATUS);
                                    String vehicleNo = vehObj.optString(Constants.VEHICLE_NO, "-----");
                                    String vehicleName = vehObj.optString(Constants.BRAND, " ") + ", " + vehObj.optString(Constants.MODEL_NAME, "");
                                    String vehicleImage = vehObj.optString(Constants.BRAND, " ") + ", " + vehObj.optString(Constants.KEY_IMAGE, "");
                                    UserData.DriverVehicleDetails driverVehicleDetail = Data.userData.new DriverVehicleDetails(vehicleName, vehicleNo, driverMappingId, driverMappingStatus, vehicleImage);
                                    Data.userData.getDriverVehicleDetailsList().add(driverVehicleDetail);
                                }
                            }
                        }
                        updateVehicleList();
                    }
                        DialogPopup.alertPopup(VehicleDetailsActivity.this, "", message);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                DialogPopup.dismissLoadingDialog();
            }

            @Override
            public void failure(RetrofitError error) {
                try {
                    DialogPopup.dismissLoadingDialog();
                    DialogPopup.alertPopup(VehicleDetailsActivity.this, "", VehicleDetailsActivity.this.getString(R.string.error_occured_tap_to_retry));
                } catch (Exception e) {
                    DialogPopup.dismissLoadingDialog();
                    e.printStackTrace();
                }
                DialogPopup.dismissLoadingDialog();
            }
        });
    }

    public void hitRemoveDriverVehicles(HashMap<String,String> params) {
        DialogPopup.showLoadingDialog(this, getString(R.string.loading));
        params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
        RestClient.getApiServices().removeVehicle(params, new Callback<Object>() {
            @Override
            public void success(Object o, Response response) {
                String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                Log.i(TAG, "fetchDriverVehicle response = " + responseStr);
                DialogPopup.dismissLoadingDialog();
                try {
                    JSONObject jObj = new JSONObject(responseStr);
                    int flag = jObj.optInt(Constants.KEY_FLAG, ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
                    String message = JSONParser.getServerMessage(jObj);
                    if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                        updateVehicleList();
                    }
                        DialogPopup.alertPopup(VehicleDetailsActivity.this, "", message);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                DialogPopup.dismissLoadingDialog();
            }

            @Override
            public void failure(RetrofitError error) {
                try {
                    DialogPopup.dismissLoadingDialog();
                    DialogPopup.alertPopup(VehicleDetailsActivity.this, "", VehicleDetailsActivity.this.getString(R.string.error_occured_tap_to_retry));
                } catch (Exception e) {
                    DialogPopup.dismissLoadingDialog();
                    e.printStackTrace();
                }
                DialogPopup.dismissLoadingDialog();
            }
        });
    }


    @Override
    public void setToolbarText(@NotNull String title) {
        ((AppCompatTextView) findViewById(R.id.title)).setText(R.string.your_vehicles);
    }

    @Override
    public void setToolbarVisibility(boolean isVisible) {

    }
}

enum VehicleMappingStatusEnum {

    DRIVER_VEHICLE_MAPPING_REMOVED(2),
    DRIVER_VEHICLE_MAPPING_REJECTED(-1),
    DRIVER_VEHICLE_MAPPING_PENDING(0),
    DRIVER_VEHICLE_MAPPING_APPROVED(1),
    DOCUMENTS_REJECTED(-1),
    DOCUMENTS_APPROVED(1),
    DOCUMENTS_WATING_FOR_APPROVAL(0);

    private int ordinal;

    VehicleMappingStatusEnum(int ordinal) {
        this.ordinal = ordinal;
    }

    public int getOrdinal() {
        return ordinal;
    }
}

class VehicleDetailsAdapter extends RecyclerView.Adapter<VehicleDetailsAdapter.CustomerVh> {

    Activity activity;
    ArrayList<UserData.DriverVehicleDetails> vehicleDetails = new ArrayList();

    VehicleDetailsAdapter(Activity activity, ArrayList<UserData.DriverVehicleDetails> vehicleDetails) {
        this.activity = activity;
        this.vehicleDetails = vehicleDetails;
    }

    @NonNull
    @Override
    public CustomerVh onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_vehicle_details, viewGroup, false);
        return new CustomerVh(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerVh customerVh, int i) {
        customerVh.tvVehicleName.setText(vehicleDetails.get(i).getVehicleName());
        customerVh.tvVehicleDetails.setText(vehicleDetails.get(i).getVehicleNo());
        String status = vehicleDetails.get(i).getDriverVehicleMappingStatus();
        if (status.equalsIgnoreCase("approved")) {
            customerVh.tvVehicleStatus.setTextColor(activity.getResources().getColor(R.color.green_btn));
        } else
            customerVh.tvVehicleStatus.setTextColor(activity.getResources().getColor(R.color.red_dark));
        customerVh.tvVehicleStatus.setText(status);
        String vehImage = vehicleDetails.get(i).getImage();
        if (!vehImage.isEmpty())
            Picasso.with(activity)
                    .load(vehImage)
                    .placeholder(R.drawable.auto_icon_front_new)
                    .transform(new CircleTransform())
                    .into(customerVh.profileImg);


    }

    @Override
    public int getItemCount() {
        return vehicleDetails.size();
    }

    class CustomerVh extends RecyclerView.ViewHolder {
        TextView tvVehicleDetails, tvVehicleStatus, tvVehicleName;
        ImageView profileImg, ivDelete;

        public CustomerVh(@NonNull View itemView) {
            super(itemView);
            tvVehicleDetails = itemView.findViewById(R.id.tvVehicleDetails);
            tvVehicleStatus = itemView.findViewById(R.id.tvVehicleStatus);
            tvVehicleName = itemView.findViewById(R.id.tvVehicleName);
            profileImg = itemView.findViewById(R.id.profileImg);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                HashMap<String,String> params=new HashMap<>();
                params.put(Constants.DRIVER_VEHICLE_MAPPING_ID,""+vehicleDetails.get(getAdapterPosition()).getDriverVehicleMappingId());
                    ((VehicleDetailsActivity)activity).hitRemoveDriverVehicles(params);
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(activity instanceof VehicleDetailsActivity)
                        ((VehicleDetailsActivity)activity).vehicleAdded(vehicleDetails.get(getAdapterPosition()));
                }
            });
        }

    }
}
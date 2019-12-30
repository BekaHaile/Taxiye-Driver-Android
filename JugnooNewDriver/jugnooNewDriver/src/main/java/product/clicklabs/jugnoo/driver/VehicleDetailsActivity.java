package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.DriverVehicleDetails;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.ui.DriverSetupFragment;
import product.clicklabs.jugnoo.driver.ui.VehicleDetailsFragment;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Log;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

import static com.crashlytics.android.beta.Beta.TAG;

public class VehicleDetailsActivity extends AppCompatActivity implements ToolbarChangeListener{

    private boolean openSelectVehicle = false;

    RecyclerView rvVehicles;
    VehicleDetailsAdapter vehicleDetailsAdapter;
    ImageView ivAddDestRide,backBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_details);
        hitFetchDriverVehicles();
        openSelectVehicle = getIntent().getBooleanExtra(Constants.OPEN_ACTIVITY_TO_SELECT_VEHICLE, false);
        initViews();
        setListeners();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==Activity.RESULT_OK){
            if(requestCode==1){
                getSupportFragmentManager().popBackStackImmediate(DriverSetupFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            setAddBtnVisibility(View.VISIBLE);
        }
    }



    private void initViews() {
        ((AppCompatTextView) findViewById(R.id.title)).setText(R.string.your_vehicles);
        rvVehicles = findViewById(R.id.rvVehicles);
        ivAddDestRide = findViewById(R.id.ivAddDestRide);
        backBtn = findViewById(R.id.backBtn);


        if (openSelectVehicle) {
            vehicleDetailsAdapter = new VehicleDetailsAdapter(this, Data.userData.getDriverVehicleDetailsList(), true);
            backBtn.setVisibility(View.GONE);
            setAddBtnVisibility(View.GONE);
            ((AppCompatTextView) findViewById(R.id.title)).setText(R.string.select_vehicle);
        } else {
            if(Data.userData.autosAvailable==1){
                setAddBtnVisibility(View.GONE);
            }
            else
                setAddBtnVisibility(View.VISIBLE);
            vehicleDetailsAdapter = new VehicleDetailsAdapter(this, Data.userData.getDriverVehicleDetailsList(), false);
        }
        rvVehicles.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvVehicles.setAdapter(vehicleDetailsAdapter);



    }

    private void setAddBtnVisibility(int visibility) {
        if(Data.userData.autosAvailable==0&&!openSelectVehicle)
        ivAddDestRide.setVisibility(visibility);
        else
            ivAddDestRide.setVisibility(View.GONE);
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
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(getSupportFragmentManager().getFragments().size()==0){
            setToolbarText(getString(R.string.your_vehicles));

                setAddBtnVisibility(View.VISIBLE);
        }
    }

    public Fragment getDriverSetupFragment() {
        return getSupportFragmentManager().findFragmentByTag(DriverSetupFragment.class.getName());
    }

    public void vehicleSelected() {
        setResult(RESULT_OK);
        finish();
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

    public void vehicleAdded(DriverVehicleDetails driverVehicleDetails){
        hitFetchDriverVehicles();
        getSupportFragmentManager().popBackStackImmediate(DriverSetupFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        openUploadDocScreen(driverVehicleDetails);
    }

    public void openUploadDocScreen(DriverVehicleDetails driverVehicleDetails){
        Intent intent =new Intent(this,DriverDocumentActivity.class);
        intent.putExtra(Constants.FROM_VEHICLE_DETAILS_SCREEN,true);
        intent.putExtra(Constants.DRIVER_VEHICLE_MAPPING_ID,driverVehicleDetails.getDriverVehicleMappingId());
        intent.putExtra(Constants.KEY_ACCESS_TOKEN,Data.userData.accessToken);
        intent.putExtra("doc_required", 3);
        startActivityForResult(intent,1);
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
                                    Data.userData.getDriverVehicleDetailsList().add(DriverVehicleDetails.parseDocumentVehicleDetails(vehObj));
                                }
                            }
                        }
                        updateVehicleList();
                    }
//                    DialogPopup.alertPopup(VehicleDetailsActivity.this, "", message);

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

    public void hitRemoveDriverVehicles(int positionInList,int mappingId) {
        DialogPopup.alertPopupTwoButtonsWithListeners(this, getString(R.string.remove_vehicle_warning), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,String> params=new HashMap<>();
                params.put(Constants.DRIVER_VEHICLE_MAPPING_ID,""+mappingId);
                DialogPopup.showLoadingDialog(VehicleDetailsActivity.this, getString(R.string.loading));
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
//                                Data.userData.getDriverVehicleDetailsList().get(positionInList).setDriverVehicleMappingStatus(2);
//                                updateVehicleList();
                                ( VehicleDetailsActivity.this).hitFetchDriverVehicles();
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
        });

    }


    @Override
    public void setToolbarText(@NotNull String title) {
        ((AppCompatTextView) findViewById(R.id.title)).setText(title);
        setAddBtnVisibility(View.GONE);
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

    private Activity activity;
    private boolean selectVehicleView ;
    private ArrayList<DriverVehicleDetails> vehicleDetails;

    VehicleDetailsAdapter(Activity activity, ArrayList<DriverVehicleDetails> vehicleDetails, boolean selectVehicleView) {
        this.selectVehicleView = selectVehicleView;
        this.activity = activity;
        this.vehicleDetails = vehicleDetails;
    }


    @Override
    public CustomerVh onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.content_vehicle_details, viewGroup, false);
        return new CustomerVh(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerVh customerVh, int i) {
        DriverVehicleDetails vehicle=vehicleDetails.get(i);
        customerVh.tvVehicleName.setText(vehicle.getVehicleName());
        customerVh.tvVehicleDetails.setText(vehicle.getVehicleNo());
        String status = vehicle.getDriverVehicleMappingStatus();
        if(vehicle.getDriverVehicleMappingStatus().equalsIgnoreCase("Rejected")){
            customerVh.tvVehicleRejectReason.setVisibility(View.VISIBLE);
            customerVh.tvVehicleRejectReason.setText(vehicle.getReason());
        }
        else
            customerVh.tvVehicleRejectReason.setVisibility(View.GONE);

        if (status.equalsIgnoreCase("approved")) {
            customerVh.tvVehicleStatus.setTextColor(activity.getResources().getColor(R.color.green_btn));
        } else
            customerVh.tvVehicleStatus.setTextColor(activity.getResources().getColor(R.color.red_dark));
        customerVh.tvVehicleStatus.setText(status);
        String vehImage = vehicleDetails.get(i).getImage();
        if (!vehImage.isEmpty())
            Picasso.with(activity)
                    .load(vehImage)
                    .placeholder(R.drawable.ic_auto_request)
                    .transform(new CircleTransform())
                    .into(customerVh.profileImg);
        if (selectVehicleView) {
            customerVh.ivDelete.setVisibility(View.GONE);
        }
        customerVh.tvVehicleType.setText(vehicle.getVehicleType());


    }

    @Override
    public int getItemCount() {
        return vehicleDetails.size();
    }

    class CustomerVh extends RecyclerView.ViewHolder {
        TextView tvVehicleDetails, tvVehicleStatus, tvVehicleName,tvVehicleRejectReason,tvVehicleType;
        ImageView profileImg, ivDelete;
        View border,rlRemoveVehicle;

        private CustomerVh(@NonNull View itemView) {
            super(itemView);
            tvVehicleDetails = itemView.findViewById(R.id.tvVehicleDetails);
            tvVehicleType = itemView.findViewById(R.id.tvVehicleType);
            rlRemoveVehicle = itemView.findViewById(R.id.rlRemoveVehicle);
            tvVehicleStatus = itemView.findViewById(R.id.tvVehicleStatus);
            tvVehicleName = itemView.findViewById(R.id.tvVehicleName);
            tvVehicleRejectReason = itemView.findViewById(R.id.tvVehicleRejectReason);
            profileImg = itemView.findViewById(R.id.profileImg);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            border = itemView.findViewById(R.id.border);
            rlRemoveVehicle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((VehicleDetailsActivity) activity).hitRemoveDriverVehicles(getAdapterPosition(), vehicleDetails.get(getAdapterPosition()).getDriverVehicleMappingId());

                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (activity instanceof VehicleDetailsActivity) {
                        if (selectVehicleView) {
//                        if(v.getTag()==null){
//                            v.setTag("Clicked");
//                            border.setBackground(((Activity)activity).getResources().getDrawable(R.drawable.circle_border_autos));
//                        }
//                        else if(v.getTag().equals("Clicked"))
//                        {
//                            v.setTag(null);
//                            border.setBackground(((Activity)activity).getResources().getDrawable(R.drawable.background_transparent));
//                        }
                            Data.userData.setActiveVehicle(vehicleDetails.get(getAdapterPosition()));
                            ((VehicleDetailsActivity) activity).vehicleSelected();
                            border.setBackground(activity.getResources().getDrawable(R.drawable.background_white_rounded_orange_bordered));

                        } else
                            ((VehicleDetailsActivity) activity).openUploadDocScreen(vehicleDetails.get(getAdapterPosition()));
                    }
                }
            });
        }

    }
}

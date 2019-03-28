package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.driver.adapters.VehicleDetail;
import product.clicklabs.jugnoo.driver.adapters.VehicleDetailsLogin;
import product.clicklabs.jugnoo.driver.adapters.VehicleDetailsProfileAdapter;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.BookingHistoryResponse;
import product.clicklabs.jugnoo.driver.ui.VehicleDetailsFragment;
import product.clicklabs.jugnoo.driver.ui.popups.DriverVehicleServiceTypePopup;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.FirebaseEvents;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.ProfileInfo;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class DriverProfileActivity extends BaseFragmentActivity implements VehicleDetailsFragment.VehicleDetailsInteractor {

    RelativeLayout relative;
    RelativeLayout driverDetailsRL;
    LinearLayout driverDetailsRLL;
    View backBtn;
    TextView title;

    TextView textViewDriverName, textViewDriverId, textViewPhoneNumber, textViewRankCity, textViewRankOverall, textViewMonthlyValue, textViewRidesTakenValue,
            textViewRidesCancelledValue, textViewRidesMissedValue, textViewTitleBarDEI, textViewmonthlyScore, textViewMonthlyText,
            textViewRidesTakenText, textViewRidesMissedText, textViewRidesCancelledText, terms,tvServiceType;
    TextView tvGender, tvDateOfBirth;

    ImageView profileImg, imageViewTitleBarDEI, ivEditIcon;
    CardView cvSwitchNavigation;
    SwitchCompat switchNavigation, switchMaxSound;
    TextView tvDocuments;
    private   RecyclerView rvVehicleTypes;
    private   View vehicleDetails,layoutVehicleServiceDetails, dividerVehicleServiceDetails,ivEditVehicle;


    public static ProfileInfo openedProfileInfo;
    private DriverVehicleServiceTypePopup driverVehicleServiceTypePopup;
    private VehicleDetailsProfileAdapter vehicleDetailsProfileAdapter;

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            textViewDriverName.setText(Data.userData.userName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile_screen);
        if (BuildConfig.DEBUG) {
            findViewById(R.id.topRl).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (Data.isCaptive()) {
                        Data.setCaptive(false);
                        Toast.makeText(DriverProfileActivity.this, "Driver made non captive", Toast.LENGTH_SHORT).show();

                    } else {
                        Data.setCaptive(true);
                        Toast.makeText(DriverProfileActivity.this, "Driver made captive", Toast.LENGTH_SHORT).show();

                    }
                    return false;
                }
            });
        }

        relative = (RelativeLayout) findViewById(R.id.activity_profile_screen);
        driverDetailsRLL = (LinearLayout) findViewById(R.id.driverDetailsRLL);
        driverDetailsRL = (RelativeLayout) findViewById(R.id.driverDetailsRL);

        new ASSL(DriverProfileActivity.this, relative, 1134, 720, false);

        backBtn = findViewById(R.id.backBtn);
        title = (TextView) findViewById(R.id.title);
        title.setTypeface(Fonts.mavenRegular(this));
        title.setText(R.string.profile);

        ivEditIcon = (ImageView) findViewById(R.id.ivEditIcon);
        ivEditIcon.getDrawable().mutate().setColorFilter(ContextCompat.getColor(this, R.color.themeColor), PorterDuff.Mode.SRC_ATOP);
        cvSwitchNavigation = (CardView) findViewById(R.id.cvSwitchNavigation);
        switchNavigation = (SwitchCompat) findViewById(R.id.switchNavigation);
        switchMaxSound = (SwitchCompat) findViewById(R.id.switchMaxSound);
        textViewDriverName = (TextView) findViewById(R.id.textViewDriverName);
        textViewDriverName.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
        textViewDriverId = (TextView) findViewById(R.id.textViewDriverId);
        textViewPhoneNumber = (TextView) findViewById(R.id.textViewPhoneNumber);
        textViewDriverId.setTypeface(Fonts.mavenRegular(this));
        textViewRankCity = (TextView) findViewById(R.id.textViewRankCity);
        textViewRankCity.setTypeface(Fonts.mavenRegular(this));
        textViewRankOverall = (TextView) findViewById(R.id.textViewRankOverall);
        textViewRankOverall.setTypeface(Fonts.mavenRegular(this));
        textViewMonthlyValue = (TextView) findViewById(R.id.textViewMonthlyValue);
        textViewMonthlyValue.setTypeface(Fonts.mavenRegular(this));

        tvGender = findViewById(R.id.tvGender);
        tvGender.setVisibility(View.GONE);
        tvDateOfBirth = findViewById(R.id.tvDateOfBirth);
        tvDateOfBirth.setVisibility(View.GONE);

        textViewRidesTakenValue = (TextView) findViewById(R.id.textViewRidesTakenValue);
        textViewRidesTakenValue.setTypeface(Fonts.mavenRegular(this));
        textViewRidesCancelledValue = (TextView) findViewById(R.id.textViewRidesCancelledValue);
        textViewRidesCancelledValue.setTypeface(Fonts.mavenRegular(this));
        textViewRidesMissedValue = (TextView) findViewById(R.id.textViewRidesMissedValue);
        textViewRidesMissedValue.setTypeface(Fonts.mavenRegular(this));
        textViewmonthlyScore = (TextView) findViewById(R.id.textViewmonthlyScore);
        textViewmonthlyScore.setTypeface(Fonts.mavenMedium(this));
        textViewMonthlyText = (TextView) findViewById(R.id.textViewMonthlyText);
        textViewMonthlyText.setTypeface(Fonts.mavenRegular(this));
        textViewMonthlyText.setText(getStringText(R.string.profile_monthly_earnings_text));
        textViewRidesTakenText = (TextView) findViewById(R.id.textViewRidesTakenText);
        textViewRidesTakenText.setTypeface(Fonts.mavenRegular(this));
        textViewRidesTakenText.setText(getStringText(R.string.rides_taken));

        textViewRidesMissedText = (TextView) findViewById(R.id.textViewRidesMissedText);
        textViewRidesMissedText.setTypeface(Fonts.mavenRegular(this));
        textViewRidesMissedText.setText(getResources().getString(R.string.rides_missed));

        textViewRidesCancelledText = (TextView) findViewById(R.id.textViewRidesCancelledText);
        textViewRidesCancelledText.setTypeface(Fonts.mavenRegular(this));
        tvServiceType =  (TextView)findViewById(R.id.tvServiceType);

        tvDocuments = findViewById(R.id.tvDocuments);

        terms = (TextView) findViewById(R.id.terms);
        terms.setTypeface(Fonts.mavenRegular(this));


        textViewRidesCancelledText.setText(getStringText(R.string.rides_cancelled));


        profileImg = (ImageView) findViewById(R.id.profileImg);
        imageViewTitleBarDEI = (ImageView) findViewById(R.id.imageViewTitleBarDEI);

        backBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MyApplication.getInstance().logEvent(FirebaseEvents.PROFILE_PAGE + "_" + FirebaseEvents.BACK, null);
               onBackPressed();
            }
        });


        getProfileInfoAsync(DriverProfileActivity.this);

        driverDetailsRL.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//				EditDriverProfile.openProfileInfo = openedProfileInfo;
                MyApplication.getInstance().logEvent(FirebaseEvents.PROFILE_PAGE + "_1_" + FirebaseEvents.CARD, null);
                startActivity(new Intent(DriverProfileActivity.this, EditDriverProfile.class));
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DriverProfileActivity.this, AboutActivity.class));
                FlurryEventLogger.event(FlurryEventNames.TERMS_OF_USE);
            }
        });
        terms.setVisibility(Prefs.with(this).getInt(Constants.KEY_SHOW_ABOUT, 1) == 1 ? View.VISIBLE : View.GONE);

        switchNavigation.setVisibility(Prefs.with(this).getInt(Constants.KEY_SHOW_WAZE_TOGGLE, 0) == 1 ? View.VISIBLE : View.GONE);
        findViewById(R.id.ivDivNavigation).setVisibility(switchNavigation.getVisibility());
        switchNavigation.setChecked(Prefs.with(this).getInt(Constants.KEY_NAVIGATION_TYPE, Constants.NAVIGATION_TYPE_GOOGLE_MAPS) == Constants.NAVIGATION_TYPE_WAZE);
        switchNavigation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Prefs.with(DriverProfileActivity.this).save(Constants.KEY_NAVIGATION_TYPE, isChecked ? Constants.NAVIGATION_TYPE_WAZE : Constants.NAVIGATION_TYPE_GOOGLE_MAPS);
            }
        });

        tvDocuments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Data.userData == null) {
                    return;
                }
                Intent intent = new Intent(DriverProfileActivity.this, DriverDocumentActivity.class);
                intent.putExtra("access_token", Data.userData.accessToken);
                intent.putExtra("in_side", true);
                intent.putExtra("doc_required", 0);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });

        switchMaxSound.setChecked(Prefs.with(this).getInt(Constants.KEY_MAX_SOUND, 1) == 1);
        switchMaxSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Prefs.with(DriverProfileActivity.this).save(Constants.KEY_MAX_SOUND, isChecked ? 1 : 0);
                if (!isChecked) {
                    AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    am.setStreamVolume(AudioManager.STREAM_MUSIC, 5, 0);
                }
            }
        });


        rvVehicleTypes = findViewById(R.id.rvVehicleDetails);
        vehicleDetails = findViewById(R.id.cvVehicleDetails);
        ivEditVehicle = findViewById(R.id.idEditVehicle);
        rvVehicleTypes.setLayoutManager(new LinearLayoutManager(this));
        rvVehicleTypes.setNestedScrollingEnabled(false);
        setVehicleModelData();

        layoutVehicleServiceDetails =    findViewById(R.id.layoutVehicleServiceDetails);
        dividerVehicleServiceDetails  =    findViewById(R.id.ivDivVehicleServiceDetails);
        setVehicleSetsData();


    }

    private void setVehicleSetsData() {
        boolean showVehicleServiceDetails = Prefs.with(this).getInt(Constants.KEY_ENABLE_VEHICLE_SETS, 0) == 1;
        if (showVehicleServiceDetails && Data.userData != null && Data.userData.getVehicleServicesModel() != null) {
            setVehicleSetDetails();
            layoutVehicleServiceDetails.setVisibility(View.VISIBLE);
            dividerVehicleServiceDetails.setVisibility(View.VISIBLE);

            tvServiceType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Data.userData == null) {
                        return;
                    }

                    List<DriverVehicleServiceTypePopup.VehicleServiceDetail> vehicleServiceDetails = Data.userData.getVehicleServicesModel();

                    if (vehicleServiceDetails.size() == 0) {
                        return;
                    }

                    if (driverVehicleServiceTypePopup == null) {
                        driverVehicleServiceTypePopup = new DriverVehicleServiceTypePopup(DriverProfileActivity.this, vehicleServiceDetails);
                    } else {
                        driverVehicleServiceTypePopup.setData(vehicleServiceDetails);

                    }
                    if(!driverVehicleServiceTypePopup.isShowing()){
                        driverVehicleServiceTypePopup.show();

                    }

                }
            });
        } else {
            layoutVehicleServiceDetails.setVisibility(View.GONE);
            dividerVehicleServiceDetails.setVisibility(View.GONE);
        }
    }

    public void setVehicleSetDetails() {
        StringBuilder builder = new StringBuilder();
        String sep = ", ";
        for(DriverVehicleServiceTypePopup.VehicleServiceDetail vehicleSet: Data.userData.getVehicleServicesModel()){
            if(vehicleSet.getChecked()==1){
                builder.append(vehicleSet.getServiceName());
                builder.append(", ");
            }

        }
        if(builder.length()>1){
            builder.delete(builder.length()-sep.length(),builder.length());

        }
        tvServiceType.setText(builder.toString());
    }


    public void performBackPressed() {

        finish();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getFragments()!=null && getSupportFragmentManager().getFragments().size()>0){
            title.setText(getString(R.string.profile));
            terms.setVisibility(View.VISIBLE);
            super.onBackPressed();

        }else{
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.left_out);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            ASSL.closeActivity(relative);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.gc();
    }

    private void getProfileInfoAsync(final Activity activity) {
        DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));
        try {
            driverDetailsRLL.setVisibility(View.GONE);
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
            HomeUtil.putDefaultParams(params);
            RestClient.getApiServices().driverProfileInfo(params,
                    new Callback<BookingHistoryResponse>() {
                        @Override
                        public void success(BookingHistoryResponse bookingHistoryResponse, Response response) {
                            try {
                                String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
                                Log.i("driverprof", jsonString);
                                JSONObject jObj;
                                jObj = new JSONObject(jsonString);
                                int flag = jObj.getInt("flag");
                                String message = JSONParser.getServerMessage(jObj);
                                if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag, null)) {
                                    if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
                                        DialogPopup.alertPopup(activity, "", message);
                                    } else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {

                                        try {
                                            String textViewDriverName = "", textViewTitleBarDEI = "", accNo = "", ifscCode = "", bankName = "", bankLoc = "", currency = "";
                                            int textViewDriverId = 0, textViewRankCity = 0, textViewRankOverall = 0,
                                                    textViewRidesTakenValue = 0, textViewRidesMissedValue = 0,
                                                    textViewRidesCancelledValue = 0, textViewOnlineHoursValue = 0;
                                            double textViewMonthlyValue = 0;
                                            if (jObj.has("driver_name")) {
                                                textViewDriverName = jObj.getString("driver_name");
                                            }
                                            if (jObj.has("driver_id")) {
                                                textViewDriverId = jObj.getInt("driver_id");
                                            }
                                            if (jObj.has("driver_city_rank")) {
                                                textViewRankCity = jObj.getInt("driver_city_rank");
                                            }
                                            if (jObj.has("driver_overall_rank")) {
                                                textViewRankOverall = jObj.getInt("driver_overall_rank");
                                            }
                                            if (jObj.has("driver_earning")) {
                                                textViewMonthlyValue = jObj.getDouble("driver_earning");
                                            }
                                            if (jObj.has("rides_taken")) {
                                                textViewRidesTakenValue = jObj.getInt("rides_taken");
                                            }
                                            if (jObj.has("rides_missed")) {
                                                textViewRidesMissedValue = jObj.getInt("rides_missed");
                                            }
                                            if (jObj.has("rides_cancelled")) {
                                                textViewRidesCancelledValue = jObj.getInt("rides_cancelled");
                                            }
                                            if (jObj.has("online_hours")) {
                                                textViewOnlineHoursValue = jObj.getInt("online_hours");
                                            }
                                            if (jObj.has("bank_account_no")) {
                                                accNo = jObj.getString("bank_account_no");
                                            }
                                            if (jObj.has("ifsc_code")) {
                                                ifscCode = jObj.getString("ifsc_code");
                                            }
                                            if (jObj.has("bank_name")) {
                                                bankName = jObj.getString("bank_name");
                                            }
                                            if (jObj.has("bank_location")) {
                                                bankLoc = jObj.getString("bank_location");
                                            }
                                            if (jObj.has("currency")) {
                                                currency = jObj.getString("currency");
                                            }
                                            openedProfileInfo = new ProfileInfo(textViewDriverName, textViewDriverId, textViewRankCity,
                                                    textViewRankOverall, textViewMonthlyValue, textViewRidesTakenValue, textViewRidesMissedValue,
                                                    textViewRidesCancelledValue, textViewOnlineHoursValue, textViewTitleBarDEI, accNo, ifscCode,
                                                    bankName, bankLoc, currency);


                                            if(Data.userData != null) {
                                                Data.userData.setGender(jObj.optInt(Constants.KEY_GENDER, Data.userData.getGender()));
                                                Data.userData.setDateOfBirth(jObj.optString(Constants.KEY_DATE_OF_BIRTH, Data.userData.getDateOfBirth()));
                                            }

                                            setUserData();

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);

                                        }
                                    }
                                }
                            } catch (Exception exception) {
                                exception.printStackTrace();
                                DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                            }
                            DialogPopup.dismissLoadingDialog();
                            driverDetailsRLL.setVisibility(View.VISIBLE);

                        }


                        @Override
                        public void failure(RetrofitError error) {
                            DialogPopup.dismissLoadingDialog();
                            driverDetailsRLL.setVisibility(View.VISIBLE);
                            DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUserData() {
        try {
            try {
                Picasso.with(DriverProfileActivity.this).load(Data.userData.userImage)
                        .transform(new CircleTransform())
                        .into(profileImg);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (openedProfileInfo != null && Data.userData != null) {
                textViewDriverName.setText("" + openedProfileInfo.textViewDriverName);
                textViewDriverId.setText(getStringText(R.string.driver_id) + " " + openedProfileInfo.textViewDriverId);
				textViewPhoneNumber.setText(getStringText(R.string.phone_no) + " " + Data.userData.phoneNo);
                if (openedProfileInfo.textViewRankCity == 0) {
                    textViewRankCity.setVisibility(View.GONE);
                } else {
                    textViewRankCity.setVisibility(View.VISIBLE);
                    textViewRankCity.setText(getStringText(R.string.rank_city) + " " + openedProfileInfo.textViewRankCity);
                }

                if (openedProfileInfo.textViewRankCity == 0) {
                    textViewRankOverall.setVisibility(View.GONE);
                } else {
                    textViewRankOverall.setVisibility(View.VISIBLE);
                    textViewRankOverall.setText(getStringText(R.string.rank_overall) + " " + openedProfileInfo.textViewRankOverall);
                }

                if (openedProfileInfo.textViewMonthlyValue > 0 && getResources().getBoolean(R.bool.show_earnings_on_profile)) {
                    textViewMonthlyValue.setText(Utils.formatCurrencyValue(openedProfileInfo.currency, openedProfileInfo.textViewMonthlyValue));
                    findViewById(R.id.rlMonthlyEarnings).setVisibility(View.VISIBLE);
                    findViewById(R.id.imageViewHorizontal1).setVisibility(View.VISIBLE);


                } else {
                    findViewById(R.id.rlMonthlyEarnings).setVisibility(View.GONE);
                    findViewById(R.id.imageViewHorizontal1).setVisibility(View.GONE);
                }
                textViewRidesTakenValue.setText("" + openedProfileInfo.textViewRidesTakenValue);
                textViewRidesCancelledValue.setText("" + openedProfileInfo.textViewRidesCancelledValue);
                textViewRidesMissedValue.setText("" + openedProfileInfo.textViewRidesMissedValue);

                tvGender.setVisibility((Prefs.with(this).getInt(Constants.KEY_DRIVER_GENDER_FILTER, 0) == 0
                        || Data.userData.getGender() == 0) ? View.GONE : View.VISIBLE);
                tvGender.setText(getString(R.string.gender)+": "+Data.userData.getGenderName(this));
                tvDateOfBirth.setVisibility((Prefs.with(this).getInt(Constants.KEY_DRIVER_DOB_INPUT, 0) == 0
                        || TextUtils.isEmpty(Data.userData.getDateOfBirth())) ? View.GONE : View.VISIBLE);
                if(!TextUtils.isEmpty(Data.userData.getDateOfBirth())) {
                    tvDateOfBirth.setText(getString(R.string.hint_date_of_birth)+": "+DateOperations.utcToLocalWithTZFallback(Data.userData.getDateOfBirth()).split(" ")[0]);
                } else {
                    tvDateOfBirth.setText("");
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setVehicleModelData(){
        boolean showVehicleSettings = Prefs.with(this).getInt(Constants.KEY_ENABLE_VEHICLE_EDIT_SETTING,0)==1;
        VehicleDetailsLogin vehicleMakeInfo = Data.userData.getVehicleDetailsLogin();


        if(!showVehicleSettings){
            vehicleDetails.setVisibility(View.GONE);
            return;
        }

        vehicleDetails.setVisibility(View.VISIBLE);


        if(Data.userData.getVehicleDetailsLogin()!=null){
            List<VehicleDetail> details = new ArrayList<>(7);
            details.add(new VehicleDetail(getString(R.string.make),vehicleMakeInfo.getVehicleMake()));
            details.add(new VehicleDetail(getString(R.string.model),vehicleMakeInfo.getVehicleModel()));
            details.add(new VehicleDetail(getString(R.string.color),vehicleMakeInfo.getColor()));
            details.add(new VehicleDetail(getString(R.string.number_of_doors),vehicleMakeInfo.getDoors()));
            details.add(new VehicleDetail(getString(R.string.no_of_seat_belts),vehicleMakeInfo.getSeatbelts()));
            details.add(new VehicleDetail(getString(R.string.vehicle_number),vehicleMakeInfo.getVehicleNumber()));
            details.add(new VehicleDetail(getString(R.string.year),vehicleMakeInfo.getYear()));


            if(rvVehicleTypes.getAdapter()==null){
                vehicleDetailsProfileAdapter = new VehicleDetailsProfileAdapter();
                rvVehicleTypes.setAdapter(vehicleDetailsProfileAdapter);
            }
            vehicleDetailsProfileAdapter.setList(details);

            rvVehicleTypes.setVisibility(View.VISIBLE);

        }else{
            rvVehicleTypes.setVisibility(View.GONE);

        }

        ivEditVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openVehicleModelFragment();


            }
        });

    }

    private void openVehicleModelFragment() {
        if(getSupportFragmentManager().findFragmentByTag(VehicleDetailsFragment.class.getName())==null){
            int cityId = Prefs.with(DriverProfileActivity.this).getInt(SPLabels.CITY_ID,1);
            int vehicleType = Prefs.with(DriverProfileActivity.this).getInt(SPLabels.VEHICLE_TYPE,0);

            VehicleDetailsFragment vehicleDetailsFragment = VehicleDetailsFragment.newInstance(Data.userData.accessToken,
                    String.valueOf(cityId),
                    String.valueOf(vehicleType),
                    Data.userData.userName, Data.userData.getVehicleDetailsLogin(),true);

            getSupportFragmentManager().beginTransaction().add(R.id.container, vehicleDetailsFragment,VehicleDetailsFragment.class.getSimpleName()).
                    addToBackStack(VehicleDetailsFragment.class.getSimpleName())
                    .commitAllowingStateLoss();
            title.setText(getString(R.string.edit_vehicle_details));
            terms.setVisibility(View.GONE);
        }
    }


    @Override
    public void onDetailsUpdated(@NotNull VehicleDetailsLogin vehicleDetails) {
        if(Data.userData!=null){
            Data.userData.setVehicleDetailsLogin(vehicleDetails);
            setVehicleModelData();
        }
        onBackPressed();
    }
}

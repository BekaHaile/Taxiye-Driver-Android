package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import product.clicklabs.jugnoo.driver.adapters.SearchListAdapter;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.SearchResultNew;
import product.clicklabs.jugnoo.driver.fragments.PlaceSearchListFragment;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

import static com.crashlytics.android.beta.Beta.TAG;

public class DestinationRideActivity extends AppCompatActivity implements SearchListAdapter.SearchListActionsHandler {

    TextView title, tvSetDestRide, tvTimer, tvAddressType, tvAddress;
    RecyclerView rvSavedDest;
    ImageView ivAddDestRide, backBtn;
    Dialog addtypeDialog;
    ImageView radioSelected;
    SearchResultNew searchResult;
    String selectedAddType;
    RelativeLayout relative;
    LinearLayout destRideEnabledView;
    int addressSelected = -1;
    CountDownTimer countDownTimer;
    EditText etOther;
    private static final int START = 1;
    private static final int STOP = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination_ride);
        initViews();
        setListeners();
        if (Data.userData.currDestRideObj != null) {
            showDestRideEnabledView();
        } else {
            hideDestRideEnabledView();
        }
    }

    @Override
    public void onTextChange(String text) {

    }

    @Override
    public void onSearchPre() {

    }

    @Override
    public void onSearchPost() {

    }

    @Override
    public void onPlaceClick(SearchResultNew autoCompleteSearchResult) {

    }

    @Override
    public void onPlaceSearchPre() {

    }

    @Override
    public void onPlaceSearchPost(SearchResultNew searchResult) {
        tvSetDestRide.setText(getString(R.string.set_destination));
        this.searchResult = searchResult;
        createDestTypeDialog();
        addtypeDialog.show();

    }

    @Override
    public void onPlaceSearchError() {

    }

    @Override
    public void onPlaceSaved() {

    }


    private void initViews() {
        title = findViewById(R.id.title);
        rvSavedDest = findViewById(R.id.rvSavedDest);
        rvSavedDest.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvSavedDest.setAdapter(new SavedAddressAdapter(this, Data.userData.getSavedAddressList()));
        ivAddDestRide = findViewById(R.id.ivAddDestRide);
        tvAddressType = findViewById(R.id.textViewSearchName);
        tvTimer = findViewById(R.id.tvTimer);
        tvAddress = findViewById(R.id.textViewSearchAddress);
        destRideEnabledView = findViewById(R.id.destRideEnabledView);
        backBtn = findViewById(R.id.backBtn);
        tvSetDestRide = findViewById(R.id.tvSetDestRide);
        if (Data.userData.getSavedAddressList().isEmpty())
            tvSetDestRide.setText(R.string.add_destination);
        else
            tvSetDestRide.setText(R.string.set_destination);

    }

    private void setListeners() {
        backBtn.setOnClickListener(view -> DestinationRideActivity.this.onBackPressed());

        ivAddDestRide.setOnClickListener(view -> getSupportFragmentManager().beginTransaction()
                .add(R.id.relativeLayoutContainer, PlaceSearchListFragment.newInstance(), PlaceSearchListFragment.class.getName())
                .addToBackStack(PlaceSearchListFragment.class.getName())
                .commit());

        tvSetDestRide.setOnClickListener(view -> {
            if (tvSetDestRide.getText().toString().equalsIgnoreCase(getString(R.string.add_destination))) {
                ivAddDestRide.performClick();
            } else {
                if (Data.userData.currDestRideObj == null)
                    startDestinationRide(START);
                else
                    startDestinationRide(STOP);
            }
        });

    }


    public void createDestTypeDialog() {
        addtypeDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        addtypeDialog.setContentView(R.layout.dialog_destinstion_ride);
        WindowManager.LayoutParams layoutParams = Objects.requireNonNull(addtypeDialog.getWindow()).getAttributes();
        layoutParams.dimAmount = 0.6f;
        addtypeDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        addtypeDialog.setCancelable(true);
        addtypeDialog.setCanceledOnTouchOutside(true);
        addtypeDialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
        addtypeDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        addtypeDialog.findViewById(R.id.llHome).setOnClickListener(view -> {
            selectedAddType = "Home";
            selectRadio(view.findViewById(R.id.ivRadioHome));
            addtypeDialog.findViewById(R.id.etOther).setVisibility(View.GONE);
        });
        addtypeDialog.findViewById(R.id.llWork).setOnClickListener(view -> {
            selectedAddType = "Work";
            selectRadio(view.findViewById(R.id.ivRadioWork));
            addtypeDialog.findViewById(R.id.etOther).setVisibility(View.GONE);
        });
        addtypeDialog.findViewById(R.id.llOther).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedAddType = "Other";
                selectRadio(view.findViewById(R.id.ivRadioOther));
                addtypeDialog.findViewById(R.id.etOther).setVisibility(View.VISIBLE);
            }
        });

        addtypeDialog.findViewById(R.id.btnCancel).setOnClickListener(view -> addtypeDialog.dismiss());
        addtypeDialog.findViewById(R.id.btnOk).setOnClickListener(view -> {
            if (searchResult != null) {

                String othr = ((EditText) addtypeDialog.findViewById(R.id.etOther)).getText().toString();
                if (selectedAddType.equalsIgnoreCase("Other")) {
                    if (othr.equalsIgnoreCase("")) {
                        ((EditText) addtypeDialog.findViewById(R.id.etOther)).setError("Please enter a name");
                        return;
                    }
                    selectedAddType = othr;

                }
                searchResult.setName(selectedAddType);
                DialogPopup.showLoadingDialog(DestinationRideActivity.this, getString(R.string.loading));
                addtypeDialog.dismiss();
                Data.userData.getSavedAddressList().add(searchResult);
                rvSavedDest.getAdapter().notifyDataSetChanged();
                HomeUtil.saveAddress(DestinationRideActivity.this, searchResult, false);

            }
        });

    }

    public void addressSaved() {
//        Fragment fragment = getSupportFragmentManager().findFragmentByTag(PlaceSearchListFragment.class.getName());
//        if (fragment != null) {
//            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
//        }
        getSupportFragmentManager().popBackStack(PlaceSearchListFragment.class.getName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);

        rvSavedDest.getAdapter().notifyDataSetChanged();

    }

    private void selectRadio(ImageView view) {
        if (radioSelected != null)
            radioSelected.setImageDrawable(getResources().getDrawable(R.drawable.radio_unslelcet));
        radioSelected = view;

        view.setImageDrawable(getResources().getDrawable(R.drawable.radio_select));

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void startDestinationRide(int status) {

        HashMap<String, String> params = new HashMap<>();
        params.put(Constants.KEY_FLAG, "" + status);
        params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
        if (status == START) {
            if (addressSelected < 0) {
                Utils.showToast(this, "Please select an address");
                return;
            }
            params.put(Constants.KEY_ADDRESS_ID, "" + Data.userData.getSavedAddressList().get(addressSelected).getPlaceId());
        }


        RestClient.getApiServices().toggleDriverDest(params, new Callback<Object>() {
            @Override
            public void success(Object o, Response response) {
                String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                Log.i(TAG, "addHomeAndWorkAddress response = " + responseStr);
                DialogPopup.dismissLoadingDialog();
                try {
                    JSONObject jObj = new JSONObject(responseStr);
                    int flag = jObj.optInt(Constants.KEY_FLAG, ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
                    String message = JSONParser.getServerMessage(jObj);
                    if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                        if (jObj.has("time")) {
                            if (addressSelected > -1) {
                                SearchResultNew searchResultSelected = Data.userData.getSavedAddressList().get(addressSelected);
                                Data.userData.currDestRideObj = Data.userData.new CurrDestRide(searchResultSelected.getAddress(), searchResultSelected.getLatitude(), searchResultSelected.getLongitude(), jObj.getInt("time"), System.currentTimeMillis(), searchResultSelected.getName());
                                showDestRideEnabledView();
                            }

                        } else {
                            Data.userData.currDestRideObj = null;
                            hideDestRideEnabledView();
                        }
                        DialogPopup.alertPopup(DestinationRideActivity.this, "", message);

                    } else {
                        DialogPopup.alertPopup(DestinationRideActivity.this, "", message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                try {
                    DialogPopup.dismissLoadingDialog();
                    DialogPopup.alertPopup(DestinationRideActivity.this, "", DestinationRideActivity.this.getString(R.string.error_occured_tap_to_retry));
                } catch (Exception e) {
                    DialogPopup.dismissLoadingDialog();
                    e.printStackTrace();
                }
            }
        });
    }

    public void hideDestRideEnabledView() {
        tvSetDestRide.setText(R.string.set_destination);
        destRideEnabledView.setVisibility(View.GONE);
        rvSavedDest.setVisibility(View.VISIBLE);
    }

    public void showDestRideEnabledView() {
        destRideEnabledView.setVisibility(View.VISIBLE);
        rvSavedDest.setVisibility(View.GONE);
        tvAddressType.setText(Data.userData.currDestRideObj.getType());
        tvAddress.setText(Data.userData.currDestRideObj.getAddress());
        tvSetDestRide.setText(R.string.disable);
        if (countDownTimer != null)
            countDownTimer.cancel();
        countDownTimer = new CountDownTimer(Data.userData.currDestRideObj.getDestinationRideTimeRem() * 1000, 1000) {

            public void onTick(long millisUntilFinished) {

                String time = millisUntilFinished / (1000 * 60 * 60) + " : " + millisUntilFinished / (1000 * 60) % 60 + " : " + (millisUntilFinished / 1000) % 60;
                tvTimer.setText(time);
            }

            public void onFinish() {
                DialogPopup.alertPopup(DestinationRideActivity.this, "", DestinationRideActivity.this.getString(R.string.dest_trip_disabled));
                Data.userData.currDestRideObj=null;
                hideDestRideEnabledView();
            }
        };
        countDownTimer.start();
    }


    public class SavedAddressAdapter extends RecyclerView.Adapter<SavedAddressAdapter.Viewholder> {
        Activity activity;
        ArrayList<SearchResultNew> savedAddlist;

        SavedAddressAdapter(Activity activity, ArrayList<SearchResultNew> savedAddList) {
            this.activity = activity;
            this.savedAddlist = savedAddList;
        }

        @NonNull
        @Override
        public SavedAddressAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_destination_ride, parent, false);
            return new Viewholder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull SavedAddressAdapter.Viewholder holder, int position) {
            String name = savedAddlist.get(position).getName();
            String address = savedAddlist.get(position).getAddress();
            holder.textViewSearchName.setText(name == null ? "" : name);
            holder.textViewSearchAddress.setText(address == null ? "" : address);
        }

        @Override
        public int getItemCount() {
            return savedAddlist == null ? 0 : savedAddlist.size();
        }

        public class Viewholder extends RecyclerView.ViewHolder {
            View root;
            ImageView imageViewType, ivDeleteAddress;
            TextView textViewSearchName, textViewSearchAddress;

            public Viewholder(View itemView) {
                super(itemView);
                root = itemView;
                relative = root.findViewById(R.id.relative);
                imageViewType = root.findViewById(R.id.imageViewType);
                textViewSearchName = root.findViewById(R.id.textViewSearchName);
                textViewSearchAddress = root.findViewById(R.id.textViewSearchAddress);
                ivDeleteAddress = root.findViewById(R.id.ivDeleteAddress);
                relative.setOnClickListener(view -> {
                    addressSelected = getAdapterPosition();
                    selectRadio(imageViewType);
                });
                ivDeleteAddress.setOnClickListener(view -> {
                    HomeUtil.saveAddress(activity, Data.userData.getSavedAddressList().get(getAdapterPosition()), true);
                });
            }
        }
    }
}


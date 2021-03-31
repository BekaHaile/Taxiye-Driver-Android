package product.clicklabs.jugnoo.driver.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.picker.Country;
import com.picker.CountryPicker;
import com.picker.OnCountryPickerListener;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeUtil;
import product.clicklabs.jugnoo.driver.JSONParser;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by Parminder Saini on 25/05/18.
 */
public class SendCreditsFragment extends BaseFragment {

    private Activity activity;
    private TextView tvCountryCode;
    public EditText etCredits, etPhoneNumber;
    private CountryPicker countryPicker;
    RadioButton rbCustomer,rbDriver;

    public static SendCreditsFragment newInstance() {
        Bundle args = new Bundle();

        SendCreditsFragment fragment = new SendCreditsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_send_credits, container, false);

        ((TextView) rootView.findViewById(R.id.tvCredits)).setTypeface(Fonts.mavenRegular(activity));
        ((TextView) rootView.findViewById(R.id.tvPhoneNumber)).setTypeface(Fonts.mavenRegular(activity));

        ImageView ivBackground = (ImageView) rootView.findViewById(R.id.ivBackground);
        etCredits = (EditText) rootView.findViewById(R.id.etCredits); etCredits.setTypeface(Fonts.mavenRegular(activity));
        etPhoneNumber = (EditText) rootView.findViewById(R.id.etPhoneNumber); etPhoneNumber.setTypeface(Fonts.mavenRegular(activity));
        tvCountryCode = (TextView) rootView.findViewById(R.id.tvCountryCode); tvCountryCode.setTypeface(Fonts.mavenRegular(activity));
        rbCustomer = (RadioButton) rootView.findViewById(R.id.rbCustomer); rbCustomer.setTypeface(Fonts.mavenRegular(activity));
        rbDriver = (RadioButton) rootView.findViewById(R.id.rbDriver); rbDriver.setTypeface(Fonts.mavenRegular(activity));
        TextView tvCreditsLeft = (TextView) rootView.findViewById(R.id.tvCreditsLeft); tvCreditsLeft.setTypeface(Fonts.mavenRegular(activity));
        countryPicker = new CountryPicker.Builder().with(activity)
                        .listener(new OnCountryPickerListener<Country>() {
                            @Override
                            public void onSelectCountry(Country country) {
                                tvCountryCode.setText(country.getDialCode());
                            }
                        })
                        .build();

        rbDriver.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isPressed()) {
                    rbDriver.setChecked(true);
                    rbCustomer.setChecked(false);
                }
            }
        });
        rbCustomer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isPressed()) {
                    rbDriver.setChecked(false);
                    rbCustomer.setChecked(true);
                }
            }
        });
        tvCountryCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryPicker.showDialog(getChildFragmentManager());
            }
        });
        tvCountryCode.setText(Utils.getCountryCode(activity));

        Button bSend = (Button) rootView.findViewById(R.id.bSend); bSend.setTypeface(Fonts.mavenRegular(activity));
        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!rbCustomer.isChecked()&&!rbDriver.isChecked()){
                    Utils.showToast(activity, getString(R.string.select_transfer_to));
                    return;
                }
                double creditsTotal = (Data.userData == null || Data.userData.creditsEarned == null) ? 0.0 : Data.userData.creditsEarned;
                if(tvCountryCode.getText().toString().length() == 0){
                    Utils.showToast(activity, getString(R.string.please_select_country_code));
                    return;
                }
                if(etCredits.getText().toString().length() == 0){
                    Utils.showToast(activity, getString(R.string.please_enter_something));
                    return;
                }
                if(etPhoneNumber.getText().toString().length() == 0){
                    Utils.showToast(activity, getString(R.string.phone_no_cnt_be_empty));
                    return;
                }
                String countryCode = tvCountryCode.getText().toString();
                String credits = etCredits.getText().toString().trim();
//                if(creditsTotal < Double.parseDouble(credits)){
//                    Utils.showToast(activity, getString(R.string.please_enter_less_value));
//                    return;
//                }
                String phoneNumber = Utils.retrievePhoneNumberTenChars(countryCode, etPhoneNumber.getText().toString().trim());
                if(!Utils.validPhoneNumber(phoneNumber)){
                    Utils.showToast(activity, getString(R.string.please_enter_valid_phone));
                    return;
                }
                sendCreditsApi(countryCode, countryCode+phoneNumber, credits);
            }
        });

        if(Data.userData != null){
            tvCreditsLeft.setText(getString(R.string.credits_left_format,
                    Utils.formatCurrencyValue(Data.userData.getCurrency(),
                            Data.userData.creditsEarned == null ? 0.0 : Data.userData.creditsEarned)));
        }



        return rootView;
    }

    public enum UserType {
        CUSTOMER(0),
        DRIVER(1)
        ;

        private int ordinal;

        private UserType(int ordinal) {
            this.ordinal = ordinal;
        }

        public int getOrdinal() {
            return ordinal;
        }
    }
    private void sendCreditsApi(String countryCode, String phoneNo, String credits) {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
            params.put(Constants.KEY_PHONE_NO, phoneNo);
            params.put(Constants.KEY_COUNTRY_CODE, countryCode);
            params.put(Constants.KEY_AMOUNT, credits);
            params.put(Constants.KEY_LOGIN_TYPE, ""+UserType.DRIVER.getOrdinal());
            params.put(Constants.KEY_RECIEVER_TYPE, ""+(rbCustomer.isChecked()?UserType.CUSTOMER.getOrdinal():UserType.DRIVER.getOrdinal()));
            HomeUtil.putDefaultParams(params);
            DialogPopup.showLoadingDialog(activity, getString(R.string.loading));
            RestClient.getApiServices().sendCredits(params, new Callback<SettleUserDebt>() {
                @Override
                public void success(SettleUserDebt rateCardResponse, Response response) {
                    try {
                        String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
                        JSONObject jObj = new JSONObject(jsonString);
                        String message = JSONParser.getServerMessage(jObj);
                        int flag = jObj.getInt(Constants.KEY_FLAG);

                        if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                            if(Data.userData != null) {
                                Data.userData.creditsEarned = jObj.has(Constants.KEY_CREDIT_BALANCE) ? jObj.optDouble(Constants.KEY_CREDIT_BALANCE) : null;
                            }
                            DialogPopup.alertPopup(activity, "", message, true, true, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    activity.onBackPressed();
                                }
                            });
                            activity.onBackPressed();
                        } else {
                            DialogPopup.alertPopup(activity, "", message);
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        DialogPopup.alertPopup(activity, "", getString(R.string.server_error));
                    }
                    DialogPopup.dismissLoadingDialog();
                }
                @Override
                public void failure(RetrofitError error) {
                    Log.i("error", String.valueOf(error));
                    DialogPopup.dismissLoadingDialog();
                    DialogPopup.alertPopup(activity, "", getString(R.string.server_not_responding));

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            DialogPopup.alertPopup(activity, "", getString(R.string.check_internet_message));
        }
    }

    @Override
    public String getTitle() {
        return getString(R.string.send_to_a_friend);
    }
}

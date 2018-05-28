package product.clicklabs.jugnoo.driver.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    private EditText etCredits, etPhoneNumber;
    private CountryPicker countryPicker;

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
        countryPicker = new CountryPicker.Builder().with(activity)
                        .listener(new OnCountryPickerListener() {
                            @Override
                            public void onSelectCountry(Country country) {
                                tvCountryCode.setText(country.getDialCode());
                            }
                        })
                        .build();
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
                String phoneNumber = Utils.retrievePhoneNumberTenChars(countryCode, etPhoneNumber.getText().toString().trim());
                if(!Utils.validPhoneNumber(phoneNumber)){
                    Utils.showToast(activity, getString(R.string.please_enter_valid_phone));
                    return;
                }
                sendCreditsApi(countryCode, phoneNumber, credits);
            }
        });




        return rootView;
    }


    private void sendCreditsApi(String countryCode, String phoneNo, String credits) {
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
            params.put(Constants.KEY_PHONE_NO, phoneNo);
            params.put(Constants.KEY_COUNTRY_CODE, countryCode);
            params.put(Constants.KEY_CREDITS, credits);
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
                            DialogPopup.alertPopup(activity, "", message, true, true, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    activity.onBackPressed();
                                }
                            });
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

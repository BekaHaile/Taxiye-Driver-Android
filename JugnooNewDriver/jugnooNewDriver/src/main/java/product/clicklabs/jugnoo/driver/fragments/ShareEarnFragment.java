package product.clicklabs.jugnoo.driver.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.picker.Country;
import com.picker.CountryPicker;
import com.picker.OnCountryPickerListener;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeUtil;
import product.clicklabs.jugnoo.driver.MyApplication;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.ShareActivity;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.FirebaseEvents;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.NudgeClient;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class ShareEarnFragment extends Fragment {

    Button buttonShare;
    TextView textViewReferralCodeDisplay, textViewReferralCodeValue;
    TextView textViewShareReferral;
    SpannableString sstr;
    private LinearLayout linearLayoutRoot;
    private View rootView;
    private ShareActivity activity;

    @Override
    public void onStart() {
        super.onStart();
        FlurryAgent.init(activity, Data.FLURRY_KEY);
        FlurryAgent.onStartSession(activity, Data.FLURRY_KEY);
        FlurryAgent.onEvent(ShareEarnFragment.class.getSimpleName() + " started");
    }

    @Override
    public void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(activity);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_share_earn, container, false);

        activity = (ShareActivity) getActivity();

        linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
        new ASSL(activity, linearLayoutRoot, 1134, 720, false);


        buttonShare = (Button) rootView.findViewById(R.id.buttonShare);
        buttonShare.setText(Data.userData.referralButtonText);

        textViewReferralCodeDisplay = (TextView) rootView.findViewById(R.id.textViewReferralCodeDisplay);
        textViewReferralCodeDisplay.setTypeface(Data.latoRegular(activity));
        textViewReferralCodeValue = (TextView) rootView.findViewById(R.id.textViewReferralCodeValue);
        textViewReferralCodeValue.setTypeface(Data.latoRegular(activity));
        textViewReferralCodeValue.setTextColor(getResources().getColor(R.color.musturd_jugnoo));
        textViewShareReferral = (TextView) rootView.findViewById(R.id.textViewShareReferral);
        textViewShareReferral.setTypeface(Data.latoRegular(activity));

        try {
            sstr = new SpannableString(Data.userData.referralCode);
            final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
            final ForegroundColorSpan clrs = new ForegroundColorSpan(Color.parseColor("#FAA31C"));
            sstr.setSpan(bss, 0, sstr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sstr.setSpan(clrs, 0, sstr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


            textViewReferralCodeDisplay.setText("");
            textViewReferralCodeDisplay.append(getResources().getString(R.string.your_referral_code));
            textViewReferralCodeValue.setText(sstr);
            textViewReferralCodeValue.setTypeface(Data.latoHeavy(activity));

            textViewShareReferral.setText(Data.userData.referralMessage);

        } catch (Exception e) {
            e.printStackTrace();
            activity.finish();
            activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
        }

        buttonShare.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                MyApplication.getInstance().logEvent(FirebaseEvents.INVITE_AND_EARN + "_" + FirebaseEvents.SHARE, null);
                confirmCustomerNumberPopup(activity);
                NudgeClient.trackEvent(getActivity(), FlurryEventNames.NUDGE_INVITE_EARN_CLICK, null);
            }
        });

        return rootView;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            ASSL.closeActivity(linearLayoutRoot);
        } catch (Exception e) {
        }
        System.gc();
    }

    public void confirmCustomerNumberPopup(final FragmentActivity activity) {

        try {
            final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
            dialog.setContentView(R.layout.dialog_share_enter_number);

            RelativeLayout frameLayout = (RelativeLayout) dialog.findViewById(R.id.rv);
            new ASSL(activity, frameLayout, 1134, 720, true);

            WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
            layoutParams.dimAmount = 0.6f;
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);


            TextView textViewDialogTitle = (TextView) dialog.findViewById(R.id.textViewDialogTitle);
            textViewDialogTitle.setTypeface(Data.latoRegular(activity));
            final EditText customerNumber = (EditText) dialog.findViewById(R.id.customerNumber);
            final TextView tvCountryCode = (TextView) dialog.findViewById(R.id.tvCountryCode);
            tvCountryCode.setText(Utils.getCountryCode(activity));
            final CountryPicker countryPicker = new CountryPicker.Builder().with(activity)
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
                    countryPicker.showDialog(activity.getSupportFragmentManager());
                }
            });
            customerNumber.setTypeface(Data.latoRegular(activity));
            customerNumber.setHint(Data.userData.referralDialogHintText);
            textViewDialogTitle.setText(Data.userData.referralDialogText);

            final Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
            btnOk.setTypeface(Data.latoRegular(activity));
            final Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
            btnCancel.setTypeface(Data.latoRegular(activity));

            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        String code = customerNumber.getText().toString().trim();
                        if(TextUtils.isEmpty(tvCountryCode.getText().toString())){
                            Toast.makeText(activity, getString(R.string.please_select_country_code), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if ("".equalsIgnoreCase(code)) {
                            customerNumber.requestFocus();
                            customerNumber.setError(getResources().getString(R.string.phone_no_cnt_be_empty));
                        } else {
                            code = Utils.retrievePhoneNumberTenChars(tvCountryCode.getText().toString(), code);
                            if (!Utils.validPhoneNumber(code)) {
                                customerNumber.requestFocus();
                                customerNumber.setError(getResources().getString(R.string.valid_phone_number));
                            } else {
                                sendReferralMessage(activity, code, tvCountryCode.getText().toString());
                                MyApplication.getInstance().logEvent(FirebaseEvents.INVITE_AND_EARN + "_"
                                        + FirebaseEvents.SHARE + "_" + FirebaseEvents.CONFIRM_YES, null);
                                dialog.dismiss();
                                NudgeClient.trackEvent(getActivity(), FlurryEventNames.NUDGE_SHARE_OK, null);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }

                }

            });
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NudgeClient.trackEvent(getActivity(), FlurryEventNames.NUDGE_SHARE_CANCEL, null);
                    MyApplication.getInstance().logEvent(FirebaseEvents.INVITE_AND_EARN + "_"
                            + FirebaseEvents.SHARE + "_" + FirebaseEvents.CONFIRM_NO, null);
                    dialog.dismiss();
                }
            });
            customerNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                    int result = actionId & EditorInfo.IME_MASK_ACTION;
                    switch (result) {
                        case EditorInfo.IME_ACTION_DONE:
                            btnOk.performClick();
                            break;

                        case EditorInfo.IME_ACTION_NEXT:
                            break;

                        default:
                    }
                    return true;
                }
            });

            dialog.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Utils.showSoftKeyboard(activity, customerNumber);
                }
            }, 200);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void sendReferralMessage(final Activity activity, String phone_no, String countryCode) {
        try {
            if (AppStatus.getInstance(activity).isOnline(activity)) {

                DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

                HashMap<String, String> params = new HashMap<String, String>();

                params.put("access_token", Data.userData.accessToken);
                params.put("phone_no", phone_no);
                params.put(Constants.KEY_COUNTRY_CODE, countryCode);
                HomeUtil.putDefaultParams(params);
                Log.i("params", "=" + params);

                RestClient.getApiServices().sendReferralMessage(params, new Callback<RegisterScreenResponse>() {
                    @Override
                    public void success(RegisterScreenResponse registerScreenResponse, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        try {
                            JSONObject jObj = new JSONObject(responseStr);
                            int flag = jObj.getInt("flag");
                            if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                                DialogPopup.alertPopup(activity, "", jObj.getString("message"));
                            } else {
                                DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                        }
                        DialogPopup.dismissLoadingDialog();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("request fail", error.toString());
                        DialogPopup.dismissLoadingDialog();
                        DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
                    }
                });
            } else {
                DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

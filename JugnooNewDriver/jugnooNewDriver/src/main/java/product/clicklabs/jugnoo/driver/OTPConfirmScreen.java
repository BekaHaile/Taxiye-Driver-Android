package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;


import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.EmailRegisterData;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.BookingHistoryResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppConstants;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.CustomCountDownTimer;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.OtpDialog;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class OTPConfirmScreen extends BaseActivity implements CustomCountDownTimer.DownTimerOperation, LocationUpdate {

    TextView title;
    View backBtn;

    TextView textViewCounter, textViewOr, phoneNoEt;
    EditText editTextOTP;
    Button buttonVerify, btnLogin;

    RelativeLayout relative, relativeLayoutFake;
    ImageView imageViewYellowLoadingBar, imageViewChangePhoneNumber, btnOtpViaCall;
    LinearLayout layoutResendOtp, btnReGenerateOtp;
    String knowlarityMissedCallNumber = "";
    String phoneNumberToVerify = "", countryCode;
    public static String OTP_SCREEN_OPEN = null;
    boolean loginDataFetched = false;

    public static boolean intentFromRegister = false;
    public static EmailRegisterData emailRegisterData;


    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    protected void onNewIntent(Intent intent) {

        if (intent.hasExtra("message")) {
            retrieveOTPFromSMS(intent);
        }
        super.onNewIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_confrim);
        Utils.enableReceiver(OTPConfirmScreen.this, IncomingSmsReceiverReg.class, true);
        loginDataFetched = false;

        relative = (RelativeLayout) findViewById(R.id.relative);
        new ASSL(OTPConfirmScreen.this, relative, 1134, 720, false);

        title = (TextView) findViewById(R.id.title);
        title.setTypeface(Fonts.mavenRegular(this));
        title.setText(R.string.verification);

        relativeLayoutFake = (RelativeLayout) findViewById(R.id.relativeLayoutFake);
		relativeLayoutFake.setVisibility(View.GONE);
        phoneNoEt = (TextView) findViewById(R.id.phoneNoEt);
        phoneNoEt.setTypeface(Fonts.mavenRegular(this));
        editTextOTP = (EditText) findViewById(R.id.otpEt);
        editTextOTP.setTypeface(Fonts.mavenRegular(this));
        editTextOTP.setCursorVisible(true);
        buttonVerify = (Button) findViewById(R.id.verifyOtp);
        buttonVerify.setTypeface(Fonts.mavenRegular(this));

        backBtn = findViewById(R.id.backBtn);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setTypeface(Fonts.mavenRegular(this));

        layoutResendOtp = (LinearLayout) findViewById(R.id.layoutResendOtp);
        btnReGenerateOtp = (LinearLayout) findViewById(R.id.btnReGenerateOtp);
        btnOtpViaCall = (ImageView) findViewById(R.id.btnOtpViaCall);
//        textViewCounter = (TextView) findViewById(R.id.textViewCounter);
//        textViewCounter.setTypeface(Fonts.mavenRegular(getApplicationContext()));

//		((TextView) findViewById(R.id.textViewBtnOtpViaCall)).setTypeface(Fonts.mavenRegular(this));
        textViewOr = (TextView) findViewById(R.id.textViewOr);
        textViewOr.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
        ((TextView) findViewById(R.id.textViewbtnReGenerateOtp)).setTypeface(Fonts.mavenRegular(this));
        imageViewYellowLoadingBar = (ImageView) findViewById(R.id.imageViewYellowLoadingBar);
        imageViewChangePhoneNumber = (ImageView) findViewById(R.id.imageViewChangePhoneNumber);
        phoneNumberToVerify = null;
        countryCode = Utils.getCountryCode(this);

        btnReGenerateOtp.setVisibility(View.GONE);
        try {
            phoneNumberToVerify = getIntent().getStringExtra(Constants.PHONE_NO_VERIFY);
            countryCode = getIntent().getStringExtra(Constants.KEY_COUNTRY_CODE);
            if (!TextUtils.isEmpty(phoneNumberToVerify)) {
                phoneNoEt.setText(phoneNumberToVerify);

                layoutResendOtp.setVisibility(View.GONE);
                editTextOTP.setEnabled(true);
                knowlarityMissedCallNumber = getIntent().getStringExtra(Constants.KNOWLARITY_NO);
                Prefs.with(OTPConfirmScreen.this).save(SPLabels.REQUEST_LOGIN_OTP_FLAG, "true");
                try {
                    otpDetectionPopup();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        backBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                performBackPressed();
            }
        });

        editTextOTP.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    editTextOTP.setError(null);
                }
            }
        });


        buttonVerify.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String otpCode = editTextOTP.getText().toString().trim();
                if (otpCode.length() > 0) {

                    if (phoneNumberToVerify == null) {
                        sendSignupValues(OTPConfirmScreen.this, otpCode);
                    } else {
                        sendSignupValuesToEdit(OTPConfirmScreen.this, phoneNumberToVerify, otpCode, countryCode);
                    }
                    FlurryEventLogger.otpConfirmClick(otpCode);
                } else {
                    editTextOTP.requestFocus();
                    editTextOTP.setError(getResources().getString(R.string.code_empty));
                }

            }
        });

        editTextOTP.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                int result = actionId & EditorInfo.IME_MASK_ACTION;
                switch (result) {
                    case EditorInfo.IME_ACTION_DONE:
                        buttonVerify.performClick();
                        break;

                    case EditorInfo.IME_ACTION_NEXT:
                        break;

                    default:
                }
                return true;
            }
        });


        btnReGenerateOtp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                try {
                    phoneNoEt.setHint(phoneNumberToVerify);
                    phoneNoEt.setEnabled(false);
					editTextOTP.setText("");
					editTextOTP.setError(null);
                    generateOTP(phoneNumberToVerify);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        btnOtpViaCall.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!"".equalsIgnoreCase(knowlarityMissedCallNumber)) {
                    try {
                        DialogPopup.alertPopupTwoButtonsWithListeners(OTPConfirmScreen.this, "",
                                getResources().getString(R.string.give_missed_call_dialog_text),
                                getResources().getString(R.string.call_us),
                                getResources().getString(R.string.cancel),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        layoutResendOtp.setVisibility(View.GONE);
                                        btnLogin.setVisibility(View.VISIBLE);
                                        Utils.openCallIntent(OTPConfirmScreen.this, knowlarityMissedCallNumber);
                                    }
                                },
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                    }
                                }, false, false
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSignupValues(OTPConfirmScreen.this, "99999");
            }
        });

        imageViewChangePhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performBackPressed();
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        try {
            if (emailRegisterData != null) {
                phoneNoEt.setText(phoneNumberToVerify);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        OTP_SCREEN_OPEN = "yes";
        Data.deviceToken  =  FirebaseInstanceId.getInstance().getToken();

    }


    @Override
    protected void onResume() {
        super.onResume();
        try {
			relativeLayoutFake.setVisibility(View.GONE);
            if (Data.locationFetcher == null) {
                Data.locationFetcher = new LocationFetcher(this, 1000, 1);
            }
            Data.locationFetcher.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (phoneNumberToVerify == null) {
            checkIfRegisterDataNull(this);
        }
    }

    @Override
    public void onPause() {
        try {
            if (Data.locationFetcher != null) {
                Data.locationFetcher.destroy();
                Data.locationFetcher = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    public static void checkIfRegisterDataNull(Activity activity) {
        try {
            if (emailRegisterData == null) {
                activity.startActivity(new Intent(activity, SplashNewActivity.class));
                activity.finish();
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //	Retrofit
    public void sendSignupValues(final Activity activity, String otp) {
        if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

            DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));

            if (Data.locationFetcher != null) {
                Data.latitude = Data.locationFetcher.getLatitude();
                Data.longitude = Data.locationFetcher.getLongitude();
            }
            HashMap<String, String> params = new HashMap<>();
            params.put("phone_no",phoneNumberToVerify);
            params.put("password",emailRegisterData.password);
            params.put("device_token",Data.deviceToken);
            params.put("device_type",Data.DEVICE_TYPE);
            params.put("device_name",Data.deviceName);
            params.put("app_version",Data.appVersion+"");
            params.put("os_version",Data.osVersion);
            params.put("country",Data.country);
            params.put("unique_device_id",Data.uniqueDeviceId);
            params.put("latitude",Data.latitude+"");
            params.put("longitude",Data.longitude+"");
            params.put("client_id",Data.CLIENT_ID);
            params.put("login_type",Data.LOGIN_TYPE);
            params.put("otp",otp);
            params.put("country_code",countryCode);
            HomeUtil.putDefaultParams(params);

            RestClient.getApiServices().verifyOtpUsingSignupFields(params , new Callback<BookingHistoryResponse>() {


                        @Override
                        public void success(BookingHistoryResponse bookingHistoryResponse, Response response) {
                            try {
                                String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
                                JSONObject jObj;
                                jObj = new JSONObject(jsonString);
                                int flag = jObj.getInt("flag");
                                String message = JSONParser.getServerMessage(jObj);
                                if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag, null)) {
                                    if (ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag) {
                                        DialogPopup.alertPopup(activity, "", message);
                                    } else if (ApiResponseFlags.AUTH_VERIFICATION_FAILURE.getOrdinal() == flag) {
                                        DialogPopup.alertPopup(activity, "", message);
                                    } else if (ApiResponseFlags.CUSTOMER_LOGGING_IN.getOrdinal() == flag) {
                                        DialogPopup.alertPopup(activity, "", message);
                                    } else if (ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag) {
                                        DialogPopup.alertPopup(activity, "", message);
                                        btnLogin.setVisibility(View.GONE);
                                        layoutResendOtp.setVisibility(View.VISIBLE);
                                        if (customCountDownTimer != null)
                                            customCountDownTimer.cancel();
                                        btnReGenerateOtp.setVisibility(View.GONE);
                                    } else if (ApiResponseFlags.UPLOAD_DOCCUMENT.getOrdinal() == flag) {
                                        Utils.enableReceiver(OTPConfirmScreen.this, IncomingSmsReceiverReg.class, false);
                                        JSONParser.saveAccessToken(activity, jObj.getString("access_token"));
                                        Intent intent = new Intent(OTPConfirmScreen.this, DriverDocumentActivity.class);
                                        intent.putExtra("access_token", jObj.getString("access_token"));
                                        intent.putExtra("in_side", false);
										intent.putExtra("doc_required", 3);
										relativeLayoutFake.setVisibility(View.VISIBLE);
                                        startActivity(intent);
                                    } else {
                                        DialogPopup.alertPopup(activity, "", message);
                                    }
                                    DialogPopup.dismissLoadingDialog();
                                } else {
                                    DialogPopup.dismissLoadingDialog();
                                }

                            } catch (Exception exception) {
                                DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                                DialogPopup.dismissLoadingDialog();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            DialogPopup.dismissLoadingDialog();
                            DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
                        }
                    });


        } else {
            DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
        }

    }

    public void sendSignupValuesToEdit(final Activity activity, final String phoneNo, String otp, final String countryCode) {
        try {
            if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
                DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));
                HashMap<String, String> params = new HashMap<>();

                params.put("client_id", Data.CLIENT_ID);
                params.put("access_token", Data.userData.accessToken);
                params.put("is_access_token_new", "1");
                params.put("phone_no", phoneNo);
                params.put(Constants.KEY_COUNTRY_CODE, countryCode);
                params.put("verification_token", otp);
                HomeUtil.putDefaultParams(params);

                Log.i("params", ">" + params);

                RestClient.getApiServices().verifyMyContactNumber(params, new Callback<RegisterScreenResponse>() {
                    @Override
                    public void success(RegisterScreenResponse registerScreenResponse, Response response) {
                        Log.i("Server response", "response = " + response);
                        try {
                            String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                            JSONObject jObj = new JSONObject(responseStr);
                            int flag = jObj.getInt("flag");
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag, null)) {
                                if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
                                    String error = jObj.getString("error");
                                    DialogPopup.dialogBanner(activity, error);
                                } else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                                    String message = jObj.getString("message");
                                    Utils.enableReceiver(OTPConfirmScreen.this, IncomingSmsReceiverReg.class, false);
                                    DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            performBackPressed();
                                            Data.userData.phoneNo = phoneNo;
                                            Data.userData.setCountryCode(countryCode);
                                        }
                                    });
                                } else {
                                    DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                                }
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


//	Retrofit


    public void generateOTP(final String phoneNo) {
        try {
            if (AppStatus.getInstance(OTPConfirmScreen.this).isOnline(OTPConfirmScreen.this)) {
                DialogPopup.showLoadingDialog(OTPConfirmScreen.this, getResources().getString(R.string.loading));
                HashMap<String, String> params = new HashMap<>();
                params.put("phone_no", phoneNo);
                params.put(Constants.KEY_COUNTRY_CODE, countryCode);
                params.put("login_type", "1");
                HomeUtil.putDefaultParams(params);
                Prefs.with(OTPConfirmScreen.this).save(SPLabels.DRIVER_LOGIN_TIME, System.currentTimeMillis());

                RestClient.getApiServices().generateOtp(params, new Callback<RegisterScreenResponse>() {
                    @Override
                    public void success(RegisterScreenResponse registerScreenResponse, Response response) {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        DialogPopup.dismissLoadingDialog();
                        try {
                            JSONObject jObj = new JSONObject(responseStr);
                            String message = JSONParser.getServerMessage(jObj);
                            int flag = jObj.getInt("flag");
                            if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {

                                DialogPopup.dialogBanner(OTPConfirmScreen.this, message);
                                btnLogin.setVisibility(View.GONE);
                                layoutResendOtp.setVisibility(View.GONE);
                                btnReGenerateOtp.setVisibility(View.GONE);
                                editTextOTP.setEnabled(true);
                                knowlarityMissedCallNumber = jObj.optString("knowlarity_missed_call_number", "999");
                                Prefs.with(OTPConfirmScreen.this).save(SPLabels.REQUEST_LOGIN_OTP_FLAG, "true");
                                try {
                                    otpDetectionPopup();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                DialogPopup.alertPopup(OTPConfirmScreen.this, "", message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            DialogPopup.alertPopup(OTPConfirmScreen.this, "", Data.SERVER_ERROR_MSG);
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogPopup.dismissLoadingDialog();
                        DialogPopup.alertPopup(OTPConfirmScreen.this, "", Data.SERVER_ERROR_MSG);
                    }
                });
            } else {
                DialogPopup.alertPopup(OTPConfirmScreen.this, "", Data.CHECK_INTERNET_MSG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus && loginDataFetched) {
            loginDataFetched = false;
            startActivity(new Intent(OTPConfirmScreen.this, HomeActivity.class));
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        performBackPressed();
        super.onBackPressed();
    }


    public void performBackPressed() {
//        if (intentFromRegister) {
//            Intent intent = new Intent(OTPConfirmScreen.this, SplashNewActivity.class);
//            intent.putExtra("back_from_otp", true);
//            startActivity(intent);
//        } else if (phoneNumberToVerify != null) {
//            Intent intent = new Intent(OTPConfirmScreen.this, EditDriverProfile.class);
//            startActivity(intent);
//        } else {
//            Intent intent = new Intent(OTPConfirmScreen.this, LoginViaOTP.class);
//            intent.putExtra("back_from_otp", true);
//            startActivity(intent);
//        }
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.enableReceiver(OTPConfirmScreen.this, IncomingSmsReceiverReg.class, false);
        OTP_SCREEN_OPEN = null;
        ASSL.closeActivity(relative);
        System.gc();
    }


    @Override
    public void onLocationChanged(Location location, int priority) {
        Data.latitude = location.getLatitude();
        Data.longitude = location.getLongitude();
    }

    private void retrieveOTPFromSMS(Intent intent) {
        try {
            String otp = "";
            if (intent.hasExtra("message")) {
                String message = intent.getStringExtra("message");

                if (message.toLowerCase().contains("paytm")) {
                    otp = message.split("\\ ")[0];
                } else {
                    String[] arr = message.split("and\\ it\\ is\\ valid\\ till\\ ");
                    String[] arr2 = arr[0].split("Dear\\ Driver\\,\\ Your\\ One\\ Time\\ Password\\ is\\ ");
                    otp = arr2[1];
                    otp = otp.replaceAll("\\ ", "");
                }
            }
            if (Utils.checkIfOnlyDigits(otp)) {
                if (!"".equalsIgnoreCase(otp)) {
                    if (Boolean.parseBoolean(Prefs.with(OTPConfirmScreen.this).getString(SPLabels.REQUEST_LOGIN_OTP_FLAG, "false"))) {
                        editTextOTP.setText(otp);
                        editTextOTP.setCursorVisible(true);
                        editTextOTP.setSelection(editTextOTP.getText().length());
                        buttonVerify.performClick();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private OtpDialog dialog;
    private CustomCountDownTimer customCountDownTimer;


    private void otpDetectionPopup() {
        if (customCountDownTimer == null) {
			if(BuildConfig.DEBUG_MODE){
				customCountDownTimer = new CustomCountDownTimer(3000, 5, this);
			}else {
				customCountDownTimer = new CustomCountDownTimer(30000, 5, this);
			}
        }

        OtpDialog.Builder builder = new OtpDialog.Builder(OTPConfirmScreen.this)
                .purpose(AppConstants.OperationType.CALL)
                .isNumberExist(TextUtils.isEmpty(knowlarityMissedCallNumber))
                .listener(new OtpDialog.Listener() {
                    @Override
                    public void performPostAlertAction(int purpose, Bundle backpack) {
						editTextOTP.setCursorVisible(true);
                        if (purpose == AppConstants.OperationType.CALL) {
                            btnOtpViaCall.performClick();
                        } else if (purpose == AppConstants.OperationType.ENTER_OTP) {

                        }
                    }
                });

        dialog = builder.build();
        dialog.show(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
        customCountDownTimer.start();
        editTextOTP.setCursorVisible(false);
    }


    @Override
    public void updateCounterView(String text, double width) {
        if (dialog != null && dialog.isShown())
            dialog.updateCounterView(text, width);
    }

    @Override
    public void swictchLayout() {
//		editTextOTP.setCursorVisible(true);
        if (TextUtils.isEmpty(knowlarityMissedCallNumber)) {
            btnOtpViaCall.setVisibility(View.GONE);
            textViewOr.setVisibility(View.GONE);
        } else {
            btnOtpViaCall.setVisibility(View.VISIBLE);
            textViewOr.setVisibility(View.VISIBLE);
            btnReGenerateOtp.setVisibility(View.GONE);
        }
        layoutResendOtp.setVisibility(View.VISIBLE);
        if (dialog != null && dialog.isShown())
            dialog.swictchLayout();
    }


}




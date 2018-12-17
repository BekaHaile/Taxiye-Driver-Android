package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppConstants;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.CustomCountDownTimer;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.OtpDialog;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by aneeshbansal on 29/03/16.
 */
public class LoginViaOTP extends BaseActivity implements CustomCountDownTimer.DownTimerOperation {

    LinearLayout otpETextLLayout, layoutResendOtp, mainLinear, btnReGenerateOtp;
    private ImageView btnOtpViaCall;
    RelativeLayout relative, relativeLayoutFake;
    EditText otpEt;
    Button loginViaOtp, btnLogin;
    View backBtn;
    ImageView imageViewChangePhoneNumber;
    TextView textViewOr, textViewOtpNumber, title;
    String selectedLanguage = "";
    int languagePrefStatus;
    Configuration conf;
    String knowlarityMissedCallNumber = "";
    public static String OTP_SCREEN_OPEN = null;
    List<String> categories = new ArrayList<>();
    String phoneNo, countryCode;


    String phoneNoOfLoginAccount = "", accessToken = "", otpErrorMsg = "";

    String enteredEmail = "";
    String enteredPhone = "";


    @Override
    protected void onNewIntent(Intent intent) {

        if (intent.hasExtra("message")) {
            retrieveOTPFromSMS(intent);
        } else if (intent.hasExtra("otp")) {
//			retrieveOTPFromPush(intent);
        }

        super.onNewIntent(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        //fetchLanguageList();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_otp);

        relative = (RelativeLayout) findViewById(R.id.relative);
        new ASSL(LoginViaOTP.this, relative, 1134, 720, false);
        phoneNo = getIntent().getStringExtra("phone_no");
        countryCode = getIntent().getStringExtra(Constants.KEY_COUNTRY_CODE);

        otpEt = (EditText) findViewById(R.id.otpEt);
        otpEt.setTypeface(Fonts.mavenRegular(getApplicationContext()));
        otpEt.setEnabled(false);
		otpEt.setFocusable(true);
		otpEt.setCursorVisible(true);
        layoutResendOtp = (LinearLayout) findViewById(R.id.layoutResendOtp);
        otpETextLLayout = (LinearLayout) findViewById(R.id.otpETextLLayout);

        mainLinear = (LinearLayout) findViewById(R.id.mainLinear);
        btnReGenerateOtp = (LinearLayout) findViewById(R.id.btnReGenerateOtp);
        relativeLayoutFake = (RelativeLayout) findViewById(R.id.relativeLayoutFake);
        relativeLayoutFake.setVisibility(View.GONE);

        btnOtpViaCall = (ImageView) findViewById(R.id.btnOtpViaCall);

        btnLogin = (Button) findViewById(R.id.btnLogin);
		backBtn = findViewById(R.id.backBtn);
        btnLogin.setTypeface(Fonts.mavenRegular(getApplicationContext()));

        imageViewChangePhoneNumber = (ImageView) findViewById(R.id.imageViewChangePhoneNumber);

        loginViaOtp = (Button) findViewById(R.id.loginViaOtp);
        loginViaOtp.setTypeface(Fonts.mavenRegular(getApplicationContext()));

        textViewOtpNumber = (TextView) findViewById(R.id.textViewOtpNumber);
        textViewOtpNumber.setTypeface(Fonts.mavenRegular(getApplicationContext()));
        textViewOr = (TextView) findViewById(R.id.textViewOr);
        textViewOr.setTypeface(Fonts.mavenRegular(getApplicationContext()));

		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Fonts.mavenRegular(getApplicationContext()));
		title.setText(R.string.login);

        selectedLanguage = Prefs.with(LoginViaOTP.this).getString(SPLabels.SELECTED_LANGUAGE, "");

        try {

            if (getIntent().hasExtra("phone_no") && getIntent().hasExtra("otp")) {
                otpEt.setText(getIntent().getStringExtra("otp"));
                otpEt.setSelection(otpEt.getText().length());
                textViewOtpNumber.setText(Prefs.with(LoginViaOTP.this).getString(SPLabels.DRIVER_LOGIN_PHONE_NUMBER, ""));
                loginViaOtp.performClick();
            } else if (getIntent().hasExtra("phone_no")) {
                generateOTP(phoneNo, countryCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otpEt.setError(null);
            }
        });
        textViewOtpNumber.setText(phoneNo);

        btnReGenerateOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((Utils.validPhoneNumber(phoneNo))) {
					otpEt.setText("");
					otpEt.setError(null);
                    generateOTP(phoneNo, countryCode);
                }
            }
        });


		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				performbackPressed();
			}
		});

        imageViewChangePhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Prefs.with(LoginViaOTP.this).save(SPLabels.REQUEST_LOGIN_OTP_FLAG, "false");
                Intent intent = new Intent(LoginViaOTP.this, SplashNewActivity.class);
                intent.putExtra("no_anim", "yes");
                intent.putExtra("number", phoneNo);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.right_in, R.anim.right_out);
            }
        });


        btnOtpViaCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"".equalsIgnoreCase(knowlarityMissedCallNumber)) {
                    DialogPopup.alertPopupTwoButtonsWithListeners(LoginViaOTP.this, "",
                            getResources().getString(R.string.give_missed_call_dialog_text),
                            getResources().getString(R.string.call_us),
                            getResources().getString(R.string.cancel),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    btnLogin.setVisibility(View.VISIBLE);
                                    layoutResendOtp.setVisibility(View.GONE);
                                    btnReGenerateOtp.setVisibility(View.GONE);
                                    Utils.openCallIntent(LoginViaOTP.this, knowlarityMissedCallNumber);
                                }
                            },
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }, false, false
                    );
                }
            }
        });


        loginViaOtp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String otpCode = otpEt.getText().toString().trim();
                if (otpCode.length() > 0) {
                    sendLoginValues(LoginViaOTP.this, "", countryCode + String.valueOf(phoneNo), "", otpCode);
                } else {
                    otpEt.requestFocus();
                    otpEt.setError(getResources().getString(R.string.code_empty));
                }
            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sendLoginValues(LoginViaOTP.this, "", countryCode + String.valueOf(phoneNo), "", "99999");
            }
        });


        otpEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    otpEt.setTextSize(20);
                } else {
                    otpEt.setTextSize(15);
                }
            }
        });


        try {                                                                                        // to get AppVersion, OS version, country code and device name
            Data.filldetails(LoginViaOTP.this);
        } catch (Exception e) {
            Log.e("error in fetching appversion and gcm key", ".." + e.toString());
        }


        Prefs.with(LoginViaOTP.this).save(SPLabels.REQUEST_LOGIN_OTP_FLAG, "false");
        OTP_SCREEN_OPEN = "yes";
        Prefs.with(LoginViaOTP.this).save(SPLabels.LOGIN_VIA_OTP_STATE, true);

        if (System.currentTimeMillis() < (Prefs.with(LoginViaOTP.this).getLong(SPLabels.DRIVER_LOGIN_TIME, 0) + 600000)
                && (!"".equalsIgnoreCase(Prefs.with(LoginViaOTP.this).getString(SPLabels.DRIVER_LOGIN_PHONE_NUMBER, "")))) {
            fetchMessages();
        }

    }

    public void generateOTP(final String phoneNo, final String countryCode) {
        try {
            if (AppStatus.getInstance(LoginViaOTP.this).isOnline(LoginViaOTP.this)) {
                DialogPopup.showLoadingDialog(LoginViaOTP.this, getResources().getString(R.string.loading));
                HashMap<String, String> params = new HashMap<>();
                params.put("phone_no", countryCode + phoneNo);
                params.put(Constants.KEY_COUNTRY_CODE, countryCode);
                params.put("login_type", "1");
                HomeUtil.putDefaultParams(params);
                Prefs.with(LoginViaOTP.this).save(SPLabels.DRIVER_LOGIN_PHONE_NUMBER, phoneNo);
                Prefs.with(LoginViaOTP.this).save(SPLabels.DRIVER_LOGIN_TIME, System.currentTimeMillis());

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
                                DialogPopup.dialogBanner(LoginViaOTP.this, message);
                                layoutResendOtp.setVisibility(View.GONE);
                                btnReGenerateOtp.setVisibility(View.GONE);
                                btnLogin.setVisibility(View.GONE);
                                loginViaOtp.setVisibility(View.VISIBLE);
                                otpEt.setEnabled(true);
                                knowlarityMissedCallNumber = jObj.optString("knowlarity_missed_call_number", "");
                                Prefs.with(LoginViaOTP.this).save(SPLabels.REQUEST_LOGIN_OTP_FLAG, "true");
                                otpDetectionPopup();
                            } else {
                                DialogPopup.alertPopupWithListener(LoginViaOTP.this, "", message, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        performbackPressed();
                                    }
                                });
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            DialogPopup.alertPopupWithListener(LoginViaOTP.this, "", Data.SERVER_ERROR_MSG, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    performbackPressed();
                                }
                            });
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogPopup.dismissLoadingDialog();
                        DialogPopup.alertPopupWithListener(LoginViaOTP.this, "", Data.SERVER_ERROR_MSG, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                performbackPressed();
                            }
                        });
                    }
                });
            } else {
                DialogPopup.alertPopupWithListener(LoginViaOTP.this, "", Data.CHECK_INTERNET_MSG, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        performbackPressed();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    if (Boolean.parseBoolean(Prefs.with(LoginViaOTP.this).getString(SPLabels.REQUEST_LOGIN_OTP_FLAG, "false"))) {
                        otpEt.setText(otp);
                        otpEt.setSelection(otpEt.getText().length());
                        loginViaOtp.performClick();
                        // Dismiss dialog if shown after read otp from sms
                        if(dialog != null && dialog.isShown()) {
                            dialog.dismiss();
							otpEt.setCursorVisible(true);
                            customCountDownTimer.cancel();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendLoginValues(final Activity activity, final String emailId, final String phoneNo, final String password, final String otp) {
        if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
            final Dialog dialogLoading = DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading), true);
            conf = getResources().getConfiguration();

            if (Data.locationFetcher != null) {
                Data.latitude = Data.locationFetcher.getLatitude();
                Data.longitude = Data.locationFetcher.getLongitude();
            }

            HashMap<String, String> params = new HashMap<String, String>();

            params.put("email", emailId);
            params.put("phone_no", phoneNo);
            params.put("password", password);
            params.put("login_otp", otp);
            params.put("device_token", Data.deviceToken);
            params.put("device_type", Data.DEVICE_TYPE);
            params.put("device_name", Data.deviceName);
            params.put("app_version", "" + Data.appVersion);
            params.put("os_version", Data.osVersion);
            params.put("country", Data.country);
            params.put("unique_device_id", Data.uniqueDeviceId);
            params.put("latitude", "" + Data.latitude);
            params.put("longitude", "" + Data.longitude);
            params.put("client_id", Data.CLIENT_ID);
            params.put("login_type", Data.LOGIN_TYPE);
            params.put("locale", conf.locale.toString());
            HomeUtil.putDefaultParams(params);


            if (Utils.isAppInstalled(activity, Data.GADDAR_JUGNOO_APP)) {
                params.put("auto_n_cab_installed", "1");
            } else {
                params.put("auto_n_cab_installed", "0");
            }

            if (Utils.isAppInstalled(activity, Data.UBER_APP)) {
                params.put("uber_installed", "1");
            } else {
                params.put("uber_installed", "0");
            }

            if (Utils.telerickshawInstall(activity)) {
                params.put("telerickshaw_installed", "1");
            } else {
                params.put("telerickshaw_installed", "0");
            }


            if (Utils.olaInstall(activity)) {
                params.put("ola_installed", "1");
            } else {
                params.put("ola_installed", "0");
            }

            if (Utils.isDeviceRooted()) {
                params.put("device_rooted", "1");
            } else {
                params.put("device_rooted", "0");

            }

            RestClient.getApiServices().sendLoginValuesRetro(params, new Callback<RegisterScreenResponse>() {
                @Override
                public void success(RegisterScreenResponse registerScreenResponse, Response response) {
                    try {

                        String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
                        JSONObject jObj;
                        jObj = new JSONObject(jsonString);
                        int flag = jObj.getInt("flag");
                        String message = JSONParser.getServerMessage(jObj);

                        if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag, null)) {
                            if (ApiResponseFlags.INCORRECT_PASSWORD.getOrdinal() == flag) {
                                DialogPopup.alertPopup(activity, "", message);
                            } else if (ApiResponseFlags.CUSTOMER_LOGGING_IN.getOrdinal() == flag) {
                                SplashNewActivity.sendToCustomerAppPopup("Alert", message, activity);
                            } else if (ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag) {
                                DialogPopup.alertPopup(activity, "", message);
                            } else if (ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag) {
                                DialogPopup.alertPopup(activity, "", message);
//								mainLinear.setVisibility(View.VISIBLE);
                                layoutResendOtp.setVisibility(View.VISIBLE);
                                if(customCountDownTimer != null)
                                    customCountDownTimer.cancel();
                                btnLogin.setVisibility(View.GONE);
                                btnReGenerateOtp.setVisibility(View.VISIBLE);
                                otpEt.setText("");
                            } else if (ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag) {
                                DialogPopup.alertPopup(activity, "", getResources().getString(R.string.no_not_verified));
                            } else if (ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag) {
                                if (!SplashNewActivity.checkIfUpdate(jObj.getJSONObject("login"), activity)) {
                                    new JSONParser().parseAccessTokenLoginData(activity, jsonString);
                                    startService(new Intent(getApplicationContext(), DriverLocationUpdateService.class));
                                    Database.getInstance(LoginViaOTP.this).insertEmail(emailId);
                                    startActivity(new Intent(LoginViaOTP.this, HomeActivity.class));
                                    finish();
                                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                                }
                            } else if (ApiResponseFlags.UPLOAD_DOCCUMENT.getOrdinal() == flag) {
                                JSONParser.saveAccessToken(activity, jObj.getString("access_token"));
                                Intent intent = new Intent(LoginViaOTP.this, DriverDocumentActivity.class);
                                intent.putExtra("access_token", jObj.getString("access_token"));
                                intent.putExtra("in_side", false);
								intent.putExtra("doc_required", 3);
								relativeLayoutFake.setVisibility(View.VISIBLE);
                                startActivity(intent);
                            } else {
                                DialogPopup.alertPopup(activity, "", message);
                            }
                            if (dialogLoading != null) {
                                dialogLoading.dismiss();
                            }
                        } else {
                            if (dialogLoading != null) {
                                dialogLoading.dismiss();
                            }
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                        DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                    }
                    if (dialogLoading != null) {
                        dialogLoading.dismiss();
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    if (dialogLoading != null) {
                        dialogLoading.dismiss();
                    }
                    DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
                }
            });

        } else {
            DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
        }

    }

    public void performbackPressed() {
        Prefs.with(LoginViaOTP.this).save(SPLabels.REQUEST_LOGIN_OTP_FLAG, "false");
        Intent intent = new Intent(LoginViaOTP.this, SplashNewActivity.class);
        intent.putExtra("no_anim", "yes");
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }

    @Override
    public void onBackPressed() {
        performbackPressed();
        super.onBackPressed();
    }

	@Override
	protected void onResume(){
		super.onResume();
		try {
			relativeLayoutFake.setVisibility(View.GONE);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    @Override
    protected void onDestroy() {
        Prefs.with(LoginViaOTP.this).save(SPLabels.LOGIN_VIA_OTP_STATE, false);
        OTP_SCREEN_OPEN = null;

        super.onDestroy();
    }


    public void fetchMessages() {

        try {
            Uri uri = Uri.parse("content://sms/inbox");
            String[] selectionArgs;
            String selection;
            Cursor cursor;

            selectionArgs = new String[]{Long.toString(System.currentTimeMillis() - 600000)};
            selection = "date>?";
            cursor = LoginViaOTP.this.getContentResolver().query(uri, null, selection, selectionArgs, null);

            if (cursor != null) {
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
                    String sender = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                    try {
                        String date = DateOperations.getTimeStampUTCFromMillis(Long
                                .parseLong(cursor.getString(cursor.getColumnIndexOrThrow("date"))));
                        if (body.toLowerCase().contains("jugnoo")) {
                            String otp = "";
                            String message = body;

                            if (message.toLowerCase().contains("paytm")) {
                                otp = message.split("\\ ")[0];
                            } else {
                                String[] arr = message.split("and\\ it\\ is\\ valid\\ till\\ ");
                                String[] arr2 = arr[0].split("Dear\\ Driver\\,\\ Your\\ One\\ Time\\ Password\\ is\\ ");
                                otp = arr2[1];
                                otp = otp.replaceAll("\\ ", "");
                            }

                            if (Utils.checkIfOnlyDigits(otp)) {
                                if (!"".equalsIgnoreCase(otp)) {
                                    try {
                                        otpEt.setText(otp);
                                        otpEt.setSelection(otpEt.getText().length());
                                        textViewOtpNumber.setText(Prefs.with(LoginViaOTP.this).getString(SPLabels.DRIVER_LOGIN_PHONE_NUMBER, ""));
                                        loginViaOtp.performClick();
                                        // Dismiss dialog if shown after read otp from sms
                                        otpExist = true;
                                        if(dialog != null && dialog.isShown()) {
                                            dialog.dismiss();
											otpEt.setCursorVisible(true);
                                            customCountDownTimer.cancel();
                                        }
                                        break;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private OtpDialog dialog;
    private CustomCountDownTimer customCountDownTimer;
    private boolean otpExist = false;

    private void otpDetectionPopup() {
        if(otpExist) {
            otpExist = false;
            if(dialog != null && dialog.isShown()) {
                dialog.dismiss();
				otpEt.setCursorVisible(true);
                customCountDownTimer.cancel();
            }
            return;
        }

        if(customCountDownTimer == null) {
			if(BuildConfig.DEBUG_MODE){
				customCountDownTimer = new CustomCountDownTimer(3000, 5, this);
			}else {
				customCountDownTimer = new CustomCountDownTimer(30000, 5, this);
			}
        }

        OtpDialog.Builder builder = new OtpDialog.Builder(LoginViaOTP.this)
                .purpose(AppConstants.OperationType.CALL)
                .isNumberExist(TextUtils.isEmpty(knowlarityMissedCallNumber))
                .listener(new OtpDialog.Listener() {
                    @Override
                    public void performPostAlertAction(int purpose, Bundle backpack) {
                        otpEt.setCursorVisible(true);
                        if(purpose == AppConstants.OperationType.CALL) {
                            btnOtpViaCall.performClick();
                        } else if(purpose == AppConstants.OperationType.ENTER_OTP) {

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
        otpEt.setCursorVisible(false);
    }



    @Override
    public void updateCounterView(String text, double width) {
        if (dialog != null && dialog.isShown())
            dialog.updateCounterView(text, width);
    }

    @Override
    public void swictchLayout() {
        btnReGenerateOtp.setVisibility(View.VISIBLE);
//        otpEt.setCursorVisible(true);
        if(TextUtils.isEmpty(knowlarityMissedCallNumber)){
            layoutResendOtp.setVisibility(View.GONE);
        }else {
            layoutResendOtp.setVisibility(View.VISIBLE);
        }
        if(dialog != null && dialog.isShown())
            dialog.swictchLayout();
    }

}

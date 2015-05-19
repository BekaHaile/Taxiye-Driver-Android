package product.clicklabs.jugnoo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.facebook.Session;
import com.flurry.android.FlurryAgent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.ArrayList;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.AppMode;
import product.clicklabs.jugnoo.datastructure.EmailRegisterData;
import product.clicklabs.jugnoo.datastructure.FacebookRegisterData;
import product.clicklabs.jugnoo.datastructure.PreviousAccountInfo;
import product.clicklabs.jugnoo.utils.AppStatus;
import product.clicklabs.jugnoo.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.utils.DeviceTokenGenerator;
import product.clicklabs.jugnoo.utils.DialogPopup;
import product.clicklabs.jugnoo.utils.FacebookLoginCallback;
import product.clicklabs.jugnoo.utils.FacebookLoginHelper;
import product.clicklabs.jugnoo.utils.FacebookUserData;
import product.clicklabs.jugnoo.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.utils.Fonts;
import product.clicklabs.jugnoo.utils.IDeviceTokenReceiver;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.Utils;
import rmn.androidscreenlibrary.ASSL;

public class RegisterScreen extends Activity implements LocationUpdate {

    TextView textViewTitle;
    ImageView imageViewBack;

    Button buttonFacebookSignup;
    TextView orText;

    EditText editTextUserName, editTextEmail, editTextPhone, editTextPassword, editTextReferralCode;
    TextView textViewPolicy;
    Button buttonEmailSignup;

//    TextView textViewScroll;
//    ScrollView scrollView;

    LinearLayout relative;

    String name = "", referralCode = "", emailId = "", phoneNo = "", password = "", accessToken = "";

    public static boolean facebookLogin = false;
    boolean sendToOtpScreen = false;

    public static JSONObject multipleCaseJSON;

    public void resetFlags() {
        sendToOtpScreen = false;
    }

    // *****************************Used for flurry work***************//
    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.init(this, Config.getFlurryKey());
        FlurryAgent.onStartSession(this, Config.getFlurryKey());
        FlurryAgent.onEvent("Register started");
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        relative = (LinearLayout) findViewById(R.id.relative);
        new ASSL(RegisterScreen.this, relative, 1134, 720, false);

        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        textViewTitle.setTypeface(Fonts.latoRegular(this), Typeface.BOLD);
        imageViewBack = (ImageView) findViewById(R.id.imageViewBack);

        buttonFacebookSignup = (Button) findViewById(R.id.buttonFacebookSignup);
        buttonFacebookSignup.setTypeface(Fonts.latoRegular(this));
        orText = (TextView) findViewById(R.id.orText);
        orText.setTypeface(Fonts.latoRegular(getApplicationContext()));

        editTextUserName = (EditText) findViewById(R.id.editTextUserName);
        editTextUserName.setTypeface(Fonts.latoRegular(this));
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextEmail.setTypeface(Fonts.latoRegular(this));
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        editTextPhone.setTypeface(Fonts.latoRegular(this));
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextPassword.setTypeface(Fonts.latoRegular(this));
        editTextReferralCode = (EditText) findViewById(R.id.editTextReferralCode);
        editTextReferralCode.setTypeface(Fonts.latoRegular(this));

        textViewPolicy = (TextView) findViewById(R.id.textViewPolicy);
        textViewPolicy.setTypeface(Fonts.latoLight(this), Typeface.BOLD);

        buttonEmailSignup = (Button) findViewById(R.id.buttonEmailSignup);
        buttonEmailSignup.setTypeface(Fonts.latoRegular(this));

//        scrollView = (ScrollView) findViewById(R.id.scrollView);
//        textViewScroll = (TextView) findViewById(R.id.textViewScroll);


        buttonFacebookSignup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new FacebookLoginHelper().openFacebookSession(RegisterScreen.this, facebookLoginCallback, true);
            }
        });

        imageViewBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                performBackPressed();
            }
        });

        editTextUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
//                    scrollView.smoothScrollTo(0, editTextUserName.getBottom());
                }
                else {
                    editTextUserName.setError(null);
                }
            }
        });

        editTextEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
//                    scrollView.smoothScrollTo(0, editTextEmail.getBottom());
                }
                else {
                    editTextEmail.setError(null);
                }
            }
        });


        editTextPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
//                    scrollView.smoothScrollTo(0, editTextPhone.getBottom());
                }
                else {
                    editTextPhone.setError(null);
                }
            }
        });

        editTextPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
//                    scrollView.smoothScrollTo(0, editTextPassword.getBottom());
                }
                else {
                    editTextPassword.setError(null);
                }
            }
        });

        editTextReferralCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
//                    scrollView.smoothScrollTo(0, editTextReferralCode.getBottom());
                }
                else {
                    editTextReferralCode.setError(null);
                }
            }
        });


        buttonEmailSignup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String name = editTextUserName.getText().toString().trim();
                if (name.length() > 0) {
                    name = name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
                }
                String referralCode = editTextReferralCode.getText().toString().trim();
                String emailId = editTextEmail.getText().toString().trim();
                boolean noFbEmail = false;

                if (facebookLogin && emailId.equalsIgnoreCase("")) {
                    emailId = "n@n.c";
                    noFbEmail = true;
                }


                String phoneNo = editTextPhone.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();


                if ("".equalsIgnoreCase(name)) {
                    editTextUserName.requestFocus();
                    editTextUserName.setError("Please enter name");
                }
                else if(!Utils.hasAlphabets(name)) {
                    editTextUserName.requestFocus();
                    editTextUserName.setError("Please enter at least one alphabet");
                }
                else {
                    if ("".equalsIgnoreCase(emailId)) {
                        editTextEmail.requestFocus();
                        editTextEmail.setError("Please enter email id");
                    } else {
                        if ("".equalsIgnoreCase(phoneNo)) {
                            editTextPhone.requestFocus();
                            editTextPhone.setError("Please enter phone number");
                        } else {
                            phoneNo = Utils.retrievePhoneNumberTenChars(phoneNo);
                            if (!Utils.validPhoneNumber(phoneNo)) {
                                editTextPhone.requestFocus();
                                editTextPhone.setError("Please enter valid phone number");
                            } else {
                                phoneNo = "+91" + phoneNo;
                                if ("".equalsIgnoreCase(password)) {
                                    editTextPassword.requestFocus();
                                    editTextPassword.setError("Please enter password");
                                } else {
                                    if (Utils.isEmailValid(emailId)) {
                                        if (password.length() >= 6) {

                                            if (facebookLogin) {
                                                if (noFbEmail) {
                                                    emailId = "";
                                                }
                                                sendFacebookSignupValues(RegisterScreen.this, referralCode, phoneNo, password);
                                                FlurryEventLogger.facebookSignupClicked(Data.facebookUserData.userEmail);
                                                if (!"".equalsIgnoreCase(referralCode)) {
                                                    FlurryEventLogger.referralCodeAtFBSignup(Data.facebookUserData.userEmail, referralCode);
                                                }
                                            } else {
                                                sendSignupValues(RegisterScreen.this, name, referralCode, emailId, phoneNo, password);
                                                FlurryEventLogger.emailSignupClicked(emailId);
                                                if (!"".equalsIgnoreCase(referralCode)) {
                                                    FlurryEventLogger.referralCodeAtEmailSignup(emailId, referralCode);
                                                }
                                            }


                                        } else {
                                            editTextPassword.requestFocus();
                                            editTextPassword.setError("Password must be of atleast six characters");
                                        }

                                    } else {
                                        editTextEmail.requestFocus();
                                        editTextEmail.setError("Please enter valid email id");
                                    }

                                }
                            }
                        }
                    }
                }

            }
        });


        editTextReferralCode.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                int result = actionId & EditorInfo.IME_MASK_ACTION;
                switch (result) {
                    case EditorInfo.IME_ACTION_DONE:
                        buttonEmailSignup.performClick();
                        break;

                    case EditorInfo.IME_ACTION_NEXT:
                        break;

                    default:
                }
                return true;
            }
        });


        SpannableString sstr = new SpannableString("Terms and Conditions");
        final ForegroundColorSpan clrs = new ForegroundColorSpan(getResources().getColor(R.color.yellow_orange));
        sstr.setSpan(clrs, 0, sstr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        textViewPolicy.setText("");
        textViewPolicy.append("By signing up, you agree to \n");
        textViewPolicy.append(sstr);
        textViewPolicy.append(" of Jugnoo");

        textViewPolicy.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://jugnoo.in/terms_of_use"));
                startActivity(browserIntent);
            }
        });


        try {
            if (facebookLogin) {
                editTextUserName.setText(Data.facebookUserData.firstName + " " + Data.facebookUserData.lastName);
                editTextUserName.setEnabled(false);
                if ("".equalsIgnoreCase(Data.facebookUserData.userEmail)) {
                    editTextEmail.setText("");
                    editTextEmail.setEnabled(true);
                } else {
                    editTextEmail.setText(Data.facebookUserData.userEmail);
                    editTextEmail.setEnabled(false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (getIntent().hasExtra("back_from_otp")) {
                if (facebookLogin) {
                    editTextReferralCode.setText(OTPConfirmScreen.facebookRegisterData.referralCode);
                    editTextPhone.setText(OTPConfirmScreen.facebookRegisterData.phoneNo);
                    editTextPassword.setText(OTPConfirmScreen.facebookRegisterData.password);
                } else {
                    editTextUserName.setText(OTPConfirmScreen.emailRegisterData.name);
                    editTextEmail.setText(OTPConfirmScreen.emailRegisterData.emailId);
                    editTextReferralCode.setText(OTPConfirmScreen.emailRegisterData.referralCode);
                    editTextPhone.setText(OTPConfirmScreen.emailRegisterData.phoneNo);
                    editTextPassword.setText(OTPConfirmScreen.emailRegisterData.password);
                }

                editTextUserName.setSelection(editTextUserName.getText().length());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


//        final View activityRootView = findViewById(R.id.linearLayoutMain);
//        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(
//            new OnGlobalLayoutListener() {
//
//                @Override
//                public void onGlobalLayout() {
//                    Rect r = new Rect();
//                    // r will be populated with the coordinates of your view
//                    // that area still visible.
//                    activityRootView.getWindowVisibleDisplayFrame(r);
//
//                    int heightDiff = activityRootView.getRootView()
//                        .getHeight() - (r.bottom - r.top);
//                    if (heightDiff > 100) { // if more than 100 pixels, its
//                        // probably a keyboard...
//
//                        /************** Adapter for the parent List *************/
//
//                        ViewGroup.LayoutParams params_12 = textViewScroll
//                            .getLayoutParams();
//
//                        params_12.height = (int) (heightDiff);
//
//                        textViewScroll.setLayoutParams(params_12);
//                        textViewScroll.requestLayout();
//
//                    } else {
//
//                        ViewGroup.LayoutParams params = textViewScroll
//                            .getLayoutParams();
//                        params.height = 0;
//                        textViewScroll.setLayoutParams(params);
//                        textViewScroll.requestLayout();
//
//                    }
//                }
//            });


        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        new DeviceTokenGenerator(this).generateDeviceToken(this, new IDeviceTokenReceiver() {

            @Override
            public void deviceTokenReceived(final String regId) {
                Data.deviceToken = regId;
                Log.e("deviceToken in IDeviceTokenReceiver", Data.deviceToken + "..");
            }
        });


//		editTextUserName.setText("Support Meals 31");
//		editTextEmail.setText("support+m31@jugnoo.in");
//		editTextPhone.setText("+919000000231");
//		editTextPassword.setText("jugnoopass");


        if (Data.previousAccountInfoList == null) {
            Data.previousAccountInfoList = new ArrayList<PreviousAccountInfo>();
        }
        Data.previousAccountInfoList.clear();

    }

    FacebookLoginCallback facebookLoginCallback = new FacebookLoginCallback() {
        @Override
        public void facebookLoginDone() {
            if (FacebookLoginHelper.USER_DATA != null) {
                Data.facebookUserData = new FacebookUserData(FacebookLoginHelper.USER_DATA.accessToken, FacebookLoginHelper.USER_DATA.fbId,
                    FacebookLoginHelper.USER_DATA.firstName, FacebookLoginHelper.USER_DATA.lastName, FacebookLoginHelper.USER_DATA.userName,
                    FacebookLoginHelper.USER_DATA.userEmail);
                facebookLogin = true;
                editTextUserName.setText(Data.facebookUserData.firstName + " " + Data.facebookUserData.lastName);
                editTextEmail.setText(Data.facebookUserData.userEmail);

                editTextUserName.setEnabled(false);
                editTextEmail.setEnabled(false);
                FlurryEventLogger.registerViaFBClicked(Data.facebookUserData.fbId);
            } else {
                Toast.makeText(getApplicationContext(), "Error occured during Facebook authentication", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            Session.getActiveSession().onActivityResult(this, requestCode,
                resultCode, data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    @Override
    protected void onResume() {
        super.onResume();

        if (Data.locationFetcher == null) {
            Data.locationFetcher = new LocationFetcher(RegisterScreen.this, 1000, 1);
        }
        HomeActivity.checkForAccessTokenChange(this);

    }

    @Override
    protected void onPause() {
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


    /**
     * ASync for register from server
     */
    public void sendSignupValues(final Activity activity, final String name, final String referralCode, final String emailId, final String phoneNo, final String password) {
        if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
            resetFlags();
            DialogPopup.showLoadingDialog(activity, "Loading...");

            RequestParams params = new RequestParams();

            if (Data.locationFetcher != null) {
                Data.latitude = Data.locationFetcher.getLatitude();
                Data.longitude = Data.locationFetcher.getLongitude();
            }

            params.put("user_name", name);
            params.put("phone_no", phoneNo);
            params.put("email", emailId);
            params.put("password", password);
            params.put("latitude", "" + Data.latitude);
            params.put("longitude", "" + Data.longitude);

            params.put("device_type", Data.DEVICE_TYPE);
            params.put("device_name", Data.deviceName);
            params.put("app_version", "" + Data.appVersion);
            params.put("os_version", Data.osVersion);
            params.put("country", Data.country);

            params.put("client_id", Config.getClientId());
            params.put("referral_code", referralCode);

            if(Config.getServerUrl().contains(Config.getDevServerUrl().substring(0, Config.getDevServerUrl().length() - 5))){
                if(AppMode.DEBUG == SplashNewActivity.appMode){
                    params.put("device_token", "");
                    params.put("unique_device_id", "");
                }
                else{
                    params.put("device_token", Data.deviceToken);
                    params.put("unique_device_id", Data.uniqueDeviceId);
                }
            }
            else{
                params.put("device_token", Data.deviceToken);
                params.put("unique_device_id", Data.uniqueDeviceId);
            }




            Log.i("register_using_email params", params.toString());


            AsyncHttpClient client = Data.getClient();
            client.post(Config.getServerUrl() + "/register_using_email", params,
                new CustomAsyncHttpResponseHandler() {
                    private JSONObject jObj;

                    @Override
                    public void onFailure(Throwable arg3) {
                        Log.e("request fail", arg3.toString());
                        DialogPopup.dismissLoadingDialog();
                        DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
                    }

                    @Override
                    public void onSuccess(String response) {
                        Log.i("Server response register_using_email", "response = " + response);

                        try {
                            jObj = new JSONObject(response);

                            if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
                                if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                    int flag = jObj.getInt("flag");
                                    if (ApiResponseFlags.AUTH_REGISTRATION_FAILURE.getOrdinal() == flag) {
                                        String error = jObj.getString("error");
                                        DialogPopup.alertPopup(activity, "", error);
                                    } else if (ApiResponseFlags.AUTH_ALREADY_REGISTERED.getOrdinal() == flag) {
                                        String error = jObj.getString("error");
                                        DialogPopup.alertPopup(activity, "", error);
                                    } else if (ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag) {
                                        RegisterScreen.this.name = name;
                                        RegisterScreen.this.emailId = emailId;
                                        RegisterScreen.this.phoneNo = jObj.getString("phone_no");
                                        RegisterScreen.this.password = password;
                                        RegisterScreen.this.referralCode = referralCode;
                                        RegisterScreen.this.accessToken = jObj.getString("access_token");
                                        sendToOtpScreen = true;
                                    } else if (ApiResponseFlags.AUTH_DUPLICATE_REGISTRATION.getOrdinal() == flag) {
                                        RegisterScreen.this.name = name;
                                        RegisterScreen.this.emailId = emailId;
                                        RegisterScreen.this.phoneNo = phoneNo;
                                        RegisterScreen.this.password = password;
                                        RegisterScreen.this.referralCode = referralCode;
                                        RegisterScreen.this.accessToken = "";
                                        parseDataSendToMultipleAccountsScreen(activity, jObj);
                                    } else {
                                        DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                                    }
                                    DialogPopup.dismissLoadingDialog();
                                }
                            } else {
                                DialogPopup.dismissLoadingDialog();
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                            DialogPopup.dismissLoadingDialog();
                        }
                    }
                });
        } else {
            DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
        }

    }


    /**
     * ASync for login from server
     */
    public void sendFacebookSignupValues(final Activity activity, final String referralCode, final String phoneNo, final String password) {
        if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {
            resetFlags();
            DialogPopup.showLoadingDialog(activity, "Loading...");

            RequestParams params = new RequestParams();

            if (Data.locationFetcher != null) {
                Data.latitude = Data.locationFetcher.getLatitude();
                Data.longitude = Data.locationFetcher.getLongitude();
            }

            params.put("user_fb_id", Data.facebookUserData.fbId);
            params.put("user_fb_name", Data.facebookUserData.firstName + " " + Data.facebookUserData.lastName);
            params.put("fb_access_token", Data.facebookUserData.accessToken);
            params.put("fb_mail", Data.facebookUserData.userEmail);
            params.put("username", Data.facebookUserData.userName);

            params.put("phone_no", phoneNo);
            params.put("password", password);
            params.put("referral_code", referralCode);

            params.put("latitude", "" + Data.latitude);
            params.put("longitude", "" + Data.longitude);
            params.put("device_token", Data.deviceToken);
            params.put("device_type", Data.DEVICE_TYPE);
            params.put("device_name", Data.deviceName);
            params.put("app_version", "" + Data.appVersion);
            params.put("os_version", Data.osVersion);
            params.put("country", Data.country);
            params.put("unique_device_id", Data.uniqueDeviceId);
            params.put("client_id", Config.getClientId());


            Log.e("register_using_facebook params", params.toString());


            AsyncHttpClient client = Data.getClient();
            client.post(Config.getServerUrl() + "/register_using_facebook", params,
                new CustomAsyncHttpResponseHandler() {
                    private JSONObject jObj;

                    @Override
                    public void onFailure(Throwable arg3) {
                        Log.e("request fail", arg3.toString());
                        DialogPopup.dismissLoadingDialog();
                        DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
                    }

                    @Override
                    public void onSuccess(String response) {
                        Log.i("Server response register_using_facebook", "response = " + response);

                        try {
                            jObj = new JSONObject(response);

                            if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
                                if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj)) {
                                    int flag = jObj.getInt("flag");
                                    if (ApiResponseFlags.AUTH_REGISTRATION_FAILURE.getOrdinal() == flag) {
                                        String error = jObj.getString("error");
                                        DialogPopup.alertPopup(activity, "", error);
                                    } else if (ApiResponseFlags.AUTH_ALREADY_REGISTERED.getOrdinal() == flag) {
                                        String error = jObj.getString("error");
                                        DialogPopup.alertPopup(activity, "", error);
                                    } else if (ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag) {
                                        RegisterScreen.this.phoneNo = jObj.getString("phone_no");
                                        RegisterScreen.this.password = password;
                                        RegisterScreen.this.referralCode = referralCode;
                                        RegisterScreen.this.accessToken = jObj.getString("access_token");
                                        sendToOtpScreen = true;
                                    } else if (ApiResponseFlags.AUTH_DUPLICATE_REGISTRATION.getOrdinal() == flag) {
                                        RegisterScreen.this.phoneNo = phoneNo;
                                        RegisterScreen.this.password = password;
                                        RegisterScreen.this.referralCode = referralCode;
                                        RegisterScreen.this.accessToken = "";
                                        parseDataSendToMultipleAccountsScreen(activity, jObj);
                                    } else {
                                        DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                                    }
                                    DialogPopup.dismissLoadingDialog();
                                }
                            } else {
                                DialogPopup.dismissLoadingDialog();
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            DialogPopup.dismissLoadingDialog();
                            DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                        }
                    }
                });
        } else {
            DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
        }

    }


    /**
     * Send intent to otp screen by making required data objects
     */
    public void sendIntentToOtpScreen() {
        if (!RegisterScreen.facebookLogin) {
            OTPConfirmScreen.intentFromRegister = true;
            OTPConfirmScreen.emailRegisterData = new EmailRegisterData(name, emailId, phoneNo, password, referralCode, accessToken);
            startActivity(new Intent(RegisterScreen.this, OTPConfirmScreen.class));
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
        } else {
            OTPConfirmScreen.intentFromRegister = true;
            OTPConfirmScreen.facebookRegisterData = new FacebookRegisterData(Data.facebookUserData.fbId,
                Data.facebookUserData.firstName + " " + Data.facebookUserData.lastName,
                Data.facebookUserData.accessToken,
                Data.facebookUserData.userEmail,
                Data.facebookUserData.userName,
                phoneNo, password, referralCode, accessToken);
            startActivity(new Intent(RegisterScreen.this, OTPConfirmScreen.class));
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
        }
    }


    public void parseDataSendToMultipleAccountsScreen(Activity activity, JSONObject jObj) {
        if (RegisterScreen.facebookLogin) {
            OTPConfirmScreen.facebookRegisterData = new FacebookRegisterData(Data.facebookUserData.fbId,
                Data.facebookUserData.firstName + " " + Data.facebookUserData.lastName,
                Data.facebookUserData.accessToken,
                Data.facebookUserData.userEmail,
                Data.facebookUserData.userName,
                phoneNo, password, referralCode, accessToken);
        } else {
            OTPConfirmScreen.emailRegisterData = new EmailRegisterData(name, emailId, phoneNo, password, referralCode, accessToken);
        }
        RegisterScreen.multipleCaseJSON = jObj;
        if (Data.previousAccountInfoList == null) {
            Data.previousAccountInfoList = new ArrayList<PreviousAccountInfo>();
        }
        Data.previousAccountInfoList.clear();
        Data.previousAccountInfoList.addAll(JSONParser.parsePreviousAccounts(jObj));
        startActivity(new Intent(activity, MultipleAccountsActivity.class));
        finish();
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus && sendToOtpScreen) {
            sendIntentToOtpScreen();
        }

    }


    @Override
    public void onBackPressed() {
        performBackPressed();
        super.onBackPressed();
    }


    public void performBackPressed() {
        new FacebookLoginHelper().logoutFacebook();
        Intent intent = new Intent(RegisterScreen.this, SplashNewActivity.class);
        intent.putExtra("no_anim", "yes");
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ASSL.closeActivity(relative);
        System.gc();
    }


    @Override
    public void onLocationChanged(Location location, int priority) {
        Data.latitude = location.getLatitude();
        Data.longitude = location.getLongitude();
    }

}

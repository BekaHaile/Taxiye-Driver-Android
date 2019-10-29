package com.fugu.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import androidx.core.content.PermissionChecker;
import android.support.v4.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fugu.BuildConfig;
import com.fugu.CaptureUserData;
import com.fugu.HippoColorConfig;
import com.fugu.HippoConfig;
import com.fugu.HippoNotificationConfig;
import com.fugu.R;
import com.fugu.adapter.FuguChannelsAdapter;
import com.fugu.constant.FuguAppConstant;
import com.fugu.database.CommonData;
import com.fugu.model.FuguConversation;
import com.fugu.model.FuguDeviceDetails;
import com.fugu.model.FuguGetConversationsResponse;
import com.fugu.model.FuguPutUserDetailsResponse;
import com.fugu.retrofit.APIError;
import com.fugu.retrofit.CommonParams;
import com.fugu.retrofit.ResponseResolver;
import com.fugu.retrofit.RestClient;
import com.fugu.utils.FuguLog;
import com.fugu.utils.UniqueIMEIID;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by rajatdhamija  14/12/17.
 */

public class FuguChannelsActivity extends FuguBaseActivity implements SwipeRefreshLayout.OnRefreshListener, Animation.AnimationListener {

    private static final int NOT_CONNECTED = 0;
    private static final int CONNECTED_TO_INTERNET = 1;
    private static final int CONNECTED_TO_INTERNET_VIA_WIFI = 2;
    private RelativeLayout rlRoot;
    private SwipeRefreshLayout swipeRefresh;
    private TextView tvNoInternet, tvNewConversation;
    private final int READ_PHONE_PERMISSION = 101;
    private final String TAG = FuguChannelsActivity.class.getSimpleName();
    private FuguChannelsAdapter fuguChannelsAdapter;
    private ArrayList<FuguConversation> fuguConversationList = new ArrayList<>();

    private String label = "";
    private Long userId = -1L;
    private String enUserId = "";
    private String userName = "Anonymous";
    private String businessName = "Anonymous";
    private int appVersion = 0;

    private final int IS_HIT_REQUIRED = 200;
    public static boolean isRefresh = false;
    public static Long readChannelId = -1L;
    private TextView tvPoweredBy;
    private HippoColorConfig hippoColorConfig;
    @SuppressLint("StaticFieldLeak")
    private static LinearLayout llInternet;
    @SuppressLint("StaticFieldLeak")
    private static TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fugu_activity_channels);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(NOTIFICATION_INTENT));
        initViews();
        decideAppFlow();
    }

    /**
     * Initialize Views
     */
    private void initViews() {
        hippoColorConfig = CommonData.getColorConfig();
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setToolbar(myToolbar, getIntent().getStringExtra("title"));
        appVersion = getIntent().getIntExtra("appVersion", 0);
        rlRoot = (RelativeLayout) findViewById(R.id.rlRoot);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);
        tvNoInternet = (TextView) findViewById(R.id.tvNoInternet);
        tvNewConversation = (TextView) findViewById(R.id.tvNewConversation);
        tvPoweredBy = (TextView) findViewById(R.id.tvPoweredBy);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        llInternet = (LinearLayout) findViewById(R.id.llInternet);
        configColors();
        if (!isNetworkAvailable()) {
            llInternet.setVisibility(View.VISIBLE);
            llInternet.setBackgroundColor(Color.parseColor("#FF0000"));
            tvStatus.setText(R.string.fugu_not_connected_to_internet);
        }
    }

    /**
     * Decide app flow on basis of user data and permisions
     */
    private void decideAppFlow() {
        if (CommonData.getUserDetails() != null && CommonData.getConversationList().size() > 0) {
            setUpUI();
            getConversations();
        } else if (HippoConfig.getInstance().isPermissionGranted(FuguChannelsActivity.this, Manifest.permission.READ_PHONE_STATE)) {
            sendUserDetails();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, READ_PHONE_PERMISSION);
            }
        }
    }

    /**
     * Set up application UI
     */
    private void setUpUI() {
        tvNoInternet.setVisibility(View.GONE);
        swipeRefresh.setVisibility(View.VISIBLE);
        FuguPutUserDetailsResponse.Data userData = CommonData.getUserDetails().getData();
        label = userData.getBusinessName();
        businessName = userData.getBusinessName();
        userId = userData.getUserId();
        enUserId = userData.getEn_user_id();
        if (userData.getFullName() != null && !userData.getFullName().isEmpty())
            userName = userData.getFullName();
        fuguConversationList.clear();
        if (userData.getFuguConversations().size() > 0) {
            fuguConversationList.addAll(CommonData.getConversationList());
        }
        setRecyclerViewData();
        setPoweredByText(userData);
    }

    /**
     * Change status of internet connection
     *
     * @param status status of internet conection
     */
    public static void changeStatus(int status) {
        switch (status) {
            case NOT_CONNECTED:
                llInternet.setVisibility(View.VISIBLE);
                llInternet.setBackgroundColor(Color.parseColor("#FF0000"));
                tvStatus.setText(R.string.fugu_not_connected_to_internet);
                break;
            case CONNECTED_TO_INTERNET:
            case CONNECTED_TO_INTERNET_VIA_WIFI:
                llInternet.setBackgroundColor(Color.parseColor("#FFA500"));
                tvStatus.setText(R.string.fugu_connecting);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        llInternet.setBackgroundColor(Color.parseColor("#00FF00"));
                        tvStatus.setText(R.string.fugu_connected);
                        llInternet.setVisibility(View.GONE);
                    }
                }, 1500);
                break;
            default:
                break;
        }
    }

    @Override
    public void onRefresh() {
        getConversations();
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    protected void onResume() {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.cancelAll();
        }
        super.onResume();
        if (isRefresh) {
            isRefresh = false;
            try {
                for (int i = 0; i < fuguConversationList.size(); i++) {
                    FuguConversation currentConversation = fuguConversationList.get(i);
                    if (currentConversation.getChannelId().compareTo(readChannelId) == 0) {
                        currentConversation.setUnreadCount(0);

                        if (fuguChannelsAdapter != null)
                            fuguChannelsAdapter.notifyDataSetChanged();

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        FuguLog.d(TAG, "Permission callback called-------" + requestCode);
        switch (requestCode) {
            case READ_PHONE_PERMISSION: {
                if (HippoConfig.getInstance().getTargetSDKVersion() > 22 &&
                        grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendUserDetails();
                } else if (HippoConfig.getInstance().getTargetSDKVersion() <= 22 &&
                        grantResults.length > 0 && grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
                    sendUserDetails();
                } else {
                    //ActivityCompat.shouldShowRequestPermissionRationale(FuguFuguChannelsActivity.this, Manifest.permission.READ_PHONE_STATE);
                    Toast.makeText(FuguChannelsActivity.this, "Go to Settings and grant permission to access phone state", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

    /**
     * Config Colors of App
     */
    private void configColors() {
        rlRoot.setBackgroundColor(hippoColorConfig.getHippoChannelBg());
        tvNewConversation.setTextColor(hippoColorConfig.getHippoActionBarText());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            tvNewConversation.setBackground(HippoColorConfig.makeRoundedSelector(hippoColorConfig.getHippoActionBarBg()));
        } else {
            tvNewConversation.setBackgroundDrawable(HippoColorConfig.makeRoundedSelector(hippoColorConfig.getHippoActionBarBg()));
        }
        tvNewConversation.setTextColor(hippoColorConfig.getHippoActionBarText());
        swipeRefresh.setColorSchemeColors(hippoColorConfig.getHippoThemeColorPrimary());
        tvNoInternet.setTextColor(hippoColorConfig.getHippoThemeColorSecondary());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        readChannelId = null;
    }

    /**
     * onClick functionality
     *
     * @param v view clicked
     */
    public void onClick(View v) {

        if (v.getId() == R.id.tvNoInternet) {
            if (CommonData.getUserDetails() != null) {
                getConversations();
            } else {
                sendUserDetails();
            }
        } else if (v.getId() == R.id.tvNewConversation) {

            Intent chatIntent = new Intent(FuguChannelsActivity.this, FuguChatActivity.class);
            FuguConversation conversation = new FuguConversation();
            conversation.setUserId(userId);
            conversation.setLabel(label);
            conversation.setUserName(userName);
            conversation.setStatus(STATUS_CHANNEL_OPEN);
            conversation.setEnUserId(enUserId);
            chatIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
            startActivityForResult(chatIntent, IS_HIT_REQUIRED);
        } else if (v.getId() == R.id.tvPoweredBy) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(FUGU_WEBSITE_URL));
            startActivity(i);
        }
    }

    /**
     * Broadcast receiver to handle push messages on channels screen
     */
    BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            try {
                JSONObject messageJson = new JSONObject(intent.getStringExtra(MESSAGE));

                FuguLog.d("receiver", "Got message: " + messageJson.toString());

                for (int i = 0; i < fuguConversationList.size(); i++) {
                    FuguConversation currentConversation = fuguConversationList.get(i);
                    if (messageJson.has(NEW_MESSAGE)) {
                        if (currentConversation.getChannelId() == messageJson.getLong(CHANNEL_ID)) {
                            if (messageJson.has(NEW_MESSAGE)) {
                                currentConversation.setMessage(messageJson.getString(NEW_MESSAGE));
                            }
                            currentConversation.setDateTime(messageJson.getString(DATE_TIME).replace("+00:00", ".000Z"));

                            if (HippoNotificationConfig.pushChannelId.compareTo(messageJson.getLong(CHANNEL_ID)) != 0) {
                                currentConversation.setUnreadCount(currentConversation.getUnreadCount() + 1);
                            } else {
                                currentConversation.setUnreadCount(0);
                            }
                            currentConversation.setLast_sent_by_id(messageJson.getLong("last_sent_by_id"));
                            currentConversation.setLast_sent_by_full_name(messageJson.getString("last_sent_by_full_name"));
                            if (fuguChannelsAdapter != null)
                                fuguChannelsAdapter.notifyDataSetChanged();
                        }
                    }
                }
                if (messageJson.getInt(NOTIFICATION_TYPE) == 5) {
                    getConversations();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * Send user details to server
     */
    private void sendUserDetails() {
        if (isNetworkAvailable()) {
            Gson gson = new GsonBuilder().create();
            JsonObject deviceDetailsJson = null;
            try {
                deviceDetailsJson = gson.toJsonTree(new FuguDeviceDetails(appVersion).getDeviceDetails()).getAsJsonObject();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            CaptureUserData userData = getIntent().getParcelableExtra("userData");

            if (userData == null) {
                userData = HippoConfig.getInstance().getUserData();
            }

            HashMap<String, Object> commonParams = new HashMap<>();
            HippoConfig.getInstance();
            if (HippoConfig.getmResellerToken() != null) {
                commonParams.put(RESELLER_TOKEN, HippoConfig.getmResellerToken());
                commonParams.put(REFERENCE_ID, String.valueOf(HippoConfig.getmReferenceId()));
            } else {
                commonParams.put(APP_SECRET_KEY, HippoConfig.getInstance().getAppKey());
            }
            commonParams.put(DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(FuguChannelsActivity.this));
            commonParams.put(APP_TYPE, HippoConfig.getInstance().getAppType());
            commonParams.put(DEVICE_TYPE, ANDROID_USER);
            commonParams.put(APP_VERSION, BuildConfig.VERSION_NAME);
            commonParams.put(DEVICE_DETAILS, deviceDetailsJson);

            if (userData != null) {
                if (!userData.getUserUniqueKey().trim().isEmpty())
                    commonParams.put(USER_UNIQUE_KEY, userData.getUserUniqueKey());

                if (!userData.getFullName().trim().isEmpty())
                    commonParams.put(FULL_NAME, userData.getFullName());

                if (!userData.getEmail().trim().isEmpty())
                    commonParams.put(EMAIL, userData.getEmail());

                if (!userData.getPhoneNumber().trim().isEmpty())
                    commonParams.put(PHONE_NUMBER, userData.getPhoneNumber());
            }

            if (!HippoNotificationConfig.hippoDeviceToken.isEmpty())
                commonParams.put(DEVICE_TOKEN, HippoNotificationConfig.hippoDeviceToken);
            if (userData != null && !userData.getCustom_attributes().isEmpty()) {
                commonParams.put(CUSTOM_ATTRIBUTES, new JSONObject(userData.getCustom_attributes()));
            }

            FuguLog.e(TAG + "sendUserDetails map", "==" + commonParams.toString());
            if (HippoConfig.getmResellerToken() != null) {
                apiPutUserDetailReseller(commonParams);
            } else {
                apiPutUserDetail(commonParams);
            }
        } else {
            tvNoInternet.setVisibility(View.VISIBLE);
            swipeRefresh.setVisibility(View.GONE);
            tvNewConversation.setVisibility(View.GONE);
        }
    }

    /**
     * APi to send user details
     *
     * @param commonParams params to be sent
     */
    private void apiPutUserDetail(HashMap<String, Object> commonParams) {
        CommonParams params = new CommonParams.Builder().putMap(commonParams).build();
        RestClient.getApiInterface().putUserDetails(params.getMap())
                .enqueue(new ResponseResolver<FuguPutUserDetailsResponse>(FuguChannelsActivity.this, true, false) {
                    @Override
                    public void success(FuguPutUserDetailsResponse fuguPutUserDetailsResponse) {
                        CommonData.setUserDetails(fuguPutUserDetailsResponse);
                        CommonData.setConversationList(fuguPutUserDetailsResponse.getData().getFuguConversations());
                        setUpUI();
                    }

                    @Override
                    public void failure(APIError error) {
                        if (error.getStatusCode() == FuguAppConstant.SESSION_EXPIRE) {
                            Toast.makeText(FuguChannelsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            tvNoInternet.setVisibility(View.VISIBLE);
                            swipeRefresh.setVisibility(View.GONE);
                            tvNewConversation.setVisibility(View.GONE);
                        }
                    }
                });
    }

    /**
     * APi to send user details for reseller
     *
     * @param commonParams params to be sent
     */
    private void apiPutUserDetailReseller(HashMap<String, Object> commonParams) {
        CommonParams params = new CommonParams.Builder().putMap(commonParams).build();
        RestClient.getApiInterface().putUserDetailsReseller(params.getMap())
                .enqueue(new ResponseResolver<FuguPutUserDetailsResponse>(FuguChannelsActivity.this, true, false) {
                    @Override
                    public void success(FuguPutUserDetailsResponse fuguPutUserDetailsResponse) {
                        CommonData.setUserDetails(fuguPutUserDetailsResponse);
                        CommonData.setConversationList(fuguPutUserDetailsResponse.getData().getFuguConversations());
                        setUpUI();
                    }

                    @Override
                    public void failure(APIError error) {
                        if (error.getStatusCode() == FuguAppConstant.SESSION_EXPIRE) {
                            Toast.makeText(FuguChannelsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            tvNoInternet.setVisibility(View.VISIBLE);
                            swipeRefresh.setVisibility(View.GONE);
                            tvNewConversation.setVisibility(View.GONE);

                        }
                    }
                });
    }

    /**
     * Get conversations api hit
     */
    private void getConversations() {
        if (isNetworkAvailable()) {
            CommonParams commonParams = new CommonParams.Builder()
                    .add(APP_SECRET_KEY, HippoConfig.getInstance().getAppKey())
                    .add(EN_USER_ID, enUserId)
                    .add(APP_VERSION, BuildConfig.VERSION_NAME)
                    .add(DEVICE_TYPE, 1)
                    .build();
            RestClient.getApiInterface().getConversations(commonParams.getMap())
                    .enqueue(new ResponseResolver<FuguGetConversationsResponse>(FuguChannelsActivity.this, false, false) {
                        @Override
                        public void success(FuguGetConversationsResponse fuguGetConversationsResponse) {
                            try {

                                CommonData.setConversationList(fuguGetConversationsResponse.getData().getFuguConversationList());

                                fuguConversationList.clear();
                                fuguConversationList.addAll(fuguGetConversationsResponse.getData().getFuguConversationList());
                                fuguChannelsAdapter.notifyDataSetChanged();
                                swipeRefresh.setRefreshing(false);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void failure(APIError error) {
                            swipeRefresh.setRefreshing(false);
                        }
                    });
        } else {
            swipeRefresh.setRefreshing(false);
            // Toast.makeText(FuguFuguChannelsActivity.this, getString(R.string.fugu_unable_to_connect_internet), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IS_HIT_REQUIRED && resultCode == RESULT_OK) {
            // Make sure the request was successful
            //getConversations();

            FuguConversation conversation =
                    new Gson().fromJson(data.getStringExtra(FuguAppConstant.CONVERSATION), FuguConversation.class);

            if (conversation != null && conversation.getLabelId().compareTo(-1L) != 0) {
                for (int i = 0; i < fuguConversationList.size(); i++) {
                    if (fuguConversationList.get(i).getLabelId().compareTo(conversation.getLabelId()) == 0) {
                        fuguConversationList.get(i).setChannelId(conversation.getChannelId());
                        fuguConversationList.get(i).setMessage(conversation.getDefaultMessage());
                        fuguConversationList.get(i).setDateTime(conversation.getDateTime());
                        fuguConversationList.get(i).setChannelStatus(conversation.getChannelStatus());
                        fuguConversationList.get(i).setIsTimeSet(1);
                        fuguConversationList.get(i).setLast_sent_by_id(conversation.getLast_sent_by_id());
                        fuguConversationList.get(i).setUserId(conversation.getLast_sent_by_id());
                        fuguConversationList.get(i).setEnUserId(conversation.getEnUserId());
                        fuguConversationList.get(i).setLast_message_status(conversation.getLast_message_status());
                        fuguChannelsAdapter.updateList(fuguConversationList);
                        break;
                    }
                }
            } else if (conversation != null && conversation.getLabelId().compareTo(-1L) == 0) {
                for (int i = 0; i < fuguConversationList.size(); i++) {
                    if (fuguConversationList.get(i).getChannelId().compareTo(conversation.getChannelId()) == 0) {
                        fuguConversationList.get(i).setChannelId(conversation.getChannelId());
                        fuguConversationList.get(i).setMessage(conversation.getDefaultMessage());
                        fuguConversationList.get(i).setDateTime(conversation.getDateTime());
                        fuguConversationList.get(i).setChannelStatus(conversation.getChannelStatus());
                        fuguConversationList.get(i).setIsTimeSet(1);
                        fuguConversationList.get(i).setLast_sent_by_id(conversation.getLast_sent_by_id());
                        fuguConversationList.get(i).setLast_message_status(conversation.getLast_message_status());
                        fuguChannelsAdapter.updateList(fuguConversationList);
                        break;
                    }
                }
            }

        }
        try {
            if (CommonData.getIsNewChat()) {
                getConversations();
                CommonData.setIsNewchat(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Set Recycler Data
     */
    private void setRecyclerViewData() {
        RecyclerView rvChannels = (RecyclerView) findViewById(R.id.rvChannels);
        fuguChannelsAdapter = new FuguChannelsAdapter(FuguChannelsActivity.this, fuguConversationList, userName, userId, businessName
                , new FuguChannelsAdapter.Callback() {
            @Override
            public void onClick(FuguConversation conversation) {
                Intent chatIntent = new Intent(FuguChannelsActivity.this, FuguChatActivity.class);
                chatIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
                startActivityForResult(chatIntent, IS_HIT_REQUIRED);
            }
        }, enUserId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(FuguChannelsActivity.this);
        rvChannels.setLayoutManager(layoutManager);
        rvChannels.setAdapter(fuguChannelsAdapter);
    }

    /**
     * Set powered by text
     *
     * @param userData user data
     */
    private void setPoweredByText(FuguPutUserDetailsResponse.Data userData) {
        if (!userData.getWhiteLabel()) {
            try {
                poweredByView(getString(R.string.fugu_powered_by), getString(R.string.fugu_powered_by), hippoColorConfig);
            } catch (Exception e) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                    String text = "<font color="
                            + String.format("#%06X",
                            (0xFFFFFF & hippoColorConfig.getHippoTextColorPrimary())) + ">"
                            + getString(R.string.fugu_powered_by)
                            + "<font color=" + String.format("#%06X",
                            (0xFFFFFF & hippoColorConfig.getHippoThemeColorPrimary())) + "> "
                            + getString(R.string.hippo_text) + "</font>";
                    //noinspection deprecation
                    tvPoweredBy.setText(Html.fromHtml(text));
                } else {
                    String text = "<font color="
                            + String.format("#%06X",
                            (0xFFFFFF & hippoColorConfig.getHippoTextColorPrimary())) + ">"
                            + getString(R.string.fugu_powered_by)
                            + "<font color=" + String.format("#%06X",
                            (0xFFFFFF & hippoColorConfig.getHippoThemeColorPrimary())) + "> "
                            + getString(R.string.hippo_text) + "</font>";
                    tvPoweredBy.setText(Html.fromHtml(text));
                }
                tvPoweredBy.setBackgroundDrawable(HippoColorConfig.makeSelector(hippoColorConfig.getHippoChannelItemBg(), hippoColorConfig.getHippoChannelItemBgPressed()));
            }
        } else {
            tvPoweredBy.setVisibility(View.GONE);
        }
    }

    private void poweredByView(String firstString, String lastString, HippoColorConfig hippoColorConfig) throws Exception {
        String changeString = (lastString != null ? lastString : "");
        String totalString = firstString +" "+ changeString;
        Spannable spanText = new SpannableString(totalString);
        spanText.setSpan(new StyleSpan(Typeface.BOLD), String.valueOf(firstString).length(), totalString.length(), 0);
        spanText.setSpan(new ForegroundColorSpan(hippoColorConfig.getHippoActionBarBg()), String.valueOf(firstString).length(), totalString.length(), 0);
        spanText.setSpan(new RelativeSizeSpan(0.8f), 0, String.valueOf(firstString).length(), 0);


        tvPoweredBy.setText(spanText);
        tvPoweredBy.setBackgroundDrawable(HippoColorConfig.makeSelector(hippoColorConfig.getHippoChannelItemBg(), hippoColorConfig.getHippoChannelItemBgPressed()));
    }

}

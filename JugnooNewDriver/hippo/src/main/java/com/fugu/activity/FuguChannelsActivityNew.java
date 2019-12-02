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
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import androidx.core.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.core.content.LocalBroadcastManager;
import androidx.core.content.PermissionChecker;
import androidx.core.view.ViewPager;
import androidx.core.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fugu.BuildConfig;
import com.fugu.CaptureUserData;
import com.fugu.HippoColorConfig;
import com.fugu.HippoConfig;
import com.fugu.HippoNotificationConfig;
import com.fugu.R;
import com.fugu.activity.FuguBaseActivity;
import com.fugu.activity.FuguChatActivity;
import com.fugu.adapter.FuguChannelsPagerAdapter;
import com.fugu.constant.FuguAppConstant;
import com.fugu.database.CommonData;
import com.fugu.fragments.FuguChannelsFragment;
import com.fugu.model.FuguChannelInfoObject;
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
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;

/**
 * Created by cl-macmini-01 on 1/31/18.
 */

public class FuguChannelsActivityNew extends FuguBaseActivity implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private static final int NOT_CONNECTED = 0;
    private static final int CONNECTED_TO_INTERNET = 1;
    private static final int CONNECTED_TO_INTERNET_VIA_WIFI = 2;
    public static boolean isRefresh = false;
    public static Long readChannelId = -1L;
    @SuppressLint("StaticFieldLeak")
    private static LinearLayout llInternet;
    @SuppressLint("StaticFieldLeak")
    private static TextView tvStatus;
    private final int READ_PHONE_PERMISSION = 101;
    private final String TAG = FuguChannelsActivityNew.class.getSimpleName();
    private final int IS_HIT_REQUIRED = 200;
    private TextView tvNoInternet, tvNewConversation;
    private TextView tvPoweredBy;
    private HippoColorConfig hippoColorConfig;
    private SwipeRefreshLayout swipeRefresh;
    private ArrayList<FuguConversation> hippoConversationList = new ArrayList<>();
    private String label = "";
    private Long userId = -1L;
    private String enUserId = "";
    private String userName = "Anonymous";
    private String businessName = "Anonymous";
    private int appVersion = 0;
    private TabLayout tabLayout;
    private ViewPager viewPagerChannels;
    private FuguChannelsPagerAdapter mHippoChannelsPagerAdapter;
    private CaptureUserData userData;
    /**
     * Broadcast receiver to handle push messages on channels screen
     */
    BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            try {
                JSONObject messageJson = new JSONObject(intent.getStringExtra(MESSAGE));

                if (messageJson.getInt(NOTIFICATION_TYPE) == 5) {
                    //fetch conversation again and do complete refresh
                    getConversations();
                } else {
                    //propagate push json to all tabs
                    for (Fragment fragment : mHippoChannelsPagerAdapter.getFragments()) {
                        ((FuguChannelsFragment) fragment).handleMessagePush(messageJson);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

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
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fugu_activity_channel_new);

        userData = getIntent().getParcelableExtra("userData");
        if (userData == null) {
            userData = HippoConfig.getInstance().getUserData();
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(NOTIFICATION_INTENT));
        initViews();
        decideAppFlow();

    }

    @Override
    public void onResume() {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.cancelAll();
        }
        super.onResume();
        if (isRefresh) {
            isRefresh = false;
            //refresh the unread uncount for each tab for the particular readChannelId
            for (Fragment fragment : mHippoChannelsPagerAdapter.getFragments()) {
                ((FuguChannelsFragment) fragment).resetUnreadCount(readChannelId);
            }

        }

        // check we have a new peer chat, if yes we have to refresh ourselves
        if(CommonData.getNewPeerChatCreated()){
            getConversations();
            CommonData.setNewPeerChatCreated(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        readChannelId = null;
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
                    //ActivityCompat.shouldShowRequestPermissionRationale(HippoHippoChannelsActivity.this, Manifest.permission.READ_PHONE_STATE);
                    Toast.makeText(FuguChannelsActivityNew.this, "Go to Settings and grant permission to access phone state", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

    /**
     * Init views
     */
    private void initViews() {

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        setToolbar(myToolbar, getIntent().getStringExtra("title"));

        appVersion = getIntent().getIntExtra("appVersion", 0);
        hippoColorConfig = CommonData.getColorConfig();
        findViewById(R.id.llRoot).setBackgroundColor(hippoColorConfig.getHippoChannelBg());

        tvNoInternet = (TextView) findViewById(R.id.tvNoInternet);
        tvNoInternet.setTypeface(CommonData.getFontConfig().getNormalTextTypeFace(this.getApplicationContext()));
        tvNewConversation = (TextView) findViewById(R.id.tvNewConversation);
        tvPoweredBy = (TextView) findViewById(R.id.tvPoweredBy);
        tvPoweredBy.setTypeface(CommonData.getFontConfig().getNormalTextTypeFace(this.getApplicationContext()));
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        llInternet = (LinearLayout) findViewById(R.id.llInternet);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(this);

        configColors();

        if (!isNetworkAvailable()) {
            llInternet.setVisibility(View.VISIBLE);
            llInternet.setBackgroundColor(Color.parseColor("#FF0000"));
            tvStatus.setText(R.string.fugu_not_connected_to_internet);
        }

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setBackgroundDrawable(new ColorDrawable(hippoColorConfig.getHippoActionBarBg()));
        tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this,R.color.fugu_jugnoo_orange));
        tabLayout.setTabTextColors(hippoColorConfig.getHippoActionBarText(),ContextCompat.getColor(this,android.R.color.black));
        viewPagerChannels = (ViewPager) findViewById(R.id.vwPagerChannels);

        viewPagerChannels.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {

            }

            @Override
            public void onPageScrollStateChanged(final int state) {
                enableDisableSwipeRefresh( state == ViewPager.SCROLL_STATE_IDLE );
            }
        });

        FloatingActionButton btnNewChat = (FloatingActionButton)findViewById(R.id.btnNewChat);
        btnNewChat.setOnClickListener(this);
    }


    private void enableDisableSwipeRefresh(boolean enable) {
        if (swipeRefresh != null) {
            swipeRefresh.setEnabled(enable);
        }
    }

    /**
     * Config Colors of App
     */
    private void configColors() {
        tvNewConversation.setTextColor(hippoColorConfig.getHippoActionBarText());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            tvNewConversation.setBackground(HippoColorConfig.makeRoundedSelector(hippoColorConfig.getHippoActionBarBg()));
        } else {
            tvNewConversation.setBackgroundDrawable(HippoColorConfig.makeRoundedSelector(hippoColorConfig.getHippoActionBarBg()));
        }
        swipeRefresh.setColorSchemeColors(hippoColorConfig.getHippoThemeColorPrimary());
        tvNewConversation.setTextColor(hippoColorConfig.getHippoActionBarText());
        tvNoInternet.setTextColor(hippoColorConfig.getHippoThemeColorSecondary());

    }

    /**
     * Decide app flow on basis of user data and permisions
     */
    private void decideAppFlow() {
        if (CommonData.getUserDetails() != null && CommonData.getConversationList().size() > 0) {
            setUpUI();
            getConversations();
        } else if (HippoConfig.getInstance().isPermissionGranted(FuguChannelsActivityNew.this, Manifest.permission.READ_PHONE_STATE)) {
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
        hippoConversationList.clear();
        if (userData.getFuguConversations().size() > 0) {
            hippoConversationList.addAll(CommonData.getConversationList());
        }
        setRecyclerViewData();
        setPoweredByText(userData);
    }

    /**
     * Sets the channels data
     */
    private void setRecyclerViewData() {
        // set up viewpager and load content
        ArrayList<ArrayList<FuguConversation>> conversations = new ArrayList<>();
        ArrayList<Fragment> channelFragments = new ArrayList<>();
        ArrayList<String> titles = new ArrayList<>();

        // first tab shall default to all and will have all conversations
        ArrayList<FuguConversation> allConversations = new ArrayList<>();
        for (FuguConversation conversation : hippoConversationList) {
            try {
                allConversations.add((FuguConversation) conversation.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        conversations.add(allConversations);
        channelFragments.add(FuguChannelsFragment.newInstance(allConversations));
        titles.add(getResources().getString(R.string.fugu_text_all));

        ArrayList<FuguChannelInfoObject> channels = new ArrayList<>();
        if (userData.getChannelInfoJson() != null) {
            channels = new Gson().fromJson(userData.getChannelInfoJson(),
                    new TypeToken<ArrayList<FuguChannelInfoObject>>() {
                    }.getType());

            // only keep visible channels
            ListIterator<FuguChannelInfoObject> iterator = channels.listIterator();
            while (iterator.hasNext()) {
                if (!iterator.next().getIsVisible()) {
                    iterator.remove();
                }
            }
        }

        for (FuguChannelInfoObject hippoChannelInfoObject : channels) {

            // add title
            if (hippoChannelInfoObject.getValue() != null) {
                titles.add(hippoChannelInfoObject.getValue());
            } else {
                titles.add("");
            }

            ArrayList<FuguConversation> filteredConversations = new ArrayList<>();
            // decide whether to filter by chat_type or transaction id
            if (hippoChannelInfoObject.isFilterByChatType()) {
                int chatTypeFilterValue = hippoChannelInfoObject.getChatTypeFilterValue();
                for (FuguConversation conversation : hippoConversationList) {
                    if (conversation.getChatType() == chatTypeFilterValue) {
                        try {
                            filteredConversations.add((FuguConversation) conversation.clone());
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                // filter by transaction id ( key would be present after last underscore )
                String filterKey = hippoChannelInfoObject.getKey();
                for (FuguConversation conversation : hippoConversationList) {
                    if (conversation.getTransactionId() != null) {
                        String[] split = conversation.getTransactionId().split("_");
                        if (split != null && split.length > 0) {
                            // last index shall contain the key
                            String key = split[split.length - 1];
                            if (filterKey.equals(key)) {
                                try {
                                    filteredConversations.add((FuguConversation) conversation.clone());
                                } catch (CloneNotSupportedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }

            channelFragments.add(FuguChannelsFragment.newInstance(filteredConversations));

            // add the conversation object
            conversations.add(filteredConversations);
        }

        // set up viewpager and tab
        if (mHippoChannelsPagerAdapter == null) {
            mHippoChannelsPagerAdapter = new FuguChannelsPagerAdapter(getSupportFragmentManager(),
                    channelFragments, titles);
            viewPagerChannels.setAdapter(mHippoChannelsPagerAdapter);
            viewPagerChannels.setOffscreenPageLimit(titles.size() - 1);
            tabLayout.setupWithViewPager(viewPagerChannels);
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

            try {
               changeFontInViewGroup(tabLayout);
            }catch (Exception e){
                e.printStackTrace();
            }

        } else {
            // update list
            int i = 0;
            for (Fragment fragment : mHippoChannelsPagerAdapter.getFragments()) {
                ((FuguChannelsFragment) fragment).setConversationList(conversations.get(i));
                i++;
            }
        }
    }

    /**
     * Change font for a viewgroup
     * @param viewGroup the viewGroup
     */
    void changeFontInViewGroup(ViewGroup viewGroup) {
        Typeface typeface = CommonData.getFontConfig().getNormalTextTypeFace(this.getApplicationContext());
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            if (TextView.class.isAssignableFrom(child.getClass())) {
                 ((TextView) child).setTypeface(typeface,Typeface.BOLD);
            } else if (ViewGroup.class.isAssignableFrom(child.getClass())) {
                changeFontInViewGroup((ViewGroup) viewGroup.getChildAt(i));
            }
        }
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // pass the activity result to visible fragments
        for (Fragment fragment : mHippoChannelsPagerAdapter.getFragments()) {
            ((FuguChannelsFragment) fragment).onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Set powered by text
     *
     * @param userData user data
     */
    private void setPoweredByText(FuguPutUserDetailsResponse.Data userData) {
        if (!userData.getWhiteLabel()) {
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
        } else {
            tvPoweredBy.setVisibility(View.GONE);
        }
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
                    .enqueue(new ResponseResolver<FuguGetConversationsResponse>(FuguChannelsActivityNew.this,
                            false, false) {
                        @Override
                        public void success(FuguGetConversationsResponse hippoGetConversationsResponse) {
                            try {

                                CommonData.setConversationList(hippoGetConversationsResponse.getData().getFuguConversationList());

                                hippoConversationList.clear();
                                hippoConversationList.addAll(hippoGetConversationsResponse.getData().getFuguConversationList());
                                // set data
                                setRecyclerViewData();
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
            // Toast.makeText(HippoHippoChannelsActivity.this, getString(R.string.hippo_unable_to_connect_internet), Toast.LENGTH_SHORT).show();
        }
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

            Intent chatIntent = new Intent(this, FuguChatActivity.class);
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
        else if(v.getId() == R.id.btnNewChat){
            //send a broadcast to listening parent app
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("action_type","OPEN_NEW_CHAT");
            Intent intent = new Intent();
            intent.putExtra(FUGU_CUSTOM_ACTION_PAYLOAD, jsonObject.toString());
            intent.setAction(FUGU_CUSTOM_ACTION_SELECTED);
            sendBroadcast(intent);

        }
    }


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

            HashMap<String, Object> commonParams = new HashMap<>();
            HippoConfig.getInstance();
            if (HippoConfig.getmResellerToken() != null) {
                commonParams.put(RESELLER_TOKEN, HippoConfig.getmResellerToken());
                commonParams.put(REFERENCE_ID, String.valueOf(HippoConfig.getmReferenceId()));
            } else {
                commonParams.put(APP_SECRET_KEY, HippoConfig.getInstance().getAppKey());
            }
            commonParams.put(DEVICE_ID, UniqueIMEIID.getUniqueIMEIId(FuguChannelsActivityNew.this));
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
        RestClient.getApiInterface().putUserDetails(commonParams)
                .enqueue(new ResponseResolver<FuguPutUserDetailsResponse>(FuguChannelsActivityNew.this,
                        true, false) {
                    @Override
                    public void success(FuguPutUserDetailsResponse hippoPutUserDetailsResponse) {
                        CommonData.setUserDetails(hippoPutUserDetailsResponse);
                        CommonData.setConversationList(hippoPutUserDetailsResponse.getData().getFuguConversations());
                        setUpUI();
                    }

                    @Override
                    public void failure(APIError error) {
                        if (error.getStatusCode() == FuguAppConstant.SESSION_EXPIRE) {
                            Toast.makeText(FuguChannelsActivityNew.this,
                                    error.getMessage(), Toast.LENGTH_SHORT).show();
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
        RestClient.getApiInterface().putUserDetailsReseller(commonParams)
                .enqueue(new ResponseResolver<FuguPutUserDetailsResponse>(FuguChannelsActivityNew.this,
                        true, false) {
                    @Override
                    public void success(FuguPutUserDetailsResponse hippoPutUserDetailsResponse) {
                        CommonData.setUserDetails(hippoPutUserDetailsResponse);
                        CommonData.setConversationList(hippoPutUserDetailsResponse.getData().getFuguConversations());
                        setUpUI();
                    }

                    @Override
                    public void failure(APIError error) {
                        if (error.getStatusCode() == FuguAppConstant.SESSION_EXPIRE) {
                            Toast.makeText(FuguChannelsActivityNew.this,
                                    error.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            tvNoInternet.setVisibility(View.VISIBLE);
                            swipeRefresh.setVisibility(View.GONE);
                            tvNewConversation.setVisibility(View.GONE);

                        }
                    }
                });
    }

    @Override
    public void onRefresh() {
        getConversations();
    }
}

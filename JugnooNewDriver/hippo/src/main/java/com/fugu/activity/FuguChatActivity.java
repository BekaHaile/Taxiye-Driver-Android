package com.fugu.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import androidx.annotation.NonNull;
import androidx.core.content.LocalBroadcastManager;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fugu.BuildConfig;
import com.fugu.HippoColorConfig;
import com.fugu.HippoConfig;
import com.fugu.HippoNotificationConfig;
import com.fugu.R;
import com.fugu.adapter.EventItem;
import com.fugu.adapter.FuguMessageAdapter;
import com.fugu.adapter.HeaderItem;
import com.fugu.adapter.ListItem;
import com.fugu.apis.ApiPutUserDetails;
import com.fugu.constant.FuguAppConstant;
import com.fugu.database.CommonData;
import com.fugu.datastructure.ChannelStatus;
import com.fugu.datastructure.ChatType;
import com.fugu.model.CustomAction;
import com.fugu.model.FuguConversation;
import com.fugu.model.FuguCreateConversationParams;
import com.fugu.model.FuguCreateConversationResponse;
import com.fugu.model.FuguFileDetails;
import com.fugu.model.FuguGetByLabelIdParams;
import com.fugu.model.FuguGetMessageParams;
import com.fugu.model.FuguGetMessageResponse;
import com.fugu.model.FuguUploadImageResponse;
import com.fugu.model.Message;
import com.fugu.retrofit.APIError;
import com.fugu.retrofit.MultipartParams;
import com.fugu.retrofit.ResponseResolver;
import com.fugu.retrofit.RestClient;
import com.fugu.utils.CustomAlertDialog;
import com.fugu.utils.CustomLinear;
import com.fugu.utils.CustomLinearLayoutManager;
import com.fugu.utils.DateUtils;
import com.fugu.utils.FuguImageUtils;
import com.fugu.utils.FuguLog;
import com.fugu.utils.StringUtil;
import com.fugu.utils.beatAnimation.AVLoadingIndicatorView;
import com.fugu.utils.loadingBox.LoadingBox;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.UUID;

import faye.FayeCallback;
import faye.FayeClient;
import faye.FayeClientListener;


public class FuguChatActivity extends FuguBaseActivity implements Animation.AnimationListener, Readfunctionality {

    private String TAG = getClass().getSimpleName();
    private static final int NOT_CONNECTED = 0;
    private static final int CONNECTED_TO_INTERNET = 1;
    private static final int CONNECTED_TO_INTERNET_VIA_WIFI = 2;
    // Initial FayeClient
    private static FayeClient mClient;

    // Declaring Views
    private RelativeLayout rlRoot;
    private CustomLinear llRoot;
    private CardView cvTypeMessage;
    private LinearLayout llMessageLayout;
    private TextView tvClosed;
    private TextView tvNoInternet;
    private EditText etMsg;
    private RecyclerView rvMessages;
    private FuguMessageAdapter fuguMessageAdapter;
    private Toolbar myToolbar;
    private AVLoadingIndicatorView aviTyping;
    private LinearLayout llTyping;
    private ProgressBar pbLoading;
    private FuguConversation conversation;
    private ProgressBar pbSendingImage;
    private ImageView ivSend;
    private boolean isNetworkStateChanged = false;
    private boolean isFayeChannelActive = false;
    private boolean isFirst = true;
    private Animation animSlideUp, animSlideDown;
    private FuguGetMessageResponse mFuguGetMessageResponse;
    // Declaring objects and variables
    @NonNull
    private ArrayList<ListItem> fuguMessageList = new ArrayList<>();
    @NonNull
    private TreeMap<String, ListItem> unsentMessageMap = new TreeMap<>();

    private String sentAtUTC = "";
    private Long channelId = -1L;
    public static Long currentChannelId = -1L;
    private Long userId = -1L;
    private String enUserId = "";
    private Long labelId = -1L;
    private String userName = "";
    private int isTyping = TYPING_SHOW_MESSAGE;
    private String label = "";
    private String defaultMessage = "";
    private String businessName = "";
    private int status;
    private boolean isConversationCreated;
    private FuguImageUtils fuguImageUtils;
    private int onSubscribe = CHANNEL_UNSUBSCRIBED;
    private boolean showLoading = true;
    private boolean allMessagesFetched = false;
    private DateUtils dateUtils;
    private int pageStart = 1, position;
    private int dateItemCount = 0;
    private HippoColorConfig hippoColorConfig;
    private CustomLinearLayoutManager layoutManager;
    private TextView tvDateLabel;
    private ArrayList<String> messageResponse = new ArrayList<>();
    private boolean isP2P = false;
    private FuguCreateConversationParams fuguCreateConversationParams;
    private ProgressBar pbPeerChat;
    private int previousPos = 0;
    private boolean runAnim = true, runAnim2 = false;
    private Handler handler = null;
    @SuppressLint("StaticFieldLeak")
    private static LinearLayout llInternet;
    @SuppressLint("StaticFieldLeak")
    private static TextView tvStatus;
    private HashMap<String, Long> transactionIdsMap;
    private String globalUuid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fugu_activity_chat);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiverChat,
                new IntentFilter(NOTIFICATION_INTENT));

        initViews();
        fetchIntentData();
        setUpUI();
        stateChangeListeners();

        HippoConfig.getClient(new FayeCallback() {
            @Override
            public void onClientReady(FayeClient fayeClient) {
                mClient = fayeClient;
                setUpFayeConnection();
            }
        });


        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                getIntentFilter());
        CommonData.clearPushChannel();

    }

    // intentFilter to add multiple actions
    private IntentFilter getIntentFilter() {
        IntentFilter intent = new IntentFilter();
        intent.addAction(NETWORK_STATE_INTENT);
        intent.addAction(NOTIFICATION_TAPPED);
        return intent;
    }

    private void initViews() {
        transactionIdsMap = CommonData.getTransactionIdsMap();
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        hippoColorConfig = CommonData.getColorConfig();
        rlRoot = (RelativeLayout) findViewById(R.id.rlRoot);
        llRoot = (CustomLinear) findViewById(R.id.llRoot);
        tvDateLabel = (TextView) findViewById(R.id.tvDateLabel);
        cvTypeMessage = (CardView) findViewById(R.id.cvTypeMessage);
        tvClosed = (TextView) findViewById(R.id.tvClosed);
        tvNoInternet = (TextView) findViewById(R.id.tvNoInternet);
        aviTyping = (AVLoadingIndicatorView) findViewById(R.id.aviTyping);
        llTyping = (LinearLayout) findViewById(R.id.llTyping);
        etMsg = (EditText) findViewById(R.id.etMsg);
        pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        pbSendingImage = (ProgressBar) findViewById(R.id.pbSendingImage);
        ivSend = (ImageView) findViewById(R.id.ivSend);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        llInternet = (LinearLayout) findViewById(R.id.llInternet);
        llMessageLayout = (LinearLayout) findViewById(R.id.llMessageLayout);
        fuguImageUtils = new FuguImageUtils(FuguChatActivity.this);
        dateUtils = DateUtils.getInstance();
        pbPeerChat = (ProgressBar) findViewById(R.id.pbPeerChat);
        rvMessages = (RecyclerView) findViewById(R.id.rvMessages);
        configColors();
        if (!isNetworkAvailable()) {
            llInternet.setVisibility(View.VISIBLE);
            llInternet.setBackgroundColor(Color.parseColor("#FF0000"));
            tvStatus.setText(R.string.fugu_not_connected_to_internet);
        }
        animSlideUp = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fugu_slide_up_time);
        animSlideUp.setAnimationListener(this);

        animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fugu_slide_down_time);
        animSlideDown.setAnimationListener(this);

        Typeface typeface = CommonData.getFontConfig().getNormalTextTypeFace(this.getApplicationContext());
        tvDateLabel.setTypeface(typeface); tvClosed.setTypeface(typeface); tvNoInternet.setTypeface(typeface);
        etMsg.setTypeface(typeface); tvStatus.setTypeface(typeface);


    }

    private void configColors() {
        rlRoot.setBackgroundColor(hippoColorConfig.getHippoChatBg());
        GradientDrawable drawable = (GradientDrawable) llTyping.getBackground();
        drawable.setColor(hippoColorConfig.getHippoBgMessageFrom());
        drawable.setStroke((int) getResources().getDimension(R.dimen.fugu_border_width), hippoColorConfig.getHippoBorderColor()); // set stroke width and stroke color
        aviTyping.setIndicatorColor(hippoColorConfig.getHippoPrimaryTextMsgFrom());
        tvClosed.setTextColor(hippoColorConfig.getHippoThemeColorPrimary());
        tvClosed.getBackground().setColorFilter(hippoColorConfig.getHippoChannelItemBg(), PorterDuff.Mode.SRC_ATOP);
        cvTypeMessage.getBackground().setColorFilter(hippoColorConfig.getHippoTypeMessageBg(), PorterDuff.Mode.SRC_ATOP);
        etMsg.setHintTextColor(hippoColorConfig.getHippoTypeMessageHint());
        etMsg.setTextColor(hippoColorConfig.getHippoTypeMessageText());
        tvNoInternet.setTextColor(hippoColorConfig.getHippoThemeColorPrimary());
    }

    public static void changeStatus(int status) {
        try {
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
        } catch (Exception e) {

        }
    }

    private BroadcastReceiver mMessageReceiverChat = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            try {
                JSONObject messageJson = new JSONObject(intent.getStringExtra(MESSAGE));
                FuguLog.d("receiver", "Got message: " + messageJson.toString());
                if (messageJson.getInt(NOTIFICATION_TYPE) == 5) {
                    CommonData.setIsNewchat(true);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void fetchIntentData() {
        conversation = new Gson().fromJson(getIntent().getStringExtra(FuguAppConstant.CONVERSATION), FuguConversation.class);
        if (conversation.getUnreadCount() > 0) {
            rvMessages.setAlpha(0);
        }

        FuguLog.d("userName in SDK", "FuguChatActivity onCreate " + conversation.getUserName());
        int chatType = getIntent().getIntExtra(CHAT_TYPE, ChatType.P2P.getOrdinal());
        if (!TextUtils.isEmpty(conversation.getLabel())) {
            label = conversation.getLabel();
        } else {
            label = conversation.getBusinessName();
        }
        if (conversation.getLabelId() != null) {
            labelId = conversation.getLabelId();
        }
        channelId = conversation.getChannelId();
        HippoNotificationConfig.pushChannelId = conversation.getChannelId();
        currentChannelId = conversation.getChannelId();
        FuguLog.e(TAG, "==" + HippoNotificationConfig.pushChannelId);

        userId = conversation.getUserId();
        enUserId = conversation.getEnUserId();
        userName = StringUtil.toCamelCase(conversation.getUserName());
        status = conversation.getStatus();
        defaultMessage = conversation.getDefaultMessage();
        businessName = conversation.getBusinessName();


        FuguLog.v("is p2p chat", "---> " + isP2P);

        if (chatType == ChatType.GROUP_CHAT.getOrdinal()) {
            isP2P = true;
            fuguCreateConversationParams = new Gson().fromJson(getIntent()
                    .getStringExtra(FuguAppConstant.PEER_CHAT_PARAMS), FuguCreateConversationParams.class);
        } else if (chatType == ChatType.CHAT_BY_TRANSACTION_ID.getOrdinal()) {
            isP2P = true;
            fuguCreateConversationParams = new Gson().fromJson(getIntent()
                    .getStringExtra(FuguAppConstant.PEER_CHAT_PARAMS), FuguCreateConversationParams.class);
            label = "";
        } else {
            fuguCreateConversationParams = new FuguCreateConversationParams(HippoConfig.getInstance().getAppKey(), labelId,
                    enUserId);
        }
        setToolbar(myToolbar, label);

        if (CommonData.getUnsentMessageMapByChannel(channelId) != null) {
            unsentMessageMap = CommonData.getUnsentMessageMapByChannel(channelId);
        }
    }

    private void setUpUI() {
        allMessagesFetched = false;
        fuguMessageAdapter = new FuguMessageAdapter(FuguChatActivity.this, fuguMessageList, labelId, conversation);
        layoutManager = new CustomLinearLayoutManager(FuguChatActivity.this);
        layoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(layoutManager);
        rvMessages.setAdapter(fuguMessageAdapter);
        rvMessages.setItemAnimator(null);
        fuguMessageAdapter.notifyDataSetChanged();
        rvMessages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, final int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        runAnim2 = false;
                        if (handler == null) {
                            handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (!runAnim2) {
                                        animSlideUp = AnimationUtils.loadAnimation(getApplicationContext(),
                                                R.anim.fugu_slide_up_time);
                                        tvDateLabel.startAnimation(animSlideUp);
                                        tvDateLabel.setVisibility(View.INVISIBLE);
                                        animSlideUp.setAnimationListener(new Animation.AnimationListener() {
                                            @Override
                                            public void onAnimationStart(Animation animation) {
                                            }

                                            @Override
                                            public void onAnimationEnd(Animation animation) {
                                                runAnim = true;
                                                handler = null;
                                            }

                                            @Override
                                            public void onAnimationRepeat(Animation animation) {

                                            }
                                        });
                                    } else {
                                        handler = null;
                                    }
                                }
                            }, 1200);
                        }


                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                        FuguLog.d(TAG, "scroll down triggered");
                        tvDateLabel.clearAnimation();
                        runAnim2 = true;
                        if (runAnim) {
                            tvDateLabel.setVisibility(View.VISIBLE);
                            tvDateLabel.clearAnimation();
                            animSlideDown = AnimationUtils.loadAnimation(getApplicationContext(),
                                    R.anim.fugu_slide_down_time);
                            tvDateLabel.startAnimation(animSlideDown);
                            animSlideDown.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                    runAnim = false;
                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
                            break;
                        }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                position = layoutManager.findFirstVisibleItemPosition();

                if (previousPos > position && fuguMessageList.size() != 0) {
                    if (fuguMessageList.get(position).getType() == ListItem.ITEM_TYPE_SELF || fuguMessageList.get(position).getType() == ListItem.ITEM_TYPE_OTHER) {
                        if (!TextUtils.isEmpty(((EventItem) fuguMessageList.get(position)).getEvent().getSentAtUtc())) {
                            tvDateLabel.setText(DateUtils.getDate(dateUtils.convertToLocal(((EventItem) fuguMessageList.get(position)).getEvent().getSentAtUtc())));
                        }
                    }
                } else if (fuguMessageList.size() != 0) {
                    if (fuguMessageList.get(position).getType() == ListItem.TYPE_HEADER) {
                        tvDateLabel.setText(((HeaderItem) fuguMessageList.get(position)).getDate());
                    }
                }
                previousPos = position;
            }
        });
        if (channelId.compareTo(-1L) == 0 && labelId.compareTo(-1L) != 0 && !conversation.isOpenChat()) {
            globalUuid = UUID.randomUUID().toString();
            fuguMessageList.add(new EventItem(
                    new Message(businessName,
                            -1L,
                            defaultMessage,
                            "",
                            false,
                            onSubscribe == CHANNEL_SUBSCRIBED ? MESSAGE_READ : MESSAGE_UNSENT,
                            0,
                            TEXT_MESSAGE, onSubscribe == CHANNEL_SUBSCRIBED, globalUuid)));
        }

        if (isP2P) {
            FuguLog.v("call createConversation", "setUpUI");
            transactionIdsMap = new HashMap<>();
            if (CommonData.getTransactionIdsMap() != null) {
                transactionIdsMap = CommonData.getTransactionIdsMap();
            }
            if (!transactionIdsMap.isEmpty() && transactionIdsMap.get(fuguCreateConversationParams.getTransactionId()) != null) {
                channelId = transactionIdsMap.get(fuguCreateConversationParams.getTransactionId());
                pbLoading.setVisibility(View.GONE);
                pbPeerChat.setVisibility(View.GONE);
                llMessageLayout.setVisibility(View.VISIBLE);
                cvTypeMessage.setVisibility(View.VISIBLE);
                getMessagesFromCache();
            } else {
                pbPeerChat.setVisibility(View.VISIBLE);
                llMessageLayout.setVisibility(View.GONE);
                cvTypeMessage.setVisibility(View.GONE);
            }
            createConversation(TEXT_MESSAGE, getString(R.string.fugu_empty), getString(R.string.fugu_empty), null, isP2P);
            pbSendingImage.setVisibility(View.GONE);
        } else if (conversation.isOpenChat()) {
            if (CommonData.getLabelIdResponse(labelId) != null && CommonData.getLabelIdResponse(labelId).getData().getMessages().size() > 0) {
                showLoading = false;
                fuguMessageList.clear();
                dateItemCount = 0;
                sentAtUTC = "";

                for (int i = 0; i < CommonData.getLabelIdResponse(labelId).getData().getMessages().size(); i++) {
                    Message messageObj = CommonData.getLabelIdResponse(labelId).getData().getMessages().get(i);
                    boolean isSelf = false;
                    if (messageObj.getUserId().compareTo(userId) == 0)
                        isSelf = true;
                    if (!TextUtils.isEmpty(messageObj.getSentAtUtc())) {
                        String date = DateUtils.getDate(dateUtils.convertToLocal(messageObj.getSentAtUtc()));
                        if (!sentAtUTC.equalsIgnoreCase(date)) {
                            fuguMessageList.add(new HeaderItem(date));
                            sentAtUTC = date;
                            dateItemCount = dateItemCount + 1;
                        }
                    }

                    Message message = new Message(messageObj.getId(), messageObj.getfromName(),
                            messageObj.getUserId(),
                            messageObj.getMessage(),
                            messageObj.getSentAtUtc(),
                            isSelf,
                            messageObj.getMessageStatus(),
                            i,
                            messageObj.getUrl(),
                            messageObj.getThumbnailUrl(),
                            messageObj.getMessageType(),
                            messageObj.isSent(),
                            messageObj.getUuid());
                    if (messageObj.getCustomAction() != null) {
                        message.setCustomAction(messageObj.getCustomAction());
                    }
                    fuguMessageList.add(new EventItem(message));
                }


                llRoot.setVisibility(View.VISIBLE);
                if (!CommonData.getLabelIdResponse(labelId).getData().getLabel().isEmpty()) {
                    label = CommonData.getLabelIdResponse(labelId).getData().getLabel();
                    setToolbar(myToolbar, label);
                }

                if (status == 0) {
                    cvTypeMessage.setVisibility(View.GONE);
                    llMessageLayout.setVisibility(View.GONE);
                    tvClosed.setVisibility(View.VISIBLE);
                } else {
                    if (conversation.getLabelId().compareTo(-1L) != 0) {
                        getByLabelId();
                    } else {
                        if (isFayeChannelActive) {
                            getMessages(null);
                        }
                    }
                }
            } else {
                if (conversation.getLabelId().compareTo(-1L) != 0) {
                    FuguLog.e(TAG, "onClick: " + CommonData.getConversationList().get(0).getLabelId());
                    getByLabelId();
                } else {
                    getMessagesFromCache();
                }
            }


            if (status == 0) {
                cvTypeMessage.setVisibility(View.GONE);
                llMessageLayout.setVisibility(View.GONE);
                tvClosed.setVisibility(View.VISIBLE);
            }
        } else if (channelId.compareTo(-1L) == 0) {
            setRecyclerViewData();
            llRoot.setVisibility(View.VISIBLE);

            if (status == 0) {
                cvTypeMessage.setVisibility(View.GONE);
                llMessageLayout.setVisibility(View.GONE);
                tvClosed.setVisibility(View.VISIBLE);
            }
        } else {
            if (CommonData.getMessageResponse(channelId) != null && CommonData.getMessageResponse(channelId).getData().getMessages().size() > 0) {
                getMessagesFromCache();
            } else {
                getMessages(null);
            }
        }
    }

    private void getMessagesFromCache() {
        showLoading = false;
        LoadingBox.showOn(this);
        fuguMessageList.clear();
        dateItemCount = 0;
        sentAtUTC = "";
        if (CommonData.getMessageResponse(channelId) != null) {
            for (int i = 0; i < CommonData.getMessageResponse(channelId).getData().getMessages().size(); i++) {
                Message messageObj = CommonData.getMessageResponse(channelId).getData().getMessages().get(i);
                boolean isSelf = false;
                if (messageObj.getUserId().compareTo(userId) == 0) {
                    isSelf = true;
                }

                if (!TextUtils.isEmpty(messageObj.getSentAtUtc())) {
                    String date = DateUtils.getDate(dateUtils.convertToLocal(messageObj.getSentAtUtc()));
                    if (!sentAtUTC.equalsIgnoreCase(date)) {
                        fuguMessageList.add(new HeaderItem(date));
                        sentAtUTC = date;
                        dateItemCount = dateItemCount + 1;
                    }
                }
                Message message = new Message(messageObj.getId(), messageObj.getfromName(),
                        messageObj.getUserId(),
                        messageObj.getMessage(),
                        messageObj.getSentAtUtc(),
                        isSelf,
                        messageObj.getMessageStatus(),
                        i,
                        messageObj.getUrl(),
                        messageObj.getThumbnailUrl(),
                        messageObj.getMessageType(),
                        messageObj.isSent(),
                        messageObj.getUuid());
                message.setTimeIndex(messageObj.getTimeIndex());
                if (messageObj.getCustomAction() != null) {
                    message.setCustomAction(messageObj.getCustomAction());
                }
                fuguMessageList.add(new EventItem(message));
            }
            if (!isFayeChannelActive) {
                for (String key : unsentMessageMap.keySet()) {
                    Message messageObj = ((EventItem) unsentMessageMap.get(key)).getEvent();
                    fuguMessageList.add(new EventItem(messageObj));
                }
            }
            if (!CommonData.getMessageResponse(channelId).getData().getLabel().isEmpty()) {
                label = CommonData.getMessageResponse(channelId).getData().getLabel();
                setToolbar(myToolbar, label);
            }
            setRecyclerViewData();
            llRoot.setVisibility(View.VISIBLE);

            if (status == 0) {
                cvTypeMessage.setVisibility(View.GONE);
                llMessageLayout.setVisibility(View.GONE);
                tvClosed.setVisibility(View.VISIBLE);
            }

            if (unsentMessageMap.size() == 0 && !isP2P) {
                getMessages(null);
            } else {
                setUpFayeConnection();
                if (!isP2P) {
                    getMessages(null);
                }
            }

            if (status == 0) {
                cvTypeMessage.setVisibility(View.GONE);
                llMessageLayout.setVisibility(View.GONE);
                tvClosed.setVisibility(View.VISIBLE);
            }
        } else {
            if (channelId != null && channelId.compareTo(-1L) != 0) {
                getMessages(null);
            }
        }
        LoadingBox.hide();
    }

    private void stateChangeListeners() {
        etMsg.requestFocus();
        etMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.length() > 0) {
                    ivSend.setClickable(true);
                    ivSend.setAlpha(1f);
                } else {
                    ivSend.setClickable(false);
                    ivSend.setAlpha(0.5f);
                }
            }

            private Timer timer = new Timer();
            private final long DELAY = 3000; // milliseconds

            @Override
            public void afterTextChanged(Editable editable) {
                if (isTyping != TYPING_STARTED) {
                    FuguLog.d(TAG, isTyping + "started typing");
                    // publish start typing event
                    if (channelId > -1 && !etMsg.getText().toString().isEmpty()) {
                        isTyping = TYPING_STARTED;
                        publishOnFaye(getString(R.string.fugu_empty), TEXT_MESSAGE,
                                getString(R.string.fugu_empty), getString(R.string.fugu_empty), null, NOTIFICATION_DEFAULT, null);
                    }
                }


                timer.cancel();
                timer = new Timer();
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                FuguLog.d(TAG, isTyping + "stopped typing");
                                stopTyping();
                            }
                        },
                        DELAY
                );
            }
        });

        llRoot.setOnKeyBoardStateChanged(new CustomLinear.OnKeyboardOpened() {
            @Override
            public boolean onKeyBoardStateChanged(boolean isVisible) {
                if (isVisible && fuguMessageAdapter != null && fuguMessageAdapter.getItemCount() > 0) {
                    rvMessages.scrollToPosition(fuguMessageAdapter.getItemCount() - 1);
                }
                return false;
            }
        });
    }

    private void stopTyping() {
        if (isTyping == TYPING_STARTED) {
            isTyping = TYPING_STOPPED;
            publishOnFaye(getString(R.string.fugu_empty), TEXT_MESSAGE,
                    getString(R.string.fugu_empty), getString(R.string.fugu_empty), null, NOTIFICATION_DEFAULT, null);
        }
    }

    private void setUpFayeConnection() {
        // Set FayeClient listener
        mClient.setListener(new FayeClientListener() {
            @Override
            public void onConnectedServer(FayeClient fc) {
                FuguLog.e(TAG, "Connected");

                if (channelId > -1) {
                    fc.subscribeChannel("/" + String.valueOf(channelId));
                    try {
                        final ArrayList<String> keys = new ArrayList<>(unsentMessageMap.keySet());
                        for (int i = 0; i < keys.size(); i++) {
                            Message messageObjs = ((EventItem) unsentMessageMap.get(keys.get(i))).getEvent();
                            Calendar c = Calendar.getInstance();
                            String date = DateUtils.getFormattedDate(c.getTime());
                            int currentTime = DateUtils.getTimeInMinutes(date);
                            int lastSentTime = DateUtils.getTimeInMinutes(messageObjs.getSentAtUtc()) + 330;
                            int difference = currentTime - lastSentTime;
                            if (difference >= 1) {
                                final int finalI = i;
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        for (int j = 0; j < fuguMessageList.size(); j++) {
                                            if (fuguMessageList.get(j) instanceof EventItem) {
                                                String uuid = ((EventItem) fuguMessageList.get(j)).getEvent().getUuid();
                                                String unsentUuid = keys.get(finalI);
                                                if (!TextUtils.isEmpty(uuid)) {
                                                    if (uuid.equals(unsentUuid)) {
                                                        Message messageObj = ((EventItem) fuguMessageList.get(j)).getEvent();
                                                        messageObj.setSent(false);
//                                                        messageObj.setMessageStatus(MESSAGE_UNSENT);
                                                        fuguMessageAdapter.notifyDataSetChanged();
                                                        FuguLog.e("match", uuid + "  " + unsentUuid);
                                                    } else {
                                                        FuguLog.e("not match", uuid + "  " + unsentUuid);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                });
                            } else {
                                final JSONObject messageJson = new JSONObject();
                                final Message messageObj = ((EventItem) unsentMessageMap.get(keys.get(i))).getEvent();
                                try {
                                    messageJson.put(USER_ID, String.valueOf(messageObj.getUserId()));

                                    messageJson.put(FULL_NAME, messageObj.getfromName());
                                    messageJson.put(MESSAGE, messageObj.getMessage());
                                    if (messageObj.getUrl().isEmpty()) {
                                        messageJson.put("message_type", messageObj.getMessageType());

                                    } else {
                                        messageJson.put("message_type", messageObj.getMessageType());
                                        if (fuguMessageList.get(i) instanceof EventItem) {
                                            Message messageObj2 = ((EventItem) fuguMessageList.get(i)).getEvent();
                                            messageObj2.setMessageStatus(MESSAGE_UNSENT);
                                            messageObj2.setSent(false);
                                        }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                fuguMessageAdapter.notifyDataSetChanged();
                                                updateRecycler();
                                            }
                                        });

//                                        uploadFileServerCall(messageObj.getUrl(), messageObj.getMessageIndex(), messageObj.getMessageType(), null);
                                    }
                                    messageJson.put(USER_TYPE, ANDROID_USER);
                                    messageJson.put(DATE_TIME, messageObj.getSentAtUtc());
                                    messageJson.put(MESSAGE_INDEX, messageObj.getMessageIndex());
                                    messageJson.put(IS_TYPING, TYPING_SHOW_MESSAGE);
                                    messageJson.put(MESSAGE_STATUS, messageObj.getMessageStatus());
                                    messageJson.put("UUID", messageObj.getUuid());
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if ((!messageObj.getUrl().isEmpty() && !messageObj.getMessage().isEmpty())
                                                    || (!messageObj.getMessage().isEmpty() && messageObj.getMessageType() == TEXT_MESSAGE)) {
                                                mClient.publish("/" + String.valueOf(channelId), messageJson);
                                            }
                                        }
                                    }, 500);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
//                            unsentMessageMap.remove(messageObjs.getUuid());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }
            }

            @Override
            public void onDisconnectedServer(FayeClient fc) {
                FuguLog.e(TAG, "Disconnected");
            }


            @Override
            public void onReceivedMessage(FayeClient fc, String msg, String channel) {
                FuguLog.e(TAG, "FuguMessage: " + msg);
                FuguLog.e(TAG, "channel" + channel);

                boolean isSelf = false;
                try {
                    final JSONObject messageJson = new JSONObject(msg);
                    if (!String.valueOf(messageJson.get("user_id")).equals(String.valueOf(userId)) && messageJson.has("on_subscribe")) {
                        onSubscribe = messageJson.getInt("on_subscribe");
                        FuguLog.e("onReceivedMessage onSubscribe", "==" + onSubscribe);
                    }

                    if (String.valueOf(messageJson.get("user_id")).equals(String.valueOf(userId))) {
                        FuguLog.v("onReceivedMessage", "isSelf");
                        isSelf = true;
                    }
                    if (messageJson.optInt(NOTIFICATION_TYPE, 0) == READ_MESSAGE) {
                        readFunctionality(messageJson);
                    } else {
                        if (messageJson.optInt(IS_TYPING, TYPING_STOPPED) == TYPING_STARTED) {
                            isFayeChannelActive = true;
                            readFunctionality(messageJson);
                        }
                        if (messageJson.getInt(IS_TYPING) == TYPING_SHOW_MESSAGE &&
                                (!messageJson.getString(MESSAGE).isEmpty() ||
                                        (messageJson.has("image_url") && !messageJson.getString("image_url").isEmpty()) ||
                                        (messageJson.has("url") && !messageJson.getString("url").isEmpty()) || messageJson.has(CUSTOM_ACTION))
                                && (messageJson.getInt(MESSAGE_TYPE) == TEXT_MESSAGE
                                || messageJson.getInt(MESSAGE_TYPE) == IMAGE_MESSAGE
                                || messageJson.getInt(MESSAGE_TYPE) == FILE_MESSAGE
                                || messageJson.getInt(MESSAGE_TYPE) == ACTION_MESSAGE)) {
                            FuguLog.v("onReceivedMessage", "in if 1");
                            if (isSelf && messageJson.has(MESSAGE_STATUS) && messageJson.has("UUID")
                                    && messageJson.getInt(MESSAGE_STATUS) == MESSAGE_UNSENT) {
                                FuguLog.v("onReceivedMessage", "in if 2");

                                if (fuguMessageList.get(messageJson.getInt(MESSAGE_INDEX)).getType() == ListItem.TYPE_HEADER
                                        && (messageJson.getInt(MESSAGE_INDEX) + 1 < fuguMessageList.size())) {
                                    FuguLog.v("onReceivedMessage", "in if 3");
                                    ((EventItem) fuguMessageList.get(messageJson.getInt(MESSAGE_INDEX) + 1))
                                            .getEvent()
                                            .setMessageStatus(MESSAGE_SENT);
                                } else if (messageJson.getInt(MESSAGE_INDEX) < fuguMessageList.size()) {
                                    FuguLog.v("onReceivedMessage", "in elseIf 1");

                                    ((EventItem) fuguMessageList.get(messageJson.getInt(MESSAGE_INDEX)))
                                            .getEvent()
                                            .setMessageStatus(MESSAGE_SENT);
                                }


                                unsentMessageMap.remove(messageJson.getString("UUID"));

                                if (unsentMessageMap.size() == 0 && isNetworkStateChanged) {
                                    pageStart = 1;
//                                    getMessages(null);
                                    isNetworkStateChanged = false;
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            fuguMessageAdapter.notifyItemChanged(messageJson.getInt(MESSAGE_INDEX));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            } else {
                                FuguLog.v("onReceivedMessage", "in else 1");
                                String date = DateUtils.getDate(dateUtils.convertToLocal(messageJson.getString(DATE_TIME)));
                                if (!sentAtUTC.equalsIgnoreCase(date)) {
                                    fuguMessageList.add(new HeaderItem(date));
                                    sentAtUTC = date;
                                    dateItemCount = dateItemCount + 1;
                                }

                                String url = "";
                                if (messageJson.getInt(MESSAGE_TYPE) == IMAGE_MESSAGE) {
                                    url = messageJson.optString("image_url", "");
                                } else if (messageJson.getInt(MESSAGE_TYPE) == FILE_MESSAGE) {
                                    url = messageJson.optString("url", "");
                                }
                                String sentUuid;
                                try {
                                    sentUuid = messageJson.getString("UUID");
                                } catch (Exception e) {
                                    sentUuid = UUID.randomUUID().toString();
                                }
                                Message message = new Message(0, messageJson.getString(FULL_NAME),
                                        Long.parseLong(messageJson.getString(USER_ID)),
                                        messageJson.getString(MESSAGE),
                                        messageJson.getString(DATE_TIME),
                                        isSelf,
                                        onSubscribe == 1 ? MESSAGE_READ : MESSAGE_SENT,
                                        fuguMessageList.size(),
                                        url,
                                        messageJson.has("thumbnail_url") ? messageJson.getString("thumbnail_url") : "",
                                        messageJson.getInt(MESSAGE_TYPE),
                                        onSubscribe == 1 ? true : true,
                                        sentUuid);
                                if (messageJson.has(CUSTOM_ACTION)) {
                                    message.setCustomAction(new Gson().fromJson(messageJson.getJSONObject(CUSTOM_ACTION).toString(), CustomAction.class));
                                }

                                fuguMessageList.add(new EventItem(message));

                                stopAnim();

                                if (!isSelf) {
                                    sendReadAcknowledgement();
                                }
                            }
                        }
                    }

                    if (!messageJson.getString(USER_ID).equals(String.valueOf(userId)) &&
                            onSubscribe == 1 && messageJson.has("on_subscribe")) {
                        FuguLog.v("onReceivedMessage", "in If 4");
                        for (int i = 0; i < fuguMessageList.size(); i++) {
                            if (fuguMessageList.get(i).getType() == ListItem.ITEM_TYPE_SELF && ((EventItem) fuguMessageList.get(i)).getEvent().isSent()) {
                                ((EventItem) fuguMessageList.get(i)).getEvent().setMessageStatus(MESSAGE_READ);
                            }
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                rvMessages.getRecycledViewPool().clear();
                                fuguMessageAdapter.notifyItemInserted(fuguMessageList.size() - 1);
                            }
                        });
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                if (messageJson.optInt(MESSAGE_TYPE) == TEXT_MESSAGE ||
                                        messageJson.optInt(MESSAGE_TYPE) == IMAGE_MESSAGE ||
                                        messageJson.optInt(MESSAGE_TYPE) == FILE_MESSAGE ||
                                        messageJson.optInt(MESSAGE_TYPE) == ACTION_MESSAGE) {
                                    FuguLog.v("onReceivedMessage", "in If 5");
                                    if ((messageJson.getInt(IS_TYPING) == 0) &&
                                            (!messageJson.getString(MESSAGE).isEmpty() ||
                                                    (messageJson.has("image_url") && !messageJson.getString("image_url").isEmpty()) ||
                                                    (messageJson.has("url") && !messageJson.getString("url").isEmpty()) ||
                                                    messageJson.has(CUSTOM_ACTION)) &&
                                            !String.valueOf(messageJson.get(USER_ID)).equals(String.valueOf(userId))) {
                                        FuguLog.v("onReceivedMessage", "in If 6");
                                        updateRecycler();

                                    } else if ((messageJson.getInt(IS_TYPING) == TYPING_STARTED) && !messageJson.getString(USER_ID).equals(String.valueOf(userId))) {
                                        FuguLog.v("onReceivedMessage", "in elseIf startAnim");
                                        startAnim();
                                    } else if ((messageJson.getInt(IS_TYPING) == TYPING_STOPPED) && !messageJson.getString(USER_ID).equals(String.valueOf(userId))) {
                                        FuguLog.v("onReceivedMessage", "in elseIf stopAnim");
                                        stopAnim();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            private void readFunctionality(JSONObject messageJson) {
                if (HippoConfig.getInstance().getUserData().getUserId().compareTo(messageJson.optLong(USER_ID)) > 0) {
                    for (int i = 0; i < fuguMessageList.size(); i++) {
                        if (fuguMessageList.get(i).getType() == ListItem.ITEM_TYPE_SELF) {
                            if (((EventItem) fuguMessageList.get(i)).getEvent().getMessageStatus() == MESSAGE_SENT) {
                                ((EventItem) fuguMessageList.get(i)).getEvent().setMessageStatus(MESSAGE_READ);
                            } else {
                                int status = ((EventItem) fuguMessageList.get(i)).getEvent().getMessageStatus();
                                ((EventItem) fuguMessageList.get(i)).getEvent().setMessageStatus(status);
                            }
                        }
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rvMessages.getRecycledViewPool().clear();
                        fuguMessageAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
        if (!mClient.isConnectedServer()) {
            mClient.connectServer();
        }
    }

    private void sendReadAcknowledgement() {
        if (channelId > -1) {
            publishOnFaye(getString(R.string.fugu_empty), 0,
                    getString(R.string.fugu_empty), getString(R.string.fugu_empty), null, NOTIFICATION_READ_ALL, null);
        }
    }


    private JSONObject prepareMessageJson(int onSubscribe) {
        JSONObject messageJson = new JSONObject();
        try {
            messageJson.put(USER_ID, String.valueOf(userId));
            messageJson.put(FULL_NAME, userName);
            messageJson.put(IS_TYPING, isTyping);
            messageJson.put(MESSAGE, "");
            messageJson.put(MESSAGE_TYPE, TEXT_MESSAGE);
            messageJson.put(USER_TYPE, ANDROID_USER);
            messageJson.put(ON_SUBSCRIBE, onSubscribe);
            messageJson.put(CHANNEL_ID, channelId);

            String localDate = DateUtils.getFormattedDate(
                    new Date());
            messageJson.put(DATE_TIME, DateUtils.getInstance().convertToUTC(localDate));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return messageJson;
    }

    private void addMessageToList(String message, int messageType, String imageUrl, String thumbnailUrl, FuguFileDetails fileDetails, String uuid) {
        try {
            String localDate = DateUtils.getFormattedDate(new Date());
            String date = DateUtils.getDate(localDate);
            if (!sentAtUTC.equalsIgnoreCase(date)) {
                fuguMessageList.add(new HeaderItem(date));
                sentAtUTC = date;
                dateItemCount = dateItemCount + 1;
            }
            FuguLog.d("userName in SDK", "addMessageToList " + userName);

            Message messageObj = new Message(0, userName,
                    userId,
                    message,
                    DateUtils.getInstance().convertToUTC(localDate),
                    true,
                    MESSAGE_UNSENT,
                    fuguMessageList.size(),
                    imageUrl.isEmpty() ? "" : imageUrl,
                    thumbnailUrl.isEmpty() ? "" : thumbnailUrl,
                    messageType,
                    isFayeChannelActive,
                    uuid);

            if (fileDetails != null) {
                messageObj.setFileName(fileDetails.getFileName());
                messageObj.setFileSize(fileDetails.getFileSize());
                messageObj.setFileExtension(fileDetails.getFileExtension());
                messageObj.setFilePath(fileDetails.getFilePath());
            }

            fuguMessageList.add(new EventItem(messageObj));

            etMsg.setText("");
            updateRecycler();

            if (messageType == IMAGE_MESSAGE || messageType == FILE_MESSAGE) {
                FuguLog.v("upload pic", "ready for upload");
                FuguLog.v("upload pic", "ready for upload" + imageUrl + " " + messageType);
                unsentMessageMap.put(uuid, fuguMessageList.get(fuguMessageList.size() - 1));
                CommonData.setUnsentMessageMapByChannel(channelId, unsentMessageMap);
                uploadFileServerCall(imageUrl, fuguMessageList.size() - 1, messageType, fileDetails);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void publishOnFaye(final String message, final int messageType, final String url, final String thumbnailUrl, final FuguFileDetails fileDetails, final int notificationType, String uuid) {
        try {
            if (!message.isEmpty() && messageType == TEXT_MESSAGE) {
                if (isFayeChannelActive) {
                    addMessageToList(message, messageType, url, thumbnailUrl, null, uuid);
                } else {
                    isFayeChannelActive = false;
                }
                if (!isFayeChannelActive) {
                    addMessageToList(message, messageType, url, thumbnailUrl, null, uuid);
                }
                if (!TextUtils.isEmpty(message)) {
                    publishMessage(message, messageType, url, thumbnailUrl, fileDetails, notificationType, uuid, 0);
                } else {
                    new CustomAlertDialog.Builder(FuguChatActivity.this)
                            .setMessage("Error in publishing message to faye")
                            .setPositiveButton("Ok", null)
                            .show();
                }
            } else {
                publishMessage(message, messageType, url, thumbnailUrl, fileDetails, notificationType, uuid, 0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void publishMessage(String message, int messageType, String url, String thumbnailUrl, FuguFileDetails fileDetails, int notificationType, String uuid, int position) throws JSONException {
        if (isNetworkAvailable()) {
            String localDate = DateUtils.getFormattedDate(new Date());
            FuguLog.d("userName in SDK", "publishOnFaye " + userName);
            //To be shifted
            JSONObject messageJson = new JSONObject();

            if (notificationType == NOTIFICATION_READ_ALL) {
                messageJson.put(NOTIFICATION_TYPE, notificationType);
                messageJson.put(CHANNEL_ID, channelId);
            } else {
                messageJson.put(FULL_NAME, userName);
                messageJson.put(MESSAGE, message);
                messageJson.put(MESSAGE_TYPE, messageType);
                messageJson.put(DATE_TIME, DateUtils.getInstance().convertToUTC(localDate));
                if (position == 0) {
                    messageJson.put(MESSAGE_INDEX, fuguMessageList.size() - 1);
                } else {
                    messageJson.put(MESSAGE_INDEX, position);
                }
                if (uuid != null) {
                    messageJson.put("UUID", uuid);
                }
                if (messageType == IMAGE_MESSAGE && !url.trim().isEmpty() && !thumbnailUrl.trim().isEmpty()) {
                    messageJson.put(IMAGE_URL, url);
                    messageJson.put(THUMBNAIL_URL, thumbnailUrl);
                }

                if (messageType == FILE_MESSAGE && !url.trim().isEmpty()) {
                    messageJson.put("url", url);
                    messageJson.put("file_name", fileDetails.getFileName());
                    messageJson.put("file_size", fileDetails.getFileSize());
                }

                if (messageType == TEXT_MESSAGE) {
                    messageJson.put(IS_TYPING, isTyping);
                } else {
                    messageJson.put(IS_TYPING, TYPING_SHOW_MESSAGE);
                }

                messageJson.put(MESSAGE_STATUS, MESSAGE_UNSENT);
            }

            messageJson.put(USER_ID, String.valueOf(userId));
            messageJson.put(USER_TYPE, ANDROID_USER);
            if (mClient.isConnectedServer()) {
                mClient.publish("/" + String.valueOf(channelId), messageJson);
                if (uuid != null) {
                    unsentMessageMap.remove(uuid);
                }
                CommonData.setUnsentMessageMapByChannel(channelId, unsentMessageMap);
                try {
                    if (messageType == IMAGE_MESSAGE) {
                        unsentMessageMap.remove(uuid);
                    }
                } catch (Exception e) {

                }
                if (isTyping == TYPING_SHOW_MESSAGE && messageType == TEXT_MESSAGE) {
                    isTyping = TYPING_STARTED;
                }
            } else {
                mClient.connectServer();
            }
            //end to be shifted
        } else if (!message.isEmpty() && messageType == TEXT_MESSAGE) {

        }
    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //noinspection ConstantConditions
            switch (intent.getAction()) {
                case NETWORK_STATE_INTENT:
                    FuguLog.d(TAG, "Network connectivity change " + intent.getBooleanExtra("isConnected", false));

                    if (intent.getBooleanExtra("isConnected", false)) {
                        if (!mClient.isConnectedServer())
                            mClient.connectServer();
                        if (unsentMessageMap.size() == 0) {
                            pageStart = 1;
                            if (!isFirst) {
                                getMessages(null);
                                rvMessages.scrollToPosition(fuguMessageList.size() - 1);
                                isFirst = false;
                            }
                        } else {
                            isNetworkStateChanged = true;
                        }
                    } else if (llTyping.getVisibility() == View.VISIBLE) {
                        stopAnim();
                    }
                    break;

                case NOTIFICATION_TAPPED:

                    conversation = new Gson().fromJson(intent.getStringExtra(FuguAppConstant.CONVERSATION), FuguConversation.class);

                    channelId = conversation.getChannelId();
                    userId = conversation.getUserId();
                    HippoNotificationConfig.pushChannelId = channelId;
                    currentChannelId = channelId;

                    pageStart = 1;
                    setUpUI();
                    if (mClient.isConnectedServer()) {
                        mClient.unsubscribeAll();
                        mClient.subscribeChannel("/" + String.valueOf(channelId));
                    } else {
                        mClient.connectServer();
                    }
                    break;
            }

        }
    };

    private void startAnim() {
        aviTyping.show();
        aviTyping.setVisibility(View.VISIBLE);
        llTyping.setVisibility(View.VISIBLE);

    }

    private void stopAnim() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                llTyping.setVisibility(View.GONE);
                aviTyping.setVisibility(View.GONE);
                aviTyping.hide();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.cancelAll();
        }
        HippoNotificationConfig.pushChannelId = channelId;
        currentChannelId = channelId;
        allMessagesFetched = false;

        setUpFayeConnection();
        if (CommonData.getPushBoolean() && CommonData.getPushChannel().compareTo(channelId) == 0) {
            allMessagesFetched = false;
            pageStart = 1;
            getMessages(null);
        }
        CommonData.setPushBoolean(false);
        CommonData.clearPushChannel();

        fuguMessageAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onPause() {
        super.onPause();
        HippoNotificationConfig.pushChannelId = -1L;
        CommonData.setPushChannel(channelId);
        if (unsentMessageMap.size() == 0) {
            CommonData.removeUnsentMessageMapChannel(channelId);
        } else {
            CommonData.setUnsentMessageMapByChannel(channelId, unsentMessageMap);
        }

        ArrayList<Message> tempList = new ArrayList<>();
        for (int i = 0; i < fuguMessageList.size(); i++) {
            if (fuguMessageList.get(i).getType() != ListItem.TYPE_HEADER)
                tempList.add(((EventItem) fuguMessageList.get(i)).getEvent());
        }

        if (conversation.isOpenChat()) {
            CommonData.setMessagesToLabelMap(channelId, tempList);
        } else {
            CommonData.setMessagesToMessageMap(channelId, tempList);
        }
        tempList.clear();

        // Fire stop typing event on Faye before close the chat
        stopTyping();
        stopAnim();
        stopFayeClient();
    }


    @Override
    protected void onDestroy() {
        FuguLog.e(TAG, "onDestroy");
        super.onDestroy();
        try {
            // Fire stop typing event on Faye before closing the chat
            stopTyping();
            stopFayeClient();

            ArrayList<Message> tempList = new ArrayList<>();
            for (int i = 0; i < fuguMessageList.size(); i++) {
                if (fuguMessageList.get(i) instanceof EventItem) {
                    tempList.add(((EventItem) fuguMessageList.get(i)).getEvent());
                }
            }


            if (conversation.isOpenChat()) {
                CommonData.setCachedMessages(conversation.isOpenChat(), labelId, tempList);
                CommonData.setMessagesToLabelMap(labelId, tempList);
            } else {
                CommonData.setCachedMessages(conversation.isOpenChat(), channelId, tempList);
                CommonData.setMessagesToMessageMap(channelId, tempList);
            }

            HippoNotificationConfig.pushChannelId = -1L;
            currentChannelId = -1L;

            LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiverChat);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopFayeClient() {
        try {
            HandlerThread thread = new HandlerThread("TerminateThread");
            thread.start();
            new Handler(thread.getLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (mClient.isConnectedServer()) {
                        mClient.disconnectServer();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.ivSend) {
            if (!etMsg.getText().toString().trim().isEmpty()) {
                if (channelId.compareTo(-1L) > 0) {
                    conversation.setChannelStatus(ChannelStatus.OPEN.getOrdinal());
                    isTyping = TYPING_SHOW_MESSAGE;
                    globalUuid = UUID.randomUUID().toString();
                    publishOnFaye(etMsg.getText().toString().trim(), TEXT_MESSAGE, getString(R.string.fugu_empty), getString(R.string.fugu_empty), null, NOTIFICATION_DEFAULT, globalUuid);
                    unsentMessageMap.put(globalUuid, fuguMessageList.get(fuguMessageList.size() - 1));

                } else {
                    if (mClient.isConnectedServer()) {
                        if (!isConversationCreated) {
                            conversation.setChannelStatus(ChannelStatus.OPEN.getOrdinal());
                            FuguLog.v("call createConversation", "onClick");
                            createConversation(TEXT_MESSAGE, "", "", null, isP2P);
                        }
                    } else {
                        mClient.connectServer();
                        Toast.makeText(FuguChatActivity.this, getString(R.string.fugu_unable_to_connect_internet), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else if (v.getId() == R.id.ivAttachment) {
            selectImage();
        } else if (v.getId() == R.id.tvNoInternet) {
            if (isP2P) {
                pbPeerChat.setVisibility(View.VISIBLE);
                tvNoInternet.setVisibility(View.GONE);
                FuguLog.v("call createConversation", "onClick no internet");
                createConversation(TEXT_MESSAGE, getString(R.string.fugu_empty), getString(R.string.fugu_empty), null, isP2P);
            } else if (conversation.isOpenChat() && conversation.getLabelId().compareTo(-1L) != 0) {
                FuguLog.e(TAG, "onClick: " + CommonData.getConversationList().get(0).getLabelId());
                getByLabelId();
            } else {
                getMessages(null);
            }
        }
    }

    private void createConversation(final int messageType, final String url, final String thumbnailUrl,
                                    final FuguFileDetails fileDetails, final boolean isP2P) {
        if (isNetworkAvailable()) {
            FuguLog.d("userName in SDK", "createConversation " + isP2P);
            FuguLog.d("Chat type in SDK", "chat type value " + fuguCreateConversationParams.toString());

            pbSendingImage.setVisibility(View.VISIBLE);
            ivSend.setVisibility(View.VISIBLE);

            RestClient.getApiInterface().createConversation(fuguCreateConversationParams)
                    .enqueue(new ResponseResolver<FuguCreateConversationResponse>(FuguChatActivity.this, false, false) {
                        @Override
                        public void success(FuguCreateConversationResponse fuguCreateConversationResponse) {
                            try {
                                globalUuid = UUID.randomUUID().toString();
                                isConversationCreated = true;
                                isFayeChannelActive = true;
                                channelId = fuguCreateConversationResponse.getData().getChannelId();
                                try {
                                    transactionIdsMap.put(fuguCreateConversationParams.getTransactionId(), fuguCreateConversationResponse.getData().getChannelId());
                                } catch (Exception e) {

                                }
                                if (transactionIdsMap != null)
                                    CommonData.setTransactionIdsMap(transactionIdsMap);
                                HippoNotificationConfig.pushChannelId = channelId;
                                currentChannelId = channelId;

                                FuguLog.v("channelId in createConversation is", "--> " + fuguCreateConversationResponse.getData().getChannelId());
                                mClient.subscribeChannel("/" + String.valueOf(channelId));
                                mClient.publish("/" + String.valueOf(channelId), prepareMessageJson(1));

                                isTyping = TYPING_SHOW_MESSAGE;

                                pbSendingImage.setVisibility(View.GONE);
                                ivSend.setVisibility(View.VISIBLE);

                                if (messageType == TEXT_MESSAGE) {
                                    publishOnFaye(etMsg.getText().toString().trim(), TEXT_MESSAGE,
                                            getString(R.string.fugu_empty), getString(R.string.fugu_empty), null, NOTIFICATION_DEFAULT, globalUuid);
                                } else if (messageType == IMAGE_MESSAGE) {
                                    publishOnFaye(getString(R.string.fugu_empty), messageType, url,
                                            thumbnailUrl, null, NOTIFICATION_DEFAULT, globalUuid);
                                } else if (messageType == FILE_MESSAGE) {
                                    publishOnFaye(getString(R.string.fugu_empty), messageType, url,
                                            thumbnailUrl, fileDetails, NOTIFICATION_DEFAULT, globalUuid);
                                }

                                FuguGetMessageResponse fuguGetMessageResponse = new FuguGetMessageResponse();

                                fuguGetMessageResponse.setStatusCode(fuguCreateConversationResponse.getStatusCode());
                                fuguGetMessageResponse.setMessage(fuguCreateConversationResponse.getMessage());

                                FuguGetMessageResponse.Data data = fuguGetMessageResponse.getData();


                                data.getMessages().add((new Message(businessName, -1L, defaultMessage, "", false,
                                        MESSAGE_SENT, 0, TEXT_MESSAGE, true, globalUuid)));

                                data.setLabel(label);
                                data.setFullName(userName);
                                data.setOnSubscribe(onSubscribe);
                                data.setPageSize(25);
                                data.setChannelId(channelId);
                                data.setStatus(STATUS_CHANNEL_OPEN);
                                data.setBusinessName(businessName);

                                fuguGetMessageResponse.setData(data);

                                FuguLog.v("set data is", "--> " + fuguGetMessageResponse.getData().getChannelId());

                                if (conversation.isOpenChat()) {
                                    FuguLog.v("createConversation isOpenChat", "--> " + conversation.isOpenChat());
                                    CommonData.setLabelIdResponse(channelId, fuguGetMessageResponse);
                                } else {
                                    FuguLog.v("createConversation isOpenChat not", "--> " + conversation.isOpenChat());
                                    CommonData.setMessageResponse(channelId, fuguGetMessageResponse);
                                }

                                if (isP2P) {
                                    setRecyclerViewData();
                                    llRoot.setVisibility(View.VISIBLE);
                                    getMessages(fuguCreateConversationResponse.getData().getlabel());
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void failure(APIError error) {
                            if (error.getStatusCode() == FuguAppConstant.SESSION_EXPIRE) {
                                Toast.makeText(FuguChatActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                if (isP2P) {
                                    tvNoInternet.setVisibility(View.VISIBLE);
                                    tvNoInternet.setText(error.getMessage());
                                } else {
                                    Toast.makeText(FuguChatActivity.this, getString(R.string.fugu_unable_to_connect_internet), Toast.LENGTH_SHORT).show();
                                }
                            }

                            pbSendingImage.setVisibility(View.GONE);
                            ivSend.setVisibility(View.VISIBLE);
                        }
                    });
        } else {

            if (isP2P) {
                tvNoInternet.setVisibility(View.VISIBLE);
                pbPeerChat.setVisibility(View.GONE);
            } else {
                Toast.makeText(FuguChatActivity.this, getString(R.string.fugu_unable_to_connect_internet), Toast.LENGTH_SHORT).show();
            }
            pbSendingImage.setVisibility(View.GONE);
            ivSend.setVisibility(View.VISIBLE);
        }
    }

    private void updateRecycler() {
        if (fuguMessageAdapter != null) {
            fuguMessageAdapter.notifyDataSetChanged();
            fuguMessageAdapter.updateList(fuguMessageList);
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (fuguMessageAdapter != null) {
                        try {
                            fuguMessageAdapter.notifyItemInserted(fuguMessageList.size() - 1);
                            if (fuguMessageAdapter.getItemCount() > 0) {
                                rvMessages.scrollToPosition(fuguMessageAdapter.getItemCount() - 1);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getMessages(final String channelName) {
        FuguLog.d("userName in SDK", "getMessages before call" + userName);
        if (isNetworkAvailable()) {
            if ((HippoConfig.getInstance().getUserData() == null)
                    || (TextUtils.isEmpty(HippoConfig.getInstance().getAppKey()))) {
                new ApiPutUserDetails(FuguChatActivity.this, new ApiPutUserDetails.Callback() {
                    @Override
                    public void onSuccess() {
                        apiGetMessages(channelName);
                    }

                    @Override
                    public void onFailure() {

                    }
                }).sendUserDetails(HippoConfig.getmResellerToken(), HippoConfig.getmReferenceId());
            } else {
                apiGetMessages(channelName);
            }
        } else {
            if (pageStart == 1 &&
                    (CommonData.getMessageResponse(channelId) == null ||
                            CommonData.getMessageResponse(channelId).getData().getMessages().size() == 0)) {
                llRoot.setVisibility(View.GONE);
                tvNoInternet.setVisibility(View.VISIBLE);
            }
            if (isP2P) {
                tvNoInternet.setVisibility(View.VISIBLE);
                pbPeerChat.setVisibility(View.GONE);
            }
            pbLoading.setVisibility(View.GONE);
        }
    }

    private void apiGetMessages(String channelName) {
        showLoading = false;
        if (!allMessagesFetched || isNetworkStateChanged) {
            FuguGetMessageParams commonParams = new FuguGetMessageParams(HippoConfig.getInstance().getAppKey(),
                    channelId,
                    enUserId,
                    pageStart,
                    channelName);

            RestClient.getApiInterface().getMessages(commonParams)
                    .enqueue(new ResponseResolver<FuguGetMessageResponse>(FuguChatActivity.this, showLoading, false) {
                        @Override
                        public void success(FuguGetMessageResponse fuguGetMessageResponse) {
                            mFuguGetMessageResponse = fuguGetMessageResponse;
                            LoadingBox.showOn(FuguChatActivity.this);
                            FuguLog.e("size of list--->", fuguGetMessageResponse.getData().getMessages().size() + "");
                            if (isP2P) {
                                pbPeerChat.setVisibility(View.GONE);
                                cvTypeMessage.setVisibility(View.VISIBLE);
                                llMessageLayout.setVisibility(View.VISIBLE);
                            }
                            label = fuguGetMessageResponse.getData().getLabel();
                            setToolbar(myToolbar, label);
                            if (fuguGetMessageResponse.getData().getMessages() != null) {
                                ArrayList<ListItem> tempMessageList = new ArrayList<>();
                                String tempSentAtUtc = "";

                                if (pageStart == 1) {
                                    dateItemCount = 0;
                                    fuguMessageList.clear();
                                }

                                FuguGetMessageResponse.Data messageResponseData = fuguGetMessageResponse.getData();
                                if (messageResponseData.getMessages().size() < messageResponseData.getPageSize()) {
                                    allMessagesFetched = true;
                                }
                                onSubscribe = messageResponseData.getOnSubscribe();
                                FuguLog.e("getMessages onSubscribe", "==" + onSubscribe);

                                for (int i = 0; i < messageResponseData.getMessages().size(); i++) {
                                    Message messageObj = messageResponseData.getMessages().get(i);
                                    boolean isSelf = false;
                                    if (messageObj.getUserId().compareTo(userId) == 0) {
                                        isSelf = true;
                                    }
                                    if (i != 0 || (messageResponseData.getMessages().get(0).getUserId().compareTo(HippoConfig.getInstance().getUserData().getUserId()) == 0)) {
                                        String date = DateUtils.getDate(dateUtils.convertToLocal(messageObj.getSentAtUtc()));
                                        if (!tempSentAtUtc.equalsIgnoreCase(date)) {
                                            tempMessageList.add(new HeaderItem(date));
                                            messageResponse.add(date);
                                            tempSentAtUtc = date;
                                            dateItemCount = dateItemCount + 1;
                                        }
                                    }

                                    Message message = new Message(messageObj.getId(), messageObj.getfromName(),
                                            messageObj.getUserId(),
                                            messageObj.getMessage(),
                                            messageObj.getSentAtUtc(),
                                            isSelf,
                                            messageObj.getMessageStatus(),
                                            i,
                                            messageObj.getUrl(),
                                            messageObj.getThumbnailUrl(),
                                            messageObj.getMessageType(),
                                            messageObj.isSent(),
                                            messageObj.getUuid());

                                    if (messageObj.getCustomAction() != null) {
                                        message.setCustomAction(messageObj.getCustomAction());
                                    }

                                    if (messageObj.getMessageType() == FILE_MESSAGE) {
                                        message.setFileExtension(messageObj.getFileExtension());
                                        message.setFileName(messageObj.getFileName());
                                        message.setFileSize(messageObj.getFileSize());
                                        message.setFilePath(messageObj.getFilePath());
                                    }
                                    EventItem eventItem = new EventItem(message);
                                    for (int j = 0; j < fuguMessageList.size(); j++) {
                                        if (fuguMessageList.get(j) instanceof EventItem
                                                && ((EventItem) fuguMessageList.get(j)).getEvent().getId().equals(message.getId())) {
                                            message.setTimeIndex(((EventItem) fuguMessageList.get(j)).getEvent().getTimeIndex());
                                            break;
                                        }
                                    }
                                    tempMessageList.add(eventItem);
                                }
                                if (pageStart == 1) {
                                    fuguMessageList.clear();
                                }

                                for (int i = 0; i < fuguMessageList.size(); i++) {
                                    if (fuguMessageList.get(i) instanceof HeaderItem) {
                                        if (tempSentAtUtc.equalsIgnoreCase(((HeaderItem) fuguMessageList.get(i)).getDate())) {
                                            fuguMessageList.remove(i);
                                            dateItemCount = dateItemCount - 1;
                                            break;
                                        }
                                    }
                                }

                                tempMessageList.addAll(fuguMessageList);
                                fuguMessageList.clear();
                                fuguMessageList.addAll(tempMessageList);
                                final ArrayList<String> keys = new ArrayList<>(unsentMessageMap.keySet());
                                for (int i = 0; i < keys.size(); i++) {
                                    Message messageObj = ((EventItem) unsentMessageMap.get(keys.get(i))).getEvent();
                                    messageObj.setUuid(keys.get(i));
                                    messageObj.setMessageStatus(MESSAGE_UNSENT);
                                    fuguMessageList.add(new EventItem(messageObj));
                                }
                                if (fuguMessageList.size() > 2 && fuguMessageList.get(0) instanceof EventItem) {
                                    Collections.swap(fuguMessageList, 0, 1);
                                }
                                tvNoInternet.setVisibility(View.GONE);
                                llRoot.setVisibility(View.VISIBLE);
                                if (conversation.getUnreadCount() > 0) {
                                    rvMessages.setAlpha(0);
                                }
                                if (pageStart == 1) {
                                    showLoading = false;
                                    sentAtUTC = tempSentAtUtc;
                                    CommonData.setMessageResponse(channelId, fuguGetMessageResponse);
                                    fuguMessageAdapter.updateList(fuguMessageList);
                                    fuguMessageAdapter.notifyDataSetChanged();
                                    rvMessages.scrollToPosition(fuguMessageList.size() - 1);
                                } else {
                                    pbLoading.setVisibility(View.GONE);
                                    fuguMessageAdapter.notifyItemRangeInserted(0, messageResponseData.getMessages().size());
//                                    fuguMessageAdapter.notifyDataSetChanged();
                                }
                                pageStart = fuguMessageList.size() + 1 - dateItemCount;
                            } else {
                                allMessagesFetched = true;
                            }
                            pbLoading.setVisibility(View.GONE);
                            isP2P = false;
                            LoadingBox.hide();
                        }

                        @Override
                        public void failure(APIError error) {
                            if (error.getStatusCode() == FuguAppConstant.SESSION_EXPIRE) {
                                Toast.makeText(FuguChatActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                if (pageStart == 1 &&
                                        (CommonData.getMessageResponse(channelId) == null ||
                                                CommonData.getMessageResponse(channelId).getData().getMessages().size() == 0)) {
                                    llRoot.setVisibility(View.GONE);
                                    tvNoInternet.setVisibility(View.VISIBLE);
                                }
                                pbLoading.setVisibility(View.GONE);

                                if (isP2P) {
                                    tvNoInternet.setVisibility(View.VISIBLE);
                                    pbPeerChat.setVisibility(View.GONE);
                                }
                            }
                        }
                    });
        }
    }

    private void getByLabelId() {
        if (isNetworkAvailable()) {
            if (!allMessagesFetched) {
                FuguGetByLabelIdParams commonParams = new FuguGetByLabelIdParams(HippoConfig.getInstance().getAppKey(),
                        labelId,
                        enUserId,
                        pageStart);

                RestClient.getApiInterface().getByLabelId(commonParams)
                        .enqueue(new ResponseResolver<FuguGetMessageResponse>(FuguChatActivity.this, showLoading, false) {
                            @Override
                            public void success(FuguGetMessageResponse fuguGetMessageResponse) {
                                if (pageStart == 1) {
                                    fuguMessageList.clear();
                                    dateItemCount = 0;
                                }
                                if (!TextUtils.isEmpty(fuguGetMessageResponse.getData().getLabel())) {
                                    label = fuguGetMessageResponse.getData().getLabel();
                                } else if (!TextUtils.isEmpty(conversation.getLabel())) {
                                    label = fuguGetMessageResponse.getData().getLabel();
                                }

                                setToolbar(myToolbar, label);

                                if (fuguGetMessageResponse.getData() != null && fuguGetMessageResponse.getData().getMessages() != null) {
                                    showLoading = false;
                                    ArrayList<ListItem> tempMessageList = new ArrayList<>();
                                    String tempSentAtUtc = sentAtUTC;

                                    FuguGetMessageResponse.Data data = fuguGetMessageResponse.getData();
                                    if (data.getMessages().size() < data.getPageSize()) {
                                        allMessagesFetched = true;
                                    }


                                    FuguLog.d("userName in SDK", "getByLabelId " + userName);
                                    onSubscribe = data.getOnSubscribe();
                                    channelId = data.getChannelId();
                                    HippoNotificationConfig.pushChannelId = data.getChannelId();
                                    currentChannelId = data.getChannelId();
                                    status = data.getStatus();
                                    businessName = data.getBusinessName();

                                    FuguLog.e("getByLabelId onSubscribe", "==" + onSubscribe);

                                    if (status == STATUS_CHANNEL_CLOSED) {
                                        cvTypeMessage.setVisibility(View.GONE);
                                        llMessageLayout.setVisibility(View.GONE);
                                        tvClosed.setVisibility(View.VISIBLE);
                                    }
                                    boolean isFirstToday = true;
                                    for (int i = 0; i < data.getMessages().size(); i++) {
                                        Message messageObj = data.getMessages().get(i);
                                        boolean isSelf = false;
                                        if (messageObj.getUserId().compareTo(userId) == 0)
                                            isSelf = true;
                                        if (i != 0 || (fuguGetMessageResponse.getData().getMessages().get(0).getUserId().compareTo(HippoConfig.getInstance().getUserData().getUserId()) != 0)) {
                                            String date = DateUtils.getDate(dateUtils.convertToLocal(messageObj.getSentAtUtc()));
                                            if (tempSentAtUtc.equalsIgnoreCase(date)) {
                                                if (isFirstToday) {
                                                    tempMessageList.add(new HeaderItem(date));
                                                    messageResponse.add(date);
                                                    tempSentAtUtc = date;
                                                    dateItemCount = dateItemCount + 1;
                                                    isFirstToday = false;
                                                }
                                            }
                                        }


                                        Message message = new Message(messageObj.getId(), messageObj.getfromName(),
                                                messageObj.getUserId(),
                                                messageObj.getMessage(),
                                                messageObj.getSentAtUtc(),
                                                isSelf,
                                                messageObj.getMessageStatus(),
                                                i,
                                                messageObj.getUrl(),
                                                messageObj.getThumbnailUrl(),
                                                messageObj.getMessageType(),
                                                messageObj.isSent(),
                                                messageObj.getUuid());

                                        if (messageObj.getCustomAction() != null) {
                                            message.setCustomAction(messageObj.getCustomAction());
                                        }

                                        if (messageObj.getMessageType() == FILE_MESSAGE) {
                                            message.setFileExtension(messageObj.getFileExtension());
                                            message.setFileName(messageObj.getFileName());
                                            message.setFileSize(messageObj.getFileSize());
                                            message.setFilePath(messageObj.getFilePath());
                                        }

                                        tempMessageList.add(new EventItem(message));

                                    }

                                    for (int i = 0; i < fuguMessageList.size(); i++) {
                                        if (fuguMessageList.get(i) instanceof HeaderItem) {
                                            if (tempSentAtUtc.equalsIgnoreCase(((HeaderItem) fuguMessageList.get(i)).getDate())) {
                                                fuguMessageList.remove(i);
                                                dateItemCount = dateItemCount - 1;
                                                break;
                                            }
                                        }
                                    }

                                    tempMessageList.addAll(fuguMessageList);
                                    fuguMessageList.clear();
                                    fuguMessageList.addAll(tempMessageList);
                                    sentAtUTC = tempSentAtUtc;
                                    final ArrayList<String> keys = new ArrayList<>(unsentMessageMap.keySet());
                                    FuguLog.e("keys", keys.size() + "");
                                    for (int i = 0; i < keys.size(); i++) {
                                        Message messageObj = ((EventItem) unsentMessageMap.get(keys.get(i))).getEvent();
                                        messageObj.setUuid(keys.get(i));
                                        fuguMessageList.add(new EventItem(messageObj));
                                    }
                                    tvNoInternet.setVisibility(View.GONE);
                                    llRoot.setVisibility(View.VISIBLE);

                                    if (pageStart == 1) {
                                        CommonData.setLabelIdResponse(labelId, fuguGetMessageResponse);
                                        sentAtUTC = tempSentAtUtc;
                                        setRecyclerViewData();
                                    } else {
                                        pbLoading.setVisibility(View.GONE);
                                        fuguMessageAdapter.notifyItemRangeInserted(0, data.getMessages().size());
                                    }

                                    pageStart = fuguMessageList.size() + 1 - dateItemCount;

                                } else {
                                    allMessagesFetched = true;
                                    fuguMessageList.clear();
                                }
                                pbLoading.setVisibility(View.GONE);
                            }

                            @Override
                            public void failure(APIError error) {
                                FuguLog.e("error type", error.getType() + "");
                                if (error.getStatusCode() == FuguAppConstant.SESSION_EXPIRE) {
                                    Toast.makeText(FuguChatActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    finish();
                                } else if ((error.getStatusCode() == DATA_UNAVAILABLE && error.getType() == 1)) {
                                    HippoConfig.getInstance().openChatByTransactionId("7865", HippoConfig.getInstance().getUserData().getUserUniqueKey(), "Fugu Default", null);
                                    finish();
                                } else {
                                    if (pageStart == 1 && (CommonData.getLabelIdResponse(labelId) == null
                                            || CommonData.getLabelIdResponse(labelId).getData().getMessages().size() == 0)) {
                                        llRoot.setVisibility(View.GONE);
                                        tvNoInternet.setVisibility(View.VISIBLE);
                                    }

                                    pbLoading.setVisibility(View.GONE);
                                }
                            }
                        });
            }
        } else {
            if (pageStart == 1 && (CommonData.getLabelIdResponse(labelId) == null
                    || CommonData.getLabelIdResponse(labelId).getData().getMessages().size() == 0)) {
                llRoot.setVisibility(View.GONE);
                tvNoInternet.setVisibility(View.VISIBLE);
            }
            pbLoading.setVisibility(View.GONE);
        }
    }

    private void setRecyclerViewData() {
        fuguMessageAdapter.notifyDataSetChanged();
        fuguMessageAdapter.setOnRetryListener(new FuguMessageAdapter.OnRetryListener() {
            @Override
            public void onRetry(String file, int messageIndex, int messageType, FuguFileDetails fileDetails, String uuid) {
                uploadFileServerCall(file, messageIndex, messageType, fileDetails);
            }
        });

        // Add the scroll listener
        rvMessages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (layoutManager.findFirstVisibleItemPosition() == 0 && fuguMessageList.size() >= 25
                        && !allMessagesFetched && pbLoading.getVisibility() == View.GONE) {
                    if (mClient.isConnectedServer()) {
                        pbLoading.setVisibility(View.VISIBLE);
                    }
                    if (unsentMessageMap.size() == 0) {
                        getMessages(null);
                    }
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            hideKeyboard(FuguChatActivity.this);
            onBackPressed(); // close this context and return to preview context (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mClient.isConnectedServer()) {
            isTyping = TYPING_STOPPED;
            publishOnFaye(getString(R.string.fugu_empty), TEXT_MESSAGE,
                    getString(R.string.fugu_empty), getString(R.string.fugu_empty), null, NOTIFICATION_DEFAULT, null);
            mClient.publish("/" + String.valueOf(channelId), prepareMessageJson(CHANNEL_UNSUBSCRIBED));
        }

        //FuguChannelsActivity.isRefresh = true;
        //FuguChannelsActivity.readChannelId = channelId;
        FuguChannelsActivityNew.isRefresh = true;
        FuguChannelsActivityNew.readChannelId = channelId;

        Intent intent = new Intent();
        if (fuguMessageList.size() > 0) {
            conversation.setChannelId(channelId);
            conversation.setLabelId(labelId);
            conversation.setDefaultMessage(((EventItem) fuguMessageList.get(fuguMessageList.size() - 1)).getEvent().getMessage());
            conversation.setDateTime(((EventItem) fuguMessageList.get(fuguMessageList.size() - 1)).getEvent().getSentAtUtc());
            conversation.setLast_sent_by_id(((EventItem) fuguMessageList.get(fuguMessageList.size() - 1)).getEvent().getUserId());
            conversation.setLast_message_status(((EventItem) fuguMessageList.get(fuguMessageList.size() - 1)).getEvent().getMessageStatus());
            conversation.setMessage_type(((EventItem) fuguMessageList.get(fuguMessageList.size() - 1)).getEvent().getMessageType());
            intent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));

            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED, intent);
        }

        super.onBackPressed();
    }


    /**
     * Method to select an image for Position in
     * the List of AddImages
     */
    public void selectImage() {
        fuguImageUtils.showImageChooser(OPEN_CAMERA_ADD_IMAGE, OPEN_GALLERY_ADD_IMAGE, SELECT_FILE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        FuguLog.e(TAG, "onRequestPermissionsResult" + requestCode);

        try {
            if (HippoConfig.getInstance().getTargetSDKVersion() > 22 &&
                    grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                switch (requestCode) {

                    case PERMISSION_CONSTANT_CAMERA:
                        fuguImageUtils.startCamera();
                        break;

                    case PERMISSION_READ_IMAGE_FILE:
                        compressAndSaveImageBitmap();
                        break;

                    case PERMISSION_CONSTANT_GALLERY:
                        fuguImageUtils.openGallery();
                        break;

                    case PERMISSION_SAVE_BITMAP:
                        compressAndSaveImageBitmap();
                        break;
                    case PERMISSION_READ_FILE:
                        break;
                }
            } else if (HippoConfig.getInstance().getTargetSDKVersion() <= 22 &&
                    grantResults.length > 0
                    && HippoConfig.getInstance().isPermissionGranted(FuguChatActivity.this, Manifest.permission.CAMERA)
                    && HippoConfig.getInstance().isPermissionGranted(FuguChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                switch (requestCode) {

                    case PERMISSION_CONSTANT_CAMERA:
                        fuguImageUtils.startCamera();
                        break;

                    case PERMISSION_READ_IMAGE_FILE:
                        compressAndSaveImageBitmap();
                        break;

                    case PERMISSION_CONSTANT_GALLERY:
                        fuguImageUtils.openGallery();
                        break;

                    case PERMISSION_SAVE_BITMAP:
                        compressAndSaveImageBitmap();
                        break;
                    case PERMISSION_READ_FILE:

                        break;
                }
            } else {
                Toast.makeText(FuguChatActivity.this, getString(R.string.fugu_permission_was_not_granted_text), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case OPEN_CAMERA_ADD_IMAGE:
                    compressAndSaveImageBitmap();
                    break;

                case OPEN_GALLERY_ADD_IMAGE:
                    try {
                        fuguImageUtils.copyFileFromUri(data.getData());
                        compressAndSaveImageBitmap();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Problem in fetching image...", Toast.LENGTH_LONG).show();
                    }
                    break;

                case SELECT_FILE:
                    FuguFileDetails fileDetails = fuguImageUtils.saveFile(data.getData(), FileType.PDF_FILE);
                    if (fileDetails.getFilePath().isEmpty()) {
                        Toast.makeText(this, "File not found...", Toast.LENGTH_LONG).show();
                    } else {
                        FuguLog.e(TAG, "file path" + fileDetails.getFilePath());
                        globalUuid = UUID.randomUUID().toString();
                        addMessageToList(getString(R.string.fugu_empty), FILE_MESSAGE, fileDetails.getFilePath(), "", fileDetails, globalUuid);
                    }

                    break;
            }
    }

    private void compressAndSaveImageBitmap() {
        try {
            String image = fuguImageUtils.compressAndSaveBitmap(FuguChatActivity.this);//(null, squareEdge);
            if (image == null) {
                Toast.makeText(FuguChatActivity.this, "Could not read from source", Toast.LENGTH_LONG).show();
            } else {
                globalUuid = UUID.randomUUID().toString();
                addMessageToList(getString(R.string.fugu_empty), IMAGE_MESSAGE, image, image, null, globalUuid);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(FuguChatActivity.this, "Could not read from source", Toast.LENGTH_LONG).show();
        }
    }


    private void uploadFileServerCall(String file, final int messageIndex, final int messageType, final FuguFileDetails fileDetails) {
        try {
            if (isNetworkAvailable()) {
                if (!mClient.isConnectedServer()) {
                    mClient.connectServer();
                }
                MultipartParams.Builder multipartBuilder = new MultipartParams.Builder();
                MultipartParams multipartParams = multipartBuilder
                        .add(APP_SECRET_KEY, HippoConfig.getInstance().getAppKey())
                        .add(APP_VERSION, BuildConfig.VERSION_NAME)
                        .add(DEVICE_TYPE, 1)
                        .addFile("file", new File(file)).build();

                FuguLog.v("map = ", multipartParams.getMap().toString());
                FuguLog.v("app_secret_key", "---> " + HippoConfig.getInstance().getAppKey());
                RestClient.getApiInterface()
                        .uploadFile(multipartParams.getMap())
                        .enqueue(new ResponseResolver<FuguUploadImageResponse>(FuguChatActivity.this, false, false) {
                            @Override
                            public void success(FuguUploadImageResponse fuguUploadImageResponse) {
                                FuguLog.v("success on uploading", "successssss");

                                if (channelId.compareTo(-1L) > 0) {
//                                    publishOnFaye(getString(R.string.fugu_empty), messageType, fuguUploadImageResponse.getData().getUrl(),
//                                            fuguUploadImageResponse.getData().getThumbnailUrl(), fileDetails, NOTIFICATION_DEFAULT, globalUuid);
                                    if (mClient.isConnectedServer()) {
                                        Log.e("call", "call publish");
                                        mClient.publish("", messageType, fuguUploadImageResponse.getData().getUrl(), fuguUploadImageResponse.getData().getThumbnailUrl(), fileDetails,
                                                NOTIFICATION_DEFAULT, globalUuid, fuguMessageList.size() - 1, "/" + String.valueOf(channelId),
                                                userId, userName, channelId, isTyping);

// addMessageToList("", messageType, fuguUploadImageResponse.getData().getUrl(), fuguUploadImageResponse.getData().getThumbnailUrl(), null, globalUuid);
                                        try {
                                            if (messageType == IMAGE_MESSAGE) {
                                                unsentMessageMap.remove(globalUuid);
                                            }
                                        } catch (Exception e) {

                                        }
                                        if (isTyping == TYPING_SHOW_MESSAGE && messageType == TEXT_MESSAGE) {
                                            isTyping = TYPING_STARTED;
                                        }
                                    } else {
                                        mClient.connectServer();
                                    }
                                } else if (mClient.isConnectedServer()) {
                                    if (!isConversationCreated) {
                                        FuguLog.v("call createConversation", "after upload file to server");
                                        createConversation(messageType, fuguUploadImageResponse.getData().getUrl(),
                                                fuguUploadImageResponse.getData().getThumbnailUrl(), fileDetails, isP2P);
                                    }
                                }
                            }

                            @Override
                            public void failure(APIError error) {
                                if (messageType == IMAGE_MESSAGE) {
                                    ((EventItem) fuguMessageList.get(messageIndex)).getEvent().setMessageStatus(MESSAGE_IMAGE_RETRY);
                                } else if (messageType == FILE_MESSAGE) {
                                    ((EventItem) fuguMessageList.get(messageIndex)).getEvent().setMessageStatus(MESSAGE_FILE_RETRY);
                                }

                                fuguMessageAdapter.notifyItemChanged(messageIndex);
                            }
                        });
            } else {
                if (messageType == IMAGE_MESSAGE) {
                    ((EventItem) fuguMessageList.get(messageIndex)).getEvent().setMessageStatus(MESSAGE_IMAGE_RETRY);
                } else if (messageType == FILE_MESSAGE) {
                    ((EventItem) fuguMessageList.get(messageIndex)).getEvent().setMessageStatus(MESSAGE_FILE_RETRY);
                }
                fuguMessageAdapter.notifyItemChanged(messageIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        for (int i = 0; i < fuguMessageList.size(); i++) {
//            if (fuguMessageList.get(i) instanceof EventItem
//                    && ((EventItem) fuguMessageList.get(i)).getEvent().getUuid() != null
//                    && ((EventItem) fuguMessageList.get(i)).getEvent().getUuid().equals(uuid)) {
//                fuguMessageList.remove(i);
//            }
//        }
//        unsentMessageMap.remove(uuid);
//        fuguMessageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAnimationStart(Animation animation) {
        if (!(tvDateLabel.getVisibility() == View.VISIBLE)) {
            tvDateLabel.clearAnimation();
        }

    }

    @Override
    public void onAnimationEnd(Animation animation) {
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public void sendMessage(int position) {
        Message message = ((EventItem) fuguMessageList.get(position)).getEvent();

        if (!message.isSent()) {
            try {
                isTyping = TYPING_SHOW_MESSAGE;
                publishMessage(message.getMessage(),
                        TEXT_MESSAGE, "",
                        "",
                        null,
                        NOTIFICATION_DEFAULT,
                        message.getUuid(), position);
                message.setSent(true);
                fuguMessageAdapter.notifyItemChanged(position);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void cancelMessage(int position) {
        Message message = ((EventItem) fuguMessageList.get(position)).getEvent();
        try {
            unsentMessageMap.remove(message.getUuid());
        } catch (Exception e) {

        }
        fuguMessageList.remove(position);
        fuguMessageAdapter.notifyDataSetChanged();
    }

    /**
     * Called when a custom action button is clicked
     *
     * @param buttonAction the action button object associated with this button
     */
    public void onCustomActionClicked(final Object buttonAction) {
        //send a broadcast to listening parent app
        String payload = new Gson().toJson(buttonAction);
        Intent intent = new Intent();
        intent.putExtra(FUGU_CUSTOM_ACTION_PAYLOAD, payload);
        intent.setAction(FUGU_CUSTOM_ACTION_SELECTED);
        sendBroadcast(intent);

    }
}
package com.fugu.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fugu.HippoNotificationConfig;
import com.fugu.R;
import com.fugu.activity.FuguChannelsActivityNew;
import com.fugu.activity.FuguChatActivity;
import com.fugu.adapter.FuguChannelsAdapter;
import com.fugu.constant.FuguAppConstant;
import com.fugu.database.CommonData;
import com.fugu.model.FuguConversation;
import com.fugu.model.FuguPutUserDetailsResponse;
import com.fugu.utils.FuguLog;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by cl-macmini-01 on 1/31/18.
 */

public class FuguChannelsFragment extends Fragment implements Animation.AnimationListener, FuguAppConstant {

    private final String TAG = FuguChannelsFragment.class.getSimpleName();
    private final int IS_HIT_REQUIRED = 200;
    private FuguChannelsAdapter fuguChannelsAdapter;
    private ArrayList<FuguConversation> fuguConversationList;
    private Long userId = -1L;
    private String enUserId = "";
    private String userName = "Anonymous";
    private String businessName = "Anonymous";
    private FuguChannelsActivityNew mActivity;
    private RecyclerView rvChannels;
    private LinearLayout llNoConversations;

    /**
     * Creates instance of FuguChanneFragment
     *
     * @param fuguConversationList the fugu conversation list
     * @return fugu channel fragment
     */
    public static FuguChannelsFragment newInstance(ArrayList<FuguConversation> fuguConversationList) {

        Bundle args = new Bundle();
        args.putParcelableArrayList(FuguAppConstant.CONVERSATION, fuguConversationList);
        FuguChannelsFragment fragment = new FuguChannelsFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {

        View main = inflater.inflate(R.layout.fugu_fragment_channels, container, false);
        initViews(main);
        fuguConversationList = new ArrayList<>();
        fuguConversationList = getArguments().getParcelableArrayList(FuguAppConstant.CONVERSATION);
        setRecyclerViewData();
        return main;
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        mActivity = (FuguChannelsActivityNew) context;
    }


    /**
     * Initialize Views
     */
    private void initViews(final View view) {
        rvChannels = (RecyclerView) view.findViewById(R.id.rvChannels);
        llNoConversations = (LinearLayout) view.findViewById(R.id.llNoConversations);
        llNoConversations.setVisibility(View.GONE);
        TextView tvNoConversation = llNoConversations.findViewById(R.id.tvNoConversation);
        tvNoConversation.setTypeface(CommonData.getFontConfig().getNormalTextTypeFace(mActivity.getApplicationContext()));
        tvNoConversation.setTextColor(CommonData.getColorConfig().getHippoTextColorPrimary());
        FuguPutUserDetailsResponse.Data userData = CommonData.getUserDetails().getData();
        businessName = userData.getBusinessName();
        userId = userData.getUserId();
        enUserId = userData.getEn_user_id();
        if (userData.getFullName() != null && !userData.getFullName().isEmpty())
            userName = userData.getFullName();
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


    /**
     * Reset unread count based on channel
     *
     * @param channelId the channed id
     */
    public void resetUnreadCount(long channelId) {
        try {
            for (int i = 0; i < fuguConversationList.size(); i++) {
                FuguConversation currentConversation = fuguConversationList.get(i);
                if (currentConversation.getChannelId().compareTo(channelId) == 0) {
                    currentConversation.setUnreadCount(0);

                    if (fuguChannelsAdapter != null)
                        fuguChannelsAdapter.notifyDataSetChanged();

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * Handle message push for channel
     *
     * @param messageJson the message json
     */
    public void handleMessagePush(JSONObject messageJson) {

        try {

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

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IS_HIT_REQUIRED && resultCode == Activity.RESULT_OK) {
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
                // let the parent activity handle the complete refresh
                mActivity.onRefresh();
                CommonData.setIsNewchat(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Set conversations list
     *
     * @param conversationList conversations list
     */
    public void setConversationList(final ArrayList<FuguConversation> conversationList) {
        fuguConversationList = new ArrayList<>();
        for (FuguConversation conversation : conversationList) {
            try {
                fuguConversationList.add((FuguConversation) conversation.clone());
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
        if (fuguChannelsAdapter != null) {
            fuguChannelsAdapter.updateList(fuguConversationList);
        } else {
            setRecyclerViewData();
        }
        checkForEmptyView();
    }

    /**
     * Decides whether to show empty view or not
     */
    private void checkForEmptyView() {
        if (fuguConversationList.size() > 0) {
            rvChannels.setVisibility(View.VISIBLE);
            llNoConversations.setVisibility(View.GONE);
        } else {
            rvChannels.setVisibility(View.GONE);
            llNoConversations.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Set Recycler Data
     */
    private void setRecyclerViewData() {
        fuguChannelsAdapter = new FuguChannelsAdapter(mActivity, fuguConversationList, userName, userId, businessName
                , new FuguChannelsAdapter.Callback() {
            @Override
            public void onClick(FuguConversation conversation) {
                Intent chatIntent = new Intent(mActivity, FuguChatActivity.class);
                chatIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
                // start activity for result from activity's context to get result there
                mActivity.startActivityForResult(chatIntent, IS_HIT_REQUIRED);
            }
        }, enUserId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        rvChannels.setLayoutManager(layoutManager);
        rvChannels.setAdapter(fuguChannelsAdapter);
        checkForEmptyView();
    }

}

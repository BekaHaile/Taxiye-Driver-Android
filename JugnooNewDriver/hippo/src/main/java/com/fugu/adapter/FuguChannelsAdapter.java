package com.fugu.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.fugu.FuguFontConfig;
import com.fugu.HippoColorConfig;
import com.fugu.HippoConfig;
import com.fugu.R;
import com.fugu.database.CommonData;
import com.fugu.datastructure.ChannelStatus;
import com.fugu.model.FuguConversation;
import com.fugu.utils.DateUtils;
import com.fugu.utils.FuguLog;
import com.fugu.utils.RoundedCornersTransformation;

import java.util.ArrayList;

import static com.fugu.constant.FuguAppConstant.IMAGE_MESSAGE;
import static com.fugu.constant.FuguAppConstant.MESSAGE_IMAGE_RETRY;
import static com.fugu.constant.FuguAppConstant.MESSAGE_READ;
import static com.fugu.constant.FuguAppConstant.MESSAGE_UNSENT;

/**
 * Created by Bhavya Rattan on 08/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class FuguChannelsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private ArrayList<FuguConversation> fuguConversationList = new ArrayList<>();
    private Activity activity;
    private String userName;
    private String businessName;
    private Long userId = -1L;
    private String enUserId = "";
    private HippoColorConfig hippoColorConfig;
    private Callback callback;

    public FuguChannelsAdapter(Activity activity, ArrayList<FuguConversation> fuguConversationList, String userName, Long userId, String businessName, Callback callback, String enUserId) {
        inflater = LayoutInflater.from(activity.getApplicationContext());

        hippoColorConfig = CommonData.getColorConfig();
        this.fuguConversationList = fuguConversationList;
        this.activity = activity;
        this.userName = userName;
        this.businessName = businessName;
        this.userId = userId;
        this.callback = callback;
        this.enUserId = enUserId;
    }

    public void updateList(ArrayList<FuguConversation> fuguConversationList) {
        this.fuguConversationList = fuguConversationList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.fugu_item_channels, parent, false);
        return new ChannelViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        try {
            final ChannelViewHolder channelViewHolder = (ChannelViewHolder) holder;
            final FuguConversation currentChannelItem = fuguConversationList.get(position);

            channelViewHolder.tvChannelName.setText(currentChannelItem.getLabel());
            channelViewHolder.tvChannelName.setTextColor(hippoColorConfig.getHippoTextColorPrimary());
            channelViewHolder.tvMessage.setTextColor(hippoColorConfig.getHippoTextColorPrimary());
            channelViewHolder.viewDivider.setBackgroundColor(hippoColorConfig.getHippoBorderColor());
            if (TextUtils.isEmpty(currentChannelItem.getMessage())) {
                if (!TextUtils.isEmpty(currentChannelItem.getLast_sent_by_full_name())) {
                    if (currentChannelItem.getUserId().equals(HippoConfig.getInstance().getUserData().getUserId())) {
                        if (HippoConfig.getInstance().getUserData().getUserId().compareTo(currentChannelItem.getLast_sent_by_id()) == 0) {
                            if (currentChannelItem.getMessage_type() == IMAGE_MESSAGE) {
                                channelViewHolder.tvMessage.setText("You: " + activity.getString(R.string.fugu_attachment));
                            } else {
                                channelViewHolder.tvMessage.setText("You sent a message");
                            }
                            channelViewHolder.ivMessageState.setVisibility(View.VISIBLE);
                            if (currentChannelItem.getLast_message_status() == MESSAGE_READ) {
                                channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_double));
                            } else if (currentChannelItem.getLast_message_status() == MESSAGE_UNSENT || currentChannelItem.getLast_message_status() == MESSAGE_IMAGE_RETRY) {
                                channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_ic_waiting));
                            } else {
                                channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_single));
                            }
                        } else {
                            if (currentChannelItem.getMessage_type() == IMAGE_MESSAGE) {
                                channelViewHolder.tvMessage.setText(currentChannelItem.getLast_sent_by_full_name().trim() + ": " + activity.getString(R.string.fugu_attachment));
                            } else {
                                channelViewHolder.tvMessage.setText(currentChannelItem.getLast_sent_by_full_name().trim() + " " + "sent a message");
                            }
                            channelViewHolder.ivMessageState.setVisibility(View.GONE);
                        }
                    } else {
                        channelViewHolder.tvMessage.setText(currentChannelItem.getMessage());
                        channelViewHolder.ivMessageState.setVisibility(View.GONE);
                    }
                }
            } else {
                if (currentChannelItem.getMessage().contains("\n")) {
                    channelViewHolder.tvMessage.setText(currentChannelItem.getMessage().replaceAll("\n", " "));
                    if (!TextUtils.isEmpty(currentChannelItem.getLast_sent_by_full_name())) {
                        FuguLog.e("error", HippoConfig.getInstance().getUserData().getUserId() + "");
                        FuguLog.e("error", currentChannelItem.getUserId() + "");
                        if (HippoConfig.getInstance().getUserData().getUserId().compareTo(currentChannelItem.getLast_sent_by_id()) == 0) {
                            channelViewHolder.tvMessage.setText("You: " + currentChannelItem.getMessage());
                            channelViewHolder.ivMessageState.setVisibility(View.VISIBLE);
                            if (currentChannelItem.getLast_message_status() == MESSAGE_READ) {
                                channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_double));
                            } else if (currentChannelItem.getLast_message_status() == MESSAGE_UNSENT || currentChannelItem.getLast_message_status() == MESSAGE_IMAGE_RETRY) {
                                channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_ic_waiting));
                            } else {
                                channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_single));
                            }
                        } else {
                            channelViewHolder.tvMessage.setText(currentChannelItem.getLast_sent_by_full_name().trim() + ": " + currentChannelItem.getMessage());
                            channelViewHolder.ivMessageState.setVisibility(View.GONE);
                        }
                    } else {
                        if (currentChannelItem.getUserId().equals(HippoConfig.getInstance().getUserData().getUserId())) {
                            channelViewHolder.tvMessage.setText("You: " + currentChannelItem.getMessage());
                            channelViewHolder.ivMessageState.setVisibility(View.VISIBLE);
                            if (currentChannelItem.getLast_message_status() == MESSAGE_READ) {
                                channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_double));
                            } else if (currentChannelItem.getLast_message_status() == MESSAGE_UNSENT) {
                                channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_ic_waiting));
                            } else {
                                channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_single));
                            }
                        } else {
                            channelViewHolder.tvMessage.setText(currentChannelItem.getMessage());
                            channelViewHolder.ivMessageState.setVisibility(View.GONE);
                        }
                    }
                } else {
                    if (!TextUtils.isEmpty(currentChannelItem.getLast_sent_by_full_name())) {
                        FuguLog.e("error", HippoConfig.getInstance().getUserData().getUserId() + "");
                        FuguLog.e("error", currentChannelItem.getUserId() + "");
                        if (HippoConfig.getInstance().getUserData().getUserId().compareTo(currentChannelItem.getLast_sent_by_id()) == 0) {
                            channelViewHolder.tvMessage.setText("You: " + currentChannelItem.getMessage());
                            channelViewHolder.ivMessageState.setVisibility(View.VISIBLE);
                            if (currentChannelItem.getLast_message_status() == MESSAGE_READ) {
                                channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_double));
                            } else if (currentChannelItem.getLast_message_status() == MESSAGE_UNSENT || currentChannelItem.getLast_message_status() == MESSAGE_IMAGE_RETRY) {
                                channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_ic_waiting));
                            } else {
                                channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_single));
                            }
                        } else {
                            channelViewHolder.tvMessage.setText(currentChannelItem.getLast_sent_by_full_name().trim() + ": " + currentChannelItem.getMessage());
                            channelViewHolder.ivMessageState.setVisibility(View.GONE);
                        }
                    } else {
                        if (currentChannelItem.getUserId().equals(HippoConfig.getInstance().getUserData().getUserId())) {
                            channelViewHolder.tvMessage.setText("You: " + currentChannelItem.getMessage());
                            channelViewHolder.ivMessageState.setVisibility(View.VISIBLE);
                            if (currentChannelItem.getLast_message_status() == MESSAGE_READ) {
                                channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_double));
                            } else if (currentChannelItem.getLast_message_status() == MESSAGE_UNSENT) {
                                channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_ic_waiting));
                            } else {
                                channelViewHolder.ivMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_single));
                            }
                        } else {
                            channelViewHolder.tvMessage.setText(currentChannelItem.getMessage());
                            channelViewHolder.ivMessageState.setVisibility(View.GONE);
                        }
                    }
                }
            }
//            }

            if (currentChannelItem.getUnreadCount() > 0) {
                channelViewHolder.tvChannelName.setTypeface(channelViewHolder.tvChannelName.getTypeface(),Typeface.BOLD);
                channelViewHolder.tvMessage.setTypeface(channelViewHolder.tvMessage.getTypeface(),Typeface.BOLD);
                channelViewHolder.circularTvMessageCount.setVisibility(View.VISIBLE);
                channelViewHolder.rlRoot.setSelected(true);
                //            channelViewHolder.circularTvMessageCount.setSolidColor(FuguConfig.getInstance().getThemeColor());
                //            channelViewHolder.circularTvMessageCount.setStrokeWidth(0);
                channelViewHolder.circularTvMessageCount.setText(String.valueOf(currentChannelItem.getUnreadCount()));

                channelViewHolder.tvDate.setTextColor(hippoColorConfig.getHippoTextColorPrimary());

            } else {
                channelViewHolder.tvChannelName.setTypeface(channelViewHolder.tvChannelName.getTypeface(),Typeface.NORMAL);
                channelViewHolder.tvMessage.setTypeface(channelViewHolder.tvMessage.getTypeface(),Typeface.NORMAL);
                channelViewHolder.circularTvMessageCount.setVisibility(View.GONE);
                channelViewHolder.rlRoot.setSelected(false);
                channelViewHolder.tvDate.setTextColor(hippoColorConfig.getHippoChannelDateText());
            }

            if (currentChannelItem.getChannelImage() == null || currentChannelItem.getChannelImage().trim().isEmpty()) {
                channelViewHolder.tvChannelIcon.setText(currentChannelItem.getLabel().substring(0, 1).toUpperCase());
                channelViewHolder.tvChannelIcon.setVisibility(View.VISIBLE);

                Glide.clear(channelViewHolder.ivChannelIcon);

                channelViewHolder.ivChannelIcon.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ring_grey));

                channelViewHolder.ivChannelIcon.getDrawable()
                        .setColorFilter(hippoColorConfig.getHippoChannelDateText(), PorterDuff.Mode.SRC_ATOP);
                channelViewHolder.tvChannelIcon.setTextColor(hippoColorConfig.getHippoChannelItemBg());

            } else {
                /*RequestOptions myOptions = RequestOptions
                        .bitmapTransform(new RoundedCornersTransformation(activity, 7, 2))
                        .placeholder(ContextCompat.getDrawable(activity, R.drawable.fugu_ic_channel_icon))
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .error(ContextCompat.getDrawable(activity, R.drawable.fugu_ic_channel_icon));
                Glide.with(activity).load(currentChannelItem.getChannelImage())
                        .apply(myOptions)
                        .into(channelViewHolder.ivChannelIcon);*/

                Glide.with(activity).load(currentChannelItem.getChannelImage()).asBitmap()
                        .centerCrop()
                        .placeholder(ContextCompat.getDrawable(activity, R.drawable.fugu_ic_channel_icon))
                        .error(ContextCompat.getDrawable(activity, R.drawable.fugu_ic_channel_icon))
                        .into(new BitmapImageViewTarget(channelViewHolder.ivChannelIcon) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable =
                                        RoundedBitmapDrawableFactory.create(activity.getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                channelViewHolder.ivChannelIcon.setImageDrawable(circularBitmapDrawable);
                            }
                        });

                channelViewHolder.tvChannelIcon.setVisibility(View.GONE);
            }

            if (currentChannelItem.getChannelId().compareTo(-1L) == 0) {
                channelViewHolder.tvDate.setVisibility(View.GONE);
            } else {
                channelViewHolder.tvDate.setText(DateUtils.getRelativeDate(DateUtils.getInstance().convertToLocal(currentChannelItem.getDateTime()), true));
                channelViewHolder.tvDate.setVisibility(View.VISIBLE);
            }

            if (currentChannelItem.getChannelStatus() == ChannelStatus.CLOSED.getOrdinal()
                    && currentChannelItem.getLabelId() < 0) {
                channelViewHolder.vClosed.setVisibility(View.VISIBLE);
            } else {
                channelViewHolder.vClosed.setVisibility(View.GONE);
            }

            channelViewHolder.rlRoot.setBackgroundDrawable(HippoColorConfig
                    .makeSelector(hippoColorConfig.getHippoChannelItemBg(), hippoColorConfig.getHippoChannelItemBgPressed()));

            channelViewHolder.rlRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    channelViewHolder.circularTvMessageCount.setVisibility(View.GONE);
                    currentChannelItem.setUnreadCount(0);

                    FuguConversation conversation = new FuguConversation();
                    conversation.setLabel(currentChannelItem.getLabel());
                    conversation.setChannelId(currentChannelItem.getChannelId());
                    conversation.setLabelId(currentChannelItem.getLabelId());
                    conversation.setDefaultMessage(currentChannelItem.getMessage());
                    conversation.setChannelStatus(currentChannelItem.getChannelStatus());
                    conversation.setBusinessName(businessName);
                    conversation.setUserId(userId);
                    conversation.setEnUserId(enUserId);
                    conversation.setOpenChat(true);
                    conversation.setUserName(userName);
                    conversation.setIsTimeSet(1);
                    conversation.setStatus(currentChannelItem.getStatus());
                    conversation.setLast_sent_by_id(currentChannelItem.getLast_sent_by_id());
                    callback.onClick(conversation);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return fuguConversationList.size();
    }

    class ChannelViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout rlRoot;
        private TextView tvChannelName, tvMessage, tvDate, tvChannelIcon;
        private ImageView ivChannelIcon, ivMessageState;
        private TextView circularTvMessageCount;
        private View viewDivider, vClosed;

        ChannelViewHolder(View itemView) {
            super(itemView);
            rlRoot = itemView.findViewById(R.id.rlRoot);
            tvChannelName = itemView.findViewById(R.id.tvChannelName);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvChannelIcon = itemView.findViewById(R.id.tvChannelIcon);
            ivChannelIcon = itemView.findViewById(R.id.ivChannelIcon);
            ivMessageState = itemView.findViewById(R.id.ivMessageState);
            circularTvMessageCount = itemView.findViewById(R.id.circularTvMessageCount);
            viewDivider = itemView.findViewById(R.id.viewDivider);
            vClosed = itemView.findViewById(R.id.vClosed);

            FuguFontConfig fuguFontConfig = CommonData.getFontConfig();
            tvChannelName.setTypeface(fuguFontConfig.getNormalTextTypeFace(activity.getApplicationContext()));
            tvMessage.setTypeface(fuguFontConfig.getNormalTextTypeFace(activity.getApplicationContext()));
            tvDate.setTypeface(fuguFontConfig.getNormalTextTypeFace(activity.getApplicationContext()));
            circularTvMessageCount.setTypeface(fuguFontConfig.getNormalTextTypeFace(activity.getApplicationContext()));

        }
    }

    public interface Callback {
        void onClick(FuguConversation conversation);
    }
}


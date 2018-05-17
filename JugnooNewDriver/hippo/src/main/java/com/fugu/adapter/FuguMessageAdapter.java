package com.fugu.adapter;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;;
import com.fugu.HippoColorConfig;
import com.fugu.HippoConfig;
import com.fugu.FuguFontConfig;
import com.fugu.R;
import com.fugu.activity.FuguChatActivity;
import com.fugu.constant.FuguAppConstant;
import com.fugu.database.CommonData;
import com.fugu.model.CustomAction;
import com.fugu.model.FuguConversation;
import com.fugu.model.FuguFileDetails;
import com.fugu.model.Message;
import com.fugu.utils.DateUtils;
import com.fugu.utils.FuguLog;
import com.fugu.utils.GridDividerItemDecoration;
import com.fugu.utils.RoundedCornersTransformation;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Created by Bhavya Rattan on 02/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class FuguMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements FuguAppConstant {

    private final int FUGU_TYPE_HEADER = 0;
    private final int FUGU_ITEM_TYPE_OTHER = 1;
    private final int FUGU_ITEM_TYPE_SELF = 2;
    private DateUtils fuguDateUtil = DateUtils.getInstance();
    private Long fuguLabelId;
    private OnRetryListener mOnRetry;
    private HippoColorConfig hippoColorConfig;
    private Activity activity;
    private FuguConversation fuguConversation;
    private FuguChatActivity fuguChatActivity;
    private FuguFontConfig fuguFontConfig;

    @NonNull
    private List<ListItem> fuguItems = Collections.emptyList();

    public FuguMessageAdapter(Activity activity, @NonNull List<ListItem> fuguItems, Long fuguLabelId, FuguConversation fuguConversation) {
        this.fuguItems = fuguItems;
        this.activity = activity;
        this.fuguLabelId = fuguLabelId;
        this.fuguConversation = fuguConversation;
        removeDefaultMsgTime();
        hippoColorConfig = CommonData.getColorConfig();
        fuguFontConfig = CommonData.getFontConfig();
    }

    public void setOnRetryListener(OnRetryListener OnRetryListener) {
        mOnRetry = OnRetryListener;
    }

    public interface OnRetryListener {
        void onRetry(String file, final int messageIndex, int messageType, FuguFileDetails fileDetails, String uuid);
    }

    public void updateList(@NonNull List<ListItem> items) {
        this.fuguItems = items;
        removeDefaultMsgTime();
    }

    private void removeDefaultMsgTime() {
        if (fuguItems.size() > 0) {
            for (int i = 0; i < 2; i++) {
                if (i >= fuguItems.size()) {
                    break;
                }

                if (fuguItems.get(i).getType() == FUGU_ITEM_TYPE_OTHER && fuguLabelId.compareTo(-1L) != 0) {
                    ((EventItem) fuguItems.get(i)).getEvent().setSentAtUtc("");
                    break;
                }
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == FUGU_TYPE_HEADER) {
            View normalView = LayoutInflater.from(activity).inflate(R.layout.fugu_item_message_date, parent, false);
            return new DateViewHolder(normalView);
        } else if (viewType == FUGU_ITEM_TYPE_OTHER) {
            View normalView = LayoutInflater.from(activity).inflate(R.layout.fugu_item_message_left, parent, false);
            return new OtherMessageViewHolder(normalView);
        } else if (viewType == FUGU_ITEM_TYPE_SELF) {
            View headerRow = LayoutInflater.from(activity).inflate(R.layout.fugu_item_message_right, parent, false);
            return new SelfMessageViewHolder(headerRow);
        }

        return null;
    }

    private int pxToDp(int dpParam) {
        float d = activity.getResources().getDisplayMetrics().density;
        return (int) (dpParam * d); // margin in pixels
    }

    private Long downloadFile(String url, String fileName, String ext) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription(CommonData.getUserDetails().getData().getBusinessName());
        request.setTitle(fileName);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName + ext);
        DownloadManager manager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        if (manager != null) {
            return manager.enqueue(request);
        } else
            return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int pos) {
        final int position = holder.getAdapterPosition();
        final int itemType = getItemViewType(position);
        fuguChatActivity = (FuguChatActivity) activity;
        switch (itemType) {
            case FUGU_TYPE_HEADER:
                final DateViewHolder dateViewHolder = (DateViewHolder) holder;
                HeaderItem headerItem = (HeaderItem) fuguItems.get(position);
                if (headerItem.getDate().isEmpty()) {
                    dateViewHolder.tvDate.setVisibility(View.GONE);
                } else {
                    dateViewHolder.tvDate.setText(headerItem.getDate());
                    dateViewHolder.tvDate.setVisibility(View.VISIBLE);
                }

                GradientDrawable drawable = (GradientDrawable) dateViewHolder.tvDate.getBackground();
                drawable.setStroke((int) activity.getResources().getDimension(R.dimen.fugu_border_width), hippoColorConfig.getHippoBorderColor()); // set stroke width and stroke color

                dateViewHolder.tvDate.setTextColor(hippoColorConfig.getHippoChatDateText());
                break;
            case FUGU_ITEM_TYPE_OTHER:
                final OtherMessageViewHolder otherMessageViewHolder = (OtherMessageViewHolder) holder;
                final Message currentOrderItem = ((EventItem) fuguItems.get(position)).getEvent();
                otherMessageViewHolder.tvUserName.setTextColor(hippoColorConfig.getHippoSecondaryTextMsgFromName());
                otherMessageViewHolder.tvMsg.setTextColor(hippoColorConfig.getHippoPrimaryTextMsgFrom());
                otherMessageViewHolder.tvMsg.setLinkTextColor(hippoColorConfig.getHippoPrimaryTextMsgFrom());
                otherMessageViewHolder.tvMsg.setAutoLinkMask(Linkify.ALL);
                otherMessageViewHolder.tvTime.setTextColor(hippoColorConfig.getHippoSecondaryTextMsgFrom());

                if (currentOrderItem.getMessage().isEmpty()) {
                    otherMessageViewHolder.tvMsg.setVisibility(View.INVISIBLE);
                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    params2.weight = 1.0f;
                    params2.gravity = Gravity.END;
                    otherMessageViewHolder.rlMessages.setLayoutParams(params2);
                    otherMessageViewHolder.tvMsg.setTextSize(pxToDp(3));
                } else {
                    otherMessageViewHolder.tvMsg.setText(currentOrderItem.getMessage());
                    otherMessageViewHolder.tvMsg.setTextSize(17);
                    otherMessageViewHolder.tvMsg.setVisibility(View.VISIBLE);
                }


                String userNameText = "";
                String[] userNameSplitArray;
                int userNameStringCount = 1;
                if (!TextUtils.isEmpty(currentOrderItem.getfromName())) {
                    if (currentOrderItem.getfromName().split(" ").length > 0) {
                        userNameStringCount = currentOrderItem.getfromName().trim().split(" ").length;
                    }
                    userNameSplitArray = currentOrderItem.getfromName().trim().split(" ");
                    for (int i = 0; i < userNameStringCount; i++) {
                        userNameText = userNameText.concat(userNameSplitArray[i].substring(0, 1).toUpperCase());
                        userNameText = userNameText.concat(userNameSplitArray[i].substring(1).toLowerCase());
                        userNameText = userNameText.concat(" ");
                    }
                } else {
                    userNameText = !TextUtils.isEmpty(fuguConversation.getBusinessName()) ? fuguConversation.getBusinessName()
                            : activity.getString(R.string.fugu_support);

                }
                if (position != 0 && getItemViewType(position - 1) == FUGU_ITEM_TYPE_OTHER) {
                    Message lastOrderItem = ((EventItem) fuguItems.get(position - 1)).getEvent();
                    if (currentOrderItem.getUserId().compareTo(lastOrderItem.getUserId()) != 0) {
                        otherMessageViewHolder.llMessageBg.setBackgroundResource(R.drawable.chat_bg_left);
                        otherMessageViewHolder.tvUserName.setVisibility(View.VISIBLE);
                        otherMessageViewHolder.tvUserName.setText(userNameText);
                        otherMessageViewHolder.llRoot.setPadding(pxToDp(10),
                                pxToDp(8),
                                0,
                                pxToDp(1));
                        otherMessageViewHolder.tvUserName.setPadding(pxToDp(15),
                                pxToDp(7),
                                pxToDp(10),
                                pxToDp(0));
                        otherMessageViewHolder.tvMsg.setPadding(pxToDp(15),
                                pxToDp(7),
                                pxToDp(2),
                                pxToDp(7));
                        otherMessageViewHolder.ivMsgImage.setPadding(pxToDp(0),
                                pxToDp(5),
                                0,
                                pxToDp(0));
                    } else if (position + 1 != fuguItems.size() && getItemViewType(position + 1) != FUGU_ITEM_TYPE_OTHER) {
                        otherMessageViewHolder.tvUserName.setVisibility(View.GONE);
                        otherMessageViewHolder.llMessageBg.setBackgroundResource(R.drawable.chat_bg_left_normal);
                        otherMessageViewHolder.llRoot.setPadding(pxToDp(17),
                                pxToDp(1),
                                pxToDp(0),
                                pxToDp(4));
                        otherMessageViewHolder.tvUserName.setPadding(pxToDp(8),
                                pxToDp(7),
                                pxToDp(10),
                                pxToDp(0));
                        otherMessageViewHolder.tvMsg.setPadding(pxToDp(8),
                                pxToDp(7),
                                pxToDp(2),
                                pxToDp(7));
                        otherMessageViewHolder.ivMsgImage.setPadding(pxToDp(0),
                                pxToDp(5),
                                0,
                                pxToDp(0));
                    } else {
                        otherMessageViewHolder.tvUserName.setVisibility(View.GONE);
                        otherMessageViewHolder.llMessageBg.setBackgroundResource(R.drawable.chat_bg_left_normal);
                        otherMessageViewHolder.llRoot.setPadding(pxToDp(17),
                                pxToDp(1),
                                0,
                                pxToDp(1));
                        otherMessageViewHolder.tvUserName.setPadding(pxToDp(8),
                                pxToDp(7),
                                pxToDp(10),
                                pxToDp(0));
                        otherMessageViewHolder.tvMsg.setPadding(pxToDp(8),
                                pxToDp(7),
                                pxToDp(2),
                                pxToDp(7));
                        otherMessageViewHolder.ivMsgImage.setPadding(pxToDp(0),
                                pxToDp(5),
                                0,
                                pxToDp(0));
                    }
                } else if (position != 0 && (position + 1 != fuguItems.size()) && (getItemViewType(position + 1) == FUGU_ITEM_TYPE_OTHER)) {
                    int tvMsgTopPadding;
                    if (currentOrderItem.getMessageType() == TEXT_MESSAGE) {
                        tvMsgTopPadding = 0;
                    } else if (currentOrderItem.getMessageType() == ACTION_MESSAGE) {
                        tvMsgTopPadding = pxToDp(10);
                    } else {
                        tvMsgTopPadding = pxToDp(5);
                    }
                    otherMessageViewHolder.tvUserName.setVisibility(View.VISIBLE);
                    otherMessageViewHolder.tvUserName.setText(userNameText);
                    otherMessageViewHolder.llMessageBg.setBackgroundResource(R.drawable.chat_bg_left);
                    otherMessageViewHolder.llRoot.setPadding(pxToDp(10),
                            pxToDp(4),
                            pxToDp(0),
                            pxToDp(1));
                    otherMessageViewHolder.tvUserName.setPadding(pxToDp(15),
                            pxToDp(7),
                            pxToDp(10),
                            pxToDp(0));
                    otherMessageViewHolder.tvMsg.setPadding(pxToDp(15),
                            pxToDp(tvMsgTopPadding),
                            pxToDp(2),
                            pxToDp(7));
                    otherMessageViewHolder.ivMsgImage.setPadding(pxToDp(5),
                            pxToDp(0),
                            0,
                            pxToDp(0));
                } else {
                    int tvMsgTopPadding;
                    if (currentOrderItem.getMessageType() == TEXT_MESSAGE) {
                        tvMsgTopPadding = 0;
                    } else if (currentOrderItem.getMessageType() == ACTION_MESSAGE) {
                        tvMsgTopPadding = pxToDp(10);
                    } else {
                        tvMsgTopPadding = pxToDp(5);
                    }
                    otherMessageViewHolder.tvUserName.setVisibility(View.VISIBLE);
                    otherMessageViewHolder.tvUserName.setText(userNameText);
                    otherMessageViewHolder.llMessageBg.setBackgroundResource(R.drawable.chat_bg_left);
                    otherMessageViewHolder.llRoot.setPadding(pxToDp(10),
                            pxToDp(4),
                            0,
                            pxToDp(4));
                    otherMessageViewHolder.tvUserName.setPadding(pxToDp(15),
                            pxToDp(7),
                            pxToDp(10),
                            pxToDp(0));
                    otherMessageViewHolder.tvMsg.setPadding(pxToDp(15),
                            pxToDp(tvMsgTopPadding),
                            pxToDp(2),
                            pxToDp(7));
                    otherMessageViewHolder.ivMsgImage.setPadding(pxToDp(5),
                            pxToDp(0),
                            0,
                            pxToDp(0));
                }
                if (currentOrderItem.getSentAtUtc().isEmpty()) {
                    otherMessageViewHolder.tvTime.setVisibility(View.GONE);
                } else {
                    otherMessageViewHolder.tvTime.setText(DateUtils.getTime(fuguDateUtil.convertToLocal(currentOrderItem.getSentAtUtc())));
                    otherMessageViewHolder.tvTime.setVisibility(View.VISIBLE);
                }
                if (otherMessageViewHolder.tvUserName.length() > (otherMessageViewHolder.tvMsg.length() + otherMessageViewHolder.tvTime.length())
                        && otherMessageViewHolder.tvUserName.getVisibility() == View.VISIBLE) {
                    int length;
                    switch (otherMessageViewHolder.tvMsg.length()) {
                        case 1:
                            length = otherMessageViewHolder.tvUserName.length() - otherMessageViewHolder.tvMsg.length() - otherMessageViewHolder.tvTime.length() + 2;
                            for (int i = 0; i < length; i++) {
                                otherMessageViewHolder.tvMsg.append(activity.getString(R.string.space));
                            }
                            break;
                        case 2:
                            length = otherMessageViewHolder.tvUserName.length() - otherMessageViewHolder.tvMsg.length() - otherMessageViewHolder.tvTime.length();
                            for (int i = 0; i < length; i++) {
                                otherMessageViewHolder.tvMsg.append(activity.getString(R.string.space));
                            }
                            break;
                        case 3:
                            length = otherMessageViewHolder.tvUserName.length() - otherMessageViewHolder.tvMsg.length() - otherMessageViewHolder.tvTime.length() - 1;
                            for (int i = 0; i < length; i++) {
                                otherMessageViewHolder.tvMsg.append(activity.getString(R.string.space));
                            }
                            break;
                        default:
                            length = otherMessageViewHolder.tvUserName.length() - otherMessageViewHolder.tvMsg.length() - otherMessageViewHolder.tvTime.length() - 1;
                            for (int i = 0; i < length; i++) {
                                otherMessageViewHolder.tvMsg.append(activity.getString(R.string.space));
                            }
                            break;
                    }

                }
                NinePatchDrawable drawable2 = (NinePatchDrawable) otherMessageViewHolder.llMessageBg.getBackground();
                drawable2.setColorFilter(hippoColorConfig.getHippoBgMessageFrom(), PorterDuff.Mode.MULTIPLY);
                if (!currentOrderItem.getThumbnailUrl().isEmpty()) {
                    Glide.with(activity).load(currentOrderItem.getThumbnailUrl())
                            .bitmapTransform(new RoundedCornersTransformation(activity, 7, 2))
                            .placeholder(ContextCompat.getDrawable(activity, R.drawable.placeholder))
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .error(ContextCompat.getDrawable(activity, R.drawable.placeholder))
                            .into(otherMessageViewHolder.ivMsgImage);
                    otherMessageViewHolder.rlImageMessage.setVisibility(View.VISIBLE);
                } else {
                    otherMessageViewHolder.rlImageMessage.setVisibility(View.GONE);
                }

                otherMessageViewHolder.rlImageMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showImageDialog(activity, currentOrderItem.getUrl());
                    }
                });

                if (currentOrderItem.getMessageType() == FILE_MESSAGE) {
                    otherMessageViewHolder.llFileRoot.setVisibility(View.VISIBLE);
                    otherMessageViewHolder.tvFileName.setText(currentOrderItem.getFileName());

                    otherMessageViewHolder.ivDownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            otherMessageViewHolder.ivDownload.setVisibility(View.GONE);
                            otherMessageViewHolder.rlStopDownload.setVisibility(View.VISIBLE);

                            currentOrderItem.setDownloadId(downloadFile(currentOrderItem.getUrl(),
                                    currentOrderItem.getFileName(),
                                    currentOrderItem.getFileExtension()));
                        }
                    });

                    otherMessageViewHolder.rlStopDownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            otherMessageViewHolder.ivDownload.setVisibility(View.VISIBLE);
                            otherMessageViewHolder.rlStopDownload.setVisibility(View.GONE);
                            DownloadManager manager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
                            if (manager != null) {
                                manager.remove(currentOrderItem.getDownloadId());
                            }

                        }
                    });

                    otherMessageViewHolder.llFileDetails.setVisibility(View.VISIBLE);
                    otherMessageViewHolder.tvFileSize.setText(currentOrderItem.getFileSize());
                    otherMessageViewHolder.tvExtension.setText(currentOrderItem.getFileExtension());
                } else {
                    otherMessageViewHolder.llFileRoot.setVisibility(View.GONE);
                }


                if (currentOrderItem.getMessageType() == ACTION_MESSAGE) {
                    otherMessageViewHolder.rlCustomAction.setVisibility(View.VISIBLE);
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) otherMessageViewHolder
                            .rlCustomAction.getLayoutParams();

                    // increase left margin if background is chat_bg_left
                    if (otherMessageViewHolder.llMessageBg.getBackground().getConstantState() ==
                            ContextCompat.getDrawable(activity, R.drawable.chat_bg_left).getConstantState()) {
                        layoutParams.setMargins(pxToDp(13), pxToDp(10),
                                pxToDp(10), pxToDp(10));
                    } else {
                        layoutParams.setMargins(pxToDp(10), pxToDp(10),
                                pxToDp(10), pxToDp(10));
                    }

                    CustomAction customAction = currentOrderItem.getCustomAction();
                    if (customAction != null) {
                        // title
                        if (customAction.getTitle() != null && !TextUtils.isEmpty(customAction.getTitle())) {
                            otherMessageViewHolder.tvActionTitle.setVisibility(View.VISIBLE);
                            otherMessageViewHolder.tvActionTitle.setText(customAction.getTitle());
                        } else {
                            otherMessageViewHolder.tvActionTitle.setVisibility(View.GONE);
                        }

                        // title description
                        if (customAction.getTitleDescription() != null && !TextUtils.isEmpty(customAction.getTitleDescription())) {
                            otherMessageViewHolder.tvActionTitleDescription.setVisibility(View.VISIBLE);
                            otherMessageViewHolder.tvActionTitleDescription.setText(customAction.getTitleDescription());
                        } else {
                            otherMessageViewHolder.tvActionTitleDescription.setVisibility(View.GONE);
                        }

                        // image
                        if (customAction.getImageUrl() != null && !TextUtils.isEmpty(customAction.getImageUrl())) {
                            otherMessageViewHolder.llTextualContent.setBackgroundResource(R.drawable.fugu_white_background_curved_bottom);
                            otherMessageViewHolder.ivActionImage.setVisibility(View.VISIBLE);
                            Glide.with(activity).load(customAction.getImageUrl())
                                    .placeholder(ContextCompat.getDrawable(activity, R.drawable.placeholder))
                                    .dontAnimate()
                                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                    .error(ContextCompat.getDrawable(activity, R.drawable.placeholder))
                                    .into(otherMessageViewHolder.ivActionImage);
                        } else {
                            otherMessageViewHolder.ivActionImage.setVisibility(View.GONE);
                            otherMessageViewHolder.llTextualContent.setBackgroundResource(R.drawable.fugu_white_background_curved_all_sides);
                        }
                        // description
                        if (customAction.getDescriptionObjects() != null && customAction.getDescriptionObjects().size() != 0) {
                            otherMessageViewHolder.rvActionDescription.setVisibility(View.VISIBLE);
                            otherMessageViewHolder.rvActionDescription.setLayoutManager(new LinearLayoutManager(activity));
                            otherMessageViewHolder.rvActionDescription.setNestedScrollingEnabled(false);
                            otherMessageViewHolder.rvActionDescription.setAdapter(new CustomActionDescriptionAdapter(activity,
                                    customAction.getDescriptionObjects()));
                        } else {
                            otherMessageViewHolder.rvActionDescription.setVisibility(View.GONE);
                        }
                        // buttons
                        if (customAction.getActionButtons() != null && customAction.getActionButtons().size() != 0) {
                            otherMessageViewHolder.vwActionButtonDivider.setVisibility(View.VISIBLE);
                            otherMessageViewHolder.rvActionButtons.setVisibility(View.VISIBLE);
                            otherMessageViewHolder.rvActionButtons.setNestedScrollingEnabled(false);

                            // set span size of grid
                            int span = 2;
                            int size = customAction.getActionButtons().size();
                            if (size == 1) {
                                span = 1;
                            } else if (size % 3 == 0) {
                                span = 3;
                            } else {
                                span = 2;
                            }

                            otherMessageViewHolder.rvActionButtons.setLayoutManager(new GridLayoutManager(activity, span));
                            otherMessageViewHolder.rvActionButtons.addItemDecoration(new GridDividerItemDecoration(activity));
                            otherMessageViewHolder.rvActionButtons.setAdapter(new CustomActionButtonsAdapter(activity,
                                    customAction.getActionButtons()));
                        } else {
                            otherMessageViewHolder.vwActionButtonDivider.setVisibility(View.GONE);
                            otherMessageViewHolder.rvActionButtons.setVisibility(View.GONE);
                        }
                    }
                } else {
                    otherMessageViewHolder.rlCustomAction.setVisibility(View.GONE);
                }


                break;
            case FUGU_ITEM_TYPE_SELF:
                final SelfMessageViewHolder selfMessageViewHolder = (SelfMessageViewHolder) holder;
                final Message currentOrderItem2 = ((EventItem) fuguItems.get(position)).getEvent();
                if (position != 0
                        && (getItemViewType(position - 1) == FUGU_ITEM_TYPE_SELF)
                        && (position + 1 != fuguItems.size())
                        && (getItemViewType(position + 1) == FUGU_ITEM_TYPE_SELF)) {
                    selfMessageViewHolder.fuguLlRoot.setPadding(pxToDp(0),
                            pxToDp(1),
                            pxToDp(17),
                            pxToDp(1));
                    selfMessageViewHolder.fuguRlImageMessage.setPadding(pxToDp(0),
                            pxToDp(4),
                            pxToDp(0),
                            pxToDp(4));
                } else if (position != 0
                        && ((getItemViewType(position - 1) == FUGU_ITEM_TYPE_SELF)
                        && (position + 1 != fuguItems.size())
                        && (getItemViewType(position + 1) != FUGU_ITEM_TYPE_SELF))
                        || (!(position - 1 < 0) && (getItemViewType(position - 1) == FUGU_ITEM_TYPE_SELF))) {
                    if (position == fuguItems.size() - 1) {
                        selfMessageViewHolder.fuguLlRoot.setPadding(pxToDp(0),
                                pxToDp(1),
                                pxToDp(17),
                                pxToDp(1));
                        selfMessageViewHolder.fuguRlImageMessage.setPadding(pxToDp(0),
                                pxToDp(4),
                                pxToDp(0),
                                pxToDp(4));
                    } else {
                        selfMessageViewHolder.fuguLlRoot.setPadding(pxToDp(0),
                                pxToDp(1),
                                pxToDp(17),
                                pxToDp(4));
                        selfMessageViewHolder.fuguRlImageMessage.setPadding(pxToDp(0),
                                pxToDp(4),
                                pxToDp(0),
                                pxToDp(4));
                    }

                } else if (position != 0
                        && (position + 1 != fuguItems.size())
                        && (getItemViewType(position + 1) == FUGU_ITEM_TYPE_SELF)) {
                    selfMessageViewHolder.fuguLlRoot.setPadding(pxToDp(0),
                            pxToDp(4),
                            pxToDp(10),
                            pxToDp(1));
                    selfMessageViewHolder.fuguRlImageMessage.setPadding(pxToDp(0),
                            pxToDp(4),
                            pxToDp(0),
                            pxToDp(4));
                } else {
                    if (position == fuguItems.size() - 1) {
                        selfMessageViewHolder.fuguLlRoot.setPadding(pxToDp(0),
                                pxToDp(4),
                                pxToDp(10),
                                pxToDp(1));
                        selfMessageViewHolder.fuguRlImageMessage.setPadding(pxToDp(0),
                                pxToDp(4),
                                pxToDp(0),
                                pxToDp(4));
                    } else {
                        selfMessageViewHolder.fuguLlRoot.setPadding(pxToDp(0),
                                pxToDp(4),
                                pxToDp(10),
                                pxToDp(4));
                        selfMessageViewHolder.fuguRlImageMessage.setPadding(pxToDp(0),
                                pxToDp(4),
                                pxToDp(0),
                                pxToDp(4));
                    }


                }
                if (position != 0 && (getItemViewType(position - 1) != FUGU_ITEM_TYPE_SELF)) {
                    selfMessageViewHolder.FuguLlMessageBg.setBackgroundResource(R.drawable.chat_bg_right);
                    selfMessageViewHolder.fuguRlMessages.setPadding(pxToDp(0),
                            pxToDp(0),
                            pxToDp(12),
                            pxToDp(0));
                    selfMessageViewHolder.fuguRlImageMessage.setPadding(pxToDp(0),
                            pxToDp(4),
                            pxToDp(4),
                            pxToDp(4));
                } else {
                    selfMessageViewHolder.FuguLlMessageBg.setBackgroundResource(R.drawable.chat_bg_right_normal);
                    selfMessageViewHolder.fuguRlMessages.setPadding(pxToDp(0),
                            pxToDp(0),
                            pxToDp(4),
                            pxToDp(0));
                    selfMessageViewHolder.fuguRlImageMessage.setPadding(pxToDp(0),
                            pxToDp(4),
                            0,
                            pxToDp(4));
                }

                NinePatchDrawable drawable3 = (NinePatchDrawable) selfMessageViewHolder.FuguLlMessageBg.getBackground();
                drawable3.setColorFilter(hippoColorConfig.getHippoBgMessageYou(), PorterDuff.Mode.MULTIPLY);
                selfMessageViewHolder.fuguTvMsg.setTextColor(hippoColorConfig.getHippoPrimaryTextMsgYou());
                selfMessageViewHolder.fuguTvMsg.setLinkTextColor(hippoColorConfig.getHippoPrimaryTextMsgYou());
                selfMessageViewHolder.fuguTvMsg.setAutoLinkMask(Linkify.ALL);
                selfMessageViewHolder.fuguTvTime.setTextColor(hippoColorConfig.getHippoSecondaryTextMsgYou());


                if (currentOrderItem2.getMessage().isEmpty()) {
                    selfMessageViewHolder.fuguTvMsg.setVisibility(View.INVISIBLE);
                    LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 50);
                    params2.weight = 1.0f;
                    params2.gravity = Gravity.END;
                    selfMessageViewHolder.fuguRlMessages.setLayoutParams(params2);
                    selfMessageViewHolder.fuguTvMsg.setTextSize(pxToDp(3));
                } else {
                    selfMessageViewHolder.fuguTvMsg.setText(currentOrderItem2.getMessage());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    selfMessageViewHolder.fuguRlMessages.setLayoutParams(params);
                    selfMessageViewHolder.fuguTvMsg.setTextSize(17);
                    selfMessageViewHolder.fuguTvMsg.setVisibility(View.VISIBLE);
                }

                if (currentOrderItem2.getSentAtUtc().isEmpty()) {
                    selfMessageViewHolder.fuguTvTime.setVisibility(View.GONE);
                } else {
                    selfMessageViewHolder.fuguTvTime.setText(DateUtils.getTime(fuguDateUtil.convertToLocal(currentOrderItem2.getSentAtUtc())));
                    selfMessageViewHolder.fuguTvTime.setVisibility(View.VISIBLE);
                }
                FuguLog.e("currentOrderItem.getThumbnailUrl()", "==" + currentOrderItem2.getThumbnailUrl());

                if (!currentOrderItem2.getThumbnailUrl().isEmpty()) {
                    if (currentOrderItem2.getMessageStatus() == MESSAGE_UNSENT || currentOrderItem2.getMessageStatus() == MESSAGE_IMAGE_RETRY) {
                        Glide.with(activity).load(currentOrderItem2.getThumbnailUrl())
                                .bitmapTransform(new RoundedCornersTransformation(activity, 7, 2))
                                .dontAnimate()
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .error(ContextCompat.getDrawable(activity, R.drawable.placeholder))
                                .into(selfMessageViewHolder.fuguIvMsgImage);
                        if (currentOrderItem2.getMessageStatus() == MESSAGE_IMAGE_RETRY) {
                            selfMessageViewHolder.fuguPbLoading.setVisibility(View.GONE);
                            selfMessageViewHolder.fuguBtnRetry.setVisibility(View.VISIBLE);
                        } else {
                            selfMessageViewHolder.fuguPbLoading.setVisibility(View.VISIBLE);
                            selfMessageViewHolder.fuguBtnRetry.setVisibility(View.GONE);
                        }
                    } else {
                        Glide.with(activity).load(currentOrderItem2.getThumbnailUrl())
                                //.centerCrop()
                                .bitmapTransform(new RoundedCornersTransformation(activity, 7, 2))
                                .placeholder(ContextCompat.getDrawable(activity, R.drawable.placeholder))
                                .dontAnimate()
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .error(ContextCompat.getDrawable(activity, R.drawable.placeholder))
                                .into(selfMessageViewHolder.fuguIvMsgImage);
                        selfMessageViewHolder.fuguPbLoading.setVisibility(View.GONE);
                        selfMessageViewHolder.fuguBtnRetry.setVisibility(View.GONE);
                    }
                    selfMessageViewHolder.fuguRlImageMessage.setVisibility(View.VISIBLE);
                } else {
                    selfMessageViewHolder.fuguRlImageMessage.setVisibility(View.GONE);
                }
                selfMessageViewHolder.fuguRlImageMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (currentOrderItem2.getMessageStatus() == MESSAGE_UNSENT || currentOrderItem2.getMessageStatus() == MESSAGE_IMAGE_RETRY) {
                            return;
                        }
                        showImageDialog(activity, currentOrderItem2.getUrl());
                    }
                });
                switch (currentOrderItem2.getMessageStatus()) {
                    case MESSAGE_UNSENT:
                    case MESSAGE_IMAGE_RETRY:
                    case MESSAGE_FILE_RETRY:
                        selfMessageViewHolder.fuguIvMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_ic_waiting));
                        selfMessageViewHolder.fuguIvMessageState.setVisibility(View.VISIBLE);

                        selfMessageViewHolder.fuguIvMessageState.getDrawable()
                                .setColorFilter(hippoColorConfig.getHippoSecondaryTextMsgYou(), PorterDuff.Mode.SRC_ATOP);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!currentOrderItem2.isSent()) {
                                    selfMessageViewHolder.tvTryAgain.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            fuguChatActivity.sendMessage(selfMessageViewHolder.getAdapterPosition());
//                                            selfMessageViewHolder.llRetry.setVisibility(View.GONE);
                                        }
                                    });
                                    selfMessageViewHolder.tvCancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            fuguChatActivity.cancelMessage(selfMessageViewHolder.getAdapterPosition());
//                                            selfMessageViewHolder.llRetry.setVisibility(View.GONE);
                                        }
                                    });
                                    if (TextUtils.isEmpty(currentOrderItem2.getMessage())) {
                                        selfMessageViewHolder.llRetry.setVisibility(View.GONE);
                                        selfMessageViewHolder.fuguBtnRetry.setVisibility(View.GONE);
                                        selfMessageViewHolder.fuguPbLoading.setVisibility(View.VISIBLE);
                                    } else {
                                        selfMessageViewHolder.llRetry.setVisibility(View.VISIBLE);
                                    }
                                } else {
                                    selfMessageViewHolder.llRetry.setVisibility(View.GONE);
                                }
                            }
                        }, 0);


                        break;
                    case MESSAGE_READ:
                        selfMessageViewHolder.fuguIvMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_double));
                        selfMessageViewHolder.fuguIvMessageState.getDrawable()
                                .setColorFilter(hippoColorConfig.getHippoMessageRead(), PorterDuff.Mode.SRC_ATOP);

                        selfMessageViewHolder.fuguIvMessageState.setVisibility(View.VISIBLE);
                        selfMessageViewHolder.llRetry.setVisibility(View.GONE);

                        break;
                    case MESSAGE_SENT:
                        selfMessageViewHolder.fuguIvMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_single));
                        selfMessageViewHolder.fuguIvMessageState.setVisibility(View.VISIBLE);

                        selfMessageViewHolder.fuguIvMessageState.getDrawable()
                                .setColorFilter(hippoColorConfig.getHippoSecondaryTextMsgYou(), PorterDuff.Mode.SRC_ATOP);
                        selfMessageViewHolder.llRetry.setVisibility(View.GONE);

                        break;
                    case MESSAGE_DELIVERED:
                        selfMessageViewHolder.fuguIvMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_double));
                        if (android.os.Build.VERSION.SDK_INT >= 21) {
                            selfMessageViewHolder.fuguIvMessageState.getDrawable().setTint(ContextCompat.getColor(activity, R.color.fugu_drawable_color));
                        }
                        selfMessageViewHolder.fuguIvMessageState.setVisibility(View.VISIBLE);

                        selfMessageViewHolder.fuguIvMessageState.getDrawable()
                                .setColorFilter(hippoColorConfig.getHippoSecondaryTextMsgYou(), PorterDuff.Mode.SRC_ATOP);
                        selfMessageViewHolder.llRetry.setVisibility(View.GONE);
                        break;
                    default:
                        selfMessageViewHolder.fuguIvMessageState.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.fugu_tick_single));
                        selfMessageViewHolder.fuguIvMessageState.setVisibility(View.VISIBLE);

                        selfMessageViewHolder.fuguIvMessageState.getDrawable()
                                .setColorFilter(hippoColorConfig.getHippoSecondaryTextMsgYou(), PorterDuff.Mode.SRC_ATOP);

                        selfMessageViewHolder.llRetry.setVisibility(View.GONE);
                        break;
                }

                selfMessageViewHolder.fuguBtnRetry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mOnRetry != null) {
                            selfMessageViewHolder.fuguPbLoading.setVisibility(View.VISIBLE);
                            selfMessageViewHolder.fuguBtnRetry.setVisibility(View.GONE);
                            mOnRetry.onRetry(currentOrderItem2.getUrl(), currentOrderItem2.getMessageIndex(),
                                    IMAGE_MESSAGE, null, currentOrderItem2.getUuid());
                        }
                    }
                });

                if (currentOrderItem2.getMessageType() == FILE_MESSAGE) {
                    selfMessageViewHolder.fuguLlFileRoot.setVisibility(View.VISIBLE);
                    selfMessageViewHolder.fuguTvFileName.setText(currentOrderItem2.getFileName());

                    selfMessageViewHolder.fuguIvUpload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mOnRetry != null) {

                                FuguFileDetails fileDetails = new FuguFileDetails();
                                fileDetails.setFilePath(currentOrderItem2.getFilePath());
                                fileDetails.setFileExtension(currentOrderItem2.getFileExtension());
                                fileDetails.setFileSize(currentOrderItem2.getFileSize());
                                fileDetails.setFileName(currentOrderItem2.getFileName());

                                selfMessageViewHolder.fuguRlStopUpload.setVisibility(View.VISIBLE);
                                selfMessageViewHolder.fuguIvUpload.setVisibility(View.GONE);
                                mOnRetry.onRetry(currentOrderItem2.getUrl(), currentOrderItem2.getMessageIndex(),
                                        FILE_MESSAGE, fileDetails, currentOrderItem2.getUuid());
                            }
                        }
                    });

                    selfMessageViewHolder.fuguLlFileDetails.setVisibility(View.VISIBLE);
                    selfMessageViewHolder.fuguTvFileSize.setText(currentOrderItem2.getFileSize());
                    selfMessageViewHolder.fuguTvExtension.setText(currentOrderItem2.getFileExtension());
                } else {
                    selfMessageViewHolder.fuguLlFileRoot.setVisibility(View.GONE);
                }

                if (currentOrderItem2.getMessageStatus() == MESSAGE_FILE_RETRY) {
                    selfMessageViewHolder.fuguRlStopUpload.setVisibility(View.GONE);
                    selfMessageViewHolder.fuguIvUpload.setVisibility(View.VISIBLE);
                } else if (currentOrderItem2.getMessageStatus() == MESSAGE_UNSENT) {
                    selfMessageViewHolder.fuguRlStopUpload.setVisibility(View.VISIBLE);
                    selfMessageViewHolder.fuguIvUpload.setVisibility(View.GONE);
                } else {
                    selfMessageViewHolder.fuguRlStopUpload.setVisibility(View.GONE);
                    selfMessageViewHolder.fuguIvUpload.setVisibility(View.GONE);
                }

                selfMessageViewHolder.fuguLlFileRoot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Check and ask for Permissions
                        if (!HippoConfig.getInstance().askUserToGrantPermission(activity,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE, "Please grant permission to Storage",
                                PERMISSION_READ_FILE)) return;

                        FuguLog.e("adapter file path", currentOrderItem2.getFilePath());

                        try {
                            Intent photoPickerIntent = new Intent(Intent.ACTION_VIEW);
                            File file = new File(currentOrderItem2.getFilePath());
                            photoPickerIntent.setData(Uri.fromFile(file));
                            activity.startActivity(photoPickerIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(activity.getApplicationContext(),
                                    activity.getString(R.string.fugu_file_not_found), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;

        }
    }

    @Override
    public int getItemViewType(int position) {
        return fuguItems.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return fuguItems.size();
    }

    class OtherMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUserName;
        private TextView tvMsg;
        private TextView tvTime;
        private RelativeLayout rlImageMessage;
        private ImageView ivMsgImage;
        private LinearLayout llMessageBg;
        private LinearLayout llRoot;
        private LinearLayout llFileRoot;
        private TextView tvFileName;
        private ImageView ivDownload;
        private RelativeLayout rlStopDownload, rlMessages;
        private LinearLayout llFileDetails;
        private TextView tvFileSize;
        private TextView tvExtension;
        // custom action components
        private RelativeLayout rlCustomAction;
        private ImageView ivActionImage;
        private TextView tvActionTitle;
        private TextView tvActionTitleDescription;
        private RecyclerView rvActionDescription;
        private RecyclerView rvActionButtons;
        private View vwActionButtonDivider;
        private LinearLayout llTextualContent;


        OtherMessageViewHolder(View itemView) {
            super(itemView);
            llRoot = itemView.findViewById(R.id.llRoot);
            llMessageBg = itemView.findViewById(R.id.llMessageBg);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvMsg = itemView.findViewById(R.id.tvMsg);
            tvTime = itemView.findViewById(R.id.tvTime);
            rlImageMessage = itemView.findViewById(R.id.rlImageMessage);
            ivMsgImage = itemView.findViewById(R.id.ivMsgImage);
            llFileRoot = itemView.findViewById(R.id.llFileRoot);
            tvFileName = itemView.findViewById(R.id.tvFileName);
            ivDownload = itemView.findViewById(R.id.ivDownload);
            rlStopDownload = itemView.findViewById(R.id.rlStopDownload);
            llFileDetails = itemView.findViewById(R.id.llFileDetails);
            tvFileSize = itemView.findViewById(R.id.tvFileSize);
            tvExtension = itemView.findViewById(R.id.tvExtension);
            rlMessages = itemView.findViewById(R.id.rlMessages);
            rlCustomAction = itemView.findViewById(R.id.layoutCustomAction);
            tvActionTitle = itemView.findViewById(R.id.tvActionTitle);
            rvActionDescription = itemView.findViewById(R.id.rvActionDescription);
            rvActionButtons = itemView.findViewById(R.id.rvActionButtons);
            ivActionImage = itemView.findViewById(R.id.ivActionImage);
            vwActionButtonDivider = itemView.findViewById(R.id.vwActionButtonDivider);
            tvActionTitleDescription = itemView.findViewById(R.id.tvActionDescription);
            llTextualContent = itemView.findViewById(R.id.llTextualContent);

            Typeface typeface = fuguFontConfig.getNormalTextTypeFace(activity.getApplicationContext());
            tvUserName.setTypeface(typeface); tvMsg.setTypeface(typeface); tvTime.setTypeface(typeface);
            tvFileName.setTypeface(typeface); tvExtension.setTypeface(typeface); tvActionTitle.setTypeface(typeface);
            tvActionTitleDescription.setTypeface(typeface);

        }
    }

    class SelfMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView fuguTvMsg, fuguTvTime;
        private RelativeLayout fuguRlImageMessage;
        private ImageView fuguIvMessageState, fuguIvMsgImage;
        private ProgressBar fuguPbLoading;
        private Button fuguBtnRetry;
        private LinearLayout FuguLlMessageBg;
        private LinearLayout fuguLlRoot;
        private TextView tvTryAgain;
        private TextView tvCancel;

        private LinearLayout fuguLlFileRoot;
        private TextView fuguTvFileName;
        private ImageView fuguIvUpload;
        private RelativeLayout fuguRlStopUpload, fuguRlMessages;
        private LinearLayout fuguLlFileDetails;
        private TextView fuguTvFileSize;
        private TextView fuguTvExtension;
        private LinearLayout llRetry;

        SelfMessageViewHolder(View itemView) {
            super(itemView);
            fuguLlRoot = itemView.findViewById(R.id.llRoot);
            FuguLlMessageBg = itemView.findViewById(R.id.llMessageBg);
            fuguTvMsg = itemView.findViewById(R.id.tvMsg);
            fuguTvTime = itemView.findViewById(R.id.tvTime);
            fuguRlImageMessage = itemView.findViewById(R.id.rlImageMessage);
            fuguRlMessages = itemView.findViewById(R.id.rlMessages);
            fuguIvMessageState = itemView.findViewById(R.id.ivMessageState);
            fuguIvMsgImage = itemView.findViewById(R.id.ivMsgImage);
            fuguPbLoading = itemView.findViewById(R.id.pbLoading);
            fuguBtnRetry = itemView.findViewById(R.id.btnRetry);
            tvTryAgain = itemView.findViewById(R.id.tvTryAgain);
            tvCancel = itemView.findViewById(R.id.tvCancel);
            llRetry = itemView.findViewById(R.id.llRetry);

            fuguLlFileRoot = itemView.findViewById(R.id.llFileRoot);
            fuguTvFileName = itemView.findViewById(R.id.tvFileName);
            fuguIvUpload = itemView.findViewById(R.id.ivUpload);
            fuguRlStopUpload = itemView.findViewById(R.id.rlStopUpload);
            fuguLlFileDetails = itemView.findViewById(R.id.llFileDetails);
            fuguTvFileSize = itemView.findViewById(R.id.tvFileSize);
            fuguTvExtension = itemView.findViewById(R.id.tvExtension);

            Typeface typeface = fuguFontConfig.getNormalTextTypeFace(activity.getApplicationContext());
            fuguTvMsg.setTypeface(typeface); fuguTvTime.setTypeface(typeface); tvCancel.setTypeface(typeface);
            tvTryAgain.setTypeface(typeface); fuguTvFileName.setTypeface(typeface); fuguTvFileSize.setTypeface(typeface);
            fuguTvExtension.setTypeface(typeface); fuguBtnRetry.setTypeface(typeface);
        }
    }

    class DateViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDate;

        DateViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            Typeface typeface = fuguFontConfig.getNormalTextTypeFace(activity.getApplicationContext());
            tvDate.setTypeface(typeface);
        }

    }

    private void showImageDialog(Context activity, String imgUrl) {

        try {
            final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            //setting custom layout to dialog
            dialog.setContentView(R.layout.fugu_image_dialog);
            @SuppressWarnings("ConstantConditions") WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
            lp.dimAmount = 1.0f; // Dim level. 0.0 - no dim, 1.0 - completely opaque
            dialog.getWindow().setAttributes(lp);
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(false);
            ImageView ivImage = dialog.findViewById(R.id.ivImage);
            Glide.with(activity).load(imgUrl)
                    .placeholder(ContextCompat.getDrawable(activity, R.drawable.placeholder))
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .error(ContextCompat.getDrawable(activity, R.drawable.placeholder))
                    .into(ivImage);
            TextView tvCross = dialog.findViewById(R.id.tvCross);
            tvCross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
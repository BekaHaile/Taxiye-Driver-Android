package com.fugu.support.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fugu.HippoColorConfig;
import com.fugu.HippoConfig;
import com.fugu.R;
import com.fugu.database.CommonData;
import com.fugu.support.Utils.Constants;
import com.fugu.support.Utils.SupportKeys;
import com.fugu.support.callback.OnActionTypeCallback;
import com.fugu.support.callback.SupportDetailPresenter;
import com.fugu.support.callback.SupportDetailView;
import com.fugu.support.logicImplView.HippoSupportDetailInterImpl;
import com.fugu.support.logicImplView.HippoSupportDetailView;
import com.fugu.support.model.Category;
import com.fugu.support.model.Content;
import com.fugu.support.model.Item;
import com.fugu.support.model.callbackModel.OpenChatParams;
import com.fugu.support.model.callbackModel.SendQueryChat;
import com.fugu.utils.FuguLog;
import com.google.gson.Gson;

import java.util.ArrayList;

import static com.fugu.support.Utils.SupportKeys.SupportKey.DEFAULT_SUPPORT;
import static com.fugu.support.Utils.SupportKeys.SupportKey.SUPPORT_CATEGORY_DATA;
import static com.fugu.support.Utils.SupportKeys.SupportKey.SUPPORT_PATH;
import static com.fugu.support.Utils.SupportKeys.SupportKey.SUPPORT_TAGS;
import static com.fugu.support.Utils.SupportKeys.SupportKey.SUPPORT_TRANSACTION_ID;

/**
 * Created by gurmail on 29/03/18.
 */

public class HippoSupportDetailFragment extends BaseFragment implements View.OnClickListener, SupportDetailView {

    private static final String TAG = HippoSupportDetailFragment.class.getSimpleName();

    private View rootView;
    private Toolbar toolbar;
    private TextView header, description, errorView;
    private EditText descriptionBox;
    private Button submit, callBtn, chatBtn;

    private SupportDetailPresenter supportDetailView;
    private OnActionTypeCallback typeCallback;

    private ArrayList<String> pathList;
    private ArrayList<String> mTags;
    private String transactionId;
    private String message = "";

    private Gson gson;
    private Item supportResponses;
    private Category category;
    private HippoColorConfig hippoColorConfig;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gson = new Gson();
        if (getArguments() != null) {
            if (getArguments().containsKey(DEFAULT_SUPPORT))
                supportResponses = gson.fromJson(getArguments().getString(DEFAULT_SUPPORT), Item.class);

            if(getArguments().containsKey(SUPPORT_TAGS))
                mTags = gson.fromJson(getArguments().getString(SUPPORT_TAGS), ArrayList.class);

            if (getArguments().containsKey(SUPPORT_PATH))
                pathList = gson.fromJson(getArguments().getString(SUPPORT_PATH), ArrayList.class);
            if (getArguments().containsKey(SUPPORT_TRANSACTION_ID))
                transactionId = getArguments().getString(SUPPORT_TRANSACTION_ID);
            if(getArguments().containsKey(SUPPORT_CATEGORY_DATA))
                category = gson.fromJson(getArguments().getString(SUPPORT_CATEGORY_DATA), Category.class);

            FuguLog.d(TAG, "transactionId = " + transactionId);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fugu_support_detail, container, false);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        typeCallback = (OnActionTypeCallback) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (typeCallback != null)
            typeCallback = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (supportDetailView != null)
            supportDetailView.onDestroy();
    }

    private void initView() {

        toolbar = rootView.findViewById(R.id.my_toolbar);
        header = rootView.findViewById(R.id.textViewSubtitle);

        description = rootView.findViewById(R.id.textViewDescription);
        errorView = rootView.findViewById(R.id.textViewRSOtherError);
        descriptionBox = rootView.findViewById(R.id.editTextMessage);

        descriptionBox.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.editTextMessage) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });

        submit = rootView.findViewById(R.id.buttonSubmit);
        callBtn = rootView.findViewById(R.id.buttonCall);
        chatBtn = rootView.findViewById(R.id.buttonChat);

        setData();
        setConfigColor();

        submit.setOnClickListener(this);
        callBtn.setOnClickListener(this);
        chatBtn.setOnClickListener(this);

        supportDetailView = new HippoSupportDetailView(getActivity(), this, new HippoSupportDetailInterImpl());
        try {
            setupUI(rootView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setConfigColor() {
        hippoColorConfig = CommonData.getColorConfig();

        header.setTextColor(hippoColorConfig.getHippoTextColorPrimary());
        description.setTextColor(hippoColorConfig.getHippoFaqDescription());

        descriptionBox.setHintTextColor(hippoColorConfig.getHippoTypeMessageHint());
        descriptionBox.setTextColor(hippoColorConfig.getHippoTypeMessageText());

        submit.setTextColor(hippoColorConfig.getHippoActionBarText());
        callBtn.setTextColor(hippoColorConfig.getHippoActionBarText());
        chatBtn.setTextColor(hippoColorConfig.getHippoActionBarText());


        float radius = Constants.convertDpToPixel(5);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            submit.setBackground(HippoColorConfig.makeRoundedSelector(hippoColorConfig.getHippoActionBarBg(), radius));
            callBtn.setBackground(HippoColorConfig.makeRoundedSelector(hippoColorConfig.getHippoActionBarBg(), radius));
            chatBtn.setBackground(HippoColorConfig.makeRoundedSelector(hippoColorConfig.getHippoActionBarBg(), radius));

        } else {
            submit.setBackgroundDrawable(HippoColorConfig.makeRoundedSelector(hippoColorConfig.getHippoActionBarBg(), radius));
            callBtn.setBackgroundDrawable(HippoColorConfig.makeRoundedSelector(hippoColorConfig.getHippoActionBarBg(), radius));
            chatBtn.setBackgroundDrawable(HippoColorConfig.makeRoundedSelector(hippoColorConfig.getHippoActionBarBg(), radius));

        }
    }

    private void setData() {
        setToolbar(toolbar, supportResponses.getTitle());
        Content content = supportResponses.getContent();

        if (content != null) {

            if (content.getSubHeading() == null || TextUtils.isEmpty(content.getSubHeading().getText())) {
                header.setVisibility(View.GONE);
            } else {
                header.setText(content.getSubHeading().getText());
                header.setVisibility(View.VISIBLE);
            }

            if (content.getDescription() == null || TextUtils.isEmpty(content.getDescription().getText())) {
                description.setVisibility(View.GONE);
            } else {
                description.setText(content.getDescription().getText());
                description.setVisibility(View.VISIBLE);
            }

            if (content.getQueryForm() == null || content.getQueryForm().getTextView() == null) {
                descriptionBox.setVisibility(View.GONE);
            } else {
                if(!TextUtils.isEmpty(content.getQueryForm().getTextView().getText()))
                    descriptionBox.setText(content.getQueryForm().getTextView().getText());

                descriptionBox.setVisibility(View.VISIBLE);
            }

            if (content.getSubmitButton() == null || TextUtils.isEmpty(content.getSubmitButton().getText())) {
                submit.setVisibility(View.GONE);
            } else {
                submit.setTag(content.getSubmitButton().getSucessMessage());
                submit.setText(content.getSubmitButton().getText());
                submit.setVisibility(View.VISIBLE);
            }

            if (content.getCallButton() == null || TextUtils.isEmpty(content.getCallButton().getPhone())) {
                callBtn.setVisibility(View.GONE);
            } else {
                callBtn.setText(content.getCallButton().getText());
                callBtn.setVisibility(View.VISIBLE);
            }

            if (content.getChatButton() == null || TextUtils.isEmpty(content.getChatButton().getText())) {
                chatBtn.setVisibility(View.GONE);
            } else {
                chatBtn.setText(content.getChatButton().getText());
                chatBtn.setVisibility(View.VISIBLE);
            }
        }

        pathList = CommonData.getPathList();
        pathList.add(supportResponses.getTitle());
        CommonData.setSupportPath(pathList);
    }

    /**
     * Method used to make call.
     *
     * @param activity
     * @param phoneNumber
     */
    private void openCallIntent(Activity activity, String phoneNumber) {
        try {
            Intent callIntent = new Intent(Intent.ACTION_VIEW);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            activity.startActivity(callIntent);
        } catch (Exception e) {
            e.printStackTrace();
            showError();
        }

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.buttonSubmit) {
            message = null;
            if(descriptionBox.getVisibility() == View.VISIBLE) {
                if (!TextUtils.isEmpty(descriptionBox.getText().toString().trim()))
                    message = descriptionBox.getText().toString().trim();
            }

            String successMsg = (String) submit.getTag();
            successMsg = TextUtils.isEmpty(successMsg) ? getActivity().getString(R.string.hippo_message_sucessfully) : successMsg;

            SendQueryChat queryChat = new SendQueryChat(SupportKeys.SupportQueryType.QUERY, category,
                    transactionId, CommonData.getUserUniqueKey(), supportResponses.getSupportId(),
                    pathList, message, successMsg, mTags);
            if(supportResponses.getContent() != null && supportResponses.getContent().getSubHeading() != null
                    && !TextUtils.isEmpty(supportResponses.getContent().getSubHeading().getText()))
                queryChat.setSubHeader(supportResponses.getContent().getSubHeading().getText());
            supportDetailView.sendQuery(queryChat);
        } else if (id == R.id.buttonChat) {
            SendQueryChat queryChat = new SendQueryChat(SupportKeys.SupportQueryType.CHAT, category,
                    transactionId, CommonData.getUserUniqueKey(), supportResponses.getSupportId(), pathList, mTags);

            if(supportResponses.getContent() != null && supportResponses.getContent().getSubHeading() != null
                    && !TextUtils.isEmpty(supportResponses.getContent().getSubHeading().getText()))
                queryChat.setSubHeader(supportResponses.getContent().getSubHeading().getText());

            supportDetailView.sendQuery(queryChat);
        } else if (id == R.id.buttonCall) {
            openCallIntent(getActivity(), supportResponses.getContent().getCallButton().getPhone());
        }
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void openChat(OpenChatParams chatParams) {
        HippoConfig.getInstance().openChatByTransactionId(chatParams.getTransactionId(),
                chatParams.getUserUniqueKey(), chatParams.getChannelName(),
                chatParams.getTagsList(), chatParams.getData(), "1", chatParams.getCustomAttributes());
    }

    @Override
    public void sucessfull() {
        typeCallback.removeFragment();
    }

    @Override
    public void showError() {
        Toast.makeText(getActivity(), "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
    }

}

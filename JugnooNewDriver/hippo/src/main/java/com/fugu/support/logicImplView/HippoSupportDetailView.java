package com.fugu.support.logicImplView;

import android.app.Activity;
import android.text.TextUtils;

import com.fugu.R;
import com.fugu.database.CommonData;
import com.fugu.model.FuguConversation;
import com.fugu.model.FuguPutUserDetailsResponse;
import com.fugu.support.Utils.CommonSupportParam;
import com.fugu.support.Utils.SupportKeys;
import com.fugu.support.callback.HippoSupportDetailInter;
import com.fugu.support.callback.SupportDetailPresenter;
import com.fugu.support.callback.SupportDetailView;
import com.fugu.support.model.Category;
import com.fugu.support.model.callbackModel.OpenChatParams;
import com.fugu.support.model.callbackModel.SendQueryChat;
import com.google.gson.Gson;

import java.util.ArrayList;

import static com.fugu.constant.FuguAppConstant.STATUS_CHANNEL_OPEN;

/**
 * Created by Gurmail S. Kang on 03/04/18.
 * @author gurmail
 */

public class HippoSupportDetailView implements SupportDetailPresenter, HippoSupportDetailInter.OnFinishedListener {

    private Activity activity;
    private SupportDetailView supportData;
    private HippoSupportDetailInter hippoSupportDetailInter;

    public HippoSupportDetailView(Activity activity, SupportDetailView supportData, HippoSupportDetailInter hippoSupportDetailInter) {
        this.activity = activity;
        this.supportData = supportData;
        this.hippoSupportDetailInter = hippoSupportDetailInter;
    }

    @Override
    public void sendQuery(SendQueryChat queryChat) {
        switch (queryChat.getType()) {
            case QUERY:
                hippoSupportDetailInter.getSupportData(activity, queryChat, this);
                break;
            case CHAT:
                OpenChatParams chatParams = new CommonSupportParam()
                        .getOpenChatParam(queryChat.getCategory().getCategoryName(), queryChat.getTransactionId(),
                                queryChat.getUserUniqueId(), queryChat.getSupportId(), queryChat.getPathList(), queryChat.getSubHeader());

                if(supportData != null)
                    supportData.openChat(chatParams);
                break;
            default:

                break;
        }

    }

    @Override
    public void onDestroy() {
        if(supportData != null)
            supportData = null;
    }

    @Override
    public void onSuccess() {
        if(supportData != null)
            supportData.sucessfull();
    }

    @Override
    public void onFailure() {
        if(supportData != null)
            supportData.showError();
    }


}

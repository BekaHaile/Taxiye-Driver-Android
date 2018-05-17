package com.fugu.support.logicImplView;

import android.app.Activity;
import android.widget.Toast;

import com.fugu.model.FuguCreateConversationParams;
import com.fugu.retrofit.APIError;
import com.fugu.retrofit.ResponseResolver;
import com.fugu.retrofit.RestClient;
import com.fugu.support.Utils.CommonSupportParam;
import com.fugu.support.callback.HippoSupportDetailInter;
import com.fugu.support.model.Category;
import com.fugu.support.model.SupportTicketResponse;
import com.fugu.support.model.callbackModel.SendQueryChat;

import java.util.ArrayList;

/**
 * Created by Gurmail S. Kang on 03/04/18.
 * @author gurmail
 */

public class HippoSupportDetailInterImpl implements HippoSupportDetailInter {

    private OnFinishedListener onFinishedListener;

    @Override
    public void getSupportData(Activity activity, SendQueryChat queryChat, OnFinishedListener onFinishedListener) {
        this.onFinishedListener = onFinishedListener;
        createChat(activity, queryChat.getCategory(), queryChat.getTransactionId(), queryChat.getUserUniqueId(),
                queryChat.getSupportId(), queryChat.getPathList(), queryChat.getTextboxMsg(),
                queryChat.getSuccessMessage());
    }

//    @Override
//    public void getSupportData(Activity activity, Category category, String transactionId,
//                               String userUniqueId, int supportId, ArrayList<String> pathList,
//                               String textboxMsg, String successMsg, OnFinishedListener onFinishedListener) {
//        this.onFinishedListener = onFinishedListener;
//        createChat(activity, category, transactionId, userUniqueId, supportId, pathList, textboxMsg, successMsg);
//    }


    private void createChat(final Activity activity, Category category, String transactionId,
                            String userUniqueId, int supportId, ArrayList<String> pathList,
                            String textboxMsg, final String successMsg) {
        FuguCreateConversationParams submitQueryParams = new CommonSupportParam().getSubmitQueryParams(category.getCategoryName(),
                transactionId, userUniqueId, supportId, pathList, textboxMsg, "");

        RestClient.getApiInterface().createTicket(submitQueryParams)
                .enqueue(new ResponseResolver<SupportTicketResponse>(activity, true, false) {

                    @Override
                    public void success(SupportTicketResponse fuguCreateConversationResponse) {
                        Toast.makeText(activity, successMsg, Toast.LENGTH_SHORT).show();
                        onFinishedListener.onSuccess();
                    }

                    @Override
                    public void failure(APIError error) {
                        onFinishedListener.onFailure();
                    }
                });
    }
}

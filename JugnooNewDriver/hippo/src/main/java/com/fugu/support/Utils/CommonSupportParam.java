package com.fugu.support.Utils;

import android.text.TextUtils;

import com.fugu.HippoConfig;
import com.fugu.model.CustomAttributes;
import com.fugu.model.FuguCreateConversationParams;
import com.fugu.support.model.callbackModel.OpenChatParams;
import com.fugu.utils.FuguLog;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.util.ArrayList;

/**
 * Created by Gurmail S. Kang on 06/04/18.
 * @author gurmail
 */

public class CommonSupportParam {

    public CommonSupportParam() {

    }

    /**
     * Used to open chat screens
     * @param categoryName
     * @param transactionId
     * @param userUniqueId
     * @param supportId
     * @param pathList
     * @return
     */
    public OpenChatParams getOpenChatParam(String categoryName, String transactionId, String userUniqueId,
                                           int supportId, ArrayList<String> pathList, String subHeader) {
        String channelName = getChannelName(pathList.get(pathList.size() - 1), userUniqueId, supportId, transactionId);
        String message = getMessage(userUniqueId, transactionId, categoryName, pathList.get(pathList.size() - 1),
                null, subHeader);

        if(!TextUtils.isEmpty(transactionId)) {
            transactionId = transactionId + "_" + supportId;
        } else {
            transactionId = userUniqueId + "_" + supportId;
        }

        String data[] = new String[1];
        data[0] = message;

        ArrayList<String> tagsList = new ArrayList<>();
        tagsList.add(categoryName);

        CustomAttributes attributes = getAttributes(pathList, userUniqueId, transactionId);

        return new OpenChatParams(transactionId, userUniqueId, channelName, tagsList, data, attributes);
    }



    /**
     * Use for post message through server api
     * @param categoryName
     * @param transactionId
     * @param userUniqueId
     * @param supportId
     * @param pathList
     * @param textboxMsg
     * @return
     */
    public FuguCreateConversationParams getSubmitQueryParams(String categoryName, String transactionId,
                                                             String userUniqueId, int supportId, ArrayList<String> pathList, String textboxMsg, String subHeader) {
        String channelName = getChannelName(pathList.get(pathList.size() - 1), userUniqueId, supportId, transactionId);
        String message = getMessage(userUniqueId, transactionId, categoryName, pathList.get(pathList.size() - 1),
                textboxMsg, subHeader);

        if(!TextUtils.isEmpty(transactionId)) {
            transactionId = transactionId + "_" + supportId;
        } else {
            transactionId = userUniqueId + "_" + supportId;
        }

        String msg[] = new String[1];
        msg[0] = message;
        JsonArray tagsArray = null;

        ArrayList<String> tags = new ArrayList<>();
        tags.add(categoryName);

        tagsArray = new Gson().toJsonTree(tags).getAsJsonArray();

        FuguCreateConversationParams params = new FuguCreateConversationParams(HippoConfig.getInstance().getAppKey(),
                -1l,
                transactionId,
                HippoConfig.getInstance().getUserData().getUserId(),
                channelName,
                tagsArray,
                msg,
                HippoConfig.getInstance().getUserData().getEnUserId(), 1);

        CustomAttributes attributes = getAttributes(pathList, userUniqueId, transactionId);
        params.setCustomAttributes(attributes);

        return params;

    }


    /**
     * Channel name of a particular ticket
     * @param pageTitle
     * @param userUniqueId
     * @param supportId
     * @param transactionId
     * @return
     */
    private String getChannelName(String pageTitle, String userUniqueId, int supportId, String transactionId) {
        String channelName = pageTitle + " #"+userUniqueId + "_" + supportId;
        if(!TextUtils.isEmpty(transactionId))
            channelName = pageTitle + " #"+transactionId;
        return channelName;
    }

    /**
     * Message to be send
     * @param userUniqueId
     * @param transactionId
     * @param categoryName
     * @param pageTitle
     * @param edittextBoxText
     * @param subHeader
     * @return
     */
    private String getMessage(String userUniqueId, String transactionId, String categoryName, String pageTitle,
                              String edittextBoxText, String subHeader) {
        String message = "";

        message = "[User ID: "+userUniqueId+"]";
        if(!TextUtils.isEmpty(transactionId)) {
            message = message + "\n[Transaction ID: " + transactionId+"]";
        }
        message = message + "\n[Request type: "+categoryName+"]";
        message = message + "\n[Request] ";
        if(!TextUtils.isEmpty(edittextBoxText))
            message = message + edittextBoxText;
        else if(!TextUtils.isEmpty(subHeader))
            message = message + pageTitle + "->" + subHeader;

        return message;
    }

    /**
     * Create Attributes for agent
     * @param pathList
     * @param userUniqueId
     * @param transactionId
     * @return
     */
    private CustomAttributes getAttributes(ArrayList<String> pathList, String userUniqueId, String transactionId) {
        String attributesPath = "";
        for(String str : pathList) {
            if(TextUtils.isEmpty(attributesPath)) {
                attributesPath = attributesPath + str;
            } else {
                attributesPath = attributesPath + " -> " + str;
            }
        }

        CustomAttributes attributes = new CustomAttributes();
        attributes.setPath(attributesPath);
        attributes.setUserId(userUniqueId);
        if(!TextUtils.isEmpty(transactionId)) {
            attributes.setTransactionId(transactionId);
        }
        return attributes;
    }


}

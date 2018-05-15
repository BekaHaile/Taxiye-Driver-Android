package com.fugu.model;

import org.json.JSONArray;

import java.util.ArrayList;

/**
 * Created by bhavya on 22/08/17.
 */

/*
*transaction_id, user_unique_key and other_user_unique_key and chat_type = 1
 */
public class FuguPeerChatParams {
    public String getTransactionId() {
        return transactionId;
    }

    public String getUserUniqueKey() {
        return userUniqueKey;
    }

    public int getChatType() {
        return chatType;
    }

    public ArrayList<String> getOtherUserUniqueKeys() {
        return otherUserUniqueKeys;
    }

    private String transactionId = "";
    private String userUniqueKey = "";
    private int chatType = 1;
    private ArrayList<String> otherUserUniqueKeys = new ArrayList<>();

    public FuguPeerChatParams(String transactionId, String userUniqueKey, ArrayList<String> otherUserUniqueKeys) {
        this.transactionId = transactionId;
        this.userUniqueKey = userUniqueKey;
        this.otherUserUniqueKeys = otherUserUniqueKeys;

        if (otherUserUniqueKeys.size() > 1) {
            this.chatType = 2;
        }
    }
}

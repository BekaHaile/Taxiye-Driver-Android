package com.fugu.support.callback;

import com.fugu.support.model.Category;
import com.fugu.support.model.callbackModel.SendQueryChat;

import java.util.ArrayList;

/**
 * Created by gurmail on 29/03/18.
 */

public interface SupportPresenter {

    void fetchData(String defaultFaqName, int serverDBVersion);

//    @Deprecated
//    void openChat(String categoryName, String transactionId, String userUniqueId, int supportId, ArrayList<String> pathList);

    void openChat(SendQueryChat queryChat);

    void onDestroy();

}

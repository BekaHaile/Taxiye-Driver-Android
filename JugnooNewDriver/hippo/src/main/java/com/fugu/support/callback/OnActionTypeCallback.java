package com.fugu.support.callback;

import com.fugu.support.model.Item;

import java.util.ArrayList;

/**
 * Created by gurmail on 30/03/18.
 */

public interface OnActionTypeCallback {

    void onActionType(ArrayList<Item> items, String path, String title, String transactionId, String categoryDate, ArrayList<String> mTags);

    void openDetailPage(Item items, String path, String transactionId, String categoryDate, ArrayList<String> mTags);

    void removeFragment();
}

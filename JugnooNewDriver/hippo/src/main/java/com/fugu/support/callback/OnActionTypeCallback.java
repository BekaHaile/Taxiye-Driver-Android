package com.fugu.support.callback;

import com.fugu.support.model.Item;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gurmail on 30/03/18.
 */

public interface OnActionTypeCallback {

    void onActionType(ArrayList<Item> items, String path, String title, String transactionId, String categoryDate);

    void openDetailPage(Item items, String path, String transactionId, String categoryDate);

    void removeFragment();
}

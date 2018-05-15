package com.fugu.support.callback;

import android.app.Activity;

import com.fugu.support.model.Category;
import com.fugu.support.model.callbackModel.SendQueryChat;

import java.util.ArrayList;

/**
 * Created by Gurmail S. Kang on 03/04/18.
 * @author gurmail
 */

public interface HippoSupportDetailInter {

    interface OnFinishedListener {

        void onSuccess();

        void onFailure();
    }

    void getSupportData(Activity activity, SendQueryChat queryChat, HippoSupportDetailInter.OnFinishedListener onFinishedListener);

}

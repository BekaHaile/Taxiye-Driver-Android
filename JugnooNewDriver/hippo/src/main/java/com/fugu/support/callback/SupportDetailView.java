package com.fugu.support.callback;

import com.fugu.support.model.callbackModel.OpenChatParams;

import java.util.ArrayList;

/**
 * Created by Gurmail S. Kang on 03/04/18.
 * @author gurmail
 */

public interface SupportDetailView extends BaseInterface {

    void openChat(OpenChatParams chatParams);

    void sucessfull();

    void showError();

}

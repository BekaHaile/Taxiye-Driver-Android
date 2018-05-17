package com.fugu.model;

import com.fugu.R;
import com.fugu.adapter.FuguAttachmentAdapter;

/**
 * Created by bhavya on 16/08/17.
 */

public class FuguAttachmentModel {
    private int imageIcon;
    private String text;
    private int action;

    public FuguAttachmentModel(int imageIcon, String text, int action) {
        this.imageIcon = imageIcon;
        this.text = text;
        this.action = action;
    }

    public int getImageIcon() {
        return imageIcon;
    }

    public String getText() {
        return text;
    }

    public int getAction() {
        return action;
    }
}

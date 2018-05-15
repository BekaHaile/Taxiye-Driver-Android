package com.fugu.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by cl-macmini-01 on 12/15/17.
 */

public class CustomAction {

    @SerializedName("title")
    private String title;

    @SerializedName("title_description")
    private String titleDescription;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("description")
    private ArrayList<DescriptionObject> descriptionObjects;

    @SerializedName("action_buttons")
    private ArrayList<ActionButtonModel> actionButtons;

    public String getTitleDescription() {
        return titleDescription;
    }

    public void setTitleDescription(final String titleDescription) {
        this.titleDescription = titleDescription;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public ArrayList<DescriptionObject> getDescriptionObjects() {
        return descriptionObjects;
    }

    public ArrayList<ActionButtonModel> getActionButtons() {
        return actionButtons;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public void setImageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDescriptionObjects(final ArrayList<DescriptionObject> descriptionObjects) {
        this.descriptionObjects = descriptionObjects;
    }

    public void setActionButtons(final ArrayList<ActionButtonModel> actionButtons) {
        this.actionButtons = actionButtons;
    }
}

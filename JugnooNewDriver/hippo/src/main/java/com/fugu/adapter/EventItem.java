package com.fugu.adapter;

import android.support.annotation.NonNull;

import com.fugu.model.Message;

/**
 * Created by Bhavya Rattan on 29/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class EventItem extends ListItem {

    @NonNull
    private Message event;

    public EventItem(@NonNull Message event) {
        this.event = event;
    }

    @NonNull
    public Message getEvent() {
        return event;
    }

    // here getters and setters
    // for title and so on, built
    // using event

    @Override
    public int getType() {
        if (event.isSelf())
            return ITEM_TYPE_SELF;
        else
            return ITEM_TYPE_OTHER;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof EventItem){
            Message message = ((EventItem)obj).getEvent();
            if(message.getId() != null && getEvent().getId() != null){
                return message.getId().equals(getEvent().getId());
            }
        }
        return false;
    }
}

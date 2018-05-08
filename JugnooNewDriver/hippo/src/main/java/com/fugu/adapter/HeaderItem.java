package com.fugu.adapter;

import android.support.annotation.NonNull;

/**
 * Created by Bhavya Rattan on 29/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class HeaderItem extends ListItem {

    @NonNull
    private String date;

    public HeaderItem(@NonNull String date) {
        this.date = date;
    }

    @NonNull
    public String getDate() {
        return date;
    }

    // here getters and setters
    // for title and so on, built
    // using date

    @Override
    public int getType() {
        return TYPE_HEADER;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }
}

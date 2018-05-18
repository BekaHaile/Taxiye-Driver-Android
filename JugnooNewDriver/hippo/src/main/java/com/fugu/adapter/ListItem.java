package com.fugu.adapter;

/**
 * Created by Bhavya Rattan on 29/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public abstract class ListItem {

    public static final int TYPE_HEADER = 0;
    public static final int ITEM_TYPE_OTHER = 1;
    public static final int ITEM_TYPE_SELF = 2;

    abstract public int getType();

    public abstract boolean equals(Object obj);
}
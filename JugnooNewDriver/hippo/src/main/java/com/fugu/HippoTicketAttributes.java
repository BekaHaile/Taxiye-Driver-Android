package com.fugu;

import java.util.ArrayList;

/**
 * Created by gurmail on 04/04/18.
 */

public class HippoTicketAttributes {

    private String mFaqName;
    private String mTransactionId;
    private ArrayList<String> mTags;

    public ArrayList<String> getmTags() {
        return mTags;
    }

    public String getmFaqName() {
        return mFaqName;
    }
    public String getmTransactionId() {
        return mTransactionId;
    }


    public static class Builder {

        private String mFaqName;
        private String mTransactionId;
        private ArrayList<String> mTags;

        public Builder setFaqName(String mFaqName) {
            this.mFaqName = mFaqName;
            return this;
        }

        public Builder setTransactionId(String mTransactionId) {
            this.mTransactionId = mTransactionId;
            return this;
        }

        public Builder setTags(ArrayList<String> mTags) {
            this.mTags = mTags;
            return this;
        }

        public HippoTicketAttributes build() {
            return new HippoTicketAttributes(this);
        }
    }

    private HippoTicketAttributes(Builder builder) {
        this.mFaqName = builder.mFaqName;
        this.mTransactionId = builder.mTransactionId;
        this.mTags = builder.mTags;
    }
}

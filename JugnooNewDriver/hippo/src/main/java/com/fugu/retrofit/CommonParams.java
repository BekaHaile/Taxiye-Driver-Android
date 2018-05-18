package com.fugu.retrofit;

import java.util.HashMap;

/**
 * Created by cl-macmini-33 on 27/09/16.
 */

public class CommonParams {
    HashMap<String, Object> map = new HashMap<>();

    private CommonParams(Builder builder) {
        builder.map.put("source", String.valueOf(1));
        this.map = builder.map;
    }

    public HashMap<String, Object> getMap() {
        return map;
    }


    public static class Builder {
        HashMap<String, Object> map = new HashMap<>();

        public Builder() {
        }

        public Builder add(String key, Object value) {
            map.put(key, String.valueOf(value));
            return this;
        }

        /**
         * Temp fix for future use of common key inside CommonParams constr.
         * @param map
         * @return
         */
        public Builder putMap(HashMap<String, Object> map) {
            this.map = map;
            return this;
        }

        public CommonParams build() {
            return new CommonParams(this);
        }
    }
}



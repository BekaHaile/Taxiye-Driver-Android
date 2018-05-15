package com.fugu;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by cl-macmini-01 on 2/5/18.
 */

public class FuguFontConfig {

    private String fuguNormalTextFontPath;
    private String fuguHeaderTitleFontPath;

    public Typeface getNormalTextTypeFace(Context context){
        if(fuguNormalTextFontPath!=null && !fuguNormalTextFontPath.equalsIgnoreCase("")){
            return Typeface.createFromAsset(context.getAssets(),fuguNormalTextFontPath);
        }
        return Typeface.DEFAULT;
    }

    public Typeface getHeaderTitleTypeFace(Context context){
        if(fuguHeaderTitleFontPath!=null && !fuguHeaderTitleFontPath.equalsIgnoreCase("")){
            return Typeface.createFromAsset(context.getAssets(),fuguHeaderTitleFontPath);
        }
        return Typeface.DEFAULT;
    }

    public static class Builder{

        FuguFontConfig fuguFontConfig = new FuguFontConfig();

        public Builder setNormaTextFontPath(String path){
            fuguFontConfig.fuguNormalTextFontPath = path;
            return this;
        }

        public Builder setHeaderTitleFontPath(String path){
            fuguFontConfig.fuguHeaderTitleFontPath = path;
            return this;
        }

        public FuguFontConfig build() {
            return fuguFontConfig;
        }

    }
}

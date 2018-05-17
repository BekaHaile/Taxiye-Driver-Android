package com.fugu;

import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;

/**
 * Created by bhavya on 01/08/17.
 */

public class HippoColorConfig {

    public int getHippoActionBarBg() {
        return Color.parseColor(hippoActionBarBg);
    }

    public int getHippoActionBarText() {
        return Color.parseColor(hippoActionBarText);
    }

    public int getHippoBgMessageYou() {
        return Color.parseColor(hippoBgMessageYou);
    }

    public int getHippoBgMessageFrom() {
        return Color.parseColor(hippoBgMessageFrom);
    }

    public int getHippoPrimaryTextMsgYou() {
        return Color.parseColor(hippoPrimaryTextMsgYou);
    }

    public int getHippoMessageRead() {
        return Color.parseColor(hippoMessageRead);
    }

    public int getHippoPrimaryTextMsgFrom() {
        return Color.parseColor(hippoPrimaryTextMsgFrom);
    }

    public int getHippoSecondaryTextMsgYou() {
        return Color.parseColor(hippoSecondaryTextMsgYou);
    }

    public int getHippoSecondaryTextMsgFrom() {
        return Color.parseColor(hippoSecondaryTextMsgFrom);
    }

    public int getHippoSecondaryTextMsgFromName() {
        return Color.parseColor(hippoSecondaryTextMsgFromName);
    }

    public int getHippoTextColorPrimary() {
        return Color.parseColor(hippoTextColorPrimary);
    }

    public int getHippoChannelDateText() {
        return Color.parseColor(hippoChannelDateText);
    }

    public int getHippoChatBg() {
        return Color.parseColor(hippoChatBg);
    }

    public int getHippoBorderColor() {
        return Color.parseColor(hippoBorderColor);
    }

    public int getHippoChatDateText() {
        return Color.parseColor(hippoChatDateText);
    }

    public int getHippoThemeColorPrimary() {
        return Color.parseColor(hippoThemeColorPrimary);
    }

    public int getHippoThemeColorSecondary() {
        return Color.parseColor(hippoThemeColorSecondary);
    }

    public int getHippoTypeMessageBg() {
        return Color.parseColor(hippoTypeMessageBg);
    }

    public int getHippoTypeMessageHint() {
        return Color.parseColor(hippoTypeMessageHint);
    }

    public int getHippoTypeMessageText() {
        return Color.parseColor(hippoTypeMessageText);
    }

    public int getHippoChannelBg() {
        return Color.parseColor(hippoChannelBg);
    }

    public int getHippoChannelItemBgPressed() {
        return Color.parseColor(hippoChannelItemBgPressed);
    }

    public int getHippoChannelItemBg() {
        return Color.parseColor(hippoChannelItemBg);
    }

    public int getHippoFaqDescription() {
        return Color.parseColor(hippoFaqDescription);
    }

    private String hippoActionBarBg = "#627de3";
    private String hippoActionBarText = "#ffffff";
    private String hippoBgMessageYou = "#ffffff";
    private String hippoBgMessageFrom = "#e8ecfc";
    private String hippoPrimaryTextMsgYou = "#2c2333";
    private String hippoMessageRead = "#627de3";
    private String hippoPrimaryTextMsgFrom = "#2c2333";
    private String hippoSecondaryTextMsgYou = "#8e8e8e";
    private String hippoSecondaryTextMsgFrom = "#8e8e8e";

    private String hippoSecondaryTextMsgFromName = "#627de3";
    private String hippoTextColorPrimary = "#2c2333";
    private String hippoTextColorSecondary = "#2c2333";
    private String hippoChannelDateText = "#88838c";
    private String hippoChatBg = "#f8f9ff";
    private String hippoBorderColor = "#dce0e6";
    private String hippoChatDateText = "#51445c";
    private String hippoThemeColorPrimary = "#627de3";
    private String hippoThemeColorSecondary = "#6cc64d";
    private String hippoTypeMessageBg = "#ffffff";
    private String hippoTypeMessageHint = "#8e8e8e";
    private String hippoTypeMessageText = "#2c2333";
    private String hippoChannelBg = "#ffffff";
    private String hippoChannelItemBg = "#ffffff";
    private String hippoChannelItemBgPressed = "#ffd2d1d1";
    private String hippoFaqDescription = "#858585";

    public static class Builder {
        private HippoColorConfig hippoColorConfig = new HippoColorConfig();

        public Builder hippoActionBarBg(String hippoActionBarBg) {
            hippoColorConfig.hippoActionBarBg = hippoActionBarBg;
            return this;
        }

        public Builder hippoActionBarText(String hippoActionBarText) {
            hippoColorConfig.hippoActionBarText = hippoActionBarText;
            return this;
        }

        public Builder hippoBgMessageYou(String hippoBgMessageYou) {
            hippoColorConfig.hippoBgMessageYou = hippoBgMessageYou;
            return this;
        }

        public Builder hippoBgMessageFrom(String fuguBgMessageFrom) {
            hippoColorConfig.hippoBgMessageFrom = fuguBgMessageFrom;
            return this;
        }

        public Builder hippoPrimaryTextMsgYou(String hippoPrimaryTextMsgYou) {
            hippoColorConfig.hippoPrimaryTextMsgYou = hippoPrimaryTextMsgYou;
            return this;
        }

        public Builder hippoMessageRead(String hippoMessageRead) {
            hippoColorConfig.hippoMessageRead = hippoMessageRead;
            return this;
        }

        public Builder hippoPrimaryTextMsgFrom(String hippoPrimaryTextMsgFrom) {
            hippoColorConfig.hippoPrimaryTextMsgFrom = hippoPrimaryTextMsgFrom;
            return this;
        }

        public Builder hippoSecondaryTextMsgYou(String hippoSecondaryTextMsgYou) {
            hippoColorConfig.hippoSecondaryTextMsgYou = hippoSecondaryTextMsgYou;
            return this;
        }

        public Builder hippoSecondaryTextMsgFrom(String hippoSecondaryTextMsgFrom) {
            hippoColorConfig.hippoSecondaryTextMsgFrom = hippoSecondaryTextMsgFrom;
            return this;
        }

        public Builder hippoSecondaryTextMsgFromName(String hippoSecondaryTextMsgFromName) {
            hippoColorConfig.hippoSecondaryTextMsgFromName = hippoSecondaryTextMsgFromName;
            return this;
        }

        public Builder hippoTextColorPrimary(String hippoTextColorPrimary) {
            hippoColorConfig.hippoTextColorPrimary = hippoTextColorPrimary;
            return this;
        }

        public Builder hippoTextColorSecondary(String hippoTextColorPrimary) {
            hippoColorConfig.hippoTextColorPrimary = hippoTextColorPrimary;
            return this;
        }

        public Builder hippoChannelDateText(String hippoChannelDateText) {
            hippoColorConfig.hippoChannelDateText = hippoChannelDateText;
            return this;
        }

        public Builder hippoChatBg(String hippoChatBg) {
            hippoColorConfig.hippoChatBg = hippoChatBg;
            return this;
        }

        public Builder hippoBorderColor(String hippoBorderColor) {
            hippoColorConfig.hippoBorderColor = hippoBorderColor;
            return this;
        }

        public Builder hippoChatDateText(String hippoChatDateText) {
            hippoColorConfig.hippoChatDateText = hippoChatDateText;
            return this;
        }

        public Builder hippoThemeColorPrimary(String hippoThemeColorPrimary) {
            hippoColorConfig.hippoThemeColorPrimary = hippoThemeColorPrimary;
            return this;
        }

        public Builder hippoThemeColorSecondary(String hippoThemeColorSecondary) {
            hippoColorConfig.hippoThemeColorSecondary = hippoThemeColorSecondary;
            return this;
        }

        public Builder hippoTypeMessageBg(String hippoTypeMessageBg) {
            hippoColorConfig.hippoTypeMessageBg = hippoTypeMessageBg;
            return this;
        }

        public Builder hippoTypeMessageHint(String hippoTypeMessageHint) {
            hippoColorConfig.hippoTypeMessageHint = hippoTypeMessageHint;
            return this;
        }

        public Builder hippoTypeMessageText(String hippoTypeMessageText) {
            hippoColorConfig.hippoTypeMessageText = hippoTypeMessageText;
            return this;
        }

        public Builder hippoChannelBg(String hippoChannelBg) {
            hippoColorConfig.hippoChannelBg = hippoChannelBg;
            return this;
        }

        public Builder hippoChannelItemBgPressed(String hippoChannelItemBgPressed) {
            hippoColorConfig.hippoChannelItemBgPressed = hippoChannelItemBgPressed;
            return this;
        }

        public Builder hippoChannelItemBg(String hippoChannelItemBg) {
            hippoColorConfig.hippoChannelItemBg = hippoChannelItemBg;
            return this;
        }

        public Builder hippoFaqDescription(String hippoFaqDescription) {
            hippoColorConfig.hippoFaqDescription = hippoFaqDescription;
            return this;
        }

        public HippoColorConfig build() {
            return hippoColorConfig;
        }

    }

    public static StateListDrawable makeSelector(int color, int colorPressed) {
        StateListDrawable res = new StateListDrawable();
        // res.setExitFadeDuration(400);
        //res.setAlpha(230);
        res.addState(new int[]{android.R.attr.state_pressed}, roundedBackground(0, colorPressed, false));
        res.addState(new int[]{}, roundedBackground(0, color, false));
        return res;
    }

    public static StateListDrawable makeRoundedSelector(int color) {
        return makeRoundedSelector(color, 150);
    }
    public static StateListDrawable makeRoundedSelector(int color, float radius) {
        StateListDrawable res = new StateListDrawable();
        // res.setExitFadeDuration(400);
        //res.setAlpha(230);
        res.addState(new int[]{android.R.attr.state_pressed}, roundedBackground(radius, color, true));
        res.addState(new int[]{}, roundedBackground(radius, color, false));
        return res;
    }

    private static ShapeDrawable roundedBackground(float radius, int color, boolean isPressed) {
        ShapeDrawable footerBackground = new ShapeDrawable();

        // The corners are ordered top-left, top-right, bottom-right,
        // bottom-left. For each corner, the array contains 2 values, [X_radius,
        // Y_radius]

        float[] radii = new float[8];
        radii[0] = radius;
        radii[1] = radius;

        radii[2] = radius;
        radii[3] = radius;

        radii[4] = radius;
        radii[5] = radius;

        radii[6] = radius;
        radii[7] = radius;

        footerBackground.setShape(new RoundRectShape(radii, null, null));

        footerBackground.getPaint().setColor(color);
        if (isPressed)
            footerBackground.setAlpha(250);

        return footerBackground;
    }

}

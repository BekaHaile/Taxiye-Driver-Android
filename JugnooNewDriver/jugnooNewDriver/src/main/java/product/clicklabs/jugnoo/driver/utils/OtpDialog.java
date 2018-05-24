package product.clicklabs.jugnoo.driver.utils;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.R;

/**
 * Created by gurmail on 2/3/17.
 */

public class OtpDialog {

    private static final String TAG = OtpDialog.class.getSimpleName();

    private Dialog alertDialog;

    /**
     * The instance of the Activity on which the
     * AlertDialog will be displayed
     */
    private Activity activity;

    /**
     * The receiver to which the AlertDialog
     * will return the CallBacks
     * <p/>
     * Note: listener should be an instance of
     * AlertDialog.Listener
     */
    private Listener listener;

    /**
     * The code to differentiate the various CallBacks
     * from different Dialogs
     */
    private int purpose;

    /**
     * The counterTime to be set on the Dialog
     */
    private String title;

    /**
     * The message to be set on the Dialog
     */
    private String message;

    /**
     * The text to be set on the Action Button
     */
    private String actionButton;

    /**
     * Is number don't exist then hide call button and respective views
     */
    private boolean isNumberNotExist;

    /**
     * The data to sent via the Dialog from the
     * remote parts of the Activity to other
     * parts
     */
    private Bundle backpack;
    private TextView textViewCounter;
    private ImageView imageViewYellowLoadingBar;
    private LinearLayout linearLayoutWaiting, llMissedCall;

    /**
     * Method to create and display the alert alertDialog
     *
     * @return
     */
    private OtpDialog init() {

        try {
            dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            alertDialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
            alertDialog.setContentView(R.layout.dialog_autodetect);

            Window dialogWindow = alertDialog.getWindow();
            WindowManager.LayoutParams layoutParams = dialogWindow.getAttributes();
            layoutParams.dimAmount = 0.7f;

            dialogWindow.getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;

            dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(false);

            RelativeLayout frameLayout = (RelativeLayout) alertDialog.findViewById(R.id.rv);
            new ASSL(activity, frameLayout, 1134, 720, false);

            linearLayoutWaiting = (LinearLayout) alertDialog.findViewById(R.id.linearLayoutWaiting);
            imageViewYellowLoadingBar = (ImageView) alertDialog.findViewById(R.id.imageViewYellowLoadingBar);
            TextView textViewWaiting = (TextView) alertDialog.findViewById(R.id.textViewWaiting);
            textViewCounter = (TextView) alertDialog.findViewById(R.id.textViewCounter);

            llMissedCall = (LinearLayout) alertDialog.findViewById(R.id.llMissedCall);
            TextView automaticOtpFailed = (TextView) alertDialog.findViewById(R.id.automaticOtpFailed);
            TextView giveMissedCall = (TextView) alertDialog.findViewById(R.id.giveMissedCall);
            TextView textViewOr = (TextView) alertDialog.findViewById(R.id.textViewOr);
            ImageView callView = (ImageView) alertDialog.findViewById(R.id.callView);
            Button enterOtpButton = (Button) alertDialog.findViewById(R.id.enterOtpButton);

            textViewWaiting.setTypeface(Fonts.mavenRegular(activity));
            textViewCounter.setTypeface(Fonts.mavenRegular(activity));

            automaticOtpFailed.setTypeface(Fonts.mavenRegular(activity));
            giveMissedCall.setTypeface(Fonts.mavenRegular(activity));
            textViewOr.setTypeface(Fonts.mavenRegular(activity));
            enterOtpButton.setTypeface(Fonts.mavenRegular(activity));


            if (isNumberNotExist) {
                giveMissedCall.setVisibility(View.GONE);
                textViewOr.setVisibility(View.GONE);
                callView.setVisibility(View.GONE);
            }

            llMissedCall.setVisibility(View.GONE);

            callView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    alertDialog.dismiss();

                    if (listener != null)
                        listener.performPostAlertAction(purpose, backpack);
                }
            });

            enterOtpButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                    if (listener != null)
                        listener.performPostAlertAction(AppConstants.OperationType.ENTER_OTP, backpack);
                }
            });

        } catch (Exception e) {

            e.printStackTrace();
        }

        return this;
    }

    /**
     * Method to init the initialized alertDialog
     */
    public void show() {

        // Check if activity lives
        if (activity != null)
            // Check if alertDialog contains data
            if (alertDialog != null) {
                try {
                    // Show the Dialog
                    alertDialog.show();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
    }

    /**
     * Method to dismiss the AlertDialog, if possible
     */
    public void dismiss() {

        // Check if activity lives
        if (activity != null)
            // Check if the Dialog is null
            if (alertDialog != null)
                // Check whether the alertDialog is visible
                if (alertDialog.isShowing()) {
                    try {
                        // Dismiss the Dialog
                        alertDialog.dismiss();
                        alertDialog = null;
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
    }

    public boolean isShown() {
        if (activity != null && alertDialog != null && alertDialog.isShowing())
            return true;
        else
            return false;

    }

    public void updateCounterView(String text, double widthToSet) {
        // Check if activity lives
        if (activity != null) {
            // Check if the Dialog is null
            if (alertDialog != null && alertDialog.isShowing()) {
                // Check whether the alertDialog is visible
                if (textViewCounter != null)
                    textViewCounter.setText(text);

                if (imageViewYellowLoadingBar != null) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageViewYellowLoadingBar.getLayoutParams();
                    params.width = (int) widthToSet;
                    imageViewYellowLoadingBar.setLayoutParams(params);
                }
            }
        }
    }

    public void swictchLayout() {
        llMissedCall.setVisibility(View.VISIBLE);
        linearLayoutWaiting.setVisibility(View.GONE);
    }


    /**
     * Interfaces the events from the AlertDialog
     * to the Calling Context
     */
    public interface Listener {

        /**
         * Override this method to listen to
         * the events fired from AlertDialog
         *
         * @param purpose
         * @param backpack
         */
        void performPostAlertAction(int purpose, Bundle backpack);
    }

    /**
     * Class to help building the Alert Dialog
     */
    public static class Builder {

        private OtpDialog alertDialog = new OtpDialog();

        /**
         * Constructor to build a alertDialog for Activity
         *
         * @param activity
         */
        public Builder(Activity activity) {

            alertDialog.activity = activity;

            if (activity instanceof Listener)
                alertDialog.listener = (Listener) activity;
        }

        /**
         * Constructor to build a alertDialog for Fragment
         *
         * @param fragment
         */
        public Builder(Fragment fragment) {

            alertDialog.activity = fragment.getActivity();

            if (fragment instanceof Listener)
                alertDialog.listener = (Listener) fragment;
        }

        /**
         * Sets the a unique purpose code to differentiate
         * between the CallBacks
         *
         * @param purpose
         * @return
         */
        public Builder purpose(int purpose) {
            alertDialog.purpose = purpose;
            return this;
        }

        /**
         * Sets the a custom listener to receive the CallBacks
         *
         * @param listener
         * @return
         */
        public Builder listener(Listener listener) {
            alertDialog.listener = listener;
            return this;
        }

        /**
         * Set the data to be sent via callback
         *
         * @param backpack
         * @return
         */
        public Builder backpack(Bundle backpack) {
            alertDialog.backpack = backpack;
            return this;
        }

        /**
         * Set the message for the AlertDialog
         *
         * @param resourceId
         * @return
         */
        public Builder counterTime(int resourceId) {
            return counterTime(alertDialog.activity.getString(resourceId));
        }

        /**
         * Set the message for the AlertDialog
         *
         * @param title
         * @return
         */
        public Builder counterTime(String title) {
            alertDialog.title = title;
            return this;
        }

        public Builder isNumberExist(boolean flag) {
            alertDialog.isNumberNotExist = flag;
            return this;
        }

        /**
         * Set the message for the AlertDialog
         *
         * @param resourceId
         * @return
         */
        public Builder message(int resourceId) {
            return message(alertDialog.activity.getString(resourceId));
        }

        /**
         * Set the message for the AlertDialog
         *
         * @param message
         * @return
         */
        public Builder message(String message) {
            alertDialog.message = message;
            return this;
        }

        /**
         * Set the actionButton for the AlertDialog
         *
         * @param resourceId
         * @return
         */
        public Builder button(int resourceId) {
            return button(alertDialog.activity.getString(resourceId));
        }

        /**
         * Set the actionButton for the AlertDialog
         *
         * @param button
         * @return
         */
        public Builder button(String button) {
            alertDialog.actionButton = button;
            return this;
        }

        /**
         * Method to build an AlertDialog and display
         * it on the screen. The instance returned can
         * be used to manipulate the alertDialog in future.
         *
         * @return
         */
        public OtpDialog build() {

            return alertDialog.init();
        }

        /**
         * Method to retrieve a String Resource
         *
         * @param resourceId
         * @return
         */
        private String getString(int resourceId) {
            return alertDialog.activity.getString(resourceId);
        }
    }
}

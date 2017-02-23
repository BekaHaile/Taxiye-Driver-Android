package product.clicklabs.jugnoo.driver.utils;

import android.os.CountDownTimer;

/**
 * Created by gurmail on 2/3/17.
 */

public class CustomCountDownTimer extends CountDownTimer {

    private final long mMillisInFuture;
    private DownTimerOperation timerOperation;

    public CustomCountDownTimer(long millisInFuture, long countDownInterval, DownTimerOperation timerOperation) {
        super(millisInFuture, countDownInterval);
        mMillisInFuture = millisInFuture;
        this.timerOperation = timerOperation;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        double percent = (((double) millisUntilFinished) * 100.0) / mMillisInFuture;
        double widthToSet = percent * ((double) (ASSL.Xscale() * 530)) / 100.0;

        long seconds = (long) Math.ceil(((double) millisUntilFinished) / 1000.0d);
        String text = seconds < 10 ? "0:0" + seconds : "0:" + seconds;
        if (timerOperation != null)
            timerOperation.updateCounterView(text, widthToSet);
    }

    @Override
    public void onFinish() {
        if(timerOperation != null)
            timerOperation.swictchLayout();
    }

    public interface DownTimerOperation {
        void updateCounterView(String text, double width);
        void swictchLayout();
    }
}

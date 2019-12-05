package product.clicklabs.jugnoo.driver.listeners;

import android.view.GestureDetector;
import android.view.MotionEvent;

public class DirectionsGestureListener extends GestureDetector.SimpleOnGestureListener {

    private Callback callback;
    public DirectionsGestureListener(Callback callback){
        this.callback = callback;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        final float SWIPE_MIN_DISTANCE = 125.0f;
        if (Math.abs(e1.getY() - e2.getY()) < SWIPE_MIN_DISTANCE)
            return false;


        switch (getSlope(e1.getX(), e1.getY(), e2.getX(), e2.getY())) {
            case 1:
                callback.topSwipe();
                return true;
            case 2:
                callback.leftSwipe();
                return true;
            case 3:
                callback.bottomSwipe();
                return true;
            case 4:
                callback.rightSwipe();
                return true;
        }
        return false;
    }

    private int getSlope(float x1, float y1, float x2, float y2) {
        Double angle = Math.toDegrees(Math.atan2(y1 - y2, x2 - x1));
        if (angle > 45 && angle <= 135)
            // top
            return 1;
        if (angle >= 135 && angle < 180 || angle < -135 && angle > -180)
            // left
            return 2;
        if (angle < -45 && angle >= -135)
            // down
            return 3;
        if (angle > -45 && angle <= 45)
            // right
            return 4;
        return 0;
    }


    public interface Callback{
        void topSwipe();
        void bottomSwipe();
        void leftSwipe();
        void rightSwipe();
    }

}

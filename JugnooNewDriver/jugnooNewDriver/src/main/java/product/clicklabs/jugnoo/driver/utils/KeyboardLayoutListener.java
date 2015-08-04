package product.clicklabs.jugnoo.driver.utils;

import android.graphics.Rect;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

/**
 * Created by socomo20 on 6/30/15.
 */
public class KeyboardLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {

    ViewGroup activityRootView;
    TextView textViewScroll;
    KeyBoardStateHandler keyBoardStateHandler;

    public KeyboardLayoutListener(ViewGroup activityRootView, TextView textViewScroll, KeyBoardStateHandler keyBoardStateHandler) {
        this.activityRootView = activityRootView;
        this.textViewScroll = textViewScroll;
        this.keyBoardStateHandler = keyBoardStateHandler;
    }


    @Override
    public void onGlobalLayout() {
        Rect r = new Rect();
        // r will be populated with the coordinates of your view
        // that area still visible.
        activityRootView.getWindowVisibleDisplayFrame(r);

        int heightDiff = activityRootView.getRootView()
            .getHeight() - (r.bottom - r.top);
        if (heightDiff > 100) { // if more than 100 pixels, its
            // probably a keyboard...

            /************** Adapter for the parent List *************/

            ViewGroup.LayoutParams params_12 = textViewScroll
                .getLayoutParams();

            params_12.height = (int) (heightDiff);

            textViewScroll.setLayoutParams(params_12);
            textViewScroll.requestLayout();

            keyBoardStateHandler.keyboardOpened();

        } else {

            ViewGroup.LayoutParams params = textViewScroll
                .getLayoutParams();
            params.height = 0;
            textViewScroll.setLayoutParams(params);
            textViewScroll.requestLayout();

            keyBoardStateHandler.keyBoardClosed();

        }
    }

}

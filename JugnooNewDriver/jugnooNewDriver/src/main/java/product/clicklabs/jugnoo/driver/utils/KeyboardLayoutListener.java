package product.clicklabs.jugnoo.driver.utils;

import android.graphics.Rect;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

/**
 * Created by socomo20 on 6/30/15.
 */
public class KeyboardLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {

    private ViewGroup activityRootView;
    private TextView textViewScroll;
    private KeyBoardStateHandler keyBoardStateHandler;
    /**
     * 0 for closed and 1 for opened
     */
    private int keyBoardState;
    private boolean resizeTextView;

    public KeyboardLayoutListener(ViewGroup activityRootView, TextView textViewScroll, KeyBoardStateHandler keyBoardStateHandler) {
        this.activityRootView = activityRootView;
        this.textViewScroll = textViewScroll;
        this.keyBoardStateHandler = keyBoardStateHandler;
        this.keyBoardState = 0;
        this.resizeTextView = true;
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

            if(resizeTextView) {
                ViewGroup.LayoutParams params_12 = textViewScroll
                        .getLayoutParams();

                params_12.height = (int) (heightDiff);

                textViewScroll.setLayoutParams(params_12);
            }

            if(keyBoardState != 1){
                keyBoardStateHandler.keyboardOpened();
                keyBoardState = 1;
            }


        } else {

            ViewGroup.LayoutParams params = textViewScroll
                    .getLayoutParams();
            params.height = 0;
            textViewScroll.setLayoutParams(params);
            textViewScroll.requestLayout();

            if(keyBoardState == 1) {
                keyBoardStateHandler.keyBoardClosed();
                keyBoardState = 0;
            }

        }
    }

    public void setResizeTextView(boolean resizeTextView) {
        this.resizeTextView = resizeTextView;
        if(!resizeTextView){
            ViewGroup.LayoutParams params = textViewScroll
                    .getLayoutParams();
            params.height = 0;
            textViewScroll.setLayoutParams(params);
            textViewScroll.requestLayout();
        }
    }


    public interface KeyBoardStateHandler {
        void keyboardOpened();
        void keyBoardClosed();
    }

}
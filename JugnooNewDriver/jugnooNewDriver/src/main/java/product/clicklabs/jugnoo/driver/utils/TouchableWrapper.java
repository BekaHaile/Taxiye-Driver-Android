package product.clicklabs.jugnoo.driver.utils;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.FrameLayout;

public class TouchableWrapper extends FrameLayout {

    public TouchableWrapper(Context context) {
        super(context);
        gestureDetector = new GestureDetector(context, new GestureListener());
        
        twoFingerTapDetector = new TwoFingerTapDetector() {
            @Override
            public void onTwoFingerDoubleTap() {
            	onTouchListener.onTwoFingerDoubleTap();
            }
        };
        
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
        
    }

    public void setTouchListener(OnTouchListener onTouchListener) {
        this.onTouchListener = onTouchListener;
    }

    private OnTouchListener onTouchListener;
    private GestureDetector gestureDetector;
    private TwoFingerTapDetector twoFingerTapDetector;
    private ScaleGestureDetector scaleGestureDetector;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
    	gestureDetector.onTouchEvent(event);
    	twoFingerTapDetector.onTouchEvent(event);
    	scaleGestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onTouchListener.onTouch();
                break;
            case MotionEvent.ACTION_MOVE:
            	if(event.getPointerCount() >= 2){
            		return true;
            	}
            	else{
            		return super.dispatchTouchEvent(event);
            	}
            case MotionEvent.ACTION_UP:
                onTouchListener.onRelease();
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    public interface OnTouchListener {
        public void onTouch();
        public void onRelease();
        public void onDoubleTap();
        public void onTwoFingerDoubleTap();
        public void pinchIn();
        public void pinchOut();
    }
    
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
    	
        @Override
        public boolean onDoubleTap(MotionEvent e) {
        	onTouchListener.onDoubleTap();
            return true;
        }
        
    }
    
    class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
    	
    	private float factor = 1;
    	private float THRESHOLD = 0.000001f;
    	
    	@Override
    	public boolean onScale(ScaleGestureDetector detector) {
    		float scaleFactor = detector.getScaleFactor();
    		if(scaleFactor > 1){
    			//Zoom in
    			if(Math.abs(scaleFactor - factor) > THRESHOLD){
        			onTouchListener.pinchOut();
    			}
    		}
    		else{
    			//Zoom out
    			if(Math.abs(scaleFactor - factor) > THRESHOLD){
    				onTouchListener.pinchIn();
    			}
    		}
    		factor = scaleFactor;
    		return true;
    	}
    	
    	@Override
    	public boolean onScaleBegin(ScaleGestureDetector detector) {
    		factor = detector.getScaleFactor();
    		return super.onScaleBegin(detector);
    	}
    	
    	@Override
    	public void onScaleEnd(ScaleGestureDetector detector) {
    		super.onScaleEnd(detector);
    	}
    	
    }
    
    abstract class TwoFingerTapDetector {
        
    	boolean moved = false;
    	float THRESHOLD = 20;
    	float pixX, pixY;
    	
        public boolean onTouchEvent(MotionEvent event) {
        	switch (event.getActionMasked()) {
        	case MotionEvent.ACTION_DOWN:
        		moved = false;
        		pixX = event.getX();
        		pixY = event.getY();
        		break;
        	case MotionEvent.ACTION_POINTER_UP:
        		if(!moved){
	        		if(event.getPointerCount() == 2){
	        			onTwoFingerDoubleTap();
	                    return true;
	        		}
        		}
        		break;
        	case MotionEvent.ACTION_MOVE:
        		if((Math.abs(event.getX() - pixX) > THRESHOLD) || (Math.abs(event.getY() - pixY) > THRESHOLD)){
            		moved = true;
        		}
        		break;
        	}
            return false;
        }
        
        public abstract void onTwoFingerDoubleTap();
    }
    
}





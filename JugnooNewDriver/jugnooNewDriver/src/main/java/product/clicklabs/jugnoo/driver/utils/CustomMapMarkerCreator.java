package product.clicklabs.jugnoo.driver.utils;

import product.clicklabs.jugnoo.driver.R;
import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

@SuppressWarnings("static-access")
public class CustomMapMarkerCreator {
	
	public static Bitmap createPassengerMarkerBitmap(Activity activity, ASSL assl){
		float scale = Math.min(assl.Xscale(), assl.Yscale());
		int width = (int)(50.0f * scale);
		int height = (int)(69.0f * scale);
		Bitmap mDotMarkerBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(mDotMarkerBitmap);
		Drawable shape = activity.getResources().getDrawable(R.drawable.passenger);
		shape.setBounds(0, 0, mDotMarkerBitmap.getWidth(), mDotMarkerBitmap.getHeight());
		shape.draw(canvas);
		return mDotMarkerBitmap;
	}
	
	
	public static Bitmap createPinMarkerBitmap(Activity activity, ASSL assl){
		float scale = Math.min(assl.Xscale(), assl.Yscale());
		int width = (int)(40.0f * scale);
		int height = (int)(63.0f * scale);
		Bitmap mDotMarkerBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(mDotMarkerBitmap);
		Drawable shape = activity.getResources().getDrawable(R.drawable.pin_ball);
		shape.setBounds(0, 0, mDotMarkerBitmap.getWidth(), mDotMarkerBitmap.getHeight());
		shape.draw(canvas);
		return mDotMarkerBitmap;
	}
	
	public static Bitmap createCustomMarkerBitmap(Activity activity, ASSL assl, float originalWidth, float originalHeight, int drawableId){
		float scale = Math.min(assl.Xscale(), assl.Yscale());
		int width = (int)(originalWidth * scale);
		int height = (int)(originalHeight * scale);
		Bitmap mDotMarkerBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(mDotMarkerBitmap);
		Drawable shape = activity.getResources().getDrawable(drawableId);
		shape.setBounds(0, 0, mDotMarkerBitmap.getWidth(), mDotMarkerBitmap.getHeight());
		shape.draw(canvas);
		return mDotMarkerBitmap;
	}
	
}

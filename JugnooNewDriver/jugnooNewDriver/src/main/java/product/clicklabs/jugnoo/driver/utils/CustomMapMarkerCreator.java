package product.clicklabs.jugnoo.driver.utils;

import product.clicklabs.jugnoo.driver.R;
import rmn.androidscreenlibrary.ASSL;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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




	public static Marker addTextMarkerToMap(final Context context, final GoogleMap map,
						  final LatLng location, final String text, final int padding,
						  final int fontSize) {
		Marker marker = null;

		if (context == null || map == null || location == null || text == null
				|| fontSize <= 0) {
			return marker;
		}

		final TextView textView = new TextView(context);
		textView.setText(text);
		textView.setTextSize(fontSize);

		final Paint paintText = textView.getPaint();

		final Rect boundsText = new Rect();
		paintText.getTextBounds(text, 0, textView.length(), boundsText);
		paintText.setTextAlign(Paint.Align.CENTER);

		final Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		final Bitmap bmpText = Bitmap.createBitmap(boundsText.width() + 2
				* padding, boundsText.height() + 2 * padding, conf);

		final Canvas canvasText = new Canvas(bmpText);
		paintText.setColor(Color.WHITE);

		canvasText.drawText(text, canvasText.getWidth() / 2,
				canvasText.getHeight() - padding, paintText);

		final MarkerOptions markerOptions = new MarkerOptions()
				.position(location)
				.icon(BitmapDescriptorFactory.fromBitmap(bmpText))
				.anchor(0.5f, 1);

		marker = map.addMarker(markerOptions);

		return marker;
	}

}

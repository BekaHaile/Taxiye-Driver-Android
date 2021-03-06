package product.clicklabs.jugnoo.driver.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.R;

@SuppressWarnings("static-access")
public class CustomMapMarkerCreator {


	public static Bitmap createPinMarkerBitmap(Activity activity, ASSL assl) {
		float scale = Math.min(assl.Xscale(), assl.Yscale());
		int width = (int) (40.0f * scale);
		int height = (int) (63.0f * scale);
		Bitmap mDotMarkerBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(mDotMarkerBitmap);
		Drawable shape = activity.getResources().getDrawable(R.drawable.pin_ball);
		shape.setBounds(0, 0, mDotMarkerBitmap.getWidth(), mDotMarkerBitmap.getHeight());
		shape.draw(canvas);
		return mDotMarkerBitmap;
	}

	public static Bitmap createCustomMarkerBitmap(Activity activity, ASSL assl, float originalWidth, float originalHeight, int drawableId) {
		float scale = Math.min(assl.Xscale(), assl.Yscale());
		int width = (int) (originalWidth * scale);
		int height = (int) (originalHeight * scale);
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
		final TextView textView2 = new TextView(context);
		textView.setText(text);
		textView.setTextSize(fontSize);
		textView2.setText(text);
		textView2.setTextSize(fontSize);

		final Rect boundsText = new Rect();

		final Paint paintText = textView.getPaint();
		paintText.getTextBounds(text, 0, textView.length(), boundsText);
		paintText.setTextAlign(Paint.Align.CENTER);
		paintText.setStyle(Paint.Style.STROKE);
		paintText.setStrokeWidth(4);
		paintText.setColor(Color.WHITE);


		final Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		final Bitmap bmpText = Bitmap.createBitmap(boundsText.width() + 2
				* padding, boundsText.height() + 2 * padding, conf);


		final Paint paintText2 = textView2.getPaint();
		paintText2.getTextBounds(text, 0, textView2.length(), boundsText);
		paintText2.setTextAlign(Paint.Align.CENTER);
		paintText2.setColor(Color.BLACK);

		final Canvas canvasText = new Canvas(bmpText);

		canvasText.drawText(text, canvasText.getWidth() / 2,
				canvasText.getHeight() - padding, paintText);
		canvasText.drawText(text, canvasText.getWidth() / 2,
				canvasText.getHeight() - padding, paintText2);

		final MarkerOptions markerOptions = new MarkerOptions()
				.position(location)
				.icon(BitmapDescriptorFactory.fromBitmap(bmpText))
				.anchor(0.5f, 1);

		marker = map.addMarker(markerOptions);
		marker.setTitle("");

		return marker;
	}


	public static Bitmap getTextBitmap(final Context context, ASSL assl, String text, final int fontSize, final int iconType) {
		float scale = Math.min(assl.Xscale(), assl.Yscale());
		final TextView textView = new TextView(context);
		textView.setText(text);
//		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) (scale * (float) fontSize));
		textView.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.text_size_8));
		textView.setTypeface(Fonts.mavenBold(context));

		final Rect boundsText = new Rect();

		if (iconType == 0) {

			int width = (int)(69.0f * 0.80 * scale);
			int height = (int)(92.0f * 0.80 * scale);

			final Bitmap.Config conf = Bitmap.Config.ARGB_8888;
			final Bitmap bmpText = Bitmap.createBitmap(width, height, conf);

			final Paint paint = textView.getPaint();
			paint.getTextBounds(text, 0, textView.length(), boundsText);
			paint.setTextAlign(Paint.Align.CENTER);
			paint.setColor(Color.WHITE);

			final Canvas canvasText = new Canvas(bmpText);
			Drawable shape = context.getResources().getDrawable(R.drawable.ic_red_pin);
			shape.setBounds(0, 0, bmpText.getWidth(), bmpText.getHeight());
			shape.draw(canvasText);

			canvasText.drawText(text, canvasText.getWidth() / 2, (29f*assl.Yscale()), paint);
			return bmpText;

		} else if(iconType == 2) {

			int width = (int) (86.0f * 1.0 * scale);
			int height = (int) (97.0f * 1.0 * scale);

			final Bitmap.Config conf = Bitmap.Config.ARGB_8888;
			final Bitmap bmpText = Bitmap.createBitmap(width, height, conf);

			final Paint paint = textView.getPaint();
			paint.getTextBounds(text, 0, textView.length(), boundsText);
			paint.setTextAlign(Paint.Align.CENTER);

			final Canvas canvasText = new Canvas(bmpText);
			Drawable shape;

			shape = context.getResources().getDrawable(R.drawable.delivery_orange_pin);
			paint.setColor(Color.RED);


			shape.setBounds(0, 0, bmpText.getWidth(), bmpText.getHeight());
			shape.draw(canvasText);
			canvasText.drawText(text, (canvasText.getWidth() / 2) - 5.5f, (43f * scale), paint);
			return bmpText;

		} else {

			int width = (int) (86.0f * .80 * scale);
			int height = (int) (97.0f * .80 * scale);

			final Bitmap.Config conf = Bitmap.Config.ARGB_8888;
			final Bitmap bmpText = Bitmap.createBitmap(width, height, conf);

			final Paint paint = textView.getPaint();
			paint.getTextBounds(text, 0, textView.length(), boundsText);
			paint.setTextAlign(Paint.Align.CENTER);

			final Canvas canvasText = new Canvas(bmpText);
			Drawable shape;
			if (iconType == 1) {
				shape = context.getResources().getDrawable(R.drawable.delivery_black_pin);
				paint.setColor(Color.BLACK);
			} else {
				shape = context.getResources().getDrawable(R.drawable.delivery_faded_black_pin);
				paint.setColor(Color.GRAY);
			}

			shape.setBounds(0, 0, bmpText.getWidth(), bmpText.getHeight());
			shape.draw(canvasText);
			canvasText.drawText(text, (canvasText.getWidth() / 2) - 4, (34f * assl.Yscale()), paint);
			return bmpText;
		}


	}

}

package com.squareup.picasso;

/**
 * Picasso's(ImageLoaderLibrary) class to crop bitmap in circular shape
 */

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.squareup.picasso.Transformation;
 
public class RoundBorderTransform implements Transformation {
	@Override
	public Bitmap transform(Bitmap source) {
		Bitmap source1 = source;
		try{
			int size = Math.min(source.getWidth(), source.getHeight());
	 
			int x = (source.getWidth() - size) / 2;
			int y = (source.getHeight() - size) / 2;
	 
			Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
			if (squaredBitmap != source) {
				source.recycle();
			}
	 
			Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());
	 
			Canvas canvas = new Canvas(bitmap);
			Paint paint = new Paint();
			BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
			paint.setShader(shader);
			paint.setAntiAlias(true);
	 
			final Rect rect = new Rect(0, 0, size, size);
		    final RectF rectF = new RectF(rect);
		    final float roundPx = 10;
			
		    canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	 
			squaredBitmap.recycle();
			return bitmap;
		} catch(Exception e){
			e.printStackTrace();
		}
		return source1;
	}
 
	@Override
	public String key() {
		return "circle";
	}
}
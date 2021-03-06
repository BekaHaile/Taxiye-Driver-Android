package com.squareup.picasso;

/**
 * Picasso's(ImageLoaderLibrary) class to crop bitmap in circular shape
 */

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
 
public class CircleTransform implements Transformation {
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

			Bitmap bitmap = Bitmap.createBitmap(size, size, source.isRecycled() ? squaredBitmap.getConfig() : source.getConfig());
	 
			Canvas canvas = new Canvas(bitmap);
			Paint paint = new Paint();
			BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
			paint.setShader(shader);
			paint.setAntiAlias(true);
	 
			float r = size / 2f;
			canvas.drawCircle(r, r, r, paint);
	 
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
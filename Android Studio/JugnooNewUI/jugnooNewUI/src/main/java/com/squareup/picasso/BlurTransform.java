package com.squareup.picasso;

/**
 * Picasso's(ImageLoaderLibrary) class to crop bitmap in circular shape
 */

import android.graphics.Bitmap;
import android.util.Log;
 
public class BlurTransform implements Transformation {
	@Override
	public Bitmap transform(Bitmap source) {
		try {
			Bitmap dest = fastBlur(source, 3);
			source.recycle();
			return dest;
		} catch (Exception e) {
			e.printStackTrace();
			return source;
		}
	}
	
	
	public Bitmap fastBlur(Bitmap sentBitmap, int radius) {

        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            return (null);
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[] pix = new int[width * height];
        Log.e("pix", width + " " + height + " " + pix.length);
        bitmap.getPixels(pix, 0, width, 0, 0, width, height);

        int widthM = width - 1;
        int heightM = height - 1;
        int widthH = width * height;
        int div = radius + radius + 1;

        int red[] = new int[widthH];
        int green[] = new int[widthH];
        int blue[] = new int[widthH];
        int rsum, gsum, bsum, xVal, yVal, iVal, pVal, ypVal, yiVal, ywVal;
        int vmin[] = new int[Math.max(width, height)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dvArr[] = new int[256 * divsum];
        for (iVal = 0; iVal < 256 * divsum; iVal++) {
            dvArr[iVal] = (iVal / divsum);
        }

        ywVal = yiVal = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1Val = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (yVal = 0; yVal < height; yVal++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (iVal = -radius; iVal <= radius; iVal++) {
                pVal = pix[yiVal + Math.min(widthM, Math.max(iVal, 0))];
                sir = stack[iVal + radius];
                sir[0] = (pVal & 0xff0000) >> 16;
                sir[1] = (pVal & 0x00ff00) >> 8;
                sir[2] = (pVal & 0x0000ff);
                rbs = r1Val - Math.abs(iVal);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (iVal > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (xVal = 0; xVal < width; xVal++) {

                red[yiVal] = dvArr[rsum];
                green[yiVal] = dvArr[gsum];
                blue[yiVal] = dvArr[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (yVal == 0) {
                    vmin[xVal] = Math.min(xVal + radius + 1, widthM);
                }
                pVal = pix[ywVal + vmin[xVal]];

                sir[0] = (pVal & 0xff0000) >> 16;
                sir[1] = (pVal & 0x00ff00) >> 8;
                sir[2] = (pVal & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yiVal++;
            }
            ywVal += width;
        }
        for (xVal = 0; xVal < width; xVal++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            ypVal = -radius * width;
            for (iVal = -radius; iVal <= radius; iVal++) {
                yiVal = Math.max(0, ypVal) + xVal;

                sir = stack[iVal + radius];

                sir[0] = red[yiVal];
                sir[1] = green[yiVal];
                sir[2] = blue[yiVal];

                rbs = r1Val - Math.abs(iVal);

                rsum += red[yiVal] * rbs;
                gsum += green[yiVal] * rbs;
                bsum += blue[yiVal] * rbs;

                if (iVal > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (iVal < heightM) {
                    ypVal += width;
                }
            }
            yiVal = xVal;
            stackpointer = radius;
            for (yVal = 0; yVal < height; yVal++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yiVal] = ( 0xff000000 & pix[yiVal] ) | ( dvArr[rsum] << 16 ) | ( dvArr[gsum] << 8 ) | dvArr[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (xVal == 0) {
                    vmin[yVal] = Math.min(yVal + r1Val, heightM) * width;
                }
                pVal = xVal + vmin[yVal];

                sir[0] = red[pVal];
                sir[1] = green[pVal];
                sir[2] = blue[pVal];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yiVal += width;
            }
        }

        Log.e("pix", width + " " + height + " " + pix.length);
        bitmap.setPixels(pix, 0, width, 0, 0, width, height);

        return (bitmap);
    }
	
 
	@Override
	public String key() {
		return "blur";
	}
}
package com.hua.gz.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;

public class ImageEffectUtils {
	
	private ImageEffectUtils() {
		// Cannot instantiate.
	}

	/**
	 * 
	 * @param context
	 * @param resID
	 * @param total  divide the height of original image to total parts equally.
	 * @param weight  the weight of height for reflection image
	 * @return
	 */
	public static Bitmap createReflectedImage(Context context, int resID, int total, int weight) {
		Bitmap originalImage = BitmapFactory.decodeResource(context.getResources(), resID);
		return createReflectedImage(originalImage, total, weight);
	}
	
	/**
	 * 
	 * @param context
	 * @param originalImage
	 * @param total  divide the height of original image to total parts equally.
	 * @param weight  the weight of height for reflection image
	 * @return
	 */
	public static Bitmap createReflectedImage(Bitmap originalImage, int total, int weight) {
		Bitmap reflectionImage = null;
		Bitmap bitmapWithReflection = null;
		try {
			final int reflectionGap = -1; //the gap between origin and reflection 
			int width = originalImage.getWidth();
			int height = originalImage.getHeight();
			int cut = total - weight;
			int unitHeight = height / total;
			Matrix matrix = new Matrix();
			matrix.preScale(1, -1);
			reflectionImage = Bitmap.createBitmap(originalImage, 0, unitHeight * cut, width, unitHeight * weight, matrix, false);
			bitmapWithReflection = Bitmap.createBitmap(width, (height + reflectionImage.getHeight()), Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmapWithReflection);
			//draw origin image
			canvas.drawBitmap(originalImage, 0, 0, null);
			//draw reflection image
			canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
			//create a shader that draws a linear gradient along a line.
			Paint paint = new Paint();
			LinearGradient shader = new LinearGradient(0, height, 0, bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.MIRROR);
			paint.setShader(shader);
			paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
			//make shader come into effect
			canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);
			//free up the memory 
			originalImage.recycle();
			reflectionImage.recycle();
		} catch(Exception e) {
		}
		return bitmapWithReflection;
	}
	
	/**
	 * 
	 * @param context
	 * @param originalImage
	 * @param reflectionWeight
	 * @return
	 */
	public static Bitmap createReflectedImage(Bitmap originalImage, int reflectionWeight) {
		Bitmap reflectionImage = null;
		Bitmap bitmapWithReflection = null;
		try {
			final int reflectionGap = -1; //the gap between origin and reflection 
			int width = originalImage.getWidth();
			int height = originalImage.getHeight();
			Matrix matrix = new Matrix();
			matrix.preScale(1, -1);
			reflectionImage = Bitmap.createBitmap(originalImage, 0, height - reflectionWeight, width, reflectionWeight, matrix, false);
			bitmapWithReflection = Bitmap.createBitmap(width, (height + reflectionImage.getHeight()), Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmapWithReflection);
			//draw origin image
			canvas.drawBitmap(originalImage, 0, 0, null);
			//draw reflection image
			canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);
			//create a shader that draws a linear gradient along a line.
			Paint paint = new Paint();
			LinearGradient shader = new LinearGradient(0, height, 0, bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.MIRROR);
			paint.setShader(shader);
			paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
			//make shader come into effect
			canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);
			//free up the memory 
			originalImage.recycle();
			reflectionImage.recycle();
		} catch(Exception e) {
		}
		return bitmapWithReflection;
	}
	
	public static Bitmap createRoundedCornerBitmap(Context context, Bitmap input, int radiusPixels) {
		return createRoundedCornerBitmap(context, input, radiusPixels, 
				input.getWidth(), input.getHeight(), false, false, false, false, true);
	}
	
    public static Bitmap createRoundedCornerBitmap(Context context, Bitmap input, int radiusPixels, int w, int h, 
    		boolean squareTL, boolean squareTR, boolean squareBL, boolean squareBR, boolean recyleInput) {
        Bitmap output = Bitmap.createBitmap(w, h, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final float densityMultiplier = context.getResources().getDisplayMetrics().density;
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, w, h);
        final RectF rectF = new RectF(rect);
        //make sure that our rounded corner is scaled appropriately
        final float roundPx = radiusPixels * densityMultiplier;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        //draw rectangles over the corners we want to be square
        if (squareTL ){
            canvas.drawRect(0, 0, w/2, h/2, paint);
        }
        if (squareTR ){
            canvas.drawRect(w/2, 0, w, h/2, paint);
        }
        if (squareBL ){
            canvas.drawRect(0, h/2, w/2, h, paint);
        }
        if (squareBR ){
            canvas.drawRect(w/2, h/2, w, h, paint);
        }
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //canvas.drawBitmap(input, 0,0, paint);
        canvas.drawBitmap(input, null, rect, paint);
        if(recyleInput) input.recycle();
        return output;
    }
	
}

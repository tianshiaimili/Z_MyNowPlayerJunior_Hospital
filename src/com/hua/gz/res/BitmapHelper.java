package com.hua.gz.res;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.os.Build;
import android.util.Log;

import com.hua.nowplayerjunior.os.AndroidSDK;

/**
 * @author AlfredZhong
 * @version 2012-07-27
 * @version 2012-07-28, Added functions to reduce OOM error.
 * @version 2013-12-25, Added decodeBitmap() to uniform the parameters.
 * @see http://developer.android.com/reference/android/graphics/BitmapFactory.Options.html
 */
public class BitmapHelper {
	
	private static final String TAG = BitmapHelper.class.getSimpleName();
	private static final int IO_BUFFER_SIZE = 1024 * 8;
	
	private BitmapHelper() {}
	
    /**
     * Get the size in bytes of a bitmap.
     * @param bitmap
     * @return size in bytes
     */
    @SuppressLint("NewApi")
	public static int getBitmapSize(Bitmap bitmap) {
        if (AndroidSDK.API_LEVEL >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }
        // pre-HONEYCOMB_MR1
        return bitmap.getRowBytes() * bitmap.getHeight();
    }
    
	public static void writeBitmapToFile(String filename, Bitmap bitmap, CompressFormat format, int quality) {
		BufferedOutputStream out = null;
		File file = new File(filename);
		try {
			out = new BufferedOutputStream(new FileOutputStream(file), IO_BUFFER_SIZE);
			bitmap.compress(format, quality, out);
			out.flush();
			Log.d(TAG, "writeBitmapToFile() success.");
		} catch (Exception e) {
			// If SDcard is unmounted or no permission, you will get java.io.FileNotFoundException
			// You can check permission WRITE_EXTERNAL_STORAGE and Environment.getExternalStorageState();
			Log.e(TAG, "saveImage failed : " + e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	public static Bitmap getFixedBitmap(Bitmap bm, int w, int h, boolean filter) {  
	    int width = bm.getWidth();  
	    int height = bm.getHeight();  
	    Matrix matrix = new Matrix();
	    matrix.postScale((float) w / width, (float) h / height);  
	    return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, filter);  
	}  
	
	/**
	 * please at outside use method:decodeScaledDownBitmap() prefer.
	 * better make it private , if you directly use this method with a null opts, it is easy to get out of memory without calculate the inSampleSize.
	 * @param toBeDecode
	 * @param res
	 * @param opts
	 * @return
	 */
	private static Bitmap decodeBitmap(Object toBeDecode, Resources res, BitmapFactory.Options opts) {
		try {
			if(toBeDecode instanceof byte[]) {
				byte[] data = (byte[]) toBeDecode;
				return BitmapFactory.decodeByteArray(data, 0, data.length, opts);
			} else if(toBeDecode instanceof String) {
				return BitmapFactory.decodeFile((String)toBeDecode, opts);
			} else if(toBeDecode instanceof InputStream) {
				// Do NOT close InputStream here, caller may only use this method to decode bounds.
				return BitmapFactory.decodeStream((InputStream)toBeDecode, null, opts);
			} else if(toBeDecode instanceof Integer) {
				// decodeResource will not cause exception, just return null if failed.
				// But some custom ROMs, such as HTC, may throw Resources$NotFoundException.
				return BitmapFactory.decodeResource(res, (Integer)toBeDecode, opts);
			} else if(toBeDecode instanceof FileDescriptor) {
				return BitmapFactory.decodeFileDescriptor((FileDescriptor)toBeDecode, null, opts);
			}
		} catch (Exception e) {
			Log.w(TAG, TAG + ".decodeBitmap() failed with toBeDecode : " + toBeDecode, e);
		} catch (OutOfMemoryError oom) {
			// catch OOM to avoid application crash.
			Log.e(TAG, TAG + ".decodeBitmap() failed with OutOfMemoryError.", oom);
		}
		return null;
	}
	
	/**
	 * Query the bitmap out... fields without having to allocate the memory for its pixels. 
	 * 
	 * @param toBeDecode can be byte[], String, InputStream, Integer(resId), FileDescriptor
	 * @param res set null if toBeDecode is not Integer(resId)
	 * @return BitmapFactory.Options out fields, such as opt.outWidth, opt.outHeight and opt.outMimeType.
	 */
	public static Options decodeBounds(Object toBeDecode, Resources res) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		decodeBitmap(toBeDecode, res, options);
		return options;
	}
	
	/**
	 * Returns BitmapFactory.Options.inSampleSize based on a target width and height 
	 * which is the smallest value makes output size >= target width x target height.
	 * For example, an image with resolution 2048x1536 that is decoded with an inSampleSize of 4 produces 
	 * a bitmap of approximately 512x384. Loading this into memory uses 0.75MB 
	 * rather than 12MB for the full image (assuming a bitmap configuration of ARGB_8888).
	 * 
	 * @param options
	 * @param reqMinWidth request minimal ImageView width pixel
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqMinWidth) {
		/*
		 * Note: Using powers of 2 for inSampleSize values is faster and more efficient for the decoder. 
		 * However, if you plan to cache the resized versions in memory or on disk, 
		 * it's usually still worth decoding to the most appropriate image dimensions to save space.
		 * 
		 * But remember that if inSampleSize is not the power of 2, BitmapFactor will find the proximate value
		 * which is smaller than inSampleSize and it is the power of 2 as a new inSampleSize.
		 * For example, if inSampleSize is 6, BitmapFactor will use 4 as inSampleSize;
		 * if inSampleSize is 15, BitmapFactor will use 8 as inSampleSize.
		 */
		int inSampleSize = 1;
		// -1 if there is an error trying to decode bounds.
		final int rawHeight = options.outHeight;
		final int rawWidth = options.outWidth;
		if(reqMinWidth > 0 && rawHeight > 0 && rawWidth > 0) {
			// calculate inSampleSize.
			final int reqMinHeight = reqMinWidth * rawHeight / rawWidth;
			if (rawHeight > reqMinHeight || rawWidth > reqMinWidth) {
				if (rawWidth > rawHeight) {
					inSampleSize = Math.round((float) rawHeight / (float) reqMinHeight);
				} else {
					inSampleSize = Math.round((float) rawWidth / (float) reqMinWidth);
				}
			}
			 Log.d(TAG, "Calculate inSampleSize = " + inSampleSize);
		} else {
			Log.i(TAG, "Calculate inSampleSize failed: rawWidth = " + rawWidth + ", rawHeight = " + rawHeight + ", reqMinWidth = " + reqMinWidth);
		}
		return inSampleSize;
	}
	
	/**
	 * Decode scaled down bitmap.
	 * 
	 * @param toBeDecode can be byte[], String, InputStream, Integer(resId), FileDescriptor
	 * @param res set null if toBeDecode is not Integer(resId)
	 * @param reqMinWidth request minimal ImageView width pixel
	 * @param opts set null if you don't need it
	 * @return
	 */
	public static Bitmap decodeScaledDownBitmap(Object toBeDecode, Resources res, final int reqMinWidth, final BitmapFactory.Options opts) {
		if(reqMinWidth <= 0) {
			return decodeBitmap(toBeDecode, res, opts);
		}
		BitmapFactory.Options options = decodeBounds(toBeDecode, res);
	    // reset InputStream after decode bounds.
		if(toBeDecode instanceof InputStream) {
		    try {
		    	((InputStream)toBeDecode).reset();
		    } catch(IOException e) {
		    	Log.w(TAG, "decodeScaledDownBitmap() reset InputStream failed : " + e);
		    }
		}
		final int inSampleSize = calculateInSampleSize(options, reqMinWidth);
		final int rawHeight = options.outHeight;
		final int rawWidth = options.outWidth;
	    // Decode bitmap pixel data with inSampleSize.
		if(opts != null) {
			options = opts;
		}
	    options.inJustDecodeBounds = false;
	    options.inSampleSize = inSampleSize;
	    Bitmap bitmap = decodeBitmap(toBeDecode, res, options);
	    // close InputStream after decode bitmap.
		if(toBeDecode instanceof InputStream) {
		    try {
		    	((InputStream)toBeDecode).close();
		    } catch(IOException e) {
		    	Log.w(TAG, "decodeScaledDownBitmap() close InputStream failed : " + e);
		    }
		}
	    // Log bitmap inSampleSize and bitmap size.
	    try {
	    	int originalBitmapSizeKB = rawHeight * rawWidth * 4 / 1024; // default ARGB_8888.
	    	int scaledDownBitmapSizeKB = getBitmapSize(bitmap) / 1024; // after 3.1, can use bitmap.getByteCount();
	    	Log.d(TAG, "decodeScaledDownBitmap with inSampleSize " + options.inSampleSize 
	    			+ ", output size " + originalBitmapSizeKB + "KB -> " + scaledDownBitmapSizeKB + " KB.");
	    } catch(Exception e) {
	    }
		return bitmap;
	}

}

package com.hua.gz.res;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * A helper class to manage images cache in memory and disk.
 * <p>Note: you should remove the bitmap from memory if you don't need it.
 * 
 * @author AlfredZhong
 * @version 1.0, 2011-12-28
 * @version 2012-06-27, changed super class to AbsImageCacheHelper.
 * @version 2012-07-27, added ImageCacheParams.
 */
public class TradImageCacheHelper extends AbsImageCacheHelper {

	private static final String TAG = TradImageCacheHelper.class.getSimpleName();
	private ImageCacheParams mCacheParams;
	// We use static map here to make all TradImageCacheHelper instants share one map.
	private static final Map<String, SoftReference<Bitmap>> bitmapCache;
	
	static {
		bitmapCache = new HashMap<String, SoftReference<Bitmap>>();
	}
	
	public TradImageCacheHelper(ImageCacheParams params) {
		if(params == null) {
			throw new NullPointerException("ImageCacheParams can not be null.");
		}
		mCacheParams = params;
		// call mkdirs() every time in case user delete the cache folder.
		new File(mCacheParams.getCachePath()).mkdirs();
		Log.w(TAG, mCacheParams.toString());
		Log.w(TAG, TAG + " memory cache map size is " + size());
	}
	
	@Override
	public void putImage(String key, Bitmap bitmap, boolean saveFile) {
		if(bitmap == null) {
			Log.w(TAG, TAG + " putImage failed, bitmap is null.");
			return;
		}
		// Put into cache (memory)
		bitmapCache.put(key, new SoftReference<Bitmap>(bitmap));
		if(saveFile){
			// save image file to disk.
			String filename = getFilename(key);
			// check permission and SDcard mounted in BitmapHelper.
			if(filename != null)
				BitmapHelper.writeBitmapToFile(filename, bitmap, mCacheParams.getCompressFormat(), mCacheParams.getCompressQuality());
		}
	}
	
	@Override
	public boolean containsKey(String key) {
		return bitmapCache.containsKey(key);
	}

	@Override
	public Bitmap getImage(String key) {
		return getImage(key, 0);
	}
	
	@Override
	public Bitmap getImage(String key, int reqMinWidth) {
		Bitmap bm = null;
		SoftReference<Bitmap> sr = null;
		// Whether the bitmap reference is in the map.
		if((sr = bitmapCache.get(key)) != null) {
			// Find image in memory, hold a strong reference to the referent to use it.
			bm = sr.get();
		}
		// If bitmap is not null but recycled, regards it as null.
		if(bm != null && bm.isRecycled()) {
			Log.d(TAG, "Bitmap is not null but recycled, regards it as null.");
			bm = null;
		}
		// If the bitmap is null, that means the referent has been GC or bitmap data has been recycled
		// or not put bitmap into memory, find image in disk.
		if(bm == null) {
			String filename = getFilename(key);
			if(filename != null && new File(filename).exists()) {
				// if reqMinWidth <= 0, will directly decode file.
				bm = decodeScaledDownBitmap(filename, null, reqMinWidth, null);
			}
			if(bm != null) {
				// The image file exists in the cache path, put bitmap into map.
				Log.d(TAG, "Find the bitmap in disk.");
				bitmapCache.put(key, new SoftReference<Bitmap>(bm));
			} else {
				Log.d(TAG, "Can not find the bitmap in memory and disk.");
			}
		} else {
			Log.d(TAG, "Find the bitmap in memory.");
		}
		return bm;
	}
	
	@Override
	public void removeImage(String key) {
		Bitmap bm = null;
		SoftReference<Bitmap> sr = null;
		if((sr = bitmapCache.get(key)) != null) {
			// hold a strong reference to the referent to use it
			bm = sr.get();
			// no need to keep the referent.
			sr.clear();
		}
		if(bm != null){
			bm.recycle();
			bm = null;
		}
		// remove reference in cache map
		bitmapCache.remove(key); 
	}
	
	@Override
	public void clear() {
		if(bitmapCache.size() > 0) {
			ArrayList<String> keys = new ArrayList<String>(bitmapCache.keySet());
			for(String key : keys) {
				removeImage(key);
			}
		}
		bitmapCache.clear();
	}
	
	@Override
	public String getFilename(String key) {
		if(key == null)
			return null;
		try {
			return mCacheParams.getCachePath() + URLEncoder.encode(key.replace("*", ""), "UTF-8") + "." + mCacheParams.getCompressFormat();
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "getFilename with key " + key + " failed : " + e);
		}
		return null;
	}
	
	@Override
	public int size() {
		return bitmapCache.size();
	}
	
}

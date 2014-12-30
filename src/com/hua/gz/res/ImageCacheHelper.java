package com.hua.gz.res;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

/**
 * A helper class to manage images cache in memory and disk.
 * <p>Note: When the bitmap referent has been GC, the entry in cache map will be auto removed.
 * 
 * @author AlfredZhong
 * @version 2012-04-01
 * @version 2012-06-27, changed super class to AbsImageCacheHelper.
 * @version 2012-07-27, added ImageCacheParams.
 * @version 2013-12-24, two types of map can be used.
 */
public class ImageCacheHelper extends AbsImageCacheHelper {

	private static final String TAG = ImageCacheHelper.class.getSimpleName();
	private ImageCacheParams mCacheParams;
	// We use static map here to make all ImageCacheHelper instants share one map.
	private   LruCache<String, Bitmap> bitmapCache;
	int MAX_MEMORY_SIZE = (int) (Runtime.getRuntime().maxMemory()/8);
//	static {
//		// switch map here.
//		boolean useFinalizableConcurrentMap = true;
//		if(useFinalizableConcurrentMap) {
//			bitmapCache = new FinalizableConcurrentMap<String, Bitmap>(
//					FinalizableConcurrentMap.ReferenceKeyType.SOFT, FinalizableConcurrentMap.ReferenceValueType.SOFT);
//		} else {
//			bitmapCache = new GCableConcurrentMap<String, Bitmap>(GCableConcurrentMap.GCableReferenceValueType.SOFT);
//		}
//	}
	
	public ImageCacheHelper(ImageCacheParams params) {
		if(params == null) {
			throw new NullPointerException("ImageCacheParams can not be null.");
		}
		mCacheParams = params;
		// call mkdirs() every time in case user delete the cache folder.
		new File(mCacheParams.getCachePath()).mkdirs();
		Log.w(TAG, mCacheParams.toString());
		Log.w(TAG, TAG + " memory cache map size is " + size());
		
		bitmapCache = new  LruCache<String, Bitmap>(MAX_MEMORY_SIZE ){  
  
            //override to count Bitmap size.  
            @Override  
            protected int sizeOf(String key, Bitmap value) {  
                int size = value.getRowBytes() * value.getHeight()/1024;
                Log.d(TAG, "--lru cache---bitMapSize---KB---- : "+size);
				return size ;  
            }  
              
        };
	}
	
	@Override
	public void putImage(String key, Bitmap bitmap, boolean saveFile) {
		if(bitmap == null) {
			Log.w(TAG, TAG + " putImage failed, bitmap is null.");
			return;
		}
		// Put into cache (memory)
		bitmapCache.put(key, bitmap);
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
		return (bitmapCache.get(key)!=null);
	}
	
	@Override
	public Bitmap getImage(String key) {
		return getImage(key, 0);
	}
	
	@Override
	public Bitmap getImage(String key, int reqMinWidth) {
		// Find image in memory, hold a strong reference to the referent to use it.
		Bitmap bm = bitmapCache.get(key);
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
				bitmapCache.put(key, bm);
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
		Bitmap bm = bitmapCache.get(key);
		if(bm != null){
			// find image in disk
			bm.recycle();
			bm = null;
		}
		// remove reference in cache map
		bitmapCache.remove(key); 
	}
	
	@Override
	public void clear() {
		
		bitmapCache.trimToSize(0);
		bitmapCache = null;
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
		if(bitmapCache == null){
			return 0;
		}
		return bitmapCache.size();
	}
	
}

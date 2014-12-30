package com.hua.gz.res;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

/**
 * A holder class that contains storage cache parameters.
 * 
 * @author AlfredZhong
 * @version 2012-07-27
 * @version 2013-12-31, enable both external and internal cache, can not custom cache folder.
 */
public class ImageCacheParams {

	private static final String TAG = ImageCacheParams.class.getSimpleName();
	// Default settings
	private static final String DEFAULT_FOLDER_NAME = "image_cache";
    private static final CompressFormat DEFAULT_COMPRESS_FORMAT = CompressFormat.PNG;
    private static final int DEFAULT_COMPRESS_QUALITY = 100;
    public static final long DEFAULT_EXTERNAL_TIMEOUT_IN_SECOND = CacheFileManager.HOUR * 24;
    public static final long DEFAULT_EXTERNAL_FOLDER_SIZE_IN_BYTE = CacheFileManager.MB * 10;
    public static final long DEFAULT_INTERNAL_TIMEOUT_IN_SECOND = CacheFileManager.HOUR * 12;
    public static final long DEFAULT_INTERNAL_FOLDER_SIZE_IN_BYTE = CacheFileManager.MB * 5;
    // Custom settings
	private CompressFormat compressFormat = DEFAULT_COMPRESS_FORMAT;
	private int compressQuality = DEFAULT_COMPRESS_QUALITY;
	private String cachePath;
	private long cacheTimeout;
	private long cacheSize;
	
	public ImageCacheParams(Context context) {
		boolean useExternal = false;
		if(StorageHelper.isExternalStorageRemovable()) {
			// removable external storage.
			if(StorageHelper.isExternalStorageMounted()) {
				useExternal = true;
				Log.i(TAG, "Use removable external cache dir.");
			} else {
				Log.i(TAG, "Use built-in internal cache dir.");
				useExternal = false;
			}
		} else {
			useExternal = true;
			Log.i(TAG, "Use built-in external cache dir.");
		}
		if(useExternal) {
			cachePath = getExternalCachePath(context);
			cacheTimeout = DEFAULT_EXTERNAL_TIMEOUT_IN_SECOND;
			cacheSize = DEFAULT_EXTERNAL_FOLDER_SIZE_IN_BYTE;
		} else {
			cachePath = getInternalCachePath(context);
			cacheTimeout = DEFAULT_INTERNAL_TIMEOUT_IN_SECOND;
			cacheSize = DEFAULT_INTERNAL_FOLDER_SIZE_IN_BYTE;
		}
	}
	
	/**
	 * @param context
	 * @return /mnt/sdcard/Android/data/package_name/cache/image_cache/
	 */
	public static String getExternalCachePath(Context context) {
		return StorageHelper.getExternalCacheDir(context).getAbsolutePath() 
				+ File.separator + DEFAULT_FOLDER_NAME + File.separator;
	}
	
	/**
	 * @param context
	 * @return /data/data/package_name/cache/image_cache/
	 */
	public static String getInternalCachePath(Context context) {
		return StorageHelper.getInternalCacheDir(context).getAbsolutePath() 
				+ File.separator + DEFAULT_FOLDER_NAME + File.separator;
	}
	
	public CompressFormat getCompressFormat() {
		return compressFormat;
	}

	public int getCompressQuality() {
		return compressQuality;
	}
	
	public String getCachePath() {
		return cachePath;
	}
	
	public long getCacheTimeout() {
		return cacheTimeout;
	}
	
	public long getCacheSize() {
		return cacheSize;
	}
	
}

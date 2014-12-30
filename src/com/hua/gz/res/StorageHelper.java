package com.hua.gz.res;

import java.io.File;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.hua.nowplayerjunior.os.AndroidSDK;

/**
 * See here for more info:
 * http://developer.android.com/guide/topics/data/data-storage.html
 * 
 * Environment.getRootDirectory().getPath(); // "/system"
 * Environment.getDataDirectory().getPath(); // "/data"
 * Environment.getDownloadCacheDirectory().getPath(); // "/cache"
 * Environment.getExternalStorageDirectory().getPath(); // "/mnt/sdcard"
 * 
 * @author AlfredZhong
 * @version 2012-07-27
 */
public class StorageHelper {
	
	private static final String TAG = StorageHelper.class.getSimpleName();
	
	private StorageHelper() {};
	
    /**
     * Check if external storage is built-in or removable.
     *
     * @return True if external storage is removable (like an SD card), false
     *         otherwise.
     */
    public static boolean isExternalStorageRemovable() {
        if (AndroidSDK.API_LEVEL >= Build.VERSION_CODES.GINGERBREAD) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }
	
	public static boolean isExternalStorageMounted() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			// Log.d(TAG, "External storage is present and mounted at its mount point with read/write access.");
			return true;
		}
		Log.v(TAG, "External storage state is " + state);
		return false;
	}
	
	public static boolean isExternalStorageReadable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can read and write the media
			// Log.w(TAG, "External storage is present and readable.");
			return true;
		}
		/*
		 * Something else is wrong. It may be one of many other states, 
		 * but all we need to know is we can neither read nor write
		 */
		Log.d(TAG, "External storage state is " + state);
		return false;
	}
	
    /**
     * Check if OS version has built-in external cache dir method.
     *
     * @return
     */
    public static boolean hasExternalCacheDir() {
        return AndroidSDK.API_LEVEL >= Build.VERSION_CODES.FROYO;
    }
	
    /**
     * Get the external app cache directory.
     * If the user's device is running API Level 8 or greater and they uninstall your application, 
     * this directory and all its contents will be deleted. However, during the life of your application, 
     * you should manage these cache files and remove those that aren't needed in order to preserve file space.
     *
     * @param context The context to use
     * @return The external cache dir, usually "/mnt/sdcard/Android/data/package_name/cache", "/storage/sdcard0/Android/data/package_name/cache"

     */
    public static File getExternalCacheDir(Context context) {
    	File cacheFile = null;
        if (hasExternalCacheDir()) {
        	// If calling this before Froyo(Android2.2.x) will cause java.lang.NoSuchMethodError: android.content.Context.getExternalCacheDir.
        	// Returns null if ExternalStorageState illegal or you don't have the permission(ApplicationContext : Unable to create external cache directory).
        	cacheFile = context.getExternalCacheDir();
        }
        if(cacheFile == null) {
            // Before Froyo we need to construct the external cache dir ourselves
            // internal cache dir is "/data/data/com.example.android.bitmapfun/cache"
            final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache";
        	cacheFile = new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
        }
        // "/mnt/sdcard/Android/data/package_name/cache", "/storage/sdcard0/Android/data/package_name/cache"
    	Log.d(TAG, "ExternalCacheDir " + cacheFile.getAbsolutePath() + " created? --> " + cacheFile.exists());
        return cacheFile;
    }
    
    /**
     * Returns the absolute path to the application specific cache directory on the filesystem. 
     * 
     * @param context
     * @return
     */
    public static File getInternalCacheDir(Context context) {
    	// getCacheDir: /data/data/package_name/cache
    	// getFilesDir: /data/data/package_name/files
    	return context.getCacheDir();
    }
	
}

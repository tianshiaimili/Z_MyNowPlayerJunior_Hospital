package com.hua.gz.res;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import android.content.Context;

import com.hua.gz.utils.ThreadPoolUtils;

/**
 * A helper class to manage cache files.
 * 
 * @author AlfredZhong
 * @version 2012-10-16
 */
public class CacheFileManager {
	
	private static final String TAG = CacheFileManager.class.getSimpleName();
	public static final long KB = 1024;
	public static final long MB = KB * 1024;
	public static final long MINUTE = 60;
	public static final long HOUR = MINUTE * 60;
	public static final long DAY = HOUR * 24;
	
	private CacheFileManager() {
	}
	
	public static void refreshImageCacheOffUIThread(final Context context, final long externalTimeout, final long externalSize,
			final long internalTimeout, final long internalSize) {
		ThreadPoolUtils.execute(new Runnable() {
			@Override
			public void run() {
				refreshImageCache(context, externalTimeout, externalSize, internalTimeout, internalSize);
			}
		});
	}
	
	public static void refreshImageCache(Context context, long externalTimeout, long externalSize,
			long internalTimeout, long internalSize) {
		// refresh external cache.
		String externalCachePath = ImageCacheParams.getExternalCachePath(context);
		try {
			if(externalTimeout < 0) externalTimeout = ImageCacheParams.DEFAULT_EXTERNAL_TIMEOUT_IN_SECOND;
			deleteOutdateCache(externalCachePath, externalTimeout);
		} catch (Exception e) {
		}
		try {
			if(externalSize < 0) externalSize = ImageCacheParams.DEFAULT_EXTERNAL_FOLDER_SIZE_IN_BYTE;
			shrinkFolderSize(externalCachePath, externalSize);
		} catch (Exception e) {
		}
		// refresh internal cache.
		String internalCachePath = ImageCacheParams.getInternalCachePath(context);
		try {
			if(internalTimeout < 0) internalTimeout = ImageCacheParams.DEFAULT_EXTERNAL_TIMEOUT_IN_SECOND;
			deleteOutdateCache(internalCachePath, internalTimeout);
		} catch (Exception e) {
		}
		try {
			if(internalSize < 0) internalSize = ImageCacheParams.DEFAULT_EXTERNAL_FOLDER_SIZE_IN_BYTE;
			shrinkFolderSize(internalCachePath, internalSize);
		} catch (Exception e) {
		}
	}
	
	public static File[] getCacheFolderFiles(String folderName) {
		File cacheFile = new File(folderName);
		if(!cacheFile.exists()) {
			return null;
		}
		if(!cacheFile.isDirectory()) {
			return null;
		}
		return cacheFile.listFiles();
	}
	
	/**
	 * Delete files which last modified >= timeoutInSeconds.
	 * 
	 * @param context
	 * @param folderName
	 * @param timeoutInSeconds {@link #KB}, {@link #MB}
	 */
	public static void deleteOutdateCache(String folderName, long timeoutInSeconds) {
		File[] caches = getCacheFolderFiles(folderName);
		long timeout = 0;
		if(caches != null) {
			for(File f : caches) {
				timeout = (System.currentTimeMillis() - f.lastModified()) / 1000;
				if(timeout > timeoutInSeconds) {
					android.util.Log.d(TAG, "Delete outdate file " + f.getName() + ", timeout " + timeout + "s.");
					f.delete(); // delete outdate cache.
				}
			}
		}
	}
	
	/**
	 * Delete oldest last modified files in folder until folder size <= floderMaxSizeInBytes.
	 * 
	 * @param context
	 * @param folderName
	 * @param floderMaxSizeInBytes {@link #MINUTE}, {@link #HOUR}, {@link #DAY}
	 */
	public static void shrinkFolderSize(String folderName, long floderMaxSizeInBytes) {
		File[] caches = getCacheFolderFiles(folderName);
		if(caches != null) {
			long total = 0;
			for(File f : caches) {
				total += f.length();
			}
			if(total > floderMaxSizeInBytes) {
				android.util.Log.d(TAG, "Folder size is " + total + " bytes.");
				Collections.sort(Arrays.asList(caches), new FileComparator());
				for(File f : caches) {
					android.util.Log.d(TAG, "Delete file " + f.getName() + ", size " + f.length() + "bytes.");
					total -= f.length(); // resize total before delete() which will make f.length() == 0.
					f.delete(); // delete oldest cache.
					if(total <= floderMaxSizeInBytes) {
						break;
					}
				}
			}
		}
	}
	
	private static class FileComparator implements Comparator<File> {
		
		@Override
		public int compare(File lhs, File rhs) {
			// From oldest to newest.
			return (int) (lhs.lastModified() - rhs.lastModified());
		}
		
	}
	
}

package com.hua.gz.res;

import android.content.Context;
import android.view.ViewGroup;

/**
 * @author AlfredZhong
 * @version 2013-12-25
 */
public final class ImageLoaderFactory {
	
	/**
	 * @param context
	 * @param view such as ListView and GridView
	 * @return
	 */
	public static GroupImageLoader getGroupImageLoader(Context context, ViewGroup view) {
		ImageCacheParams params = new ImageCacheParams(context);
		AbsImageCacheHelper helper = new ImageCacheHelper(params);
		return new GroupImageLoader(helper, view);
	}
	
	/**
	 * @param context
	 * @return
	 */
	public static SingleImageLoader getSingleImageLoader(Context context) {
		ImageCacheParams params = new ImageCacheParams(context);
		AbsImageCacheHelper helper = new ImageCacheHelper(params);
		return new SingleImageLoader(helper);
	}
	
}

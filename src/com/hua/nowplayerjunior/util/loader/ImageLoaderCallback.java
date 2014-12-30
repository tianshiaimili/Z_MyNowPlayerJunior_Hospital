package com.hua.nowplayerjunior.util.loader;

import android.graphics.Bitmap;

public interface ImageLoaderCallback {

	/**
	 * Call when the bitmap decoded.
	 * 
	 * @param bitmap the bitmap that you has handled.
	 */
	public Bitmap onBitmapDecoded(Bitmap bitmap);
	
}

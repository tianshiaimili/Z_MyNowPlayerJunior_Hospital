package com.hua.gz.res;

import java.io.InputStream;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.Log;
import android.widget.ImageView;

import com.hua.gz.utils.NetUtils;

/**
 * Note:
 * If you want to detach bitmap from ImageView automatically, you can extend the default ImageView
 * and override the onDetachedFromWindow to null out the bitmaps in your ImageViews. 
 * 
 * @author AlfredZhong
 * @version 2012-07-30
 */
class ImageLoader {

	private static final String TAG = ImageLoader.class.getSimpleName();
	private Bitmap mLoadingBitmap;
	private static final int FADE_IN_TIME = 800;
	private boolean mFadeInBitmap;
	private static ColorDrawable mTransparentDrawable;
	private AbsImageCacheHelper helper;
	private int mMinWidthPixel;
	private BitmapFactory.Options mOptions;
	private ImageLoaderCallback mCallback;
	
	ImageLoader(AbsImageCacheHelper helper) {
		this.helper = helper;
		Log.e(TAG, TAG + " memory cache map size is " + this.helper.size());
		mTransparentDrawable = new ColorDrawable(android.R.color.transparent);
	}
	
    /**
     * Set placeholder bitmap that shows when the the background thread is running.
     *
     * @param bitmap
     */
	public void setLoadingImage(Bitmap bitmap) {
    	mLoadingBitmap = bitmap;
    }

    /**
     * Set placeholder bitmap that shows when the the background thread is running.
     *
     * @param resId
     */
	public void setLoadingImage(Resources res, int resId) {
    	mLoadingBitmap = null;
    	try {
    		// decodeResource will not cause exception, just return null if failed.
    		// But some custom ROMs, such as HTC, may throw Resources$NotFoundException, so we'd better try catch here.
    		mLoadingBitmap = BitmapFactory.decodeResource(res, resId);
    	} catch (Exception e) {
			Log.w(TAG, "setLoadingImage failed " + e);
		}
    }
    
	public Bitmap getLoadingImage() {
    	return mLoadingBitmap;
    }
    
    /**
     * If set to true, the image will fade-in once it has been loaded by the background thread.
     *
     * @param fadeIn
     */
	public void setImageFadeIn(boolean fadeIn) {
        mFadeInBitmap = fadeIn;
    }
    
	public boolean isImageFadeIn() {
    	return mFadeInBitmap;
    }
    
    /**
     * Called when the processing is complete and the final bitmap should be set on the ImageView.
     *
     * @param imageView
     * @param bitmap
     */
    protected void setImageBitmapWithFadeIn(ImageView imageView, Bitmap bitmap) {
        if (mFadeInBitmap) {
            // Transition drawable with a transparent drwabale and the final bitmap
            final TransitionDrawable td = new TransitionDrawable(new Drawable[] {
            		mTransparentDrawable, new BitmapDrawable(imageView.getResources(), bitmap) });
            imageView.setImageDrawable(td);
            td.startTransition(FADE_IN_TIME);
        } else {
            imageView.setImageBitmap(bitmap);
        }
    }
    
    public ImageLoaderCallback getCallback() {
		return mCallback;
	}

	public void setCallback(ImageLoaderCallback callback) {
		this.mCallback = callback;
	}

	void setLocalImage(ImageView imageView, Object toBeDecode, String key, int reqMinWidth, int defaultResId, boolean isFling) {
		if(imageView == null) {
			Log.w(TAG, "ImageView is null.");
			return;
		}
		imageView.setTag(key);
		Bitmap bitmap = helper.getImage(key);
		if (bitmap != null) {
			Log.d(TAG, "setLocalImage() found image from cache.");
			imageView.setImageBitmap(bitmap);
			return;
		} 
		if(isFling) {
			Log.v(TAG, "setLocalImage() enable lazy-load and fling is true now, no need to load image.");
			return;
		}
		try {
			// use default Bitmap.Options
			bitmap = helper.decodeScaledDownBitmap(toBeDecode, imageView.getResources(), reqMinWidth, getBitmapOptions());
		} catch(Exception e) {
			Log.w(TAG, "decodeScaledDownBitmap failed : " + e);
		}
		if(bitmap == null) {
			try {
				// set default image. Do NOT use getLoadingImage() to set for local images.
				imageView.setImageResource(defaultResId);
			} catch(Exception e) {
			}
		} else {
			// local images no need to save to file.
			if(mCallback != null) {
				bitmap = mCallback.onBitmapDecoded(bitmap);
			}
			imageView.setImageBitmap(bitmap);
			helper.putImage(key, bitmap, false);
		}
    }
    
    public void setResourceImage(ImageView imageView, int resId, int reqMinWidth, int defaultResId) {
    	// use res id + configuration to distinguish same id but different locale, orientation and so on.
    	setLocalImage(imageView, resId, resId + imageView.getContext().getResources().getConfiguration().toString(), reqMinWidth, defaultResId, false);
	}
	
    public void setAssetsImage(ImageView imageView, String assetFileName, int reqMinWidth, int defaultResId) {
    	InputStream is = null;
    	try {
    		is = imageView.getContext().getAssets().open(assetFileName);
		} catch (Exception e) {
			Log.w(TAG, "setAssetsImage failed : " + e);
		}
    	setLocalImage(imageView, is, assetFileName, reqMinWidth, defaultResId, false);
	}
	
    public void setStorageImage(ImageView imageView, String filePath, int reqMinWidth, int defaultResId) {
    	setLocalImage(imageView, filePath, filePath, reqMinWidth, defaultResId, false);
	}
	
    ///////////////////////// Displaying Bitmaps Efficiently /////////////////////////
    // 1. Read bitmap dimensions and type and load a scaled down version into memory with appropriate Bitmap.Config.
    //    Bitmap.Config: ARGB_8888(4 bytes), RGB_565(2 bytes), ARGB_4444(2 bytes, deprecated), ALPHA_8(1 byte)
    // 2. Processing bitmaps off the UI thread.
    // 3. Caching Bitmaps.
    // 4. Managing Bitmap Memory.
	
	/**
	 * Set request min width pixel to load a scaled down version into memory.
	 * 
	 * @param minWidthPixel
	 */
	public void setRequestMinWidth(int minWidthPixel) {
		mMinWidthPixel = minWidthPixel;
	}
	
	public int getRequestMinWidth() {
		return mMinWidthPixel;
	}
	
	/**
	 * Set appropriate Bitmap.Config to save memory occupation.
	 * 
	 * @param minWidthPixel
	 */
	public void setBitmapOptions(BitmapFactory.Options opt) {
		mOptions = opt;
	}
	
	public BitmapFactory.Options getBitmapOptions() {
		return mOptions;
	}
	
	// only expose to same package and sub-class.
	Bitmap loadBitmap(final String url, final int reqMinWidth) throws Exception {
 		byte[] data = NetUtils.getByteArray(url);
		// decode bitmap, use default Bitmap.Options
		return helper.decodeScaledDownBitmap(data, null, reqMinWidth, getBitmapOptions());
	}
	
}

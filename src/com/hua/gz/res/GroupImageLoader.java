package com.hua.gz.res;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.hua.nowplayerjunior.os.ScalingAsyncTask;

/**
 * A helper class to load image from Internet and refresh the ImageView in GroupView.
 * <p>Need android.permission.INTERNET, android.permission.WRITE_EXTERNAL_STORAGE
 * 
 * @author AlfredZhong
 * @version 2012-04-03
 * @version 2012-06-27, changed the strategy of finding ImageView with tag, added BitmapProcessor.
 * @version 2012-07-30, added ImageLoader and LazyLoadScrollListener.
 * @version 2012-07-31, changed AbsListView to ViewGroup.
 * @version 2013-12-25, support local images.
 */
public class GroupImageLoader extends ImageLoader {

	private static final String TAG = GroupImageLoader.class.getSimpleName();
	// Simple-GroupView RemoteGroupImageLoader
	private AbsImageCacheHelper mImageCacheHelper;
	private ViewGroup mViewGroup;
	private HashMap<String, DownloadTaskStatus> mTaskStatus;
	private boolean mIsTagUnique = false;
	private boolean mReloadIfFailed = true;
	// AbsListView RemoteGroupImageLoader lazy load
	private boolean mEnableLazyLoad;
	private int mImageViewId;
	private LazyLoadScrollListener mLazyLoadScrollListener;
	public static final int TYPE_URL = 0;
	public static final int TYPE_RESOURCES = 1;
	public static final int TYPE_STORAGE = 2;
	public static final int TYPE_ASSETS = 3;
	
	// TypeKey for local images(res, storage, assets).
	private String createTypeKey(int type, String key) {
		return type + "-" + key;
	}
	
	private int retrieveType(String typeKey) {
		try {
			String typeStr = typeKey.substring(0, 1);
			return Integer.valueOf(typeStr);
		} catch(Exception e) {
			return TYPE_URL;
		}
	}
	
	private String retrieveKey(String typeKey) {
		return typeKey.substring(2);
	}
	
	private boolean isFling() {
		return mEnableLazyLoad && mLazyLoadScrollListener != null && mLazyLoadScrollListener.isFling();
	}
	
	public void clearResource(){
		mImageCacheHelper = null;
		mViewGroup = null;
		
	}
	
	@Override
	void setLocalImage(ImageView imageView, Object toBeDecode, String key, int reqMinWidth, int defaultResId, boolean isFling) {
		int type;
		if(toBeDecode instanceof Integer) {
			type = TYPE_RESOURCES;
		} else if(toBeDecode instanceof InputStream) {
			type = TYPE_ASSETS;
		} else {
			type = TYPE_STORAGE;
		}
		key = createTypeKey(type, key);
		super.setLocalImage(imageView, toBeDecode, key, reqMinWidth, defaultResId, isFling());
	};

	/**
	 * @param helper the helper to cache images
	 * @param view such as ListView and GridView
	 */
	GroupImageLoader(AbsImageCacheHelper helper, ViewGroup view) throws IllegalArgumentException {
		super(helper);
		mImageCacheHelper = helper;
		mViewGroup = view;
		if(mImageCacheHelper == null || mViewGroup == null) {
			throw new IllegalArgumentException(TAG + " parameters can not be null.");
		}
		mTaskStatus = new HashMap<String, DownloadTaskStatus>();
	}
	
	/**
	 * Set true if the view tag is unique to every item. Default is false.
	 * @param unique
	 */
	public void setImageViewTagUnique(boolean unique) {
		mIsTagUnique = unique;
	}
	
	/**
	 * Get the bitmap from the Internet with the specified URL and set the bitmap for the ImageView asynchronously.
	 * Note that don't set tag for the ImageView outside and not to call this function in a thread which not has view root.
	 * 
	 * @param imageView
	 * @param url
	 * @param position just to use position to log currently
	 */
	public void setRemoteImage(ImageView imageView, String url, int position) {
		if(imageView == null) {
			Log.w(TAG, "setRemoteImage() imageView == null.");
			return;
		}
		imageView.setTag(url);
		if(url == null || url.equals("")) {
			Log.w(TAG, "setRemoteImage() url == null or url is empty.");
			imageView.setImageBitmap(getLoadingImage());
			return;
		}
		Bitmap bitmap = mImageCacheHelper.getImage(url);
		if (bitmap != null) {
			Log.d(TAG, "setRemoteImage() found image from cache.");
			imageView.setImageBitmap(bitmap);
			return;
		}
		imageView.setImageBitmap(getLoadingImage());
		if (isFling()) {
			// No need to load image when lazy load is set true and the ListView is fling(not touch scroll).
			Log.v(TAG, "AbsListView is fling, no need to load remote image.");
			return;
		}
		// bitmap is null, that means never download or downloading or bitmap has been GC(no local copy) or download failed.
		DownloadTaskStatus status = mTaskStatus.get(url);
		boolean needNewTask = false;
		if(status != null) {
			if(status == DownloadTaskStatus.PENDING || status == DownloadTaskStatus.RUNNING) {
				// downloading, not need to run another task to download the same image.
				Log.v(TAG, "Position " + position + " is downloading. No need to load another one.");
			} else if(status == DownloadTaskStatus.SUCCESS) {
				// download success but image not found, that means bitmap has been GC(no local copy).
				Log.w(TAG, "position " + position + " has been GC. Reload it.");
				needNewTask = true;
			} else if(status == DownloadTaskStatus.FAILED) {
				// download failed.
				if(mReloadIfFailed) {
					Log.w(TAG, "position " + position + " download failed. Reload it.");
					mTaskStatus.remove(url);
					needNewTask = true;
				} else {
					Log.d(TAG, "position " + position + " download failed. ReloadIfFailed false, no need to reload it.");
				}
			}
		} else {
			// never download.
			Log.w(TAG, "position " + position + " never download. Load it.");
			needNewTask = true;
		}
		if(needNewTask) {
			Log.d(TAG, "setRemoteImage() for position " + position + " with url " + url);
			new ImageDownloadTask(mViewGroup, url).execute();
		}
	}
	
	/**
	 * Call this if you don't want to load image when the AbsListView is fling.
	 * Note that only AbsListView(ListView & GridView) can use this feather.
	 * 
	 * @param imageViewId the ImageView resource id.
	 */
	public void enableLazyLoad(int imageViewId) throws RuntimeException {
		if(mViewGroup instanceof AbsListView) {
			AbsListView absListView = (AbsListView)mViewGroup;
			mImageViewId = imageViewId;
			mEnableLazyLoad = true;
			mLazyLoadScrollListener = new LazyLoadScrollListener() {
				@Override
				protected void updateView(AbsListView view, View convertView, int position) {
					// if there are header or footer in ListView, convertView.findViewById(mImageViewId) will return null.
					// ImageView img = (ImageView) null; will return null, won't have NullPointerException.
					ImageView img = (ImageView) convertView.findViewById(mImageViewId);
					if(img != null) {
						String url = (String) img.getTag();
						int type = retrieveType(url);
						switch(type) {
						case TYPE_URL:
							setRemoteImage(img, url, position);
							break;
						case TYPE_RESOURCES:
							setResourceImage(img, Integer.valueOf(retrieveKey(url)), getRequestMinWidth(), 0);
							break;
						case TYPE_STORAGE:
							setStorageImage(img, retrieveKey(url), getRequestMinWidth(), 0);
							break;
						case TYPE_ASSETS:
							setAssetsImage(img, retrieveKey(url), getRequestMinWidth(), 0);
							break;
						}
					}
				}
			};
			absListView.setOnScrollListener(mLazyLoadScrollListener);
			Log.w(TAG, "Enable lazy load.");
		} else {
			throw new RuntimeException("ViewGroup is not an AbsListView instance, can not setOnScrollListener()");
		}
	}
	
	/**
	 * Disable lazy load.
	 * Note that only AbsListView(ListView & GridView) can use this feather.
	 */
	public void disableLazyLoad() throws RuntimeException {
		if(mViewGroup instanceof AbsListView) {
			AbsListView absListView = (AbsListView)mViewGroup;
			mEnableLazyLoad = false;
			absListView.setOnScrollListener(null);
			Log.w(TAG, "Disable lazy load.");
		} else {
			throw new RuntimeException("ViewGroup is not an AbsListView instance, can not setOnScrollListener()");
		}
	}
	
	/**
	 * Whether loader will reload the image if it download failed.
	 */
    public boolean isReloadIfFailed() {
		return mReloadIfFailed;
	}

    /**
     * Set true(default false) if you want to reload image if it download failed.
     * 
     * @param mReloadIfFailed
     */
	public void setReloadIfFailed(boolean reloadIfFailed) {
		this.mReloadIfFailed = reloadIfFailed;
	}

	/**
     * Indicates the current status of the ImageView download task. 
     * Each status will be set only once during the lifetime of a task.
     */
    private enum DownloadTaskStatus {
        /**
         * Indicates that the task has not been executed yet.
         */
        PENDING,
        /**
         * Indicates that the task is running.
         */
        RUNNING,
        /**
         * Indicates that the task has finished.
         */
        SUCCESS,
        /**
         * Indicates that the task has finished.
         */
        FAILED,
    }
    
    /**
     * A helper class to load image from the Internet and cached the bitmap data with its URL as key and refresh the ImageView.
     * 
     * @author AlfredZhong
     * @version 2012-04-03
     * @version 2012-06-27, changed the strategy of finding ImageView with tag, added BitmapProcessor.
     * @version 2012-07-25, used ViewGroup to refer to the parent view.
     * @version 2012-07-28, used WeakReference to ViewGroup to ensure the ViewGroup can be garbage collected.
     */
    private class ImageDownloadTask extends ScalingAsyncTask<Void, Void, Bitmap> {

    	private String url;
    	private WeakReference<ViewGroup> viewGroupReference;
    	
    	public ImageDownloadTask(ViewGroup view, String url) {
    		this.url = url;
    		/*
    		 * The WeakReference to the View ensures that the AsyncTask does not prevent the View 
    		 * and anything it references from being garbage collected.
    		 */
    		this.viewGroupReference = new WeakReference<ViewGroup>(view);
    		mTaskStatus.put(url, DownloadTaskStatus.PENDING);
    	}

    	@Override
    	protected Bitmap doInBackground(Void... params) {
    		mTaskStatus.put(this.url, DownloadTaskStatus.RUNNING);
    		// If it is FixedAsyncTask, check tag to determine whether the ImageView has been reuse before the task execute.
    		Bitmap bitmap = null;
         	try {
         		bitmap = loadBitmap(this.url, getRequestMinWidth());
    			if(getCallback() != null) {
    				bitmap = getCallback().onBitmapDecoded(bitmap);
    			}
    		} catch (Exception e) {
    			/*
    			 * Be aware when your code catch block, if get exception here,
    			 * the activity may crash without popping up FC dialog,
    			 * just logcat message "thread exiting with uncaught exception".
    			 * 
    			 * You should check Fire Wall if you get "java.net.SocketException: The operation timed out"
    			 * but network connection is fine.
    			 */
    			Log.w(TAG, TAG + " load bitmap failed : " + e + "\nurl : " + this.url);
    		}
         	if(bitmap != null && mImageCacheHelper != null) {
    			// put the bitmap to image cache helper.
         		// about 10-100 ms for several MB bitmap, process it off UI thread.
    			mImageCacheHelper.putImage(url, bitmap, true);
         	}
    		return bitmap;
    	}
    	
    	@Override
    	protected void onPostExecute(Bitmap bitmap) {
    		if(bitmap != null) {
    			// update status.
    			mTaskStatus.put(this.url, DownloadTaskStatus.SUCCESS);
    			/*
    			 * The ViewGroup may no longer exist, if for example, 
    			 * the user navigates away from the activity or if a configuration change happens before the task finishes.
    			 */
    			ViewGroup viewGroup = this.viewGroupReference.get();
    			if(viewGroup != null){
    				long start = System.currentTimeMillis();
    				if(mIsTagUnique) {
    					setImageView(viewGroup, this.url, bitmap);
    					Log.v(TAG, "FindViewWithTag unique cost " + (System.currentTimeMillis() - start) + " ms.");
    				} else {
    					/*
    					 * View.findViewWithTag(), if more than two view bind with the same tag, will return the first match view.
    					 * So, iterate all children of AbsListView, and find the view with same URL.
    					 * Cost several ms to iterate all children.
    					 */
    					int childCount = viewGroup.getChildCount();
    					for (int i = 0; i < childCount; i++) {
    						setImageView(viewGroup.getChildAt(i), this.url, bitmap);
    					}
    					Log.v(TAG, "FindViewWithTag all the children cost " + (System.currentTimeMillis() - start) + " ms.");
    				}
    			} else {
    				Log.w(TAG, "ViewGroup has been garbage collected or bitmap is null.");
    			}
    			viewGroup = null;
    		} else {
    			// update status.
    			mTaskStatus.put(this.url, DownloadTaskStatus.FAILED);
    		}
    		this.url = null;
    		this.viewGroupReference = null;
    	}
    	
    	private void setImageView(View parent, String tag, Bitmap bmp) {
    		View viewWithUrlTag = parent.findViewWithTag(tag);
    		if (viewWithUrlTag != null && viewWithUrlTag instanceof ImageView) {
    			ImageView iv = (ImageView) viewWithUrlTag;
    			if(iv != null) {
    				setImageBitmapWithFadeIn(iv, bmp);
    			}
    		} 
    	}

    } // end of inner class

} // end of public class

package com.hua.gz.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * A flexible ImageView which can support :
 * 1. Image event callbacks.
 * 2. Auto resize itself with the giving width or height to keep the ratio of Bitmap.
 * 3. Fixed size ImageView.
 * Note that this ImageView can only call setImageBitmap() or setImageDrawable() or setImageResource() to support the features.
 * 
 * @author AlfredZhong
 * @date 2013-02-20
 */
@SuppressLint("WrongCall")
public class FlexibleImageView extends ImageView {

	private static final String TAG = FlexibleImageView.class.getSimpleName();
	private LayoutStrategy mLayoutStrategy;
	private boolean mAfterOnLayoutCalled;
	private ImageEventListener mImageEventListener;

	public FlexibleImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public FlexibleImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FlexibleImageView(Context context) {
		super(context);
	}

	private static boolean isDrawableReady(Drawable drawable) {
		if(drawable.getIntrinsicWidth() > 0 && drawable.getIntrinsicHeight() > 0)
			return true;
		return false;
	}
	
	@Override
	public void setImageBitmap(Bitmap bm) {
		// setImageBitmap() will call setImageDrawable(), no need to handle.
		super.setImageBitmap(bm);
	}
	
	@Override
	public void setImageResource(int resId) {
		setImageDrawable(getContext().getResources().getDrawable(resId));
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		if(!mAfterOnLayoutCalled) {
			if(mImageEventListener != null && isDrawableReady(drawable)) {
				mImageEventListener.onDrawableReady(drawable);
			}
		}
		if(mLayoutStrategy != null) {
			if(!mLayoutStrategy.onLayout(drawable))
				super.setImageDrawable(drawable);
		} else {
			super.setImageDrawable(drawable);
		}
		if(mAfterOnLayoutCalled) {
			if(mImageEventListener != null && isDrawableReady(drawable)) {
				mImageEventListener.onDrawableReady(drawable);
			}
		}
	}
	
	public int getLayoutWidth() {
		return getLayoutParams().width;
	}
	
	public int getLayoutHeight() {
		return getLayoutParams().height;
	}
	
	public static interface ImageEventListener {
		void onDrawableReady(Drawable drawable);
	}
	
	/**
	 * @param afterOnLayoutCalled whether wait for onLayout() called.
	 * @param listener
	 */
	public void setImageEventListener(boolean afterOnLayoutCalled, ImageEventListener listener) {
		mAfterOnLayoutCalled = afterOnLayoutCalled;
		mImageEventListener = listener;
	}
	
	/**
	 * @param fitWidth whether the ImageView fit width, or fit height
	 * @param rawSize if true, rawSize is width, otherwise height.
	 */
	public void setLayout(boolean fitWidth, int rawSize) {
		mLayoutStrategy = new FitRatioLayoutStrategy(fitWidth, rawSize);
	}
	
	/**
	 * @param afterDrawableReady whether wait for drawable is ready, if false will occupy the fixed size no matter drawable is ready or not.
	 * @param fixedWidth
	 * @param fixedHeight
	 */
	public void setLayout(boolean afterDrawableReady, int fixedWidth, int fixedHeight) {
		mLayoutStrategy = new FixedSizeLayoutStrategy(afterDrawableReady, fixedWidth, fixedHeight);
	}
	
	private static interface LayoutStrategy {
		/**
		 * Return false to call super.setImageDrawable(drawable) after onLayout, true otherwise.
		 * @param drawable
		 * @return
		 */
		boolean onLayout(Drawable drawable);
	}
	
	private class FitRatioLayoutStrategy implements LayoutStrategy {
		
		private boolean mEnableFitWidth;
		private int mFitWidth;
		private int mFitHeight;
		
		private FitRatioLayoutStrategy(boolean fitWidth, int rawSize) {
			mEnableFitWidth = fitWidth;
			if(mEnableFitWidth) {
				mFitWidth = rawSize;
			} else {
				mFitHeight = rawSize;
			}
		}
		
		@Override
		public boolean onLayout(Drawable drawable) {
			int width = drawable.getIntrinsicWidth();
			int height = drawable.getIntrinsicHeight();
			if(width > 0 && height > 0) {
				// Note: For an of ImageView the default ScaleType value is FIT_CENTER, but for a Button is FIT_XY.
				// Drawable is ready to use. Resize drawable dimensions here.
				if(mEnableFitWidth && mFitWidth > 0) {
					getLayoutParams().width = mFitWidth;
					getLayoutParams().height = getFitHeight(mFitWidth, width, height);
				} else if(!mEnableFitWidth && mFitHeight > 0) {
					getLayoutParams().height = mFitHeight;
					getLayoutParams().width = getFitWidth(mFitHeight, width, height);
				}
				// android.util.Log.d(TAG, "FitRatioLayoutStrategy resize : (" + width + ", " + height + ") -> (" + getLayoutParams().width + ", " + getLayoutParams().height + ").");
			} else {
				android.util.Log.v(TAG, "Drawable is NOT ready to use : " + width + ", " + height);
			}
			return false;
		}
		
	} // end of inner class.
	
	private class FixedSizeLayoutStrategy implements LayoutStrategy {
		
		private boolean mAfterDrawableReady;
		private int mFixedWidth;
		private int mFixedHeight;
		
		private FixedSizeLayoutStrategy(boolean afterDrawableReady, int width, int height) {
			mAfterDrawableReady = afterDrawableReady;
			mFixedWidth = width;
			mFixedHeight = height;
		}
		
		@Override
		public boolean onLayout(Drawable drawable) {
			boolean resize = !mAfterDrawableReady;
			if(mAfterDrawableReady && isDrawableReady(drawable)) {
				resize = true;
			} 
			if(resize) {
				// Note: For an of ImageView the default ScaleType value is FIT_CENTER, but for a Button is FIT_XY.
				// android.util.Log.d(TAG, "FixedSizeLayoutStrategy resize : (" + mFixedWidth + ", " + mFixedHeight + ")");
				getLayoutParams().width = mFixedWidth;
				getLayoutParams().height = mFixedHeight;
			}
			return false;
		}
		
	} // end of inner class.
	
	public static int getFitHeight(int fitWidth, int ratioWidth, int ratioHeight) {
		if(ratioWidth == 0) {
			Log.w(TAG, "getFitHeight() ratioWidth is 0, return fitHeight 0.");
			return 0;
		}
		return fitWidth * ratioHeight / ratioWidth;
	}
	
	public static int getFitWidth(int fitHeight, int ratioWidth, int ratioHeight) {
		if(ratioHeight == 0) {
			Log.w(TAG, "getFitWidth() ratioHeight is 0, return fitWidth 0.");
			return 0;
		}
		return fitHeight * ratioWidth / ratioHeight;
	}

}

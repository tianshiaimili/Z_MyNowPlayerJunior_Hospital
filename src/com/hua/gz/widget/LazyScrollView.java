package com.hua.gz.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * A ScrollView that supports OnScrollListener to detect whether the view is on bottom or on top.
 * 
 * @author AlfredZhong
 * @version 2013-10-22
 */
public class LazyScrollView extends ObservableScrollView {

	private static final String TAG = LazyScrollView.class.getSimpleName();
	private OnScrollListener mOnScrollListener;
	private View mContentView;
	private int mDeviation = 10;
	
	public LazyScrollView(Context context) {
		super(context);
		init();
	}

    public LazyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public LazyScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    
    private void init() {
    	mContentView = getContentView();
    	super.setOnScrollEndedListener(new ObservableScrollView.OnScrollEndedListener() {
			@Override
			public void onScrollEnded(ObservableScrollView view, int x, int y) {
				if(mContentView == null) {
					mContentView = getContentView();
				}
				if(mOnScrollListener != null && mContentView != null) {
					mOnScrollListener.onScrollEnded();
					if (mContentView.getMeasuredHeight() <= getScrollY() + getHeight() + mDeviation) {
						Log.d(TAG, TAG +" onBottom");
						mOnScrollListener.onBottom();
					} else if (getScrollY() == 0) {
						Log.d(TAG, TAG +" onTop");
						mOnScrollListener.onTop();
					}
				}
			}
		});
    }
    
    public void setOnScrollEndedListener(OnScrollEndedListener l) {
    	// empty implement to avoid user to replace the listener.
    }
    
    public void setDeviation(int deviation) {
    	if(deviation <= 0)
    		return;
    	mDeviation = deviation;
    }
    
	public interface OnScrollListener {
		void onTop();
		void onBottom();
		void onScrollEnded();
	}

	public void setOnScrollListener(OnScrollListener onScrollListener) {
		mOnScrollListener = onScrollListener;
	}

}

package com.hua.gz.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * A ScrollView subclass which supports OnScrollChangedListener, OnScrollEndedListener.
 * 
 * @author AlfredZhong
 * @version 2012-10-17
 * @since API Level 3
 */
public class ObservableScrollView extends ScrollView {

	private static final String TAG = ObservableScrollView.class.getSimpleName();
    private OnScrollChangedListener mOnScrollChangedListener;
	private OnScrollEndedListener mOnScrollEndedListener;
	private int initialPosition;
	private static final int CHECK_INTERVAL = 120; // Do NOT less than 50 ms.
	private Runnable mScrollerTask;
	private boolean mEnableScrolling = true;

    public ObservableScrollView(Context context) {
        super(context);
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public ObservableScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void fling(int velocityX) {
    	// Change velocityX to change initial velocity in the X direction.
    	super.fling(velocityX);
    }
    
    @Override
    public void setVerticalScrollBarEnabled(boolean verticalScrollBarEnabled) {
    	// verticalScrollBarEnabled default true. 
    	super.setVerticalScrollBarEnabled(verticalScrollBarEnabled);
    }
	
    /**
     * Returns the content view.
     * 
     * @return the content view or null if the content view does not exist within ScrollView.
     */
    public View getContentView() {
    	return getChildAt(0);
    }
    
    public void setScrollingEnabled(boolean enable) {
    	mEnableScrolling = enable;
    }
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
    	if(!mEnableScrolling) {
    		return false;
    	}
    	return super.onInterceptTouchEvent(ev);
    }
    
    public interface OnScrollChangedListener {
    	void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy);
    }
    
    public void setOnScrollChangedListener(OnScrollChangedListener l) {
    	mOnScrollChangedListener = l;
    }
    
    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        // Only call OnScrollListener.onScrollChanged() when OnScrollListener is not null.
        if(mOnScrollChangedListener != null) {
        	// Log.v(TAG, "onScrollChanged : x = " + x + ", y = " + y);
        	mOnScrollChangedListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }
    
    public interface OnScrollEndedListener {
        void onScrollEnded(ObservableScrollView view, int x, int y);
    }
    
    public void setOnScrollEndedListener(OnScrollEndedListener l) {
    	mOnScrollEndedListener = l;
    	// Only new mScrollerTask once when OnScrollListener enabled.
    	if(mScrollerTask == null) {
        	mScrollerTask = new Runnable() {
        		public void run() {
        	        // Log.v(TAG, "Run check task.");
        	        // getScrollX() if ScrollView is horizontal, getScrollY() if ScrollView is vertical.
        			int newPosition = getScrollY();
        			if (initialPosition == newPosition) {
        				// scroll stopped.
        				Log.d(TAG, "****** ScrollView onScrollEnded. ******");
        				if (mOnScrollEndedListener != null) {
        					mOnScrollEndedListener.onScrollEnded(ObservableScrollView.this, getScrollX(), getScrollY());
        				}
        			} else {
        				// getScrollX() if ScrollView is horizontal, getScrollY() if ScrollView is vertical.
        				initialPosition = getScrollY();
        				ObservableScrollView.this.postDelayed(mScrollerTask, CHECK_INTERVAL);
        			}
        		}
        	};
    	}
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
    	// Only call OnScrollListener.onScrollChanged() when OnScrollListener is not null.
    	if(mOnScrollEndedListener != null) {
            if (ev.getAction() == MotionEvent.ACTION_UP) {
            	// Log.v(TAG, "===== ScrollView start checking. =====");
				// getScrollX() if ScrollView is horizontal, getScrollY() if ScrollView is vertical.
				initialPosition = getScrollY();
                // remove previous check task, in case it is running since you scroll too quick.
                removeCallbacks(mScrollerTask);
                postDelayed(mScrollerTask, CHECK_INTERVAL);
            }
    	}
    	return super.onTouchEvent(ev);
    }

}
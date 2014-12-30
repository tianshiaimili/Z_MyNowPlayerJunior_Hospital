package com.hua.gz.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;

/**
 * A HorizontalScrollView subclass which supports OnScrollChangedListener, OnScrollEndedListener.
 * 
 * @author AlfredZhong
 * @version 2012-10-17
 * @since API Level 3
 */
public class ObservableHorizontalScrollView extends HorizontalScrollView {

	private static final String TAG = ObservableHorizontalScrollView.class.getSimpleName();
	private OnScrollChangedListener mOnScrollChangedListener;
	private OnScrollEndedListener mOnScrollEndedListener;
	private int initialPosition;
	private static final int CHECK_INTERVAL = 120; // Do NOT less than 50 ms.
	private Runnable mScrollerTask;
	
	public ObservableHorizontalScrollView(Context context) {
		super(context);
	}
	
	public ObservableHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ObservableHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
    @Override
    public void fling(int velocityX) {
    	// Change velocityX to change initial velocity in the X direction.
    	super.fling(velocityX);
    }
    
    @Override
    public void setHorizontalScrollBarEnabled(boolean horizontalScrollBarEnabled) {
    	// horizontalScrollBarEnabled default true. 
    	super.setHorizontalScrollBarEnabled(horizontalScrollBarEnabled);
    }
	
    /**
     * Returns the content view.
     * 
     * @return the content view or null if the content view does not exist within ScrollView.
     */
    public View getContentView() {
    	return getChildAt(0);
    }
    
    public interface OnScrollChangedListener {
        void onScrollChanged(ObservableHorizontalScrollView view, int x, int y, int oldx, int oldy);
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
        void onScrollEnded(ObservableHorizontalScrollView view, int x, int y);
    }
    
    public void setOnScrollEndedListener(OnScrollEndedListener l) {
    	mOnScrollEndedListener = l;
    	// Only new mScrollerTask once when OnScrollListener enabled.
    	if(mScrollerTask == null) {
        	mScrollerTask = new Runnable() {
        		public void run() {
        	        // Log.v(TAG, "Run check task.");
        	        // getScrollX() if ScrollView is horizontal, getScrollY() if ScrollView is vertical.
        			int newPosition = getScrollX();
        			if (initialPosition == newPosition) {
        				// scroll stopped.
        				Log.d(TAG, "****** ScrollView onScrollEnded. ******");
        				if (mOnScrollEndedListener != null) {
        					mOnScrollEndedListener.onScrollEnded(ObservableHorizontalScrollView.this, getScrollX(), getScrollY());
        				}
        			} else {
        				// getScrollX() if ScrollView is horizontal, getScrollY() if ScrollView is vertical.
        				initialPosition = getScrollX();
        				ObservableHorizontalScrollView.this.postDelayed(mScrollerTask, CHECK_INTERVAL);
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
				initialPosition = getScrollX();
                // remove previous check task, in case it is running since you scroll too quick.
                removeCallbacks(mScrollerTask);
                postDelayed(mScrollerTask, CHECK_INTERVAL);
            }
    	}
    	return super.onTouchEvent(ev);
    }
	
}

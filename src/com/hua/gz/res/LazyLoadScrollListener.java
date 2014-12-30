package com.hua.gz.res;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import android.view.View;
import android.widget.AbsListView;

/**
 * A sub class of AbsListView.OnScrollListener that only load views when AbsListView is on idle state.
 * 
 * @author AlfredZhong
 * @version 2012-07-30
 */
public abstract class LazyLoadScrollListener implements AbsListView.OnScrollListener {
	
	private static final String TAG = LazyLoadScrollListener.class.getSimpleName();
	private int currentFirst;
	private int currentLast;
	private int previousFirst = -1;
	private int previousLast = -1;
	private int mVisibleItemCount;
	private int mScrollState = -1;
	
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {	
		currentFirst = firstVisibleItem;
		// visibleItemCount starts from 1(like size()), so we should minus 1.
		currentLast = firstVisibleItem + visibleItemCount - 1;
		if(currentLast >= totalItemCount) {
			currentLast = totalItemCount - 1;
		}
		mVisibleItemCount = visibleItemCount;
	}
	
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
			// The view is not scrolling. Note navigating the list using the trackball counts as 
			// being in the idle state since these transitions are not animated.
			case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
				Log.w(TAG, "SCROLL_STATE_IDLE");
				mScrollState = SCROLL_STATE_IDLE;
				// remove some items that it is showing in previous and current.
				List<Integer> currentPositions = getMixedBounds(previousFirst, previousLast, currentFirst, currentLast);
				if(currentPositions.size() == 0) {
					Log.d(TAG, "No need to refresh.");
					return;
				}
				// update views.
				// Note that position is absolute, visible index is relative.
				// position and visible index are starts from 0, visible count starts from 1.
				int visibleIndex = 0;
				for(int position : currentPositions) {
					visibleIndex = position - currentFirst; // because currentFirst is visible index 0.
					Log.v(TAG, "Update position " + position + ", visibleItemCount " + mVisibleItemCount + ", visibleIndex " + visibleIndex);
					View convertView = view.getChildAt(visibleIndex);
					updateView(view, convertView, position);
				}
				// reset previous to current.
				previousFirst = currentFirst;
				previousLast = currentLast;
				break;
			// The user is scrolling using touch, and their finger is still on the screen.
			// ** If user change the finger and keep their finger on the screen, the scroll state is scroll.
			case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				Log.v(TAG, "SCROLL_STATE_TOUCH_SCROLL");
				mScrollState = SCROLL_STATE_TOUCH_SCROLL;
				// No need to refresh views here since AbsListView state callback only triggered when it changed.
				// So even AbsListView is on scroll state for a long distance, SCROLL_STATE_TOUCH_SCROLL only trigger when it begins scroll.
				break;
			// The user had previously been scrolling using touch and had performed a fling.
			// The animation is now coasting to a stop.
			case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
				Log.w(TAG, "SCROLL_STATE_FLING");
				mScrollState = SCROLL_STATE_FLING;
				break;
		}
	}
	
	// Only cost several million seconds.
	private List<Integer> getMixedBounds(int previousFirst, int previousLast, int currentFirst, int currentLast) {
		Log.v(TAG, "previousFirst : " + previousFirst + ", previousLast : " + previousLast);
		Log.v(TAG, "currentFirst : " + currentFirst + ", currentLast : " + currentLast);
		List<Integer> previous = new ArrayList<Integer>();
		for(; previousFirst<=previousLast; previousFirst++) {
			previous.add(previousFirst);
		}
		List<Integer> current = new ArrayList<Integer>();
		for(; currentFirst<=currentLast; currentFirst++) {
			current.add(currentFirst);
		}
		current.removeAll(previous);
		return current;
	}
	
	public int getScrollState() {
		return mScrollState;
	}
	
	public boolean isFling() {
		return mScrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING;
	}
	
	protected abstract void updateView(AbsListView view, View convertView, int position);
	
}

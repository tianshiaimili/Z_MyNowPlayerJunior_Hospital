  package com.hua.gz.widget;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

import com.hua.activity.R;
import com.hua.gz.utils.AnimUtils;

/**
 * @author AlfredZhong
 * @version 2013-11-04
 */
public class RefreshableLinearLayout extends LinearLayout {

	private static final String TAG = RefreshableLinearLayout.class.getSimpleName();
	// refresh bar UI fields.
	private View refreshView;
	private ImageView refreshArrow;
	private ProgressBar progressBar;
	private TextView refreshHintTextView;
	private TextView refreshTimeTextView;
	private static final String REFRESH_HINT_PULL_TO_REFRESH = "pull to refresh";
	private static final String REFRESH_HINT_RELEASE_WILL_REFRESH = "release will refresh";
	private String pullToRefreshHint, releaseWillReleaseHint;
	private static final String LAST_REFRESH_TIME_HINT_PREFIX = "last refresh time: ";
	private String lastRefreshTimeHintPrefix;
	private String lastRefreshTime;
	private SimpleDateFormat dateFormat;
	// refresh bar control fields.
	private static final int REFRESH_TARGET_TOP_DP = -60;
	private int refreshTargetTop;
	private Scroller scroller;
	private int lastY;
	private boolean isRefreshing = false;
	private RefreshListener refreshListener;
	private RotateAnimation downToUpAnimation, upToDownAnimation;
	private boolean isUp = false;
	private boolean mEnableScrolling = true;
	
	public RefreshableLinearLayout(Context context) {
		super(context);
		initRefreshHeader();
	}
	
	public RefreshableLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initRefreshHeader();
	}
	
	private void initRefreshHeader() {
		scroller = new Scroller(getContext());
		refreshView = LayoutInflater.from(getContext()).inflate(R.layout.pull_to_refresh_bar, null);
		refreshArrow = (ImageView) refreshView.findViewById(R.id.pull_to_refresh_bar_arrow);
		progressBar = (ProgressBar) refreshView.findViewById(R.id.pull_to_refresh_bar_progress);
		refreshHintTextView = (TextView) refreshView.findViewById(R.id.pull_to_refresh_bar_refresh_hint);
		refreshTimeTextView = (TextView) refreshView.findViewById(R.id.pull_to_refresh_bar_refresh_time);
		refreshTargetTop = (int) (getContext().getResources().getDisplayMetrics().density * REFRESH_TARGET_TOP_DP);
		LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, -refreshTargetTop);
		lp.topMargin = refreshTargetTop;
		lp.gravity = Gravity.CENTER;
		addView(refreshView, lp);
		downToUpAnimation = AnimUtils.getUpsidedownAnimation(false, 250);
		upToDownAnimation = AnimUtils.getUpsidedownAnimation(true, 250);
		pullToRefreshHint = REFRESH_HINT_PULL_TO_REFRESH;
		releaseWillReleaseHint = REFRESH_HINT_RELEASE_WILL_REFRESH;
		lastRefreshTimeHintPrefix = LAST_REFRESH_TIME_HINT_PREFIX;
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
	}
	
	public interface RefreshListener {
		public void onRefresh(RefreshableLinearLayout layout);
	}

	public void setRefreshListener(RefreshListener listener) {
		this.refreshListener = listener;
	}
	
	public void setRefreshHintPullToRefresh(String hint) {
		pullToRefreshHint = hint;
	}
	
	public void setRefreshHintReleaseToRefresh(String hint) {
		releaseWillReleaseHint = hint;
	}
	
	public void setRefreshHintLastRefreshTimePrefix(String prefix) {
		lastRefreshTimeHintPrefix = prefix;
	}
	
	public void setRefreshTime(Date date) {
		lastRefreshTime = dateFormat.format(date);
	}
	
	public void closeRefreshBar() {
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.refreshView.getLayoutParams();
		int i = lp.topMargin;
		refreshArrow.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.INVISIBLE);
		scroller.startScroll(0, i, 0, refreshTargetTop);
		invalidate();
		isRefreshing = false;
	}
	
    public void setScrollingEnabled(boolean enable) {
    	mEnableScrolling = enable;
    }
	
	/*
	 * @see android.view.ViewGroup#onInterceptTouchEvent(android.view.MotionEvent)
	 * 
	 * Events will be received in the following order:
	 * 1. You will receive the down event here.
	 * 2. The down event will be handled either by a child of this view group, or given to your own onTouchEvent() method to handle; 
	 *    this means you should implement onTouchEvent() to return true, so you will continue to see the rest of the gesture (instead of looking for a parent view to handle it). 
	 *    Also, by returning true from onTouchEvent(), you will not receive any following events in onInterceptTouchEvent() and all touch processing must happen in onTouchEvent() like normal.
	 * 3. For as long as you return false from this function, each following event (up to and including the final up) will be delivered first here and then to the target's onTouchEvent().
	 * 4. If you return true from here, you will not receive any following events: the target view will receive the same event but with the action MotionEvent.ACTION_CANCEL, 
	 *    and all further events will be delivered to your onTouchEvent() method and no longer appear here.
	 * 
	 * Explain and notes:
	 * 1. Chain of Responsibility Design Pattern, to manage touch event on ViewGroup before ViewGroup onTouchEvent() and child view onTouchEvent().
	 * 2. How touch event dispatching onInterceptTouchEvent() and ViewGroup onTouchEvent() and child view onTouchEvent() 
	 *    relies on onInterceptTouchEvent() return-value. Especially the DOWN event will affect MOVE event and UP event.
	 * 3. onInterceptTouchEvent() return-value decides onTouchEvent() receiver; onTouchEvent() return-value decides whether event should pass to parent to handle.
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent e) {
    	if(!mEnableScrolling) {
    		return false;
    	}
		int action = e.getAction();
		int y = (int) e.getRawY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			lastY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			int moveDistance = y - lastY;
			lastY = y;
			if (moveDistance > 6 && canScroll()) {
				// We should do movement here, no need to pass to child view onTouchEvent(), handle it onTouchEvent() here itself.
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			break;
		case MotionEvent.ACTION_CANCEL:
			break;
		}
		return false;
	}
	
	/*
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int y = (int) event.getRawY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			lastY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			int moveDistance = y - lastY;
			if (((moveDistance < 6) && (moveDistance > -1)) || (!isRefreshing)) {
				doMovement(moveDistance);
			}
			lastY = y;
			break;
		case MotionEvent.ACTION_UP:
			LinearLayout.LayoutParams lp = (LayoutParams) refreshView.getLayoutParams();
			if (lp.topMargin > 0) {
				// refresh
				int i = lp.topMargin;
				// remember to clear refresh arrow animation here, otherwise the arrow visibility will be visible after animation end.
				refreshArrow.clearAnimation();
				refreshArrow.setVisibility(View.INVISIBLE);
				progressBar.setVisibility(View.VISIBLE);
				refreshTimeTextView.setVisibility(View.VISIBLE);
				refreshHintTextView.setVisibility(View.VISIBLE);
				scroller.startScroll(0, i, 0, 0 - i);
				invalidate();
				if (refreshListener != null) {
					refreshListener.onRefresh(this);
					isRefreshing = true;
				}
			} else {
				// return to init state.
				int i = lp.topMargin;
				scroller.startScroll(0, i, 0, refreshTargetTop);
				invalidate();
			}
			break;
		case MotionEvent.ACTION_CANCEL:
			Log.d(TAG, TAG + " onTouchEvent ACTION_CANCEL");
			break;
		}
		return true;
	}

	/**
	 * Whether the child view is on the top which can scroll.
	 * @return
	 */
	private boolean canScroll() {
		View childView;
		if (getChildCount() > 1) {
			childView = this.getChildAt(1);
			if (childView instanceof ListView) {
				int top = ((ListView) childView).getChildAt(0).getTop();
				int pad = ((ListView) childView).getListPaddingTop();
				if ((Math.abs(top - pad)) < 3 && ((ListView) childView).getFirstVisiblePosition() == 0) {
					return true;
				} else {
					return false;
				}
			} else if (childView instanceof ScrollView) {
				if (((ScrollView) childView).getScrollY() == 0) {
					return true;
				} else {
					return false;
				}
			}

		}
		return false;
	}
	
	@Override
	public void computeScroll() {
		if(scroller.computeScrollOffset()){
			int i = this.scroller.getCurrY();
		      LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams)this.refreshView.getLayoutParams();
		      // update top margin
		      lp.topMargin = Math.max(i, refreshTargetTop);
		      this.refreshView.setLayoutParams(lp);
		      this.refreshView.invalidate();
		      invalidate();
		}
	}
	
	private void doMovement(int moveY) {
		LinearLayout.LayoutParams lp = (LayoutParams) refreshView.getLayoutParams();
		// get top margin
		float f1 = lp.topMargin;
		float f2 = moveY * 0.3F;
		int i = (int) (f1 + f2);
		// update top margin
		lp.topMargin = i;
		// refresh UI
		refreshView.setLayoutParams(lp);
		refreshView.invalidate();
		refreshArrow.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.INVISIBLE);
		refreshTimeTextView.setVisibility(View.VISIBLE);
		refreshHintTextView.setVisibility(View.VISIBLE);
		refreshTimeTextView.setText(lastRefreshTimeHintPrefix + lastRefreshTime);
		invalidate();
		if (lp.topMargin > 0) {
			refreshHintTextView.setText(releaseWillReleaseHint);
			if(!isUp) {
				isUp = true;
				AnimUtils.startAnimation(refreshArrow, downToUpAnimation);
			}
		} else {
			refreshHintTextView.setText(pullToRefreshHint);
			if(isUp) {
				isUp = false;
				AnimUtils.startAnimation(refreshArrow, upToDownAnimation);
			}
		}
	}
	
}

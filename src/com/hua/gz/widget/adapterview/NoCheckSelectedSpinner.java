package com.hua.gz.widget.adapterview;

import java.lang.reflect.Method;
import java.util.TreeSet;

import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.AdapterView;
import android.widget.Spinner;

/**
 * A helper Spinner class which enable OnItemSelectedListener.onItemSelected() event when click currently selected item.
 * Default not to check all items; You can use {@link #setNoCheckItemsSet(TreeSet)} to set just not to check some of them.
 * 
 * @author AlfredZhong
 * @version 2012-06-10
 */
public class NoCheckSelectedSpinner extends Spinner {

	private static final String TAG = NoCheckSelectedSpinner.class.getSimpleName();
	private static final String METHOD_NAME = "selectionChanged";
	private static Method mSelectionChangedMethod = null;
	private static final int INVALID_POSITION = -1;
	private int lastSelected = INVALID_POSITION;
	private TreeSet<Integer> mNoCheckItemsSet;

	static {
		try {
			Class<?> targetClass = AdapterView.class;
			Class<?> noparams[] = {};
			mSelectionChangedMethod = targetClass.getDeclaredMethod(METHOD_NAME, noparams);
			if (mSelectionChangedMethod != null) {
				mSelectionChangedMethod.setAccessible(true);
			}
		} catch (Exception e) {
			Log.e(TAG, "getDeclaredMethod selectionChanged failed.");
			throw new RuntimeException(e);
		}
	}

	public NoCheckSelectedSpinner(Context context) {
		super(context);
	}

	public NoCheckSelectedSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NoCheckSelectedSpinner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	protected void invokeSelectionChangedReflectionMethod() {
		try {
			Log.e(TAG, "Trying to call invokeSelectionChangedReflectionMethod().");
			Object noparams[] = {};
			mSelectionChangedMethod.invoke(this, noparams);
		} catch (Exception e) {
			Log.e(TAG, "invoke selectionChanged failed.", e);
		}
	}
	
	/**
	 * Set items position that you want spinner not to check whether it is selected when you select it.
	 * If not set, will not to check all the items. That means OnItemSelectedListener.onItemSelected() event 
	 * will trigger when user clicks currently selected item.
	 * @param noCheckItems
	 */
	public void setNoCheckItemsSet(TreeSet<Integer> noCheckItemsPosition) {
		mNoCheckItemsSet = noCheckItemsPosition;
	}
	
	private void checkSelected(int currentSelected) {
		Log.d(TAG, "lastSelected = " + lastSelected + ", currentSelected = " + currentSelected);
		if (lastSelected == currentSelected) {
			Log.w(TAG, "lastSelected == currentSelected.");
			if(mNoCheckItemsSet != null) {
				// only some of them not to check selected.
				if(mNoCheckItemsSet.contains(lastSelected)) {
					invokeSelectionChangedReflectionMethod();
				} else {
					Log.w(TAG, "mNoCheckItemsSet not contains " + currentSelected);
				}
			} else {
				// all the items not to check selected.
				invokeSelectionChangedReflectionMethod();
			}
		}
		/*
		 * NOTE: set lastSelected -1 to make sure next onLayout() or onClick()
		 * without touching the spinner by user will not trigger super.selectionChanged().
		 */
		lastSelected = INVALID_POSITION;
		Log.i(TAG, TAG + " set lastSelected = -1.");
	}
	
	// Just override to log.
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Called when spinner(not dialog list item) is touched, OnTouchListener won't disable this function.
		// Spinner OnTouchListener first, and then the following codes.
		int action = event.getAction();
		if(action == MotionEvent.ACTION_DOWN) {
			Log.i(TAG, TAG + " onTouchEvent() ACTION_DOWN, lastSelected = " + lastSelected);
		} else if(action == MotionEvent.ACTION_CANCEL) {
			// Almost not fire this event.
			Log.e(TAG, TAG + " onTouchEvent() ACTION_CANCEL");
			lastSelected = INVALID_POSITION;
		}
		return super.onTouchEvent(event);
	}
	
	// IMPORTANT : To resume the lastSelected position when spinner is clicked(before spinner dialog item is clicked).
	@Override
	public boolean performClick() {
		// Called when spinner(not dialog list item) is clicked(after touch).
		boolean handledPerformClick = super.performClick();
		if(handledPerformClick) {
			// getSelectedItemPosition() before select item.
			int last = getSelectedItemPosition();
			lastSelected = last;
			Log.w(TAG, TAG + " performClick() true, set lastSelected = " + lastSelected);
		} else {
			// It could not access here because Spinner.performClick() will return true if it is clicked.
			Log.e(TAG, TAG + " performClick() false,  set lastSelected = -1.");
			lastSelected = INVALID_POSITION;
		}
		return handledPerformClick;
	}
	
	@Override
	public void onClick(DialogInterface dialog, int which) {
		super.onClick(dialog, which);
		// Called when spinner list item is selected.
		Log.i(TAG, TAG + " onClick().");
		/*
		 * It works well on older Android versions like 2.x, 
		 * but unfortunately it doesn't work on version 3.0 and later (tried 3.2 and 4.0.3).
		 * For some reason the onClick() method is never called on newer platforms.
		 * So we don't check selected in onClick(), check it in onLayout().
		 * If we check in onClick(), we don't need set lastSelected = -1,
		 * but initial 0 when new instance and lastSelected = which here.
		 */
		//checkSelected(which);
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		// Called when spinner list item is selected or spinner is being build.
		Log.i(TAG, TAG + " onLayout(), changed == " + changed);
		/*
		 * It works well on any Android versions from 2.x.
		 */
		checkSelected(getSelectedItemPosition());
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		super.onWindowFocusChanged(hasWindowFocus);
		// Called when the window holding spinner gain or lost focus.
		Log.i(TAG, TAG + " onWindowFocusChanged(), hasWindowFocus == " + hasWindowFocus);
		if(hasWindowFocus) {
			// window gain focus.
			// NOTE: set lastSelected -1 in case user cancel the spinner dialog instead of selecting an item.
			lastSelected = INVALID_POSITION;
			Log.i(TAG, TAG + " set lastSelected = -1.");
		}
	}
	
}

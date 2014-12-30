package com.hua.gz.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * @author AlfredZhong
 * @version 2013-06-26
 */
public class LayoutUtils {

	private LayoutUtils() {
	}
	
	public static interface OnViewGlobalLayoutListener {
		
		public void onViewGlobalLayout(View view);
		
	}
	
	/**
	 * Try to get the view layout info before onResume().
	 * 
	 * @param view the view you want to get its layout info before onResume(), ViewGroup is also OK.
	 * @param listener the callback when you can get the view layout info.
	 */
	public static void getViewLayoutInfoBeforeOnResume(final View view, final LayoutUtils.OnViewGlobalLayoutListener listener) {
		// The ViewTreeObserver observer is not guaranteed to remain valid for the lifetime of this View.
		// So we have to check isAlive().
		if(view.getViewTreeObserver().isAlive()) {
			view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
				// We use the new method when supported
				@SuppressWarnings("deprecation")
				// We check which build version we are using.
				@SuppressLint("NewApi")
				@Override
				public void onGlobalLayout() {
					// remove listener
					if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
						view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
					} else {
						view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
					}
					if (listener != null) {
						listener.onViewGlobalLayout(view);
					}
				}
			});
		} else {
			System.err.println("== The ViewTreeObserver is NOT alive. ==");
		}
	}
	
	public static View getContentView(Activity act) {
		return act.getWindow().getDecorView().findViewById(android.R.id.content);
	}
	
	public static void setViewLayoutParam(View view, ViewGroup.LayoutParams params) {
		// fill_parent -1, match_parent -1, wrap_content -2.
		ViewGroup.LayoutParams target;
		ViewGroup parent = (ViewGroup) view.getParent();
		if(parent == null) {
			target = new FrameLayout.LayoutParams(params);
		} else if(parent instanceof FrameLayout) {
			target = new FrameLayout.LayoutParams(params);
		} else if(parent instanceof LinearLayout) {
			target = new LinearLayout.LayoutParams(params);
		} else if(parent instanceof RelativeLayout) {
			target = new RelativeLayout.LayoutParams(params);
		} else {
			// Note that there are some other LayoutParams, e.g. AbsListView.LayoutParams.
			// but we only handle layout here.
			target = params;
		}
		// view LayoutParams class type depends on view's parent layout.
		view.setLayoutParams(target);
	}
	
	/**
	 * Set layout_gravity="center" for the view.
	 * Note that this view's LayoutParams should be one of FrameLayout.LayoutParams
	 * or LinearLayout.LayoutParams or RelativeLayout.LayoutParams.
	 * 
	 * @param view
	 */
	public static void setViewCenterInParent(View view) {
		ViewGroup.LayoutParams lp = view.getLayoutParams();
		// set layout_gravity
		if(lp instanceof FrameLayout.LayoutParams) {
			((FrameLayout.LayoutParams)lp).gravity = android.view.Gravity.CENTER;
		} else if(lp instanceof LinearLayout.LayoutParams) {
			((LinearLayout.LayoutParams)lp).gravity = android.view.Gravity.CENTER;
		} else if(lp instanceof RelativeLayout.LayoutParams) {
			((RelativeLayout.LayoutParams)lp).addRule(RelativeLayout.CENTER_IN_PARENT);
		}
	}
	
	public static void addListHeaderView(ListView listView, View header) {
		// should make sure ListView with null adapter before adding header view.
		if(listView.getAdapter() != null) {
			System.err.println("Should call setAdapter(null) before adding header view.");
		} 
		listView.addHeaderView(header);
	}
	
	public static void addListFooterView(ListView listView, View footer) {
		// should make sure ListView with adapter before adding footer view.
		ListAdapter adapter = listView.getAdapter();
		if(adapter == null) {
			System.err.println("Should call setAdapter() before adding footer view.");
		} else {
			if(adapter.getCount() > 0) {
				System.out.println("You can use empty count adapter before adding footer view.");
			}
		}
		listView.addFooterView(footer);
	}
	
}

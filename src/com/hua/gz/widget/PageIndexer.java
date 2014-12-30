package com.hua.gz.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * A helper class to generate page indexer.
 * 
 * @author AlfredZhong
 * @version 1.0, 2012-02-26
 */
public class PageIndexer extends LinearLayout implements OnClickListener {
	
	private static final String TAG = PageIndexer.class.getSimpleName();
	private Context mContext;
	private ImageView[] mImageViews;
	private int mViewCount;
	private int mDefaultSelected = 0;
	private int mCurSelected;
	private float density; // 0.75, 1.0, 1.5, 2.0
	private PageClickListener listener;

	public PageIndexer(Context context) {
		super(context);
		mContext = context;
		density = context.getResources().getDisplayMetrics().density;
	}

	public PageIndexer(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		density = context.getResources().getDisplayMetrics().density;
	}
	
	/**
	 * Generate views programmely.
	 * 
	 * @param count page count
	 * @param indexImageResId state_list drawable resource id of index ground ball, you should set enabled as unselected, disabled as selected.
	 * @param padding index ground ball padding in dp.
	 */
	public void generateViews(int count, int indexImageResId, int padding) {
		if(count <= 0)
			return;
		mViewCount = count;
		this.removeAllViews();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		params.gravity = android.view.Gravity.CENTER_VERTICAL;
		ImageView img = null;
		mImageViews = new ImageView[mViewCount];
		for(int i=0; i<mViewCount; i++) {
			img = new ImageView(mContext);
			img.setImageResource(indexImageResId);
			img.setPadding(dip2px(padding), dip2px(padding), dip2px(padding), dip2px(padding));
			img.setClickable(true);
			img.setEnabled(true);
			img.setTag(i);
			img.setOnClickListener(this);
			mImageViews[i] = img;
			this.addView(img, params);
		}
		mCurSelected = mDefaultSelected;
		// the selected ImageView can not be clicked.
		mImageViews[mCurSelected].setEnabled(false);
	}
	
	public void updateSelected(int position) {
    	if (position < 0 || position > mViewCount - 1 || position == mCurSelected)
    		return;
    	mImageViews[mCurSelected].setEnabled(true);
    	mImageViews[position].setEnabled(false);
    	mCurSelected = position;
	}
	
	public final int dip2px(float dipValue){    
        return (int)(dipValue * density + 0.5f);   
    } 
	
	public final int px2dip(float pxValue) {
		return (int)(pxValue / density + 0.5f);
	}

	@Override
	public void onClick(View v) {
		int position = (Integer)(v.getTag());
		Log.d(TAG, "onClick position " + position);
		if(listener != null) {
			listener.onPageClick(position);
		}
	}
	
	/**
	 * Interface definition for a callback to be invoked when page click.
	 */
	public interface PageClickListener {
		public void onPageClick(int pageIndex);
	}
	
	public void setPageClickListener(PageClickListener listener) {
		this.listener = listener;
	}

}

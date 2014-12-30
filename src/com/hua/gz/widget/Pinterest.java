package com.hua.gz.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.LinearLayout;

/**
 * A ScrollView that used to work as pinterest.
 * 
 * Note:
 * use 0dp height instead of LayoutParams.FILL_PARENT for better performance.
 * 
 * @author AlfredZhong
 * @version 2013-10-23
 */
public class Pinterest extends LazyScrollView {

	private static final String TAG = Pinterest.class.getSimpleName();
	private LinearLayout mContainer;
	private int mColumnNum;
	private List<LinearLayout> mColumns;
	private SparseIntArray mColumnHeights;
	private int mItemCount;
	
	public Pinterest(Context context) {
		super(context);
		init();
	}

    public Pinterest(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public Pinterest(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }
    
    @SuppressWarnings("deprecation")
	private void init() {
    	mContainer = new LinearLayout(getContext());
		LinearLayout.LayoutParams containerLayoutParams = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		mContainer.setPadding(0, 0, 0, 0);
    	addView(mContainer, containerLayoutParams);
    	mItemCount = 0;
    }
    
    public List<LinearLayout> createPinterest(int columnNum, int totalWidth) {
    	mColumnNum = columnNum;
    	mColumns = new ArrayList<LinearLayout>(mColumnNum);
    	mColumnHeights = new SparseIntArray(mColumnNum);
    	int itemWidth = totalWidth / columnNum;
		for (int i = 0; i < columnNum; i++) {
			LinearLayout layout = new LinearLayout(getContext());
			LinearLayout.LayoutParams itemParam = new LinearLayout.LayoutParams(
					itemWidth, LayoutParams.WRAP_CONTENT);
			layout.setOrientation(LinearLayout.VERTICAL);
			layout.setLayoutParams(itemParam);
			mColumns.add(layout);
			mContainer.addView(layout);
		}
		return mColumns;
    }
	
    public void addItem(int columnIndex, View view, int itemHeight) {
    	mColumns.get(columnIndex).addView(view);
    	mColumnHeights.put(columnIndex, mColumnHeights.get(columnIndex) + itemHeight);
    	mItemCount++;
    }
    
    public int addItem(View view, int itemHeight) {
    	int columnIndex = getShortestColumn();
    	addItem(columnIndex, view, itemHeight);
    	return columnIndex;
    }
    
    public View getItem(String tag) {
    	return findViewWithTag(tag);
    }
    
    public int getItemCount() {
    	return mItemCount;
    }
    
	public int getShortestColumn() {
		int pos = 0;
		for (int i = 0; i < mColumnNum; i++) {
			if (mColumnHeights.get(i) < mColumnHeights.get(pos)) {
				pos = i;
			}
		}
		return pos;
	}
	
	public LinearLayout getShortestColumnContainer() {
		if(mColumns == null)
			return null;
		return mColumns.get(getShortestColumn());
	}
	
	public LinearLayout getColumnContainer(int column) {
		if(mColumns == null || column >= mColumnNum)
			return null;
		return mColumns.get(column);
	}
	
	public View getItem(int columnIndex, int rowIndex) {
		if(mColumns == null || columnIndex >= mColumnNum || rowIndex >= mColumns.size())
			return null;
		return mColumns.get(columnIndex).getChildAt(rowIndex);
	}
	
	/**
	 * @return List.get(0) is on screen views; List.get(1) is off screen views.
	 */
	public List<List<View>> getPinterestViews(int topOffset, int bottomOffset) {
		// all the views on ScrollView, no matter current on screen or not, are all visible.
		List<View> onScreen = new ArrayList<View>();
		List<View> offScreen = new ArrayList<View>();
		for (int i = 0; i < mColumnNum; i++) {
			LinearLayout containnerColumn = mColumns.get(i);
			int childCount = containnerColumn.getChildCount();
			View child;
			for(int j=0; j<childCount; j++) {
				child = containnerColumn.getChildAt(j);
				if(child.getBottom() >= (getScrollY() - topOffset) && child.getTop() <= (getScrollY() + getHeight() + bottomOffset)) {
					onScreen.add(child);
				} else {
					offScreen.add(child);
				}
			}
		}
		List<List<View>> ret = new ArrayList<List<View>>(2);
		ret.add(onScreen);
		ret.add(offScreen);
		return ret;
	}
	
	/*
	public boolean isIntersects(View item) {
		android.graphics.Rect itemBounds = new android.graphics.Rect();
		item.getHitRect(itemBounds);
		android.graphics.Rect bounds = new android.graphics.Rect();
		getDrawingRect(bounds);
		return android.graphics.Rect.intersects(bounds, itemBounds);
	}
	*/
	
	public void removeAllItem() {
		mContainer.removeAllViews();
	}
	
	@Override
	public String toString() {
		StringBuilder columnsHeight = new StringBuilder("Columns height : [");
		for (int i = 0; i < mColumnNum; i++) {
			columnsHeight.append(mColumnHeights.get(i)).append(",");
		}
		columnsHeight.replace(columnsHeight.length() - 1, columnsHeight.length(), "");
		columnsHeight.append("]");
		String ret = columnsHeight.toString();
		Log.d(TAG, ret);
		return ret;
	}
	
}

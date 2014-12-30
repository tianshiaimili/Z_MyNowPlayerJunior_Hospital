package com.hua.gz.widget;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;
import android.widget.ListView;

/**
 * @author AlfredZhong
 * @version 2012-04-19
 * @version 2013-02-28
 */
public class QuickAlphabeticBar extends ImageButton {

	// alphabetic bar & ListView
	private ListView mList;
	private Paint mPaint = new Paint();
	private String[] mLetters;
	private HashMap<String, Integer> mAlphaIndexer;
	private int mTextSize = 14;
	private int mTextColor = Color.WHITE;

	public QuickAlphabeticBar(Context context) {
		super(context);
	}

	public QuickAlphabeticBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public QuickAlphabeticBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setListView(ListView list) {
		mList = list;
	}

	public void setAlphaIndexer(HashMap<String, Integer> alphaIndexer) {
		mAlphaIndexer = alphaIndexer;
	}

	public void setLetters(String[] sections) {
		mLetters = sections;
		invalidate();
	}
	
	public void setLetterTextSize(int pixels) {
		mTextSize = pixels;
	}
	
	public void setLetterTextColor(int color) {
		mTextColor = color;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mLetters == null) {
			return;
		}
		int width = getWidth();
		int height = getHeight();
		float singleHeight = height / (float) mLetters.length;
		mPaint.setColor(mTextColor);
		mPaint.setTextSize(mTextSize);
		mPaint.setTypeface(Typeface.DEFAULT_BOLD);
		mPaint.setAntiAlias(true);
		float textWidth;
		for (int i = 0; i < mLetters.length; i++) {
			textWidth = mPaint.measureText(mLetters[i]) / 2;
			float xPos = width / 2 - textWidth;
			float yPos = singleHeight * i + singleHeight / 2;
			canvas.drawText(mLetters[i], xPos, yPos, mPaint);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mLetters == null)
			return super.onTouchEvent(event);
		float y = event.getY();
		int selectIndex = (int) (y / (getHeight() / mLetters.length));
		if (selectIndex < mLetters.length && selectIndex >= 0) {
			String key = mLetters[selectIndex];
			if (mAlphaIndexer.containsKey(key)) {
				int pos = mAlphaIndexer.get(key);
				if (mList.getHeaderViewsCount() > 0) {
					this.mList.setSelectionFromTop(pos + mList.getHeaderViewsCount(), 0);
				} else {
					this.mList.setSelectionFromTop(pos, 0);
				}
			}
		}
		return super.onTouchEvent(event);
	}

}

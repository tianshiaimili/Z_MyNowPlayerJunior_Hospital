package com.hua.nowplayerjunior.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils.TruncateAt;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

/**
 * Since API Level 4, Android 1.6, support density. So, pay attention to android:minSdkVersion="4".
 * <pre>Note:
 * screen area(whole screen)
 * application area(not including status bar) 
 * view area(not including status bar and title bar)
 * </pre>
 * 
 * @author AlfredZhong
 * @version 1.0, 2012-03-09
 */
public class DisplayUtils {

	protected static final String TAG = DisplayUtils.class.getSimpleName();

	private DisplayUtils() {
		// Can not instantiate.
	}
	
	private static DisplayMetrics metrics;
	
	/**
	 * Returns the current display metrics that are in effect for this resource object. 
	 * The returned object should be treated as read-only.
	 * 
	 * @param context
	 * @return The resource's current display metrics. 
	 */
	public static final DisplayMetrics getDisplayMetrics(Context context) {
		if(metrics == null) {
			/*
			 * You can also get DisplayMetrics in Activity.
			 * DisplayMetrics metrics = new DisplayMetrics();
			 * getWindowManager().getDefaultDisplay().getMetrics(metrics);
			 * or (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			 * Then use metrics.xxx.
			 * Note that getWindowManager().getDefaultDisplay().getWidth() or get something else
			 * can NOT call before activity come to the front, otherwise you will always get 0.
			 */
			metrics  = context.getResources().getDisplayMetrics();
		}
		return metrics;
	}
	
	public static final int getScreenWidth(Context context) {
		return getDisplayMetrics(context).widthPixels;
	}
	
	public static final int getScreenHeight(Context context) {
		return getDisplayMetrics(context).heightPixels;
	}
	
	public static final int getDensityDpi(Context context) {
		return getDisplayMetrics(context).densityDpi;
	}
	
	/**
	 * Since API level 4.
	 * @param context
	 * @return
	 */
	public static final float getDensity(Context context) {
		return getDisplayMetrics(context).density;
	}
	
	/**
	 * Since API level 4.
	 * @param ctx
	 * @param dipValue
	 * @return
	 */
	public static final int dip2px(Context ctx, float dipValue){    
		final float density = getDensity(ctx);
		/*
		 * equals to Math.round(dipValue * density) or Math.floor(dipValue * density + 0.5)
		 * The 0.5 is added and then the result of the calculation is cast as an integer
		 * causing it to truncate the mantissa and leaving characteristic as a properly rounded integer value.
		 */
        return (int)(dipValue * density + 0.5f);
    } 
	
	/**
	 * Since API level 4.
	 * @param ctx
	 * @param pxValue
	 * @return
	 */
	public static final int px2dip(Context ctx, float pxValue) {
		final float density = getDensity(ctx);
		return (int)(pxValue / density + 0.5f);
	}
	
	/**
	 * It is OK to call this method onCreate().
	 * Note that this method ONLY works on phone.
	 * @param context
	 * @return
	 */
	public static int getStatusBarHeight(Context context) {
		int result = 0;
		// @android:dimen/status_bar_height
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}
	
	/**
	 * You can NOT call this method onCreate(), otherwise will return 0.
	 * You should call it onWindowFocusChanged() or after onWindowFocusChanged().
	 * 
	 * @param activity
	 * @return [0] is status bar height, [1] is title bar height.
	 */
	public static int[] getBarHeight(final Activity activity) {
		// get application area(not including status bar).
		Rect appArea = new Rect();
		Window window = activity.getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(appArea);
        // appArea.top is the top position of this view relative to its parent, it is status bar height.
        int statusBarHeight = appArea.top;
        Log.v(TAG, "Application area(not including status bar) top is " + statusBarHeight);
        // get view area(not including status bar and title bar).
        int viewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        Log.v(TAG, "View area(not including status bar and title bar) top is " + viewTop);
        int titleBarHeight;
        if(viewTop == 0) {
        	// if theme is Theme.NoTitleBar, viewTop will be 0.
        	titleBarHeight = 0;
        } else {
        	titleBarHeight = Math.abs(viewTop - appArea.top);
        }
        Log.d(TAG, "Status bar height is " + statusBarHeight + ", Title bar height is " + titleBarHeight);
        return new int[]{ statusBarHeight, titleBarHeight };
	}
	
	public interface OnBarHeightKnown {
		/**
		 * @param statusBarHeight not 0 even if theme is Theme.NoTitleBar.Fullscreen.
		 * @param titleBarHeight 0 if theme is Theme.NoTitleBar or Theme.NoTitleBar.Fullscreen
		 */
		public void onBarHeightKnown(int statusBarHeight, int titleBarHeight);
	}
	
	/**
	 * The callback will run in UI thread. Suit for API Level from 1.6.
	 * Note that this callback will be invoked tens of milliseconds later.
	 * 
	 * @param activity
	 * @param view
	 * @param callback
	 */
	public static void getBarHeight(final Activity activity, final OnBarHeightKnown callback) {
		// only View.post() can get status bar height and title bar height.
		TextView view = new TextView(activity.getApplicationContext());
		view.post(new Runnable() {
			@Override
			public void run() {
				int[] barsHeight = getBarHeight(activity);
		        if(callback != null) {
		        	callback.onBarHeightKnown(barsHeight[0], barsHeight[1]);
		        }
			}
		});
	}
	
    /**
     * Returns pixel value with given unit and value.  See {@link TypedValue} for the possible dimension units.
     * 
     * @param context
     * @param unit The desired dimension unit. e.g. TypedValue.COMPLEX_UNIT_DIP, TypedValue.COMPLEX_UNIT_SP, 
     * 		TypedValue.COMPLEX_UNIT_PT and so on.
     * @param size The desired size in the given units.
     */
    public static float getRawSize(Context context, int unit, float size) {
        Resources r;
        if (context == null)
            r = Resources.getSystem();
        else
            r = context.getResources();
        return TypedValue.applyDimension(unit, size, r.getDisplayMetrics());
    }
	
	/**
	 * Returns device diagonal pixels.
	 * 
	 * @param context
	 * @return
	 */
	public static double getDiagonalPixels(Context context) {
		double widthPow = Math.pow(getScreenWidth(context), 2);
		double heightPow = Math.pow(getScreenHeight(context), 2);
		return Math.sqrt(widthPow + heightPow);
	}
	
	/**
	 * Returns density dpi based on width and height.
	 * <p>Note: Since API level 4.
	 * 
	 * @param context
	 * @return
	 */
	public static double getRealDPI(Context context, float screenSize) {
		return getDiagonalPixels(context) / screenSize;
	}
	
	/**
	 * Returns approximate screen size in inch.
	 * <p>Note: the return value is not reliable because device manufacturers will choose density values 
	 * that they feel deliver the best results for existing applications.
	 * <p>Note: Since API level 4.
	 * 
	 * @param context
	 * @return
	 */
	public static double getApproximateScreenSize(Context context) {
		return getDiagonalPixels(context) / (160 * getDensity(context));
	}
	
	/**
	 * Returns TextView's text width(not TextView width).
	 * <p>TextView default size is 14sp. Note that text size is not equal to text width and height.
	 * <p>Note that padding is not in calculation scope.
	 * 
	 * @param tv
	 * @return text width in pixel or 0 if TextView.getText() is empty.
	 */
	public static int getTextWidth(TextView tv) {
		/*
		 * It has a little bit deviation(usually smaller than the real value 2px) to calculate TextView width as follow:
		 * Paint paint = new Paint(); // new Paint instance, not TextView's Paint instance.
		 * paint.setTextSize(tv.getTextSize());
		 * return paint.measureText(tv.getText().toString());
		 */
		Paint paint = tv.getPaint(); // Retrieves TextPaint, subclass of Paint.
		// Log.v(TAG, "TextView size is " + paint.getTextSize() + " sp.");
		// If text is empty, it will return 0.
		return Math.round(paint.measureText(tv.getText().toString()));
	}
	
	/**
	 * Returns TextView's text height(text height means line height, not TextView height).
	 * <p>TextView default size is 14sp. Note that text size is not equal to text width and height.
	 * <p>Note that padding is not in calculation scope.
	 * <p>Note that TextView.getHeight() is higher than DisplayUtils.getTextHeight().
	 * DisplayUtils.getTextHeight() should be equal to TextView.getLineHeight().
	 * 
	 * @param tv
	 * @return text height in pixel(Even though text is empty, it can calculate height).
	 */
	public static int getTextHeight(TextView tv) {
		Paint paint = tv.getPaint();
		FontMetrics fm = paint.getFontMetrics();
		float textHeight = fm.descent - fm.ascent;
		Log.v(TAG, "TextView size is " + tv.getTextSize() + " sp, textHeight " + textHeight);
		return (int) (textHeight + 0.5f);
	}
	
	/**
	 * Returns R.attr Dimension Value.
	 * For example, android.R.attr.listPreferredItemHeight
	 * 
	 * @param context
	 * @param attrId
	 * @return
	 */
    public static float getAttrDimensionValue(Context context, int attrId) {
    	TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(attrId, value, true);
        // return value.getDimension(getDisplayMetrics(context)); // It is also OK. 
        return TypedValue.complexToDimensionPixelSize(value.data, getDisplayMetrics(context));
    }
	
    public static int getHorizontalMargin(View view) {
    	ViewGroup.MarginLayoutParams vlp = (ViewGroup.MarginLayoutParams)view.getLayoutParams();
    	return vlp.leftMargin + vlp.rightMargin;
    }
    
    public static int getVerticalMargin(View view) {
    	ViewGroup.MarginLayoutParams vlp = (ViewGroup.MarginLayoutParams)view.getLayoutParams();
    	return vlp.topMargin + vlp.bottomMargin;
    }
    
    public static int getLeftMargin(View view) {
    	ViewGroup.MarginLayoutParams vlp = (ViewGroup.MarginLayoutParams)view.getLayoutParams();
    	return vlp.leftMargin;
    }
    
    public static int getTopMargin(View view) {
    	ViewGroup.MarginLayoutParams vlp = (ViewGroup.MarginLayoutParams)view.getLayoutParams();
    	return vlp.topMargin;
    }
    
    public static int getRightMargin(View view) {
    	ViewGroup.MarginLayoutParams vlp = (ViewGroup.MarginLayoutParams)view.getLayoutParams();
    	return vlp.rightMargin;
    }
    
    public static int getBottomMargin(View view) {
    	ViewGroup.MarginLayoutParams vlp = (ViewGroup.MarginLayoutParams)view.getLayoutParams();
    	return vlp.bottomMargin;
    }
    
    /**
     * Returns TextView line count.
     * Note that this method will ignore line separator "\n".
     * 
     * @param textView
     * @param textViewWidth
     * @return TextView line count
     */
    public static int getTextViewLineCount(TextView textView, int textViewWidth) {
		float textSize = textView.getTextSize();
		Typeface textTypeface = textView.getTypeface(); // may be null.
		TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
		paint.setTextSize(textSize);
		paint.setTypeface(textTypeface);
		int lineCount = 0;
		int index = 0;
		int length = textView.getText().length();
		while(index < length - 1) {
			index += paint.breakText(textView.getText(), index, length, true, textViewWidth, null);
			lineCount++;
		}
		// System.out.println("TextView textSize = " + textSize + ", textTypeface = " + textTypeface + ", lineCount = " + lineCount);
		return lineCount;
	}
    
    /**
     * Returns TextView height.
     * 
     * @param textView
     * @param textViewWidth
     * @return TextView height
     */
    public static int getTextViewHeight(TextView textView, int textViewWidth) {
    	int lineHeight = getTextHeight(textView);
    	// getTextViewLineCount() will return 0 if string is empty.
    	if(textView.getText() == null || textView.getText().equals(""))
    		return lineHeight;
    	return lineHeight * getTextViewLineCount(textView, textViewWidth);
    }
    
    /**
     * Construct a space text to occupy the specific dp with " " character.
     * 
     * @param textView
     * @param spaceDP
     * @return space text with " " character.
     */
    public static String getSpaceText(TextView textView, int spaceDP) {
    	String text = textView.getText().toString();
    	textView.setText(" ");
		int spaceWidth = DisplayUtils.getTextWidth(textView);
		int spaceCount = DisplayUtils.dip2px(textView.getContext(), spaceDP) / spaceWidth;
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<spaceCount; i++) {
			sb.append(" ");
		}
		// restore the TextView text.
		textView.setText(text);
		return sb.toString();
    }
    
    /**
     * Set TextView android:ellipsize="end" and android:maxLines="maxLine".
     * Note that should make sure you are calling this method after TextView layout info known.
     * 
     * @param textView
     * @param maxLine
     * @param useTextViewAPI
     */
    public static void setTextViewEllipsizeEnd(TextView textView, int maxLine, boolean useTextViewAPI) {
    	if(useTextViewAPI) {
    		textView.setMaxLines(maxLine);
    		textView.setEllipsize(TruncateAt.END);
    		textView.invalidate();
    	} else {
    		Log.v(TAG, "setTextViewEllipsizeEnd textView.getLineCount() " + textView.getLineCount());
    		if (textView.getLineCount() <= maxLine) {
    			return;
    		}
			final CharSequence text = textView.getText();
			final int lineEndIndex = textView.getLayout().getLineEnd(maxLine - 1);
			int skipCharSize = 3;
			// calculate end symbol width.
			String endSymbol = "...";
			textView.setText(endSymbol);
			int endSymbolWidth = getTextWidth(textView);
			// calculate last three char width.
			final CharSequence lastThree = text.subSequence(lineEndIndex - 3, lineEndIndex);
			String lastChar = lastThree.subSequence(2, 3).toString();
			textView.setText(lastChar);
			int lastCharWidth = getTextWidth(textView);
			if(lastCharWidth < endSymbolWidth) {
				String lastSecondChar = text.subSequence(1, 2).toString();
				textView.setText(lastSecondChar);
				int lastSecondCharWidth = getTextWidth(textView);
				if(lastSecondCharWidth < endSymbolWidth) {
					skipCharSize = 3;
				} else {
					skipCharSize = 2;
				}
			} else {
				skipCharSize = 1;
			}
			Log.v(TAG, "setTextViewEllipsizeEnd lastThree " + lastThree + ", skipCharSize " + skipCharSize);
			String ellipsizeEndText = text.subSequence(0, lineEndIndex - skipCharSize) + endSymbol;
			textView.setText(ellipsizeEndText);
			textView.invalidate();
    	}
    }
	
	/**
	 * Set TextView android:ellipsize="end" and android:maxLines="n" to support any text line count.
	 * 
	 * @param textView
	 * @param useTextViewAPI
	 */
	public static void setTextViewEllipsizeEndBeforeOnResume(final TextView textView, final boolean useTextViewAPI) {
		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				int singleLineHeight = textView.getLineHeight();
				int totalHeight = textView.getHeight();
				int tempSingleLineHeight = getTextHeight(textView);
				if(singleLineHeight <= 0) {
					singleLineHeight = tempSingleLineHeight;
				} else if(singleLineHeight != tempSingleLineHeight) {
					Log.w(TAG, "setTextViewEllipsizeEnd warning: TextView.getLineHeight() is " + singleLineHeight
							+ ", " + TAG + ".getTextHeight() is " + tempSingleLineHeight);
				}
				int maxLine = totalHeight / singleLineHeight;
				setTextViewEllipsizeEnd(textView, maxLine, useTextViewAPI);
				Log.v(TAG, "setTextViewEllipsizeEnd " + singleLineHeight + ", " + textView.getHeight() + ", " + maxLine);
			}
		};
		if(textView.getHeight() == 0) {
			// before onResume().
			LayoutUtils.getViewLayoutInfoBeforeOnResume(textView, new LayoutUtils.OnViewGlobalLayoutListener() {
				@Override
				public void onViewGlobalLayout(View view) {
					runnable.run();
				}
			});
		} else {
			// after onResume().
			runnable.run();
		}
	}
    
}

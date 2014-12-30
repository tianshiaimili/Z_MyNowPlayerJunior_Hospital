package com.hua.gz.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 一个用于生成标签文字布局的工具类.
 * 
 * @author AlfredZhong
 * @version 2012-08-09
 */
public class TagTextBuilder {

	private LayoutInflater inflater;
	private Context mContext;
	// TagText fields.
	private LinearLayout.LayoutParams layoutParams;
	private int topBgResId, centerBgResId, bottomBgResId, singleBgResId;
	private int maxLineTagsNum = Integer.MAX_VALUE;
	private TagTextListener listener;

	public TagTextBuilder(Context context) {
		mContext = context;
		inflater = LayoutInflater.from(mContext);
	}
	
	/**
	 * 设置背景drawable resource id.
	 * 
	 * @param top
	 * @param center
	 * @param bottom
	 * @param single
	 */
	public void setBackgroundResId(int top, int center, int bottom, int single) {
		topBgResId = top;
		centerBgResId = center;
		bottomBgResId = bottom;
		singleBgResId = single;
	}

	/**
	 * 设置一行最多显示的tag个数. 默认不限制个数, 只与文字宽度和屏幕宽度有关.
	 * 
	 * @param maxLineTagsNum
	 */
	public void setMaxLineTagsNum(int maxLineTagsNum) {
		if(maxLineTagsNum <= 0)
			throw new IllegalArgumentException("MaxLineTagsNum should be larger than 0.");
		this.maxLineTagsNum = maxLineTagsNum;
	}
	
	public void setLayoutParams(LinearLayout.LayoutParams params) {
		layoutParams = params;
	}
	
	public interface TagTextListener {
		public void onTextViewGenerated(TextView textview, int position, int row, int colomn);
	}
	
	public void setTagTextListener(TagTextListener listener) {
		this.listener = listener;
	}

	/**
	 * 生成标签效果布局.
	 * 
	 * @param linearlayoutWidth 每一行的LinearLayout的width.
	 * @param linearlayoutResId 每一行的LinearLayout的layout resource id.
	 * @param textviewResId 每一个标签TextView的layout resource id.
	 * @param tags 要显示的标签.
	 * @return
	 */
	public LinearLayout generateTagText(int linearlayoutWidth, int linearlayoutResId, int textviewResId, List<String> tags) {
		// 生成布局父layout
		LinearLayout container = new LinearLayout(mContext);
		if(layoutParams == null) {
			layoutParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
		}
		container.setLayoutParams(layoutParams);
		container.setOrientation(LinearLayout.VERTICAL);
		if(tags == null || tags.size() == 0) {
			return container;
		}
		LinearLayout line = (LinearLayout) inflater.inflate(linearlayoutResId, container, false);
		LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) line.getLayoutParams();
		final int lineWidth = linearlayoutWidth - llp.leftMargin - llp.rightMargin;
		// 计算TextView的margins和paddings.
		TextView text = (TextView) inflater.inflate(textviewResId, line, false);
		final int horizontalPadding = text.getPaddingLeft() + text.getPaddingRight();
		ViewGroup.MarginLayoutParams vlp = (ViewGroup.MarginLayoutParams)text.getLayoutParams();
		final int horizontalMargin = vlp.leftMargin + vlp.rightMargin;
		// 计算所有的tags共需要多少行, 各自分布在哪一行.
		int len = tags.size();
		// contents用于存放每一行的tags.
		List<List<String>> contents = new ArrayList<List<String>>();
		// lineContent用于存放一行的tags.
		List<String> lineContent = new ArrayList<String>();
		contents.add(lineContent);
		int left = lineWidth;
		int textWidth;
		for(int i=0; i<len; i++) {
			text.setText(tags.get(i));
			textWidth = Math.round(text.getPaint().measureText(text.getText().toString())) + horizontalPadding;
			left = left - horizontalMargin - textWidth;
			if(0 <= left &&  lineContent.size() < maxLineTagsNum) {
				// 够位置而且没有超出一行的tag个数, 放在本行.
				lineContent.add(tags.get(i));
			} else {
				if(lineContent.size() == 0) {
					// 如果没放过一个TextView, 说明这个TextView比行宽还宽，无需新建一行, 直接作为一行.
					lineContent.add(tags.get(i));
					left = lineWidth;
					lineContent = new ArrayList<String>();
					contents.add(lineContent);
				} else {
					// 不够位置, 放在下一行.
					lineContent = new ArrayList<String>();
					contents.add(lineContent);
					lineContent.add(tags.get(i));
					left = lineWidth - horizontalMargin - textWidth;
				}
			}
		}
		int count = contents.size(); // 总行数
		int lineTagsNum; // 一行tags的总个数
		int position = 0;
		// 生成标签文字布局.
		for(int i=0; i<count; i++) {
			lineContent = contents.get(i);
			lineTagsNum = lineContent.size();
			line = (LinearLayout) inflater.inflate(linearlayoutResId, container, false);
			line.setBackgroundResource(getBackgroundRes(count, i));
			container.addView(line);
			// 生成当前行的文字.
			for(int j=0; j<lineTagsNum; j++) {
				text = (TextView) inflater.inflate(textviewResId, line, false);
				text.setText(lineContent.get(j));
				line.addView(text);
				if(listener != null) {
					listener.onTextViewGenerated(text, position, i, j);
				}
				position++;
			}
		}
		// costs around 100ms.
		return container;
	}
	
	public List<TextView> getTextViews(LinearLayout container) {
		if(container == null)
			return null;
		List<TextView> views = new ArrayList<TextView>();
		List<LinearLayout> layouts = new ArrayList<LinearLayout>();
		int count = container.getChildCount();
		for(int i=0; i<count; i++) {
			layouts.add((LinearLayout) container.getChildAt(i));
		}
		LinearLayout ll;
		for(View v : layouts) {
			ll = (LinearLayout) v;
			count = ll.getChildCount();
			for(int i=0; i<count; i++) {
				views.add((TextView) ll.getChildAt(i));
			}
		}
		return views;
	}
	
	private int getBackgroundRes(int count, int order) {
		if(count > 1) {
			if(order == 0) {
				return topBgResId;
			} else if(order == count - 1) {
				return bottomBgResId;
			} else {
				return centerBgResId;
			}
		}
		return singleBgResId;
	}
	
}

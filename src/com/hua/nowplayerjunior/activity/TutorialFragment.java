package com.hua.nowplayerjunior.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.hua.activity.R;
import com.hua.gz.app.BaseFragment;
import com.hua.gz.app.LayoutUtils;
import com.hua.gz.res.ImageLoaderFactory;
import com.hua.gz.res.SingleImageLoader;
import com.hua.gz.utils.DisplayUtils;
import com.hua.gz.widget.ConvertPagerAdapter;
import com.hua.gz.widget.PageIndexer;
import com.hua.nowid.activity.NowIDLoginActivity;
import com.pccw.nmal.Nmal;
import com.pccw.nmal.util.LanguageHelper;

public class TutorialFragment extends BaseFragment {
	
    private ViewPager viewPager;
    private PageIndexer indexer;
    private SingleImageLoader viewPagerSingleLoader;
    private static final int PAGE_COUNT = 6;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.tutorial_fragment, container, false);
		viewPager = (ViewPager)view.findViewById(R.id.viewPager);
		indexer = (PageIndexer) view.findViewById(R.id.page_indexer);
		viewPager.setAdapter(new TutorialPagerAdapter(getActivity()));
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				indexer.updateSelected(position);
			}
			@Override
			public void onPageScrolled(int position, float arg1, int arg2) {
			}
			@Override
			public void onPageScrollStateChanged(int position) {
			}
		});
		indexer.generateViews(PAGE_COUNT, R.drawable.tutorial_index_round, DisplayUtils.dip2px(getActivity(), 1.5f));
		indexer.setPageClickListener(new PageIndexer.PageClickListener() {
			@Override
			public void onPageClick(int pageIndex) {
				viewPager.setCurrentItem(pageIndex);
			}
		});
		viewPagerSingleLoader = ImageLoaderFactory.getSingleImageLoader(getActivity());
		view.findViewById(R.id.tutorial_skip).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		return view;
	}
	
    private class TutorialPagerAdapter extends ConvertPagerAdapter {

    	private LayoutInflater inflater;
    	private int reqWidth;
    	
		public TutorialPagerAdapter(Context context) {
			super(context);
			inflater = LayoutInflater.from(context);
			reqWidth = DisplayUtils.dip2px(getActivity(), 320);
		}

		private void setPageImage(View convertView, int i, boolean setNull) {
			ImageView img;
			switch(i) {
			case 0:
				img = (ImageView) convertView.findViewById(R.id.imageView3);
				if(setNull)
					img.setImageBitmap(null);
				else
					viewPagerSingleLoader.setResourceImage(img, R.drawable.tut_welcome_bg, reqWidth, 0);
				break;
			case 1:
				img = (ImageView) convertView;
				if(setNull)
					img.setImageBitmap(null);
				else
					viewPagerSingleLoader.setResourceImage(img, R.drawable.tut_program, reqWidth, 0);
				break;
			case 2:
				img = (ImageView) convertView;
				if(setNull)
					img.setImageBitmap(null);
				else
					viewPagerSingleLoader.setResourceImage(img, R.drawable.tut_channel, reqWidth, 0);
				break;
			case 3:
				img = (ImageView) convertView;
				if(setNull)
					img.setImageBitmap(null);
				else
					viewPagerSingleLoader.setResourceImage(img, R.drawable.tut_fungame, reqWidth, 0);
				break;
			case 4:
				img = (ImageView) convertView;
				if(setNull)
					img.setImageBitmap(null);
				else
					viewPagerSingleLoader.setResourceImage(img, R.drawable.tut_playtime, reqWidth, 0);
				break;
			case 5:
				img = (ImageView) convertView.findViewById(R.id.imageView7);
				if(setNull)
					img.setImageBitmap(null);
				else
					viewPagerSingleLoader.setResourceImage(img, R.drawable.tut_nowid, reqWidth, 0);
				break;
			}
		}
		
		@Override
		public View instantiateView(final int position, View convertView, ViewGroup parent) {
			if(convertView == null) {
				switch(position) {
				case 0:
					convertView = inflater.inflate(R.layout.tutorial_welcome, parent, false);
					break;
				case 1:
				case 2:
				case 3:
				case 4:
					ImageView img = new ImageView(getActivity());
					img.setAdjustViewBounds(true);
					LayoutUtils.setViewLayoutParam(img, new ViewGroup.LayoutParams(reqWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
					convertView = img;
					break;
				case 5:
					convertView = inflater.inflate(R.layout.tutorial_now_id, parent, false);
					Button register = (Button) convertView.findViewById(R.id.setting_tutorial_register);
					register.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Uri uriUrl = Uri.parse(getResources().getString(R.string.nowid_landing_register_url)); 
							Intent intent = new Intent(Intent.ACTION_VIEW, uriUrl);
							startActivity(intent);
						}
					});
					Button login = (Button) convertView.findViewById(R.id.setting_tutorial_login);
					login.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(getActivity(), NowIDLoginActivity.class);
							intent.putExtra("lang", LanguageHelper.getCurrentLanguage());
							intent.putExtra("userAgent", Nmal.getWebViewUserAgent());
							startActivityForResult(intent, 0);
						}
					});
					Button guest = (Button) convertView.findViewById(R.id.setting_tutorial_guest);
					guest.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							TutorialFragment.this.finish();
						}
					});
					break;
				}
			}
			setPageImage(convertView, position, false);
			return convertView;
		}

		@Override
		public void destroyView(int position, View convertView) {
			setPageImage(convertView, position, true);
		}

		@Override
		public int getCount() {
			return PAGE_COUNT;
		}  
          
    }
	
}

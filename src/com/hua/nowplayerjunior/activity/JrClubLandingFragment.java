package com.hua.nowplayerjunior.activity;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hua.activity.R;
import com.hua.gz.app.FragmentUtils;
import com.hua.gz.app.WeakHandler;
import com.hua.gz.res.GroupImageLoader;
import com.hua.gz.res.ImageLoaderFactory;
import com.hua.gz.res.SingleImageLoader;
import com.hua.gz.utils.DisplayUtils;
import com.hua.gz.utils.ExternalIntentUtils;
import com.hua.gz.widget.ConvertPagerAdapter;
import com.hua.gz.widget.FlexibleImageView;
import com.hua.gz.widget.PageIndexer;
import com.hua.gz.widget.adapterview.ObservableListView;
import com.hua.nowplayerjunior.constants.Constants;
import com.hua.nowplayerjunior.utils.LayoutUtils;
import com.pccw.nmal.appdata.AppInfo;
import com.pccw.nmal.appdata.JsonZip;
import com.pccw.nmal.appdata.JsonZip.ZipType;
import com.pccw.nmal.model.Article;
import com.pccw.nmal.model.Article.ArticleData;
import com.pccw.nmal.model.Banner;
import com.pccw.nmal.model.Banner.BannerData;
import com.pccw.nmal.model.StreamInfo;
import com.pccw.nmal.util.LanguageHelper;

public class JrClubLandingFragment extends UIEventBaseFragment {

	private static final String TAG = JrClubLandingFragment.class.getSimpleName();
	private static final int HANDLER_DATA_READY = 0;
    private ObservableListView listView;
    private ViewPager viewPager;
    private PageIndexer indexer;
    private ProgressDialog progressDialog;
	private JrClubLandingListAdapter jrClubLandingListAdapter;
	private BannerPagerAdapter bannerPagerAdapter;
	private GroupImageLoader imageLoader;
    private SingleImageLoader viewPagerSingleLoader;

	@Override
	protected void afterQualityAndBookmarkSelected(List<List<StreamInfo>> playlist, int quality, int bookmark) {
	}
	
    private final InnerStaticHandler mHandler = new InnerStaticHandler(this);
	private static class InnerStaticHandler extends WeakHandler<JrClubLandingFragment> {

		public InnerStaticHandler(JrClubLandingFragment contextObject) {
			super(contextObject);
		}

		@Override
		public void handleWeakHandlerMessage(JrClubLandingFragment contextObject, Message msg) {
			switch(msg.what) {
			case HANDLER_DATA_READY:
				contextObject.jrClubLandingListAdapter.setData(Article.getInstance().getArticleList());
				List<BannerData> banners = Banner.getInstance().getBannerList();
				contextObject.bannerPagerAdapter.setData(banners);
				contextObject.indexer.generateViews(banners.size(), R.drawable.index_round, 
						DisplayUtils.dip2px(contextObject.getActivity(), 2));
				break;
			}
		}
		
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.jrclub_landing,container, false);
		listView = (ObservableListView)view.findViewById(R.id.jrclub_listview);
	    imageLoader = ImageLoaderFactory.getGroupImageLoader(getActivity(), listView);
		imageLoader.setRequestMinWidth(DisplayUtils.dip2px(getActivity(), 100));
		imageLoader.setLoadingImage(getResources(), R.drawable.jr_club_news_thumbnail_bg);
		imageLoader.enableLazyLoad(R.id.jr_club_icon_image);
		
		View headerView = inflater.inflate(R.layout.jrclub_landing_header, null, false);
		viewPager = (ViewPager)headerView.findViewById(R.id.viewPager);
		indexer = (PageIndexer) headerView.findViewById(R.id.page_indexer);
		int width = DisplayUtils.getScreenWidth(getActivity());
		int height = FlexibleImageView.getFitHeight(width, 356, 153);
		LayoutUtils.setViewLayoutParam(viewPager, new ViewGroup.LayoutParams(width, height));
		viewPagerSingleLoader = ImageLoaderFactory.getSingleImageLoader(getActivity());
		viewPagerSingleLoader.setLoadingImage(getResources(), R.drawable.jr_club_topbanner_thumbnail_bg);
		LayoutUtils.addListHeaderView(listView, headerView);
		// pass scroll event to header and list items.
		listView.setScrollingEnabled(false);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// setup list.
		jrClubLandingListAdapter = new JrClubLandingListAdapter();
		listView.setAdapter(jrClubLandingListAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ArticleData data = (ArticleData) parent.getItemAtPosition(position);
				Bundle bundle = new Bundle();
				bundle.putString("article_id", data.getId());
				JrClubDeatailFragment f = new JrClubDeatailFragment();
				f.setArguments(bundle);
				FragmentUtils.replace(getActivity(), f, R.id.fragment_container,
						JrClubDeatailFragment.class.getSimpleName(), "JrClub");
			}
		});
		// setup banner.
		bannerPagerAdapter = new BannerPagerAdapter(getActivity());
		viewPager.setAdapter(bannerPagerAdapter);
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
		indexer.setPageClickListener(new PageIndexer.PageClickListener() {
			@Override
			public void onPageClick(int pageIndex) {
				viewPager.setCurrentItem(pageIndex);
			}
		});
	}
	
    private class BannerPagerAdapter extends ConvertPagerAdapter {

    	private List<BannerData> banners;
    	
		public BannerPagerAdapter(Context context) {
			super(context);
		}
		
		public void setData(List<BannerData> banners) {
			Log.d(TAG, "banners size " + banners.size());
			this.banners = banners;
			notifyDataSetChanged();
		}

		@SuppressWarnings("deprecation")
		@Override
		public View instantiateView(final int position, View convertView, ViewGroup parent) {
			if(convertView == null) {
				ImageView img = new ImageView(getActivity());
				img.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 
						ViewGroup.LayoutParams.FILL_PARENT));
				img.setScaleType(ImageView.ScaleType.FIT_XY);
				convertView = img;
			}
			ImageView bannerImage = (ImageView) convertView;
			viewPagerSingleLoader.setRemoteImage(bannerImage, banners.get(position).getImage1(), 
					viewPagerSingleLoader.getLoadingImage(), DisplayUtils.getScreenWidth(getActivity()));
			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(banners.get(position).getLink1() == null
							|| banners.get(position).getLink1().equals("")) {
						return;
					}
					ExternalIntentUtils.openLink(getActivity(), banners.get(position).getLink1());
				}
			});
			return convertView;
		}

		@Override
		public void destroyView(int position, View convertView) {
			if(convertView != null) {
				ImageView bannerImage = (ImageView) convertView;
				bannerImage.setImageBitmap(null);
			}
		}

		@Override
		public int getCount() {
			if (banners != null)
				return banners.size();
			return 0;
		}  
          
    }
	
	private class JrClubLandingListAdapter extends BaseAdapter {

		private LayoutInflater aLayoutInflater;
		private	List<ArticleData> articleData;
		private String space;
	
		JrClubLandingListAdapter() {
			aLayoutInflater = LayoutInflater.from(getActivity());
		}
		
		public void setData(List<ArticleData> datas) {
			this.articleData = datas;
			this.notifyDataSetChanged();
		}
		
		@Override
		public int getCount() {
			if (articleData != null)
				return articleData.size();
			return 0;
		}

		@Override
		public ArticleData getItem(int position) {
			if (articleData != null)
				return articleData.get(position);
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		// 1. convertView
		// 2. ViewHolder(option)
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = aLayoutInflater.inflate(R.layout.jrclub_listview_item, parent, false);
				holder = new ViewHolder();
				holder.icon = (ImageView) convertView.findViewById(R.id.jr_club_icon_image);
				holder.type = (ImageView) convertView.findViewById(R.id.jr_club_type);
				holder.title  = (TextView) convertView.findViewById(R.id.jr_club_title);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			ArticleData data = getItem(position);
			if(space == null) {
				space = DisplayUtils.getSpaceText(holder.title, 72);
			}
			holder.title.setText(space + data.getTitle1());
			//DisplayUtils.setTextViewEllipsizeEndBeforeOnResume(holder.title, false);
			imageLoader.setRemoteImage(holder.icon, data.getImage1(), position);
			boolean typeNews = true;
			List<String> tags = data.getTagList();
			if(tags == null || tags.size() == 0) {
				typeNews = true;
			} else {
				typeNews = tags.get(0).equalsIgnoreCase("news");
			}
			if (typeNews) {
				imageLoader.setResourceImage(holder.type, R.drawable.jr_club_news_latess, R.drawable.jr_club_news_latess, 0);
			} else {
				imageLoader.setResourceImage(holder.type, R.drawable.jr_club_news_benfits, R.drawable.jr_club_news_benfits, 0);
			}
			if(position == getCount() - 1) {
				// last item
				convertView.setPadding(0, 0, 0, DisplayUtils.dip2px(getActivity(), 60));
			} else {
				convertView.setPadding(0, 0, 0, 0);
			}
			return convertView;
		}

	}
	
	private static class ViewHolder {
		
		ImageView icon;
		ImageView type;
		TextView title;
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
//		((NowplayerJrActivity)getActivity()).enableBackButton(false);
//		((NowplayerJrActivity)getActivity()).showTitleLogo();
		JsonZip jsonZip = new JsonZip(getActivity(), AppInfo.getJsonVersionPath(), Constants.JSON_ZIP_VERSION_PREFIX, LanguageHelper.getCurrentLanguage());
		if (jsonZip.shouldUpdateJSONZipVersion(ZipType.PKG) && !jsonZip.isDownloading()) {
			Article.getInstance();
			showProgressDialog();
			jsonZip.startDownload(ZipType.PKG, callback);
		}else {
			if (!Article.getInstance().isArticleListCompleted()) {
//				AppDataLoader.parseArticle(getActivity());
			}
	        if (progressDialog != null) {
	            progressDialog.dismiss();
	            progressDialog = null;
	        }
	        mHandler.sendEmptyMessage(HANDLER_DATA_READY);
		}
	}
	
	
	private JCB callback = new JCB();
	private class JCB implements JsonZip.Callback {

		@Override
		public void updateProgress(int precent) {
		}

		@Override
		public void onDownloadCompleted(ZipType zipType, boolean isOK) {
//			Log.d(AppDataLoader.TAG, TAG + " onDownloadCompleted " + isOK);
			if (isOK) {
//				AppDataLoader.parseArticle(getActivity());
				closeProgressDialog();
				mHandler.sendEmptyMessage(HANDLER_DATA_READY);
			}
		}
	}
}

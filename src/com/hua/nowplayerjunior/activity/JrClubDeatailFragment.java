package com.hua.nowplayerjunior.activity;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hua.activity.R;
import com.hua.gz.res.ImageLoaderFactory;
import com.hua.gz.widget.FlexibleImageView;
import com.hua.nowplayerjunior.util.loader.SingleImageLoader;
import com.hua.nowplayerjunior.utils.DisplayUtils;
import com.pccw.nmal.model.Article;
import com.pccw.nmal.model.Article.ArticleData;
import com.pccw.nmal.model.StreamInfo;

public class JrClubDeatailFragment extends UIEventBaseFragment {

	private ImageView tdc_imgView;
	private TextView textTitle;
	private TextView textContent;
	private com.hua.gz.res.SingleImageLoader loader;
	private String articleId;
	private ArticleData article;
	private FlexibleImageView fImgView;
	private List<String> tags;

	@Override
	protected void afterQualityAndBookmarkSelected(
			List<List<StreamInfo>> playlist, int quality, int bookmark) {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.jrclub_detail, container, false);
		fImgView = (FlexibleImageView) view.findViewById(R.id.jr_news_image);
		textTitle = (TextView) view.findViewById(R.id.jr_title);
		textContent = (TextView) view.findViewById(R.id.jr_content);
		tdc_imgView = (ImageView) view.findViewById(R.id.jr_qrc_image);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		loadData();
		refreshUI();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		loader = ImageLoaderFactory.getSingleImageLoader(activity);
		loader.setLoadingImage(getResources(), R.drawable.jr_club_topbanner_thumbnail_bg);
		// DisplayUtils.dip2px(ctx, 80);
		// DisplayUtils.getScreenWidth(context) / 2
		// one pixel 4 bytes, width * height * 4. 1080 * 1920 around 8M memory.
		// 16M - 64M
		// loader.setLoadingImage(defaultLoading)
		// loader.setRequestMinWidth(minWidthPixel)
		// loader.setRemoteImage(imageView, url, null, 0)
	}

	@Override
	public void onResume() {
		super.onResume();
//		((NowplayerJrActivity)getActivity()).enableBackButton(true,getResources().getString(R.string.tab_jrclub_title));
	}

	private void loadData() {
		Bundle bundle = getArguments();
		articleId = bundle.getString("article_id");
		article = Article.getInstance().getArticleDataById(articleId);
		tags = article.getTagList();
		boolean typeNews = true;
		if(tags == null || tags.size() == 0) {
			typeNews = true;
		} else {
			typeNews = tags.get(0).equalsIgnoreCase("news");
		}
		if (typeNews) {
//			((NowplayerJrActivity)getActivity()).showTitleText(getResources().getString(R.string.jrclub_news_title));
		} else {
//			((NowplayerJrActivity)getActivity()).showTitleText(getResources().getString(R.string.jrclub_benfits_title));
		}
//		article = new ArticleData();
//		article.setTitle1("Vinyl williamsburg non velit, master cleanse four loko banh mi.");
//		article.setDescription1("Vinyl williamsburg non velit, master cleanse four loko banh mi,Enim kogi keytar trust fund pop-up portland gentrify.");
//		article.setImage1("http://www.dcfever.com/articles/news/2009/10/091020_pccw_hd_channel_01.jpg");
//		article.setImage2("http://www.xieyidian.com/wp-content/uploads/2008/10/101408-1257-1.png");
	}

	private void refreshUI() {
		textTitle.setText(article.getTitle1());
		textContent.setText(article.getDescription1());
		int width = DisplayUtils.getScreenWidth(getActivity());
		int height = FlexibleImageView.getFitHeight(width, 320, 137);
		fImgView.setLayout(false, width, height);
		loader.setRemoteImage(fImgView, article.getImage2(), loader.getLoadingImage(), width);
		//loader.setRemoteImage(tdc_imgView, article.getImage2(), null, 0);
	}
}

package com.hua.nowplayerjunior.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.hua.activity.R;
import com.hua.gz.app.AppLocaleAide;
import com.hua.gz.utils.ExternalIntentUtils;
import com.hua.gz.utils.WebViewUtils;
import com.pccw.nmal.appdata.AppInfo;

public class MoreGameFragment extends Fragment {
	
	private static final String TAG = MoreGameFragment.class.getSimpleName();
	private String url;
	private WebView webView;
	private String url_en, url_zh;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//super.onCreateView(inflater, container, savedInstanceState);
		View view  = inflater.inflate(R.layout.more_game_fragment, container, false);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		((MainActivity)getActivity()).enableBackButton(true, getResources().getString(R.string.tabbar_item_setting_title));
		webView = (WebView)getActivity().findViewById(R.id.webView);
		WebViewUtils.initInternalBrowser(getActivity(), webView);
		WebViewUtils.limitWebView(webView, true, true, true, true);
		webView.setBackgroundColor(0);
		webView.setWebViewClient(new MyWebViewClient());
		webView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				ProgressBar progressBar = (ProgressBar) getActivity().findViewById(R.id.webview_progress);
				if (progressBar != null) {
					if (view.getVisibility() == View.VISIBLE) {
						if (progress == 100) {
							// web view finished loading, hide progress bar
							progressBar.setVisibility(View.GONE);
						} else {
							// web view is loading, show and update progress bar
							progressBar.setProgress(progress);
							progressBar.setVisibility(View.VISIBLE);
						}
					} else {
						progressBar.setVisibility(View.GONE);
					}
				}
			}
		});
		
		url = AppInfo.getRelatedAppURL();
		//url = "https://dl.dropboxusercontent.com/u/248068544/relatedApp/nowplayerJr/html/[lang]/index.html"; //-------test------
		Log.d(TAG, "load url:"+url);
		if (url != null) {
			url_en = url.replace("[lang]", "en-us");
			url_zh = url.replace("[lang]", "zh-hk");
//			//----------------test---------------------
//			url_en = "http://10.37.157.12:8080/relatedApp/en/relatedapp.html";
//			url_zh = "http://10.37.157.12:8080/relatedApp/zh/relatedapp.html";
			if(AppLocaleAide.isAppLocaleEn(getActivity())) {
				webView.loadUrl(url_en);
			} else {
				webView.loadUrl(url_zh);
			}
		}
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (webView != null) {
			final ViewGroup viewGroup = (ViewGroup) webView.getParent();
			if (viewGroup != null) {
				viewGroup.removeView(webView);
			}
			webView.destroy();
			webView = null;
		}
	}
	
	class MyWebViewClient extends WebViewClient{
		
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if(url.startsWith("https://play.google.com/store/apps/details?id=")){
				Log.d(TAG, "jump out for url:"+url);
		        ExternalIntentUtils.goToMarket(getActivity(), url);
		        //Tell the WebView you took care of it.
				return true;
			}else{
				Log.d(TAG, "not jump out for url:"+url);
				view.loadUrl(url);
				return false;
			}
			
		}
		
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			
		}
		
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			if(url.equalsIgnoreCase(url_en) || url.equalsIgnoreCase(url_zh)){
				view.clearHistory(); // clear history, so that cannot go back to previous language
			}
			if (view.canGoBack()) {
				((MainActivity)getActivity()).enableBackButton(true);
			} else {
				((MainActivity)getActivity()).enableBackButton(true, getResources().getString(R.string.tabbar_item_setting_title));
			}
		}
	}


	public boolean onBackPressed() {
		if (webView.canGoBack()) {
			webView.goBack();
			return true;
		} else {
			return false;
		}
	}
	
}

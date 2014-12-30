package com.hua.nowplayerjunior.activity;

import java.util.Random;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hua.activity.R;
import com.pccw.nmal.appdata.AppInfo;
import com.pccw.nmal.util.LanguageHelper;
//import android.app.Fragment;

public class FAQFragment extends Fragment {
	
	//private static final String URL = "http://nowplayer.now.com/public/mobile/androidhelp/index[lang].html";
	private WebView webView;
	private boolean requestClearHistory;
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.faq_fragment, container, false);
        
    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		webView = (WebView) getView().findViewById(R.id.faqWebView);
		webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new FaqWebViewClient());
	    webView.setBackgroundColor(0);
	    webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
		((MainActivity)getActivity()).enableBackButton(true, getResources().getString(R.string.tabbar_item_setting_title));
	}
	
	@Override
	public void onResume() {
		super.onResume();
		webView.loadUrl(AppInfo.getServiceNoticeURL().replace("[lang]", LanguageHelper.getCurrentLanguage().startsWith("zh") ? "-zh" : "") + "?t=" + new Random().nextInt());
		requestClearHistory = true;
	}

	public boolean onBackPressed() {
		if (webView.canGoBack()) {
			webView.goBack();
			return true;
		} else {
			return false;
		}
	}
	
	public void onDestroyView() {
		super.onDestroyView();
		if (webView != null) {
			final ViewGroup viewGroup = (ViewGroup) webView.getParent();
			if (viewGroup != null) {
				viewGroup.removeView(webView);
			}
			webView.destroy();
		}
	}

	class FaqWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.startsWith(WebView.SCHEME_TEL)) {
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url)); 
                startActivity(intent);
				return true;
			}
			return url.startsWith("js2objc:");
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			if (view.canGoBack()) {
				((MainActivity)getActivity()).enableBackButton(true);
			} else {
				((MainActivity)getActivity()).enableBackButton(true, getResources().getString(R.string.tabbar_item_setting_title));
			}
			if (requestClearHistory) {
				view.clearHistory();
				requestClearHistory = false;
			}
		}
	}
}


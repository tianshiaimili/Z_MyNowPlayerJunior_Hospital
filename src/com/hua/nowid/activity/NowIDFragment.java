package com.hua.nowid.activity;

import com.pccw.nmal.util.LanguageHelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class NowIDFragment extends WebViewFragment {

	private static final String TAG = NowIDFragment.class.getName();	
	private String language;
	private String startUrl;
	private String successUrl;
	private String cancelUrl;
	private String userAgent;
	protected ProgressDialog dialog;
	
	public String getLanguage() {
		if (language != null && language.startsWith("zh")) {
			return "zh";
		} else {
			return "en";
		}
	}

	public void setLanguage(String language) {
		if (language == null || language.startsWith("en")) {
			this.language = "en";
		} else if (language.startsWith("zh")) {
			this.language = "zh";
		}
	}

	public String getStartUrl() {
		return startUrl;
	}

	public void setStartUrl(String startUrl) {
		this.startUrl = startUrl;
	}

	public String getSuccessUrl() {
		return successUrl;
	}

	public void setSuccessUrl(String successUrl) {
		this.successUrl = successUrl;
	}

	public String getCancelUrl() {
		return cancelUrl;
	}

	public void setCancelUrl(String cancelUrl) {
		this.cancelUrl = cancelUrl;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getWebView().getSettings().setJavaScriptEnabled(true);
		getWebView().getSettings().setSavePassword(false);
		if (userAgent != null) {
			getWebView().getSettings().setUserAgentString(userAgent);
		}
		getWebView().loadUrl(getStartUrl().replace("[lang]", getLanguage()));
		getWebView().setWebViewClient(new NowIDDefaultWebViewClient());
	}
	
	protected class NowIDDefaultWebViewClient extends WebViewClient {
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
			Log.d("NowIDDefaultWebViewClient", "onPageStarted, url: " + url);
			
			if (dialog != null) {
				dialog.dismiss();
			}
			
			if (url.startsWith(getSuccessUrl())) {
				// Close view
				view.stopLoading();
				Log.d("NowIDDefaultWebViewClient", "Close web view (Success)");
				getActivity().setResult(Activity.RESULT_OK);
				getActivity().finish();
			} else if (url.startsWith(getCancelUrl())) {
				// Close view
				view.stopLoading();
				Log.d("NowIDDefaultWebViewClient", "Close web view (Cancel)");
				getActivity().setResult(Activity.RESULT_CANCELED);
				getActivity().finish();
			} else if (url.endsWith("#open")) {
				view.stopLoading();
				Log.d("NowIDDefaultWebViewClient", "Open system browser");
				Uri uri = Uri.parse(url.substring(0, url.length() - 5));
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			} else {
				dialog = ProgressDialog.show(NowIDFragment.this.getActivity(),
						LanguageHelper.getLocalizedString("progress.dialog.title"), 
						LanguageHelper.getLocalizedString("progress.dialog.message"), true);
			}
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);
			Log.d(TAG, "onPageFinished, url:" + url);
			if (dialog != null) {
				dialog.dismiss();
			}
		}

		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			// TODO Auto-generated method stub
			super.onReceivedError(view, errorCode, description, failingUrl);
			Log.d(TAG, "onReceivedError, url:" + failingUrl);
			if (dialog != null) {
				dialog.dismiss();
			}
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.startsWith(WebView.SCHEME_TEL)) {
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url)); 
                startActivity(intent);
				return true;
			}
			return super.shouldOverrideUrlLoading(view, url);
		}
	}
}

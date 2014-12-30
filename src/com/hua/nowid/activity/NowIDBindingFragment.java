package com.hua.nowid.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class NowIDBindingFragment extends NowIDFragment {
	
	private static final String TAG = NowIDBindingFragment.class.getName();
	private static final String START_URL = "http://tvbinding.now.com/mobilebinding/[lang]/index.jsp";
	private static final String SUCCESS_URL = "http://tvbinding.now.com/mobilebinding/success.html";
	private static final String CANCEL_URL = "http://tvbinding.now.com/mobilebinding/cancel.html";

	public NowIDBindingFragment() {
		super();
		setStartUrl(START_URL);
		setSuccessUrl(SUCCESS_URL);
		setCancelUrl(CANCEL_URL);
	}

}

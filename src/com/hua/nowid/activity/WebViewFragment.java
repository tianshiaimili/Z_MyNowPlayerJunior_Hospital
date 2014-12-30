/*
* Copyright (C) 2010 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.hua.nowid.activity;

import com.pccw.nowid.NowIDLoginStatus;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;

/**
* A fragment that displays a WebView.
* <p>
* The WebView is automically paused or resumed when the Fragment is paused or resumed.
*/
public class WebViewFragment extends Fragment {
    private WebView mWebView;
    private boolean mIsWebViewAvailable;
    private ViewGroup parentVG;

    public WebViewFragment() {
    }

    /**
* Called to instantiate the view. Creates and returns the WebView.
*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        if (mWebView != null) {
            mWebView.destroy();
        }
        parentVG = container;
        mWebView = new WebView(getActivity());
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setVerticalScrollBarEnabled(true);
        mIsWebViewAvailable = true;
        updateSecureCookie();
        
        return mWebView;
    }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//((ViewGroup)mWebView.getParent()).removeView(mWebView);
		//parentVG.addView(mWebView);
	}

	/**
* Called when the fragment is visible to the user and actively running. Resumes the WebView.
*/
    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    /**
* Called when the fragment is no longer resumed. Pauses the WebView.
*/
    @Override
    public void onResume() {
    	updateSecureCookie();
        mWebView.onResume();
        super.onResume();
    }

    /**
* Called when the WebView has been detached from the fragment.
* The WebView is no longer available after this time.
*/
    @Override
    public void onDestroyView() {
        mIsWebViewAvailable = false;
        super.onDestroyView();
    }

    /**
* Called when the fragment is no longer in use. Destroys the internal state of the WebView.
*/
    @Override
    public void onDestroy() {
        if (mWebView != null) {
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    /**
* Gets the WebView.
*/
    public WebView getWebView() {
        return mIsWebViewAvailable ? mWebView : null;
    }
    
    public void updateSecureCookie(){
    	CookieSyncManager.createInstance(this.getActivity().getApplicationContext());
		CookieManager cookieManager = CookieManager.getInstance();
		String cookieString = "NOWSESSIONID="+NowIDLoginStatus.getInstance().getSecureCookie();
		cookieManager.removeAllCookie();
		cookieManager.setCookie(".now.com", cookieString);
		Log.v("WebViewFragment", cookieString);
		CookieSyncManager.getInstance().sync();
    }
}
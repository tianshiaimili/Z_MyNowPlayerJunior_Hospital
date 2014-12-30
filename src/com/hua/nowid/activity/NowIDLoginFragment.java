package com.hua.nowid.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import com.pccw.nmal.util.LanguageHelper;
import com.pccw.nowid.LoginResultTask;

/**
 * Fragment Class for displaying the Now ID login webpage.
 */
public class NowIDLoginFragment extends NowIDFragment {

	public NowIDLoginFragment() {
		super();
		setStartUrl("https://login.now.com/netpass/[lang]/app/now_login.jsp?mode=prod");
	}

	private static final String TAG = NowIDLoginFragment.class.getName();
	private static final String SUCCESS_URL = "https://login.now.com/netpass/app/LoginResult.do";
	private static final String CANCEL_URL = "https://login.now.com/netpass/app/LoginResult.do?tag=login_cancel";

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getWebView().setWebViewClient(new NowIDLoginWebViewClient());
	}

	class NowIDLoginWebViewClient extends NowIDDefaultWebViewClient {

		private final String TAG = NowIDLoginWebViewClient.class.getName();

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			Log.d(TAG, "onPageStarted, url: " + url);

			if (dialog != null) {
				dialog.dismiss();
			}
			
			if (url.startsWith(CANCEL_URL)) {
				Log.d(TAG, "Cancel button clicked");
				if (getActivity() instanceof NowIDLoginActivity) {
					((NowIDLoginActivity)getActivity()).postExecuteFinished();
					getActivity().setResult(Activity.RESULT_CANCELED);
					getActivity().finish();
				}
			} else if (url.startsWith(SUCCESS_URL)) {
				Log.d(TAG, "Stopping at result page");
				view.stopLoading();
				
				LoginResultTask loginResultTask = new LoginResultTask(NowIDLoginFragment.this.getActivity());
				loginResultTask.setCallback((NowIDLoginActivity)NowIDLoginFragment.this.getActivity());
				loginResultTask.execute(url, getUserAgent());
			} else if (url.endsWith("#open")) {
				view.stopLoading();
				Log.d("NowIDDefaultWebViewClient", "Open system browser");
				Uri uri = Uri.parse(url.substring(0, url.length() - 5));
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			} else {
				dialog = ProgressDialog.show(NowIDLoginFragment.this.getActivity(),
						LanguageHelper.getLocalizedString("progress.dialog.title"), 
						LanguageHelper.getLocalizedString("progress.dialog.message"), true);
			}
		}
	}
}
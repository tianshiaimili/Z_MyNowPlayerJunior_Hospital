package com.hua.gz.app;

import java.util.Locale;

import com.hua.gz.app.AppLocaleAide.AppLocaleAideSupport;
import com.hua.gz.app.AppProgressDialogAide.ProgressDialogCancelListener;
import com.hua.gz.app.AppProgressDialogAide.ProgressDialogDismissListener;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class BaseFragmentActivity extends FragmentActivity implements AppLocaleAideSupport {
	
	private AppLocaleAide mAppLocaleAide = new AppLocaleAide(this);
	private AppProgressDialogAide mAppDialogAide;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		mAppDialogAide = new AppProgressDialogAide(this);
		mAppLocaleAide.syncLocaleWithAppLocaleOnCreate(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mAppLocaleAide.syncLocaleWithAppLocaleOnResume(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAppDialogAide.destroyProgressDialog();
	}
	
	//////////////////////ProgressDialog //////////////////////
	
	public void setProgressDialogMessage(CharSequence message) {
		mAppDialogAide.setProgressDialogMessage(message);
	}
	
	public void setProgressDialogDismissListener(ProgressDialogDismissListener listener) {
		mAppDialogAide.setProgressDialogDismissListener(listener);
	}
	
	public void setProgressDialogCancelListener(ProgressDialogCancelListener listener) {
		mAppDialogAide.setProgressDialogCancelListener(listener);
	}
	
	public final void showProgressDialog(boolean cancelable) {
		mAppDialogAide.showProgressDialog(cancelable);
	}
	
	public final void showProgressDialog(Context context, boolean cancelable) {
		mAppDialogAide.showProgressDialog(context, cancelable);
	}
	
	public final void dismissProgressDialog() {
		mAppDialogAide.dismissProgressDialog();
	}
	
	public void setProgressDialogCancelTag(Object cancelTag) {
		mAppDialogAide.setProgressDialogCancelTag(cancelTag);
	}
	
	public final boolean progressDialogHasCanceled(Object cancelTag) {
		return mAppDialogAide.progressDialogHasCanceled(cancelTag);
	}

	//////////////////////end of "ProgressDialog" //////////////////////
	
	////////////////////// Application Locale Configuration //////////////////////
	
	public void setAppLocale(Context context, Locale newLocale) {
		mAppLocaleAide.setAppLocale(context, newLocale);
	}
	
	@Override
	public void onLocaleChanged() {
	}
	
	//////////////////////end of "Application Locale Configuration" //////////////////////
	
}

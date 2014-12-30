package com.hua.app;

import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.Toast;

import com.hua.nowplayerjunior.utils.LogUtils2;

// Referenced classes of package com.pccw.gzmobile.app:
//            AppLocaleAide, AppProgressDialogAide

public class BaseActivity extends Activity
    implements AppLocaleAide.AppLocaleAideSupport
{

	 private static final String TAG =" com/pccw/gzmobile/app/BaseActivity.getSimpleName()";
	    private AppLocaleAide mAppLocaleAide;
	    private AppProgressDialogAide mAppDialogAide;
	
    public BaseActivity()
    {
        mAppLocaleAide = new AppLocaleAide(this);
    }

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        LogUtils2.w((new StringBuilder(String.valueOf(getClass().getName()))).append(" onCreate(), extras = ").append(getIntent().getExtras()).toString());
        if(savedInstanceState != null)
            Log.e(TAG, (new StringBuilder(String.valueOf(getClass().getSimpleName()))).append(" savedInstanceState = ").append(savedInstanceState).toString());
        /**
         * �ı����Ե�����
         */
        mAppLocaleAide.syncLocaleWithAppLocaleOnCreate(this);
        mAppDialogAide = new AppProgressDialogAide(this);
    }

    protected void onRestart()
    {
        super.onRestart();
        LogUtils2.d((new StringBuilder(String.valueOf(getClass().getName()))).append(" onRestart().").toString());
    }

    protected void onStart()
    {
        super.onStart();
        LogUtils2.d((new StringBuilder(String.valueOf(getClass().getName()))).append(" onStart().").toString());
    }

    protected void onResume()
    {
        super.onResume();
        LogUtils2.d((new StringBuilder(String.valueOf(getClass().getName()))).append(" onResume().").toString());
        mAppLocaleAide.syncLocaleWithAppLocaleOnResume(this);
    }

    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        LogUtils2.i((new StringBuilder(String.valueOf(getClass().getName()))).append(" onNewIntent().").toString());
    }

    protected void onPause()
    {
        super.onPause();
        LogUtils2.d((new StringBuilder(String.valueOf(getClass().getName()))).append(" onPause().").toString());
    }

    public void onBackPressed()
    {
        super.onBackPressed();
        Log.v(TAG, (new StringBuilder(String.valueOf(getClass().getName()))).append(" onBackPressed().").toString());
    }

    public void finish()
    {
        super.finish();
        Log.v(TAG, (new StringBuilder(String.valueOf(getClass().getName()))).append(" finish().").toString());
    }

    protected void onStop()
    {
        super.onStop();
        LogUtils2.d((new StringBuilder(String.valueOf(getClass().getName()))).append(" onStop().").toString());
    }

    protected void onDestroy()
    {
        super.onDestroy();
        LogUtils2.i((new StringBuilder(String.valueOf(getClass().getName()))).append(" onDestroy().").toString());
        mAppDialogAide.destroyProgressDialog();
    }

    public boolean dispatchKeyEvent(KeyEvent event)
    {
        LogUtils2.d((new StringBuilder(String.valueOf(getClass().getName()))).append(" dispatchKeyEvent().").toString());
        return super.dispatchKeyEvent(event);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        LogUtils2.d((new StringBuilder(String.valueOf(getClass().getName()))).append(" onKeyDown().").toString());
        return super.onKeyDown(keyCode, event);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event)
    {
        LogUtils2.d((new StringBuilder(String.valueOf(getClass().getName()))).append(" onKeyUp().").toString());
        return super.onKeyUp(keyCode, event);
    }

    public boolean onPrepareOptionsMenu(Menu menu)
    {
        LogUtils2.i((new StringBuilder(String.valueOf(getClass().getName()))).append(" onPrepareOptionsMenu().").toString());
        return super.onPrepareOptionsMenu(menu);
    }

    public void setProgressDialogMessage(CharSequence message)
    {
        mAppDialogAide.setProgressDialogMessage(message);
    }

    public void setProgressDialogDismissListener(AppProgressDialogAide.ProgressDialogDismissListener listener)
    {
        mAppDialogAide.setProgressDialogDismissListener(listener);
    }

    public void setProgressDialogCancelListener(AppProgressDialogAide.ProgressDialogCancelListener listener)
    {
        mAppDialogAide.setProgressDialogCancelListener(listener);
    }

    public final void showProgressDialog(boolean cancelable)
    {
        mAppDialogAide.showProgressDialog(cancelable);
    }

    public final void showProgressDialog(Context context, boolean cancelable)
    {
        mAppDialogAide.showProgressDialog(context, cancelable);
    }

    public final void dismissProgressDialog()
    {
        mAppDialogAide.dismissProgressDialog();
    }

    public void setProgressDialogCancelTag(Object cancelTag)
    {
        mAppDialogAide.setProgressDialogCancelTag(cancelTag);
    }

    public final boolean progressDialogHasCanceled(Object cancelTag)
    {
        return mAppDialogAide.progressDialogHasCanceled(cancelTag);
    }

    public void setAppLocale(Context context, Locale newLocale)
    {
        mAppLocaleAide.setAppLocale(context, newLocale);
    }

    public void onLocaleChanged()
    {
    }

    public void toast(String text, boolean shortDuration)
    {
        Toast.makeText(this, text, shortDuration ? 0 : 1).show();
    }

    public void toast(int textResId, boolean shortDuration)
    {
        Toast.makeText(this, getResources().getString(textResId), shortDuration ? 0 : 1).show();
    }

   

}


/*
	DECOMPILATION REPORT

	Decompiled from: E:\WorkSoftwareTool\NowEclipse\workspace2\nmplayer\trunk\nmplayer\libs\supportlib.jar
	Total time: 328 ms
	Jad reported messages/errors:
	Exit status: 0
	Caught exceptions:
*/
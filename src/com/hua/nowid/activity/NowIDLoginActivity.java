package com.hua.nowid.activity;

import android.content.Context;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;

import com.hua.nowplayerjunior.utils.UserSetting;
import com.hua.nowplayerjunior.utils.UserSetting.UserSettingType;
import com.pccw.nmal.Nmal;
import com.pccw.nmal.nowid.sso.NowIdSSO;
import com.pccw.nmal.nowid.sso.NowTVData;
import com.pccw.nowid.LoginResultTask;
import com.pccw.nowid.NowIDLoginStatus;

public class NowIDLoginActivity extends FragmentActivity implements LoginResultTask.Callback {
	// TODO: handle exception
	public static Context appContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appContext = getApplicationContext();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (VERSION.SDK_INT >= 11 && getActionBar() != null) {
			getActionBar().hide();
		}
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		
		NowIDFragment fragment = new NowIDLoginFragment();
		Bundle extras = getIntent().getExtras();
		fragment.setLanguage(extras != null ? extras.getString("lang") : null);
		fragment.setUserAgent(extras != null ? extras.getString("userAgent") : null);
		fragmentTransaction.add(android.R.id.content, fragment); 
		fragmentTransaction.commit();
	}

	@Override
	public void postExecuteFinished() {
		Log.d("NowIDLoginActivity", NowIDLoginStatus.getInstance().dump());
		setResult(TextUtils.isEmpty(NowIDLoginStatus.getInstance().getSecureCookie()) ? RESULT_CANCELED : RESULT_OK);
		final UserSetting userSetting = new UserSetting(appContext);
    	userSetting.save(UserSettingType.LOGIN_NOWID);
    	
    	if (!TextUtils.isEmpty(NowIDLoginStatus.getInstance().getSecureCookie())) {
    		NowTVData nowTVData = new NowTVData();
    		nowTVData.setSecureCookie(NowIDLoginStatus.getInstance().getSecureCookie());
    		nowTVData.setUserAgent(Nmal.getWebViewUserAgent());
    		NowIdSSO.Factory.getInstance(this).createData(nowTVData);
    	}
    	
		finish();
	}
}

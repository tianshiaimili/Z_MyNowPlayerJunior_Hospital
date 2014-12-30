package com.hua.nowid.activity;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;
import android.os.Bundle;
import android.os.Build.VERSION;

public class NowIDBindingActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		if (VERSION.SDK_INT >= 11 && getActionBar() != null) {
			getActionBar().hide();
		}

		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		
		NowIDFragment fragment = new NowIDBindingFragment();
		Bundle extras = getIntent().getExtras();
		fragment.setLanguage(extras != null ? extras.getString("lang") : null);
		fragment.setUserAgent(extras != null ? extras.getString("userAgent") : null);
		fragmentTransaction.add(android.R.id.content, fragment); 
		fragmentTransaction.commit();
	}
	
}

package com.hua.nowplayerjunior.activity;

import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Window;
import android.view.WindowManager;

import com.hua.nowplayerjunior.receiver.PlayTimeControlAlertDialogReceiver;
import com.hua.nowplayerjunior.service.PlayTimeControlService;
import com.hua.nowplayerjunior.service.PlayerControlProxy;
import com.hua.nowplayerjunior.service.PlayerControlProxy.VideoType;
import com.pccw.nmal.appdata.AppInfo;

public class VideoPlayerActivity extends FragmentActivity {
	
	private Bundle extras;
	private VideoType videoType;
	private PlayTimeControlAlertDialogReceiver ptcAlertDialogReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppInfo.restoreInfo(savedInstanceState);
		
		//LanguageHelper.updateDefaultLanguage();
		PlayerControlProxy.getInstance().setVideoPlayerActivity(this);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setBackgroundDrawable(new ColorDrawable(0));

		if (savedInstanceState == null) {
			extras = getIntent().getExtras();
		} else {
			extras = savedInstanceState.getBundle("extras");
		}
		videoType = VideoType.values()[extras.getInt("videoType")];
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		
		VideoPlayerFragment fragment = new VideoPlayerFragment();
		fragment.setArguments(extras);
		fragmentTransaction.add(android.R.id.content, fragment); 
		fragmentTransaction.commit();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		PlayTimeControlService.setAppInForeground(true);
		ptcAlertDialogReceiver = new PlayTimeControlAlertDialogReceiver(this);
    	LocalBroadcastManager.getInstance(this).registerReceiver(
			ptcAlertDialogReceiver,
    		new IntentFilter(PlayTimeControlAlertDialogReceiver.PLAY_TIME_CONTROL_ALERT_BROADCAST));
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		PlayTimeControlService.setAppInForeground(false);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(ptcAlertDialogReceiver);
	}
	
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        AppInfo.saveInfo(outState);
        outState.putBundle("extras", extras);
    }
    
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		PlayerControlProxy.getInstance().movieIsTerminated();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		VideoPlayerFragment fragment = (VideoPlayerFragment) getSupportFragmentManager().findFragmentById(android.R.id.content);
		if (fragment != null) {
//			fragment.onWindowFocusChanged(hasFocus);
		}
	}
	
    @Override
    public void onAttachFragment(Fragment fragment) {
    	//PlayerControl.getInstance().setFragment((SherlockFragment) fragment);
    	super.onAttachFragment(fragment);
    }
}

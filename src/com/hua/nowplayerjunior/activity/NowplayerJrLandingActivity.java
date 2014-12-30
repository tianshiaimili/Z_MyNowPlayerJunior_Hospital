package com.hua.nowplayerjunior.activity;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Window;
import android.widget.TextView;

import com.hua.activity.R;
import com.hua.app.BaseActivity;
import com.hua.nmal.appdate.AppInfo;
import com.hua.nmal.appdate.AppInfo.DownloadInfoCallback;
import com.hua.nowplayerjunior.constants.Constants;
import com.hua.nowplayerjunior.utils.LogUtils2;
import com.lifevibes.lvmediaplayer.LVMediaPlayer;
import com.pccw.nmal.net.UpdateSessionResponseHandler;
import com.pccw.nmal.net.WebTvApiRequest;
import com.pccw.nmal.net.WebTvApiRequest.WebTvApiRequestType;
import com.pccw.nmal.nowid.sso.NowIdSSO;
import com.pccw.nmal.nowid.sso.NowTVData;
import com.pccw.nmal.service.PixelLogService;
import com.pccw.nowid.NowIDLoginStatus;

public class NowplayerJrLandingActivity extends BaseActivity implements DownloadInfoCallback,WebTvApiRequest.Callback {

	private LandingCountDownTimer landing;
	private final long startTime = 2000;
	private final long interval = 1000;
	private String appVersionName;
	private final AppInfo appInfo = new AppInfo(this, Constants.APP_INFO_URL, Constants.APP_INFO_APP_ID);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.landing);
		try {
			appVersionName = (getPackageManager().getPackageInfo(
					getPackageName(), 0)).versionName;

		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		TextView versionView = (TextView) this.findViewById(R.id.app_version);

		versionView.setText("Version " + appVersionName);
		landing = new LandingCountDownTimer(startTime, interval);
		landing.start();
	}

	   @Override
		protected void onDestroy() {
			super.onDestroy();
			if (appInfo != null) {
				appInfo.cancelDownloadInfo();
			}
		}
	
	
	public class LandingCountDownTimer extends CountDownTimer {
		public LandingCountDownTimer(long startTime, long interval) {
			super(startTime, interval);
		}

		@Override
		public void onFinish() {
			// TODO Auto-generated method stub
			LogUtils2.d("LandingCountDownTimer", "onFinish");
			 downloadInfo();
		}

		@Override
		public void onTick(long arg0) {
			// TODO Auto-generated method stub

		}

	}

	public void downloadInfo() {
		
		Intent intent = new Intent(getBaseContext(), MainActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.activityanim_in, R.anim.activityanim_out);
		finish();
	}

	private void toLoadSecureCookie() {
		String secureCookie = null;
		NowTVData nowTVData = NowIdSSO.Factory.getInstance(this).getData();
		if (nowTVData != null) {
			secureCookie = nowTVData.getSecureCookie();
        }
		if (secureCookie != null) {
        	NowIDLoginStatus.getInstance().setSecureCookie(secureCookie);
        	WebTvApiRequest npr = WebTvApiRequest.getInstance();
        	npr.setCallback(this);
        	npr.setAPIDomain(Constants.APP_INFO_URL);
			npr.updateSession(false); // here problem
		} else {
			toContinue();
		}
	}
	
	
	 private void toContinue() {
			Intent Intent = new Intent(getApplicationContext(), MainActivity.class);
			startActivity(Intent);
			landing.cancel();
			finish();
			// Pixel log
	        try {
	            String version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
	            PixelLogService pixelLog = new PixelLogService(
	            		Constants.PIXEL_LOG_URL, 
	            		Constants.PIXEL_LOG_APP_NAME, 
	            		LVMediaPlayer.getUniqueIdentifier(this), 
	            		NowIDLoginStatus.getInstance().getNowID(), 
	            		NowIDLoginStatus.getInstance().getFsa());
	            pixelLog.pixelLogInOpenApp(version);
	        } catch (NameNotFoundException e) {
	            e.printStackTrace();
	        }
	    }
	
	///////below method come from  class DownloadInfoCallback 
	@Override
	public void onDownloadInfoSuccess() {
		
		toContinue();
		
	}

	@Override
	public void onDownloadInfoFailed(String reason) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRegionChanged(String oldRegion, String newRegion) {
		// TODO Auto-generated method stub
		
	}
	/////////up method come from  class DownloadInfoCallback 

	
	///////below method come from  class WebTvApiRequest.Callback
	@Override
	public void onResponseReceived(WebTvApiRequestType requestType,
			String response) {
		new UpdateSessionResponseHandler().parseResponse(response);
		toContinue();		
	}

	@Override
	public void onFailure(WebTvApiRequestType requestType, String errorCode) {
		toContinue();
	}

	/////up method come from  class WebTvApiRequest.Callback
	
}

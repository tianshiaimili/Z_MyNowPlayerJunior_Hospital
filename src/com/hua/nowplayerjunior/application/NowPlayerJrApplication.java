package com.hua.nowplayerjunior.application;

import android.app.Application;

import com.hua.nowplayerjunior.constants.Constants;
import com.hua.nowplayerjunior.utils.BuildConstants;
import com.hua.nowplayerjunior.utils.LogUtils2;
import com.pccw.common.notification.NotificationServiceSetting;
import com.pccw.nmal.Nmal;
import com.pccw.nmal.net.WebTvApiRequest;
import com.pccw.nmal.util.LanguageHelper;

public class NowPlayerJrApplication extends Application{

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		LogUtils2.d("*****NowPlayerJrApplication**********");
		Nmal.init(getApplicationContext());
		WebTvApiRequest.getInstance().setAPIDomain(Constants.apiDoamin);
		BuildConstants.init(getApplicationContext());
		
	}
	
	public static void refreshPushUI(){
		
		NotificationServiceSetting.setCancelBtnString(LanguageHelper.getLocalizedString("notification.close.label"));
		NotificationServiceSetting.setViewBtnText(LanguageHelper.getLocalizedString("notification.open.label"));
		NotificationServiceSetting.setContentTitle(LanguageHelper.getLocalizedString("app_name"));
		
	}
	
	
}

package com.hua.nowplayerjunior.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.hua.activity.R;
import com.hua.nowplayerjunior.application.NowPlayerJrApplication;
import com.pccw.common.notification.NotificationService;
import com.pccw.common.notification.NotificationServiceSetting;

public class NotificationStartupReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		startService(context);
	}
	
	public static void startService(final Context context){
		//NotificationServiceSetting.init(context);
				
		new AsyncTask<Object, Integer, Boolean>(){
	    	@Override
	    	protected Boolean doInBackground(Object... params) {
	    		//NotificationService.startWithAlarmManager(context, NotificationService.class);
	    		context.startService(new Intent(context, NotificationService.class));
	            return true;
	        }
	    }.execute();
	}
	
	public static void registerToPushServer(){
		 //Set the Starting Class
		NotificationServiceSetting.registerStartingClass(NowPlayerJrApplication.class);
		 //Set AppId
		NotificationServiceSetting.registerAppId("10");
		NotificationServiceSetting.setIcon(R.drawable.nav_nowplayer_logo);
		NotificationServiceSetting.setNumOfNotice(5);
		NotificationServiceSetting.setShowDialogNum(1);
		NotificationServiceSetting.setDisplayNum(false);
		 //Set Notification Receiver Time Interval
		NotificationServiceSetting.setTimeInterval(900000);
		NotificationServiceSetting.setFlagAutoClear(true); 

//		String userLanguage = SettingLanguage.getCurrentLanguage();
//		//if(Locale.getDefault().getLanguage().equalsIgnoreCase("zh")){
//		if(userLanguage.equalsIgnoreCase("zh")){
//			NotificationServiceSetting.setCancelBtnString("�����);
//			NotificationServiceSetting.setViewBtnText("�d��");	
//			NotificationServiceSetting.setContentTitle("Nowplayer ����");
//		}else {
//			NotificationServiceSetting.setCancelBtnString("Cancel");
//			NotificationServiceSetting.setViewBtnText("View");	
//			NotificationServiceSetting.setContentTitle("Nowplayer notification");
//		}	
		NowPlayerJrApplication.refreshPushUI();
	}
}

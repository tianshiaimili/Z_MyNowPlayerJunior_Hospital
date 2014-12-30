package com.hua.nowplayerjunior.utils;

import android.content.Context;

import com.hua.gz.app.aide.AppLocaleAide;
import com.pccw.android.ad.common.UserSettings;
import com.hua.nowplayerjunior.utils.UserSetting;

/**
 * 对连接设置的一些设置，还有初始化一开始的语言
 *
 */
public class BuildConstants {
	// usually to define different URL, e.g. true for QA, false for production
 	public static final boolean DEBUG = true;
 	
 	// should show family of app in setting fragment? for samsung store
 	public static final boolean SHOW_FAMILY_APP = true;

	public static void init(Context context) {
		// Ad Engine
		com.pccw.android.ad.common.GlobalInfo.setDebug(DEBUG);
		com.pccw.android.ad.common.AppConstants.setSlotAppName("JL");
		com.pccw.android.ad.common.AppConstants.initUrls();
		// Sync language setting to AdEngine Lib 
		if(AppLocaleAide.isAppLocaleZh(context)){
			UserSettings.setLanguage(UserSettings.LANGUAGE_CHINESE);
		} else { 
			UserSettings.setLanguage(UserSettings.LANGUAGE_ENGLISH);
		}
	}
}

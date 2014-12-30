package com.hua.nowplayerjunior.constants;


public class Constants {

	public static final String PROGRESS_PACKAGE_NAME = "com.hua.activity";
//	public static final String APP_INFO_URL = "https://webtvapi.now.com/WebTVAPI/getAppInfo";
//	public static final String APP_INFO_URL = "https://dl.dropboxusercontent.com/u/76227729/nowplayerJr/appInfo_Android_v2.txt";
	public static final String APP_INFO_URL = "http://webtvapi.now.com/";
//	public static final String APP_INFO_URL = "http://10.37.157.12:8080/nowplayerjr/app_info.txt";
//	public static final String APP_INFO_URL = "http://10.37.157.12:8080/nowplayerjr2/app_info.txt";
	public static final String APP_INFO_APP_ID = "03";
	/**
	 * 可参考 http://blog.csdn.net/wufen1103/article/details/7846691
	 * %n$ms：代表输出的是字符串，n代表是第几个参数，设置m的值可以在输出之前放置空格 
	 * %n$md：代表输出的是整数，n代表是第几个参数，设置m的值可以在输出之前放置空格，也可以设为0m,在输出之
	 */
	public static final String JSON_ZIP_VERSION_PREFIX = "/%1$s";
	public static final String PIXEL_LOG_URL = "http://nowplayer.now.com/plog.gif";
	public static final String PIXEL_LOG_APP_NAME = "AndroidJrApp";
	//public static final String TERMS_AND_CONDITION_TEXT_URL = "http://nowplayer.now.com/public/mobile/TermsAndConditions_%s.txt";
	public static final String TERMS_AND_CONDITION_TEXT_URL = "";
	
	public static final String CMS_JSON_LIVE_CHANNEL_URL_SUFFIX = "%1$s/getAllChannels.json";
	public static final String CMS_JSON_LIVE_CHANNEL_CATALOG = "liveCatalog.json";
	public static final String CMS_JSON_LIVE_CHANNEL_DETAIL = "liveDetail.json";

	public static final String apiDoamin = "http://webtvapi.now.com/";
	
	//--------gz test---------
//	public static final String WATCH_AND_LEARN_CATEGORY_ZIP = "http://10.37.157.12:8080/nowplayerjr/categories.zip";
//	public static final String WATCH_AND_LEARN_CATEGORY = "http://10.37.157.12:8080/nowplayerjr/watch_and_learn.json";
//	public static final String CARD_LIST_URL = "http://10.37.157.12:8080/nowplayerjr/%s.zip";
	//------------dropbox -----------------
//	public static final String WATCH_AND_LEARN_CATEGORY_ZIP = "https://dl.dropboxusercontent.com/u/199594163/pccw/nowplayerjr/categories.zip";
//	public static final String WATCH_AND_LEARN_CATEGORY = "https://dl.dropboxusercontent.com/u/199594163/pccw/nowplayerjr/watch_and_learn.json";
//	public static final String CARD_LIST_URL = "https://dl.dropboxusercontent.com/u/199594163/pccw/nowplayerjr/%s.zip";
    //------------HK QA --------------
	public static final String WATCH_AND_LEARN_CATEGORY_ZIP = "http://nowtvstatic.now.com/np/public/mobile/03/games/watch_and_learn/categories.zip";
	public static final String WATCH_AND_LEARN_CATEGORY = "http://nowtvstatic.now.com/np/public/mobile/03/games/watch_and_learn/watch_and_learn.json";
	public static final String CARD_LIST_URL = "http://nowtvstatic.now.com/np/public/mobile/03/games/watch_and_learn/%s.zip";

	
	public static final String WNL_CAT_FILE_PREX = "catFilePrex";
	public static final String CATEGORY_COUNT = "categoryCount";
	public static final boolean ENABLE_WATCH_AND_LEARN_CHECKOUT = true;
	public static final boolean ENABLE_JR_CLUB_CHECKOUT = true;
	public static final boolean ENABLE_JR_CLUB_AUTO_CHECKOUT = true;
	
	public static final String OLIVE_SING_SONG_JSON="http://10.37.157.12:8080/nowplayerjr201408/OliveCards.json";
	public static final String OLIVE_SING_SONG_IMG_ZIP="http://10.37.157.12:8080/nowplayerjr201408/OliveCards.zip";
	
}

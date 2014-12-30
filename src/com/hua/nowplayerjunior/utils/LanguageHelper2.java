package com.hua.nowplayerjunior.utils;
import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;

public class LanguageHelper2 {

	private static LanguageHelper2 instance;
	private static Context context;
	
	private static String currentLanguage;
	
	private LanguageHelper2(){}
	
    /**
     * Initialize instance. This is called by {@link com.pccw.nmal.Nmal#init(Context)}.
     * @param context the application context.
     */
	public static void init(Context con){
		if(instance == null){
			instance = new LanguageHelper2();
		}		
		if(context == null){
			context = con;
		}
		
	}

	/**
	 * Gets the LanguageHelper instance.
	 * @return the LanguageHelper instance.
	 */
	public static LanguageHelper2 getInstance(){
		return instance;
	}

	/**
	 * Gets the current language saved in preference. If not found in preference, returns
	 * the system language.
	 * @return the current lanauge, which is the first two characters of the Locale's 
	 * language part.
	 */
	public static String getCurrentLanguage() {
		String systemLanguage = Locale.getDefault().getLanguage().substring(0, 2).toLowerCase();
		SharedPreferences userLanguagePreference = context.getSharedPreferences("userLanguage",Context.MODE_PRIVATE);		
		currentLanguage = userLanguagePreference.getString("language", systemLanguage);
		return currentLanguage;
	}

	/**
	 * Sets the current language and save it to preference.
	 * @param currentLanguage the current language to change to. (Should be either "en" for
	 * English and "zh" for Chinese in most case.)
	 */
	public static void setCurrentLanguage(String currentLanguage) { //"zh" or "en"
		LanguageHelper2.currentLanguage = currentLanguage;
		
		SharedPreferences userLanguagePreference = context.getSharedPreferences("userLanguage",Context.MODE_PRIVATE);	
		Editor editor = userLanguagePreference.edit();
		editor.putString("language", currentLanguage);
		editor.commit();	
		
        Locale locale2 = new Locale(currentLanguage); 
        Locale.setDefault(locale2);
        Configuration config2 = new Configuration();
        config2.locale = locale2;
        context.getResources().updateConfiguration(config2, context.getResources().getDisplayMetrics());
	}
	
	/**
	 * Gets the localized String inside the strings resources according to the current
	 * set language.
	 * @param stringName the key name of the string resources
	 * @return the localized String retrieve from strings resource
	 */
	public static String getLocalizedString(String stringName){
		String stringValue = null;		
		setLocale(getCurrentLanguage());
		stringValue = context.getString(context.getResources().getIdentifier(stringName,"string",context.getPackageName()));
		return stringValue;
	}
	
	private static void setLocale(String localeLanguage){
		Locale locale = new Locale (localeLanguage);
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		if(context != null) {
			context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());	
		}
	}
}


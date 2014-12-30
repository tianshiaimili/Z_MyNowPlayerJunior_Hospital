package com.hua.gz.app;

import java.util.Locale;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * A helper class to change app locale and callback onResume() if app locale changed.
 * Note that app locale will save with PreferenceManager.getDefaultSharedPreferences(context)
 * which it is application level SharedPreferences, only one file of this kind, file name is packagename_preferences.xml.
 * 
 * User guide:
 * 1 Activity or Fragment implements AppLocaleAideSupport.
 * 2 private AppLocaleAide mAppLocaleAide = new AppLocaleAide(this);
 * 3 onCreate() call mAppLocaleAide.syncLocaleWithAppLocaleOnCreate(context);
 * 4 onResume() call mAppLocaleAide.syncLocaleWithAppLocaleOnResume(context);
 * 5 Call setAppLocale() when you need. 
 * 
 * @author AlfredZhong
 * @version 2013-07-22
 * @version 2014-01-03, only support onLocaleChanged() callback onResume().
 */
public final class AppLocaleAide {
	
	////////////////////// Application Locale Configuration //////////////////////
	
    private static final String TAG = AppLocaleAide.class.getSimpleName();
    private final AppLocaleAideSupport mAppLocaleAideSupport; // for app locale changed callback.
    private Locale mLocale; // for app locale changed callback.
    public static final Locale SIMPLIFIED_CHINESE = new Locale("zh", "CN");
	public static final Locale TRADITIONAL_CHINESE_HK = new Locale("zh", "HK");
	public static final Locale TRADITIONAL_CHINESE_TW = new Locale("zh", "TW");
	public static final Locale ENGLISH_US = new Locale("en", "US");
	// Locale.toString() examples: "en", "en_US", "_US", "en__POSIX", "en_US_POSIX"
	private static final String APP_LOCALE_SHARED_PREFERENCES_LANGUAGE_KEY = "app_locale_language";
	private static final String APP_LOCALE_SHARED_PREFERENCES_COUNTRY_KEY = "app_locale_country";
	private static final String APP_LOCALE_SHARED_PREFERENCES_VARIANT_KEY = "app_locale_variant";
	private static final String APP_LOCALE_SHARED_PREFERENCES_NO_LANGUAGE = "no_language";
	private static final String APP_LOCALE_SHARED_PREFERENCES_NO_COUNTRY = "NO_COUNTRY";
	private static final String APP_LOCALE_SHARED_PREFERENCES_NO_VARIANT = "NO_VARIANT";
	
    public interface AppLocaleAideSupport {
    	
        /**
         * This method will be called if language changed when activity or fragment onResume().
         * Note that if you don't recreate activities after locale changed,
         * you should call setContentView() to update whole views or setBackgroundDrawable() to update Buttons 
         * or setImageDrawable(), setImageBitmap() to update ImageViews.
         * Do NOT use setXXXResource() to update UI, it may not work on some devices sometimes.
         */
        public void onLocaleChanged();
        
    }
	
    public AppLocaleAide(AppLocaleAideSupport support) {
    	if(support == null) {
    		throw new IllegalArgumentException("AppLocaleAideSupport can NOT be null when using AppLocaleAide instance.");
    	}
    	mAppLocaleAideSupport = support;
    }
    
    /**
     * Set application default locale.
     * Very useful to call this at Application.onCreate() if you don't want the app default locale same as system locale.
     */
    public static void setDefaultAppLocale(Locale defaultLocale) {
    	Locale.setDefault(defaultLocale);
    }
    
    /**
     * Returns application locale.
     */
    public static Locale getAppLocale(Context context) {
        String language = PreferenceManager.getDefaultSharedPreferences(context)
        		.getString(APP_LOCALE_SHARED_PREFERENCES_LANGUAGE_KEY, APP_LOCALE_SHARED_PREFERENCES_NO_LANGUAGE);
        String country = PreferenceManager.getDefaultSharedPreferences(context)
        		.getString(APP_LOCALE_SHARED_PREFERENCES_COUNTRY_KEY, APP_LOCALE_SHARED_PREFERENCES_NO_COUNTRY);
        String variant = PreferenceManager.getDefaultSharedPreferences(context)
        		.getString(APP_LOCALE_SHARED_PREFERENCES_VARIANT_KEY, APP_LOCALE_SHARED_PREFERENCES_NO_VARIANT);
        // check whether we have SharedPreferences language.
        if(language.equals(APP_LOCALE_SHARED_PREFERENCES_NO_LANGUAGE)
        		&& country.equals(APP_LOCALE_SHARED_PREFERENCES_NO_COUNTRY)
        		&& variant.equals(APP_LOCALE_SHARED_PREFERENCES_NO_VARIANT)) {
        	// Never save app locale before, use default locale. You can use setDefaultAppLocale() to update default locale when app launch .
        	// Locale.getDefault() and setDefault() only affect the application locale setting, not system locale setting.
        	return Locale.getDefault();
        } else {
        	return new Locale(language, country, variant);
        }
    }
    
    /**
     * Returns application locale language, e.g. "zh", "en".
     * @param context
     * @return
     */
    @SuppressLint("DefaultLocale")
	public static String getAppLocaleLanguage(Context context) {
    	return getAppLocale(context).getLanguage().substring(0, 2).toLowerCase(Locale.US);
    }
    
    /**
     * Whether current app locale is English.
     */
    public static boolean isAppLocaleEn(Context context) {
    	Locale loc = getAppLocale(context);
    	// Locale.equals() returns true only same language, country and variant.
    	// In case same language different country, we only compare language here: 
    	// !!! NOTE !!! some "Locale.getLanguage()" ROMs return like "zh_CN", some return like "zh". So we use String.contains() not equals().
    	boolean ret = loc.getLanguage().contains("en");
    	Log.d(TAG, "Current app locale is " + loc);
    	return ret;
    }
    
    /**
     * Whether current app locale is Chinese.
     */
    public static boolean isAppLocaleZh(Context context) {
    	Locale loc = getAppLocale(context);
    	// Locale.equals() returns true only same language, country and variant.
    	// In case same language different country, we only compare language here: 
    	// !!! NOTE !!! some "Locale.getLanguage()" ROMs return like "zh_CN", some return like "zh". So we use String.contains() not equals().
    	boolean ret = loc.getLanguage().contains("zh");
    	Log.d(TAG, "Current app locale is " + loc);
    	return ret;
    }
    
	/**
	 * Returns the localized String inside the strings resources according to application locale.
	 * 
	 * @param context
	 * @param stringName the key name of the string resources
	 * @return the localized String retrieve from strings resource
	 */
	public static String getLocalizedString(Context context, String stringName){
		return context.getString(context.getResources().getIdentifier(stringName, "string", context.getPackageName()));
	}
	
    /**
     * Set current activity configuration locale and save application locale setting to SharedPreferences.
     * Remember to refresh UI yourself for this page. Other pages will have onLocaleChanged() callback.
     * 
     * @param context
     * @param newLocale
     * @return true if application locale changed, otherwise false.
     */
    public boolean setAppLocale(Context context, Locale newLocale) {
    	/*
    	 * Check whether the activity or fragment locale is different from application locale setting.
    	 * Because we use "Resources.updateConfiguration()" to achieve app locale, and there is only one application resources,
    	 * so every context will get the same configuration. That means you can NOT check locale by comparing configuration locale and app locale.
    	 * Also, other pages may make minor change on configuration locale, e.g. "en_US" to "en".
    	 * Therefore, we keep an mLocale inside every AppLocaleAide instance, and compare it with app locale.
    	 */
    	Locale resLocale = context.getResources().getConfiguration().locale;
    	Locale selfLocale = mLocale == null ? resLocale : mLocale;
		Log.v(TAG, "selfLocale = " + selfLocale + ", newLocale = " + newLocale 
				+ "; resLocale = " + resLocale + ", defaultLocale =" + Locale.getDefault() + ", mLocale " + mLocale);
		boolean changed;
    	if(selfLocale.equals(newLocale)) {
    		Log.d(TAG, context.getClass().getSimpleName() + " no need to setAppLocale, using same locale " + newLocale);
    		changed = false;
    	} else {
        	// Resources.updateConfiguration() only affect activity/fragment instance, not application.
        	Resources res = context.getResources();
        	Configuration cfg = res.getConfiguration();
        	cfg.locale = newLocale;
        	res.updateConfiguration(cfg, res.getDisplayMetrics());
        	// setDefault locale just to synchronize default locale with application locale. 
        	Locale.setDefault(cfg.locale);
        	// save application locale to SharedPreferences
        	SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        	editor.putString(APP_LOCALE_SHARED_PREFERENCES_LANGUAGE_KEY, newLocale.getLanguage());
        	editor.putString(APP_LOCALE_SHARED_PREFERENCES_COUNTRY_KEY, newLocale.getCountry());
        	editor.putString(APP_LOCALE_SHARED_PREFERENCES_VARIANT_KEY, newLocale.getVariant());
        	editor.commit();
        	Log.w(TAG, context.getClass().getSimpleName() + " setAppLocale from " + selfLocale + " to " + newLocale);
        	changed = true;
    	}
    	// sync mLocale to new application locale no matter locale changed or not.
    	mLocale = newLocale;
    	return changed;
    }
    
    /**
     * Sync activity locale to application locale setting.
     * 
     * Note:
     * 1. Should call this method on Activity.onCreate() before setContentView() or on Fragment.onCreate()
     *    to avoid configuration has been reset by OS.
     * 2. Should call this method on Activity.onResume() or on Fragment.onResume()
     *    to sync configuration if other page update locale.
     * 3. Do NOT call getResources().getXXX() before calling this method.
     * 4. Check locale onResume() instead of onRestart() because some activities don't have onRestart(), 
     *    e.g. activities embedded inside ActivityGroup; or fragments don't have onRestart().
     * 5. Activity locale only affect activity instance, not application.
     *    So you should sync every Activity.onCreate() before setContentView() or on Fragment.onCreate().
     * 
     * @param context
     * @param triggerCallback set true when call this method onResume(), otherwise true.
     */
    private final boolean syncLocaleWithAppLocale(Context context, boolean triggerCallback) {
    	// sync activity locale to application locale before calling onLocaleChanged() callback.
    	boolean changed = setAppLocale(context, getAppLocale(context));
    	if(changed && triggerCallback) {
    		// We have check mAppLocaleAideSupport on constructor. It should not be null.
    		mAppLocaleAideSupport.onLocaleChanged();
    	}
    	return changed;
    }
    
    public final void syncLocaleWithAppLocaleOnCreate(Context context) {
    	Log.v(TAG, "syncLocaleWithAppLocaleOnCreate");
    	syncLocaleWithAppLocale(context, false);
    }
    
    public final void syncLocaleWithAppLocaleOnResume(Context context) {
    	Log.v(TAG, "syncLocaleWithAppLocaleOnResume");
    	syncLocaleWithAppLocale(context, true);
    }

    ////////////////////// end of "Application Locale Configuration" //////////////////////
	
}

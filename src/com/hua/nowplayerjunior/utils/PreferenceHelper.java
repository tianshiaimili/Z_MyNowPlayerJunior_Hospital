package com.hua.nowplayerjunior.utils;

import java.util.UUID;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * Convenience class for reading and writing shared preference.
 * 
 */
public class PreferenceHelper {
    public static final String PROMPTED_TNC = "tncPreference";
    public static final String PROMPTED_VIDEO_QUALITY_WARNING = "videoQualityWarning";
    public static final String GENERATED_UID = "generateUID";
    public static final String ALLOW_STREAM_IN_3G = "allowStreamIn3G";
    public static final String SECURE_COOKIE = "secureCookie";
    
    private static PreferenceHelper instance;
    private static Context mContext;
    
    private PreferenceHelper(){}
    
    /**
     * Initialize instance. This is called by {@link com.pccw.nmal.Nmal#init(Context)}.
     * @param context the application context.
     */
    public static void init(Context context){
        if(instance == null){
            instance = new PreferenceHelper();
        }       
        if(mContext == null){
            PreferenceHelper.mContext = context;
        }
        
    }
    
    /**
     * Gets the PreferenceHelper instance.
     * @return the PreferenceHelper instance.
     */
    public static PreferenceHelper getInstance(){
        return instance;
    }

    /**
     * Gets a boolean preference value with a key.
     * @param key the key of the preference.
     * @return value of the preference. false if the key is not found.
     */
    public static boolean getPreferenceBoolean(String key) {
        SharedPreferences preference = mContext.getSharedPreferences("userPreference",Context.MODE_PRIVATE);       
        return preference.getBoolean(key, false);
    }
    
    /**
     * Gets a String preference value with a key.
     * @param key the key of the preference.
     * @return value of the preference. null if the key is not found.
     */
    public static String getPreferenceString(String key) {
    	SharedPreferences preference = mContext.getSharedPreferences("userPreference",Context.MODE_PRIVATE);
    	return preference.getString(key, null);
    }

    /**
     * Gets an int preference value with a key.
     * @param key the key of the preference.
     * @return value of the preference. null if the key is not found.
     */
    public static int getPreferenceInt(String key) {
    	SharedPreferences preference = mContext.getSharedPreferences("userPreference",Context.MODE_PRIVATE);
    	return preference.getInt(key, 0);
    }
    
    /**
     * Gets a long preference value with a key.
     * @param key the key of the preference.
     * @return value of the preference. null if the key is not found.
     */
    public static long getPreferenceLong(String key) {
    	SharedPreferences preference = mContext.getSharedPreferences("userPreference",Context.MODE_PRIVATE);
    	return preference.getLong(key, 0);
    }
    /**
     * Sets a boolean preference value with a key.
     * @param key the key of the preference.
     * @param value the value of the preference.
     */
    public static void setPreference(String key, boolean value) {
        Log.i("Preference", "set '" + key + "' to '" + value + "'");
        SharedPreferences preference = mContext.getSharedPreferences("userPreference",Context.MODE_PRIVATE);   
        Editor editor = preference.edit();
        editor.putBoolean(key, value);
        editor.commit();    
    }
    
    /**
     * Sets a String preference value with a key.
     * @param key the key of the preference.
     * @param value the value of the preference.
     */
    public static void setPreference(String key, String value) {
        Log.i("Preference", "set '" + key + "' to '" + value + "'");
        SharedPreferences preference = mContext.getSharedPreferences("userPreference",Context.MODE_PRIVATE);   
        Editor editor = preference.edit();
        editor.putString(key, value);
        editor.commit();
    }

    
    /**
     * Sets an int preference value with a key.
     * @param key the key of the preference.
     * @param value the value of the preference.
     */
    public static void setPreference(String key, int value) {
        Log.i("Preference", "set '" + key + "' to '" + value + "'");
        SharedPreferences preference = mContext.getSharedPreferences("userPreference",Context.MODE_PRIVATE);   
        Editor editor = preference.edit();
        editor.putInt(key, value);
        editor.commit();
    }
    
    
    /**
     * Sets a long preference value with a key.
     * @param key the key of the preference.
     * @param value the value of the preference.
     */
    public static void setPreference(String key, long value) {
        Log.i("Preference", "set '" + key + "' to '" + value + "'");
        SharedPreferences preference = mContext.getSharedPreferences("userPreference",Context.MODE_PRIVATE);   
        Editor editor = preference.edit();
        editor.putLong(key, value);
        editor.commit();
    }
    /**
     * Removes the preference from saved preference.
     * @param key the key of the preference.
     */
    public static void removePreference(String key) {
    	Log.i("Preference", "remove '" + key);
        SharedPreferences preference = mContext.getSharedPreferences("userPreference",Context.MODE_PRIVATE);   
        Editor editor = preference.edit();
        editor.remove(key);
        editor.commit();
    }
    
    /**
     * Generate a UUID for this device and save it to preference.
     * @return If a generated UUID is saved in preference, return the UUID. Otherwise,
     * A new UUID is generated, saved in preference and returned to caller.
     */
    public static String getGenerateUID() {
        SharedPreferences preference = mContext.getSharedPreferences("userPreference",Context.MODE_PRIVATE);
        String generateUID = preference.getString(GENERATED_UID, null);
        if (generateUID == null) {
            generateUID = UUID.randomUUID().toString().replace("-", "");
            
            Editor editor = preference.edit();
            editor.putString(GENERATED_UID, generateUID);
            editor.commit();   
        }
        return generateUID;
    }
}


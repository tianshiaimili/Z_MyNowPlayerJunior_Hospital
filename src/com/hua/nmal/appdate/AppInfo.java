package com.hua.nmal.appdate;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.hua.nowplayerjunior.util.http.AsyncHttpPost;
import com.pccw.nmal.Nmal;

/**
 * Class for retrieving AppInfo
 */
public class AppInfo {

	private static final String TAG = AppInfo.class.getSimpleName();
	private static String appInfoUrl;
	private static String appId;
	private static Context context;
	private static String region;
	private static String imageDomain;
	private static String[] tabConfig;
	private static String catalogueDomain;
	private static String allowedVersion;
	private static String serviceNoticeURL;
	private static String relatedAppURL;
	private static String longPlayPrompt;
	private static String verimatrixHost;
	private static String forceUpdateVersion;
	private static String updateURL;
	private static String jsonVersionPath;
	private static boolean regionChanged = false;
	private static String tncURL;
	private static boolean canActivateGiftCode = false;
	private static boolean isGiftCodePromoEnd = false;
	private static String libraryId;
	
	private DownloadInfo downloadInfo;
	
	/**
	 * AppInfo class constructor.
	 * @param context the application context.
	 * @param appInfoUrl the URL to load the AppInfo from.
	 * @param appId the application ID that is assigned to the app.
	 */
	public AppInfo(Context context, String appInfoUrl, String appId) {
		super();
		AppInfo.appInfoUrl = appInfoUrl;//APP_INFO_URL = "http://webtvapi.now.com/";
		AppInfo.appId = appId;//APP_INFO_APP_ID = "03";
		AppInfo.context = context;
	}

	/**
	 * Gets the app ID
	 * @return the app ID
	 */
	public static String getAppId() {
		return appId;
	}
	
	/**
	 * Gets the region of the device.
	 * @return the region in two letters. (e.g. "HK", "OO")
	 */
	public static String getRegion() {
		return region;
	}
	
	/**
	 * Determine if the device is running in "Oversea" mode.
	 * @return true if the device is in Oversea, false otherwise.
	 */
	public static boolean isOversea() {
		return !region.equalsIgnoreCase("HK");
	}
	
	/**
	 * Gets the image domain. The image domain is the prefix for loading image resources 
	 * from the network. 
	 * @return the image domain.
	 */
	public static String getImageDomain() {
		return imageDomain;
	}

	/**
	 * Gets the tab configuration. The App should only display tabs that are present in
	 * this list and obey to the order when displaying tabs. 
	 * @return the tab configuration.
	 */
	public static String[] getTabConfig() {
		return tabConfig;
	}
	
	/**
	 * Gets the catalogue domain. For retrieving JSON zip files.
	 * @return the catalogue domain.
	 */
	public static String getCatalogueDomain() {
		return catalogueDomain;
	}
	
	/**
	 * Gets the service notice URL. The service notice URL contains a string "[lang]",
	 * which should be replaced with empty string for English language and "_zh" for 
	 * Chinese language when used to load the service notice page actually.
	 * @return the service notice URL.
	 */
	public static String getServiceNoticeURL() {
		return serviceNoticeURL;
	}
	
	/**
	 * Gets the related app URL. The related app URL contains a string "[lang]",
	 * which should be replaced with empty string for English language and "_zh" for 
	 * Chinese language when used to load the service notice page actually.
	 * @return the service notice URL.
	 */
	public static String getRelatedAppURL() {
		return relatedAppURL;
	}
	
	/**
	 * Gets the long play prompt time.
	 * @return the long play prompt time in milliseconds.
	 */
	public static long getLongPlayPromptLong(){
		Log.d("getLongPlayPromptLong", longPlayPrompt);
		if (longPlayPrompt==null||"".equals(longPlayPrompt)){
			return 180l * 60000l;
		}
		Long temp = Long.parseLong(longPlayPrompt.trim())* 60000;
		return temp;
	}
	
	/**
	 * Gets the host for Verimatrix server.
	 * @return the host for Verimatrix server.
	 */
	public static String getVerimatrixHost() {
		return verimatrixHost;
	}
	
	/**
	 * Check if the app needs forced update. When a forced update is needed, the app should
	 * show a popup dialogue prompting the user about that, and direct the user to the App
	 * update page (e.g. a market://package_name URL) when the dialogue is close.
	 * @return true if forced update needed, false otherwise.
	 */
	public static boolean isForceUpdate() {
		boolean result = false;
		
		PackageManager pm = context.getPackageManager();
		try {
			String currentVersion = pm.getPackageInfo(context.getPackageName(), 0).versionName;
			if (forceUpdateVersion != null) {
				result = convertVersionName(currentVersion) < convertVersionName(forceUpdateVersion);
			} else {
				result = false;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * Get the Apps update URL
	 * @return Update URL in string; null if not provided from appinfo.
	 */
	public static String getUpdateURL() {
		return updateURL;
	}
	
	/**
	 * Get the JSON version URL
	 * @return URL of json version in string; null if not provided from appinfo.
	 */
	public static String getJsonVersionPath() {
		return jsonVersionPath;
	}
	
	/**
	 * Get the TnC URL
	 * @return TnC URL version in string; null if not provided from appinfo.
	 */
	public static String getTncURL() {
		return tncURL;
	}
	
	public static boolean canActivateGiftCode(){
		return canActivateGiftCode;
	}
	
	public static boolean isGiftCodePromoEnd(){
		return isGiftCodePromoEnd;
	}
	
	public static String getLibraryId() {
		return libraryId;
	}
	
	/**
	 * Callback interface for AppInfo download 
	 */
	public interface DownloadInfoCallback {
		/**
		 * Callback when AppInfo has been downloaded successfully.
		 */
		public void onDownloadInfoSuccess();
		/**
		 * Callback when AppInfo download has failed.
		 * @param reason the reason of failure.
		 */
		public void onDownloadInfoFailed(String reason);
		/**
		 * Callback when a change of region is detected.
		 * @param oldRegion the old region code before downloading.
		 * @param newRegion the new region code after downloading.
		 */
		public void onRegionChanged(String oldRegion, String newRegion);
	}
	
	private DownloadInfoCallback downloadInfoCallback;

	/**
	 * Sets the object to receive the AppInfo callbacks.
	 * @param dcc the callback object to receive the callbacks.
	 */
	public void setDownloadConfigCallback(DownloadInfoCallback dcc) {
		downloadInfoCallback = dcc;
	}

	private class DownloadInfo extends AsyncHttpPost {

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != null) {
				if (parseInfoJSON(result)) {
					if (downloadInfoCallback != null) {
						downloadInfoCallback.onDownloadInfoSuccess();
						if (regionChanged) {
							downloadInfoCallback.onRegionChanged(null, region);
						}
					}
				}
			} else {
				if (downloadInfoCallback != null) {
					downloadInfoCallback.onDownloadInfoFailed("cannot load config info JSON");
				}
			}
		}
	}
	
	/**
	 * Starts downloading AppInfo. Caller should use {@link #setDownloadConfigCallback(DownloadInfoCallback dcc)}
	 * method to set the callback object to receive the result of the download.
	 */
	public void downloadInfo() {
		cancelDownloadInfo();
		downloadInfo = new DownloadInfo();
		String dlURL;
        
		dlURL = appInfoUrl+getAppId()+"/"+"1"+"/"+"getAppInfo";
		HttpPost httpPost = new HttpPost(dlURL);
		//HttpPost httpPost = new HttpPost(appInfoUrl);
        StringEntity se;
		try {
			se = new StringEntity("{\"appVersion\": \"" + trimAppVersion(getVersionName()) +"\",\"deviceType\": \"ANDROID\",\"callerReferenceNo\": \"" + "C" + Calendar.getInstance().getTimeInMillis() +  "\"}");
	        httpPost.setEntity(se);
	        httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
	        httpPost.setHeader("User-Agent", Nmal.getWebViewUserAgent());

			Log.d(TAG, "AppInfo URL: " + appInfoUrl);
			if (android.os.Build.VERSION.SDK_INT < 11){//before honeycomb
				downloadInfo.execute(httpPost);
			} else{
				downloadInfo.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,httpPost);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Cancel an AppInfo download in progress.
	 */
	public void cancelDownloadInfo() {
		Log.d("InfoService", "cancelDownloadInfo() called");
		if (downloadInfo != null) {
			downloadInfo.cancel(true);
		}
	}
	
	public String getVersionName() {
		PackageManager pm = context.getPackageManager();
		String currentVersion = null;
		
		try {
			Log.d(TAG, context.getPackageName());
			currentVersion = pm.getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		return currentVersion;
		
	}
	
	/**
	 * Check if the app's version has newer version. If both this method and the {@link #isForceUpdate()}
	 * method return false, a dialogue should be shown to remind user an app update is available,
	 * but should also let user bypass the update temporarily.
	 * @return true if no updates available, false otherwise.
	 */
	public boolean isAllowedVersion() {
		boolean	result = false;

		PackageManager pm = context.getPackageManager();
		try {
			String currentVersion = pm.getPackageInfo(context.getPackageName(), 0).versionName;
			if (allowedVersion != null) {
				result = convertVersionName(currentVersion) >= convertVersionName(allowedVersion);
			} else {
				result = false;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return result;
	}
	
	private static double convertVersionName(String version) throws Exception{
		if (version == null) {
			return 0.0;
		} else {
			// Strip all characters behind dot until one dot left
			while (version.indexOf(".") != version.lastIndexOf(".")) {
				version = version.substring(0, version.lastIndexOf("."));
			}
			return Double.valueOf(version);
		}
	}

	private boolean parseInfoJSON(String jsonContent) {
		Log.d("InfoService", "parseInfoJSON: " + jsonContent);
		try {
			JSONObject jsonObj = new JSONObject(jsonContent);
			
			// compulsory fields
			String tempImageDomain = jsonObj.optString("imageDomain");
			if (tempImageDomain == null) {
				if (downloadInfoCallback != null) {
					downloadInfoCallback.onDownloadInfoFailed("imageDomain is null");
				}
				return false;
			}
			
			String tempRegion = jsonObj.getString("region");
			if (tempRegion == null) {
				if (downloadInfoCallback != null) {
					downloadInfoCallback.onDownloadInfoFailed("region is null");
				}
				return false;
			}
			
			JSONArray tabConfigArray = jsonObj.optJSONArray("tabConfig");
			if (tabConfigArray == null) {
				/*
				if (downloadInfoCallback != null) {
					downloadInfoCallback.onDownloadInfoFailed("tabConfig is null");
				}
				return false;
				*/
				Log.w(TAG, "tabConfig is null");
				tabConfigArray = new JSONArray();
			}
			String[] tempTabConfig = new String[tabConfigArray.length()];
			for (int i = 0; i < tabConfigArray.length(); i++) {
				tempTabConfig[i] = tabConfigArray.getString(i);
			}

			String tempCatalogueDomain = jsonObj.optString("catalogueDomain");
			if (tempCatalogueDomain == null) {
				if (downloadInfoCallback != null) {
					downloadInfoCallback.onDownloadInfoFailed("catalogueDomain is null");
				}
				return false;
			}
			
			String tempAllowedVersion = jsonObj.optString("allowedVersion");
			if (tempAllowedVersion == null) {
				if (downloadInfoCallback != null) {
					downloadInfoCallback.onDownloadInfoFailed("allowedVersion is null");
				}
				return false;
			}
			
			String tempServiceNoticeURL = jsonObj.getString("serviceNoticeURL");
			if (tempServiceNoticeURL == null) {
				if (downloadInfoCallback != null) {
					downloadInfoCallback.onDownloadInfoFailed("serviceNoticeURL is null");
				}
				return false;
			}
			
			String tempRelatedAppURL = jsonObj.optString("relatedAppURL");
			if (tempRelatedAppURL == null) {
				if (downloadInfoCallback != null) {
					downloadInfoCallback.onDownloadInfoFailed("relatedAppURL is null");
				}
				return false;
			}
			
			String tempLongPlayPrompt = jsonObj.optString("longPlayPrompt");
			if (tempLongPlayPrompt == null) {
				if (downloadInfoCallback != null) {
					downloadInfoCallback.onDownloadInfoFailed("longPlayPrompt is null");
				}
			}

			String tempVerimatrixHost = jsonObj.optString("verimatrixHost");
			if (tempVerimatrixHost == null) {
				if (downloadInfoCallback != null) {
					downloadInfoCallback.onDownloadInfoFailed("verimatrixHost is null");
				}
			}
			
			String tempForceUpdateVersion = jsonObj.optString("forceUpdateVersion");
			if (tempForceUpdateVersion == null) {
				if (downloadInfoCallback != null) {
					downloadInfoCallback.onDownloadInfoFailed("tempForceUpdateVersion is null");
				}
			}
			
			String tempJsonVersionPath = jsonObj.optString("jsonVersionPath");
			if (tempJsonVersionPath == null) {
				if (downloadInfoCallback != null) {
					downloadInfoCallback.onDownloadInfoFailed("jsonVersionPath is null");
				}
			}
			
			String tempTncURL = jsonObj.optString("tncURL");
			if (tempTncURL == null) {
				if (downloadInfoCallback != null) {
					downloadInfoCallback.onDownloadInfoFailed("tempTncURL is null");
				}
			}
			
			
			String tempCanActivateGiftCode = jsonObj.optString("canActivateGiftCode");
			if (tempCanActivateGiftCode == null) {
				if (downloadInfoCallback != null) {
					downloadInfoCallback.onDownloadInfoFailed("canActivateGiftCode is null");
				}
			}
			
			
			String tempIsGiftCodePromoEnd = jsonObj.optString("isGiftCodePromoEnd");
			if (tempIsGiftCodePromoEnd == null) {
				if (downloadInfoCallback != null) {
					downloadInfoCallback.onDownloadInfoFailed("isGiftCodePromoEnd is null");
				}
			}
			
			// Set to static copy after checking all values are valid
			imageDomain = tempImageDomain;
			regionChanged = !tempRegion.equals(region);
			region = tempRegion;
			tabConfig = tempTabConfig;
			catalogueDomain = tempCatalogueDomain;
			allowedVersion = tempAllowedVersion;
			serviceNoticeURL = tempServiceNoticeURL;
			relatedAppURL = tempRelatedAppURL;
			longPlayPrompt = tempLongPlayPrompt;
			verimatrixHost = tempVerimatrixHost;
			forceUpdateVersion = tempForceUpdateVersion;
			jsonVersionPath = tempJsonVersionPath;
			tncURL = tempTncURL;
			canActivateGiftCode = "true".equalsIgnoreCase(tempCanActivateGiftCode) || "yes".equalsIgnoreCase(tempCanActivateGiftCode);
			isGiftCodePromoEnd = "true".equalsIgnoreCase(tempIsGiftCodePromoEnd) || "yes".equalsIgnoreCase(tempIsGiftCodePromoEnd);
			//optional fields
			//updateURL
			String tempUpdateURL = jsonObj.optString("updateURL");
			updateURL = (tempUpdateURL == null || "".equals(tempUpdateURL)) ? null:jsonObj.optString("updateURL");
			
			String tempLibraryId = jsonObj.optString("libraryId").trim();
			libraryId = (tempLibraryId == null || tempLibraryId.isEmpty()) ? null: tempLibraryId;
			
	        // For AdEngine Lib
			//UserSettings.setOversea(InfoService.isOversea());
			//UserSettings.setRegion(InfoService.getRegion());
			
			return true;
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			if (downloadInfoCallback != null) {
				downloadInfoCallback.onDownloadInfoFailed("parse JSON failed");
			}
			return false;
		}
	}
	
	public static void restoreInfo(Bundle bundle) {
		if (bundle != null) {
			Log.d("InfoService", "restoreInfo");
			region = bundle.getString("region");
			imageDomain = bundle.getString("imageDomain");
			catalogueDomain = bundle.getString("catalogueDomain");
			allowedVersion = bundle.getString("allowedVersion");
			serviceNoticeURL = bundle.getString("serviceNoticeURL");
			relatedAppURL = bundle.getString("relatedAppURL");
			longPlayPrompt = bundle.getString("longPlayPrompt");
			tabConfig = bundle.getStringArray("tabConfig");
			regionChanged = bundle.getBoolean("regionChanged");
			verimatrixHost = bundle.getString("verimatrixHost");
			forceUpdateVersion = bundle.getString("forceUpdateVersion");
			updateURL = bundle.getString("updateURL");
			tncURL = bundle.getString("tncURL");
			
			// For AdEngine Lib
			//UserSettings.setOversea(InfoService.isOversea());
		}
	}
	
	public static void saveInfo(Bundle bundle) {
		if (bundle != null) {
			Log.d("InfoService", "saveInfo");
			bundle.putString("region", region);
			bundle.putString("imageDomain", imageDomain);
			bundle.putString("catalogueDomain", catalogueDomain);
			bundle.putString("allowedVersion", allowedVersion);
			bundle.putString("serviceNoticeURL", serviceNoticeURL);
			bundle.putString("relatedAppURL", relatedAppURL);
			bundle.putString("longPlayPrompt", longPlayPrompt);
			bundle.putStringArray("tabConfig", tabConfig);
			bundle.putBoolean("regionChanged", regionChanged);
			bundle.putString("verimatrixHost", verimatrixHost);
			bundle.putString("forceUpdateVersion", forceUpdateVersion);
			bundle.putString("updateURL", updateURL);
			bundle.putString("tncURL", tncURL);
		}
	}
	
	public static String trimAppVersion(String av){
		String result = "";
		if(av!=null && !"".equals(av)){
			String[] array = av.split("\\.");
			if (array!=null && array.length>=2){
				result = array[0]+"."+array[1];
			}
		}
		return result;
	}
	
	public static String getDownloadUrl(){
		return appInfoUrl+getAppId()+"/"+"1"+"/"+"getAppInfo";
	}
}


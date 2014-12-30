package com.hua.nowplayerjunior.os;

import com.hua.nowplayerjunior.ref.ReflectionUtils;


/**
 * @author AlfredZhong
 * @version 2012-09-10
 * @version 2012-09-12, added reflection of VERSION.SDK_INT if parse VERSION.SDK failed.
 */
public final class AndroidSDK {

	private static final String TAG = AndroidSDK.class.getSimpleName();
	
	/*
	 * When comes to API Level, we should pay attention to:
	 * 
	 * 1. Class exists ?
	 * If not, will cause java.lang.ClassNotFoundException or java.lang.NoClassDefFoundError
	 * You can use If-Else to choose use class or not.
	 * 
	 * 2. Method exists ?
	 * If not, will cause java.lang.NoSuchMethodError or java.lang.VerifyError
	 * You can use If-Else to choose use method or not if method is not used to initiate constant value.
	 * 
	 * 3. Constant value ?
	 * If so, check the value initiate by static method ?
	 * If so, check the static method, if the method can not run in current API level,
	 * will cause java.lang.VerifyError: unable to resolve static field xxx; 
	 * if not(initiate by constant value, not static method), feel free to use it.
	 * 
	 * In a word, don't use any class or any method that not exists in current API level,
	 * nor constant value initiate by an static method which can not run in current API level.
	 * 
	 * So, this class can NOT have any class or method that not exist in API level 1,
	 * or constant value as we have mentioned above.
	 */
	private AndroidSDK() {};
	
	/**
	 * Current device Android API level.
	 * 
	 * @see android.os.Build.VERSION_CODES
	 * @since API Level 1
	 */
	public static final int API_LEVEL = getApiLevel();
	
	/*
	 * Only run once to initiate API_LEVEL.
	 * This method only cost 1 ~ 2ms at most.
	 */
	@SuppressWarnings("deprecation")
	private static int getApiLevel() {
		/*
		 * android.os.Build.VERSION.SDK since API 1, android.os.Build.VERSION.SDK_INT since API 4.
		 * Use Build.VERSION.SDK_INT in pre-DONUT (Android 1.6) devices will cause java.lang.VerifyError:
		 * unable to resolve static field SDK_INT in Landroid/os/Build$VERSION.
		 * So we use android.os.Build.VERSION.SDK here.
		 */
		final int INVALID_API_LEVEL = 0;
		int apiLevel = INVALID_API_LEVEL;
		try {
			// NumberFormatException will never happen here, but we still try catch due to it is deprecated.
			apiLevel = Integer.parseInt(android.os.Build.VERSION.SDK);
			android.util.Log.w(TAG, "android.os.Build.VERSION.SDK = " + apiLevel);
		} catch(Exception e) {
			android.util.Log.e(TAG, "parseInt(android.os.Build.VERSION.SDK) failed : " + e);
		}
		if(apiLevel == INVALID_API_LEVEL) {
			// parse android.os.Build.VERSION.SDK failed.
			try {
				/*
				 * Use reflection to get android.os.Build.VERSION.SDK_INT,
				 * otherwise Android OS will initiate SDK_INT due to it is a constant value,
				 * no matter "apiLevel == INVALID_API_LEVEL" true or false
				 * which will cause java.lang.VerifyError: unable to resolve static field SDK_INT; 
				 */
				apiLevel = (Integer) ReflectionUtils.getStaticFieldValue(ReflectionUtils.getField(
						ReflectionUtils.getInnerClass("android.os.Build", "VERSION"), true, "SDK_INT"));
				android.util.Log.w(TAG, "android.os.Build.VERSION.SDK_INT = " + apiLevel);
			} catch (Exception e) {
				android.util.Log.e(TAG, "reflect android.os.Build.VERSION.SDK_INT failed : " + e);
			}
		}
		/*
		 * Constant values will be written to constant value table when compile. It won't load the class when we use class's constant value.
		 * So use constant values won't cause java.lang.ClassNotFoundException or java.lang.VerifyError.
		 * So feel free to use Build.VERSION_CODES.XX in any Android API level device, even though Build.VERSION_CODES since API 4.
		 */
		return apiLevel;
	}
	
}

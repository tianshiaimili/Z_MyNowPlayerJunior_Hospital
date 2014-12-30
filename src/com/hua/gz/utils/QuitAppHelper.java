package com.hua.gz.utils;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.util.Log;

/**
 * User guide:
 * 1. Call onActivityCreate() in Activity onCreate() after super.onCreate()
 * 2. Call onActivityDestroy() in Activity onDestroy() after super.onDestroy().
 * 3. Call quitApp() to quit application.
 * 
 * @version 2013-02-22
 */
public class QuitAppHelper {

	private static final String TAG = QuitAppHelper.class.getSimpleName();
	private static List<Activity> sActivitiesList;
	private static boolean mQuitted;
	
	private QuitAppHelper() {
	}
	
	private static synchronized List<Activity> getActivityList() {
		if(sActivitiesList == null) {
			sActivitiesList = new LinkedList<Activity>();
		}
		return sActivitiesList;
	}
	
	/**
	 * Call in Activity onCreate() after super.onCreate()
	 * @param act the activity to create.
	 * @param ignoreQuittedStateActivities Activty class to ignore quitted state, usually launch app activities.
	 */
	public static synchronized void onActivityCreate(Activity act, Class<? extends Activity>... ignoreQuittedStateActivities) {
		if(ignoreQuittedStateActivities == null || ignoreQuittedStateActivities.length == 0) {
			Log.w(TAG, "No activity need ignore quitted state.");
		} else {
			for(Class<?> clazz : ignoreQuittedStateActivities) {
				/*
				 * We changed quitted ture when we quitted app,
				 * so we have to ignore some activities to reset quitted state to launch app.
				 */
				if(act.getClass().getName().equals(clazz.getName())) {
					Log.w(TAG, act + " is activity that need ignore quitted state. Reset quitted to false.");
					mQuitted = false;
					break;
				}
			}
		}
		if(mQuitted) {
			/*
			 * Reach here only if:
			 * A -- B -- C -- D, press HOME key, app is killed by system. 
			 * User launchs app aging, D onCreate(), ABC have not been recreate yet, at this time, quitted is false.
			 * When user taps BACK key, D removed from list, C onCreate(), since quitted is false, no need to finish C.
			 * If user call quitApp() at C activity, quitted is true, since List<Activity> is clear due to app killed,
			 * and currently only contains C, so quitApp() can only finish C and System will bring back to B.
			 * So we need to check quitted is true to know that B is onCreate() aging due to app killed.
			 * If so, we do NOT need A & B to create, just finish it onCreate().
			 */
			// 
			Log.e(TAG, act + " is recreated after application killed. Since quitted is true, no need to create.");
			act.finish();
			return;
		}
		// normal Activity.onCreate().
		List<Activity> list = getActivityList();
		if(!list.contains(act)) {
			list.add(act);
			Log.d(TAG, "Added activity " + act + " into quit app control list.");
		}
	}
	
	/**
	 * Call in Activity onDestroy() after super.onDestroy()
	 * @param act the activity to destroy.
	 */
	public static synchronized void onActivityDestroy(Activity act) {
		if(mQuitted)
			/*
			 * If mQuitted true, quitApp() will clear the list, no need to List.remove(Object).
			 * Otherwise you will get java.util.ConcurrentModificationException if
			 * the list is modified when quitApp() is not iterating the last list element
			 * (If it is handling the last element, it is fine).
			 */
			return;
		if(getActivityList().remove(act))
			Log.d(TAG, "onActivityDestroy - removed activity " + act + " from quit app control list.");
	}
	
	/**
	 * Finish all the started activities.
	 * Note that this function only finish activities, not including services.
	 */
	public static synchronized void quitApp() {
		mQuitted = true;
		List<Activity> list = getActivityList();
		for (Activity oneActivity : list) {
			Log.d(TAG, "quitApp - activity " + oneActivity);
			oneActivity.finish();
		}
		list.clear();
	}
	
}

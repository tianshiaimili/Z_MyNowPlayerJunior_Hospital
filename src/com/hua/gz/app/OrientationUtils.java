package com.hua.gz.app;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.WindowManager;

/**
 * 1 android:configChanges
 * Specify one or more configuration changes that the activity will handle itself by onConfigurationChanged().
 * Attributes: mcc|mnc|locale|touchscreen|keyboard|keyboardHidden|navigation|orientation|screenLayout|uiMode|screenSize|smallestScreenSize|fontScale
 * Note: usually, when we use fragment, we must use "orientation|screenSize" at least.
 * 
 * 2 android:screenOrientation
 * Specify the orientation an activity should be run in.
 * Attributes: unspecified, user, behind, landscape, portrait, reverseLandscape, reversePortrait, sensorLandscape, sensorPortrait, sensor, fullSensor, nosensor
 * 
 * @author AlfredZhong
 * @version 2013-11-18
 */
public class OrientationUtils {
	
	private static final String TAG = OrientationUtils.class.getSimpleName();
	
	/**
	 * The desired orientation of this activity.
	 */
	public static enum RequestedOrientation {
		SENSOR, PORTRAIT, LANDSCAPE, FULL_SENSOR, SENSOR_PORTRAIT, SENSOR_LANDSCAPE, REVERSE_PORTRAIT, REVERSE_LANDSCAPE;
	}
	
	/**
	 * the rotation of the screen from its "natural" orientation.
	 * ROTATION_0 is PORTRAIT or LANDSCAPE.
	 * 
	 * A simplified overview is shown in the diagram below. 
	 * o is camera; M is menu key, H is home key, B is back key.
	 * 
	 * case: PORTRAIT is ROTATION_0.
	 * ---------
	 * |   o   |
	 * |       |
	 * |       |
	 * |       |
	 * |       |
	 * | M H B |
	 * ---------
	 * case: LANDSCAPE is ROTATION_0.
	 * ------------------
	 * |	    o       |
	 * |                |
	 * |                |
	 * |   M    H    B  |
	 * ------------------
	 */
	public static enum NaturalRotation {
		ROTATION_0, ROTATION_90, ROTATION_180, ROTATION_270;
	}
	
	/**
	 * The screen display absolute orientation, not the device orientation.
	 * PORTRAIT is ROTATION_0 or ROTATION_270.
	 */
	public static enum ScreenOrientation {
		PORTRAIT, LANDSCAPE, REVERSE_PORTRAIT, REVERSE_LANDSCAPE;
	}

	/**
	 * Sensor, The orientation is determined by the device orientation sensor. 
	 * The orientation of the display depends on how the user is holding the device; it changes when the user rotates the device. 
	 * Some devices, though, will not rotate to all four possible orientations, by default. To allow all four orientations, use "fullSensor".
	 * 
	 * Portrait, Portrait orientation (the display is taller than it is wide).
	 * Landscape, Landscape orientation (the display is wider than it is tall).
	 * 
	 * Full sensor, The orientation is determined by the device orientation sensor for any of the 4 orientations. 
	 * This is similar to "sensor" except this allows any of the 4 possible screen orientations, 
	 * regardless of what the device will normally do (for example, some devices won't normally use reverse portrait or reverse landscape, but this enables those). 
	 * Added in API level 9.
	 *  
	 * Sensor Portrait, Portrait orientation, but can be either normal or reverse portrait based on the device sensor. Added in API level 9.
	 * Sensor Landscape, Landscape orientation, but can be either normal or reverse landscape based on the device sensor. Added in API level 9.
	 * 
	 * Reverse Portrait, Portrait orientation in the opposite direction from normal portrait.Added in API level 9.
	 * Reverse Landscape, Landscape orientation in the opposite direction from normal landscape.Added in API level 9.
	 * 
	 * Note: 
	 * [RequestedOrientation, Activity.onConfigurationChanged() callback about orientation, UI reverse-able]
	 * 1. [sensor | fullSensor(API 9), YES, YES], sensor may only support portrait and landscape, but fullSensor supports 4 orientations.
	 * 2. [sensorPortrait(API 9), NO, YES when portrait <--> reverse portrait]
	 * 3. [sensorLandscape(API 9), NO, YES when landscape <--> reverse landscape]
	 * 4. [portrait | reversePortrait(API 9), NO, NO]
	 * 5. [landscape | reverseLandscape(API 9), NO, NO]
	 * 
	 * @param act
	 * @since API 9
	 */
	public static final void setRequestedOrientation(Activity act, RequestedOrientation orientation) {
		switch(orientation) {
		case SENSOR:
			act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			break;
		case PORTRAIT:
			act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			break;
		case LANDSCAPE:
			act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			break;
		case FULL_SENSOR:
			act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
			break;
		case SENSOR_PORTRAIT:
			act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
			break;
		case SENSOR_LANDSCAPE:
			act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
			break;
		case REVERSE_PORTRAIT:
			act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
			break;
		case REVERSE_LANDSCAPE:
			act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
			break;
		}
	}
	
	/**
	 * Note: portrait and reversePortrait are PORTRAIT; landscape and reverseLandscape are LANDSCAPE.
	 * 
	 * Note: 
	 * [RequestedOrientation, orientation]
	 * 1. [sensor | fullSensor(API 9), PORTRAIT or LANDSCAPE]
	 * 2. [sensorPortrait(API 9), PORTRAIT]
	 * 3. [sensorLandscape(API 9), LANDSCAPE]
	 * 4. [portrait, PORTRAIT]
	 * 5. [landscape, LANDSCAPE]
	 * 6. [reversePortrait(API 9), PORTRAIT]
	 * 7. [reverseLandscape(API 9), LANDSCAPE]
	 * 
	 * @param config Activity.onConfigurationChanged() or getResources().getConfiguration()
	 * @return true if PORTRAIT, false if LANDSCAPE.
	 */
	public static boolean isOrientationPortrait(Configuration config) {
		if(config.orientation == Configuration.ORIENTATION_PORTRAIT) {
			Log.d(TAG, "Current orientation is protrait");
			return true;
		}
		return false;
	}
	
	/**
	 * Note: 
	 * [RequestedOrientation, rotation]
	 * 1. [sensor | fullSensor(API 9), 4 rotations]
	 * 2. [sensorPortrait(API 9), (ROTATION_0 or ROTATION_180) or (ROTATION_90 or ROTATION_270)]
	 * 3. [sensorLandscape(API 9), (ROTATION_90 or ROTATION_270) or (ROTATION_0 or ROTATION_180)]
	 * 4. [portrait, ROTATION_0 or ROTATION_270]
	 * 5. [landscape, ROTATION_90 or ROTATION_0]
	 * 6. [reversePortrait(API 9), ROTATION_180 or ROTATION_90]
	 * 7. [reverseLandscape(API 9), ROTATION_270 or ROTATION_180]
	 * 
	 * @param context
	 * @return
	 * @since API 9
	 */
    public static NaturalRotation getRotation(Context context) {
    	final int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
    	// System.out.println("rotation " + rotation);
        switch (rotation) {
        case Surface.ROTATION_0:
            return NaturalRotation.ROTATION_0;
        case Surface.ROTATION_90:
            return NaturalRotation.ROTATION_90;
        case Surface.ROTATION_180:
            return NaturalRotation.ROTATION_180;
        case Surface.ROTATION_270:
            return NaturalRotation.ROTATION_270;
        default:
        	// never reach here.
            return null;
        }
    }
	
    /**
     * @param act
     * @return
     * @since API 9
     */
    private static ScreenOrientation getScreenOrientationFromSensor(Activity act) {
		boolean portrait = isOrientationPortrait(act.getResources().getConfiguration());
		NaturalRotation rotation = getRotation(act);
		if(portrait) {
			// phone || tablet
			if(rotation == NaturalRotation.ROTATION_0 || rotation == NaturalRotation.ROTATION_270) {
				return ScreenOrientation.PORTRAIT;
			} else {
				// NaturalRotation.ROTATION_180 || NaturalRotation.ROTATION_90
				return ScreenOrientation.REVERSE_PORTRAIT;
			}
		} else {
			// phone || tablet
			if(rotation == NaturalRotation.ROTATION_90 || rotation == NaturalRotation.ROTATION_0) {
				return ScreenOrientation.LANDSCAPE;
			} else {
				// NaturalRotation.ROTATION_270 || NaturalRotation.ROTATION_180
				return ScreenOrientation.REVERSE_LANDSCAPE;
			}
		}
    }
    
	/**
	 * @param act
	 * @return
	 * @since API 9
	 */
	public static ScreenOrientation getScreenOrientation(Activity act) {
		int requestedOrientation = act.getRequestedOrientation();
		switch(requestedOrientation) {
		case ActivityInfo.SCREEN_ORIENTATION_SENSOR:
			return getScreenOrientationFromSensor(act);
		case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
			return ScreenOrientation.PORTRAIT;
		case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
			return ScreenOrientation.LANDSCAPE;
		case ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR:
			return getScreenOrientationFromSensor(act);
		case ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT:
			return getScreenOrientationFromSensor(act);
		case ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE:
			return getScreenOrientationFromSensor(act);
		case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
			return ScreenOrientation.REVERSE_PORTRAIT;
		case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
			return ScreenOrientation.REVERSE_LANDSCAPE;
		default:
			// never reach here.
			return null;
		}
	}
	
    /**
     * @param act
     * @return true if portrait is ROTATION_0, false if portrait is ROTATION_270.
     * @since API 9
     */
    public static boolean isPortraitRotation0(Activity act) {
    	ScreenOrientation orientation = getScreenOrientation(act);
    	NaturalRotation rotation = getRotation(act);
    	switch(orientation) {
    	case PORTRAIT:
    		return rotation == NaturalRotation.ROTATION_0;
    	case LANDSCAPE:
    		return rotation == NaturalRotation.ROTATION_90;
    	case REVERSE_PORTRAIT:
    		return rotation == NaturalRotation.ROTATION_180;
    	case REVERSE_LANDSCAPE:
    		return rotation == NaturalRotation.ROTATION_270;
    	default:
    		// never reach here.
    		return false;
    	}
    }
    
    /**
     * @since API 9
     */
	public static abstract class FourOrientationEventListener extends OrientationEventListener {

		private ScreenOrientation mOrientation = null;
		private Activity mActivity;
		
		public FourOrientationEventListener(Activity act) {
			super(act);
			mActivity = act;
		}
		
		@Override
		public void onOrientationChanged(int naturalDegree) {
			// System.out.println("orientation " + orientation);
			if(naturalDegree == OrientationEventListener.ORIENTATION_UNKNOWN) {
				return;
			}
			if(mOrientation == null) {
				mOrientation = getScreenOrientation(mActivity);
			}
			ScreenOrientation so = null;
			boolean isPortraitRotation0 = isPortraitRotation0(mActivity);
			// use [-20, 20] for 0, 90, 180, 270
			if(naturalDegree >= 340 && naturalDegree <= 359 || naturalDegree >= 0 && naturalDegree <= 20) {
				// 0
				if(isPortraitRotation0) {
					so = ScreenOrientation.PORTRAIT;
				} else {
					so = ScreenOrientation.LANDSCAPE;
				}
			} else if(naturalDegree >= 70 && naturalDegree <= 110) {
				// 90
				if(isPortraitRotation0) {
					so = ScreenOrientation.REVERSE_LANDSCAPE;
				} else {
					so = ScreenOrientation.PORTRAIT;
				}
			} else if(naturalDegree >= 160 && naturalDegree <= 200) {
				// 180
				if(isPortraitRotation0) {
					so = ScreenOrientation.REVERSE_PORTRAIT;
				} else {
					so = ScreenOrientation.REVERSE_LANDSCAPE;
				}
			} else if(naturalDegree >= 250 && naturalDegree <= 290) {
				// 270
				if(isPortraitRotation0) {
					so = ScreenOrientation.LANDSCAPE;
				} else {
					so = ScreenOrientation.REVERSE_PORTRAIT;
				}
			} else {
				so = mOrientation;
			}
			if(mOrientation != so) {
				System.err.println("onOrientationChanged " + so + ", isPortraitRotation0 " + isPortraitRotation0);
				onOrientationChanged(so);
			}
			mOrientation = so;
		}
		
		public abstract void onOrientationChanged(ScreenOrientation orientation);
		
	}
    
}

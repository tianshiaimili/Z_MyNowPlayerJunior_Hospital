package com.hua.gz.utils;

import android.annotation.SuppressLint;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;

/**
 * Animations handy class.
 * @version 2013-11-06
 */
public class AnimUtils {

	public static void startAnimation(View view, Animation anim) {
		view.clearAnimation();
		view.startAnimation(anim);
		//view.invalidate();
	}
	
	@SuppressLint("NewApi")
	public static AlphaAnimation getFadeInOutAnimation(final View view, final float fromAlpha, final float toAlpha, long duration) {
		AlphaAnimation fadeInOut = new AlphaAnimation(fromAlpha, toAlpha);
		fadeInOut.setAnimationListener(new Animation.AnimationListener() {
			public void onAnimationStart(Animation animation) {
				view.setAlpha(fromAlpha); // setAlpha() since from API level 11, Android 3.0
			}
			public void onAnimationEnd(Animation animation) {
				view.setAlpha(toAlpha); // setAlpha() since from API level 11, Android 3.0
			}
			public void onAnimationRepeat(Animation animation) {
			}
		});
		fadeInOut.setDuration(duration);
		return fadeInOut;
	}
	
	public static void startFadeInAnimation(final View view, long duration) {
		startAnimation(view, getFadeInOutAnimation(view, 0, 1, duration));
		// set specific start time sample code
		///Animation anim = getFadeInOutAnimation(view, 0, 1, duration);
		// anim.setStartTime(startTimeMillis);
		// view.setAnimation(anim);
	}
	
	public static void startFadeOutAnimation(final View view, long duration) {
		startAnimation(view, getFadeInOutAnimation(view, 1, 0, duration));
	}
	
	
	public static RotateAnimation getUpsidedownAnimation(boolean clockwise, long durationMillis) {
		float fromDegrees, toDegrees;
		if(clockwise) {
			fromDegrees = -180;
			toDegrees = 0;
		} else {
			fromDegrees = 0;
			toDegrees = -180;
		}
		RotateAnimation anim = new RotateAnimation(fromDegrees, toDegrees, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);  
		anim.setInterpolator(new LinearInterpolator());
		anim.setDuration(durationMillis);  
		anim.setFillAfter(true);
		return anim;
	}
	
	public static void startUpsidedownAnimation(View view, boolean clockwise, long durationMillis) {
		startAnimation(view, getUpsidedownAnimation(clockwise, durationMillis));
	}
	
	public static void startTranslateSelfUpAnimation(View view, long duration, boolean fillAfter, boolean toOriginalPosition) {
		// base on (0, 0), -1 means y moving to Y_0, that means up, therefore 1 means moving down.
		int toY = toOriginalPosition ? 0 : -1;
		// One of Animation.ABSOLUTE, Animation.RELATIVE_TO_SELF, or Animation.RELATIVE_TO_PARENT.
		// The animation end up in the position determined by the layout. So it may be clipped against its parent's bounds.
		TranslateAnimation translate = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, 
				Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, toY);
		translate.setDuration(duration);
		translate.setFillAfter(fillAfter);
		startAnimation(view, translate);
	}
	
	// for the previous movement
		public static Animation inFromRightAnimation() {

			Animation inFromRight = new TranslateAnimation(
					Animation.RELATIVE_TO_PARENT, +1.0f,
					Animation.RELATIVE_TO_PARENT, 0.0f,
					Animation.RELATIVE_TO_PARENT, 0.0f,
					Animation.RELATIVE_TO_PARENT, 0.0f);
			inFromRight.setDuration(200);
			inFromRight.setInterpolator(new AccelerateInterpolator());
			return inFromRight;
		}

		public static Animation outToLeftAnimation() {
			Animation outtoLeft = new TranslateAnimation(
					Animation.RELATIVE_TO_PARENT, 0.0f,
					Animation.RELATIVE_TO_PARENT, -1.0f,
					Animation.RELATIVE_TO_PARENT, 0.0f,
					Animation.RELATIVE_TO_PARENT, 0.0f);
			outtoLeft.setDuration(200);
			outtoLeft.setInterpolator(new AccelerateInterpolator());
			return outtoLeft;
		}

		// for the next movement
		public static Animation inFromLeftAnimation() {
			Animation inFromLeft = new TranslateAnimation(
					Animation.RELATIVE_TO_PARENT, -1.0f,
					Animation.RELATIVE_TO_PARENT, 0.0f,
					Animation.RELATIVE_TO_PARENT, 0.0f,
					Animation.RELATIVE_TO_PARENT, 0.0f);
			inFromLeft.setDuration(200);
			inFromLeft.setInterpolator(new AccelerateInterpolator());
			return inFromLeft;
		}

		public static Animation outToRightAnimation() {
			Animation outtoRight = new TranslateAnimation(
					Animation.RELATIVE_TO_PARENT, 0.0f,
					Animation.RELATIVE_TO_PARENT, +1.0f,
					Animation.RELATIVE_TO_PARENT, 0.0f,
					Animation.RELATIVE_TO_PARENT, 0.0f);
			outtoRight.setDuration(200);
			outtoRight.setInterpolator(new AccelerateInterpolator());
			return outtoRight;
		}
}

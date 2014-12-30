package com.hua.nowplayerjunior.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.hua.activity.R;
import com.hua.nowplayerjunior.activity.NowplayerJrLandingActivity;
import com.hua.nowplayerjunior.receiver.PlayTimeControlAlertDialogReceiver;
import com.pccw.nowid.NowIDLoginStatus;

public class PlayTimeControlService extends IntentService {
	
	private static boolean isAppInForeground;
	
	public PlayTimeControlService() {
		this(PlayTimeControlService.class.getSimpleName());
	}
	
	public PlayTimeControlService(String name) {
		super(name);
	}

	public static void setAppInForeground(boolean inForeground) {
		isAppInForeground = inForeground;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d("PlayTimeControlService", "alarm triggered!!!!");
		
		if (TextUtils.isEmpty(NowIDLoginStatus.getInstance().getSecureCookie())) {
			return;
		}
		
		if (isAppInForeground) {
			Intent newIntent = new Intent(PlayTimeControlAlertDialogReceiver.PLAY_TIME_CONTROL_ALERT_BROADCAST);
			LocalBroadcastManager.getInstance(this).sendBroadcast(newIntent);
		} else {
			// App in background, show notification
			NotificationManager notificationManager = (NotificationManager) 
					  getSystemService(NOTIFICATION_SERVICE);
			Intent newIntent = new Intent(this, NowplayerJrLandingActivity.class);
			newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent pIntent = PendingIntent.getActivity(this, 0, newIntent, 0);
			Notification noti = new NotificationCompat.Builder(this)
	        .setContentTitle(getString(R.string.app_name))
	        .setContentText(getString(R.string.ptc_notification_message))
	        .setSmallIcon(R.drawable.ic_launcher)
	        .setContentIntent(pIntent)
	        .build();
	     
	        noti.flags |= Notification.FLAG_AUTO_CANCEL;

	        notificationManager.notify(0, noti); 
		}
	}

}

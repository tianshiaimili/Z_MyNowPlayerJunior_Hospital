package com.hua.nowplayerjunior.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hua.nowplayerjunior.service.PlayTimeControlAlertDialogController;

public class PlayTimeControlAlertDialogReceiver extends BroadcastReceiver {

	private final Context mContext;
	public static final String PLAY_TIME_CONTROL_ALERT_BROADCAST = "ptcAlertBroadcast";

	public PlayTimeControlAlertDialogReceiver(Context context) {
		this.mContext = context;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		PlayTimeControlAlertDialogController ptcadc = PlayTimeControlAlertDialogController.getInstance(
			this.mContext);
		ptcadc.showAlert();
	}
	
}

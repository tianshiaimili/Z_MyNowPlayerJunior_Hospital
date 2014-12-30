package com.hua.nowplayerjunior.activity;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.hua.activity.R;
import com.hua.nowplayerjunior.constants.Constants;
import com.hua.nowplayerjunior.service.PlayTimeControlAlertDialogController;
import com.hua.nowplayerjunior.service.PlayTimeControlAlertDialogController.PasswordCheckCallback;
import com.hua.nowplayerjunior.utils.MyAlertDialog;
import com.lifevibes.lvmediaplayer.LVMediaPlayer;
import com.pccw.nmal.service.PixelLogService;
import com.pccw.nmal.util.PreferenceHelper;
import com.pccw.nowid.NowIDLoginStatus;

public class SettingPlayTimeControlFragment extends Fragment {

	public static final String PTC_TIME_LIMIT_INDEX_PREF = "ptcTimeLimitIndex";
	public static final String PTC_ALARM_MILLIS = "ptcAlarmMillis";
	private static final int[] timeLimitText = {R.string.ptc_time_limit_1,
		R.string.ptc_time_limit_2, R.string.ptc_time_limit_3,
		R.string.ptc_time_limit_4, R.string.ptc_time_limit_5,
		R.string.ptc_time_limit_6
	};
	private ViewGroup sectionToggle;
	private ViewGroup sectionTimeLimit;
	private ViewGroup sectionPlayTimeEndsAt;
	private Button btnPlayTimeControlToggle;
	private CheckBox chkPlayTimeControl;
	private Button btnTimeLimit;
	private TextView txtTimeLimit;
	private Button btnPlayTimeEndsAt;
	private TextView txtPlayTimeEndsAt;
	
	private BroadcastReceiver ptcListener = new PTCListener();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.setting_playtimecontrol, null);
		sectionToggle = (ViewGroup) view.findViewById(R.id.sectionToggle);
		sectionTimeLimit = (ViewGroup) view.findViewById(R.id.sectionTimeLimit);
		sectionPlayTimeEndsAt = (ViewGroup) view.findViewById(R.id.sectionPlayTimeEndsAt);
		btnPlayTimeControlToggle = (Button) view.findViewById(R.id.btnPlayTimeControlToggle);
		btnPlayTimeControlToggle.setOnClickListener(new ToggleListener());
		chkPlayTimeControl = (CheckBox) view.findViewById(R.id.chkPlayTimeControl);
		btnTimeLimit = (Button) view.findViewById(R.id.btnTimeLimit);
		btnTimeLimit.setOnClickListener(new PlayTimeSelectListener());
		txtTimeLimit = (TextView) view.findViewById(R.id.txtTimeLimit);
		btnPlayTimeEndsAt = (Button) view.findViewById(R.id.btnPlayTimeEndsAt);
		txtPlayTimeEndsAt = (TextView) view.findViewById(R.id.txtPlayTimeEndsAt);
		return view;
	}


	@Override
	public void onResume() {
		super.onResume();
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(ptcListener, new IntentFilter(PlayTimeControlAlertDialogController.PLAY_TIME_CONTROL_ACTION_UNLOCKED));
//		((NowplayerJrActivity)getActivity()).enableBackButton(true, getResources().getString(0/*R.string.tabbar_item_setting_title*/));
		refreshUI();
	}

	@Override
	public void onPause() {
		super.onPause();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(ptcListener);
	}
	
	public void refreshUI() {
		long alarmTime = PreferenceHelper.getPreferenceLong(PTC_ALARM_MILLIS);
		if (alarmTime > 0) {
			chkPlayTimeControl.setChecked(true);
			sectionTimeLimit.setVisibility(View.GONE);
			sectionPlayTimeEndsAt.setVisibility(View.VISIBLE);
			String alarmString = new SimpleDateFormat("HH:mm").format(new Date(alarmTime));
			txtPlayTimeEndsAt.setText(alarmString);
		} else {
			chkPlayTimeControl.setChecked(false);
			sectionTimeLimit.setVisibility(View.VISIBLE);
			sectionPlayTimeEndsAt.setVisibility(View.GONE);
			txtTimeLimit.setText(getString(timeLimitText[PreferenceHelper.getPreferenceInt(PTC_TIME_LIMIT_INDEX_PREF)]));
		}
	}
	
	private class ToggleListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (!chkPlayTimeControl.isChecked()) {
				int index = PreferenceHelper.getPreferenceInt(PTC_TIME_LIMIT_INDEX_PREF);
				int minutes = 0;//SettingPlayTimeSelectFragment.TIME_LIMIT_IN_MINUTES[index];
				startPTCAlarm(minutes);
				refreshUI();
				PixelLogService pixelLog = new PixelLogService(Constants.PIXEL_LOG_URL, Constants.PIXEL_LOG_APP_NAME, LVMediaPlayer.getUniqueIdentifier(getActivity()), NowIDLoginStatus.getInstance().getNowID(), NowIDLoginStatus.getInstance().getFsa());
				pixelLog.pixelLogAction(pixelLog.PIXELLOG_PLAYTIMECONTROL_ON, "", "");
			} else {
				PlayTimeControlAlertDialogController ptcadc = PlayTimeControlAlertDialogController.getInstance(getActivity());
				ptcadc.setPasswordCheckCallback(new PasswordCheckCallback() {
					
					@Override
					public void onWrongPassword() {
					}
					
					@Override
					public void onPasswordCorrect() {
						cancelPTCAlarm();
						refreshUI();
					}
					
					@Override
					public void onCheckFailed() {
						MyAlertDialog.newInstnace("").show(getFragmentManager(), "systemBusy");
					}
				});
				ptcadc.showPasswordDialog(true);
			}
		}
		
	}
	
	private class PlayTimeSelectListener implements OnClickListener {

		@Override
		public void onClick(View v) {
//			SettingPlayTimeSelectFragment newFragment = new SettingPlayTimeSelectFragment();
//			newFragment.setPlayTimeControlFragment(SettingPlayTimeControlFragment.this);
//			FragmentTransaction transaction = getFragmentManager().beginTransaction();
//			transaction.replace(R.id.fragment_container, newFragment);
//			transaction.addToBackStack(null);
//			transaction.commit();
		}
		
	}
	
	public long startPTCAlarm(int minutesAfter) {
//		Log.d("SettingPlayTimeControlFragment", "startPTCAlarm");
//		AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
//		Intent intent = new Intent(getActivity(), PlayTimeControlService.class);
//		PendingIntent pi = PendingIntent.getService(getActivity(), 0, intent, 0);
//		Calendar cal = Calendar.getInstance();
//		cal.add(Calendar.MINUTE, minutesAfter);
//		//cal.add(Calendar.SECOND, 15);
//		alarmManager.set(AlarmManager.RTC, cal.getTimeInMillis(), pi);
//		PreferenceHelper.setPreference(PTC_ALARM_MILLIS, cal.getTimeInMillis());
//		return cal.getTimeInMillis();
		return 0;
	}
	
	public void cancelPTCAlarm() {
//		AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
//		Intent intent = new Intent(getActivity(), PlayTimeControlService.class);
//		PendingIntent pi = PendingIntent.getService(getActivity(), 0, intent, 0);
//		alarmManager.cancel(pi);
//		PreferenceHelper.removePreference(PTC_ALARM_MILLIS);
	}
	
	private class PTCListener extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			refreshUI();
		}
	}
}


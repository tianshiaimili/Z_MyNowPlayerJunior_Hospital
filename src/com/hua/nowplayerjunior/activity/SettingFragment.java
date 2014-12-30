package com.hua.nowplayerjunior.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.hua.activity.R;
import com.hua.nowplayerjunior.utils.MyAlertDialog;
import com.hua.nowplayerjunior.utils.UserSetting;
import com.hua.nowplayerjunior.utils.UserSetting.UserSettingType;
import com.pccw.nmal.appdata.AppInfo;
import com.pccw.nmal.checkout.CheckoutFlowController;
import com.pccw.nmal.checkout.SpecialFeatureCheckout;
import com.pccw.nmal.model.StreamInfo;
import com.pccw.nmal.net.UpdateSessionResponseHandler;
import com.pccw.nmal.net.WebTvApiRequest;
import com.pccw.nmal.net.WebTvApiRequest.Callback;
import com.pccw.nmal.net.WebTvApiRequest.WebTvApiRequestType;
import com.pccw.nmal.util.LanguageHelper;
import com.pccw.nmal.util.PreferenceHelper;
import com.pccw.nowid.NowIDLoginStatus;

public class SettingFragment extends UIEventBaseFragment implements Callback {
	
	private Button btnLanguage, btnNowId, btnStream3G, btnPushAlert, btnServiceNotice, btnPlayTimeControl;
	private TextView txtLanguage, txtNowId, txtPlayTimeControl;
	private CheckBox chkStream3G, chkPushAlert;
	private ImageView diLanguage, diNowId, diServiceNotice, diPlayTimeControl;
	private UserSetting userSetting;
	private boolean triggerPTCafterUpdateSession = false;
	private Button moreGameBT, tutorialBtn;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.setting, container, false);
		btnLanguage = (Button) view.findViewById(R.id.btnLanguage);
		btnNowId = (Button) view.findViewById(R.id.btnNowId);
		btnStream3G = (Button) view.findViewById(R.id.btnStream3G);
		btnPushAlert = (Button) view.findViewById(R.id.btnPushAlert);
		btnServiceNotice = (Button) view.findViewById(R.id.btnServiceNotice);
		btnPlayTimeControl = (Button) view.findViewById(R.id.btnPlayTimeControl);
		txtLanguage = (TextView) view.findViewById(R.id.txtLanguage);
		txtNowId = (TextView) view.findViewById(R.id.txtNowId);
		txtPlayTimeControl = (TextView) view.findViewById(R.id.txtPlayTimeControl);
		diLanguage = (ImageView) view.findViewById(R.id.diLanguage);
		diNowId = (ImageView) view.findViewById(R.id.diNowId);
		diServiceNotice = (ImageView) view.findViewById(R.id.diServiceNotice);
		diPlayTimeControl = (ImageView) view.findViewById(R.id.diPlayTimeControl);
		chkStream3G = (CheckBox) view.findViewById(R.id.chkStream3G);
		chkPushAlert = (CheckBox) view.findViewById(R.id.chkPushAlert);
		

		ButtonOnTouchListener buttonOnTouchListener = new ButtonOnTouchListener();
		for (Button b : new Button[]{btnLanguage, btnNowId, btnStream3G, btnPushAlert, btnServiceNotice, btnPlayTimeControl}) {
			b.setOnTouchListener(buttonOnTouchListener);
		}
		btnNowId.setOnClickListener(new NowIdButtonListener());
		btnLanguage.setOnClickListener(new LanguageButtonListener());
		btnServiceNotice.setOnClickListener(new ServiceNoticeButtonListener());
		btnPlayTimeControl.setOnClickListener(new PlayTimeControlButtonListener());
		btnStream3G.setOnClickListener(new Allow3GButtonListener());
		chkStream3G.setOnCheckedChangeListener(new Allow3GCheckboxListener());
		btnPushAlert.setOnClickListener(new PushAlertButtonListener());
		chkPushAlert.setOnCheckedChangeListener(new PushAlertCheckboxListener());
		
		moreGameBT = (Button) view.findViewById(R.id.more_game_bt);
		moreGameBT.setOnClickListener(new MoreGameButtonListener());
		
		tutorialBtn = (Button) view.findViewById(R.id.setting_tutorial);
		tutorialBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), TutorialActivity.class));
			}
		});
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		chkStream3G.setChecked(PreferenceHelper.getPreferenceBoolean(PreferenceHelper.ALLOW_STREAM_IN_3G));
		userSetting = new UserSetting(getActivity());
		((MainActivity)getActivity()).enableBackButton(false);
		((MainActivity)getActivity()).showTitleLogo();
		refreshLabel();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		WebTvApiRequest request = new WebTvApiRequest();
		showProgressDialog();
		request.setCallback(this);
		request.updateSession(false);
	}

	private class ButtonOnTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			TextView txtTarget = null;
			ImageView diTarget = null;
			switch (v.getId()) {
			case R.id.btnLanguage:
				txtTarget = txtLanguage;
				diTarget = diLanguage;
				break;
			case R.id.btnNowId:
				txtTarget = txtNowId;
				diTarget = diNowId;
				break;
			case R.id.btnStream3G:
			case R.id.btnPushAlert:
				break;
			case R.id.btnServiceNotice:
				diTarget = diServiceNotice;
				break;
			case R.id.btnPlayTimeControl:
				txtTarget = txtPlayTimeControl;
				diTarget = diPlayTimeControl;
				break;
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				((Button)v).setTextColor(getResources().getColor(R.color.SettingTextColor));
				if (txtTarget != null) {
					txtTarget.setTextColor(getResources().getColor(R.color.SettingTextColor));
				}
				if (diTarget != null) {
					diTarget.setImageResource(R.drawable.disclosureindicator);
				}
			} else if (event.getAction() == MotionEvent.ACTION_DOWN) {
				((Button)v).setTextColor(getResources().getColor(R.color.SettingTextColorActive));
				if (txtTarget != null) {
					txtTarget.setTextColor(getResources().getColor(R.color.SettingTextColorActive));
				}
				if (diTarget != null) {
					diTarget.setImageResource(R.drawable.disclosureindicator_white);
				}
			}
			return false;
		}
		
	}
	
	private void refreshLabel() {
		try {
			
			btnLanguage.setText(getString(R.string.setting_language_label));
			btnNowId.setText(getString(R.string.setting_nowid_label));
			btnStream3G.setText(getString(R.string.setting_3gdata_lable));
			btnServiceNotice.setText(getString(R.string.tabbar_item_userguide_title));
			btnPlayTimeControl.setText(getString(R.string.ptc_title));
			if (LanguageHelper.getCurrentLanguage().equals("en")) {
				txtLanguage.setText(getString(R.string.setting_language_en_label));
			} else {
				txtLanguage.setText(getString(R.string.setting_language_zh_label));
			}
			btnPushAlert.setText(getString(R.string.setting_pushalert_lable));
			chkStream3G.setChecked(PreferenceHelper.getPreferenceBoolean(PreferenceHelper.ALLOW_STREAM_IN_3G));
			chkPushAlert.setChecked(userSetting.isPushAlertOn());
			if(NowIDLoginStatus.getInstance().isLoggedIn()){
				if(NowIDLoginStatus.getInstance().isEmailVerified()){
					if (NowIDLoginStatus.getInstance().isBinded()){
						txtNowId.setText(getString(R.string.setting_nowid_binded_label));
					} else {
						txtNowId.setText(getString(R.string.setting_nowid_notbinded_label));
					}
				} else {
					txtNowId.setText(getString(R.string.setting_nowid_notverified_label));
				}
			} else {
				txtNowId.setText(getString(R.string.setting_nowid_loggedout_label));
				PreferenceHelper.removePreference(SettingPlayTimeControlFragment.PTC_ALARM_MILLIS);
			}
			txtPlayTimeControl.setText(PreferenceHelper.getPreferenceLong(
					SettingPlayTimeControlFragment.PTC_ALARM_MILLIS) > 0 ? 
					getString(R.string.ptc_on) : getString(R.string.ptc_off));			
			moreGameBT.setText(getString(R.string.setting_more_game));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private class NowIdButtonListener implements OnClickListener {
		
		@Override
		public void onClick(View v) {
			triggerPTCafterUpdateSession = false;
			if (NowIDLoginStatus.getInstance().isLoggedIn()) {
				SettingNowIdFragment newFragment = new SettingNowIdFragment();
				FragmentTransaction transaction = getFragmentManager().beginTransaction();
				transaction.replace(R.id.fragment_container, newFragment);
				transaction.addToBackStack(null);
				transaction.commit();
			} else {
				NowIDLoginFragment newFragment = new NowIDLoginFragment();
				FragmentTransaction transaction = getFragmentManager().beginTransaction();
				transaction.replace(R.id.fragment_container, newFragment);
				transaction.addToBackStack(null);
				transaction.commit();
			}
		}
	}
	
	private class LanguageButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			SettingLanguageFragment newFragment = new SettingLanguageFragment();
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.replace(R.id.fragment_container, newFragment);
			transaction.addToBackStack(null);
			transaction.commit();
		}
		
	}
	
	private class ServiceNoticeButtonListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			FAQFragment newFragment = new FAQFragment();
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.replace(R.id.fragment_container, newFragment);
			transaction.addToBackStack(null);
			transaction.commit();
		}
		
	}
	
	private class MoreGameButtonListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			MoreGameFragment newFragment = new MoreGameFragment();
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.replace(R.id.fragment_container, newFragment);
			transaction.addToBackStack(null);
			transaction.commit();
		}
		
	}

	private class PlayTimeControlButtonListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			triggerPTCafterUpdateSession = true;
			openPlayTimeControlFragment();
		}
		
	}
	
	private class Allow3GButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			chkStream3G.performClick();
		}
	}
	
	private class Allow3GCheckboxListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			PreferenceHelper.setPreference(PreferenceHelper.ALLOW_STREAM_IN_3G, isChecked);
		}
	}

	private class PushAlertButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			chkPushAlert.performClick();
		}
	}
	
	private class PushAlertCheckboxListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			userSetting.setPushAlertOn(isChecked);
			userSetting.save(UserSettingType.APP_LAUNCH);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		/*
		WebTvApiRequest request = new WebTvApiRequest();
		request.setCallback(new WebTvApiRequest.Callback() {
			
			@Override
			public void onResponseReceived(WebTvApiRequestType requestType,
					String response) {
				new UpdateSessionResponseHandler().parseResponse(response);
				closeProgressDialog();
				refreshLabel();
			}
			
			@Override
			public void onFailure(WebTvApiRequestType requestType, String errorCode) {
				closeProgressDialog();
				refreshLabel();
				
			}
		});
		showProgressDialog();
		request.setCallback(this);
		request.updateSession(false);
		*/
		if (resultCode != Activity.RESULT_OK) {
			triggerPTCafterUpdateSession = false;
		}
	}

	@Override
	protected void afterQualityAndBookmarkSelected(
			List<List<StreamInfo>> playlist, int quality, int bookmark) {
	}

	@Override
	public void onResponseReceived(WebTvApiRequestType requestType,
			String response) {
		UpdateSessionResponseHandler handler = new UpdateSessionResponseHandler();
		handler.parseResponse(response);
		closeProgressDialog();
		refreshLabel();
		
		if ((requestType == WebTvApiRequestType.CheckoutSpecialFeature) && triggerPTCafterUpdateSession) {
			if (NowIDLoginStatus.getInstance().isLoggedIn()) {
				openPlayTimeControlFragment();
			}
		}

	}

	@Override
	public void onSuccess() {
		triggerPTCafterUpdateSession = false;
		closeProgressDialog();
		SettingPlayTimeControlFragment newFragment = new SettingPlayTimeControlFragment();
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, newFragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

	@Override
	public void onFailure(WebTvApiRequestType requestType, String errorCode) {
		closeProgressDialog();
	}
	
	@Override
	public void onNeedSubscription() {
		closeProgressDialog();
		triggerPTCafterUpdateSession = false;
		MyAlertDialog alert = MyAlertDialog.newInstance(-1, 
				LanguageHelper.getLocalizedString("alert.subscribe.specialFeature.title"), 
				LanguageHelper.getLocalizedString("alert.subscribe.specialFeature.message"), 
				LanguageHelper.getLocalizedString("alert.button.call"),
				LanguageHelper.getLocalizedString("alert.button.cancel"));
		alert.setCallback(this);
		alert.show(getFragmentManager(), "aSubscribe");
	}
	
	private void openPlayTimeControlFragment() {
		if (NowIDLoginStatus.getInstance().isLoggedIn()) {
			CheckoutFlowController cfc = new CheckoutFlowController(getActivity());
			showProgressDialog();
			SpecialFeatureCheckout sfc = new SpecialFeatureCheckout("L00862", 
					AppInfo.getAppId());
			cfc.setCheckoutStepHandler(sfc);
			cfc.setCheckoutEventHandler(SettingFragment.this);
			cfc.startCheckout();
		} else {
			NowIDLoginFragment newFragment = new NowIDLoginFragment();
			newFragment.setCallerFragment(SettingFragment.this);
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			transaction.replace(R.id.fragment_container, newFragment);
			transaction.addToBackStack(null);
			transaction.commit();
		}

	}
	
}

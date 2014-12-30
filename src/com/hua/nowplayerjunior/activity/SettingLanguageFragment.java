package com.hua.nowplayerjunior.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.hua.activity.R;
import com.hua.gz.app.AppLocaleAide;
import com.hua.gz.app.BaseFragment;
import com.hua.nowplayerjunior.constants.Constants;
import com.hua.nowplayerjunior.utils.UserSetting;
import com.hua.nowplayerjunior.utils.UserSetting.UserSettingType;
import com.pccw.android.ad.common.UserSettings;
import com.pccw.nmal.appdata.AppInfo;
import com.pccw.nmal.appdata.JsonZip;
import com.pccw.nmal.appdata.JsonZip.ZipType;
import com.pccw.nmal.model.VOD;
import com.pccw.nmal.util.LanguageHelper;

public class SettingLanguageFragment extends BaseFragment {

	private Button btnEnglish, btnChinese;
	private ImageView checkmarkEnglish, checkmarkChinese;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.setting_language, null);
		btnEnglish = (Button) view.findViewById(R.id.btnEnglish);
		btnChinese = (Button) view.findViewById(R.id.btnChinese);
		checkmarkEnglish = (ImageView) view.findViewById(R.id.checkmarkEnglish);
		checkmarkChinese = (ImageView) view.findViewById(R.id.checkmarkChinese);
		
		ButtonOnTouchListener buttonOnTouchListener = new ButtonOnTouchListener();
		ButtonOnClickListener buttonOnClickListener = new ButtonOnClickListener();
		for (Button b : new Button[]{btnEnglish, btnChinese}) {
			b.setOnTouchListener(buttonOnTouchListener);
			b.setOnClickListener(buttonOnClickListener);
		}
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		refreshButtonState(LanguageHelper.getCurrentLanguage());
	}

	@Override
	public void onResume() {
		super.onResume();
		((MainActivity)getActivity()).enableBackButton(true, getResources().getString(R.string.tabbar_item_setting_title));
	}

	private class ButtonOnClickListener implements OnClickListener {
		String currentLanguage = "en";
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnEnglish:
				currentLanguage = "en";
				setAppLocale(getActivity(), AppLocaleAide.ENGLISH_US);
				
				// Sync language setting to AdEngine Lib 
				UserSettings.setLanguage(UserSettings.LANGUAGE_ENGLISH);
				break;
			case R.id.btnChinese:
				currentLanguage = "zh";
				setAppLocale(getActivity(), AppLocaleAide.TRADITIONAL_CHINESE_HK);
				
				// Sync language setting to AdEngine Lib 
				UserSettings.setLanguage(UserSettings.LANGUAGE_CHINESE);
				break;
			}
			LanguageHelper.setCurrentLanguage(currentLanguage);
			UserSetting us = new UserSetting(getActivity());
			us.setLanguage(currentLanguage);
			us.save(UserSettingType.APP_LAUNCH);
			refreshButtonState(LanguageHelper.getCurrentLanguage());
			JsonZip jsonZip = new JsonZip(getActivity(), AppInfo.getJsonVersionPath(), Constants.JSON_ZIP_VERSION_PREFIX, LanguageHelper.getCurrentLanguage());
			jsonZip.clearJSONZip(ZipType.PKG);
			jsonZip.clearJSONZip(ZipType.EPG);
			VOD.getInstance().clearVodChannelList();
			((MainActivity)getActivity()).refreshTab();
			((MainActivity)getActivity()).enableBackButton(true, getResources().getString(R.string.tabbar_item_setting_title));
		}
		
	}
	
	private class ButtonOnTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (v.getId()) {
			case R.id.btnEnglish:
				break;
				
			case R.id.btnChinese:
				break;
			}
			return false;
		}
	}
	
	private void refreshButtonState(String language) {
		if ("en".equals(language)) {
			LanguageHelper.setCurrentLanguage("en");
			btnEnglish.setSelected(true);
			btnChinese.setSelected(false);
			checkmarkEnglish.setVisibility(View.VISIBLE);
			checkmarkChinese.setVisibility(View.INVISIBLE);
		} else {
			LanguageHelper.setCurrentLanguage("zh");
			btnEnglish.setSelected(false);
			btnChinese.setSelected(true);
			checkmarkEnglish.setVisibility(View.INVISIBLE);
			checkmarkChinese.setVisibility(View.VISIBLE);
		}
	}
}

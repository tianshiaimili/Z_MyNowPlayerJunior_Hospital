package com.hua.nowplayerjunior.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.hua.activity.R;
import com.pccw.nmal.Nmal;
import com.pccw.nmal.appdata.AppInfo;
import com.pccw.nmal.nowid.sso.ExternalStorageSSOImpl;
import com.pccw.nmal.nowid.sso.NowIdSSO;
import com.pccw.nmal.nowid.sso.NowTVData;
import com.pccw.nmal.util.PreferenceHelper;
import com.pccw.nowid.NowIDLoginStatus;


public class NowIDCallFragment extends Fragment {
	
	private Button callButton;
	private Button activateButton;
	private UIEventBaseFragment callerFragment;
	
	public void setCallerFragment(UIEventBaseFragment fragment) {
		this.callerFragment = fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.nowid_call_or_activate, null);
		callButton = (Button) view.findViewById(R.id.callButton);
		callButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (NowIDLoginStatus.getInstance().isLoggedIn()) {
					Intent intent = new Intent(Intent.ACTION_DIAL);
					intent.setData(Uri.parse("tel:" + getString(R.string.nowid_button_call_number)));
					startActivity(intent); 
				}
			}
		});
		
		activateButton = (Button) view.findViewById(R.id.activateButton);
		activateButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String nowidString ="";
				if(NowIDLoginStatus.getInstance().getNowID()!=null){
					nowidString = "&id=" + Base64.encodeToString(NowIDLoginStatus.getInstance().getNowID().getBytes(), Base64.DEFAULT);
				}
				Uri uriUrl = Uri.parse(getResources().getString(R.string.nowid_landing_register_giftcode_url) + nowidString);
				Intent intent = new Intent(Intent.ACTION_VIEW, uriUrl);
				startActivity(intent);
			}
		});
		
		if (!AppInfo.canActivateGiftCode()){
			view.findViewById(R.id.nowid_login_activation_pane).setVisibility(View.INVISIBLE);
		}
		
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
//		((NowplayerJrActivity) getActivity()).enableBackButton(true);
	}

	@Override
	public void onActivityResult(int arg0, int arg1, Intent arg2) {
		if (NowIDLoginStatus.getInstance().isLoggedIn()) {
			NowIdSSO sso = new ExternalStorageSSOImpl(getActivity());
			NowTVData nowTVData = new NowTVData();
			nowTVData.setSecureCookie(NowIDLoginStatus.getInstance().getSecureCookie());
			nowTVData.setUserAgent(Nmal.getWebViewUserAgent());
			if (!sso.createData(nowTVData)) {
				PreferenceHelper.setPreference(PreferenceHelper.SECURE_COOKIE, NowIDLoginStatus.getInstance().getSecureCookie());
			}
			
			if (!NowIDLoginStatus.getInstance().isEmailVerified()) {
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.replace(R.id.fragment_container, new SettingNowIdFragment());
				ft.commit();
			/*
			} else if (!NowIDLoginStatus.getInstance().isBinded()) {
				Intent intent = new Intent(getActivity(), NowIDBindingActivity.class);
				if (callerFragment != null) {
					callerFragment.showProgressDialog();
					callerFragment.startActivityForResult(intent, 0);
				}
				*/
			} else {
				if (callerFragment != null) {
					callerFragment.onActivityResult(arg0, arg1, arg2);
				}
			}
		}
		getFragmentManager().popBackStack();
	}
}

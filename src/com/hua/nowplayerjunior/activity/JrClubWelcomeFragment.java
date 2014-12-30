package com.hua.nowplayerjunior.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.hua.activity.R;
import com.hua.gz.app.FragmentUtils;
import com.hua.nowplayerjunior.constants.Constants;
import com.pccw.nmal.appdata.AppInfo;
import com.pccw.nmal.checkout.CheckoutFlowController;
import com.pccw.nmal.checkout.SpecialFeatureCheckout;
import com.pccw.nmal.model.StreamInfo;
import com.pccw.nowid.NowIDLoginStatus;

public class JrClubWelcomeFragment extends UIEventBaseFragment {

	private static final String TAG = JrClubWelcomeFragment.class.getSimpleName();
	private boolean needAutoCheck = true;
	
	@Override
	protected void afterQualityAndBookmarkSelected(
			List<List<StreamInfo>> playlist, int quality, int bookmark) {

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.jr_club_welcome, container, false);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if(needAutoCheck){
			if(Constants.ENABLE_JR_CLUB_AUTO_CHECKOUT) {
				beginCheckoutSpecialFeatures();
			}
		}
		getActivity().findViewById(R.id.jrclub_logon_button).setOnClickListener(new OnClickListener() {
			

			@Override
			public void onClick(View v) {
				if(Constants.ENABLE_JR_CLUB_CHECKOUT){
					beginCheckoutSpecialFeatures();
				}else{
					doSuccessWork();
				}
			}
		});
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d(TAG, "onAttach()");
		needAutoCheck = true;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d(TAG, "onDestroyView()");
		needAutoCheck = false;
	}
	@Override
	public void onDetach() {
		super.onDetach();
		Log.d(TAG, "onDetach()");
		needAutoCheck = true;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume()");
	}
	
	private void doSuccessWork() {
		
		String tag = JrClubLandingFragment.class.getSimpleName();
		FragmentUtils.replace(getActivity(), new JrClubLandingFragment(),
				R.id.fragment_container, tag, tag);
	}
	@Override
	public void onSuccess() {
		closeProgressDialog();
		Log.d(TAG, "onSuccess()");
		if(needAutoCheck){
			needAutoCheck = false;
		}
		doSuccessWork();
	}
	
	

	@Override
	public void onNotLoggedIn() {
		closeProgressDialog();
		Log.d(TAG, "onNotLoggedIn()");
		if(needAutoCheck){
			needAutoCheck = false;
		}else{
			/*
			Intent intent = new Intent(getActivity(), NowIDLoginActivity.class);
			intent.putExtra("lang", LanguageHelper.getCurrentLanguage());
			intent.putExtra("userAgent", Nmal.getWebViewUserAgent());
			startActivityForResult(intent, 0);
			*/
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			NowIDLoginFragment loginFragment = new NowIDLoginFragment();
			loginFragment.setCallerFragment(this);
			ft.replace(R.id.fragment_container, loginFragment);
			ft.addToBackStack(null);
			ft.commit();
		}
		
	}

	@Override
	public void onNotBinded() {
		
		closeProgressDialog();
		Log.d(TAG, "onNotBinded()");
		if(needAutoCheck){
			needAutoCheck = false;
		}else{
			// Needed to check if is logged in first
			if (NowIDLoginStatus.getInstance().isLoggedIn()) {
				/*
				Intent intent = new Intent(getActivity(), NowIDBindingActivity.class);
				intent.putExtra("lang", LanguageHelper.getCurrentLanguage());
				intent.putExtra("userAgent", Nmal.getWebViewUserAgent());
				startActivityForResult(intent, 0);
				*/
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				NowIDConnectFragment connectFragment = new NowIDConnectFragment();
				connectFragment.setCallerFragment(this);
				ft.replace(R.id.fragment_container, connectFragment);
				ft.addToBackStack(null);
				ft.commit();
			} else {
				onNotLoggedIn();
			}
		}
		
	}
	
	@Override
	public void onNeedSubscription() {
		closeProgressDialog();
		/*
		Log.d(TAG, "onNeedSubscription()");
		MyAlertDialog alert = MyAlertDialog.newInstance(-1, 
				LanguageHelper.getLocalizedString("alert.subscribe.specialFeature.title"), 
				LanguageHelper.getLocalizedString("alert.subscribe.specialFeature.message"), 
				LanguageHelper.getLocalizedString("alert.button.call"),
				LanguageHelper.getLocalizedString("alert.button.cancel"));
		alert.setCallback(this);
		alert.show(getFragmentManager(), "aSubscribe");
		*/
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		NowIDCallFragment callFragment = new NowIDCallFragment();
		callFragment.setCallerFragment(this);
		ft.replace(R.id.fragment_container, callFragment);
		ft.addToBackStack(null);
		ft.commit();
		
		needAutoCheck = false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		closeProgressDialog();
		Log.d(TAG, "onActivityResult() result code:"+resultCode);
		needAutoCheck = false;
		if (resultCode == Activity.RESULT_OK) {
			beginCheckoutSpecialFeatures();
		}else{
			//getActivity().onBackPressed();
		}
	}
	private void beginCheckoutSpecialFeatures() {
		CheckoutFlowController cfc = new CheckoutFlowController(getActivity());
		showProgressDialog();
		SpecialFeatureCheckout sfc = new SpecialFeatureCheckout("L00861", 
				AppInfo.getAppId());
		cfc.setCheckoutStepHandler(sfc);
		cfc.setCheckoutEventHandler(this);
		cfc.startCheckout();
	}

}

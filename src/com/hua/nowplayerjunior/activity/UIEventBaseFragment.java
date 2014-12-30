package com.hua.nowplayerjunior.activity;


import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;

import com.hua.activity.R;
import com.hua.nowplayerjunior.constants.Constants;
import com.hua.nowplayerjunior.service.ProxyServerControl;
import com.hua.nowplayerjunior.utils.ErrorCodeString;
import com.hua.nowplayerjunior.utils.MyAlertDialog;
import com.hua.nowplayerjunior.utils.MyAlertDialog.Callback;
import com.lifevibes.lvmediaplayer.LVMediaPlayer;
import com.pccw.nmal.checkout.CheckoutFlowController.CheckoutUIEvent;
import com.pccw.nmal.model.StreamInfo;
import com.pccw.nmal.service.PixelLogService;
import com.pccw.nmal.util.LanguageHelper;
import com.pccw.nowid.NowIDLoginStatus;

public abstract class UIEventBaseFragment extends Fragment implements CheckoutUIEvent, Callback {

	protected ProgressDialog progressDialog;
	protected String lastServiceId;
	protected int lastBookmark;
	// Remember to set these two fields for VOD checkout
	protected String lastProgramName;
	protected String lastProductId;

	@Override
	public void onNotLoggedIn() {
		closeProgressDialog();
		//if (!(this instanceof LiveChannelFragment)) {
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			NowIDLoginFragment loginFragment = new NowIDLoginFragment();
			loginFragment.setCallerFragment(this);
			ft.replace(R.id.fragment_container, loginFragment);
			ft.addToBackStack(null);
			ft.commit();
		//}
	}
	
	@Override
	public void onNotBinded() {
		closeProgressDialog();

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
	



	@Override
	public void onConnectivityWarning() {
		closeProgressDialog();
		MyAlertDialog dialog = MyAlertDialog.newInstance(-1, 
				LanguageHelper.getLocalizedString("setting.enable.3g.checkout.prompt.title"), 
				LanguageHelper.getLocalizedString("setting.enable.3g.checkout.prompt.message"), 
				LanguageHelper.getLocalizedString("setting.enable.3g.checkout.prompt.goto.setting"), 
				LanguageHelper.getLocalizedString("setting.enable.3g.checkout.prompt.confirm"));
		dialog.setCallback(this);
		dialog.show(getFragmentManager(), ErrorCodeString.Prompt3GEnableMessage);
	}

	@Override
	public void onParentalLock(String responseCode) {
		closeProgressDialog();
	}
	
	@Override
	public void onDeviceRegistration(String responseCode) {
		closeProgressDialog();
	}

	@Override
	public void onCheckoutFailed(String errorCode) {
		MyAlertDialog alert = null;

		closeProgressDialog();
		if ("GEO_CHECK_FAIL".equalsIgnoreCase(errorCode)) {
			alert = MyAlertDialog.newInstance(-1, 
					LanguageHelper.getLocalizedString("error.alert.general.error.title"), 
					LanguageHelper.getLocalizedString("error.geo.check.failed"), 
					LanguageHelper.getLocalizedString("info.load.error.button"), 
					LanguageHelper.getLocalizedString("alert.button.cancel"));
		} else if(ErrorCodeString.ParentalLockInvalidPIN.equalsIgnoreCase(errorCode) || ErrorCodeString.ParentalLockFirstTimeSetup.equalsIgnoreCase(errorCode)
					|| ErrorCodeString.ParentalLockNoPIN.equalsIgnoreCase(errorCode)){
				errorCode = "10016";

			alert = MyAlertDialog.newInstnace(errorCode);
		} else if ("BINDING_NOT_FOUND_WITHOUT_GIFTCODE".equalsIgnoreCase(errorCode)){
			try{
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				NowIDConnectFragment connectFragment = new NowIDConnectFragment();
				connectFragment.setCallerFragment(this);
				ft.replace(R.id.fragment_container, connectFragment);
				ft.addToBackStack(null);
				ft.commit();
				return;
			} catch (Exception e){
				Log.d(getClass().getSimpleName(),"fragmentManager is null, no need to do anything");
				return;
			}
		} else if ("NEED_SUB_WITHOUT_GIFTCODE".equalsIgnoreCase(errorCode)){
			try{
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				NowIDCallFragment callFragment = new NowIDCallFragment();
				callFragment.setCallerFragment(this);
				ft.replace(R.id.fragment_container, callFragment);
				ft.addToBackStack(null);
				ft.commit();
				return;
			} catch (Exception e){
				Log.d(getClass().getSimpleName(),"fragmentManager is null, no need to do anything");
				return;
			}
		} else if ("BINDING_NOT_FOUND_WITH_GIFTCODE_EXPIRED".equalsIgnoreCase(errorCode)){
			
			try{
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				NowIDConnectFragment connectFragment = new NowIDConnectFragment();
				connectFragment.setGiftCodeExpired(true);
				connectFragment.setCallerFragment(this);
				ft.replace(R.id.fragment_container, connectFragment);
				ft.addToBackStack(null);
				ft.commit();
				return;		
			} catch (Exception e){
				Log.d(getClass().getSimpleName(),"fragmentManager is null, no need to do anything");
				return;
			}
		} else if ("NEED_SUB_WITH_GIFTCODE_EXPIRED".equalsIgnoreCase(errorCode)){
			
			try{
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				NowIDGiftCodeExpiredFragment giftcodeExpiredFragment = new NowIDGiftCodeExpiredFragment();
				giftcodeExpiredFragment.setCallerFragment(this);
				ft.replace(R.id.fragment_container, giftcodeExpiredFragment);
				ft.addToBackStack(null);
				ft.commit();
				return;	
			} catch (Exception e){
				Log.d(getClass().getSimpleName(),"fragmentManager is null, no need to do anything");
				return;
			}
		}  else {
			alert = MyAlertDialog.newInstnace(errorCode);
		}
		
		alert.setCallback(this);
		alert.show(getFragmentManager(), errorCode);
	}

	@Override
	public void onReceivedPlaylist(List<List<StreamInfo>> playlist, String serviceId, int bookmark) {
		closeProgressDialog();
		lastServiceId = serviceId;
		lastBookmark = bookmark;
		ProxyServerControl.setCcCustId(null);
		ProxyServerControl.setCcDomain(null);
		ProxyServerControl.setCcPoolType(null);
		afterQualitySelected(playlist, 1);
		//promptQualitySelection(playlist	}
	}
	
	@Override
	public void onReceivedPlaylistWithConcurrent(java.util.List<java.util.List<StreamInfo>> playlist, String serviceId, int bookmark, String ccCustId, String ccDomain, String ccPoolType) {
		closeProgressDialog();
		lastServiceId = serviceId;
		lastBookmark = bookmark;
		ProxyServerControl.setCcCustId(ccCustId);
		ProxyServerControl.setCcDomain(ccDomain);
		ProxyServerControl.setCcPoolType(ccPoolType);
		afterQualitySelected(playlist, 1);
	};
	
	@Override
	public void onSuccess() {
		// For verifyNowIDPasswword and checkoutSpecialFeature
	}

	@Override
	public void onNeedSubscription() {
		closeProgressDialog();
		/*
		MyAlertDialog alert = MyAlertDialog.newInstance(-1, 
				LanguageHelper.getLocalizedString("alert.subscribe.now.junior.title"), 
				LanguageHelper.getLocalizedString("alert.subscribe.now.junior.message"), 
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
	}

	// Callback for connectivity warning
	@Override
	public void onClickOKButton(String tag) {
		if ("aSubscribe".equals(tag)) {
			String url = getResources().getString(R.string.subscribe_hotline_tel);
			Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
			startActivity(intent);
		} else if (ErrorCodeString.Prompt3GEnableMessage.equals(tag)) {
//			((NowplayerJrActivity)getActivity()).tapSettingTab();
		} else if ("GEO_CHECK_FAIL".equals(tag)) {
			getActivity().finish();
			System.exit(0);
		}
	}

	// Callback for connectivity warning
	@Override
	public void onClickCancelButton(String tag) {
		closeProgressDialog();
	}

	protected void showProgressDialog() {
		Log.d("showProgressDialog", "showProgressDialog");
		if (progressDialog == null) {
			progressDialog = ProgressDialog.show(getActivity(), 
					LanguageHelper.getLocalizedString("progress.dialog.title"), 
					LanguageHelper.getLocalizedString("progress.dialog.message"));
			progressDialog.setCancelable(false);
			progressDialog.setOnKeyListener(new OnKeyListener() {
				
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

					if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK){
						progressDialog.dismiss();
					}
					
					return false;
				}
			});
		}
	}

	protected void closeProgressDialog() {
		if (progressDialog != null) {
	        progressDialog.dismiss();
	        progressDialog = null;
	    }
	}

	protected void promptQualitySelection(final List<List<StreamInfo>> playlists) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(LanguageHelper.getLocalizedString("setting.quality.selection.title"));
		final String[] options = { LanguageHelper.getLocalizedString("setting.quality.selection.high"), 
				LanguageHelper.getLocalizedString("setting.quality.selection.low")};
		builder.setItems(options, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int item) {
				Log.d("QualitySelection", "clicked item at index: " + item );
				int videoQualitySelection = playlists.get(item).get(0).getQuality();
				Log.d("QualitySelection", "videoQualitySelection: " + videoQualitySelection );
				afterQualitySelected(playlists, videoQualitySelection);
			}
		});
		builder.setNegativeButton(LanguageHelper.getLocalizedString("alert.button.cancel"), new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				afterQualitySelected(playlists, -1);
			}
		});
		builder.setCancelable(false);
		builder.show();
	}
	
	protected void afterQualitySelected(final List<List<StreamInfo>> playlists, final int quality) {
		if (quality < 0) {
			return;
		}
		if (lastBookmark > 0) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			String titleName;
			if (LanguageHelper.getCurrentLanguage().startsWith("zh") && lastProgramName.length() > 14) {
				titleName = lastProgramName.substring(0, 11) + "...";
			}
			else if (LanguageHelper.getCurrentLanguage().startsWith("en") && lastProgramName.length() > 22) {
				titleName = lastProgramName.substring(0, 19) + "...";
			}	
			else {
				titleName = lastProgramName;		
			}
			builder.setTitle(titleName);
			final String[] options = { LanguageHelper.getLocalizedString("setting.bookmark.selection.playfromlastviewedscene"), 
					LanguageHelper.getLocalizedString("setting.bookmark.selection.resume")};
			builder.setItems(options, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int item) {
					if (item == 0) {
						PixelLogService pixelLog = new PixelLogService(Constants.PIXEL_LOG_URL, Constants.PIXEL_LOG_APP_NAME, LVMediaPlayer.getUniqueIdentifier(getActivity()), NowIDLoginStatus.getInstance().getNowID(), NowIDLoginStatus.getInstance().getFsa());
						pixelLog.pixelLogAction(pixelLog.PIXELLOG_BOOKMARK_LASTVIEWEDSCENE, "", lastProductId);
					} else {
						lastBookmark = 0;
						PixelLogService pixelLog = new PixelLogService(Constants.PIXEL_LOG_URL, Constants.PIXEL_LOG_APP_NAME, LVMediaPlayer.getUniqueIdentifier(getActivity()), NowIDLoginStatus.getInstance().getNowID(), NowIDLoginStatus.getInstance().getFsa());
						pixelLog.pixelLogAction(pixelLog.PIXELLOG_BOOKMARK_BEGINNING, "", lastProductId);
					}
					afterQualityAndBookmarkSelected(playlists, quality, lastBookmark);
				}

			});
			builder.setNegativeButton(LanguageHelper.getLocalizedString("alert.button.cancel"), 
					new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				}
			}
					);

			builder.setCancelable(false);
			builder.show();

		} else {
			afterQualityAndBookmarkSelected(playlists, quality, 0);
		}
	}
	
	protected abstract void afterQualityAndBookmarkSelected(List<List<StreamInfo>> playlist, int quality, int bookmark);

	@Override
	public void onSystemMaintenance() {
		
	}
	
	@Override
	public void onAccountNotFound_BPL() {
		
	}

	@Override
	public void onAvailableDeviceSlot_BPL() {
		
	}

	@Override
	public void onNotRegisteredDevice_BPL() {

	}

}


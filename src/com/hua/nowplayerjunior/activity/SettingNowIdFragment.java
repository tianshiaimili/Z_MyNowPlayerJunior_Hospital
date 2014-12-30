package com.hua.nowplayerjunior.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hua.activity.R;
import com.hua.nowid.activity.NowIDBindingActivity;
import com.hua.nowid.activity.NowIDChangePasswordActivity;
import com.hua.nowplayerjunior.utils.MyAlertDialog;
import com.pccw.nmal.Nmal;
import com.pccw.nmal.appdata.AppInfo;
import com.pccw.nmal.model.StreamInfo;
import com.pccw.nmal.net.BaseResponseHandler;
import com.pccw.nmal.net.UpdateSessionResponseHandler;
import com.pccw.nmal.net.WebTvApiRequest;
import com.pccw.nmal.net.WebTvApiRequest.Callback;
import com.pccw.nmal.net.WebTvApiRequest.WebTvApiRequestType;
import com.pccw.nmal.util.LanguageHelper;
import com.pccw.nowid.NowIDLoginStatus;

public class SettingNowIdFragment extends UIEventBaseFragment implements Callback {

	Button btnChangePassword, btnBind, btnUnbind, btnLogout, btnGiftCode;
	TextView txtYourNowId, txtNowId, 
		txtBindedHeader, txtBindedMessage, 
		txtNotBindHeader, txtNotBindMessage, 
		txtEmailNotVerifiedHeader, txtEmailNotVerifiedMessage;
	View lrBinded, lrNotBinded, lrEmailNotVerified;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.setting_nowid, null);
		btnChangePassword = (Button) view.findViewById(R.id.btnChangePassword);
		btnBind = (Button) view.findViewById(R.id.btnBind);
		btnUnbind = (Button) view.findViewById(R.id.btnUnbind);
		lrBinded = view.findViewById(R.id.nowIdBinded);
		lrNotBinded = view.findViewById(R.id.nowIdNotBinded);
		lrEmailNotVerified = view.findViewById(R.id.emailNotVerified);
		txtYourNowId = (TextView) view.findViewById(R.id.txtYourNowId);
		txtNowId = (TextView) view.findViewById(R.id.txtNowId);
		txtBindedHeader = (TextView) view.findViewById(R.id.txtBindedHeader);
		txtBindedMessage = (TextView) view.findViewById(R.id.txtBindedMessage);
		txtNotBindHeader = (TextView) view.findViewById(R.id.txtNotBindHeader);
		txtNotBindMessage = (TextView) view.findViewById(R.id.txtNotBindMessage);
		txtEmailNotVerifiedHeader = (TextView) view.findViewById(R.id.txtEmailNotVerifiedHeader);
		txtEmailNotVerifiedMessage = (TextView) view.findViewById(R.id.txtEmailNotVerifiedMessage);
		btnGiftCode = (Button) view.findViewById(R.id.giftCodeExpiryDate);
		
		btnChangePassword.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), NowIDChangePasswordActivity.class);
				intent.putExtra("lang", LanguageHelper.getCurrentLanguage());
				intent.putExtra("userAgent", Nmal.getWebViewUserAgent());
				startActivity(intent);
			}
		});
		btnBind.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), NowIDBindingActivity.class);
				intent.putExtra("lang", LanguageHelper.getCurrentLanguage());
				intent.putExtra("userAgent", Nmal.getWebViewUserAgent());
				startActivity(intent);
			}
		});
		btnUnbind.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				nowIDUnbind();
			}
		});
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshLabel();
//		((NowplayerJrActivity)getActivity()).enableBackButton(true, getResources().getString(R.string.tabbar_item_setting_title));
//		((NowplayerJrActivity)getActivity()).enableLogoutButton(true);
		showProgressDialog();
		WebTvApiRequest request = WebTvApiRequest.getInstance();
		request.setCallback(this);
		request.updateSession(false);
	}
	
	@Override
	public void onPause() {
		super.onPause();
//		((NowplayerJrActivity)getActivity()).enableLogoutButton(false);
	}
	
	@Override
	public void onResponseReceived(WebTvApiRequestType requestType, String response) {
		if (requestType == WebTvApiRequestType.UpdateSession) {
			UpdateSessionResponseHandler handler = new UpdateSessionResponseHandler();
			handler.parseResponse(response);
			refreshLoginStatus();
		} else if (requestType == WebTvApiRequestType.Unbind) {
			WebTvApiRequest request = WebTvApiRequest.getInstance();
			request.updateSession(false);
		}
		closeProgressDialog();
	}

	@Override
	public void onFailure(WebTvApiRequestType requestType, String errorCode) {
		closeProgressDialog();
	}

	public void refreshLoginStatus() {
		if (NowIDLoginStatus.getInstance().isBinded()) {
			lrBinded.setVisibility(View.VISIBLE);
			lrNotBinded.setVisibility(View.GONE);
			lrEmailNotVerified.setVisibility(View.GONE);
		} else if (NowIDLoginStatus.getInstance().isEmailVerified()) {
			lrBinded.setVisibility(View.GONE);
			lrNotBinded.setVisibility(View.VISIBLE);
			lrEmailNotVerified.setVisibility(View.GONE);
		} else {
			lrBinded.setVisibility(View.GONE);
			lrNotBinded.setVisibility(View.GONE);
			lrEmailNotVerified.setVisibility(View.VISIBLE);
		}
		
		if (!AppInfo.isGiftCodePromoEnd()){
			if (NowIDLoginStatus.getInstance().isGiftCodeExpired()){
				btnGiftCode.setText(getString(R.string.nowid_setting_giftcode_expired));
				btnGiftCode.setVisibility(View.VISIBLE);
			} else if (NowIDLoginStatus.getInstance().getGiftCodeExpiryDate()> 0){
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
				Date date = new Date(NowIDLoginStatus.getInstance().getGiftCodeExpiryDate());
				btnGiftCode.setText(getString(R.string.nowid_setting_giftcode_expiry_date) + sdf.format(date));
				btnGiftCode.setVisibility(View.VISIBLE);
			} else {
				btnGiftCode.setVisibility(View.INVISIBLE);
			}
		} else{
			btnGiftCode.setVisibility(View.INVISIBLE);
		}
	}
	
	public void refreshLabel() {
		txtYourNowId.setText(getResources().getString(R.string.setting_nowid_yournowid_label));
		txtNowId.setText(NowIDLoginStatus.getInstance().getNowID());
		txtBindedHeader.setText(getResources().getString(R.string.setting_nowid_binding_header));
		txtBindedMessage.setText(getResources().getString(R.string.setting_nowid_binding_unbind_message));
		txtNotBindHeader.setText(getResources().getString(R.string.setting_nowid_binding_header));
		txtNotBindMessage.setText(getResources().getString(R.string.setting_nowid_binding_bind_message)); 
		txtEmailNotVerifiedHeader.setText(getResources().getString(R.string.setting_nowid_emailnotverified_header));
		txtEmailNotVerifiedMessage.setText(getResources().getString(R.string.setting_nowid_emailnotverified_message));
		btnChangePassword.setText(getResources().getString(R.string.setting_nowid_changepassword_label));
		btnBind.setText(getResources().getString(R.string.setting_nowid_bind_label));
		btnUnbind.setText(getResources().getString(R.string.setting_nowid_unbind_label));
	}

	@Override
	protected void afterQualitySelected(List<List<StreamInfo>> playlists, int quality) {
		// We don't have checkout here
	}

	@Override
	protected void afterQualityAndBookmarkSelected(
			List<List<StreamInfo>> playlist, int quality, int bookmark) {
		// Again, we don't have checkout here
		
	}
	
	private void nowIDUnbind() {
		Builder unbindDialog = new AlertDialog.Builder(getActivity());
		unbindDialog.setTitle(LanguageHelper.getLocalizedString("setting.nowid.binding.unbind.alert.header"));
		unbindDialog.setMessage(LanguageHelper.getLocalizedString("setting.nowid.binding.unbind.alert.message"));
		DialogInterface.OnClickListener confirmClick = new DialogInterface.OnClickListener() {

			ProgressDialog bindProgressDialog;
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				WebTvApiRequest npr = WebTvApiRequest.getInstance();
				npr.setCallback(callback);
				bindProgressDialog = new ProgressDialog(getActivity());
				bindProgressDialog.setMessage(LanguageHelper.getLocalizedString("progress.dialog.unbinding"));
				bindProgressDialog.setIndeterminate(true);
				bindProgressDialog.show();
				npr.updateSession(false);
			}
			
			WebTvApiRequest.Callback callback = new WebTvApiRequest.Callback() {

				@Override
				public void onResponseReceived(WebTvApiRequestType requestType,
						String response) {
					if (requestType == WebTvApiRequestType.UpdateSession) {
						UpdateSessionResponseHandler handler = new UpdateSessionResponseHandler();
						handler.parseResponse(response);
						if ("SUCCESS".equals(handler.getResponseCode()) && NowIDLoginStatus.getInstance().isBinded()) {
							WebTvApiRequest npr = WebTvApiRequest.getInstance();
							npr.setCallback(this);
							npr.unbindNowId();
						} else {
							MyAlertDialog alert = MyAlertDialog.newInstnace(handler.getResponseCode());
							alert.show(getFragmentManager(), null);
							if (bindProgressDialog != null && bindProgressDialog.isShowing()) {
								bindProgressDialog.dismiss();
							}
							refreshLoginStatus();
						}
					}
					if (requestType == WebTvApiRequestType.Unbind) {
						if (bindProgressDialog != null && bindProgressDialog.isShowing()) {
							bindProgressDialog.dismiss();
						}
						BaseResponseHandler handler = new BaseResponseHandler();
						handler.parseResponse(response);
						if ("SUCCESS".equals(handler.getResponseCode())) {
						} else {
							MyAlertDialog alert = MyAlertDialog.newInstnace(handler.getResponseCode());
							alert.show(getFragmentManager(), null);
						}
						onActivityCreated(null);
					}
				}

				@Override
				public void onFailure(WebTvApiRequestType requestType,
						String errorCode) {
					if (bindProgressDialog != null && bindProgressDialog.isShowing()) {
						bindProgressDialog.dismiss();
					}
					refreshLoginStatus();
				}
				
			};

		};
		DialogInterface.OnClickListener cancelClick= new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
		};
		unbindDialog.setPositiveButton(LanguageHelper.getLocalizedString("setting.nowid.binding.unbind.alert.confirm"), confirmClick);
		unbindDialog.setNegativeButton(LanguageHelper.getLocalizedString("setting.nowid.binding.unbind.alert.cancel"), cancelClick);
		unbindDialog.setCancelable(false);
		unbindDialog.show();

	}

}

package com.hua.nowplayerjunior.service;

import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.hua.activity.R;
import com.pccw.nmal.appdata.AppInfo;
import com.pccw.nmal.checkout.CheckoutFlowController;
import com.pccw.nmal.checkout.CheckoutFlowController.CheckoutUIEvent;
import com.pccw.nmal.checkout.VerifyNowIdPasswordCheckout;
import com.pccw.nmal.model.StreamInfo;
import com.pccw.nmal.net.WebTvApiRequest;
import com.pccw.nmal.net.WebTvApiRequest.WebTvApiRequestType;
import com.pccw.nmal.util.LanguageHelper;
import com.pccw.nmal.util.PreferenceHelper;

public class PlayTimeControlAlertDialogController {

	public static final String PLAY_TIME_CONTROL_ACTION_UNLOCKED = "ptcUnlocked";
	private static PlayTimeControlAlertDialogController instance;
	private Context mContext;
	private View inputPasswordView;
	private EditText passwordText;
	private ProgressDialog progressDialog;
	private PasswordCheckCallback callback;
	private AlertDialog passwordDialog;
	private boolean isDialogShowing;
	
	private PlayTimeControlAlertDialogController(Context context) {
		this.mContext = context;
	}
	
	public static synchronized PlayTimeControlAlertDialogController getInstance(Context context) {
		if (instance == null) {
			instance = new PlayTimeControlAlertDialogController(context);
		}
		return instance;
	}
	
	public interface PasswordCheckCallback {
		void onPasswordCorrect();
		void onWrongPassword();
		void onCheckFailed();
	}
	
	public void setPasswordCheckCallback(PasswordCheckCallback passwordCheckCallback) {
		this.callback = passwordCheckCallback;
	}
	
	public void showAlert() {
		if (!isDialogShowing) {
			isDialogShowing = true;
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder
				.setTitle(LanguageHelper.getLocalizedString("app_name"))
				.setMessage(LanguageHelper.getLocalizedString("ptc_notification_message"))
				.setPositiveButton(LanguageHelper.getLocalizedString("ptc_notification_turnoff"), 
					new OnClickListener() {
		
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							showPasswordDialog(false);
						}
					})
				.setCancelable(false)
				.create()
				.show();
		}
	}
	
	public void showPasswordDialog(boolean withCancelButton) {
		inputPasswordView = ((LayoutInflater) mContext.getSystemService(
				Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.ptc_input_password, null);
			passwordText = (EditText) inputPasswordView.findViewById(R.id.editPassword);
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder
			.setTitle(R.string.ptc_password_dialog_title)
			.setView(inputPasswordView)
			.setPositiveButton(0/*R.string.alert_button_ok*/, new PasswordEnteredListener())
			.setCancelable(false);
		if (withCancelButton) {
			builder.setNegativeButton(0/*R.string.alert_button_cancel*/, null);
		}
		passwordDialog = builder.create();
		passwordDialog.show();
	}
	
	private class PasswordEnteredListener implements OnClickListener, WebTvApiRequest.Callback {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			String password = passwordText.getText().toString();
			showProgressDialog();
			CheckoutFlowController cfc = new CheckoutFlowController(mContext);
			VerifyNowIdPasswordCheckout vnipc = 
					new VerifyNowIdPasswordCheckout(password, AppInfo.getAppId());
			cfc.setCheckoutStepHandler(vnipc);
			cfc.setCheckoutEventHandler(new CheckoutUIEvent() {
				
				@Override
				public void onSuccess() {
					if (callback != null) {
						closeProgressDialog();
						callback.onPasswordCorrect();
					} else {
						unlock();
					}
				}
				
				@Override
				public void onReceivedPlaylist(List<List<StreamInfo>> playlist,
						String serviceId, int bookmark) {}
				
				@Override
				public void onNotLoggedIn() {
					unlock();
				}
				
				@Override
				public void onNotBinded() {}
				
				@Override
				public void onNeedSubscription() {}
				
				@Override
				public void onConnectivityWarning() {}
				
				@Override
				public void onCheckoutFailed(String errorCode) {
					if (callback != null && !"FAIL".equals(errorCode)) {
						closeProgressDialog();
						callback.onCheckFailed();
					} else {
						closeProgressDialog();
						showErrorAlert();
					}
				}

				@Override
				public void onSystemMaintenance() {
					
				}

				@Override
				public void onParentalLock(String responseCode) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onDeviceRegistration(String responseCode) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onReceivedPlaylistWithConcurrent(
						List<List<StreamInfo>> playlist, String serviceId,
						int bookmark, String ccCustId, String ccDomain,
						String ccPoolType) {
					onReceivedPlaylist(playlist, serviceId, bookmark);
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
				
			});
			cfc.startCheckout();
		}

		@Override
		public void onResponseReceived(WebTvApiRequestType requestType,
				String response) {
		}

		@Override
		public void onFailure(WebTvApiRequestType requestType, String errorCode) {
			
		}

	}
	
	private void showErrorAlert() {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder
			.setTitle(mContext.getString(0/*R.string.error_alert_general_error_title*/))
			.setMessage(mContext.getString(0/*R.string.ve_purchase_password_invalid_title*/))
			.setPositiveButton(0/*R.string.alert_button_back*/, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					if (callback != null) {
						callback.onWrongPassword();
					} else {
						showPasswordDialog(false);
					}
				}
			})
			.setCancelable(false)
			.create()
			.show();
	}
	
	private void unlock() {
		isDialogShowing = false;
		closeProgressDialog();
		PreferenceHelper.removePreference(/*SettingPlayTimeControlFragment.PTC_ALARM_MILLIS*/"");
		Intent newIntent = new Intent(PLAY_TIME_CONTROL_ACTION_UNLOCKED);
		LocalBroadcastManager.getInstance(mContext).sendBroadcast(newIntent);
	}

	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = ProgressDialog.show(mContext, 
					LanguageHelper.getLocalizedString("progress.dialog.title"), 
					LanguageHelper.getLocalizedString("progress.dialog.message"));
		}
	}

	private void closeProgressDialog() {
		if (progressDialog != null) {
	        progressDialog.dismiss();
	        progressDialog = null;
	    }
	}
}


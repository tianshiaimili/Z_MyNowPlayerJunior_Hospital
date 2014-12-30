package com.hua.gz.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

public class SingletonAlertDialog implements DialogInterface.OnClickListener {

	private AlertDialog mAlertDialog;
	private DialogCallback mDialogCallback;
	private int mCurrentDialogId;
	private Context mContext;
	
    public interface DialogCallback {
        public void onOKButtonClicked(int dialogId);
        public void onCancelButtonClicked(int dialogId);
    }
    
    public void setDialogCallback(DialogCallback callback) {
    	mDialogCallback = callback;
    }
    
    public void removeDialogCallback() {
    	mDialogCallback = null;
    }
    
    public SingletonAlertDialog(Context context, DialogCallback callback) {
    	mContext = context;
    	mDialogCallback = callback;
    }

    public void showDialog(int id, int icon, String title, String message, 
    		String okBtn, String cancelBtn, boolean cancelable) {
    	// create singleton AlertDialog instance.
    	if(mAlertDialog == null) {
    		mAlertDialog = new AlertDialog.Builder(mContext)
    			.setIcon(icon)
    			.setTitle(title)
    			.setMessage(message)
    			.setPositiveButton(okBtn, SingletonAlertDialog.this)
    			.setNegativeButton(cancelBtn, SingletonAlertDialog.this)
    			.setCancelable(cancelable)
    			.create();
    	}
    	// refresh current AlertDialog.
    	mCurrentDialogId = id;
    	mAlertDialog.setIcon(icon);
    	mAlertDialog.setTitle(title);
    	mAlertDialog.setMessage(message);
    	mAlertDialog.setCancelable(cancelable);
    	// Important!!! show the dialog before calling getButton().
    	mAlertDialog.show();
    	if(okBtn != null) {
    		mAlertDialog.getButton(Dialog.BUTTON_POSITIVE).setVisibility(View.VISIBLE);
    		mAlertDialog.getButton(Dialog.BUTTON_POSITIVE).setText(okBtn);
    	} else {
    		mAlertDialog.getButton(Dialog.BUTTON_POSITIVE).setVisibility(View.GONE);
    	}
    	if(cancelBtn != null) {
    		mAlertDialog.getButton(Dialog.BUTTON_NEGATIVE).setVisibility(View.VISIBLE);
    		mAlertDialog.getButton(Dialog.BUTTON_NEGATIVE).setText(cancelBtn);
    	} else {
    		mAlertDialog.getButton(Dialog.BUTTON_NEGATIVE).setVisibility(View.GONE);
    	}
    }
    
    public void dismissDialog() {
    	if(mAlertDialog != null) {
    		mAlertDialog.dismiss();
    	}
    }
    
	@Override
	public void onClick(DialogInterface dialog, int which) {
		if(mDialogCallback == null) {
			return;
		}
		switch(which) {
		case Dialog.BUTTON_POSITIVE:
			mDialogCallback.onOKButtonClicked(mCurrentDialogId);
			break;
		case Dialog.BUTTON_NEGATIVE:
			mDialogCallback.onCancelButtonClicked(mCurrentDialogId);
			break;
		}
	}
	
}

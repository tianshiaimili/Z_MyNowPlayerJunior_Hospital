package com.hua.nowplayerjunior.utils;

import com.pccw.nmal.util.LanguageHelper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;


public class MyAlertDialog extends DialogFragment {

    public interface Callback {
        public void onClickOKButton(String tag);
        public void onClickCancelButton(String tag);
    }
    
    private Callback callback;
    
    public void setCallback(Callback callback) {
        this.callback = callback;
    }
    
    public void removeCallback() {
        setCallback(null);
    }
    
    public static MyAlertDialog newInstance(int icon, String title, String message, 
            String okBtn, String cancelBtn) {
        MyAlertDialog frag = new MyAlertDialog();
        Bundle args = new Bundle();
        args.putInt("icon", icon);
        args.putString("title", title);
        args.putString("message", message);
        args.putString("okBtn", okBtn);
        args.putString("cancelBtn", cancelBtn);
        frag.setArguments(args);
        frag.setCancelable(false);
        return frag;
    }
    
    public static MyAlertDialog newInstnace(String errorCode) {
        Log.i("MyAlertDialog", "error alert view receive error code: " + errorCode);
        String title;
        String message;
        String okBtnTitle = LanguageHelper.getLocalizedString("alert.button.back");
        
        if (ErrorCodeString.NOT_ACTIVATED.equals(errorCode)) {
            title = null;
            message = LanguageHelper.getLocalizedString("error.alert.email.not.verified.message");
            okBtnTitle = LanguageHelper.getLocalizedString("alert.button.ok");
        } else if (ErrorCodeString.NoConnection.equals(errorCode) 
                || ErrorCodeString.NowPlayerRequestTimeout.equals(errorCode)
                || ErrorCodeString.ProxyServerRequestTimeout.equals(errorCode)
                || ErrorCodeString.EPGDataTimeout.equals(errorCode)
                || ErrorCodeString.SearchRequestTimeout.equals(errorCode)) {
            title = LanguageHelper.getLocalizedString("error.alert.general.error.title");
            message = LanguageHelper.getLocalizedString("error.alert.connection.timeout.message");
        } else if (ErrorCodeString.VodNormalEnd.equals(errorCode)
                || ErrorCodeString.VodIdleTooLong.equals(errorCode)
                || ErrorCodeString.SharpCut.equals(errorCode)) {
			okBtnTitle = LanguageHelper.getLocalizedString("alert.button.exit");	
            title = LanguageHelper.getLocalizedString("alert.end.of.program.title");
            message = LanguageHelper.getLocalizedString("alert.end.of.program.message");  
//        } else if (ErrorCodeString.GeoCheckFail.equals(errorCode)) {
//            title = SettingLanguage.getLocalizedString("error.alert.geo.block.title");
//            message = SettingLanguage.getLocalizedString("error.alert.geo.block.message");  
        } else if ("80001".equals(errorCode) || "80002".equals(errorCode) || "80003".equals(errorCode) || "80004".equals(errorCode) 
                || "80005".equals(errorCode) || "80006".equals(errorCode)
                || "80011".equals(errorCode) || "80012".equals(errorCode) || "80015".equals(errorCode) || "80016".equals(errorCode)
                || "80017".equals(errorCode) || "80018".equals(errorCode) 
                || "80022".equals(errorCode) || "80023".equals(errorCode) || "80024".equals(errorCode) || "80025".equals(errorCode)
                || "80026".equals(errorCode) || "80027".equals(errorCode) || "80028".equals(errorCode) || "80029".equals(errorCode) 
                || "80031".equals(errorCode) || "80032".equals(errorCode) || "80033".equals(errorCode) || "80034".equals(errorCode) 
                || "80041".equals(errorCode) || "80042".equals(errorCode) || "80043".equals(errorCode) || "80044".equals(errorCode) 
                || "80121".equals(errorCode) || "80122".equals(errorCode) || "80125".equals(errorCode) || "80126".equals(errorCode) 
                || "80127".equals(errorCode) || "80142".equals(errorCode) || "80143".equals(errorCode) 
                || "90001".equals(errorCode) || "90002".equals(errorCode) 
                || "90010".equals(errorCode) || "90011".equals(errorCode) || "90012".equals(errorCode) || "90013".equals(errorCode)
                || "90027".equals(errorCode)
                || "90050".equals(errorCode) || "90051".equals(errorCode) || "90052".equals(errorCode) || "90053".equals(errorCode) 
                || "90054".equals(errorCode) || "90055".equals(errorCode) || "90056".equals(errorCode) 
                || "90060".equals(errorCode) || "90061".equals(errorCode) || "90062".equals(errorCode) || "90063".equals(errorCode) 
                ) {
            title = null;
            message = LanguageHelper.getLocalizedString("error.alert.proxy.server.code." + errorCode);
            // bug 12247: Update the button from "Back" to "OK" "確定"
            if("80025".equals(errorCode)) {
            	okBtnTitle = LanguageHelper.getLocalizedString("alert.button.ok");
            }
        } else if (ErrorCodeString.NXPPlayerErrorRooted.equals(errorCode)) {
            title = LanguageHelper.getLocalizedString("error.alert.general.error.title");
            message = LanguageHelper.getLocalizedString("error.alert.general.error.message") + " (" + errorCode + ")";
        } 
        // parental lock error code for nowplayer junior
        else if(errorCode.equals("10016")){
        	title = LanguageHelper.getLocalizedString("error.alert.general.error.title");
        	message = LanguageHelper.getLocalizedString("error.alert.parentallock.message") + " (" + errorCode + ")";
        	okBtnTitle = "OK"; 
        }
        else {
            title = LanguageHelper.getLocalizedString("error.alert.general.error.title");
            message = LanguageHelper.getLocalizedString("error.alert.general.error.message");// + " (" + errorCode + ")";
        }
        return MyAlertDialog.newInstance(-1, title, message, okBtnTitle, null);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int icon = getArguments().getInt("icon");
        String title = getArguments().getString("title");
        String message = getArguments().getString("message");
        String okBtn = getArguments().getString("okBtn");
        String cancelBtn = getArguments().getString("cancelBtn");

        return new AlertDialog.Builder(getActivity())
                .setIcon(icon)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(okBtn,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            if (callback!=null) {
                                callback.onClickOKButton(getTag());
                            }
                        }
                    }
                )
                .setNegativeButton(cancelBtn,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            if (callback!=null) {
                                callback.onClickCancelButton(getTag());
                            }
                        }
                    }
                )
                .create();
    }
}

package com.hua.nowid.activity;

public class NowIDChangePasswordFragment extends NowIDFragment {

	private static final String TAG = NowIDChangePasswordFragment.class.getName();
	private static final String START_URL = "https://login.now.com/netpass/[lang]/app/now_change_pass.jsp";
	private static final String SUCCESS_URL = "https://login.now.com/netpass/common/now_app_close_xml.jsp";
	private static final String CANCEL_URL = "https://login.now.com/netpass/common/now_app_close_xml.jsp";

	public NowIDChangePasswordFragment() {
		super();
		setStartUrl(START_URL);
		setSuccessUrl(SUCCESS_URL);
		setCancelUrl(CANCEL_URL);
	}

}

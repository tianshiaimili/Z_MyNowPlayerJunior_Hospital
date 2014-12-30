package com.hua.nowplayerjunior.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.BounceInterpolator;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.hua.activity.R;
import com.hua.gz.app.aide.AppLocaleAide;
import com.hua.gz.model.CustomerDataModle;
import com.hua.nmal.appdate.AppInfo;
import com.hua.nowplayerjunior.constants.Constants;
import com.hua.nowplayerjunior.interactf.InteractiveFragment;
import com.hua.nowplayerjunior.receiver.NotificationStartupReceiver;
import com.hua.nowplayerjunior.receiver.PlayTimeControlAlertDialogReceiver;
import com.hua.nowplayerjunior.service.PlayTimeControlService;
import com.hua.nowplayerjunior.util.http.AsyncHttpCallback;
import com.hua.nowplayerjunior.util.http.AsyncHttpGet;
import com.hua.nowplayerjunior.util.http.HttpRequestItem;
import com.hua.nowplayerjunior.utils.LogUtils2;
import com.hua.nowplayerjunior.utils.MyAlertDialog;
import com.hua.nowplayerjunior.utils.UserSetting;
import com.hua.nowplayerjunior.utils.UserSetting.UserSettingType;
import com.lifevibes.lvmediaplayer.LVMediaPlayer;
import com.pccw.android.ad.AdSplashDialog;
import com.pccw.android.ad.common.AppConfigInfo;
import com.pccw.android.ad.common.UserSettings;
import com.pccw.common.notification.NotificationService;
import com.pccw.nmal.nowid.sso.ExternalStorageSSOImpl;
import com.pccw.nmal.nowid.sso.NowIdSSO;
import com.pccw.nmal.service.PixelLogService;
import com.pccw.nmal.util.LanguageHelper;
import com.pccw.nmal.util.PreferenceHelper;
import com.pccw.nowid.NowIDLoginStatus;

public class MainActivity extends FragmentActivity implements
		AsyncHttpCallback, MyAlertDialog.Callback, AppInfo.DownloadInfoCallback {

	private final static String TITLE_YUYUE ="yuyue";
	private final static String TITLE_MIND ="mind";
	private final static String TITLE_FIND ="find";
	private final static String TITLE_MORE ="more";
	
	private final static String TAG = MainActivity.class.getSimpleName();
	public static Context appContext;

	private static final String TERMS_AND_CONDITION_PROMPT = "tncPrompt";
	private ProgressDialog progressDialog;
	private NotificationService mBoundService = null;
	private boolean mIsBound;
	private AppInfo appInfo = new AppInfo(this, Constants.APP_INFO_URL,
			Constants.APP_INFO_APP_ID);
	/**
	 * 频道、自选节目、jr.Club、互动区、设定
	 */
	private Fragment liveFragment;
	/**
	 * 自选节目
	 */
	private Fragment programsFragment;
	/**
	 * dianying
	 */
	private Fragment movieFragment; 
	private Fragment veFragment,
			faqFragment, settingFragment;
	/**
	 * 互动区
	 */
	private Fragment interactiveFragment;
	private Fragment jrClubFragment;
	private MenuItem myPlayListBtn;

	private String version = "";
	private PixelLogService pixelLog;

	private boolean shouldShowPlaylistBtn = false;
	private int tabCount;

	private PagerAdapter mPagerAdapter;
	private Button backButton;
	private Button logoutButton;
	private TextView navTitleText;
	private RelativeLayout.LayoutParams navTitleTextRLLparam;
	private ImageView navLogoImage;

	private Fragment lastFragment;

	private OrientationEventListener orientationEventListener;
	private boolean sensorOrientationEnabled;

	private PlayTimeControlAlertDialogReceiver ptcAlertDialogReceiver;
	
	/**
	 * 下方tab的图片button
	 */
	private ImageButton tabbarImageButtons[];
	/**
	 * 正常情况下 下方tab 的button图片id
	 */
	private int tabbarIconIds[];
	/**
	 *  下方tab 选中后显示的图片id
	 */
	private int tabbarIconActiveIds[];
	/**
	 * tab 显示的文字
	 */
	private TextView tabbarTextViews[];
	/**
	 * 这个应该是点击后 下方跳出来的动画图片
	 */
	private ImageView tabbarImageViews[];
	private Fragment tabbarFragments[];

	private Fragment currentFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		LogUtils2.d("*****************************");
		/*
		 * set language
		 */
		LanguageHelper.setCurrentLanguage(LanguageHelper.getCurrentLanguage());
		// 设置一开始进去 显示的广告 或者条款
//		 promptTermsAndCondition();
		PreferenceHelper.setPreference(
				PreferenceHelper.PROMPTED_VIDEO_QUALITY_WARNING, false);
		//
		setContentView(R.layout.main);
		appContext = getApplicationContext();

		AppInfo.restoreInfo(savedInstanceState);

		 downloadInfo();

		UserSetting us = new UserSetting(appContext);
		us.save(UserSettingType.APP_LAUNCH); // save user setting to push server

		UserSettings
				.setLanguage(AppLocaleAide.isAppLocaleEn(this) ? UserSettings.LANGUAGE_ENGLISH
						: UserSettings.LANGUAGE_CHINESE);

	}

	/**
	 * 判断服务 是否在运行
	 * @return
	 */
	public boolean isNowplayerServiceRunning() {

		ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

		List<RunningServiceInfo> runningServiceInfoList = activityManager
				.getRunningServices(Integer.MAX_VALUE);

		for (RunningServiceInfo info : runningServiceInfoList) {
			if ("com.hua.nowplayerjunior.service".equals(info.service
					.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public void setupActionbar() {

		ArrayList<String> tempTabList = new ArrayList<String>();
		// for (String tab : AppInfo.getTabConfig().clone()) {
		// if ("Live".equals(tab) ||
		// "Programs".equals(tab) ||
		// "Setting".equals(tab) ||
		// "Interactive".equals(tab)||
		// "JrClub".equals(tab) ) {
		// tempTabList.add(tab);
		// }
		// }

		tempTabList.add("Live");
		tempTabList.add("Programs");
//		tempTabList.add("JrClub");
		tempTabList.add("Interactive");
		tempTabList.add("Setting");

		String[] tabList = tempTabList.toArray(new String[] {});

		if (tabList.length == 3) {
			setContentView(R.layout.tabbar_3_tab);
			tabCount = 3;
		} else if (tabList.length == 4) {
			setContentView(R.layout.tabbar_4_tab);
			tabCount = 4;
		} else {
			setContentView(R.layout.tabbar_5_tab_jr_club);
			tabCount = 5;
		}

		LogUtils2.i("************setupActionbar****************");
		
		liveFragment = new AddCustomerFragment();
		programsFragment = new RootCategoryFragment();
		settingFragment = new SettingFragment();
		interactiveFragment = new InteractiveFragment();
		jrClubFragment = new JrClubWelcomeFragment();

		backButton = (Button) findViewById(R.id.nav_back_btn);
		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		// 退出....
		logoutButton = (Button) findViewById(R.id.nav_logout_btn);
		logoutButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				NowIdSSO sso = new ExternalStorageSSOImpl(
						MainActivity.this);
				sso.removeData();
				PreferenceHelper
						.removePreference(PreferenceHelper.SECURE_COOKIE);
				NowIDLoginStatus.getInstance().clearAllValues();
				cancelPTCAlarm();
				final UserSetting userSetting = new UserSetting(appContext);
				userSetting.save(UserSettingType.LOGOUT_NOWID);
				onBackPressed();
			}
		});

		navTitleText = (TextView) findViewById(R.id.nav_title_text);// 免费区 等
																	// 页头什么的描述
		navTitleTextRLLparam = (RelativeLayout.LayoutParams) navTitleText
				.getLayoutParams();
		navLogoImage = (ImageView) findViewById(R.id.imageViewnav_nowplayer_logo);// nowplayer
																					// 的ico

		tabbarImageButtons = new ImageButton[tabCount]; 
		tabbarIconIds = new int[tabCount];
		tabbarIconActiveIds = new int[tabCount];
		tabbarTextViews = new TextView[tabCount];
		tabbarImageViews = new ImageView[tabCount];
		tabbarFragments = new Fragment[tabCount];

		for (int i = 0; i < tabCount; i++) {
			int realIndex = i + 1;
			tabbarImageButtons[i] = (ImageButton) findViewById(getResources()
					.getIdentifier("tabbarImageButton" + realIndex, "id",
							Constants.PROGRESS_PACKAGE_NAME));
			// 可以通过此方法 循环获取xml中定义的Components
			// getResources().getIdentifier("在xml中定义的id,或者说图片名（针对drawable）",
			// "类型 例如 id string drawable 等", "包名");
			// //可以通过此方式 获取的 对应的xml中定义的值
			// getBaseContext().getResources().getString(R.string.tabbar_item_live_title);

			tabbarImageViews[i] = (ImageView) findViewById(getResources()
					.getIdentifier("tabbarImageView" + realIndex, "id",
							Constants.PROGRESS_PACKAGE_NAME));
			tabbarTextViews[i] = (TextView) findViewById(getResources()
					.getIdentifier("tabbarTextView" + realIndex, "id",
							Constants.PROGRESS_PACKAGE_NAME));

			if (tabList[i].equalsIgnoreCase("Live")) {
				tabbarImageButtons[i]
						.setImageResource(R.drawable.tabicon_channel);
				tabbarTextViews[i].setText(LanguageHelper
						.getLocalizedString("tabbar.item.live.title"));// channel
				tabbarIconIds[i] = R.drawable.tabicon_channel;
				tabbarIconActiveIds[i] = R.drawable.tabicon_channel_active;
			} else if (tabList[i].equalsIgnoreCase("Programs")) {
				tabbarImageButtons[i]
						.setImageResource(R.drawable.tabicon_ondemand);
				tabbarTextViews[i].setText(LanguageHelper
						.getLocalizedString("tabbar.item.program.title"));// On
																			// Demand
				tabbarIconIds[i] = R.drawable.tabicon_ondemand;
				tabbarIconActiveIds[i] = R.drawable.tabicon_ondemand_active;
			} else if (tabList[i].equalsIgnoreCase("Setting")) {
				tabbarImageButtons[i]
						.setImageResource(R.drawable.tabicon_setting);
				tabbarTextViews[i].setText(LanguageHelper
						.getLocalizedString("tabbar.item.setting.title"));// Settings
				tabbarIconIds[i] = R.drawable.tabicon_setting;
				tabbarIconActiveIds[i] = R.drawable.tabicon_setting_active;
			} else if (tabList[i].equalsIgnoreCase("Interactive")) {
				tabbarImageButtons[i]
						.setImageResource(R.drawable.tabicon_interactive);
				tabbarTextViews[i].setText(LanguageHelper
						.getLocalizedString("tabbar.item.interactive.title"));// Fun
																				// &amp;
																				// Games
																				// &amp表示转换特殊的字符
				tabbarIconIds[i] = R.drawable.tabicon_interactive;
				tabbarIconActiveIds[i] = R.drawable.tabicon_interactive_active;
			} else if (tabList[i].equalsIgnoreCase("JrClub")) {
				tabbarImageButtons[i]
						.setImageResource(R.drawable.tabicon_jrclub);
				tabbarTextViews[i]
						.setText(getString(R.string.tab_jrclub_title));// Jr.
																		// Club
				tabbarIconIds[i] = R.drawable.tabicon_jrclub;
				tabbarIconActiveIds[i] = R.drawable.tabicon_jrclub_active;
			}

			tabbarImageButtons[i].setSelected(false);
		}

	}

	public void cancelPTCAlarm() {
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(this, PlayTimeControlService.class);
		PendingIntent pi = PendingIntent.getService(this, 0, intent, 0);
		alarmManager.cancel(pi);
		PreferenceHelper
				.removePreference(SettingPlayTimeControlFragment.PTC_ALARM_MILLIS);
	}

	@Override
	public void onBackPressed() {
		FragmentManager manager = getSupportFragmentManager();
		lastFragment = manager.findFragmentById(R.id.fragment_container);
		if (lastFragment instanceof FAQFragment) {
			if (!((FAQFragment) lastFragment).onBackPressed()) {
				super.onBackPressed();
			}
		} else if (lastFragment instanceof MoreGameFragment) {
			if (!((MoreGameFragment) lastFragment).onBackPressed()) {
				super.onBackPressed();
			}
		} else if (lastFragment instanceof AddCustomerFragment) {
			if (((AddCustomerFragment) lastFragment).isfullscreen()) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			} else {
				super.onBackPressed();
			}
		} else {
			super.onBackPressed();
		}
	}

	private void restartService() {
		/*
		 * NotificationServiceSetting.init(appContext); // Establish a
		 * connection with the service. We use an explicit // class name because
		 * we want a specific service implementation that // we know will be
		 * running in our own process (and thus won't be // supporting component
		 * replacement by other applications). new AsyncTask<Object, Integer,
		 * Boolean>(){ //register to the push server first
		 * 
		 * @Override protected Boolean doInBackground(Object... params) {
		 * NotificationStartupReceiver.registerToPushServer(); return true; }
		 * 
		 * @Override protected void onPostExecute(Boolean result) {
		 * startService(new Intent(NowplayerActivity.this,
		 * NotificationService.class)); //bindService(new
		 * Intent(NowplayerActivity.this, NotificationService.class),
		 * mConnection, Context.BIND_AUTO_CREATE); //mIsBound = true; }
		 * }.execute();
		 */
		NotificationStartupReceiver.startService(this);

		Log.d("NowplayerActivity", "doBindService");
	}

	/**
	 * 设置一开始提示的条款
	 */
	private void promptTermsAndCondition() {
		if (!PreferenceHelper
				.getPreferenceBoolean(PreferenceHelper.PROMPTED_TNC)) {
			progressDialog = ProgressDialog.show(this, LanguageHelper
					.getLocalizedString("progress.dialog.title"),
					LanguageHelper
							.getLocalizedString("progress.dialog.message"));

			HttpRequestItem requestItem = new HttpRequestItem();
			String url = String.format(Constants.TERMS_AND_CONDITION_TEXT_URL,
					LanguageHelper.getCurrentLanguage());
			requestItem.setUrl(url);

			AsyncHttpGet httpget = new AsyncHttpGet();
			httpget.setCallback(this);
			httpget.execute(new Object[] { requestItem });

		} else {
			// Ad Engine - show splash screen on startup
			LogUtils2.d("AdSplashDialog.showSplashDialog.....");
			AdSplashDialog.showSplashDialog(this,
					AppConfigInfo.getAdSplashURL());
		}
	}

	/**
	 * 2使用方法popBackStack()从activity的后退栈中弹出fragment们（这可以模拟后退键引发的动作）。
	 */
	public void clearBackStack(){
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		for(int i=0;i<fragmentManager.getBackStackEntryCount();i++){
			fragmentManager.popBackStack();
		}
		
	}
	
	public void stopOrientationListener(){
		if(orientationEventListener != null){
			orientationEventListener.disable();
		}
	}
	
	public void TabItemTapped(View imageButton) {

		if (imageButton.isSelected())
			return;

//		clearBackStack();// Multiple layers fragment back press crash bug fixed.

		stopOrientationListener();

		/**
		 * TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, -113, getResources()
						.getDisplayMetrics())
						这个表示 设置像素为dip 格式的 而不是单单写入-113来表示像素
		 */
		final int regular_margin = Math.round(TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, -113, getResources()
						.getDisplayMetrics()));
		final int active_margin = Math.round(TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, -35, getResources()
						.getDisplayMetrics()));

		Log.d("nowplayerkids", "regular_margin = " + regular_margin);
		Log.d("nowplayerkids", "active_margin = " + active_margin);

		final RelativeLayout parent = (RelativeLayout) imageButton.getParent();
		final ImageView bgimg = (ImageView) parent.findViewWithTag("btnbg");
		final TextView title = (TextView) parent.findViewWithTag("title");
		final LinearLayout tabbar = (LinearLayout) findViewById(R.id.tabbar);

		// Deselect all

		//这里是设置选中一个后 再选择别的时候 颜色的转换
		for (int i = 0; i < tabbar.getChildCount(); i++) {

			final RelativeLayout tabitem = (RelativeLayout) tabbar
					.getChildAt(i);
			final TextView tabitem_title = (TextView) tabitem
					.findViewWithTag("title");
			final ImageView tabitem_bgimg = (ImageView) tabitem //ImageButton 选中后的背景图片
					.findViewWithTag("btnbg");
			final ImageButton tabitem_btn = (ImageButton) tabitem
					.findViewWithTag("btn");
			final MarginLayoutParams tabitem_lp = (MarginLayoutParams) tabitem_bgimg
					.getLayoutParams();

			// check if selected
			// if (tabitem_lp.bottomMargin != regular_margin) {
			//表示上一个button被选中了，然后这里把之前（即上一个被选中）的颜色还原成没有被选中状态
			if (tabitem_btn.isSelected()) {
				// deselect it
				Animation regular_animation = new Animation() {

					@Override
					protected void applyTransformation(float interpolatedTime,
							Transformation t) {

						tabitem_lp.bottomMargin = (int) ((active_margin - regular_margin) * (1 - interpolatedTime))
								+ regular_margin;
						tabitem_bgimg.setLayoutParams(tabitem_lp);

						/**
						 *  getWindow().getDecorView()这个方法获取到的view就是程序不包括标题栏的部分
						 */
						View rootView = getWindow().getDecorView()
								.findViewById(android.R.id.content);
						rootView.invalidate();
					}
				};

				regular_animation.setDuration(200);
				regular_animation.setRepeatMode(0);
				regular_animation.setFillAfter(true);
				regular_animation.setFillBefore(true);
				regular_animation.setInterpolator(new BounceInterpolator());
				regular_animation.setAnimationListener(new AnimationListener() {

					ImageView iv;

					public AnimationListener setStopTarget(ImageView iv) {
						this.iv = iv;
						return this;
					}

					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						Log.d("Animation", "regular_animation is ended");
						iv.clearAnimation();
					}
				}.setStopTarget(tabitem_bgimg));
				tabitem_bgimg.clearAnimation();
				tabitem_bgimg.startAnimation(regular_animation);

				int buttonImageDrawableId = 0;
				for (int j = 0; j < tabbarImageButtons.length; j++) {
					if (tabbarImageButtons[j].getId() == tabitem_btn.getId()) {
						buttonImageDrawableId = tabbarIconIds[j];
						// /////
						tabitem_btn.setImageResource(tabbarIconIds[j]);
						// ////
					}
				}
				// //
				 tabitem_btn.setImageDrawable(getResources().getDrawable(buttonImageDrawableId));
				tabitem_title.setTextColor(getResources().getColor(
						R.color.TabBar_Title_Normal));
				tabitem_btn.setSelected(false);

			}
		}

		// ////////////////////////

		if (bgimg != null && title != null) {

			FragmentManager fragmentManager = getSupportFragmentManager();
			final FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();

			final MarginLayoutParams lp = (MarginLayoutParams) bgimg
					.getLayoutParams();

			Log.d("nowplayerkids", "margin = " + lp.bottomMargin);

			imageButton.setSelected(true);

			// if (lp.bottomMargin == regular_margin) {
			if (imageButton.isSelected()) { // //////////////////////////
				// Make active

				Animation active_animation = new Animation() {

					@Override
					protected void applyTransformation(float interpolatedTime,
							Transformation t) {

						lp.bottomMargin = (int) ((active_margin - regular_margin) * interpolatedTime)
								+ regular_margin;
						bgimg.setLayoutParams(lp);

						View rootView = getWindow().getDecorView()
								.findViewById(android.R.id.content);
						rootView.invalidate();
					}
				};

				active_animation.setDuration(200);
				active_animation.setRepeatMode(0);
				active_animation.setInterpolator(new BounceInterpolator());
				active_animation.setFillAfter(true);
				active_animation.setFillBefore(true);
				active_animation.setAnimationListener(new AnimationListener() {

					ImageView iv;

					public AnimationListener setStopTarget(ImageView iv) {
						this.iv = iv;
						return this;
					}

					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						Log.d("Animation", "regular_animation is ended");
						iv.clearAnimation();
					}
				}.setStopTarget(bgimg));
				bgimg.startAnimation(active_animation);

				int buttonImageDrawableId = 0;
				for (int j = 0; j < tabbarImageButtons.length; j++) {
					if (tabbarImageButtons[j].getId() == imageButton.getId()) {
						buttonImageDrawableId = tabbarIconIds[j];
						((ImageButton) imageButton)
								.setImageResource(tabbarIconActiveIds[j]);
					}
				}
//				  ((ImageButton)v).setImageResource(buttonImageDrawableId);
				title.setTextColor(getResources().getColor(
						R.color.TabBar_Title_Active));

				if ((title.getText().toString())
						.equalsIgnoreCase(LanguageHelper
								.getLocalizedString("tabbar.item.live.title"))) {
					showTitle(TITLE_YUYUE);
					showTabFragment(liveFragment, fragmentTransaction);
					
				} else if ((title.getText().toString())
						.equalsIgnoreCase(LanguageHelper
								.getLocalizedString("tabbar.item.program.title"))) {
					showTitle(TITLE_MIND);
					showTabFragment(programsFragment, fragmentTransaction);
					
				}else if ((title.getText().toString())
						.equalsIgnoreCase(LanguageHelper
								.getLocalizedString("tabbar.item.setting.title"))) {
//					fragmentTransaction.replace(R.id.fragment_container,
//							settingFragment);
					showTitle(TITLE_MORE);
					showTabFragment(settingFragment, fragmentTransaction);
					
				} else if ((title.getText().toString())
						.equalsIgnoreCase(LanguageHelper
								.getLocalizedString("tabbar.item.interactive.title"))) {
//					fragmentTransaction.replace(R.id.fragment_container,
//							interactiveFragment);
					showTitle(TITLE_FIND);
					showTabFragment(interactiveFragment, fragmentTransaction);
//					pixelLog.pixelLogOnTabPress(
//							PixelLogService.PIXELLOG_INTERACTIVE, version);
				} 
				fragmentTransaction.commit();

			} else {
				// Make Regular

				Animation regular_animation = new Animation() {

					@Override
					protected void applyTransformation(float interpolatedTime,
							Transformation t) {

						lp.bottomMargin = (int) ((active_margin - regular_margin) * (1 - interpolatedTime))
								+ regular_margin;
						bgimg.setLayoutParams(lp);

						View rootView = getWindow().getDecorView()
								.findViewById(android.R.id.content);
						rootView.invalidate();
					}
				};

				regular_animation.setDuration(200);
				regular_animation.setRepeatMode(0);
				regular_animation.setFillAfter(true);
				regular_animation.setFillBefore(true);
				bgimg.clearAnimation();
				bgimg.startAnimation(regular_animation);

				((ImageButton) imageButton).setImageDrawable(getResources().getDrawable(
						R.drawable.tabicon_channel));
				title.setTextColor(getResources().getColor(
						R.color.TabBar_Title_Normal));

			}
		}

	}

	private void downloadInfo() {
		//模拟 先不从网络获取数据
//		appInfo.setDownloadConfigCallback(this);
//		appInfo.downloadInfo();
		setupActionbar();
		TabItemTapped(tabbarImageButtons[0]);
		
	}

	@Override
	public void onDownloadInfoSuccess() {
		Log.d("NowplayerLanding", "onDownloadInfoSuccess");
		setupActionbar();
		TabItemTapped(tabbarImageButtons[0]);

	}

	@Override
	public void onDownloadInfoFailed(String reason) {

	}

	@Override
	public void onRegionChanged(String oldRegion, String newRegion) {

	}

	@Override
	public void onSuccess(String result) {

	}

	@Override
	public void onFailure(Exception exception) {

	}

	@Override
	public void onClickOKButton(String tag) {

	}

	@Override
	public void onClickCancelButton(String tag) {

	}

	private void checkOverlapping() {
		
		navTitleText.getParent().requestLayout();
		if(navTitleText.getVisibility() == View.VISIBLE && 
				backButton.getVisibility() == View.VISIBLE){
			
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) navTitleText.getLayoutParams();
			if(navTitleText.getLeft() < backButton.getRight()){
				params.addRule(RelativeLayout.CENTER_HORIZONTAL, 0);
				params.addRule(RelativeLayout.RIGHT_OF, R.id.nav_back_btn);
			}else {
				params.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);
				params.addRule(RelativeLayout.RIGHT_OF, 0);
			}
			
			navTitleText.setLayoutParams(params);
			
		}
		
	}
	
	public void enableBackButton(boolean isEnabled, String text) {

		backButton = (Button) findViewById(R.id.nav_back_btn);
		backButton.setVisibility(isEnabled ? View.VISIBLE : View.INVISIBLE);
		backButton.setText(text);
		checkOverlapping();
		
	}

	public void enableBackButton(boolean isEnabled) {

		enableBackButton(isEnabled, LanguageHelper.getLocalizedString("navigationbar.item.back"));
		
	}

	public void showTitleLogo() {

		navLogoImage.setVisibility(View.VISIBLE);
		navTitleText.setVisibility(View.INVISIBLE);
		
	}

	public void showTitle(String content){
		navLogoImage.setVisibility(View.INVISIBLE);
		if(content.equals(TITLE_YUYUE)){
			navTitleText.setText(getResources().getString(R.string.title_yuyue));
		}else if(content.equals(TITLE_MIND)){
			navTitleText.setText(getResources().getString(R.string.title_mind));
		}else if(content.equals(TITLE_FIND)){
			navTitleText.setText(getResources().getString(R.string.title_find));
		}else if(content.equals(TITLE_MORE)){
			navTitleText.setText(getResources().getString(R.string.title_more));
		}
		navTitleText.setVisibility(View.VISIBLE);
	}
	
	
	public void refreshTab() {
		// TODO Auto-generated method stub

	}

	
	public Fragment getCurrentFragment(){
	
		return currentFragment;
	}
	
	public void setCurrentFragment(Fragment fragment){
		currentFragment = fragment;
	}
	
	
	/**
	 * when change the tab ,the fragment will change 
	 * @param fragment
	 * @param fragmentTransaction
	 */
	public void showTabFragment(Fragment fragment,FragmentTransaction fragmentTransaction){
		
		if(fragment == null) return;
		
		String fragmentTag = fragment.getClass().getSimpleName();
		LogUtils2.d("simpleName== "+fragmentTag);
		
		if(fragment.isAdded()){
			LogUtils2.d("********isadd************");
			LogUtils2.e("getCurrentFragment()== "+getCurrentFragment());
			fragmentTransaction.hide(getCurrentFragment());
			fragmentTransaction.show(fragment);
			setCurrentFragment(fragment);
		}else {
			LogUtils2.d("*******new add************");
//			fragmentTransaction.replace(R.id.fragment_container,
//					programsFragment);
//			fragmentTransaction.add(programsFragment, "programsFragment");
			fragmentTransaction.add(R.id.fragment_container, fragment,fragmentTag);
			fragmentTransaction.addToBackStack(fragmentTag);
			if(getCurrentFragment() != null){
				fragmentTransaction.hide(getCurrentFragment());
			}
			setCurrentFragment(fragment);
		}
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		LogUtils2.e("00000000000000000000 ="+resultCode+"   resultCode= "+resultCode);
		liveFragment.onActivityResult(requestCode, resultCode, data);
		if(data != null){
			
			LogUtils2.e(((CustomerDataModle)data.getSerializableExtra("info")).getUsername());
		}
		if(resultCode == 200){
			LogUtils2.e("123456789");
		}
		
	}
	
}

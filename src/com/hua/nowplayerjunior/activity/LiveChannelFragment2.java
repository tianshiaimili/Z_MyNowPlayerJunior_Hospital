package com.hua.nowplayerjunior.activity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.commonsware.cwac.layouts.AspectLockedFrameLayout;
import com.hua.activity.R;
import com.hua.nowplayerjunior.constants.Constants;
import com.hua.nowplayerjunior.service.PlayerControlProxy;
import com.hua.nowplayerjunior.service.PlayerControlProxy.VideoType;
import com.hua.nowplayerjunior.utils.DisplayUtils;
import com.lifevibes.lvmediaplayer.LVMediaPlayer;
import com.pccw.nmal.appdata.AppInfo;
import com.pccw.nmal.appdata.JsonZip;
import com.pccw.nmal.appdata.JsonZip.ZipType;
import com.pccw.nmal.checkout.CheckoutFlowController;
import com.pccw.nmal.checkout.LiveChannelCheckout;
import com.pccw.nmal.model.LiveCatalog;
import com.pccw.nmal.model.LiveCatalog.LiveCatalogChannelData;
import com.pccw.nmal.model.LiveDetail;
import com.pccw.nmal.model.LiveDetail.LiveDetailData;
import com.pccw.nmal.model.StreamInfo;
import com.pccw.nmal.model.StreamInfo.StreamType;
import com.pccw.nmal.service.PixelLogService;
import com.pccw.nmal.util.DownloadImage;
import com.pccw.nmal.util.ImageCache;
import com.pccw.nmal.util.LanguageHelper;

public class LiveChannelFragment2 extends UIEventBaseFragment {

	private static final String TAG = LiveChannelFragment2.class.getSimpleName();
	private static final int EPG_UPDATE_INTERVAL = 60000;
	private static final double VIDEO_FRAME_ASPECT_RATIO = 1.777777777777777d;
	private View view;
	private HashMap<String, DownloadLiveChannelImage> downloadImageList;
	private ImageButton buttonimageButtonChannel3rd;
	private MyListAdapter myListAdapter;
	private DownloadImage downloadPromotePosterImage = new DownloadImage(getActivity());

	private ImageView promotePoster;
	private ImageView channelPlayButton;
	private ImageButton channelFullScreenButton;
	private AspectLockedFrameLayout videoFrameLayout;
	private RelativeLayout channelInfoFrame;
	private TextView programTitle;
	private TextView programTime;
	private LiveCatalogChannelData currentChannel;
	private VideoPlayerFragment videoPlayerFragment; 	
	private EPGUpdateTimer epgUpdateTimer;
	
	private ImageView[] channelLogoImageView;
	private int lastTabIndex;
	private boolean mIsFullScreen = false;
	private boolean shouldCheckoutOnResume = false;
	private ImageView[] channelSeperatorIV;
	
	public LiveChannelFragment2() {}

	public void setVideoPlayerFragment(VideoPlayerFragment videoPlayerFragment) {
		this.videoPlayerFragment = videoPlayerFragment;
	}
	
	public void setupLiveChannelView() {

		JsonZip jsonZip = new JsonZip(this.getActivity(), AppInfo.getJsonVersionPath(), Constants.JSON_ZIP_VERSION_PREFIX, LanguageHelper.getCurrentLanguage());
		if (jsonZip.shouldUpdateJSONZipVersion(ZipType.PKG) && !jsonZip.isDownloading()) {
			Log.v("TAG", "jsonZip.shouldUpdateJSONZipVersion(ZipType.PKG) && !jsonZip.isDownloading()");
			LiveCatalog.getInstance().clearLiveChannelGenreList();
			showProgressDialog();
			jsonZip.startDownload(ZipType.PKG, callback);
		} else {
			if (!LiveCatalog.getInstance().isLiveChannelListLoaded()) {
				parseLiveChannelJSON();
			}
			if(!LiveDetail.getInstance().isLiveDetailLoaded()){
				parseLiveDeatilJSON();
				
			}
			


			ArrayList<LiveCatalogChannelData> liveChannelList = LiveCatalog.getInstance().getLiveChannelList(true);
	
			if (liveChannelList == null) return;	// don't update view if there is no data yet!!
			
			videoFrameLayout = (AspectLockedFrameLayout) view.findViewById(R.id.videoFrame);
			videoFrameLayout.setAspectRatio(VIDEO_FRAME_ASPECT_RATIO);
			channelInfoFrame = (RelativeLayout) view.findViewById(R.id.channelInfoFrame);
			
			LinearLayout channelListLinearLayout = (LinearLayout) view.findViewById(R.id.channelListLinearLayout);
			channelListLinearLayout.removeAllViews();
			channelLogoImageView = new ImageView[liveChannelList.size()];
			channelSeperatorIV = new ImageView[liveChannelList.size()];
			int i = 0;
			for (final LiveCatalogChannelData lcd : liveChannelList) {
				//------------for channel logo ------------
				channelLogoImageView[i] = new ImageView(getActivity());
				final int DP_120 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, getResources().getDisplayMetrics());
				final int DP_7 = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 7, getResources().getDisplayMetrics());
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(DP_120, -1);
				channelLogoImageView[i].setLayoutParams(layoutParams);
				channelLogoImageView[i].setBackgroundResource(R.drawable.channel_btn_bg);
				channelLogoImageView[i].setPadding(DP_7, DP_7, DP_7, DP_7);
				channelLogoImageView[i].setOnClickListener(new OnClickListener() {
					
					private int index;
					
					public OnClickListener setIndex(int index) {
						this.index = index;
						return this;
					}
					
					@Override
					public void onClick(View v) {
						highlightChannelBarIcon(index);
						handleChannelSwitching();
						
					}
				}.setIndex(i));
				Bitmap b = ImageCache.getInstance().get(lcd.getChannelLogoLink());
				Drawable channelLogo = b == null ? null : new BitmapDrawable(getResources(), b);
				if (channelLogo == null) {
					channelLogoImageView[i].setTag(lcd.getChannelLogoLink());
					if (!downloadImageList.containsKey(lcd.getId())) {
						DownloadLiveChannelImage downloadImage = new DownloadLiveChannelImage(lcd);
						downloadImage.executeWithThreadPool(channelLogoImageView[i]);
						downloadImageList.put(lcd.getId(), downloadImage);
					}
				} else {
					channelLogoImageView[i].setImageDrawable(channelLogo);
				}
				
				//----------for channel separator ---
				int width = DisplayUtils.dip2px(getActivity(), 2);
				//int height = DisplayUtils.dip2px(getActivity(), 24);
				LinearLayout.LayoutParams layoutParam = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.FILL_PARENT);
				layoutParam.topMargin = DisplayUtils.dip2px(getActivity(), 15);
				layoutParam.bottomMargin = DisplayUtils.dip2px(getActivity(), 15);
				layoutParam.gravity = Gravity.CENTER_VERTICAL;
				channelSeperatorIV[i] = new ImageView(getActivity());
				channelSeperatorIV[i].setLayoutParams(layoutParam);
				Drawable d = getResources().getDrawable(R.drawable.seperator_light);
				d.setAlpha((int) (255*0.4));
				channelSeperatorIV[i].setBackgroundDrawable(d);
//				channelSeperatorIV[i].setTag("channel_separator_"+i);
				
				channelListLinearLayout.addView(channelLogoImageView[i]);
				channelListLinearLayout.addView(channelSeperatorIV[i]);
				i++;
			}
			
			
			highlightChannelBarIcon(lastTabIndex);
			//channelLogoImageView[0].performClick();
			
			//Prepare EPG
			epgUpdateTimer = new EPGUpdateTimer(EPG_UPDATE_INTERVAL, EPG_UPDATE_INTERVAL);
			epgUpdateTimer.onFinish();
			epgUpdateTimer.start();
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (downloadImageList == null) {
			downloadImageList = new HashMap<String, DownloadLiveChannelImage>();
		}
		if (savedInstanceState != null) {
			lastTabIndex = savedInstanceState.getInt("tabIndex");
		}
		//setupLiveChannelView();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.live_ch_fragment,container, false);

		promotePoster = (ImageView) view.findViewById(R.id.promotePoster);
		channelPlayButton = (ImageView) view.findViewById(R.id.channelPlayBtn);
		channelFullScreenButton = (ImageButton) view.findViewById(R.id.channelFSBtn);
		channelFullScreenButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mIsFullScreen) {
					getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				} else {
					getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				}
			}
		});
		programTitle = (TextView) view.findViewById(R.id.programtitle);
		programTime = (TextView) view.findViewById(R.id.programtime);

		//view = inflater.inflate(R.layout.live_ch_fragment_junior,container, false);
		//adWebView = (WebView) view.findViewById(R.id.adWebView);
		return view;
	}

	private class DownloadLiveChannelImage extends DownloadImage {
		public LiveCatalogChannelData channelData;
		public DownloadLiveChannelImage(LiveCatalogChannelData data) {
			super(getActivity());
			channelData = data;
		}

		@Override
		protected Drawable doInBackground(Object... params) {
			return super.doInBackground(params);
		}

		@Override
		protected void onPostExecute(Drawable result) {
			super.onPostExecute(result);
			if (result != null) {
				//Bitmap roundCornerBitmap = getRoundCornerBitmap(bitmap, 10);
				imv.setImageDrawable(result);
				//int newHeight = (view.getWidth()/2) * result.getIntrinsicHeight() / result.getIntrinsicWidth();
				//GridView.LayoutParams lp = new GridView.LayoutParams((view.getWidth()/2), newHeight);
				//imv.setLayoutParams(lp);
				//channelData.setLiveChannelLogo(imv.getDrawable());
				// Workaround to save the round-cornered bitmap to cache once more
				//ImageCache.getInstance().add(imv.getTag().toString(), result.);
			}
			//Log.d("LiveChannelFragment", "!!!notifyDataSetChanged()");
			//myListAdapter.notifyDataSetChanged();

		}	

		/*convert drawable to bitmap*/
		public Bitmap drawableToBitmap(Drawable drawable) {
			Bitmap bitmap = Bitmap.createBitmap(
					drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight(),
					drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
			Canvas canvas = new Canvas(bitmap);
			//canvas.setBitmap(bitmap);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			drawable.draw(canvas);
			return bitmap;
		}
	}    

	@Override
	public void onResume() {
		super.onResume();
//		((NowplayerJrActivity)getActivity()).enableBackButton(false);
//		((NowplayerJrActivity)getActivity()).showTitleLogo();
//		Log.v(TAG, "onResume");
//		setupLiveChannelView();
//		if (videoPlayerFragment != null) {
//			channelPlayButton.setVisibility(View.INVISIBLE);
//			promotePoster.setVisibility(View.INVISIBLE);
//			channelFullScreenButton.setVisibility(View.VISIBLE);
//			((NowplayerJrActivity) getActivity()).startOrientationListener();
//		} else if (shouldCheckoutOnResume && currentChannel != null) {
//			showPromotePoster(currentChannel);
//			handleChannelSwitching();
//		}
		/* Replaced by Ad Engine
    	if (adWebView != null) {
    		adWebView.getSettings().setJavaScriptEnabled(true);
    		adWebView.loadUrl(Constants.AD_BANNER_LIVE.replace("[lang]", 
    			SettingLanguage.getCurrentLanguage().equals("zh") ? 
    			Constants.AD_BANNER_LANG_ZH : 
    			Constants.AD_BANNER_LANG_EN)
    			 + "?t=" + new Random().nextInt());
    	}
		 */

		// Ad Engine
		//((NowplayerJrActivity) getActivity()).reloadAdBanner();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//		((NowplayerJrActivity)getActivity()).stopOrientationListener();
		if (epgUpdateTimer != null) {
			epgUpdateTimer.cancel();
		}
	}

	public class MyListAdapter extends ArrayAdapter<LiveCatalogChannelData> {

		ArrayList<LiveCatalogChannelData> mList;
		LayoutInflater mInflater;
		int mResource;
		Context mContext;
		int selectedIndex;
		
		public MyListAdapter(Context context, int resource, ArrayList<LiveCatalogChannelData> list) {
			super(context, resource, list);

			mResource = resource;
			mInflater = getActivity().getLayoutInflater();
			mList = list;
			mContext = context;
		}

		public int getIndex() {
			return selectedIndex;
		}
		
		public void setIndex(int selectedIndex) {
			this.selectedIndex = selectedIndex;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ImageView imageView;
			Bitmap b = ImageCache.getInstance().get(mList.get(position).getChannelLogoLink());
			Drawable channelLogo = b == null ? null : new BitmapDrawable(getResources(), b);

			imageView = new ImageView(mContext);
			imageView.setLayoutParams(new GridView.LayoutParams(87, 87));
			imageView.setTag(mList.get(position).getChannelLogoLink());
			if (selectedIndex == position) { 
				imageView.setBackgroundResource(R.drawable.channel_btn_bg_active);
			} else {
				imageView.setBackgroundResource(R.drawable.channel_btn_bg);
			}
			//imageView.setAdjustViewBounds(true);
			//imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			//imageView.setPadding(10, 10, 10, 10);
			if (channelLogo == null) {
				imageView.setImageDrawable(null);
		
				//int newHeight = (view.getWidth()/2) * imageView.getDrawable().getIntrinsicHeight() / imageView.getDrawable().getIntrinsicWidth();
				//GridView.LayoutParams lp = new GridView.LayoutParams((view.getWidth()/2), newHeight);
				//imageView.setLayoutParams(lp);

				if (!downloadImageList.containsKey(mList.get(position).getId())) {
					DownloadLiveChannelImage downloadImage = new DownloadLiveChannelImage(mList.get(position));
					downloadImage.executeWithThreadPool(imageView);
					downloadImageList.put(mList.get(position).getId(), downloadImage);
				}
			}
			else{
				imageView.setImageDrawable(channelLogo);
				//imageView.setBackgroundResource(R.drawable.channel_btn_bg);
				//int newHeight = (parent.getWidth())/2 * channelLogo.getIntrinsicHeight() / channelLogo.getIntrinsicWidth();
				//GridView.LayoutParams lp = new GridView.LayoutParams(parent.getWidth()/2, newHeight);
				//imageView.setLayoutParams(lp);
			}
			
			return imageView;  
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			showProgressDialog();
			LiveChannelCheckout liveCheckout = new LiveChannelCheckout(currentChannel, null, "", Constants.APP_INFO_APP_ID, StreamType.ADAPTIVE);
			CheckoutFlowController checkoutFlowController = new CheckoutFlowController(getActivity());
			checkoutFlowController.setCheckoutStepHandler(liveCheckout);
			checkoutFlowController.setCheckoutEventHandler(this);
			checkoutFlowController.startCheckout();
		}
		shouldCheckoutOnResume = false;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		
		// Bugfix for Bugzilla 12512
		PlayerControlProxy pcp = PlayerControlProxy.getInstance();
		pcp.setFragment(null);
		pcp.setLiveChannelFragment(null);
		
		if (downloadImageList != null) {
			for(int i=0; i<downloadImageList.size(); i++) {
				if (downloadImageList.get(i) != null && !downloadImageList.get(i).isCancelled())
					downloadImageList.get(i).cancel(true);
			}
			downloadImageList.clear();
		}
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
		videoPlayerFragment = null;
		PlayerControlProxy.getInstance().terminateMoviePlayer();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt("tabIndex", lastTabIndex);
	}
	
	private JCB callback = new JCB();
	private class JCB implements JsonZip.Callback {

		@Override
		public void updateProgress(int precent) {
		}

		@Override
		public void onDownloadCompleted(ZipType zipType, boolean isOK) {
			Log.d(TAG, "JSONZip onDownloadCompleted isOK=" + isOK);
			if (isOK) {
				if (zipType == ZipType.PKG) {
					Log.v(TAG, "onDownloadCompleted, before parsing");
					if (parseLiveChannelJSON() && parseLiveDeatilJSON()) {
						Log.v(TAG, "onDownloadCompleted, after parsing");
						epgUpdateTimer = new EPGUpdateTimer(EPG_UPDATE_INTERVAL, EPG_UPDATE_INTERVAL);
						epgUpdateTimer.onFinish();
						epgUpdateTimer.start();
						closeProgressDialog();
						setupLiveChannelView();
					} else {
						closeProgressDialog();
					}
				}
			}
		}
	}

	private boolean parseLiveChannelJSON() {
		Log.v("parseLiveChannelJSON", "parseLiveChannelJSON");
		JsonZip jsonZip = new JsonZip(this.getActivity(), AppInfo.getJsonVersionPath(), Constants.JSON_ZIP_VERSION_PREFIX, LanguageHelper.getCurrentLanguage());
		String result = jsonZip.getJSONData(ZipType.PKG, Constants.CMS_JSON_LIVE_CHANNEL_CATALOG);
		return (LiveCatalog.getInstance().parseLiveCatalogJSON(result));
	}
	
	private boolean parseLiveDeatilJSON() {
		Log.v("parseLiveDeatilJSON", "parseLiveDeatilJSON");
		JsonZip jsonZip = new JsonZip(this.getActivity(), AppInfo.getJsonVersionPath(), Constants.JSON_ZIP_VERSION_PREFIX, LanguageHelper.getCurrentLanguage());
		String result = jsonZip.getJSONData(ZipType.PKG, Constants.CMS_JSON_LIVE_CHANNEL_DETAIL);
		return (LiveDetail.getInstance().parseLiveDetailJSON(result));
	}
	
	private void highlightChannelBarIcon(int index) {
		ArrayList<LiveCatalogChannelData>  channelList =LiveCatalog.getInstance().getLiveChannelList(true);
		if (channelList.size()<=index) {
			index = 0;
		}
		for (int j = 0; j < channelLogoImageView.length; j++) {
			if (index == j) {
				channelLogoImageView[j].setBackgroundResource(R.drawable.channel_btn_bg_active);
			} else {
				channelLogoImageView[j].setBackgroundResource(R.drawable.channel_btn_bg);
			}
			
			if ((index == j) || (index ==j+1)) {
				channelSeperatorIV[j].setVisibility(View.INVISIBLE);
			}else if(j == channelLogoImageView.length-1){
				channelSeperatorIV[j].setVisibility(View.GONE);
			} 
			else {
				channelSeperatorIV[j].setVisibility(View.VISIBLE);
			}
		}
		lastTabIndex = index;
		currentChannel = LiveCatalog.getInstance().getLiveChannelList(true).get(index);
		showPromotePoster(currentChannel);
	}
	
	private void handleChannelSwitching() {
		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//		((NowplayerJrActivity) getActivity()).stopOrientationListener();
		PlayerControlProxy.getInstance().terminateMoviePlayer();
		videoPlayerFragment = null;
		
		if (epgUpdateTimer != null) {
			epgUpdateTimer.cancel();
			epgUpdateTimer.onFinish();
		}
		/*
		if (!NowIDLoginStatus.getInstance().isLoggedIn()) {
			showPromotePoster(currentChannel);
		} else {
		*/
			showProgressDialog();
			LiveChannelCheckout liveCheckout = new LiveChannelCheckout(currentChannel, null, "", Constants.APP_INFO_APP_ID, StreamType.ADAPTIVE);
			CheckoutFlowController checkoutFlowController = new CheckoutFlowController(getActivity());
			checkoutFlowController.setCheckoutStepHandler(liveCheckout);
			checkoutFlowController.setCheckoutEventHandler(this);
			checkoutFlowController.startCheckout();
			/*
		}
*/
		
	}
	
	private void showPromotePoster(LiveCatalogChannelData currentChannel) {
		promotePoster.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//if (NowIDLoginStatus.getInstance().isBinded()) {
					handleChannelSwitching();
				//} else {
				//	NowplayerJrActivity.showNowIDPages(LiveChannelFragment.this);
				//}
			}
		});
		
		
		promotePoster.setVisibility(View.VISIBLE);
		channelPlayButton.setVisibility(View.VISIBLE);
		channelFullScreenButton.setVisibility(View.INVISIBLE);

		Bitmap b = ImageCache.getInstance().get(currentChannel.getPhonePromoPosterLink());
		Drawable poster = b == null ? null : new BitmapDrawable(getResources(), b);
		if (poster != null) {
			promotePoster.setImageDrawable(poster);
		} else {
			promotePoster.setTag(currentChannel.getPhonePromoPosterLink());
			downloadPromotePosterImage.cancel(true);
			downloadPromotePosterImage = new DownloadImage(getActivity());
			downloadPromotePosterImage.executeWithThreadPool(promotePoster);
		}
	}
	
	private void playVideo(List<List<StreamInfo>> playlist, int quality) {

		promotePoster.setVisibility(View.INVISIBLE);
		channelPlayButton.setVisibility(View.INVISIBLE);
		channelFullScreenButton.setVisibility(View.VISIBLE);
		
		StreamInfo[] streamInfoPlaylist = new StreamInfo[playlist.size()];

		for (int i=0; i< playlist.size(); i++) {
			streamInfoPlaylist[i] = playlist.get(i).get(0);
			Log.d(TAG, "add this item to extra for streaming: " + 
					streamInfoPlaylist[i].toString());
		}
		
		/*
		videoPlayerFragment = new VideoPlayerFragment();
		videoPlayerFragment.setLiveChannelFragment(this);
		Bundle arguments = new Bundle();
		arguments.putString("channel", currentChannel.getId());
		arguments.putParcelableArray("streams", streamInfoPlaylist);
		arguments.putBoolean("isSlateVideo", false);
		arguments.putInt("selectedQuality", quality);
		arguments.putInt("videoType", VideoType.VideoTypeLive.ordinal());
		arguments.putString("classification", "");
		arguments.putInt("videoBookmarkSelection", 0);
		arguments.putString("pidvideoBookmarkSelection", "");
		arguments.putString("nowid", NowIDLoginStatus.getInstance().getNowID());
		arguments.putBoolean("calledFromFragment", true);
		
		videoPlayerFragment.setArguments(arguments);
		FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
		transaction.replace(R.id.videoPlayer, videoPlayerFragment);
		transaction.commit();
		*/
		PlayerControlProxy pcp = PlayerControlProxy.getInstance();
		pcp.setIsEmbeddedView(true);
		pcp.setFragment(this);
		//TODO 在原来的基础上注视掉
//		pcp.setLiveChannelFragment(this);
		pcp.setPlaylists(playlist);
		pcp.setChannelNo(currentChannel.getId());
		pcp.setChannelLogoPath(null);
		pcp.setVideoQualitySelection(quality);
		pcp.setVideoType(VideoType.VideoTypeVod);
		pcp.setServiceId(lastServiceId);
		
		pcp.playNext();
		
//		((NowplayerJrActivity)getActivity()).startOrientationListener();
		
		new PixelLogService(Constants.PIXEL_LOG_URL, Constants.PIXEL_LOG_APP_NAME, 
				LVMediaPlayer.getUniqueIdentifier(getActivity())).pixelLogAppRunNxp();
	}
	
	private class EPGUpdateTimer extends CountDownTimer {

		public EPGUpdateTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			if (programTitle != null && programTime != null && currentChannel != null) {
				String channelNo = currentChannel.getId();
				LiveDetailData epgProgram = LiveDetail.getInstance().getProgramByChannelAndTime(channelNo, System.currentTimeMillis());
				if (epgProgram != null) {
					programTitle.setText(epgProgram.getName());
					programTime.setText(epgProgram.getStartTime() + " - " + epgProgram.getEndTime());
				} else {
					programTitle.setText("");
					programTime.setText("");
				}
			}
			this.start();
		}

		@Override
		public void onTick(long millisUntilFinished) {
		}
	}
	
	public void toggleVideoViewToFullScreen(boolean isFullScreen) {
		this.mIsFullScreen = isFullScreen;
		if (channelInfoFrame != null && videoFrameLayout != null) {
			channelInfoFrame.setVisibility(isFullScreen ? View.GONE : View.VISIBLE);
			if (isFullScreen) {
				videoFrameLayout.resetAspectRatio();
			} else {
				videoFrameLayout.setAspectRatio(VIDEO_FRAME_ASPECT_RATIO);
			}
			if (videoPlayerFragment != null) {
//				videoPlayerFragment.setFullScreen(isFullScreen);
			}
		}
	}
	
	public boolean isfullscreen(){
		return mIsFullScreen;
	}

	public void setFullScreenButtonVisibile(boolean isVisibile) {
		channelFullScreenButton.setVisibility(isVisibile ? View.VISIBLE : View.INVISIBLE);
	}
	
	public void setChannelPlayButtonVisibile(boolean isVisibile) {
		channelPlayButton.setVisibility(isVisibile ? View.VISIBLE : View.INVISIBLE);
	}
	
	@Override
	protected void afterQualitySelected(List<List<StreamInfo>> playlists, int quality) {
		if (quality > 0) {
			playVideo(playlists, quality);
		} else {
			showPromotePoster(currentChannel);
		}
	}

	@Override
	protected void afterQualityAndBookmarkSelected(
			List<List<StreamInfo>> playlist, int quality, int bookmark) {
		//No bookmark for live, and we've overridden afterQualitySelected already
	}
	
	/**
	 * Workaround for java.lang.IllegalStateException: No activity bug
	 * See http://stackoverflow.com/questions/15207305/
	 */
	@Override
	public void onDetach() {
	    super.onDetach();

	    try {
	        Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
	        childFragmentManager.setAccessible(true);
	        childFragmentManager.set(this, null);

	    } catch (NoSuchFieldException e) {
	        throw new RuntimeException(e);
	    } catch (IllegalAccessException e) {
	        throw new RuntimeException(e);
	    }
	}

	public void resetLiveChannelViews() {
		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		showPromotePoster(currentChannel);
	}
}

 
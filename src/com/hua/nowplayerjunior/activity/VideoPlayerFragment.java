package com.hua.nowplayerjunior.activity;

import android.support.v4.app.Fragment;

import com.hua.nowplayerjunior.utils.MyAlertDialog;
import com.pccw.nmal.appdata.AppInfo;

public class VideoPlayerFragment extends Fragment implements MyAlertDialog.Callback, AppInfo.DownloadInfoCallback {

	@Override
	public void onDownloadInfoSuccess() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDownloadInfoFailed(String reason) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRegionChanged(String oldRegion, String newRegion) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClickOKButton(String tag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClickCancelButton(String tag) {
		// TODO Auto-generated method stub
		
	}
/*
	public static final String TAG = VideoPlayerFragment.class.getSimpleName();
	//public static final String FLASH_URL = "http://iis01.no-ip.biz/smp/nowPlayerFlash.html";
	public static final String FLASH_URL = "https://nowplayer.now.com/public/mobile/smp/nowPlayerFlash.html";
	private static final String HDMI_INTENT = "android.intent.action.HDMI_PLUGGED";
    public final static String EXTRA_HDMI_PLUGGED_STATE = "state";
	private StreamInfo[] streams;
	private VideoType videoType;
	private String videoUri;
	private boolean isSlateVideo;
	private int quality;
	private String channelNo;
	private String programName;
	private String channelLogoPath;
	private boolean hasChapter;
	private String productID;
	private boolean videoSelected = false;
	private boolean backFromResume = false;
	private boolean cameFromCreate = false;
	private int videoBookmarkSelection;
	private String pidvideoBookmarkSelection;
	
	private NowPlayerVideoView videoView;
	private NowPlayerMediaController mediaController;
	private ProgressBar progressBar;
	private ImageView mClassImage;
	private TextView mClassText;
	private ImageButton mMultipleAudioBtn;
	private LVAudioTrack[] mAudioList = null;
	private int currentAudioId = 0;
	private TextView mProgramName;
	private TextView mProgramDuration;
	private ImageView mChannelLogo;
	private VideoInfoPoller videoInfoPoller;
	private AppInfo is = new AppInfo(getActivity(), Constants.APP_INFO_URL, Constants.APP_INFO_APP_ID);
	
	LVMediaPlayerListener mpl = new LVMediaPlayerListener();
	
	private int duration = 0;
	private int lastPos = 0;

	private boolean videoIsEnded = false;
	private String pendingErrorCode;
	private boolean receiverUnregistered = true;
	
	private boolean promptingDisallow3GAlert = false;
	private boolean hdmiPlugged = false;
	
	private Context ctx;
	
	//For long time prompt
	private long longTimeStartTime = -1;
	private final long longTimePromptStartTime = 60000;
	private final long interval = 1000;
	private LongPlayCountDownTimer longPlayTimer;
	private LongPlayPromptCountDownTimer promptTimer;
	private EPGProgramTimer epgProgramTimer; 
	private AlertDialog longPlayAlertDialog;
	private int longPlayTick;
	private int promptTick;
	
	private WatermarkView watermarkView;
	private String watermarkText;
	private boolean shouldShowWatermark = false;
	
	private boolean calledFromFragment = false;
	private LiveChannelFragment liveChannelFragment;
	
	private boolean pausedByPTC = false;

	private boolean isQualityWarningClicked = false;
	
	public void setPendingErrorCode(String pendingErrorCode) {
		this.pendingErrorCode = pendingErrorCode;
	}
	
	public void setLiveChannelFragment(LiveChannelFragment liveChannelFragment) {
		this.liveChannelFragment = liveChannelFragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ctx=this.getActivity().getApplicationContext();
		Bundle extras = getArguments();
		if (extras != null) {
			//mode = extras.getInt("mode");
			videoType = VideoType.values()[extras.getInt("videoType")];
			channelNo = extras.getString("channel");
			isSlateVideo = extras.getBoolean("isSlateVideo", true);
			watermarkText = extras.getString("nowid");
			Parcelable[] parcelArray = extras.getParcelableArray("streams");
			streams = new StreamInfo[parcelArray.length];
			for (int i = 0; i < parcelArray.length; i++) {
				streams[i] = (StreamInfo) parcelArray[i];
			}
			quality = extras.getInt("selectedQuality");
			videoBookmarkSelection = extras.getInt("videoBookmarkSelection");
			pidvideoBookmarkSelection = extras.getString("pidvideoBookmarkSelection");
			programName = extras.getString("programName");
			productID = extras.getString("productId");
			hasChapter = extras.getBoolean("hasChapter", false);
			channelLogoPath = extras.getString("channelLogoPath");
//			if (channelLogoPath != null && !channelLogoPath.equals("")) {
//				channelLogoPath = AppInfo.getImageDomain() + channelLogoPath;
//			}
			calledFromFragment = extras.getBoolean("calledFromFragment", false);
		}
		
        // check if the video quality warning prompted
        if (!isSlateVideo && !PreferenceHelper.getPreferenceBoolean(PreferenceHelper.PROMPTED_VIDEO_QUALITY_WARNING)) {
            MyAlertDialog dialog = MyAlertDialog.newInstance(-1, 
            		LanguageHelper.getLocalizedString("error.alert.video.quality.warning.title"), 
            		LanguageHelper.getLocalizedString("error.alert.video.quality.warning.message"), 
            		LanguageHelper.getLocalizedString("alert.button.ok"), 
                    null);
            dialog.setCallback(new Callback() {
				
				@Override
				public void onClickOKButton(String tag) {
					// TODO Auto-generated method stub
					isQualityWarningClicked = true;
				}
				
				@Override
				public void onClickCancelButton(String tag) {
					// TODO Auto-generated method stub
					
				}
			});
            dialog.show(this.getFragmentManager(), PreferenceHelper.PROMPTED_VIDEO_QUALITY_WARNING);
            PreferenceHelper.setPreference(PreferenceHelper.PROMPTED_VIDEO_QUALITY_WARNING, true);
            
			is = new AppInfo(getActivity(), Constants.APP_INFO_URL, Constants.APP_INFO_APP_ID);
			is.setDownloadConfigCallback(this);
			is.downloadInfo();
            
        }
		
		cameFromCreate = true;
		
		promptTimer = new LongPlayPromptCountDownTimer(longTimePromptStartTime, interval);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = null;
		view = inflater.inflate(R.layout.video_native, null);
		videoView = (NowPlayerVideoView) view.findViewById(R.id.playerVideoView);
		mediaController = (NowPlayerMediaController) view.findViewById(R.id.playerMediaController);
		mClassImage = (ImageView) view.findViewById(R.id.classImage);
		mClassText = (TextView) view.findViewById(R.id.classText);
		mClassImage.setVisibility(View.GONE);
		mClassText.setVisibility(View.GONE);
		mMultipleAudioBtn =  (ImageButton) view.findViewById(R.id.multiaudio);
		mMultipleAudioBtn.setEnabled(false);
		mMultipleAudioBtn.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()) {
		        case MotionEvent.ACTION_DOWN:
		        	mMultipleAudioBtn.setImageResource(R.drawable.btn_player_audio_onpress);
		            break;
		        case MotionEvent.ACTION_UP:
		        	mMultipleAudioBtn.setImageResource(R.drawable.btn_player_audio);
		        	makeMultipleSelection();
		        }
		        return true;
			}
		});
		watermarkView = (WatermarkView) view.findViewById(R.id.waterMark);
		mChannelLogo = (ImageView) view.findViewById(R.id.epgChannelLogo);
		mProgramName = (TextView) view.findViewById(R.id.programName);
		mProgramDuration = (TextView) view.findViewById(R.id.programTime);
		if (videoType == VideoType.VideoTypeLive) {
			mProgramName.setVisibility(View.VISIBLE);
			mProgramDuration.setVisibility(View.VISIBLE);
			mChannelLogo.setVisibility(View.VISIBLE);

			String url = LiveCatalog.getInstance().getLiveChannelDataById(channelNo).getChannelLogoLink();
			Bitmap b = ImageCache.getInstance().get(url);
			if (b != null) {
				mChannelLogo.setImageBitmap(b);
			} else {
				new DownloadChannelLogo().executeWithThreadPool(url);
			}
		} else if (!isSlateVideo && (videoType == VideoType.VideoTypeVod ||
				videoType == VideoType.VideoTypeVE || 
				videoType == VideoType.VideoTypeVESVOD)) {
			mProgramName.setVisibility(View.VISIBLE);
			mProgramDuration.setVisibility(View.GONE);
			mChannelLogo.setVisibility(View.VISIBLE);
			
			if ((channelLogoPath != null) && (!channelLogoPath.isEmpty())) {
				Bitmap b = ImageCache.getInstance().get(channelLogoPath);
				if (b != null) {
					mChannelLogo.setImageBitmap(b);
				} else {
					new DownloadChannelLogo().executeWithThreadPool(channelLogoPath);
				}
			}
		} else {
			mProgramName.setVisibility(View.GONE);
			mProgramDuration.setVisibility(View.GONE);
			mChannelLogo.setVisibility(View.GONE);
		}

		progressBar = (ProgressBar) view.findViewById(R.id.playerProgressBar);
		
		if (calledFromFragment) {
			mediaController.setFullScreenToggleImage(R.drawable.mp_btn_exit_fullscreen);
			mediaController.setOnScreenModeToggle(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (mediaController.isFullScreen()) {
						getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
					} else {
						getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
					}
				}
			});
			setFullScreen(false);
		} else {
			mediaController.setFullScreen(true);
			mediaController.setOnScreenModeToggle(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					PlayerControlProxy.getInstance().terminateMoviePlayer();
				}
			});
		}

		view.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (calledFromFragment && liveChannelFragment != null) {
						if (getActivity().getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
							if (videoView != null && videoView.isPlaying()) {
								liveChannelFragment.setChannelPlayButtonVisibile(true);
								videoView.pause();
							} else if (videoView != null && !videoView.isPlaying()) {
								liveChannelFragment.setChannelPlayButtonVisibile(false);
								videoView.start();
							}
						}
					}
					videoView.toggleMediaControlsVisiblity(); // video_native handle control visibility
				}
				return false;
			}
		});
		
		if (getActivity().getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			setFullScreen (true);
		}

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		PlayerControlProxy.getInstance().setFragment(this);

		if (streams != null) {
			for (StreamInfo s : streams) {
				if (s.getQuality() == quality) {
					videoUri = s.getUrl();
					Log.d(TAG, "going to play URL: " + videoUri);
					videoView.setVideoURI(Uri.parse(videoUri));
					if (!isSlateVideo) {
						videoView.setMediaController(mediaController);
						mediaController.setLive(videoType == VideoType.VideoTypeLive);
					}
					videoView.setOnErrorListener(mpl);
					videoView.setOnCompletionListener(mpl);
					videoView.setOnPreparedListener(mpl);
					videoSelected = true;
					
					if (!isSlateVideo && watermarkText != null) {
						if (watermarkText.length() > 30) {
							watermarkText = watermarkText.substring(0, 27) + "...";
						}
						watermarkView.setVisibility(View.INVISIBLE);
						watermarkView.setText(watermarkText);
						shouldShowWatermark = true;
					}
					
					if (!isSlateVideo && (videoType == VideoType.VideoTypeVod ||
						videoType == VideoType.VideoTypeVE || 
						videoType == VideoType.VideoTypeVESVOD)) {
						
						mProgramName.setText(programName);
					}
					
					break;
				}
			}
		}
		//videoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
	}
	
	private void makeMultipleSelection() {
		
		Log.d(TAG, "showAudioDialog");
		if (null == mAudioList) {
			return;
		}
		lastPos = videoView.getCurrentPosition();
		
		final ArrayList<String> audioNameList = new ArrayList<String>();
		for (LVAudioTrack t:mAudioList) {
			audioNameList.add(t.getName());
		}
		
		currentAudioId ++;
		if (currentAudioId >= audioNameList.size()){
			currentAudioId = 0;
		}
		if (audioNameList.size() > 1) {
			progressBar.setVisibility(View.VISIBLE);
			videoView.changeAudio(audioNameList.get(currentAudioId));
		}
	}
	
	
	
	@Override
	public void onResume() {
		super.onResume();
		backFromResume = true;
		if (calledFromFragment) {
			onWindowFocusChanged(true);
		}
	}
	
	public void onWindowFocusChanged(boolean hasFocus) {
		Log.d(TAG, "testing onWindowFocusChanged(" + hasFocus + ") called , backFromResume = " + backFromResume);
		
		if(isQualityWarningClicked){
			isQualityWarningClicked = false;
			return;
		}
		
		if (hasFocus && backFromResume) {
			is = new AppInfo(getActivity(), Constants.APP_INFO_URL, Constants.APP_INFO_APP_ID);
			is.setDownloadConfigCallback(this);
			is.downloadInfo();
		}
	}
	
	public void afterCheckingRegion() {
		Log.d(TAG, "afterCheckingRegion");
		PlayerControlProxy.getInstance().setVideoFragmentRunning(true);
		if (videoSelected) {
			if (!videoIsEnded) {
				if (lastPos == 0 && !pausedByPTC) {
					// Start Polling task
					if (videoInfoPoller != null) {
						videoInfoPoller.cancel(true);
					}
					videoInfoPoller = new VideoInfoPoller();
					
			    	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			    		videoInfoPoller.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			    	} else {
			    		videoInfoPoller.execute();
			    	}

				}
				
				if (lastPos != 0 && isSlateVideo) {
					// Terminate movie player directly, don't resume playback
					PlayerControlProxy.getInstance().terminateMoviePlayer();
					return;
				}
				
				if (!pausedByPTC) {
					// Resume playback at last position
					progressBar.setVisibility(View.VISIBLE);
						videoView.start();
						videoView.seekTo(lastPos);
					
					if (cameFromCreate) {
						videoView.start();
						if (videoBookmarkSelection > 0) {
							videoView.seekTo(videoBookmarkSelection * 1000);
						}
						cameFromCreate = false;
					}
				}
			}
			this.getActivity().registerReceiver(this.my3GReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
			this.getActivity().registerReceiver(this.myHdmiListener, new IntentFilter(HDMI_INTENT));
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(PlayTimeControlAlertDialogReceiver.PLAY_TIME_CONTROL_ALERT_BROADCAST);
			intentFilter.addAction(PlayTimeControlAlertDialogController.PLAY_TIME_CONTROL_ACTION_UNLOCKED);
			LocalBroadcastManager.getInstance(getActivity()).registerReceiver(this.myPlayTimeControlListener, intentFilter);
			receiverUnregistered = false;
		} else {
			if (pendingErrorCode == null) {
				pendingErrorCode = "Error";
			}
		}

		if (pendingErrorCode != null) {
			PlayerControlProxy.getInstance().errorControl(pendingErrorCode);
			pendingErrorCode = null;
		}
		backFromResume = false;
	}

	@Override
	public void onPause() {
		Log.d(TAG, "onPause() called");
		if ((!videoIsEnded) && (!isSlateVideo) && NowIDLoginStatus.getInstance().isLoggedIn() && ((videoType == VideoType.VideoTypeVESVOD) || (videoType == VideoType.VideoTypeVE) || (videoType == VideoType.VideoTypeVod))) {	
			WebTvApiRequest.getInstance().setBookmark(pidvideoBookmarkSelection, Integer.toString(videoView.getCurrentPosition()/1000));
		}
		PlayerControlProxy.getInstance().setVideoFragmentRunning(false);
		if (duration > 0) {
			// Save last position only for VODs
			lastPos = videoView.getCurrentPosition();
			Log.d(TAG, "lastPos saved: " + lastPos);
		}
		if (videoInfoPoller != null) {
			videoInfoPoller.cancel(true);
		}
		videoView.pause();

		if (!receiverUnregistered) {
			this.getActivity().unregisterReceiver(my3GReceiver);
			this.getActivity().unregisterReceiver(myHdmiListener);
			LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(myPlayTimeControlListener);
			receiverUnregistered = true;
		}
		if (is != null) {
			is.cancelDownloadInfo();
		}
		if (longPlayTimer!=null){
			longPlayTimer.cancel();
		}
		if (epgProgramTimer != null) {
			epgProgramTimer.cancel();
		}

		super.onPause();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestory() called");
		if (watermarkView != null) {
			watermarkView.stop();
		}
	}

	public void stopPlayback() {
		if (videoView != null) {
			videoView.stopPlayback();
			videoIsEnded = true;
		}
	}
	
	private void resetLongPlayPrompt(){
		Log.d("resetLongPlayPrompt", "Long Play Prompt Reset");
		if (longPlayTimer!=null){
			longPlayTimer.cancel();
		}
		
		longTimeStartTime = is.getLongPlayPromptLong();
		if (longTimeStartTime<=0||videoType.equals(VideoType.VideoTypeVod)){
			Log.d("resetLongPlayPrompt", "Long Play Prompt not started");
			return;
		}
		Log.d("resetLongPlayPrompt", ""+longTimeStartTime);
		longPlayTimer = new LongPlayCountDownTimer(longTimeStartTime, interval);
		longPlayTimer.start();
		longPlayTick=0;
	}

	private class VideoInfoPoller extends AsyncTask<Void, Void, Void> {
	
		@Override
		protected Void doInBackground(Void... params) {
			//Log.d("VideoInfoPoller", "doInBackground started...");
			boolean currentPositionIsZero = true;
			boolean durationIsInvalid = true;
			int bp = 0;
			while(!isCancelled() && (currentPositionIsZero || durationIsInvalid)) {
				if (videoView.isPlaying()) {
					bp = videoView.getCurrentPosition();
					if (progressBar != null && bp > 0) {
						currentPositionIsZero = false;
					}
					int d = videoView.getDuration();
					if (bp > 0) {
						if (d > 0) {
							duration = d;
							PlayerControlProxy.getInstance().movieDuration(d);
						}
						Log.d("VideoInfoPoller", "duration = " + d);
						durationIsInvalid = d <= 0 && videoType != VideoType.VideoTypeLive;
					}
				}
				
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
				}
			}
			//Log.d("VideoInfoPoller", "doInBackground finished...");
			return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			//Log.d("VideoInfoPoller", "onCancelled finished...");
		}


		@Override
		protected void onPostExecute(Void result) {
			if (!isCancelled()) {
				progressBar.setVisibility(View.INVISIBLE);
				if (shouldShowWatermark) {
					if (!calledFromFragment) {
						watermarkView.showWatermark();
					}
					shouldShowWatermark = false; //Avoid triggering second time
				}
			}
			//Log.d("VideoInfoPoller", "onPostExecute finished...");
		}
		
		
	}

	*//**
	 * Hybrid class for both MediaPlayerLister and flash player JS interface
	 *//*
	final class LVMediaPlayerListener implements OnCompletionListener, OnErrorListener, OnPreparedListener, OnSeekCompleteListener {

		// MediaPlayer
		@Override
		public void onPrepared(MediaPlayer arg0) {
			Log.d(TAG, "onPrepared called");
			arg0.setLooping(false);
			((LVMediaPlayer) arg0).setOnSeekCompleteListener(this);
			mediaController.updatePausePlay();
			
			mAudioList = videoView.getAudioTracks();
			if ((mAudioList != null) && (mAudioList.length > 1)) {
    			mMultipleAudioBtn.setEnabled(true);
    			mMultipleAudioBtn.setImageResource(R.drawable.btn_player_audio);
    		}
			
			// in live channel, if user fast change orientation to landscape before video on prepared, the player control will not be landscape mode. Bug id: 13766
			if (getActivity().getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
				setFullScreen (true);
			}
			
			progressBar.setVisibility(View.INVISIBLE); // fix show loading icon when streaming
			
			videoView.startAt(lastPos);
		}

		// MediaPlayer
		@Override
		public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
			onError();
			return true;
		}

		// MediaPlayer
		@Override
		public void onCompletion(MediaPlayer arg0) {
			onCompletion();
		}

		// MediaPlayer
		@Override
		public void onSeekComplete(MediaPlayer mp) {
			Log.d(TAG, "onSeekComplete called");
			progressBar.setVisibility(View.INVISIBLE);
			mediaController.updatePausePlay();
		}

		// Common
		public void onError() {
			Log.d(TAG, "onError()");
			if (!receiverUnregistered) {
				getActivity().unregisterReceiver(my3GReceiver);
				getActivity().unregisterReceiver(myHdmiListener);
				LocalBroadcastManager.getInstance(getActivity()).registerReceiver(VideoPlayerFragment.this.myPlayTimeControlListener, new IntentFilter(PlayTimeControlAlertDialogReceiver.PLAY_TIME_CONTROL_ALERT_BROADCAST));
				receiverUnregistered = true;
			}
			if (isSlateVideo) {
				// Ignore errors for slate
				PlayerControlProxy.getInstance().movieIsEnded();
			} else {
				PlayerControlProxy.getInstance().movieHasError(ErrorCodeString.MoviePlayerError);
			}
		}
		
		// Common
		public void onCompletion() {
			Log.d(TAG, "onCompletion()");
			if ((!isSlateVideo) && NowIDLoginStatus.getInstance().isLoggedIn() && ((videoType == VideoType.VideoTypeVESVOD) || (videoType == VideoType.VideoTypeVE) || (videoType == VideoType.VideoTypeVod))) {
				WebTvApiRequest.getInstance().removeBookmark(pidvideoBookmarkSelection);
			}
			videoIsEnded = true;
			lastPos = 0;
			PlayerControlProxy.getInstance().movieIsEnded();
		}

		// Flash JS interface
		public void onDurationChanged(float duration) {
			Log.d(TAG, "onDurationChanged() " + duration);
			PlayerControlProxy.getInstance().movieDuration(duration);
		}
		
		// Flash JS interface
		// Called when playstate has change to playing
		public void startPlay() {
			if (progressBar != null) {
				VideoPlayerFragment.this.getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						 progressBar.setVisibility(View.INVISIBLE);
					}
				});
			}
		}
	}
	
    private BroadcastReceiver my3GReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();

            if (!action.equals(ConnectivityManager.CONNECTIVITY_ACTION)){
                return;
            }

//            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false); 
//            NetworkInfo aNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
//            connectivityMsg +=
//                  (aNetworkInfo.getType()==0?"mobile":"wifi") + " is " + (noConnectivity?" disconnected":" connected");
//            connectivityMsg += ";";

            NetworkInfo aNetworkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
            if (aNetworkInfo != null) {
                if (!promptingDisallow3GAlert) {
                    if (aNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE
                            && aNetworkInfo.isConnected()
                            && !(PreferenceHelper.getPreferenceBoolean(PreferenceHelper.ALLOW_STREAM_IN_3G))) {
                        Log.d("VideoPlayerFragment", "disallow 3g, but 3g now");

                        VideoPlayerFragment.this.stopPlayback();
                        
                        promptingDisallow3GAlert = true;
                        
                        MyAlertDialog dialog = MyAlertDialog.newInstance(-1, 
                        	LanguageHelper.getLocalizedString("setting.enable.3g.playback.prompt.title"), 
                        	LanguageHelper.getLocalizedString("setting.enable.3g.playback.prompt.message"), 
                        	LanguageHelper.getLocalizedString("setting.enable.3g.playback.prompt.cancel"), 
                        	LanguageHelper.getLocalizedString("setting.enable.3g.playback.prompt.play"));
                        dialog.setCallback(VideoPlayerFragment.this);
                        dialog.show(getFragmentManager(), ErrorCodeString.Prompt3GEnableDuringPlayback);
                    }
                }
            }

        }
    };

    @Override
    public void onClickOKButton(String tag) {
        if (ErrorCodeString.Prompt3GEnableDuringPlayback.equals(tag)) {
            Log.d(TAG, "ok button clicked during prompt 3g disallow in playback");
            promptingDisallow3GAlert = false;
            PlayerControlProxy.getInstance().movieHasError(ErrorCodeString.Prompt3GEnableDuringPlayback);
        }
    }

    @Override
    public void onClickCancelButton(String tag) {
        if (ErrorCodeString.Prompt3GEnableDuringPlayback.equals(tag)) {

        	videoView.setVideoURI(Uri.parse(videoUri));
            videoView.start();

            promptingDisallow3GAlert = false;
            Log.d(TAG, "cancel button clicked during prompt 3g disallow in playback");
        }
    }

	@Override
	public void onDownloadInfoSuccess() {
		// Region not changed. Continue playback
		afterCheckingRegion();
		resetLongPlayPrompt();

		if (videoType == VideoType.VideoTypeLive) {
			epgProgramTimer = new EPGProgramTimer(60000, 60000);
			epgProgramTimer.updateEPGDisplay();
			epgProgramTimer.start();
		}

	}

	@Override
	public void onDownloadInfoFailed(String reason) {
		// Ignore DownloadFailed
	}

	@Override
	public void onRegionChanged(String oldRegion, String newRegion) {
		PlayerControlProxy.getInstance().movieHasError(ErrorCodeString.GeoCheckFail);
	}
    
	public class LongPlayCountDownTimer extends CountDownTimer{
    	public LongPlayCountDownTimer(long startTime, long interval){
    		super(startTime, interval);
    	}

		@Override
		public void onFinish() {
			Log.d("LongPlayCountDownTimer", "onFinish");
			promptTimer.start();
			promptTick=0;
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			longPlayAlertDialog = builder
			.setTitle(LanguageHelper.getLocalizedString("warning.alert.long.play.title"))
			.setMessage(String.format(LanguageHelper.getLocalizedString("warning.alert.long.play.text"), is.getLongPlayPromptLong() / 1000 / 3600))
			.setPositiveButton(LanguageHelper.getLocalizedString("warning.alert.continue"), new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					longPlayTimer.start();
					longPlayTick=0;
					promptTimer.cancel();
				}
			})
			.create();
			longPlayAlertDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
			{
			    @Override
			    public void onCancel(DialogInterface dialog)
			    {
			    	longPlayTimer.start();
					longPlayTick=0;
					promptTimer.cancel();
			    }
			});
			longPlayAlertDialog.show();
		}

		@Override
		public void onTick(long arg0) {
			//longPlayTick++;
			//Log.d("LongPlayCountDownTimer", ""+longPlayTick);
		}
    	
    }
	
	public class LongPlayPromptCountDownTimer extends CountDownTimer{
    	public LongPlayPromptCountDownTimer(long startTime, long interval){
    		super(startTime, interval);
    	}

		@Override
		public void onFinish() {
			Log.d("LongPlayPromptCountDownTimer", "onFinish");
			if (longPlayAlertDialog!=null&&longPlayAlertDialog.isShowing()){
				longPlayAlertDialog.dismiss();
			}
			longPlayAlertDialog=null;
			PlayerControlProxy.getInstance().movieIsTerminated();
			if (!calledFromFragment) {
				getActivity().finish();
			}
		}

		@Override
		public void onTick(long arg0) {
			//promptTick++;
			//Log.d("LongPlayPromptCountDownTimer", ""+promptTick);
		}
    	
    }
    
	public class EPGProgramTimer extends CountDownTimer {

		public EPGProgramTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		public void updateEPGDisplay() {
			
			LiveDetailData program = LiveDetail
					.getInstance()
					.getProgramByChannelAndTime(channelNo, System.currentTimeMillis());
			if (program != null) {
				String programName = program.getName();
				String programTime = program.getStartTime() + " - " + program.getEndTime();
				mProgramName.setText(programName);
				mProgramDuration.setText(programTime);
			}
		}
		
		@Override
		public void onFinish() {
			updateEPGDisplay();
			start();
		}

		@Override
		public void onTick(long millisUntilFinished) {
		}
		
	}
	
	private class DownloadChannelLogo extends DownloadImage {
		String url;

		public DownloadChannelLogo() {
			super(getActivity());
		}
		
		@Override
		protected Drawable doInBackground(Object... params) {
			url = (String) params[0];
			return super.doInBackground(params);
		}



		@Override
		protected void onPostExecute(Drawable result) {
			Bitmap b = ImageCache.getInstance().get(url);
			if (b != null) {
				mChannelLogo.setImageBitmap(b);
			}
		}
	}
	
	public void setFullScreen(boolean isFullScreen) {
		if (mediaController != null) {
			mediaController.setFullScreen(isFullScreen);
			if (!isFullScreen) {
				mediaController.hide();
			}else{
				mediaController.show();
			}
		}
		if (liveChannelFragment != null) {
			liveChannelFragment.setFullScreenButtonVisibile(!isFullScreen);
			if (isFullScreen) {
				liveChannelFragment.setChannelPlayButtonVisibile(false);
			} else {
				liveChannelFragment.setChannelPlayButtonVisibile(
						progressBar.getVisibility() == View.INVISIBLE && !videoView.isPlaying());
			}

		}
		if (isFullScreen && watermarkView != null) {
			watermarkView.showWatermark();
		} else {
			watermarkView.hideWatermark();
		}
	}

	private BroadcastReceiver myHdmiListener = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			 String action = intent.getAction();
			 if (action.equals(HDMI_INTENT)) {
				 if (intent.getBooleanExtra(EXTRA_HDMI_PLUGGED_STATE, false)) {
					 hdmiPlugged = true;
					 stopPlayback();
					 AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					 builder
					 	.setCancelable(false)
					 	.setMessage(getString(R.string.error_alert_hdmi_message))
					 	.setPositiveButton(getString(R.string.alert_button_ok), new OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								PlayerControlProxy.getInstance().terminateMoviePlayer();
							}
						})
					 	.create()
					 	.show();
				 }
			 }
		}
	};
	
	private BroadcastReceiver myPlayTimeControlListener = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (PlayTimeControlAlertDialogReceiver.PLAY_TIME_CONTROL_ALERT_BROADCAST.equals(intent.getAction())) {
				if (videoView.isPlaying()) {
					videoView.pause();
				}
				pausedByPTC = true;
			}
			if (PlayTimeControlAlertDialogController.PLAY_TIME_CONTROL_ACTION_UNLOCKED.equals(intent.getAction())) {
				if (pausedByPTC) {
						videoView.start();
				}
				pausedByPTC = false;
			}
		}
	};

	public  void updateBookmark(){
		WebTvApiRequest.getInstance().setBookmark(pidvideoBookmarkSelection, Integer.toString(videoView.getCurrentPosition()/1000));		
	}*/
	
}

package com.hua.nowplayerjunior.service;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.hua.activity.R;
import com.hua.nowplayerjunior.activity.AddCustomerFragment;
import com.hua.nowplayerjunior.activity.ProgramFragment;
import com.hua.nowplayerjunior.activity.SeriesFragment;
import com.hua.nowplayerjunior.activity.VideoPlayerActivity;
import com.hua.nowplayerjunior.activity.VideoPlayerFragment;
import com.hua.nowplayerjunior.utils.ErrorCodeString;
import com.hua.nowplayerjunior.utils.MyAlertDialog;
import com.hua.nowplayerjunior.utils.MyAlertDialog.Callback;
import com.pccw.nmal.model.StreamInfo;
import com.pccw.nmal.util.LanguageHelper;
import com.pccw.nowid.NowIDLoginStatus;

/**
 * This class was not meant to be existed, yet circumstances and tight schedule
 * make this class as a resurrected version of PlayerControl class......
 *
 */
public class PlayerControlProxy implements Callback, ProxyServerControl.Callback {

	private static final String TAG = PlayerControlProxy.class.getSimpleName();
	private static PlayerControlProxy instance;
	public enum VideoType {VideoTypeLive, VideoTypeVod, VideoTypeVE, VideoTypeVESVOD};
	
	////// Variables to be clear everytime when playing begins
	private VideoPlayerFragment videoPlayerFragment;
	private List<List<StreamInfo>> playlists;
	private VideoPlayerActivity videoPlayerActivity; 
	private Fragment fragment;
	private String channelNo;
	private boolean isMainVideoPlayed = false; 
	private int videoQualitySelection;
	private int videoBookmarkSelection;
	private String productId;
	private String programName;
	private String channelLogoPath;
	private boolean hasChapter;
	private int currentPlayingIndex;
	private String serviceId;
	private VideoType videoType;
	private boolean videoFragmentRunning;
	private boolean isEmbeddedView;
	private AddCustomerFragment liveChannelFragment;

	public void setVideoPlayerActivity(VideoPlayerActivity videoPlayerActivity) {
		this.videoPlayerActivity = videoPlayerActivity;
	}
	public void setFragment(Fragment fragment) {
		this.fragment = fragment;
	}
	public void setPlaylists(List<List<StreamInfo>> playlists) {
		this.playlists = playlists;
	}
	public void setChannelNo(String channelNo) {
		this.channelNo = channelNo;
	}
	public void setVideoQualitySelection(int videoQualitySelection) {
		this.videoQualitySelection = videoQualitySelection;
	}
	public void setVideoBookmarkSelection(int videoBookmarkSelection) {
		this.videoBookmarkSelection = videoBookmarkSelection;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public void setProgramName(String programName) {
		this.programName = programName;
	}
	public void setChannelLogoPath(String channelLogoPath) {
		this.channelLogoPath = channelLogoPath;
	}
	public void setHasChapter(boolean hasChapter) {
		this.hasChapter = hasChapter;
	}
	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}
	public void setVideoType(VideoType videoType) {
		this.videoType = videoType;
	}
	public void setIsEmbeddedView(boolean isEmbeddedView) {
		this.isEmbeddedView = isEmbeddedView;
	}
	public void setLiveChannelFragment(AddCustomerFragment liveChannelFragment) {
		this.liveChannelFragment = liveChannelFragment;
	}
	//////


	private PlayerControlProxy() {}
	
	public synchronized static PlayerControlProxy getInstance() {
		if (instance == null) {
			instance = new PlayerControlProxy();
		}
		return instance;
	}
	
	public void playNext() {
		if (this.isMainVideoPlayed) {
			// all video are played, should return to previous page
			Log.d(TAG, "all videos are played");
			//remove handler
			handler.removeCallbacks(mExceedVodMaxDuration);
			
			MyAlertDialog alertDialog = MyAlertDialog.newInstnace(ErrorCodeString.VodNormalEnd);
			alertDialog.setCallback(this);
			alertDialog.show(this.fragment.getFragmentManager(), ErrorCodeString.VodNormalEnd);
			// will terminate the player after the button clicked //TODO: Check if it really does
			//}
			return;
		}
		
		if (this.videoBookmarkSelection == 0) {
			this.currentPlayingIndex += 1;
		} else {
			this.currentPlayingIndex = 0;
		}
		
		if ((this.currentPlayingIndex == 0) || (this.currentPlayingIndex >= this.playlists.get(0).size())) {
			// play main video
			this.currentPlayingIndex = 0;

			// playmainvideo
			if (ProxyServerControl.SERVICE_ID_FTA.equalsIgnoreCase(this.serviceId)) {
				this.playMainVideo();
			} else {
				// init the proxy server control here
				ProxyServerControl psControl = ProxyServerControl.getInstance();
				psControl.setCallback(this);
				NowIDLoginStatus loginStatus = NowIDLoginStatus.getInstance();
				psControl.startConcurrentControl(loginStatus.getFsa(), 
					this.serviceId, 
					loginStatus.getNetpassId(), 
					this.videoType);
			}
			//wait for the token is valid callback
		} else {
			// play slate video
			this.playVideo(this.currentPlayingIndex);
		}

	}
	
	private void playVideo(final int playIndex) {
		Log.d(TAG, "play the index of " + playIndex);
		//        List<StreamInfo> streamInfoPlaylist = new ArrayList<StreamInfo>();
		StreamInfo[] streamInfoPlaylist = new StreamInfo[this.playlists.size()];

		for (int i=0; i<this.playlists.size(); i++) {
			streamInfoPlaylist[i] = this.playlists.get(i).get(playIndex);
			Log.d(TAG, "add this item to extra for streaming: " + 
					streamInfoPlaylist[i].toString());
		}

		//        Log.d(PlayerControlClassName, "play this url now: " + videoUrl);
		//        Log.d(PlayerControlClassName, "try to play the streaming: " + streamInfoPlaylist.toString());
		if (videoPlayerActivity != null) {
			Log.d(TAG, "Going to finish videoPlayerActivity");
			videoPlayerActivity.finish();
			videoPlayerActivity = null;
		}
		
		if (isEmbeddedView) {
			videoPlayerFragment = new VideoPlayerFragment();
//			liveChannelFragment.setVideoPlayerFragment(videoPlayerFragment);
//			videoPlayerFragment.setLiveChannelFragment(liveChannelFragment);
			Bundle arguments = new Bundle();
			arguments.putString("channel", channelNo);
			arguments.putParcelableArray("streams", streamInfoPlaylist);
			arguments.putBoolean("isSlateVideo", false);
			arguments.putInt("selectedQuality", videoQualitySelection);
			arguments.putInt("videoType", VideoType.VideoTypeLive.ordinal());
			arguments.putString("classification", "");
			arguments.putInt("videoBookmarkSelection", 0);
			arguments.putString("pidvideoBookmarkSelection", "");
			arguments.putString("nowid", NowIDLoginStatus.getInstance().getNowID());
			arguments.putBoolean("calledFromFragment", true);
			
			videoPlayerFragment.setArguments(arguments);
			FragmentTransaction transaction = liveChannelFragment.getChildFragmentManager().beginTransaction();
			transaction.replace(R.id.videoPlayer, videoPlayerFragment);
			transaction.commit();	
		} else {
			Intent intent = new Intent(this.fragment.getActivity(), VideoPlayerActivity.class);
			//intent.putExtra("mode", VideoPlayerActivity.FORCE_NATIVE);
			//        intent.putExtra("urls", new String[] { videoUrl });
			intent.putExtra("channel", channelNo);
			intent.putExtra("streams", streamInfoPlaylist);
			intent.putExtra("isSlateVideo", !isMainVideoPlayed);
			intent.putExtra("selectedQuality", videoQualitySelection);
			intent.putExtra("videoType", videoType.ordinal());
			//intent.putExtra("classification", classification);
			intent.putExtra("videoBookmarkSelection", videoBookmarkSelection);
			intent.putExtra("pidvideoBookmarkSelection", this.productId);
			intent.putExtra("nowid", NowIDLoginStatus.getInstance().getNowID());
			
			intent.putExtra("productId", this.productId);
			intent.putExtra("programName", this.programName);
			intent.putExtra("channelLogoPath", this.channelLogoPath);
			intent.putExtra("hasChapter", this.hasChapter);
			
			this.fragment.startActivity(intent);
		}
	}

	public void terminateMoviePlayer() {
		Log.i(TAG, "terminate movie player and set to nil now");
		ProxyServerControl.getInstance().terminateToken();
		this.resetAllHandlerAndProperties();
	}

	public void setFragment(VideoPlayerFragment videoPlayerFragment) {
		this.fragment = videoPlayerFragment;
	}

	public void setVideoFragmentRunning(boolean videoFragmentRunning) {
		this.videoFragmentRunning = videoFragmentRunning;
		if (this.isMainVideoPlayed) {
			if (videoFragmentRunning) {
				ProxyServerControl.getInstance().resumeConcurrentControl();
			} else {
				ProxyServerControl.getInstance().stopAllTimer();
			}
		}
	}

	public void errorControl(String errorCode) {
		Log.i(TAG, "receive error code: " + errorCode);
		if (this.fragment instanceof VideoPlayerFragment) {
//			((VideoPlayerFragment) this.fragment).stopPlayback();
		} else if (this.videoPlayerFragment instanceof VideoPlayerFragment) {
//			videoPlayerFragment.stopPlayback();
		}
		MyAlertDialog dialog;
		if (ErrorCodeString.GeoCheckFail.equalsIgnoreCase(errorCode)) {
			dialog = MyAlertDialog.newInstance(-1, 
					LanguageHelper.getLocalizedString("error.alert.geo.block.title"), 
					LanguageHelper.getLocalizedString("error.alert.geo.block.message"), 
					LanguageHelper.getLocalizedString("error.alert.geo.block.quit"),
					LanguageHelper.getLocalizedString("alert.button.cancel"));
		} else {
			dialog = MyAlertDialog.newInstnace(errorCode);
		}
		dialog.setCallback(this);
		if (isEmbeddedView || this.fragment instanceof ProgramFragment || this.fragment instanceof SeriesFragment) {
			dialog.show(this.fragment.getFragmentManager(), errorCode);
		} else if (videoFragmentRunning) {
			dialog.show(this.fragment.getFragmentManager(), errorCode);
		} else if (this.fragment instanceof VideoPlayerFragment) {
//			((VideoPlayerFragment) this.fragment).setPendingErrorCode(errorCode);
		}
	}

	private Handler handler = new Handler();
	
	public void movieDuration(float duration) {
		Log.d(TAG, "got movie duration: " + duration);
		// remove listener to of app is entering to background

		if (this.isMainVideoPlayed) {
			if (this.videoType == VideoType.VideoTypeVod ||
				this.videoType == VideoType.VideoTypeVE ||
				this.videoType == VideoType.VideoTypeVESVOD) {
				Log.i(TAG, "vod max duration: " + duration + "millisecond.");
				handler.postDelayed(mExceedVodMaxDuration, (long)(duration * 1.5));
				
			}
		} else {
			// slate video
			// add timer to kill slate video if player has problem to play second time

			// add listener to trigger the app is entering to background 
		}

	}
	
	private Runnable mExceedVodMaxDuration = new Runnable() { 
		public void run() {
			Log.i(TAG, "exceed vod maximum duration limit");
			if (fragment!=null && fragment instanceof VideoPlayerFragment){
//				((VideoPlayerFragment) fragment).updateBookmark();
			}
			PlayerControlProxy.getInstance().errorControl(ErrorCodeString.VodIdleTooLong);
		}
	};

	public void movieIsEnded() {
		Log.i(TAG, "movie is ended reached, try to play next");
		this.playNext();
	}

	public void movieHasError(String movieplayererror) {
		MyAlertDialog alert = MyAlertDialog.newInstnace(movieplayererror);
		alert.setCallback(this);
		if (videoPlayerFragment != null) {
			alert.show(videoPlayerFragment.getFragmentManager(), null);
		} else if (fragment != null) {
			alert.show(fragment.getFragmentManager(), null);
		}
	}

	public void movieIsTerminated() {
		Log.i(TAG, "movie is terminated");
		ProxyServerControl.getInstance().terminateToken();
		resetAllHandlerAndProperties();
	}

	// MyAlertDialog
	@Override
	public void onClickOKButton(String tag) {
		if (ErrorCodeString.VodNormalEnd.equals(tag)) {
			this.terminateMoviePlayer();
		} else if (ErrorCodeString.Prompt3GEnableMessage.equals(tag)) {
			//Intent intent = new Intent(this.fragment.getActivity(), SettingActivity.class);
			//this.fragment.startActivity(intent);
			resetAllHandlerAndProperties();
		} else if (ErrorCodeString.GeoCheckFail.equals(tag)) {
			this.fragment.getActivity().finish();
			System.exit(0);
		} else {
			// Assume this is the Ok button for error dialog
			this.terminateMoviePlayer();
		}
	}

	// MyAlertDialog
	@Override
	public void onClickCancelButton(String tag) {
		// TODO Auto-generated method stub
		
	}

	// ProxyServerControl
	@Override
	public void proxyServerControlError(String errorCode) {
		Log.d(TAG, "proxy server request error: " + errorCode);
		this.errorControl(errorCode);
	}

	// ProxyServerControl
	@Override
	public void tokenIsValid() {
		Log.d(TAG, "token got play main video");
		this.playMainVideo();
	}

	// ProxyServerControl
	@Override
	public void didProxyTermToken(boolean success, String errorCode) {
		if (!success) {
			Log.d(TAG, "proxy terminate token with errorCode: " + errorCode);
		} else {
			Log.d(TAG, "proxy terminate token success");
		}
	}
	
	public void playMainVideo() {
		this.isMainVideoPlayed = true;
		this.currentPlayingIndex = 0;
		this.playVideo(this.currentPlayingIndex);
	}
	
	private void clearAllProperties() {
		isMainVideoPlayed = false;
		isEmbeddedView = false;
	}
	
	private void resetAllHandlerAndProperties() {
		ProxyServerControl.getInstance().killProxyControlInstance();
		if (videoPlayerActivity != null) {
			videoPlayerActivity.finish();
		}
		if (isEmbeddedView && liveChannelFragment != null && videoPlayerFragment != null) {
//			liveChannelFragment.setVideoPlayerFragment(null);
			liveChannelFragment.getChildFragmentManager().beginTransaction().remove(videoPlayerFragment).commit();
//			liveChannelFragment.resetLiveChannelViews();
		}
		clearAllProperties();
		
		//bug fix for 13834, need to reset all fragment when clear properties
		videoPlayerFragment = null;
		fragment = null;
		liveChannelFragment = null;
		
		handler.removeCallbacksAndMessages(null);
	}
}



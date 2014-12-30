package com.hua.nowplayerjunior.activity;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.hua.activity.R;
import com.hua.nowplayerjunior.constants.Constants;
import com.hua.nowplayerjunior.service.PlayerControlProxy;
import com.hua.nowplayerjunior.service.PlayerControlProxy.VideoType;
import com.lifevibes.lvmediaplayer.LVMediaPlayer;
import com.pccw.nmal.checkout.CheckoutFlowController;
import com.pccw.nmal.checkout.VODCheckout;
import com.pccw.nmal.model.StreamInfo;
import com.pccw.nmal.model.StreamInfo.StreamType;
import com.pccw.nmal.model.VOD;
import com.pccw.nmal.model.VOD.VODCategoryNodeData;
import com.pccw.nmal.model.VOD.VODData;
import com.pccw.nmal.service.PixelLogService;
import com.pccw.nmal.util.DownloadImage;
import com.pccw.nmal.util.ImageCache;

public class ProgramFragment extends UIEventBaseFragment {

	private static final String TAG = ProgramFragment.class.getSimpleName();
	private String nodeID; // original CategoryId
	private String episodeId;
	private String programName;
	private VODData lastEpisode;
	private ProgressDialog progressDialog;
	private boolean isDownloadingChannelLogo;
	
	static ProgramFragment newInstance(String CategoryID, String episodeId) {
		ProgramFragment f = new ProgramFragment();
		Bundle b = new Bundle();
		b.putString("CategoryID", CategoryID);
		b.putString("episodeId", episodeId);
		f.setArguments(b);
		return f;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
	super.onSaveInstanceState(outState);
	outState.putString("nodeID", nodeID);
	outState.putString("episodeId", episodeId);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Bundle b = savedInstanceState;
		if (savedInstanceState == null) {
			b = this.getArguments();
		}
		nodeID = b.getString("CategoryID");
		episodeId = b.getString("episodeId");
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.program_fragment, container, false);		
		return v;
	}
	
	private void setupView(View v) {
		//final OnDemandEpisode episode = OnDemand.getInstance().getCategoryById(CategoryID).getProductList().get(episodeId);
		final VODData vodData = VOD.getInstance().getVODDataByNodeId(nodeID).get(nodeID+episodeId);
		
		ImageView channelLogo = (ImageView) v.findViewById(R.id.channellogo);
		TextView titleText = (TextView) v.findViewById(R.id.titleText);
		TextView durationLabel = (TextView) v.findViewById(R.id.durationlabel);
		TextView duration = (TextView) v.findViewById(R.id.duration);
		TextView langlabel = (TextView) v.findViewById(R.id.langlabel);
		TextView lang = (TextView) v.findViewById(R.id.lang);
		TextView synosis = (TextView) v.findViewById(R.id.synosis);
		ImageButton playButton = (ImageButton) v.findViewById(R.id.playButton);
		
		programName = vodData.getEpisodeTitle();
		titleText.setText(programName);
		durationLabel.setText(getString(R.string.movie_program_duration_title));
		duration.setText(Integer.toString(vodData.getDuration()) + getString(R.string.movie_program_minute_text));
		langlabel.setText(getString(R.string.movie_program_language_title));
		lang.setText(vodData.getLanguages());
		synosis.setText(vodData.getWebSynopsis());
		
		String imageUrl = vodData.getWebImg1Path();
		channelLogo.setTag(imageUrl);
		Bitmap b = ImageCache.getInstance().get(imageUrl);
		Drawable seriesImage = b == null ? null : new BitmapDrawable(getResources(), b);
		
		if (seriesImage == null) {
			if (!isDownloadingChannelLogo) { 
				DownloadImage downloadImage = new DownloadImage(getActivity());
				downloadImage.executeWithThreadPool(channelLogo);
				isDownloadingChannelLogo = true;
			}
		} else {
			channelLogo.setImageDrawable(seriesImage);
		}
		
		playButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				lastProductId = vodData.getEpisodeId();
				lastProgramName = vodData.getEpisodeTitle();
				lastEpisode = vodData;
				startCheckout(lastEpisode);
			}
		});
		
		if (vodData.isHlsAssetStatus() && vodData.isWebAssetStatus()) {
			playButton.setEnabled(true);
		} else {
			playButton.setEnabled(false);
		}
	}
	
    @Override
    public void onResume() {
    	super.onResume();
    	String text = VOD.getInstance().getVODCategoryByNodeId(nodeID).getName();
//		((NowplayerJrActivity)getActivity()).enableBackButton(true, text);
    	setupView(getView());
    	closeProgressDialog();
    }
    
    @Override
    public void onDestroyView() {
    	super.onDestroyView();
    	closeProgressDialog();
    }

    /*
	@Override
	public void onNotBinded() {
		closeProgressDialog();

		if (NowIDLoginStatus.getInstance().isLoggedIn()) {
			Intent intent = new Intent(getActivity(), NowIDBindingActivity.class);
			intent.putExtra("lang", LanguageHelper.getCurrentLanguage());
			intent.putExtra("userAgent", Nmal.getWebViewUserAgent());
			startActivityForResult(intent, 0);
		} else {
			Intent intent = new Intent(getActivity(), NowIDLoginActivity.class);
			intent.putExtra("lang", LanguageHelper.getCurrentLanguage());
			intent.putExtra("userAgent", Nmal.getWebViewUserAgent());
			startActivityForResult(intent, 0);
		}
	}
	*/
    
	private void startCheckout(VODData episode) {
		CheckoutFlowController checkoutFlowController = new CheckoutFlowController(getActivity());
		checkoutFlowController.setCheckoutEventHandler(ProgramFragment.this);
		VODCheckout vodCheckout = new VODCheckout(episode, "", Constants.APP_INFO_APP_ID, StreamType.ADAPTIVE);
		checkoutFlowController.setCheckoutStepHandler(vodCheckout);
		checkoutFlowController.startCheckout();
		showProgressDialog();
	}
	
    @Override
	protected void afterQualityAndBookmarkSelected(List<List<StreamInfo>> playlist, int quality, int bookmark) {
		if (quality > 0) {
			StreamInfo[] streamInfoPlaylist = new StreamInfo[playlist.size()];

			for (int i=0; i< playlist.size(); i++) {
				streamInfoPlaylist[i] = playlist.get(i).get(0);
				Log.d(TAG, "add this item to extra for streaming: " + 
						streamInfoPlaylist[i].toString());
			}
			
			/*
			Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
			//arguments.putString("channel", );
			intent.putExtra("streams", streamInfoPlaylist);
			intent.putExtra("isSlateVideo", false);
			intent.putExtra("selectedQuality", quality);
			intent.putExtra("videoType", VideoType.VideoTypeVod.ordinal());
			intent.putExtra("classification", "");
			intent.putExtra("videoBookmarkSelection", bookmark);
			intent.putExtra("pidvideoBookmarkSelection", lastProductId);
			intent.putExtra("nowid", NowIDLoginStatus.getInstance().getNowID());
			intent.putExtra("calledFromFragment", false);

			startActivity(intent);
			*/
			VODCategoryNodeData odc = VOD.getInstance().getVODCategoryByNodeId(nodeID); // OnDemand.getInstance().getCategoryById(CategoryID);

			PlayerControlProxy pcp = PlayerControlProxy.getInstance();
			pcp.setIsEmbeddedView(false);
			pcp.terminateMoviePlayer();
			pcp.setFragment(this);
			pcp.setPlaylists(playlist);
			pcp.setChannelNo(null);
			pcp.setChannelLogoPath(odc.getCategoryImagePath());
			pcp.setProgramName(programName);
			pcp.setVideoQualitySelection(quality);
			pcp.setVideoType(VideoType.VideoTypeVod);
			pcp.setVideoBookmarkSelection(bookmark);
			pcp.setProductId(lastProductId);
			pcp.setHasChapter(false);
			pcp.setServiceId(lastServiceId);
			
			pcp.playNext();
			
			new PixelLogService(Constants.PIXEL_LOG_URL, Constants.PIXEL_LOG_APP_NAME, 
					LVMediaPlayer.getUniqueIdentifier(getActivity())).pixelLogAppRunNxp();
			
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			startCheckout(lastEpisode);
		}
	}

}

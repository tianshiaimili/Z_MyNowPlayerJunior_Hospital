package com.hua.nowplayerjunior.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hua.activity.R;
import com.hua.nowplayerjunior.constants.Constants;
import com.hua.nowplayerjunior.service.PlayerControlProxy;
import com.hua.nowplayerjunior.service.PlayerControlProxy.VideoType;
import com.pccw.nmal.checkout.CheckoutFlowController;
import com.pccw.nmal.checkout.VODCheckout;
import com.pccw.nmal.model.StreamInfo;
import com.pccw.nmal.model.StreamInfo.StreamType;
import com.pccw.nmal.model.VOD;
import com.pccw.nmal.model.VOD.VODCategoryNodeData;
import com.pccw.nmal.model.VOD.VODData;
import com.pccw.nmal.util.DownloadImage;
import com.pccw.nmal.util.ImageCache;

public class SeriesFragment extends UIEventBaseFragment {

	private static final String TAG = SeriesFragment.class.getSimpleName();
	private View thisView;
	private String CategoryID;
	private String seriesId;
	private VODCategoryNodeData series;
	private List<VODData> episodeList;
	private ImageView channellogo;
	private TextView programTitle;
	private TextView content;
	private TextView actorandhost_title;
	private TextView actor;
	private ListView listView;
	private boolean isDownloadingSeriesImage;
	private String programName;
	private VODData lastEpisode;
	
	public static SeriesFragment newInstance(String CategoryID, String seriesId) {
		SeriesFragment f = new SeriesFragment();
		Bundle args = new Bundle();
        args.putString("CategoryID", CategoryID);
        args.putString("seriesId", seriesId);
        f.setArguments(args);
		return f;
	}
	
	public SeriesFragment() {
		
	}
	
	private void initArguments(Bundle savedInstanceState) {
    	Bundle b = savedInstanceState;
    	if (b == null)
    	   b = this.getArguments();
    	
    	if (b != null) {
    		this.CategoryID = b.getString("CategoryID"); // hardcode 5 works fine (in ASM conversion)
    		this.seriesId = b.getString("seriesId");
    		this.series =  VOD.getInstance().getVODCategoryByNodeId(seriesId); 
    		this.episodeList = new ArrayList<VODData>(VOD.getInstance().getVODDataByNodeId(seriesId).values());
    	}
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		initArguments(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		thisView = inflater.inflate(R.layout.series_fragment, container, false);
		setupView();
		return thisView;
	}


	public void setupView() {
		channellogo = (ImageView) thisView.findViewById(R.id.channellogo);
		programTitle = (TextView) thisView.findViewById(R.id.programTitle);
		content = (TextView) thisView.findViewById(R.id.content);
		listView = (ListView) thisView.findViewById(R.id.listView01);
		
		String imageUrl = series.getHdImg1Path();
		channellogo.setTag(imageUrl);
		Bitmap b = ImageCache.getInstance().get(imageUrl);
		Drawable seriesImage = b == null ? null : new BitmapDrawable(getResources(), b);
		
		if (seriesImage == null) {
			if (!isDownloadingSeriesImage) { 
				DownloadImage downloadImage = new DownloadImage(getActivity());
				downloadImage.executeWithThreadPool(channellogo);
				isDownloadingSeriesImage = true;
			}
		} else {
			channellogo.setImageDrawable(seriesImage);
		}
		
		programTitle.setText(series.getName());
		content.setText(series.getSynopsis());
		
		List<VODData> episodeListToShow = new ArrayList<VODData>();
		for (VODData episode : episodeList) {
			if (!((!episode.isHlsAssetStatus() || !episode.isWebAssetStatus()) && episode.isDisplayWhenAssetReady())) {
				episodeListToShow.add(episode);
			}
		}
		listView.setAdapter(new EpisodeListAdapter(getActivity(), R.layout.series_fragment, episodeListToShow));
	}
	
	@Override
	public void onResume() {
		super.onResume();
		String text = VOD.getInstance().getVODCategoryByNodeId(CategoryID).getName();
		((MainActivity)getActivity()).enableBackButton(true, text);
	}
	
	private class EpisodeListAdapter extends ArrayAdapter<VODData> {

		private List<VODData> mList;
		
		
		public EpisodeListAdapter(Context context, int resource, List<VODData> objects) {
			super(context, resource, objects);
			
			mList = objects;
		}
		
    	@Override
    	public View getView(final int position, View convertView, ViewGroup parent){
    		
            View view;
    		if(convertView == null){
    			view = getActivity().getLayoutInflater().inflate(R.layout.series_fragment_row, null);
    		}
    		else{
    			view = convertView;
    		}

    		TextView episodeName = (TextView) view.findViewById(R.id.episodeName);
    		ImageButton playButton = (ImageButton) view.findViewById(R.id.playButton);
    		

    		if (mList.get(position).isHlsAssetStatus() && mList.get(position).isWebAssetStatus()) {
    			playButton.setEnabled(true);
    			final VODData episode = mList.get(position);
    			playButton.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						programName = episode.getEpisodeTitle();
						lastEpisode = episode;
						lastProductId = episode.getEpisodeId();
						lastProgramName = episode.getEpisodeTitle();
						startCheckout(lastEpisode);
					}
				});
        		episodeName.setText(mList.get(position).getEpisodeName());
    		} else {
    			playButton.setEnabled(false);
        		episodeName.setText(mList.get(position).getEpisodeName() + getString(R.string.ve_details_svod_coming_soon));
    		}
    		
    		return view;
    	}
	}

	@Override
	protected void afterQualityAndBookmarkSelected(
			List<List<StreamInfo>> playlist, int quality, int bookmark) {
		if (quality > 0) {
			StreamInfo[] streamInfoPlaylist = new StreamInfo[playlist.size()];

			for (int i=0; i< playlist.size(); i++) {
				streamInfoPlaylist[i] = playlist.get(i).get(0);
				Log.d(TAG, "add this item to extra for streaming: " + 
						streamInfoPlaylist[i].toString());
			}
			
			VODCategoryNodeData odc = VOD.getInstance().getVODCategoryByNodeId(seriesId);
			
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
		}
	}
	

	private void startCheckout(VODData episode) {
		CheckoutFlowController checkoutFlowController = new CheckoutFlowController(getActivity());
		checkoutFlowController.setCheckoutEventHandler(SeriesFragment.this);
		VODCheckout onDemandCheckout = new VODCheckout(episode, "", Constants.APP_INFO_APP_ID,StreamType.ADAPTIVE);
		checkoutFlowController.setCheckoutStepHandler(onDemandCheckout);
		checkoutFlowController.startCheckout();
		showProgressDialog();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			startCheckout(lastEpisode);
		}
	}
}

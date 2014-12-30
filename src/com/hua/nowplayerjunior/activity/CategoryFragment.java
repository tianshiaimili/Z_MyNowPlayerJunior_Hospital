package com.hua.nowplayerjunior.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hua.activity.R;
import com.hua.gz.utils.DisplayUtils;
import com.hua.nowplayerjunior.constants.Constants;
import com.hua.nowplayerjunior.utils.LogUtils2;
import com.pccw.nmal.appdata.AppInfo;
import com.pccw.nmal.appdata.JsonZip;
import com.pccw.nmal.appdata.JsonZip.ZipType;
import com.pccw.nmal.model.VOD;
import com.pccw.nmal.model.VOD.VODCategoryNodeData;
import com.pccw.nmal.model.VOD.VODData;
import com.pccw.nmal.util.DownloadImage;
import com.pccw.nmal.util.ImageCache;
import com.pccw.nmal.util.LanguageHelper;


public class CategoryFragment extends Fragment{

	private String nodeID; //original CategoryId
	//private HashMap<String, DownloadCategoryImage> downloadImageList;
	private HashMap<String, DownloadImage> downloadImageList = new HashMap<String, DownloadImage>();
	private View view;
	private ProgressDialog progressDialog;
//	private CategoryListAdapterP categoryListAdapterP;
//	private CategoryListAdapterS categoryListAdapterS;
	private CategoryListAdapter categoryListAdapter;
	private RootCategoryFragment parent;
	
	private TextView  categoryTextView;
	private boolean isShowTextView;
	
	
	public static CategoryFragment newInstance(String CategoryID, RootCategoryFragment parent)
	{
		CategoryFragment f = new CategoryFragment();
        Bundle args = new Bundle();
        args.putString("CategoryID", CategoryID);
        f.setArguments(args);
        f.parent = parent;
        return f;
	}
	
	public void setupCategoryView() {  
        
		if(!isShowTextView){
			categoryTextView.setVisibility(View.VISIBLE);
			return;
		}
		
		JsonZip jsonZip = new JsonZip(this.getActivity(), AppInfo.getJsonVersionPath(), Constants.JSON_ZIP_VERSION_PREFIX, LanguageHelper.getCurrentLanguage());
		if (jsonZip.shouldUpdateJSONZipVersion(ZipType.PKG) && !jsonZip.isDownloading()) {
			showProgressDialog();
			VOD.getInstance().clearVodChannelList();
			jsonZip.startDownload(ZipType.PKG, callback);
		}else {
			LogUtils2.e("setupCategoryView ----------");
			if ((!VOD.getInstance().isRootCategoryListCompleted()) || (!VOD.getInstance().isVODDataListCompleted())){
				parseRootCategoryJSON();
				parseVODDatalJSON();
			}

			Map<String, VODCategoryNodeData> CategoryListS =  VOD.getInstance().getVODCategoryByNodeId(nodeID).childNodes;
			Map<String, VODData> CategoryListP = VOD.getInstance().getVODDataByNodeId(nodeID);
			
			List<Object> mList  = new ArrayList <Object>(); // don't change add all order
			mList.addAll(new ArrayList<VODCategoryNodeData>(CategoryListS.values())); 
			mList.addAll(new ArrayList<VODData>(CategoryListP.values())); 
			
			GridView gridview = (GridView) view.findViewById(R.id.categorygridview);
			CategoryListAdapter categoryListAdapter = new CategoryListAdapter (getActivity(), R.layout.category_fragment ,mList);
			gridview.setAdapter(categoryListAdapter);
			
		}

	}
	
	public class CategoryListAdapter extends ArrayAdapter<Object>{
		List<Object> mList  = new ArrayList <Object>();
		LayoutInflater mInflater;
		int mResource;
		private Context mContext;

		private int mPosterWidth = 0;
		private int mPosterHeight = 0;

		public CategoryListAdapter(Context context, int resource, List<Object> arrayList){
			super(context, resource, arrayList);

			mResource = resource;
			mInflater = getActivity().getLayoutInflater();
			mContext = context;
			mList = arrayList;			
		}

		
		@Override
		public int getCount() {
		    return mList.size();
		}
		
		@Override
    	public View getView(final int position, View convertView, ViewGroup parent){
    		
            View view;
    		if(convertView == null){
    			view = mInflater.inflate(R.layout.icon, null);
    		}
    		else{
    			view = convertView;
    		}

    		//------in order to fix the scrolling will have padding style problem.
			boolean isLastOne = getCount()>=3&&(position==(getCount()-1));
			if(position==0||position ==1 ){
				view.setPadding(0, DisplayUtils.dip2px(getActivity(), 10), 0, 0);
			}else if(isLastOne){
				
				int topPadding = DisplayUtils.dip2px(getActivity(), 10);
				int bottomPadding = DisplayUtils.dip2px(getActivity(), 65);//should larger than the tab bar height dp value 64;
				view.setPadding(0, topPadding, 0, bottomPadding);
			}
			else{
				view.setPadding(0, DisplayUtils.dip2px(getActivity(), 10), 0, 0);
			}
			//---------------end--------------
    		
			final Object obj =  mList.get(position);
			if (obj instanceof VODData) {
				ImageButton imageView = (ImageButton)view.findViewById(R.id.icon_image);
				String downloadUrl = ((VODData) obj).getWebImg1Path();
				imageView.setTag(downloadUrl);

				Bitmap b = ImageCache.getInstance().get(downloadUrl);
				Drawable seriesImage = b == null ? null : new BitmapDrawable(getResources(), b);

				if(seriesImage == null){
					imageView.setImageDrawable(null);
					if (!downloadImageList.containsKey(downloadUrl)) { 
						DownloadCategoryImage downloadImage = new DownloadCategoryImage((VODData)obj, 200, 200);
						downloadImage.executeWithThreadPool(imageView);
						downloadImageList.put(downloadUrl, downloadImage);
					}
				}
				else{
					imageView.setImageDrawable(seriesImage);
				}
				imageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						CategoryFragment.this.parent.openNextFragment("P", nodeID, ((VODData) obj).getEpisodeId());
					}
				});
				TextView seriesTitleText = (TextView)view.findViewById(R.id.icon_text);
				seriesTitleText.setText(((VODData) obj).getEpisodeTitle());
				return view;
			} else {
				ImageButton imageView = (ImageButton)view.findViewById(R.id.icon_image);
				String downloadUrl = ((VODCategoryNodeData) obj).getHdImg1Path();
				imageView.setTag(downloadUrl);

				Bitmap b = ImageCache.getInstance().get(downloadUrl);
				Drawable seriesImage = b == null ? null : new BitmapDrawable(getResources(), b);

				if(seriesImage == null){
					imageView.setImageDrawable(null);
					if (!downloadImageList.containsKey(downloadUrl)) { 
						DownloadCategoryImageS downloadImage = new DownloadCategoryImageS(((VODCategoryNodeData) obj), 200, 200);
						downloadImage.executeWithThreadPool(imageView);
						downloadImageList.put(downloadUrl, downloadImage);
					}
				} else {
					imageView.setImageDrawable(seriesImage);
				}
				imageView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						CategoryFragment.this.parent.openNextFragment("S", nodeID, ((VODCategoryNodeData) obj).getNodeId()); 
					}
				});

				TextView seriesTitleText = (TextView)view.findViewById(R.id.icon_text);
				seriesTitleText.setText(((VODCategoryNodeData) obj).getName());
				return view;
			}
    	}

	}
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }	
	
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = ProgressDialog.show(getActivity(), 
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
	        
    private void initArguments(Bundle savedInstanceState)
    {
    	Bundle b = savedInstanceState;
    	if (b == null)
    	   b = this.getArguments();
    	
    	if (b != null) {
    		this.nodeID = b.getString("CategoryID");
    	}
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("CategoryID", this.nodeID);
    }
 
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		inflater = getActivity().getLayoutInflater();
		initArguments(savedInstanceState);
		view = inflater.inflate(R.layout.category_fragment, container, false);
		categoryTextView = (TextView) view.findViewById(R.id.category_textview);
    	setupCategoryView();
		return view;
	}
 
	private class DownloadCategoryImage extends DownloadImage {
		public VODData categoryData;
		public DownloadCategoryImage(VODData vodData) {
			super(getActivity());
			categoryData = vodData;
		}
		
		public DownloadCategoryImage(VODData vodData, int reqW, int reqH) {
			super(getActivity(), reqW, reqH);
			categoryData = vodData;
		}

		@Override
		protected Drawable doInBackground(Object... params) {
			return super.doInBackground(params);
		}

		@Override
		protected void onPostExecute(Drawable result) {
			super.onPostExecute(result);
			downloadImageList.remove(categoryData.getWebImg1Path());
		}
	}
	
	private class DownloadCategoryImageS extends DownloadImage {
		public VODCategoryNodeData categoryData;
		public DownloadCategoryImageS(VODCategoryNodeData data) {
			super(getActivity());
			categoryData = data;
		}
		
		public DownloadCategoryImageS(VODCategoryNodeData data, int reqW, int reqH) {
			super(getActivity(), reqW, reqH);
			categoryData = data;
		}

		@Override
		protected Drawable doInBackground(Object... params) {
			return super.doInBackground(params);
		}

		@Override
		protected void onPostExecute(Drawable result) {
			super.onPostExecute(result);
			downloadImageList.remove(categoryData.getHdImg1Path());
		}

	}
	
    @Override
    public void onResume(){
    	super.onResume();
    	setupCategoryView();
    }
    
   
    @Override
    public void onDestroyView() {
    	super.onDestroyView();
    	
		if (downloadImageList != null) {
			downloadImageList.clear();
		}
		
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }

    }
    
    
	private JCB callback = new JCB();
	private class JCB implements JsonZip.Callback {

		@Override
		public void updateProgress(int precent) {
		}

		@Override
		public void onDownloadCompleted(ZipType zipType, boolean isOK) {

			if (isOK) {
				if (parseRootCategoryJSON() && parseVODDatalJSON()) {
					closeProgressDialog();
					setupCategoryView();
				} else {
					closeProgressDialog();
				}
			}
		}
	}
	
	private boolean parseRootCategoryJSON() {
		JsonZip jsonZip = new JsonZip(this.getActivity(), AppInfo.getJsonVersionPath(), Constants.JSON_ZIP_VERSION_PREFIX, LanguageHelper.getCurrentLanguage());
		String getOnDemandRootCategory = jsonZip.getJSONData(ZipType.PKG, "vodCatalog.json");
		return (VOD.getInstance().parseVODCatergories(getOnDemandRootCategory));

	}


	private boolean parseVODDatalJSON() {
		JsonZip jsonZip = new JsonZip(this.getActivity(), AppInfo.getJsonVersionPath(), Constants.JSON_ZIP_VERSION_PREFIX, LanguageHelper.getCurrentLanguage());
		String getOnDemandCategory = jsonZip.getJSONData(ZipType.PKG, "vodDetail.json");
		return (VOD.getInstance().parseVODDetails(getOnDemandCategory));

	}
    
}

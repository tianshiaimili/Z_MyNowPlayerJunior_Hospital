package com.hua.nowplayerjunior.activity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hua.activity.R;
import com.hua.nowplayerjunior.adapter.PagerAdapterFr;
import com.hua.nowplayerjunior.constants.Constants;
import com.hua.nowplayerjunior.utils.LogUtils2;
import com.pccw.nmal.appdata.AppInfo;
import com.pccw.nmal.appdata.JsonZip;
import com.pccw.nmal.appdata.JsonZip.ZipType;
import com.pccw.nmal.model.OnDemand.OnDemandCategory;
import com.pccw.nmal.model.VOD;
import com.pccw.nmal.model.VOD.VODCategoryNodeData;
import com.pccw.nmal.util.LanguageHelper;

/**
 * 这是自选节目fragment
 * @author zero
 *
 */
public class RootCategoryFragment extends Fragment{

	private View view;
	private ProgressDialog progressDialog;
    private PagerAdapter mPagerAdapter;
    private Fragment[] CategoryFragmentArray = new Fragment[20];
    private String[] CategoryFragmentName = new String[20];
    
    public RootCategoryFragment() {}
        
    public class  RootCategoryListAdapter extends ArrayAdapter<OnDemandCategory>{
    	
    	ArrayList<OnDemandCategory> mList;
    	LayoutInflater mInflater;
    	int mResource;
    	
    	public  RootCategoryListAdapter(Context context, int resource, ArrayList<OnDemandCategory> list){
    		super(context, resource, list);
    		
    		mResource = resource;
    		mInflater = getActivity().getLayoutInflater();
    		mList = list;
    	}
    	
    	@Override
    	public View getView(int position, View convertView, ViewGroup parent){
    		View view;
    		
    		if(convertView == null){
    			view = mInflater.inflate(mResource, null);
    		}
    		else{
    			view = convertView;
    		}

    		TextView textView = (TextView)view.findViewById(R.id.pager_title_strip);
    		textView.setText(mList.get(position).getCategoryName());
    		
    		return view;
    	}
    }
	
	
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = ProgressDialog.show(getActivity(), 
					LanguageHelper.getLocalizedString("progress.dialog.title"), 
					LanguageHelper.getLocalizedString("progress.dialog.message"));
			progressDialog.setCancelable(false);
			progressDialog.setOnKeyListener(new OnKeyListener() {
				
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

					if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK){
						progressDialog.dismiss();
					}
					
					return false;
				}
			});
		}
	}

	private void closeProgressDialog() {
		if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
	}
		
    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View view = inflater.inflate(R.layout.root_category_fragment, null);
    	this.view = view;
    	//this.initialisePaging();
		return view;
	}
    
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}
    
    private void initialisePaging() {
    	
    	String rootcatId = VOD.getInstance().getVODRootNodeID();
    	VODCategoryNodeData rootCategory = VOD.getInstance().getVODCategoryByNodeId(rootcatId);
    	LogUtils2.e("rootCategory=="+rootCategory);
    	if (rootCategory != null) {
    		//LinkedHashMap<String, VODCategoryNodeData> maincategoryList = rootCategory.childNodes;
    		List<VODCategoryNodeData> RootCategoryList = new ArrayList<VODCategoryNodeData>(rootCategory.childNodes.values());
    		LogUtils2.e("RootCategoryList=="+RootCategoryList.size());
    		List<String> categoryIdList = new ArrayList<String>(RootCategoryList.size());

    		for (int i = 0; i < RootCategoryList.size(); i++) {
    			CategoryFragmentName[i] = RootCategoryList.get(i).getName();
    			categoryIdList.add(RootCategoryList.get(i).getNodeId());
    		}
    		this.mPagerAdapter  = new PagerAdapterFr(getChildFragmentManager(), categoryIdList, CategoryFragmentName, this);
    		final ViewPager pager = (ViewPager)view.findViewById(R.id.viewpager);
    		pager.setAdapter(this.mPagerAdapter);
    	}
    	
    	if(rootCategory == null){
    		int lenght = 3;
    		String [] tempContent = {"title1","title2","title3"};
    		String [] tempID = {"id1","id2","id3"};
    		List<String> categoryIdList = new ArrayList<String>(lenght);

    		for (int i = 0; i < lenght; i++) {
    			CategoryFragmentName[i] = tempContent[i];
    			categoryIdList.add(tempID[i]);
    		}
    		this.mPagerAdapter  = new PagerAdapterFr(getChildFragmentManager(), categoryIdList, CategoryFragmentName, this);
    		final ViewPager pager = (ViewPager)view.findViewById(R.id.viewpager);
    		pager.setAdapter(this.mPagerAdapter);
    		
    	}
    	
    	
    	
    }
    
    @Override
    public void onResume(){
		super.onResume();
		((MainActivity)getActivity()).enableBackButton(false);
		((MainActivity)getActivity()).showTitleLogo();
		JsonZip jsonZip = new JsonZip(this.getActivity(), AppInfo.getJsonVersionPath(), Constants.JSON_ZIP_VERSION_PREFIX, LanguageHelper.getCurrentLanguage());
		String getvodCatalog = jsonZip.getJSONData(ZipType.PKG, "vodCatalog.json");

		if (jsonZip.shouldUpdateJSONZipVersion(ZipType.PKG) && !jsonZip.isDownloading()) {
//			showProgressDialog();
			jsonZip.startDownload(ZipType.PKG, callback);
			
		}else {
			if (getvodCatalog != null) {
				VOD.getInstance().parseVODCatergories(getvodCatalog);
			}
			
	        if (progressDialog != null) {
	            progressDialog.dismiss();
	            progressDialog = null;
	        }
			
		}
		initialisePaging();
    }
       
    @Override
    public void onDestroyView() {
    	super.onDestroyView();
    	
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
				if (parseVODCategoriesJSON()) {
					closeProgressDialog();
					initialisePaging();
				} else {
					closeProgressDialog();
				}
			}
		}
	}
    
	private boolean parseVODCategoriesJSON() {
		JsonZip jsonZip = new JsonZip(this.getActivity(), AppInfo.getJsonVersionPath(), Constants.JSON_ZIP_VERSION_PREFIX, LanguageHelper.getCurrentLanguage());
		String getVodCatalog = jsonZip.getJSONData(ZipType.PKG,  "vodCatalog.json"); // deleted LanguageHelper.getCurrentLanguage() +
		return (VOD.getInstance().parseVODCatergories(getVodCatalog));
	}

	public void openNextFragment(String type, String arg1, String arg2) {
		Fragment newFragment;
		if ("P".equals(type)) {
			newFragment = ProgramFragment.newInstance(arg1, arg2);
		} else if ("S".equals(type)) {
			newFragment = SeriesFragment.newInstance(arg1, arg2);
		} else {
			return;
		}
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, newFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
	}
	
	/**
	 * Workaround for java.lang.IllegalStateException: No activity bug
	 * See http://stackoverflow.com/questions/15207305/
	 */
	@Override
	public void onPause() {
	    super.onPause();

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
	
	
}


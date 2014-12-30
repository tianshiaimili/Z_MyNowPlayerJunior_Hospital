package com.hua.nowplayerjunior.interactf;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.hua.activity.R;
import com.hua.nowplayerjunior.activity.MainActivity;
import com.hua.nowplayerjunior.activity.UIEventBaseFragment;
import com.hua.nowplayerjunior.constants.Constants;
import com.hua.nowplayerjunior.utils.MyAlertDialog;
import com.lifevibes.lvmediaplayer.LVMediaPlayer;
import com.pccw.nmal.appdata.AppInfo;
import com.pccw.nmal.checkout.CheckoutFlowController;
import com.pccw.nmal.checkout.SpecialFeatureCheckout;
import com.pccw.nmal.model.StreamInfo;
import com.pccw.nmal.service.PixelLogService;
import com.pccw.nmal.util.LanguageHelper;
import com.pccw.nowid.NowIDLoginStatus;

public class InteractiveFragment extends UIEventBaseFragment {

	private enum gameChoice {DO_YOU_KNOW, PATRICK_N_FRIEND, WATCH_N_LEARN,OLIVE_SING_SONG}
	private String[] titles;
	private int[] iconIds = new int[] { R.drawable.interactive_doyouknow,
			R.drawable.interactive_patricknfriends,R.drawable.interactive_eyecards
			,R.drawable.interactive_olivecard_header};
	protected gameChoice CURRENT_CHOICE;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.interactive, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		titles = new String[] {getResources().getString(R.string.do_you_know_menu_title),
					getResources().getString(R.string.patrick_n_friends_title),
					getResources().getString(R.string.interactive_watch_n_learn_list_title),
					getResources().getString(R.string.interactive_sing_with_olive_title)};
		loadListView();
	}

	@Override
	public void onResume() {
		super.onResume();
		((MainActivity) getActivity()).enableBackButton(false);
		((MainActivity) getActivity()).showTitleLogo();
	}

	public void loadListView() {
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < titles.length; i++) {
			Map<String, Object> itemMap = new HashMap<String, Object>();
			itemMap.put("rowIcon", iconIds[i]);
			itemMap.put("rowTitle", titles[i]);
			listItems.add(itemMap);
		}
		SimpleAdapter simpleAdapter = new SimpleAdapter(this.getActivity(),
				listItems, R.layout.list_item, new String[] { "rowTitle",
						"rowIcon" }, new int[] { R.id.rowTitle, R.id.rowIcon });
		ListView listView = (ListView) getActivity()
				.findViewById(R.id.menuList);
		listView.setAdapter(simpleAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				PixelLogService pixelLog = new PixelLogService(Constants.PIXEL_LOG_URL, Constants.PIXEL_LOG_APP_NAME, LVMediaPlayer.getUniqueIdentifier(getActivity()), NowIDLoginStatus.getInstance().getNowID(), NowIDLoginStatus.getInstance().getFsa());
				String fragmentTag;
				Bundle bun;
				switch (position) {
				case 0:
//					ft.replace(R.id.fragment_container,
//							new DoYouKnowMenuFragment(), "DO_YOU_KNOW_MENU");
//					ft.addToBackStack(null);
//					ft.commit();
					CURRENT_CHOICE = gameChoice.DO_YOU_KNOW;
					beginCheckoutSpecialFeatures("L00860");					
					
					break;
				case 1:
//					PatrickAndFriendsFragment dFragment = new PatrickAndFriendsFragment();
//					String fragmentTag = PatrickAndFriendsFragment.class.getSimpleName();
//					Bundle bun = new Bundle();
//					bun.putString("CHOICE_INTERACTIVE",fragmentTag);
//					dFragment.setArguments(bun);
//					ft.replace(R.id.fragment_container,
//							dFragment);
//					ft.addToBackStack(null);
//					ft.commit();
					CURRENT_CHOICE = gameChoice.PATRICK_N_FRIEND;
					beginCheckoutSpecialFeatures("L00854");
					break;
				case 2:
//					if(!Constants.ENABLE_WATCH_AND_LEARN_CHECKOUT){
//						WatchAndLearnCategoryFragment interaFragment = new WatchAndLearnCategoryFragment();
//						fragmentTag = WatchAndLearnCategoryFragment.class.getSimpleName();
//					    bun = new Bundle();
//						bun.putString("CHOICE_INTERACTIVE", fragmentTag);
//						interaFragment.setArguments(bun);
//						com.pccw.gz.app.FragmentUtils.replace(getActivity(),interaFragment,
//								R.id.fragment_container, fragmentTag, null);
//					}else{
//						CURRENT_CHOICE = gameChoice.WATCH_N_LEARN;
//						beginCheckoutSpecialFeatures("L00859");
//						
//					}
					break;
				case 3:
//					OliveSingSongFragment fragment=new OliveSingSongFragment();
//					fragmentTag=OliveSingSongFragment.class.getSimpleName();
//					com.pccw.gz.app.FragmentUtils.replace(getActivity(),fragment,
//							R.id.fragment_container, fragmentTag, null);
					
					CURRENT_CHOICE=gameChoice.OLIVE_SING_SONG;
					beginCheckoutSpecialFeatures("L00863");
					
				break;	
					
				}
			}
		});
	}
	
	private void doSuccessWork() {
//		String pnfTag = PatrickAndFriendsFragment.class.getSimpleName();
//		String wnlTag = WatchAndLearnCategoryFragment.class.getSimpleName();
//		String dykTag = DoYouKnowMenuFragment.class.getSimpleName();
//		String ossTag=OliveSingSongFragment.class.getSimpleName();
//		PixelLogService pixelLog = new PixelLogService(Constants.PIXEL_LOG_URL, Constants.PIXEL_LOG_APP_NAME, LVMediaPlayer.getUniqueIdentifier(getActivity()), NowIDLoginStatus.getInstance().getNowID(), NowIDLoginStatus.getInstance().getFsa());
//		if(CURRENT_CHOICE==gameChoice.PATRICK_N_FRIEND){
//			pixelLog.pixelLogAction(PixelLogService.PIXELLOG_INTERACTIVE_PATRICK_CARD , "", "");
//			com.pccw.gz.app.FragmentUtils.replace(getActivity(), 
//					new PatrickAndFriendsFragment(), R.id.fragment_container, pnfTag,pnfTag);
//		}else if(CURRENT_CHOICE == gameChoice.WATCH_N_LEARN){
//			pixelLog.pixelLogAction(PixelLogService.PIXELLOG_GAMES_WNL_CARD , "", "");
//			com.pccw.gz.app.FragmentUtils.replace(getActivity(), 
//					new WatchAndLearnCategoryFragment(), R.id.fragment_container, wnlTag,wnlTag);
//			
//		}else if(CURRENT_CHOICE == gameChoice.DO_YOU_KNOW) {
//			pixelLog.pixelLogAction(PixelLogService.PIXELLOG_INTERACTIVE_KNOWLEDGECARD  , "", "");
//			com.pccw.gz.app.FragmentUtils.replace(getActivity(), 
//					new DoYouKnowMenuFragment(), R.id.fragment_container, dykTag,dykTag);		
//		
//		}else if(CURRENT_CHOICE==gameChoice.OLIVE_SING_SONG){
//			pixelLog.pixelLogAction(PixelLogService.PIXELLOG_OLIVE_SING_SONG, "", "");
//			com.pccw.gz.app.FragmentUtils.replace(getActivity(),new OliveSingSongFragment(),
//					R.id.fragment_container, ossTag, ossTag);
//		}
	}
	@Override
	public void onSuccess() {
		closeProgressDialog();
		doSuccessWork();
	}
	
	

//	@Override
//	public void onNotLoggedIn() {
//		closeProgressDialog();
////		Intent intent = new Intent(getActivity(), NowIDLoginActivity.class);
////		intent.putExtra("lang", LanguageHelper.getCurrentLanguage());
////		intent.putExtra("userAgent", Nmal.getWebViewUserAgent());
////		startActivityForResult(intent, 0);
//		
//		FragmentTransaction ft = getFragmentManager().beginTransaction();
//		NowIDLoginFragment loginFragment = new NowIDLoginFragment();
//		loginFragment.setCallerFragment(this);
//		ft.replace(R.id.fragment_container, loginFragment);
//		ft.addToBackStack(null);
//		ft.commit();
//	}

	@Override
	public void onNeedSubscription() {
		closeProgressDialog();
		MyAlertDialog alert = MyAlertDialog.newInstance(-1, 
				LanguageHelper.getLocalizedString("alert.subscribe.specialFeature.title"), 
				LanguageHelper.getLocalizedString("alert.subscribe.specialFeature.message"), 
				LanguageHelper.getLocalizedString("alert.button.call"),
				LanguageHelper.getLocalizedString("alert.button.cancel"));
		alert.setCallback(this);
		alert.show(getFragmentManager(), "aSubscribe");
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			beginCheckoutSpecialFeatures("L00854");
		}else{
			//getActivity().onBackPressed();
		}
	}
	private void beginCheckoutSpecialFeatures(String libraryID) {
		CheckoutFlowController cfc = new CheckoutFlowController(getActivity());
		showProgressDialog();
		SpecialFeatureCheckout sfc = new SpecialFeatureCheckout(libraryID, 
				AppInfo.getAppId());
		cfc.setCheckoutStepHandler(sfc);
		cfc.setCheckoutEventHandler(this);
		cfc.startCheckout();
	}
	@Override
	protected void afterQualityAndBookmarkSelected(
			List<List<StreamInfo>> playlist, int quality, int bookmark) {
		
	}

}

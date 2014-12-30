package com.hua.nowplayerjunior.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import cn.pedant.SweetAlert.widget.SweetAlertDialog;
import cn.pedant.SweetAlert.widget.SweetAlertDialog.CloseDialogImpl;

import com.hua.activity.R;
import com.hua.gz.adapter.AppointmentAdapter;
import com.hua.gz.model.CustomerDataModle;
import com.hua.nowid.activity.AddAppointmentUserActivity;
import com.hua.nowid.activity.AddAppointmentUserActivity.AddAppointmentListener;
import com.hua.nowplayerjunior.utils.LogUtils2;
import com.pccw.nmal.model.StreamInfo;

public class AddCustomerFragment extends UIEventBaseFragment implements AddAppointmentListener{

	private static final String TAG = AddCustomerFragment.class.getSimpleName();
	private static final int EPG_UPDATE_INTERVAL = 60000;
	private static final int ADD_APPOINTMENT = 100;
	private static final double VIDEO_FRAME_ASPECT_RATIO = 1.777777777777777d;
	private View view;
	private Button yuyueButton;
	private ListView contentListView;
	private AppointmentAdapter mAdapter;
	private List<CustomerDataModle> mListViewList = new ArrayList<CustomerDataModle>();
	private View headView;
	private View endView;
	private boolean isaddHead;
	private Activity mactivity;
	
	/**
	 * 用来显示自定的dialog
	 */
	private SweetAlertDialog mSweetAlertDialog;
	
	public AddCustomerFragment() {}
	

	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			int code = msg.what;
			int lastItemPosition = Integer.valueOf(String.valueOf(msg.obj));
			LogUtils2.i("obj lastItemPosition  = "+lastItemPosition);
			switch (code) {
			case 1:
				View view = contentListView.getChildAt(lastItemPosition - 1);
				LogUtils2.e("*****==view = "+view);
				view.setBackgroundResource(R.drawable.appointment_item_end_background);
				break;

			default:
				break;
			}
			
		};
	};
	
	
	public void onAttach(android.app.Activity activity) {
		super.onAttach(activity);
		mactivity = activity;
		LogUtils2.i("mactivity== "+mactivity);
	};
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		inflater = getActivity().getLayoutInflater();
		view = inflater.inflate(R.layout.live_ch_fragment2,container, false);
		inItView(view);
		
		return view;
	}

	/**
	 * 
	 * @param view
	 */
	public void inItView(View view){
		yuyueButton = (Button) view.findViewById(R.id.yuyueButton);
		yuyueButton.setOnClickListener(new AddUserButtonListener());
		initListView(view);
		
	}
	
	

	public  void initListView(View view) {
		contentListView = (ListView) view.findViewById(R.id.contentListView);
//		contentListView.

		
//		for (int i = 0; i < 20; i++) {
//			
//			CustomerDataModle customerDataModle = new CustomerDataModle("德玛西亚"+i, "你猜", "2010 - 20 - 10");
//			mListViewList.add(customerDataModle);
//		}
		
		mAdapter = new AppointmentAdapter(getActivity(),mListViewList);
//		if(!mAdapter.isEmpty()){
//			
//			headView = LayoutInflater.from(getActivity()).inflate(R.layout.appointment_content_item, null);
//			headView.setBackgroundResource(R.drawable.appointment_item_head_background);
//			((TextView)headView.findViewById(R.id.userName_TV)).setText("科比");
//			((TextView)headView.findViewById(R.id.userSex_TV)).setText("你猜");
//			((TextView)headView.findViewById(R.id.userdate_TV)).setText("2007 - 10 - 20");
//			
//			endView = LayoutInflater.from(getActivity()).inflate(R.layout.appointment_content_item, null);
//			endView.setBackgroundResource(R.drawable.appointment_item_end_background);
//			
//			contentListView.addHeaderView(headView);
//			contentListView.addFooterView(endView);
//		}
		if(mAdapter.isEmpty()){
//			contentListView.setAdapter(mAdapter);
		}
		contentListView.setDivider(null);
		contentListView.setDividerHeight(0);
		contentListView.setOnScrollListener(new MyScrollListener(contentListView));
	}

	

	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}

	

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
//		outState.putInt("tabIndex", lastTabIndex);
	}


//	private boolean parseLiveChannelJSON() {
//		Log.v("parseLiveChannelJSON", "parseLiveChannelJSON");
//		JsonZip jsonZip = new JsonZip(this.getActivity(), AppInfo.getJsonVersionPath(), Constants.JSON_ZIP_VERSION_PREFIX, LanguageHelper.getCurrentLanguage());
//		String result = jsonZip.getJSONData(ZipType.PKG, Constants.CMS_JSON_LIVE_CHANNEL_CATALOG);
//		return (LiveCatalog.getInstance().parseLiveCatalogJSON(result));
//	}
//	
//	private boolean parseLiveDeatilJSON() {
//		Log.v("parseLiveDeatilJSON", "parseLiveDeatilJSON");
//		JsonZip jsonZip = new JsonZip(this.getActivity(), AppInfo.getJsonVersionPath(), Constants.JSON_ZIP_VERSION_PREFIX, LanguageHelper.getCurrentLanguage());
//		String result = jsonZip.getJSONData(ZipType.PKG, Constants.CMS_JSON_LIVE_CHANNEL_DETAIL);
//		return (LiveDetail.getInstance().parseLiveDetailJSON(result));
//	}
	
	/**
	 * Workaround for java.lang.IllegalStateException: No activity bug
	 * See http://stackoverflow.com/questions/15207305/
	 */
	@Override
	public void onDetach() {
	    super.onDetach();
	}

	
	class AddUserButtonListener implements OnClickListener{

		

		@Override
		public void onClick(View v) {
			if(v.getId() == R.id.yuyueButton){
//				
//				CustomerDataModle customerDataModle = new CustomerDataModle("科比呀","你猜","啦啦啦");
//				if(mAdapter.isEmpty() && !isaddHead){
//					
//					headView = LayoutInflater.from(getActivity()).inflate(R.layout.appointment_content_item, null);
//					headView.setBackgroundResource(R.drawable.appointment_item_both_background);
//					((TextView)headView.findViewById(R.id.userName_TV)).setText("科比");
//					((TextView)headView.findViewById(R.id.userSex_TV)).setText("你猜");
//					((TextView)headView.findViewById(R.id.userdate_TV)).setText("2007 - 10 - 20");
//					contentListView.addHeaderView(headView);
//					isaddHead = true;
//					contentListView.setAdapter(mAdapter);
//				}else {
//					headView.setBackgroundResource(R.drawable.appointment_item_head_background);
//					((TextView)headView.findViewById(R.id.userName_TV)).setText("科比555555");
//					mAdapter.appendList(customerDataModle);
//					contentListView.invalidate();
//					int lastItemPosition = contentListView.getCount();
//				}
//				mListViewList.add(customerDataModle);
				
//				new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE).setCloseDialogImpl( new MyCloseDialogImpl())
//	             .setTitleText("Are you sure?")
//	             .setContentText("Won't be able to recover this file!"
//	             		+ "Won't be able to recover this file!"
//	             		+ "Won't be able to recover this file!"
//	             		+ "Won't be able to recov阿萨德死了烦死了的快放假啊开发来看er t"
//	             		+ "私搭风格sad联发科技阿萨德理发师的发生率的副科级啊岁的老父卡死"
//	             		+ "的罚款随即大幅拉升对方哈里斯款到发书健康"
//	             		+ "思考的非公开垃圾的很疯狂拉升阶段返回拉萨快点发货拉克斯多夫哈克大家分开"
//	             		+ "his file!")
//	             .setConfirmText("删除!")
//	             .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//	             @Override
//	             public void onClick(SweetAlertDialog sDialog) {
//	                 // reuse previous dialog instance
//	             	mSweetAlertDialog = sDialog;
//	             	mSweetAlertDialog.setTitleText("Deleted!")
//	                         .setContentText("Your imaginary file has been deleted!")
//	                         .setConfirmText("")
//	                         .setConfirButtonBackground()
//	                         .setConfirmClickListener(null)
//	                         .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
////	             	mSweetAlertDialog.setContentView(v);
//	                
//	             }
//	             })
//	             .show();
				
//				mSweetAlertDialog = new SweetAlertDialog(getActivity());
//				mSweetAlertDialog.setContentView(R.layout.showadduser2);
////				mSweetAlertDialog.setAddUserLayout();
//				mSweetAlertDialog.show();
				
			///上面的做test
				AddAppointmentUserActivity userActivity = new AddAppointmentUserActivity();
				userActivity.setAddAppointmentListener(AddCustomerFragment.this);
				Intent intent = new Intent(getActivity(), userActivity.getClass());
				LogUtils2.i("startActivity*******************");
				
				startActivityForResult(intent, getActivity().RESULT_FIRST_USER);
				getActivity().overridePendingTransition(R.anim.fade_forward, R.anim.fade_back);
				LogUtils2.i("after*******************");
			}
		}
		
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		LogUtils2.e("777777");
		super.onActivityResult(requestCode, resultCode, data);
		LogUtils2.e("++++++++++++");
		LogUtils2.e("+++++++++ ="+resultCode+"   resultCode= "+resultCode);
		if(resultCode == 200){
			
//			CustomerDataModle info = (CustomerDataModle) data.getSerializableExtra("info");
//			
////			CustomerDataModle customerDataModle = new CustomerDataModle("科比呀","你猜","啦啦啦");
//			if(mAdapter.isEmpty() && !isaddHead){
//				
//				headView = LayoutInflater.from(getActivity()).inflate(R.layout.appointment_content_item, null);
//				headView.setBackgroundResource(R.drawable.appointment_item_both_background);
//				((TextView)headView.findViewById(R.id.userName_TV)).setText(info.getUsername());
//				((TextView)headView.findViewById(R.id.userSex_TV)).setText(info.getSex());
//				((TextView)headView.findViewById(R.id.userdate_TV)).setText(info.getDate());
//				contentListView.addHeaderView(headView);
//				isaddHead = true;
//				contentListView.setAdapter(mAdapter);
//			}else {
//				headView.setBackgroundResource(R.drawable.appointment_item_head_background);
//				mAdapter.appendList(info);
//				contentListView.invalidate();
//			}
		}
	}
	

	@Override
	protected void afterQualityAndBookmarkSelected(
			List<List<StreamInfo>> playlist, int quality, int bookmark) {
		
	}


	public boolean isfullscreen() {
		return false;
	}
	
	class MyCloseDialogImpl implements CloseDialogImpl{

		@Override
		public void onFinishAnimation() {

			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					if(mSweetAlertDialog != null){
						
						mSweetAlertDialog.dismiss();
					}
					
				}
			}, 600);
			
		}
		
	}
	
	// for the ListView scrollListener
	class MyScrollListener implements OnScrollListener{

		ListView mListView;
		
		public MyScrollListener(ListView listView){
			mListView = listView;
		}
		
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, final int totalItemCount) {
			int lastIndex = mListView.getLastVisiblePosition();
			LogUtils2.d("lastposition == "+ lastIndex);
			LogUtils2.d("totalItemCount == "+ totalItemCount);
			
			if(lastIndex >0 && lastIndex == totalItemCount - 1){
				LogUtils2.e("*****************************");
				LogUtils2.i("totalItemCount - 1==="+(totalItemCount - 1));
//				handler.postDelayed(new Runnable() {
//					
//					@Override
//					public void run() {
//						View view2  = mListView.getChildAt(totalItemCount - 1); 
//						LogUtils2.e("view2=="+view2);
//						view2.setBackgroundResource(R.drawable.appointment_item_end_background);
////						view2.setBackgroundColor(Color.parseColor("#000033"));
//						LogUtils2.e("-------------------");
//					}
//				}, 500);
				
//				mListView.getc
			}else if(lastIndex > 0 && lastIndex < totalItemCount -1){
//				handler.obtainMessage(1).sendToTarget();
				for(int i=1;i<visibleItemCount;i++){
					
				
//				handler.post(new Runnable() {
//					
//					@Override
//					public void run() {
//
//						View view2  = mListView.getChildAt(i); 
//						LogUtils2.e("view2=="+view2);
//						view2.setBackgroundResource(R.drawable.appointment_item_end_background);
//						
//					}
//				});
//					handler.obtainMessage(1,i).sendToTarget();
			}
			}
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			
		}
		
	}

	@Override
	public void onReceiveData(Intent data) {
		
		LogUtils2.d("** execute onReceiveData");
		
		if(data != null){
			
			CustomerDataModle info = (CustomerDataModle) data.getSerializableExtra("info");
			
//		CustomerDataModle customerDataModle = new CustomerDataModle("科比呀","你猜","啦啦啦");
			if(mAdapter.isEmpty() && !isaddHead){
				
				headView = LayoutInflater.from(getActivity()).inflate(R.layout.appointment_content_item, null);
				headView.setBackgroundResource(R.drawable.appointment_item_both_background);
				((TextView)headView.findViewById(R.id.userName_TV)).setText(info.getUsername());
				((TextView)headView.findViewById(R.id.userSex_TV)).setText(info.getSex());
				((TextView)headView.findViewById(R.id.userdate_TV)).setText(info.getDate());
				contentListView.addHeaderView(headView);
				isaddHead = true;
				contentListView.setAdapter(mAdapter);
			}else {
				headView.setBackgroundResource(R.drawable.appointment_item_head_background);
				mAdapter.appendList(info);
				contentListView.invalidate();
			}
		}
		
	}
	
	
}

 
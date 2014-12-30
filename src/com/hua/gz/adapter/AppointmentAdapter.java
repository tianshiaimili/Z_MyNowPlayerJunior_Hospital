package com.hua.gz.adapter;

import java.util.ArrayList;
import java.util.List;

import com.hua.activity.R;
import com.hua.gz.model.CustomerDataModle;
import com.hua.nowplayerjunior.utils.LogUtils2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class AppointmentAdapter extends BaseAdapter{

	private List<CustomerDataModle> mList ;
	private Context mContext ;
	private AppointmentAdapter mAdapter;
	
	
	public AppointmentAdapter (Context context){
		
		mList = new ArrayList<CustomerDataModle>();
		mContext = context;
		
	}
	
	public AppointmentAdapter (Context context,List<CustomerDataModle> customerDataModles){
		
		mContext = context;
		if(customerDataModles != null){
			mList = customerDataModles;
		}else {
			
			mList = new ArrayList<CustomerDataModle>();
		}
		
	}
	
	public AppointmentAdapter instanceAdapter(Context context){
		
		if(mAdapter == null){
			
			mAdapter = new AppointmentAdapter(context);
			
		}
		
		return mAdapter;
	}
	
	public void appendList(CustomerDataModle dataModle){
		
		if(mList != null){
			mList.add(dataModle);
			notifyDataSetChanged();
		}else {
			LogUtils2.e(" the Mlist is null"); 
		}
	}
	
	
	@Override
	public int getCount() {
		return (mList != null && mList.size() >0) ? mList.size():0;
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder mViewHolder;
		if(convertView == null){
			convertView = LayoutInflater.from(mContext).inflate(R.layout.appointment_content_item, null);
			mViewHolder = new ViewHolder();
		}else {
			mViewHolder = (ViewHolder) convertView.getTag();
		}
		
		if(mList.size() == 1 && position == 0){
			convertView.setBackgroundResource(R.drawable.appointment_item_end_background);
		}else if(position == mList.size() -1 ){
			convertView.setBackgroundResource(R.drawable.appointment_item_end_background);
		}else {
			convertView.setBackgroundResource(R.drawable.appointment_item_background);
		}
		
		mViewHolder.username = (TextView) convertView.findViewById(R.id.userName_TV);
		mViewHolder.sex = (TextView) convertView.findViewById(R.id.userSex_TV);
		mViewHolder.date = (TextView) convertView.findViewById(R.id.userdate_TV);
		
		mViewHolder.username.setText(mList.get(position).getUsername());
		mViewHolder.sex.setText(mList.get(position).getSex());
		mViewHolder.date.setText(mList.get(position).getDate());
		
		convertView.setTag(mViewHolder);
		
		return convertView;
	}

	
	class ViewHolder {
		
		TextView username;
		TextView sex;
		TextView date;
		
	}
	
}

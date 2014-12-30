package com.hua.nowid.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.hua.activity.R;
import com.hua.gz.model.CustomerDataModle;
import com.hua.gz.widget.CustomerToast;
import com.hua.nowplayerjunior.utils.LogUtils2;

public class AddAppointmentUserActivity extends Activity{

	private View contentView;
	private Button comfirtButton;
	private EditText nameEditText;
	private EditText dateEditText;
	private RadioButton manrRadioButton,womenRadioButton;
	private static AddAppointmentListener addAppointmentListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.add_appointment_user);
//		contentView = LayoutInflater.from(getBaseContext()).inflate(R.layout.add_appointment_user, null);
		initContentView();
		
		
	}
	
	public void initContentView(){
		
		comfirtButton = (Button) findViewById(R.id.comfirtButton);
		comfirtButton.setOnClickListener(new AddOnClickListener());
		nameEditText = (EditText) findViewById(R.id.add_U_name_editT);
		dateEditText = (EditText) findViewById(R.id.add_U_date_TextV);
		if(dateEditText.isFocused()){
			CustomerToast.makeText(getApplicationContext(), "请输入姓名", 300).show();
		}
		manrRadioButton = (RadioButton) findViewById(R.id.man_radioB);
		womenRadioButton = (RadioButton) findViewById(R.id.woman_radioB);
		
	}
	
	
	class AddOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			
			int id = v.getId();
			String name = null;
			String sex = null;
			String date = null;
			LogUtils2.i("is come in..");
//			AppointmentInfo info = new i
			switch (id) {
			case R.id.comfirtButton:
				
				if(nameEditText.getText().toString().equals("")){
//					Toast.makeText(getBaseContext(), "请输入姓名", 300).show();
					CustomerToast.makeText(getApplicationContext(), "请输入姓名", 1000).show();
				}else {
					name = nameEditText.getText().toString();
				}
				
				if(dateEditText.getText().toString().equals("")){
//					Toast.makeText(getBaseContext(), "请选择日期", 300).show();
					CustomerToast.makeText(getApplicationContext(), "请输入姓名", 1000).show();
				}else {
					date = dateEditText.getText().toString();
				}
				
				if(manrRadioButton.isChecked()){
					sex = (String) manrRadioButton.getText();
				}else if(womenRadioButton.isChecked()){
					sex = (String) womenRadioButton.getText();
				}
				
				if(name != null && sex != null && date != null){
					CustomerDataModle info = new CustomerDataModle(name, sex, date);
					Intent intent = new Intent();
					intent.putExtra("info", info);
					setResult(200, intent);
					if(addAppointmentListener != null)
						addAppointmentListener.onReceiveData(intent);
					finish();
					overridePendingTransition(R.anim.fade_forward, R.anim.fade_back);
				}
				
				break;

			default:
				break;
			}
			
			
		}
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if(keyCode == KeyEvent.KEYCODE_BACK){
			LogUtils2.e("oooooooooooooooooooooo");
			finish();
			overridePendingTransition(R.anim.fade_forward, R.anim.fade_back);
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	public interface AddAppointmentListener{
		public void onReceiveData(Intent intent);
	}



	public static AddAppointmentListener getAddAppointmentListener() {
		return addAppointmentListener;
	}

	public static void setAddAppointmentListener(
			AddAppointmentListener addAppointmentListener) {
		AddAppointmentUserActivity.addAppointmentListener = addAppointmentListener;
	}
	
	
	
}

package com.hua.nowplayerjunior.activity;

import android.os.Bundle;

import com.hua.activity.R;
import com.hua.gz.app.BaseFragmentActivity;
import com.hua.gz.app.FragmentUtils;

public class TutorialActivity extends BaseFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tutorial_activity);
		FragmentUtils.setContentViewFragment(this, R.id.activity_fragment_container, new TutorialFragment(), null);
	}
	
}

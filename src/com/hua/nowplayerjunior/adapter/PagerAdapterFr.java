package com.hua.nowplayerjunior.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import com.hua.nowplayerjunior.activity.CategoryFragment;
import com.hua.nowplayerjunior.activity.RootCategoryFragment;

public class PagerAdapterFr extends FragmentPagerAdapter {
	 
    private List<Fragment> fragments;
    private List<String> categoryIds;
    private String[] fragmentnames = new String[20];
	private RootCategoryFragment parent;
    /**
     * @param fm
     * @param fragments
     */
    public PagerAdapterFr(FragmentManager fm, List<String> categoryIds, String[] fragmentnames, RootCategoryFragment parent) {
        super(fm);
        this.categoryIds = categoryIds;
        this.fragmentnames = fragmentnames;
        this.parent = parent;
    }
    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
     */
    @Override
    public Fragment getItem(int position) {
    	Log.d("PagerAdapterFr", "getItem at " + position);
    	return CategoryFragment.newInstance(categoryIds.get(position), parent);
    }
 
    /* (non-Javadoc)
     * @see android.support.v4.view.PagerAdapter#getCount()
     */
    @Override
    public int getCount() {
        return categoryIds.size();
    }
    
    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentnames[position];
    }
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		Log.d("PagerAdapterFr", "destroyItem at pos " + position);
		super.destroyItem(container, position, object);
	}
}

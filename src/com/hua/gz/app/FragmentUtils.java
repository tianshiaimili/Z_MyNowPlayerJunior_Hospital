package com.hua.gz.app;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

public class FragmentUtils {
	
	public static void add(FragmentActivity act, Fragment fragment, int containerId, String tag) {
		FragmentManager manager = act.getSupportFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();
		ft.add(containerId, fragment, tag);
		ft.commit();
	}
	
	public static void add(FragmentActivity act, Fragment fragment, int containerId, String tag, String backStackStateName) {
		FragmentManager manager = act.getSupportFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();
		ft.add(containerId, fragment, tag);
		ft.addToBackStack(backStackStateName);
		ft.commit();
	}
	
	public static void remove(FragmentActivity act, Fragment fragment) {
		FragmentManager manager = act.getSupportFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();
		ft.remove(fragment);
		ft.commit();
	}
	
	public static void remove(FragmentActivity act, Fragment fragment, String backStackStateName) {
		FragmentManager manager = act.getSupportFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();
		ft.remove(fragment);
		ft.addToBackStack(backStackStateName);
		ft.commit();
	}
	
	public static void replace(FragmentActivity act, Fragment fragment, int containerId, String tag) {
		FragmentManager manager = act.getSupportFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();
		ft.replace(containerId, fragment, tag);
		ft.commit();
	}
	
	public static void replace(FragmentActivity act, Fragment fragment, int containerId, String tag, String backStackStateName) {
		FragmentManager manager = act.getSupportFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();
		ft.replace(containerId, fragment, tag);
		ft.addToBackStack(backStackStateName);
		ft.commit();
	}

	public static void attach(FragmentActivity act, Fragment fragment) {
		FragmentManager manager = act.getSupportFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();
		ft.attach(fragment);
		ft.commit();
	}
	
	public static void attach(FragmentActivity act, Fragment fragment, String backStackStateName) {
		FragmentManager manager = act.getSupportFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();
		ft.attach(fragment);
		ft.addToBackStack(backStackStateName);
		ft.commit();
	}
	
	public static void detach(FragmentActivity act, Fragment fragment) {
		FragmentManager manager = act.getSupportFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();
		ft.detach(fragment);
		ft.commit();
	}
	
	public static void detach(FragmentActivity act, Fragment fragment, String backStackStateName) {
		FragmentManager manager = act.getSupportFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();
		ft.detach(fragment);
		ft.addToBackStack(backStackStateName);
		ft.commit();
	}
	
	public static void show(FragmentActivity act, Fragment fragment) {
		FragmentManager manager = act.getSupportFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();
		ft.show(fragment);
		ft.commit();
	}
	
	public static void hide(FragmentActivity act, Fragment fragment) {
		FragmentManager manager = act.getSupportFragmentManager();
		FragmentTransaction ft = manager.beginTransaction();
		ft.hide(fragment);
		ft.commit();
	}
	
	/**
	 * Add a fragment to FragmentActivity content view with its containerId.
	 * Call this method after FragmentActivity.setContentView()
	 * 
	 * @param act
	 * @param containerId
	 * @param fragment
	 * @param tag
	 */
	public static void setContentViewFragment(FragmentActivity act, int containerId, Fragment fragment, String tag) {
		// Do NOT use getFragmentManager().
		act.getSupportFragmentManager().beginTransaction().add(containerId, fragment, tag).commit();
	}
	
	public static interface FragmentFeed {
		public Fragment newFragment(String tag); 
	}
	
	/**
	 * A tab switcher for fragment tabs.
	 * This tab switcher only support non-z-ordering tabs.
	 * 
	 * @author AlfredZhong
	 * @version 2013-08-27
	 */
	public static class FragmentSwitcher {
		
		// the FragmentActivity holding the tabs.
		private FragmentActivity mFragmentActivity;
		// current showing fragment.
		private Fragment mCurrentFragment;
		// fragment container id.
		private int mContainerId;
		// fragment feed.
		private FragmentFeed mFragmentFeed;

		public FragmentSwitcher(FragmentActivity act, int containerId, FragmentFeed feed) {
			mContainerId = containerId;
			mFragmentFeed = feed;
		}
		
		public Fragment switchFragment(String fragmentTag) {
			if(fragmentTag == null) {
				throw new NullPointerException("Fragment tag can NOT be null.");
			}
			FragmentManager manager = mFragmentActivity.getSupportFragmentManager();
			FragmentTransaction ft = manager.beginTransaction();
			if(mCurrentFragment != null) {
				// detach current fragment from UI if any.
				ft.detach(mCurrentFragment);
			}
			mCurrentFragment = manager.findFragmentByTag(fragmentTag);
			if(mCurrentFragment == null) {
				// this fragment never show before, add it and show.
				mCurrentFragment = mFragmentFeed.newFragment(fragmentTag);
				ft.add(mContainerId, mCurrentFragment, fragmentTag);
			} else {
				// re-attach fragment to show.
				ft.attach(mCurrentFragment);
			}
			ft.commit();
			return mCurrentFragment;
		}
		
		public Fragment getCurrentFragment() {
			return mCurrentFragment;
		}
		
	} // end of inner class.
	
	/**
	 * A tab switcher for fragment tabs.
	 * This tab switcher supports z-ordering tabs.
	 * 
	 * @author AlfredZhong
	 * @version 2013-10-15
	 */
	public static class FragmentTabSwitcher {
		
		private static final String TAG = FragmentTabSwitcher.class.getSimpleName();
		// the FragmentActivity holding the tabs.
		private final FragmentActivity mFragmentActivity;
		// A HashMap of tab stacks. we use root fragment tags as tabs id.
		private final HashMap<String, LinkedList<String>> mTabStacks;
		// fragment container id.
		private final int mContainerId;
		// fragment feed.
		private final FragmentFeed mRootFragmentFeed;
		// tabs count.
		private final int mTabCount;
		// current showing fragment.
		private Fragment mCurrentFragment;
		// current tab id, it is also the root fragment tag of this tab.
		private String mCurrentTabId;
		
		public FragmentTabSwitcher(FragmentActivity act, int containerId, FragmentFeed rootFragmentFeed, List<String> rootFragmentTags) {
			mFragmentActivity = act;
			mContainerId = containerId;
			mRootFragmentFeed = rootFragmentFeed;
			mTabCount = rootFragmentTags.size(); 
			mTabStacks = new HashMap<String, LinkedList<String>>();
	        for(int i=0; i<mTabCount; i++) {
	        	String tag = rootFragmentTags.get(i);
	        	if(tag == null) {
	        		throw new RuntimeException("Tab tag can NOT be null.");
	        	}
	        	if(tag.equals("")) {
	        		throw new RuntimeException("Tab tag can NOT be empty.");
	        	}
	        	if(mTabStacks.containsKey(tag)) {
	        		throw new RuntimeException("Tab tag " + tag + " should be unique.");
	        	}
	        	mTabStacks.put(tag, new LinkedList<String>());
	        }
		}
		
		/**
		 * Check current tab stack before returning the tab stack.
		 * 
		 * @return current tab stack or null if never switchTab() or key is not correct.
		 */
		private LinkedList<String> getCurrentTabStack() {
			if(mCurrentTabId == null) {
				Log.w(TAG, "Please call switchTab() first before using other methods.");
				return null;
			}
			LinkedList<String> tab = mTabStacks.get(mCurrentTabId);
			if(tab == null) {
				Log.w(TAG, "Can NOT find the the tab with key " + mCurrentTabId);
				return null;
			}
			return tab;
		}
		
		/**
		 * Switch tab with tab root fragment tag.
		 * 
		 * @param rootFragmentTag
		 */
		public void switchTab(String rootFragmentTag) {
			Log.d(TAG, "switchTab " + rootFragmentTag);
			mCurrentTabId = rootFragmentTag;
			// Do NOT update current fragment here, since push and pop will update it.
			// And if you update current fragment here, push and pop may detach the current one.
			if (getCurrentTabStack().size() == 0) {
				// First time this tab is selected. So add first fragment of that tab.
				pushFragment(true, mRootFragmentFeed.newFragment(rootFragmentTag));
			} else {
				/*
				 * We are switching tabs, and target tab is already has at least one fragment. 
				 * No need of stack pushing. Just show the target fragment
				 */
				pushFragment(false, peekTopmostFragment());
			}
		}
		
		public boolean isCurrentTabStackEmpty() {
			LinkedList<String> tab = getCurrentTabStack();
			boolean ret = tab == null || tab.isEmpty();
			if(ret)
				Log.w(TAG, "Current tab stack is empty.");
			return ret;
		}
		
		/**
		 * Retrieves, but does not remove the topmost fragment.
		 */
		public Fragment peekTopmostFragment() {
			if(isCurrentTabStackEmpty()) {
				return null;
			}
			// We use LinkedList to act as stack. Therefore:
			// LinkedList.push() is LinkedList.addFirst()
			// LinkedList.pop() is removeFirst()
			// LinkedList.peek() is LinkedList.getFirst()
			return mFragmentActivity.getSupportFragmentManager().findFragmentByTag(getCurrentTabStack().getFirst());
		}
		
		/**
		 * To add a fragment to the current tab.
		 * You can add many fragments at the same time by:
		 * 1. pushFragment(A); pushFragment(B); pushFragment(C);
		 * In this case, fragment A & B will go through "onAttach() -- onDestroyView()",
		 * and this period is quick enough to avoid showing their UI to user,
		 * and fragment C will be shown and user can BACK to previous fragments.
		 * 2. pushFragment(A, B, C);
		 * In this case, fragment A & B will go through "onAttach() -- onCreate()",
		 * and this period is quick enough to avoid showing their UI to user,
		 * and fragment C will be shown and user can BACK to previous fragments.
		 * 
		 * @param add true if we start a new fragment to current tab.
		 * @param fragments fragments to be push to current tab stack.
		 */
		private void pushFragment(boolean add, Fragment... fragments) {
			FragmentManager manager = mFragmentActivity.getSupportFragmentManager();
			FragmentTransaction ft = manager.beginTransaction();
			for(Fragment fragment : fragments) {
				if(mCurrentFragment != null) {
					/*
					 * Detach the given fragment from the UI. This is the same state as
					 * when it is put on the back stack: the fragment is removed from the UI,
					 * however its state is still being actively managed by the fragment manager.
					 * When going into this state its view hierarchy is destroyed.
					 * 
					 * if fragment is onResume(), detach() will cause "onPause() - onDestroyView()", NOT onDetach().
					 */
					Log.d(TAG, "Detach fragment " + mCurrentFragment.getTag());
					ft.detach(mCurrentFragment);
				}
				if(add) {
					String fragmentTag = mCurrentTabId + "-" + getCurrentTabStack().size();
					Log.d(TAG, "Add new fragment " + fragmentTag);
					// add new fragment and show.
					ft.add(mContainerId, fragment, fragmentTag);
					// add fragment to the stack.
					getCurrentTabStack().addFirst(fragmentTag);
				} else {
					// switching tabs, re-attach the current tab fragment.
					// attach() will cause onCreateView(), NOT onAttach().
					Log.d(TAG, "Attach fragment " + fragment.getTag());
					ft.attach(fragment);
				}
				mCurrentFragment = fragment;
			}
			ft.commit();
		}
		
		/**
		 * To add a fragment to the current tab.
		 * 
		 * @param fragment
		 */
		public void pushFragment(Fragment fragment) {
			pushFragment(true, fragment);
		}
		
		/**
		 * To add some fragments to the current tab.
		 * 
		 * @param fragment
		 */
		public void pushFragments(Fragment... fragments) {
			pushFragment(true, fragments);
		}
		
		/**
		 * Pop the topmost fragment. In other words, the topmost fragment will be removed from tab stack.
		 */
		private Fragment popTopmostFragment() {
			if(isCurrentTabStackEmpty())
				return null;
			String tag = getCurrentTabStack().removeFirst();
			Log.d(TAG, "Remove fragment " + tag);
			return mFragmentActivity.getSupportFragmentManager().findFragmentByTag(tag);
		}
		
		/**
		 * Pop current fragment from the current tab stack.
		 * Usually for user navigate back.
		 */
		public Fragment popFragment() {
			FragmentManager manager = mFragmentActivity.getSupportFragmentManager();
			FragmentTransaction ft = manager.beginTransaction();
			// remove from stack and remove the fragment.
			// remove() will cause Fragment.onDetach() without back stack.
			ft.remove(popTopmostFragment());
			// show the previous fragment.
			mCurrentFragment = peekTopmostFragment();
			/*
			 * Re-attach a fragment after it had previously been detached from the UI with detach().
			 * This causes its view hierarchy to be re-created, attached to the UI, and displayed.
			 */
			ft.attach(mCurrentFragment);
			Log.d(TAG, "Attach fragment " + mCurrentFragment.getTag());
			ft.commit();
			return mCurrentFragment;
		}
		
		public Fragment popToRootFragment() {
			int size = getCurrentTabStack().size();
			if(size > 1) {
				FragmentManager manager = mFragmentActivity.getSupportFragmentManager();
				FragmentTransaction ft = manager.beginTransaction();
				for(int i=0; i<size -1; i++) {
					// remove from stack and remove the fragment.
					ft.remove(popTopmostFragment());
				}
				// show the root fragment.
				mCurrentFragment = peekTopmostFragment();
				ft.attach(mCurrentFragment);
				Log.d(TAG, "Attach fragment " + mCurrentFragment.getTag());
				ft.commit();
			}
			return mCurrentFragment;
		}
		
		public boolean isRootFragment() {
			return getCurrentTabStack().size() == 1;
		}
		
		public Fragment getCurrentFragment() {
			return mCurrentFragment;
		}
		
		public int getTabCount() {
			return mTabCount;
		}
		
	} // end of inner class.

}

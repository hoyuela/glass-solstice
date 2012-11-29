package com.discover.mobile.navigation;

import static com.discover.mobile.common.ReferenceUtility.safeGetReferenced;

import java.lang.ref.SoftReference;

import android.support.v4.app.Fragment;
import android.widget.ListView;

import com.discover.mobile.section.FragmentComponentInfo;
import com.google.common.base.Throwables;

/**
 * Responder handles showing a {@link Fragment} when selected.
 */
final class FragmentNavigationItem extends NavigationItem {
	
	private final FragmentComponentInfo fragmentInfo;
	
	private SoftReference<Fragment> fragmentRef;
	
	FragmentNavigationItem(final FragmentComponentInfo fragmentInfo, final NavigationItemAdapter adapter,
			final NavigationItemView view, final int absoluteIndex) {
		
		super(adapter, view, absoluteIndex);
		
		this.fragmentInfo = fragmentInfo;
	}
	
	@Override
	void onClick(final ListView listView) {
		makeVisible();
		
		// TODO show highlight if selected
	}
	
	void show() {
		adapter.insert(this, absoluteIndex);
		
		// TODO
	}
	
	void hide() {
		adapter.remove(this);
		
		// TODO
	}
	
	private void makeVisible() {
		adapter.getNavigationRoot().makeFragmentVisible(getCachedOrCreateFragment());
	}
	
	Fragment getCachedOrCreateFragment() {
		Fragment fragment = safeGetReferenced(fragmentRef);
		
		if(fragment == null) {
			try {
				fragment = fragmentInfo.getFragmentClass().newInstance();
			} catch(final Exception e) {
				throw Throwables.propagate(e);
			}
			fragmentRef = new SoftReference<Fragment>(fragment);
		}
		
		return fragment;
	}
	
}

package com.discover.mobile.navigation;

import static com.discover.mobile.common.ReferenceUtility.safeGetReferenced;

import java.lang.ref.SoftReference;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.discover.mobile.R;
import com.google.common.base.Throwables;

abstract class NavigationItem {
	
	final NavigationItemAdapter navigationItemAdapter;
	final int absoluteIndex;
	
	private final int viewResource;
	
	private SoftReference<Fragment> fragmentRef;
	
	NavigationItem(final NavigationItemAdapter navigationItemAdapter, final int viewResource, final int absoluteIndex) {
		this.navigationItemAdapter = navigationItemAdapter;
		this.viewResource = viewResource;
		this.absoluteIndex = absoluteIndex;
	}
	
	abstract Class<? extends Fragment> getFragmentClass();
	abstract int getViewType();
	abstract void customizeItemView(View view, TextView title);
	abstract void onClick(ListView listView);
	
	final View getView(final View convertView) {
		View view;
		if(convertView == null)
			view = navigationItemAdapter.getLayoutInflater().inflate(viewResource, null);
		else
			view = convertView;
		
		final TextView title = (TextView) view.findViewById(R.id.title);
		
		customizeItemView(view, title);
		
		return view;
	}
	
	final void showFragment() {
		Fragment fragment = safeGetReferenced(fragmentRef);
		if(fragment == null) {
			try {
				fragment = getFragmentClass().newInstance();
			} catch(final Exception e) {
				throw Throwables.propagate(e);
			}
			fragmentRef = new SoftReference<Fragment>(fragment);
		}
		
		navigationItemAdapter.getNavigationRoot().replaceMainFragment(fragment, true);
	}
	
}

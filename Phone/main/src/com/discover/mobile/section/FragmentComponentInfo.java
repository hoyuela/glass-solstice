package com.discover.mobile.section;

import javax.annotation.concurrent.Immutable;

import com.actionbarsherlock.app.SherlockFragment;

@Immutable
public class FragmentComponentInfo extends ComponentInfo {
	
	private final Class<? extends SherlockFragment> fragmentClass;
	
	public FragmentComponentInfo(final int titleResource, final Class<? extends SherlockFragment> fragmentClass) {
		super(titleResource);
		
		this.fragmentClass = fragmentClass;
	}
	
	public final Class<? extends SherlockFragment> getFragmentClass() {
		return fragmentClass;
	}
	
}

package com.discover.mobile.section;

import javax.annotation.concurrent.Immutable;

import com.actionbarsherlock.app.SherlockFragment;

@Immutable
public abstract class BaseComponentInfo {
	
	private final int titleResource;
	private final Class<? extends SherlockFragment> fragmentClass;
	
	BaseComponentInfo(final int titleResource, final Class<? extends SherlockFragment> fragmentClass) {
		this.titleResource = titleResource;
		this.fragmentClass = fragmentClass;
	}
	
	public final int getTitleResource() {
		return titleResource;
	}
	
	public final Class<? extends SherlockFragment> getFragmentClass() {
		return fragmentClass;
	}
	
}

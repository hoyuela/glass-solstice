package com.discover.mobile.section;

import javax.annotation.concurrent.Immutable;

import com.actionbarsherlock.app.SherlockFragment;

@Immutable
public final class SubSectionInfo extends BaseComponentInfo {
	
	public SubSectionInfo(final int titleResource, final Class<? extends SherlockFragment> fragmentClass) {
		super(titleResource, fragmentClass);
	}
	
}

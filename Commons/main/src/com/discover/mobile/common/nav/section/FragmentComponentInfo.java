package com.discover.mobile.common.nav.section;

import javax.annotation.concurrent.Immutable;

import com.actionbarsherlock.app.SherlockFragment;

@Immutable
public class FragmentComponentInfo extends ComponentInfo {

	private final Class<? extends SherlockFragment> fragmentClass;

	private final String url;


	public FragmentComponentInfo(final int titleResource, final Class<? extends SherlockFragment> fragmentClass) {
		this(titleResource, fragmentClass, null);
	}

	public FragmentComponentInfo(final int titleResource, final Class<? extends SherlockFragment> fragmentClass, final String link){
		super(titleResource);
		this.url = link;
		this.fragmentClass = fragmentClass;
	}

	public final Class<? extends SherlockFragment> getFragmentClass() {
		return fragmentClass;
	}

	public final String getUrl(){
		return url;
	}

}

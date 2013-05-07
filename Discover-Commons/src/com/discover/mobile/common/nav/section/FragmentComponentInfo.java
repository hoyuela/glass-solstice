package com.discover.mobile.common.nav.section;

import javax.annotation.concurrent.Immutable;

import android.view.View.OnClickListener;

import com.actionbarsherlock.app.SherlockFragment;
/**
 * This Component info class accepts a fragment class. The default action for this is to just 
 * transition the fragment into view. 
 * 
 * @author ajleeds
 *
 */
@Immutable
public class FragmentComponentInfo extends ComponentInfo {

	private final Class<? extends SherlockFragment> fragmentClass;

	public FragmentComponentInfo(final int titleResource, final Class<? extends SherlockFragment> fragmentClass) {
		super(titleResource);
		this.fragmentClass = fragmentClass;
	}
	
	public FragmentComponentInfo(final int titleResource, final boolean isicon, final Class<? extends SherlockFragment> fragmentClass) {
		super(titleResource,isicon);
		this.fragmentClass = fragmentClass;
	}

	public FragmentComponentInfo(final int titleResource, final boolean showPushCount, final OnClickListener pushCLickListener, final Class<? extends SherlockFragment> fragmentClass)
	{
		super(titleResource,showPushCount,pushCLickListener);
		this.fragmentClass = fragmentClass;
	}
	public final Class<? extends SherlockFragment> getFragmentClass() {
		return fragmentClass;
	}

}

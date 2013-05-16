package com.discover.mobile.common.nav.section;

import javax.annotation.concurrent.Immutable;

import android.util.Log;
import android.view.View.OnClickListener;
/**
 * Base Component info class. This class is needed so that the other classes can all share the 
 * title resource.
 * 
 * @author ajleeds
 *
 */
@Immutable
public abstract class ComponentInfo {

	private final int titleResource;
	private final boolean isUrl;
	private boolean  showPushCount = false;
	private OnClickListener clickListener;
	
	ComponentInfo(final int titleResource) {
		this(titleResource, false);
	}

	ComponentInfo(final int titleResource, final boolean isUrl) {
		this.titleResource = titleResource;
		this.isUrl = isUrl;
	}

	public ComponentInfo(int titleResource,boolean showPushCount,OnClickListener clickListener)
	{
		this.titleResource = titleResource;
		this.showPushCount = showPushCount;
		this.isUrl = false;
		this.clickListener = clickListener;
	}

	public final int getTitleResource() {
		return titleResource;
	}

	public final boolean getIsExternalLink(){
		return isUrl;
	}

	public final boolean isPushCountAvailable()
	{
		return showPushCount;
	}
	
	public final OnClickListener getPushClick()
	{
		return clickListener;
	}
}

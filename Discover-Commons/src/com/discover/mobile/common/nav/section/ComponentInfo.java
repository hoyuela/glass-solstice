package com.discover.mobile.common.nav.section;

import javax.annotation.concurrent.Immutable;
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

	ComponentInfo(final int titleResource) {
		this(titleResource, false);
	}

	ComponentInfo(final int titleResource, final boolean isUrl) {
		this.titleResource = titleResource;
		this.isUrl = isUrl;
	}


	public final int getTitleResource() {
		return titleResource;
	}

	public final boolean getIsExternalLink(){
		return isUrl;
	}

}

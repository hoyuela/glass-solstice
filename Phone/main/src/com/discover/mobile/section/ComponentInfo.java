package com.discover.mobile.section;

import javax.annotation.concurrent.Immutable;

@Immutable
public abstract class ComponentInfo {

	private final int titleResource;

	ComponentInfo(final int titleResource) {
		this.titleResource = titleResource;
	}


	public final int getTitleResource() {
		return titleResource;
	}

}

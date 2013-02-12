package com.discover.mobile.common.nav.section;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;

@Immutable
public class GroupComponentInfo extends ComponentInfo {

	public static final int NO_SUB_SECTIONS = -1;

	private final ImmutableList<ComponentInfo> subSections;

	public GroupComponentInfo(final int titleResource, final ComponentInfo... subSections) {
		super(titleResource);

		this.subSections = ImmutableList.<ComponentInfo>copyOf(subSections);
	}

	public final ImmutableList<ComponentInfo> getSubSections() {
		return subSections;
	}

}

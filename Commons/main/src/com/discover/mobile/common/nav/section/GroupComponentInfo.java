package com.discover.mobile.common.nav.section;

import javax.annotation.concurrent.Immutable;

import com.google.common.collect.ImmutableList;

@Immutable
public class GroupComponentInfo extends ComponentInfo {

	public static final int NO_SUB_SECTIONS = -1;

	private final ImmutableList<FragmentComponentInfo> subSections;

	public GroupComponentInfo(final int titleResource, final FragmentComponentInfo... subSections) {
		super(titleResource);

		this.subSections = ImmutableList.<FragmentComponentInfo>copyOf(subSections);
	}

	public final ImmutableList<FragmentComponentInfo> getSubSections() {
		return subSections;
	}

}

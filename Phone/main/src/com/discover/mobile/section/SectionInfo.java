package com.discover.mobile.section;

import javax.annotation.concurrent.Immutable;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.common.collect.ImmutableList;

@Immutable
public class SectionInfo extends BaseComponentInfo {
	
	private final ImmutableList<SubSectionInfo> subSections;
	
	public SectionInfo(final int titleResource, final Class<? extends SherlockFragment> fragmentClass,
			final SubSectionInfo... subSections) {
		
		super(titleResource, fragmentClass);
		
		this.subSections = ImmutableList.<SubSectionInfo>copyOf(subSections);
	}
	
	public final ImmutableList<SubSectionInfo> getSubSections() {
		return subSections;
	}
	
}

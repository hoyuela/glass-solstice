package com.discover.mobile.common.nav.section;

import javax.annotation.concurrent.Immutable;

import android.view.View.OnClickListener;

@Immutable
public class ClickComponentInfo extends ComponentInfo {

	private final OnClickListener onClickListener;



	public ClickComponentInfo(final int titleResource, final OnClickListener listener) {
		this(titleResource, false, listener);
	}
	
	public ClickComponentInfo(final int titleResource, final boolean isUrl, final OnClickListener listener){
		super(titleResource, isUrl);
		this.onClickListener = listener;
	}

	public final OnClickListener getOnClickListener() {
		return onClickListener;
	}

}

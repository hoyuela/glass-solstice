/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.ui.modals;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

import com.discover.mobile.bank.R;
import com.discover.mobile.common.ui.modals.ModalTopView;

public class AtmLocatorHelpModalTop extends ScrollView implements ModalTopView {

	public AtmLocatorHelpModalTop(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		final View view = inflate(context, R.layout.bank_atm_modal_top, null);
		addView(view);
	}

	@Override
	public void setTitle(final int resource) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTitle(final String text) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setContent(final int resouce) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setContent(final String content) {
		// TODO Auto-generated method stub

	}



}

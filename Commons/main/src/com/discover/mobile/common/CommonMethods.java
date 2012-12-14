package com.discover.mobile.common;

import android.view.View;
import android.widget.TextView;

public final class CommonMethods {
	public final static void setViewGone(View v) {
		v.setVisibility(View.GONE);
	}
	
	public final static void setViewVisible(View v) {
		v.setVisibility(View.VISIBLE);
	}
	
	public final static void setViewInvisible(final View v) {
		v.setVisibility(View.INVISIBLE);
	}

	public final static void showLabelWithText(TextView label, String text) {
		label.setText(text);
		setViewVisible(label);
	}
	
	private CommonMethods(){
		throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
	}
}

package com.discover.mobile.common.ui.toggle;

import android.graphics.Rect;
import android.view.View;

public interface TransformationMethodCompat {

	public CharSequence getTransformation(CharSequence source, View view);
	
	public void onFocusChanged(View view, CharSequence sourceText, boolean focused, int direction, Rect previouslyFocusedRect);
}

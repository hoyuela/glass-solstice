package com.discover.mobile.common.callback;

import java.lang.ref.WeakReference;
import java.util.Set;

import android.widget.TextView;

import com.discover.mobile.common.callback.GenericCallbackListener.CompletionListener;
import com.google.common.collect.Sets;

class ClearTextFieldsCompletionListener implements CompletionListener {
	
	private final Set<WeakReference<TextView>> textViewRefs;
	
	ClearTextFieldsCompletionListener(final TextView[] textViews) {
		textViewRefs = Sets.newHashSet();
		for(final TextView textView : textViews)
			textViewRefs.add(new WeakReference<TextView>(textView));
	}

	@Override
	public CallbackPriority getCallbackPriority() {
		return CallbackPriority.MIDDLE;
	}

	@Override
	public void complete(final Object result) {
		for(final WeakReference<TextView> textViewRef : textViewRefs) {
			final TextView textView = textViewRef.get();
			if(textView != null)
				textView.setText("");
		}
	}
	
}

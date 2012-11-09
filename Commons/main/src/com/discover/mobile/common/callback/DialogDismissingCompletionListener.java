package com.discover.mobile.common.callback;

import static com.discover.mobile.common.ReferenceUtility.safeGetReferenced;

import java.lang.ref.WeakReference;

import javax.annotation.Nonnull;

import android.app.Dialog;

import com.discover.mobile.common.callback.GenericCallbackListener.CompletionListener;

class DialogDismissingCompletionListener implements CompletionListener {
	
	private final WeakReference<Dialog> dialogRef;
	
	DialogDismissingCompletionListener(final @Nonnull Dialog dialog) {
		dialogRef = new WeakReference<Dialog>(dialog);
	}
	
	@Override
	public CallbackPriority getCallbackPriority() {
		return CallbackPriority.FIRST;
	}
	
	@Override
	public void complete(final Object result) {
		final Dialog dialog = safeGetReferenced(dialogRef);
		if(dialog != null)
			dialog.dismiss();
	}
	
}

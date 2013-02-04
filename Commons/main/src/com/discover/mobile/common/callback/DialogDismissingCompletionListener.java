package com.discover.mobile.common.callback;

import static com.discover.mobile.common.ReferenceUtility.safeGetReferenced;

import java.lang.ref.WeakReference;

import javax.annotation.Nonnull;

import android.app.Dialog;

import com.discover.mobile.common.callback.GenericCallbackListener.CompletionListener;
import com.discover.mobile.common.net.NetworkServiceCall;

public class DialogDismissingCompletionListener implements CompletionListener {
	
	private final WeakReference<Dialog> dialogRef;
	
	public DialogDismissingCompletionListener(final @Nonnull Dialog dialog) {
		dialogRef = new WeakReference<Dialog>(dialog);
	}
	
	@Override
	public CallbackPriority getCallbackPriority() {
		return CallbackPriority.FIRST;
	}
	
	@Override
	public void complete(final NetworkServiceCall<?> sender, final Object result) {
		final Dialog dialog = safeGetReferenced(dialogRef);

		//Dismiss dialog if it is showing
		if (dialog != null && dialog.isShowing()) {
			try {	
				dialog.dismiss();
			} catch (final Exception ex) {
				//This is here to handle the case where an orientation change causes a crash
			}
		}
	}
	
}

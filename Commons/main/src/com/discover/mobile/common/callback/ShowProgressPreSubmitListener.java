package com.discover.mobile.common.callback;

import static com.discover.mobile.common.ThreadUtility.assertMainThreadExecution;

import javax.annotation.Nonnull;

import android.app.ProgressDialog;

import com.discover.mobile.common.callback.GenericCallbackListener.PreSubmitListener;

class ShowProgressPreSubmitListener implements PreSubmitListener {
	
	private final ProgressDialog dialog;
	
	ShowProgressPreSubmitListener(final @Nonnull ProgressDialog dialog) {
		this.dialog = dialog;
	}
	
	@Override
	public Order getOrder() {
		return Order.LAST;
	}

	@Override
	public void preSubmit() {
		assertMainThreadExecution();
		
		dialog.show();
	}
	
}

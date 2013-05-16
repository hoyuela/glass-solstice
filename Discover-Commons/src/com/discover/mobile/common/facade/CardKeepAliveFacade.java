package com.discover.mobile.common.facade;

import android.content.Context;

public interface CardKeepAliveFacade {

	/**
	 * Requests a call be made to Card's session refresh service.
	 */
	public void refreshCardSession(final Context context);

}

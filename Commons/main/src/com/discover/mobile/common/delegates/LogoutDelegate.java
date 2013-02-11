/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.common.delegates;

import android.app.Activity;

import com.discover.mobile.common.error.ErrorHandlerUi;

/**
 * @author ekaram
 *
 */
public interface LogoutDelegate {

	public void logout(Activity fromActivity, ErrorHandlerUi errorUi);
	
}

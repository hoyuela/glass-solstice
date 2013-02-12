/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.common.facade;

import android.app.Activity;

import com.discover.mobile.common.error.ErrorHandlerUi;

/**
 * @author ekaram
 *
 */
public interface LogoutFacade {

	public void logout(Activity fromActivity, ErrorHandlerUi errorUi);
	
}

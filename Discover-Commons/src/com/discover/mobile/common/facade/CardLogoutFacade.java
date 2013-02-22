/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.common.facade;

import android.app.Activity;

import com.discover.mobile.common.AccountType;
import com.discover.mobile.common.error.ErrorHandlerUi;

/**
 * A facade to support common shared logout code 
 * @author ekaram
 *
 */
public interface CardLogoutFacade {

	/**
	 * A common interface for logout since we are sharing the components
	 * 
	 * @param fromActivity - the calling activity 
	 * @param errorUi - the errorUI to use for callback 
	 * @param accountType - card or bank
	 */
	public void logout(Activity fromActivity, ErrorHandlerUi errorUi);
	
}

/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.common.facade;

import android.app.Activity;

import com.discover.mobile.common.AccountType;
import com.discover.mobile.common.error.ErrorHandlerUi;

/**
 * @author ekaram
 *
 */
public class LogoutFacadeImpl implements LogoutFacade{

	public void logout(Activity fromActivity, ErrorHandlerUi errorUi, AccountType accountType){
		
		switch (accountType) {
		case CARD_ACCOUNT:
			FacadeFactory.getCardLogoutFacade().logout(fromActivity, errorUi);
			break;
		case BANK_ACCOUNT:
			FacadeFactory.getBankLogoutFacade().logout(fromActivity, errorUi);
			break;
		}
		return;
		
	}

	
	
	
}

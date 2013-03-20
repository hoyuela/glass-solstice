/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.common.facade;

import android.app.Activity;

import com.discover.mobile.common.AccountType;
import com.discover.mobile.common.auth.KeepAlive;
import com.discover.mobile.common.error.ErrorHandlerUi;

/**
 * @author ekaram
 *
 */
public class LogoutFacadeImpl implements LogoutFacade{

	@Override
	public void logout(Activity fromActivity, ErrorHandlerUi errorUi, AccountType accountType){
		
		switch (accountType) {
		case CARD_ACCOUNT:
			KeepAlive.setCardAuthenticated(false);
			FacadeFactory.getCardLogoutFacade().logout(fromActivity, errorUi);
			break;
		case BANK_ACCOUNT:
			KeepAlive.setBankAuthenticated(false);
			FacadeFactory.getBankLogoutFacade().logout(fromActivity, errorUi);
			break;
		}
		return;
		
	}

	
	
	
}

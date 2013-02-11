/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.common.delegates;

import com.discover.mobile.common.auth.bank.strong.BankStrongAuthDetails;
import com.discover.mobile.common.error.ErrorHandlerUi;

import android.content.Context;

/**
 * 
 * @author ekaram
 *
 */
public interface StrongAuthDelegate {
	
	public void navToCardStrongAuth(final Context context, final String strongAuthQuestion, final String strongAuthQuestionId);
	
	public void navToBankStrongAuth(final Context context);
	
	public void handleBankStrongAuthFailure(final ErrorHandlerUi errorHandlerUi, final String errorMessage,
			final BankStrongAuthDetails details);
	
	
}

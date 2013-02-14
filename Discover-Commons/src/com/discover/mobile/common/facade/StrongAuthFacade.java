/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.common.facade;

import android.content.Context;

/**
 * 
 * @author ekaram
 *
 */
public interface StrongAuthFacade {
	
	public void navToCardStrongAuth(final Context context, final String strongAuthQuestion, final String strongAuthQuestionId);
	
	public void navToBankStrongAuth(final Context context);
	

}

/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.common.delegates;

import android.content.Context;

/**
 * @author ekaram
 *
 */
public interface StrongAuthDelegate {
	
	public void navigateToStrongAuth(final Context context, final String strongAuthQuestion, final String strongAuthQuestionId);

}

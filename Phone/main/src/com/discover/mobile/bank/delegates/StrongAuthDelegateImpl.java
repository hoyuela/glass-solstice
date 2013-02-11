/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.bank.delegates;

import android.content.Context;
import android.content.Intent;

import com.discover.mobile.bank.security.EnhancedAccountSecurityActivity;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.delegates.StrongAuthDelegate;

/**
 * @author ekaram
 * 
 */
public class StrongAuthDelegateImpl implements StrongAuthDelegate {

	public void navigateToStrongAuth(Context context, String strongAuthQuestion, String strongAuthQuestionId) {
		final Intent strongAuth = new Intent(context, EnhancedAccountSecurityActivity.class);

		strongAuth.putExtra(IntentExtraKey.STRONG_AUTH_QUESTION, strongAuthQuestion);
		strongAuth.putExtra(IntentExtraKey.STRONG_AUTH_QUESTION_ID, strongAuthQuestionId);
		
		context.startActivity(strongAuth);

	}

}

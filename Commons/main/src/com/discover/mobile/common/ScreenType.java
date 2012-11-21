package com.discover.mobile.common;

import android.content.Intent;

public enum ScreenType {
	
	MAINTENANCE,
	LOCKED_OUT_USER,
	BAD_ACCOUNT_STATUS,
	FORGOT_PASSWORD,
	FORGOT_ID,
	FORGOT_BOTH,
	UID_STRENGTH_HELP,
	PASSWORD_STRENGTH_HELP,
	STRONG_AUTH_LOCKED_OUT,
	ACCOUNT_LOCKED_FAILED_ATTEMPTS;
	
	public static final String INTENT_KEY = "ScreenType"; //$NON-NLS-1$
	
	public static ScreenType getExtraFromIntent(final Intent intent) {
		return (ScreenType) intent.getExtras().getSerializable(ScreenType.INTENT_KEY);
	}
	
	public void addExtraToIntent(final Intent intent) {
		intent.putExtra(INTENT_KEY, this);
	}
	
}

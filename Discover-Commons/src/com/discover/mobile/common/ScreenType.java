package com.discover.mobile.common;

import android.content.Intent;

public enum ScreenType {
	
	UNSCHEDULED_MAINTENANCE,
	SCHEDULED_MAINTENANCE,
	TEMPORARY_OUTAGE,
	LOCKED_OUT_USER,
	BAD_ACCOUNT_STATUS,
	FORGOT_PASSWORD,
	FORGOT_ID,
	FORGOT_BOTH,
	UID_STRENGTH_HELP,
	PASSWORD_STRENGTH_HELP,
	STRONG_AUTH_LOCKED_OUT,
	STRONG_AUTH_NOT_ENROLLED,
	ACCOUNT_LOCKED_FAILED_ATTEMPTS,
	ACCOUNT_NUMBER_REREGISTERED,
	ACCOUNT_NOT_YET_SETUP,
	ACCOUNT_NUMBER_CHANGED,
	INTERNAL_SERVER_ERROR_500,
	INTERNAL_SERVER_ERROR_503,
	HTTP_FORBIDDEN,
	NO_DATA_FOUND,
	NOT_PRIMARY_CARDHOLDER;
	
	public static final String INTENT_KEY = "ScreenType"; //$NON-NLS-1$
	
	public static ScreenType getExtraFromIntent(final Intent intent) {
		return (ScreenType) intent.getExtras().getSerializable(ScreenType.INTENT_KEY);
	}
	
	public void addExtraToIntent(final Intent intent) {
		intent.putExtra(INTENT_KEY, this);
	}
	
}

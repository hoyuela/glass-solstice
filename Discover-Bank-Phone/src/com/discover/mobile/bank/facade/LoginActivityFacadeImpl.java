/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.bank.facade;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.discover.mobile.bank.login.LockOutUserActivity;
import com.discover.mobile.bank.login.LoginActivity;
import com.discover.mobile.common.BaseFragmentActivity;
import com.discover.mobile.common.IntentExtraKey;
import com.discover.mobile.common.ScreenType;
import com.discover.mobile.common.facade.LoginActivityFacade;

/**
 * A facade for the Login Activity 
 * @author ekaram
 *
 */
public class LoginActivityFacadeImpl implements LoginActivityFacade {
	
	/*
	 * (non-Javadoc)
	 * @see com.discover.mobile.common.facade.LoginActivityFacade#navToLogin(android.content.Context)
	 */
	@Override
	public void navToLogin(final Context context){
		final Intent login = new Intent(context, LoginActivity.class);
		context.startActivity(login);

	}
	
	/*
	 * (non-Javadoc)
	 * @see com.discover.mobile.common.facade.LoginActivityFacade#navToLoginWithMessage(android.app.Activity, android.os.Bundle)
	 */
	@Override
	public void navToLoginWithMessage(final Activity currentActivity, final Bundle bundle){	
		
		// Send an intent to open login activity if current activity is not login
		if (! currentActivity.getClass().equals(LoginActivity.class)){
			
			final Intent intent = new Intent(currentActivity, LoginActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			intent.putExtras(bundle);
			
			currentActivity.startActivity(intent);

			// Close current activity
			currentActivity.finish();
		} else if (bundle != null) {
			if (bundle.containsKey(IntentExtraKey.SESSION_EXPIRED)) {
				((LoginActivity) currentActivity).showSessionExpired();
			} else if (bundle
					.containsKey(IntentExtraKey.SHOW_SUCESSFUL_LOGOUT_MESSAGE)) {
				((LoginActivity) currentActivity).showLogoutSuccessful();
			} else if (bundle.containsKey(IntentExtraKey.ERROR_CODE)) {
				((LoginActivity) currentActivity).showErrorMessage(
						bundle.getString(IntentExtraKey.ERROR_CODE),
						bundle.getString(IntentExtraKey.SHOW_ERROR_MESSAGE));
			} else if (bundle.containsKey(IntentExtraKey.SHOW_ERROR_MESSAGE)) {
				((LoginActivity) currentActivity).showErrorMessage(bundle
						.getString(IntentExtraKey.SHOW_ERROR_MESSAGE));
			} else {
				//clear bad input
				((LoginActivity) currentActivity).clearPasscodeFields();
			}
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.discover.mobile.common.facade.LoginActivityFacade#navToLockoutScreen(android.content.Context, com.discover.mobile.common.ScreenType)
	 */
	@Override
	public void navToLockoutScreen(final Context context, final ScreenType screenType){
		final Intent maintenancePageIntent = new Intent(context, LockOutUserActivity.class);
		screenType.addExtraToIntent(maintenancePageIntent);
		context.startActivity(maintenancePageIntent);
		
	}

	/* (non-Javadoc)
	 * @see com.discover.mobile.common.delegates.LoginFacade#getLoginActivity()
	 */
	@Override
	public BaseFragmentActivity getLoginActivity() {
		return new LoginActivity();
	}

	/* (non-Javadoc)
	 * @see com.discover.mobile.common.delegates.LoginFacade#getLoginActivityClass()
	 */
	@Override
	public Class getLoginActivityClass() {
		return LoginActivity.class;
	}
	


}

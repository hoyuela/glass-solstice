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
import com.discover.mobile.common.BaseActivity;
import com.discover.mobile.common.ScreenType;
import com.discover.mobile.common.facade.LoginFacade;

/**
 * @author ekaram
 *
 */
public class LoginFacadeImpl implements LoginFacade {
	
	public void navToLogin(Context context){
		final Intent login = new Intent(context, LoginActivity.class);
		context.startActivity(login);

	}
	
	public void navToLoginWithMessage(Activity currentActivity, Bundle bundle){	
		
		// Send an intent to open login activity if current activity is not login
		if (currentActivity.getClass().equals(LoginActivity.class)){
			
			final Intent intent = new Intent(currentActivity, LoginActivity.class);
			
			currentActivity.startActivity(intent);

			// Close current activity
			currentActivity.finish();
		} 
		
	}
	
	public void navToLockoutScreen(Context context, ScreenType screenType){
		final Intent maintenancePageIntent = new Intent(context, LockOutUserActivity.class);
		screenType.addExtraToIntent(maintenancePageIntent);
		context.startActivity(maintenancePageIntent);
		
	}

	/* (non-Javadoc)
	 * @see com.discover.mobile.common.delegates.LoginFacade#getLoginActivity()
	 */
	@Override
	public BaseActivity getLoginActivity() {
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

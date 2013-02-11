/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.bank.delegates;

import android.content.Context;
import android.content.Intent;

import com.discover.mobile.bank.login.LockOutUserActivity;
import com.discover.mobile.bank.login.LoginActivity;
import com.discover.mobile.common.ScreenType;

/**
 * @author ekaram
 *
 */
public class LoginDelegateImpl {
	
	public void navToLogin(Context context){
		final Intent login = new Intent(context, LoginActivity.class);
		context.startActivity(login);
		
	}
	
	public void navToLockoutScreen(Context context, ScreenType screenType){
		final Intent maintenancePageIntent = new Intent(context, LockOutUserActivity.class);
		screenType.addExtraToIntent(maintenancePageIntent);
		context.startActivity(maintenancePageIntent);
		
	}

}

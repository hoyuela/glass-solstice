/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.common.delegates;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.discover.mobile.common.BaseActivity;
import com.discover.mobile.common.ScreenType;

/**
 * @author ekaram
 *
 */
public interface LoginDelegate {
	
	public void navToLogin(Context context);
	public void navToLockoutScreen(Context context, ScreenType screenType);
	public Class getLoginActivityClass();
	public BaseActivity getLoginActivity();
	public void navToLoginWithMessage(Activity currentActivity, Bundle bundle);
	
}

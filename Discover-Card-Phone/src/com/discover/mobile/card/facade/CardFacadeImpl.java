/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.card.facade;

import android.app.Activity;
import android.content.Intent;

import com.discover.mobile.card.error.CardErrorHandler;
import com.discover.mobile.card.login.register.ForgotCredentialsActivity;
import com.discover.mobile.card.login.register.RegistrationAccountInformationActivity;
import com.discover.mobile.card.navigation.CardNavigationRootActivity;
import com.discover.mobile.card.services.CardUrlManager;
import com.discover.mobile.common.BaseActivity;
import com.discover.mobile.common.error.ErrorHandler;
import com.discover.mobile.common.facade.CardFacade;

/**
 * The impl class for the card nav facade 
 * @author ekaram
 *
 */
public class CardFacadeImpl implements CardFacade{

	@Override
	public void navToRegister(BaseActivity callingActivity) {
		final Intent newVisibleIntent = new Intent(callingActivity, RegistrationAccountInformationActivity.class);
		callingActivity.startActivity(newVisibleIntent);
		callingActivity.finish();
	}

	@Override
	public void navToForgot(BaseActivity callingActivity) {
		final Intent newVisibleIntent = new Intent(callingActivity, ForgotCredentialsActivity.class);
		callingActivity.startActivity(newVisibleIntent);
		callingActivity.finish();
		
		
	}

	@Override
	public void navToHomeFragment(Activity callingActivity) {
		final Intent strongAuth = new Intent(callingActivity, CardNavigationRootActivity.class);

		callingActivity.startActivityForResult(strongAuth, 0);
		
	}

	@Override
	public ErrorHandler getCardErrorHandler() {
		return CardErrorHandler.getInstance();
	}

	@Override
	public String getPreAuthUrl() {
		return CardUrlManager.getPreAuthUrl();
	}

	@Override
	public void initPhoneGap() {
		//TODO add phone gap initialization code here!
		
	}
	
	

	


	
}

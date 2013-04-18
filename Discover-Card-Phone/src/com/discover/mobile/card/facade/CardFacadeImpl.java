/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.card.facade;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.discover.mobile.card.CardSessionContext;
import com.discover.mobile.card.R;
import com.discover.mobile.card.common.sessiontimer.PageTimeOutUtil;
import com.discover.mobile.card.error.CardErrorHandler;
import com.discover.mobile.card.login.register.ForgotCredentialsActivity;
import com.discover.mobile.card.login.register.RegistrationAccountInformationActivity;
import com.discover.mobile.card.navigation.CardNavigationRootActivity;
import com.discover.mobile.card.services.CardUrlManager;
import com.discover.mobile.card.services.auth.AccountDetails;
import com.discover.mobile.common.BaseActivity;
import com.discover.mobile.common.error.ErrorHandler;
import com.discover.mobile.common.facade.CardFacade;
import com.discover.mobile.common.facade.CardKeepAliveFacade;
import com.discover.mobile.common.ui.CardInfoForToggle;
import com.discover.mobile.card.common.sessiontimer.PageTimeOutUtil;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;

 
/**
 * The impl class for the card nav facade 
 * @author ekaram
 *
 */
public class CardFacadeImpl implements CardFacade, CardKeepAliveFacade{

	private PageTimeOutUtil pagetimeout;	
	
	//private Context context;	
	
	@Override
	public void navToRegister(final BaseActivity callingActivity) {		
		final Intent newVisibleIntent = new Intent(callingActivity, RegistrationAccountInformationActivity.class);
		callingActivity.startActivity(newVisibleIntent);
		callingActivity.finish();
	}

	@Override
	public void navToForgot(final BaseActivity callingActivity) {
		final Intent newVisibleIntent = new Intent(callingActivity, ForgotCredentialsActivity.class);
		callingActivity.startActivity(newVisibleIntent);
		callingActivity.finish();
	}

	@Override
	public void navToHomeFragment(final Activity callingActivity) {
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
	/**
	 * This method will return card member name and last 4 digit of card in  
	 */
	@Override
	public CardInfoForToggle getCardInfoForToggle(Context context) {
		try
		{
			final CardShareDataStore cardShareDataStoreObj = CardShareDataStore.getInstance(context);
			AccountDetails cardHomedata = (AccountDetails)cardShareDataStoreObj.getValueOfAppCache(context.getString(R.string.account_details));		
	    	CardInfoForToggle cardInfo = new CardInfoForToggle();
			cardInfo.setCardEndingDigits(cardHomedata.lastFourAcctNbr);
			cardInfo.setCardAccountName(cardHomedata.primaryCardMember.nameOnCard);
			cardHomedata=null;
			return cardInfo;			
		}
		catch(Exception e)
		{
		  e.printStackTrace();		  	
		}
		return null;		
	}
/**
 * This method will keep on refreshing the session for card from bank side 
 * TODO - Pass the context as an argument to this method call
 */
	@Override
	public void refreshCardSession() {	
		
		/*try{
			//TODO pass context which is received in argument
			
			pagetimeout = new PageTimeOutUtil(context);
			pagetimeout.keepSessionAlive();
		}
		catch(Exception e)
		{
			 e.printStackTrace();
		}*/		
	}
}

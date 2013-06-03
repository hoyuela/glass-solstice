/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.card.facade;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.discover.mobile.card.CardSessionContext;
import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.SessionCookieManager;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;
import com.discover.mobile.card.common.utils.Utils;
import com.discover.mobile.card.error.CardErrorHandler;
import com.discover.mobile.card.fastcheck.FastcheckUtil;
import com.discover.mobile.card.login.register.ForgotCredentialsActivity;
import com.discover.mobile.card.login.register.RegistrationAccountInformationActivity;
import com.discover.mobile.card.navigation.CardNavigationRootActivity;
import com.discover.mobile.card.services.CardUrlManager;
import com.discover.mobile.card.services.auth.AccountDetails;
import com.discover.mobile.common.AccountType;
import com.discover.mobile.common.BaseFragmentActivity;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.error.ErrorHandler;
import com.discover.mobile.common.facade.CardFacade;
import com.discover.mobile.common.facade.LoginActivityInterface;
import com.discover.mobile.common.nav.NavigationRootActivity;
import com.discover.mobile.common.ui.CardInfoForToggle;

/**
 * The impl class for the card nav facade
 * 
 * @author ekaram
 * 
 */
public class CardFacadeImpl implements CardFacade {
    // private Context context;

    @Override
    public void navToRegister(final BaseFragmentActivity callingActivity) {
        final Intent newVisibleIntent = new Intent(callingActivity,
                RegistrationAccountInformationActivity.class);
        callingActivity.startActivity(newVisibleIntent);
      //DEFECT 96355
       // callingActivity.finish();  
      //DEFECT 96355
    }

    @Override
    public void navToForgot(final BaseFragmentActivity callingActivity) {
        final Intent newVisibleIntent = new Intent(callingActivity,
                ForgotCredentialsActivity.class);
        callingActivity.startActivity(newVisibleIntent);
        callingActivity.finish();
    }

    @Override
    public void navToHomeFragment(final Activity callingActivity) {
        final Intent strongAuth = new Intent(callingActivity,
                CardNavigationRootActivity.class);

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
        // TODO add phone gap initialization code here!

    }

    /**
     * This method will return card member name and last 4 digit of card in
     */
    @Override
    public CardInfoForToggle getCardInfoForToggle(final Context context) {
        try {

            final CardInfoForToggle cardInfo = new CardInfoForToggle();

            final CardShareDataStore cardShareDataStoreObj = CardShareDataStore
                    .getInstance(context);
            final AccountDetails cardHomedata = (AccountDetails) cardShareDataStoreObj
                    .getValueOfAppCache(context
                            .getString(R.string.account_details));

           if ((cardHomedata != null) && (cardHomedata.lastFourAcctNbr != null)
					&& (cardHomedata.primaryCardMember.nameOnCard != null)) {
                cardInfo.setCardEndingDigits(cardHomedata.lastFourAcctNbr);
                cardInfo.setCardAccountName(Utils.getCardTypeFromGroupCode(
                        context, cardHomedata.cardProductGroupCode));
            } else {
                // Go to AC Home
                Utils.updateAccountDetails(context, new CardEventListener() {

                    @Override
                    public void onSuccess(final Object data) {
                        // TODO Auto-generated method stub
                        Globals.setLoggedIn(true);
                        final CardShareDataStore cardShareDataStoreObj = CardShareDataStore
                                .getInstance(context);
                        final SessionCookieManager sessionCookieManagerObj = cardShareDataStoreObj
                                .getCookieManagerInstance();
                        sessionCookieManagerObj.setCookieValues();

                        if (context instanceof LoginActivityInterface) {
                            final LoginActivityInterface callingActivity = (LoginActivityInterface) context;
                            callingActivity
                                    .updateAccountInformation(AccountType.CARD_ACCOUNT);
                        }

                        CardSessionContext.getCurrentSessionDetails()
                                .setNotCurrentUserRegisteredForPush(false);
                        CardSessionContext.getCurrentSessionDetails()
                                .setAccountDetails((AccountDetails) data);

                        cardShareDataStoreObj.addToAppCache(
                                context.getString(R.string.account_details),
                                data);
                        final AccountDetails cardHomedata = (AccountDetails) data;
                        cardInfo.setCardEndingDigits(cardHomedata.lastFourAcctNbr);
                        cardInfo.setCardAccountName(Utils
                                .getCardTypeFromGroupCode(context,
                                        cardHomedata.cardProductGroupCode));
                    }

                    @Override
                    public void OnError(final Object data) {
                        // TODO Auto-generated method stub

                    }
                }, "Discover", "Authenticating......");
            }

            return cardInfo;
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    
    
 
    @Override
    public String getPreAuthBaseUrl() {
		return CardUrlManager.getBaseUrl();

    }

    //Defect id 97126
    @Override
    public void navToProvideFeedback(Activity callingActivity) {
        // TODO Auto-generated method stub
        Utils.createProvideFeedbackDialog(callingActivity, "cardLogin-pg");
    }
  //Defect id 97126
    
    @Override
	public boolean fastcheckTokenExists(final NavigationRootActivity callingActivity) {
		String fastcheckToken = FastcheckUtil.readFastcheckToken(callingActivity);
		if (fastcheckToken == null || fastcheckToken.trim().equals("")) return false;
		else return true;
	}

}

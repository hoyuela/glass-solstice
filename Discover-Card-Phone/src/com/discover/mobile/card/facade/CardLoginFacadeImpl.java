/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.card.facade;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.EditText;
import android.widget.TextView;

import com.discover.mobile.card.CardSessionContext;
import com.discover.mobile.card.R;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.SessionCookieManager;
import com.discover.mobile.card.common.net.error.CardErrorBean;
import com.discover.mobile.card.common.net.error.CardErrorResponseHandler;
import com.discover.mobile.card.common.net.error.CardErrorUIWrapper;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.common.net.utility.NetworkUtility;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;
import com.discover.mobile.card.error.CardErrHandler;
import com.discover.mobile.card.error.CardErrorHandlerUi;
import com.discover.mobile.card.navigation.CardNavigationRootActivity;
import com.discover.mobile.card.services.auth.AccountDetails;
import com.discover.mobile.card.services.auth.AuthenticateCall;
import com.discover.mobile.card.services.auth.BankPayload;
import com.discover.mobile.card.services.auth.SSOAuthenticateCall;
import com.discover.mobile.common.AccountType;
import com.discover.mobile.common.BaseActivity;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.callback.GenericAsyncCallback;
import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;
import com.discover.mobile.common.error.BaseExceptionFailureHandler;
import com.discover.mobile.common.facade.CardLoginFacade;
import com.discover.mobile.common.facade.FacadeFactory;
import com.discover.mobile.common.facade.LoginActivityInterface;
import com.discover.mobile.common.net.NetworkServiceCall;

/**
 * The impl class for the card nav facade 
 * @author ekaram
 *
 */
public class CardLoginFacadeImpl implements CardLoginFacade,CardEventListener, CardErrorHandlerUi {
	private Context context;


	@Override
	public void login(final LoginActivityInterface callingActivity, final String username, final String password) {
		final WSRequest request = new WSRequest();
        final String authString = NetworkUtility.getAuthorizationString(
                username, password);
        context = callingActivity.getContext();

        // Setting the headers available for the service
        final HashMap<String, String> headers = request.getHeaderValues();
        headers.put("Authorization", authString);
        headers.put("X-Override-UID", "true");

        final String url = NetworkUtility.getWebServiceUrl(context,
                R.string.login_url);
        request.setUrl(url);
        request.setHeaderValues(headers);

        final WSAsyncCallTask serviceCall = new WSAsyncCallTask(context,
                new AccountDetails(), "Discover", "Authenticating...", this);
        serviceCall.execute(request);

		
	}

	@Override
	public void loginWithPayload(final LoginActivityInterface callingActivity, final String tokenValue, final String hashedTokenValue) {
		final AsyncCallback<BankPayload> callback = GenericAsyncCallback
				.<BankPayload> builder((Activity) callingActivity)
				.showProgressDialog("Discover", "Loading...", true)
				.withSuccessListener(new SuccessListener<BankPayload>() {

					@Override
					public CallbackPriority getCallbackPriority() {
						return CallbackPriority.MIDDLE;
					}

					@Override
					public void success(final NetworkServiceCall<?> sender, final BankPayload value) {
						// Continues the SSO daisy chain by returning control back to Bank.
						FacadeFactory.getBankLoginFacade().authorizeWithBankPayload(value.payload);

					}
				})
				.withErrorResponseHandler(new com.discover.mobile.card.error.CardBaseErrorResponseHandler(callingActivity))
				.withExceptionFailureHandler(new BaseExceptionFailureHandler())
				.build();

		new SSOAuthenticateCall((Context) callingActivity, callback, tokenValue, hashedTokenValue).submit();
	}

	@Override
	public void toggleLoginToBank() {
		// TODO Card needs to contact their end-point to get a Bank payload.

		// TODO This returned payload is sent to the following place which
		// should handle the rest.
		FacadeFactory.getBankLoginFacade().authorizeWithBankPayload(
				"The payload string here");
	}

	@Override
	public void toggleToCard(final Context arg0) {
		// TODO The Card side was already authenticated during Bank Login and
		// kept alive via refresh calls.

	}

	@Override
	public void OnError(Object data) {
		   CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler((CardErrorHandlerUi)this);
	        cardErrorResHandler.handleCardError((CardErrorBean) data);

		
	}

	@Override
	public void onSuccess(Object data) {
		Globals.setLoggedIn(true);
        final CardShareDataStore cardShareDataStoreObj = CardShareDataStore.getInstance(context);
        final SessionCookieManager sessionCookieManagerObj = cardShareDataStoreObj.getCookieManagerInstance();
        sessionCookieManagerObj.setCookieValues();

        final LoginActivityInterface callingActivity = (LoginActivityInterface) context;

        callingActivity.updateAccountInformation(AccountType.CARD_ACCOUNT);

        CardSessionContext.getCurrentSessionDetails()
                .setNotCurrentUserRegisteredForPush(false);
        CardSessionContext.getCurrentSessionDetails().setAccountDetails(
                (AccountDetails) data);

        cardShareDataStoreObj.addToAppCache(context.getString(R.string.account_details), (AccountDetails)data);
        final Intent confirmationScreen = new Intent(context,
                CardNavigationRootActivity.class);
        TrackingHelper.trackPageView(AnalyticsPage.CARD_LOGIN);

        context.startActivity(confirmationScreen);
        
        // Close current activity
        if(context instanceof Activity)
            ((Activity)context).finish();

		
	}

	@Override
	public TextView getErrorLabel() {
		// TODO Auto-generated method stub
		 return ((com.discover.mobile.common.error.ErrorHandlerUi)context).getErrorLabel();

	}

	@Override
	public List<EditText> getInputFields() {
		// TODO Auto-generated method stub
		return ((com.discover.mobile.common.error.ErrorHandlerUi)context).getInputFields();

	}

	@Override
	public void showCustomAlert(AlertDialog alert) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showOneButtonAlert(int title, int content, int buttonText) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void showDynamicOneButtonAlert(int title, String content,
			int buttonText) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Context getContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setLastError(int errorCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getLastError() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public CardErrHandler getCardErrorHandler() {
		// TODO Auto-generated method stub
		return CardErrorUIWrapper.getInstance();

	}
	
}
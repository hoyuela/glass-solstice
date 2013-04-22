/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.card.facade;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.discover.mobile.card.CardSessionContext;
import com.discover.mobile.card.R;
import com.discover.mobile.card.auth.strong.StrongAuthHandler;
import com.discover.mobile.card.auth.strong.StrongAuthListener;
import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.SessionCookieManager;
import com.discover.mobile.card.common.net.error.CardErrorBean;
import com.discover.mobile.card.common.net.error.CardErrorCallbackListener;
import com.discover.mobile.card.common.net.error.CardErrorResponseHandler;
import com.discover.mobile.card.common.net.error.CardErrorUIWrapper;
import com.discover.mobile.card.common.net.error.CardErrorUtil;
import com.discover.mobile.card.common.net.service.WSAsyncCallTask;
import com.discover.mobile.card.common.net.service.WSRequest;
import com.discover.mobile.card.common.net.utility.NetworkUtility;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;
import com.discover.mobile.card.common.utils.Utils;
import com.discover.mobile.card.error.CardErrHandler;
import com.discover.mobile.card.error.CardErrorHandlerUi;
import com.discover.mobile.card.login.register.RegistrationAccountInformationActivity;
import com.discover.mobile.card.navigation.CardNavigationRootActivity;
import com.discover.mobile.card.services.auth.AccountDetails;
import com.discover.mobile.card.services.auth.BankPayload;
import com.discover.mobile.card.services.auth.SSOAuthenticate;
import com.discover.mobile.common.AccountType;
import com.discover.mobile.common.Globals;
import com.discover.mobile.common.analytics.AnalyticsPage;
import com.discover.mobile.common.analytics.TrackingHelper;
import com.discover.mobile.common.facade.CardLoginFacade;
import com.discover.mobile.common.facade.FacadeFactory;
import com.discover.mobile.common.facade.LoginActivityInterface;

/**
 * The impl class for the card nav facade
 * 
 * @author ekaram
 * 
 */
public class CardLoginFacadeImpl implements CardLoginFacade, CardEventListener, CardErrorHandlerUi
{
	private Context context;
	private CardEventListener SSOCardEventListener;
	private StrongAuthListener authListener;
	private final String LOG_TAG = CardLoginFacadeImpl.class.getSimpleName();
	private String bankPayloadText;
	private String userId;
	private boolean uidIsAccountNumber = false;
	private StrongAuthListener listener;
	private boolean showToggleFlag = false;;
	private final int SA_LOCKED = 1402;
	private final int NOT_ENROLLED = 1401;
	private final int SSN_NOT_MATCHED = 1111;
	private final int SSO_ERROR_FLAG = 1102;
	private final int SSO_SSN_MATCHED = 1106;
	private final String NOT_ENROLLED_MSG = "NOTENROLLED";
	private final String SA_LOCKED_MSG = "LOCKOUT";
	private WSRequest request;

	@Override
	public void login(final LoginActivityInterface callingActivity, final String username, final String password)
	{
		request = new WSRequest();
		final String authString = NetworkUtility.getAuthorizationString(username, password);
		context = callingActivity.getContext();

		// Setting the headers available for the service
		final HashMap<String, String> headers = request.getHeaderValues();
		headers.put("Authorization", authString);
		// headers.put("X-Override-UID", "true");

		final String url = NetworkUtility.getWebServiceUrl(context, R.string.login_url);
		request.setUrl(url);
		request.setHeaderValues(headers);
		request.setUsername(username);
		request.setPassword(password);
		// /request.setCookieHander(); //do not use
		final WSAsyncCallTask serviceCall = new WSAsyncCallTask(context, new AccountDetails(), "Discover", "Authenticating...", this);
		serviceCall.execute(request);

		listener = new StrongAuthListener()
		{

			@Override
			public void onStrongAuthSucess(final Object data)
			{
				// TODO Auto-generated method stub

				// TODO Auto-generated method stub
				final Intent confirmationScreen = new Intent(context, CardNavigationRootActivity.class);
				confirmationScreen.putExtra("showToggleFlag", showToggleFlag);
				TrackingHelper.trackPageView(AnalyticsPage.CARD_LOGIN);
				context.startActivity(confirmationScreen);
				// Close current activity
				if (context instanceof Activity) {
					((Activity) context).finish();
				}

			}

			@Override
			public void onStrongAuthError(final Object data)
			{
				// TODO Auto-generated method stub

				final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(CardLoginFacadeImpl.this);
				cardErrorResHandler.handleCardError((CardErrorBean) data);

			}

			@Override
			public void onStrongAuthCardLock(final Object data)
			{
				// TODO Auto-generated method stub
				final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(CardLoginFacadeImpl.this);
				cardErrorResHandler.handleCardError((CardErrorBean) data);
			}

			@Override
			public void onStrongAuthSkipped(final Object data)
			{
				// TODO Auto-generated method stub
				final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(CardLoginFacadeImpl.this);
				cardErrorResHandler.handleCardError((CardErrorBean) data, new CardErrorCallbackListener()
				{

					@Override
					public void onButton2Pressed()
					{
						// TODO Auto-generated method stub

					}

					@Override
					public void onButton1Pressed()
					{
						// Go to AC Home
						// getAcHome();
					}
				});
			}

			@Override
			public void onStrongAuthNotEnrolled(final Object data)
			{

				final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(CardLoginFacadeImpl.this);
				cardErrorResHandler.handleCardError((CardErrorBean) data, new CardErrorCallbackListener()
				{

					@Override
					public void onButton2Pressed()
					{
						// TODO Auto-generated method stub

					}

					@Override
					public void onButton1Pressed()
					{
						// Go to Big Browser
						final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.discover.com"));
						context.startActivity(browserIntent);
					}
				});

			}
		};
	}

	@Override
	public void loginWithPayload(final LoginActivityInterface callingActivity, final String tokenValue, final String hashedTokenValue)
	{
		context = callingActivity.getContext();
		Log.i(LOG_TAG, " tokenValue " + tokenValue + " hashedTokenValue " + hashedTokenValue);

		// Listen SSO service call
		SSOCardEventListener = new CardEventListener()
		{
			@Override
			public void onSuccess(final Object data)
			{
				// Get the bankpayload and return to bank via facade
				final BankPayload bankPayload = (BankPayload) data;
				Log.i(LOG_TAG, "---payload-- " + bankPayload.payload);
				FacadeFactory.getBankLoginFacade().authorizeWithBankPayload(bankPayload.payload);
			}

			@Override
			public void OnError(final Object data)
			{
				final CardErrorBean cardErrorBean = (CardErrorBean) data;
				final CardShareDataStore cardShareDataStore = CardShareDataStore.getInstance(context);
				final String cache = (String) cardShareDataStore.getValueOfAppCache("WWW-Authenticate");

				Log.i(LOG_TAG, "--cache--" + cache + " cardErrorBean " + cardErrorBean.getErrorCode());

				// If error code is 401 and cache contains challenge
				// then show strong auth question
				if (cardErrorBean.getErrorCode().contains("" + HttpURLConnection.HTTP_UNAUTHORIZED) && 
						cache != null && cache.contains("challenge"))
				{
					cardShareDataStore.deleteCacheObject("WWW-Authenticate");

					// Check if it's required strong authentication. Skip check for SA
					final StrongAuthHandler authHandler = new StrongAuthHandler(callingActivity.getContext(), authListener,true);
					authHandler.strongAuth();
				}
				else if (cardErrorBean.getErrorCode().contains("" + HttpURLConnection.HTTP_UNAUTHORIZED) && 
						cache != null && cache.contains("skipped"))
				{
					authListener.onStrongAuthSkipped(cardErrorBean);
				}
				else if(cardErrorBean.getErrorCode().contains("" + HttpURLConnection.HTTP_FORBIDDEN)
						&& cardErrorBean.getErrorCode().contains(""+SA_LOCKED)
						& cardErrorBean.getErrorCode().contains("" +SA_LOCKED_MSG))
				{
					authListener.onStrongAuthCardLock(cardErrorBean);
				}
				else if(cardErrorBean.getErrorCode().contains("" + HttpURLConnection.HTTP_FORBIDDEN)
						&& cardErrorBean.getErrorCode().contains(""+NOT_ENROLLED)
						&& cardErrorBean.getErrorCode().contains("" +NOT_ENROLLED_MSG))
				{
					Log.i(LOG_TAG, "--NOt Enrolled --");
					authListener.onStrongAuthNotEnrolled(cardErrorBean);
				}
				else if (cardErrorBean.getErrorCode().contains("" + HttpURLConnection.HTTP_FORBIDDEN) && 
						(cardErrorBean.getErrorCode().contains(""+SSO_ERROR_FLAG) || 
								cardErrorBean.getErrorCode().contains(""+SSO_SSN_MATCHED)))
				{
					final boolean isSSODLinkable = cardErrorBean.getIsSSODelinkable();
					final boolean isSSOUser = cardErrorBean.getIsSSOUser();
					final boolean isSSNMatch = cardErrorBean.getIsSSNMatched();

					Log.i(LOG_TAG, "isSSODLinkable " + isSSODLinkable + " isSSOUser " + isSSOUser + " isSSNMatch " + isSSNMatch);

					// Get error model based on error flags
					getErrorMatchModelForPayload(isSSOUser, isSSNMatch, isSSODLinkable, cardErrorBean);
				}

				//If SSN not Matched
				else if(cardErrorBean.getErrorCode().contains("" + HttpURLConnection.HTTP_FORBIDDEN)
						&& cardErrorBean.getErrorCode().contains(""+SSN_NOT_MATCHED))
				{
					//SSN does not matched. Show SSN not match model
					getErrorMatchModelForPayload(false, false, false, cardErrorBean);
				}
				else
				{
					final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(CardLoginFacadeImpl.this);
					cardErrorResHandler.handleCardError((CardErrorBean) data);
				}
			}
		};

		authListener = new StrongAuthListener()
		{

			@Override
			public void onStrongAuthSucess(final Object data)
			{
				// Calling service for SSO authentication
				getSSOAuthenticationWithoutToken();
			}

			@Override
			public void onStrongAuthError(final Object data)
			{
				// TODO Auto-generated method stub
				final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(CardLoginFacadeImpl.this);
				cardErrorResHandler.handleCardError((CardErrorBean) data);
			}

			@Override
			public void onStrongAuthCardLock(final Object data)
			{
				// TODO Auto-generated method stub
				final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(CardLoginFacadeImpl.this);
				cardErrorResHandler.handleCardError((CardErrorBean) data);
			}

			@Override
			public void onStrongAuthSkipped(final Object data)
			{
				// TODO Auto-generated method stub
				final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(CardLoginFacadeImpl.this);
				cardErrorResHandler.handleCardError((CardErrorBean) data, new CardErrorCallbackListener()
				{

					@Override
					public void onButton2Pressed()
					{
						// TODO Auto-generated method stub

					}

					@Override
					public void onButton1Pressed()
					{
						// Go to AC Home
						getAcHome();
					}
				});
			}

			@Override
			public void onStrongAuthNotEnrolled(final Object data)
			{

				Log.i(LOG_TAG, "--NOt Enrolled 1--");
				final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(CardLoginFacadeImpl.this);
				cardErrorResHandler.handleCardError((CardErrorBean) data, new CardErrorCallbackListener()
				{

					@Override
					public void onButton2Pressed()
					{
						// TODO Auto-generated method stub

					}

					@Override
					public void onButton1Pressed()
					{
						Log.i(LOG_TAG, "--NOt Enrolled 2--");
						// Go to Big Browser
						final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.discover.com"));
						context.startActivity(browserIntent);
					}
				});

			}
		};

		Log.i(LOG_TAG, "---SSoAuthenticate---");
		final SSOAuthenticate authenticate = new SSOAuthenticate(callingActivity.getContext(), SSOCardEventListener);
		authenticate.sendRequest(tokenValue, hashedTokenValue);
	}

	@Override
	public void toggleLoginToBank(final Context context)
	{
		this.context = context;
		final CardEventListener cardEventListener = new CardEventListener()
		{

			@Override
			public void onSuccess(final Object data)
			{
				// Set Payload data
				// Get the bankpayload and return to bank via facade

				final BankPayload bankPayload = (BankPayload) data;
				bankPayloadText = bankPayload.payload;
				Log.i("Payload from Bank.....", "" + bankPayloadText);

				if (bankPayloadText == null)
				{
					final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(CardLoginFacadeImpl.this);
					final CardErrorCallbackListener errorClickCallback = new CardErrorCallbackListener()
					{
						@Override
						public void onButton1Pressed()
						{
							// Register Button click flow - Register step-1
							final Intent registrationActivity = new Intent(context, RegistrationAccountInformationActivity.class);
							context.startActivity(registrationActivity);
						}

						@Override
						public void onButton2Pressed()
						{
							// Handled automatically
						}
					};
					final CardErrorUtil cardErrUtil = new CardErrorUtil(context);
					final String errorMessage = cardErrUtil.getMessageforErrorCode("40311021");
					final String errorTitle = cardErrUtil.getTitleforErrorCode("1401_LOCKOUT");
					final CardErrorBean cardErrorBean = new CardErrorBean(errorTitle, errorMessage, "40311023", false, "101");
					cardErrorResHandler.handleCardError(cardErrorBean, errorClickCallback);

				}
				else
				{
					FacadeFactory.getBankLoginFacade().authorizeWithBankPayload(bankPayloadText);
				}
			}

			@Override
			public void OnError(final Object data)
			{
				// SSN NOT MATCH HANDLED
				final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(CardLoginFacadeImpl.this);
				cardErrorResHandler.handleCardError((CardErrorBean) data);

			}
		};

		// Get payload from server
		final CardShareDataStore cardShareDataStoreObj = CardShareDataStore.getInstance(context);
		final AccountDetails accountDetails = (AccountDetails) cardShareDataStoreObj.getValueOfAppCache(context.getString(R.string.account_details));
		if (accountDetails.isSSNMatched)
		{
			getBankPayloadFromServer(cardEventListener);
		}
		else
		{
			Globals.setCurrentAccount(AccountType.CARD_ACCOUNT);
			final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(CardLoginFacadeImpl.this);
			final CardErrorUtil cardErrorUtil = new CardErrorUtil(context);
			final CardErrorBean bean = new CardErrorBean(cardErrorUtil.getTitleforErrorCode("4031102_SSN_NOT_MATCH"), cardErrorUtil.getMessageforErrorCode("4031102_SSN_NOT_MATCH"), "4031102", false, "0");
			cardErrorResHandler.handleCardError(bean);
		}

	}

	/**
	 * This methods make a GET call and get payload data from server
	 * 
	 * @param listener
	 */
	public void getBankPayloadFromServer(final CardEventListener listener)
	{

		final WSRequest request = new WSRequest();
		final HashMap<String, String> headers = request.getHeaderValues();
		final String url = NetworkUtility.getWebServiceUrl(context, R.string.sso_authenticate_bank_payload);

		request.setUrl(url);
		request.setHeaderValues(headers);

		final WSAsyncCallTask serviceCall = new WSAsyncCallTask(context, new BankPayload(), "Discover", "Authenticating...", listener);
		serviceCall.execute(request);

	}

	@Override
	public void toggleToCard(final Context context)
	{

		Utils.updateAccountDetails(context, new CardEventListener()
		{

			@Override
			public void onSuccess(final Object data)
			{
				// TODO Auto-generated method stub
				Globals.setLoggedIn(true);
				final CardShareDataStore cardShareDataStoreObj = CardShareDataStore.getInstance(context);
				final SessionCookieManager sessionCookieManagerObj = cardShareDataStoreObj.getCookieManagerInstance();
				sessionCookieManagerObj.setCookieValues();

				/*
				 * final LoginActivityInterface callingActivity = (A) context;
				 * 
				 * callingActivity
				 * .updateAccountInformation(AccountType.CARD_ACCOUNT);
				 */

				CardSessionContext.getCurrentSessionDetails().setNotCurrentUserRegisteredForPush(false);
				CardSessionContext.getCurrentSessionDetails().setAccountDetails((AccountDetails) data);

				cardShareDataStoreObj.addToAppCache(context.getString(R.string.account_details), data);
				final Intent confirmationScreen = new Intent(context, CardNavigationRootActivity.class);
				TrackingHelper.trackPageView(AnalyticsPage.CARD_LOGIN);

				Globals.setCurrentAccount(AccountType.CARD_ACCOUNT);

				showToggleFlag = true;
				confirmationScreen.putExtra("showToggleFlag", showToggleFlag);

				context.startActivity(confirmationScreen);

				// Close current activity
				if (context instanceof Activity) {
					((Activity) context).finish();
				}

			}

			@Override
			public void OnError(final Object data)
			{
				// TODO Auto-generated method stub
				final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(CardLoginFacadeImpl.this);
				cardErrorResHandler.handleCardError((CardErrorBean) data);

			}
		}, "Discover", "Authenticating......");
	}

	@Override
	public void OnError(final Object data)
	{
		final CardErrorBean bean = (CardErrorBean) data;
		boolean ssoUser = false;
		boolean delinkable = false;
		final boolean isSSNMatched = bean.getIsSSNMatched();
		final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(this);
		final String statusCode = bean.getErrorCode();
		Log.d("status code", "statusCode---" + statusCode);
		bean.getErrorMessage();
		delinkable = bean.getIsSSODelinkable();
		ssoUser = bean.getIsSSOUser();

		final CardShareDataStore cardShareDataStore = CardShareDataStore.getInstance(context);
		final String cache = (String) cardShareDataStore.getValueOfAppCache("WWW-Authenticate");
		if (statusCode.equalsIgnoreCase("4031102") || statusCode.equalsIgnoreCase("4031106"))
		{
			if (ssoUser && !delinkable) // A/L/U status
			{
				if (isSSNMatched)
				{ // Bank call for Auth
					FacadeFactory.getBankLoginFacade().authDueToALUStatus(request.getUsername(), request.getPassword());
				}
				else
				{
					// Show SSN not matched modal

					final CardErrorUtil cardErrorUtil = new CardErrorUtil(context);
					final CardErrorBean beanError = new CardErrorBean(cardErrorUtil.getTitleforErrorCode("4031102_SSN_NOT_MATCH"), cardErrorUtil.getMessageforErrorCode("4031102_SSN_NOT_MATCH"), "4031102",
							false, "0");
					cardErrorResHandler.handleCardError(beanError);
				}

			}
			else if (ssoUser && delinkable)// ZB status
			{
				final CardErrorCallbackListener errorClickCallback = new CardErrorCallbackListener()
				{
					@Override
					public void onButton1Pressed()
					{
						// Register Button click flow - Big browser link
						final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.discover.com"));
						context.startActivity(browserIntent);
					}

					@Override
					public void onButton2Pressed()
					{
						// TODO Cancel Button click flow
					}
				};
				final CardErrorBean cardErrorBean = (CardErrorBean) data;
				cardErrorBean.setErrorCode("40311022");
				cardErrorBean.setFooterStatus("101");
				cardErrorResHandler.handleCardError(cardErrorBean, errorClickCallback);

			}
		}
		//CardErrorBean cardErrorBean = (CardErrorBean) data;

		// If error code is 401 and cache contains challenge
		// then show strong auth question
		else if (bean.getErrorCode().contains("" + HttpURLConnection.HTTP_UNAUTHORIZED) && 
				cache != null && cache.contains("challenge"))
		{
			cardShareDataStore.deleteCacheObject("WWW-Authenticate");

			// Check if it's required strong authentication. Skip check for SA
			final StrongAuthHandler authHandler = new StrongAuthHandler(context, listener,true);
			authHandler.strongAuth();
		}
		else if (bean.getErrorCode().contains("" + HttpURLConnection.HTTP_UNAUTHORIZED) && 
				cache != null && cache.contains("skipped"))
		{
			listener.onStrongAuthSkipped(bean);
		}
		else if(bean.getErrorCode().contains("" + HttpURLConnection.HTTP_FORBIDDEN)
				&& bean.getErrorCode().contains(""+SA_LOCKED)
				& bean.getErrorCode().contains("" +SA_LOCKED_MSG))
		{
			listener.onStrongAuthCardLock(bean);
		}
		else if(bean.getErrorCode().contains("" + HttpURLConnection.HTTP_FORBIDDEN)
				&& bean.getErrorCode().contains(""+NOT_ENROLLED)
				&& bean.getErrorCode().contains("" +NOT_ENROLLED_MSG))
		{
			listener.onStrongAuthNotEnrolled(bean);
		}
		else
		{
			// CardErrorResponseHandler cardErrorResHandler = new
			// CardErrorResponseHandler((CardErrorHandlerUi)this);
			cardErrorResHandler.handleCardError((CardErrorBean) data);
		}
	}

	@Override
	public void onSuccess(final Object data)
	{
		Globals.setLoggedIn(true);
		final CardShareDataStore cardShareDataStoreObj = CardShareDataStore.getInstance(context);
		final SessionCookieManager sessionCookieManagerObj = cardShareDataStoreObj.getCookieManagerInstance();
		sessionCookieManagerObj.setCookieValues();
		final LoginActivityInterface callingActivity = (LoginActivityInterface) context;
		callingActivity.updateAccountInformation(AccountType.CARD_ACCOUNT);
		CardSessionContext.getCurrentSessionDetails().setNotCurrentUserRegisteredForPush(false);
		CardSessionContext.getCurrentSessionDetails().setAccountDetails((AccountDetails) data);
		cardShareDataStoreObj.addToAppCache(context.getString(R.string.account_details), data);

		final boolean isSSOUserVar = ((AccountDetails) data).isSSOUser;

		uidIsAccountNumber = Utils.validateUserforSSO(userId);
		if (uidIsAccountNumber && isSSOUserVar)
		{// Cannot Login with Account no
			// modal
			final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(this);
			final CardErrorCallbackListener errorClickCallback = new CardErrorCallbackListener()
			{
				@Override
				public void onButton1Pressed()
				{
					// Register Button click flow - Register step-1
					final Intent registrationActivity = new Intent(context, RegistrationAccountInformationActivity.class);
					context.startActivity(registrationActivity);
				}

				@Override
				public void onButton2Pressed()
				{
					// Handled automatically
				}
			};
			final CardErrorUtil cardErrUtil = new CardErrorUtil(context);
			final String errorMessage = cardErrUtil.getMessageforErrorCode("4031102_SSO_AccountNo");
			final String errorTitle = cardErrUtil.getTitleforErrorCode("1401_LOCKOUT");
			final CardErrorBean cardErrorBean = new CardErrorBean(errorTitle, errorMessage, "40311023", false, "101");
			cardErrorResHandler.handleCardError(cardErrorBean, errorClickCallback);
		}
		else if (!uidIsAccountNumber && isSSOUserVar)
		{ // Strong Auth flow
			CardSessionContext.getCurrentSessionDetails().setNotCurrentUserRegisteredForPush(false);
			CardSessionContext.getCurrentSessionDetails().setAccountDetails((AccountDetails) data);
			cardShareDataStoreObj.addToAppCache(context.getString(R.string.account_details), data);
			if (shouldShowSSOToggle(data))
			{
				showToggleFlag = true;

				//Strong auth need. Done skip checking with server if SA required or not.
				final StrongAuthHandler authHandler = new StrongAuthHandler(context, listener,false);
				authHandler.strongAuth();
			}
		}
		else
			// if(!isSSOUserVar)
		{ // Card normal flow
			final Intent confirmationScreen = new Intent(context, CardNavigationRootActivity.class);
			confirmationScreen.putExtra("showToggleFlag", showToggleFlag);
			TrackingHelper.trackPageView(AnalyticsPage.CARD_LOGIN);
			context.startActivity(confirmationScreen);
			if (context instanceof Activity) {
				((Activity) context).finish();
			}
		}
	}

	/**
	 * This method will decide whether we need to show SSO toggle button or not
	 * 
	 * @param acHome
	 * @return
	 */
	public boolean shouldShowSSOToggle(final Object acHome)
	{
		final boolean isSSOUserVar = ((AccountDetails) acHome).isSSOUser;
		final String payLoadSSOTextVar = ((AccountDetails) acHome).payLoadSSOText;
		if (isSSOUserVar && payLoadSSOTextVar != null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public TextView getErrorLabel()
	{
		// TODO Auto-generated method stub
		return ((com.discover.mobile.common.error.ErrorHandlerUi) context).getErrorLabel();

	}

	@Override
	public List<EditText> getInputFields()
	{
		// TODO Auto-generated method stub
		return ((com.discover.mobile.common.error.ErrorHandlerUi) context).getInputFields();

	}

	@Override
	public void showCustomAlert(final AlertDialog alert)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void showOneButtonAlert(final int title, final int content, final int buttonText)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void showDynamicOneButtonAlert(final int title, final String content, final int buttonText)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Context getContext()
	{
		// TODO Auto-generated method stub
		return context;
	}

	@Override
	public void setLastError(final int errorCode)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public int getLastError()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public CardErrHandler getCardErrorHandler()
	{
		// TODO Auto-generated method stub
		return CardErrorUIWrapper.getInstance();

	}

	/**
	 * Based on Flag, this method will show error models and nevigate to AC Home
	 * 
	 * @param isSSOUser
	 * @param isSSNMatch
	 * @param isSSODLinkable
	 * @param cardErrBean
	 */
	public void getErrorMatchModelForPayload(final boolean isSSOUser, final boolean isSSNMatch, final boolean isSSODLinkable, final CardErrorBean cardErrBean)
	{
		// ALU status
		if (isSSOUser && !isSSODLinkable)
		{
			// If SSN is matched redirect to bank
			if (isSSNMatch)
			{
				FacadeFactory.getBankLoginFacade().authDueToALUStatus();
			}

			// If SSN is not matched then display error and go
			// to login page
			else
			{
				// SSN not match model
				final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(CardLoginFacadeImpl.this);
				final CardErrorUtil cardErrorUtil = new CardErrorUtil(context);
				final CardErrorBean bean = new CardErrorBean(cardErrorUtil.getTitleforErrorCode("4031102_SSN_NOT_MATCH"), cardErrorUtil.getMessageforErrorCode("4031102_SSN_NOT_MATCH"),
						cardErrBean.getErrorCode(), false, cardErrBean.getNeedHelpFooter());
				cardErrorResHandler.handleCardError(bean);
			}
		}

		// ZB status
		else if (isSSOUser && isSSODLinkable)
		{
			// Show alert for ZB status
			cardErrBean.setFooterStatus("101");
			final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(CardLoginFacadeImpl.this);
			cardErrorResHandler.handleCardError(cardErrBean, new CardErrorCallbackListener()
			{

				@Override
				public void onButton2Pressed()
				{
					// TODO Auto-generated method stub

				}

				@Override
				public void onButton1Pressed()
				{
					// Big Broweser
					final Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.discover.com"));
					context.startActivity(browserIntent);
				}
			});
		}

		// SSN not matched
		else if (!isSSNMatch)
		{
			// Show SSN Error model
			final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(CardLoginFacadeImpl.this);
			final CardErrorUtil cardErrorUtil = new CardErrorUtil(context);
			final CardErrorBean bean = new CardErrorBean(cardErrorUtil.getTitleforErrorCode("4031102_SSN_NOT_MATCH"), cardErrorUtil.getMessageforErrorCode("4031102_SSN_NOT_MATCH"),
					cardErrBean.getErrorCode(), false, cardErrBean.getNeedHelpFooter());
			cardErrorResHandler.handleCardError(bean, new CardErrorCallbackListener()
			{

				@Override
				public void onButton2Pressed()
				{
					// TODO Auto-generated method stub

				}

				@Override
				public void onButton1Pressed()
				{

					// Go to AC HOME
					getAcHome();
				}
			});
		}
	}

	/**
	 * This will call SSO Authentication service without sending Authentication
	 * header
	 */
	public void getSSOAuthenticationWithoutToken()
	{
		final CardEventListener cardEventListener = new CardEventListener()
		{

			@Override
			public void onSuccess(final Object data)
			{

				// Get the bankpayload and return to bank via facade
				final BankPayload bankPayload = (BankPayload) data;
				Log.i(LOG_TAG, "---payload-- " + bankPayload.payload);
				FacadeFactory.getBankLoginFacade().authorizeWithBankPayload(bankPayload.payload);
			}

			@Override
			public void OnError(final Object data)
			{
				final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(CardLoginFacadeImpl.this);
				cardErrorResHandler.handleCardError((CardErrorBean) data);
			}
		};

		final SSOAuthenticate authenticate = new SSOAuthenticate(context, cardEventListener);
		authenticate.sendRequest(null, null);
	}

	/**
	 * Get account information from server and go to AC Home
	 */
	public void getAcHome()
	{
		// Go to AC Home
		Utils.updateAccountDetails(context, new CardEventListener()
		{

			@Override
			public void onSuccess(final Object data)
			{
				// TODO Auto-generated method stub
				Globals.setLoggedIn(true);
				final CardShareDataStore cardShareDataStoreObj = CardShareDataStore.getInstance(context);
				final SessionCookieManager sessionCookieManagerObj = cardShareDataStoreObj.getCookieManagerInstance();
				sessionCookieManagerObj.setCookieValues();

				final LoginActivityInterface callingActivity = (LoginActivityInterface) context;

				callingActivity.updateAccountInformation(AccountType.CARD_ACCOUNT);

				CardSessionContext.getCurrentSessionDetails().setNotCurrentUserRegisteredForPush(false);
				CardSessionContext.getCurrentSessionDetails().setAccountDetails((AccountDetails) data);

				cardShareDataStoreObj.addToAppCache(context.getString(R.string.account_details), data);
				final Intent confirmationScreen = new Intent(context, CardNavigationRootActivity.class);
				TrackingHelper.trackPageView(AnalyticsPage.CARD_LOGIN);

				context.startActivity(confirmationScreen);

				// Close current activity
				if (context instanceof Activity) {
					((Activity) context).finish();
				}

			}

			@Override
			public void OnError(final Object data)
			{
				// TODO Auto-generated method stub
				final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(CardLoginFacadeImpl.this);
				cardErrorResHandler.handleCardError((CardErrorBean) data);

			}
		}, "Discover", "Authenticating......");
	}
}
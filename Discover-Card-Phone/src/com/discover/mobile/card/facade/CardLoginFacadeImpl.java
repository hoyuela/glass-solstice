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
	private boolean showToggleFlag = false;
	private final int SA_LOCKED = 1402;
	private final int NOT_ENROLLED = 1401;
	private final int SSN_NOT_MATCHED = 1111;
	private final int SSO_SSN_MATCHED = 1106;
	private final int SSO_ERROR_FLAG = 1102;
	private final String NOT_ENROLLED_MSG = "NOTENROLLED";
	private final String SA_LOCKED_MSG = "LOCKOUT";

	private  WSRequest request ;

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
			public void onStrongAuthSucess(Object data)
			{
				// TODO Auto-generated method stub

				// TODO Auto-generated method stub
				final Intent confirmationScreen = new Intent(context, CardNavigationRootActivity.class);
				confirmationScreen.putExtra("showToggleFlag", showToggleFlag);
				TrackingHelper.trackPageView(AnalyticsPage.CARD_LOGIN);
				context.startActivity(confirmationScreen);
				// Close current activity
				if (context instanceof Activity)
					((Activity) context).finish();

			}

			@Override
			public void onStrongAuthError(Object data)
			{
				// TODO Auto-generated method stub

				CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(CardLoginFacadeImpl.this);
				cardErrorResHandler.handleCardError((CardErrorBean) data);

			}

			@Override
			public void onStrongAuthCardLock(Object data)
			{
				// TODO Auto-generated method stub
				CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(CardLoginFacadeImpl.this);
				cardErrorResHandler.handleCardError((CardErrorBean) data);
			}

			@Override
			public void onStrongAuthSkipped(Object data)
			{
				// TODO Auto-generated method stub
				CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(CardLoginFacadeImpl.this);
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
			public void onStrongAuthNotEnrolled(Object data)
			{

				CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(CardLoginFacadeImpl.this);
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
						Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.discover.com"));
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
			public void onSuccess(Object data)
			{
				// Get the bankpayload and return to bank via facade
				BankPayload bankPayload = (BankPayload) data;
				Log.i(LOG_TAG, "---payload-- " + bankPayload.payload);
				FacadeFactory.getBankLoginFacade().authorizeWithBankPayload(bankPayload.payload);
			}

			@Override
			public void OnError(Object data)
			{
				CardErrorBean cardErrorBean = (CardErrorBean) data;
				CardShareDataStore cardShareDataStore = CardShareDataStore.getInstance(context);
				String cache = (String) cardShareDataStore.getValueOfAppCache("WWW-Authenticate");

				Log.i(LOG_TAG, "--cache--" + cache + " cardErrorBean " + cardErrorBean.getErrorCode());

				// If error code is 401 and cache contains challenge
				// then show strong auth question
				if (cardErrorBean.getErrorCode().contains("" + HttpURLConnection.HTTP_UNAUTHORIZED) && 
						cache != null && cache.contains("challenge"))
				{
					cardShareDataStore.deleteCacheObject("WWW-Authenticate");

					// Check if it's required strong authentication. Skip check for SA
					StrongAuthHandler authHandler = new StrongAuthHandler(callingActivity.getContext(), authListener,true);
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
						(cardErrorBean.getErrorCode().contains(""+SSO_ERROR_FLAG)||cardErrorBean.getErrorCode().contains(""+SSO_SSN_MATCHED)))
				{
					boolean isSSODLinkable = cardErrorBean.getIsSSODelinkable();
					boolean isSSOUser = cardErrorBean.getIsSSOUser();
					boolean isSSNMatch = cardErrorBean.getIsSSNMatched();

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
					CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(CardLoginFacadeImpl.this);
					cardErrorResHandler.handleCardError((CardErrorBean) data);
				}
			}
		};

		authListener = new StrongAuthListener()
		{

			@Override
			public void onStrongAuthSucess(Object data)
			{
				// Calling service for SSO authentication
				getSSOAuthenticationWithoutToken();
			}

			@Override
			public void onStrongAuthError(Object data)
			{
				// TODO Auto-generated method stub
				CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(CardLoginFacadeImpl.this);
				cardErrorResHandler.handleCardError((CardErrorBean) data);
			}

			@Override
			public void onStrongAuthCardLock(Object data)
			{
				// TODO Auto-generated method stub
				CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(CardLoginFacadeImpl.this);
				cardErrorResHandler.handleCardError((CardErrorBean) data);
			}

			@Override
			public void onStrongAuthSkipped(Object data)
			{
				// TODO Auto-generated method stub
				CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(CardLoginFacadeImpl.this);
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
			public void onStrongAuthNotEnrolled(Object data)
			{

				Log.i(LOG_TAG, "--NOt Enrolled 1--");
				CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(CardLoginFacadeImpl.this);
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
						Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.discover.com"));
						context.startActivity(browserIntent);
					}
				});

			}
		};

		Log.i(LOG_TAG, "---SSoAuthenticate---");
		SSOAuthenticate authenticate = new SSOAuthenticate(callingActivity.getContext(), SSOCardEventListener);
		authenticate.sendRequest(tokenValue, hashedTokenValue);
	}

	@Override
	public void toggleLoginToBank(final Context context)
	{
		this.context = context;
		CardEventListener cardEventListener = new CardEventListener()
		{

			@Override
			public void onSuccess(Object data)
			{
				// Set Payload data
				// Get the bankpayload and return to bank via facade

				BankPayload bankPayload = (BankPayload) data;
				bankPayloadText = bankPayload.payload;
				Log.i("Payload from Bank.....", "" + bankPayloadText);

				if (bankPayloadText == null)
				{
					final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(CardLoginFacadeImpl.this);
					CardErrorCallbackListener errorClickCallback = new CardErrorCallbackListener()
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
					CardErrorUtil cardErrUtil = new CardErrorUtil(context);
					final String errorMessage = cardErrUtil.getMessageforErrorCode("40311021");
					final String errorTitle = cardErrUtil.getTitleforErrorCode("1401_LOCKOUT");
					CardErrorBean cardErrorBean = new CardErrorBean(errorTitle, errorMessage, "40311023", false, "101");
					cardErrorResHandler.handleCardError(cardErrorBean, errorClickCallback);

				}
				else
				{
					FacadeFactory.getBankLoginFacade().authorizeWithBankPayload(bankPayloadText);
				}
			}

			@Override
			public void OnError(Object data)
			{
				// SSN NOT MATCH HANDLED
				CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(CardLoginFacadeImpl.this);
				cardErrorResHandler.handleCardError((CardErrorBean) data);

			}
		};

		// Get payload from server
		final CardShareDataStore cardShareDataStoreObj = CardShareDataStore.getInstance(context);
		AccountDetails accountDetails = (AccountDetails) cardShareDataStoreObj.getValueOfAppCache(context.getString(R.string.account_details));
		if (accountDetails.isSSNMatched)
		{
			getBankPayloadFromServer(cardEventListener);
		}
		else
		{
			Globals.setCurrentAccount(AccountType.CARD_ACCOUNT);
			CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(CardLoginFacadeImpl.this);
			CardErrorUtil cardErrorUtil = new CardErrorUtil(context);
			CardErrorBean bean = new CardErrorBean(cardErrorUtil.getTitleforErrorCode("4031102_SSN_NOT_MATCH"), cardErrorUtil.getMessageforErrorCode("4031102_SSN_NOT_MATCH"), "4031102", false, "0");
			cardErrorResHandler.handleCardError(bean);
		}

	}

	/**
	 * This methods make a GET call and get payload data from server
	 * 
	 * @param listener
	 */
	public void getBankPayloadFromServer(CardEventListener listener)
	{

		WSRequest request = new WSRequest();
		HashMap<String, String> headers = request.getHeaderValues();
		String url = NetworkUtility.getWebServiceUrl(context, R.string.sso_authenticate_bank_payload);

		request.setUrl(url);
		request.setHeaderValues(headers);

		WSAsyncCallTask serviceCall = new WSAsyncCallTask(context, new BankPayload(), "Discover", "Authenticating...", listener);
		serviceCall.execute(request);

	}

	@Override
	public void toggleToCard(final Context context)
	{

		Utils.updateAccountDetails(context, new CardEventListener()
		{

			@Override
			public void onSuccess(Object data)
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

				cardShareDataStoreObj.addToAppCache(context.getString(R.string.account_details), (AccountDetails) data);
				final Intent confirmationScreen = new Intent(context, CardNavigationRootActivity.class);
				TrackingHelper.trackPageView(AnalyticsPage.CARD_LOGIN);

				Globals.setCurrentAccount(AccountType.CARD_ACCOUNT);

				showToggleFlag = true;
				confirmationScreen.putExtra("showToggleFlag", showToggleFlag);

				context.startActivity(confirmationScreen);

				// Close current activity
				if (context instanceof Activity)
					((Activity) context).finish();

			}

			@Override
			public void OnError(Object data)
			{
				// TODO Auto-generated method stub
				CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(CardLoginFacadeImpl.this);
				cardErrorResHandler.handleCardError((CardErrorBean) data);

			}
		}, "Discover", "Authenticating......");
	}

	@Override
	public void OnError(final Object data)
	{
		CardErrorBean bean = (CardErrorBean) data;
		boolean ssoUser = false;
		boolean delinkable = false;
		final boolean isSSNMatched = bean.getIsSSNMatched();
		final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler((CardErrorHandlerUi) this);
		String statusCode = bean.getErrorCode();
		Log.d("status code", "statusCode---" + statusCode);
		String errorResponseData = bean.getErrorMessage();
		delinkable = bean.getIsSSODelinkable();
		ssoUser = bean.getIsSSOUser();
		
		CardShareDataStore cardShareDataStore = CardShareDataStore.getInstance(context);
		String cache = (String) cardShareDataStore.getValueOfAppCache("WWW-Authenticate");
		if (statusCode.equalsIgnoreCase("4031102")||statusCode.equalsIgnoreCase("4031106"))
		{
			if (ssoUser && !delinkable) // A/L/U status
			{
				if (isSSNMatched)
				{ // Bank call for Auth
					if(null!=request)
					FacadeFactory.getBankLoginFacade().authDueToALUStatus(request.getUsername(),request.getPassword());
				}
				else
				{
					// Show SSN not matched modal

					CardErrorUtil cardErrorUtil = new CardErrorUtil(context);
					CardErrorBean beanError = new CardErrorBean(cardErrorUtil.getTitleforErrorCode("4031102_SSN_NOT_MATCH"), cardErrorUtil.getMessageforErrorCode("4031102_SSN_NOT_MATCH"), "4031102",
							false, "0");
					cardErrorResHandler.handleCardError(beanError);
				}

			}
			else if (ssoUser && delinkable)// ZB status
			{
				CardErrorCallbackListener errorClickCallback = new CardErrorCallbackListener()
				{
					@Override
					public void onButton1Pressed()
					{
						// Register Button click flow - Big browser link
						Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.discover.com"));
						context.startActivity(browserIntent);
					}

					@Override
					public void onButton2Pressed()
					{
						// TODO Cancel Button click flow
					}
				};
				CardErrorBean cardErrorBean = (CardErrorBean) data;
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
			StrongAuthHandler authHandler = new StrongAuthHandler(context, listener,true);
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
	public void onSuccess(Object data)
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

		boolean isSSOUserVar = ((AccountDetails) data).isSSOUser;

		uidIsAccountNumber = Utils.validateUserforSSO(userId);
		if (uidIsAccountNumber && isSSOUserVar)
		{// Cannot Login with Account no
			// modal
			final CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler((CardErrorHandlerUi) this);
			CardErrorCallbackListener errorClickCallback = new CardErrorCallbackListener()
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
			CardErrorUtil cardErrUtil = new CardErrorUtil(context);
			final String errorMessage = cardErrUtil.getMessageforErrorCode("4031102_SSO_AccountNo");
			final String errorTitle = cardErrUtil.getTitleforErrorCode("1401_LOCKOUT");
			CardErrorBean cardErrorBean = new CardErrorBean(errorTitle, errorMessage, "40311023", false, "101");
			cardErrorResHandler.handleCardError(cardErrorBean, errorClickCallback);
		}
		else if (!uidIsAccountNumber && isSSOUserVar)
		{ // Strong Auth flow
			CardSessionContext.getCurrentSessionDetails().setNotCurrentUserRegisteredForPush(false);
			CardSessionContext.getCurrentSessionDetails().setAccountDetails((AccountDetails) data);
			cardShareDataStoreObj.addToAppCache(context.getString(R.string.account_details), (AccountDetails) data);
			if (shouldShowSSOToggle(data))
			{
				showToggleFlag = true;
				
				//Strong auth need. Done skip checking with server if SA required or not.
				StrongAuthHandler authHandler = new StrongAuthHandler(context, listener,false);
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
			if (context instanceof Activity)
				((Activity) context).finish();
		}
	}

	/**
	 * This method will decide whether we need to show SSO toggle button or not
	 * 
	 * @param acHome
	 * @return
	 */
	public boolean shouldShowSSOToggle(Object acHome)
	{
		boolean isSSOUserVar = ((AccountDetails) acHome).isSSOUser;
		String payLoadSSOTextVar = ((AccountDetails) acHome).payLoadSSOText;
		if (isSSOUserVar && payLoadSSOTextVar != null)
			return true;
		else
			return false;
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
	public void showCustomAlert(AlertDialog alert)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void showOneButtonAlert(int title, int content, int buttonText)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void showDynamicOneButtonAlert(int title, String content, int buttonText)
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
	public void setLastError(int errorCode)
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
	public void getErrorMatchModelForPayload(boolean isSSOUser, final boolean isSSNMatch, boolean isSSODLinkable, final CardErrorBean cardErrBean)
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
				CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(CardLoginFacadeImpl.this);
				CardErrorUtil cardErrorUtil = new CardErrorUtil(context);
				CardErrorBean bean = new CardErrorBean(cardErrorUtil.getTitleforErrorCode("4031102_SSN_NOT_MATCH"), cardErrorUtil.getMessageforErrorCode("4031102_SSN_NOT_MATCH"),
						cardErrBean.getErrorCode(), false, cardErrBean.getNeedHelpFooter());
				cardErrorResHandler.handleCardError(bean);
			}
		}

		// ZB status
		else if (isSSOUser && isSSODLinkable)
		{
			// Show alert for ZB status
			cardErrBean.setFooterStatus("101");
			CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(CardLoginFacadeImpl.this);
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
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.discover.com"));
					context.startActivity(browserIntent);
				}
			});
		}

		// SSN not matched
		else if (!isSSNMatch)
		{
			// Show SSN Error model
			CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(CardLoginFacadeImpl.this);
			CardErrorUtil cardErrorUtil = new CardErrorUtil(context);
			CardErrorBean bean = new CardErrorBean(cardErrorUtil.getTitleforErrorCode("4031102_SSN_NOT_MATCH"), cardErrorUtil.getMessageforErrorCode("4031102_SSN_NOT_MATCH"),
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
		CardEventListener cardEventListener = new CardEventListener()
		{

			@Override
			public void onSuccess(Object data)
			{

				// Get the bankpayload and return to bank via facade
				BankPayload bankPayload = (BankPayload) data;
				Log.i(LOG_TAG, "---payload-- " + bankPayload.payload);
				FacadeFactory.getBankLoginFacade().authorizeWithBankPayload(bankPayload.payload);
			}

			@Override
			public void OnError(Object data)
			{
				CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(CardLoginFacadeImpl.this);
				cardErrorResHandler.handleCardError((CardErrorBean) data);
			}
		};

		SSOAuthenticate authenticate = new SSOAuthenticate(context, cardEventListener);
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
			public void onSuccess(Object data)
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

				cardShareDataStoreObj.addToAppCache(context.getString(R.string.account_details), (AccountDetails) data);
				final Intent confirmationScreen = new Intent(context, CardNavigationRootActivity.class);
				TrackingHelper.trackPageView(AnalyticsPage.CARD_LOGIN);

				context.startActivity(confirmationScreen);

				// Close current activity
				if (context instanceof Activity)
					((Activity) context).finish();

			}

			@Override
			public void OnError(Object data)
			{
				// TODO Auto-generated method stub
				CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler(CardLoginFacadeImpl.this);
				cardErrorResHandler.handleCardError((CardErrorBean) data);

			}
		}, "Discover", "Authenticating......");
	}
}
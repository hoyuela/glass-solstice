/**
 * 
 */
package com.discover.mobile.card.auth.strong;

import java.net.HttpURLConnection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.discover.mobile.card.common.CardEventListener;
import com.discover.mobile.card.common.net.error.CardErrorBean;
import com.discover.mobile.card.common.net.error.CardErrorResponseHandler;
import com.discover.mobile.card.common.sharedata.CardShareDataStore;
import com.discover.mobile.card.error.CardErrorHandlerUi;
import com.discover.mobile.card.services.auth.strong.StrongAuthCheck;
import com.discover.mobile.card.services.auth.strong.StrongAuthDetails;
import com.discover.mobile.card.services.auth.strong.StrongAuthQuestion;
import com.discover.mobile.common.IntentExtraKey;

/**
 * 
 * ©2013 Discover Bank
 * 
 * This class handles Strong Authentication flow. It check with server if Strong
 * Authentication is required or not. If its not required it send success to
 * calling Activity so that calling activity can move further with normal flow.
 * 
 * If Strong Authentication is required than it execute complete Strong
 * Authentication flow like displaying Question and verifying answer with server
 * 
 * @author CTS
 * 
 * @version 1.0
 */
public class StrongAuthHandler
{

	private final String TAG = StrongAuthHandler.class.getSimpleName();
	private final Context context;
	private CardEventListener listener;
	private CardEventListener strongAuthQuestionListener;
	private int requestCode;
	public static StrongAuthListener authListener;
	private final int SALOCKED = 1401;
	private final int NOTENROLLED = 1402;
	private boolean skipCheck = false;

	/**
	 * Constructor incase of
	 * 
	 * @param context
	 * @param authListener
	 */
	public StrongAuthHandler(Context context, StrongAuthListener authListener,boolean skipCheck)
	{
		this.context = context;
		this.authListener = authListener;
		this.skipCheck = skipCheck;
	}

	/**
	 * Default Constructor, pass Requestcode for intent
	 * 
	 * @param context
	 * @param listener
	 *            CardEventListener
	 * @param requestCode
	 */
	public StrongAuthHandler(Context context, CardEventListener listener, int requestCode)
	{
		this.context = context;
		this.listener = listener;
		this.requestCode = requestCode;
	}

	/**
	 * This methods make a service calls Strong Auth flow.
	 * 
	 */
	public void strongAuth()
	{

		strongAuthQuestionListener = new CardEventListener()
		{

			@Override
			public void onSuccess(Object data)
			{
				StrongAuthDetails authDetails = (StrongAuthDetails) data;
				Intent strongAuth = new Intent(context, EnhancedAccountSecurityActivity.class);
				strongAuth.putExtra(IntentExtraKey.STRONG_AUTH_QUESTION, authDetails.questionText);
				strongAuth.putExtra(IntentExtraKey.STRONG_AUTH_QUESTION_ID, authDetails.questionId);
				if (authListener != null)
				{
					context.startActivity(strongAuth);
				}
				else
				{
					((Activity) context).startActivityForResult(strongAuth, requestCode);
				}

			}

			@Override
			public void OnError(Object data)
			{
				if (authListener != null)
				{
					authListener.onStrongAuthError(data);
				}
				else
				{
					CardErrorResponseHandler cardErrorResHandler = new CardErrorResponseHandler((CardErrorHandlerUi) context);
					cardErrorResHandler.handleCardError((CardErrorBean) data);
				}
			}
		};

		if (skipCheck)
		{
			StrongAuthQuestion authQuestion = new StrongAuthQuestion(context, strongAuthQuestionListener);
			authQuestion.sendRequest();
		}
		else
		{
			// Call strong auth web service
			StrongAuthCheck authCheckCall = new StrongAuthCheck(context, new CardEventListener()
			{

				// On success return back success to calling activity
				@Override
				public void onSuccess(Object data)
				{
					if (authListener != null)
					{
						authListener.onStrongAuthSucess(data);
					}
					else
					{
						listener.onSuccess(data);
					}
				}

				@Override
				public void OnError(Object data)
				{
					CardErrorBean bean = (CardErrorBean) data;
					CardShareDataStore cardShareDataStore = CardShareDataStore.getInstance(context);
					String cache = (String) cardShareDataStore.getValueOfAppCache("WWW-Authenticate");
					
					Log.i(TAG, "--Error message-- "+bean.getErrorMessage());

					// If error code is 401 and cache contains challenge
					// then show strong auth question
					if (bean.getErrorCode().contains("" + HttpURLConnection.HTTP_UNAUTHORIZED) && 
							cache != null && cache.contains("challenge"))
					{
						cardShareDataStore.deleteCacheObject("WWW-Authenticate");
						StrongAuthQuestion authQuestion = new StrongAuthQuestion(context, strongAuthQuestionListener);
						authQuestion.sendRequest();
					}
					else if (bean.getErrorCode().contains("" + HttpURLConnection.HTTP_UNAUTHORIZED) && 
							cache != null && cache.contains("skipped"))
					{
						if (authListener != null)
						{
							authListener.onStrongAuthSkipped(data);
						}
					}
					else if(bean.getErrorCode().contains("" + HttpURLConnection.HTTP_FORBIDDEN)
							&& bean.getErrorCode().contains(""+SALOCKED))
					{
						if (authListener != null)
						{
							authListener.onStrongAuthCardLock(data);
						}
					}
					else if(bean.getErrorCode().contains("" + HttpURLConnection.HTTP_FORBIDDEN)
							&& bean.getErrorCode().contains(""+NOTENROLLED))
					{
						if (authListener != null)
						{
							authListener.onStrongAuthNotEnrolled(data);
						}
					}
					else
					{
						if (authListener != null)
						{
							authListener.onStrongAuthError(data);
						}
						else
						{
							listener.OnError(data);
						}
					}
				}
			});
			authCheckCall.sendRequest();
		}
	}
}

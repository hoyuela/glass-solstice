/**
 * Copyright Solstice-Mobile 2013
 */
package com.discover.mobile.bank.delegates;

import android.app.Activity;

import com.discover.mobile.bank.BankNetworkServiceCallManager;
import com.discover.mobile.common.callback.GenericAsyncCallback;
import com.discover.mobile.common.callback.GenericAsyncCallback.Builder;
import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;
import com.discover.mobile.common.callback.LockScreenCompletionListener;
import com.discover.mobile.common.delegates.AsyncCallbackDelegate;
import com.discover.mobile.common.error.BaseExceptionFailureHandler;
import com.discover.mobile.common.error.ErrorHandlerUi;

/**
 * @author ekaram
 *
 */
public class BankPhoneAsyncCallbackBuilder implements AsyncCallbackDelegate{
	
	
	//FIXME create default card async callback builder
//	/**
//	 * Creates the builder required for a Card Network Service call.
//	 * 
//	 * @param resultType - holds the class type to be used by the NetworkServiceCall to map an incoming JSON
//	 * 						successful response.
//	 * @param activity - Reference to an activity that will send out the the service request
//	 * @param errorHandler - Reference to an implementation of an ErrorHandlerUi
//	 * @param hasProgressDialog - Used to determine whether a progress dialog should be shown while a 
//	 * 								network service call is being processed.
//	 * @return Returns the builder to be used by a Card related NetworkServiceCall<> to handle a response.
//	 */
//	public static <T> Builder<T> createDefaultCardAsyncBuilder(final Class<T> resultType, 
//			final Activity activity, final ErrorHandlerUi errorHandler,final boolean hasProgressDialog) {
//		
//		Builder<T> builder = null;
//		
//		if( hasProgressDialog ) {
//			builder = GenericAsyncCallback.<T>builder(activity)
//								.showProgressDialog("Discover", "Loading...", true)
//								.withExceptionFailureHandler(new BaseExceptionFailureHandler())
//								.withErrorResponseHandler(new CardBaseErrorResponseHandler(errorHandler))
//								.withCompletionListener(new LockScreenCompletionListener(activity));
//		} else {
//			builder = GenericAsyncCallback.<T>builder(activity)
//					.withExceptionFailureHandler(new BaseExceptionFailureHandler())
//					.withErrorResponseHandler(new CardBaseErrorResponseHandler(errorHandler))
//					.withCompletionListener(new LockScreenCompletionListener(activity));
//		}
//		
//		return builder;
//	}

	/* (non-Javadoc)
	 * @see com.discover.mobile.common.delegates.AsyncCallbackDelegate#createDefaultCallbackBuilder(java.lang.Class, android.app.Activity, com.discover.mobile.error.ErrorHandlerUi)
	 */
	@Override
	public <T> Builder<T> createDefaultCallbackBuilder(Class<T> arg0, Activity activity, ErrorHandlerUi arg2) {
		Builder<T> builder = null;
		
		builder = GenericAsyncCallback.<T>builder(activity)
				.withExceptionFailureHandler(new BaseExceptionFailureHandler())
				.withExceptionFailureHandler(BankNetworkServiceCallManager.getInstance())
				.withErrorResponseHandler(BankNetworkServiceCallManager.getInstance())
				.withStartListener(BankNetworkServiceCallManager.getInstance())
				.withSuccessListener((SuccessListener<T>) BankNetworkServiceCallManager.getInstance())
				.withCompletionListener(new LockScreenCompletionListener(activity));
		
		
		return builder;
	}

	/* (non-Javadoc)
	 * @see com.discover.mobile.common.delegates.AsyncCallbackDelegate#createDefaultCallbackBuilder(java.lang.Class, android.app.Activity, com.discover.mobile.error.ErrorHandlerUi, boolean)
	 */
	@Override
	public <T> Builder<T> createDefaultCallbackBuilder(Class<T> arg0, Activity activity, ErrorHandlerUi arg2, boolean hasProgressDialog) {
		Builder<T> builder = null;
		
		builder = GenericAsyncCallback.<T>builder(activity)
				.withExceptionFailureHandler(new BaseExceptionFailureHandler())
				.withExceptionFailureHandler(BankNetworkServiceCallManager.getInstance())
				.withErrorResponseHandler(BankNetworkServiceCallManager.getInstance())
				.withStartListener(BankNetworkServiceCallManager.getInstance())
				.withSuccessListener((SuccessListener<T>) BankNetworkServiceCallManager.getInstance())
				.withCompletionListener(new LockScreenCompletionListener(activity));
		
		
		return builder;
	}

}

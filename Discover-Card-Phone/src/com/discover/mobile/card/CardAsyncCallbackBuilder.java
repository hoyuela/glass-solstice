package com.discover.mobile.card;
/**
 * Copyright Solstice-Mobile 2013
 */


import android.app.Activity;

import com.discover.mobile.card.error.CardBaseErrorResponseHandler;
import com.discover.mobile.common.callback.GenericAsyncCallback;
import com.discover.mobile.common.callback.GenericAsyncCallback.Builder;
import com.discover.mobile.common.error.BaseExceptionFailureHandler;
import com.discover.mobile.common.error.ErrorHandlerUi;


/**
 * Every GenericAsyncCallback<> used in a NetworkServiceCall<> should minimally have an 
 * ExceptionFailureHandler for local errors, ErrorResponseHandler for error responses, and a SuccessListener 
 * for successful responses. In addition,  a GenericAsyncCallback can have a CompleteListener
 * which is used to execute some action after all other handlers have been executed. 
 * 
 * This class serves as a library of default GenericAsyncCallback builders with the necessary 
 * ExceptionFailureHandler, ErrorResponseHandler, and CompleteListeners required minimally to handle
 * a network service call. The builder is returned to the application to apply its own SuccessListener. 
 * 
 * @author henryoyuela
 *
 */
public class CardAsyncCallbackBuilder {
	
	/**
	 * Creates the builder required for a Card Network Service call.
	 * 
	 * @param resultType - holds the class type to be used by the NetworkServiceCall to map an incoming JSON
	 * 						successful response.
	 * @param activity - Reference to an activity that will send out the the service request
	 * @param errorHandler - Reference to an implementation of an ErrorHandlerUi
	 * @param hasProgressDialog - Used to determine whether a progress dialog should be shown while a 
	 * 								network service call is being processed.
	 * @return Returns the builder to be used by a Card related NetworkServiceCall<> to handle a response.
	 */
	public static <T> Builder<T> createDefaultCallbackBuilder(final Class<T> resultType, 
			final Activity activity, final ErrorHandlerUi errorHandler,final boolean hasProgressDialog) {
		
		Builder<T> builder = null;
		
		if( hasProgressDialog ) {
			builder = GenericAsyncCallback.<T>builder(activity)
								.showProgressDialog("Discover", "Loading...", true)
								.withExceptionFailureHandler(new BaseExceptionFailureHandler())
								.withErrorResponseHandler(new CardBaseErrorResponseHandler(errorHandler));
		} else {
			builder = GenericAsyncCallback.<T>builder(activity)
					.withExceptionFailureHandler(new BaseExceptionFailureHandler())
					.withErrorResponseHandler(new CardBaseErrorResponseHandler(errorHandler));
		}
		
		return builder;
	}

	

}

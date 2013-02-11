package com.discover.mobile.common.delegates;

import android.app.Activity;

import com.discover.mobile.common.callback.GenericAsyncCallback.Builder;
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
public interface AsyncCallbackDelegate {

	public <T> Builder<T> createDefaultCallbackBuilder(final Class<T> resultType, 
			final Activity activity, final ErrorHandlerUi errorHandler);
	
	public <T> Builder<T> createDefaultCallbackBuilder(final Class<T> resultType, 
			final Activity activity, final ErrorHandlerUi errorHandler, final boolean hasProgressDialog);

	
	
	
}

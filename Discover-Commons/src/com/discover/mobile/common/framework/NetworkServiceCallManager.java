package com.discover.mobile.common.framework;

import java.io.Serializable;

import android.os.Bundle;
import android.util.Log;

import com.discover.mobile.common.AccountType;
import com.discover.mobile.common.AlertDialogParent;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.callback.GenericCallbackListener.CompletionListener;
import com.discover.mobile.common.callback.GenericCallbackListener.ErrorResponseHandler;
import com.discover.mobile.common.callback.GenericCallbackListener.ExceptionFailureHandler;
import com.discover.mobile.common.callback.GenericCallbackListener.StartListener;
import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;
import com.discover.mobile.common.facade.FacadeFactory;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.error.ErrorResponse;

/**
 * Class is used to maintain state of NetworkServiceCall<> requests and responses in a single area in the application. This
 * class follows a singleton design pattern and its single instance is added as a listener to each GenericAsyncCallback<> built
 * and associated with a NetworkServiceCall<> via the AsynCallbackBuilderLibrary. 
 * 
 * @author solstice
 *
 */
public abstract class NetworkServiceCallManager implements StartListener, SuccessListener<Serializable>,
ErrorResponseHandler, ExceptionFailureHandler, CompletionListener {
	/**
	 * Used to print logs into Android logcat
	 */
	private static final String TAG = "NeServiceCallManager";
	/**
	 * Holds a reference to the Previous NetworkServiceCall<> sent out by the application, used to
	 * retransmit a NetworkServiceCall<> when required. Set in the start() method implementation
	 * each time a NetworkServiceCall<> is made.
	 */
	protected NetworkServiceCall<?> prevCall;
	/**
	 * Holds a reference to the Current NetworkServiceCall<> being processed by the application, used to
	 * keep context of the state of the application with respect to NetworkServiceCalls. Set in the start()
	 * method implementation each time a NetworkServiceCall<> is made.
	 */
	protected NetworkServiceCall<?> curCall;
	
	/**
	 * indicates bank / card
	 * 
	 * @return
	 */
	protected abstract AccountType getAccountType();
	
	
	
	/**
	 * @return Returns the priority that should be assigned to NetworkServiceCallManager within the
	 * GenericAsyncCallback<> priority queue when processing an incoming response to a NetworkServiceCall.
	 */
	@Override
	public CallbackPriority getCallbackPriority() {
		return CallbackPriority.MIDDLE;
	}

	
	/**
	 * Method defines the implementation of the handleFailure callback defined by ErrorResponseHandler.
	 * 
	 */
	@Override
	public abstract boolean handleFailure(final NetworkServiceCall<?> sender, final ErrorResponse<?> error);

	/**
	 * Method defines the implementation of the handleFailure callback defined by ExceptionFailureHandler.
	 * Used to close any progress dialog being displayed on the active activity because of a NetworkServiceCall<>.
	 */
	@Override
	public abstract boolean handleFailure(final NetworkServiceCall<?> arg0, final Throwable arg1) ;

	/**
	 * Method defines the implementation of the success callback defined by SuccessListener.
	 * called by NetworkServiceCall<> via a GenericAsyncCallback<> when a successful response
	 * to an HTTP request has been received. NetworkServiceCallManager uses this method
	 * to control any navigation or retransmission of a NetworkServiceCall<>.
	 */
	@Override
	public void success(final NetworkServiceCall<?> sender, final Serializable result) {
		// FIRST UPDATE GLOBAL CACHE IF DESIRED
		if ( sender.cacheResults() ) { 
			CacheManager.instance().updateCache(result);
		}
		
		Bundle bundle = FacadeFactory.getConductorFacade(getAccountType()).getBundleForCall(sender.hashCode());
		boolean isHandled = handleCustomServiceResult(sender,result,bundle);
		
		// the default success handler just uses the conductor to navigate to the caller's requested destination
		if ( !isHandled ) { 
			FacadeFactory.getConductorFacade(getAccountType()).success(sender, result);
		}
	}

	/**
	 *  Override this method to support custom handling of success results
	 *  
	 * @param sender
	 * @param result
	 * @param bundle
	 * @return 
	 * @return
	 */
	protected boolean handleCustomServiceResult(final NetworkServiceCall<?> sender, final Serializable result, Bundle bundle){
		// override me for custom handling 
		return false;
	}

	/**
	 * Method defines the implementation of the start callback defined by StartListener.
	 * Called by NetworkServiceCall<> when submit() is called to start sending an HTTP request.
	 * This methods starts a progress dialog on the active Activity and remembers the previous
	 * NetworkServiceCall<> made in the event it needs to be retransmitted because of a StrongAuth
	 * challenge.
	 */
	@Override
	public void start(final NetworkServiceCall<?> sender) {
		final AlertDialogParent activeActivity = (AlertDialogParent)DiscoverActivityManager.getActiveActivity();
		activeActivity.startProgressDialog();

		//Update curCall and prevCall it is a different service request
		if( curCall == null || curCall.getClass() != sender.getClass() ) {
			prevCall = curCall;
			curCall = sender;
		} else {
			if( Log.isLoggable(TAG, Log.WARN)) {
				Log.w(TAG, "Current NetworkServiceCall was not updated!");
			}
		}
	}

	/**
	 * Method defines the implementation of the complete callback defined by CompletionListener.
	 * Called by NetworkServcieCall<> when a request has been completed irrespective of whether the success
	 * passed or failed.
	 */
	@Override
	public abstract void complete(final NetworkServiceCall<?> sender, final Object result) ;
	
	public NetworkServiceCall<?> getLastServiceCall() {
		return curCall;
	}

}

package com.discover.mobile.common.net;

import java.io.Serializable;

import javax.annotation.Nonnull;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import com.discover.mobile.common.callback.DialogDismissingCompletionListener;
import com.discover.mobile.common.callback.GenericAsyncCallback;
import com.discover.mobile.common.callback.GenericCallbackListener.CompletionListener;
import com.discover.mobile.common.callback.GenericCallbackListener.ErrorResponseHandler;
import com.discover.mobile.common.callback.GenericCallbackListener.ExceptionFailureHandler;
import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;
import com.discover.mobile.common.net.error.ErrorResponse;

/**
 * Utility class used to link NetworkServiceCall<> objects together such that they are executed in a FIFO order,
 * one following the other after a response has been received.
 * 
 * The following is a code snippet showing how to use this class. In this example loginCall will send out its
 * request first, and on a successful response customerCall will send out its request:
 * 
 * //Create the NetworkServieCall<> for authenticating with the Bank Authentication Server
 * CreateBankLoginCall loginCall = new CreateBankLoginCall(activity, callback, credentials);
 * 
 * //Create the NetworkServiceCall<> for downloading customer information after successfully authenticating
 * CustomerServiceCall customerCall = createCustomerDownloadCall(activity);
 * 
 * //Create the queue which will link the NetworkServiceCall<> objects
 * NetworkServiceCallQueue serviceCallQueue = new NetworkServiceCallQueue(activity, loginCall);
 * serviceCallQueue.enqueue(customerCall, NetworkServiceCallQueue.EventType.Success);
 * 
 * //Start sending out the HTTP requests
 * serviceCallQueue.submit();
 * 
 * @author henryoyuela
 *
 */
final public class NetworkServiceCallQueue {
	/**
	 * TAG used to print logs into Android logcat
	 */
	private static final String TAG = NetworkServiceCallQueue.class.getSimpleName();
	/**
	 * Container used to hold the NetworkServiceObjects enqueued
	 */
	private final NetworkServiceCall<?>[] services = new NetworkServiceCall<?>[5];
	/**
	 * Reference to the activity 
	 */
	private final @Nonnull Activity activity;
	/**
	 * Contains the index of the NetworkServiceCall<> object at the head of the queue
	 */
	private int headIndex = 0;
	/**
	 * Contains the index of the NetworkServiceCall<> object at the tail of the queue
	 */
	private int tailIndex = 0;
	
	/**
	 * Used to specify when an enqueued NetworkServiceCall<> object should be de-queued and
	 * triggered to send its HTTP request.
	 *  
	 * @author henryoyuela
	 *
	 */
	public static enum EventType {
		Complete,
		Success,
		Error,
		Exception
	}
	
	@SuppressWarnings("unused")
	private NetworkServiceCallQueue(){
		throw new UnsupportedOperationException("This class is non-instantiable"); //$NON-NLS-1$
	}
	/**
	 * 
	 * @param activity Reference to the Activity that will own the instance of the NetworkServiceCallQueue
	 * @param serviceCall NetworkServiceCall<> object to be placed at the head of the queue
	 */
	public NetworkServiceCallQueue(final @Nonnull Activity activity, final @Nonnull NetworkServiceCall<?> serviceCall) {
		this.activity = activity;
		this.enqueue(serviceCall, EventType.Success);
	}
	/**
	 * 
	 * @return Returns the current size of the queue. Does not specify how many NetworkServiceCall<> objects are in the
	 * 			queue, but how many it can hold.
	 */
	public int size() {
		return services.length;
	}
	/**
	 * 
	 * @return Returns the number of NetworkServiceObjects<> have been enqueued
	 */
	public int count() {
		return tailIndex;
	}
	/**
	 * Used to increase the capacity of the queue. Initial size is 5.
	 * 
	 * @param size Used to specify the new size of the queue. Must be larger than the existing size of the queue.
	 * 
	 */
	public void resize(int size) {
		if( size > services.length ) {
			NetworkServiceCall<?>[] newServices = new NetworkServiceCall<?>[services.length + size];
			for( int i = 0; i < services.length; i++) {
				newServices[i] = services[i];
			}
		} else {
			if( Log.isLoggable(TAG, Log.WARN)) {
				Log.w(TAG, "Queue is larger than the size provided");
			}
		}
	}
	
	/**
	 * 
	 * @param serviceCall NetworkServiceCall object to enqueue
	 * @param event Specifies when the NetworkServiceCall<> object should be triggered to send its request
	 */
	public void enqueue(final @Nonnull NetworkServiceCall<?> serviceCall, EventType event) {
		enqueue(serviceCall, event, null);
	}
	
	/**
	 * 
	 * @param serviceCall NetworkServiceCall object to enqueue
	 * @param event Specifies when the NetworkServiceCall<> object should be triggered to send its request
	 */
	public void enqueue(final @Nonnull NetworkServiceCall<?> serviceCall, EventType event, NetworkServiceCallCondition<?> condition) {
		//Verify the maximum size of the queue has not been reached
		if( (tailIndex + 1) == services.length ) {
			//Increase the size of the queue
			resize(services.length*2);
		}	
		
		TypedReferenceHandler<?> handler = serviceCall.getHandlerSafe();
		
		//Verify the handler of the NetworkSericeCall<> is of the right type 
		if( handler.getCallback() instanceof GenericAsyncCallback<?>) {
			//Verify that there are more than one Service Call Objects in the queue, if so then link the newly
			//enqueued NetworkServiceCall<> object with the tail of the queue 
			if( !isEmpty() ) {
				//Fetch the handler for the tail in order to link with enqueued NetworkServiceCall<> object
				handler = tail().getHandlerSafe();
				
				@SuppressWarnings("unchecked")
				GenericAsyncCallback<Serializable> asyncCallback =  (GenericAsyncCallback<Serializable>) handler.getCallback();
			
				switch(event) {
				case Complete:
					asyncCallback.getBuilder().withCompletionListener( new CallNextServiceInQueue<Serializable>(serviceCall, condition));
					break;
				case Success:
					asyncCallback.getBuilder().withSuccessListener( new CallNextServiceInQueue<Serializable>(serviceCall, condition));
					break;
				case Error:
					asyncCallback.getBuilder().withErrorResponseHandler( new CallNextServiceInQueue<Serializable>(serviceCall, condition));	
					break;
				case Exception:
					asyncCallback.getBuilder().withExceptionFailureHandler( new CallNextServiceInQueue<Serializable>(serviceCall, condition));
					break;
				}	
				
				//Must rebuild handler with new listener
				asyncCallback.rebuild(asyncCallback.getBuilder());
			} else {
				if(Log.isLoggable(TAG, Log.DEBUG)) {
					Log.d(TAG,"First Service in Queue");
				}
			}
			
			//Add Service Object to tail
			services[tailIndex] = serviceCall;
			
			//Move tail to the end of the queue
			++tailIndex;
		} else {
			if(Log.isLoggable(TAG, Log.ERROR)) {
				Log.e(TAG,"Unable to enque service call, invalid handler type");
			}
		}
	}
	/**
	 * 
	 * @return Returns the NetworkServiceCall<> object at the head of the queue
	 */
	private NetworkServiceCall<?> head() {
		return services[headIndex];
	}
	/**
	 * 
	 * @return Returns the NetworkServiceCall<> object at the tail of the queue
	 */
	private NetworkServiceCall<?> tail() {
		return services[tailIndex-1];
	}
	
	/**
	 * 
	 * @return True if queue is empty, False otherwise
	 */
	public boolean isEmpty() {
		return  head() == null || tailIndex == 0;
	}
	/**
	 * Used to start triggering the NetworkServiceCall<> objects in the queue in a FIFO order while showing
	 * a progress dialog as they are being processed. At completion the progress dialog will be closed.
	 * 
	 * @param title Title of the Progress Dialog
	 * @param message Message to display in the center of the Progress Dialog 
	 */
	public void submitWithProgressDialog(String title, String message) {
		final @Nonnull ProgressDialog dialog = ProgressDialog.show(activity, title, message, true);
		
		TypedReferenceHandler<?> handler = head().getHandlerSafe();
		
		if( handler.getCallback() instanceof GenericAsyncCallback<?>) {
			@SuppressWarnings("unchecked")
			GenericAsyncCallback<Serializable> asyncCallback =  (GenericAsyncCallback<Serializable>) handler.getCallback();
			asyncCallback.getBuilder().withCompletionListener(new DialogDismissingCompletionListener(dialog));
			asyncCallback.rebuild(asyncCallback.getBuilder());
		} else {
			if( Log.isLoggable(TAG, Log.ERROR)) {
				Log.e(TAG, "Unable to show progress dialog, invalid handler type");
			}
		}
		
		this.submit();	
	}
	/**
	 * Used to start triggering the NetworkServiceCall<> objects in the queue in a FIFO order.
	 */
	public void submit() {
		if( !isEmpty() ) {
			head().submit();
		} else {
			if( Log.isLoggable(TAG, Log.WARN)) {
				Log.w(TAG, "Unable to run services, no services found in queue");
			}
		}
	}
	/**
	 * Utility class used to link NetworkSericeCall<> objects together via its response handler.
	 * 
	 * @author henryoyuela
	 *
	 * @param <Serializable>
	 */
	@SuppressWarnings("hiding")
	public class CallNextServiceInQueue<Serializable> implements SuccessListener<Serializable>, 
		ExceptionFailureHandler, ErrorResponseHandler, CompletionListener {
		private final NetworkServiceCall<?> mNetworkServiceCall;
		private final NetworkServiceCallCondition<?> mCondition;
		
		private CallNextServiceInQueue(NetworkServiceCall<?> networkServiceCall, NetworkServiceCallCondition<?> condition) {
			mNetworkServiceCall = networkServiceCall;
			mCondition = condition;
		}
		
		@Override
		public CallbackPriority getCallbackPriority() {
			return CallbackPriority.LAST;
		}

		private void executeNextCall() {
			if( mCondition != null ) {
				if( mCondition.isCallable() ) {
					mNetworkServiceCall.submit();
				} else {
					if( Log.isLoggable(TAG, Log.WARN)) {
						Log.w(TAG, "Condition was not met to execute " +mNetworkServiceCall.toString());
					}
				}
			} else {
				mNetworkServiceCall.submit();
			}
		}
		
		@Override
		public void success(Serializable value) {
			if( mCondition != null ) {
				mCondition.success((java.io.Serializable) value);
			}
			
			executeNextCall();
		}

		@Override
		public void complete(Object result) {
			if( mCondition != null ) {
				mCondition.complete(result);
			}
			
			executeNextCall();		
		}

		@Override
		public boolean handleFailure(ErrorResponse<?> errorResponse) {
			if( mCondition != null ) {
				mCondition.handleFailure(errorResponse);
			}
			
			executeNextCall();
			return false;
		}

		/**
		 * 
		 * @param executionException Reference to the exception that was thrown
		 * @param networkServiceCall Reference to the network service call where the exception occurred
		 */
		@Override
		public boolean handleFailure(Throwable executionException, final NetworkServiceCall<?> networkServiceCall) {
			if( mCondition != null ) {
				mCondition.handleFailure(executionException, networkServiceCall);
			}
			
			executeNextCall();
			return false;
		}		
	};
	
	

}

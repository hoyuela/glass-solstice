package com.discover.mobile.common.callback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

import com.discover.mobile.common.callback.GenericCallbackListener.CompletionListener;
import com.discover.mobile.common.callback.GenericCallbackListener.ErrorResponseHandler;
import com.discover.mobile.common.callback.GenericCallbackListener.ExceptionFailureHandler;
import com.discover.mobile.common.callback.GenericCallbackListener.StartListener;
import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.error.ErrorResponse;

/**
 * A listener class used to monitor NetworkServiceCall<> events such as when it starts, succeeds, fails 
 * because of an HTTP error response, or fails because of an exception. 
 * 
 * @author ghayworth,ekaram, hoyuela
 *
 * @param <V>
 */
public final class GenericAsyncCallback<V> implements AsyncCallback<V> {
	
	// FIXME make ALL callbacks with the same priority happen in appropriate order, not just the order for that type of
	// callback
	
	/**
	 * Used to print logs into Android logcat
	 */
	private static final String TAG = GenericAsyncCallback.class.getSimpleName();
	/**
	 * List of listeners notified when a NetworkServiceCall<> starts processing a request
	 */
	private @Nonnull List<StartListener> startListeners;
	/**
	 * List of listeners notified when a NetworkServiceCall<> completes, irrespective it was 
	 * successful or failed.
	 */
	private @Nonnull List<CompletionListener> completionListeners;
	/**
	 * List of listeners notified when a NetworkServiceCall<> receives a 200 OK HTTP response
	 */
	private @Nonnull List<SuccessListener<V>> successListeners;
	/**
	 * List of listeners notified when a NetworkServiceCall<> receives an error response
	 */
	private @Nonnull List<ExceptionFailureHandler> exceptionFailureHandlers;
	/**
	 * Reference to builder class used to construct collection of listeners
	 */
	private @Nonnull Builder<V> builder;
	/**
	 * We only need one error response handler; we will use inheritance 
	 * @see BaseErrorResponseHandler
	 */
	private @Nonnull ErrorResponseHandler errorResponseHandler;
	
	/**
	 * Constructor used by Builder<> class to generate an instance of GenericAsyncCallback<> 
	 * with collection of listeners specified using the builder.
	 * 
	 * @param builder
	 */
	private GenericAsyncCallback(final Builder<V> builder) {
		completionListeners = safeSortedCopy(builder.completionListeners);
		successListeners = safeSortedCopy(builder.successListeners);
		exceptionFailureHandlers = safeSortedCopy(builder.exceptionFailureHandlers);
		startListeners = safeSortedCopy(builder.startListeners);
		errorResponseHandler = builder.errorResponseHandler;
		this.builder = builder;
	}

	/**
	 * Callback invoked when a NetworkServiceCall<> starts
	 * @param sender Reference to calling NetworkServiceCall<>
	 */
	@Override
	public void start(final NetworkServiceCall<?> sender) {
		for(final StartListener listener : startListeners){
			listener.start(sender);
		}
	}
	/**
	 * Called when the {@link NetworkServiceCall} finishes, no matter what the result was. This will be called before
	 * {@link #success(Object)}, {@link #failure(Throwable)} or {@link #failure(ErrorResponse)} is called. If an
	 * {@code Exception} is thrown during the execution of this method it will prevent the status-specific method from
	 * being called.
	 * 
	 * @param sender Reference to calling NetworkServiceCall<>
	 * @param result The result of the call before it is passed to the more specific, status-related methods
	 */
	@Override
	public void complete(final NetworkServiceCall<?> sender, final Object result) {
		for(final CompletionListener listener : completionListeners){
			listener.complete(sender, result);
		}
	}
	/**
	 * Callback invoked when a NetworkServiceCall<> receives a 200 OK response
	 * 
	 * @param sender Reference to calling NetworkServiceCall<>
	 * @param value Contains any content that was found in the body of the 200 OK response
	 */
	@Override
	public void success(final NetworkServiceCall<?> sender, final V value) {
		for(final SuccessListener<V> listener : successListeners){
			listener.success(sender, value);
		}
	}

	/**
	 * Callback invoked when an exception occurs during sending a request or processing a response for a
	 * NetworkServiceCall<>. 
	 * 
	 * @param executionException Reference to the exception that was thrown
	 * @param networkServiceCall Reference to the network service call where the exception occurred
	 */
	@Override
	public void failure(final NetworkServiceCall<?> sender, final Throwable executionException) {
		Log.w(TAG, "caught throwable during execution", executionException);
		
		boolean handled = false;
		for(final ExceptionFailureHandler handler : exceptionFailureHandlers) {
			handled = handler.handleFailure(sender, executionException);
			if(handled){
				break;
			}
		}
		
		if(!handled){
			throw new UnsupportedOperationException("No handler for throwable", executionException);
		}
	}
	/**
	 * Callback invoked when a NetworkServiceCall<> receives an HTTP error response. 
	 * 
	 * @param sender Reference to calling NetworkServiceCall<>
	 * @param errorResponse Contains any content that was provided in the body of the HTTP error response.
	 */
	@Override
	public void failure(final NetworkServiceCall<?> sender, final ErrorResponse<?> errorResponse) {
		Log.w(TAG, "server returned errorResponse: " + errorResponse);
		
		boolean handled = false;
		handled = errorResponseHandler.handleFailure(sender, errorResponse);
		
		if(!handled){
			Log.e(TAG,"No handler for errorResponse: " + errorResponse);
		}
	}
	
	/**
	 * This function should not be called during the processing of an HTTP request via
	 * NetworkServiceCall as it is not thread-safe.
	 * 
	 * @return Returns the current builder held by this instance of GenericAsyncCallback. 
	 * 
	 */
	public Builder<V> getBuilder() {
		return builder;
	}
	
	/**
	 *
	 * Used to reconstruct the list of listeners used by the GenericAsyncCallback to
	 * handle events raised by a NetworkServiceCall<>. 
	 * 
	 * This function should not be called during the processing of an HTTP request via
	 * NetworkServiceCall as it is not thread-safe.
	 * 
	 * @param builder Holds the lists of event listeners 
	 */
	public void rebuild(final Builder<V> builder) {
		completionListeners = safeSortedCopy(builder.completionListeners);
		successListeners = safeSortedCopy(builder.successListeners);
		exceptionFailureHandlers = safeSortedCopy(builder.exceptionFailureHandlers);
		startListeners = safeSortedCopy(builder.startListeners);
		errorResponseHandler = builder.errorResponseHandler;
		this.builder = builder;
	}
	
	
	private static @Nonnull <L extends GenericCallbackListener> List<L> safeSortedCopy(@Nullable final List<L> list) {
		if(list == null || list.isEmpty()){
			return Collections.emptyList();
		}
		
		final List<L> returnList = new ArrayList<L>(list);
		
		if(list.size() > 1){
			Collections.sort(returnList, new GenericCallbackListenerComparator());
		}
		
		return returnList;
	}
	
	public static void safeClear(final List<?> list) {
		if(!list.isEmpty()){
			list.clear();
		}
	}
	
	public static <V> Builder<V> builder(final @Nonnull Activity activity) {
		return new Builder<V>(activity);
	}
	
	/**
	 * Helper class defined to build GenericAsyncCallback<> instances. A GenericAsyncCallback<> can only
	 * be instantiated via a builder class. All listeners are provided via the builder class and are
	 * sorted and copied over to the GenericAsyncCallback upon calling build on the Builder<> class.
	 * 
	 * @author henryoyuela
	 *
	 * @param <V>
	 */
	public static final class Builder<V> {
		

		private final @Nonnull Activity activity;
		
		private @Nullable List<CompletionListener> completionListeners;
		private @Nullable List<SuccessListener<V>> successListeners;
		private @Nullable List<ExceptionFailureHandler> exceptionFailureHandlers;
		private @Nullable List<StartListener> startListeners;
		public ErrorResponseHandler errorResponseHandler;

		
		public Builder(final @Nonnull Activity activity) {
			this.activity = activity;
		}
		
		public GenericAsyncCallback<V> build() {
			return new GenericAsyncCallback<V>(this);
		}
		
		public Builder<V> withStartListener(final StartListener startListener) {
			if(startListeners == null){
				startListeners = new LinkedList<StartListener>();
			}
			startListeners.add(startListener);
			
			return this;
		}
		
		public Builder<V> withCompletionListener(final CompletionListener completionListener) {
			if(completionListeners == null){
				completionListeners = new LinkedList<CompletionListener>();
			}
			completionListeners.add(completionListener);
			
			return this;
		}
		
		public Builder<V> withSuccessListener(final SuccessListener<V> successListener) {
			if(successListeners == null){
				successListeners = new LinkedList<SuccessListener<V>>();
			}
			successListeners.add(successListener);
			
			return this;
		}
		
		public Builder<V> withExceptionFailureHandler(final ExceptionFailureHandler exceptionFailureHandler) {
			if(exceptionFailureHandlers == null){
				exceptionFailureHandlers = new LinkedList<ExceptionFailureHandler>();
			}
			exceptionFailureHandlers.add(exceptionFailureHandler);
			
			return this;
		}
		
		public Builder<V> withErrorResponseHandler(final ErrorResponseHandler errorResponseHandler) {
			this.errorResponseHandler = errorResponseHandler;
		
			return this;
		}
		
		public Builder<V> showProgressDialog(final String title, final String message, final boolean indeterminate) {
			final @Nonnull ProgressDialog dialog = ProgressDialog.show(activity, title, message, indeterminate);
			
			withCompletionListener(new DialogDismissingCompletionListener(dialog));
			
			return this;
		}
		
		public Builder<V> handleCustomDialog(final @Nonnull Dialog dialog) {
			withCompletionListener(new DialogDismissingCompletionListener(dialog));
			
			return this;
		}
		
		/**
		 * Launch an {@link Intent} for this class after a successful result.
		 * 
		 * @param successLaunchIntentClass
		 * @return This builder so that calls can be chained 
		 */
		public Builder<V> launchIntentOnSuccess(final @Nonnull Class<?> successLaunchIntentClass) {
			withSuccessListener(new FireIntentSuccessListener<V>(activity, successLaunchIntentClass));
			
			return this;
		}
		
		public Builder<V> clearTextViewsOnComplete(final TextView... textViews) {
			withCompletionListener(new ClearTextFieldsCompletionListener(textViews));
			
			return this;
		}
		
		public Builder<V> finishCurrentActivityOnSuccess(final Activity activityToFinish) {
			withCompletionListener(new FinishActivityCompletionListener(activityToFinish));
			
			return this;
		}
		
	}


	
}

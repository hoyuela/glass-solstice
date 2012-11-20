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
import com.discover.mobile.common.callback.GenericCallbackListener.SuccessListener;
import com.discover.mobile.common.net.error.ErrorResponse;

public final class GenericAsyncCallback<V> implements AsyncCallback<V> {
	
	// FIXME make ALL callbacks with the same priority happen in appropriate order, not just the order for that type of
	// callback
	
	private static final String TAG = GenericAsyncCallback.class.getSimpleName();
	
	private final @Nonnull List<CompletionListener> completionListeners;
	private final @Nonnull List<SuccessListener<V>> successListeners;
	private final @Nonnull List<ExceptionFailureHandler> exceptionFailureHandlers;
	private final @Nonnull List<ErrorResponseHandler> errorResponseHandlers;
	
	private GenericAsyncCallback(final Builder<V> builder) {
		completionListeners = safeSortedCopy(builder.completionListeners);
		successListeners = safeSortedCopy(builder.successListeners);
		exceptionFailureHandlers = safeSortedCopy(builder.exceptionFailureHandlers);
		errorResponseHandlers = safeSortedCopy(builder.errorResponseHandlers);
	}

	@Override
	public void complete(final Object result) {
		for(final CompletionListener listener : completionListeners)
			listener.complete(result);
		
		safeClear(completionListeners);
	}
	
	@Override
	public void success(final V value) {
		for(final SuccessListener<V> listener : successListeners)
			listener.success(value);
		
		safeClear(successListeners);
	}

	@Override
	public void failure(final Throwable executionException) {
		Log.w(TAG, "caught throwable during execution", executionException);
		
		// TODO generic handling, error message, throw exception if not handled
		
		boolean handled = false;
		for(final ExceptionFailureHandler handler : exceptionFailureHandlers) {
			handled = handler.handleFailure(executionException);
			if(handled)
				break;
		}
		
		safeClear(exceptionFailureHandlers);
		
		if(!handled)
			throw new UnsupportedOperationException("No handler for throwable", executionException);
	}
	
	@Override
	public void failure(final ErrorResponse<?> errorResponse) {
		Log.w(TAG, "server returned errorResponse: " + errorResponse);
		
		// TODO generic handling, error message, throw exception if not handled

		boolean handled = false;
		for(final ErrorResponseHandler handler : errorResponseHandlers) {
			handled = handler.handleFailure(errorResponse);
			if(handled)
				break;
		}
		
		safeClear(errorResponseHandlers);
		
		if(!handled)
			throw new UnsupportedOperationException("No handler for errorResponse: " + errorResponse);
	}
	
	@SuppressWarnings("null")
	private static @Nonnull <L extends GenericCallbackListener> List<L> safeSortedCopy(@Nullable final List<L> list) {
		if(list == null || list.isEmpty())
			return Collections.emptyList();
		
		final List<L> returnList = new ArrayList<L>(list);
		
		if(list.size() > 1)
			Collections.sort(returnList, new GenericCallbackListenerComparator());
		
		return returnList;
	}
	
	private static void safeClear(final List<?> list) {
		if(!list.isEmpty())
			list.clear();
	}
	
	public static <V> Builder<V> builder(final @Nonnull Activity activity) {
		return new Builder<V>(activity);
	}
	
	public static final class Builder<V> {
		
		private final @Nonnull Activity activity;
		
		private @Nullable List<CompletionListener> completionListeners;
		private @Nullable List<SuccessListener<V>> successListeners;
		private @Nullable List<ExceptionFailureHandler> exceptionFailureHandlers;
		private @Nullable List<ErrorResponseHandler> errorResponseHandlers;
		
		public Builder(final @Nonnull Activity activity) {
			this.activity = activity;
		}
		
		public GenericAsyncCallback<V> build() {
			return new GenericAsyncCallback<V>(this);
		}
		
		public Builder<V> withCompletionListener(final CompletionListener completionListener) {
			if(completionListeners == null)
				completionListeners = new LinkedList<CompletionListener>();
			completionListeners.add(completionListener);
			
			return this;
		}
		
		public Builder<V> withSuccessListener(final SuccessListener<V> successListener) {
			if(successListeners == null)
				successListeners = new LinkedList<SuccessListener<V>>();
			successListeners.add(successListener);
			
			return this;
		}
		
		public Builder<V> withExceptionFailureHandler(final ExceptionFailureHandler exceptionFailureHandler) {
			if(exceptionFailureHandlers == null)
				exceptionFailureHandlers = new LinkedList<ExceptionFailureHandler>();
			exceptionFailureHandlers.add(exceptionFailureHandler);
			
			return this;
		}
		
		public Builder<V> withErrorResponseHandler(final ErrorResponseHandler errorResponseHandler) {
			if(errorResponseHandlers == null)
				errorResponseHandlers = new LinkedList<ErrorResponseHandler>();
			errorResponseHandlers.add(errorResponseHandler);
			
			return this;
		}
		
		public Builder<V> showProgressDialog(final String title, final String message, final boolean indeterminate) {
			@SuppressWarnings("null")
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
		@SuppressWarnings("null")
		public Builder<V> launchIntentOnSuccess(final @Nonnull Class<?> successLaunchIntentClass) {
			withSuccessListener(new FireIntentSuccessListener<V>(activity, successLaunchIntentClass));
			
			return this;
		}
		
		public Builder<V> clearTextViewsOnComplete(final TextView... textViews) {
			withCompletionListener(new ClearTextFieldsCompletionListener(textViews));
			
			return this;
		}
		
	}
	
}

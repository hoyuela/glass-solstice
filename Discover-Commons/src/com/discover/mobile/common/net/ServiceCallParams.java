package com.discover.mobile.common.net;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map;

import android.content.Context;

import com.discover.mobile.common.Struct;
import com.discover.mobile.common.net.error.ErrorResponseParser;

@Struct
public abstract class ServiceCallParams {

	public final String httpMethod;
	public final String path;
	
	//If this is null it will use the default error responses
	public ErrorResponseParser<?> errorResponseParser = null;
	public Map<String,String> headers = null;
	
	// Timeout variables which need to be set 
	// Should be defaulted based on externalized values known by a class with Context (see NertworkServiceCall)
	public int connectTimeoutSeconds = -1;
	public int readTimeoutSeconds = -1;
	
	/**
	 * Should never be {@code true} at the same time as {@link #requiresSessionForRequest}.
	 */
	public boolean clearsSessionBeforeRequest = false;
	/**
	 * Should never be {@code true} at the same time as {@link #clearsSessionBeforeRequest}.
	 */
	public boolean requiresSessionForRequest = true;
	public boolean clearsSessionAfterRequest = false;
	
	public boolean sendDeviceIdentifiers = false;
	
	private boolean isCancellable = false;
	
	//------------------------------ ServiceCallParams class methods ------------------------------
	
		private ServiceCallParams(final String httpMethod, final String path) {
			checkArgument(path != null && !path.isEmpty(), "path cannot be empty"); //$NON-NLS-1$
			
			this.httpMethod = httpMethod;
			this.path = path;
		}
		
		public boolean isCancellable() {
			return this.isCancellable;
		}
		
		public void setCancellable(final boolean cancellable) {
			this.isCancellable = cancellable;
		}
	
	//-------------------------------------- Inner Classes --------------------------------------	
		
	@Struct
	public static class GetCallParams extends ServiceCallParams {
		
		public GetCallParams(final String path) {
			super("GET", path); //$NON-NLS-1$
		}
		
	}

	@Struct
	public static class PostCallParams extends ServiceCallParams {
		
		/**
		 * The body of the request. If the body is a {@link String} it will be sent as-is, otherwise it will be
		 * serialized as JSON.
		 */
		public Object body = null;
		public RequestBodySerializer customBodySerializer = null;
		
		public PostCallParams(final String path) {
			super("POST", path); //$NON-NLS-1$
		}
		
	}
	
	@Struct
	public static class DeleteCallParams extends ServiceCallParams {
		
		public DeleteCallParams(final String path) {
			super("DELETE", path); //$NON-NLS-1$
		}		
	}
	
	@Struct
	public static class PutCallParams extends ServiceCallParams {
		
		public PutCallParams(final String path) {
			super("PUT", path); //$NON-NLS-1$
		}		
	}
	
	/** Converts a String resource, specified by its R id, to an integer to be used as a timeout.<br>
	 * 	@return {@code int} parsed or -1 if a {@link NumberFormatException} occurred. */
	public static int parseTimeout(Context context, int resourceId) {
		try {
			return Integer.parseInt(context.getString(resourceId));
		} catch (NumberFormatException e) {
			return -1;
		}
	}
}

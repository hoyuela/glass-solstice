package com.discover.mobile.common.net;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map;

import com.discover.mobile.common.Struct;
import com.discover.mobile.common.net.response.ErrorResponseParser;

@Struct
public abstract class ServiceCallParams {

	public final String httpMethod;
	public final String path;
	
	public ErrorResponseParser<?> errorResponseParser = null;
	public Map<String,String> headers = null;
	
	// TODO consider other timeout defaults
	public int connectTimeoutSeconds = 15;
	public int readTimeoutSeconds = 15;
	
	/**
	 * Should never be {@code true} at the same time as {@link #requiresSessionForRequest}.
	 */
	public boolean clearsSessionBeforeRequest = false;
	/**
	 * Should never be {@code true} at the same time as {@link #clearsSessionBeforeRequest}.
	 */
	public boolean requiresSessionForRequest = true;
	
	public boolean sendDeviceIdentifiers = false;
	
	@Struct
	public static class GetCallParams extends ServiceCallParams {
		
		public GetCallParams(final String path) {
			super("GET", path);
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
			super("POST", path);
		}
		
	}
	
	private ServiceCallParams(final String httpMethod, final String path) {
		checkArgument(path != null && !path.isEmpty(), "path cannot be empty");
		
		this.httpMethod = httpMethod;
		this.path = path;
	}
	
}

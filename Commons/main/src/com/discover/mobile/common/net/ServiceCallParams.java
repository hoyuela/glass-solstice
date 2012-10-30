package com.discover.mobile.common.net;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map;

import com.discover.mobile.common.net.response.ErrorResponseParser;

public abstract class ServiceCallParams {

	public final String httpMethod;
	public final String path;
	
	// Optional/Defaulted
	public ErrorResponseParser<?> errorResponseParser = null;
	public Map<String,String> headers = null;
	// TODO consider other timeout defaults
	public int connectTimeoutSeconds = 15;
	public int readTimeoutSeconds = 15;
	
	public static class GetCallParams extends ServiceCallParams {
		
		public GetCallParams(final String path) {
			super("GET", path);
		}
		
	}
	
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

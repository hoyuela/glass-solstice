package com.discover.mobile.common.net.json;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.discover.mobile.common.net.NetworkServiceCall;

/**
 * A {@link NetworkServiceCall} that handles mapping of JSON requests and responses.
 * 
 * @param <R> The <u>r</u>esult type that this service call will return
 * @param <M> The <u>m</u>odel type for the JSON result
 */
public abstract class JsonMappingNetworkServiceCall<R,M> extends NetworkServiceCall<R> {
	
	private final Class<M> modelClass;
	
	protected JsonMappingNetworkServiceCall(final Context context, final ServiceCallParams params,
			final Class<M> modelClass) {
		
		super(context, params);
		
		checkNotNull(modelClass, "modelClass cannot be null");
		
		this.modelClass = modelClass;
	}
	
	protected abstract R createResultFromModel(M model);
	
	@Override
	protected R parseResponse(final int status, final Map<String,List<String>> headers, final InputStream body)
			throws IOException {
		
		final M model = parseModelFromResponse(body);
		return createResultFromModel(model);
	}
	
	private M parseModelFromResponse(final InputStream responseBody) throws IOException {
		return JacksonObjectMapperHolder.mapper.readValue(responseBody, modelClass);
	}
	
}

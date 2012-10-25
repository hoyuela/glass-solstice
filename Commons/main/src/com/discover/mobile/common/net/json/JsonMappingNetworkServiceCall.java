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
 * @param <M> The <u>m</u>odel type for the JSON result
 */
public abstract class JsonMappingNetworkServiceCall<M> extends NetworkServiceCall<M> {
	
	private final Class<M> modelClass;
	
	protected JsonMappingNetworkServiceCall(final Context context, final ServiceCallParams params,
			final Class<M> modelClass) {
		
		super(context, params);
		
		checkNotNull(modelClass, "modelClass cannot be null");
		
		this.modelClass = modelClass;
	}
	
	@Override
	protected M parseSuccessResponse(final int status, final Map<String,List<String>> headers, final InputStream body)
			throws IOException {
		
		return JacksonObjectMapperHolder.mapper.readValue(body, modelClass);
	}
	
}

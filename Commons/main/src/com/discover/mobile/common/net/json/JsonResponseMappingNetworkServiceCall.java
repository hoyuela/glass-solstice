package com.discover.mobile.common.net.json;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;

import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.ServiceCallParams;

/**
 * A {@link NetworkServiceCall} that handles mapping of JSON requests and responses.
 * 
 * @param <M> The <u>m</u>odel type for the JSON result
 */
public abstract class JsonResponseMappingNetworkServiceCall<M> extends NetworkServiceCall<M> {
	
	private static final String TAG = JsonResponseMappingNetworkServiceCall.class.getSimpleName();
	
	private final Class<M> modelClass;
	
	/**
	 * JSON mapping service call used with the base url defaulted to card.
	 * @param context
	 * @param params
	 */
	protected JsonResponseMappingNetworkServiceCall(final Context context, final ServiceCallParams params,
			final Class<M> modelClass) {
		
		super(context, params);
		
		checkNotNull(modelClass, "modelClass cannot be null");
		
		this.modelClass = modelClass;
		Log.d(TAG, modelClass.toString());
	}
	
	/**
	 * JSON Response mapping service for Bank. 
	 * @param context
	 * @param params
	 * @param modelClass
	 * @param isCard Determines if the card base url is used. 
	 */
	protected JsonResponseMappingNetworkServiceCall(final Context context, final ServiceCallParams params,
			final Class<M> modelClass, boolean isCard) {
		
		super(context, params, isCard);
		
		checkNotNull(modelClass, "modelClass cannot be null");
		
		this.modelClass = modelClass;
		Log.d(TAG, modelClass.toString());
	}
	
	@Override
	protected M parseSuccessResponse(final int status, final Map<String,List<String>> headers, final InputStream body)
			throws IOException {
		return JacksonObjectMapperHolder.mapper.readValue(body, modelClass);
	}
	
}

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
 * A {@link NetworkServiceCall} that handles mapping of unnamed list JSON requests and responses.
 * 
 * @param <M> The <u>m</u>odel type for the JSON result
 * @param <V> The <u>I</u>nner type for the JSON result
 */
public abstract class UnamedListJsonResponseMappingNetworkServiceCall<M, I> extends JsonResponseMappingNetworkServiceCall<M>{

	private static final String TAG = "UnamedListJsonResponseMapping";

	private final Class<I> innerClass;

	/**
	 * JSON mapping service call used with the base url defaulted to card.
	 * @param context
	 * @param params
	 */
	protected UnamedListJsonResponseMappingNetworkServiceCall(final Context context, final ServiceCallParams params,
			final Class<M> modelClass, final Class<I> innerClass) {
		super(context, params, modelClass);
		checkNotNull(modelClass, "modelClass cannot be null");

		this.innerClass = innerClass;
		Log.d(TAG, modelClass.toString());
		
	}



	@Override
	protected M parseSuccessResponse(final int status, final Map<String,List<String>> headers, final InputStream body)
			throws IOException {
		return super.parseSuccessResponse(status, headers, body);
	}

	/**
	 * Parses an unnamed list and returns a list of the model class.
	 * @param body - json body to parse
	 * @param model - model class to map the objects to
	 * @return a list of model obects
	 * @throws IOException
	 */
	public List<I> parseUnamedList(final InputStream body)
			throws IOException {
		final List<I> object = JacksonObjectMapperHolder.mapper.readValue(body, JacksonObjectMapperHolder.mapper.getTypeFactory().constructCollectionType(List.class, this.innerClass));
		return object;
	}
}

package com.discover.mobile.common.net.json;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
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

	/** Holds information that is used to communicate between the caller sending the request and handler handling the response*/
	private final Bundle extras;
	
	/**
	 * JSON mapping service call used with the base url defaulted to card.
	 * @param context
	 * @param params
	 */
	protected JsonResponseMappingNetworkServiceCall(final Context context, final ServiceCallParams params,
			final Class<M> modelClass) {
		super(context,params);
		checkNotNull(modelClass, "modelClass cannot be null");

		extras = new Bundle();
		this.modelClass = modelClass;
		Log.d(TAG, modelClass.toString());
	}

	/**
	 * JSON mapping service call used with the base url defaulted to card.
	 * @param context
	 * @param params
	 */
	protected JsonResponseMappingNetworkServiceCall(final Context context, final ServiceCallParams params,
			final Class<M> modelClass, final String url) {
		super(context,params, url);
		checkNotNull(modelClass, "modelClass cannot be null");

		extras = new Bundle();
		this.modelClass = modelClass;
		Log.d(TAG, modelClass.toString());
	}



	@Override
	protected M parseSuccessResponse(final int status, final Map<String,List<String>> headers, final InputStream body)
			throws IOException {
		return JacksonObjectMapperHolder.mapper.readValue(body, modelClass);
	}

	/** Method used to fetch the bundle of information stored in the service call. */
	public Bundle getExtras() {
		return extras;
	}

}

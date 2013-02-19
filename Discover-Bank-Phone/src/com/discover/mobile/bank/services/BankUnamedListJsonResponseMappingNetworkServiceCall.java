package com.discover.mobile.bank.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.ServiceCallParams;
import com.discover.mobile.common.net.json.UnamedListJsonResponseMappingNetworkServiceCall;
import com.discover.mobile.common.net.json.bank.ReceivedUrl;

/**
 * A {@link NetworkServiceCall} that handles mapping of unnamed list JSON
 * requests and responses.
 * 
 * @param <M>
 *            The <u>m</u>odel type for the JSON result
 * @param <V>
 *            The <u>I</u>nner type for the JSON result
 */
public abstract class BankUnamedListJsonResponseMappingNetworkServiceCall<M, I>
extends UnamedListJsonResponseMappingNetworkServiceCall<M, I> {

	/**
	 * 
	 * @param context
	 * @param params
	 * @param modelClass
	 * @param innerClass
	 */
	protected BankUnamedListJsonResponseMappingNetworkServiceCall(
			final Context context, final ServiceCallParams params, final Class<M> modelClass,
			final Class<I> innerClass) {
		super(context, params, modelClass, innerClass);

	}

	@Override
	protected String getBaseUrl() {
		return BankUrlManager.getBaseUrl();
	}


	/**
	 * Parses through the headers and gets retrieved URL's from the header
	 * @param headers - header to get the links form
	 * @return the hashmap of links
	 */
	protected Map<String, ReceivedUrl> parseHeaderForLinks(final Map<String,List<String>> headers){
		final Map<String, ReceivedUrl> urls = new HashMap<String, ReceivedUrl>();
		final List<String> links = headers.get("Link");
		if(null == links && links.isEmpty()){return urls;}
		for(final String string : links){
			final String[] pieces = string.replaceAll("<|>|rel=|\"| ", "").split(";");
			final ReceivedUrl url = new ReceivedUrl();
			url.url = pieces[0];
			urls.put(pieces[1], url);
		}
		return urls;
	}

}

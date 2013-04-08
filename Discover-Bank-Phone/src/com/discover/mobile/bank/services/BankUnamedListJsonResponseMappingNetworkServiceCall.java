package com.discover.mobile.bank.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.discover.mobile.bank.services.json.ReceivedUrl;
import com.discover.mobile.common.net.NetworkServiceCall;
import com.discover.mobile.common.net.ServiceCallParams;
import com.discover.mobile.common.net.json.UnamedListJsonResponseMappingNetworkServiceCall;

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
	extends UnamedListJsonResponseMappingNetworkServiceCall<M, I> 
	implements BackgroundServiceCall{

	/**
	 * Flag used to determine whether service call is to run silently in background.
	 */
	boolean isBackgroundCall = false;
	
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

		try{
			final List<String> links = headers.get("Link");
			if(null == links || links.isEmpty()){return urls;}
			final String[] strings = links.get(0).replaceAll("<|>|rel=|\"| ", "").split(",");
			for(final String string : strings){
				final String[] pieces = string.split(";");
				final ReceivedUrl url = new ReceivedUrl();
				url.url = pieces[0];
				urls.put(pieces[1], url);
			}
			return urls;
		}catch(final Exception e){
			//Return an empty link object if any exception occurs
			return urls;
		}
	}
	
	@Override
	public void setIsBackgroundCall(final boolean value) {
		isBackgroundCall = value;	
	}

	@Override
	public boolean isBackgroundCall() {
		return isBackgroundCall;
	}

}

/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.services.atm;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.cordova.api.LOG;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.BankJsonResponseMappingNetworkServiceCall;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.services.error.BankErrorResponseParser;
import com.discover.mobile.common.DiscoverActivityManager;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.SimpleReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.json.JacksonObjectMapperHolder;
import com.discover.mobile.common.utils.StringUtility;

/**
 * Get the location information from a string object.  This is used to reverse geocode an address/
 * 
 * Example Response:
 * 
 * 
    "results": [
        {
            "address_components": [
                {
                    "long_name": "60060",
                    "short_name": "60060",
                    "types": [
                        "postal_code"
                    ]
                },
                {
                    "long_name": "Mundelein",
                    "short_name": "Mundelein",
                    "types": [
                        "locality",
                        "political"
                    ]
                },
                {
                    "long_name": "Lake",
                    "short_name": "Lake",
                    "types": [
                        "administrative_area_level_2",
                        "political"
                    ]
                },
                {
                    "long_name": "Illinois",
                    "short_name": "IL",
                    "types": [
                        "administrative_area_level_1",
                        "political"
                    ]
                },
                {
                    "long_name": "United States",
                    "short_name": "US",
                    "types": [
                        "country",
                        "political"
                    ]
                }
            ],
            "formatted_address": "Mundelein, IL 60060, USA",
            "geometry": {
                "bounds": {
                    "northeast": {
                        "lat": 42.3062738,
                        "lng": -87.974051
                    },
                    "southwest": {
                        "lat": 42.226515,
                        "lng": -88.12215979999999
                    }
                },
                "location": {
                    "lat": 42.2785596,
                    "lng": -88.0314174
                },
                "location_type": "APPROXIMATE",
                "viewport": {
                    "northeast": {
                        "lat": 42.3062738,
                        "lng": -87.974051
                    },
                    "southwest": {
                        "lat": 42.226515,
                        "lng": -88.12215979999999
                    }
                }
            },
            "types": [
                "postal_code"
            ]
        }
    ],
    "status": "OK"
}
 * @author jthornton
 *
 */
public class GetLocationFromAddressServiceCall extends BankJsonResponseMappingNetworkServiceCall<AddressToLocationDetail>{

	/**Tag used for logging*/
	private static final String TAG = GetLocationFromAddressServiceCall.class.getSimpleName();

	/**Handler to return the info back to the UI*/
	private final TypedReferenceHandler<AddressToLocationDetail> handler;

	/**Service call helper*/
	private final AtmServiceHelper helper;

	/**ID to append to the query string*/
	private static final String CLIENT = "&client=";

	/**Signature to sign the request with*/
	private static final String SIGNATURE = "&signature=";

	/**
	 * 
	 * @param context Reference to the context invoking the API
	 * @param callback Reference to the Handler for the response
	 */
	public GetLocationFromAddressServiceCall(final Context context, 
			final AsyncCallback<AddressToLocationDetail> callback, final AtmServiceHelper helper) {

		super(context, new GetCallParams(
				signString(BankUrlManager.getAtmAddressToLocationSignitureUrl(), helper.getAddressToLocationString())) {
			{
				//This service call is made after authenticating and receiving a token,
				//therefore the session should not be cleared otherwise the token will be wiped out
				clearsSessionBeforeRequest = false;
				//This ensures the token is added to the HTTP Authorization Header of the HTTP request
				requiresSessionForRequest = false;
				//This ensure the required device information is supplied in the Headers of the HTTP request
				sendDeviceIdentifiers = true;
				// Specify what error parser to use when receiving an error response is received
				errorResponseParser = BankErrorResponseParser.instance();
				// Makes the service call cancellable
				setCancellable(true);
			}
		}, AddressToLocationDetail.class, BankUrlManager.getAtmAddressToLocationBaseUrl());
		this.helper = helper;
		handler = new SimpleReferenceHandler<AddressToLocationDetail>(callback);
	}

	@Override
	protected AddressToLocationDetail parseSuccessResponse(final int status, final Map<String,List<String>> header, 
			final InputStream body) throws IOException {

		final AddressToLocationDetail data = super.parseSuccessResponse(status, header, body);
		return data;
	}

	@Override
	public TypedReferenceHandler<AddressToLocationDetail> getHandler() {
		return handler;
	}

	/**
	 * Parses an unnamed list and returns a list of the model class.
	 * @param body - json body to parse
	 * @param model - model class to map the objects to
	 * @return a list of model obects
	 * @throws IOException
	 */
	public List<AddressToLocationDetail> parseList(final InputStream body)
			throws IOException {
		final List<AddressToLocationDetail> object = 
				JacksonObjectMapperHolder.getMapper().readValue(body, 
						JacksonObjectMapperHolder.getMapper().getTypeFactory().constructCollectionType(
								List.class, AddressToLocationDetail.class));
		return object;
	}

	/**
	 * @return the helper
	 */
	public AtmServiceHelper getHelper() {
		return helper;
	}

	/**
	 * Sign the query string
	 * @param baseUrl - base url
	 * @param query - query string
	 */
	private static String signString(final String baseUrl, final String query){
		String returnString = "";
		try {
			//Get the private key from the resources
			String keyString = DiscoverActivityManager.getString(R.string.atm_private_key);

			//Format the key correctly
			keyString = keyString.replace(StringUtility.DASH, StringUtility.PLUS);
			keyString = keyString.replace(StringUtility.UNDERSCORE, StringUtility.SLASH);

			//Decode the string
			final byte[] key = Base64.decode(keyString, Base64.DEFAULT);

			//Get the url ready to be signed by appending the client
			final URL url = new URL(baseUrl + query +  CLIENT + DiscoverActivityManager.getString(R.string.atm_client_id));

			//Sign the url
			returnString = signRequest(key, url.getPath(), url.getQuery());

			/**
			 * If any error occurs, fall back to the original string without the client id and the signature.
			 * If the quota has not been reached, then the service call may still go through.
			 */
		} catch (final MalformedURLException e) {
			logError(e);
			returnString = query;
		} catch (final InvalidKeyException e) {
			logError(e);
			returnString = query;
		} catch (final NoSuchAlgorithmException e) {
			logError(e);
			returnString = query;
		} catch (final UnsupportedEncodingException e) {
			logError(e);
			returnString = query;
		} catch (final URISyntaxException e) {
			logError(e);
			returnString = query;
		}

		return returnString;
	}

	/**
	 * Log an exception if logging is enabled
	 * @param e - exception to log
	 */
	private static void logError(final Exception e) {
		if(LOG.isLoggable(Log.ERROR)){
			Log.e(TAG, "Error Signing Request: " + e.getMessage());
		}
	}

	/**
	 * Sign the request
	 * @param key - key used to sign the query sting with
	 * @param path - path of the service call
	 * @param query - query of the service call
	 * @return the signed query string
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws UnsupportedEncodingException
	 * @throws URISyntaxException
	 */
	private static String signRequest(final byte[] key, final String path, final String query) throws 
	NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, URISyntaxException {
		final String algorithm = "HmacSHA1";

		// Retrieve the proper URL components to sign
		final String resource = path + StringUtility.QUESTION_MARK + query;

		// Get an HMAC-SHA1 signing key from the raw key bytes
		final SecretKeySpec sha1Key = new SecretKeySpec(key, algorithm);

		// Get an HMAC-SHA1 Mac instance and initialize it with the HMAC-SHA1 key
		final Mac mac = Mac.getInstance(algorithm);
		mac.init(sha1Key);

		// compute the binary signature for the request
		final byte[] sigBytes = mac.doFinal(resource.getBytes());

		// base 64 encode the binary signature
		String signature = Base64.encodeToString(sigBytes, Base64.DEFAULT);

		// convert the signature to 'web safe' base 64
		signature = signature.replace(StringUtility.PLUS, StringUtility.DASH);
		signature = signature.replace(StringUtility.SLASH, StringUtility.UNDERSCORE);

		return resource + SIGNATURE + signature;
	}

}

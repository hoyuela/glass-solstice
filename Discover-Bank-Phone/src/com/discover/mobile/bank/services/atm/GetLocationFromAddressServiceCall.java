/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.services.atm;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.discover.mobile.bank.services.BankJsonResponseMappingNetworkServiceCall;
import com.discover.mobile.bank.services.BankUrlManager;
import com.discover.mobile.bank.services.error.BankErrorResponseParser;
import com.discover.mobile.common.callback.AsyncCallback;
import com.discover.mobile.common.net.ServiceCallParams.GetCallParams;
import com.discover.mobile.common.net.SimpleReferenceHandler;
import com.discover.mobile.common.net.TypedReferenceHandler;
import com.discover.mobile.common.net.json.JacksonObjectMapperHolder;

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

	private final TypedReferenceHandler<AddressToLocationDetail> handler;

	/**Service call helper*/
	private final AtmServiceHelper helper;

	/**
	 * 
	 * @param context Reference to the context invoking the API
	 * @param callback Reference to the Handler for the response
	 */
	public GetLocationFromAddressServiceCall(final Context context, 
			final AsyncCallback<AddressToLocationDetail> callback, final AtmServiceHelper helper) {

		super(context, new GetCallParams(helper.getAddressToLocationString()) {
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
}

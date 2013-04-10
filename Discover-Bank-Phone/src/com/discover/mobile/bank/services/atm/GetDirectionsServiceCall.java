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


/**
 * Service call for getting direction to an ATM from a location
 * 
 * Example URL: http://maps.googleapis.com/maps/api/directions/json?origin=Chicago,IL&destination=Los+Angeles,CA&sensor=false
 * 
 * Example Response
 * {
    "routes": [
        {
            "bounds": {
                "northeast": {
                    "lat": 41.88571,
                    "lng": -87.64247
                },
                "southwest": {
                    "lat": 41.88052,
                    "lng": -87.64471
                }
            },
            "copyrights": "Map data ©2013 Google, Sanborn",
            "legs": [
                {
                    "distance": {
                        "text": "0.5 mi",
                        "value": 749
                    },
                    "duration": {
                        "text": "3 mins",
                        "value": 176
                    },
                    "end_address": "570 West Monroe Street, Chicago, IL 60661, USA",
                    "end_location": {
                        "lat": 41.88054000000001,
                        "lng": -87.64247
                    },
                    "start_address": "647 West Lake Street, Chicago, IL 60661, USA",
                    "start_location": {
                        "lat": 41.8857,
                        "lng": -87.64471
                    },
                    "steps": [
                        {
                            "distance": {
                                "text": "128 ft",
                                "value": 39
                            },
                            "duration": {
                                "text": "1 min",
                                "value": 4
                            },
                            "end_location": {
                                "lat": 41.88571,
                                "lng": -87.64424000000001
                            },
                            "html_instructions": "Head <b>east</b> on <b>W Lake St</b> toward <b>N Desplaines St</b>",
                            "polyline": {
                                "points": "sxs~Flb}uOA}A"
                            },
                            "start_location": {
                                "lat": 41.8857,
                                "lng": -87.64471
                            },
                            "travel_mode": "DRIVING"
                        },
                        {
                            "distance": {
                                "text": "0.4 mi",
                                "value": 578
                            },
                            "duration": {
                                "text": "2 mins",
                                "value": 140
                            },
                            "end_location": {
                                "lat": 41.88052,
                                "lng": -87.64406000000001
                            },
                            "html_instructions": "Take the 1st <b>right</b> onto <b>N Desplaines St</b>",
                            "polyline": {
                                "points": "uxs~Fn_}uOhB?N?jCCbCGpBCfCEfCC`CE~BC"
                            },
                            "start_location": {
                                "lat": 41.88571,
                                "lng": -87.64424000000001
                            },
                            "travel_mode": "DRIVING"
                        },
                        {
                            "distance": {
                                "text": "433 ft",
                                "value": 132
                            },
                            "duration": {
                                "text": "1 min",
                                "value": 32
                            },
                            "end_location": {
                                "lat": 41.88054000000001,
                                "lng": -87.64247
                            },
                            "html_instructions": "Turn <b>left</b> onto <b>W Monroe St</b><div style=\"font-size:0.9em\">Destination will be on the left</div>",
                            "polyline": {
                                "points": "gxr~Fj~|uOA}DA_B?_@"
                            },
                            "start_location": {
                                "lat": 41.88052,
                                "lng": -87.64406000000001
                            },
                            "travel_mode": "DRIVING"
                        }
                    ],
                    "via_waypoint": []
                }
            ],
            "overview_polyline": {
                "points": "sxs~Flb}uOA}AhB?zCCtFKnGI`GIC}G?_@"
            },
            "summary": "N Desplaines St",
            "warnings": [],
            "waypoint_order": []
        }
    ],
    "status": "OK"
 * }
 * @author jthornton
 *
 */
public class GetDirectionsServiceCall extends BankJsonResponseMappingNetworkServiceCall<Directions> {

	private final TypedReferenceHandler<Directions> handler;

	/**Service call helper*/
	private final AtmServiceHelper helper;

	/**
	 * 
	 * @param context Reference to the context invoking the API
	 * @param callback Reference to the Handler for the response
	 */
	public GetDirectionsServiceCall(final Context context, 
			final AsyncCallback<Directions> callback, final AtmServiceHelper helper) {

		super(context, new GetCallParams(helper.getDirectionsQueryString()) {
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
			}
		}, Directions.class, BankUrlManager.getAtmDirectionsBaseUrl());
		this.helper = helper;
		handler = new SimpleReferenceHandler<Directions>(callback);
	}

	@Override
	protected Directions parseSuccessResponse(final int status, final Map<String,List<String>> header, final InputStream body)
			throws IOException {

		final Directions data = super.parseSuccessResponse(status, header, body);
		return data;
	}

	@Override
	public TypedReferenceHandler<Directions> getHandler() {
		return handler;
	}

	/**
	 * @return the helper
	 */
	public AtmServiceHelper getHelper() {
		return helper;
	}
}

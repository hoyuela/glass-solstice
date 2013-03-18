/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.services.atm;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJOJ object used in getting the directions from the Google services for the current location
 * 
 * Example Response is :
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
public class Directions implements Serializable{

	/**Generated id*/
	private static final long serialVersionUID = -3403202779874035118L;

	/**Route details*/
	@JsonProperty("routes")
	public List<RouteDetail> routes;

	/**Status of the request*/
	@JsonProperty("status")
	public String status;
}

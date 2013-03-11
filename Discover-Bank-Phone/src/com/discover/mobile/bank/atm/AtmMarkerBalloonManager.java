/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.atm;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.atm.AtmDetail;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.Marker;

/**
 * Info overlay balloon manager.  Creates and populates the view that will
 * be placed above the marker in the map.
 * @author jthornton
 *
 */
public class AtmMarkerBalloonManager{

	/**Map of markers and atm so that the atm*/
	private final Map<Marker, AtmDetail> markerMap;

	/**Context used for rendering*/
	private final Context context;

	/**Image for the street view*/
	private ImageView streetView;

	/**Image for the get directions view*/
	private ImageView directions;

	/**
	 * Constructor for the class
	 * @param context - context used to render the layout
	 */
	public AtmMarkerBalloonManager(final Context context){
		this.context = context;
		markerMap = new HashMap<Marker, AtmDetail>();
	}

	/**
	 * Get the view that needs to be displayed above the marker
	 * @param marker - marker that was clicked
	 * @return the view that needs to be displayed above the marker
	 */
	public View getViewForMarker(final Marker marker){
		final View view = LayoutInflater.from(context).inflate(R.layout.bank_atm_marker_info, null);
		view.setClickable(false);
		final AtmDetail atm = markerMap.get(marker); 

		//Means it was the current location that was clicked.
		if(null == atm){
			return null;
		}

		final TextView name = (TextView) view.findViewById(R.id.name);
		final TextView address = (TextView) view.findViewById(R.id.address);
		streetView = (ImageView) view.findViewById(R.id.street_view);
		final TextView hours = (TextView) view.findViewById(R.id.hours);
		final TextView directionsLabel = (TextView) view.findViewById(R.id.directions_label);
		directions = (ImageView) view.findViewById(R.id.directions);

		name.setText(atm.locationName);
		address.setText(atm.address1);
		hours.setText(atm.atmHrs.replace("Sat", "\nSat"));
		final String distance = String.format(Locale.US, "%.2f", atm.distanceFromUser);
		directionsLabel.setText(String.format(context.getString(R.string.atm_location_get_directions), distance));
		return view;
	}

	/**
	 * Add a marker and atm to the map
	 * @param marker - marker to be linked to the atm
	 * @param object - atm 
	 */
	public void addMarkerAndAtm(final Marker marker, final LocationObject object){
		markerMap.put(marker, (AtmDetail)object);
	}

	/**
	 * Get the click handler for the info window when it is displayed
	 * @return the click handler for the info window when it is displayed
	 */
	public GoogleMap.OnInfoWindowClickListener getOnClickListener(){
		return new OnInfoWindowClickListener(){

			@Override
			public void onInfoWindowClick(final Marker arg0) {

				Toast.makeText(context, "Street view is under construction", 3000).show();	
				//				if(v == streetView){
				//				}else if(v == directions){
				//					Toast.makeText(context, "Getting directions is under construction.", 3000).show();		
				//				}
			}
		};
	}
}
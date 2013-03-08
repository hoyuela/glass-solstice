/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.atm;

import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;

/**
 * Info window adatper.  Used when a marker is clicked and the info window needs
 * to be displayed above the marker
 * @author jthornton
 *
 */
public class DiscoverInfoWindowAdapter implements InfoWindowAdapter{

	/**Manager that will render the layout*/
	private final AtmMarkerBalloonManager manager;

	/**
	 * Constructor for the adapter
	 * @param manager - manager that will be rendering the layouts
	 */
	public DiscoverInfoWindowAdapter(final AtmMarkerBalloonManager manager){
		this.manager = manager;
	}

	/**
	 * Add the marker and the location object to the manager
	 * @param marker - marker that will be placed on the map
	 * @param object - object associated with the marker
	 */
	public void addMarkerAndAtm(final Marker marker, final LocationObject object){
		manager.addMarkerAndAtm(marker, object);
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.android.gms.maps.GoogleMap.InfoWindowAdapter#getInfoContents(com.google.android.gms.maps.model.Marker)
	 */
	@Override
	public View getInfoContents(final Marker marker) {
		return manager.getViewForMarker(marker);
	}

	/**
	 * 
	 */
	@Override
	public View getInfoWindow(final Marker marker) {
		return null;
	}

	/**
	 * Get the click listener for the info window
	 */
	public GoogleMap.OnInfoWindowClickListener getInfoWindowClickListener(){
		return manager.getOnClickListener();
	}

}

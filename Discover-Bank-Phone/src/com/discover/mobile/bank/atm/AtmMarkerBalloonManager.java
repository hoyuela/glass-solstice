/*
 * © Copyright Solstice Mobile 2013
 */
package com.discover.mobile.bank.atm;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.discover.mobile.bank.BankExtraKeys;
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

	/**Current location address object*/
	private Address address;

	/**Fragment with the balloons*/
	private final AtmMapFragment fragment;

	/**
	 * Constructor for the class
	 * @param context - context used to render the layout
	 */
	public AtmMarkerBalloonManager(final AtmMapFragment fragment){
		this.fragment = fragment;
		context = fragment.getActivity();
		markerMap = new HashMap<Marker, AtmDetail>();
	}

	/**
	 * Get the view that needs to be displayed above the marker
	 * @param marker - marker that was clicked
	 * @return the view that needs to be displayed above the marker
	 */
	public View getViewForMarker(final Marker marker){
		final AtmDetail atm = markerMap.get(marker); 

		//Means it was the current location that was clicked.
		if(null == atm){
			return getCurrentLocationView();
		}else{
			return getMarkerView(atm);
		}


	}

	private View getCurrentLocationView() {
		final View view = LayoutInflater.from(context).inflate(R.layout.bank_atm_current_locaiton, null);
		final TextView addressBox = (TextView) view.findViewById(R.id.address);
		final TextView city = (TextView) view.findViewById(R.id.city);

		addressBox.setText(address.getAddressLine(0));
		city.setText(address.getLocality() +", " + address.getAdminArea());
		return view;
	}

	private View getMarkerView(final AtmDetail atm) {
		final View view = LayoutInflater.from(context).inflate(R.layout.bank_atm_marker_info, null);
		final TextView name = (TextView) view.findViewById(R.id.name);
		final TextView address = (TextView) view.findViewById(R.id.address);
		streetView = (ImageView) view.findViewById(R.id.street_view);
		final TextView hours = (TextView) view.findViewById(R.id.hours);
		final TextView directionsLabel = (TextView) view.findViewById(R.id.directions_label);

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
			public void onInfoWindowClick(final Marker marker) {
				final AtmDetail atm = markerMap.get(marker); 

				//Means it was the current location that was clicked.
				if(null != atm){

					try {

						final String addressString =  atm.address1 + " "  + atm.city +" " + atm.state;
						final Geocoder coder = new Geocoder(context);
						final List<Address> addresses = coder.getFromLocationName(addressString, 1);
						final Bundle bundle = new Bundle();
						if(null != addresses || addresses.isEmpty()){
							bundle.putDouble(BankExtraKeys.STREET_LAT, atm.getLatitude());
							bundle.putDouble(BankExtraKeys.STREET_LON, atm.getLongitude());							
						}else{
							bundle.putDouble(BankExtraKeys.STREET_LAT, addresses.get(0).getLatitude());
							bundle.putDouble(BankExtraKeys.STREET_LON, addresses.get(0).getLongitude());
						}
						bundle.putInt(BankExtraKeys.ATM_ID, atm.id);
						fragment.showStreetView(bundle);
					} catch (final IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}				
			}
		};
	}

	/**
	 * @return the address
	 */
	public Address getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(final Address address) {
		this.address = address;
	}
}
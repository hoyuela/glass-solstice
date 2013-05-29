package com.discover.mobile.bank.atm;

import java.util.List;

import android.location.Location;
import android.os.Handler;
import android.os.Looper;

import com.discover.mobile.bank.R;
import com.discover.mobile.bank.services.atm.AtmDetail;
import com.discover.mobile.bank.services.atm.AtmResults;
import com.discover.mobile.common.DiscoverActivityManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * A Runnable class which will calculate the needed camera transformation
 * for the GoogleMaps API so that the current mapWrapper object can
 * adjust its camera position to show all available pins within 25 miles.
 * @author scottseward
 *
 */
public class AutoZoomRunnable implements Runnable{

	private AtmResults results = null;
	private int endIndex = 0;
	private DiscoverMapWrapper mapWrapper = null;

	public AutoZoomRunnable(final DiscoverMapWrapper mapWrapper, final AtmResults atms, final int endIndex) {
		this.endIndex = endIndex;
		results = atms;
		this.mapWrapper = mapWrapper;
	}

	@Override
	public void run() {
		if(results != null && results.results != null && null != mapWrapper.getMap()) {
			final int twentyFiveMiles = 25;
			final long quarterSecond = 250;

			final List<AtmDetail> atms = results.results.atms;
			final Location currentLocation = mapWrapper.getCurrentLocation();
			final LatLngBounds.Builder atmBoundsBuilder = new LatLngBounds.Builder();

			if(atms != null) {
				//Include the current location in the boundary set so it will always be shown.
				atmBoundsBuilder.include(new LatLng(currentLocation.getLatitude(), 
						currentLocation.getLongitude()));
				//Find all ATMs that are within 25 miles of our current location, and add them to
				//the boundary set.
				for(int i = 0; i < endIndex; ++i) {
					if(atms.get(i).distanceFromUser <= twentyFiveMiles) {
						atmBoundsBuilder.include(
								new LatLng(atms.get(i).getLatitude(), atms.get(i).getLongitude()));
					}	
				}
			}

			//A padding that is used around the edge of the screen to compensate for the size
			//of the search bar and the button bar at the bottom of the screen.
			final int padding = 
					(int)DiscoverActivityManager.getActiveActivity().getResources().getDimension(R.dimen.atm_zoom_padding);

			//The collection of locations that our bounds will contain.
			final LatLngBounds atmBounds = atmBoundsBuilder.build();

			//Queue the map zoom animation onto the main thread.
			new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
				@Override
				public void run() {
					mapWrapper.animateZoomChange(CameraUpdateFactory.newLatLngBounds(atmBounds, padding));
				}
			}, quarterSecond);
		}
	}

}
